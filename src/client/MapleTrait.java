package client;

import static client.MapleTrait.MapleTraitType.values;
import constants.GameConstants;

/**
 *
 * @author zjj
 */
public class MapleTrait {

    private MapleTraitType type;
    private int totalExp = 0;
    private int localTotalExp = 0;
    private short exp = 0;
    private byte level = 0;

    /**
     *
     * @param t
     */
    public MapleTrait(MapleTraitType t) {
        this.type = t;
    }

    /**
     *
     * @param exp
     */
    public void setExp(int exp) {
        this.totalExp = exp;
        this.localTotalExp = exp;
        recalcLevel();
    }

    /**
     *
     * @param exp
     */
    public void addExp(int exp) {
        this.totalExp += exp;
        this.localTotalExp += exp;
        if (exp != 0) {
            recalcLevel();
        }
    }

    /**
     *
     * @return
     */
    public boolean recalcLevel() {
        if (this.totalExp < 0) {
            this.totalExp = 0;
            this.localTotalExp = 0;
            this.level = 0;
            this.exp = 0;
            return false;
        }
        int oldLevel = this.level;
        for (byte i = 0; i < 100; i = (byte) (i + 1)) {
            if (GameConstants.getTraitExpNeededForLevel(i) > this.localTotalExp) {
                this.exp = (short) (GameConstants.getTraitExpNeededForLevel(i) - this.localTotalExp);
                this.level = (byte) (i - 1);
                return this.level > oldLevel;
            }
        }
        this.exp = 0;
        this.level = 100;
        this.totalExp = GameConstants.getTraitExpNeededForLevel(this.level);
        this.localTotalExp = this.totalExp;
        return this.level > oldLevel;
    }

    /**
     *
     * @return
     */
    public int getLevel() {
        return this.level;
    }

    /**
     *
     * @return
     */
    public int getExp() {
        return this.exp;
    }

    /**
     *
     * @return
     */
    public int getTotalExp() {
        return this.totalExp;
    }

    /**
     *
     * @return
     */
    public int getLocalTotalExp() {
        return this.localTotalExp;
    }

    /**
     *
     * @param exp
     */
    public void addLocalExp(int exp) {
        this.localTotalExp += exp;
    }

    /**
     *
     */
    public void clearLocalExp() {
        this.localTotalExp = this.totalExp;
    }

    /**
     *
     * @return
     */
    public MapleTraitType getType() {
        return this.type;
    }

    /**
     *
     */
    public static enum MapleTraitType {

        /**
         *
         */
        charisma(500, MapleStat.领袖),

        /**
         *
         */
        insight(500, MapleStat.洞察),

        /**
         *
         */
        will(500, MapleStat.意志),

        /**
         *
         */
        craft(500, MapleStat.手技),

        /**
         *
         */
        sense(500, MapleStat.感性),

        /**
         *
         */
        charm(5_000, MapleStat.魅力);
        final int limit;
        final MapleStat stat;

        private MapleTraitType(int type, MapleStat theStat) {
            this.limit = type;
            this.stat = theStat;
        }

        /**
         *
         * @return
         */
        public int getLimit() {
            return this.limit;
        }

        /**
         *
         * @return
         */
        public MapleStat getStat() {
            return this.stat;
        }

        /**
         *
         * @param q
         * @return
         */
        public static MapleTraitType getByQuestName(String q) {
            String qq = q.substring(0, q.length() - 3);
            for (MapleTraitType t : values()) {
                if (t.name().equals(qq)) {
                    return t;
                }
            }
            return null;
        }
    }
}
