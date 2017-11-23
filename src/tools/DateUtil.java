package tools;

import java.util.Date;
import java.util.SimpleTimeZone;

/**
 *
 * @author zjj
 */
public class DateUtil {

    private static final long FT_UT_OFFSET = 116_444_520_000_000_000L;

    /**
     *
     * @return
     */
    public static boolean isDST() {
        return SimpleTimeZone.getDefault().inDaylightTime(new Date());
    }

    /**
     *
     * @param timeStampinMillis
     * @return
     */
    public static long getFileTimestamp(long timeStampinMillis) {
        return getFileTimestamp(timeStampinMillis, false);
    }

    /**
     *
     * @param timeStampinMillis
     * @param roundToMinutes
     * @return
     */
    public static long getFileTimestamp(long timeStampinMillis, boolean roundToMinutes) {
        if (isDST()) {
            timeStampinMillis -= 3_600_000L;
        }
        timeStampinMillis += 50_400_000L;
        long time;
        if (roundToMinutes) {
            time = timeStampinMillis / 1_000L / 60L * 600_000_000L;
        } else {
            time = timeStampinMillis * 10_000L;
        }
        return time + 116_444_520_000_000_000L;
    }
}
