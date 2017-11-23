package tools;

import java.util.Random;

/**
 *
 * @author zjj
 */
public class Randomizer {

    private static final Random rand = new Random();

    /**
     *
     * @return
     */
    public static int nextInt() {
        return rand.nextInt();
    }

    /**
     *
     * @param arg0
     * @return
     */
    public static int nextInt(int arg0) {
        return rand.nextInt(arg0);
    }

    /**
     *
     * @param bytes
     */
    public static void nextBytes(byte[] bytes) {
        rand.nextBytes(bytes);
    }

    /**
     *
     * @return
     */
    public static boolean nextBoolean() {
        return rand.nextBoolean();
    }

    /**
     *
     * @return
     */
    public static double nextDouble() {
        return rand.nextDouble();
    }

    /**
     *
     * @return
     */
    public static float nextFloat() {
        return rand.nextFloat();
    }

    /**
     *
     * @return
     */
    public static long nextLong() {
        return rand.nextLong();
    }

    /**
     *
     * @param lbound
     * @param ubound
     * @return
     */
    public static int rand(int lbound, int ubound) {
        return nextInt(ubound - lbound + 1) + lbound;
    }

    /**
     *
     * @param rate
     * @return
     */
    public static boolean isSuccess(int rate) {
        return rate > nextInt(100);
    }
}
