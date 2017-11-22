package client;

public enum MapleStat {

    SKIN(0x1),
    FACE(0x2),
    HAIR(0x4),
    LEVEL(0x40),
    JOB(0x80),
    STR(0x100),
    DEX(0x200),
    INT(0x400),
    LUK(0x800),
    HP(0x1000),
    MAXHP(0x2000),
    MP(0x4000),
    MAXMP(0x8000),
    AVAILABLEAP(0x10000),
    AVAILABLESP(0x20000),
    EXP(0x40000),
    FAME(0x80000),
    MESO(0x100000),
    PET(0x200008),
    GACHAPONEXP(0x80000), // int
    疲劳(0x80000), //疲劳
    领袖(0x100000), //领袖
    洞察(0x200000), //洞察
    意志(0x400000), //意志
    手技(0x800000), //手技
    感性(0x1000000), //感性
    魅力(0x2000000), //魅力
    TODAYS_TRAITS(0x4000000), //今日获得
    TRAIT_LIMIT(0x8000000),
    BATTLE_EXP(0x10000000),
    BATTLE_RANK(0x20000000),
    BATTLE_POINTS(0x40000000),
    ICE_GAGE((int) 0x80000000L),
    VIRTUE((int) 0x100000000L),
    性别((int) 0x200000000L);
    private final int i;

    private MapleStat(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }

    public static final MapleStat getByValue(final int value) {
        for (final MapleStat stat : MapleStat.values()) {
            if (stat.i == value) {
                return stat;
            }
        }
        return null;
    }

    public static enum Temp {

        STR(0x1),
        DEX(0x2),
        INT(0x4),
        LUK(0x8),
        WATK(0x10),
        WDEF(0x20),
        MATK(0x40),
        MDEF(0x80),
        ACC(0x100),
        AVOID(0x200),
        SPEED(0x400),
        JUMP(0x800);
        private final int i;

        private Temp(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }
}
