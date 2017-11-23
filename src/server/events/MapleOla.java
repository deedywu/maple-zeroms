
package server.events;

import client.MapleCharacter;
import java.util.concurrent.ScheduledFuture;
import server.Randomizer;
import server.Timer.EventTimer;
import tools.MaplePacketCreator;

/**
 *
 * @author zjj
 */
public class MapleOla extends MapleEvent {

    private static final long serialVersionUID = 845_748_150_824L;
    private long time = 600_000; //reduce for less time
    private long timeStarted = 0;
    private transient ScheduledFuture<?> olaSchedule;
    private int[] stages = new int[3];
    //stg1 = ch00-ch04 = 5 ports
    //stg2 = ch00-ch07 = 8 ports
    //stg3 = ch00-ch15 = 16 ports

    /**
     *
     * @param channel
     * @param mapid
     */
    public MapleOla(final int channel, final int[] mapid) {
        super(channel, mapid);
    }

    /**
     *
     * @param chr
     */
    @Override
    public void finished(final MapleCharacter chr) {
        givePrize(chr);
        //chr.finishAchievement(21);
    }

    /**
     *
     * @param chr
     */
    @Override
    public void onMapLoad(MapleCharacter chr) {
        if (isTimerStarted()) {
            chr.getClient().getSession().write(MaplePacketCreator.getClock((int) (getTimeLeft() / 1_000)));
        }
    }

    /**
     *
     */
    @Override
    public void startEvent() { // TODO: Messages
        unreset();
        super.reset(); //isRunning = true
        broadcast(MaplePacketCreator.getClock((int) (time / 1_000)));
        this.timeStarted = System.currentTimeMillis();

        olaSchedule = EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < mapid.length; i++) {
                    for (MapleCharacter chr : getMap(i).getCharactersThreadsafe()) {
                        warpBack(chr);
                    }
                    unreset();
                }
            }
        }, this.time);

        broadcast(MaplePacketCreator.serverNotice(0, "门已打开。按箭头↑键进入入口."));
    }

    /**
     *
     * @return
     */
    public boolean isTimerStarted() {
        return timeStarted > 0;
    }

    /**
     *
     * @return
     */
    public long getTime() {
        return time;
    }

    /**
     *
     */
    public void resetSchedule() {
        this.timeStarted = 0;
        if (olaSchedule != null) {
            olaSchedule.cancel(false);
        }
        olaSchedule = null;
    }

    /**
     *
     */
    @Override
    public void reset() {
        super.reset();
        resetSchedule();
        getMap(0).getPortal("join00").setPortalState(false);
        stages = new int[]{0, 0, 0};
    }

    /**
     *
     */
    @Override
    public void unreset() {
        super.unreset();
        resetSchedule();
        getMap(0).getPortal("join00").setPortalState(true);
        stages = new int[]{Randomizer.nextInt(5), Randomizer.nextInt(8), Randomizer.nextInt(15)};
    }

    /**
     *
     * @return
     */
    public long getTimeLeft() {
        return time - (System.currentTimeMillis() - timeStarted);
    }

    /**
     *
     * @param portalName
     * @param mapid
     * @return
     */
    public boolean isCharCorrect(String portalName, int mapid) {
        final int st = stages[(mapid % 10) - 1];
        return portalName.equals("ch" + (st < 10 ? "0" : "") + st);
    }
}
