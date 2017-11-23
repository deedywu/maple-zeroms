package server;

import database.DatabaseConnection;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Emilyx3
 */
public class ServerProperties {

    private static final Properties props = new Properties();
    private static final String[] toLoad = {
        "Settings.ini"
    };

    private ServerProperties() {
    }

    static {
        for (String s : toLoad) {
            InputStreamReader fr;
            try {
                fr = new InputStreamReader(new FileInputStream(s), "UTF-8");
                props.load(fr);
                fr.close();
            } catch (IOException ex) {
                System.out.println("加载Settings错误：" + ex);
            }

        }
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM auth_server_channel_ip")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                props.put(rs.getString("name") + rs.getInt("channelid"), rs.getString("value"));
            }
            rs.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0); //Big ass error.
        }

    }

    public static String getProperty(String s) {
        return props.getProperty(s);
    }

    public static void setProperty(String prop, String newInf) {
        props.setProperty(prop, newInf);
    }

    public static String getProperty(String s, String def) {
        return props.getProperty(s, def);
    }
}
