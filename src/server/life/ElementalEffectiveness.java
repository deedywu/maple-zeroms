package server.life;

/**
 *
 * @author zjj
 */
public enum ElementalEffectiveness {

    /**
     *
     */
    NORMAL, 

    /**
     *
     */
    IMMUNE, 

    /**
     *
     */
    STRONG, 

    /**
     *
     */
    WEAK, 

    /**
     *
     */
    NEUTRAL;

    /**
     *
     * @param num
     * @return
     */
    public static ElementalEffectiveness getByNumber(int num) {
        switch (num) {
            case 1:
                return IMMUNE;
            case 2:
                return STRONG;
            case 3:
                return WEAK;
            case 4:
                return NEUTRAL;
            default:
                throw new IllegalArgumentException("Unkown effectiveness: " + num);
        }
    }
}
