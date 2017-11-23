
package client.inventory;

import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author zjj
 */
public class MapleInventoryIdentifier implements Serializable {

    private static final long serialVersionUID = 21_830_921_831_301L;
    private AtomicInteger runningUID;
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private Lock readLock = rwl.readLock(), writeLock = rwl.writeLock();
    private static MapleInventoryIdentifier instance = new MapleInventoryIdentifier();

    /**
     *
     */
    public MapleInventoryIdentifier() {
        this.runningUID = new AtomicInteger(0);
        getNextUniqueId();
    }

    /**
     *
     * @return
     */
    public static int getInstance() {
        return instance.getNextUniqueId();
    }

    /**
     *
     * @return
     */
    public int getNextUniqueId() {
        if (grabRunningUID() <= 0) {
            setRunningUID(initUID());
        }
        incrementRunningUID();
        return grabRunningUID();
    }

    /**
     *
     * @return
     */
    public int grabRunningUID() {
        readLock.lock();
        try {
            return runningUID.get(); //maybe we should really synchronize/do more here..
        } finally {
            readLock.unlock();
        }
    }

    /**
     *
     */
    public void incrementRunningUID() {
        setRunningUID(grabRunningUID() + 1);
    }

    /**
     *
     * @param rUID
     */
    public void setRunningUID(int rUID) {
        if (rUID < grabRunningUID()) { //dont go backwards.
            return;
        }
        writeLock.lock();
        try {
            runningUID.set(rUID); //maybe we should really synchronize/do more here..
        } finally {
            writeLock.unlock();
        }
    }

    /**
     *
     * @return
     */
    public int initUID() {
        int ret = 0;
        if (grabRunningUID() > 0) {
            return grabRunningUID();
        }
        try {
            int[] ids = new int[4];
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT MAX(uniqueid) FROM inventoryitems");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ids[0] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT MAX(petid) FROM pets");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[1] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT MAX(ringid) FROM rings");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[2] = rs.getInt(1) + 1;
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT MAX(partnerringid) FROM rings");
            rs = ps.executeQuery();
            if (rs.next()) {
                ids[3] = rs.getInt(1) + 1; //biggest pl0x. but if this happens -> o_O
            }
            rs.close();
            ps.close();

            for (int i = 0; i < 4; i++) {
                if (ids[i] > ret) {
                    ret = ids[i];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
