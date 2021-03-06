package scripting;

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import client.inventory.*;
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.guild.MapleGuild;
import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.List;
import server.*;
import server.Timer.EtcTimer;
import server.Timer.MapTimer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.Event_DojoAgent;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleReactor;
import server.maps.SavedLocationType;
import server.quest.MapleQuest;
import tools.MaplePacketCreator;
import tools.packet.PetPacket;
import tools.packet.UIPacket;

/**
 *
 * @author zjj
 */
public abstract class AbstractPlayerInteraction {

    private MapleClient c;

    /**
     *
     * @param c
     */
    public AbstractPlayerInteraction(final MapleClient c) {
        this.c = c;
    }

    /**
     *
     * @return
     */
    public final MapleClient getClient() {
        return c;
    }

    /**
     *
     * @return
     */
    public final MapleClient getC() {
        return c;
    }

    /**
     *
     * @return
     */
    public MapleCharacter getChar() {
        c.getPlayer().getInventory(MapleInventoryType.USE).listById(1).iterator();
        return c.getPlayer();
    }

    /**
     *
     * @return
     */
    public final ChannelServer getChannelServer() {
        return c.getChannelServer();
    }

    /**
     *
     * @return
     */
    public final MapleCharacter getPlayer() {
        return c.getPlayer();
    }

    /**
     *
     * @return
     */
    public final MapleMap getMap() {
        return c.getPlayer().getMap();
    }

    /**
     *
     * @param event
     * @return
     */
    public final EventManager getEventManager(final String event) {
        return c.getChannelServer().getEventSM().getEventManager(event);
    }

    /**
     *
     * @return
     */
    public final EventInstanceManager getEventInstance() {
        return c.getPlayer().getEventInstance();
    }

    /**
     *
     * @param name
     */
    public final void forceRemovePlayerByCharName(String name) {
        ChannelServer.forceRemovePlayerByCharName(name);
    }

    /**
     *
     * @param map
     */
    public final void warp(final int map) {
        final MapleMap mapz = getWarpMap(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    /**
     *
     * @param map
     */
    public final void warp_Instanced(final int map) {
        final MapleMap mapz = getMap_Instanced(map);
        try {
            c.getPlayer().changeMap(mapz, mapz.getPortal(Randomizer.nextInt(mapz.getPortals().size())));
        } catch (Exception e) {
            c.getPlayer().changeMap(mapz, mapz.getPortal(0));
        }
    }

    /**
     *
     * @param map
     * @param portal
     */
    public final void warp(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        if (portal != 0 && map == c.getPlayer().getMapId()) { //test
            final Point portalPos = new Point(c.getPlayer().getMap().getPortal(portal).getPosition());
            /*
             * if (portalPos.distanceSq(getPlayer().getPosition()) < 90000.0) {
             * //estimation
             * c.getSession().write(MaplePacketCreator.instantMapWarp((byte)
             * portal)); //until we get packet for far movement, this will do
             * c.getPlayer().checkFollow();
             * c.getPlayer().getMap().movePlayer(c.getPlayer(), portalPos); }
             * else {
             */
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            //   }
        } else {
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    /**
     *
     * @param map
     * @param portal
     */
    public final void warpS(final int map, final int portal) {
        final MapleMap mapz = getWarpMap(map);
        c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    /**
     *
     * @param map
     * @param portal
     */
    public final void warp(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109_060_000 || map == 109_060_002 || map == 109_060_004) {
            portal = mapz.getSnowballPortal();
        }
        if (map == c.getPlayer().getMapId()) { //test
            final Point portalPos = new Point(c.getPlayer().getMap().getPortal(portal).getPosition());
            /*
             * if (portalPos.distanceSq(getPlayer().getPosition()) < 90000.0) {
             * //estimation c.getPlayer().checkFollow();
             * c.getSession().write(MaplePacketCreator.instantMapWarp((byte)
             * c.getPlayer().getMap().getPortal(portal).getId()));
             * c.getPlayer().getMap().movePlayer(c.getPlayer(), new
             * Point(c.getPlayer().getMap().getPortal(portal).getPosition())); }
             * else {
             */
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
            //   }
        } else {
            c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
        }
    }

    /**
     *
     * @param map
     * @param portal
     */
    public final void warpS(final int map, String portal) {
        final MapleMap mapz = getWarpMap(map);
        if (map == 109_060_000 || map == 109_060_002 || map == 109_060_004) {
            portal = mapz.getSnowballPortal();
        }
        c.getPlayer().changeMap(mapz, mapz.getPortal(portal));
    }

    /**
     *
     * @param mapid
     * @param portal
     */
    public final void warpMap(final int mapid, final int portal) {
        final MapleMap map = getMap(mapid);
        for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
            chr.changeMap(map, map.getPortal(portal));
        }
    }

    /**
     *
     */
    public final void playPortalSE() {
        c.getSession().write(MaplePacketCreator.showOwnBuffEffect(0, 5));
    }

    private final MapleMap getWarpMap(final int map) {
        return ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(map);
    }

    /**
     *
     * @param map
     * @return
     */
    public final MapleMap getMap(final int map) {
        return getWarpMap(map);
    }

    /**
     *
     * @param map
     * @return
     */
    public final MapleMap getMap_Instanced(final int map) {
        return c.getPlayer().getEventInstance() == null ? getMap(map) : c.getPlayer().getEventInstance().getMapInstance(map);
    }

    /**
     *
     * @param MapID
     * @param MapID2
     */
    public final void spawnMap(int MapID, int MapID2) {
        for (ChannelServer chan : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                if (getC().getChannel() == chr.getClient().getChannel()) {
                    if (chr.getMapId() == MapID) {
                        warp(MapID2);
                    }
                }
            }
        }
    }

    /**
     *
     * @param MapID
     */
    public final void spawnMap(int MapID) {
        for (ChannelServer chan : ChannelServer.getAllInstances()) {
            for (MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                if (chr == null) {
                    continue;
                }
                if (getC().getChannel() == chr.getClient().getChannel()) {
                    if (chr.getMapId() == getMapId()) {
                        warp(MapID);
                    }
                }
            }
        }
    }

    /**
     *
     * @param id
     * @param qty
     */
    public void spawnMonster(final int id, final int qty) {
        spawnMob(id, qty, new Point(c.getPlayer().getPosition()));
    }

    /**
     *
     * @param id
     * @param qty
     * @param x
     * @param y
     * @param map
     */
    public final void spawnMobOnMap(final int id, final int qty, final int x, final int y, final int map) {
        for (int i = 0; i < qty; i++) {
            getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y));
        }
    }

    /**
     *
     * @param id
     * @param qty
     * @param x
     * @param y
     * @param map
     * @param hp
     */
    public final void spawnMobOnMap(final int id, final int qty, final int x, final int y, final int map, final int hp) {
        for (int i = 0; i < qty; i++) {
            getMap(map).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), new Point(x, y), hp);
        }
    }

    /**
     *
     * @param id
     * @param qty
     * @param x
     * @param y
     */
    public final void spawnMob(final int id, final int qty, final int x, final int y) {
        spawnMob(id, qty, new Point(x, y));
    }

    /**
     *
     * @param id
     * @param mapid
     * @param x
     * @param y
     */
    public final void spawnMob_map(final int id, int mapid, final int x, final int y) {
        spawnMob_map(id, mapid, new Point(x, y));
    }

    /**
     *
     * @param id
     * @param mapid
     * @param pos
     */
    public final void spawnMob_map(final int id, final int mapid, final Point pos) {
        c.getChannelServer().getMapFactory().getMap(mapid).spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
    }

    /**
     *
     * @param id
     * @param x
     * @param y
     */
    public final void spawnMob(final int id, final int x, final int y) {
        spawnMob(id, 1, new Point(x, y));
    }

    /**
     *
     * @param id
     * @param qty
     * @param pos
     */
    public final void spawnMob(final int id, final int qty, final Point pos) {
        for (int i = 0; i < qty; i++) {
            c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    /**
     *
     * @param ids
     */
    public final void killMob(int ids) {
        c.getPlayer().getMap().killMonster(ids);
    }

    /**
     *
     */
    public final void killAllMob() {
        c.getPlayer().getMap().killAllMonsters(true);
    }

    /**
     *
     * @param delta
     */
    public final void addHP(final int delta) {
        c.getPlayer().addHP(delta);
    }

    /**
     *
     * @param type
     * @param x
     */
    public final void setPlayerStat(final String type, int x) {
        switch (type) {
            case "LVL":
                c.getPlayer().setLevel((short) x);
                break;
            case "STR":
                c.getPlayer().getStat().setStr((short) x);
                break;
            case "DEX":
                c.getPlayer().getStat().setDex((short) x);
                break;
            case "INT":
                c.getPlayer().getStat().setInt((short) x);
                break;
            case "LUK":
                c.getPlayer().getStat().setLuk((short) x);
                break;
            case "HP":
                c.getPlayer().getStat().setHp(x);
                break;
            case "MP":
                c.getPlayer().getStat().setMp(x);
                break;
            case "MAXHP":
                c.getPlayer().getStat().setMaxHp((short) x);
                break;
            case "MAXMP":
                c.getPlayer().getStat().setMaxMp((short) x);
                break;
            case "RAP":
                c.getPlayer().setRemainingAp((short) x);
                break;
            case "RSP":
                c.getPlayer().setRemainingSp((short) x);
                break;
            case "GID":
                c.getPlayer().setGuildId(x);
                break;
            case "GRANK":
                c.getPlayer().setGuildRank((byte) x);
                break;
            case "ARANK":
                c.getPlayer().setAllianceRank((byte) x);
                break;
            case "GENDER":
                c.getPlayer().setGender((byte) x);
                break;
            case "FACE":
                c.getPlayer().setFace(x);
                break;
            case "HAIR":
                c.getPlayer().setHair(x);
                break;
            default:
                break;
        }
    }

    /**
     *
     * @param type
     * @return
     */
    public final int getPlayerStat(final String type) {
        switch (type) {
            case "LVL":
                return c.getPlayer().getLevel();
            case "STR":
                return c.getPlayer().getStat().getStr();
            case "DEX":
                return c.getPlayer().getStat().getDex();
            case "INT":
                return c.getPlayer().getStat().getInt();
            case "LUK":
                return c.getPlayer().getStat().getLuk();
            case "HP":
                return c.getPlayer().getStat().getHp();
            case "MP":
                return c.getPlayer().getStat().getMp();
            case "MAXHP":
                return c.getPlayer().getStat().getMaxHp();
            case "MAXMP":
                return c.getPlayer().getStat().getMaxMp();
            case "RAP":
                return c.getPlayer().getRemainingAp();
            case "RSP":
                return c.getPlayer().getRemainingSp();
            case "GID":
                return c.getPlayer().getGuildId();
            case "GRANK":
                return c.getPlayer().getGuildRank();
            case "ARANK":
                return c.getPlayer().getAllianceRank();
            case "GM":
                return c.getPlayer().isGM() ? 1 : 0;
            case "ADMIN":
                return c.getPlayer().isAdmin() ? 1 : 0;
            case "GENDER":
                return c.getPlayer().getGender();
            case "FACE":
                return c.getPlayer().getFace();
            case "HAIR":
                return c.getPlayer().getHair();
            default:
                break;
        }
        return -1;
    }

    /**
     *
     * @return
     */
    public final String getName() {
        return c.getPlayer().getName();
    }

    /**
     *
     * @param itemid
     * @return
     */
    public final boolean haveItem(final int itemid) {
        return haveItem(itemid, 1);
    }

    /**
     *
     * @param itemid
     * @param quantity
     * @return
     */
    public final boolean haveItem(final int itemid, final int quantity) {
        return haveItem(itemid, quantity, false, true);
    }

    /**
     *
     * @param itemid
     * @param quantity
     * @param checkEquipped
     * @param greaterOrEquals
     * @return
     */
    public final boolean haveItem(final int itemid, final int quantity, final boolean checkEquipped, final boolean greaterOrEquals) {
        return c.getPlayer().haveItem(itemid, quantity, checkEquipped, greaterOrEquals);
    }

    /**
     *
     * @return
     */
    public final boolean canHold() {
        for (int i = 1; i <= 5; i++) {
            if (c.getPlayer().getInventory(MapleInventoryType.getByType((byte) i)).getNextFreeSlot() <= -1) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public final boolean canHold(final int itemid) {
        return c.getPlayer().getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    /**
     *
     * @param itemid
     * @param quantity
     * @return
     */
    public final boolean canHold(final int itemid, final int quantity) {
        return MapleInventoryManipulator.checkSpace(c, itemid, quantity, "");
    }

    /**
     *
     * @param id
     * @return
     */
    public final MapleQuestStatus getQuestRecord(final int id) {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id));
    }

    /**
     *
     * @param id
     * @return
     */
    public final byte getQuestStatus(final int id) {
        return c.getPlayer().getQuestStatus(id);
    }

    /**
     *
     * @param id
     */
    public void completeQuest(int id) {
        c.getPlayer().setQuestAdd(id);
    }

    /**
     *
     * @param id
     * @return
     */
    public final boolean isQuestActive(final int id) {
        return getQuestStatus(id) == 1;
    }

    /**
     *
     * @param id
     * @return
     */
    public final boolean isQuestFinished(final int id) {
        return getQuestStatus(id) == 2;
    }

    /**
     *
     * @param msg
     */
    public final void showQuestMsg(final String msg) {
        c.getSession().write(MaplePacketCreator.showQuestMsg(msg));
    }

    /**
     *
     * @param id
     * @param data
     */
    public final void forceStartQuest(final int id, final String data) {
        MapleQuest.getInstance(id).forceStart(c.getPlayer(), 0, data);
    }

    /**
     *
     * @param id
     * @param data
     * @param filler
     */
    public final void forceStartQuest(final int id, final int data, final boolean filler) {
        MapleQuest.getInstance(id).forceStart(c.getPlayer(), 0, filler ? String.valueOf(data) : null);
    }

    /**
     *
     */
    public void clearAranPolearm() {
        this.c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).removeItem((byte) -11);
    }

    /**
     *
     * @param id
     */
    public void forceStartQuest(final int id) {
        MapleQuest.getInstance(id).forceStart(c.getPlayer(), 0, null);
    }

    /**
     *
     * @param id
     */
    public void forceCompleteQuest(final int id) {
        MapleQuest.getInstance(id).forceComplete(getPlayer(), 0);
    }

    /**
     *
     * @param npcId
     */
    public void spawnNpc(final int npcId) {
        c.getPlayer().getMap().spawnNpc(npcId, c.getPlayer().getPosition());
    }

    /**
     *
     * @param npcId
     * @param x
     * @param y
     */
    public final void spawnNpc(final int npcId, final int x, final int y) {
        c.getPlayer().getMap().spawnNpc(npcId, new Point(x, y));
    }

    /**
     *
     * @param npcId
     * @param pos
     */
    public final void spawnNpc(final int npcId, final Point pos) {
        c.getPlayer().getMap().spawnNpc(npcId, pos);
    }

    /**
     *
     * @param mapid
     * @param npcId
     */
    public final void removeNpc(final int mapid, final int npcId) {
        c.getChannelServer().getMapFactory().getMap(mapid).removeNpc(npcId);
    }

    /**
     *
     * @param mapid
     * @param id
     */
    public final void forceStartReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.forceStartReactor(c);
                break;
            }
        }
    }

    /**
     *
     * @param mapid
     * @param id
     */
    public final void destroyReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    /**
     *
     * @param mapid
     * @param id
     */
    public final void hitReactor(final int mapid, final int id) {
        MapleMap map = c.getChannelServer().getMapFactory().getMap(mapid);
        MapleReactor react;

        for (final MapleMapObject remo : map.getAllReactorsThreadsafe()) {
            react = (MapleReactor) remo;
            if (react.getReactorId() == id) {
                react.hitReactor(c);
                break;
            }
        }
    }

    /**
     *
     * @return
     */
    public final int getJob() {
        return c.getPlayer().getJob();
    }

    /**
     *
     * @param 类型
     * @return
     */
    public final int getNX(int 类型) {
        return c.getPlayer().getCSPoints(类型);
    }

    /**
     *
     * @param amount
     */
    public final void gainD(final int amount) {
        c.getPlayer().modifyCSPoints(2, amount, true);
    }

    /**
     *
     * @param amount
     */
    public final void gainNX(final int amount) {
        c.getPlayer().modifyCSPoints(1, amount, true);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param period
     */
    public final void gainItemPeriod(final int id, final short quantity, final int period) { //period is in days
        gainItem(id, quantity, false, period, -1, "", (byte) 0);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param period
     * @param owner
     */
    public final void gainItemPeriod(final int id, final short quantity, final long period, final String owner) { //period is in days
        gainItem(id, quantity, false, period, -1, owner, (byte) 0);
    }

    /**
     *
     * @param id
     * @param quantity
     */
    public final void gainItem(final int id, final short quantity) {
        gainItem(id, quantity, false, 0, -1, "", (byte) 0);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param period
     * @param Flag
     */
    public final void gainItem(final int id, final short quantity, final long period, byte Flag) {
        gainItem(id, quantity, false, period, -1, "", (byte) Flag);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param randomStats
     */
    public final void gainItem(final int id, final short quantity, final boolean randomStats) {
        gainItem(id, quantity, randomStats, 0, -1, "", (byte) 0);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param randomStats
     * @param slots
     */
    public final void gainItem(final int id, final short quantity, final boolean randomStats, final int slots) {
        gainItem(id, quantity, randomStats, 0, slots, "", (byte) 0);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param period
     */
    public final void gainItem(final int id, final short quantity, final long period) {
        gainItem(id, quantity, false, period, -1, "", (byte) 0);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param randomStats
     * @param period
     * @param slots
     */
    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots) {
        gainItem(id, quantity, randomStats, period, slots, "", (byte) 0);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param randomStats
     * @param period
     * @param slots
     * @param owner
     * @param Flag
     */
    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner, byte Flag) {
        gainItem(id, quantity, randomStats, period, slots, owner, c, Flag);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param randomStats
     * @param period
     * @param slots
     * @param owner
     * @param cg
     * @param Flag
     */
    public final void gainItem(final int id, final short quantity, final boolean randomStats, final long period, final int slots, final String owner, final MapleClient cg, byte Flag) {
        if (quantity >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(id);

            if (!MapleInventoryManipulator.checkSpace(cg, id, quantity, "")) {
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                final Equip item = (Equip) (randomStats ? ii.randomizeStats((Equip) ii.getEquipById(id)) : ii.getEquipById(id));
                if (period > 0) {
                    //item.setExpiration(System.currentTimeMillis() + (period * 1 * 60 * 60 * 1000));  
                    item.setExpiration(System.currentTimeMillis() + (period * 60 * 60 * 1_000));
                }
                if (slots > 0) {
                    item.setUpgradeSlots((byte) (item.getUpgradeSlots() + slots));
                }
                if (owner != null) {
                    item.setOwner(owner);
                }
                final String name = ii.getName(id);
                if (id / 10_000 == 114 && name != null && name.length() > 0) { //medal
                    final String msg = "你已获得称号 <" + name + ">";
                    cg.getPlayer().dropMessage(5, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            } else {
                MapleInventoryManipulator.addById(cg, id, quantity, owner == null ? "" : owner, null, period, Flag);
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -quantity, true, false);
        }
        cg.getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
    }

    /**
     *
     * @param id
     * @param str
     * @param dex
     * @param luk
     * @param Int
     * @param hp
     * @param mp
     * @param watk
     * @param matk
     * @param wdef
     * @param mdef
     * @param hb
     * @param mz
     * @param ty
     * @param yd
     */
    public final void gainItem(final int id, final int str, final int dex, final int luk, final int Int, final int hp, int mp, int watk, int matk, int wdef, int mdef, int hb, int mz, int ty, int yd) {
        gainItemS(id, str, dex, luk, Int, hp, mp, watk, matk, wdef, mdef, hb, mz, ty, yd, c);
    }

    /**
     *
     * @param id
     * @param str
     * @param dex
     * @param luk
     * @param Int
     * @param hp
     * @param mp
     * @param watk
     * @param matk
     * @param wdef
     * @param mdef
     * @param hb
     * @param mz
     * @param ty
     * @param yd
     * @param time
     */
    public final void gainItem(final int id, final int str, final int dex, final int luk, final int Int, final int hp, int mp, int watk, int matk, int wdef, int mdef, int hb, int mz, int ty, int yd, int time) {
        gainItemS(id, str, dex, luk, Int, hp, mp, watk, matk, wdef, mdef, hb, mz, ty, yd, c, time);
    }

    /**
     *
     * @param id
     * @param str
     * @param dex
     * @param luk
     * @param Int
     * @param hp
     * @param mp
     * @param watk
     * @param matk
     * @param wdef
     * @param mdef
     * @param hb
     * @param mz
     * @param ty
     * @param yd
     * @param cg
     */
    public final void gainItemS(final int id, final int str, final int dex, final int luk, final int Int, final int hp, int mp, int watk, int matk, int wdef, int mdef, int hb, int mz, int ty, int yd, final MapleClient cg) {
        if (1 >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(id);

            if (!MapleInventoryManipulator.checkSpace(cg, id, 1, "")) {
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                final Equip item = (Equip) (ii.getEquipById(id));

                final String name = ii.getName(id);
                if (id / 10_000 == 114 && name != null && name.length() > 0) { //medal
                    final String msg = "你已获得称号 <" + name + ">";
                    cg.getPlayer().dropMessage(5, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                if (str > 0) {
                    item.setStr((short) str);
                }
                if (dex > 0) {
                    item.setDex((short) dex);
                }
                if (luk > 0) {
                    item.setLuk((short) luk);
                }
                if (Int > 0) {
                    item.setInt((short) Int);
                }
                if (hp > 0) {
                    item.setHp((short) hp);
                }
                if (mp > 0) {
                    item.setMp((short) mp);
                }
                if (watk > 0) {
                    item.setWatk((short) watk);
                }
                if (matk > 0) {
                    item.setMatk((short) matk);
                }
                if (wdef > 0) {
                    item.setWdef((short) wdef);
                }
                if (mdef > 0) {
                    item.setMdef((short) mdef);
                }
                if (hb > 0) {
                    item.setAvoid((short) hb);
                }
                if (mz > 0) {
                    item.setAcc((short) mz);
                }
                if (ty > 0) {
                    item.setJump((short) ty);
                }
                if (yd > 0) {
                    item.setSpeed((short) yd);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            } else {
                MapleInventoryManipulator.addById(cg, id, (short) 1, "", (byte) 0);
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -1, true, false);
        }
        cg.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) 1, true));
    }

    /**
     *
     * @param id
     * @param str
     * @param dex
     * @param luk
     * @param Int
     * @param hp
     * @param mp
     * @param watk
     * @param matk
     * @param wdef
     * @param mdef
     * @param hb
     * @param mz
     * @param ty
     * @param yd
     * @param cg
     * @param time
     */
    public final void gainItemS(final int id, final int str, final int dex, final int luk, final int Int, final int hp, int mp, int watk, int matk, int wdef, int mdef, int hb, int mz, int ty, int yd, final MapleClient cg, int time) {
        if (1 >= 0) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final MapleInventoryType type = GameConstants.getInventoryType(id);

            if (!MapleInventoryManipulator.checkSpace(cg, id, 1, "")) {
                return;
            }
            if (type.equals(MapleInventoryType.EQUIP) && !GameConstants.isThrowingStar(id) && !GameConstants.isBullet(id)) {
                final Equip item = (Equip) (ii.getEquipById(id));

                final String name = ii.getName(id);
                if (id / 10_000 == 114 && name != null && name.length() > 0) { //medal
                    final String msg = "你已获得称号 <" + name + ">";
                    cg.getPlayer().dropMessage(5, msg);
                    cg.getPlayer().dropMessage(5, msg);
                }
                if (time > 0) {
                    item.setExpiration(System.currentTimeMillis() + (time * 60 * 60 * 1_000));
                }
                if (str > 0) {
                    item.setStr((short) str);
                }
                if (dex > 0) {
                    item.setDex((short) dex);
                }
                if (luk > 0) {
                    item.setLuk((short) luk);
                }
                if (Int > 0) {
                    item.setInt((short) Int);
                }
                if (hp > 0) {
                    item.setHp((short) hp);
                }
                if (mp > 0) {
                    item.setMp((short) mp);
                }
                if (watk > 0) {
                    item.setWatk((short) watk);
                }
                if (matk > 0) {
                    item.setMatk((short) matk);
                }
                if (wdef > 0) {
                    item.setWdef((short) wdef);
                }
                if (mdef > 0) {
                    item.setMdef((short) mdef);
                }
                if (hb > 0) {
                    item.setAvoid((short) hb);
                }
                if (mz > 0) {
                    item.setAcc((short) mz);
                }
                if (ty > 0) {
                    item.setJump((short) ty);
                }
                if (yd > 0) {
                    item.setSpeed((short) yd);
                }
                MapleInventoryManipulator.addbyItem(cg, item.copy());
            } else {
                MapleInventoryManipulator.addById(cg, id, (short) 1, "", (byte) 0);
            }
        } else {
            MapleInventoryManipulator.removeById(cg, GameConstants.getInventoryType(id), id, -1, true, false);
        }
        cg.getSession().write(MaplePacketCreator.getShowItemGain(id, (short) 1, true));
    }

    /**
     *
     * @param songName
     */
    public final void changeMusic(final String songName) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.musicChange(songName));
    }

    /**
     *
     * @param songName
     */
    public final void cs(final String songName) {
        getPlayer().getMap().broadcastMessage(MaplePacketCreator.showEffect(songName));
    }

    /**
     *
     * @param type
     * @param channel
     * @param message
     * @param smegaEar
     */
    public final void worldMessage(final int type, int channel, final String message, boolean smegaEar) {
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(type, channel, message, smegaEar).getBytes());
    }

    /**
     *
     * @param type
     * @param message
     */
    public final void worldMessage(final int type, final String message) {
        World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(type, message).getBytes());
    }

    /**
     *
     * @param maxLevel
     * @param mod
     */
    public void givePartyExp_PQ(int maxLevel, double mod) {
        if ((getPlayer().getParty() == null) || (getPlayer().getParty().getMembers().size() == 1)) {
            int amount = (int) Math.round(GameConstants.getExpNeededForLevel(getPlayer().getLevel() > maxLevel ? maxLevel + getPlayer().getLevel() / 10 : getPlayer().getLevel()) / (Math.min(getPlayer().getLevel(), maxLevel) / 10.0D) / mod);
            gainExp(amount);
            return;
        }
        int cMap = getPlayer().getMapId();
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if ((curChar != null) && ((curChar.getMapId() == cMap) || (curChar.getEventInstance() == getPlayer().getEventInstance()))) {
                int amount = (int) Math.round(GameConstants.getExpNeededForLevel(curChar.getLevel() > maxLevel ? maxLevel + curChar.getLevel() / 10 : curChar.getLevel()) / (Math.min(curChar.getLevel(), maxLevel) / 10.0D) / mod);
                curChar.gainExp(amount, true, true, true);
            }
        }
    }
    // default playerMessage and mapMessage to use type 5

    /**
     *
     * @param message
     */
    public final void playerMessage(final String message) {
        playerMessage(5, message);
    }

    /**
     *
     * @param message
     */
    public final void mapMessage(final String message) {
        mapMessage(5, message);
    }

    /**
     *
     * @param message
     */
    public final void guildMessage(final String message) {
        guildMessage(5, message);
    }

    /**
     *
     * @param type
     * @param message
     */
    public final void playerMessage(final int type, final String message) {
        c.getSession().write(MaplePacketCreator.serverNotice(type, message));
    }

    /**
     *
     * @param type
     * @param message
     */
    public final void mapMessage(final int type, final String message) {
        c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(type, message));
    }

    /**
     *
     * @param type
     * @param message
     */
    public final void guildMessage(final int type, final String message) {
        if (getPlayer().getGuildId() > 0) {
            World.Guild.guildPacket(getPlayer().getGuildId(), MaplePacketCreator.serverNotice(type, message));
        }
    }

    /**
     *
     * @return
     */
    public final MapleGuild getGuild() {
        return getGuild(getPlayer().getGuildId());
    }

    /**
     *
     * @param guildid
     * @return
     */
    public final MapleGuild getGuild(int guildid) {
        return World.Guild.getGuild(guildid);
    }

    /**
     *
     * @return
     */
    public final MapleParty getParty() {
        return c.getPlayer().getParty();
    }

    /**
     *
     * @param mapid
     * @return
     */
    public final int getCurrentPartyId(int mapid) {
        return getMap(mapid).getCurrentPartyId();
    }

    /**
     *
     * @param MapID
     */
    public void czdt(int MapID) {
        MapleCharacter player = c.getPlayer();
        int mapid = MapID;
        MapleMap map = player.getMap();
        if (player.getClient().getChannelServer().getMapFactory().destroyMap(mapid)) {
            MapleMap newMap = player.getClient().getChannelServer().getMapFactory().getMap(mapid);
            MaplePortal newPor = newMap.getPortal(0);
            LinkedHashSet<MapleCharacter> mcs = new LinkedHashSet<>(map.getCharacters()); // do NOT remove, fixing ConcurrentModificationEx.
            outerLoop:
            for (MapleCharacter m : mcs) {
                for (int x = 0; x < 5; x++) {
                    try {
                        //m.changeMap(newMap, newPor);
                        continue outerLoop;
                    } catch (Throwable t) {
                    }
                }
            }
        }
    }

    /*
     * 获取角色组队
     */

    /**
     *
     * @return
     */

    public int getPartySize() {
        return getParty() != null ? getParty().getMembers().size() : -1;
    }

    /**
     *
     * @return
     */
    public int getPartyID() {
        return getParty() != null ? getParty().getPartyId() : -1;
    }

    /**
     *
     * @param cid
     * @return
     */
    public boolean isChrInParty(int cid) {
        return getParty() != null && getParty().getMemberById(cid) != null;
    }

    /**
     *
     * @return
     */
    public int getPartyAverageLevel() {
        return getParty().getAverageLevel();
    }

    /**
     *
     * @return
     */
    public final boolean isLeader() {
        if (getParty() == null) {
            return false;
        }
        return getParty().getLeader().getId() == c.getPlayer().getId();
    }

    /**
     *
     * @param job
     * @return
     */
    public final boolean isAllPartyMembersAllowedJob(final int job) {
        if (c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : c.getPlayer().getParty().getMembers()) {
            if (mem.getJobId() / 100 != job) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param pqName
     * @param times
     * @return
     */
    public final boolean isAllPartyMembersAllowedPQ(String pqName, int times) {
        return this.isAllPartyMembersAllowedPQ(pqName, times, 1);
    }

    /**
     *
     * @param pqName
     * @param times
     * @param day
     * @return
     */
    public final boolean isAllPartyMembersAllowedPQ(String pqName, int times, int day) {
        if (getParty() != null) {
            for (MaplePartyCharacter partyCharacter : getParty().getMembers()) {
                MapleCharacter player = getChannelServer().getPlayerStorage().getCharacterById(partyCharacter.getId());
                if (player != null && getDaysPQLog(pqName, day) < times) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param pqName
     * @param times
     * @param day
     * @return
     */
    public final MaplePartyCharacter getNotAllowedPQMember(String pqName, int times, int day) {
        if (getParty() == null) {
            return null;
        }
        for (MaplePartyCharacter partyCharacter : getParty().getMembers()) {
            MapleCharacter player = getChannelServer().getPlayerStorage().getCharacterById(partyCharacter.getId());
            if (player != null && getDaysPQLog(pqName, day) < times) {
                continue;
            }
            return partyCharacter;
        }
        return null;
    }

    /**
     *
     * @param pqName
     * @param times
     * @return
     */
    public final String getNotAllowedPQMemberName(String pqName, int times) {
        return this.getNotAllowedPQMemberName(pqName, times, 1);
    }

    /**
     *
     * @param string
     * @param times
     * @param day
     * @return
     */
    public final String getNotAllowedPQMemberName(String string, int times, int day) {
        if (this.getNotAllowedPQMember(string, times, day) != null) {
            return this.getNotAllowedPQMember(string, times, day).getName();
        }
        return null;
    }

    /**
     *
     * @param pqName
     * @param num
     */
    public final void gainMembersPQ(String pqName, int num) {
        if (getParty() == null) {
            return;
        }
        for (MaplePartyCharacter partyCharacter : getParty().getMembers()) {
            MapleCharacter player = getChannelServer().getPlayerStorage().getCharacterById(partyCharacter.getId());
            if (player == null) {
                continue;
            }
            player.setPQLog(pqName, 0, num);
        }
    }

    /**
     *
     * @param pqName
     * @param days
     * @return
     */
    public int getDaysPQLog(String pqName, int days) {
        return getPlayer().getDaysPQLog(pqName, 0, days);
    }

    /**
     *
     * @param pqName
     * @return
     */
    public int getPQLog(String pqName) {
        return getPlayer().getPQLog(pqName);
    }

    /**
     *
     * @param pqName
     * @param type
     * @return
     */
    public int getPQLog(String pqName, int type) {
        return getPlayer().getPQLog(pqName, type);
    }

    /**
     *
     * @param pqName
     * @param type
     * @param days
     * @return
     */
    public int getPQLog(String pqName, int type, int days) {
        return getPlayer().getDaysPQLog(pqName, type, days);
    }

    /**
     *
     * @param pqName
     */
    public void setPQLog(String pqName) {
        getPlayer().setPQLog(pqName);
    }

    /**
     *
     * @param pqName
     * @param type
     */
    public void setPQLog(String pqName, int type) {
        getPlayer().setPQLog(pqName, type);
    }

    /**
     *
     * @param pqName
     * @param type
     * @param count
     */
    public void setPQLog(String pqName, int type, int count) {
        getPlayer().setPQLog(pqName, type, count);
    }

    /**
     *
     * @param pqName
     */
    public void resetPQLog(String pqName) {
        getPlayer().resetPQLog(pqName);
    }

    /**
     *
     * @param pqName
     * @param type
     */
    public void resetPQLog(String pqName, int type) {
        getPlayer().resetPQLog(pqName, type);
    }

    /**
     *
     * @param pqName
     */
    public void setPartyPQLog(String pqName) {
        this.setPartyPQLog(pqName, 0);
    }

    /**
     *
     * @param pqName
     * @param type
     */
    public void setPartyPQLog(String pqName, int type) {
        this.setPartyPQLog(pqName, type, 1);
    }

    /**
     *
     * @param pqName
     * @param type
     * @param count
     */
    public void setPartyPQLog(String pqName, int type, int count) {
        if (this.getPlayer().getParty() == null || this.getPlayer().getParty().getMembers().size() == 1) {
            getPlayer().setPQLog(pqName, type, count);
            return;
        }
        int n4 = this.getPlayer().getMapId();
        for (MaplePartyCharacter partyCharacter : this.getPlayer().getParty().getMembers()) {
            MapleCharacter player = this.getPlayer().getMap().getCharacterById(partyCharacter.getId());
            if (player == null || player.getMapId() != n4) {
                continue;
            }
            player.setPQLog(pqName, type, count);
        }
    }

    /**
     *
     * @return
     */
    public final boolean allMembersHere() {
        if (c.getPlayer().getParty() == null) {
            return false;
        }
        for (final MaplePartyCharacter mem : c.getPlayer().getParty().getMembers()) {
            final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(mem.getId());
            if (chr == null) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param min
     * @param max
     * @return
     */
    public final boolean isAllPartyMembersAllowedLevel(int min, int max) {
        if (getParty() == null) {
            return false;
        }
        for (MaplePartyCharacter d2 : getParty().getMembers()) {
            if (d2.getLevel() >= min && d2.getLevel() <= max) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     *
     * @param questID
     * @param coolDownTime
     * @return
     */
    public final boolean isAllPartyMembersNotCoolDown(int questID, int coolDownTime) {
        return getParty() != null && this.getIsInCoolDownMember(questID, coolDownTime) == null;
    }

    /**
     *
     * @param questID
     * @param coolDownTime
     * @return
     */
    public final String getIsInCoolDownMemberName(int questID, int coolDownTime) {
        MaplePartyCharacter d2 = this.getIsInCoolDownMember(questID, coolDownTime);
        return d2 != null ? d2.getName() : null;
    }

    /**
     *
     * @param questID
     * @param coolDownTime
     * @return
     */
    public final MaplePartyCharacter getIsInCoolDownMember(int questID, int coolDownTime) {
        if (getParty() != null) {
            for (MaplePartyCharacter partyCharacter : getParty().getMembers()) {
                MapleCharacter player = getChannelServer().getPlayerStorage().getCharacterById(partyCharacter.getId());
                if (player == null) {
                    return partyCharacter;
                }
                MapleQuestStatus status = player.getQuestNAdd(MapleQuest.getInstance(questID));
                if (status == null || status.getCustomData() == null || Long.valueOf(status.getCustomData()) + (long) coolDownTime <= System.currentTimeMillis()) {
                    continue;
                }
                return partyCharacter;
            }
        }
        return null;
    }

    /**
     *
     * @param mapId
     */
    public final void warpParty(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp(mapId, 0);
            return;
        }
        final MapleMap target = getMap(mapId);
        final int cMap = getPlayer().getMapId();

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    /**
     *
     * @param mapId
     * @param portal
     */
    public final void warpParty(final int mapId, final String portal) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp(mapId, portal);
            return;
        }
        final MapleMap target = getMap(mapId);
        final int cMap = getPlayer().getMapId();

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(portal));
            }
        }
    }

    /**
     *
     * @param mapId
     * @param portal
     */
    public final void warpParty(final int mapId, final int portal) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            if (portal < 0) {
                warp(mapId);
            } else {
                warp(mapId, portal);
            }
            return;
        }
        final boolean rand = portal < 0;
        final MapleMap target = getMap(mapId);
        final int cMap = getPlayer().getMapId();

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                if (rand) {
                    try {
                        curChar.changeMap(target, target.getPortal(Randomizer.nextInt(target.getPortals().size())));
                    } catch (Exception e) {
                        curChar.changeMap(target, target.getPortal(0));
                    }
                } else {
                    curChar.changeMap(target, target.getPortal(portal));
                }
            }
        }
    }

    /**
     *
     * @param mapId
     */
    public final void warpParty_Instanced(final int mapId) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            warp_Instanced(mapId);
            return;
        }
        final MapleMap target = getMap_Instanced(mapId);

        final int cMap = getPlayer().getMapId();
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == cMap || curChar.getEventInstance() == getPlayer().getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
    }

    /**
     *
     * @param gain
     */
    public void gainDY(int gain) {
        c.getPlayer().modifyCSPoints(2, gain, true);
    }

    /**
     *
     * @param gain
     */
    public void gainMeso(int gain) {
        c.getPlayer().gainMeso(gain, true, false, true);
    }

    /**
     *
     * @param gain
     */
    public void gainExp(int gain) {
        c.getPlayer().gainExp(gain, true, true, true);
    }

    /**
     *
     * @param gain
     */
    public void gainExpR(int gain) {
        c.getPlayer().gainExp(gain * c.getChannelServer().getExpRate(), true, true, true);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param party
     */
    public final void givePartyItems(final int id, final short quantity, final List<MapleCharacter> party) {
        for (MapleCharacter chr : party) {
            if (quantity >= 0) {
                MapleInventoryManipulator.addById(chr.getClient(), id, quantity, (byte) 0);
            } else {
                MapleInventoryManipulator.removeById(chr.getClient(), GameConstants.getInventoryType(id), id, -quantity, true, false);
            }
            chr.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, quantity, true));
        }
    }

    /**
     *
     * @param id
     * @param quantity
     */
    public final void givePartyItems(final int id, final short quantity) {
        givePartyItems(id, quantity, false);
    }

    /**
     *
     * @param id
     * @param quantity
     * @param removeAll
     */
    public final void givePartyItems(final int id, final short quantity, final boolean removeAll) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainItem(id, (short) (removeAll ? -getPlayer().itemQuantity(id) : quantity));
            return;
        }

        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                gainItem(id, (short) (removeAll ? -curChar.itemQuantity(id) : quantity), false, 0, 0, "", curChar.getClient(), (byte) 0);
            }
        }
    }

    /**
     *
     * @param amount
     * @param party
     */
    public final void givePartyExp(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.gainExp(amount, true, true, true);
        }
    }

    /**
     *
     * @param amount
     */
    public final void givePartyExp(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainExp(amount);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.gainExp(amount, true, true, true);
            }
        }
    }

    /**
     *
     * @param amount
     * @param party
     */
    public final void givePartyNX(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.modifyCSPoints(1, amount, true);
        }
    }

    /**
     *
     * @param amount
     */
    public final void givePartyDY(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainDY(amount);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.modifyCSPoints(2, amount, true);
            }
        }
    }

    /**
     *
     * @param amount
     */
    public final void givePartyMeso(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainMeso(amount);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.gainMeso(amount, true);
            }
        }
    }

    /**
     *
     * @param amount
     */
    public final void givePartyNX(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            gainNX(amount);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.modifyCSPoints(1, amount, true);
            }
        }
    }

    /**
     *
     * @param amount
     * @param party
     */
    public final void endPartyQuest(final int amount, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            chr.endPartyQuest(amount);
        }
    }

    /**
     *
     * @param amount
     */
    public final void endPartyQuest(final int amount) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            getPlayer().endPartyQuest(amount);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.endPartyQuest(amount);
            }
        }
    }

    /**
     *
     * @param id
     * @param party
     */
    public final void removeFromParty(final int id, final List<MapleCharacter> party) {
        for (final MapleCharacter chr : party) {
            final int possesed = chr.getInventory(GameConstants.getInventoryType(id)).countById(id);
            if (possesed > 0) {
                MapleInventoryManipulator.removeById(c, GameConstants.getInventoryType(id), id, possesed, true, false);
                chr.getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -possesed, true));
            }
        }
    }

    /**
     *
     * @param id
     */
    public final void removeFromParty(final int id) {
        givePartyItems(id, (short) 0, true);
    }

    /**
     *
     * @param skill
     * @param level
     */
    public final void useSkill(final int skill, final int level) {
        if (level <= 0) {
            return;
        }
        SkillFactory.getSkill(skill).getEffect(level).applyTo(c.getPlayer());
    }

    /**
     *
     * @param id
     */
    public final void useItem(final int id) {
        MapleItemInformationProvider.getInstance().getItemEffect(id).applyTo(c.getPlayer());
        c.getSession().write(UIPacket.getStatusMsg(id));
    }

    /**
     *
     * @param id
     */
    public final void cancelItem(final int id) {
        c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(id), false, -1);
    }

    /**
     *
     * @return
     */
    public final int getMorphState() {
        return c.getPlayer().getMorphState();
    }

    /**
     *
     * @param id
     */
    public final void removeAll(final int id) {
        c.getPlayer().removeAll(id);
    }

    /**
     *
     * @param closeness
     * @param index
     */
    public final void gainCloseness(final int closeness, final int index) {
        final MaplePet pet = getPlayer().getPet(index);
        if (pet != null) {
            pet.setCloseness(pet.getCloseness() + closeness);
            getClient().getSession().write(PetPacket.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
        }
    }

    /**
     *
     * @param closeness
     */
    public final void gainClosenessAll(final int closeness) {
        for (final MaplePet pet : getPlayer().getPets()) {
            if (pet != null) {
                pet.setCloseness(pet.getCloseness() + closeness);
                getClient().getSession().write(PetPacket.updatePet(pet, getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            }
        }
    }

    /**
     *
     * @param mapid
     */
    public final void resetMap(final int mapid) {
        getMap(mapid).resetFully();
    }

    /**
     *
     * @param id
     */
    public final void openNpc(final int id) {
        NPCScriptManager.getInstance().start(getClient(), id);
    }

    /**
     *
     * @param id
     * @param wh
     */
    public void openNpc(int id, int wh) {
        NPCScriptManager.getInstance().dispose(c);
        NPCScriptManager.getInstance().start(getClient(), id, wh);
    }

    /**
     *
     * @param Text
     */
    public void serverNotice(String Text) {
        getClient().getChannelServer().broadcastPacket(MaplePacketCreator.serverNotice(6, Text));
    }

    /**
     *
     * @param cg
     * @param id
     */
    public final void openNpc(final MapleClient cg, final int id) {
        NPCScriptManager.getInstance().start(cg, id);
    }

    /**
     *
     * @return
     */
    public final int getMapId() {
        return c.getPlayer().getMap().getId();
    }

    /**
     *
     * @param mobid
     * @return
     */
    public final boolean haveMonster(final int mobid) {
        for (MapleMapObject obj : c.getPlayer().getMap().getAllMonstersThreadsafe()) {
            final MapleMonster mob = (MapleMonster) obj;
            if (mob.getId() == mobid) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final int getChannelNumber() {
        return c.getChannel();
    }

    /**
     *
     * @param mapid
     * @return
     */
    public final int getMonsterCount(final int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getNumMonsters();
    }

    /**
     *
     * @param id
     * @param level
     * @param masterlevel
     */
    public final void teachSkill(final int id, final byte level, final byte masterlevel) {
        getPlayer().changeSkillLevel(SkillFactory.getSkill(id), level, masterlevel);
    }

    /**
     *
     * @param id
     * @param level
     */
    public final void teachSkill(final int id, byte level) {
        final ISkill skil = SkillFactory.getSkill(id);
        if (getPlayer().getSkillLevel(skil) > level) {
            level = getPlayer().getSkillLevel(skil);
        }
        getPlayer().changeSkillLevel(skil, level, skil.getMaxLevel());
    }

    /**
     *
     * @param mapid
     * @return
     */
    public final int getPlayerCount(final int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharactersSize();
    }

    /**
     *
     */
    public final void dojo_getUp() {
        c.getSession().write(MaplePacketCreator.updateInfoQuest(1_207, "pt=1;min=4;belt=1;tuto=1")); //todo
        c.getSession().write(MaplePacketCreator.dojoWarpUp());
        // c.getSession().write(MaplePacketCreator.Mulung_DojoUp2());
        // c.getSession().write(MaplePacketCreator.instantMapWarp((byte) 6));
    }

    /**
     *
     * @param dojo
     * @param fromresting
     * @return
     */
    public final boolean dojoAgent_NextMap(final boolean dojo, final boolean fromresting) {
        if (dojo) {
            return Event_DojoAgent.warpNextMap(c.getPlayer(), fromresting);
        }
        return Event_DojoAgent.warpNextMap_Agent(c.getPlayer(), fromresting);
    }

    /**
     *
     * @return
     */
    public final int dojo_getPts() {
        return c.getPlayer().getDojo();
    }

    /**
     *
     * @return
     */
    public final byte getShopType() {
        return c.getPlayer().getPlayerShop().getShopType();
    }

    /**
     *
     * @param loc
     * @return
     */
    public final MapleEvent getEvent(final String loc) {
        return c.getChannelServer().getEvent(MapleEventType.valueOf(loc));
    }

    /**
     *
     * @param loc
     * @return
     */
    public final int getSavedLocation(final String loc) {
        final Integer ret = c.getPlayer().getSavedLocation(SavedLocationType.fromString(loc));
        if (ret == null || ret == -1) {
            return 100_000_000;
        }
        return ret;
    }

    /**
     *
     * @param loc
     */
    public final void saveLocation(final String loc) {
        c.getPlayer().saveLocation(SavedLocationType.fromString(loc));
    }

    /**
     *
     * @param loc
     */
    public final void saveReturnLocation(final String loc) {
        c.getPlayer().saveLocation(SavedLocationType.fromString(loc), c.getPlayer().getMap().getReturnMap().getId());
    }

    /**
     *
     * @param loc
     */
    public final void clearSavedLocation(final String loc) {
        c.getPlayer().clearSavedLocation(SavedLocationType.fromString(loc));
    }

    /**
     *
     * @param msg
     */
    public final void summonMsg(final String msg) {
        if (!c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        c.getSession().write(UIPacket.summonMessage(msg));
    }

    /**
     *
     * @param type
     */
    public final void summonMsg(final int type) {
        if (!c.getPlayer().hasSummon()) {
            playerSummonHint(true);
        }
        c.getSession().write(UIPacket.summonMessage(type));
    }

    /**
     *
     * @param msg
     */
    public final void HSText(final String msg) {
        c.getSession().write(MaplePacketCreator.HSText(msg));
    }

    /**
     *
     * @param msg
     * @param width
     * @param height
     */
    public final void showInstruction(final String msg, final int width, final int height) {
        c.getSession().write(MaplePacketCreator.sendHint(msg, width, height));
    }

    /**
     *
     * @param summon
     */
    public final void playerSummonHint(final boolean summon) {
        c.getPlayer().setHasSummon(summon);
        c.getSession().write(UIPacket.summonHelper(summon));
    }

    /**
     *
     * @param id
     * @return
     */
    public final String getInfoQuest(final int id) {
        return c.getPlayer().getInfoQuest(id);
    }

    /**
     *
     * @param id
     * @param data
     */
    public final void updateInfoQuest(final int id, final String data) {
        c.getPlayer().updateInfoQuest(id, data);
    }

    /**
     *
     * @param data
     * @return
     */
    public final boolean getEvanIntroState(final String data) {
        return getInfoQuest(22_013).equals(data);
    }

    /**
     *
     * @param data
     */
    public final void updateEvanIntroState(final String data) {
        updateInfoQuest(22_013, data);
    }

    /**
     *
     */
    public final void Aran_Start() {
        c.getSession().write(UIPacket.Aran_Start());
    }

    /**
     *
     * @param data
     * @param v1
     */
    public final void evanTutorial(final String data, final int v1) {
        c.getSession().write(MaplePacketCreator.getEvanTutorial(data));
    }

    /**
     *
     * @param data
     */
    public final void AranTutInstructionalBubble(final String data) {
        c.getSession().write(UIPacket.AranTutInstructionalBalloon(data));
    }

    /**
     *
     * @param data
     */
    public final void ShowWZEffect(final String data) {
        c.getSession().write(UIPacket.AranTutInstructionalBalloon(data));
    }

    /**
     *
     * @param data
     * @param info
     */
    public final void showWZEffect(final String data, int info) {
        c.getSession().write(UIPacket.ShowWZEffect(data, info));
    }

    /**
     *
     * @param data
     */
    public final void EarnTitleMsg(final String data) {
        c.getSession().write(UIPacket.EarnTitleMsg(data));
    }

    /**
     *
     * @param enabled
     */
    public final void MovieClipIntroUI(final boolean enabled) {
        c.getSession().write(UIPacket.IntroDisableUI(enabled));
        c.getSession().write(UIPacket.IntroLock(enabled));
    }

    /**
     *
     * @param i
     * @return
     */
    public MapleInventoryType getInvType(int i) {
        return MapleInventoryType.getByType((byte) i);
    }

    /**
     *
     * @param id
     * @return
     */
    public String getItemName(final int id) {
        return MapleItemInformationProvider.getInstance().getName(id);
    }

    /**
     *
     * @param id
     * @param name
     * @param level
     * @param closeness
     * @param fullness
     * @param period
     */
    public void gainPet(int id, String name, int level, int closeness, int fullness, long period) {//给予宠物
        if (id > 5_010_000 || id < 5_000_000) {
            id = 5_000_000;
        }
        if (level > 30) {
            level = 30;
        }
        if (closeness > 30_000) {
            closeness = 30_000;
        }
        if (fullness > 100) {
            fullness = 100;
        }
        name = getItemName(id);
        try {
            MapleInventoryManipulator.addById(c, id, (short) 1, "", MaplePet.createPet(id, name, level, closeness, fullness, MapleInventoryIdentifier.getInstance(), id == 5_000_054 ? (int) period : 0), period, (byte) 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param invType
     * @param slot
     * @param quantity
     */
    public void removeSlot(int invType, byte slot, short quantity) {
        MapleInventoryManipulator.removeFromSlot(c, getInvType(invType), slot, quantity, true);
    }

    /**
     *
     * @param gp
     */
    public void gainGP(final int gp) {
        if (getPlayer().getGuildId() <= 0) {
            return;
        }
        World.Guild.gainGP(getPlayer().getGuildId(), gp); //1 for
    }

    /**
     *
     * @return
     */
    public int getGP() {
        if (getPlayer().getGuildId() <= 0) {
            return 0;
        }
        return World.Guild.getGP(getPlayer().getGuildId()); //1 for
    }

    /**
     *
     * @param path
     */
    public void showMapEffect(String path) {
        getClient().getSession().write(UIPacket.MapEff(path));
    }

    /**
     *
     * @param itemid
     * @return
     */
    public int itemQuantity(int itemid) {
        return getPlayer().itemQuantity(itemid);
    }

    /**
     *
     * @param event
     * @return
     */
    public EventInstanceManager getDisconnected(String event) {
        EventManager em = getEventManager(event);
        if (em == null) {
            return null;
        }
        for (EventInstanceManager eim : em.getInstances()) {
            if (eim.isDisconnected(c.getPlayer()) && eim.getPlayerCount() > 0) {
                return eim;
            }
        }
        return null;
    }

    /**
     *
     * @param reactorId
     * @param state
     * @return
     */
    public boolean isAllReactorState(final int reactorId, final int state) {
        boolean ret = false;
        for (MapleReactor r : getMap().getAllReactorsThreadsafe()) {
            if (r.getReactorId() == reactorId) {
                ret = r.getState() == state;
            }
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     *
     * @param id
     */
    public void spawnMonster(int id) {
        spawnMonster(id, 1, new Point(getPlayer().getPosition()));
    }

    // summon one monster, remote location

    /**
     *
     * @param id
     * @param x
     * @param y
     */
    public void spawnMonster(int id, int x, int y) {
        spawnMonster(id, 1, new Point(x, y));
    }

    // multiple monsters, remote location

    /**
     *
     * @param id
     * @param qty
     * @param x
     * @param y
     */
    public void spawnMonster(int id, int qty, int x, int y) {
        spawnMonster(id, qty, new Point(x, y));
    }

    // handler for all spawnMonster

    /**
     *
     * @param id
     * @param qty
     * @param pos
     */
    public void spawnMonster(int id, int qty, Point pos) {
        for (int i = 0; i < qty; i++) {
            getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(id), pos);
        }
    }

    /**
     *
     * @param text
     * @param npc
     */
    public void sendNPCText(final String text, final int npc) {
        getMap().broadcastMessage(MaplePacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
    }

    /**
     *
     * @param flag
     * @return
     */
    public boolean getTempFlag(final int flag) {
        return (c.getChannelServer().getTempFlag() & flag) == flag;
    }

    /**
     *
     * @return
     */
    public int getGamePoints() {
        return this.c.getPlayer().getGamePoints();
    }

    /**
     *
     * @param amount
     */
    public void gainGamePoints(int amount) {
        this.c.getPlayer().gainGamePoints(amount);
    }

    /**
     *
     */
    public void resetGamePoints() {
        this.c.getPlayer().resetGamePoints();
    }

    /**
     *
     * @return
     */
    public int getGamePointsPD() {
        return this.c.getPlayer().getGamePointsPD();
    }

    /**
     *
     * @return
     */
    public int getskillzq() {//
        return this.c.getPlayer().getSkillzq();
    }

    /**
     *
     * @param amount
     */
    public void setskillzq(int amount) {//
        this.c.getPlayer().setSkillzq(amount);
    }

    /**
     *
     * @param amount
     */
    public void gainGamePointsPD(int amount) {
        this.c.getPlayer().gainGamePointsPD(amount);
    }

    /**
     *
     */
    public void resetGamePointsPD() {
        this.c.getPlayer().resetGamePointsPD();
    }

    /**
     *
     * @return
     */
    public int getPS() {
        return this.c.getPlayer().getGamePointsPS();
    }

    /**
     *
     * @param amount
     */
    public void gainPS(int amount) {
        this.c.getPlayer().gainGamePointsPS(amount);
    }

    /**
     *
     */
    public void resetPS() {
        this.c.getPlayer().resetGamePointsPS();
    }

    /**
     *
     * @param A
     * @return
     */
    public boolean beibao(int A) {
        if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > -1 && A == 1) {
            return true;
        }
        if (this.c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() > -1 && A == 2) {
            return true;
        }
        if (this.c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > -1 && A == 3) {
            return true;
        }
        if (this.c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() > -1 && A == 4) {
            return true;
        }
        return this.c.getPlayer().getInventory(MapleInventoryType.CASH).getNextFreeSlot() > -1 && A == 5;
    }

    /**
     *
     * @param A
     * @param kw
     * @return
     */
    public boolean beibao(int A, int kw) {
        if (this.c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNextFreeSlot() > kw && A == 1) {
            return true;
        }
        if (this.c.getPlayer().getInventory(MapleInventoryType.USE).getNextFreeSlot() > kw && A == 2) {
            return true;
        }
        if (this.c.getPlayer().getInventory(MapleInventoryType.SETUP).getNextFreeSlot() > kw && A == 3) {
            return true;
        }
        if (this.c.getPlayer().getInventory(MapleInventoryType.ETC).getNextFreeSlot() > kw && A == 4) {
            return true;
        }
        return this.c.getPlayer().getInventory(MapleInventoryType.CASH).getNextFreeSlot() > kw && A == 5;
    }

    /**
     *
     * @param mapid
     */
    public final void startAriantPQ(int mapid) {
        for (final MapleCharacter chr : c.getPlayer().getMap().getCharacters()) {
            chr.updateAriantScore();
            chr.changeMap(mapid);
            c.getPlayer().getMap().resetAriantPQ(c.getPlayer().getAverageMapLevel());
            chr.getClient().getSession().write(MaplePacketCreator.getClock(8 * 60));
            chr.dropMessage(5, "建议把你的小地图忘下移动，来查看排名.");
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    chr.updateAriantScore();
                }
            }, 800);
            EtcTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    chr.getClient().getSession().write(MaplePacketCreator.showAriantScoreBoard());
                    MapTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            chr.changeMap(980_010_010, 0);
                            chr.resetAriantScore();
                        }
                    }, 9_000);
                }
            }, (8 * 60) * 1_000);
        }
    }

    /**
     *
     * @param bossid
     * @return
     */
    public int getBossLog(String bossid) {
        return this.c.getPlayer().getBossLog(bossid);
    }

    /**
     *
     * @param bossid
     * @param type
     * @return
     */
    public int getBossLog(String bossid, int type) {
        return this.c.getPlayer().getBossLog(bossid, type);
    }

    /**
     *
     * @param bossid
     */
    public void setBossLog(String bossid) {
        this.c.getPlayer().setBossLog(bossid);
    }

    /**
     *
     * @param bossid
     * @param type
     */
    public void setBossLog(String bossid, int type) {
        this.c.getPlayer().setBossLog(bossid, type);
    }

    /**
     *
     * @param bossid
     * @param type
     * @param count
     */
    public void setBossLog(String bossid, int type, int count) {
        this.c.getPlayer().setBossLog(bossid, type, count);
    }

    /**
     *
     * @param bossid
     */
    public void resetBossLog(String bossid) {
        this.c.getPlayer().resetBossLog(bossid);
    }

    /**
     *
     * @param bossid
     * @param type
     */
    public void resetBossLog(String bossid, int type) {
        this.c.getPlayer().resetBossLog(bossid, type);
    }

    /**
     *
     * @param bossid
     */
    public final void givePartyBossLog(String bossid) {
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            setBossLog(bossid);
            return;
        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                curChar.setBossLog(bossid);
            }
        }
    }

    /**
     *
     * @param bossid
     * @param num
     * @return
     */
    public final boolean getPartyBossLog(String bossid, int num) {  //(1,3);  false 
        if (getPlayer().getParty() == null || getPlayer().getParty().getMembers().size() == 1) {
            int bossnum = getBossLog(bossid);
            return bossnum <= num;

        }
        for (final MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            final MapleCharacter curChar = getMap().getCharacterById(chr.getId());
            if (curChar != null) {
                int bossnum = curChar.getBossLog(bossid);
                if (bossnum > num) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param bossid
     * @return
     */
    public int getBosslog(String bossid) {
        return getPlayer().getBossLog(bossid);
    }

    /**
     *
     * @param bossid
     */
    public void setBosslog(String bossid) {
        getPlayer().setBossLog(bossid);
    }

    /**
     *
     * @param bossid
     * @return
     */
    public int getOneTimeLog(String bossid) {
        return getPlayer().getOneTimeLog(bossid);
    }

    /**
     *
     * @param bossid
     */
    public void setOneTimeLog(String bossid) {
        getPlayer().setOneTimeLog(bossid);
    }

    /**
     *
     */
    public void resetAp() {
        boolean beginner = getPlayer().getJob() == 0 || getPlayer().getJob() == 1_000 || getPlayer().getJob() == 2_001;
        getPlayer().resetStatsByJob(beginner);
    }
}
