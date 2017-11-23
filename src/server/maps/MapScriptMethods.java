
package server.maps;

import client.MapleClient;
import client.MapleQuestStatus;
import client.SkillFactory;
import java.awt.Point;
import scripting.EventManager;
import scripting.NPCScriptManager;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.quest.MapleQuest;
import server.quest.MapleQuest.MedalQuest;
import tools.MaplePacketCreator;
import tools.packet.UIPacket;

/**
 *
 * @author zjj
 */
public class MapScriptMethods {

    private static final Point witchTowerPos = new Point(-60, 184);
    private static final String[] mulungEffects = {
        "我一直在等你！如果你有一丝的勇气，你会走在那个门的右边!",
        "如何勇敢的你采取的道场训练塔!",
        "我会确保你会后悔带上了道场训练塔!",
        "我做你的倡导坚韧！但不要把你的勇气与鲁莽!",
        "如果你想一步一步地走到失败的道路上，用一切方法去做!"};

    private static enum onFirstUserEnter {

        pepeking_effect,
        dojang_Eff,
        PinkBeen_before,
        onRewordMap,
        StageMsg_together,
        StageMsg_davy,
        party6weatherMsg,
        StageMsg_juliet,
        StageMsg_romio,
        moonrabbit_mapEnter,
        astaroth_summon,
        boss_Ravana,
        killing_BonusSetting,
        killing_MapSetting,
        metro_firstSetting,
        balog_bonusSetting,
        balog_summon,
        easy_balog_summon,
        Sky_TrapFEnter,
        shammos_Fenter,
        PRaid_D_Fenter,
        PRaid_B_Fenter,
        GhostF,
        PTtutor000,
        boss_Event_PinkZakum,
        summon_pepeking,
        Xerxes_summon,
        VanLeon_Before,
        cygnus_Summon,
        storymap_scenario,
        shammos_FStart,
        kenta_mapEnter,
        iceman_FEnter,
        iceman_Boss,
        prisonBreak_mapEnter,
        Visitor_Cube_poison,
        Visitor_Cube_Hunting_Enter_First,
        VisitorCubePhase00_Start,
        visitorCube_addmobEnter,
        Visitor_Cube_PickAnswer_Enter_First_1,
        visitorCube_medicroom_Enter,
        visitorCube_iceyunna_Enter,
        Visitor_Cube_AreaCheck_Enter_First,
        visitorCube_boomboom_Enter,
        visitorCube_boomboom2_Enter,
        CubeBossbang_Enter,
        MalayBoss_Int,
        mPark_summonBoss,
        NULL;

        private static onFirstUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    private static enum onUserEnter {

        babyPigMap,
        crash_Dragon,
        evanleaveD,
        getDragonEgg,
        meetWithDragon,
        go1010100,
        go1010200,
        go1010300,
        go1010400,
        evanPromotion,
        PromiseDragon,
        evanTogether,
        incubation_dragon,
        TD_MC_Openning,
        TD_MC_gasi,
        TD_MC_title,
        cygnusJobTutorial,
        cygnusTest,
        startEreb,
        dojang_Msg,
        dojang_1st,
        reundodraco,
        undomorphdarco,
        explorationPoint,
        goAdventure,
        go10000,
        go20000,
        go30000,
        go40000,
        go50000,
        go1000000,
        go1010000,
        go1020000,
        go2000000,
        go104000000,
        goArcher,
        goPirate,
        goRogue,
        goMagician,
        goSwordman,
        goLith,
        iceCave,
        mirrorCave,
        aranDirection,
        rienArrow,
        rien,
        check_count,
        Massacre_first,
        Massacre_result,
        aranTutorAlone,
        evanAlone,
        dojang_QcheckSet,
        Sky_StageEnter,
        outCase,
        balog_buff,
        balog_dateSet,
        Sky_BossEnter,
        Sky_GateMapEnter,
        shammos_Enter,
        shammos_Result,
        shammos_Base,
        dollCave00,
        dollCave01,
        Sky_Quest,
        enterBlackfrog,
        onSDI,
        blackSDI,
        summonIceWall,
        metro_firstSetting,
        start_itemTake,
        PRaid_D_Enter,
        PRaid_B_Enter,
        PRaid_Revive,
        PRaid_W_Enter,
        PRaid_WinEnter,
        PRaid_FailEnter,
        Ghost,
        NULL;

        private static onUserEnter fromString(String Str) {
            try {
                return valueOf(Str);
            } catch (IllegalArgumentException ex) {
                return NULL;
            }
        }
    };

    /**
     *
     * @param c
     * @param scriptName
     */
    public static void startScript_FirstUser(MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        } //o_O
        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("您已经建立与地图脚本Script_FirstUser【" + scriptName + "】的连接");
        }
        switch (scriptName) {
            case "summon_pepeking": {
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/W"));
                if (c.getPlayer().getMap().getAllMonster().isEmpty()) {
                    int rand = Randomizer.rand(0, 2);
                    MapleMonster mob = MapleLifeFactory.getMonster(3_300_005 + rand);
                    OverrideMonsterStats oms = new OverrideMonsterStats();
                    oms.setOExp(7_110);
                    oms.setOHp(mob.getMobMaxHp());
                    oms.setOMp(mob.getMobMaxMp());
                    mob.setOverrideStats(oms);
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(358, -68));
                switch (rand) {
                    case 0:
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                        break;
                    case 1:
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeG"));
                        break;
                    case 2:
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeW"));
                        break;
                    default:
                        break;
                }
                } else {
                    c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                }
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/chat/nugu"));
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/B"));
                break;
            }
        }
        switch (onFirstUserEnter.fromString(scriptName)) {
            case pepeking_effect: {
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/W"));
                if (c.getPlayer().getMap().getAllMonster().isEmpty()) {
                    int rand = Randomizer.rand(0, 2);
                    MapleMonster mob = MapleLifeFactory.getMonster(3_300_005 + rand);
                    OverrideMonsterStats oms = new OverrideMonsterStats();
                    oms.setOExp(7_110);
                    oms.setOHp(mob.getMobMaxHp());
                    oms.setOMp(mob.getMobMaxMp());
                    mob.setOverrideStats(oms);
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, new Point(358, -68));
                switch (rand) {
                    case 0:
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                        break;
                    case 1:
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeG"));
                        break;
                    case 2:
                        c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeW"));
                        break;
                    default:
                        break;
                }
                } else {
                    c.sendPacket(MaplePacketCreator.showEffect("pepeKing/pepe/pepeB"));
                }
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/chat/nugu"));
                c.sendPacket(MaplePacketCreator.showEffect("pepeKing/frame/B"));
                break;
            }
            case dojang_Eff: {
                int temp = (c.getPlayer().getMapId() - 925_000_000) / 100;
                int stage = (int) (temp - ((temp / 100) * 100));

                sendDojoClock(c, getTiming(stage) * 60);
                sendDojoStart(c, stage - getDojoStageDec(stage));
                break;
            }
            case PinkBeen_before: {
                handlePinkBeanStart(c);
                break;
            }
            case onRewordMap: {
                reloadWitchTower(c);
                break;
            }
            case GhostF: {
                c.getPlayer().getMap().startMapEffect("这个地图感觉阴森森的..有种莫名的奇怪感觉..", 5_120_025);
                break;
            }
            //5120018 = ludi(none), 5120019 = orbis(start_itemTake - onUser)
            case moonrabbit_mapEnter: {
                c.getPlayer().getMap().startMapEffect("粥环绕月球的月见草种子和保护月球兔子！", 5_120_016);
                break;
            }
            case StageMsg_together: {
                switch (c.getPlayer().getMapId()) {
                    case 103_000_800:
                        c.getPlayer().getMap().startMapEffect("解决问题并收集通行证的数量!", 5_120_017);
                        break;
                    case 103_000_801:
                        c.getPlayer().getMap().startMapEffect("上绳索，揭开正确的组合!", 5_120_017);
                        break;
                    case 103_000_802:
                        c.getPlayer().getMap().startMapEffect("在平台上推出正确的组合!", 5_120_017);
                        break;
                    case 103_000_803:
                        c.getPlayer().getMap().startMapEffect("在桶上，揭开正确的组合!", 5_120_017);
                        break;
                    case 103_000_804:
                        c.getPlayer().getMap().startMapEffect("打败绿水灵王和他的爪牙!", 5_120_017);
                        break;
                }
                break;
            }
            case StageMsg_romio: {
                switch (c.getPlayer().getMapId()) {
                    case 926_100_000:
                        c.getPlayer().getMap().startMapEffect("请找到隐藏的门，通过调查实验室！", 5_120_021);
                        break;
                    case 926_100_001:
                        c.getPlayer().getMap().startMapEffect("找到你的方式通过这黑暗！", 5_120_021);
                        break;
                    case 926_100_100:
                        c.getPlayer().getMap().startMapEffect("充满能量的烧杯！", 5_120_021);
                        break;
                    case 926_100_200:
                        c.getPlayer().getMap().startMapEffect("获取实验的文件通过每个门！", 5_120_021);
                        break;
                    case 926_100_203:
                        c.getPlayer().getMap().startMapEffect("请打败所有的怪物！", 5_120_021);
                        break;
                    case 926_100_300:
                        c.getPlayer().getMap().startMapEffect("找到你的方法通过实验室！", 5_120_021);
                        break;
                    case 926_100_401:
                        c.getPlayer().getMap().startMapEffect("请保护我的爱人！", 5_120_021);

                        break;
                }
                break;
            }
            case StageMsg_juliet: {
                switch (c.getPlayer().getMapId()) {
                    case 926_110_000:
                        c.getPlayer().getMap().startMapEffect("请找到隐藏的门，通过调查实验室！", 5_120_022);
                        break;
                    case 926_110_001:
                        c.getPlayer().getMap().startMapEffect("找到你的方式通过这黑暗！", 5_120_022);
                        break;
                    case 926_110_100:
                        c.getPlayer().getMap().startMapEffect("充满能量的烧杯！", 5_120_022);
                        break;
                    case 926_110_200:
                        c.getPlayer().getMap().startMapEffect("获取实验的文件通过每个门！", 5_120_022);
                        break;
                    case 926_110_203:
                        c.getPlayer().getMap().startMapEffect("请打败所有的怪物！", 5_120_022);
                        break;
                    case 926_110_300:
                        c.getPlayer().getMap().startMapEffect("找到你的方法通过实验室！", 5_120_022);
                        break;
                    case 926_110_401:
                        c.getPlayer().getMap().startMapEffect("请保护我的爱人！", 5_120_022);
                        break;
                }
                break;
            }
            case party6weatherMsg: {
                switch (c.getPlayer().getMapId()) {
                    case 930_000_000:
                        c.getPlayer().getMap().startMapEffect("进入传送点，我要对你们施放变身魔法了！", 5_120_023);
                        break;
                    case 930_000_100:
                        c.getPlayer().getMap().startMapEffect("消灭所有怪物！", 5_120_023);
                        break;
                    case 930_000_200:
                        c.getPlayer().getMap().startMapEffect("对荆棘施放稀释的毒液4个！", 5_120_023);
                        break;
                    case 930_000_300:
                        c.getPlayer().getMap().startMapEffect("妈妈你在哪里呜呜哭哭喔我迷路了", 5_120_023);
                        break;
                    case 930_000_400:
                        c.getPlayer().getMap().startMapEffect("找我对话拿净化之珠其中一个队员集满10个怪物珠给我！", 5_120_023);
                        break;
                    case 930_000_500:
                        c.getPlayer().getMap().startMapEffect("从怪人书桌中寻找紫色魔力石！！", 5_120_023);
                        break;
                    case 930_000_600:
                        c.getPlayer().getMap().startMapEffect("将紫色魔力石放在祭坛上！", 5_120_023);
                        break;
                }
                break;
            }
            case StageMsg_davy: {
                switch (c.getPlayer().getMapId()) {
                    case 925_100_000:
                        c.getPlayer().getMap().startMapEffect("击败外的怪物的船舶推进!", 5_120_020);
                        break;
                    case 925_100_100:
                        c.getPlayer().getMap().startMapEffect("我们必须证明自己！给我海盗勋章!", 5_120_020);
                        break;
                    case 925_100_200:
                        c.getPlayer().getMap().startMapEffect("在这里击败守卫!", 5_120_020);
                        break;
                    case 925_100_300:
                        c.getPlayer().getMap().startMapEffect("消灭这里的守卫!", 5_120_020);
                        break;
                    case 925_100_400:
                        c.getPlayer().getMap().startMapEffect("锁上门！密封船舶动力的根!", 5_120_020);
                        break;
                    case 925_100_500:
                        c.getPlayer().getMap().startMapEffect("主，消灭海盗!", 5_120_020);
                        break;
                }
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("Pirate");
                if (c.getPlayer().getMapId() == 925_100_500 && em != null && em.getProperty("stage5") != null) {
                    int mobId = Randomizer.nextBoolean() ? 9_300_119 : 9_300_119; //lord pirate
                    final int st = Integer.parseInt(em.getProperty("stage5"));
                    switch (st) {
                        case 1:
                            mobId =  /*
                 * Randomizer.nextBoolean() ? 9300119 :
                 */ 9_300_105; //angry
                            break;
                        case 2:
                            mobId =  /*
                 * Randomizer.nextBoolean() ?
                 */ 9_300_106 /*
                 * : 9300105/*
                                     * : 9300105
                                     */; //enraged
                            break;
                    }
                    final MapleMonster shammos = MapleLifeFactory.getMonster(mobId);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                    }
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(shammos, new Point(411, 236));
                }
                break;
            }
            case astaroth_summon: {
                c.getPlayer().getMap().resetFully();
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(9_400_633), new Point(600, -26)); //rough estimate
                break;
            }
            case boss_Ravana: { //event handles this so nothing for now until i find out something to do with it
                c.getPlayer().getMap().broadcastMessage(MaplePacketCreator.serverNotice(5, "那已经出现!"));
                break;
            }
            case killing_BonusSetting: { //spawns monsters according to mapid
                //910320010-910320029 = Train 999 bubblings.
                //926010010-926010029 = 30 Yetis
                //926010030-926010049 = 35 Yetis
                //926010050-926010069 = 40 Yetis
                //926010070-926010089 - 50 Yetis (specialized? immortality)
                //TODO also find positions to spawn these at
                c.getPlayer().getMap().resetFully();
                c.getSession().write(MaplePacketCreator.showEffect("killing/bonus/bonus"));
                c.getSession().write(MaplePacketCreator.showEffect("killing/bonus/stage"));
                Point pos1 = null, pos2 = null, pos3 = null;
                int spawnPer = 0;
                int mobId = 0;
                //9700019, 9700029
                //9700021 = one thats invincible
                if (c.getPlayer().getMapId() >= 910_320_010 && c.getPlayer().getMapId() <= 910_320_029) {
                    pos1 = new Point(121, 218);
                    pos2 = new Point(396, 43);
                    pos3 = new Point(-63, 43);
                    mobId = 9_700_020;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926_010_010 && c.getPlayer().getMapId() <= 926_010_029) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9_700_019;
                    spawnPer = 10;
                } else if (c.getPlayer().getMapId() >= 926_010_030 && c.getPlayer().getMapId() <= 926_010_049) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9_700_019;
                    spawnPer = 15;
                } else if (c.getPlayer().getMapId() >= 926_010_050 && c.getPlayer().getMapId() <= 926_010_069) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9_700_019;
                    spawnPer = 20;
                } else if (c.getPlayer().getMapId() >= 926_010_070 && c.getPlayer().getMapId() <= 926_010_089) {
                    pos1 = new Point(0, 88);
                    pos2 = new Point(-326, -115);
                    pos3 = new Point(361, -115);
                    mobId = 9_700_029;
                    spawnPer = 20;
                } else {
                    break;
                }
                for (int i = 0; i < spawnPer; i++) {
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos1));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos2));
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mobId), new Point(pos3));
                }
                c.getPlayer().startMapTimeLimitTask(120, c.getPlayer().getMap().getReturnMap());
                break;
            }
            case shammos_Fenter: {
                if (c.getPlayer().getMapId() >= 921_120_100 && c.getPlayer().getMapId() < 921_120_500) {
                    final MapleMonster shammos = MapleLifeFactory.getMonster(9_300_275);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().registerMonster(shammos);
                        if (c.getPlayer().getEventInstance().getProperty("HP") != null) {
                            shammos.setHp(Long.parseLong(c.getPlayer().getEventInstance().getProperty("HP")));
                        } else {
                            c.getPlayer().getEventInstance().setProperty("HP", "50000");
                        }
                    }
                    c.getPlayer().getMap().spawnMonsterWithEffectBelow(shammos, new Point(c.getPlayer().getMap().getPortal(0).getPosition()), 12);
                    shammos.switchController(c.getPlayer(), false);
                    // c.getSession().write(MaplePacketCreator.getNodeProperties(shammos, c.getPlayer().getMap()));

                }
                break;
            }
            case PRaid_D_Fenter: {
                switch (c.getPlayer().getMapId() % 10) {
                    case 0:
                        c.getPlayer().getMap().startMapEffect("消灭所有的怪物!", 5_120_033);
                        break;
                    case 1:
                        c.getPlayer().getMap().startMapEffect("打破盒子，消灭怪物!", 5_120_033);
                        break;
                    case 2:
                        c.getPlayer().getMap().startMapEffect("消除!", 5_120_033);
                        break;
                    case 3:
                        c.getPlayer().getMap().startMapEffect("消灭所有的怪物!", 5_120_033);
                        break;
                    case 4:
                        c.getPlayer().getMap().startMapEffect("找到另一边的路!", 5_120_033);
                        break;
                }
                break;
            }
            case PRaid_B_Fenter: {
                c.getPlayer().getMap().startMapEffect("打败幽灵船船长!", 5_120_033);
                break;
            }
            case balog_summon:
            case easy_balog_summon: { //we dont want to reset
                break;
            }
            case metro_firstSetting:
            case killing_MapSetting:
            case Sky_TrapFEnter:
            case balog_bonusSetting: { //not needed
                c.getPlayer().getMap().resetFully();
                break;
            }
            default: {
                //  System.out.println("未處理的腳本 : " + scriptName + ", 型態 : onUserEnter - 地圖ID " + c.getPlayer().getMapId());
                //    FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "未處理的腳本 : " + scriptName + ", 型態 : onUserEnter - 地圖ID " + c.getPlayer().getMapId());
                break;
            }
        }
    }

    /**
     *
     * @param c
     * @param scriptName
     */
    public static void startScript_User(MapleClient c, String scriptName) {
        if (c.getPlayer() == null) {
            return;
        } //o_O

        if (c.getPlayer().isGM()) {
            c.getPlayer().dropMessage("您已经建立与地图脚本Script_User【" + scriptName + "】的连接");
        }

        String data = "";
        switch (scriptName) {
            case "103000804": {
                if (c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) {
                    //       c.getPlayer().getMap().spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(123), new Point(125, -2175));
                }
            }
        }
        switch (onUserEnter.fromString(scriptName)) {
            case cygnusTest:
            case cygnusJobTutorial: {
                showIntro(c, "Effect/Direction.img/cygnusJobTutorial/Scene" + (c.getPlayer().getMapId() - 913_040_100));
                break;
            }
            case shammos_Enter: { //nothing to go on inside the map
                // c.getSession().write(MaplePacketCreator.sendPyramidEnergy("shammos_LastStage", String.valueOf((c.getPlayer().getMapId() % 1000) / 100)));
                if (c.getPlayer().getEventInstance() != null && c.getPlayer().getMapId() == 921_120_500) {
                    NPCScriptManager.getInstance().dispose(c); //only boss map.
                    NPCScriptManager.getInstance().start(c, 2_022_006);
                }
                break;
            }
            case start_itemTake: { //nothing to go on inside the map
                final EventManager em = c.getChannelServer().getEventSM().getEventManager("OrbisPQ");
                if (em != null && em.getProperty("pre").equals("0")) {
                    NPCScriptManager.getInstance().dispose(c);
                    NPCScriptManager.getInstance().start(c, 2_013_001);
                }
                break;
            }
            case PRaid_W_Enter: {
                // c.getSession().write(MaplePacketCreator.sendPyramidEnergy("PRaid_expPenalty", "0"));
                //  c.getSession().write(MaplePacketCreator.sendPyramidEnergy("PRaid_ElapssedTimeAtField", "0"));
                // c.getSession().write(MaplePacketCreator.sendPyramidEnergy("PRaid_Point", "-1"));
                //  c.getSession().write(MaplePacketCreator.sendPyramidEnergy("PRaid_Bonus", "-1"));
                //  c.getSession().write(MaplePacketCreator.sendPyramidEnergy("PRaid_Total", "-1"));
                //  c.getSession().write(MaplePacketCreator.sendPyramidEnergy("PRaid_Team", ""));
                //  c.getSession().write(MaplePacketCreator.sendPyramidEnergy("PRaid_IsRevive", "0"));
                //   c.getPlayer().writePoint("PRaid_Point", "-1");
                //  c.getPlayer().writeStatus("Red_Stage", "1");
                //  c.getPlayer().writeStatus("Blue_Stage", "1");
                //   c.getPlayer().writeStatus("redTeamDamage", "0");
                //   c.getPlayer().writeStatus("blueTeamDamage", "0");
                break;
            }
            case PRaid_D_Enter:
            case PRaid_B_Enter:
            case PRaid_WinEnter: //handled by event
            case PRaid_FailEnter: //also
            case PRaid_Revive: //likely to subtract points or remove a life, but idc rly
            case metro_firstSetting:
            case blackSDI:
            case summonIceWall:
            case onSDI:
            case enterBlackfrog:
            case Sky_Quest: //forest that disappeared 240030102
            case dollCave00:
            case dollCave01:
            case shammos_Base:
            case shammos_Result:
            case Sky_BossEnter:
            case Sky_GateMapEnter:
            case balog_dateSet:
            case balog_buff:
            case outCase:
            case Sky_StageEnter:
            case dojang_QcheckSet:
            case evanTogether:
            case aranTutorAlone:
            case Ghost: {
                c.getPlayer().getMap().startMapEffect("这个地图感觉阴森森的..有一种莫名的奇怪感觉..", 5_120_025);
                break;
            }
            case evanAlone: { //no idea
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            }
            case startEreb:
            case mirrorCave:
            case babyPigMap:
            case evanleaveD: {
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            }
            case dojang_Msg: {
                c.getPlayer().getMap().startMapEffect(mulungEffects[Randomizer.nextInt(mulungEffects.length)], 5_120_024);
                break;
            }
            case dojang_1st: {
                c.getPlayer().writeMulungEnergy();
                break;
            }
            case undomorphdarco:
            case reundodraco: {
                c.getPlayer().cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(2_210_016), false, -1);
                break;
            }
            case goAdventure: {
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(UIPacket.MapNameDisplay(10_000));

                // c.getSession().write(UIPacket.IntroLock(true));
                // BUG in MSEA v.91, so let's skip this part.
                ///  showIntro(c, "Effect/Direction3.img/goAdventure/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case crash_Dragon:
                showIntro(c, "Effect/Direction4.img/crash/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case getDragonEgg:
                showIntro(c, "Effect/Direction4.img/getDragonEgg/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case meetWithDragon:
                showIntro(c, "Effect/Direction4.img/meetWithDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case PromiseDragon:
                showIntro(c, "Effect/Direction4.img/PromiseDragon/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            case evanPromotion:
                switch (c.getPlayer().getMapId()) {
                    case 900_090_000:
                        data = "Effect/Direction4.img/promotion/Scene0" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900_090_001:
                        data = "Effect/Direction4.img/promotion/Scene1";
                        break;
                    case 900_090_002:
                        data = "Effect/Direction4.img/promotion/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 900_090_003:
                        data = "Effect/Direction4.img/promotion/Scene3";
                        break;
                    case 900_090_004:
                        c.getSession().write(UIPacket.IntroDisableUI(false));
                        c.getSession().write(UIPacket.IntroLock(false));
                        c.getSession().write(MaplePacketCreator.enableActions());
                        final MapleMap mapto = c.getChannelServer().getMapFactory().getMap(900_010_000);
                        c.getPlayer().changeMap(mapto, mapto.getPortal(0));
                        return;
                }
                showIntro(c, data);
                break;
            case TD_MC_title: {
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(MaplePacketCreator.enableActions());
                c.getSession().write(UIPacket.MapEff("temaD/enter/mushCatle"));
                break;
            }
            case explorationPoint: {
                if (c.getPlayer().getMapId() == 104_000_000) {
                    c.getSession().write(UIPacket.IntroDisableUI(false));
                    c.getSession().write(UIPacket.IntroLock(false));
                    c.getSession().write(MaplePacketCreator.enableActions());
                    c.getSession().write(UIPacket.MapNameDisplay(c.getPlayer().getMapId()));
                }
                MedalQuest m = null;
                for (MedalQuest mq : MedalQuest.values()) {
                    for (int i : mq.maps) {
                        if (c.getPlayer().getMapId() == i) {
                            m = mq;
                            break;
                        }
                    }
                }
                if (m != null && c.getPlayer().getLevel() >= m.level && c.getPlayer().getQuestStatus(m.questid) != 2) {
                    if (c.getPlayer().getQuestStatus(m.lquestid) != 1) {
                        MapleQuest.getInstance(m.lquestid).forceStart(c.getPlayer(), 0, "0");
                    }
                    if (c.getPlayer().getQuestStatus(m.questid) != 1) {
                        MapleQuest.getInstance(m.questid).forceStart(c.getPlayer(), 0, null);
                        final StringBuilder sb = new StringBuilder("enter=");
                        for (int i = 0; i < m.maps.length; i++) {
                            sb.append("0");
                        }
                        c.getPlayer().updateInfoQuest(m.questid - 2_005, sb.toString());
                        MapleQuest.getInstance(m.questid - 1_995).forceStart(c.getPlayer(), 0, "0");
                    }
                    final String quest = c.getPlayer().getInfoQuest(m.questid - 2_005);
                    final MapleQuestStatus stat = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(m.questid - 1_995));
                    if (stat.getCustomData() == null) { //just a check.
                        stat.setCustomData("0");
                    }
                    int number = Integer.parseInt(stat.getCustomData());
                    final StringBuilder sb = new StringBuilder("enter=");
                    boolean changedd = false;
                    for (int i = 0; i < m.maps.length; i++) {
                        boolean changed = false;
                        try {
                            if (c.getPlayer().getMapId() == m.maps[i]) {
                                if (quest.substring(i + 6, i + 7).equals("0")) {
                                    sb.append("1");
                                    changed = true;
                                    changedd = true;
                                }
                            }
                            if (!changed) {
                                sb.append(quest.substring(i + 6, i + 7));
                            }
                        } catch (Exception ex) {
                        }
                    }
                    if (changedd) {
                        number++;
                        c.getPlayer().updateInfoQuest(m.questid - 2_005, sb.toString());
                        MapleQuest.getInstance(m.questid - 1_995).forceStart(c.getPlayer(), 0, String.valueOf(number));
                        c.getPlayer().dropMessage(5, "访问 " + number + "/" + m.maps.length + " 个地区.");
                        c.getPlayer().dropMessage(5, "称号 " + String.valueOf(m) + " 已完成了");
                        c.getSession().write(MaplePacketCreator.showQuestMsg("称号 " + String.valueOf(m) + " 已完成访问 " + number + "/" + m.maps.length + " 个地区"));
                    }
                }
                break;
            }
            case go10000:
            case go20000:
            case go30000:
            case go40000:
            case go50000:
            case go1000000:
            case go1020000:
            case go104000000:
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
            //c.getSession().write(MaplePacketCreator.enableActions());
            case go2000000:
            case go1010000:
            case go1010100:
            case go1010200:
            case go1010300:
            case go1010400: {
                c.getSession().write(UIPacket.MapNameDisplay(c.getPlayer().getMapId()));
                break;
            }
            case goArcher: {
                showIntro(c, "Effect/Direction3.img/archer/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goPirate: {
                showIntro(c, "Effect/Direction3.img/pirate/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goRogue: {
                showIntro(c, "Effect/Direction3.img/rogue/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goMagician: {
                showIntro(c, "Effect/Direction3.img/magician/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goSwordman: {
                showIntro(c, "Effect/Direction3.img/swordman/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case goLith: {
                showIntro(c, "Effect/Direction3.img/goLith/Scene" + (c.getPlayer().getGender() == 0 ? "0" : "1"));
                break;
            }
            case TD_MC_Openning: {
                showIntro(c, "Effect/Direction2.img/open");
                break;
            }
            case TD_MC_gasi: {
                showIntro(c, "Effect/Direction2.img/gasi");
                break;
            }
            case aranDirection: {
                switch (c.getPlayer().getMapId()) {
                    case 914_090_010:
                        data = "Effect/Direction1.img/aranTutorial/Scene0";
                        break;
                    case 914_090_011:
                        data = "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914_090_012:
                        data = "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914_090_013:
                        data = "Effect/Direction1.img/aranTutorial/Scene3";
                        break;
                    case 914_090_100:
                        data = "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
                        break;
                    case 914_090_200:
                        data = "Effect/Direction1.img/aranTutorial/Maha";
                        break;
                }
                showIntro(c, data);
                break;
            }
            case iceCave: {
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20_000_014), (byte) -1, (byte) 0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20_000_015), (byte) -1, (byte) 0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20_000_016), (byte) -1, (byte) 0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20_000_017), (byte) -1, (byte) 0);
                c.getPlayer().changeSkillLevel(SkillFactory.getSkill(20_000_018), (byte) -1, (byte) 0);
                c.getSession().write(UIPacket.ShowWZEffect("Effect/Direction1.img/aranTutorial/ClickLirin", -1));
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                c.getSession().write(MaplePacketCreator.enableActions());
                break;
            }
            case rienArrow: {
                if (c.getPlayer().getInfoQuest(21_019).equals("miss=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21_019, "miss=o;arr=o;helper=clear");
                    c.getSession().write(UIPacket.AranTutInstructionalBalloon("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow3"));
                }
                break;
            }
            case rien: {
                if (c.getPlayer().getQuestStatus(21_101) == 2 && c.getPlayer().getInfoQuest(21_019).equals("miss=o;arr=o;helper=clear")) {
                    c.getPlayer().updateInfoQuest(21_019, "miss=o;arr=o;ck=1;helper=clear");
                }
                c.getSession().write(UIPacket.IntroDisableUI(false));
                c.getSession().write(UIPacket.IntroLock(false));
                break;
            }
            case check_count: {
                if (c.getPlayer().getMapId() == 950_101_010 && (!c.getPlayer().haveItem(4_001_433, 20) || c.getPlayer().getLevel() < 50)) { //ravana Map
                    final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(950_101_100); //exit Map
                    c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                }
                break;
            }
            case Massacre_first: { //sends a whole bunch of shit.
                if (c.getPlayer().getPyramidSubway() == null) {
                    c.getPlayer().setPyramidSubway(new Event_PyramidSubway(c.getPlayer()));
                }
                break;
            }
            case Massacre_result: { //clear, give exp, etc.
                //if (c.getPlayer().getPyramidSubway() == null) {
                c.getSession().write(MaplePacketCreator.showEffect("pvp/lose"));
                //} else {
                //	c.getSession().write(MaplePacketCreator.showEffect("pvp/victory"));
                //}
                //left blank because pyramidsubway handles this.
                break;
            }
            default: {
                //  System.out.println("未處理的腳本 : " + scriptName + ", 型態 : onUserEnter - 地圖ID " + c.getPlayer().getMapId());
                //  FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "未處理的腳本 : " + scriptName + ", 型態 : onUserEnter - 地圖ID " + c.getPlayer().getMapId());
                break;
            }
        }
    }

    private static final int getTiming(int ids) {
        if (ids <= 5) {
            return 5;
        } else if (ids >= 7 && ids <= 11) {
            return 6;
        } else if (ids >= 13 && ids <= 17) {
            return 7;
        } else if (ids >= 19 && ids <= 23) {
            return 8;
        } else if (ids >= 25 && ids <= 29) {
            return 9;
        } else if (ids >= 31 && ids <= 35) {
            return 10;
        } else if (ids >= 37 && ids <= 38) {
            return 15;
        }
        return 0;
    }

    private static final int getDojoStageDec(int ids) {
        if (ids <= 5) {
            return 0;
        } else if (ids >= 7 && ids <= 11) {
            return 1;
        } else if (ids >= 13 && ids <= 17) {
            return 2;
        } else if (ids >= 19 && ids <= 23) {
            return 3;
        } else if (ids >= 25 && ids <= 29) {
            return 4;
        } else if (ids >= 31 && ids <= 35) {
            return 5;
        } else if (ids >= 37 && ids <= 38) {
            return 6;
        }
        return 0;
    }

    private static void showIntro(final MapleClient c, final String data) {
        c.getSession().write(UIPacket.IntroDisableUI(true));
        c.getSession().write(UIPacket.IntroLock(true));
        c.getSession().write(UIPacket.ShowWZEffect(data, -1));
    }

    private static void sendDojoClock(MapleClient c, int time) {
        c.getSession().write(MaplePacketCreator.getClock(time));
    }

    private static void sendDojoStart(MapleClient c, int stage) {
        c.getSession().write(MaplePacketCreator.environmentChange("Dojang/start", 4));
        c.getSession().write(MaplePacketCreator.environmentChange("dojang/start/stage", 3));
        c.getSession().write(MaplePacketCreator.environmentChange("dojang/start/number/" + stage, 3));
        c.getSession().write(MaplePacketCreator.trembleEffect(0, 1));
    }

    private static void handlePinkBeanStart(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.resetFully();

        if (!map.containsNPC(2_141_000)) {
            map.spawnNpc(2_141_000, new Point(-190, -42));
        }
    }

    private static void reloadWitchTower(MapleClient c) {
        final MapleMap map = c.getPlayer().getMap();
        map.killAllMonsters(false);

        final int level = c.getPlayer().getLevel();
        int mob;
        if (level <= 10) {
            mob = 9_300_367;
        } else if (level <= 20) {
            mob = 9_300_368;
        } else if (level <= 30) {
            mob = 9_300_369;
        } else if (level <= 40) {
            mob = 9_300_370;
        } else if (level <= 50) {
            mob = 9_300_371;
        } else if (level <= 60) {
            mob = 9_300_372;
        } else if (level <= 70) {
            mob = 9_300_373;
        } else if (level <= 80) {
            mob = 9_300_374;
        } else if (level <= 90) {
            mob = 9_300_375;
        } else if (level <= 100) {
            mob = 9_300_376;
        } else {
            mob = 9_300_377;
        }
        map.spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(mob), witchTowerPos);
    }
}
