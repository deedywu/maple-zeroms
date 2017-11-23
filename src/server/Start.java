package server;

import client.MapleCharacter;
import client.SkillFactory;
import constants.ServerConstants;
import handling.MapleServerHandler;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginServer;
import handling.cashshop.CashShopServer;
import handling.login.LoginInformationProvider;
import handling.world.World;
import java.sql.SQLException;
import database.DatabaseConnection;
import handling.world.family.MapleFamilyBuff;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Map;
import server.Timer.*;
import server.events.MapleOxQuizFactory;
import server.life.MapleLifeFactory;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.StringUtil;

public class Start {

    public static boolean Check = true;
    public static final Start instance = new Start();
    private static int maxUsers = 0;

    public static void main(final String args[]) {
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.Admin"))) {
            System.out.println("[!!! Admin Only Mode Active !!!]");
        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.AutoRegister"))) {
            System.out.println("ZeroMS服务端----------[加载注册系统]成功!");
        }
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        }

        World.init();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        LoginInformationProvider.getInstance();
        MapleQuest.initQuests();
        MapleLifeFactory.loadQuestCounts();
//        ItemMakerFactory.getInstance();
        MapleItemInformationProvider.getInstance().load();
        RandomRewards.getInstance();
        SkillFactory.getSkill(99999999);
        MapleOxQuizFactory.getInstance().initialize();
        MapleCarnivalFactory.getInstance();
        MapleGuildRanking.getInstance().RankingUpdate();
        MapleFamilyBuff.getBuffEntry();
        MapleServerHandler.registerMBean();
        // RankingWorker.getInstance().run();
        // MTSStorage.load();
        CashItemFactory.getInstance().initialize();
        LoginServer.run_startup_configurations();
        ChannelServer.startChannel_Main();

        //  System.out.println("[加载商城端口启动中]");
        CashShopServer.run_startup_configurations();
        // System.out.println("[加载商城端口完成]");
        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        在线统计(1);
        //自动存档(1);
        在线时间(1);
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.RandDrop"))) {
            ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            System.out.println("SpeedRunner错误:" + e);
        }
        World.registerRespawn();
        LoginServer.setOn();
        System.out.println("\r\n当前经验倍率:" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.Exp")) + "  当前物品倍率：" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.Drop")) + "  当前金币倍率：" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.Meso")));
        System.out.println("\r\n当前开放职业: "
                + " 冒险家 = " + Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.冒险家"))
                + " 骑士团 = " + Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.骑士团"))
                + " 战神 = " + Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.战神")));
        System.out.println("\r\n加载完成!开端成功! :::");
        // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    }

    public void startServer() throws InterruptedException {
        Check = false;
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.Admin", "false"))) {
            System.out.println("[!!! Admin Only Mode Active !!!]");
        }
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.AutoRegister", "true"))) {
            ServerConstants.自动注册 = Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.AutoRegister", "true"));
            System.out.println("ZeroMS服务端----------[加载注册系统]成功!");
        }
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
                ps.executeUpdate();
            }
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET lastGainHM = 0")) {
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new RuntimeException("[EXCEPTION] Please check if the SQL server is active.");
        }

        World.init();
        WorldTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        MobTimer.getInstance().start();
        CloneTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        LoginInformationProvider.getInstance();
        MapleQuest.initQuests();
        MapleLifeFactory.loadQuestCounts();
//        ItemMakerFactory.getInstance();
        MapleItemInformationProvider.getInstance().load();
        RandomRewards.getInstance();
        SkillFactory.getSkill(99999999);
        MapleOxQuizFactory.getInstance().initialize();
        MapleCarnivalFactory.getInstance();
        MapleGuildRanking.getInstance().RankingUpdate();
        MapleFamilyBuff.getBuffEntry();
        MapleServerHandler.registerMBean();

        // MTSStorage.load();
        PredictCardFactory.getInstance().initialize();
        CashItemFactory.getInstance().initialize();
        LoginServer.run_startup_configurations();
        ChannelServer.startChannel_Main();

        //  System.out.println("[加载商城端口启动中]");
        CashShopServer.run_startup_configurations();
        // System.out.println("[加载商城端口完成]");
        CheatTimer.getInstance().register(AutobanManager.getInstance(), 60000);
        // 在线统计(1);
        //自动存档(1);

        在线时间(1);
        if (Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.RandDrop"))) {
            ChannelServer.getInstance(1).getMapFactory().getMap(910000000).spawnRandDrop();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
        try {
            SpeedRunner.getInstance().loadSpeedRuns();
        } catch (SQLException e) {
            System.out.println("SpeedRunner错误:" + e);
        }
        World.registerRespawn();
        LoginServer.setOn();
        MapleMapFactory.loadCustomLife();
        WorldTimer.getInstance().register(DatabaseConnection.CloseSQLConnections, 600 * 1000);
        System.out.println("\r\n当前经验倍率:" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.Exp")) + "  当前物品倍率：" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.Drop")) + "  当前金币倍率：" + Integer.parseInt(ServerProperties.getProperty("ZeroMS.Meso")));
        System.out.println("\r\n当前开放职业: "
                + " 冒险家 = " + Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.冒险家"))
                + " 骑士团 = " + Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.骑士团"))
                + " 战神 = " + Boolean.parseBoolean(ServerProperties.getProperty("ZeroMS.战神")));
        System.out.println("\r\n服务端启动完成！... \r\n\r\n感谢使用 ZeroMS 079S3.0版 官方论坛：mxd.592uc.com");
    }

//    public static void 自动存档(int time) {
//        Timer.WorldTimer.getInstance().register(new Runnable() {
//
//            public void run() {
//
//                int ppl = 0;
//                try {
//                    for (ChannelServer cserv : ChannelServer.getAllInstances()) {
//                        for (MapleCharacter chr : cserv.getPlayerStorage().getAllCharacters()) {
//                            if (chr == null) {
//                                continue;
//                            }
//                            ppl++;
//                            chr.saveToDB(false, false);
//                        }
//                    }
//                } catch (Exception e) {
//                }
//                 System.out.println("[自动存档] 已经将 " + ppl + " 个玩家保存到数据中.");
//
//            }
//        }, 3600000* time);//1小时
//    }
    public static void 在线统计(int time) {
        System.out.println("服务端启用在线统计." + time + "分钟统计一次在线的人数信息.");
        Timer.WorldTimer.getInstance().register(new Runnable() {

            public void run() {
                Map connected = World.getConnected();
                StringBuilder conStr = new StringBuilder(new StringBuilder().append(FileoutputUtil.CurrentReadable_Time()).append(" 在线人数: ").toString());
                for (Iterator i$ = connected.keySet().iterator(); i$.hasNext();) {
                    int i = ((Integer) i$.next()).intValue();
                    if (i == 0) {
                        int users = ((Integer) connected.get(Integer.valueOf(i))).intValue();
                        conStr.append(StringUtil.getRightPaddedStr(String.valueOf(users), ' ', 3));
                        if (users > Start.maxUsers) {
                            Start.maxUsers = users;
                        }
                        conStr.append(" 最高在线: ");
                        conStr.append(Start.maxUsers);
                        break;
                    }
                }
                System.out.println(conStr.toString());
                if (Start.maxUsers > 0) {
                    FileoutputUtil.log("在线统计.txt", conStr.toString() + "\r\n");
                }
            }
        }, 60000 * time);//这里是1分钟。
    }

    public static void 在线时间(int time) {
        Timer.WorldTimer.getInstance().register(new Runnable() {

            @Override
            public void run() {
                try {
                    for (ChannelServer chan : ChannelServer.getAllInstances()) {
                        for (MapleCharacter chr : chan.getPlayerStorage().getAllCharacters()) {
                            if (chr == null) {
                                continue;
                            }
                            chr.gainGamePoints(1);
                            if (chr.getGamePoints() < 5) {

                                chr.resetGamePointsPS();
                                chr.resetGamePointsPD();
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }, 60000 * time);
    }

    public static class Shutdown implements Runnable {

        @Override
        public void run() {
            new Thread(ShutdownServer.getInstance()).start();
        }
    }
}
