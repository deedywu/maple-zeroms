package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import constants.ServerConstants.PlayerGMRank;
import handling.channel.ChannelServer;
import handling.world.World;
import scripting.NPCScriptManager;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.StringUtil;
import tools.packet.PetPacket;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import java.util.Arrays;
import server.life.MapleMonster;
import server.maps.MapleMap;

/**
 *
 * @author Emilyx3
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class 存档 extends save {
    }

    public static class 帮助 extends help {
    }

    public static class 领取点券 extends gainPoint {
    }

    public static class GM爆率 extends Mobdrop {
    }

    public static class ea extends 查看 {
    }

    public static class 解卡 extends 查看 {

    }

    public static class 复活 extends fh {

    }

    public static class 破攻 extends pg {

    }

    public static class 查看 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            //   PredictCardFactory.getInstance().initialize();
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            c.getPlayer().dropMessage(1, "假死已处理完毕.以后遇到假死，可以点聊天或者@ea，解决假死问题");
            //c.getPlayer().dropMessage(6, "当前时间是" + FileoutputUtil.CurrentReadable_Time() + " GMT+8 | 经验值倍率 " + (Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0) + "%, 怪物倍率 " + (Math.round(c.getPlayer().getDropMod()) * 100) * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%, 金币倍率 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100 + "%");
            c.getPlayer().dropMessage(6, "当前延迟 " + c.getPlayer().getClient().getLatency() + " 毫秒");
            //  NPCScriptManager.getInstance().start(c, 9102001);
            if (c.getPlayer().isAdmin()) {
                c.sendPacket(MaplePacketCreator.sendPyramidEnergy("massacre_hit", String.valueOf(50)));

//                  c.sendPacket(MaplePacketCreator.sendPyramidResult((byte) 1, 23));
//                MapleCharacter chr = c.getPlayer();
//                for (MaplePet pet : chr.getPets()) {
//                    if (pet.getSummoned()) {
//                        int newFullness = pet.getFullness() - PetDataFactory.getHunger(pet.getPetItemId());
//                        newFullness = 100;
//                        if (newFullness <= 5) {
//                            pet.setFullness(15);
//                            chr.unequipPet(pet, true);
//                        } else {
//                            pet.setFullness(newFullness);
//                            chr.getClient().getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem(pet.getInventoryPosition()), true));
//                        }
//                    }
//                }
//                c.getPlayer().getStat().setDex((short) 4);
//                c.getPlayer().updateSingleStat(MapleStat.DEX, 4);
                //   System.out.println(new Date(System.currentTimeMillis() + (long) (3 * 60 * 60 * 1000)));
                //     c.getPlayer().getGuild().gainGP(50);  
                //     c.getPlayer().saveToDB(false, false);
            }//      
            return 1;
        }
    }

    public static class save extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().saveToDB(false, false);
            c.getPlayer().dropMessage("存档成功");
            return 1;
        }
    }

    public static class gainPoint extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 9270034);
            return 1;
        }
    }

    public static class Mobdrop extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().dispose(c);
            c.getSession().write(MaplePacketCreator.enableActions());
            NPCScriptManager npc = NPCScriptManager.getInstance();
            npc.start(c, 9900007);
            return 1;
        }
    }

    public static class mob extends 怪物 {
    }

    public static class 怪物 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            MapleMonster mob = null;
            for (final MapleMapObject monstermo : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物 " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "找不到地图上的怪物");
            }
            return 1;
        }
    }

    public static class 爆率 extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            NPCScriptManager.getInstance().start(c, 9010000, 1);
            return 1;
        }
    }

    public static class CGM extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted[1] == null) {
                c.getPlayer().dropMessage(6, "请打字谢谢.");
                return 1;
            }
            if (c.getPlayer().isGM()) {
                c.getPlayer().dropMessage(6, "因为你自己是GM无法使用此命令,可以尝试!cngm <讯息> 來建立GM聊天頻道~");
                return 1;
            }
            if (!c.getPlayer().getCheatTracker().GMSpam(100000, 1)) { // 5 minutes.
                World.Broadcast.broadcastGMMessage(MaplePacketCreator.serverNotice(6, "頻道 " + c.getPlayer().getClient().getChannel() + " 玩家 [" + c.getPlayer().getName() + "] : " + StringUtil.joinStringFrom(splitted, 1)).getBytes());
                c.getPlayer().dropMessage(6, "讯息已经发给GM了!");
            } else {
                c.getPlayer().dropMessage(6, "为了防止对GM刷屏所以每1分鐘只能发一次.");
            }
            return 1;
        }
    }

    public static class fh extends CommandExecute {

        public int execute(MapleClient c, String[] splitted) {
            if (c.getPlayer().getLevel() < 70) {
                c.getPlayer().dropMessage(5, "等级达到70级才可以使用这个命令.");
                return 0;
            }
            if (c.getPlayer().isAlive()) {
                c.getPlayer().dropMessage(5, "您都还没有挂掉，怎么能使用这个命令呢。");
                return 0;
            }
            if ((c.getPlayer().getBossLog("原地复活") >= 5) && (c.getPlayer().getCSPoints(2) < 300)) {
                c.getPlayer().dropMessage(5, "您今天的免费复活次数已经用完或者您的抵用卷不足300点。");
                return 0;
            }
            if (c.getPlayer().getBossLog("原地复活") < 5) {
                c.getPlayer().setBossLog("原地复活");
                c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp());
                c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp());
                c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
                c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp());
                c.getPlayer().dispelDebuffs();
                c.getPlayer().dropMessage(5, "恭喜您原地复活成功，您今天还可以免费使用: " + (5 - c.getPlayer().getBossLog("原地复活")) + " 次。");
                return 1;
            }
            if (c.getPlayer().getCSPoints(2) >= 300) {
                c.getPlayer().modifyCSPoints(2, -300);
                c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp());
                c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp());
                c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
                c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp());
                c.getPlayer().dispelDebuffs();
                c.getPlayer().dropMessage(5, "恭喜您原地复活成功，本次复活花费抵用卷 300 点。");
                return 1;
            }
            c.getPlayer().dropMessage(5, "复活失败，您今天的免费复活次数已经用完或者您的抵用卷不足300点。");
            return 0;
        }
    }

    public static class pg extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            int VipCount = c.getPlayer().getVip();
            long maxdamage = (199999 + (VipCount * 10000));
            int damage = 0;
            damage = Math.min(damage, 199999 + (VipCount * 10000));
            if ((maxdamage >= 2147483647) || (maxdamage < 0)) {
                maxdamage = 2147483647;
            }
            String mds = "当前您的伤害上限为： " + maxdamage + " ";
            c.getPlayer().dropMessage(5, "============================================================");
            c.getPlayer().dropMessage(5, "                  " + c.getChannelServer().getServerName() + "    ");
            c.getPlayer().dropMessage(5, "============================================================");
            // c.getPlayer().dropMessage(-11, "目前您的岛民等级为：" + c.getPlayer().getMaplewing("cardlevel") + "  拥有贡献点: " + c.getPlayer().getMaplewing("maple"));
            // c.getPlayer().dropMessage(-11, "当前伤害上限倍率：" + ChannelServer.getpogpngbilv());
            c.getPlayer().dropMessage(5, "基础伤害: " + c.getPlayer().getStat().getCurrentMaxBaseDamage() + "");
            // c.getPlayer().dropMessage(5, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
            c.getPlayer().dropMessage(5, "伤害上限计算公式： 基础上限(199999) + 破攻石增加的上限 ");
            c.getPlayer().dropMessage(-1, mds);
            c.getPlayer().dropMessage(5, mds);
            return 1;
        }
    }

    /*  public static class STR extends DistributeStatCommands {

        public STR() {
            stat = MapleStat.STR;
        }
    }

    public static class DEX extends DistributeStatCommands {

        public DEX() {
            stat = MapleStat.DEX;
        }
    }

    public static class INT extends DistributeStatCommands {

        public INT() {
            stat = MapleStat.INT;
        }
    }

    public static class LUK extends DistributeStatCommands {

        public LUK() {
            stat = MapleStat.LUK;
        }
    }

    public abstract static class DistributeStatCommands extends CommandExecute {

        protected MapleStat stat = null;
        private static int statLim = 999;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) amount);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) amount);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) amount);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) amount);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
            }
        }

        private int getStat(MapleCharacter player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public int execute(MapleClient c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(5, "输入的数字无效.");
                return 0;
            }
            int change = 0;
            try {
                change = Integer.parseInt(splitted[1]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "输入的数字无效.");
                return 0;
            }
            if (change <= 0) {
                c.getPlayer().dropMessage(5, "您必须输入一个大于 0 的数字.");
                return 0;
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(5, "您的能力点不足.");
                return 0;
            }
            if (getStat(c.getPlayer()) + change > statLim) {
                c.getPlayer().dropMessage(5, "所要分配的能力点总和不能大于 " + statLim + ".");
                return 0;
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + change);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLEAP, c.getPlayer().getRemainingAp());
            c.getPlayer().dropMessage(5, "加点成功您的 " +StringUtil.makeEnumHumanReadable(stat.name()) + " 提高了 " + change + "点.");
            return 1;
        }
    }*/

    public static class help extends CommandExecute {

        @Override
        public int execute(MapleClient c, String[] splitted) {
            c.getPlayer().dropMessage(5, "指令列表 :");
            c.getPlayer().dropMessage(5, "@解卡/@查看/@ea  <解除异常+查看当前状态>");
            c.getPlayer().dropMessage(5, "@CGM 讯息        <传送讯息給GM>");
            c.getPlayer().dropMessage(5, "@str, @dex, @int, @luk <数值>");
            c.getPlayer().dropMessage(5, "@爆率       <查询当前地图怪物爆率>");
            c.getPlayer().dropMessage(5, "@怪物/@mob <查看身边怪物信息/血量>");
            //c.getPlayer().dropMessage(5, "@领取点券        < 充值领取点券 >");
            c.getPlayer().dropMessage(5, "@存档            < 储存当前人物信息 >");
            c.getPlayer().dropMessage(5, "@复活/fh      < 原地复活，每天有5次原地复活的机会超过需要300点卷 >");
            // c.getPlayer().dropMessage(5, "@召唤队友/zhdy   < 召唤队友到身边，每天有5次的机会超过需要300点卷 >");
            c.getPlayer().dropMessage(5, "@pg   查看自己攻击伤害上限(也可以使用@破攻)");
            return 1;
        }
    }
}
