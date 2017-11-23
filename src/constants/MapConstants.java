package constants;

/**
 *
 * @author zjj
 */
public class MapConstants {

    /**
     *
     * @param mapid
     * @return
     */
    public static boolean isStartingEventMap(final int mapid) {
        switch (mapid) {
            case 109_010_000:
            case 109_020_001:
            case 109_030_001:
            case 109_030_101:
            case 109_030_201:
            case 109_030_301:
            case 109_030_401:
            case 109_040_000:
            case 109_060_001:
            case 109_060_002:
            case 109_060_003:
            case 109_060_004:
            case 109_060_005:
            case 109_060_006:
            case 109_080_000:
            case 109_080_001:
            case 109_080_002:
            case 109_080_003:
                return true;
        }
        return false;
    }

    /**
     *
     * @param mapid
     * @return
     */
    public static boolean isEventMap(final int mapid) {
        return mapid >= 109_010_000 && mapid < 109_050_000 || mapid > 109_050_001 && mapid < 109_090_000;
    }
}
