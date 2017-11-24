package client;

import client.MapleTrait.MapleTraitType;
import client.anticheat.CheatTracker;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.ItemLoader;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import client.inventory.MapleRing;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import database.DatabaseException;
import handling.MaplePacket;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.PlayerBuffStorage;
import handling.world.PlayerBuffValueHolder;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyBuff.MapleFamilyBuffEntry;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildCharacter;
import java.awt.Point;
import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import scripting.EventInstanceManager;
import scripting.NPCScriptManager;
import server.*;
import server.Timer.BuffTimer;
import server.Timer.EtcTimer;
import server.Timer.EventTimer;
import server.Timer.MapTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.life.PlayerNPC;
import server.maps.*;
import server.movement.LifeMovementFragment;
import server.quest.MapleQuest;
import server.shops.IMaplePlayerShop;
import tools.ConcurrentEnumMap;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.MockIOSession;
import tools.Pair;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;
import tools.packet.MonsterCarnivalPacket;
import tools.packet.PetPacket;
import tools.packet.PlayerShopPacket;
import tools.packet.UIPacket;

/**
 *
 * @author zjj
 */
public class MapleCharacter extends AbstractAnimatedMapleMapObject implements Serializable {

    private static final long serialVersionUID = 845_748_950_829L;
    private String name, chalktext, BlessOfFairy_Origin, charmessage;
    private long lastCombo, lastfametime, keydown_skill;
    private byte dojoRecord, gmLevel, gender, initialSpawnPoint, skinColor, guildrank = 5, allianceRank = 5, world, fairyExp = 30, numClones, subcategory; // Make this a quest record, TODO : Transfer it somehow with the current data
    private short level, mulung_energy, combo, availableCP, totalCP, fame, hpApUsed, job, remainingAp;
    private int accountid, id, meso, exp, hair, face, mapid, bookCover, dojo,
            guildid = 0, fallcounter = 0, maplepoints, acash, chair, itemEffect, points, vpoints,
            rank = 1, rankMove = 0, jobRank = 1, jobRankMove = 0, marriageId, marriageItemId = 0,
            currentrep, totalrep, linkMid = 0, coconutteam = 0, followid = 0, battleshipHP = 0,
            expression, constellation, blood, month, day, beans, beansNum, beansRange, prefix, vip, skillzq;
    private int modid;//怪物ID
    private int modsl;//怪物数量
    private int itemsid;//怪物ID
    private int itemsl;//怪物数量
    private int jf, lqjf, bossjf, cjjf;//积分
    private int tuiguang2;//推广值
    private boolean canSetBeansNum;
    private Point old = new Point(0, 0);
    private boolean smega, hidden, hasSummon = false;
    private int[] wishlist, rocks, savedLocations, regrocks, remainingSp = new int[10];
    private transient AtomicInteger inst;
    private transient List<LifeMovementFragment> lastres;
    private List<Integer> lastmonthfameids;
    private List<MapleDoor> doors;
    private List<MaplePet> pets;
    private transient WeakReference<MapleCharacter>[] clones;
    private transient Set<MapleMonster> controlled;
    private transient Set<MapleMapObject> visibleMapObjects;
    private transient ReentrantReadWriteLock visibleMapObjectsLock;
    private Map<MapleQuest, MapleQuestStatus> quests;
    private Map<Integer, String> questinfo;
    private Map<ISkill, SkillEntry> skills = new LinkedHashMap<>();
    private transient Map<MapleBuffStat, MapleBuffStatValueHolder> effects = new ConcurrentEnumMap<>(MapleBuffStat.class);
    private transient Map<Integer, MapleSummon> summons;
    private transient Map<Integer, MapleCoolDownValueHolder> coolDowns = new LinkedHashMap<>();
    private transient Map<MapleDisease, MapleDiseaseValueHolder> diseases = new ConcurrentEnumMap<>(MapleDisease.class);
    private CashShop cs;
    private transient Deque<MapleCarnivalChallenge> pendingCarnivalRequests;
    private transient MapleCarnivalParty carnivalParty;
    private BuddyList buddylist;
    private MonsterBook monsterbook;
    private transient CheatTracker anticheat;
    private MapleClient client;
    private PlayerStats stats;
    private transient PlayerRandomStream CRand;
    private transient MapleMap map;
    private transient MapleShop shop;
    private transient MapleDragon dragon;
    private transient RockPaperScissors rps;
    private MapleStorage storage;
    private transient MapleTrade trade;
    private MapleMount mount;
    private List<Integer> finishedAchievements = new ArrayList<>();
    private MapleMessenger messenger;
    private byte[] petStore;
    private transient IMaplePlayerShop playerShop;
    private MapleParty party;
    private boolean invincible = false, canTalk = true, clone = false, followinitiator = false, followon = false;
    private MapleGuildCharacter mgc;
    private MapleFamilyCharacter mfc;
    private transient EventInstanceManager eventInstance;
    private MapleInventory[] inventory;
    private SkillMacro[] skillMacros = new SkillMacro[5];
    private MapleKeyLayout keylayout;
    private transient ScheduledFuture<?> beholderHealingSchedule, beholderBuffSchedule, BerserkSchedule,
            dragonBloodSchedule, fairySchedule, mapTimeLimitTask, fishing;
    private long nextConsume = 0, pqStartTime = 0;
    private transient Event_PyramidSubway pyramidSubway = null;
    private transient List<Integer> pendingExpiration = null, pendingSkills = null;
    private transient Map<Integer, Integer> movedMobs = new HashMap<>();
    private String teleportname = "";
    private int APQScore;
    private long lasttime = 0L;
    private long currenttime = 0L;
    private long deadtime = 1_000L;
    private MapleCharacter chars;
    private long nengl = 0;
    private long nengls = 0;
    private static String[] ariantroomleader = new String[3]; // AriantPQ
    private static int[] ariantroomslot = new int[3]; // AriantPQ
    public int apprentice = 0,
            /**
             *
             */
            master = 0; // apprentice ID for master

    /**
     *
     */
    public boolean DebugMessage = false;

    /**
     *
     */
    public int ariantScore = 0;

    /**
     *
     */
    public long lastGainHM;
    private EnumMap<MapleTraitType, MapleTrait> traits;
    private boolean changed_wishlist, changed_trocklocations, changed_skillmacros, changed_achievements, changed_savedlocations, changed_pokemon, changed_questinfo, changed_skills, changed_reports, changed_vcores,
            changed_extendedSlots, changed_innerSkills, changed_keyValue;

    private MapleCharacter(final boolean ChannelServer) {
        setStance(0);
        setPosition(new Point(0, 0));

        inventory = new MapleInventory[MapleInventoryType.values().length];
        for (MapleInventoryType type : MapleInventoryType.values()) {
            inventory[type.ordinal()] = new MapleInventory(type, (byte) 100);
        }
        quests = new LinkedHashMap<>(); // Stupid erev quest.
        stats = new PlayerStats(this);
        for (int i = 0; i < remainingSp.length; i++) {
            remainingSp[i] = 0;
        }
        if (ChannelServer) {
            lastMoveItemTime = 0;
            lastCheckPeriodTime = 0;
            lastQuestTime = 0;
            lastHPTime = 0;
            lastMPTime = 0;
            lastCombo = 0;
            mulung_energy = 0;
            combo = 0;
            keydown_skill = 0;
            smega = true;
            petStore = new byte[3];
            for (int i = 0; i < petStore.length; i++) {
                petStore[i] = (byte) -1;
            }
            wishlist = new int[10];
            rocks = new int[10];
            regrocks = new int[5];
            clones = new WeakReference[25];
            for (int i = 0; i < clones.length; i++) {
                clones[i] = new WeakReference<>(null);
            }
            inst = new AtomicInteger();
            inst.set(0); // 1 = NPC/ Quest, 2 = Duey, 3 = Hired Merch store, 4 = Storage
            keylayout = new MapleKeyLayout();
            doors = new ArrayList<>();
            controlled = new LinkedHashSet<>();
            summons = new LinkedHashMap<>();
            visibleMapObjects = new LinkedHashSet<>();
            visibleMapObjectsLock = new ReentrantReadWriteLock();
            pendingCarnivalRequests = new LinkedList<>();

            savedLocations = new int[SavedLocationType.values().length];
            for (int i = 0; i < SavedLocationType.values().length; i++) {
                savedLocations[i] = -1;
            }
            questinfo = new LinkedHashMap<>();
            anticheat = new CheatTracker(this);
            pets = new ArrayList<>();
        }
    }

    /**
     *
     * @param client
     * @param type
     * @return
     */
    public static MapleCharacter getDefault(final MapleClient client, final int type) {
        MapleCharacter ret = new MapleCharacter(false);
        ret.client = client;
        ret.map = null;
        ret.exp = 0;
        ret.gmLevel = 0;
        ret.job = (short) (type == 1 ? 0 : (type == 0 ? 1_000 : (type == 3 ? 2_001 : (type == 4 ? 3_000 : 2_000))));
        ret.beans = 0;
        ret.meso = 0;
        ret.level = 1;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.buddylist = new BuddyList((byte) 20);

        ret.stats.str = 12;
        ret.stats.dex = 5;
        ret.stats.int_ = 4;
        ret.stats.luk = 4;
        ret.stats.maxhp = 50;
        ret.stats.hp = 50;
        ret.stats.maxmp = 50;
        ret.stats.mp = 50;
        ret.modid = 0;
        ret.modsl = 0;
        ret.lqjf = 0;
        ret.jf = 0;
        ret.cjjf = 0;
        ret.bossjf = 0;
        ret.prefix = 0;

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, ret.accountid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret.client.setAccountName(rs.getString("name"));
                    ret.acash = rs.getInt("ACash");
                    ret.maplepoints = rs.getInt("mPoints");
                    ret.points = rs.getInt("points");
                    ret.vpoints = rs.getInt("vpoints");
                    ret.lastGainHM = rs.getLong("lastGainHM");
                }
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error getting character default" + e);
        }
        return ret;
    }

    /**
     *
     * @param ct
     * @param client
     * @param isChannel
     * @return
     */
    public final static MapleCharacter ReconstructChr(final CharacterTransfer ct, final MapleClient client, final boolean isChannel) {
        final MapleCharacter ret = new MapleCharacter(true); // Always true, it's change channel
        ret.client = client;
        if (!isChannel) {
            ret.client.setChannel(ct.channel);
        }

        ret.mount_id = ct.mount_id;
        ret.DebugMessage = ct.DebugMessage;
        ret.id = ct.characterid;
        ret.name = ct.name;
        ret.level = ct.level;
        ret.fame = ct.fame;

        ret.CRand = new PlayerRandomStream();

        ret.stats.str = ct.str;
        ret.stats.dex = ct.dex;
        ret.stats.int_ = ct.int_;
        ret.stats.luk = ct.luk;
        ret.stats.maxhp = ct.maxhp;
        ret.stats.maxmp = ct.maxmp;
        ret.stats.hp = ct.hp;
        ret.stats.mp = ct.mp;

        ret.chalktext = ct.chalkboard;
        ret.exp = ct.exp;
        ret.hpApUsed = ct.hpApUsed;
        ret.remainingSp = ct.remainingSp;
        ret.remainingAp = ct.remainingAp;
        ret.beans = ct.beans;
        ret.jf = ct.jf;
        ret.lqjf = ct.lqjf;
        ret.cjjf = ct.cjjf;
        ret.bossjf = ct.bossjf;
        ret.modid = ct.modid;
        ret.modsl = ct.modsl;
        ret.meso = ct.meso;
        ret.gmLevel = ct.gmLevel;
        ret.skinColor = ct.skinColor;
        ret.gender = ct.gender;
        ret.job = ct.job;
        ret.hair = ct.hair;
        ret.face = ct.face;
        ret.accountid = ct.accountid;
        ret.mapid = ct.mapid;
        ret.initialSpawnPoint = ct.initialSpawnPoint;
        ret.world = ct.world;
        ret.bookCover = ct.mBookCover;
        ret.dojo = ct.dojo;
        ret.dojoRecord = ct.dojoRecord;
        ret.guildid = ct.guildid;
        ret.guildrank = ct.guildrank;
        ret.allianceRank = ct.alliancerank;
        ret.points = ct.points;
        ret.vpoints = ct.vpoints;
        ret.fairyExp = ct.fairyExp;
        ret.marriageId = ct.marriageId;
        ret.currentrep = ct.currentrep;
        ret.totalrep = ct.totalrep;
        ret.charmessage = ct.charmessage;
        ret.expression = ct.expression;
        ret.constellation = ct.constellation;
        ret.blood = ct.blood;
        ret.month = ct.month;
        ret.day = ct.day;
        ret.vip = ct.vip;
        ret.skillzq = ct.skillzq;
        ret.makeMFC(ct.familyid, ct.seniorid, ct.junior1, ct.junior2);
        if (ret.guildid > 0) {
            ret.mgc = new MapleGuildCharacter(ret);
        }
        ret.buddylist = new BuddyList(ct.buddysize);
        ret.subcategory = ct.subcategory;
        ret.prefix = ct.prefix;

        if (isChannel) {
            final MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();
            ret.map = mapFactory.getMap(ret.mapid);
            if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                ret.map = mapFactory.getMap(100_000_000);
            } else if (ret.map.getForcedReturnId() != 999_999_999) {
                ret.map = ret.map.getForcedReturnMap();
            }
            MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
            if (portal == null) {
                portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                ret.initialSpawnPoint = 0;
            }
            ret.setPosition(portal.getPosition());

            final int messengerid = ct.messengerid;
            if (messengerid > 0) {
                ret.messenger = World.Messenger.getMessenger(messengerid);
            }
        } else {

            ret.messenger = null;
        }
        int partyid = ct.partyid;
        if (partyid >= 0) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null && party.getMemberById(ret.id) != null) {
                ret.party = party;
            }
        }

        MapleQuestStatus queststatus;
        MapleQuestStatus queststatus_from;
        MapleQuest quest;
        for (final Map.Entry<Integer, Object> qs : ct.Quest.entrySet()) {
            quest = MapleQuest.getInstance(qs.getKey());
            queststatus_from = (MapleQuestStatus) qs.getValue();

            queststatus = new MapleQuestStatus(quest, queststatus_from.getStatus());
            queststatus.setForfeited(queststatus_from.getForfeited());
            queststatus.setCustomData(queststatus_from.getCustomData());
            queststatus.setCompletionTime(queststatus_from.getCompletionTime());

            if (queststatus_from.getMobKills() != null) {
                for (final Map.Entry<Integer, Integer> mobkills : queststatus_from.getMobKills().entrySet()) {
                    queststatus.setMobKills(mobkills.getKey(), mobkills.getValue());
                }
            }
            ret.quests.put(quest, queststatus);
        }
        for (final Map.Entry<Integer, SkillEntry> qs : ct.Skills.entrySet()) {
            ret.skills.put(SkillFactory.getSkill(qs.getKey()), qs.getValue());
        }
        for (final Integer zz : ct.finishedAchievements) {
            ret.finishedAchievements.add(zz);
        }
        ret.monsterbook = new MonsterBook(ct.mbook);
        ret.inventory = (MapleInventory[]) ct.inventorys;
        ret.BlessOfFairy_Origin = ct.BlessOfFairy;
        ret.skillMacros = (SkillMacro[]) ct.skillmacro;
        ret.petStore = ct.petStore;
        ret.keylayout = new MapleKeyLayout(ct.keymap);
        ret.questinfo = ct.InfoQuest;
        ret.savedLocations = ct.savedlocation;
        ret.wishlist = ct.wishlist;
        ret.rocks = ct.rocks;
        ret.regrocks = ct.regrocks;
        ret.buddylist.loadFromTransfer(ct.buddies);
        // ret.lastfametime
        // ret.lastmonthfameids
        ret.keydown_skill = 0; // Keydown skill can't be brought over
        ret.lastfametime = ct.lastfametime;
        ret.lastmonthfameids = ct.famedcharacters;
        ret.storage = (MapleStorage) ct.storage;
        ret.cs = (CashShop) ct.cs;
        client.setAccountName(ct.accountname);
        ret.acash = ct.ACash;
        ret.lastGainHM = ct.lastGainHM;
        ret.maplepoints = ct.MaplePoints;
        ret.numClones = ct.clonez;
        ret.mount = new MapleMount(ret, ct.mount_itemid, GameConstants.isKOC(ret.job) ? 10_001_004 : (GameConstants.isAran(ret.job) ? 20_001_004 : (GameConstants.isEvan(ret.job) ? 20_011_004 : 1_004)), ct.mount_Fatigue, ct.mount_level, ct.mount_exp);

        ret.stats.recalcLocalStats(true);

        return ret;
    }

    /**
     *
     * @param charid
     * @param client
     * @param channelserver
     * @return
     */
    public static MapleCharacter loadCharFromDB(int charid, MapleClient client, boolean channelserver) {
        final MapleCharacter ret = new MapleCharacter(channelserver);
        ret.client = client;
        ret.id = charid;

        Connection con = DatabaseConnection.getConnection();
        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;

        try {
            ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException("Loading the Char Failed (char not found)");
            }

            ret.mount_id = rs.getInt("mountid");
            ret.vip = rs.getInt("vip");
            ret.skillzq = rs.getInt("skillzq");
            ret.name = rs.getString("name");
            ret.level = rs.getShort("level");
            ret.fame = rs.getShort("fame");
            ret.modid = rs.getInt("modid");
            ret.modsl = rs.getInt("modsl");
            ret.jf = rs.getInt("jf");
            ret.lqjf = rs.getInt("lqjf");
            ret.bossjf = rs.getInt("bossjf");
            ret.cjjf = rs.getInt("cjjf");
            ret.stats.str = rs.getShort("str");
            ret.stats.dex = rs.getShort("dex");
            ret.stats.int_ = rs.getShort("int");
            ret.stats.luk = rs.getShort("luk");
            ret.stats.maxhp = rs.getShort("maxhp");
            ret.stats.maxmp = rs.getShort("maxmp");
            ret.stats.hp = rs.getShort("hp");
            ret.stats.mp = rs.getShort("mp");
            ret.exp = rs.getInt("exp");
            ret.hpApUsed = rs.getShort("hpApUsed");
            final String[] sp = rs.getString("sp").split(",");
            for (int i = 0; i < ret.remainingSp.length; i++) {
                ret.remainingSp[i] = Integer.parseInt(sp[i]);
            }
            ret.remainingAp = rs.getShort("ap");
            ret.beans = rs.getInt("beans");
            ret.meso = rs.getInt("meso");
            ret.gmLevel = rs.getByte("gm");
            ret.skinColor = rs.getByte("skincolor");
            ret.gender = rs.getByte("gender");
            ret.job = rs.getShort("job");
            ret.hair = rs.getInt("hair");
            ret.face = rs.getInt("face");
            ret.accountid = rs.getInt("accountid");
            ret.mapid = rs.getInt("map");
            ret.initialSpawnPoint = rs.getByte("spawnpoint");
            ret.world = rs.getByte("world");
            ret.guildid = rs.getInt("guildid");
            ret.guildrank = rs.getByte("guildrank");
            ret.allianceRank = rs.getByte("allianceRank");
            ret.currentrep = rs.getInt("currentrep");
            ret.totalrep = rs.getInt("totalrep");

            ret.makeMFC(rs.getInt("familyid"), rs.getInt("seniorid"), rs.getInt("junior1"), rs.getInt("junior2"));
            if (ret.guildid > 0) {
                ret.mgc = new MapleGuildCharacter(ret);
            }
            ret.buddylist = new BuddyList(rs.getByte("buddyCapacity"));
            ret.subcategory = rs.getByte("subcategory");
            ret.mount = new MapleMount(ret, 0, ret.job > 1_000 && ret.job < 2_000 ? 10_001_004 : (ret.job >= 2_000 ? (ret.job == 2_001 || (ret.job >= 2_200 && ret.job <= 2_218) ? 20_011_004 : (ret.job >= 3_000 ? 30_001_004 : 20_001_004)) : 1_004), (byte) 0, (byte) 1, 0);
            ret.rank = rs.getInt("rank");
            ret.rankMove = rs.getInt("rankMove");
            ret.jobRank = rs.getInt("jobRank");
            ret.jobRankMove = rs.getInt("jobRankMove");
            ret.marriageId = rs.getInt("marriageId");
            ret.charmessage = rs.getString("charmessage");
            ret.expression = rs.getInt("expression");
            ret.constellation = rs.getInt("constellation");
            ret.blood = rs.getInt("blood");
            ret.month = rs.getInt("month");
            ret.day = rs.getInt("day");
            ret.prefix = rs.getInt("prefix");

            if (channelserver) {
                MapleMapFactory mapFactory = ChannelServer.getInstance(client.getChannel()).getMapFactory();//又爆这句错误
                ret.map = mapFactory.getMap(ret.mapid);
                if (ret.map == null) { //char is on a map that doesn't exist warp it to henesys
                    ret.map = mapFactory.getMap(100_000_000);
                }
                MaplePortal portal = ret.map.getPortal(ret.initialSpawnPoint);
                if (portal == null) {
                    portal = ret.map.getPortal(0); // char is on a spawnpoint that doesn't exist - select the first spawnpoint instead
                    ret.initialSpawnPoint = 0;
                }
                ret.setPosition(portal.getPosition());

                int partyid = rs.getInt("party");
                if (partyid >= 0) {
                    MapleParty party = World.Party.getParty(partyid);
                    if (party != null && party.getMemberById(ret.id) != null) {
                        ret.party = party;
                    }
                }
                ret.bookCover = rs.getInt("monsterbookcover");
                ret.dojo = rs.getInt("dojo_pts");
                ret.dojoRecord = rs.getByte("dojoRecord");
                final String[] pets = rs.getString("pets").split(",");
                for (int i = 0; i < ret.petStore.length; i++) {
                    ret.petStore[i] = Byte.parseByte(pets[i]);
                }
                rs.close();
                ps.close();
                ps = con.prepareStatement("SELECT * FROM achievements WHERE accountid = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.finishedAchievements.add(rs.getInt("achievementid"));
                }

            }
            rs.close();
            ps.close();

            boolean compensate_previousEvans = false;
            ps = con.prepareStatement("SELECT * FROM queststatus WHERE characterid = ?");
            ps.setInt(1, charid);
            rs = ps.executeQuery();
            pse = con.prepareStatement("SELECT * FROM queststatusmobs WHERE queststatusid = ?");

            while (rs.next()) {
                final int id = rs.getInt("quest");
                if (id == 170_000) {
                    compensate_previousEvans = true;
                }
                final MapleQuest q = MapleQuest.getInstance(id);
                final MapleQuestStatus status = new MapleQuestStatus(q, rs.getByte("status"));
                final long cTime = rs.getLong("time");
                if (cTime > -1) {
                    status.setCompletionTime(cTime * 1_000);
                }
                status.setForfeited(rs.getInt("forfeited"));
                status.setCustomData(rs.getString("customData"));
                ret.quests.put(q, status);
                pse.setInt(1, rs.getInt("queststatusid"));
                try (ResultSet rsMobs = pse.executeQuery()) {
                    while (rsMobs.next()) {
                        status.setMobKills(rsMobs.getInt("mob"), rsMobs.getInt("count"));
                    }
                }
            }
            rs.close();
            ps.close();
            pse.close();

            if (channelserver) {
                ret.CRand = new PlayerRandomStream();
                ret.monsterbook = MonsterBook.loadCards(charid);

                ps = con.prepareStatement("SELECT * FROM inventoryslot where characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    throw new RuntimeException("No Inventory slot column found in SQL. [inventoryslot]*********************");
                }
                ret.getInventory(MapleInventoryType.EQUIP).setSlotLimit(rs.getByte("equip"));
                ret.getInventory(MapleInventoryType.USE).setSlotLimit(rs.getByte("use"));
                ret.getInventory(MapleInventoryType.SETUP).setSlotLimit(rs.getByte("setup"));
                ret.getInventory(MapleInventoryType.ETC).setSlotLimit(rs.getByte("etc"));
                ret.getInventory(MapleInventoryType.CASH).setSlotLimit(rs.getByte("cash"));
                ps.close();
                rs.close();

                for (Pair<IItem, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(false, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                    if (mit.getLeft().getPet() != null) {
                        ret.pets.add(mit.getLeft().getPet());
                    }
                }

                ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                if (rs.next()) {
                    ret.getClient().setAccountName(rs.getString("name"));
                    ret.lastGainHM = rs.getLong("lastGainHM");
                    ret.acash = rs.getInt("ACash");
                    ret.maplepoints = rs.getInt("mPoints");
                    ret.points = rs.getInt("points");
                    ret.vpoints = rs.getInt("vpoints");

                    if (rs.getTimestamp("lastlogon") != null) {
                        final Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(rs.getTimestamp("lastlogon").getTime());
                        /*
                         * if (cal.get(Calendar.DAY_OF_WEEK) + 1 ==
                         * Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                         * ret.acash += 500; }
                         */
                    }
                    rs.close();
                    ps.close();

                    ps = con.prepareStatement("UPDATE accounts SET lastlogon = CURRENT_TIMESTAMP() WHERE id = ?");
                    ps.setInt(1, ret.accountid);
                    ps.executeUpdate();
                } else {
                    rs.close();
                }
                ps.close();

                ps = con.prepareStatement("SELECT * FROM questinfo WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                while (rs.next()) {
                    ret.questinfo.put(rs.getInt("quest"), rs.getString("customData"));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT skillid, skilllevel, masterlevel, expiration FROM skills WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                ISkill skil;
                while (rs.next()) {
                    skil = SkillFactory.getSkill(rs.getInt("skillid"));
                    if (skil != null && GameConstants.isApplicableSkill(rs.getInt("skillid"))) {
                        ret.skills.put(skil, new SkillEntry(rs.getByte("skilllevel"), rs.getByte("masterlevel"), rs.getLong("expiration")));
                    } else if (skil == null) { //doesnt. exist. e.g. bb
                        ret.remainingSp[GameConstants.getSkillBookForSkill(rs.getInt("skillid"))] += rs.getByte("skilllevel");
                    }
                }
                rs.close();
                ps.close();

                ret.expirationTask(false); //do it now

                // Bless of Fairy handling
                ps = con.prepareStatement("SELECT * FROM characters WHERE accountid = ? ORDER BY level DESC");
                ps.setInt(1, ret.accountid);
                rs = ps.executeQuery();
                byte maxlevel_ = 0;
                while (rs.next()) {
                    if (rs.getInt("id") != charid) { // Not this character
                        byte maxlevel = (byte) (rs.getShort("level") / 10);

                        if (maxlevel > 20) {
                            maxlevel = 20;
                        }
                        if (maxlevel > maxlevel_) {
                            maxlevel_ = maxlevel;
                            ret.BlessOfFairy_Origin = rs.getString("name");
                        }

                    } else if (charid < 17_000 && !compensate_previousEvans && ret.job >= 2_200 && ret.job <= 2_218) { //compensate, watch max charid
                        for (int i = 0; i <= GameConstants.getSkillBook(ret.job); i++) {
                            ret.remainingSp[i] += 2; //2 that they missed. gg
                        }
                        ret.setQuestAdd(MapleQuest.getInstance(170_000), (byte) 0, null); //set it so never again
                    }
                }
                ret.skills.put(SkillFactory.getSkill(GameConstants.getBOF_ForJob(ret.job)), new SkillEntry(maxlevel_, (byte) 0, -1));
                ps.close();
                rs.close();
                // END

                ps = con.prepareStatement("SELECT * FROM skillmacros WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int position;
                while (rs.next()) {
                    position = rs.getInt("position");
                    SkillMacro macro = new SkillMacro(rs.getInt("skill1"), rs.getInt("skill2"), rs.getInt("skill3"), rs.getString("name"), rs.getInt("shout"), position);
                    ret.skillMacros[position] = macro;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `key`,`type`,`action` FROM keymap WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();

                final Map<Integer, Pair<Byte, Integer>> keyb = ret.keylayout.Layout();
                while (rs.next()) {
                    keyb.put(Integer.valueOf(rs.getInt("key")), new Pair<>(rs.getByte("type"), rs.getInt("action")));
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `locationtype`,`map` FROM savedlocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                while (rs.next()) {
                    ret.savedLocations[rs.getInt("locationtype")] = rs.getInt("map");
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT `characterid_to`,`when` FROM famelog WHERE characterid = ? AND DATEDIFF(NOW(),`when`) < 30");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                ret.lastfametime = 0;
                ret.lastmonthfameids = new ArrayList<>(31);
                while (rs.next()) {
                    ret.lastfametime = Math.max(ret.lastfametime, rs.getTimestamp("when").getTime());
                    ret.lastmonthfameids.add(rs.getInt("characterid_to"));
                }
                rs.close();
                ps.close();

                ret.buddylist.loadFromDb(charid);
                ret.storage = MapleStorage.loadStorage(ret.accountid);
                ret.cs = new CashShop(ret.accountid, charid, ret.getJob());

                ps = con.prepareStatement("SELECT sn FROM wishlist WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int i = 0;
                while (rs.next()) {
                    ret.wishlist[i] = rs.getInt("sn");
                    i++;
                }
                while (i < 10) {
                    ret.wishlist[i] = 0;
                    i++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM trocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                int r = 0;
                while (rs.next()) {
                    ret.rocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 10) {
                    ret.rocks[r] = 999_999_999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT mapid FROM regrocklocations WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                r = 0;
                while (rs.next()) {
                    ret.regrocks[r] = rs.getInt("mapid");
                    r++;
                }
                while (r < 5) {
                    ret.regrocks[r] = 999_999_999;
                    r++;
                }
                rs.close();
                ps.close();

                ps = con.prepareStatement("SELECT * FROM mountdata WHERE characterid = ?");
                ps.setInt(1, charid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    throw new RuntimeException("No mount data found on SQL column");
                }
                final IItem mount = ret.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18);
                ret.mount = new MapleMount(ret, mount != null ? mount.getItemId() : 0, ret.job > 1_000 && ret.job < 2_000 ? 10_001_004 : (ret.job >= 2_000 ? (ret.job == 2_001 || ret.job >= 2_200 ? 20_011_004 : (ret.job >= 3_000 ? 30_001_004 : 20_001_004)) : 1_004), rs.getByte("Fatigue"), rs.getByte("Level"), rs.getInt("Exp"));
                ps.close();
                rs.close();

                ret.stats.recalcLocalStats(true);
            } else { // Not channel server
                for (Pair<IItem, MapleInventoryType> mit : ItemLoader.INVENTORY.loadItems(true, charid).values()) {
                    ret.getInventory(mit.getRight()).addFromDB(mit.getLeft());
                }
            }
        } catch (SQLException ess) {
            ess.printStackTrace();
            System.out.println("加载角色数据信息出错...");
            FileoutputUtil.outputFileError("log\\Packet_Except.log", ess);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ignore) {
            }
        }
        return ret;
    }

    /**
     *
     * @param chr
     * @param type
     * @param db
     */
    public static void saveNewCharToDB(final MapleCharacter chr, final int type, final boolean db) {
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;
        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);

            ps = con.prepareStatement("INSERT INTO characters (level, fame, str, dex, luk, `int`, exp, hp, mp, maxhp, maxmp, sp, ap, gm, skincolor, gender, job, hair, face, map, meso, hpApUsed, spawnpoint, party, buddyCapacity, monsterbookcover, dojo_pts, dojoRecord, pets, subcategory, marriageId, currentrep, totalrep, prefix, accountid, name, world, mountid,vip,skillzq) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, 1); // Level
            ps.setShort(2, (short) 0); // Fame
            final PlayerStats stat = chr.stats;
            ps.setShort(3, stat.getStr()); // Str
            ps.setShort(4, stat.getDex()); // Dex
            ps.setShort(5, stat.getInt()); // Int
            ps.setShort(6, stat.getLuk()); // Luk
            ps.setInt(7, 0); // EXP
            ps.setShort(8, stat.getHp()); // HP
            ps.setShort(9, stat.getMp());
            ps.setShort(10, stat.getMaxHp()); // MP
            ps.setShort(11, stat.getMaxMp());
            ps.setString(12, "0,0,0,0,0,0,0,0,0,0"); // Remaining SP
            ps.setShort(13, (short) 0); // Remaining AP
            ps.setInt(14, chr.getClient().gm ? 5 : 0); // GM Level
            ps.setByte(15, chr.skinColor);
            ps.setByte(16, chr.gender);
            ps.setShort(17, chr.job);
            ps.setInt(18, chr.hair);
            ps.setInt(19, chr.face);
            ps.setInt(20, type == 1 ? 0 : (type == 2 ? 0 : (type == 1 ? 130_030_000 : 910_000_000)));//新手出生地图140000000战神
            ps.setInt(21, chr.meso); // Meso
            ps.setShort(22, (short) 0); // HP ap used
            ps.setByte(23, (byte) 0); // Spawnpoint
            ps.setInt(24, -1); // Party
            ps.setByte(25, chr.buddylist.getCapacity()); // Buddylist
            ps.setInt(26, 0); // Monster book cover
            ps.setInt(27, 0); // Dojo
            ps.setInt(28, 0); // Dojo record
            ps.setString(29, "-1,-1,-1");
            ps.setInt(30, 0); //for now
            ps.setInt(31, 0); //marriage ID
            ps.setInt(32, 0); //current reps
            ps.setInt(33, 0); //total reps
            ps.setInt(34, chr.prefix);
            ps.setInt(35, chr.getAccountID());
            ps.setString(36, chr.name);
            ps.setByte(37, chr.world);
            ps.setInt(38, chr.mount_id);
            ps.setInt(39, chr.vip);
            ps.setInt(40, chr.skillzq);

            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                chr.id = rs.getInt(1);
            } else {
                throw new DatabaseException("Inserting char failed.");
            }
            ps.close();
            rs.close();
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (final MapleQuestStatus q : chr.quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1_000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();

                if (q.hasMobKills()) {
                    for (int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.executeUpdate();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();

            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 32); // Eq
            ps.setByte(3, (byte) 32); // Use
            ps.setByte(4, (byte) 32); // Setup
            ps.setByte(5, (byte) 32); // ETC
            ps.setByte(6, (byte) 60); // Cash
            ps.execute();
            ps.close();

            ps = con.prepareStatement("INSERT INTO mountdata (characterid, `Level`, `Exp`, `Fatigue`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            ps.setByte(2, (byte) 1);
            ps.setInt(3, 0);
            ps.setByte(4, (byte) 0);
            ps.execute();
            ps.close();

            List<Pair<IItem, MapleInventoryType>> listing = new ArrayList<>();
            for (final MapleInventory iv : chr.inventory) {
                for (final IItem item : iv.list()) {
                    listing.add(new Pair<>(item, iv.getType()));
                }
            }
            ItemLoader.INVENTORY.saveItems(listing, con, chr.id);

            /*
             * // SEA 102 final int[] array1 = {2, 3, 4, 5, 6, 7, 16, 17, 18,
             * 19, 23, 25, 26, 27, 31, 34, 37, 38, 41, 44, 45, 46, 50, 57, 59,
             * 60, 61, 62, 63, 64, 65, 8, 9, 24, 30}; final int[] array2 = {4,
             * 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 4,
             * 5, 6, 6, 6, 6, 6, 6, 6, 4, 4, 4, 4}; final int[] array3 = {10,
             * 12, 13, 18, 6, 11, 8, 5, 0, 4, 1, 19, 14, 15, 3, 17, 9, 20, 22,
             * 50, 51, 52, 7, 53, 100, 101, 102, 103, 104, 105, 106, 16, 23, 24,
             * 2};
             */
            int[] array1 = {2, 3, 4, 5, 6, 7, 16, 17, 18, 19, 23, 25, 26, 27, 29, 31, 34, 35, 37, 38, 40, 41, 43, 44, 45, 46, 48, 50, 56, 57, 59, 60, 61, 62, 63, 64, 65};
            int[] array2 = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 4, 4, 4, 5, 5, 6, 6, 6, 6, 6, 6, 6};
            int[] array3 = {10, 12, 13, 18, 24, 21, 8, 5, 0, 4, 1, 19, 14, 15, 52, 2, 17, 11, 3, 20, 16, 23, 9, 50, 51, 6, 22, 7, 53, 54, 100, 101, 102, 103, 104, 105, 106};

            ps = con.prepareStatement("INSERT INTO keymap (characterid, `key`, `type`, `action`) VALUES (?, ?, ?, ?)");
            ps.setInt(1, chr.id);
            for (int i = 0; i < array1.length; i++) {
                ps.setInt(2, array1[i]);
                ps.setInt(3, array2[i]);
                ps.setInt(4, array3[i]);
                ps.execute();
            }
            ps.close();

            con.commit();
        } catch (SQLException | DatabaseException e) {
            e.printStackTrace();
            FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
            System.err.println("[charsave] Error saving character data");
            try {
                con.rollback();
            } catch (SQLException ex) {
                e.printStackTrace();
                FileoutputUtil.outputFileError("log\\Packet_Except.log", ex);
                System.err.println("[charsave] Error Rolling Back");
            }
        } finally {
            try {
                if (pse != null) {
                    pse.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                e.printStackTrace();
                FileoutputUtil.outputFileError("log\\Packet_Except.log", e);
                System.err.println("[charsave] Error going back to autocommit mode");
            }
        }
    }

    /**
     *
     * @param dc
     * @param fromcs
     */
    public void saveToDB(boolean dc, boolean fromcs) {
        if (isClone()) {
            return;
        }
        Connection con = DatabaseConnection.getConnection();

        PreparedStatement ps = null;
        PreparedStatement pse = null;
        ResultSet rs = null;

        try {
            con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            con.setAutoCommit(false);

            ps = con.prepareStatement("UPDATE characters SET level = ?, fame = ?, str = ?, dex = ?, luk = ?, `int` = ?, exp = ?, hp = ?, mp = ?, maxhp = ?, maxmp = ?, sp = ?, ap = ?, gm = ?, skincolor = ?, gender = ?, job = ?, hair = ?, face = ?, map = ?, meso = ?, hpApUsed = ?, spawnpoint = ?, party = ?, buddyCapacity = ?, monsterbookcover = ?, dojo_pts = ?, dojoRecord = ?, pets = ?, subcategory = ?, marriageId = ?, currentrep = ?, totalrep = ?, charmessage = ?, expression = ?, constellation = ?, blood = ?, month = ?, day = ?, beans = ?, prefix = ?, name = ?, mountid = ?, vip = ?, skillzq = ?, modid = ?, modsl = ?, jf = ?, lqjf = ?, bossjf = ?, cjjf = ?  WHERE id = ?", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, level);
            ps.setShort(2, fame);
            ps.setShort(3, stats.getStr());
            ps.setShort(4, stats.getDex());
            ps.setShort(5, stats.getLuk());
            ps.setShort(6, stats.getInt());
            ps.setInt(7, exp);
            ps.setShort(8, stats.getHp() < 1 ? 50 : stats.getHp());
            ps.setShort(9, stats.getMp());
            ps.setShort(10, stats.getMaxHp());
            ps.setShort(11, stats.getMaxMp());
            final StringBuilder sps = new StringBuilder();
            for (int i = 0; i < remainingSp.length; i++) {
                sps.append(remainingSp[i]);
                sps.append(",");
            }
            final String sp = sps.toString();
            ps.setString(12, sp.substring(0, sp.length() - 1));
            ps.setShort(13, remainingAp);
            ps.setByte(14, gmLevel);
            ps.setByte(15, skinColor);
            ps.setByte(16, gender);
            ps.setShort(17, job);
            ps.setInt(18, hair);
            ps.setInt(19, face);
            if (!fromcs && map != null) {
                if (map.getForcedReturnId() != 999_999_999) {
                    ps.setInt(20, map.getForcedReturnId());
                } else {
                    ps.setInt(20, stats.getHp() < 1 ? map.getReturnMapId() : map.getId());
                }
            } else {
                ps.setInt(20, mapid);
            }
            ps.setInt(21, meso);
            ps.setShort(22, hpApUsed);
            if (map == null) {
                ps.setByte(23, (byte) 0);
            } else {
                final MaplePortal closest = map.findClosestSpawnpoint(getPosition());
                ps.setByte(23, (byte) (closest != null ? closest.getId() : 0));
            }
            ps.setInt(24, party != null ? party.getId() : -1);
            ps.setShort(25, buddylist.getCapacity());
            ps.setInt(26, bookCover);
            ps.setInt(27, dojo);
            ps.setInt(28, dojoRecord);
            final StringBuilder petz = new StringBuilder();
            int petLength = 0;
            for (final MaplePet pet : pets) {
                pet.saveToDb();
                if (pet.getSummoned()) {

                    petz.append(pet.getInventoryPosition());
                    petz.append(",");
                    petLength++;
                }
            }
            while (petLength < 3) {
                petz.append("-1,");
                petLength++;
            }
            final String petstring = petz.toString();
            ps.setString(29, petstring.substring(0, petstring.length() - 1));
            ps.setByte(30, subcategory);
            ps.setInt(31, marriageId);
            ps.setInt(32, currentrep);
            ps.setInt(33, totalrep);
            ps.setString(34, charmessage);
            ps.setInt(35, expression);
            ps.setInt(36, constellation);
            ps.setInt(37, blood);
            ps.setInt(38, month);
            ps.setInt(39, day);
            ps.setInt(40, beans);
            ps.setInt(41, prefix);
            ps.setString(42, name);
            ps.setInt(43, mount_id);
            ps.setInt(44, vip);
            ps.setInt(45, skillzq);
            ps.setInt(46, modid);
            ps.setInt(47, modsl);
            ps.setInt(48, jf);
            ps.setInt(49, lqjf);
            ps.setInt(50, bossjf);
            ps.setInt(51, cjjf);
            ps.setInt(52, id);

            if (ps.executeUpdate() < 1) {
                ps.close();
                throw new DatabaseException("Character not in database (" + id + ")");
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?");
            for (int i = 0; i < 5; i++) {
                final SkillMacro macro = skillMacros[i];
                if (macro != null) {
                    ps = con.prepareStatement("INSERT INTO skillmacros (characterid, skill1, skill2, skill3, name, shout, position) VALUES (?, ?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, id);
                    ps.setInt(2, macro.getSkill1());
                    ps.setInt(3, macro.getSkill2());
                    ps.setInt(4, macro.getSkill3());
                    ps.setString(5, macro.getName());
                    ps.setInt(6, macro.getShout());
                    ps.setInt(7, i);
                    ps.execute();
                    ps.close();
                }
            }

            deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO inventoryslot (characterid, `equip`, `use`, `setup`, `etc`, `cash`) VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, id);
            ps.setByte(2, getInventory(MapleInventoryType.EQUIP).getSlotLimit());
            ps.setByte(3, getInventory(MapleInventoryType.USE).getSlotLimit());
            ps.setByte(4, getInventory(MapleInventoryType.SETUP).getSlotLimit());
            ps.setByte(5, getInventory(MapleInventoryType.ETC).getSlotLimit());
            ps.setByte(6, getInventory(MapleInventoryType.CASH).getSlotLimit());
            ps.execute();
            ps.close();

            saveInventory(con);

            deleteWhereCharacterId(con, "DELETE FROM questinfo WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO questinfo (`characterid`, `quest`, `customData`) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            for (final Entry<Integer, String> q : questinfo.entrySet()) {
                ps.setInt(2, q.getKey());
                ps.setString(3, q.getValue());
                ps.execute();
            }
            ps.close();

            deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO queststatus (`queststatusid`, `characterid`, `quest`, `status`, `time`, `forfeited`, `customData`) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            pse = con.prepareStatement("INSERT INTO queststatusmobs VALUES (DEFAULT, ?, ?, ?)");
            ps.setInt(1, id);
            for (final MapleQuestStatus q : quests.values()) {
                ps.setInt(2, q.getQuest().getId());
                ps.setInt(3, q.getStatus());
                ps.setInt(4, (int) (q.getCompletionTime() / 1_000));
                ps.setInt(5, q.getForfeited());
                ps.setString(6, q.getCustomData());
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                rs.next();

                if (q.hasMobKills()) {
                    for (int mob : q.getMobKills().keySet()) {
                        pse.setInt(1, rs.getInt(1));
                        pse.setInt(2, mob);
                        pse.setInt(3, q.getMobKills(mob));
                        pse.executeUpdate();
                    }
                }
                rs.close();
            }
            ps.close();
            pse.close();

            deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO skills (characterid, skillid, skilllevel, masterlevel, expiration) VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1, id);

            for (final Entry<ISkill, SkillEntry> skill : skills.entrySet()) {
                if (GameConstants.isApplicableSkill(skill.getKey().getId())) { //do not save additional skills
                    ps.setInt(2, skill.getKey().getId());
                    ps.setByte(3, skill.getValue().skillevel);
                    ps.setByte(4, skill.getValue().masterlevel);
                    ps.setLong(5, skill.getValue().expiration);
                    ps.execute();
                }
            }
            ps.close();

            List<MapleCoolDownValueHolder> cd = getCooldowns();
            if (dc && cd.size() > 0) {
                ps = con.prepareStatement("INSERT INTO skills_cooldowns (charid, SkillID, StartTime, length) VALUES (?, ?, ?, ?)");
                ps.setInt(1, getId());
                for (final MapleCoolDownValueHolder cooling : cd) {
                    ps.setInt(2, cooling.skillId);
                    ps.setLong(3, cooling.startTime);
                    ps.setLong(4, cooling.length);
                    ps.execute();
                }
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO savedlocations (characterid, `locationtype`, `map`) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            for (final SavedLocationType savedLocationType : SavedLocationType.values()) {
                if (savedLocations[savedLocationType.getValue()] != -1) {
                    ps.setInt(2, savedLocationType.getValue());
                    ps.setInt(3, savedLocations[savedLocationType.getValue()]);
                    ps.execute();
                }
            }
            ps.close();

            ps = con.prepareStatement("DELETE FROM achievements WHERE accountid = ?");
            ps.setInt(1, accountid);
            ps.executeUpdate();
            ps.close();
            ps = con.prepareStatement("INSERT INTO achievements(charid, achievementid, accountid) VALUES(?, ?, ?)");
            for (Integer achid : finishedAchievements) {
                ps.setInt(1, id);
                ps.setInt(2, achid);
                ps.setInt(3, accountid);
                ps.executeUpdate();
            }
            ps.close();

            /*
             * deleteWhereCharacterId(con, "DELETE FROM buddies WHERE
             * characterid = ? AND pending = 0"); ps =
             * con.prepareStatement("INSERT INTO buddies (characterid,
             * `buddyid`, `pending`) VALUES (?, ?, 0)"); ps.setInt(1, id); for
             * (BuddylistEntry entry : buddylist.getBuddies()) { if
             * (entry.isVisible()) { ps.setInt(2, entry.getCharacterId());
             * ps.execute(); } } ps.close();
             */
            // if (buddylist.changed()) {
            deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?");
            ps = con.prepareStatement("INSERT INTO buddies (characterid, `buddyid`, `pending`) VALUES (?, ?, ?)");
            ps.setInt(1, id);
            for (BuddyEntry entry : buddylist.getBuddies()) {
                if (entry != null) {
                    ps.setInt(2, entry.getCharacterId());
                    ps.setInt(3, entry.isVisible() ? 0 : 1);
                    ps.execute();
                }
            }
            ps.close();

            ps = con.prepareStatement("UPDATE accounts SET `ACash` = ?, `mPoints` = ?, `points` = ?, `vpoints` = ? WHERE id = ?");
            ps.setInt(1, acash);
            ps.setInt(2, maplepoints);
            ps.setInt(3, points);
            ps.setInt(4, vpoints);
            ps.setInt(5, client.getAccID());
            ps.execute();
            ps.close();

            if (storage != null) {
                storage.saveToDB();
            }

            ps = con.prepareStatement("UPDATE accounts SET `lastGainHM` = ? WHERE id = ?");
            ps.setLong(1, lastGainHM);
            ps.setInt(2, client.getAccID());
            ps.execute();
            ps.close();

            if (cs != null) {
                cs.save();
            }
            PlayerNPC.updateByCharId(this);
            keylayout.saveKeys(id);
            mount.saveMount(id);
            monsterbook.saveCards(id);

            deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?");
            for (int i = 0; i < getWishlistSize(); i++) {
                ps = con.prepareStatement("INSERT INTO wishlist(characterid, sn) VALUES(?, ?) ");
                ps.setInt(1, getId());
                ps.setInt(2, wishlist[i]);
                ps.execute();
                ps.close();
            }

            deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?");
            for (int i = 0; i < rocks.length; i++) {
                if (rocks[i] != 999_999_999) {
                    ps = con.prepareStatement("INSERT INTO trocklocations(characterid, mapid) VALUES(?, ?) ");
                    ps.setInt(1, getId());
                    ps.setInt(2, rocks[i]);
                    ps.execute();
                    ps.close();
                }
            }

            deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?");
            for (int i = 0; i < regrocks.length; i++) {
                if (regrocks[i] != 999_999_999) {
                    ps = con.prepareStatement("INSERT INTO regrocklocations(characterid, mapid) VALUES(?, ?) ");
                    ps.setInt(1, getId());
                    ps.setInt(2, regrocks[i]);
                    ps.execute();
                    ps.close();
                }
            }

            con.commit();
        } catch (SQLException | DatabaseException | UnsupportedOperationException e) {
            //e.printStackTrace();
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
            // System.err.println(MapleClient.getLogMessage(this, "[charsave] Error saving character data") + e);
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, ex);
                //     System.err.println(MapleClient.getLogMessage(this, "[charsave] Error Rolling Back") + e);
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (pse != null) {
                    pse.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.setAutoCommit(true);
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (SQLException e) {
                FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                //    System.err.println(MapleClient.getLogMessage(this, "[charsave] Error going back to autocommit mode") + e);
            }
        }
    }

    private void deleteWhereCharacterId(Connection con, String sql) throws SQLException {
        deleteWhereCharacterId(con, sql, id);
    }

    /**
     *
     * @param con
     * @param sql
     * @param id
     * @throws SQLException
     */
    public static void deleteWhereCharacterId(Connection con, String sql, int id) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     *
     * @param con
     * @throws SQLException
     */
    public void saveInventory(final Connection con) throws SQLException {
        List<Pair<IItem, MapleInventoryType>> listing = new ArrayList<>();
        for (final MapleInventory iv : inventory) {
            for (final IItem item : iv.list()) {
                listing.add(new Pair<>(item, iv.getType()));
            }
        }
        if (con != null) {
            ItemLoader.INVENTORY.saveItems(listing, con, id);
        } else {
            ItemLoader.INVENTORY.saveItems(listing, id);
        }
    }

    /**
     *
     * @return
     */
    public final PlayerStats getStat() {
        return stats;
    }

    /**
     *
     * @return
     */
    public final PlayerRandomStream CRand() {
        return CRand;
    }

    /**
     *
     * @param mplew
     */
    public final void QuestInfoPacket(final tools.data.output.MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(questinfo.size());

        for (final Entry<Integer, String> q : questinfo.entrySet()) {
            mplew.writeShort(q.getKey());
            mplew.writeMapleAsciiString(q.getValue() == null ? "" : q.getValue());
        }
    }

    /**
     *
     * @param questid
     * @param data
     */
    public final void updateInfoQuest(final int questid, final String data) {
        questinfo.put(questid, data);
        client.getSession().write(MaplePacketCreator.updateInfoQuest(questid, data));
    }

    /**
     *
     * @param questid
     * @return
     */
    public final String getInfoQuest(final int questid) {
        if (questinfo.containsKey(questid)) {
            return questinfo.get(questid);
        }
        return "";
    }

    /**
     *
     * @return
     */
    public final int getNumQuest() {
        int i = 0;
        for (final MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !(q.isCustom())) {
                i++;
            }
        }
        return i;
    }

    /**
     *
     * @param quest
     * @return
     */
    public final byte getQuestStatus(final int quest) {
        return getQuest(MapleQuest.getInstance(quest)).getStatus();
    }

    /**
     *
     * @param quest
     * @return
     */
    public final MapleQuestStatus getQuest(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            return new MapleQuestStatus(quest, (byte) 0);
        }
        return quests.get(quest);
    }

    /**
     *
     * @param quest
     */
    public void setQuestAdd(int quest) {
        setQuestAddZ(MapleQuest.getInstance(quest), (byte) 2, null);
    }

    /**
     *
     * @param quest
     * @param status
     * @param customData
     */
    public final void setQuestAddZ(final MapleQuest quest, final byte status, final String customData) {

        final MapleQuestStatus stat = new MapleQuestStatus(quest, status);
        stat.setCustomData(customData);
        quests.put(quest, stat);

    }

    /**
     *
     * @param quest
     * @param status
     * @param customData
     */
    public final void setQuestAdd(final MapleQuest quest, final byte status, final String customData) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus stat = new MapleQuestStatus(quest, status);
            stat.setCustomData(customData);
            quests.put(quest, stat);
        }
    }

    /**
     *
     * @param quest
     * @return
     */
    public final MapleQuestStatus getQuestNAdd(final MapleQuest quest) {
        if (!quests.containsKey(quest)) {
            final MapleQuestStatus status = new MapleQuestStatus(quest, (byte) 0);
            quests.put(quest, status);
            return status;
        }
        return quests.get(quest);
    }

    /**
     *
     * @param quest
     * @return
     */
    public MapleQuestStatus getQuestRemove(MapleQuest quest) {
        return (MapleQuestStatus) this.quests.remove(quest);
    }

    /**
     *
     * @param quest
     * @return
     */
    public final MapleQuestStatus getQuestNoAdd(final MapleQuest quest) {
        return quests.get(quest);
    }

    /**
     *
     * @param quest
     */
    public final void updateQuest(final MapleQuestStatus quest) {
        updateQuest(quest, false);
    }

    /**
     *
     * @param quest
     * @param update
     */
    public final void updateQuest(final MapleQuestStatus quest, final boolean update) {
        quests.put(quest.getQuest(), quest);
        if (!(quest.isCustom())) {
            client.getSession().write(MaplePacketCreator.updateQuest(quest));
            if (quest.getStatus() == 1 && !update) {
                client.getSession().write(MaplePacketCreator.updateQuestInfo(this, quest.getQuest().getId(), quest.getNpc(), (byte) 8));
            }
        }
    }

    /**
     *
     * @return
     */
    public final Map<Integer, String> getInfoQuest_Map() {
        return questinfo;
    }

    /**
     *
     * @return
     */
    public final Map<MapleQuest, MapleQuestStatus> getQuest_Map() {
        return quests;
    }

    /**
     *
     * @param skillid
     * @return
     */
    public boolean isActiveBuffedValue(int skillid) {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param effect
     * @return
     */
    public Integer getBuffedValue(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.value;
    }

    /**
     *
     * @param effect
     * @return
     */
    public final Integer getBuffedSkill_X(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getX();
    }

    /**
     *
     * @param effect
     * @return
     */
    public final Integer getBuffedSkill_Y(final MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return null;
        }
        return mbsvh.effect.getY();
    }

    /**
     *
     * @param stat
     * @param skill
     * @return
     */
    public boolean isBuffFrom(MapleBuffStat stat, ISkill skill) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        if (mbsvh == null) {
            return false;
        }
        return mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skill.getId();
    }

    /**
     *
     * @param stat
     * @return
     */
    public int getBuffSource(MapleBuffStat stat) {
        final MapleBuffStatValueHolder mbsvh = effects.get(stat);
        return mbsvh == null ? -1 : mbsvh.effect.getSourceId();
    }

    /**
     *
     * @param itemid
     * @param checkEquipped
     * @return
     */
    public int getItemQuantity(int itemid, boolean checkEquipped) {
        int possesed = inventory[GameConstants.getInventoryType(itemid).ordinal()].countById(itemid);
        if (checkEquipped) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        return possesed;
    }

    /**
     *
     * @param effect
     * @param value
     */
    public void setBuffedValue(MapleBuffStat effect, int value) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        if (mbsvh == null) {
            return;
        }
        mbsvh.value = value;
    }

    /**
     *
     * @param effect
     * @return
     */
    public Long getBuffedStarttime(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.startTime;
    }

    /**
     *
     * @param effect
     * @return
     */
    public MapleStatEffect getStatForBuff(MapleBuffStat effect) {
        final MapleBuffStatValueHolder mbsvh = effects.get(effect);
        return mbsvh == null ? null : mbsvh.effect;
    }

    private void prepareDragonBlood(final MapleStatEffect bloodEffect) {
        if (dragonBloodSchedule != null) {
            dragonBloodSchedule.cancel(false);
        }
        dragonBloodSchedule = BuffTimer.getInstance().register(new Runnable() {

            @Override
            public void run() {
                if (stats.getHp() - bloodEffect.getX() > 1) {
                    cancelBuffStats(MapleBuffStat.DRAGONBLOOD);
                } else {
                    addHP(-bloodEffect.getX());
                    client.getSession().write(MaplePacketCreator.showOwnBuffEffect(bloodEffect.getSourceId(), 5));
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), bloodEffect.getSourceId(), 5), false);
                }
            }
        }, 4_000, 4_000);
    }

    /**
     *
     * @param time
     * @param to
     */
    public void startMapTimeLimitTask(int time, final MapleMap to) {
        client.getSession().write(MaplePacketCreator.getClock(time));

        time *= 1_000;
        mapTimeLimitTask = MapTimer.getInstance().register(new Runnable() {

            @Override
            public void run() {
                changeMap(to, to.getPortal(0));
            }
        }, time, time);
    }

    /**
     *
     * @param VIP
     */
    public void startFishingTask(final boolean VIP) {
        final int time = GameConstants.getFishingTime(VIP, isGM());
        cancelFishingTask();

        fishing = EtcTimer.getInstance().register(new Runnable() { //no real reason for clone.

            @Override
            public void run() {
                final boolean expMulti = haveItem(2_300_001, 1, false, true);
                if (!expMulti && !haveItem(2_300_000, 1, false, true)) {
                    cancelFishingTask();
                    return;
                }
                MapleInventoryManipulator.removeById(client, MapleInventoryType.USE, expMulti ? 2_300_001 : 2_300_000, 1, false, false);

                final int randval = RandomRewards.getInstance().getFishingReward();

                switch (randval) {
                    case 0: // Meso
                        final int money = Randomizer.rand(expMulti ? 15 : 10, expMulti ? 75_000 : 50_000);
                        gainMeso(money, true);
                        client.getSession().write(UIPacket.fishingUpdate((byte) 1, money));
                        break;
                    case 1: // EXP
                        final int experi = Randomizer.nextInt(Math.abs(GameConstants.getExpNeededForLevel(level) / 600) + 1);
                        gainExp(expMulti ? (experi * 3 / 2) : experi, true, false, true);
                        client.getSession().write(UIPacket.fishingUpdate((byte) 2, experi));
                        break;
                    default:
                        MapleInventoryManipulator.addById(client, randval, (short) 1, (byte) 0);
                        client.getSession().write(UIPacket.fishingUpdate((byte) 0, randval));
                        break;
                }
                map.broadcastMessage(UIPacket.fishingCaught(id));
            }
        }, time, time);
    }

    /**
     *
     */
    public void cancelMapTimeLimitTask() {
        if (mapTimeLimitTask != null) {
            mapTimeLimitTask.cancel(false);
        }
    }

    /**
     *
     */
    public void cancelFishingTask() {
        if (fishing != null) {
            fishing.cancel(false);
        }
    }

    /**
     *
     * @param effect
     * @param starttime
     * @param schedule
     */
    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule) {
        registerEffect(effect, starttime, schedule, effect.getStatups());
    }

    /**
     *
     * @param effect
     * @param starttime
     * @param schedule
     * @param statups
     */
    public void registerEffect(MapleStatEffect effect, long starttime, ScheduledFuture<?> schedule, List<Pair<MapleBuffStat, Integer>> statups) {
        if (effect.isHide()) {
            this.hidden = true;
            map.broadcastMessage(this, MaplePacketCreator.removePlayerFromMap(getId()), false);
        } else if (effect.isDragonBlood()) {
            prepareDragonBlood(effect);
        } else if (effect.isBerserk()) {
            checkBerserk();
        } else if (effect.isMonsterRiding_()) {
            getMount().startSchedule();
        } else if (effect.isBeholder()) {
            prepareBeholderEffect();
        } else if (effect.getSourceId() == 1_001 || effect.getSourceId() == 10_001_001 || effect.getSourceId() == 1_001) {
            prepareRecovery();
        }
        int clonez = 0;
        for (Pair<MapleBuffStat, Integer> statup : statups) {
            if (statup.getLeft() == MapleBuffStat.ILLUSION) {
                clonez = statup.getRight();
            }
            int value = statup.getRight();
            if (statup.getLeft() == MapleBuffStat.MONSTER_RIDING && effect.getSourceId() == 5_221_006) {
                if (battleshipHP <= 0) {//quick hack
                    battleshipHP = value; //copy this as well
                }
            }
            effects.put(statup.getLeft(), new MapleBuffStatValueHolder(effect, starttime, schedule, value));
        }
        if (clonez > 0) {
            int cloneSize = Math.max(getNumClones(), getCloneSize());
            if (clonez > cloneSize) { //how many clones to summon
                for (int i = 0; i < clonez - cloneSize; i++) { //1-1=0
                    cloneLook();
                }
            }
        }
        stats.recalcLocalStats();
        //System.out.println("Effect registered. Effect: " + effect.getSourceId());
    }

    /**
     *
     * @param effect
     * @param startTime
     * @return
     */
    public List<MapleBuffStat> getBuffStats(final MapleStatEffect effect, final long startTime) {
        final List<MapleBuffStat> bstats = new ArrayList<>();
        final Map<MapleBuffStat, MapleBuffStatValueHolder> allBuffs = new EnumMap<>(effects);
        for (Entry<MapleBuffStat, MapleBuffStatValueHolder> stateffect : allBuffs.entrySet()) {
            final MapleBuffStatValueHolder mbsvh = stateffect.getValue();
            if (mbsvh.effect.sameSource(effect) && (startTime == -1 || startTime == mbsvh.startTime)) {
                bstats.add(stateffect.getKey());
            }
        }
        return bstats;
    }

    private boolean deregisterBuffStats(List<MapleBuffStat> stats) {
        boolean clonez = false;
        List<MapleBuffStatValueHolder> effectsToCancel = new ArrayList<>(stats.size());
        for (MapleBuffStat stat : stats) {
            final MapleBuffStatValueHolder mbsvh = effects.remove(stat);
            if (mbsvh != null) {
                boolean addMbsvh = true;
                for (MapleBuffStatValueHolder contained : effectsToCancel) {
                    if (mbsvh.startTime == contained.startTime && contained.effect == mbsvh.effect) {
                        addMbsvh = false;
                    }
                }
                if (addMbsvh) {
                    effectsToCancel.add(mbsvh);
                }
                if (stat == MapleBuffStat.SUMMON || stat == MapleBuffStat.PUPPET || stat == MapleBuffStat.REAPER) {
                    final int summonId = mbsvh.effect.getSourceId();
                    final MapleSummon summon = summons.get(summonId);
                    if (summon != null) {
                        map.broadcastMessage(MaplePacketCreator.removeSummon(summon, true));
                        map.removeMapObject(summon);
                        removeVisibleMapObject(summon);
                        summons.remove(summonId);
                        if (summon.getSkill() == 1_321_007) {
                            if (beholderHealingSchedule != null) {
                                beholderHealingSchedule.cancel(false);
                                beholderHealingSchedule = null;
                            }
                            if (beholderBuffSchedule != null) {
                                beholderBuffSchedule.cancel(false);
                                beholderBuffSchedule = null;
                            }
                        }
                    }
                } else if (stat == MapleBuffStat.DRAGONBLOOD) {
                    if (dragonBloodSchedule != null) {
                        dragonBloodSchedule.cancel(false);
                        dragonBloodSchedule = null;
                    }
                } else if (stat == MapleBuffStat.ILLUSION) {
                    disposeClones();
                    clonez = true;
                }
            }
        }
        for (MapleBuffStatValueHolder cancelEffectCancelTasks : effectsToCancel) {
            if (getBuffStats(cancelEffectCancelTasks.effect, cancelEffectCancelTasks.startTime).isEmpty()) {
                if (cancelEffectCancelTasks.schedule != null) {
                    cancelEffectCancelTasks.schedule.cancel(false);
                }
            }
        }
        return clonez;
    }

    /**
     * @param effect
     * @param overwrite when overwrite is set no data is sent and all the
     * Buffstats in the StatEffect are deregistered
     * @param startTime
     */
    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime) {
        cancelEffect(effect, overwrite, startTime, effect.getStatups());
    }

    /**
     *
     * @param effect
     * @param overwrite
     * @param startTime
     * @param statups
     */
    public void cancelEffect(final MapleStatEffect effect, final boolean overwrite, final long startTime, List<Pair<MapleBuffStat, Integer>> statups) {
        List<MapleBuffStat> buffstats;
        if (!overwrite) {
            buffstats = getBuffStats(effect, startTime);
        } else {
            buffstats = new ArrayList<>(statups.size());
            for (Pair<MapleBuffStat, Integer> statup : statups) {
                buffstats.add(statup.getLeft());
            }
        }
        if (buffstats.size() <= 0) {
            return;
        }
        final boolean clonez = deregisterBuffStats(buffstats);
        if (effect.isMagicDoor()) {
            // remove for all on maps
            if (!getDoors().isEmpty()) {
                MapleDoor door = getDoors().iterator().next();
                for (MapleCharacter chr : door.getTarget().getCharacters()) {
                    door.sendDestroyData(chr.client);
                }
                for (MapleCharacter chr : door.getTown().getCharacters()) {
                    door.sendDestroyData(chr.client);
                }
                for (MapleDoor destroyDoor : getDoors()) {
                    door.getTarget().removeMapObject(destroyDoor);
                    door.getTown().removeMapObject(destroyDoor);
                }
                removeDoor();
                silentPartyUpdate();
            }
        } else if (effect.isMonsterRiding_() || getMountId() == effect.getSourceId()) {
            getMount().cancelSchedule();
        } else if (effect.isMonsterRiding()) {
            cancelEffectFromBuffStat(MapleBuffStat.MECH_CHANGE);
        } else if (effect.isAranCombo()) {
            combo = 0;
        }
        // check if we are still logged in o.o
        if (!overwrite) {
            cancelPlayerBuffs(buffstats);
            if (effect.isHide() && client.getChannelServer().getPlayerStorage().getCharacterById(this.getId()) != null) { //Wow this is so fking hacky...
                this.hidden = false;
                map.broadcastMessage(this, MaplePacketCreator.spawnPlayerMapobject(this), false);

                for (final MaplePet pet : pets) {
                    if (pet.getSummoned()) {
                        map.broadcastMessage(this, PetPacket.showPet(this, pet, false, false), false);
                    }
                }
                for (final WeakReference<MapleCharacter> chr : clones) {
                    if (chr.get() != null) {
                        map.broadcastMessage(chr.get(), MaplePacketCreator.spawnPlayerMapobject(chr.get()), false);
                    }
                }
            }
        }
        if (!clonez) {
            for (WeakReference<MapleCharacter> chr : clones) {
                if (chr.get() != null) {
                    chr.get().cancelEffect(effect, overwrite, startTime);
                }
            }
        }
        //System.out.println("Effect deregistered. Effect: " + effect.getSourceId());
    }

    /**
     *
     * @param stat
     */
    public void cancelBuffStats(MapleBuffStat... stat) {
        List<MapleBuffStat> buffStatList = Arrays.asList(stat);
        deregisterBuffStats(buffStatList);
        cancelPlayerBuffs(buffStatList);
    }

    /**
     *
     * @param stat
     */
    public void cancelEffectFromBuffStat(MapleBuffStat stat) {
        if (effects.get(stat) != null) {
            cancelEffect(effects.get(stat).effect, false, -1);
        }
    }

    private void cancelPlayerBuffs(List<MapleBuffStat> buffstats) {
        boolean write = client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null;
        if (buffstats.contains(MapleBuffStat.HOMING_BEACON)) {
            if (write) {
                client.getSession().write(MaplePacketCreator.cancelHoming());
            }
        } else if (buffstats.contains(MapleBuffStat.MONSTER_RIDING)) {
            if (write) {
                stats.recalcLocalStats();
            }
            client.getSession().write(MaplePacketCreator.cancelBuffMONSTER(buffstats));
            map.broadcastMessage(this, MaplePacketCreator.cancelForeignBuffMONSTER(getId(), buffstats), false);
        } else {
            if (write) {
                stats.recalcLocalStats();
            }
            client.getSession().write(MaplePacketCreator.cancelBuff(buffstats));
            map.broadcastMessage(this, MaplePacketCreator.cancelForeignBuff(getId(), buffstats), false);
        }
    }

    /**
     *
     */
    public void dispel() {
        if (!isHidden()) {
            final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
            for (MapleBuffStatValueHolder mbsvh : allBuffs) {
                if (mbsvh.effect.isSkill() && mbsvh.schedule != null && !mbsvh.effect.isMorph()) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                }
            }
        }
    }

    /**
     *
     * @param skillid
     */
    public void dispelSkill(int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (skillid == 0) {
                if (mbsvh.effect.isSkill() && (mbsvh.effect.getSourceId() == 4_331_003 || mbsvh.effect.getSourceId() == 4_331_002 || mbsvh.effect.getSourceId() == 4_341_002 || mbsvh.effect.getSourceId() == 22_131_001 || mbsvh.effect.getSourceId() == 1_321_007 || mbsvh.effect.getSourceId() == 2_121_005 || mbsvh.effect.getSourceId() == 2_221_005 || mbsvh.effect.getSourceId() == 2_311_006 || mbsvh.effect.getSourceId() == 2_321_003 || mbsvh.effect.getSourceId() == 3_111_002 || mbsvh.effect.getSourceId() == 3_111_005 || mbsvh.effect.getSourceId() == 3_211_002 || mbsvh.effect.getSourceId() == 3_211_005 || mbsvh.effect.getSourceId() == 4_111_002)) {
                    cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    break;
                }
            } else if (mbsvh.effect.isSkill() && mbsvh.effect.getSourceId() == skillid) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    /**
     *
     * @param skillid
     */
    public void dispelBuff(int skillid) {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.getSourceId() == skillid) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    /**
     *
     */
    public void cancelAllBuffs_() {
        effects.clear();
    }

    /**
     *
     */
    public void cancelAllBuffs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            cancelEffect(mbsvh.effect, false, mbsvh.startTime);
        }
    }

    /**
     *
     */
    public void cancelMorphs() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            switch (mbsvh.effect.getSourceId()) {
                case 5_111_005:
                case 5_121_003:
                case 15_111_002:
                case 13_111_005:
                    return; // Since we can't have more than 1, save up on loops
                default:
                    if (mbsvh.effect.isMorph()) {
                        cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                    }
            }
        }
    }

    /**
     *
     * @return
     */
    public int getMorphState() {
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMorph()) {
                return mbsvh.effect.getSourceId();
            }
        }
        return -1;
    }

    /**
     *
     * @param buffs
     */
    public void silentGiveBuffs(List<PlayerBuffValueHolder> buffs) {
        if (buffs == null) {
            return;
        }
        for (PlayerBuffValueHolder mbsvh : buffs) {
            mbsvh.effect.silentApplyBuff(this, mbsvh.startTime);
        }
    }

    /**
     *
     * @return
     */
    public List<PlayerBuffValueHolder> getAllBuffs() {
        List<PlayerBuffValueHolder> ret = new ArrayList<>();
        LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());
        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            ret.add(new PlayerBuffValueHolder(mbsvh.startTime, mbsvh.effect));
        }
        return ret;
    }

    /**
     *
     */
    public void cancelMagicDoor() {
        final LinkedList<MapleBuffStatValueHolder> allBuffs = new LinkedList<>(effects.values());

        for (MapleBuffStatValueHolder mbsvh : allBuffs) {
            if (mbsvh.effect.isMagicDoor()) {
                cancelEffect(mbsvh.effect, false, mbsvh.startTime);
                break;
            }
        }
    }

    /**
     *
     * @param skillid
     * @return
     */
    public int getSkillLevel(int skillid) {
        return getSkillLevel(SkillFactory.getSkill(skillid));
    }

    /**
     *
     * @param skillid
     * @param targets
     */
    public final void handleEnergyCharge(final int skillid, final int targets) {
        final ISkill echskill = SkillFactory.getSkill(skillid);
        final byte skilllevel = getSkillLevel(echskill);
        if (skilllevel > 0) {
            final MapleStatEffect echeff = echskill.getEffect(skilllevel);
            //  System.out.println("获取能量B："+ skilllevel);
            if (targets > 0) {
                //     System.out.println("获取能量C："+ targets);
//                if (nengl <= 10) {
//                    nengl = nengl + 1;
//                } else {
                if (getBuffedValue(MapleBuffStat.ENERGY_CHARGE) == null) {
                    echeff.applyEnergyBuff(this, true); // Infinity time
                    //    System.out.println("获取能量D：");
                } else {
                    Integer energyLevel = getBuffedValue(MapleBuffStat.ENERGY_CHARGE);
                    //TODO: bar going down
                    if (energyLevel <= 15_000 /*
                     * && nengls <= 20
                             */) {
                        energyLevel += (echeff.getX() * targets);

                        client.getSession().write(MaplePacketCreator.showOwnBuffEffect(skillid, 2));
                        map.broadcastMessage(this, MaplePacketCreator.showBuffeffect(id, skillid, 2), false);

                        if (energyLevel >= 15_000) {
                            energyLevel = 15_000;
                        }/*
                         * if (nengls <= 20) { nengls = nengls + 1; }
                         */

                        //System.out.println("获取能量E："+ energyLevel);

                        List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, energyLevel));
                        client.getSession().write(MaplePacketCreator.能量条(stat, energyLevel / 1_000)); //????????????????
                        //client.getSession().write(MaplePacketCreator.givePirateBuff(energyLevel, 0, stat));
                        // client.getSession().write(MaplePacketCreator.giveEnergyChargeTest(energyLevel, echeff.getDuration() / 1000));
                        setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyLevel);
                        Timer.BuffTimer.getInstance().schedule(new Runnable() {

                            @Override
                            public void run() {
                                Integer energyLevel = 0;
                                setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyLevel);
                                List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, energyLevel));
                                client.getSession().write(MaplePacketCreator.能量条(stat, 0)); //????????????????
                            }
                        }, 3 * 60 * 1_000);
                        /*
                         * Timer.WorldTimer.getInstance().register(new
                         * Runnable() {
                         *
                         * public void run() { Integer energyLevel =
                         * getBuffedValue(MapleBuffStat.ENERGY_CHARGE); try {
                         * energyLevel = 1; System.out.println("获取能量Z：" +
                         * energyLevel); List<Pair<MapleBuffStat, Integer>> stat
                         * = Collections.singletonList(new Pair<MapleBuffStat,
                         * Integer>(MapleBuffStat.ENERGY_CHARGE, energyLevel));
                         * client.getSession().write(MaplePacketCreator.能量条(stat,
                         * energyLevel / 1000)); //????????????????
                         * setBuffedValue(MapleBuffStat.ENERGY_CHARGE,
                         * Integer.valueOf(energyLevel)); } catch (Exception e)
                         * { } } }, 10000);
                         */
                    }

                    /*
                     * else if (nengls > 20) { nengls = 0; nengl = 0; //
                     * System.out.println("获取能量F："+ energyLevel);
                     * List<Pair<MapleBuffStat, Integer>> stat =
                     * Collections.singletonList(new Pair<MapleBuffStat,
                     * Integer>(MapleBuffStat.ENERGY_CHARGE, 0));
                     * client.getSession().write(MaplePacketCreator.能量条(stat,
                     * 0)); //????????????????
                     * //client.getSession().write(MaplePacketCreator.givePirateBuff(energyLevel,
                     * 0, stat)); //
                     * client.getSession().write(MaplePacketCreator.giveEnergyChargeTest(energyLevel,
                     * echeff.getDuration() / 1000));
                     * setBuffedValue(MapleBuffStat.ENERGY_CHARGE,
                     * Integer.valueOf(0)); // echeff.applyEnergyBuff(this,
                     * false); // One with time //
                     * setBuffedValue(MapleBuffStat.ENERGY_CHARGE,
                     * Integer.valueOf(10001)); }
                     */
                }
                // System.out.println("能量S："+ nengls);
                // System.out.println("能量："+ nengl);
                /*
                 * Timer.WorldTimer.getInstance().register(new Runnable() {
                 * @Override public void run() { energyPoint -= 200; if
                 * (energyPoint <= 0) { echeff.applyEnergyBuff(chrs, false); //
                 * One with time setBuffedValue(MapleBuffStat.ENERGY_CHARGE,
                 * Integer.valueOf(10001)); } try { } catch (Exception e) { } }
                 * }, 60000 * 1);
                 */
                //   }
            }
        }
    }

    /**
     *
     * @param damage
     */
    public final void handleBattleshipHP(int damage) {
        if (isActiveBuffedValue(5_221_006)) {
            battleshipHP -= damage;
            if (battleshipHP <= 0) {
                battleshipHP = 0;
                final MapleStatEffect effect = getStatForBuff(MapleBuffStat.MONSTER_RIDING);
                client.getSession().write(MaplePacketCreator.skillCooldown(5_221_006, effect.getCooldown()));
                addCooldown(5_221_006, System.currentTimeMillis(), effect.getCooldown() * 1_000);
                dispelSkill(5_221_006);
            }
        }
    }

    /**
     *
     */
    public final void handleOrbgain() {
        int orbcount = getBuffedValue(MapleBuffStat.COMBO);
        ISkill combo;
        ISkill advcombo;

        switch (getJob()) {
            case 1_110:
            case 1_111:
            case 1_112:
                combo = SkillFactory.getSkill(11_111_001);
                advcombo = SkillFactory.getSkill(11_110_005);
                break;
            default:
                combo = SkillFactory.getSkill(1_111_002);
                advcombo = SkillFactory.getSkill(1_120_003);
                break;
        }

        MapleStatEffect ceffect = null;
        int advComboSkillLevel = getSkillLevel(advcombo);
        if (advComboSkillLevel > 0) {
            ceffect = advcombo.getEffect(advComboSkillLevel);
        } else if (getSkillLevel(combo) > 0) {
            ceffect = combo.getEffect(getSkillLevel(combo));
        } else {
            return;
        }

        if (orbcount < ceffect.getX() + 1) {
            int neworbcount = orbcount + 1;
            if (advComboSkillLevel > 0 && ceffect.makeChanceResult()) {
                if (neworbcount < ceffect.getX() + 1) {
                    neworbcount++;
                }
            }
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, neworbcount));
            setBuffedValue(MapleBuffStat.COMBO, neworbcount);
            int duration = ceffect.getDuration();
            duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));

            client.getSession().write(MaplePacketCreator.giveBuff(combo.getId(), duration, stat, ceffect));
            map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(this, getId(), stat, ceffect), false);
        }
    }

    /**
     *
     */
    public void handleOrbconsume() {
        ISkill combo;

        switch (getJob()) {
            case 1_110:
            case 1_111:
                combo = SkillFactory.getSkill(11_111_001);
                break;
            default:
                combo = SkillFactory.getSkill(1_111_002);
                break;
        }
        if (getSkillLevel(combo) <= 0) {
            return;
        }
        MapleStatEffect ceffect = getStatForBuff(MapleBuffStat.COMBO);
        if (ceffect == null) {
            return;
        }
        List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, 1));
        setBuffedValue(MapleBuffStat.COMBO, 1);
        int duration = ceffect.getDuration();
        duration += (int) ((getBuffedStarttime(MapleBuffStat.COMBO) - System.currentTimeMillis()));

        client.getSession().write(MaplePacketCreator.giveBuff(combo.getId(), duration, stat, ceffect));
        map.broadcastMessage(this, MaplePacketCreator.giveForeignBuff(this, getId(), stat, ceffect), false);
    }

    /**
     *
     */
    public void silentEnforceMaxHpMp() {
        stats.setMp(stats.getMp());
        stats.setHp(stats.getHp(), true);
    }

    /**
     *
     */
    public void enforceMaxHpMp() {
        List<Pair<MapleStat, Integer>> statups = new ArrayList<>(2);
        if (stats.getMp() > stats.getCurrentMaxMp()) {
            stats.setMp(stats.getMp());
            statups.add(new Pair<>(MapleStat.MP, Integer.valueOf(stats.getMp())));
        }
        if (stats.getHp() > stats.getCurrentMaxHp()) {
            stats.setHp(stats.getHp());
            statups.add(new Pair<>(MapleStat.HP, Integer.valueOf(stats.getHp())));
        }
        if (statups.size() > 0) {
            client.getSession().write(MaplePacketCreator.updatePlayerStats(statups, getJob()));
        }
    }

    /**
     *
     * @return
     */
    public MapleMap getMap() {
        return map;
    }

    /**
     *
     * @return
     */
    public MonsterBook getMonsterBook() {
        return monsterbook;
    }

    /**
     *
     * @param newmap
     */
    public void setMap(MapleMap newmap) {
        this.map = newmap;
    }

    /**
     *
     * @param PmapId
     */
    public void setMap(int PmapId) {
        this.mapid = PmapId;
    }

    /**
     *
     * @return
     */
    public int getMapId() {
        if (map != null) {
            return map.getId();
        }
        return mapid;
    }

    /**
     *
     * @return
     */
    public byte getInitialSpawnpoint() {
        return initialSpawnPoint;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public final String getBlessOfFairyOrigin() {
        return this.BlessOfFairy_Origin;
    }

    /**
     *
     * @return
     */
    public final short getLevel() {
        return level;
    }

    /**
     *
     * @return
     */
    public final short getFame() {
        return fame;
    }

    /**
     *
     * @return
     */
    public final int getDojo() {
        return dojo;
    }

    /**
     *
     * @return
     */
    public final int getDojoRecord() {
        return dojoRecord;
    }

    /**
     *
     * @return
     */
    public final int getFallCounter() {
        return fallcounter;
    }

    /**
     *
     * @return
     */
    public final MapleClient getClient() {
        return client;
    }

    /**
     *
     * @param client
     */
    public final void setClient(final MapleClient client) {
        this.client = client;
    }

    /**
     *
     * @return
     */
    public int getExp() {
        return exp;
    }

    /**
     *
     * @return
     */
    public short getRemainingAp() {
        return remainingAp;
    }

    /**
     *
     * @return
     */
    public int getRemainingSp() {
        return remainingSp[GameConstants.getSkillBook(job)]; //default
    }

    /**
     *
     * @param skillbook
     * @return
     */
    public int getRemainingSp(final int skillbook) {
        return remainingSp[skillbook];
    }

    /**
     *
     * @return
     */
    public int[] getRemainingSps() {
        return remainingSp;
    }

    /**
     *
     * @return
     */
    public int getRemainingSpSize() {
        int ret = 0;
        for (int i = 0; i < remainingSp.length; i++) {
            if (remainingSp[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public short getHpApUsed() {
        return hpApUsed;
    }

    /**
     *
     * @return
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     *
     * @param hpApUsed
     */
    public void setHpApUsed(short hpApUsed) {
        this.hpApUsed = hpApUsed;
    }

    /**
     *
     * @return
     */
    public byte getSkinColor() {
        return skinColor;
    }

    /**
     *
     * @param skinColor
     */
    public void setSkinColor(byte skinColor) {
        this.skinColor = skinColor;
    }

    /**
     *
     * @return
     */
    public short getJob() {
        return job;
    }

    /**
     *
     * @return
     */
    public byte getGender() {
        return gender;
    }

    /**
     *
     * @return
     */
    public int getHair() {
        return hair;
    }

    /**
     *
     * @return
     */
    public int getFace() {
        return face;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param exp
     */
    public void setExp(int exp) {
        this.exp = exp;
    }

    /**
     *
     * @param hair
     */
    public void setHair(int hair) {
        this.hair = hair;
    }

    /**
     *
     * @param face
     */
    public void setFace(int face) {
        this.face = face;
    }

    /**
     *
     * @param fame
     */
    public void setFame(short fame) {
        this.fame = fame;
    }

    /**
     *
     * @param dojo
     */
    public void setDojo(final int dojo) {
        this.dojo = dojo;
    }

    /**
     *
     * @param reset
     */
    public void setDojoRecord(final boolean reset) {
        if (reset) {
            dojo = 0;
            dojoRecord = 0;
        } else {
            dojoRecord++;
        }
    }

    /**
     *
     * @param fallcounter
     */
    public void setFallCounter(int fallcounter) {
        this.fallcounter = fallcounter;
    }

    /**
     *
     * @return
     */
    public Point getOldPosition() {
        return old;
    }

    /**
     *
     * @param x
     */
    public void setOldPosition(Point x) {
        this.old = x;
    }

    /**
     *
     * @param remainingAp
     */
    public void setRemainingAp(short remainingAp) {
        this.remainingAp = remainingAp;
    }

    /**
     *
     * @param remainingSp
     */
    public void setRemainingSp(int remainingSp) {
        this.remainingSp[GameConstants.getSkillBook(job)] = remainingSp; //default
    }

    /**
     *
     * @param remainingSp
     * @param skillbook
     */
    public void setRemainingSp(int remainingSp, final int skillbook) {
        this.remainingSp[skillbook] = remainingSp;
    }

    /**
     *
     * @param gender
     */
    public void setGender(byte gender) {
        this.gender = gender;
    }

    /**
     *
     * @param invinc
     */
    public void setInvincible(boolean invinc) {
        invincible = invinc;
    }

    /**
     *
     * @return
     */
    public boolean isInvincible() {
        return invincible;
    }

    /**
     *
     * @return
     */
    public CheatTracker getCheatTracker() {
        return anticheat;
    }

    /**
     *
     * @return
     */
    public BuddyList getBuddylist() {
        return buddylist;
    }

    /**
     *
     * @param famechange
     */
    public void addFame(int famechange) {
        this.fame += famechange;
        /*
         * if (this.fame >= 50) { finishAchievement(7); }
         */
    }

    /**
     *
     * @param mapid
     * @param portal
     * @param msg
     */
    public void changeMapBanish(final int mapid, final String portal, final String msg) {
        dropMessage(5, msg);
        final MapleMap map = client.getChannelServer().getMapFactory().getMap(mapid);
        if (map != null) {
            changeMap(map, map.getPortal(portal));
        }
    }

    /**
     *
     * @param to
     */
    public void changeMap(final int to) {
        MapleMap map = ChannelServer.getInstance(getClient().getChannel()).getMapFactory().getMap(to);
        changeMapInternal(map, map.getPortal(0).getPosition(), MaplePacketCreator.getWarpToMap(map, 0, this), map.getPortal(0));
    }

    /**
     *
     * @param map
     * @param portal
     */
    public void changeMap(int map, int portal) {
        MapleMap warpMap = client.getChannelServer().getMapFactory().getMap(map);
        changeMap(warpMap, warpMap.getPortal(portal));
    }

    /**
     *
     * @param to
     * @param pos
     */
    public void changeMap(final MapleMap to, final Point pos) {
        changeMapInternal(to, pos, MaplePacketCreator.getWarpToMap(to, 128, this), null);
    }

    /**
     *
     * @param to
     * @param pto
     */
    public void changeMap(final MapleMap to, final MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), MaplePacketCreator.getWarpToMap(to, pto.getId(), this), null);
    }

    /**
     *
     * @param to
     * @param pto
     */
    public void changeMapPortal(final MapleMap to, final MaplePortal pto) {
        changeMapInternal(to, pto.getPosition(), MaplePacketCreator.getWarpToMap(to, pto.getId(), this), pto);
    }

    private void changeMapInternal(final MapleMap to, final Point pos, MaplePacket warpPacket, final MaplePortal pto) {
        if (to == null) {
            return;
        }
        saveToDB(false, false);
        final int nowmapid = map.getId();
        if (eventInstance != null) {
            eventInstance.changedMap(this, to.getId());
        }
        final boolean pyramid = pyramidSubway != null;
        if (map.getId() == nowmapid) {
            client.getSession().write(warpPacket);

            map.removePlayer(this);
            if (!isClone() && client.getChannelServer().getPlayerStorage().getCharacterById(getId()) != null) {
                map = to;
                setPosition(pos);
                to.addPlayer(this);
                stats.relocHeal();
            }
        }
        if (party != null) {
            silentPartyUpdate();
            getClient().getSession().write(MaplePacketCreator.updateParty(getClient().getChannel(), party, PartyOperation.SILENT_UPDATE, null));
            updatePartyMemberHP();
        }
        if (pyramid && pyramidSubway != null) { //checks if they had pyramid before AND after changing
            pyramidSubway.onChangeMap(this, to.getId());
        }

    }

    /**
     *
     */
    public void leaveMap() {
        //controlled.clear();
        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.clear();
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
        if (chair != 0) {
            cancelFishingTask();
            chair = 0;
        }
        cancelMapTimeLimitTask();
    }

    /**
     *
     * @param newJob
     */
    public void changeJob(int newJob) {
        try {
            final boolean isEv = GameConstants.isEvan(job) || GameConstants.isResist(job);
            this.job = (short) newJob;
            if (newJob != 0 && newJob != 1_000 && newJob != 2_000 && newJob != 2_001 && newJob != 3_000) {
                if (isEv) {
                    remainingSp[GameConstants.getSkillBook(newJob)] += 5;
                    client.getSession().write(UIPacket.getSPMsg((byte) 5, (short) newJob));
                } else {
                    remainingSp[GameConstants.getSkillBook(newJob)]++;
                    if (newJob % 10 >= 2) {
                        remainingSp[GameConstants.getSkillBook(newJob)] += 2;
                    }
                }
            }
            if (newJob > 0 && !isGM()) {
                resetStatsByJob(true);
                if (!GameConstants.isEvan(newJob)) {
                    if (getLevel() > (newJob == 200 ? 8 : 10) && newJob % 100 == 0 && (newJob % 1_000) / 100 > 0) { //first job
                        remainingSp[GameConstants.getSkillBook(newJob)] += 3 * (getLevel() - (newJob == 200 ? 8 : 10));
                    }
                } else if (newJob == 2_200) {
                    MapleQuest.getInstance(22_100).forceStart(this, 0, null);
                    MapleQuest.getInstance(22_100).forceComplete(this, 0);
                    expandInventory((byte) 1, 4);
                    expandInventory((byte) 2, 4);
                    expandInventory((byte) 3, 4);
                    expandInventory((byte) 4, 4);
                    client.getSession().write(MaplePacketCreator.getEvanTutorial("UI/tutorial/evan/14/0"));
                    dropMessage(5, "The baby Dragon hatched and appears to have something to tell you. Click the baby Dragon to start a conversation.");
                }
            }
            client.getSession().write(MaplePacketCreator.updateSp(this, false, isEv));
            updateSingleStat(MapleStat.JOB, newJob);

            int maxhp = stats.getMaxHp(), maxmp = stats.getMaxMp();

            switch (job) {
                case 100: // Warrior
                case 1_100: // Soul Master
                case 2_100: // Aran
                case 3_200:
                    maxhp += Randomizer.rand(200, 250);
                    break;
                case 200: // Magician
                case 2_200: //evan
                case 2_210: //evan
                    maxmp += Randomizer.rand(100, 150);
                    break;
                case 300: // Bowman
                case 400: // Thief
                case 500: // Pirate
                case 3_300:
                case 3_500:
                    maxhp += Randomizer.rand(100, 150);
                    maxmp += Randomizer.rand(25, 50);
                    break;
                case 110: // Fighter
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 120: // Page
                case 130: // Spearman
                case 510: // 打手
                case 512: // 拳霸
                case 1_110: // Soul Master
                case 2_110: // Aran
                case 3_210:
                    maxhp += Randomizer.rand(300, 350);
                    break;
                case 210: // FP
                case 220: // IL
                case 230: // Cleric
                    maxmp += Randomizer.rand(400, 450);
                    break;
                case 310: // Bowman
                case 312: // Bowman
                case 320: // Crossbowman
                case 322: // Bowman
                case 410: // Assasin
                case 412: // Assasin
                case 420: // Bandit
                case 422: // Assasin
                case 430: // Semi Dualer
                case 520: // 槍手
                case 522: // 槍神
                case 1_310: // Wind Breaker
                case 1_410: // Night Walker
                case 3_310:
                case 3_510:
                    maxhp += Randomizer.rand(300, 350);
                    maxhp += Randomizer.rand(150, 200);
                    break;
                case 900: // GM
                case 800: // Manager
                    maxhp += 30_000;
                    maxhp += 30_000;
                    break;
            }
            if (maxhp >= 30_000) {
                maxhp = 30_000;
            }
            if (maxmp >= 30_000) {
                maxmp = 30_000;
            }

            stats.setMaxHp((short) maxhp);
            stats.setMaxMp((short) maxmp);
            stats.setHp((short) maxhp);
            stats.setMp((short) maxmp);
            List<Pair<MapleStat, Integer>> statup = new ArrayList<>(4);
            statup.add(new Pair<>(MapleStat.MAXHP, Integer.valueOf(maxhp)));
            statup.add(new Pair<>(MapleStat.MAXMP, Integer.valueOf(maxmp)));
            statup.add(new Pair<>(MapleStat.HP, Integer.valueOf(maxhp)));
            statup.add(new Pair<>(MapleStat.MP, Integer.valueOf(maxmp)));
            stats.recalcLocalStats();
            client.getSession().write(MaplePacketCreator.updatePlayerStats(statup, getJob()));
            map.broadcastMessage(this, MaplePacketCreator.showForeignEffect(getId(), 8), false);
            silentPartyUpdate();
            guildUpdate();
            familyUpdate();
            if (dragon != null) {
                map.broadcastMessage(MaplePacketCreator.removeDragon(this.id));
                map.removeMapObject(dragon);
                dragon = null;
            }
            baseSkills();
            if (newJob >= 2_200 && newJob <= 2_218) { //make new
                if (getBuffedValue(MapleBuffStat.MONSTER_RIDING) != null) {
                    cancelBuffStats(MapleBuffStat.MONSTER_RIDING);
                }
                makeDragon();
                map.spawnDragon(dragon);
                map.updateMapObjectVisibility(this, dragon);
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e); //all jobs throw errors :(
        }
    }

    /**
     *
     */
    public void baseSkills() {
        if (GameConstants.getJobNumber(job) >= 3) { //third job.
            List<Integer> skills = SkillFactory.getSkillsByJob(job);
            if (skills != null) {
                for (int i : skills) {
                    final ISkill skil = SkillFactory.getSkill(i);
                    if (skil != null && !skil.isInvisible() && skil.isFourthJob() && getSkillLevel(skil) <= 0 && getMasterLevel(skil) <= 0 && skil.getMasterLevel() > 0) {
                        changeSkillLevel(skil, (byte) 0, (byte) skil.getMasterLevel()); //usually 10 master
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void makeDragon() {
        dragon = new MapleDragon(this);
    }

    /**
     *
     * @return
     */
    public MapleDragon getDragon() {
        return dragon;
    }

    /**
     *
     * @param ap
     */
    public void gainAp(short ap) {
        this.remainingAp += ap;
        updateSingleStat(MapleStat.AVAILABLEAP, this.remainingAp);
    }

    /**
     *
     * @param sp
     */
    public void gainSP(int sp) {
        this.remainingSp[GameConstants.getSkillBook(job)] += sp; //default
        client.getSession().write(MaplePacketCreator.updateSp(this, false));
        client.getSession().write(UIPacket.getSPMsg((byte) sp, (short) job));
    }

    /**
     *
     * @param sp
     * @param skillbook
     */
    public void gainSP(int sp, final int skillbook) {
        this.remainingSp[skillbook] += sp; //default
        client.getSession().write(MaplePacketCreator.updateSp(this, false));
        client.getSession().write(UIPacket.getSPMsg((byte) sp, (short) job));
    }

    /**
     *
     * @param sp
     */
    public void resetSP(int sp) {
        for (int i = 0; i < this.remainingSp.length; i++) {
            this.remainingSp[i] = sp;
        }
        updateSingleStat(MapleStat.AVAILABLESP, getRemainingSp());
        //   this.client.getSession().write(MaplePacketCreator.updateSp(this, false));
    }

    /**
     *
     */
    public void resetAPSP() {
        for (int i = 0; i < remainingSp.length; i++) {
            this.remainingSp[i] = 0;
        }
        client.getSession().write(MaplePacketCreator.updateSp(this, false));
        gainAp((short) -this.remainingAp);
    }

    /**
     *
     * @return
     */
    public int getAllSkillLevels() {
        int rett = 0;
        for (Map.Entry ret : this.skills.entrySet()) {
            if ((!((Skill) ret.getKey()).isBeginnerSkill()) && (((SkillEntry) ret.getValue()).skillevel > 0)) {
                rett += ((SkillEntry) ret.getValue()).skillevel;
            }
        }
        return rett;
    }

    /**
     *
     * @param skill
     * @param newLevel
     * @param newMasterlevel
     */
    public void changeSkillLevel(final ISkill skill, byte newLevel, byte newMasterlevel) { //1 month
        if (skill == null) {
            return;
        }
        changeSkillLevel(skill, newLevel, newMasterlevel, skill.isTimeLimited() ? (System.currentTimeMillis() + (long) (30L * 24L * 60L * 60L * 1_000L)) : -1);
    }

    /**
     *
     * @param skill
     * @param newLevel
     * @param newMasterlevel
     * @param expiration
     */
    public void changeSkillLevel(final ISkill skill, byte newLevel, byte newMasterlevel, long expiration) {
        if (skill == null || (!GameConstants.isApplicableSkill(skill.getId()) && !GameConstants.isApplicableSkill_(skill.getId()))) {
            return;
        }
        client.getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, expiration));
        if (newLevel == 0 && newMasterlevel == 0) {
            if (skills.containsKey(skill)) {
                skills.remove(skill);
            } else {
                return; //nothing happen
            }
        } else {
            skills.put(skill, new SkillEntry(newLevel, newMasterlevel, expiration));
        }
        if (GameConstants.isRecoveryIncSkill(skill.getId())) {
            stats.relocHeal();
        } else if (GameConstants.isElementAmp_Skill(skill.getId())) {
            stats.recalcLocalStats();
        }

    }

    /**
     *
     * @param skill
     * @param newLevel
     * @param newMasterlevel
     */
    public void changeSkillLevel_Skip(final ISkill skill, byte newLevel, byte newMasterlevel) {
        if (skill == null) {
            return;
        }
        client.getSession().write(MaplePacketCreator.updateSkill(skill.getId(), newLevel, newMasterlevel, -1L));
        if (newLevel == 0 && newMasterlevel == 0) {
            if (skills.containsKey(skill)) {
                skills.remove(skill);
            } else {
                //nothing happen

            }
        } else {
            skills.put(skill, new SkillEntry(newLevel, newMasterlevel, -1L));
        }

    }

    /**
     *
     */
    public void playerDead() {
        final MapleStatEffect statss = getStatForBuff(MapleBuffStat.SOUL_STONE);
        if (statss != null) {
            dropMessage(5, "You have been revived by Soul Stone.");
            getStat().setHp(((getStat().getMaxHp() / 100) * statss.getX()));
            setStance(0);
            changeMap(getMap(), getMap().getPortal(0));
            return;
        }
        int[] charmID = {5_130_000, 5_130_002, 5_131_000, 4_031_283, 4_140_903};

        int possesed = 0;
        int i;

        //Check for charms
        for (i = 0; i < charmID.length; i++) {
            int quantity = getItemQuantity(charmID[i], false);
            if (possesed == 0 && quantity > 0) {
                possesed = quantity;
                break;
            }
        }
        if (possesed > 0) {
            possesed -= 1;
            getClient().getSession().write(MaplePacketCreator.serverNotice(5, "因使用了 [护身符] 死亡后您的经验不会减少！剩余 (" + possesed + " 个)"));
            MapleInventoryManipulator.removeById(getClient(), MapleItemInformationProvider.getInstance().getInventoryType(charmID[i]), charmID[i], 1, true, false);
        } else {
            if (getEventInstance() != null) {
                getEventInstance().playerKilled(this);
            }
            dispelSkill(0);
            cancelEffectFromBuffStat(MapleBuffStat.MORPH);
            cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            cancelEffectFromBuffStat(MapleBuffStat.REAPER);
            cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
            checkFollow();
            if (job != 0 && job != 1_000 && job != 2_000) {
                float diepercentage = 0.0f;
                int expforlevel = GameConstants.getExpNeededForLevel(level);
                if (map.isTown() || FieldLimitType.RegularExpLoss.check(map.getFieldLimit())) {
                    diepercentage = 0.01f;
                } else {
                    float v8 = 0.0f;
                    if (this.job / 100 == 3) {
                        v8 = 0.08f;
                    } else {
                        v8 = 0.2f;
                    }
                    diepercentage = (float) (v8 / this.stats.getLuk() + 0.05);
                }
                int v10 = (int) (exp - (long) ((double) expforlevel * diepercentage));
                if (v10 < 0) {
                    v10 = 0;
                }
                this.exp = v10;
            }
            this.updateSingleStat(MapleStat.EXP, this.exp);
            if (!stats.checkEquipDurabilitys(this, -100)) { //i guess this is how it works ?
                dropMessage(5, "耐久度已经归零.");
            } //lol
            if (pyramidSubway != null) {
                stats.setHp((short) 50);
                pyramidSubway.fail(this);
            }
        }
    }

    /**
     *
     */
    public void updatePartyMemberHP() {
        if (party != null) {
            final int channel = client.getChannel();
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                    MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (other != null) {
                        other.getClient().getSession().write(MaplePacketCreator.updatePartyMemberHP(getId(), stats.getHp(), stats.getCurrentMaxHp()));
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void receivePartyMemberHP() {
        if (party == null) {
            return;
        }
        int channel = client.getChannel();
        for (MaplePartyCharacter partychar : party.getMembers()) {
            if (partychar.getMapid() == getMapId() && partychar.getChannel() == channel) {
                MapleCharacter other = ChannelServer.getInstance(channel).getPlayerStorage().getCharacterByName(partychar.getName());
                if (other != null) {
                    client.getSession().write(MaplePacketCreator.updatePartyMemberHP(other.getId(), other.getStat().getHp(), other.getStat().getCurrentMaxHp()));
                }
            }
        }
    }

    /**
     *
     * @param delta
     */
    public void healHP(int delta) {
        addHP(delta);
//        client.getSession().write(MaplePacketCreator.showOwnHpHealed(delta));
//        getMap().broadcastMessage(this, MaplePacketCreator.showHpHealed(getId(), delta), false);
    }

    /**
     *
     * @param delta
     */
    public void healMP(int delta) {
        addMP(delta);
//        client.getSession().write(MaplePacketCreator.showOwnHpHealed(delta));
//        getMap().broadcastMessage(this, MaplePacketCreator.showHpHealed(getId(), delta), false);
    }

    /**
     * Convenience function which adds the supplied parameter to the current hp
     * then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setHp(int)
     * @param delta
     */
    public void addHP(int delta) {
        if (stats.setHp(stats.getHp() + delta)) {
            updateSingleStat(MapleStat.HP, stats.getHp());
        }
    }

    /**
     * Convenience function which adds the supplied parameter to the current mp
     * then directly does a updateSingleStat.
     *
     * @see MapleCharacter#setMp(int)
     * @param delta
     */
    public void addMP(int delta) {
        if (stats.setMp(stats.getMp() + delta)) {
            updateSingleStat(MapleStat.MP, stats.getMp());
        }
    }

    /**
     *
     * @param hpDiff
     * @param mpDiff
     */
    public void addMPHP(int hpDiff, int mpDiff) {
        List<Pair<MapleStat, Integer>> statups = new ArrayList<>();

        if (stats.setHp(stats.getHp() + hpDiff)) {
            statups.add(new Pair<>(MapleStat.HP, Integer.valueOf(stats.getHp())));
        }
        if (stats.setMp(stats.getMp() + mpDiff)) {
            statups.add(new Pair<>(MapleStat.MP, Integer.valueOf(stats.getMp())));
        }
        if (statups.size() > 0) {
            client.getSession().write(MaplePacketCreator.updatePlayerStats(statups, getJob()));
        }
    }
    private long lastHPTime, lastMPTime, lastCheckPeriodTime, lastMoveItemTime, lastQuestTime;

    /**
     *
     */
    public long lastRecoveryTime = 0;

    /**
     *
     * @return
     */
    public final boolean canQuestAction() {
        if (lastQuestTime + 250 > System.currentTimeMillis()) {
            return false;
        }
        lastQuestTime = System.currentTimeMillis();
        return true;
    }

    private void prepareRecovery() {
        lastRecoveryTime = System.currentTimeMillis();
    }

    /**
     *
     * @return
     */
    public boolean canRecovery() {
        return lastRecoveryTime > 0 && lastRecoveryTime + 5_000 < System.currentTimeMillis() + 5_000;
    }

    /**
     *
     */
    public void doRecovery() {
        MapleStatEffect eff = getStatForBuff(MapleBuffStat.RECOVERY);
        if (eff != null) {
            prepareRecovery();
            if (stats.getHp() > stats.getCurrentMaxHp()) {
                this.cancelEffectFromBuffStat(MapleBuffStat.RECOVERY);
            } else {
                healHP(eff.getX());
            }
        }
    }

    /**
     *
     * @return
     */
    public final boolean canHP() {
        if (lastHPTime + 5_000 > System.currentTimeMillis()) {
            return false;
        }
        lastHPTime = System.currentTimeMillis();
        return true;
    }

    /**
     *
     * @return
     */
    public final boolean canMP() {
        if (lastMPTime + 5_000 > System.currentTimeMillis()) {
            return false;
        }
        lastMPTime = System.currentTimeMillis();
        return true;
    }

    /**
     *
     * @return
     */
    public final boolean canCheckPeriod() {
        if (lastCheckPeriodTime + 30_000 > System.currentTimeMillis()) {
            return false;
        }
        lastCheckPeriodTime = System.currentTimeMillis();
        return true;
    }

    /**
     *
     * @return
     */
    public final boolean canMoveItem() {
        if (lastMoveItemTime + 250 > System.currentTimeMillis()) {
            return false;
        }
        lastMoveItemTime = System.currentTimeMillis();
        return true;
    }

    /**
     *
     * @param stat
     * @param newval
     */
    public void updateSingleStat(MapleStat stat, int newval) {
        updateSingleStat(stat, newval, false);
    }

    /**
     * Updates a single stat of this MapleCharacter for the client. This method
     * only creates and sends an update packet, it does not update the stat
     * stored in this MapleCharacter instance.
     *
     * @param stat
     * @param newval
     * @param itemReaction
     */
    public void updateSingleStat(MapleStat stat, int newval, boolean itemReaction) {
        /*
         * if (stat == MapleStat.AVAILABLESP) {
         * client.getSession().write(MaplePacketCreator.updateSp(this,
         * itemReaction, false)); return; }
         */
        Pair<MapleStat, Integer> statpair = new Pair<>(stat, Integer.valueOf(newval));
        client.getSession().write(MaplePacketCreator.updatePlayerStats(Collections.singletonList(statpair), itemReaction, getJob()));
    }

    /**
     *
     * @param total
     * @param show
     * @param inChat
     * @param white
     */
    public void gainExp(final int total, final boolean show, final boolean inChat, final boolean white) {//200级以后是否还能获得经验 设置等级上限
        try {
            int prevexp = getExp();
            int needed = GameConstants.getExpNeededForLevel(level);
            if ((GameConstants.isKOC(job) && level >= Integer.parseInt(ServerProperties.getProperty("ZeroMS.QLevel"))) || (!GameConstants.isKOC(job) && level >= Integer.parseInt(ServerProperties.getProperty("ZeroMS.MLevel")))) {
                if (exp + total > needed) {
                    setExp(needed);
                } else {
                    exp += total;
                }
            } else {
                boolean leveled = false;
                if (exp + total >= needed) {
                    exp += total;
                    levelUp();
                    leveled = true;
                    needed = GameConstants.getExpNeededForLevel(level);
                    if (exp > needed) {
                        setExp(needed);
                    }
                } else {
                    exp += total;
                }
                if (total > 0) {
                    // familyRep(prevexp, needed, leveled);
                }
            }
            if (total != 0) {
                if (exp < 0) { // After adding, and negative
                    if (total > 0) {
                        setExp(needed);
                    } else if (total < 0) {
                        setExp(0);
                    }
                }
                if (show) { // still show the expgain even if it's not there
                    client.getSession().write(MaplePacketCreator.GainEXP_Others(total, inChat, white));
                }
                /*
                 * if (total > 0) { stats.checkEquipLevels(this, total); //gms
                 * like }
                 */
                updateSingleStat(MapleStat.EXP, getExp());
            }
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e); //all jobs throw errors :(
        }
    }

    /**
     *
     * @param prevexp
     * @param needed
     * @param leveled
     */
    public void familyRep(int prevexp, int needed, boolean leveled) {
        if (mfc != null) {
            int onepercent = needed / 100;
            int percentrep = (prevexp / onepercent + getExp() / onepercent);
            if (leveled) {
                percentrep = 100 - percentrep + (level / 2);
            }
            if (percentrep > 0) {
                int sensen = World.Family.setRep(mfc.getFamilyId(), mfc.getSeniorId(), percentrep, level);
                if (sensen > 0) {
                    World.Family.setRep(mfc.getFamilyId(), sensen, percentrep / 2, level); //and we stop here
                }
            }
        }
    }

    /**
     *
     * @param gain
     * @param show
     * @param white
     * @param pty
     * @param wedding_EXP
     * @param Class_Bonus_EXP
     * @param Equipment_Bonus_EXP
     * @param Premium_Bonus_EXP
     */
    public void gainExpMonster(final int gain, final boolean show, final boolean white, final byte pty, int wedding_EXP, int Class_Bonus_EXP, int Equipment_Bonus_EXP, int Premium_Bonus_EXP) {
        int 结婚经验 = 0;
        if (this.marriageId > 0) {
            MapleCharacter MarrChr = this.map.getCharacterById(this.marriageId);
            if (MarrChr != null) {
                结婚经验 += (int) (gain / 100.0D * 20.0D);
            }
        }

        // int 结婚经验 = gain * getMarriageId() / 10; 
        // if (结婚经验 <= 0 && getMarriageId() >= 1) {
        //      结婚经验 = 1;
        //  }else if(getMarriageId() == 0){
        //       结婚经验 = 0;
        //   }   
        //System.out.print("物品吊坠应得经验"+Equipment_Bonus_EXP+"");
        int total = gain + Class_Bonus_EXP + Equipment_Bonus_EXP + Premium_Bonus_EXP + 结婚经验;
        int partyinc = 0;
        int prevexp = getExp();
        if (canCheckPeriod()) {
            expirationTask(true, false);
        }
        if (pty > 1) {
            // (Exp / 20) * (member + 1)
            partyinc = (int) (((float) (gain / 50.0)) * (pty + 1));
            total += partyinc;
        }

        if (gain > 0 && total < gain) { //just in case
            total = Integer.MAX_VALUE;
        }
        int needed = GameConstants.getExpNeededForLevel(level);
        if (level >= 200 || (GameConstants.isKOC(job) && level >= 200)) { //等级限制
            if (exp + total > needed) {
                setExp(needed);
            } else {
                exp += total;
            }
        } else {
            boolean leveled = false;
            if (exp + total >= needed) {
                exp += total;
                levelUp();
                leveled = true;
                needed = GameConstants.getExpNeededForLevel(level);
                if (exp > needed) {
                    setExp(needed);
                }
            } else {
                exp += total;
            }
            if (total > 0) {
                //   familyRep(prevexp, needed, leveled);
            }
        }
        if (gain != 0) {
            if (exp < 0) { // After adding, and negative
                if (gain > 0) {
                    setExp(GameConstants.getExpNeededForLevel(level));
                } else if (gain < 0) {
                    setExp(0);
                }
            }
            updateSingleStat(MapleStat.EXP, getExp());
            if (show) { // still show the expgain even if it's not there
                //    client.getSession().write(MaplePacketCreator.GainEXP_Monster(gain, white, wedding_EXP, partyinc, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP));
                client.getSession().write(MaplePacketCreator.GainEXP_Monster(gain, white, partyinc, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP, 结婚经验));
            }
            // stats.checkEquipLevels(this, total);
        }
    }

    /**
     *
     * @param item
     * @param type
     */
    public void forceReAddItem_NoUpdate(IItem item, MapleInventoryType type) {
        getInventory(type).removeSlot(item.getPosition());
        getInventory(type).addFromDB(item);
    }

    /**
     *
     * @param item
     * @param type
     */
    public void forceReAddItem(IItem item, MapleInventoryType type) { //used for stuff like durability, item exp/level, probably owner?
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.getSession().write(MaplePacketCreator.updateSpecialItemUse(item, type == MapleInventoryType.EQUIPPED ? (byte) 1 : type.getType()));
        }
    }

    /**
     *
     * @param item
     * @param type
     */
    public void forceReAddItem_Flag(IItem item, MapleInventoryType type) { //used for flags
        forceReAddItem_NoUpdate(item, type);
        if (type != MapleInventoryType.UNDEFINED) {
            client.getSession().write(MaplePacketCreator.updateSpecialItemUse_(item, type == MapleInventoryType.EQUIPPED ? (byte) 1 : type.getType()));
        }
    }

    /**
     *
     */
    public void silentPartyUpdate() {
        if (party != null) {
            World.Party.updateParty(party.getId(), PartyOperation.SILENT_UPDATE, new MaplePartyCharacter(this));
        }
    }

    /**
     *
     * @return
     */
    public boolean isGM() {
        return gmLevel > 0;
    }

    /**
     *
     * @return
     */
    public boolean isAdmin() {
        return gmLevel >= 2;
    }

    /**
     *
     * @return
     */
    public int getGMLevel() {
        return gmLevel;
    }

    /**
     *
     * @return
     */
    public boolean isPlayer() {
        return gmLevel == 0;
    }

    /**
     *
     * @param level
     * @return
     */
    public boolean hasGmLevel(int level) {
        return gmLevel >= level;
    }

    /**
     *
     * @param type
     * @return
     */
    public final MapleInventory getInventory(MapleInventoryType type) {
        return inventory[type.ordinal()];
    }

    /**
     *
     * @return
     */
    public final MapleInventory[] getInventorys() {
        return inventory;
    }

    /**
     *
     */
    public final void expirationTask() {
        expirationTask(false);
    }

    /**
     *
     * @param pending
     */
    public final void expirationTask(boolean pending) {
        expirationTask(false, pending);
    }

    /**
     *
     * @param packet
     * @param pending
     */
    public final void expirationTask(boolean packet, boolean pending) {
        if (pending) {
            if (pendingExpiration != null) {
                for (Integer z : pendingExpiration) {
                    client.sendPacket(MTSCSPacket.itemExpired(z));
                }
            }
            pendingExpiration = null;
            if (pendingSkills != null) {
                for (Integer z : pendingSkills) {
                    client.sendPacket(MaplePacketCreator.updateSkill(z, 0, 0, -1));
                    client.sendPacket(MaplePacketCreator.serverNotice(5, "[" + SkillFactory.getSkillName(z) + "] 技能已经过期"));
                }
            } //not real msg
            pendingSkills = null;
            return;
        }
        long expiration;
        final List<Integer> ret = new ArrayList<>();
        final long currenttime = System.currentTimeMillis();
        final List<Pair<MapleInventoryType, IItem>> toberemove = new ArrayList<>(); // This is here to prevent deadlock.
        final List<IItem> tobeunlock = new ArrayList<>(); // This is here to prevent deadlock.

        for (final MapleInventoryType inv : MapleInventoryType.values()) {
            for (final IItem item : getInventory(inv)) {
                expiration = item.getExpiration();

                if (expiration != -1 && !GameConstants.isPet(item.getItemId()) && currenttime > expiration) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        tobeunlock.add(item);
                    } else if (currenttime > expiration) {
                        toberemove.add(new Pair<>(inv, item));
                    }
                } else if (item.getItemId() == 5_000_054 && item.getPet() != null && item.getPet().getSecondsLeft() <= 0) {
                    toberemove.add(new Pair<>(inv, item));
                }
            }
        }
        IItem item;
        for (final Pair<MapleInventoryType, IItem> itemz : toberemove) {
            item = itemz.getRight();
            ret.add(item.getItemId());
            if (packet) {
                getInventory(itemz.getLeft()).removeItem(item.getPosition(), item.getQuantity(), false, this);
            } else {
                getInventory(itemz.getLeft()).removeItem(item.getPosition(), item.getQuantity(), false);
            }
        }
        for (final IItem itemz : tobeunlock) {
            itemz.setExpiration(-1);
            itemz.setFlag((byte) (itemz.getFlag() - ItemFlag.LOCK.getValue()));
        }

        this.pendingExpiration = ret;

        final List<Integer> skilz = new ArrayList<>();
        final List<ISkill> toberem = new ArrayList<>();
        for (Entry<ISkill, SkillEntry> skil : skills.entrySet()) {
            if (skil.getValue().expiration != -1 && currenttime > skil.getValue().expiration) {
                toberem.add(skil.getKey());
            }
        }
        for (ISkill skil : toberem) {
            skilz.add(skil.getId());
            this.skills.remove(skil);
        }
        this.pendingSkills = skilz;
    }

    /**
     *
     * @return
     */
    public MapleShop getShop() {
        return shop;
    }

    /**
     *
     * @param shop
     */
    public void setShop(MapleShop shop) {
        this.shop = shop;
    }

    /**
     *
     * @return
     */
    public int getMeso() {
        return meso;
    }

    /**
     *
     * @return
     */
    public final int[] getSavedLocations() {
        return savedLocations;
    }

    /**
     *
     * @param type
     * @return
     */
    public int getSavedLocation(SavedLocationType type) {
        return savedLocations[type.getValue()];
    }

    /**
     *
     * @param type
     */
    public void saveLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = getMapId();
    }

    /**
     *
     * @param type
     * @param mapz
     */
    public void saveLocation(SavedLocationType type, int mapz) {
        savedLocations[type.getValue()] = mapz;
    }

    /**
     *
     * @param type
     */
    public void clearSavedLocation(SavedLocationType type) {
        savedLocations[type.getValue()] = -1;
    }

    /**
     *
     * @return
     */
    public int getDY() {
        return maplepoints;
    }

    /**
     *
     * @param set
     */
    public void setDY(int set) {
        this.maplepoints = set;
    }

    /**
     *
     * @param gain
     */
    public void gainDY(int gain) {
        this.maplepoints += gain;
        // setDY(getDY() + gain);
    }

    /**
     *
     * @param gain
     * @param show
     */
    public void gainMeso(int gain, boolean show) {
        gainMeso(gain, show, false, false);
    }

    /**
     *
     * @param gain
     * @param show
     * @param enableActions
     */
    public void gainMeso(int gain, boolean show, boolean enableActions) {
        gainMeso(gain, show, enableActions, false);
    }

    /**
     *
     * @param gain
     * @param show
     * @param enableActions
     * @param inChat
     */
    public void gainMeso(int gain, boolean show, boolean enableActions, boolean inChat) {
        if (meso + gain < 0) {
            client.getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        meso += gain;
        updateSingleStat(MapleStat.MESO, meso, enableActions);
        if (show) {
            client.getSession().write(MaplePacketCreator.showMesoGain(gain, inChat));
        }
    }

    /**
     *
     * @param monster
     * @param aggro
     */
    public void controlMonster(MapleMonster monster, boolean aggro) {
        if (clone) {
            return;
        }
        monster.setController(this);
        controlled.add(monster);
        client.getSession().write(MobPacket.controlMonster(monster, false, aggro));
    }

    /**
     *
     * @param monster
     */
    public void stopControllingMonster(MapleMonster monster) {
        if (clone) {
            return;
        }
        if (monster != null && controlled.contains(monster)) {
            controlled.remove(monster);
        }
    }

    /**
     *
     * @param monster
     */
    public void checkMonsterAggro(MapleMonster monster) {
        if (clone || monster == null) {
            return;
        }
        if (monster.getController() == this) {
            monster.setControllerHasAggro(true);
        } else {
            monster.switchController(this, true);
        }
    }

    /**
     *
     * @return
     */
    public Set<MapleMonster> getControlled() {
        return controlled;
    }

    /**
     *
     * @return
     */
    public int getControlledSize() {
        return controlled.size();
    }

    /**
     *
     * @return
     */
    public int getAccountID() {
        return accountid;
    }

    /**
     *
     * @param id
     * @param skillID
     */
    public void mobKilled(final int id, final int skillID) {
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() != 1 || !q.hasMobKills()) {
                continue;
            }
            if (q.mobKilled(id, skillID)) {
                client.getSession().write(MaplePacketCreator.updateQuestMobKills(q));
                if (q.getQuest().canComplete(this, null)) {
                    client.getSession().write(MaplePacketCreator.getShowQuestCompletion(q.getQuest().getId()));
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public final List<MapleQuestStatus> getStartedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 1 && !(q.isCustom())) {
                ret.add(q);
            }
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public final List<MapleQuestStatus> getCompletedQuests() {
        List<MapleQuestStatus> ret = new LinkedList<>();
        for (MapleQuestStatus q : quests.values()) {
            if (q.getStatus() == 2 && !(q.isCustom())) {
                ret.add(q);
            }
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public Map<ISkill, SkillEntry> getSkills() {
        return Collections.unmodifiableMap(skills);
    }

    /**
     *
     * @param skill
     * @return
     */
    public byte getSkillLevel(final ISkill skill) {
        final SkillEntry ret = skills.get(skill);
        if (ret == null || ret.skillevel <= 0) {
            return 0;
        }
        return (byte) Math.min(skill.getMaxLevel(), ret.skillevel + (skill.isBeginnerSkill() ? 0 : stats.incAllskill));
    }

    /**
     *
     * @param skill
     * @return
     */
    public byte getMasterLevel(final int skill) {
        return getMasterLevel(SkillFactory.getSkill(skill));
    }

    /**
     *
     * @param skill
     * @return
     */
    public byte getMasterLevel(final ISkill skill) {
        final SkillEntry ret = skills.get(skill);
        if (ret == null) {
            return 0;
        }
        return ret.masterlevel;
    }

    /**
     *
     */
    public void levelUp() {
        //  getClient().StartWindow();
        if (GameConstants.isKOC(job)) {
            if (level <= 70) {
                remainingAp += 6;
            } else {
                remainingAp += 5;
            }
        } else {
            remainingAp += 5;
        }
        int maxhp = stats.getMaxHp();
        int maxmp = stats.getMaxMp();

        if (job == 0 || job == 1_000 || job == 2_000 || job == 2_001 || job == 3_000) { // Beginner
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(10, 12);
        } else if (job >= 100 && job <= 132) { // Warrior
            final ISkill improvingMaxHP = SkillFactory.getSkill(1_000_001);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 200 && job <= 232) { // Magician
            final ISkill improvingMaxMP = SkillFactory.getSkill(2_000_001);
            final int slevel = getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        } else if (job >= 3_200 && job <= 3_212) { //battle mages get their own little neat thing
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(42, 44);
        } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1_300 && job <= 1_311) || (job >= 1_400 && job <= 1_411) || (job >= 3_300 && job <= 3_312)) { // Bowman, Thief, Wind Breaker and Night Walker
            maxhp += Randomizer.rand(20, 24);
            maxmp += Randomizer.rand(14, 16);
        } else if ((job >= 500 && job <= 522) || (job >= 3_500 && job <= 3_512)) { // Pirate
            final ISkill improvingMaxHP = SkillFactory.getSkill(5_100_000);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        } else if (job >= 1_100 && job <= 1_111) { // Soul Master
            final ISkill improvingMaxHP = SkillFactory.getSkill(11_000_000);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(24, 28);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 1_200 && job <= 1_211) { // Flame Wizard
            final ISkill improvingMaxMP = SkillFactory.getSkill(12_000_000);
            final int slevel = getSkillLevel(improvingMaxMP);
            if (slevel > 0) {
                maxmp += improvingMaxMP.getEffect(slevel).getX() * 2;
            }
            maxhp += Randomizer.rand(10, 14);
            maxmp += Randomizer.rand(22, 24);
        } else if (job >= 1_500 && job <= 1_512) { // Pirate
            final ISkill improvingMaxHP = SkillFactory.getSkill(15_100_000);
            final int slevel = getSkillLevel(improvingMaxHP);
            if (slevel > 0) {
                maxhp += improvingMaxHP.getEffect(slevel).getX();
            }
            maxhp += Randomizer.rand(22, 26);
            maxmp += Randomizer.rand(18, 22);
        } else if (job >= 2_100 && job <= 2_112) { // Aran
            maxhp += Randomizer.rand(50, 52);
            maxmp += Randomizer.rand(4, 6);
        } else if (job >= 2_200 && job <= 2_218) { // Evan
            maxhp += Randomizer.rand(12, 16);
            maxmp += Randomizer.rand(50, 52);
        } else { // GameMaster
            maxhp += Randomizer.rand(50, 100);
            maxmp += Randomizer.rand(50, 100);
        }
        maxmp += stats.getTotalInt() / 10;
        exp -= GameConstants.getExpNeededForLevel(level);
        level += 1;
        int level = getLevel();

        // 成就系統
        /*
         * if (level == 30) { finishAchievement(2); } if (level == 70) {
         * finishAchievement(3); } if (level == 120) { finishAchievement(4); }
         * if (level == 200) { finishAchievement(5); }
         */
        if (!isGM()) {
            if (level == 10 || level == 30 || level == 70 || level == 120 || level == 200) {
                final StringBuilder sb = new StringBuilder("[恭喜] ");
                final IItem medal = getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -26);
                if (medal != null) { // Medal
                    sb.append("<");
                    sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
                    sb.append("> ");
                }
                sb.append(getName());
                sb.append(" 达到了 ").append(level).append(" 级,让我们一起恭喜他/她吧!");
                World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, sb.toString()).getBytes());
            }
        }
        // maxhp = (short) Math.min(30000, Math.abs(maxhp));
        //maxmp = (short) Math.min(30000, Math.abs(maxmp));

        maxhp = Math.min(30_000, maxhp);
        maxmp = Math.min(30_000, maxmp);
        final List<Pair<MapleStat, Integer>> statup = new ArrayList<>(8);

        statup.add(new Pair(MapleStat.AVAILABLEAP, Integer.valueOf(this.remainingAp)));
        statup.add(new Pair<>(MapleStat.MAXHP, maxhp));
        statup.add(new Pair<>(MapleStat.MAXMP, maxmp));
        statup.add(new Pair<>(MapleStat.HP, maxhp));
        statup.add(new Pair<>(MapleStat.MP, maxmp));
        statup.add(new Pair<>(MapleStat.EXP, exp));
        statup.add(new Pair<>(MapleStat.LEVEL, (int) level));

        if (isGM() || (job != 0 && job != 1_000 && job != 2_000 && job != 2_001 && job != 3_000)) { // Not Beginner, Nobless and Legend
            remainingSp[GameConstants.getSkillBook(this.job)] += 3;
            client.getSession().write(MaplePacketCreator.updateSp(this, false));
        } else if (level <= 10) {
            stats.setStr((short) (stats.getStr() + remainingAp));
            remainingAp = 0;

            statup.add(new Pair<>(MapleStat.STR, (int) stats.getStr()));
        }

        statup.add(new Pair<>(MapleStat.AVAILABLEAP, (int) remainingAp));

        stats.setMaxHp((short) maxhp);
        stats.setMaxMp((short) maxmp);
        stats.setHp((short) maxhp);
        stats.setMp((short) maxmp);
        client.getSession().write(MaplePacketCreator.updatePlayerStats(statup, getJob()));
        map.broadcastMessage(this, MaplePacketCreator.showForeignEffect(getId(), 0), false);
        stats.recalcLocalStats();
        silentPartyUpdate();
        guildUpdate();
        familyUpdate();

        saveToDB(false, false);
//                if (GameConstants.isAran(job)) {
//            switch (level) {
//                case 30:
//                    client.getSession().write(MaplePacketCreator.startMapEffect("You have reached level 30! To job advance, go back to Lirin of Rien.", 5120000, true));
//                    break;
//                case 70:
//                    client.getSession().write(MaplePacketCreator.startMapEffect("You have reached level 70! To job advance, talk to your job instructor in El Nath.", 5120000, true));
//                    break;
//                case 120:
//                    client.getSession().write(MaplePacketCreator.startMapEffect("You have reached level 120! To job advance, talk to your job instructor in Leafre.", 5120000, true));
//                    break;
//            }
//        }
//        if (GameConstants.isKOC(job) && level == 70) {
//            client.getSession().write(MaplePacketCreator.startMapEffect("You have reached level 70! To job advance, talk to your job instructor in Erev.", 5120000, true));
//        }
        /*
         * if (GameConstants.isEvan(job)) { switch (level) { case 9:
         * client.getSession().write(MaplePacketCreator.startMapEffect("請確保您完成所有的任務需要達到10級之前，否則你將無法繼續.",
         * 5120000, true)); break; case 10: case 20: case 30: case 40: case 50:
         * case 60: case 80: case 100: case 120: case 160: if (job < 2218) {
         * changeJob(job == 2001 ? 2200 : (job == 2200 ? 2210 : (job + 1)));
         * //automatic } break; } }
         */
 /*
         * if (getSubcategory() == 1) { //db level 2 switch (level) { case 2:
         * client.getSession().write(MaplePacketCreator.startMapEffect("Click
         * the lightbulb above you and accept the [Required] quest. Remake the
         * character if this quest is not showing.", 5120009, true)); break;
         * case 10:
         * client.getSession().write(MaplePacketCreator.startMapEffect("Go and
         * advance to a Rogue at Dark Lord in Kerning City. Make sure you do ALL
         * the [Required] quests.", 5120000, true)); break; case 15:
         * client.getSession().write(MaplePacketCreator.startMapEffect("Make
         * sure you have been doing all the required quests. Remember that
         * saving SP is possible.", 5120000, true)); break; case 20:
         * client.getSession().write(MaplePacketCreator.startMapEffect("You have
         * reached level 20. If you have done all your required quests, you can
         * enter Secret Garden and advance.", 5120000, true)); break; case 30:
         * client.getSession().write(MaplePacketCreator.startMapEffect("You have
         * reached level 30. Please go to Lady Syl to advance.", 5120000,
         * true)); break; case 55:
         * client.getSession().write(MaplePacketCreator.startMapEffect("You have
         * reached level 55. Please go to Lady Syl and do a few quests to
         * advance.", 5120000, true)); break; case 70:
         * client.getSession().write(MaplePacketCreator.startMapEffect("You have
         * reached level 70. Please go to your job instructor in Elnath to
         * advance.", 5120000, true)); break; case 120:
         * client.getSession().write(MaplePacketCreator.startMapEffect("You have
         * reached level 120. Please go to your job instructor in Leafre to
         * advance.", 5120000, true)); break; } }
         */
        //if (map.getForceMove() > 0 && map.getForceMove() <= getLevel()) {
        //    changeMap(map.getReturnMap(), map.getReturnMap().getPortal(0));
        //    dropMessage(5, "You have been expelled from the map.");
        //}
    }

    /**
     *
     * @param key
     * @param type
     * @param action
     */
    public void changeKeybinding(int key, byte type, int action) {
        if (type != 0) {
            keylayout.Layout().put(Integer.valueOf(key), new Pair<>(type, action));
        } else {
            keylayout.Layout().remove(key);
        }
    }

    /**
     *
     */
    public void sendMacros() {
        for (int i = 0; i < 5; i++) {
            if (skillMacros[i] != null) {
                client.getSession().write(MaplePacketCreator.getMacros(skillMacros));
                break;
            }
        }
    }

    /**
     *
     * @param position
     * @param updateMacro
     */
    public void updateMacros(int position, SkillMacro updateMacro) {
        skillMacros[position] = updateMacro;
    }

    /**
     *
     * @return
     */
    public final SkillMacro[] getMacros() {
        return skillMacros;
    }

    /**
     *
     * @param reason
     * @param duration
     * @param greason
     * @param IPMac
     */
    public void tempban(String reason, Calendar duration, int greason, boolean IPMac) {
        if (IPMac) {
            client.banMacs();
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)");
            ps.setString(1, client.getSession().getRemoteAddress().toString().split(":")[0]);
            ps.execute();
            ps.close();

            client.getSession().close();

            ps = con.prepareStatement("UPDATE accounts SET tempban = ?, banreason = ?, greason = ? WHERE id = ?");
            Timestamp TS = new Timestamp(duration.getTimeInMillis());
            ps.setTimestamp(1, TS);
            ps.setString(2, reason);
            ps.setInt(3, greason);
            ps.setInt(4, accountid);
            ps.execute();
            ps.close();
        } catch (SQLException ex) {
            System.err.println("Error while tempbanning" + ex);
        }

    }

    /**
     *
     * @param reason
     * @param IPMac
     * @param autoban
     * @param hellban
     * @return
     */
    public final boolean ban(String reason, boolean IPMac, boolean autoban, boolean hellban) {
        hellban = false;
        if (lastmonthfameids == null) {
            throw new RuntimeException("Trying to ban a non-loaded character (testhack)");
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ?")) {
                ps.setInt(1, autoban ? 2 : 1);
                ps.setString(2, reason);
                ps.setInt(3, accountid);
                ps.execute();
            }

            client.banMacs();
//
//            if (hellban) {
//                PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
//                psa.setInt(1, accountid);
//                ResultSet rsa = psa.executeQuery();
//                if (rsa.next()) {
//                    PreparedStatement pss = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE email = ? ");
//                    pss.setInt(1, autoban ? 2 : 1);
//                    pss.setString(2, reason);
//                    pss.setString(3, rsa.getString("email"));
//                    pss.execute();
//                    pss.close();
//                }
//                rsa.close();
//                psa.close();
//            }

        } catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
            return false;
        }
        client.getSession().close();
        return true;
    }

    /**
     *
     * @param id
     * @param reason
     * @param accountId
     * @param gmlevel
     * @param hellban
     * @return
     */
    public static boolean ban(String id, String reason, boolean accountId, int gmlevel, boolean hellban) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;

            if (id.matches("/[0-9]{1,3}\\..*")) {
                return true;
            }
            if (accountId) {
                ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ?");
            } else {
                ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?");
            }
            boolean ret = false;
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int z = rs.getInt(1);
                    try (PreparedStatement psb = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE id = ? AND gm < ?")) {
                        psb.setString(1, reason);
                        psb.setInt(2, z);
                        psb.setInt(3, gmlevel);
                        psb.execute();
                    }
                    if (gmlevel > 100) {
                        try ( //admin ban
                                PreparedStatement psa = con.prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
                            psa.setInt(1, z);
                            ResultSet rsa = psa.executeQuery();
                            if (rsa.next()) {
                                String sessionIP = rsa.getString("sessionIP");
                                if (sessionIP != null && sessionIP.matches("/[0-9]{1,3}\\..*")) {
                                    try (PreparedStatement psz = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)")) {
                                        psz.setString(1, sessionIP);
                                        psz.execute();
                                    }
                                }
                                if (rsa.getString("macs") != null) {
                                    String[] macData = rsa.getString("macs").split(", ");
                                    if (macData.length > 0) {
                                        MapleClient.banMacs(macData);
                                    }
                                }
//                        if (hellban) {
//                            PreparedStatement pss = con.prepareStatement("UPDATE accounts SET banned = 1, banreason = ? WHERE email = ?" + (sessionIP == null ? "" : " OR SessionIP = ?"));
//                            pss.setString(1, reason);
//                            pss.setString(2, rsa.getString("email"));
//                            if (sessionIP != null) {
//                                pss.setString(3, sessionIP);
//                            }
//                            pss.execute();
//                            pss.close();
//                        }
                            }
                            rsa.close();
                        }
                    }
                    ret = true;
                }
            }
            ps.close();
            return ret;
        } catch (SQLException ex) {
            System.err.println("Error while banning" + ex);
        }
        return false;
    }

    /**
     * Oid of players is always = the cid
     */
    @Override
    public int getObjectId() {
        return getId();
    }

    /**
     * Throws unsupported operation exception, oid of players is read only
     */
    @Override
    public void setObjectId(int id) {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @return
     */
    public MapleStorage getStorage() {
        return storage;
    }

    /**
     *
     * @param mo
     */
    public void addVisibleMapObject(MapleMapObject mo) {
        if (clone) {
            return;
        }
        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.add(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    /**
     *
     * @param mo
     */
    public void removeVisibleMapObject(MapleMapObject mo) {
        if (clone) {
            return;
        }
        visibleMapObjectsLock.writeLock().lock();
        try {
            visibleMapObjects.remove(mo);
        } finally {
            visibleMapObjectsLock.writeLock().unlock();
        }
    }

    /**
     *
     * @param mo
     * @return
     */
    public boolean isMapObjectVisible(MapleMapObject mo) {
        visibleMapObjectsLock.readLock().lock();
        try {
            return !clone && visibleMapObjects.contains(mo);
        } finally {
            visibleMapObjectsLock.readLock().unlock();
        }
    }

    /**
     *
     * @return
     */
    public Collection<MapleMapObject> getAndWriteLockVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().lock();
        return visibleMapObjects;
    }

    /**
     *
     */
    public void unlockWriteVisibleMapObjects() {
        visibleMapObjectsLock.writeLock().unlock();
    }

    /**
     *
     * @return
     */
    public boolean isAlive() {
        return stats.getHp() > 0;
    }

    /**
     *
     * @param client
     */
    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.removePlayerFromMap(this.getObjectId()));
        for (final WeakReference<MapleCharacter> chr : clones) {
            if (chr.get() != null) {
                chr.get().sendDestroyData(client);
            }
        }
    }

    /**
     *
     * @param client
     */
    @Override
    public void sendSpawnData(MapleClient client) {
        if (client.getPlayer().allowedToTarget(this)) {
            client.getSession().write(MaplePacketCreator.spawnPlayerMapobject(this));

            for (final MaplePet pet : pets) {
                if (pet.getSummoned()) {
                    client.getSession().write(PetPacket.showPet(this, pet, false, false));
                    client.sendPacket(PetPacket.petStatUpdate(this));

                }
            }
            for (final WeakReference<MapleCharacter> chr : clones) {
                if (chr.get() != null) {
                    chr.get().sendSpawnData(client);
                }
            }
            if (summons != null) {
                for (final MapleSummon summon : summons.values()) {
                    client.getSession().write(MaplePacketCreator.spawnSummon(summon, false));
                }
            }
            if (followid > 0) {
                //   client.getSession().write(MaplePacketCreator.followEffect(followinitiator ? id : followid, followinitiator ? followid : id, null));
            }
        }
    }

    /**
     *
     */
    public final void equipChanged() {
        map.broadcastMessage(this, MaplePacketCreator.updateCharLook(this), false);
        map.broadcastMessage(MaplePacketCreator.loveEffect());
        stats.recalcLocalStats();
        if (getMessenger() != null) {
            World.Messenger.updateMessenger(getMessenger().getId(), getName(), client.getChannel());
        }
        saveToDB(false, false);
    }

    /**
     *
     * @param index
     * @return
     */
    public final MaplePet getPet(final int index) {
        byte count = 0;
        for (final MaplePet pet : pets) {
            if (pet.getSummoned()) {
                if (count == index) {
                    return pet;
                }
                count++;
            }
        }
        return null;
    }

    /**
     *
     * @param pet
     */
    public void addPet(final MaplePet pet) {
        if (pets.contains(pet)) {
            pets.remove(pet);
        }
        pets.add(pet);
        // So that the pet will be at the last
        // Pet index logic :(
    }

    /**
     *
     * @param pet
     */
    public void removePet(MaplePet pet) {
        pet.setSummoned(0);
        pets.remove(pet);
    }

    /**
     *
     * @return
     */
    public final List<MaplePet> getSummonedPets() {
        List<MaplePet> ret = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ret.add(null);
        }
        for (final MaplePet pet : pets) {
            if (pet != null && pet.getSummoned()) {
                int index = pet.getSummonedValue() - 1;
                ret.remove(index);
                ret.add(index, pet);
            }
        }
        List<Integer> nullArr = new ArrayList();
        nullArr.add(null);
        ret.removeAll(nullArr);
        return ret;
    }

    /**
     *
     * @param index
     * @return
     */
    public final MaplePet getSummonedPet(final int index) {
        for (final MaplePet pet : getSummonedPets()) {
            if (pet.getSummonedValue() - 1 == index) {
                return pet;
            }
        }
        return null;
    }

    /**
     *
     */
    public final void shiftPetsRight() {
        List<MaplePet> petsz = getSummonedPets();
        if (petsz.size() >= 3 || petsz.size() < 1) {
        } else {
            boolean[] indexBool = new boolean[]{false, false, false};
            for (int i = 0; i < 3; i++) {
                for (MaplePet p : petsz) {
                    if (p.getSummonedValue() == i + 1) {
                        indexBool[i] = true;
                    }
                }
            }
            if (petsz.size() > 1) {
                if (!indexBool[2]) {
                    petsz.get(0).setSummoned(2);
                    petsz.get(1).setSummoned(3);
                } else if (!indexBool[1]) {
                    petsz.get(0).setSummoned(2);
                }
            } else if (indexBool[0]) {
                petsz.get(0).setSummoned(2);
            }
        }
    }

    /**
     *
     * @return
     */
    public final int getPetSlotNext() {
        List<MaplePet> petsz = getSummonedPets();
        int index = 0;
        if (petsz.size() >= 3) {
            unequipPet(getSummonedPet(0), false);
        } else {
            boolean[] indexBool = new boolean[]{false, false, false};
            for (int i = 0; i < 3; i++) {
                for (MaplePet p : petsz) {
                    if (p.getSummonedValue() == i + 1) {
                        indexBool[i] = true;
                    }
                }
            }
            for (boolean b : indexBool) {
                if (!b) {
                    break;
                }
                index++;
            }
            index = Math.min(index, 2);
            for (MaplePet p : petsz) {
                if (p.getSummonedValue() == index + 1) {
                    unequipPet(p, false);
                }
            }
        }
        return index;
    }

    /**
     *
     * @param petz
     * @return
     */
    public final byte getPetIndex(final MaplePet petz) {
        return (byte) Math.max(-1, petz.getSummonedValue() - 1);
    }

    /**
     *
     * @param petId
     * @return
     */
    public final byte getPetIndex(final int petId) {
        for (final MaplePet pet : getSummonedPets()) {
            if (pet.getUniqueId() == petId) {
                return (byte) Math.max(-1, pet.getSummonedValue() - 1);
            }
        }
        return -1;
    }

    /**
     *
     * @param petId
     * @return
     */
    public final byte getPetIndexById(final int petId) {
        for (final MaplePet pet : getSummonedPets()) {
            if (pet.getPetItemId() == petId) {
                return (byte) Math.max(-1, pet.getSummonedValue() - 1);
            }
        }
        return -1;
    }

    /**
     *
     * @return
     */
    public final List<MaplePet> getPets() {
        return pets;
    }

    /**
     *
     */
    public final void unequipAllPets() {
        for (final MaplePet pet : getSummonedPets()) {
            unequipPet(pet, false);
        }
    }

    /**
     *
     * @param pet
     * @param hunger
     */
    public void unequipPet(MaplePet pet, boolean hunger) {
        if (pet.getSummoned()) {
            pet.saveToDb();

            List<MaplePet> summonedPets = getSummonedPets();
            if (summonedPets.contains(pet)) {
                summonedPets.remove(pet);
                int i = 1;
                for (MaplePet p : summonedPets) {
                    if (p == null) {
                        continue;
                    }
                    p.setSummoned(i);
                    i++;
                }
            }
            if (map != null) {
                map.broadcastMessage(this, PetPacket.showPet(this, pet, true, hunger), true);
            }
            //List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>();
            //stats.add(new Pair<MapleStat, Integer>(MapleStat.PET, Integer.valueOf(0)));
            pet.setSummoned(0);
            client.sendPacket(PetPacket.petStatUpdate(this));
            client.sendPacket(MaplePacketCreator.enableActions());
        }
    }

    /**
     *
     * @return
     */
    public final long getLastFameTime() {
        return lastfametime;
    }

    /**
     *
     * @return
     */
    public final List<Integer> getFamedCharacters() {
        return lastmonthfameids;
    }

    /**
     *
     * @param from
     * @return
     */
    public FameStatus canGiveFame(MapleCharacter from) {
        if (lastfametime >= System.currentTimeMillis() - 60 * 60 * 24 * 1_000) {
            return FameStatus.NOT_TODAY;
        } else if (from == null || lastmonthfameids == null || lastmonthfameids.contains(from.getId())) {
            return FameStatus.NOT_THIS_MONTH;
        }
        return FameStatus.OK;
    }

    /**
     *
     * @param to
     */
    public void hasGivenFame(MapleCharacter to) {
        lastfametime = System.currentTimeMillis();
        lastmonthfameids.add(to.getId());
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO famelog (characterid, characterid_to) VALUES (?, ?)")) {
            ps.setInt(1, getId());
            ps.setInt(2, to.getId());
            ps.execute();
        } catch (SQLException e) {
            System.err.println("ERROR writing famelog for char " + getName() + " to " + to.getName() + e);
        }
    }

    /**
     *
     * @return
     */
    public final MapleKeyLayout getKeyLayout() {
        return this.keylayout;
    }

    /**
     *
     * @return
     */
    public MapleParty getParty() {
        return party;
    }

    /**
     *
     * @return
     */
    public int getPartyId() {
        return (party != null ? party.getId() : -1);
    }

    /**
     *
     * @return
     */
    public byte getWorld() {
        return world;
    }

    /**
     *
     * @param world
     */
    public void setWorld(byte world) {
        this.world = world;
    }

    /**
     *
     * @param party
     */
    public void setParty(MapleParty party) {
        this.party = party;
    }

    /**
     *
     * @return
     */
    public MapleTrade getTrade() {
        return trade;
    }

    /**
     *
     * @param trade
     */
    public void setTrade(MapleTrade trade) {
        this.trade = trade;
    }

    /**
     *
     * @return
     */
    public EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    /**
     *
     * @param eventInstance
     */
    public void setEventInstance(EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    /**
     *
     * @param door
     */
    public void addDoor(MapleDoor door) {
        doors.add(door);
    }

    /**
     *
     */
    public void clearDoors() {
        doors.clear();
    }

    /**
     *
     * @return
     */
    public List<MapleDoor> getDoors() {
        return new ArrayList<>(doors);
    }

    /**
     *
     */
    public void setSmega() {
        if (smega) {
            smega = false;
            dropMessage(5, "You have set megaphone to disabled mode");
        } else {
            smega = true;
            dropMessage(5, "You have set megaphone to enabled mode");
        }
    }

    /**
     *
     * @return
     */
    public boolean getSmega() {
        return smega;
    }

    /**
     *
     * @return
     */
    public Map<Integer, MapleSummon> getSummons() {
        return summons;
    }

    /**
     *
     * @return
     */
    public int getChair() {
        return chair;
    }

    /**
     *
     * @return
     */
    public int getItemEffect() {
        return itemEffect;
    }

    /**
     *
     * @param chair
     */
    public void setChair(int chair) {
        this.chair = chair;
        stats.relocHeal();
    }

    /**
     *
     * @param itemEffect
     */
    public void setItemEffect(int itemEffect) {
        this.itemEffect = itemEffect;
    }

    /**
     *
     * @return
     */
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.PLAYER;
    }

    /**
     *
     * @return
     */
    public int getFamilyId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getFamilyId();
    }

    /**
     *
     * @return
     */
    public int getSeniorId() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getSeniorId();
    }

    /**
     *
     * @return
     */
    public int getJunior1() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior1();
    }

    /**
     *
     * @return
     */
    public int getJunior2() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getJunior2();
    }

    /**
     *
     * @return
     */
    public int getCurrentRep() {
        return currentrep;
    }

    /**
     *
     * @return
     */
    public int getTotalRep() {
        return totalrep;
    }

    /**
     *
     * @param _rank
     */
    public void setCurrentRep(int _rank) {
        currentrep = _rank;
        if (mfc != null) {
            mfc.setCurrentRep(_rank);
        }
    }

    /**
     *
     * @param _rank
     */
    public void setTotalRep(int _rank) {
        totalrep = _rank;
        if (mfc != null) {
            mfc.setTotalRep(_rank);
        }
    }

    /**
     *
     * @return
     */
    public int getGuildId() {
        return guildid;
    }

    /**
     *
     * @return
     */
    public byte getGuildRank() {
        return guildrank;
    }

    /**
     *
     * @param _id
     */
    public void setGuildId(int _id) {
        guildid = _id;
        if (guildid > 0) {
            if (mgc == null) {
                mgc = new MapleGuildCharacter(this);

            } else {
                mgc.setGuildId(guildid);
            }
        } else {
            mgc = null;
        }
    }

    /**
     *
     * @param _rank
     */
    public void setGuildRank(byte _rank) {
        guildrank = _rank;
        if (mgc != null) {
            mgc.setGuildRank(_rank);
        }
    }

    /**
     *
     * @return
     */
    public MapleGuildCharacter getMGC() {
        return mgc;
    }

    /**
     *
     * @param rank
     */
    public void setAllianceRank(byte rank) {
        allianceRank = rank;
        if (mgc != null) {
            mgc.setAllianceRank(rank);
        }
    }

    /**
     *
     * @return
     */
    public byte getAllianceRank() {
        return allianceRank;
    }

    /**
     *
     * @return
     */
    public MapleGuild getGuild() {
        if (getGuildId() <= 0) {
            return null;
        }
        return World.Guild.getGuild(getGuildId());
    }

    /**
     *
     */
    public void guildUpdate() {
        if (guildid <= 0) {
            return;
        }
        mgc.setLevel((short) level);
        mgc.setJobId(job);
        World.Guild.memberLevelJobUpdate(mgc);
    }

    /**
     *
     */
    public void saveGuildStatus() {
        MapleGuild.setOfflineGuildStatus(guildid, guildrank, allianceRank, id);
    }

    /**
     *
     */
    public void familyUpdate() {
        if (mfc == null) {
            return;
        }
        World.Family.memberFamilyUpdate(mfc, this);
    }

    /**
     *
     */
    public void saveFamilyStatus() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET familyid = ?, seniorid = ?, junior1 = ?, junior2 = ? WHERE id = ?")) {
                if (mfc == null) {
                    ps.setInt(1, 0);
                    ps.setInt(2, 0);
                    ps.setInt(3, 0);
                    ps.setInt(4, 0);
                } else {
                    ps.setInt(1, mfc.getFamilyId());
                    ps.setInt(2, mfc.getSeniorId());
                    ps.setInt(3, mfc.getJunior1());
                    ps.setInt(4, mfc.getJunior2());
                }
                ps.setInt(5, id);
                ps.execute();
            }
        } catch (SQLException se) {
            System.out.println("SQLException: " + se.getLocalizedMessage());
            se.printStackTrace();
        }
        //MapleFamily.setOfflineFamilyStatus(familyid, seniorid, junior1, junior2, currentrep, totalrep, id);
    }

    /**
     *
     * @param type
     * @param quantity
     */
    public void modifyCSPoints(int type, int quantity) {
        modifyCSPoints(type, quantity, false);
    }

    /**
     *
     * @param message
     */
    public void dropMessage(String message) {
        dropMessage(6, message);
    }

    // 1点券 2抵用券
    /**
     *
     * @param type
     * @param quantity
     * @param show
     */
    public void modifyCSPoints(int type, int quantity, boolean show) {

        switch (type) {
            case 1:
                if (acash + quantity < 0) {
                    if (show) {
                        dropMessage(5, "你的点卷已经满了");
                    }
                    return;
                }
                acash += quantity;
                break;
            case 2:
                if (maplepoints + quantity < 0) {
                    if (show) {
                        dropMessage(5, "你的抵用卷已经满了.");
                    }
                    return;
                }
                maplepoints += quantity;
                break;
            default:
                break;
        }
        if (show && quantity != 0) {
            dropMessage(5, "你已经 " + (quantity > 0 ? "获得 " : "使用 ") + quantity + (type == 1 ? " 点卷." : " 抵用卷."));
            //client.getSession().write(MaplePacketCreator.showSpecialEffect(19));
        }
    }

    /**
     *
     * @param type
     * @return
     */
    public int getCSPoints(int type) {
        switch (type) {
            case 1:
                return acash;
            case 2:
                return maplepoints;
            default:
                return 0;
        }
    }

    /**
     *
     * @param itemid
     * @return
     */
    public final boolean hasEquipped(int itemid) {
        return inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid) >= 1;
    }

    /**
     *
     * @param itemid
     * @param quantity
     * @param checkEquipped
     * @param greaterOrEquals
     * @return
     */
    public final boolean haveItem(int itemid, int quantity, boolean checkEquipped, boolean greaterOrEquals) {
        final MapleInventoryType type = GameConstants.getInventoryType(itemid);
        int possesed = inventory[type.ordinal()].countById(itemid);
        if (checkEquipped && type == MapleInventoryType.EQUIP) {
            possesed += inventory[MapleInventoryType.EQUIPPED.ordinal()].countById(itemid);
        }
        if (greaterOrEquals) {
            return possesed >= quantity;
        } else {
            return possesed == quantity;
        }
    }

    /**
     *
     * @param itemid
     * @param quantity
     * @return
     */
    public final boolean haveItem(int itemid, int quantity) {
        return haveItem(itemid, quantity, true, true);
    }

    /**
     *
     * @param itemid
     * @return
     */
    public final boolean haveItem(int itemid) {
        return haveItem(itemid, 1, true, true);
    }

    /**
     *
     */
    public void maxAllSkills() {
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("wz") + "/" + "String.wz"));
        MapleData skilldData = dataProvider.getData("Skill.img");
        for (MapleData skill_ : skilldData.getChildren()) {
            try {
                Skill skill = (Skill) SkillFactory.getSkill1(Integer.parseInt(skill_.getName()));
                if (level >= 0) {
                    changeSkillLevel(skill, skill.getMaxLevel(), (byte) skill.getMaxLevel());
                }
            } catch (NumberFormatException nfe) {
                break;
            } catch (NullPointerException npe) {
            }
        }
    }

    /**
     *
     * @param score
     */
    public void setAPQScore(int score) {
        this.APQScore = score;
    }

    /**
     *
     * @return
     */
    public int getAPQScore() {
        return APQScore;
    }

    /**
     *
     * @return
     */
    public long getLasttime() {
        return this.lasttime;
    }

    /**
     *
     * @param lasttime
     */
    public void setLasttime(long lasttime) {
        this.lasttime = lasttime;
    }

    /**
     *
     * @return
     */
    public long getCurrenttime() {
        return this.currenttime;
    }

    /**
     *
     * @param currenttime
     */
    public void setCurrenttime(long currenttime) {
        this.currenttime = currenttime;
    }

    /**
     *
     */
    public static enum FameStatus {

        /**
         *
         */
        OK,
        /**
         *
         */
        NOT_TODAY,
        /**
         *
         */
        NOT_THIS_MONTH
    }

    /**
     *
     * @return
     */
    public byte getBuddyCapacity() {
        return buddylist.getCapacity();
    }

    /**
     *
     * @param capacity
     */
    public void setBuddyCapacity(byte capacity) {
        buddylist.setCapacity(capacity);
        client.getSession().write(MaplePacketCreator.updateBuddyCapacity(capacity));
    }

    /**
     *
     * @return
     */
    public MapleMessenger getMessenger() {
        return messenger;
    }

    /**
     *
     * @param messenger
     */
    public void setMessenger(MapleMessenger messenger) {
        this.messenger = messenger;
    }

    /**
     *
     * @param skillId
     * @param startTime
     * @param length
     */
    public void addCooldown(int skillId, long startTime, long length) {
        coolDowns.put(skillId, new MapleCoolDownValueHolder(skillId, startTime, length));
    }

    /**
     *
     * @param skillId
     */
    public void removeCooldown(int skillId) {
        if (coolDowns.containsKey(skillId)) {
            coolDowns.remove(skillId);
        }
    }

    /**
     *
     * @param skillId
     * @return
     */
    public boolean skillisCooling(int skillId) {
        return coolDowns.containsKey(skillId);
    }

    /**
     *
     * @param skillid
     * @param starttime
     * @param length
     */
    public void giveCoolDowns(final int skillid, long starttime, long length) {
        addCooldown(skillid, starttime, length);
    }

    /**
     *
     * @param cooldowns
     */
    public void giveCoolDowns(final List<MapleCoolDownValueHolder> cooldowns) {
        int time;
        if (cooldowns != null) {
            for (MapleCoolDownValueHolder cooldown : cooldowns) {
                coolDowns.put(cooldown.skillId, cooldown);
            }
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                ResultSet rs;
                try (PreparedStatement ps = con.prepareStatement("SELECT SkillID,StartTime,length FROM skills_cooldowns WHERE charid = ?")) {
                    ps.setInt(1, getId());
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        if (rs.getLong("length") + rs.getLong("StartTime") - System.currentTimeMillis() <= 0) {
                            continue;
                        }
                        giveCoolDowns(rs.getInt("SkillID"), rs.getLong("StartTime"), rs.getLong("length"));
                    }
                }
                rs.close();
                deleteWhereCharacterId(con, "DELETE FROM skills_cooldowns WHERE charid = ?");

            } catch (SQLException e) {
                System.err.println("Error while retriving cooldown from SQL storage");
            }
        }
    }

    /**
     *
     * @return
     */
    public List<MapleCoolDownValueHolder> getCooldowns() {
        return new ArrayList<>(coolDowns.values());
    }

    /**
     *
     * @return
     */
    public final List<MapleDiseaseValueHolder> getAllDiseases() {
        return new ArrayList<>(diseases.values());
    }

    /**
     *
     * @param dis
     * @return
     */
    public final boolean hasDisease(final MapleDisease dis) {
        return diseases.keySet().contains(dis);
    }

    /**
     *
     * @param disease
     * @param skill
     */
    public void giveDebuff(final MapleDisease disease, MobSkill skill) {
        giveDebuff(disease, skill.getX(), skill.getDuration(), skill.getSkillId(), skill.getSkillLevel());
    }

    /**
     *
     * @param disease
     * @param x
     * @param duration
     * @param skillid
     * @param level
     */
    public void giveDebuff(final MapleDisease disease, int x, long duration, int skillid, int level) {
        final List<Pair<MapleDisease, Integer>> debuff = Collections.singletonList(new Pair<>(disease, Integer.valueOf(x)));

        if (!hasDisease(disease) && diseases.size() < 2) {
            if (!(disease == MapleDisease.SEDUCE || disease == MapleDisease.STUN)) {
                if (isActiveBuffedValue(2_321_005)) {
                    return;
                }
            }

            diseases.put(disease, new MapleDiseaseValueHolder(disease, System.currentTimeMillis(), duration));
            client.getSession().write(MaplePacketCreator.giveDebuff(debuff, skillid, level, (int) duration));
            map.broadcastMessage(this, MaplePacketCreator.giveForeignDebuff(id, debuff, skillid, level), false);
        }
    }

    /**
     *
     * @param ld
     */
    public final void giveSilentDebuff(final List<MapleDiseaseValueHolder> ld) {
        if (ld != null) {
            for (final MapleDiseaseValueHolder disease : ld) {
                diseases.put(disease.disease, disease);
            }
        }
    }

    /**
     *
     * @param debuff
     */
    public void dispelDebuff(MapleDisease debuff) {
        if (hasDisease(debuff)) {
            long mask = debuff.getValue();
            boolean first = debuff.isFirst();
            client.getSession().write(MaplePacketCreator.cancelDebuff(mask, first));
            map.broadcastMessage(this, MaplePacketCreator.cancelForeignDebuff(id, mask, first), false);

            diseases.remove(debuff);
        }
    }

    /**
     *
     */
    public void dispelDebuffs() {
        dispelDebuff(MapleDisease.CURSE);
        dispelDebuff(MapleDisease.DARKNESS);
        dispelDebuff(MapleDisease.POISON);
        dispelDebuff(MapleDisease.SEAL);
        dispelDebuff(MapleDisease.WEAKEN);
    }

    /**
     *
     */
    public void cancelAllDebuffs() {
        diseases.clear();
    }

    /**
     *
     * @param level
     */
    public void setLevel(final short level) {
        this.level = (short) (level - 1);
    }

    /**
     *
     * @param to
     * @param msg
     */
    public void sendNote(String to, String msg) {
        sendNote(to, msg, 0);
    }

    /**
     *
     * @param to
     * @param msg
     * @param fame
     */
    public void sendNote(String to, String msg, int fame) {
        MapleCharacterUtil.sendNote(to, getName(), msg, fame);
    }

    /**
     *
     */
    public void showNote() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM notes WHERE `to`=?", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                ps.setString(1, getName());
                ResultSet rs = ps.executeQuery();
                rs.last();
                int count = rs.getRow();
                rs.first();
                client.getSession().write(MTSCSPacket.showNotes(rs, count));
                rs.close();
            }
        } catch (SQLException e) {
            System.err.println("Unable to show note" + e);
        }
    }

    /**
     *
     * @param id
     * @param fame
     */
    public void deleteNote(int id, int fame) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT gift FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("gift") == fame && fame > 0) { //not exploited! hurray
                    addFame(fame);
                    updateSingleStat(MapleStat.FAME, getFame());
                    client.getSession().write(MaplePacketCreator.getShowFameGain(fame));
                }
            }
            rs.close();
            ps.close();
            ps = con.prepareStatement("DELETE FROM notes WHERE `id`=?");
            ps.setInt(1, id);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Unable to delete note" + e);
        }
    }

    /**
     *
     * @param inc
     */
    public void mulung_EnergyModify(boolean inc) {
        if (inc) {
            if (mulung_energy + 100 > 10_000) {
                mulung_energy = 10_000;
            } else {
                mulung_energy += 100;
            }
        } else {
            mulung_energy = 0;
        }
        //    client.getSession().write(MaplePacketCreator.MulungEnergy(mulung_energy));
    }

    /**
     *
     */
    public void writeMulungEnergy() {
        //  client.getSession().write(MaplePacketCreator.MulungEnergy(mulung_energy));
    }

    /**
     *
     * @param type
     * @param inc
     */
    public void writeEnergy(String type, String inc) {
        // client.getSession().write(MaplePacketCreator.sendPyramidEnergy(type, inc));
    }

    /**
     *
     * @param type
     * @param inc
     */
    public void writeStatus(String type, String inc) {
        //  client.getSession().write(MaplePacketCreator.sendGhostStatus(type, inc));
    }

    /**
     *
     * @param type
     * @param inc
     */
    public void writePoint(String type, String inc) {
        //  client.getSession().write(MaplePacketCreator.sendGhostPoint(type, inc));
    }

    /**
     *
     * @return
     */
    public final short getCombo() {
        return combo;
    }

    /**
     *
     * @param combo
     */
    public void setCombo(final short combo) {
        this.combo = combo;
    }

    /**
     *
     * @return
     */
    public final long getLastCombo() {
        return lastCombo;
    }

    /**
     *
     * @param combo
     */
    public void setLastCombo(final long combo) {
        this.lastCombo = combo;
    }

    /**
     *
     * @return
     */
    public final long getKeyDownSkill_Time() {
        return keydown_skill;
    }

    /**
     *
     * @param keydown_skill
     */
    public void setKeyDownSkill_Time(final long keydown_skill) {
        this.keydown_skill = keydown_skill;
    }

    /**
     *
     */
    public void checkBerserk() {
        if (BerserkSchedule != null) {
            BerserkSchedule.cancel(false);
            BerserkSchedule = null;
        }

        final ISkill BerserkX = SkillFactory.getSkill(1_320_006);
        final int skilllevel = getSkillLevel(BerserkX);
        if (skilllevel >= 1) {
            final MapleStatEffect ampStat = BerserkX.getEffect(skilllevel);
            stats.Berserk = stats.getHp() * 100 / stats.getMaxHp() <= ampStat.getX();
            client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1_320_006, 1, (byte) (stats.Berserk ? 1 : 0)));
            map.broadcastMessage(this, MaplePacketCreator.showBuffeffect(getId(), 1_320_006, 1, (byte) (stats.Berserk ? 1 : 0)), false);

            BerserkSchedule = BuffTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    checkBerserk();
                }
            }, 10_000);
        }
    }

    private void prepareBeholderEffect() {
        if (beholderHealingSchedule != null) {
            beholderHealingSchedule.cancel(false);
        }
        if (beholderBuffSchedule != null) {
            beholderBuffSchedule.cancel(false);
        }
        ISkill bHealing = SkillFactory.getSkill(1_320_008);
        final int bHealingLvl = getSkillLevel(bHealing);
        final int berserkLvl = getSkillLevel(SkillFactory.getSkill(1_320_006));

        if (bHealingLvl > 0) {
            final MapleStatEffect healEffect = bHealing.getEffect(bHealingLvl);
            int healInterval = healEffect.getX() * 1_000;
            beholderHealingSchedule = BuffTimer.getInstance().register(new Runnable() {

                @Override
                public void run() {
                    int remhppercentage = (int) Math.ceil((getStat().getHp() * 100.0) / getStat().getMaxHp());
                    if (berserkLvl == 0 || remhppercentage >= berserkLvl + 10) {
                        addHP(healEffect.getHp());
                    }
                    client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1_321_007, 2));
                    map.broadcastMessage(MaplePacketCreator.summonSkill(getId(), 1_321_007, 5));
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), 1_321_007, 2), false);
                }
            }, healInterval, healInterval);
        }
        ISkill bBuff = SkillFactory.getSkill(1_320_009);
        final int bBuffLvl = getSkillLevel(bBuff);
        if (bBuffLvl > 0) {
            final MapleStatEffect buffEffect = bBuff.getEffect(bBuffLvl);
            int buffInterval = buffEffect.getX() * 1_000;
            beholderBuffSchedule = BuffTimer.getInstance().register(new Runnable() {

                @Override
                public void run() {
                    buffEffect.applyTo(MapleCharacter.this);
                    client.getSession().write(MaplePacketCreator.showOwnBuffEffect(1_321_007, 2));
                    map.broadcastMessage(MaplePacketCreator.summonSkill(getId(), 1_321_007, Randomizer.nextInt(3) + 6));
                    map.broadcastMessage(MapleCharacter.this, MaplePacketCreator.showBuffeffect(getId(), 1_321_007, 2), false);
                }
            }, buffInterval, buffInterval);
        }
    }

    /**
     *
     * @param text
     */
    public void setChalkboard(String text) {
        this.chalktext = text;
        map.broadcastMessage(MTSCSPacket.useChalkboard(getId(), text));
    }

    /**
     *
     * @return
     */
    public String getChalkboard() {
        return chalktext;
    }

    /**
     *
     * @return
     */
    public MapleMount getMount() {
        return mount;
    }

    /**
     *
     * @return
     */
    public int[] getWishlist() {
        return wishlist;
    }

    /**
     *
     */
    public void clearWishlist() {
        for (int i = 0; i < 10; i++) {
            wishlist[i] = 0;
        }
    }

    /**
     *
     * @return
     */
    public int getWishlistSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (wishlist[i] > 0) {
                ret++;
            }
        }
        return ret;
    }

    /**
     *
     * @param wl
     */
    public void setWishlist(int[] wl) {
        this.wishlist = wl;
    }

    /**
     *
     * @return
     */
    public int[] getRocks() {
        return rocks;
    }

    /**
     *
     * @return
     */
    public int getRockSize() {
        int ret = 0;
        for (int i = 0; i < 10; i++) {
            if (rocks[i] != 999_999_999) {
                ret++;
            }
        }
        return ret;
    }

    /**
     *
     * @param map
     */
    public void deleteFromRocks(int map) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == map) {
                rocks[i] = 999_999_999;
                break;
            }
        }
    }

    /**
     *
     */
    public void addRockMap() {
        if (getRockSize() >= 10) {
            return;
        }
        rocks[getRockSize()] = getMapId();
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean isRockMap(int id) {
        for (int i = 0; i < 10; i++) {
            if (rocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public int[] getRegRocks() {
        return regrocks;
    }

    /**
     *
     * @return
     */
    public int getRegRockSize() {
        int ret = 0;
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] != 999_999_999) {
                ret++;
            }
        }
        return ret;
    }

    /**
     *
     * @param map
     */
    public void deleteFromRegRocks(int map) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == map) {
                regrocks[i] = 999_999_999;
                break;
            }
        }
    }

    /**
     *
     */
    public void addRegRockMap() {
        if (getRegRockSize() >= 5) {
            return;
        }
        regrocks[getRegRockSize()] = getMapId();
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean isRegRockMap(int id) {
        for (int i = 0; i < 5; i++) {
            if (regrocks[i] == id) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public List<LifeMovementFragment> getLastRes() {
        return lastres;
    }

    /**
     *
     * @param lastres
     */
    public void setLastRes(List<LifeMovementFragment> lastres) {
        this.lastres = lastres;
    }

    /**
     *
     * @param bookCover
     */
    public void setMonsterBookCover(int bookCover) {
        this.bookCover = bookCover;
    }

    /**
     *
     * @return
     */
    public int getMonsterBookCover() {
        return bookCover;
    }

    /**
     *
     * @param bossid
     * @return
     */
    public int getOneTimeLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con1.prepareStatement("select count(*) from onetimelog where characterid = ? and log = ?");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret_count = rs.getInt(1);
                } else {
                    ret_count = -1;
                }
            }
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
            //log.error("Error while read bosslog.", Ex);
            return -1;
        }
    }

    /**
     *
     * @param bossid
     */
    public void setOneTimeLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con1.prepareStatement("insert into onetimelog (characterid, log) values (?,?)");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            //   log.error("Error while insert bosslog.", Ex);
        }
    }

    /* public int getBossLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con1.prepareStatement("select count(*) from bosslog where characterid = ? and bossid = ? and lastattempt >= subtime(current_timestamp, '1 0:0:0.0')");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            } else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
            //log.error("Error while read bosslog.", Ex);
            return -1;
        }
    }

    public void setBossLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con1.prepareStatement("insert into bosslog (characterid, bossid) values (?,?)");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            //   log.error("Error while insert bosslog.", Ex);
        }
    }
     */
    /**
     *
     * @param boss
     * @return
     */
    public int getBossLog(String boss) {
        return getBossLog(boss, 0);
    }

    /**
     *
     * @param boss
     * @param type
     * @return
     */
    public int getBossLog(String boss, int type) {
        try {
            int count = 0;

            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT * FROM bosslog WHERE characterid = ? AND bossid = ?");
            ps.setInt(1, this.id);
            ps.setString(2, boss);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                count = rs.getInt("count");
                Timestamp bossTime = rs.getTimestamp("time");
                rs.close();
                ps.close();
                if (type == 0) {
                    if (bossTime != null) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(bossTime.getTime());
                        if ((cal.get(5) + 1 <= Calendar.getInstance().get(5)) || (cal.get(2) + 1 <= Calendar.getInstance().get(2))) {
                            count = 0;
                            ps = con.prepareStatement("UPDATE bosslog SET count = 0  WHERE characterid = ? AND bossid = ?");
                            ps.setInt(1, this.id);
                            ps.setString(2, boss);
                            ps.executeUpdate();
                        }
                    }
                    rs.close();
                    ps.close();
                    ps = con.prepareStatement("UPDATE bosslog SET time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?");
                    ps.setInt(1, this.id);
                    ps.setString(2, boss);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement psu = con.prepareStatement("INSERT INTO bosslog (characterid, bossid, count, type) VALUES (?, ?, ?, ?)")) {
                    psu.setInt(1, this.id);
                    psu.setString(2, boss);
                    psu.setInt(3, 0);
                    psu.setInt(4, type);
                    psu.executeUpdate();
                }
            }
            rs.close();
            ps.close();
            return count;
        } catch (Exception Ex) {
            // log.error("Error while read bosslog.", Ex);
        }
        return -1;
    }

    /*  public int getBossLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con1.prepareStatement("select count(*) from bosslog where characterid = ? and bossid = ? and lastattempt >= subtime(current_timestamp, '1 0:0:0.0')");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ret_count = rs.getInt(1);
            } else {
                ret_count = -1;
            }
            rs.close();
            ps.close();
            return ret_count;
        } catch (Exception Ex) {
            //log.error("Error while read bosslog.", Ex);
            return -1;
        }
    }

    public void setBossLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con1.prepareStatement("insert into bosslog (characterid, bossid) values (?,?)");
            ps.setInt(1, id);
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            //   log.error("Error while insert bosslog.", Ex);
        }
    }
     */
    /**
     *
     * @param boss
     */
    public void setBossLog(String boss) {
        setBossLog(boss, 0);
    }

    /**
     *
     * @param boss
     * @param type
     */
    public void setBossLog(String boss, int type) {
        setBossLog(boss, type, 1);
    }

    /**
     *
     * @param boss
     * @param type
     * @param count
     */
    public void setBossLog(String boss, int type, int count) {
        int bossCount = getBossLog(boss, type);
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE bosslog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?")) {
                ps.setInt(1, bossCount + count);
                ps.setInt(2, type);
                ps.setInt(3, this.id);
                ps.setString(4, boss);
                ps.executeUpdate();
            }
        } catch (Exception Ex) {
            // log.error("Error while set bosslog.", Ex);
        }
    }

    /**
     *
     * @param boss
     */
    public void resetBossLog(String boss) {
        resetBossLog(boss, 0);
    }

    /**
     *
     * @param boss
     * @param type
     */
    public void resetBossLog(String boss, int type) {
        try {
            Connection con = DatabaseConnection.getConnection();

            try (PreparedStatement ps = con.prepareStatement("UPDATE bosslog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND bossid = ?")) {
                ps.setInt(1, 0);
                ps.setInt(2, type);
                ps.setInt(3, this.id);
                ps.setString(4, boss);
                ps.executeUpdate();
            }
        } catch (Exception Ex) {
            // log.error("Error while reset bosslog.", Ex);
        }
    }

    /**
     *
     * @param bossid
     */
    public void setPrizeLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con1.prepareStatement("insert into Prizelog (accid, bossid) values (?,?)");
            ps.setInt(1, getClient().getAccID());
            ps.setString(2, bossid);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Wx) {
        }
    }

    /**
     *
     * @param bossid
     * @return
     */
    public int getPrizeLog(String bossid) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            int ret_count = 0;
            PreparedStatement ps;
            ps = con1.prepareStatement("select count(*) from Prizelog where accid = ? and bossid = ?");
            ps.setInt(1, getClient().getAccID());
            ps.setString(2, bossid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ret_count = rs.getInt(1);
                } else {
                    ret_count = -1;
                }
            }
            ps.close();
            return ret_count;
        } catch (Exception Wx) {
            return -1;
        }
    }

    /**
     *
     * @param pqName
     * @return
     */
    public int getPQLog(String pqName) {
        return this.getPQLog(pqName, 0);
    }

    /**
     *
     * @param pqName
     * @param times
     * @return
     */
    public int getPQLog(String pqName, int times) {
        return this.getPQLog(pqName, times, 1);
    }

    /**
     *
     * @param pqName
     * @param times
     * @return
     */
    public int getDaysPQLog(String pqName, int times) {
        return this.getDaysPQLog(pqName, 0, times);
    }

    /**
     *
     * @param pqName
     * @param times
     * @param day
     * @return
     */
    public int getDaysPQLog(String pqName, int times, int day) {
        return this.getPQLog(pqName, times, day);
    }

    /**
     *
     * @param pqName
     * @param times
     * @param day
     * @return
     */
    public int getPQLog(String pqName, int times, int day) {
        try (Connection con1 = DatabaseConnection.getConnection();) {
            int n4 = 0;
            try (PreparedStatement ps = con1.prepareStatement("SELECT `count`,`time` FROM pqlog WHERE characterid = ? AND pqname = ?")) {
                ps.setInt(1, this.id);
                ps.setString(2, pqName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        n4 = rs.getInt("count");
                        Timestamp timestamp = rs.getTimestamp("time");
                        rs.close();
                        ps.close();
                        if (times == 0) {
                            int n5;
                            Calendar calendar = Calendar.getInstance();
                            Calendar calendar2 = Calendar.getInstance();
                            if (timestamp != null) {
                                calendar2.setTimeInMillis(timestamp.getTime());
                                calendar2.add(6, day);
                            }
                            if (calendar.get(1) - calendar2.get(1) > 1) {
                                n5 = 0;
                            } else if (calendar.get(1) - calendar2.get(1) >= 0) {
                                if (calendar.get(1) - calendar2.get(1) > 0) {
                                    calendar2.add(1, 1);
                                }
                                n5 = calendar.get(6) - calendar2.get(6);
                            } else {
                                n5 = -1;
                            }
                            if (n5 >= 0) {
                                n4 = 0;
                                try (PreparedStatement psi = con1.prepareStatement("UPDATE pqlog SET count = 0, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND pqname = ?")) {
                                    psi.setInt(1, this.id);
                                    psi.setString(2, pqName);
                                    psi.executeUpdate();
                                }
                            }
                        }
                    } else {
                        try (PreparedStatement pss = con1.prepareStatement("INSERT INTO pqlog (characterid, pqname, count, type) VALUES (?, ?, ?, ?)")) {
                            pss.setInt(1, this.id);
                            pss.setString(2, pqName);
                            pss.setInt(3, 0);
                            pss.setInt(4, times);
                            pss.executeUpdate();
                        }
                    }
                }
            }
            return n4;
        } catch (SQLException sQLException) {
            System.err.println("Error while get pqlog: " + sQLException);
            return -1;
        }
    }

    /**
     *
     * @param pqname
     */
    public void setPQLog(String pqname) {
        this.setPQLog(pqname, 0);
    }

    /**
     *
     * @param pqname
     * @param type
     */
    public void setPQLog(String pqname, int type) {
        this.setPQLog(pqname, type, 1);
    }

    /**
     *
     * @param pqname
     * @param type
     * @param count
     */
    public void setPQLog(String pqname, int type, int count) {
        int times = this.getPQLog(pqname, type);
        try (Connection con1 = DatabaseConnection.getConnection();) {
            try (PreparedStatement ps = con1.prepareStatement("UPDATE pqlog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND pqname = ?")) {
                ps.setInt(1, times + count);
                ps.setInt(2, type);
                ps.setInt(3, this.id);
                ps.setString(4, pqname);
                ps.executeUpdate();
            }
        } catch (SQLException sQLException) {
            System.err.println("Error while set pqlog: " + sQLException);
        }
    }

    /**
     *
     * @param pqname
     */
    public void resetPQLog(String pqname) {
        this.resetPQLog(pqname, 0);
    }

    /**
     *
     * @param pqname
     * @param type
     */
    public void resetPQLog(String pqname, int type) {
        try (Connection con1 = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con1.prepareStatement("UPDATE pqlog SET count = ?, type = ?, time = CURRENT_TIMESTAMP() WHERE characterid = ? AND pqname = ?")) {
                ps.setInt(1, 0);
                ps.setInt(2, type);
                ps.setInt(3, this.id);
                ps.setString(4, pqname);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error while reset pqlog: " + e);
        }
    }

    /**
     *
     * @return
     */
    public long getPQPoint() {
        return Long.valueOf(this.getOneInfo(7_907, "point") != null ? this.getOneInfo(7_907, "point") : "0");
    }

    /**
     *
     * @param point
     */
    public void gainPQPoint(long point) {
        this.updateOneInfo(7_907, "point", String.valueOf(this.getPQPoint() + point));
    }

    /**
     *
     * @param type
     * @param message
     */
    public void dropMessage(int type, String message) {
        /*
         * if (type == -1) {
         * client.getSession().write(UIPacket.getTopMsg(message)); } else
         */ if (type == -2) {
            client.getSession().write(PlayerShopPacket.shopChat(message, 0)); //0 or what
        } else {
            client.getSession().write(MaplePacketCreator.serverNotice(type, message));
        }
    }

    /**
     *
     * @return
     */
    public IMaplePlayerShop getPlayerShop() {
        return playerShop;
    }

    /**
     *
     * @param playerShop
     */
    public void setPlayerShop(IMaplePlayerShop playerShop) {
        this.playerShop = playerShop;
    }

    /**
     *
     * @return
     */
    public int getConversation() {
        return inst.get();
    }

    /**
     *
     * @param inst
     */
    public void setConversation(int inst) {
        this.inst.set(inst);
    }

    /**
     *
     * @return
     */
    public MapleCarnivalParty getCarnivalParty() {
        return carnivalParty;
    }

    /**
     *
     * @param party
     */
    public void setCarnivalParty(MapleCarnivalParty party) {
        carnivalParty = party;
    }

    /**
     *
     * @param ammount
     */
    public void addCP(int ammount) {
        totalCP += ammount;
        availableCP += ammount;
    }

    /**
     *
     * @param ammount
     */
    public void useCP(int ammount) {
        availableCP -= ammount;
    }

    /**
     *
     * @return
     */
    public int getAvailableCP() {
        return availableCP;
    }

    /**
     *
     * @return
     */
    public int getTotalCP() {
        return totalCP;
    }

    /**
     *
     */
    public void resetCP() {
        totalCP = 0;
        availableCP = 0;
    }

    /**
     *
     * @param request
     */
    public void addCarnivalRequest(MapleCarnivalChallenge request) {
        pendingCarnivalRequests.add(request);
    }

    /**
     *
     * @return
     */
    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return pendingCarnivalRequests.pollLast();
    }

    /**
     *
     */
    public void clearCarnivalRequests() {
        pendingCarnivalRequests = new LinkedList<>();
    }

    /**
     *
     * @param enemyavailable
     * @param enemytotal
     */
    public void startMonsterCarnival(final int enemyavailable, final int enemytotal) {
        client.getSession().write(MonsterCarnivalPacket.startMonsterCarnival(this, enemyavailable, enemytotal));
    }

    /**
     *
     * @param party
     * @param available
     * @param total
     * @param team
     */
    public void CPUpdate(final boolean party, final int available, final int total, final int team) {
        client.getSession().write(MonsterCarnivalPacket.CPUpdate(party, available, total, team));
    }

    /**
     *
     * @param name
     * @param lostCP
     * @param team
     */
    public void playerDiedCPQ(final String name, final int lostCP, final int team) {
        client.getSession().write(MonsterCarnivalPacket.playerDiedMessage(name, lostCP, team));
    }

    /*
     * public void setAchievementFinished(int id) { if
     * (!finishedAchievements.contains(id)) { finishedAchievements.add(id); } }
     *
     * public boolean achievementFinished(int achievementid) { return
     * finishedAchievements.contains(achievementid); }
     *
     * public void finishAchievement(int id) { if (!achievementFinished(id)) {
     * if (isAlive() && !isClone()) {
     * MapleAchievements.getInstance().getById(id).finishAchievement(this); } }
     * }
     *
     * public List<Integer> getFinishedAchievements() { return
     * finishedAchievements; }
     *
     * public void modifyAchievementCSPoints(int type, int quantity) { switch
     * (type) { case 1: acash += quantity; break; case 2: maplepoints +=
     * quantity; break; } }
     */
    /**
     *
     * @return
     */
    public boolean getCanTalk() {
        return this.canTalk;
    }

    /**
     *
     * @param talk
     */
    public void canTalk(boolean talk) {
        this.canTalk = talk;
    }

    /**
     *
     * @return
     */
    public int getHp() {
        return stats.hp;
    }

    /**
     *
     * @param hp
     */
    public void setHp(int hp) {
        stats.setHp(hp);
    }

    /**
     *
     * @return
     */
    public int getMp() {
        return stats.mp;
    }

    /**
     *
     * @param mp
     */
    public void setMp(int mp) {
        stats.setMp(mp);
    }

    /**
     *
     * @return
     */
    public int getStr() {
        return stats.str;
    }

    /**
     *
     * @return
     */
    public int getDex() {
        return stats.dex;
    }

    /**
     *
     * @return
     */
    public int getLuk() {
        return stats.luk;
    }

    /**
     *
     * @return
     */
    public int getInt() {
        return stats.int_;
    }

    /**
     *
     * @return
     */
    public int getEXPMod() {
        return stats.expMod;
    }

    /**
     *
     * @return
     */
    public int getDropMod() {
        return stats.dropMod;
    }

    /**
     *
     * @return
     */
    public int getCashMod() {
        return stats.cashMod;
    }

    /**
     *
     * @param p
     */
    public void setPoints(int p) {
        this.points = p;
        /*
         * if (this.points >= 1) { finishAchievement(1); }
         */
    }

    /**
     *
     * @return
     */
    public int getPoints() {
        return points;
    }

    /**
     *
     * @param p
     */
    public void setVPoints(int p) {
        this.vpoints = p;
    }

    /**
     *
     * @return
     */
    public int getVPoints() {
        return vpoints;
    }

    /**
     *
     * @return
     */
    public CashShop getCashInventory() {
        return cs;
    }

    /**
     *
     * @param id
     */
    public void removeAll(int id) {
        removeAll(id, true, false);
    }

    /**
     *
     * @param id
     * @param show
     * @param checkEquipped
     */
    public void removeAll(int id, boolean show, boolean checkEquipped) {
        MapleInventoryType type = GameConstants.getInventoryType(id);
        int possessed = getInventory(type).countById(id);

        if (possessed > 0) {
            MapleInventoryManipulator.removeById(getClient(), type, id, possessed, true, false);
            if (show) {
                getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, (short) -possessed, true));
            }
        }
        if ((checkEquipped) && (type == MapleInventoryType.EQUIP)) {
            type = MapleInventoryType.EQUIPPED;
            possessed = getInventory(type).countById(id);
            if (possessed > 0) {
                MapleInventoryManipulator.removeById(getClient(), type, id, possessed, true, false);
                if (show) {
                    getClient().getSession().write(MaplePacketCreator.getShowItemGain(id, (short) (-possessed), true));
                }
                equipChanged();
            }
        }
        /*
         * if (type == MapleInventoryType.EQUIP) { //check equipped type =
         * MapleInventoryType.EQUIPPED; possessed =
         * getInventory(type).countById(id);
         *
         * if (possessed > 0) {
         * MapleInventoryManipulator.removeById(getClient(), type, id,
         * possessed, true, false);
         * getClient().getSession().write(MaplePacketCreator.getShowItemGain(id,
         * (short)-possessed, true)); } }
         */
    }

    //TODO: more than one crush/friendship ring at a time
    /**
     *
     * @param equip
     * @return
     */
    public Pair<List<MapleRing>, List<MapleRing>> getRings(boolean equip) {
        MapleInventory iv = getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<>(equippedC.size());
        for (IItem item : equippedC) {
            equipped.add((Item) item);
        }
        Collections.sort(equipped);
        List<MapleRing> crings = new ArrayList<>();
        List<MapleRing> frings = new ArrayList<>();
        MapleRing ring;
        for (Item item : equipped) {
            if (item.getRing() != null) {
                ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isFriendshipRing(item.getItemId()) || GameConstants.isCrushRing(item.getItemId())) {
                    if (equip) {
                        if (GameConstants.isCrushRing(item.getItemId())) {
                            crings.add(ring);
                        } else if (GameConstants.isFriendshipRing(item.getItemId())) {
                            frings.add(ring);
                        }
                    } else if (crings.isEmpty() && GameConstants.isCrushRing(item.getItemId())) {
                        crings.add(ring);
                    } else if (frings.isEmpty() && GameConstants.isFriendshipRing(item.getItemId())) {
                        frings.add(ring);
                    } //for 3rd person the actual slot doesnt matter, so we'll use this to have both shirt/ring same?
                    //however there seems to be something else behind this, will have to sniff someone with shirt and ring, or more conveniently 3-4 of those
                }
            }
        }
        if (equip) {
            iv = getInventory(MapleInventoryType.EQUIP);
            for (IItem item : iv.list()) {
                if (item.getRing() != null && GameConstants.isEffectRing(item.getItemId())) {
                    ring = item.getRing();
                    ring.setEquipped(false);
                    if (GameConstants.isFriendshipRing(item.getItemId())) {
                        frings.add(ring);
                    } else if (GameConstants.isCrushRing(item.getItemId())) {
                        crings.add(ring);
                    }
                }
            }
        }
        Collections.sort(frings, new MapleRing.RingComparator());
        Collections.sort(crings, new MapleRing.RingComparator());
        return new Pair<>(crings, frings);
    }

    /**
     *
     * @return
     */
    public int getFH() {
        MapleFoothold fh = getMap().getFootholds().findBelow(getPosition());
        if (fh != null) {
            return fh.getId();
        }
        return 0;
    }

    /**
     *
     * @param exp
     */
    public void startFairySchedule(boolean exp) {
        startFairySchedule(exp, false);
    }

    /**
     *
     * @param exp
     * @param equipped
     */
    public void startFairySchedule(boolean exp, boolean equipped) {
        cancelFairySchedule(exp);
        if (fairyExp < 50 && stats.equippedFairy) {
            if (equipped) {
                dropMessage(5, "The Fairy Pendant's experience points will increase to " + (fairyExp) + "% after one hour.");
            }
            fairySchedule = EtcTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    if (fairyExp < 50 && stats.equippedFairy) {
                        fairyExp = 50;
                        dropMessage(5, "The Fairy Pendant's EXP was boosted to " + fairyExp + "%.");
                        startFairySchedule(false, true);
                    } else {
                        cancelFairySchedule(!stats.equippedFairy);
                    }
                }
            }, 60 * 60 * 1_000);
        } else {
            cancelFairySchedule(!stats.equippedFairy);
        }
    }

    /**
     *
     * @param exp
     */
    public void cancelFairySchedule(boolean exp) {
        if (fairySchedule != null) {
            fairySchedule.cancel(false);
            fairySchedule = null;
        }
        if (exp) {
            this.fairyExp = 30;
        }
    }

    /**
     *
     * @return
     */
    public byte getFairyExp() {
        return fairyExp;
    }

    /**
     *
     * @return
     */
    public int getCoconutTeam() {
        return coconutteam;
    }

    /**
     *
     * @param team
     */
    public void setCoconutTeam(int team) {
        coconutteam = team;
    }

    /**
     *
     * @param slot
     */
    public void spawnPet(byte slot) {
        spawnPet(slot, false, true);
    }

    /**
     *
     * @param slot
     * @param lead
     */
    public void spawnPet(byte slot, boolean lead) {
        spawnPet(slot, lead, true);
    }

    /**
     *
     * @param slot
     * @param lead
     * @param broadcast
     */
    public void spawnPet(byte slot, boolean lead, boolean broadcast) {
        final IItem item = getInventory(MapleInventoryType.CASH).getItem(slot);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (item == null || item.getItemId() > 5_010_000 || item.getItemId() < 5_000_000) {
            return;
        }
        switch (item.getItemId()) {
            case 5_000_047:
            case 5_000_028: {
                final MaplePet pet = MaplePet.createPet(item.getItemId() + 1, MapleInventoryIdentifier.getInstance());
                if (pet != null) {
                    MapleInventoryManipulator.addById(client, item.getItemId() + 1, (short) 1, item.getOwner(), pet, 45, (byte) 0);
                    MapleInventoryManipulator.removeFromSlot(client, MapleInventoryType.CASH, slot, (short) 1, false);
                }
                break;
            }
            default: {
                final MaplePet pet = item.getPet();
                if (pet != null && (item.getItemId() != 5_000_054 || pet.getSecondsLeft() > 0) && (item.getExpiration() == -1 || item.getExpiration() > System.currentTimeMillis())) {
                    if (pet.getSummoned()) { // Already summoned, let's keep it
                        unequipPet(pet, false);
                    } else {
                        int leadid = 8;
                        if (GameConstants.isKOC(getJob())) {
                            leadid = 10_000_018;
                        } else if (GameConstants.isAran(getJob())) {
                            leadid = 20_000_024;
                        }
                        if (getSkillLevel(SkillFactory.getSkill(leadid)) == 0 && getPet(0) != null) {
                            unequipPet(getPet(0), false);
                        } else if (lead) { // Follow the Lead
                            shiftPetsRight();
                        }
                        final Point pos = getPosition();
                        pet.setPos(pos);
                        try {
                            pet.setFh(getMap().getFootholds().findBelow(pos).getId());
                        } catch (NullPointerException e) {
                            pet.setFh(0); //lol, it can be fixed by movement
                        }
                        pet.setStance(0);
                        pet.setSummoned(getPetSlotNext() + 1);

                        addPet(pet);
                        if (broadcast) {
                            getMap().broadcastMessage(this, PetPacket.showPet(this, pet, false, false), true);

                            //final List<Pair<MapleStat, Integer>> stats = new ArrayList<Pair<MapleStat, Integer>>(1);
                            //stats.add(new Pair<MapleStat, Integer>(MapleStat.PET, Integer.valueOf(pet.getUniqueId())));
                            client.sendPacket(PetPacket.petStatUpdate(this));
                        }
                    }
                }
                break;
            }
        }
        client.sendPacket(PetPacket.emptyStatUpdate());
    }

    /**
     *
     * @param mobid
     */
    public void addMoveMob(int mobid) {
        if (movedMobs.containsKey(mobid)) {
            movedMobs.put(mobid, movedMobs.get(mobid) + 1);
            if (movedMobs.get(mobid) > 30) { //trying to move not null monster = broadcast dead
                for (MapleCharacter chr : getMap().getCharactersThreadsafe()) { //also broadcast to others
                    if (chr.getMoveMobs().containsKey(mobid)) { //they also tried to move this mob
                        chr.getClient().getSession().write(MobPacket.killMonster(mobid, 1));
                        chr.getMoveMobs().remove(mobid);
                    }
                }
            }
        } else {
            movedMobs.put(mobid, 1);
        }
    }

    /**
     *
     * @return
     */
    public Map<Integer, Integer> getMoveMobs() {
        return movedMobs;
    }

    /**
     *
     * @return
     */
    public int getLinkMid() {
        return linkMid;
    }

    /**
     *
     * @param lm
     */
    public void setLinkMid(int lm) {
        this.linkMid = lm;
    }

    /**
     *
     * @return
     */
    public boolean isClone() {
        return clone;
    }

    /**
     *
     * @param c
     */
    public void setClone(boolean c) {
        this.clone = c;
    }

    /**
     *
     * @return
     */
    public WeakReference<MapleCharacter>[] getClones() {
        return clones;
    }

    /**
     *
     * @return
     */
    public MapleCharacter cloneLooks() {
        MapleClient cs = new MapleClient(null, null, new MockIOSession());

        final int minus = (getId() + Randomizer.nextInt(getId())); // really randomize it, dont want it to fail

        MapleCharacter ret = new MapleCharacter(true);
        ret.id = minus;
        ret.client = cs;
        ret.exp = 0;
        ret.meso = 0;
        ret.beans = beans;
        ret.blood = blood;
        ret.month = month;
        ret.day = day;
        ret.charmessage = charmessage;
        ret.expression = expression;
        ret.constellation = constellation;
        ret.remainingAp = 0;
        ret.fame = 0;
        ret.accountid = client.getAccID();
        ret.name = name;
        ret.level = level;
        ret.fame = fame;
        ret.job = job;
        ret.hair = hair;
        ret.face = face;
        ret.skinColor = skinColor;
        ret.bookCover = bookCover;
        ret.monsterbook = monsterbook;
        ret.mount = mount;
        ret.CRand = new PlayerRandomStream();
        ret.gmLevel = gmLevel;
        ret.gender = gender;
        ret.mapid = map.getId();
        ret.map = map;
        ret.setStance(getStance());
        ret.chair = chair;
        ret.itemEffect = itemEffect;
        ret.guildid = guildid;
        ret.currentrep = currentrep;
        ret.totalrep = totalrep;
        ret.stats = stats;
        ret.effects.putAll(effects);
        if (ret.effects.get(MapleBuffStat.ILLUSION) != null) {
            ret.effects.remove(MapleBuffStat.ILLUSION);
        }
        if (ret.effects.get(MapleBuffStat.SUMMON) != null) {
            ret.effects.remove(MapleBuffStat.SUMMON);
        }
        if (ret.effects.get(MapleBuffStat.REAPER) != null) {
            ret.effects.remove(MapleBuffStat.REAPER);
        }
        if (ret.effects.get(MapleBuffStat.PUPPET) != null) {
            ret.effects.remove(MapleBuffStat.PUPPET);
        }
        ret.guildrank = guildrank;
        ret.allianceRank = allianceRank;
        ret.hidden = hidden;
        ret.setPosition(new Point(getPosition()));
        for (IItem equip : getInventory(MapleInventoryType.EQUIPPED)) {
            ret.getInventory(MapleInventoryType.EQUIPPED).addFromDB(equip);
        }
        ret.skillMacros = skillMacros;
        ret.keylayout = keylayout;
        ret.questinfo = questinfo;
        ret.savedLocations = savedLocations;
        ret.wishlist = wishlist;
        ret.rocks = rocks;
        ret.regrocks = regrocks;
        ret.buddylist = buddylist;
        ret.keydown_skill = 0;
        ret.lastmonthfameids = lastmonthfameids;
        ret.lastfametime = lastfametime;
        ret.storage = storage;
        ret.cs = this.cs;
        ret.client.setAccountName(client.getAccountName());
        ret.acash = acash;
        ret.lastGainHM = lastGainHM;
        ret.maplepoints = maplepoints;
        ret.clone = true;
        ret.client.setChannel(this.client.getChannel());
        System.out.println("cloneLooks输出：" + this.client.getChannel());
        while (map.getCharacterById(ret.id) != null || client.getChannelServer().getPlayerStorage().getCharacterById(ret.id) != null) {
            ret.id++;
        }
        ret.client.setPlayer(ret);
        return ret;
    }

    /**
     *
     */
    public final void cloneLook() {
        if (clone) {
            return;
        }
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() == null) {
                final MapleCharacter newp = cloneLooks();
                map.addPlayer(newp);
                map.broadcastMessage(MaplePacketCreator.updateCharLook(newp));
                map.movePlayer(newp, getPosition());
                clones[i] = new WeakReference<>(newp);
                return;
            }
        }
    }

    /**
     *
     */
    public final void disposeClones() {
        numClones = 0;
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                map.removePlayer(clones[i].get());
                clones[i].get().getClient().disconnect(false, false);
                clones[i] = new WeakReference<>(null);
                numClones++;
            }
        }
    }

    /**
     *
     * @return
     */
    public final int getCloneSize() {
        int z = 0;
        for (WeakReference<MapleCharacter> clone1 : clones) {
            if (clone1.get() != null) {
                z++;
            }
        }
        return z;
    }

    /**
     *
     */
    public void spawnClones() {
        if (numClones == 0 && stats.hasClone) {
            cloneLook(); //once and never again
        }
        for (int i = 0; i < numClones; i++) {
            cloneLook();
        }
        numClones = 0;
    }

    /**
     *
     * @return
     */
    public byte getNumClones() {
        return numClones;
    }

    /**
     *
     * @param d
     */
    public void setDragon(MapleDragon d) {
        this.dragon = d;
    }

    /**
     *
     */
    public final void spawnSavedPets() {
        for (int i = 0; i < petStore.length; i++) {
            if (petStore[i] > -1) {
                spawnPet(petStore[i], false, false);
            }
        }
        client.getSession().write(PetPacket.petStatUpdate(this));
        petStore = new byte[]{-1, -1, -1};
    }

    /**
     *
     * @return
     */
    public final byte[] getPetStores() {
        return petStore;
    }

    /**
     *
     * @param str
     * @param dex
     * @param int_
     * @param luk
     */
    public void resetStats(final int str, final int dex, final int int_, final int luk) {
        List<Pair<MapleStat, Integer>> stat = new ArrayList<>(2);
        int total = stats.getStr() + stats.getDex() + stats.getLuk() + stats.getInt() + getRemainingAp();

        total -= str;
        stats.setStr((short) str);

        total -= dex;
        stats.setDex((short) dex);

        total -= int_;
        stats.setInt((short) int_);

        total -= luk;
        stats.setLuk((short) luk);

        setRemainingAp((short) total);

        stat.add(new Pair<>(MapleStat.STR, str));
        stat.add(new Pair<>(MapleStat.DEX, dex));
        stat.add(new Pair<>(MapleStat.INT, int_));
        stat.add(new Pair<>(MapleStat.LUK, luk));
        stat.add(new Pair<>(MapleStat.AVAILABLEAP, total));
        client.getSession().write(MaplePacketCreator.updatePlayerStats(stat, false, getJob()));
    }

    /**
     *
     * @return
     */
    public Event_PyramidSubway getPyramidSubway() {
        return pyramidSubway;
    }

    /**
     *
     * @param ps
     */
    public void setPyramidSubway(Event_PyramidSubway ps) {
        this.pyramidSubway = ps;
    }

    /**
     *
     * @return
     */
    public byte getSubcategory() {
        if (job >= 430 && job <= 434) {
            return 1; //dont set it
        }
        return subcategory;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public int itemQuantity(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).countById(itemid);
    }

    /**
     *
     * @param rps
     */
    public void setRPS(RockPaperScissors rps) {
        this.rps = rps;
    }

    /**
     *
     * @return
     */
    public RockPaperScissors getRPS() {
        return rps;
    }

    /**
     *
     * @return
     */
    public long getNextConsume() {
        return nextConsume;
    }

    /**
     *
     * @param nc
     */
    public void setNextConsume(long nc) {
        this.nextConsume = nc;
    }

    /**
     *
     * @return
     */
    public int getRank() {
        return rank;
    }

    /**
     *
     * @return
     */
    public int getRankMove() {
        return rankMove;
    }

    /**
     *
     * @return
     */
    public int getJobRank() {
        return jobRank;
    }

    /**
     *
     * @return
     */
    public int getJobRankMove() {
        return jobRankMove;
    }

    /**
     *
     * @param channel
     */
    public void changeChannel(final int channel) {
        Integer energyLevel = getBuffedValue(MapleBuffStat.ENERGY_CHARGE);
        if (energyLevel != null && energyLevel > 0) {
            setBuffedValue(MapleBuffStat.ENERGY_CHARGE, energyLevel);
            List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, energyLevel));
            client.getSession().write(MaplePacketCreator.能量条(stat, 0)); //???????????????????????????????
        }
        String[] socket = client.getChannelServer().getIP().split(":");
        final ChannelServer toch = ChannelServer.getInstance(channel);

        if (channel == client.getChannel() || toch == null || toch.isShutdown()) {
            // client.getSession().write(MaplePacketCreator.serverBlocked(1));
            return;
        }
        changeRemoval();

        final ChannelServer ch = ChannelServer.getInstance(client.getChannel());
        if (getMessenger() != null) {
            World.Messenger.silentLeaveMessenger(getMessenger().getId(), new MapleMessengerCharacter(this));
        }
        PlayerBuffStorage.addBuffsToStorage(getId(), getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(getId(), getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(getId(), getAllDiseases());
        World.ChannelChange_Data(new CharacterTransfer(this), getId(), channel);
        ch.removePlayer(this);
        client.updateLoginState(MapleClient.CHANGE_CHANNEL, client.getSessionIPAddress());
        String s = this.client.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf(47) + 1, s.length()));
        try {
            client.getSession().write(MaplePacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(toch.getIP().split(":")[1])));
        } catch (UnknownHostException ex) {
            Logger.getLogger(MapleCharacter.class.getName()).log(Level.SEVERE, null, ex);
        }
        saveToDB(false, false);
        getMap().removePlayer(this);
        client.setPlayer(null);
        client.setReceiving(false);
        expirationTask(true, false);
    }

    /**
     *
     * @param type
     * @param amount
     */
    public void expandInventory(byte type, int amount) {
        final MapleInventory inv = getInventory(MapleInventoryType.getByType(type));
        inv.addSlot((byte) amount);
        // client.getSession().write(MaplePacketCreator.getSlotUpdate(type, (byte) inv.getSlotLimit()));
    }

    /**
     *
     * @param other
     * @return
     */
    public boolean allowedToTarget(MapleCharacter other) {
        return other != null && (!other.isHidden() || getGMLevel() >= other.getGMLevel());
    }

    /**
     *
     * @return
     */
    public int getFollowId() {
        return followid;
    }

    /**
     *
     * @param fi
     */
    public void setFollowId(int fi) {
        this.followid = fi;
        if (fi == 0) {
            this.followinitiator = false;
            this.followon = false;
        }
    }

    /**
     *
     * @param fi
     */
    public void setFollowInitiator(boolean fi) {
        this.followinitiator = fi;
    }

    /**
     *
     * @param fi
     */
    public void setFollowOn(boolean fi) {
        this.followon = fi;
    }

    /**
     *
     * @return
     */
    public boolean isFollowOn() {
        return followon;
    }

    /**
     *
     * @return
     */
    public boolean isFollowInitiator() {
        return followinitiator;
    }

    /**
     *
     */
    public void checkFollow() {
        if (followon) {
            //  map.broadcastMessage(MaplePacketCreator.followEffect(id, 0, null));
            //  map.broadcastMessage(MaplePacketCreator.followEffect(followid, 0, null));
            MapleCharacter tt = map.getCharacterById(followid);
            //client.getSession().write(MaplePacketCreator.getFollowMessage("Follow canceled."));
            if (tt != null) {
                tt.setFollowId(0);
                // tt.getClient().getSession().write(MaplePacketCreator.getFollowMessage("Follow canceled."));
            }
            setFollowId(0);
        }
    }

    /**
     *
     * @return
     */
    public int getMarriageId() {
        return marriageId;
    }

    /**
     *
     * @param mi
     */
    public void setMarriageId(final int mi) {
        this.marriageId = mi;
    }

    /**
     *
     * @return
     */
    public int getMarriageItemId() {
        return marriageItemId;
    }

    /**
     *
     * @param mi
     */
    public void setMarriageItemId(final int mi) {
        this.marriageItemId = mi;
    }

    /**
     *
     * @return
     */
    public boolean isStaff() {
        return this.gmLevel > ServerConstants.PlayerGMRank.NORMAL.getLevel();
    }

    // TODO: gvup, vic, lose, draw, VR
    /**
     *
     * @param questid
     * @return
     */
    public boolean startPartyQuest(final int questid) {
        boolean ret = false;
        if (!quests.containsKey(MapleQuest.getInstance(questid)) || !questinfo.containsKey(questid)) {
            final MapleQuestStatus status = getQuestNAdd(MapleQuest.getInstance(questid));
            status.setStatus((byte) 1);
            updateQuest(status);
            switch (questid) {
                case 1_300:
                case 1_301:
                case 1_302: //carnival, ariants.
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0;gvup=0;vic=0;lose=0;draw=0");
                    break;
                case 1_204: //herb town pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;have2=0;have3=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                case 1_206: //ellin pq
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have0=0;have1=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
                default:
                    updateInfoQuest(questid, "min=0;sec=0;date=0000-00-00;have=0;rank=F;try=0;cmp=0;CR=0;VR=0");
                    break;
            }
            ret = true;
        } //started the quest.
        return ret;
    }

    /*
     * 任务信息
     */
    /**
     *
     * @param questid
     * @param data
     * @param show
     */
    public void updateInfoQuest(int questid, String data, boolean show) {
        questinfo.put(questid, data);
        changed_questinfo = true;
        if (show) {
            client.getSession().write(MaplePacketCreator.updateInfoQuest(questid, data));
        }
    }

    /**
     *
     * @param questid
     */
    public void removeInfoQuest(int questid) {
        if (questinfo.containsKey(questid)) {
            this.updateInfoQuest(questid, "");
            questinfo.remove(questid);
            changed_questinfo = true;
        }
    }

    /**
     *
     * @param id
     * @param stat
     * @return
     */
    public String getInfoQuestStatS(int id, String stat) {
        String info = getInfoQuest(id);
        if (info != null && info.length() > 0 && info.contains(stat)) {
            int startIndex = info.indexOf(stat) + stat.length() + 1;
            int until = info.indexOf(';', startIndex);
            return info.substring(startIndex, until != -1 ? until : info.length());
        }
        return "";
    }

    /**
     *
     * @param id
     * @param stat
     * @return
     */
    public int getInfoQuestStat(int id, String stat) {
        String statz = getInfoQuestStatS(id, stat);
        return (statz == null || "".equals(statz)) ? 0 : Integer.parseInt(statz);
    }

    /**
     *
     * @param id
     * @param stat
     * @param statData
     */
    public void setInfoQuestStat(int id, String stat, int statData) {
        setInfoQuestStat(id, stat, statData);
    }

    /**
     *
     * @param id
     * @param stat
     * @param statData
     */
    public void setInfoQuestStat(int id, String stat, String statData) {
        String info = getInfoQuest(id);
        if (info.length() == 0 || !info.contains(stat)) {
            updateInfoQuest(id, stat + "=" + statData + (info.length() == 0 ? "" : ";") + info);
        } else {
            String newInfo = stat + "=" + statData;
            String beforeStat = info.substring(0, info.indexOf(stat));
            int from = info.indexOf(';', info.indexOf(stat) + stat.length());
            String afterStat = from == -1 ? "" : info.substring(from + 1);
            updateInfoQuest(id, beforeStat + newInfo + (afterStat.length() != 0 ? (";" + afterStat) : ""));
        }
    }

    /**
     *
     * @param questid
     * @param key
     * @return
     */
    public String getOneInfo(final int questid, final String key) {
        if (!questinfo.containsKey(questid) || key == null) {
            return null;
        }
        final String[] split = questinfo.get(questid).split(";");
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length == 2 && split2[0].equals(key)) {
                return split2[1];
            }
        }
        return null;
    }

    /**
     *
     * @param questid
     * @param key
     * @param value
     */
    public void updateOneInfo(final int questid, final String key, final String value) {
        if (!questinfo.containsKey(questid) || key == null || value == null) {
            return;
        }
        final String[] split = questinfo.get(questid).split(";");
        boolean changed = false;
        final StringBuilder newQuest = new StringBuilder();
        for (String x : split) {
            final String[] split2 = x.split("="); //should be only 2
            if (split2.length != 2) {
                continue;
            }
            if (split2[0].equals(key)) {
                newQuest.append(key).append("=").append(value);
            } else {
                newQuest.append(x);
            }
            newQuest.append(";");
            changed = true;
        }

        updateInfoQuest(questid, changed ? newQuest.toString().substring(0, newQuest.toString().length() - 1) : newQuest.toString());
    }

    /**
     *
     * @param questid
     */
    public void recalcPartyQuestRank(final int questid) {
        if (!startPartyQuest(questid)) {
            final String oldRank = getOneInfo(questid, "rank");
            if (oldRank == null || oldRank.equals("S")) {
                return;
            }
            final String[] split = questinfo.get(questid).split(";");
            String newRank = null;
            switch (oldRank) {
                case "A":
                    newRank = "S";
                    break;
                case "B":
                    newRank = "A";
                    break;
                case "C":
                    newRank = "B";
                    break;
                case "D":
                    newRank = "C";
                    break;
                case "F":
                    newRank = "D";
                    break;
                default:
                    return;
            }
            final List<Pair<String, Pair<String, Integer>>> questInfo = MapleQuest.getInstance(questid).getInfoByRank(newRank);
            for (Pair<String, Pair<String, Integer>> q : questInfo) {
                boolean found = false;
                final String val = getOneInfo(questid, q.right.left);
                if (val == null) {
                    return;
                }
                int vall = 0;
                try {
                    vall = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                    return;
                }
                switch (q.left) {
                    case "less":
                        found = vall < q.right.right;
                        break;
                    case "more":
                        found = vall > q.right.right;
                        break;
                    case "equal":
                        found = vall == q.right.right;
                        break;
                    default:
                        break;
                }
                if (!found) {
                    return;
                }
            }
            //perfectly safe
            updateOneInfo(questid, "rank", newRank);
        }
    }

    /**
     *
     * @param questid
     */
    public void tryPartyQuest(final int questid) {
        try {
            startPartyQuest(questid);
            pqStartTime = System.currentTimeMillis();
            updateOneInfo(questid, "try", String.valueOf(Integer.parseInt(getOneInfo(questid, "try")) + 1));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("tryPartyQuest error");
        }
    }

    /**
     *
     * @param questid
     */
    public void endPartyQuest(final int questid) {
        try {
            startPartyQuest(questid);
            if (pqStartTime > 0) {
                final long changeTime = System.currentTimeMillis() - pqStartTime;
                final int mins = (int) (changeTime / 1_000 / 60), secs = (int) (changeTime / 1_000 % 60);
                final int mins2 = Integer.parseInt(getOneInfo(questid, "min")), secs2 = Integer.parseInt(getOneInfo(questid, "sec"));
                if (mins2 <= 0 || mins < mins2) {
                    updateOneInfo(questid, "min", String.valueOf(mins));
                    updateOneInfo(questid, "sec", String.valueOf(secs));
                    updateOneInfo(questid, "date", FileoutputUtil.CurrentReadable_Date());
                }
                final int newCmp = Integer.parseInt(getOneInfo(questid, "cmp")) + 1;
                updateOneInfo(questid, "cmp", String.valueOf(newCmp));
                updateOneInfo(questid, "CR", String.valueOf((int) Math.ceil((newCmp * 100.0) / Integer.parseInt(getOneInfo(questid, "try")))));
                recalcPartyQuestRank(questid);
                pqStartTime = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("endPartyQuest error");
        }

    }

    /**
     *
     * @param itemId
     */
    public void havePartyQuest(final int itemId) {
        int questid = 0, index = -1;
        switch (itemId) {
            case 1_002_798:
                questid = 1_200; //henesys
                break;
            case 1_072_369:
                questid = 1_201; //kerning
                break;
            case 1_022_073:
                questid = 1_202; //ludi
                break;
            case 1_082_232:
                questid = 1_203; //orbis
                break;
            case 1_002_571:
            case 1_002_572:
            case 1_002_573:
            case 1_002_574:
                questid = 1_204; //herbtown
                index = itemId - 1_002_571;
                break;
            case 1_122_010:
                questid = 1_205; //magatia
                break;
            case 1_032_061:
            case 1_032_060:
                questid = 1_206; //ellin
                index = itemId - 1_032_060;
                break;
            case 3_010_018:
                questid = 1_300; //ariant
                break;
            case 1_122_007:
                questid = 1_301; //carnival
                break;
            case 1_122_058:
                questid = 1_302; //carnival2
                break;
            default:
                return;
        }
        startPartyQuest(questid);
        updateOneInfo(questid, "have" + (index == -1 ? "" : index), "1");
    }

    /**
     *
     * @param beginnerJob
     */
    public void resetStatsByJob(boolean beginnerJob) {
        int baseJob = (beginnerJob ? (job % 1_000) : (job % 1_000 / 100 * 100)); //1112 -> 112 -> 1 -> 100
        switch (baseJob) {
            case 100:
                //first job = warrior
                resetStats(25, 4, 4, 4);
                break;
            case 200:
                resetStats(4, 4, 20, 4);
                break;
            case 300:
            case 400:
                resetStats(4, 25, 4, 4);
                break;
            case 500:
                resetStats(4, 20, 4, 4);
                break;
            default:
                break;
        }
    }

    /**
     *
     * @return
     */
    public boolean hasSummon() {
        return hasSummon;
    }

    /**
     *
     * @param summ
     */
    public void setHasSummon(boolean summ) {
        this.hasSummon = summ;
    }

    /**
     *
     */
    public void removeDoor() {
        final MapleDoor door = getDoors().iterator().next();
        for (final MapleCharacter chr : door.getTarget().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleCharacter chr : door.getTown().getCharactersThreadsafe()) {
            door.sendDestroyData(chr.getClient());
        }
        for (final MapleDoor destroyDoor : getDoors()) {
            door.getTarget().removeMapObject(destroyDoor);
            door.getTown().removeMapObject(destroyDoor);
        }
        clearDoors();
    }

    /**
     *
     */
    public void changeRemoval() {
        changeRemoval(false);
    }

    /**
     *
     * @param dc
     */
    public void changeRemoval(boolean dc) {
        if (getTrade() != null) {
            MapleTrade.cancelTrade(getTrade(), client);
        }
        if (getCheatTracker() != null) {
            getCheatTracker().dispose();
        }
        if (!dc) {
            cancelEffectFromBuffStat(MapleBuffStat.MONSTER_RIDING);
            cancelEffectFromBuffStat(MapleBuffStat.SUMMON);
            cancelEffectFromBuffStat(MapleBuffStat.REAPER);
            cancelEffectFromBuffStat(MapleBuffStat.PUPPET);
        }
        if (getPyramidSubway() != null) {
            getPyramidSubway().dispose(this);
        }
        if (playerShop != null && !dc) {
            playerShop.removeVisitor(this);
            if (playerShop.isOwner(this)) {
                playerShop.setOpen(true);
            }
        }
        if (!getDoors().isEmpty()) {
            removeDoor();
        }
        disposeClones();
        NPCScriptManager.getInstance().dispose(client);
    }

    /**
     *
     * @param newTick
     */
    public void updateTick(int newTick) {
        anticheat.updateTick(newTick);
    }

    /**
     *
     * @param buff
     * @return
     */
    public boolean canUseFamilyBuff(MapleFamilyBuffEntry buff) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Long.parseLong(stat.getCustomData()) + (24 * 3_600_000) < System.currentTimeMillis();
    }

    /**
     *
     * @param buff
     */
    public void useFamilyBuff(MapleFamilyBuffEntry buff) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(buff.questID));
        stat.setCustomData(String.valueOf(System.currentTimeMillis()));
    }

    /**
     *
     * @return
     */
    public List<Pair<Integer, Integer>> usedBuffs() {
        //assume count = 1
        List<Pair<Integer, Integer>> used = new ArrayList<>();
        for (MapleFamilyBuffEntry buff : MapleFamilyBuff.getBuffEntry()) {
            if (!canUseFamilyBuff(buff)) {
                used.add(new Pair<>(buff.index, buff.count));
            }
        }
        return used;
    }

    /**
     *
     * @return
     */
    public String getTeleportName() {
        return teleportname;
    }

    /**
     *
     * @param tname
     */
    public void setTeleportName(final String tname) {
        teleportname = tname;
    }

    /**
     *
     * @return
     */
    public int getNoJuniors() {
        if (mfc == null) {
            return 0;
        }
        return mfc.getNoJuniors();
    }

    /**
     *
     * @return
     */
    public MapleFamilyCharacter getMFC() {
        return mfc;
    }

    /**
     *
     * @param familyid
     * @param seniorid
     * @param junior1
     * @param junior2
     */
    public void makeMFC(final int familyid, final int seniorid, final int junior1, final int junior2) {
        if (familyid > 0) {
            MapleFamily f = World.Family.getFamily(familyid);
            if (f == null) {
                mfc = null;
            } else {
                mfc = f.getMFC(id);
                if (mfc == null) {
                    mfc = f.addFamilyMemberInfo(this, seniorid, junior1, junior2);
                }
                if (mfc.getSeniorId() != seniorid) {
                    mfc.setSeniorId(seniorid);
                }
                if (mfc.getJunior1() != junior1) {
                    mfc.setJunior1(junior1);
                }
                if (mfc.getJunior2() != junior2) {
                    mfc.setJunior2(junior2);
                }
            }
        } else {
            mfc = null;
        }
    }

    /**
     *
     * @param newf
     * @param news
     * @param newj1
     * @param newj2
     */
    public void setFamily(final int newf, final int news, final int newj1, final int newj2) {
        if (mfc == null || newf != mfc.getFamilyId() || news != mfc.getSeniorId() || newj1 != mfc.getJunior1() || newj2 != mfc.getJunior2()) {
            makeMFC(newf, news, newj1, newj2);
        }
    }

    /**
     *
     * @param skillid
     * @return
     */
    public int maxBattleshipHP(int skillid) {
        return (getSkillLevel(skillid) * 5_000) + ((getLevel() - 120) * 3_000);
    }

    /**
     *
     * @return
     */
    public int currentBattleshipHP() {
        return battleshipHP;
    }

    /**
     *
     * @param msg
     */
    public void sendEnglishQuiz(String msg) {
        client.getSession().write(MaplePacketCreator.englishQuizMsg(msg));
    }

    /**
     *
     */
    public void fakeRelog() {
        client.getSession().write(MaplePacketCreator.getCharInfo(this));
        final MapleMap mapp = getMap();
//        mapp.setCheckStates(false);
        mapp.removePlayer(this);
        mapp.addPlayer(this);
//        mapp.setCheckStates(true);
    }

    /*
     * public String getcharmessage(){ System.err.println("CharMessage(get)");
     * return charmessage; }
     *
     * public void setcharmessage(int s){
     * System.err.println("CharMessage(set)"); charmessage += s; }
     */
    /**
     *
     * @return
     */
    public String getcharmessage() {
        //System.err.println("CharMessage(get)");
        return charmessage;
    }

    /**
     *
     * @param s
     */
    public void setcharmessage(String s) {
        //System.err.println("CharMessage(set)");
        charmessage /*
                 * +
                 */ = s;
    }

    /**
     *
     * @return
     */
    public int getexpression() {
        return expression;
    }

    /**
     *
     * @param s
     */
    public void setexpression(int s) {
        expression /*
                 * +
                 */ = s;
    }

    /**
     *
     * @return
     */
    public int getconstellation() {
        return constellation;
    }

    /**
     *
     * @param s
     */
    public void setconstellation(int s) {
        constellation /*
                 * +
                 */ = s;
    }

    /**
     *
     * @return
     */
    public int getblood() {
        return blood;
    }

    /**
     *
     * @param s
     */
    public void setblood(int s) {
        blood /*
                 * +
                 */ = s;
    }

    /**
     *
     * @return
     */
    public int getmonth() {
        return month;
    }

    /**
     *
     * @param s
     */
    public void setmonth(int s) {
        month /*
                 * +
                 */ = s;
    }

    /**
     *
     * @return
     */
    public int getday() {
        return day;
    }

    /**
     *
     * @param s
     */
    public void setday(int s) {
        day /*
                 * +
                 */ = s;
    }

    /**
     *
     * @return
     */
    public int getTeam() {
        return coconutteam;
    }

    /**
     *
     * @return
     */
    public int getBeans() {
        return beans;
    }

    /**
     *
     * @param s
     */
    public void gainBeans(int s) {
        beans += s;
    }

    /**
     *
     * @param s
     */
    public void setBeans(int s) {
        beans = s;
    }

    /**
     *
     * @return
     */
    public int getBeansNum() {
        return beansNum;
    }

    /**
     * @param beansNum the beansNum to set
     */
    public void setBeansNum(int beansNum) {
        this.beansNum = beansNum;
    }

    /**
     * @return the beansRange
     */
    public int getBeansRange() {
        return beansRange;
    }

    /**
     * @param beansRange the beansRange to set
     */
    public void setBeansRange(int beansRange) {
        beansRange = beansRange;
    }

    /**
     * @return the canSetBeansNum
     */
    public boolean isCanSetBeansNum() {
        return canSetBeansNum;
    }

    /**
     * @param canSetBeansNum the canSetBeansNum to set
     */
    public void setCanSetBeansNum(boolean canSetBeansNum) {
        this.canSetBeansNum = canSetBeansNum;
    }

    /**
     *
     * @return
     */
    public int getjf() {
        return jf;
    }

    /**
     *
     * @param s
     */
    public void gainjf(int s) {
        jf += s;
    }

    /**
     *
     * @param s
     */
    public void setjf(int s) {
        jf = s;
    }

    /**
     *
     * @return
     */
    public int getlqjf() {
        return lqjf;
    }

    /**
     *
     * @param s
     */
    public void gainlqjf(int s) {
        lqjf += s;
    }

    /**
     *
     * @param s
     */
    public void setlqjf(int s) {
        lqjf = s;
    }

    /**
     *
     * @return
     */
    public int getbossjf() {
        return bossjf;
    }

    /**
     *
     * @param s
     */
    public void gainbossjf(int s) {
        bossjf += s;
    }

    /**
     *
     * @param s
     */
    public void setbossjf(int s) {
        bossjf = s;
    }

    /**
     *
     * @return
     */
    public int getcjjf() {
        return cjjf;
    }

    /**
     *
     * @param s
     */
    public void gaincjjf(int s) {
        cjjf += s;
    }

    /**
     *
     * @param s
     */
    public void setcjjf(int s) {
        cjjf = s;
    }

    /**
     *
     * @return
     */
    public boolean haveGM() {
        return gmLevel >= 2 && gmLevel <= 3;
    }

    /**
     *
     * @param prefix
     */
    public void setprefix(int prefix) {
        this.prefix = prefix;
    }

    /**
     *
     * @return
     */
    public int getPrefix() {
        return prefix;
    }

    /**
     *
     * @param msg
     * @param itemId
     */
    public void startMapEffect(String msg, int itemId) {
        startMapEffect(msg, itemId, 10_000);
    }

    /**
     *
     * @param msg
     * @param itemId
     */
    public void startMapEffect1(String msg, int itemId) {
        startMapEffect(msg, itemId, 20_000);
    }

    /**
     *
     * @param msg
     * @param itemId
     * @param duration
     */
    public void startMapEffect(String msg, int itemId, int duration) {
        final MapleMapEffect mapEffect = new MapleMapEffect(msg, itemId);
        getClient().getSession().write(mapEffect.makeStartData());
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                getClient().getSession().write(mapEffect.makeDestroyData());
            }
        }, duration);
    }

    /**
     *
     * @param type
     * @return
     */
    public int getHyPay(int type) {
        int pay = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            try (PreparedStatement ps = con.prepareStatement("select * from hypay where accname = ?")) {
                ps.setString(1, getClient().getAccountName());
                rs = ps.executeQuery();
                if (rs.next()) {
                    switch (type) {
                        case 1:
                            pay = rs.getInt("pay");
                            break;
                        case 2:
                            pay = rs.getInt("payUsed");
                            break;
                        case 3:
                            pay = rs.getInt("pay") + rs.getInt("payUsed");
                            break;
                        case 4:
                            pay = rs.getInt("payReward");
                            break;
                        default:
                            pay = 0;
                            break;
                    }
                } else {
                    try (PreparedStatement psu = con.prepareStatement("insert into hypay (accname, pay, payUsed, payReward) VALUES (?, ?, ?, ?)")) {
                        psu.setString(1, getClient().getAccountName());
                        psu.setInt(2, 0);
                        psu.setInt(3, 0);
                        psu.setInt(4, 0);
                        psu.executeUpdate();
                    }
                }
            }
            rs.close();
        } catch (SQLException ex) {
            System.err.println("获取充值信息发生错误: " + ex);
        }
        return pay;
    }

    /**
     *
     * @param hypay
     * @return
     */
    public int gainHyPay(int hypay) {
        int pay = getHyPay(1);
        int payUsed = getHyPay(2);
        int payReward = getHyPay(4);
        if (hypay <= 0) {
            return 0;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE hypay SET pay = ? ,payUsed = ? ,payReward = ? where accname = ?")) {
                ps.setInt(1, pay + hypay);
                ps.setInt(2, payUsed);
                ps.setInt(3, payReward);
                ps.setString(4, getClient().getAccountName());
                ps.executeUpdate();
            }
            return 1;
        } catch (SQLException ex) {
            System.err.println("加减充值信息发生错误: " + ex);
        }
        return 0;
    }

    /**
     *
     * @param hypay
     * @return
     */
    public int addHyPay(int hypay) {
        int pay = getHyPay(1);
        int payUsed = getHyPay(2);
        int payReward = getHyPay(4);
        if (hypay > pay) {
            return -1;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE hypay SET pay = ? ,payUsed = ? ,payReward = ? where accname = ?")) {
                ps.setInt(1, pay - hypay);
                ps.setInt(2, payUsed + hypay);
                ps.setInt(3, payReward + hypay);
                ps.setString(4, getClient().getAccountName());
                ps.executeUpdate();
            }
            return 1;
        } catch (SQLException ex) {
            System.err.println("加减充值信息发生错误: " + ex);
        }
        return -1;
    }

    /**
     *
     * @param pay
     * @return
     */
    public int delPayReward(int pay) {
        int payReward = getHyPay(4);
        if (pay <= 0) {
            return -1;
        }
        if (pay > payReward) {
            return -1;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE hypay SET payReward = ? where accname = ?")) {
                ps.setInt(1, payReward - pay);
                ps.setString(2, getClient().getAccountName());
                ps.executeUpdate();
            }
            return 1;
        } catch (SQLException ex) {
            System.err.println("加减消费奖励信息发生错误: " + ex);
        }
        return -1;
    }

    /*
     * 新的角色泡点，按帐号计算
     */
    /**
     *
     * @return
     */
    public int getGamePoints() {
        try {
            int gamePoints = 0;
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, getClient().getAccID());
                ps.setInt(2, getWorld());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        gamePoints = rs.getInt("gamePoints");
                        Timestamp updateTime = rs.getTimestamp("updateTime");
                        Calendar sqlcal = Calendar.getInstance();
                        if (updateTime != null) {
                            sqlcal.setTimeInMillis(updateTime.getTime());
                        }
                        if (sqlcal.get(Calendar.DAY_OF_MONTH) + 1 <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH) || sqlcal.get(Calendar.MONTH) + 1 <= Calendar.getInstance().get(Calendar.MONTH) || sqlcal.get(Calendar.YEAR) + 1 <= Calendar.getInstance().get(Calendar.YEAR)) {
                            gamePoints = 0;
                            try (PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePoints = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                                psu.setInt(1, getClient().getAccID());
                                psu.setInt(2, getWorld());
                                psu.executeUpdate();
                            }
                        }
                    } else {
                        try (PreparedStatement psu = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePoints) VALUES (?, ?, ?)")) {
                            psu.setInt(1, getClient().getAccID());
                            psu.setInt(2, getWorld());
                            psu.setInt(3, 0);
                            psu.executeUpdate();
                        }
                    }
                }
            }
            return gamePoints;
        } catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误 - 数据库查询失败1" + Ex);
        }
        return -1;
    }

    /**
     *
     * @return
     */
    public int getGamePointsPD() {
        try {
            int gamePointsPD = 0;
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, getClient().getAccID());
                ps.setInt(2, getWorld());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        gamePointsPD = rs.getInt("gamePointspd");
                        Timestamp updateTime = rs.getTimestamp("updateTime");
                        Calendar sqlcal = Calendar.getInstance();
                        if (updateTime != null) {
                            sqlcal.setTimeInMillis(updateTime.getTime());
                        }
                        if ((sqlcal.get(5) + 1 <= Calendar.getInstance().get(5)) || (sqlcal.get(2) + 1 <= Calendar.getInstance().get(2)) || (sqlcal.get(1) + 1 <= Calendar.getInstance().get(1))) {
                            gamePointsPD = 0;
                            try (PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePointspd = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                                psu.setInt(1, getClient().getAccID());
                                psu.setInt(2, getWorld());
                                psu.executeUpdate();
                            }
                        }
                    } else {
                        try (PreparedStatement psu = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePointspd) VALUES (?, ?, ?)")) {
                            psu.setInt(1, getClient().getAccID());
                            psu.setInt(2, getWorld());
                            psu.setInt(3, 0);
                            psu.executeUpdate();
                        }
                    }
                }
            }
            return gamePointsPD;
        } catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误 - 数据库查询失败2" + Ex);
        }
        return -1;
    }

    /**
     *
     * @param amount
     */
    public void gainGamePoints(int amount) {
        int gamePoints = getGamePoints() + amount;
        updateGamePoints(gamePoints);
    }

    /**
     *
     * @param amount
     */
    public void gainGamePointsPD(int amount) {
        int gamePointsPD = getGamePointsPD() + amount;
        updateGamePointsPD(gamePointsPD);
    }

    /**
     *
     */
    public void resetGamePointsPD() {
        updateGamePointsPD(0);
    }

    /**
     *
     * @param amount
     */
    public void updateGamePointsPD(int amount) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePointspd = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, amount);
                ps.setInt(2, getClient().getAccID());
                ps.setInt(3, getWorld());
                ps.executeUpdate();
            }
        } catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误 - 数据库更新失败." + Ex);
        }
    }

    /**
     *
     */
    public void resetGamePoints() {
        updateGamePoints(0);
    }

    /**
     *
     * @param amount
     */
    public void updateGamePoints(int amount) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePoints = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, amount);
                ps.setInt(2, getClient().getAccID());
                ps.setInt(3, getWorld());
                ps.executeUpdate();
            }
        } catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误 - 数据库更新失败." + Ex);
        }
    }

    /**
     *
     * @return
     */
    public int getGamePointsRQ() {
        try {
            int gamePointsRQ = 0;
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, getClient().getAccID());
                ps.setInt(2, getWorld());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        gamePointsRQ = rs.getInt("gamePointsrq");
                        Timestamp updateTime = rs.getTimestamp("updateTime");
                        Calendar sqlcal = Calendar.getInstance();
                        if (updateTime != null) {
                            sqlcal.setTimeInMillis(updateTime.getTime());
                        }
                        if ((sqlcal.get(5) + 1 <= Calendar.getInstance().get(5)) || (sqlcal.get(2) + 1 <= Calendar.getInstance().get(2)) || (sqlcal.get(1) + 1 <= Calendar.getInstance().get(1))) {
                            gamePointsRQ = 0;
                            try (PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePointsrq = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                                psu.setInt(1, getClient().getAccID());
                                psu.setInt(2, getWorld());
                                psu.executeUpdate();
                            }
                        }
                    } else {
                        try (PreparedStatement psu = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePointsrq) VALUES (?, ?, ?)")) {
                            psu.setInt(1, getClient().getAccID());
                            psu.setInt(2, getWorld());
                            psu.setInt(3, 0);
                            psu.executeUpdate();
                        }
                    }
                }
            }
            return gamePointsRQ;
        } catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误 - 数据库查询失败3" + Ex);
        }
        return -1;
    }

    /**
     *
     * @param amount
     */
    public void gainGamePointsRQ(int amount) {
        int gamePointsRQ = getGamePointsRQ() + amount;
        updateGamePointsRQ(gamePointsRQ);
    }

    /**
     *
     */
    public void resetGamePointsRQ() {
        updateGamePointsRQ(0);
    }

    /**
     *
     * @param amount
     */
    public void updateGamePointsRQ(int amount) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePointsrq = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, amount);
                ps.setInt(2, getClient().getAccID());
                ps.setInt(3, getWorld());
                ps.executeUpdate();
            }
        } catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误 - 数据库更新失败." + Ex);
        }
    }

    /**
     *
     * @return
     */
    public int getGamePointsPS() {
        try {
            int gamePointsRQ = 0;
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts_info WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, getClient().getAccID());
                ps.setInt(2, getWorld());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    gamePointsRQ = rs.getInt("gamePointsps");
                    Timestamp updateTime = rs.getTimestamp("updateTime");
                    Calendar sqlcal = Calendar.getInstance();
                    if (updateTime != null) {
                        sqlcal.setTimeInMillis(updateTime.getTime());
                    }
                    if ((sqlcal.get(5) + 1 <= Calendar.getInstance().get(5)) || (sqlcal.get(2) + 1 <= Calendar.getInstance().get(2)) || (sqlcal.get(1) + 1 <= Calendar.getInstance().get(1))) {
                        gamePointsRQ = 0;
                        try (PreparedStatement psu = con.prepareStatement("UPDATE accounts_info SET gamePointsps = 0, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                            psu.setInt(1, getClient().getAccID());
                            psu.setInt(2, getWorld());
                            psu.executeUpdate();
                        }
                    }
                } else {
                    try (PreparedStatement psu = con.prepareStatement("INSERT INTO accounts_info (accId, worldId, gamePointsps) VALUES (?, ?, ?)")) {
                        psu.setInt(1, getClient().getAccID());
                        psu.setInt(2, getWorld());
                        psu.setInt(3, 0);
                        psu.executeUpdate();
                    }
                }
                rs.close();
            }
            return gamePointsRQ;
        } catch (SQLException Ex) {
            System.err.println("获取角色帐号的在线时间点出现错误 - 数据库查询失败" + Ex);
        }
        return -1;
    }

    /**
     *
     * @param amount
     */
    public void gainGamePointsPS(int amount) {
        int gamePointsPS = getGamePointsPS() + amount;
        updateGamePointsPS(gamePointsPS);
    }

    /**
     *
     */
    public void resetGamePointsPS() {
        updateGamePointsPS(0);
    }

    /**
     *
     * @param amount
     */
    public void updateGamePointsPS(int amount) {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts_info SET gamePointsps = ?, updateTime = CURRENT_TIMESTAMP() WHERE accId = ? AND worldId = ?")) {
                ps.setInt(1, amount);
                ps.setInt(2, getClient().getAccID());
                ps.setInt(3, getWorld());
                ps.executeUpdate();
            }
        } catch (SQLException Ex) {
            System.err.println("更新角色帐号的在线时间出现错误 - 数据库更新失败." + Ex);
        }
    }

    /**
     *
     * @return
     */
    public long getDeadtime() {
        return this.deadtime;
    }

    /**
     *
     * @param deadtime
     */
    public void setDeadtime(long deadtime) {
        this.deadtime = deadtime;
    }

    /**
     *
     * @param mobexp
     */
    public void increaseEquipExp(int mobexp) { //道具经验
        MapleItemInformationProvider mii = MapleItemInformationProvider.getInstance();
        try {
            for (IItem item : getInventory(MapleInventoryType.EQUIPPED).list()) {
                Equip nEquip = (Equip) item;
                String itemName = mii.getName(nEquip.getItemId());
                if (itemName == null) {
                    continue;
                }
                //////System.out.println("执行1");
                if ((itemName.contains("重生") && nEquip.getEquipLevel() < 4) || itemName.contains("永恒") && nEquip.getEquipLevel() < 6) {
                    //////System.out.println("执行2");
                    nEquip.gainItemExp(client, mobexp, itemName.contains("永恒"));
                }
                // ////System.out.println("执行3");
            }
        } catch (Exception ex) {
        }
    }

    /**
     *
     */
    public void reloadC() {
        client.getSession().write(MaplePacketCreator.getCharInfo(client.getPlayer()));
        client.getPlayer().getMap().removePlayer(client.getPlayer());
        client.getPlayer().getMap().addPlayer(client.getPlayer());
    }

    /**
     *
     */
    public void maxSkills() {
        for (ISkill sk : SkillFactory.getAllSkills()) {
            changeSkillLevel(sk, sk.getMaxLevel(), sk.getMaxLevel());
        }
    }

    /**
     *
     */
    public void UpdateCash() {
        getClient().getSession().write(MaplePacketCreator.showCharCash(this));
    }

    /**
     *
     * @param room
     * @return
     */
    public static String getAriantRoomLeaderName(int room) {
        return ariantroomleader[room];
    }

    /**
     *
     * @param room
     * @return
     */
    public static int getAriantSlotsRoom(int room) {
        return ariantroomslot[room];
    }

    /**
     *
     * @param room
     */
    public static void removeAriantRoom(int room) {
        ariantroomleader[room] = "";
        ariantroomslot[room] = 0;
    }

    /**
     *
     * @param room
     * @param charname
     */
    public static void setAriantRoomLeader(int room, String charname) {
        ariantroomleader[room] = charname;
    }

    /**
     *
     * @param room
     * @param slot
     */
    public static void setAriantSlotRoom(int room, int slot) {
        ariantroomslot[room] = slot;
    }

    /**
     *
     */
    public void addAriantScore() {
        ariantScore++;
    }

    /**
     *
     */
    public void resetAriantScore() {
        ariantScore = 0;
    }

    /**
     *
     * @return
     */
    public int getAriantScore() { // TODO: code entire score system
        return ariantScore;
    }

    /**
     *
     */
    public void updateAriantScore() {
        getMap().broadcastMessage(MaplePacketCreator.updateAriantScore(this.getName(), getAriantScore(), false));
    }

    /**
     *
     * @return
     */
    public int getAveragePartyLevel() {
        int averageLevel = 0, size = 0;
        for (MaplePartyCharacter pl : getParty().getMembers()) {
            averageLevel += pl.getLevel();
            size++;
        }
        if (size <= 0) {
            return level;
        }
        averageLevel /= size;
        return averageLevel;
    }

    /**
     *
     * @return
     */
    public int getAverageMapLevel() {
        int averageLevel = 0, size = 0;
        for (MapleCharacter pl : getMap().getCharacters()) {
            averageLevel += pl.getLevel();
            size++;
        }
        if (size <= 0) {
            return level;
        }
        averageLevel /= size;
        return averageLevel;
    }

    /**
     *
     * @param app
     */
    public void setApprentice(int app) {
        this.apprentice = app;
    }

    /**
     *
     * @return
     */
    public boolean hasApprentice() {
        return apprentice > 0;
    }

    /**
     *
     * @return
     */
    public int getMaster() {
        return this.master;
    }

    /**
     *
     * @return
     */
    public int getApprentice() {
        return this.apprentice;
    }

    /**
     *
     * @return
     */
    public MapleCharacter getApp() {
        return client.getChannelServer().getPlayerStorage().getCharacterById(this.apprentice);
    }

    /**
     *
     * @return
     */
    public MapleCharacter getMster() {
        return client.getChannelServer().getPlayerStorage().getCharacterById(this.master);
    }

    /**
     *
     * @param mstr
     */
    public void setMaster(int mstr) {
        this.master = mstr;
    }

    /**
     *
     * @param incluedEquip
     * @return
     */
    public MapleRing getMarriageRing(boolean incluedEquip) {
        MapleInventory iv = getInventory(MapleInventoryType.EQUIPPED);
        Collection<IItem> equippedC = iv.list();
        List<Item> equipped = new ArrayList<>(equippedC.size());
        MapleRing ring;
        for (IItem item : equippedC) {
            equipped.add((Item) item);
        }
        for (Item item : equipped) {
            if (item.getRing() != null) {
                ring = item.getRing();
                ring.setEquipped(true);
                if (GameConstants.isMarriageRing(item.getItemId())) {
                    return ring;
                }
            }
        }
        if (incluedEquip) {
            iv = getInventory(MapleInventoryType.EQUIP);
            for (IItem item : iv.list()) {
                if (item.getRing() != null && GameConstants.isMarriageRing(item.getItemId())) {
                    ring = item.getRing();
                    ring.setEquipped(false);
                    return ring;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param control
     */
    public void setDebugMessage(boolean control) {
        DebugMessage = control;
    }

    /**
     *
     * @return
     */
    public boolean getDebugMessage() {
        return DebugMessage;
    }

    /**
     *
     * @return
     */
    public int getNX() {
        return getCSPoints(1);
    }

    /**
     *
     * @param itemid
     * @return
     */
    public final boolean canHold(final int itemid) {
        return getInventory(GameConstants.getInventoryType(itemid)).getNextFreeSlot() > -1;
    }

    /**
     *
     * @param questID
     * @return
     */
    public int getIntRecord(int questID) {
        final MapleQuestStatus stat = getQuestNAdd(MapleQuest.getInstance(questID));
        if (stat.getCustomData() == null) {
            stat.setCustomData("0");
        }
        return Integer.parseInt(stat.getCustomData());
    }

    /**
     *
     * @param questID
     * @return
     */
    public int getIntNoRecord(int questID) {
        final MapleQuestStatus stat = getQuestNoAdd(MapleQuest.getInstance(questID));
        if (stat == null || stat.getCustomData() == null) {
            return 0;
        }
        return Integer.parseInt(stat.getCustomData());
    }

    /**
     *
     */
    public void updatePetEquip() {
        if (getIntNoRecord(122_221) > 0) {
            client.getSession().write(MaplePacketCreator.petAutoHP(getIntRecord(122_221)));
        }
        if (getIntNoRecord(122_222) > 0) {
            client.getSession().write(MaplePacketCreator.petAutoMP(getIntRecord(122_222)));
        }
    }

    /**
     *
     */
    public void spawnBomb() {
        final MapleMonster bomb = MapleLifeFactory.getMonster(9_300_166);
        bomb.changeLevel(250, true);
        getMap().spawnMonster_sSack(bomb, getPosition(), -2);
        EventTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                map.killMonster(bomb, client.getPlayer(), false, false, (byte) 1);
            }
        }, 10 * 1_000);
    }

    /**
     *
     * @return
     */
    public boolean isAriantPQMap() {
        switch (getMapId()) {
            case 980_010_101:
            case 980_010_201:
            case 980_010_301:
                return true;
        }
        return false;
    }
    private int MobVac = 0, MobVac2 = 0;

    /**
     *
     * @param type
     */
    public void addMobVac(int type) {
        if (type == 1) {
            MobVac++;
        } else if (type == 2) {
            MobVac2++;
        }
    }

    /**
     *
     * @param type
     * @return
     */
    public int getMobVac(int type) {
        switch (type) {
            case 1:
                return MobVac;
            case 2:
                return MobVac2;
            default:
                return 0;
        }
    }
    private int mount_id = 0;

    /**
     *
     * @return
     */
    public int getMountId() {
        return mount_id;
    }

    /**
     *
     * @param id
     */
    public void setMountId(int id) {
        mount_id = id;
    }

    /**
     *
     * @return
     */
    public int getVip() {
        return vip;
    }

    /**
     *
     * @param gain
     */
    public void gainVip(int gain) {
        this.vip += gain;
        // setDY(getDY() + gain);
    }

    /**
     *
     * @param id
     */
    public void setVip(int id) {
        vip = id;
    }

    /**
     *
     * @return
     */
    public int getSkillzq() {
        return skillzq;
    }

    /**
     *
     * @param gain
     */
    public void gainSkillzq(int gain) {
        this.skillzq += gain;
        // setDY(getDY() + gain);
    }

    /**
     *
     * @param id
     */
    public void setSkillzq(int id) {
        skillzq = id;
    }

    /**
     *
     * @param id
     * @param amount
     */
    public void gainIten(int id, int amount) {
        MapleInventoryManipulator.addById(getClient(), id, (short) amount, (byte) 0);
    }

    /**
     *
     * @return
     */
    public long getLastHM() {
        return lastGainHM;
    }

    /**
     *
     * @param newTime
     */
    public void setLastHM(long newTime) {
        lastGainHM = newTime;
    }

    /**
     *
     */
    public void 刷新状态() {
        this.getClient().getSession().write(MaplePacketCreator.getCharInfo(this));
        this.getMap().removePlayer(this);
        this.getMap().addPlayer(this);
        this.getClient().getSession().write(MaplePacketCreator.enableActions());
    }

    /**
     *
     * @return
     */
    public int Lianjie() {
        Connection con = DatabaseConnection.getConnection();
        int count = 0;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT count(*) as cc FROM accounts WHERE loggedin = 2");
            for (ResultSet re = ps.executeQuery(); re.next();) {
                count = re.getInt("cc");
            }

        } catch (SQLException ex) {
            java.util.logging.Logger.getLogger(EventInstanceManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    /**
     *
     * @param t
     * @return
     */
    public MapleTrait getTrait(MapleTraitType t) {
        return traits.get(t);
    }

    /**
     *
     * @return
     */
    public int getmodid() {//modid
        return modid;
    }

    /**
     *
     * @return
     */
    public int getmodsl() {//modsl
        return modsl;
    }

    /**
     *
     * @return
     */
    public int getitemsid() {//modid
        return itemsid;
    }

    /**
     *
     * @return
     */
    public int getitemsl() {//modsl
        return itemsl;
    }

    /**
     *
     * @param set
     */
    public void setmodid(int set) {
        this.modid = set;
    }

    /**
     *
     * @param set
     */
    public void setmodsl(int set) {
        this.modsl = set;
    }

    /**
     *
     * @param set
     */
    public void setitemsid(int set) {
        this.itemsid = set;
    }

    /**
     *
     * @param set
     */
    public void setitemsl(int set) {
        this.itemsl = set;
    }

    /**
     *
     * @param gain
     */
    public void gainmodid(int gain) {
        this.modid += gain;
    }

    /**
     *
     * @param gain
     */
    public void gainmodsl(int gain) {
        this.modsl += gain;
    }

    /**
     *
     * @param gain
     */
    public void gainitemsid(int gain) {
        this.itemsid += gain;
    }

    /**
     *
     * @param gain
     */
    public void gainitemsl(int gain) {
        this.itemsl += gain;
    }

    /**
     *
     * @param bossname
     * @return
     */
    public int getBossRank(String bossname) {
        try {
            int points = 0;
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM bossRank WHERE cname = ? AND bossname = ?")) {
                ps.setInt(1, id);
                ps.setString(2, bossname);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    points = rs.getInt("points");
                    Calendar sqlcal = Calendar.getInstance();
                    if ((sqlcal.get(5) + 1 <= Calendar.getInstance().get(5)) || (sqlcal.get(2) + 1 <= Calendar.getInstance().get(2)) || (sqlcal.get(1) + 1 <= Calendar.getInstance().get(1))) {
                        points = 0;
                        try (PreparedStatement psu = con.prepareStatement("UPDATE bossRank SET points = 0, WHERE cname = ? AND bossname= ?")) {
                            psu.setInt(1, id);
                            psu.setString(2, bossname);
                            psu.executeUpdate();
                        }
                    }
                } else {
                    try (PreparedStatement psu = con.prepareStatement("INSERT INTO bossRank (cname, bossname, points) VALUES (?, ?, ?)")) {
                        psu.setInt(1, id);
                        psu.setString(2, bossname);
                        psu.setInt(3, 0);
                        psu.executeUpdate();
                    }
                }
                rs.close();
            }
            return points;
        } catch (SQLException Ex) {
            //   bossname.error("Error while insert bossRank.", Ex);
        }
        return -1;
    }

    /**
     *
     * @param bossname
     * @param points
     */
    public void setBossRankCount(String bossname, int points) {
        Connection con1 = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con1.prepareStatement("insert into bossRank (cname, bossname, points) values (?,?,?)");
            ps.setInt(1, id);
            ps.setString(2, bossname);
            ps.setInt(3, points);
            ps.executeUpdate();
            ps.close();
        } catch (Exception Ex) {
            //   bossname.error("Error while insert bossRank.", Ex);
        }
    }

}
