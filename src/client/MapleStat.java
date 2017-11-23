package client;

/**
 *
 * @author zjj
 */
public enum MapleStat {

    /**
     *
     */
    SKIN(0x1),

    /**
     *
     */
    FACE(0x2),

    /**
     *
     */
    HAIR(0x4),

    /**
     *
     */
    LEVEL(0x40),

    /**
     *
     */
    JOB(0x80),

    /**
     *
     */
    STR(0x100),

    /**
     *
     */
    DEX(0x200),

    /**
     *
     */
    INT(0x400),

    /**
     *
     */
    LUK(0x800),

    /**
     *
     */
    HP(0x1000),

    /**
     *
     */
    MAXHP(0x2000),

    /**
     *
     */
    MP(0x4000),

    /**
     *
     */
    MAXMP(0x8000),

    /**
     *
     */
    AVAILABLEAP(0x1_0000),

    /**
     *
     */
    AVAILABLESP(0x2_0000),

    /**
     *
     */
    EXP(0x4_0000),

    /**
     *
     */
    FAME(0x8_0000),

    /**
     *
     */
    MESO(0x10_0000),

    /**
     *
     */
    PET(0x20_0008),

    /**
     *
     */
    GACHAPONEXP(0x8_0000), // int

    /**
     *
     */
    疲劳(0x8_0000), //疲劳

    /**
     *
     */
    领袖(0x10_0000), //领袖

    /**
     *
     */
    洞察(0x20_0000), //洞察

    /**
     *
     */
    意志(0x40_0000), //意志

    /**
     *
     */
    手技(0x80_0000), //手技

    /**
     *
     */
    感性(0x100_0000), //感性

    /**
     *
     */
    魅力(0x200_0000), //魅力

    /**
     *
     */
    TODAYS_TRAITS(0x400_0000), //今日获得

    /**
     *
     */
    TRAIT_LIMIT(0x800_0000),

    /**
     *
     */
    BATTLE_EXP(0x1000_0000),

    /**
     *
     */
    BATTLE_RANK(0x2000_0000),

    /**
     *
     */
    BATTLE_POINTS(0x4000_0000),

    /**
     *
     */
    ICE_GAGE((int) 0x8000_0000L),

    /**
     *
     */
    VIRTUE((int) 0x1_0000_0000L),

    /**
     *
     */
    性别((int) 0x2_0000_0000L);
    private final int i;

    private MapleStat(int i) {
        this.i = i;
    }

    /**
     *
     * @return
     */
    public int getValue() {
        return i;
    }

    /**
     *
     * @param value
     * @return
     */
    public static final MapleStat getByValue(final int value) {
        for (final MapleStat stat : MapleStat.values()) {
            if (stat.i == value) {
                return stat;
            }
        }
        return null;
    }

    /**
     *
     */
    public static enum Temp {

        /**
         *
         */
        STR(0x1),

        /**
         *
         */
        DEX(0x2),

        /**
         *
         */
        INT(0x4),

        /**
         *
         */
        LUK(0x8),

        /**
         *
         */
        WATK(0x10),

        /**
         *
         */
        WDEF(0x20),

        /**
         *
         */
        MATK(0x40),

        /**
         *
         */
        MDEF(0x80),

        /**
         *
         */
        ACC(0x100),

        /**
         *
         */
        AVOID(0x200),

        /**
         *
         */
        SPEED(0x400),

        /**
         *
         */
        JUMP(0x800);
        private final int i;

        private Temp(int i) {
            this.i = i;
        }

        /**
         *
         * @return
         */
        public int getValue() {
            return i;
        }
    }
}
