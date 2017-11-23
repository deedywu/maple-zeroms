package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.ServerProperties;

/**
 * All OdinMS servers maintain a Database Connection. This class therefore
 * "singletonices" the connection per process.
 *
 *
 * @author Frz
 */
public class DatabaseConnection {

    private static final HashMap<Integer, ConWrapper> connections = new HashMap();
    private final static Logger log = LoggerFactory.getLogger(DatabaseConnection.class);
    private static String dbDriver = "", dbUrl = "", dbUser = "", dbPass = "";
    private static final long connectionTimeOut = 30 * 60 * 1000;
    private static final ReentrantLock lock = new ReentrantLock();

    public static int getConnectionsCount() {
        return connections.size();
    }

    public static void close() {
        try {
            Thread cThread = Thread.currentThread();
            Integer threadID = (int) cThread.getId();
            ConWrapper ret = connections.get(threadID);
            if (ret != null) {
                Connection c = ret.getConnection();
                if (!c.isClosed()) {
                    c.close();
                }
                lock.lock();
                try {
                    connections.remove(threadID);
                } finally {
                    lock.unlock();
                }
            }

        } catch (SQLException ex) {
        }
    }

    public static Connection getConnection() {

        Thread cThread = Thread.currentThread();
        Integer threadID = (int) cThread.getId();
        ConWrapper ret;

        ret = connections.get(threadID);

        if (ret == null) {
            Connection retCon = connectToDB();
            ret = new ConWrapper(threadID, retCon);
            lock.lock();
            try {
                connections.put(threadID, ret);
            } finally {
                lock.unlock();
            }
        }

        Connection c = ret.getConnection();
        try {
            if (c.isClosed()) {
                Connection retCon = connectToDB();
                lock.lock();
                try {
                    connections.remove(threadID);
                    connections.put(threadID, ret);
                } finally {
                    lock.unlock();
                }
                ret = new ConWrapper(threadID, retCon);
            }
        } catch (Exception e) {
        } finally {
        }

        return ret.getConnection();
    }

    static class ConWrapper {

        private final int tid;
        private long lastAccessTime;
        private Connection connection;

        public ConWrapper(int tid, Connection con) {
            this.tid = tid;
            this.lastAccessTime = System.currentTimeMillis();
            this.connection = con;
        }

        public boolean close() {
            boolean ret = false;

            if (connection == null) {
                ret = false;
            } else {

                try {
                    lock.lock();
                    try {
                        if (expiredConnection() || this.connection.isValid(10)) {
                            try {
                                this.connection.close();
                                ret = true;
                            } catch (SQLException e) {
                                ret = false;
                            }
                        }
                        connections.remove(tid);
                    } finally {
                        lock.unlock();
                    }
                } catch (SQLException ex) {
                    ret = false;

                }
            }

            return ret;
        }

        public Connection getConnection() {
            if (expiredConnection()) {
                try { // Assume that the connection is stale
                    connection.close();
                } catch (SQLException err) {
                }
                this.connection = connectToDB();
            }
            lastAccessTime = System.currentTimeMillis(); // Record Access
            return this.connection;
        }

        /**
         * Returns whether this connection has expired
         *
         * @return
         */
        public boolean expiredConnection() {
            return System.currentTimeMillis() - lastAccessTime >= connectionTimeOut;
        }
    }

    private static Connection connectToDB() {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // touch the mysql driver
        } catch (final ClassNotFoundException e) {
            System.err.println("ERRORA：" + e);
        }
        try {
            Properties props = new Properties();
            props.put("user", ServerProperties.getProperty("user"));
            props.put("password", ServerProperties.getProperty("password"));
            props.put("autoReconnect", "true");
            props.put("characterEncoding", "UTF8");
            final Connection con = DriverManager.getConnection("jdbc:mysql://" + ServerProperties.getProperty("ip") + ":" + ServerProperties.getProperty("port") + "/" + ServerProperties.getProperty("database"), props);
            return con;
        } catch (SQLException e) {
            System.out.println("JDBC连接错误。请确定帐号密码正确。");
            throw new DatabaseException(e);
        }

    }

    public static boolean isInitialized() {
        return !dbUser.equals("");
    }

    public static void closeTimeout() {
        int i = 0;
        lock.lock();
        List<Integer> keys = new ArrayList(connections.keySet());
        try {
            for (Integer tid : keys) {
                ConWrapper con = connections.get(tid);
                if (con.close()) {
                    i++;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public static void closeAll() {
        synchronized (connections) {
            for (ConWrapper con : connections.values()) {
                try {
                    con.connection.close();
                } catch (SQLException ex) {
                }
            }
        }
    }
    public final static Runnable CloseSQLConnections = new Runnable() {

        @Override
        public void run() {
            DatabaseConnection.closeTimeout();
        }
    };
}
