
package client.inventory;

import database.DatabaseConnection;
import java.awt.Point;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleItemInformationProvider;
import server.movement.AbsoluteLifeMovement;
import server.movement.LifeMovement;
import server.movement.LifeMovementFragment;

/**
 *
 * @author zjj
 */
public class MaplePet implements Serializable {

    /**
     *
     */
    public static enum PetFlag {

        /**
         *
         */
        ITEM_PICKUP(0x01, 5_190_000, 5_191_000),

        /**
         *
         */
        EXPAND_PICKUP(0x02, 5_190_002, 5_191_002), //idk

        /**
         *
         */
        AUTO_PICKUP(0x04, 5_190_003, 5_191_003), //idk

        /**
         *
         */
        UNPICKABLE(0x08, 5_190_005, -1), //not coded

        /**
         *
         */
        LEFTOVER_PICKUP(0x10, 5_190_004, 5_191_004), //idk

        /**
         *
         */
        HP_CHARGE(0x20, 5_190_001, 5_191_001),

        /**
         *
         */
        MP_CHARGE(0x40, 5_190_006, -1),

        /**
         *
         */
        PET_BUFF(0x80, -1, -1), //idk

        /**
         *
         */
        PET_DRAW(0x100, 5_190_007, -1), //nfs

        /**
         *
         */
        PET_DIALOGUE(0x200, 5_190_008, -1); //nfs

        private final int i, item, remove;

        private PetFlag(int i, int item, int remove) {
            this.i = i;
            this.item = item;
            this.remove = remove;
        }

        /**
         *
         * @return
         */
        public final int getValue() {
            return i;
        }

        /**
         *
         * @param flag
         * @return
         */
        public final boolean check(int flag) {
            return (flag & i) == i;
        }

        /**
         *
         * @param itemId
         * @return
         */
        public static final PetFlag getByAddId(final int itemId) {
            for (PetFlag flag : PetFlag.values()) {
                if (flag.item == itemId) {
                    return flag;
                }
            }
            return null;
        }

        /**
         *
         * @param itemId
         * @return
         */
        public static final PetFlag getByDelId(final int itemId) {
            for (PetFlag flag : PetFlag.values()) {
                if (flag.remove == itemId) {
                    return flag;
                }
            }
            return null;
        }
    }

    private static final long serialVersionUID = 9_179_541_993_413_738_569L;
    private String name;
    private int Fh = 0, stance = 0, uniqueid, petitemid, secondsLeft = 0;
    private Point pos;
    private byte fullness = 100, level = 1, summoned = 0;
    private short inventorypos = 0, closeness = 0, flags = 0;
    private boolean changed = false;

    private MaplePet(final int petitemid, final int uniqueid) {
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
    }

    private MaplePet(final int petitemid, final int uniqueid, final short inventorypos) {
        this.petitemid = petitemid;
        this.uniqueid = uniqueid;
        this.inventorypos = inventorypos;
    }

    /**
     *
     * @param itemid
     * @param petid
     * @param inventorypos
     * @return
     */
    public static final MaplePet loadFromDb(final int itemid, final int petid, final short inventorypos) {
        try {
            final MaplePet ret = new MaplePet(itemid, petid, inventorypos);

            Connection con = DatabaseConnection.getConnection(); // Get a connection to the database
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM pets WHERE petid = ?") // Get pet details..
            ) {
                ps.setInt(1, petid);
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    return null;
                }   ret.setName(rs.getString("name"));
                ret.setCloseness(rs.getShort("closeness"));
                ret.setLevel(rs.getByte("level"));
                ret.setFullness(rs.getByte("fullness"));
                ret.setSecondsLeft(rs.getInt("seconds"));
                ret.setFlags(rs.getShort("flags"));
                ret.changed = false;
                rs.close();
            }

            return ret;
        } catch (SQLException ex) {
            Logger.getLogger(MaplePet.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     *
     */
    public final void saveToDb() {
        if (!changed) {
            return;
        }
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, seconds = ?, flags = ? WHERE petid = ?")) {
            ps.setString(1, name); // Set name
            ps.setByte(2, level); // Set Level
            ps.setShort(3, closeness); // Set Closeness
            ps.setByte(4, fullness); // Set Fullness
            ps.setInt(5, secondsLeft);
            ps.setShort(6, flags);
            ps.setInt(7, uniqueid); // Set ID
            ps.executeUpdate(); // Execute statement
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }
        changed = false;
    }

    /**
     *
     * @param itemid
     * @param uniqueid
     * @return
     */
    public static final MaplePet createPet(final int itemid, final int uniqueid) {
        return createPet(itemid, MapleItemInformationProvider.getInstance().getName(itemid), 1, 0, 100, uniqueid, itemid == 5_000_054 ? 18_000 : 0);
    }

    /**
     *
     * @param itemid
     * @param name
     * @param level
     * @param closeness
     * @param fullness
     * @param uniqueid
     * @param secondsLeft
     * @return
     */
    public static final MaplePet createPet(int itemid, String name, int level, int closeness, int fullness, int uniqueid, int secondsLeft) {
        if (uniqueid <= -1) { //wah
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        short ret1 = MapleItemInformationProvider.getInstance().getPetFlagInfo(itemid);
        try ( // Commit to db first
                PreparedStatement pse = DatabaseConnection.getConnection().prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, seconds, flags) VALUES (?, ?, ?, ?, ?, ?, ?)")) {             pse.setInt(1, uniqueid);
            pse.setString(2, name);
            pse.setByte(3, (byte) level);
            pse.setShort(4, (short) closeness);
            pse.setByte(5, (byte) fullness);
            pse.setInt(6, secondsLeft);
            pse.setShort(7, (short) ret1); //flags
            pse.executeUpdate();
        } catch (final SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        final MaplePet pet = new MaplePet(itemid, uniqueid);
        pet.setName(name);
        pet.setLevel(level);
        pet.setFullness(fullness);
        pet.setCloseness(closeness);
        pet.setFlags(ret1);
        pet.setSecondsLeft(secondsLeft);

        return pet;
    }

    /**
     *
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public final void setName(final String name) {
        this.name = name;
        this.changed = true;
    }

    /**
     *
     * @return
     */
    public final boolean getSummoned() {
        return summoned > 0;
    }

    /**
     *
     * @return
     */
    public final byte getSummonedValue() {
        return summoned;
    }

    /**
     *
     * @param summoned
     */
    public final void setSummoned(final int summoned) {
        this.summoned = (byte) summoned;
    }

    /**
     *
     * @return
     */
    public final short getInventoryPosition() {
        return inventorypos;
    }

    /**
     *
     * @param inventorypos
     */
    public final void setInventoryPosition(final short inventorypos) {
        this.inventorypos = inventorypos;
    }

    /**
     *
     * @return
     */
    public int getUniqueId() {
        return uniqueid;
    }

    /**
     *
     * @return
     */
    public final short getCloseness() {
        return closeness;
    }

    /**
     *
     * @param closeness
     */
    public final void setCloseness(int closeness) {
        if (closeness >= 2_147_483_647 || closeness <= 0) {
            closeness = 1;
        }
        this.closeness = (short) closeness;
        this.changed = true;
    }

    /**
     *
     * @return
     */
    public final byte getLevel() {
        return level;
    }

    /**
     *
     * @param level
     */
    public final void setLevel(final int level) {
        this.level = (byte) level;
        this.changed = true;
    }

    /**
     *
     * @return
     */
    public final byte getFullness() {
        return fullness;
    }

    /**
     *
     * @param fullness
     */
    public final void setFullness(final int fullness) {
        this.fullness = (byte) fullness;
        this.changed = true;
    }

    /**
     *
     * @return
     */
    public final short getFlags() {
        return flags;
    }

    /**
     *
     * @param fffh
     */
    public final void setFlags(final int fffh) {
        this.flags = (short) fffh;
        this.changed = true;
    }

    /**
     *
     * @return
     */
    public final int getFh() {
        return Fh;
    }

    /**
     *
     * @param Fh
     */
    public final void setFh(final int Fh) {
        this.Fh = Fh;
    }

    /**
     *
     * @return
     */
    public final Point getPos() {
        return pos;
    }

    /**
     *
     * @param pos
     */
    public final void setPos(final Point pos) {
        this.pos = pos;
    }

    /**
     *
     * @return
     */
    public final int getStance() {
        return stance;
    }

    /**
     *
     * @param stance
     */
    public final void setStance(final int stance) {
        this.stance = stance;
    }

    /**
     *
     * @return
     */
    public final int getPetItemId() {
        return petitemid;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public final boolean canConsume(final int itemId) {
        final MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        for (final int petId : mii.petsCanConsume(itemId)) {
            if (petId == petitemid) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param movement
     */
    public final void updatePosition(final List<LifeMovementFragment> movement) {
        for (final LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    setPos(((LifeMovement) move).getPosition());
                }
                setStance(((LifeMovement) move).getNewstate());
            }
        }
    }

    /**
     *
     * @return
     */
    public final int getSecondsLeft() {
        return secondsLeft;
    }

    /**
     *
     * @param sl
     */
    public final void setSecondsLeft(int sl) {
        this.secondsLeft = sl;
        this.changed = true;
    }
}
