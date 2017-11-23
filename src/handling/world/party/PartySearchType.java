package handling.world.party;

/**
 *
 * @author zjj
 */
public enum PartySearchType {

    /**
     *
     */
    Kerning(20, 200, 1_000, false),

    /**
     *
     */
    Ludi(30, 200, 1_001, false),

    /**
     *
     */
    Orbis(50, 200, 1_002, false),

    /**
     *
     */
    Pirate(60, 200, 1_003, false),

    /**
     *
     */
    Magatia(70, 200, 1_004, false),

    /**
     *
     */
    ElinForest(40, 200, 1_005, false),

    /**
     *
     */
    Pyramid(40, 200, 1_008, false),

    /**
     *
     */
    Dragonica(100, 200, 1_009, false), //what the fk

    /**
     *
     */
    Hoblin(80, 200, 1_011, false),

    /**
     *
     */
    Henesys(10, 200, 1_012, false),

    /**
     *
     */
    武陵道场(25, 200, 1_013, false), //武陵道场

    /**
     *
     */
    Balrog_Normal(50, 250, 2_000, true), //蝙蝠魔

    /**
     *
     */
    Zakum(50, 250, 2_002, true), //扎昆远征

    /**
     *
     */
    Horntail(80, 250, 2_003, true), //黑龙远征

    /**
     *
     */
    PinkBean(140, 250, 2_004, true), //品克缤

    /**
     *
     */
    ChaosZakum(100, 250, 2_005, true), //进阶扎昆远征

    /**
     *
     */
    ChaosHT(110, 250, 2_006, true), //进阶黑龙远征

    /**
     *
     */
    VonLeon(120, 250, 2_007, true), //班·雷昂

    /**
     *
     */
    Cygnus(170, 250, 2_008, true), //希纳斯女皇

    /**
     *
     */
    Akyrum(120, 250, 2_009, true), //阿卡伊勒远征队

    /**
     *
     */
    Hillah(120, 250, 2_010, true), //希拉远征队

    /**
     *
     */
    ChaosPB(170, 250, 2_011, true), //混沌品克缤

    /**
     *
     */
    CWKPQ(90, 250, 2_011, true);

    /**
     *
     */
    public final int id;

    /**
     *
     */
    public final int minLevel;

    /**
     *
     */
    public final int maxLevel;

    /**
     *
     */
    public final int timeLimit;

    /**
     *
     */
    public final boolean exped;

    PartySearchType(int minLevel, int maxLevel, int value, boolean exped) {
        this.id = value;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.exped = exped;
        this.timeLimit = exped ? 20 : 5;
    }

    /**
     *
     * @param id
     * @return
     */
    public static PartySearchType getById(int id) {
        for (PartySearchType pst : PartySearchType.values()) {
            if (pst.id == id) {
                return pst;
            }
        }
        return null;
    }
}
