
package handling.channel.handler;

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.PlayerStats;
import client.SkillFactory;
import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.Randomizer;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author zjj
 */
public class StatsHandling {

    private static Logger log = LoggerFactory.getLogger(StatsHandling.class);

    /**
     *
     * @param slea
     * @param c
     * @param chr
     */
    public static final void DistributeAP(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final List<Pair<MapleStat, Integer>> statupdate = new ArrayList<>(2);
        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr.getJob()));
        chr.updateTick(slea.readInt());

        final PlayerStats stat = chr.getStat();
        final int job = chr.getJob();
        if (chr.getRemainingAp() > 0) {
            switch (slea.readInt()) {
                case 256: // Str
                    if (stat.getStr() >= 999) {
                        return;
                    }
                    stat.setStr((short) (stat.getStr() + 1));
                    statupdate.add(new Pair<>(MapleStat.STR, (int) stat.getStr()));
                    break;
                case 512: // Dex
                    if (stat.getDex() >= 999) {
                        return;
                    }
                    stat.setDex((short) (stat.getDex() + 1));
                    statupdate.add(new Pair<>(MapleStat.DEX, (int) stat.getDex()));
                    break;
                case 1_024: // Int
                    if (stat.getInt() >= 999) {
                        return;
                    }
                    stat.setInt((short) (stat.getInt() + 1));
                    statupdate.add(new Pair<>(MapleStat.INT, (int) stat.getInt()));
                    break;
                case 2_048: // Luk
                    if (stat.getLuk() >= 999) {
                        return;
                    }
                    stat.setLuk((short) (stat.getLuk() + 1));
                    statupdate.add(new Pair<>(MapleStat.LUK, (int) stat.getLuk()));
                    break;
                case 8_192: // HP
                    short maxhp = stat.getMaxHp();
                    if (chr.getHpApUsed() >= 10_000 || maxhp >= 30_000) {
                        return;
                    }
                    if (job == 0) { // Beginner
                        maxhp += Randomizer.rand(8, 12);
                    } else if ((job >= 100 && job <= 132) || (job >= 3_200 && job <= 3_212)) { // Warrior
                        ISkill improvingMaxHP = SkillFactory.getSkill(1_000_001);
                        int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(20, 25);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getX();
                        }
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job))) { // Magician
                        maxhp += Randomizer.rand(10, 20);
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1_300 && job <= 1_312) || (job >= 1_400 && job <= 1_412) || (job >= 3_300 && job <= 3_312)) { // Bowman
                        maxhp += Randomizer.rand(16, 20);
                    } else if ((job >= 500 && job <= 522) || (job >= 3_500 && job <= 3_512)) { // Pirate
                        ISkill improvingMaxHP = SkillFactory.getSkill(5_100_000);
                        int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(18, 22);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1_500 && job <= 1_512) { // Pirate
                        ISkill improvingMaxHP = SkillFactory.getSkill(15_100_000);
                        int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(18, 22);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1_100 && job <= 1_112) { // Soul Master
                        ISkill improvingMaxHP = SkillFactory.getSkill(11_000_000);
                        int improvingMaxHPLevel = c.getPlayer().getSkillLevel(improvingMaxHP);
                        maxhp += Randomizer.rand(36, 42);
                        if (improvingMaxHPLevel >= 1) {
                            maxhp += improvingMaxHP.getEffect(improvingMaxHPLevel).getY();
                        }
                    } else if (job >= 1_200 && job <= 1_212) { // Flame Wizard
                        maxhp += Randomizer.rand(15, 21);
                    } else if (job >= 2_000 && job <= 2_112) { // Aran
                        maxhp += Randomizer.rand(40, 50);
                    } else { // GameMaster
                        maxhp += Randomizer.rand(50, 100);
                    }
                    maxhp = (short) Math.min(30_000, Math.abs(maxhp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxHp(maxhp);
                    statupdate.add(new Pair<>(MapleStat.MAXHP, (int) maxhp));
                    break;
                case 32_768: // MP
                    short maxmp = stat.getMaxMp();
                    short Int = (short) ((short) (stat.getInt() / 10) - 10);
                    if (Int < 0) {
                        Int = 0;
                    }
                    if (chr.getHpApUsed() >= 10_000 || stat.getMaxMp() >= 30_000) {
                        return;
                    }
                    if (job == 0) { // Beginner
                        maxmp += Randomizer.rand(6, 8);
                    } else if (job >= 100 && job <= 132) { // Warrior
                        maxmp += Randomizer.rand(2, 4);
                    } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3_200 && job <= 3_212)) { // Magician
                        ISkill improvingMaxMP = SkillFactory.getSkill(2_000_001);
                        int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                        maxmp += Randomizer.rand(18, 20);
                        if (improvingMaxMPLevel >= 1) {
                            maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                        }
                    } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 522) || (job >= 3_200 && job <= 3_212) || (job >= 3_500 && job <= 3_512) || (job >= 1_300 && job <= 1_312) || (job >= 1_400 && job <= 1_412) || (job >= 1_500 && job <= 1_512)) { // Bowman
                        maxmp += Randomizer.rand(10, 12);
                    } else if (job >= 1_100 && job <= 1_112) { // Soul Master
                        maxmp += Randomizer.rand(6, 9);
                    } else if (job >= 1_200 && job <= 1_212) { // Flame Wizard
                        ISkill improvingMaxMP = SkillFactory.getSkill(12_000_000);
                        int improvingMaxMPLevel = c.getPlayer().getSkillLevel(improvingMaxMP);
                        maxmp += Randomizer.rand(18, 20);
                        if (improvingMaxMPLevel >= 1) {
                            maxmp += improvingMaxMP.getEffect(improvingMaxMPLevel).getY() * 2;
                        }
                    } else if (job >= 2_000 && job <= 2_112) { // Aran
                        maxmp += Randomizer.rand(6, 9);
                    } else { // GameMaster
                        maxmp += Randomizer.rand(50, 100);
                    }
                    maxmp = (short) (maxmp + Int);
                    maxmp = (short) Math.min(30_000, Math.abs(maxmp));
                    chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                    stat.setMaxMp(maxmp);
                    statupdate.add(new Pair<>(MapleStat.MAXMP, (int) maxmp));
                    break;
                default:
                    c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, chr.getJob()));
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
            statupdate.add(new Pair<>(MapleStat.AVAILABLEAP, (int) chr.getRemainingAp()));
            c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr.getJob()));
        }
    }

    /**
     *
     * @param skillid
     * @param c
     * @param chr
     */
    public static void DistributeSP(final int skillid, final MapleClient c, final MapleCharacter chr) {
        boolean isBeginnerSkill = false;
        final int remainingSp;

        switch (skillid) {
            case 1_000:
            case 1_001:
            case 1_002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(1_000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(1_001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(1_002));
                remainingSp = Math.min((chr.getLevel() - 1), 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 10_001_000:
            case 10_001_001:
            case 10_001_002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(10_001_000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(10_001_001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(10_001_002));
                remainingSp = Math.min((chr.getLevel() - 1), 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 20_001_000:
            case 20_001_001:
            case 20_001_002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(20_001_000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(20_001_001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(20_001_002));
                remainingSp = Math.min((chr.getLevel() - 1), 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 20_011_000:
            case 20_011_001:
            case 20_011_002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(20_011_000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(20_011_001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(20_011_002));
                remainingSp = Math.min((chr.getLevel() - 1), 6) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true;
                break;
            }
            case 30_001_000:
            case 30_001_001:
            case 30_000_002: {
                final int snailsLevel = chr.getSkillLevel(SkillFactory.getSkill(30_001_000));
                final int recoveryLevel = chr.getSkillLevel(SkillFactory.getSkill(30_001_001));
                final int nimbleFeetLevel = chr.getSkillLevel(SkillFactory.getSkill(30_000_002));
                remainingSp = Math.min((chr.getLevel() - 1), 9) - snailsLevel - recoveryLevel - nimbleFeetLevel;
                isBeginnerSkill = true; //resist can max ALL THREE
                break;
            }
            default: {
                remainingSp = chr.getRemainingSp(GameConstants.getSkillBookForSkill(skillid));
                break;
            }
        }
        final ISkill skill = SkillFactory.getSkill(skillid);

        if (skill.hasRequiredSkill()) {
            if (chr.getSkillLevel(SkillFactory.getSkill(skill.getRequiredSkillId())) < skill.getRequiredSkillLevel()) {
//                AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill without the required skill (" + skillid + ")");
                return;
            }
        }
        final int maxlevel = skill.isFourthJob() ? chr.getMasterLevel(skill) : skill.getMaxLevel();
        final int curLevel = chr.getSkillLevel(skill);

        if (skill.isInvisible() && chr.getSkillLevel(skill) == 0) {
            if ((skill.isFourthJob() && chr.getMasterLevel(skill) == 0) || (!skill.isFourthJob() && maxlevel < 10 && !isBeginnerSkill)) {
                //AutobanManager.getInstance().addPoints(c, 1000, 0, "Illegal distribution of SP to invisible skills (" + skillid + ")");
                return;
            }
        }

        for (int i : GameConstants.blockedSkills) {
            if (skill.getId() == i) {
                chr.dropMessage(1, "你可能不会增加这个技能.");
                return;
            }
        }

        if ((remainingSp > 0 && curLevel + 1 <= maxlevel) && (skill.canBeLearnedBy(chr.getJob()) || isBeginnerSkill)) {
            if (!isBeginnerSkill) {
                final int skillbook = GameConstants.getSkillBookForSkill(skillid);
                chr.setRemainingSp(chr.getRemainingSp(skillbook) - 1, skillbook);
            }
            chr.updateSingleStat(MapleStat.AVAILABLESP, chr.getRemainingSp());
            // c.getSession().write(MaplePacketCreator.updateSp(chr, false));
            chr.changeSkillLevel(skill, (byte) (curLevel + 1), chr.getMasterLevel(skill));
        } else if (!skill.canBeLearnedBy(chr.getJob())) {
//            AutobanManager.getInstance().addPoints(c, 1000, 0, "Trying to learn a skill for a different job (" + skillid + ")");

        }
    }

    /**
     *
     * @param slea
     * @param c
     * @param chr
     */
    public static final void AutoAssignAP(final SeekableLittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        List statupdate = new ArrayList(2);
        //   c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, 0));
        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr.getJob()));
        final PlayerStats playerst = chr.getStat();
        slea.readInt();
        int count = slea.readInt();
        for (int i = 0; i < count; i++) {
            int update = slea.readInt();
            int updatenumber = slea.readInt();
            if (chr.getRemainingAp() >= updatenumber) {
                switch (update) {
                    case 256:
                        if (playerst.getStr() + updatenumber >= 30_000) {
                            return;
                        }
                        playerst.setStr((short) (playerst.getStr() + updatenumber));
                        statupdate.add(new Pair(MapleStat.STR, Integer.valueOf(playerst.getStr())));
                        break;
                    case 512:
                        if (playerst.getDex() + updatenumber >= 30_000) {
                            return;
                        }
                        playerst.setDex((short) (playerst.getDex() + updatenumber));
                        statupdate.add(new Pair(MapleStat.DEX, Integer.valueOf(playerst.getDex())));
                        break;
                    case 1_024:
                        if (playerst.getInt() + updatenumber >= 30_000) {
                            return;
                        }
                        playerst.setInt((short) (playerst.getInt() + updatenumber));
                        statupdate.add(new Pair(MapleStat.INT, Integer.valueOf(playerst.getInt())));
                        break;
                    case 2_048:
                        if (playerst.getLuk() + updatenumber >= 30_000) {
                            return;
                        }
                        playerst.setLuk((short) (playerst.getLuk() + updatenumber));
                        statupdate.add(new Pair(MapleStat.LUK, Integer.valueOf(playerst.getLuk())));
                        break;
                    default:
                        //  c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, 0));
                        c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, chr.getJob()));
                        return;
                }
                chr.setRemainingAp((short) (chr.getRemainingAp() - updatenumber));
            } else {
                log.info("[h4x] Player {} is distributing AP to {} without having any", chr.getName(), update);
            }
        }
        // statupdate.add(new Pair(MapleStat.AVAILABLEAP, Integer.valueOf(chr.getRemainingAp())));
        statupdate.add(new Pair<>(MapleStat.AVAILABLEAP, (int) chr.getRemainingAp()));
        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr.getJob()));
        //   c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, 0));

        /* chr.updateTick(slea.readInt());
        slea.skip(4);
        if (slea.available() < 16) {
            System.out.println("AutoAssignAP : \n" + slea.toString(true));
            FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "AutoAssignAP : \n" + slea.toString(true));
            return;
        }
        final int PrimaryStat = slea.readInt();
        final int amount = slea.readInt();
        final int SecondaryStat = slea.readInt();
        final int amount2 = slea.readInt();
        if (amount < 0 || amount2 < 0) {
            return;
        }

        final PlayerStats playerst = chr.getStat();

        List<Pair<MapleStat, Integer>> statupdate = new ArrayList<Pair<MapleStat, Integer>>(2);
        c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr.getJob()));

        if (chr.getRemainingAp() == amount + amount2) {
            switch (PrimaryStat) {
                case 64: // Str
                    if (playerst.getStr() + amount > 999) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, (int) playerst.getStr()));
                    break;
                case 128: // Dex
                    if (playerst.getDex() + amount > 999) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, (int) playerst.getDex()));
                    break;
                case 256: // Int
                    if (playerst.getInt() + amount > 999) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, (int) playerst.getInt()));
                    break;
                case 512: // Luk
                    if (playerst.getLuk() + amount > 999) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, (int) playerst.getLuk()));
                    break;
                default:
                    c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, chr.getJob()));
                    return;
            }
            switch (SecondaryStat) {
                case 64: // Str
                    if (playerst.getStr() + amount2 > 999) {
                        return;
                    }
                    playerst.setStr((short) (playerst.getStr() + amount2));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.STR, (int) playerst.getStr()));
                    break;
                case 128: // Dex
                    if (playerst.getDex() + amount2 > 999) {
                        return;
                    }
                    playerst.setDex((short) (playerst.getDex() + amount2));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.DEX, (int) playerst.getDex()));
                    break;
                case 256: // Int
                    if (playerst.getInt() + amount2 > 999) {
                        return;
                    }
                    playerst.setInt((short) (playerst.getInt() + amount2));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.INT, (int) playerst.getInt()));
                    break;
                case 512: // Luk
                    if (playerst.getLuk() + amount2 > 999) {
                        return;
                    }
                    playerst.setLuk((short) (playerst.getLuk() + amount2));
                    statupdate.add(new Pair<MapleStat, Integer>(MapleStat.LUK, (int) playerst.getLuk()));
                    break;
                default:
                    c.getSession().write(MaplePacketCreator.updatePlayerStats(MaplePacketCreator.EMPTY_STATUPDATE, true, chr.getJob()));
                    return;
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            statupdate.add(new Pair<MapleStat, Integer>(MapleStat.AVAILABLEAP, (int) chr.getRemainingAp()));
            c.getSession().write(MaplePacketCreator.updatePlayerStats(statupdate, true, chr.getJob()));
        }*/
    }
}
