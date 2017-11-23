
package server.events;

import client.MapleCharacter;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.RandomRewards;
import server.Timer.EventTimer;
import server.maps.MapleMap;
import server.maps.SavedLocationType;
import tools.MaplePacketCreator;

/**
 *
 * @author zjj
 */
public abstract class MapleEvent {

    /**
     *
     */
    protected int[] mapid;

    /**
     *
     */
    protected int channel;

    /**
     *
     */
    protected boolean isRunning = false;

    /**
     *
     * @param channel
     * @param mapid
     */
    public MapleEvent(final int channel, final int[] mapid) {
        this.channel = channel;
        this.mapid = mapid;
    }

    /**
     *
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     *
     * @param i
     * @return
     */
    public MapleMap getMap(final int i) {
        return getChannelServer().getMapFactory().getMap(mapid[i]);
    }

    /**
     *
     * @return
     */
    public ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    /**
     *
     * @param packet
     */
    public void broadcast(final MaplePacket packet) {
        for (int i = 0; i < mapid.length; i++) {
            getMap(i).broadcastMessage(packet);
        }
    }

    /**
     *
     * @param chr
     */
    public void givePrize(final MapleCharacter chr) {
        final int reward = RandomRewards.getInstance().getEventReward();
        switch (reward) {
            case 0:
                chr.gainMeso(666_666, true, false, false);
                chr.dropMessage(5, "你获得 666666 冒险币");
                break;
            case 1:
                chr.gainMeso(999_999, true, false, false);
                chr.dropMessage(5, "你获得 999999 冒险币");
                break;
            case 2:
                chr.modifyCSPoints(0, 500, false);
                // chr.gainMeso(666666, true, false, false);
                chr.dropMessage(5, "你获得 500 点卷");
                break;
            case 3:
                chr.addFame(5);
                chr.dropMessage(5, "你获得 5 人气");
                break;
            default:
                break;
        }

        //  final int quantity = (max_quantity > 1 ? Randomizer.nextInt(max_quantity) : 0) + 1;
        if (MapleInventoryManipulator.checkSpace(chr.getClient(), 4_032_226, 3, "")) {
            MapleInventoryManipulator.addById(chr.getClient(), 4_032_226, (short) 3, (byte) 0);
            chr.dropMessage(5, "你获得 3 个黄金猪猪");
        } else {
            // givePrize(chr); //do again until they get
            chr.gainMeso(888_888, true, false, false);
            chr.dropMessage(5, "由于你背包满了。所以只能给予你冒险币！");
        }
        /*     else {
            int max_quantity = 1;
            switch (reward) {
                case 5062000:
                    max_quantity = 3;
                    break;
                case 5220000:
                    max_quantity = 25;
                    break;
                case 4031307:
                case 5050000:
                    max_quantity = 5;
                    break;
                case 2022121:
                    max_quantity = 10;
                    break;
            }
            final int quantity = (max_quantity > 1 ? Randomizer.nextInt(max_quantity) : 0) + 1;
            if (MapleInventoryManipulator.checkSpace(chr.getClient(), reward, quantity, "")) {
                MapleInventoryManipulator.addById(chr.getClient(), reward, (short) quantity, (byte) 0);
            } else {
                // givePrize(chr); //do again until they get
                chr.gainMeso(100000, true, false, false);
                chr.dropMessage(5, "參加奖 100000 冒险币");
            }
            //5062000 = 1-3
            //5220000 = 1-25
            //5050000 = 1-5
            //2022121 = 1-10
            //4031307 = 1-5
        }*/
    }

    /**
     *
     * @param chr
     */
    public void finished(MapleCharacter chr) { //most dont do shit here
    }

    /**
     *
     * @param chr
     */
    public void onMapLoad(MapleCharacter chr) { //most dont do shit here
    }

    /**
     *
     */
    public void startEvent() {
    }

    /**
     *
     * @param chr
     */
    public void warpBack(MapleCharacter chr) {
        int map = chr.getSavedLocation(SavedLocationType.EVENT);
        if (map <= -1) {
            map = 104_000_000;
        }
        final MapleMap mapp = chr.getClient().getChannelServer().getMapFactory().getMap(map);
        chr.changeMap(mapp, mapp.getPortal(0));
    }

    /**
     *
     */
    public void reset() {
        isRunning = true;
    }

    /**
     *
     */
    public void unreset() {
        isRunning = false;
    }

    /**
     *
     * @param cserv
     * @param auto
     */
    public static final void setEvent(final ChannelServer cserv, final boolean auto) {
        if (auto) {
            for (MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = cserv.getEvent(t);
                if (e.isRunning) {
                    for (int i : e.mapid) {
                        if (cserv.getEvent() == i) {
                            e.broadcast(MaplePacketCreator.serverNotice(0, "距离活动开始只剩下一分钟!"));
                            e.broadcast(MaplePacketCreator.getClock(60));
                            EventTimer.getInstance().schedule(new Runnable() {

                                @Override
                                public void run() {
                                    e.startEvent();
                                }
                            }, 60_000);
                            break;
                        }
                    }
                }
            }
        }
        cserv.setEvent(-1);
    }

    /**
     *
     * @param chr
     * @param channel
     */
    public static final void mapLoad(final MapleCharacter chr, final int channel) {
        if (chr == null) {
            return;
        } //o_o
        for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = ChannelServer.getInstance(channel).getEvent(t);
            if (e.isRunning) {
                if (chr.getMapId() == 109_050_000) { //finished map
                    e.finished(chr);
                }
                for (int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.onMapLoad(chr);
                    }
                }
            }
        }
    }

    /**
     *
     * @param chr
     */
    public static final void onStartEvent(final MapleCharacter chr) {
        for (MapleEventType t : MapleEventType.values()) {
            final MapleEvent e = chr.getClient().getChannelServer().getEvent(t);
            if (e.isRunning) {
                for (int i : e.mapid) {
                    if (chr.getMapId() == i) {
                        e.startEvent();
                        chr.dropMessage(5, String.valueOf(t) + " 活动开始");
                    }
                }
            }
        }
    }

    /**
     *
     * @param event
     * @param cserv
     * @return
     */
    public static final String scheduleEvent(final MapleEventType event, final ChannelServer cserv) {
        if (cserv.getEvent() != -1 || cserv.getEvent(event) == null) {
            return "该活动已经被禁止安排了.";
        }
        for (int i : cserv.getEvent(event).mapid) {
            if (cserv.getMapFactory().getMap(i).getCharactersSize() > 0) {
                return "该活动已经在执行中.";
            }
        }
        cserv.setEvent(cserv.getEvent(event).mapid[0]);
        cserv.getEvent(event).reset();
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "活动 " + String.valueOf(event) + " 即将在频道 " + cserv.getChannel() + " 举行 , 要参加的玩家请到频道 " + cserv.getChannel() + ".请找到自由市场相框活动npc并进入！").getBytes());
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "活动 " + String.valueOf(event) + " 即将在频道 " + cserv.getChannel() + " 举行 , 要参加的玩家请到频道 " + cserv.getChannel() + ".请找到自由市场相框活动npc并进入！").getBytes());
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(0, "活动 " + String.valueOf(event) + " 即将在频道 " + cserv.getChannel() + " 举行 , 要参加的玩家请到频道 " + cserv.getChannel() + ".请找到自由市场相框活动npc并进入！").getBytes());
        return "";
    }
}
