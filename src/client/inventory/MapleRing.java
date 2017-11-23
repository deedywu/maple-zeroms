package client.inventory;

import client.MapleCharacter;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import database.DatabaseConnection;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import server.MapleInventoryManipulator;

/**
 *
 * @author zjj
 */
public class MapleRing implements Serializable {

    private static final long serialVersionUID = 9_179_541_993_413_738_579L;
    private int ringId;
    private int ringId2;
    private int partnerId;
    private int itemId;
    private String partnerName;
    private boolean equipped = false;

    private MapleRing(int id, int id2, int partnerId, int itemid, String partnerName) {
        this.ringId = id;
        this.ringId2 = id2;
        this.partnerId = partnerId;
        this.itemId = itemid;
        this.partnerName = partnerName;
    }

    /**
     *
     * @param ring
     * @return
     */
    public static Equip loadFromDb(IItem ring) {
        try {
            Connection con = DatabaseConnection.getConnection(); // Get a connection to the database

            Equip eq;
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE ringid = ?") // Get ring details..
            ) {
                ps.setInt(1, ring.getUniqueId());
                ResultSet rs = ps.executeQuery();
                rs.next();
                MapleRing ret = new MapleRing(ring.getItemId(), rs.getInt("partnerRingId"), rs.getInt("partnerChrId"), rs.getInt("itemid"), rs.getString("partnerName"));
                ret.setEquipped(false);
                eq = new Equip(ring.getItemId(), ring.getPosition(), ring.getUniqueId(), ring.getFlag());
                rs.close();
            }

            return eq;
        } catch (SQLException ex) {
            //      log.error("Error loading ring from DB", ex);
            return null;
        }
    }

    /**
     *
     * @param ringId
     * @return
     */
    public static MapleRing loadFromDb(int ringId) {
        return loadFromDb(ringId, false);
    }

    /**
     *
     * @param ringId
     * @param equipped
     * @return
     */
    public static MapleRing loadFromDb(int ringId, boolean equipped) {
        try {
            Connection con = DatabaseConnection.getConnection(); // Get a connection to the database
            MapleRing ret;
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE ringId = ?") // Get details..
            ) {
                ps.setInt(1, ringId);
                ResultSet rs = ps.executeQuery();
                ret = null;
                if (rs.next()) {
                    ret = new MapleRing(ringId, rs.getInt("partnerRingId"), rs.getInt("partnerChrId"), rs.getInt("itemid"), rs.getString("partnerName"));
                    ret.setEquipped(equipped);
                }   rs.close();
            }

            return ret;
        } catch (SQLException ex) {
            ex.printStackTrace();

            return null;
        }
    }

    /**
     *
     * @param itemid
     * @param chr
     * @param player
     * @param id
     * @param ringId
     * @throws SQLException
     */
    public static void addToDB(int itemid, MapleCharacter chr, String player, int id, int[] ringId) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("INSERT INTO rings (ringId, itemid, partnerChrId, partnerName, partnerRingId) VALUES (?, ?, ?, ?, ?)");
        ps.setInt(1, ringId[0]);
        ps.setInt(2, itemid);
        ps.setInt(3, chr.getId());
        ps.setString(4, chr.getName());
        ps.setInt(5, ringId[1]);
        ps.executeUpdate();
        ps.close();

        ps = con.prepareStatement("INSERT INTO rings (ringId, itemid, partnerChrId, partnerName, partnerRingId) VALUES (?, ?, ?, ?, ?)");
        ps.setInt(1, ringId[1]);
        ps.setInt(2, itemid);
        ps.setInt(3, id);
        ps.setString(4, player);
        ps.setInt(5, ringId[0]);
        ps.executeUpdate();
        ps.close();
    }

    /**
     *
     * @param itemid
     * @param partner1
     * @param partner2
     * @param msg
     * @param id2
     * @param sn
     * @return
     */
    public static int createRing(int itemid, MapleCharacter partner1, String partner2, String msg, int id2, int sn) {
        try {
            if (partner1 == null) {
                return -2;
            } else if (id2 <= 0) {
                return -1;
            }
            return makeRing(itemid, partner1, partner2, id2, msg, sn);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     *
     * @param itemid
     * @param partner1
     * @param partner2
     * @param id2
     * @param msg
     * @param sn
     * @return
     * @throws Exception
     */
    public static int makeRing(int itemid, MapleCharacter partner1, String partner2, int id2, String msg, int sn) throws Exception { //return partner1 the id
        int[] ringID = {MapleInventoryIdentifier.getInstance(), MapleInventoryIdentifier.getInstance()};
        //[1] = partner1, [0] = partner2
        try {
            addToDB(itemid, partner1, partner2, id2, ringID);
        } catch (MySQLIntegrityConstraintViolationException mslcve) {
            return 0;
        }
        MapleInventoryManipulator.addRing(partner1, itemid, ringID[1], sn);
        partner1.getCashInventory().gift(id2, partner1.getName(), msg, sn, ringID[0]);
        return 1;
    }

    /**
     *
     * @return
     */
    public int getRingId() {
        return ringId;
    }

    /**
     *
     * @return
     */
    public int getPartnerRingId() {
        return ringId2;
    }

    /**
     *
     * @return
     */
    public int getPartnerChrId() {
        return partnerId;
    }

    /**
     *
     * @return
     */
    public int getItemId() {
        return itemId;
    }

    /**
     *
     * @return
     */
    public boolean isEquipped() {
        return equipped;
    }

    /**
     *
     * @param equipped
     */
    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    /**
     *
     * @return
     */
    public String getPartnerName() {
        return partnerName;
    }

    /**
     *
     * @param partnerName
     */
    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MapleRing) {
            return ((MapleRing) o).getRingId() == getRingId();
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + this.ringId;
        return hash;
    }

    /**
     *
     * @param player
     */
    public static void removeRingFromDb(MapleCharacter player) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM rings WHERE partnerChrId = ?");
            ps.setInt(1, player.getId());
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                ps.close();
                rs.close();
                return;
            }
            int otherId = rs.getInt("partnerRingId");
            int otherotherId = rs.getInt("ringId");
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM rings WHERE ringId = ? OR ringId = ?");
            ps.setInt(1, otherotherId);
            ps.setInt(2, otherId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException sex) {
            sex.printStackTrace();
        }
    }

    /**
     *
     */
    public static class RingComparator implements Comparator<MapleRing>, Serializable {

        @Override
        public int compare(MapleRing o1, MapleRing o2) {
            if (o1.ringId < o2.ringId) {
                return -1;
            } else if (o1.ringId == o2.ringId) {
                return 0;
            } else {
                return 1;
            }
        }
    }
}
