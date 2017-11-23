package server;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleDisease;
import client.MapleStat;
import client.PlayerStats;
import client.SkillFactory;
import client.inventory.IItem;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.channel.ChannelServer;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleCarnivalFactory.MCSkill;
import server.Timer.BuffTimer;
import server.life.MapleMonster;
import server.maps.MapleDoor;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.MapleMist;
import server.maps.MapleSummon;
import server.maps.SummonMovementType;
import tools.MaplePacketCreator;
import tools.Pair;

/**
 *
 * @author zjj
 */
public class MapleStatEffect implements Serializable {

    private static final long serialVersionUID = 9_179_541_993_413_738_569L;
    private byte mastery, mhpR, mmpR, mobCount, attackCount, bulletCount;
    private short hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, mpCon, hpCon, damage, prop, ehp, emp, ewatk, ewdef, emdef;
    private double hpR, mpR;
    private int duration, sourceid, moveTo, x, y, z, itemCon, itemConNo, bulletConsume, moneyCon, cooldown, morphId = 0, expinc;
    private boolean overTime, skill, partyBuff = true;
    private List<Pair<MapleBuffStat, Integer>> statups;
    private Map<MonsterStatus, Integer> monsterStatus;
    private Point lt, rb;
    private int expBuff, itemup, mesoup, cashup, berserk, illusion, booster, berserk2, cp, nuffSkill;
    private byte level;
//    private List<Pair<Integer, Integer>> randomMorph;
    private List<MapleDisease> cureDebuffs;

    /**
     *
     * @param source
     * @param skillid
     * @param overtime
     * @param level
     * @return
     */
    public static final MapleStatEffect loadSkillEffectFromData(final MapleData source, final int skillid, final boolean overtime, final byte level) {
        return loadFromData(source, skillid, true, overtime, level);
    }

    /**
     *
     * @param source
     * @param itemid
     * @return
     */
    public static final MapleStatEffect loadItemEffectFromData(final MapleData source, final int itemid) {
        return loadFromData(source, itemid, false, false, (byte) 1);
    }

    private static final void addBuffStatPairToListIfNotZero(final List<Pair<MapleBuffStat, Integer>> list, final MapleBuffStat buffstat, final Integer val) {
        if (val != 0) {
            list.add(new Pair<>(buffstat, val));
        }
    }

    private static MapleStatEffect loadFromData(final MapleData source, final int sourceid, final boolean skill, final boolean overTime, final byte level) {
        final MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = level;
        if (source == null) {
            return ret;
        }
        ret.duration = MapleDataTool.getIntConvert("time", source, -1);
        ret.hp = (short) MapleDataTool.getInt("hp", source, 0);
        ret.hpR = MapleDataTool.getInt("hpR", source, 0) / 100.0;
        ret.mp = (short) MapleDataTool.getInt("mp", source, 0);
        ret.mpR = MapleDataTool.getInt("mpR", source, 0) / 100.0;
        ret.mhpR = (byte) MapleDataTool.getInt("mhpR", source, 0);
        ret.mmpR = (byte) MapleDataTool.getInt("mmpR", source, 0);
        ret.mpCon = (short) MapleDataTool.getInt("mpCon", source, 0);
        ret.hpCon = (short) MapleDataTool.getInt("hpCon", source, 0);
        ret.prop = (short) MapleDataTool.getInt("prop", source, 100);
        ret.cooldown = MapleDataTool.getInt("cooltime", source, 0);
        ret.expinc = MapleDataTool.getInt("expinc", source, 0);
        ret.morphId = MapleDataTool.getInt("morph", source, 0);
        ret.cp = MapleDataTool.getInt("cp", source, 0);
        ret.nuffSkill = MapleDataTool.getInt("nuffSkill", source, 0);
        ret.mobCount = (byte) MapleDataTool.getInt("mobCount", source, 1);

        if (skill) {
            switch (sourceid) {
                case 1_100_002:
                case 1_100_003:
                case 1_200_002:
                case 1_200_003:
                case 1_300_002:
                case 1_300_003:
                case 3_100_001:
                case 3_200_001:
                case 11_101_002:
                case 13_101_002:
                    ret.mobCount = 6;
                    break;
            }
        }

        /*
         * final MapleData randMorph = source.getChildByPath("morphRandom"); if
         * (randMorph != null) { for (MapleData data : randMorph.getChildren())
         * { ret.randomMorph.add(new Pair( MapleDataTool.getInt("morph", data,
         * 0), MapleDataTool.getIntConvert("prop", data, 0))); } }
         */
        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        } else {
            ret.duration *= 1_000; // items have their times stored in ms, of course
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph() || ret.isFinalAttack();
        }
        final ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<>();

        ret.mastery = (byte) MapleDataTool.getInt("mastery", source, 0);
        ret.watk = (short) MapleDataTool.getInt("pad", source, 0);
        ret.wdef = (short) MapleDataTool.getInt("pdd", source, 0);
        ret.matk = (short) MapleDataTool.getInt("mad", source, 0);
        ret.mdef = (short) MapleDataTool.getInt("mdd", source, 0);
        ret.ehp = (short) MapleDataTool.getInt("emhp", source, 0);
        ret.emp = (short) MapleDataTool.getInt("emmp", source, 0);
        ret.ewatk = (short) MapleDataTool.getInt("epad", source, 0);
        ret.ewdef = (short) MapleDataTool.getInt("epdd", source, 0);
        ret.emdef = (short) MapleDataTool.getInt("emdd", source, 0);
        ret.acc = (short) MapleDataTool.getIntConvert("acc", source, 0);
        ret.avoid = (short) MapleDataTool.getInt("eva", source, 0);
        ret.speed = (short) MapleDataTool.getInt("speed", source, 0);
        ret.jump = (short) MapleDataTool.getInt("jump", source, 0);
        ret.expBuff = MapleDataTool.getInt("expBuff", source, 0);
        ret.cashup = MapleDataTool.getInt("cashBuff", source, 0);
        ret.itemup = MapleDataTool.getInt("itemupbyitem", source, 0);
        ret.mesoup = MapleDataTool.getInt("mesoupbyitem", source, 0);
        ret.berserk = MapleDataTool.getInt("berserk", source, 0);
        ret.berserk2 = MapleDataTool.getInt("berserk2", source, 0);
        ret.booster = MapleDataTool.getInt("booster", source, 0);
        ret.illusion = MapleDataTool.getInt("illusion", source, 0);

        List<MapleDisease> cure = new ArrayList<>(5);
        if (MapleDataTool.getInt("poison", source, 0) > 0) {
            cure.add(MapleDisease.POISON);
        }
        if (MapleDataTool.getInt("seal", source, 0) > 0) {
            cure.add(MapleDisease.SEAL);
        }
        if (MapleDataTool.getInt("darkness", source, 0) > 0) {
            cure.add(MapleDisease.DARKNESS);
        }
        if (MapleDataTool.getInt("weakness", source, 0) > 0) {
            cure.add(MapleDisease.WEAKEN);
        }
        if (MapleDataTool.getInt("curse", source, 0) > 0) {
            cure.add(MapleDisease.CURSE);
        }
        ret.cureDebuffs = cure;

        final MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point) ltd.getData();
            ret.rb = (Point) source.getChildByPath("rb").getData();
        }

        ret.x = MapleDataTool.getInt("x", source, 0);
        ret.y = MapleDataTool.getInt("y", source, 0);
        ret.z = MapleDataTool.getInt("z", source, 0);
        ret.damage = (short) MapleDataTool.getIntConvert("damage", source, 100);
        ret.attackCount = (byte) MapleDataTool.getIntConvert("attackCount", source, 1);
        ret.bulletCount = (byte) MapleDataTool.getIntConvert("bulletCount", source, 1);
        ret.bulletConsume = MapleDataTool.getIntConvert("bulletConsume", source, 0);
        ret.moneyCon = MapleDataTool.getIntConvert("moneyCon", source, 0);

        ret.itemCon = MapleDataTool.getInt("itemCon", source, 0);
        ret.itemConNo = MapleDataTool.getInt("itemConNo", source, 0);
        ret.moveTo = MapleDataTool.getInt("moveTo", source, -1);

        Map<MonsterStatus, Integer> monsterStatus = new EnumMap<>(MonsterStatus.class);
        if (ret.overTime && ret.getSummonMovementType() == null) {
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WATK, Integer.valueOf(ret.watk));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.WDEF, Integer.valueOf(ret.wdef));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MATK, Integer.valueOf(ret.matk));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MDEF, Integer.valueOf(ret.mdef));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACC, Integer.valueOf(ret.acc));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.AVOID, Integer.valueOf(ret.avoid));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.SPEED, Integer.valueOf(ret.speed));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.JUMP, Integer.valueOf(ret.jump));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAXHP, (int) ret.mhpR);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MAXMP, (int) ret.mmpR);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.EXPRATE, ret.expBuff); // EXP
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ACASH_RATE, ret.cashup); // custom
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.DROP_RATE, ret.itemup * 200); // defaults to 2x
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.MESO_RATE, ret.mesoup * 200); // defaults to 2x
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BERSERK_FURY, ret.berserk2);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.PYRAMID_PQ, ret.berserk);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.BOOSTER, ret.booster);
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ILLUSION, ret.illusion);

            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_WATK, Integer.valueOf(ret.ewatk));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_WDEF, Integer.valueOf(ret.ewdef));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MDEF, Integer.valueOf(ret.emdef));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MAXHP, Integer.valueOf(ret.ehp));
            addBuffStatPairToListIfNotZero(statups, MapleBuffStat.ENHANCED_MAXMP, Integer.valueOf(ret.ehp));
        }
        if (skill) { // hack because we can't get from the datafile...
            switch (sourceid) {
                case 2_001_002: // magic guard
                case 12_001_001:
                case 22_111_001:
                    statups.add(new Pair<>(MapleBuffStat.MAGIC_GUARD, ret.x));
                    break;
                case 2_301_003: // invincible
                    statups.add(new Pair<>(MapleBuffStat.INVINCIBLE, ret.x));
                    break;
                case 35_120_000:
                case 35_001_002: //TEMP. mech
                    ret.duration = 60 * 120 * 1_000;
                    break;
                case 9_001_004: // hide
                    ret.duration = 60 * 120 * 1_000;
                    statups.add(new Pair<>(MapleBuffStat.DARKSIGHT, ret.x));
                    break;
                case 13_101_006: // Wind Walk
                case 4_001_003: // darksight
                case 14_001_003: // cygnus ds
                case 4_330_001:
                case 30_001_001: //resist beginner hide
                    statups.add(new Pair<>(MapleBuffStat.DARKSIGHT, ret.x));
                    break;
                case 4_211_003: // pickpocket
                    statups.add(new Pair<>(MapleBuffStat.PICKPOCKET, ret.x));
                    break;
                case 4_211_005: // mesoguard
                    statups.add(new Pair<>(MapleBuffStat.MESOGUARD, ret.x));
                    break;
                case 4_111_001: // mesoup
                    statups.add(new Pair<>(MapleBuffStat.MESOUP, ret.x));
                    break;
                case 4_111_002: // shadowpartner
                case 14_111_000: // cygnus
                    statups.add(new Pair<>(MapleBuffStat.SHADOWPARTNER, ret.x));
                    break;
                case 11_101_002: // All Final attack
                case 13_101_002:
                    statups.add(new Pair<>(MapleBuffStat.FINALATTACK, ret.x));
                    break;
                case 3_101_004: // soul arrow
                case 3_201_004:
                case 2_311_002: // mystic door - hacked buff icon
                case 13_101_003:
                case 33_101_003:
                case 8_001:
                case 10_008_001:
                case 20_008_001:
                case 20_018_001:
                case 30_008_001:
                    statups.add(new Pair<>(MapleBuffStat.SOULARROW, ret.x));
                    break;
                case 1_211_006: // wk charges
                case 1_211_003:
                case 1_211_004:
                case 1_211_005:
                case 1_211_008:
                case 1_211_007:
                case 1_221_003:
                case 1_221_004:
                case 11_111_007:
                case 21_111_005:
                case 15_101_006:
                    statups.add(new Pair<>(MapleBuffStat.WK_CHARGE, ret.x));
                    break;
                case 12_101_005:
                case 22_121_001: // Elemental Reset
                    statups.add(new Pair<>(MapleBuffStat.ELEMENT_RESET, ret.x));
                    break;
                case 3_121_008:
                    statups.add(new Pair<>(MapleBuffStat.CONCENTRATE, ret.x));
                    break;
                case 5_110_001: // Energy Charge
                case 15_100_004:
                    statups.add(new Pair<>(MapleBuffStat.ENERGY_CHARGE, 0));
                    break;
                case 1_101_005: // booster
                case 1_101_004:
                case 1_201_005:
                case 1_201_004:
                case 1_301_005:
                case 1_301_004:
                case 3_101_002:
                case 3_201_002:
                case 4_101_003:
                case 4_201_002:
                case 2_111_005: // spell booster, do these work the same?
                case 2_211_005:
                case 5_101_006:
                case 5_201_003:
                case 11_101_001:
                case 12_101_004:
                case 13_101_001:
                case 14_101_002:
                case 15_101_002:
                case 21_001_003: // Aran - Pole Arm Booster
                case 22_141_002: // Magic Booster
                case 4_301_002:
                case 32_101_005:
                case 33_001_003:
                case 35_101_006:
                case 35_001_003: //TEMP.BOOSTER
                    statups.add(new Pair<>(MapleBuffStat.BOOSTER, ret.x));
                    break;
                case 5_121_009:
                case 15_111_005:
                    statups.add(new Pair<>(MapleBuffStat.SPEED_INFUSION, ret.x));
                    break;
                case 4_321_000: //tornado spin uses same buffstats
                    ret.duration = 1_000;
                    statups.add(new Pair<>(MapleBuffStat.DASH_SPEED, 100 + ret.x));
                    statups.add(new Pair<>(MapleBuffStat.DASH_JUMP, ret.y)); //always 0 but its there
                    break;
                case 5_001_005: // Dash
                case 15_001_003:
                    statups.add(new Pair<>(MapleBuffStat.DASH_SPEED, ret.x * 2));
                    statups.add(new Pair<>(MapleBuffStat.DASH_JUMP, ret.y * 2));
                    break;
                case 1_101_007: // pguard
                case 1_201_007:
                    statups.add(new Pair<>(MapleBuffStat.POWERGUARD, ret.x));
                    break;
                case 32_111_004: //conversion
                    statups.add(new Pair<>(MapleBuffStat.CONVERSION, ret.x));
                    break;
                case 1_301_007: // hyper body
                case 9_001_008:
                case 8_003:
                case 10_008_003:
                case 20_008_003:
                case 20_018_003:
                case 30_008_003:
                    statups.add(new Pair<>(MapleBuffStat.MAXHP, ret.x));
                    statups.add(new Pair<>(MapleBuffStat.MAXMP, ret.y));
                    break;
                case 1_001: // recovery
                    statups.add(new Pair<>(MapleBuffStat.RECOVERY, ret.x));
                    break;
                case 1_111_002: // combo
                case 11_111_001: // combo
                    statups.add(new Pair<>(MapleBuffStat.COMBO, 1));
                    break;
                case 21_120_007: //combo barrier
                    statups.add(new Pair<>(MapleBuffStat.COMBO_BARRIER, ret.x));
                    break;
                case 5_211_006: // Homing Beacon
                case 5_220_011: // Bullseye
                case 22_151_002: //killer wings
                    ret.duration = 60 * 120_000;
                    statups.add(new Pair<>(MapleBuffStat.HOMING_BEACON, ret.x));
                    break;
                case 1_011: // Berserk fury
                case 10_001_011:
                case 20_001_011:
                case 20_011_011:
                case 30_001_011:
                    statups.add(new Pair<>(MapleBuffStat.BERSERK_FURY, 1));
                    break;
                case 1_010:
                case 10_001_010:// Invincible Barrier
                case 20_001_010:
                case 20_011_010:
                case 30_001_010:
                    statups.add(new Pair<>(MapleBuffStat.DIVINE_BODY, 1));
                    break;
                case 1_311_006: //dragon roar
                    ret.hpR = -ret.x / 100.0;
                    statups.add(new Pair<>(MapleBuffStat.DRAGON_ROAR, ret.y));
                    break;
                case 4_341_007:
                    statups.add(new Pair<>(MapleBuffStat.THORNS, ret.x << 8 | ret.y));
                    break;
                case 4_341_002:
                    ret.duration = 60 * 1_000;
                    ret.hpR = -ret.x / 100.0;
                    statups.add(new Pair<>(MapleBuffStat.FINAL_CUT, ret.y));
                    break;
                case 4_331_002:
                    statups.add(new Pair<>(MapleBuffStat.MIRROR_IMAGE, ret.x));
                    break;
                case 4_331_003:
                    ret.duration = 60 * 1_000;
                    statups.add(new Pair<>(MapleBuffStat.OWL_SPIRIT, ret.y));
                    break;
                case 1_311_008: // dragon blood
                    statups.add(new Pair<>(MapleBuffStat.DRAGONBLOOD, ret.x));
                    break;
                case 1_121_000: // maple warrior, all classes
                case 1_221_000:
                case 1_321_000:
                case 2_121_000:
                case 2_221_000:
                case 2_321_000:
                case 3_121_000:
                case 3_221_000:
                case 4_121_000:
                case 4_221_000:
                case 5_121_000:
                case 5_221_000:
                case 21_121_000: // Aran - Maple Warrior
                case 22_171_000:
                case 4_341_000:
                case 32_121_007:
                case 33_121_007:
                case 35_121_007:
                    statups.add(new Pair<>(MapleBuffStat.MAPLE_WARRIOR, ret.x));
                    break;
                case 15_111_006: //spark
                    statups.add(new Pair<>(MapleBuffStat.SPARK, ret.x));
                    break;
                case 3_121_002: // sharp eyes bow master
                case 3_221_002: // sharp eyes marksmen
                case 33_121_004:
                case 8_002:
                case 10_008_002:
                case 20_008_002:
                case 20_018_002:
                case 30_008_002:
                    statups.add(new Pair<>(MapleBuffStat.SHARP_EYES, ret.x << 8 | ret.y));
                    break;
                case 22_151_003: //magic resistance
                    statups.add(new Pair<>(MapleBuffStat.MAGIC_RESISTANCE, ret.x));
                    break;
                case 21_101_003: // Body Pressure
                    statups.add(new Pair<>(MapleBuffStat.BODY_PRESSURE, ret.x));
                    break;
                case 21_000_000: // Aran Combo
                    statups.add(new Pair<>(MapleBuffStat.ARAN_COMBO, 100));
                    break;
                case 21_100_005: // Combo Drain
                case 32_101_004:
                    statups.add(new Pair<>(MapleBuffStat.COMBO_DRAIN, ret.x));
                    break;
                case 21_111_001: // Smart Knockback
                    statups.add(new Pair<>(MapleBuffStat.SMART_KNOCKBACK, ret.x));
                    break;
                case 22_131_001: //magic shield
                    statups.add(new Pair<>(MapleBuffStat.MAGIC_SHIELD, ret.x));
                    break;
                case 22_181_003: //soul stone
                    statups.add(new Pair<>(MapleBuffStat.SOUL_STONE, 1));
                    break;
                case 4_001_002: // disorder
                case 14_001_002: // cygnus disorder
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                case 5_221_009: // Mind Control
                    monsterStatus.put(MonsterStatus.HYPNOTIZE, 1);
                    break;
                case 1_201_006: // threaten
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                case 1_211_002: // charged blow
                //case 1111008: // shout  屏蔽虎咆哮 防止掉线
                case 4_211_002: // assaulter
                // case 3101005: // arrow bomb  屏蔽爆炸箭 防止掉线
                case 1_111_005: // coma: sword
                case 1_111_006: // coma: axe
                case 4_221_007: // boomerang step
                case 5_101_002: // Backspin Blow
                case 5_101_003: // Double Uppercut
                case 5_121_004: // Demolition
                case 5_121_005: // Snatch
                case 5_121_007: // Barrage
                case 5_201_004: // pirate blank shot
                case 4_121_008: // Ninja Storm
                case 22_151_001:
                case 4_201_004: //steal, new
                case 33_101_001:
                case 33_101_002:
                case 32_111_010:
                case 32_121_004:
                case 33_111_002:
                case 33_121_002:
                case 35_101_003:
                case 35_111_015:
                case 5_111_002: //energy blast
                case 15_101_005:
                case 4_331_005:
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                case 4_321_002:
                    monsterStatus.put(MonsterStatus.DARKNESS, 1);
                    break;
                case 4_221_003:
                case 4_121_003:
                case 33_121_005:
                    monsterStatus.put(MonsterStatus.SHOWDOWN, ret.x);
                    monsterStatus.put(MonsterStatus.MDEF, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    break;
                // case 2201004: // cold beam   屏蔽冰冻术 防止掉线
                // case 2211002: // ice strike   屏蔽冰咆哮 防止掉线
                // case 3211003: // blizzard    屏蔽寒冰箭 防止掉线
                case 2_211_006: // il elemental compo
                // case 2221007: // Blizzard     屏蔽落霜冰破 防止掉线
                case 5_211_005: // Ice Splitter
                case 2_121_006: // Paralyze
                case 21_120_006: // Tempest
                case 22_121_000:
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    ret.duration *= 2; // freezing skills are a little strange
                    break;
                case 2_101_003: // fp slow
                case 2_201_003: // il slow
                case 12_101_001:
                case 22_141_003: // Slow
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                case 2_101_005: // poison breath
                case 2_111_006: // fp elemental compo
                case 2_121_003: // ice demon
                case 2_221_003: // fire demon
                //  case 3111003: //inferno, new  烈火箭移除 防止掉线
                case 22_161_002: //phantom imprint
                    monsterStatus.put(MonsterStatus.POISON, 1);
                    break;
                case 4_121_004: // Ninja ambush
                case 4_221_004:
                    monsterStatus.put(MonsterStatus.NINJA_AMBUSH, (int) ret.damage);
                    break;
                case 2_311_005:
                    monsterStatus.put(MonsterStatus.DOOM, 1);
                    break;
                case 32_111_006:
                    statups.add(new Pair<>(MapleBuffStat.REAPER, 1));
                    break;
                case 4_341_006:
                case 3_111_002: // puppet ranger
                case 3_211_002: // puppet sniper
                case 13_111_004: // puppet cygnus
                case 5_211_001: // Pirate octopus summon
                case 5_220_002: // wrath of the octopi
                case 33_111_003:
                    statups.add(new Pair<>(MapleBuffStat.PUPPET, 1));
                    break;
                case 3_211_005: // golden eagle
                case 3_111_005: // golden hawk
                case 33_111_005:
                case 35_111_002:
                    statups.add(new Pair<>(MapleBuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                case 3_221_005: // frostprey
                case 2_121_005: // elquines
                    statups.add(new Pair<>(MapleBuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    break;
                case 2_311_006: // summon dragon
                case 3_121_006: // phoenix
                case 2_221_005: // ifrit
                case 2_321_003: // bahamut
                case 1_321_007: // Beholder
                case 5_211_002: // Pirate bird summon
                case 11_001_004:
                case 12_001_004:
                case 12_111_004: // Itrit
                case 13_001_004:
                case 14_001_005:
                case 15_001_004:
                    statups.add(new Pair<>(MapleBuffStat.SUMMON, 1));
                    break;
                case 2_311_003: // hs
                case 9_001_002: // GM hs
                    statups.add(new Pair<>(MapleBuffStat.HOLY_SYMBOL, ret.x));
                    break;
                case 2_211_004: // il seal
                case 2_111_004: // fp seal
                case 12_111_002: // cygnus seal
                    monsterStatus.put(MonsterStatus.SEAL, 1);
                    break;
                case 4_111_003: // shadow web
                case 14_111_001:
                    monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                    break;
                case 4_121_006: // spirit claw
                    statups.add(new Pair<>(MapleBuffStat.SPIRIT_CLAW, 0));
                    break;
                case 2_121_004:
                case 2_221_004:
                case 2_321_004: // Infinity
                    statups.add(new Pair<>(MapleBuffStat.INFINITY, ret.x));
                    break;
                case 1_121_002:
                case 1_221_002:
                case 1_321_002: // Stance
                case 21_121_003: // Aran - Freezing Posture
                case 32_121_005:
                    statups.add(new Pair<>(MapleBuffStat.STANCE, (int) ret.prop));
                    break;
                case 1_005: // Echo of Hero
                case 10_001_005: // Cygnus Echo
                case 20_001_005: // Aran
                case 20_011_005: // Evan
                case 30_001_005:
                    statups.add(new Pair<>(MapleBuffStat.ECHO_OF_HERO, ret.x));
                    break;
                case 1_026: // Soaring
                case 10_001_026: // Soaring
                case 20_001_026: // Soaring
                case 20_011_026: // Soaring
                case 30_001_026:
                    ret.duration = 60 * 120 * 1_000; //because it seems to dispel asap.
                    statups.add(new Pair<>(MapleBuffStat.SOARING, 1));
                    break;
                case 2_121_002: // mana reflection
                case 2_221_002:
                case 2_321_002:
                    statups.add(new Pair<>(MapleBuffStat.MANA_REFLECTION, 1));
                    break;
                case 2_321_005: // holy shield
                    statups.add(new Pair<>(MapleBuffStat.HOLY_SHIELD, ret.x));
                    break;
                case 3_121_007: // Hamstring
                    statups.add(new Pair<>(MapleBuffStat.HAMSTRING, ret.x));
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                case 3_221_006: // Blind
                case 33_111_004:
                    statups.add(new Pair<>(MapleBuffStat.BLIND, ret.x));
                    monsterStatus.put(MonsterStatus.ACC, ret.x);
                    break;
                case 33_121_006: //feline berserk
                    statups.add(new Pair<>(MapleBuffStat.MAXHP, ret.x));
                    statups.add(new Pair<>(MapleBuffStat.WATK, ret.y));//temp
                    //statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.DASH_SPEED, ret.z));
                    break;
                case 32_001_003: //dark aura
                case 32_120_000:
                    ret.duration = 60 * 120 * 1_000; //because it seems to dispel asap.
                    statups.add(new Pair<>(MapleBuffStat.DARK_AURA, ret.x));
                    break;
                case 32_101_002: //blue aura
                case 32_110_000:
                    ret.duration = 60 * 120 * 1_000; //because it seems to dispel asap.
                    statups.add(new Pair<>(MapleBuffStat.BLUE_AURA, ret.x));
                    break;
                case 32_101_003: //yellow aura
                case 32_120_001:
                    ret.duration = 60 * 120 * 1_000; //because it seems to dispel asap.
                    statups.add(new Pair<>(MapleBuffStat.YELLOW_AURA, ret.x));
                    break;
                case 33_101_004: //it's raining mines
                    statups.add(new Pair<>(MapleBuffStat.RAINING_MINES, ret.x)); //x?
                    break;
                case 35_101_007: //perfect armor
                    ret.duration = 60 * 120 * 1_000;
                    statups.add(new Pair<>(MapleBuffStat.PERFECT_ARMOR, ret.x));
                    break;
                case 35_121_006: //satellite safety
                    ret.duration = 60 * 120 * 1_000;
                    statups.add(new Pair<>(MapleBuffStat.SATELLITESAFE_PROC, ret.x));
                    statups.add(new Pair<>(MapleBuffStat.SATELLITESAFE_ABSORB, ret.y));
                    break;
                case 35_001_001: //flame
                case 35_101_009:
                case 35_111_007: //TEMP
                    //pre-bb = 35111007,
                    ret.duration = 8_000;
                    statups.add(new Pair<>(MapleBuffStat.MECH_CHANGE, (int) level)); //ya wtf
                    break;
                case 35_121_013:
                //case 35111004: //siege
                case 35_101_002: //TEMP
                    ret.duration = 5_000;
                    statups.add(new Pair<>(MapleBuffStat.MECH_CHANGE, (int) level)); //ya wtf
                    break;
                case 35_121_005: //missile
                    ret.duration = 60 * 120 * 1_000;
                    statups.add(new Pair<>(MapleBuffStat.MECH_CHANGE, (int) level)); //ya wtf
                    break;
                default:
                    break;
            }
        }
        if (ret.isMonsterRiding()) {
            statups.add(new Pair<>(MapleBuffStat.MONSTER_RIDING, 1));
        }
        if (ret.isMorph() || ret.isPirateMorph()) {
            statups.add(new Pair<>(MapleBuffStat.MORPH, ret.getMorph()));
        }
        ret.monsterStatus = monsterStatus;
        statups.trimToSize();
        ret.statups = statups;

        return ret;
    }

    /**
     * @param applyto
     * @param obj
     * @param attack damage done by the skill
     */
    public final void applyPassive(final MapleCharacter applyto, final MapleMapObject obj) {
        if (makeChanceResult()) {
            switch (sourceid) { // MP eater
                case 2_100_000:
                case 2_200_000:
                case 2_300_000:
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    final MapleMonster mob = (MapleMonster) obj; // x is absorb percentage
                    if (!mob.getStats().isBoss()) {
                        final int absorbMp = Math.min((int) (mob.getMobMaxMp() * (getX() / 70.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.getStat().setMp((short) (applyto.getStat().getMp() + absorbMp));
                            applyto.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sourceid, 1));
                            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), sourceid, 1), false);
                        }
                    }
                    break;
            }
        }
    }

    /**
     *
     * @param chr
     * @return
     */
    public final boolean applyTo(MapleCharacter chr) {
        return applyTo(chr, chr, true, null, duration);
    }

    /**
     *
     * @param chr
     * @param pos
     * @return
     */
    public final boolean applyTo(MapleCharacter chr, Point pos) {
        return applyTo(chr, chr, true, pos, duration);
    }

    private final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos) {
        return applyTo(applyfrom, applyto, primary, pos, duration);
    }

    /**
     *
     * @param applyfrom
     * @param applyto
     * @param primary
     * @param pos
     * @param newDuration
     * @return
     */
    public final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos, int newDuration) {
        if (isHeal() && (applyfrom.getMapId() == 749_040_100 || applyto.getMapId() == 749_040_100)) {
            return false; //z
            //} else if (isSoaring() && !applyfrom.getMap().canSoar()) {
            //	return false;
        } else if (sourceid == 4_341_006 && applyfrom.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) == null) {
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false; //not working
        } else if (sourceid == 33_101_004 && applyfrom.getMap().isTown()) {
            applyfrom.dropMessage(5, "You may not use this skill in towns.");
            applyfrom.getClient().getSession().write(MaplePacketCreator.enableActions());
            return false; //not supposed to
        }
        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);

        final PlayerStats stat = applyto.getStat();
        if (primary) {
            if (itemConNo != 0 && !applyto.isClone()) {
                MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
            }
        } else if (!primary && isResurrection()) {
            hpchange = stat.getMaxHp();
            applyto.setStance(0); //TODO fix death bug, player doesnt spawn on other screen
        }
        if (isDispel() && makeChanceResult()) {
            applyto.dispelDebuffs();
        } else if (isHeroWill()) {
            applyto.dispelDebuff(MapleDisease.SEDUCE);
        } else if (cureDebuffs.size() > 0) {
            for (final MapleDisease debuff : cureDebuffs) {
                applyfrom.dispelDebuff(debuff);
            }
        } else if (isMPRecovery()) {
            final int toDecreaseHP = ((stat.getMaxHp() / 100) * 10);
            if (stat.getHp() > toDecreaseHP) {
                hpchange += -toDecreaseHP; // -10% of max HP
                mpchange += ((toDecreaseHP / 100) * getY());
            } else {
                hpchange = stat.getHp() == 1 ? 0 : stat.getHp() - 1;
            }
        }
        final List<Pair<MapleStat, Integer>> hpmpupdate = new ArrayList<>(2);
        if (hpchange != 0) {
            if (hpchange < 0 && (-hpchange) > stat.getHp() && !applyto.hasDisease(MapleDisease.ZOMBIFY)) {
                return false;
            }
            stat.setHp(stat.getHp() + hpchange);
        }
        if (mpchange != 0) {
            if (mpchange < 0 && (-mpchange) > stat.getMp()) {
                return false;
            }
            //short converting needs math.min cuz of overflow
            stat.setMp(stat.getMp() + mpchange);

            hpmpupdate.add(new Pair<>(MapleStat.MP, Integer.valueOf(stat.getMp())));
        }
        hpmpupdate.add(new Pair<>(MapleStat.HP, Integer.valueOf(stat.getHp())));

        applyto.getClient().getSession().write(MaplePacketCreator.updatePlayerStats(hpmpupdate, true, applyto.getJob()));

        if (expinc != 0) {
            applyto.gainExp(expinc, true, true, false);
//            applyto.getClient().getSession().write(MaplePacketCreator.showSpecialEffect(19));
        } else if (GameConstants.isMonsterCard(sourceid)) {
            applyto.getMonsterBook().addCard(applyto.getClient(), sourceid);
        } else if (isSpiritClaw() && !applyto.isClone()) {
            MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
            IItem item;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                item = use.getItem((byte) i);
                if (item != null) {
                    if (GameConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= 200) {
                        MapleInventoryManipulator.removeById(applyto.getClient(), MapleInventoryType.USE, item.getItemId(), 200, false, true);
                        break;
                    }
                }
            }
        } else if (cp != 0 && applyto.getCarnivalParty() != null) {
            applyto.getCarnivalParty().addCP(applyto, cp);
            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
            for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                chr.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
            }
        } else if (nuffSkill != 0 && applyto.getParty() != null) {
            final MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(nuffSkill);
            if (skil != null) {
                final MapleDisease dis = skil.getDisease();
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    if (chr.getParty() == null || (chr.getParty().getId() != applyto.getParty().getId())) {
                        if (skil.targetsAll || Randomizer.nextBoolean()) {
                            if (dis == null) {
                                chr.dispel();
                            } else if (skil.getSkill() == null) {
                                chr.giveDebuff(dis, 1, 30_000, MapleDisease.getByDisease(dis), 1);
                            } else {
                                chr.giveDebuff(dis, skil.getSkill());
                            }
                            if (!skil.targetsAll) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (overTime && !isEnergyCharge()) {
            applyBuffEffect(applyfrom, applyto, primary, newDuration);
        }
        if (skill) {
            // removeMonsterBuff(applyfrom);
        }
        if (primary) {
            if ((overTime || isHeal()) && !isEnergyCharge()) {
                applyBuff(applyfrom, newDuration);
            }
            if (isMonsterBuff()) {
                applyMonsterBuff(applyfrom);
            }
        }
        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(applyfrom, this, new Point(pos == null ? applyfrom.getPosition() : pos), summonMovementType);
            if (!tosummon.isPuppet()) {
                applyfrom.getCheatTracker().resetSummonAttack();
            }
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.getSummons().put(sourceid, tosummon);
            tosummon.addHP((short) x);
            if (isBeholder()) {
                tosummon.addHP((short) 1);
            }
            if (sourceid == 4_341_006) {
                applyfrom.cancelEffectFromBuffStat(MapleBuffStat.MIRROR_IMAGE);
            }
        } else if (isMagicDoor()) { // Magic Door
            MapleDoor door = new MapleDoor(applyto, new Point(applyto.getPosition()), sourceid); // Current Map door
            if (door.getTownPortal() != null) {

                applyto.getMap().spawnDoor(door);
                applyto.addDoor(door);

                MapleDoor townDoor = new MapleDoor(door); // Town door
                applyto.addDoor(townDoor);
                door.getTown().spawnDoor(townDoor);

                if (applyto.getParty() != null) { // update town doors
                    applyto.silentPartyUpdate();
                }
            } else {
                applyto.dropMessage(5, "你可能沒辦法使用傳送們因為村莊內禁止..");
            }

        } else if (isMist()) {
            final Rectangle bounds = calculateBoundingBox(pos != null ? pos : new Point(applyfrom.getPosition()), applyfrom.isFacingLeft());
            final MapleMist mist = new MapleMist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, getDuration(), false);
            /*
             * } else if (isMist()) { Rectangle bounds =
             * calculateBoundingBox(applyfrom.getPosition(),
             * applyfrom.isFacingLeft()); MapleMist mist = new MapleMist(bounds,
             * applyfrom, this); //applyfrom.getMap().spawnMist(mist,
             * getDuration(), sourceid == 2111003, false);
             * applyfrom.getMap().spawnMist(mist, getDuration(), false);
             */

        } else if (isTimeLeap()) { // Time Leap
            for (MapleCoolDownValueHolder i : applyto.getCooldowns()) {
                if (i.skillId != 5_121_010) {
                    applyto.removeCooldown(i.skillId);
                    applyto.getClient().getSession().write(MaplePacketCreator.skillCooldown(i.skillId, 0));
                }
            }
        } else {
            for (WeakReference<MapleCharacter> chrz : applyto.getClones()) {
                if (chrz.get() != null) {
                    applyTo(chrz.get(), chrz.get(), primary, pos, newDuration);
                }
            }
        }
        return true;
    }

    /**
     *
     * @param applyto
     * @return
     */
    public final boolean applyReturnScroll(final MapleCharacter applyto) {
        if (moveTo != -1) {
            MapleMap target;
            if (moveTo == 999_999_999) {
                target = applyto.getMap().getReturnMap();
            } else {
                target = ChannelServer.getInstance(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                if (target.getId() / 10_000_000 != 60 && applyto.getMapId() / 10_000_000 != 61) {
                    if (target.getId() / 10_000_000 != 21 && applyto.getMapId() / 10_000_000 != 20) {
                        if (target.getId() / 10_000_000 != 12) {
                            if (target.getId() / 10_000_000 != applyto.getMapId() / 10_000_000) {
                                return false;
                            }
                        }
                    }
                }
            }
            try {
                applyto.changeMap(target, target.getPortal(0));
            } catch (Exception ex) {
                applyto.dropMessage(5, "本地图目前尚未开放.");
                return false;
            }
            return true;
            //  }
        }
        return false;
    }

    private final boolean isSoulStone() {
        return skill && sourceid == 22_181_003;
    }

    private final void applyBuff(final MapleCharacter applyfrom, int newDuration) {
        if (isSoulStone()) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                    if (chr.getParty() != null && chr.getParty().equals(applyfrom.getParty()) && chr.isAlive()) {
                        membrs++;
                    }
                }
                List<MapleCharacter> awarded = new ArrayList<>();
                while (awarded.size() < Math.min(membrs, y)) {
                    for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                        if (chr.isAlive() && chr.getParty().equals(applyfrom.getParty()) && !awarded.contains(chr) && Randomizer.nextInt(y) == 0) {
                            awarded.add(chr);
                        }
                    }
                }
                for (MapleCharacter chr : awarded) {
                    applyTo(applyfrom, chr, false, null, newDuration);
                    chr.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sourceid, 2));
                    chr.getMap().broadcastMessage(chr, MaplePacketCreator.showBuffeffect(chr.getId(), sourceid, 2), false);
                }
            }
        } else if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff())) {
            final Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
            final List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));

            for (final MapleMapObject affectedmo : affecteds) {
                final MapleCharacter affected = (MapleCharacter) affectedmo;

                if (affected != applyfrom && (isGmBuff() || applyfrom.getParty().equals(affected.getParty()))) {
                    if ((isResurrection() && !affected.isAlive()) || (!isResurrection() && affected.isAlive())) {
                        applyTo(applyfrom, affected, false, null, newDuration);
                        affected.getClient().getSession().write(MaplePacketCreator.showOwnBuffEffect(sourceid, 2));
                        affected.getMap().broadcastMessage(affected, MaplePacketCreator.showBuffeffect(affected.getId(), sourceid, 2), false);
                    }
                    if (isTimeLeap()) {
                        for (MapleCoolDownValueHolder i : affected.getCooldowns()) {
                            if (i.skillId != 5_121_010) {
                                affected.removeCooldown(i.skillId);
                                affected.getClient().getSession().write(MaplePacketCreator.skillCooldown(i.skillId, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    private final void removeMonsterBuff(final MapleCharacter applyfrom) {
        List<MonsterStatus> cancel = new ArrayList<>();
        ;
        switch (sourceid) {
            case 1_111_007:
                cancel.add(MonsterStatus.WDEF);
                cancel.add(MonsterStatus.WEAPON_DEFENSE_UP);
                //cancel.add(MonsterStatus.WEAPON_IMMUNITY);
                break;
            case 1_211_009:
                cancel.add(MonsterStatus.MDEF);
                cancel.add(MonsterStatus.MAGIC_DEFENSE_UP);
                //cancel.add(MonsterStatus.MAGIC_IMMUNITY);
                break;
            case 1_311_007:
                cancel.add(MonsterStatus.WATK);
                cancel.add(MonsterStatus.WEAPON_ATTACK_UP);
                cancel.add(MonsterStatus.MATK);
                cancel.add(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            default:
                return;
        }
        final Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (MonsterStatus stat : cancel) {
                    ((MapleMonster) mo).cancelStatus(stat);
                }
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    private final void applyMonsterBuff(final MapleCharacter applyfrom) {
        final Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (Map.Entry<MonsterStatus, Integer> stat : getMonsterStati().entrySet()) {
                    ((MapleMonster) mo).applyStatus(applyfrom, new MonsterStatusEffect(stat.getKey(), stat.getValue(), sourceid, null, false), isPoison(), getDuration(), false);
                }
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    private final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft) {
        if (lt == null || rb == null) {
            return new Rectangle(posFrom.x, posFrom.y, facingLeft ? 1 : -1, 1);
        }
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }

    /**
     *
     * @param d
     */
    public final void setDuration(int d) {
        this.duration = d;
    }

    /**
     *
     * @param chr
     * @param starttime
     */
    public final void silentApplyBuff(final MapleCharacter chr, final long starttime) {
        final int localDuration = alchemistModifyVal(chr, duration, false);
        chr.registerEffect(this, starttime, BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime),
                ((starttime + localDuration) - System.currentTimeMillis())));

        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(chr, this, chr.getPosition(), summonMovementType);
            if (!tosummon.isPuppet()) {
                chr.getCheatTracker().resetSummonAttack();
                chr.getMap().spawnSummon(tosummon);
                chr.getSummons().put(sourceid, tosummon);
                tosummon.addHP((short) x);
                if (isBeholder()) {
                    tosummon.addHP((short) 1);
                }
            }
        }
    }

    /**
     *
     * @param applyto
     * @param combo
     */
    public final void applyComboBuffA(final MapleCharacter applyto, short combo) {
        final ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<>();
        //  final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ARAN_COMBO, (int) combo));
        //applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, 99999, stat, this)); // Hackish timing, todo find out
        // statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.矛连击强化2, Integer.valueOf(combo)));
        statups.add(new Pair<>(MapleBuffStat.SHARP_EYES, Integer.valueOf(x << 8 | y)));
        //  statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.WDEF, Integer.valueOf(combo / 2)));
        //  statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MDEF, Integer.valueOf(combo / 2)));
        applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, 29_999, statups, this)); // Hackish timing, todo find out

        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, ((starttime + 29_999) - System.currentTimeMillis()));
        applyto.registerEffect(this, starttime, schedule);
    }

    /**
     *
     * @param applyto
     * @param combo
     */
    public final void applyComboBuff(final MapleCharacter applyto, short combo) {
        final ArrayList<Pair<MapleBuffStat, Integer>> statups = new ArrayList<>();
        //  final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ARAN_COMBO, (int) combo));
        //applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, 99999, stat, this)); // Hackish timing, todo find out
        // statups.add(new Pair<MapleBuffStat, Integer>(MapleBuffStat.矛连击强化2, Integer.valueOf(combo)));
        statups.add(new Pair<>(MapleBuffStat.矛连击强化, Integer.valueOf(combo / 5)));
        statups.add(new Pair<>(MapleBuffStat.WDEF, Integer.valueOf(combo / 2)));
        statups.add(new Pair<>(MapleBuffStat.MDEF, Integer.valueOf(combo / 2)));
        applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(this.sourceid, 29_999, statups, this)); // Hackish timing, todo find out

        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, ((starttime + 29_999) - System.currentTimeMillis()));
        applyto.registerEffect(this, starttime, schedule);
    }

    /**
     *
     * @param applyto
     * @param infinity
     */
    public final void applyEnergyBuff(final MapleCharacter applyto, final boolean infinity) {
        final List<Pair<MapleBuffStat, Integer>> stat = this.statups;

        final long starttime = System.currentTimeMillis();
        if (infinity) {
            applyto.setBuffedValue(MapleBuffStat.ENERGY_CHARGE, 0);
            //  this.statups = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.ENERGY_CHARGE, duration));
            //this.statups = stat;
            // List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, duration));
            applyto.getClient().getSession().write(MaplePacketCreator.能量条(stat, duration / 1_000)); //????????????????
            //       applyto.getClient().getSession().write(MaplePacketCreator.giveEnergyChargeTest(0, duration / 1000));
            applyto.registerEffect(this, starttime, null);
        } else {
            applyto.cancelEffect(this, true, -1);
            //          applyto.getClient().getSession().write(MaplePacketCreator.能量条(applyto.getId(), duration / 1000)); //????????????????
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveEnergyChargeTest(applyto.getId(), 10_000, duration / 1_000), false);
            final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
            final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, ((starttime + duration) - System.currentTimeMillis()));
            this.statups = Collections.singletonList(new Pair<>(MapleBuffStat.ENERGY_CHARGE, 10_000));
            applyto.registerEffect(this, starttime, schedule);
            this.statups = stat;
        }
    }

    private final void applyBuffEffect(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final int newDuration) {
        int localDuration = newDuration;
        // localDuration = 5000;
        if (primary) {
            localDuration = alchemistModifyVal(applyfrom, localDuration, false);
            applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showBuffeffect(applyto.getId(), sourceid, 1), false);
        }
        List<Pair<MapleBuffStat, Integer>> localstatups = statups;
        boolean normal = true;
        switch (sourceid) {
            case 5_001_005: // Dash
            case 5_121_009: // Speed Infusion
            case 15_111_005:

            case 15_001_003: {
                applyto.getClient().getSession().write(MaplePacketCreator.givePirate(statups, localDuration / 1_000, sourceid));
                // applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignPirate(statups, localDuration / 1000, applyto.getId(), sourceid), false);
                normal = false;
                break;
            }
            case 5_211_006: // Homing Beacon
            case 22_151_002: //killer wings
            case 5_220_011: {// Bullseye
                if (applyto.getLinkMid() > 0) {
                    applyto.getClient().getSession().write(MaplePacketCreator.cancelHoming());
                    applyto.getClient().getSession().write(MaplePacketCreator.giveHoming(sourceid, applyto.getLinkMid()));
                } else {
                    return;
                }
                normal = false;
                break;
            }
            case 13_101_006:
            case 4_330_001:
            case 4_001_003:
            case 14_001_003: { // Dark Sight
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.DARKSIGHT, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            //case 22131001: {//magic shield
            //final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MAGIC_SHIELD, x));
            //applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
            //break;
            //}
            case 32_001_003: //dark aura
            case 32_120_000: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.DARK_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.BLUE_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.YELLOW_AURA);
                break;
            }
            case 32_101_002: //blue aura
            case 32_110_000: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.BLUE_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.YELLOW_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.DARK_AURA);
                break;
            }
            case 32_101_003: //yellow aura
            case 32_120_001: {
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.YELLOW_AURA, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.BLUE_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStat.DARK_AURA);
                break;
            }
            /*
             * case 1211008: case 1211007: { //lightning if
             * (applyto.getBuffedValue(MapleBuffStat.WK_CHARGE) != null &&
             * applyto.getBuffSource(MapleBuffStat.WK_CHARGE) != sourceid) {
             * localstatups = Collections.singletonList(new Pair<MapleBuffStat,
             * Integer>(MapleBuffStat.LIGHTNING_CHARGE, 1)); }
             * applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(sourceid,
             * localDuration, localstatups, this)); normal = false; break; }            /*
             * case 1211008: case 1211007: { //lightning if
             * (applyto.getBuffedValue(MapleBuffStat.WK_CHARGE) != null &&
             * applyto.getBuffSource(MapleBuffStat.WK_CHARGE) != sourceid) {
             * localstatups = Collections.singletonList(new Pair<MapleBuffStat,
             * Integer>(MapleBuffStat.LIGHTNING_CHARGE, 1)); }
             * applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(sourceid,
             * localDuration, localstatups, this)); normal = false; break; }
             */

            case 35_001_001: //flame
            case 35_101_009:
            case 35_111_007: //TEMP
            case 35_101_002: //TEMP
            case 35_121_013:
            //  case 35111004: siege
            case 35_121_005: { //missile
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.MECH_CHANGE, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 1_111_002:
            case 11_111_001: { // Combo
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.COMBO, 1));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 3_101_004:
            case 3_201_004:
            case 13_101_003: { // Soul Arrow
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.SOULARROW, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 4_111_002:
            case 14_111_000: { // Shadow Partne
                final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.SHADOWPARTNER, 0));
                applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                break;
            }
            case 15_111_006: { // Spark
                localstatups = Collections.singletonList(new Pair<>(MapleBuffStat.SPARK, x));
                applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }

            case 1_121_010: // Enrage
                applyto.handleOrbconsume();
                break;
            default:
                if (isMorph() || isPirateMorph()) {
                    final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.MORPH, Integer.valueOf(getMorph(applyto))));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                } else if (isMonsterSkill()) {
                    if (applyto.getSkillzq() <= 0) {
                        return;
                    }
                    final int mountid = parseMountInfoA(applyto, sourceid, applyto.getSkillzq());
                    //  final int mountid2 = parseMountInfo_Pure(applyto, sourceid);
                    if (mountid != 0) {
                        final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.MONSTER_RIDING, 0));
                        //applyto.getClient().getSession().write(MaplePacketCreator.cancelBuff(null));
                        applyto.getClient().getSession().write(MaplePacketCreator.giveMount(applyto, mountid, sourceid, stat));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMonsterRiding(applyto.getId(), stat, mountid, sourceid), false);
                    } else {
                        return;
                    }
                    normal = false;
                    /* } else if (isBattleShip()) {
                    int ridingLevel = 1932000;
                        final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MONSTER_RIDING, 0));
                        applyto.getClient().getSession().write(MaplePacketCreator.cancelBuff(null));
                        applyto.getClient().getSession().write(MaplePacketCreator.giveMount(applyto,ridingLevel, sourceid, stat));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMonsterRiding(applyto.getId(), stat, ridingLevel, sourceid), false);
                     */
                } else if (isMonsterRiding()) {
                    localDuration = 2_100_000_000;
                    int mountid = parseMountInfo(applyto, sourceid);
                    int mountid2 = parseMountInfo_Pure(applyto, sourceid);
                    if (sourceid == 1_013 && applyto.getMountId() != 0) {
                        mountid = applyto.getMountId();;
                        mountid2 = applyto.getMountId();
                    }
                    if (mountid != 0 && mountid2 != 0) {
                        final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.MONSTER_RIDING, 0));
                        applyto.cancelEffectFromBuffStat(MapleBuffStat.POWERGUARD);
                        applyto.cancelEffectFromBuffStat(MapleBuffStat.MANA_REFLECTION);
                        applyto.getClient().getSession().write(MaplePacketCreator.giveMount(applyto, mountid2, sourceid, stat));
                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMonsterRiding(applyto.getId(), stat, mountid, sourceid), false);
                    } else {
                        return;
                    }
                    normal = false;
//                    final int mountid = parseMountInfo(applyto, sourceid);
//                    final int mountid2 = parseMountInfo_Pure(applyto, sourceid);
//                    if (mountid != 0 && mountid2 != 0) {
//                        final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.MONSTER_RIDING, 0));
//                        //applyto.getClient().getSession().write(MaplePacketCreator.cancelBuff(null));
//                        applyto.getClient().getSession().write(MaplePacketCreator.giveMount(applyto, mountid, sourceid, stat));
//                        applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.showMonsterRiding(applyto.getId(), stat, mountid, sourceid), false);
//                    } else {
//                        return;
//                    }
//                    normal = false;
                    /*
                     * } else if (isBattleShip()) { int ridingLevel = 1932000;
                     * final List<Pair<MapleBuffStat, Integer>> stat =
                     * Collections.singletonList(new Pair<MapleBuffStat,
                     * Integer>(MapleBuffStat.MONSTER_RIDING, 0));
                     * applyto.getClient().getSession().write(MaplePacketCreator.cancelBuff(null));
                     * applyto.getClient().getSession().write(MaplePacketCreator.giveMount(applyto,ridingLevel,
                     * sourceid, stat));
                     * applyto.getMap().broadcastMessage(applyto,
                     * MaplePacketCreator.showMonsterRiding(applyto.getId(),
                     * stat, ridingLevel, sourceid), false);
                     */
                } else if (isSoaring()) {
                    localstatups = Collections.singletonList(new Pair<>(MapleBuffStat.SOARING, 1));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), localstatups, this), false);
                    applyto.getClient().getSession().write(MaplePacketCreator.giveBuff(sourceid, localDuration, localstatups, this));
                    // normal = false;
                    //} else if (berserk > 0) {
                    //    final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<MapleBuffStat, Integer>(MapleBuffStat.PYRAMID_PQ, berserk));
                    //    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isBerserkFury() || berserk2 > 0) {
                    final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.BERSERK_FURY, 1));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                } else if (isDivineBody()) {
                    final List<Pair<MapleBuffStat, Integer>> stat = Collections.singletonList(new Pair<>(MapleBuffStat.DIVINE_BODY, 1));
                    applyto.getMap().broadcastMessage(applyto, MaplePacketCreator.giveForeignBuff(applyto, applyto.getId(), stat, this), false);
                }
                break;
        }
        if (!isMonsterRiding_()) {
            applyto.cancelEffect(this, true, -1, localstatups);
        }
        // Broadcast effect to self
        if (normal && statups.size() > 0) {
            applyto.getClient().getSession().write(MaplePacketCreator.giveBuff((skill ? sourceid : -sourceid), localDuration, statups, this));
        }
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
        //System.out.println("Started effect " + sourceid + ". Duration: " + localDuration + ", Actual Duration: " + (((starttime + localDuration) - System.currentTimeMillis())));
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, ((starttime + localDuration) - System.currentTimeMillis()));
        applyto.registerEffect(this, starttime, schedule, localstatups);
    }

    /**
     *
     * @param player
     * @param skillid
     * @return
     */
    public static final int parseMountInfo(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 1_004: // Monster riding
            case 10_001_004:
            case 20_001_004:
            case 20_011_004:
            case 30_001_004:
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (-118)) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (-119)) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (-118)).getItemId();
                }
                return parseMountInfo_Pure(player, skillid);
            default:
                return GameConstants.getMountItem(skillid);
        }
    }

    /**
     *
     * @param player
     * @param skillid
     * @param s
     * @return
     */
    public static final int parseMountInfoA(final MapleCharacter player, final int skillid, int s) {
        switch (skillid) {
            case 1_017: // Monster riding
            case 10_001_019:
            case 20_001_019:
                return GameConstants.getMountS(s);
            default:
                return GameConstants.getMountS(s);
        }
    }

    /**
     *
     * @param player
     * @param skillid
     * @return
     */
    public static final int parseMountInfo_Pure(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 80_001_000:
            case 1_004: // Monster riding
            case 11_004: // Monster riding
            case 10_001_004:
            case 20_001_004:
            case 20_011_004:
            case 20_021_004:
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (-18)) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (-19)) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (-18)).getItemId();
                }
                return 0;
            default:
                return GameConstants.getMountItem(skillid);
        }
    }

    private final int calcHPChange(final MapleCharacter applyfrom, final boolean primary) {
        int hpchange = 0;
        if (hp != 0) {
            if (!skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, hp, true);
                } else {
                    hpchange += hp;
                }
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange /= 2;
                }
            } else { // assumption: this is heal
                hpchange += makeHealHP(hp / 100.0, applyfrom.getStat().getTotalMagic(), 3, 5);
                if (applyfrom.hasDisease(MapleDisease.ZOMBIFY)) {
                    hpchange = -hpchange;
                }
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getStat().getCurrentMaxHp() * hpR) / (applyfrom.hasDisease(MapleDisease.ZOMBIFY) ? 2 : 1);
        }
        // actually receivers probably never get any hp when it's not heal but whatever
        if (primary) {
            if (hpCon != 0) {
                hpchange -= hpCon;
            }
        }
        switch (this.sourceid) {
            case 4_211_001: // Chakra
                final PlayerStats stat = applyfrom.getStat();
                int v42 = getY() + 100;
                int v38 = Randomizer.rand(1, 100) + 100;
                hpchange = (int) ((v38 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
                hpchange += makeHealHP(getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
                break;
        }
        return hpchange;
    }

    private static final int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    private static final int getElementalAmp(final int job) {
        switch (job) {
            case 211:
            case 212:
                return 2_110_001;
            case 221:
            case 222:
                return 2_210_001;
            case 1_211:
            case 1_212:
                return 12_110_001;
            case 2_215:
            case 2_216:
            case 2_217:
            case 2_218:
                return 22_150_000;
        }
        return -1;
    }

    private final int calcMPChange(final MapleCharacter applyfrom, final boolean primary) {
        int mpchange = 0;
        if (mp != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, mp, true);
            } else {
                mpchange += mp;
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getStat().getCurrentMaxMp() * mpR);
        }
        if (primary) {
            if (mpCon != 0) {
                double mod = 1.0;

                final int ElemSkillId = getElementalAmp(applyfrom.getJob());
                if (ElemSkillId != -1) {
                    final ISkill amp = SkillFactory.getSkill(ElemSkillId);
                    final int ampLevel = applyfrom.getSkillLevel(amp);
                    if (ampLevel > 0) {
                        MapleStatEffect ampStat = amp.getEffect(ampLevel);
                        mod = ampStat.getX() / 100.0;
                    }
                }
                final Integer Concentrate = applyfrom.getBuffedSkill_X(MapleBuffStat.CONCENTRATE);
                final int percent_off = applyfrom.getStat().mpconReduce + (Concentrate == null ? 0 : Concentrate);
                if (applyfrom.getBuffedValue(MapleBuffStat.INFINITY) != null) {
                    mpchange = 0;
                } else {
                    mpchange -= (mpCon - (mpCon * percent_off / 100)) * mod;
                }
            }
        }
        return mpchange;
    }

    private final int alchemistModifyVal(final MapleCharacter chr, final int val, final boolean withX) {
        if (!skill) {
            int offset = chr.getStat().RecoveryUP;
            final MapleStatEffect alchemistEffect = getAlchemistEffect(chr);
            if (alchemistEffect != null) {
                offset += (withX ? alchemistEffect.getX() : alchemistEffect.getY());
            } else {
                offset += 100;
            }
            return (val * offset / 100);
        }
        return val;
    }

    private final MapleStatEffect getAlchemistEffect(final MapleCharacter chr) {
        ISkill al;
        switch (chr.getJob()) {
            case 411:
            case 412:
                al = SkillFactory.getSkill(4_110_000);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
            case 1_411:
            case 1_412:
                al = SkillFactory.getSkill(14_110_003);
                if (chr.getSkillLevel(al) <= 0) {
                    return null;
                }
                return al.getEffect(chr.getSkillLevel(al));
        }
        if (GameConstants.isResist(chr.getJob())) {
            al = SkillFactory.getSkill(30_000_002);
            if (chr.getSkillLevel(al) <= 0) {
                return null;
            }
            return al.getEffect(chr.getSkillLevel(al));
        }
        return null;
    }

    /**
     *
     * @param newid
     */
    public final void setSourceId(final int newid) {
        sourceid = newid;
    }

    private final boolean isGmBuff() {
        switch (sourceid) {
            case 1_005: // echo of hero acts like a gm buff
            case 10_001_005: // cygnus Echo
            case 20_001_005: // Echo
            case 20_011_005:
            case 30_001_005:
            case 9_001_000: // GM dispel
            case 9_001_001: // GM haste
            case 9_001_002: // GM Holy Symbol
            case 9_001_003: // GM Bless
            case 9_001_005: // GM resurrection
            case 9_001_008: // GM Hyper body
                return true;
            default:
                return false;
        }
    }

    private final boolean isEnergyCharge() {
        return skill && (sourceid == 5_110_001 || sourceid == 15_100_004);
    }

    private final boolean isMonsterBuff() {
        switch (sourceid) {
            case 1_201_006: // threaten
            case 2_101_003: // fp slow
            case 2_201_003: // il slow
            case 12_101_001: // cygnus slow
            case 2_211_004: // il seal
            case 2_111_004: // fp seal
            case 12_111_002: // cygnus seal
            case 2_311_005: // doom
            case 4_111_003: // shadow web
            case 14_111_001: // cygnus web
            case 4_121_004: // Ninja ambush
            case 4_221_004: // Ninja ambush
            case 22_151_001:
            case 22_141_003:
            case 22_121_000:
            case 22_161_002:
            case 4_321_002:
                return skill;
        }
        return false;
    }

    /**
     *
     * @param pb
     */
    public final void setPartyBuff(boolean pb) {
        this.partyBuff = pb;
    }

    private final boolean isPartyBuff() {
        if (lt == null || rb == null || !partyBuff) {
            return isSoulStone();
        }
        switch (sourceid) {
            case 1_211_003:
            case 1_211_004:
            case 1_211_005:
            case 1_211_006:
            case 1_211_007:
            case 1_211_008:
            case 1_221_003:
            case 1_221_004:
            case 11_111_007:
            case 12_101_005:
            case 4_311_001:
                return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public final boolean isHeal() {
        return sourceid == 2_301_002 || sourceid == 9_101_000;
    }

    /**
     *
     * @return
     */
    public final boolean isResurrection() {
        return sourceid == 9_001_005 || sourceid == 2_321_006;
    }

    /**
     *
     * @return
     */
    public final boolean isTimeLeap() {
        return sourceid == 5_121_010;
    }

    /**
     *
     * @return
     */
    public final short getHp() {
        return hp;
    }

    /**
     *
     * @return
     */
    public final short getMp() {
        return mp;
    }

    /**
     *
     * @return
     */
    public final byte getMastery() {
        return mastery;
    }

    /**
     *
     * @return
     */
    public final short getWatk() {
        return watk;
    }

    /**
     *
     * @return
     */
    public final short getMatk() {
        return matk;
    }

    /**
     *
     * @return
     */
    public final short getWdef() {
        return wdef;
    }

    /**
     *
     * @return
     */
    public final short getMdef() {
        return mdef;
    }

    /**
     *
     * @return
     */
    public final short getAcc() {
        return acc;
    }

    /**
     *
     * @return
     */
    public final short getAvoid() {
        return avoid;
    }

    /**
     *
     * @return
     */
    public final short getHands() {
        return hands;
    }

    /**
     *
     * @return
     */
    public final short getSpeed() {
        return speed;
    }

    /**
     *
     * @return
     */
    public final short getJump() {
        return jump;
    }

    /**
     *
     * @return
     */
    public final int getDuration() {
        return duration;
    }

    /**
     *
     * @return
     */
    public final boolean isOverTime() {
        return overTime;
    }

    /**
     *
     * @return
     */
    public final List<Pair<MapleBuffStat, Integer>> getStatups() {
        return statups;
    }

    /**
     *
     * @param effect
     * @return
     */
    public final boolean sameSource(final MapleStatEffect effect) {
        return effect != null && this.sourceid == effect.sourceid && this.skill == effect.skill;
    }

    /**
     *
     * @return
     */
    public final int getX() {
        return x;
    }

    /**
     *
     * @return
     */
    public final int getY() {
        return y;
    }

    /**
     *
     * @return
     */
    public final int getZ() {
        return z;
    }

    /**
     *
     * @return
     */
    public final short getDamage() {
        return damage;
    }

    /**
     *
     * @return
     */
    public final byte getAttackCount() {
        return attackCount;
    }

    /**
     *
     * @return
     */
    public final byte getBulletCount() {
        return bulletCount;
    }

    /**
     *
     * @return
     */
    public final int getBulletConsume() {
        return bulletConsume;
    }

    /**
     *
     * @return
     */
    public final byte getMobCount() {
        return mobCount;
    }

    /**
     *
     * @return
     */
    public final int getMoneyCon() {
        return moneyCon;
    }

    /**
     *
     * @return
     */
    public final int getCooldown() {
        return cooldown;
    }

    /**
     *
     * @return
     */
    public final Map<MonsterStatus, Integer> getMonsterStati() {
        return monsterStatus;
    }

    /**
     *
     * @return
     */
    public final int getBerserk() {
        return berserk;
    }

    /**
     *
     * @return
     */
    public final boolean isHide() {
        return skill && sourceid == 9_001_004;
    }

    /**
     *
     * @return
     */
    public final boolean isDragonBlood() {
        return skill && sourceid == 1_311_008;
    }

    /**
     *
     * @return
     */
    public final boolean isBerserk() {
        return skill && sourceid == 1_320_006;
    }

    /**
     *
     * @return
     */
    public final boolean isBeholder() {
        return skill && sourceid == 1_321_007;
    }

    /**
     *
     * @return
     */
    public final boolean isMPRecovery() {
        return skill && sourceid == 5_101_005;
    }

    /**
     *
     * @return
     */
    public final boolean isMonsterRiding_() {
        return skill && (sourceid == 1_004 || sourceid == 10_001_004 || sourceid == 20_001_004 || sourceid == 20_011_004 || sourceid == 30_001_004);
    }

    /**
     *
     * @return
     */
    public final boolean isMonsterRiding() {
        return skill && (isMonsterRiding_() || GameConstants.getMountItem(sourceid) != 0);
    }

    /**
     *
     * @return
     */
    public final boolean isMonsterSkill() {
        return skill && sourceid == 1_017 || sourceid == 20_001_019 || sourceid == 10_001_019;
    }

    /**
     *
     * @return
     */
    public final boolean isMagicDoor() {
        return skill && (sourceid == 2_311_002 || sourceid == 8_001 || sourceid == 10_008_001 || sourceid == 20_008_001 || sourceid == 20_018_001 || sourceid == 30_008_001);
    }

    /**
     *
     * @return
     */
    public final boolean isMesoGuard() {
        return skill && sourceid == 4_211_005;
    }

    /**
     *
     * @return
     */
    public final boolean isCharge() {
        switch (sourceid) {
            case 1_211_003:
            case 1_211_008:
            case 11_111_007:
            case 12_101_005:
            case 15_101_006:
            case 21_111_005:
                return skill;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final boolean isPoison() {
        switch (sourceid) {
            case 2_111_003:
            case 2_101_005:
            case 2_111_006:
            case 2_121_003:
            case 2_221_003:
            case 12_111_005: // Flame gear
            case 3_111_003: //inferno, new
            case 22_161_002: //phantom imprint
                return skill;
        }
        return false;
    }

    /*
     * public boolean isPoison() { return skill && (sourceid == 2111003 ||
     * sourceid == 4221006 || sourceid == 12111005 || sourceid == 14111006 ||
     * sourceid == 22161003 || sourceid == 32121006 || sourceid == 1076 ||
     * sourceid == 11076 || sourceid == 4121015); // poison mist, smokescreen
     * and flame gear, recovery aura }
     */

    private final boolean isMist() {
        return skill && (sourceid == 2_111_003 || sourceid == 4_221_006 || sourceid == 12_111_005 || sourceid == 14_111_006 || sourceid == 22_161_003); // poison mist, smokescreen and flame gear, recovery aura
    }

    private final boolean isSpiritClaw() {
        return skill && sourceid == 4_121_006;
    }

    private final boolean isDispel() {
        return skill && (sourceid == 2_311_001 || sourceid == 9_001_000);
    }

    private final boolean isHeroWill() {
        switch (sourceid) {
            case 1_121_011:
            case 1_221_012:
            case 1_321_010:
            case 2_121_008:
            case 2_221_008:
            case 2_321_009:
            case 3_121_009:
            case 3_221_008:
            case 4_121_009:
            case 4_221_008:
            case 5_121_008:
            case 5_221_010:
            case 21_121_008:
            case 22_171_004:
            case 4_341_008:
            case 32_121_008:
            case 33_121_008:
            case 35_121_008:
                return skill;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final boolean isAranCombo() {
        return sourceid == 21_000_000;
    }

    /**
     *
     * @return
     */
    public final boolean isCombo() {
        switch (sourceid) {
            case 1_111_002:
            case 11_111_001: // Combo
                return skill;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final boolean isPirateMorph() {
        switch (sourceid) {
            case 15_111_002:
            case 5_111_005:
            case 5_121_003:
                return skill;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final boolean isMorph() {
        return morphId > 0;
    }

    /**
     *
     * @return
     */
    public final int getMorph() {
        switch (sourceid) {
            case 15_111_002:
            case 5_111_005:
                return 1_000;
            case 5_121_003:
                return 1_001;
            case 5_101_007:
                return 1_002;
            case 13_111_005:
                return 1_003;
        }
        return morphId;
    }

    /**
     *
     * @return
     */
    public final boolean isDivineBody() {
        switch (sourceid) {
            case 1_010:
            case 10_001_010:// Invincible Barrier
            case 20_001_010:
            case 20_011_010:
            case 30_001_010:
                return skill;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final boolean isBerserkFury() {
        switch (sourceid) {
            case 1_011: // Berserk fury
            case 10_001_011:
            case 20_001_011:
            case 20_011_011:
            case 30_001_011:
                return skill;
        }
        return false;
    }

    /**
     *
     * @param chr
     * @return
     */
    public final int getMorph(final MapleCharacter chr) {
        final int morph = getMorph();
        switch (morph) {
            case 1_000:
            case 1_001:
            case 1_003:
                return morph + (chr.getGender() == 1 ? 100 : 0);
        }
        return morph;
    }

    /**
     *
     * @return
     */
    public final byte getLevel() {
        return level;
    }

    /**
     *
     * @return
     */
    public final SummonMovementType getSummonMovementType() {
        if (!skill) {
            return null;
        }
        switch (sourceid) {
            case 3_211_002: // puppet sniper
            case 3_111_002: // puppet ranger
            case 13_111_004: // puppet cygnus
            case 5_211_001: // octopus - pirate
            case 5_220_002: // advanced octopus - pirate
                //case 4111007: //TEMP
                return SummonMovementType.STATIONARY;
            case 3_211_005: // golden eagle
            case 3_111_005: // golden hawk
            case 2_311_006: // summon dragon
            case 3_221_005: // frostprey
            case 3_121_006: // phoenix
                return SummonMovementType.CIRCLE_FOLLOW;
            case 5_211_002: // bird - pirate
                return SummonMovementType.CIRCLE_STATIONARY;
            case 32_111_006: //reaper
                return SummonMovementType.WALK_STATIONARY;
            case 1_321_007: // beholder
            case 2_121_005: // elquines
            case 2_221_005: // ifrit
            case 2_321_003: // bahamut
            case 12_111_004: // Ifrit
            case 11_001_004: // soul
            case 12_001_004: // flame
            case 13_001_004: // storm
            case 14_001_005: // darkness
            case 15_001_004: // lightning
                return SummonMovementType.FOLLOW;
        }
        return null;
    }

    /**
     *
     * @return
     */
    public final boolean isSkill() {
        return skill;
    }

    /**
     *
     * @return
     */
    public final int getSourceId() {
        return sourceid;
    }

    /**
     *
     * @return
     */
    public final boolean isSoaring() {
        switch (sourceid) {
            case 1_026: // Soaring
            case 10_001_026: // Soaring
            case 20_001_026: // Soaring
            case 20_011_026: // Soaring
            case 30_001_026:
                return skill;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final boolean isFinalAttack() {
        switch (sourceid) {
            case 13_101_002:
            case 11_101_002:
                return skill;
        }
        return false;
    }

    /**
     *
     * @return true if the effect should happen based on it's probablity, false
     * otherwise
     */
    public final boolean makeChanceResult() {
        return prop == 100 || Randomizer.nextInt(99) < prop;
    }

    /**
     *
     * @return
     */
    public final short getProb() {
        return prop;
    }

    private boolean isBattleShip() {
        return (this.skill) && (this.sourceid == 5_221_006);
    }

    /**
     *
     */
    public static class CancelEffectAction implements Runnable {

        private final MapleStatEffect effect;
        private final WeakReference<MapleCharacter> target;
        private final long startTime;

        /**
         *
         * @param target
         * @param effect
         * @param startTime
         */
        public CancelEffectAction(final MapleCharacter target, final MapleStatEffect effect, final long startTime) {
            this.effect = effect;
            this.target = new WeakReference<>(target);
            this.startTime = startTime;
        }

        @Override
        public void run() {
            final MapleCharacter realTarget = target.get();
            if (realTarget != null && !realTarget.isClone()) {
                realTarget.cancelEffect(effect, false, startTime);
            }
        }
    }
}
