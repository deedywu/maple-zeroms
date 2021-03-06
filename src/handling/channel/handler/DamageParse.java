package handling.channel.handler;

import client.ISkill;
import client.MapleBuffStat;
import client.MapleCharacter;
import client.PlayerStats;
import client.SkillFactory;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.Equip;
import client.inventory.IItem;
import client.inventory.MapleInventoryType;
import client.status.MonsterStatus;
import client.status.MonsterStatusEffect;
import constants.GameConstants;
import handling.world.World;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.Element;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.AttackPair;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;
import tools.data.input.LittleEndianAccessor;

/**
 *
 * @author zjj
 */
public class DamageParse {

    private final static int[] charges = {1_211_005, 1_211_006};

    /**
     *
     * @param attack
     * @param theSkill
     * @param player
     * @param attackCount
     * @param maxDamagePerMonster
     * @param effect
     * @param attack_type
     */
    public static void applyAttack(final AttackInfo attack, final ISkill theSkill, final MapleCharacter player, int attackCount, final double maxDamagePerMonster, final MapleStatEffect effect, final AttackType attack_type) {
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.人物死亡攻击);
            return;
        }
        if (attack.real) {
            // player.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }
        if (attack.skill != 0) {
            boolean ban = false;
            String lastReason = "";
            String reason = "";
            if (effect == null) {
                player.getClient().getSession().write(MaplePacketCreator.enableActions());
                return;
            }

            // 检测外挂区---------------
            reason = Damage_MobCount(player, effect, attack);
            if (!"null".equals(reason)) {// 怪物数量
                lastReason = reason;
                ban = false;
            }
            if (!ban) {
                reason = Damage_AttackCount(player, effect, attack, attackCount);
            }
            if (!"null".equals(reason)) {//伤害次数
                lastReason = reason;
                ban = false;
            }
            if (!ban) {
                reason = Damage_HighDamage(player, effect, attack);
            }
            if (!"null".equals(reason)) {// 伤害过高
                lastReason = reason;
                ban = false;
                // ban = true;
            }

            // 检测外挂区结束-------------
            // 封锁区
            //if (ban && !player.isAdmin()) {
            //    player.ban(lastReason, false, true, false);//封号
            //   FileoutputUtil.logToFile_chr(player, FileoutputUtil.ban_log, lastReason);// 输出文挡
            //  World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[封号系统]" + player.getName() + " 该玩家攻击异常被系统自动封号处理。").getBytes());//广播
            //  return;
            // }
            // 封锁区结束
            if (GameConstants.isMulungSkill(attack.skill)) {
                if (player.getMapId() / 10_000 != 92_502) {
                    //AutobanManager.getInstance().autoban(player.getClient(), "Using Mu Lung dojo skill out of dojo maps.");
                    return;
                } else {
                    player.mulung_EnergyModify(false);
                }
            }
            if (GameConstants.isPyramidSkill(attack.skill)) {
                if (player.getMapId() / 1_000_000 != 926) {
                    //AutobanManager.getInstance().autoban(player.getClient(), "Using Pyramid skill outside of pyramid maps.");
                    return;
                } else if (player.getPyramidSubway() == null || !player.getPyramidSubway().onSkillUse(player)) {
                    return;
                }
            }

        }
        int totDamage = 0;
        final MapleMap map = player.getMap();

        if (attack.skill == 4_211_006) { // meso explosion
            for (AttackPair oned : attack.allDamage) {
                if (oned.attack != null) {
                    continue;
                }
                final MapleMapObject mapobject = map.getMapObject(oned.objectid, MapleMapObjectType.ITEM);

                if (mapobject != null) {
                    final MapleMapItem mapitem = (MapleMapItem) mapobject;
                    mapitem.getLock().lock();
                    try {
                        if (mapitem.getMeso() > 0) {
                            if (mapitem.isPickedUp()) {
                                return;
                            }
                            map.removeMapObject(mapitem);
                            map.broadcastMessage(MaplePacketCreator.explodeDrop(mapitem.getObjectId()));
                            mapitem.setPickedUp(true);
                        } else {
                            player.getCheatTracker().registerOffense(CheatingOffense.其他异常);
                            return;
                        }
                    } finally {
                        mapitem.getLock().unlock();
                    }
                } else {
                    player.getCheatTracker().registerOffense(CheatingOffense.金钱炸弹_不存在道具);
                    return; // etc explosion, exploding nonexistant things, etc.
                }
            }
        }
        int fixeddmg, totDamageToOneMonster = 0;
        long hpMob = 0;
        final PlayerStats stats = player.getStat();

        int CriticalDamage = stats.passive_sharpeye_percent();
        byte ShdowPartnerAttackPercentage = 0;
        if (attack_type == AttackType.RANGED_WITH_SHADOWPARTNER || attack_type == AttackType.NON_RANGED_WITH_MIRROR) {
            final MapleStatEffect shadowPartnerEffect;
            if (attack_type == AttackType.NON_RANGED_WITH_MIRROR) {
                shadowPartnerEffect = player.getStatForBuff(MapleBuffStat.MIRROR_IMAGE);
            } else {
                shadowPartnerEffect = player.getStatForBuff(MapleBuffStat.SHADOWPARTNER);
            }
            if (shadowPartnerEffect != null) {
                if (attack.skill != 0 && attack_type != AttackType.NON_RANGED_WITH_MIRROR) {
                    ShdowPartnerAttackPercentage = (byte) shadowPartnerEffect.getY();
                } else {
                    ShdowPartnerAttackPercentage = (byte) shadowPartnerEffect.getX();
                }
            }
            attackCount /= 2; // hack xD
        }

        byte overallAttackCount; // Tracking of Shadow Partner additional damage.
        double maxDamagePerHit = 0;
        MapleMonster monster;
        MapleMonsterStats monsterstats;
        boolean Tempest;

        for (final AttackPair oned : attack.allDamage) {
            monster = map.getMonsterByOid(oned.objectid);

            if (monster != null) {
                //Damage_Position(player, monster, attack);//全图打检测
                totDamageToOneMonster = 0;
                hpMob = monster.getHp();
                monsterstats = monster.getStats();
                fixeddmg = monsterstats.getFixedDamage();
                Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == 21_120_006;
                maxDamagePerHit = CalculateMaxWeaponDamagePerHit(player, monster, attack, theSkill, effect, maxDamagePerMonster, CriticalDamage);
                overallAttackCount = 0; // Tracking of Shadow Partner additional damage.
                Integer eachd;
                for (Pair<Integer, Boolean> eachde : oned.attack) {
                    eachd = eachde.left;
                    overallAttackCount++;

                    if (overallAttackCount - 1 == attackCount) { // Is a Shadow partner hit so let's divide it once
                        maxDamagePerHit = (maxDamagePerHit / 100) * ShdowPartnerAttackPercentage;
                    }

                    if (fixeddmg != -1) {
                        if (monsterstats.getOnlyNoramlAttack()) {
                            eachd = attack.skill != 0 ? 0 : fixeddmg;
                        } else {
                            eachd = fixeddmg;
                        }
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = attack.skill != 0 ? 0 : Math.min(eachd, (int) maxDamagePerHit);  // Convert to server calculated damage
                    } else if (!player.isGM()) {
                        if (Tempest) { // Monster buffed with Tempest
                            if (eachd > monster.getMobMaxHp()) {
                                eachd = (int) Math.min(monster.getMobMaxHp(), Integer.MAX_VALUE);
                                player.getCheatTracker().registerOffense(CheatingOffense.攻击力过高);
                            }
                        } else if (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
                            if (eachd > maxDamagePerHit) {
                                player.getCheatTracker().registerOffense(CheatingOffense.攻击力过高);
                                if (eachd > maxDamagePerHit * 2) {
                                    FileoutputUtil.logToFile_chr(player, FileoutputUtil.fixdam_ph, " 技能 " + attack.skill + " 怪物 " + monster.getId() + " 预计伤害:" + (long) maxDamagePerHit + "  实际" + eachd);
                                    eachd = (int) (maxDamagePerHit * 2); // Convert to server calculated damage
                                    player.getCheatTracker().registerOffense(CheatingOffense.攻击过高2);
                                }
                            }
                        } else if (eachd > maxDamagePerHit * 2) {
                            FileoutputUtil.logToFile_chr(player, FileoutputUtil.fixdam_ph, " 技能 " + attack.skill + " 怪物 " + monster.getId() + " 预计伤害:" + (long) maxDamagePerHit + "  实际" + eachd);
                            eachd = (int) (maxDamagePerHit);
                        }
                    }
                    if (player == null) { // o_O
                        return;
                    }
                    totDamageToOneMonster += eachd;
                    //force the miss even if they dont miss. popular wz edit
                    if (monster.getId() == 9_300_021 && player.getPyramidSubway() != null) { //miss
                        player.getPyramidSubway().onMiss(player);
                    }
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);
                if (attack.skill == 2_301_002 && !monsterstats.getUndead()) {
                    player.ban("修改WZ", true, true, false);
                    FileoutputUtil.logToFile_chr(player, FileoutputUtil.ban_log, "使用群体治愈伤害怪物 " + monster.getId());
                    World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[封号系统] " + player.getName() + " 该玩家攻击异常被系统自动封号处理。"));
                    return;
                }
                double Position_range = player.getPosition().distanceSq(monster.getPosition());
                double Count_range = 700000.0;
                if (Position_range > Count_range) { // 815^2 <-- the most ranged attack in the game is Flame Wheel at 815 range
                    player.getCheatTracker().registerOffense(CheatingOffense.攻击范围过大, " 技能 " + attack.skill + " 范围 : " + (long) Position_range + "正常范围 " + (long) Count_range); // , Double.toString(Math.sqrt(distance))
                    return;
                }
                // pickpocket
                if (player.getBuffedValue(MapleBuffStat.PICKPOCKET) != null) {
                    switch (attack.skill) {
                        case 0:
                        case 4_001_334:
                        case 4_201_005:
                        case 4_211_002:
                        case 4_211_004:
                        case 4_221_003:
                        case 4_221_007:
                            handlePickPocket(player, monster, oned);
                            break;
                    }
                }
                final MapleStatEffect ds = player.getStatForBuff(MapleBuffStat.DARKSIGHT);
                if (ds != null && !player.isGM()) {
                    if (ds.getSourceId() != 4_330_001 || !ds.makeChanceResult()) {
                        player.cancelEffectFromBuffStat(MapleBuffStat.DARKSIGHT);
                    }
                }

                if (totDamageToOneMonster > 0) {
                    if (attack.skill != 1_221_011) {
                        monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    } else {
                        monster.damage(player, (monster.getStats().isBoss() ? 199_999 : (monster.getHp() - 1)), true, attack.skill);
                    }
                    if (monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) { //test
                        player.addHP(-(7_000 + Randomizer.nextInt(8_000))); //this is what it seems to be?
                    }
                    if (stats.hpRecoverProp > 0) {
                        if (Randomizer.nextInt(100) <= stats.hpRecoverProp) {//i think its out of 100, anyway
                            player.healHP(stats.hpRecover);
                        }
                    }
                    if (stats.mpRecoverProp > 0) {
                        if (Randomizer.nextInt(100) <= stats.mpRecoverProp) {//i think its out of 100, anyway
                            player.healMP(stats.mpRecover);
                        }
                    }
                    if (player.getBuffedValue(MapleBuffStat.COMBO_DRAIN) != null) {
                        stats.setHp((stats.getHp() + ((int) Math.min(monster.getMobMaxHp(), Math.min(((int) ((double) totDamage * (double) player.getStatForBuff(MapleBuffStat.COMBO_DRAIN).getX() / 100.0)), stats.getMaxHp() / 2)))), true);
                    }
                    // effects
                    switch (attack.skill) {
                        case 4_101_005: //drain
                        case 5_111_004: { // Energy Drain
                            stats.setHp((stats.getHp() + ((int) Math.min(monster.getMobMaxHp(), Math.min(((int) ((double) totDamage * (double) theSkill.getEffect(player.getSkillLevel(theSkill)).getX() / 100.0)), stats.getMaxHp() / 2)))), true);
                            break;
                        }
                        case 5_211_006:
                        case 22_151_002: //killer wing
                        case 5_220_011: {//homing
                            player.setLinkMid(monster.getObjectId());
                            break;
                        }
                        case 1_311_005: { // Sacrifice
                            final int remainingHP = stats.getHp() - totDamage * effect.getX() / 100;
                            stats.setHp(remainingHP < 1 ? 1 : remainingHP);
                            break;
                        }
                        case 4_301_001:
                        case 4_311_002:
                        case 4_311_003:
                        case 4_331_000:
                        case 4_331_004:
                        case 4_331_005:
                        case 4_341_005:
                        case 4_221_007: // Boomerang Stab
                        case 4_221_001: // Assasinate
                        case 4_211_002: // Assulter
                        case 4_201_005: // Savage Blow
                        case 4_001_002: // Disorder
                        case 4_001_334: // Double Stab
                        case 4_121_007: // Triple Throw
                        case 4_111_005: // Avenger
                        case 4_001_344: { // Lucky Seven
                            // Venom
                            final ISkill skill = SkillFactory.getSkill(4_120_005);
                            final ISkill skill2 = SkillFactory.getSkill(4_220_005);
                            final ISkill skill3 = SkillFactory.getSkill(4_340_001);
                            if (player.getSkillLevel(skill) > 0) {
                                //屏蔽武器用毒 防止掉线
//                                final MapleStatEffect venomEffect = skill.getEffect(player.getSkillLevel(skill));
//                                MonsterStatusEffect monsterStatusEffect;
//
//                                for (int i = 0; i < attackCount; i++) {
//                                    if (venomEffect.makeChanceResult()) {
//                                        if (monster.getVenomMulti() < 3) {
//                                            monster.setVenomMulti((byte) (monster.getVenomMulti() + 1));
//                                            monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4120005, null, false);
//                                            monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
//                                        }
//                                    }
//                                }
                            } else if (player.getSkillLevel(skill2) > 0) {
                                final MapleStatEffect venomEffect = skill2.getEffect(player.getSkillLevel(skill2));
                                MonsterStatusEffect monsterStatusEffect;

                                for (int i = 0; i < attackCount; i++) {
                                    if (venomEffect.makeChanceResult()) {
                                        if (monster.getVenomMulti() < 3) {
                                            monster.setVenomMulti((byte) (monster.getVenomMulti() + 1));
                                            monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4_220_005, null, false);
                                            monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                                        }
                                    }
                                }
                            } else if (player.getSkillLevel(skill3) > 0) {
                                final MapleStatEffect venomEffect = skill3.getEffect(player.getSkillLevel(skill3));
                                MonsterStatusEffect monsterStatusEffect;

                                for (int i = 0; i < attackCount; i++) {
                                    if (venomEffect.makeChanceResult()) {
                                        if (monster.getVenomMulti() < 3) {
                                            monster.setVenomMulti((byte) (monster.getVenomMulti() + 1));
                                            monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.POISON, 1, 4_340_001, null, false);
                                            monster.applyStatus(player, monsterStatusEffect, false, venomEffect.getDuration(), true);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 4_201_004: { //steal
                            monster.handleSteal(player);
                            break;
                        }
                        //case 21101003: // body pressure
                        case 21_000_002: // Double attack
                        case 21_100_001: // Triple Attack
                        case 21_100_002: // Pole Arm Push
                        case 21_100_004: // Pole Arm Smash
                        case 21_110_002: // Full Swing
                        case 21_110_003: // Pole Arm Toss
                        case 21_110_004: // Fenrir Phantom
                        case 21_110_006: // Whirlwind
                        case 21_110_007: // (hidden) Full Swing - Double Attack
                        case 21_110_008: // (hidden) Full Swing - Triple Attack
                        case 21_120_002: // Overswing
                        case 21_120_005: // Pole Arm finale
                        case 21_120_006: // Tempest
                        case 21_120_009: // (hidden) Overswing - Double Attack
                        case 21_120_010: { // (hidden) Overswing - Triple Attack
                            if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null && !monster.getStats().isBoss()) {
                                final MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.WK_CHARGE);
                                if (eff != null && eff.getSourceId() == 21_111_005) {
                                    monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.SPEED, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1_000, false);
                                }
                            }
                            if (player.getBuffedValue(MapleBuffStat.BODY_PRESSURE) != null && !monster.getStats().isBoss()) {
                                final MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.BODY_PRESSURE);

                                if (eff != null && eff.makeChanceResult() && !monster.isBuffed(MonsterStatus.NEUTRALISE)) {
                                    monster.applyStatus(player, new MonsterStatusEffect(MonsterStatus.NEUTRALISE, 1, eff.getSourceId(), null, false), false, eff.getX() * 1_000, false);
                                }
                            }
                            break;
                        }
                        default: //passives attack bonuses
                            break;
                    }
                    if (totDamageToOneMonster > 0) {
                        IItem weapon_ = player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
                        if (weapon_ != null) {
                            MonsterStatus stat = GameConstants.getStatFromWeapon(weapon_.getItemId()); //10001 = acc/darkness. 10005 = speed/slow.
                            if (stat != null && Randomizer.nextInt(100) < GameConstants.getStatChance()) {
                                final MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(stat, GameConstants.getXForStat(stat), GameConstants.getSkillForStat(stat), null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, 10_000, false, false);
                            }
                        }
                        if (player.getBuffedValue(MapleBuffStat.BLIND) != null) {
                            final MapleStatEffect eff = player.getStatForBuff(MapleBuffStat.BLIND);

                            if (eff.makeChanceResult()) {
                                final MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.ACC, eff.getX(), eff.getSourceId(), null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1_000, false);
                            }

                        }
                        if (player.getBuffedValue(MapleBuffStat.HAMSTRING) != null) {
                            final ISkill skill = SkillFactory.getSkill(3_121_007);
                            final MapleStatEffect eff = skill.getEffect(player.getSkillLevel(skill));

                            if (eff.makeChanceResult()) {
                                final MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.SPEED, eff.getX(), 3_121_007, null, false);
                                monster.applyStatus(player, monsterStatusEffect, false, eff.getY() * 1_000, false);
                            }
                        }
                        if (player.getJob() == 121) { // WHITEKNIGHT
                            for (int charge : charges) {
                                final ISkill skill = SkillFactory.getSkill(charge);
                                if (player.isBuffFrom(MapleBuffStat.WK_CHARGE, skill)) {
                                    final MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.FREEZE, 1, charge, null, false);
                                    monster.applyStatus(player, monsterStatusEffect, false, skill.getEffect(player.getSkillLevel(skill)).getY() * 2_000, false);
                                    break;
                                }
                            }
                        }
                    }
                    if (effect != null && effect.getMonsterStati().size() > 0) {
                        if (effect.makeChanceResult()) {
                            for (Map.Entry<MonsterStatus, Integer> z : effect.getMonsterStati().entrySet()) {
                                monster.applyStatus(player, new MonsterStatusEffect(z.getKey(), z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), false);
                            }
                        }
                    }
                }
            }
        }
        if (attack.skill == 4_331_003 && totDamageToOneMonster < hpMob) {
            return;
        }
        if (attack.skill != 0 && (attack.targets > 0 || (attack.skill != 4_331_003 && attack.skill != 4_341_002)) && attack.skill != 21_101_003 && attack.skill != 5_110_001 && attack.skill != 15_100_004 && attack.skill != 11_101_002 && attack.skill != 13_101_002) {
            effect.applyTo(player, attack.position);
        }
        if (totDamage > 1) {
            final CheatTracker tracker = player.getCheatTracker();

            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1_000) {
                tracker.registerOffense(CheatingOffense.人物无敌, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
    }

    /**
     *
     * @param attack
     * @param theSkill
     * @param player
     * @param effect
     */
    public static final void applyAttackMagic(final AttackInfo attack, final ISkill theSkill, final MapleCharacter player, final MapleStatEffect effect) {
        if (!player.isAlive()) {
            player.getCheatTracker().registerOffense(CheatingOffense.人物死亡攻击);
            return;
        }
        if (effect == null) {
            player.getClient().getSession().write(MaplePacketCreator.enableActions());
            return;
        }
        if (attack.real) {
            //  player.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }
        int last = effect.getAttackCount() > effect.getBulletCount() ? effect.getAttackCount() : effect.getBulletCount();
        boolean ban = false;
        String lastReason = "";
        String reason = "";

        // 检测外挂区---------------
        reason = Damage_MobCount(player, effect, attack);
        if (!"null".equals(reason)) {// 怪物数量
            lastReason = reason;
            ban = false;
        }
        reason = Damage_AttackCount(player, effect, attack, last);
        if (!"null".equals(reason)) {//伤害次数
            lastReason = reason;
            ban = false;
        }
        reason = Damage_HighDamage(player, effect, attack);
        if (!"null".equals(reason)) {// 伤害过高
            lastReason = reason;
            ban = false;
        }
        // 检测外挂区结束-------------
        // 封锁区
        //if (ban && !player.isAdmin()) {
        //player.ban(lastReason, false, true, false);//封号
        // FileoutputUtil.logToFile_chr(player, FileoutputUtil.ban_log, lastReason);// 输出文挡
        // World.Broadcast.broadcastMessage(MaplePacketCreator.serverNotice(6, "[封号系统]" + player.getName() + " 该玩家攻击异常被系统自动封号处理。").getBytes());//广播
        // return;
        // }
        // 封锁区结束
        if (GameConstants.isMulungSkill(attack.skill)) {
            if (player.getMapId() / 10_000 != 92_502) {
                //AutobanManager.getInstance().autoban(player.getClient(), "Using Mu Lung dojo skill out of dojo maps.");
                return;
            } else {
                player.mulung_EnergyModify(false);
            }
        }
        if (GameConstants.isPyramidSkill(attack.skill)) {
            if (player.getMapId() / 1_000_000 != 926) {
                //AutobanManager.getInstance().autoban(player.getClient(), "Using Pyramid skill outside of pyramid maps.");
                return;
            } else if (player.getPyramidSubway() == null || !player.getPyramidSubway().onSkillUse(player)) {
                return;
            }
        }
        final PlayerStats stats = player.getStat();
//	double minDamagePerHit;
        double maxDamagePerHit;
        if (attack.skill == 1_000 || attack.skill == 10_001_000 || attack.skill == 20_001_000 || attack.skill == 20_011_000 || attack.skill == 30_001_000) {
            maxDamagePerHit = 40;
        } else if (GameConstants.isPyramidSkill(attack.skill)) {
            maxDamagePerHit = 1;
        } else {
            final double v75 = (effect.getMatk() * 0.058);
//	    minDamagePerHit = stats.getTotalMagic() * (stats.getInt() * 0.5 + (v75 * v75) + (effect.getMastery() * 0.9 * effect.getMatk()) * 3.3) / 100;
            maxDamagePerHit = stats.getTotalMagic() * (stats.getInt() * 0.5 + (v75 * v75) + effect.getMatk() * 3.3) / 100;
        }
        maxDamagePerHit *= 1.04; // Avoid any errors for now

        final Element element = player.getBuffedValue(MapleBuffStat.ELEMENT_RESET) != null ? Element.NEUTRAL : theSkill.getElement();

        double MaxDamagePerHit = 0;
        int totDamageToOneMonster, totDamage = 0, fixeddmg;
        byte overallAttackCount;
        boolean Tempest;
        MapleMonsterStats monsterstats;
        int CriticalDamage = stats.passive_sharpeye_percent();
        final ISkill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(player.getJob()));
        final int eaterLevel = player.getSkillLevel(eaterSkill);

        final MapleMap map = player.getMap();

        for (final AttackPair oned : attack.allDamage) {
            final MapleMonster monster = map.getMonsterByOid(oned.objectid);

            if (monster != null) {
                //Damage_Position(player, monster, attack);
                Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == 21_120_006 && !monster.getStats().isBoss();
                totDamageToOneMonster = 0;
                monsterstats = monster.getStats();
                fixeddmg = monsterstats.getFixedDamage();
                MaxDamagePerHit = CalculateMaxMagicDamagePerHit(player, theSkill, monster, monsterstats, stats, element, CriticalDamage, maxDamagePerHit);
                overallAttackCount = 0;
                Integer eachd;
                for (Pair<Integer, Boolean> eachde : oned.attack) {
                    eachd = eachde.left;
                    overallAttackCount++;
                    if (fixeddmg != -1) {
                        eachd = monsterstats.getOnlyNoramlAttack() ? 0 : fixeddmg; // Magic is always not a normal attack
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = 0; // Magic is always not a normal attack
                    } else if (!player.isGM()) {
                        if (Tempest) { // Buffed with Tempest
                            // In special case such as Chain lightning, the damage will be reduced from the maxMP.
                            if (eachd > monster.getMobMaxHp()) {
                                eachd = (int) Math.min(monster.getMobMaxHp(), Integer.MAX_VALUE);
                                player.getCheatTracker().registerOffense(CheatingOffense.魔法伤害过高);
                            }
                        } else if (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY) && !monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) {
                            if (eachd > maxDamagePerHit) {
                                player.getCheatTracker().registerOffense(CheatingOffense.魔法伤害过高);
                                if (eachd > MaxDamagePerHit * 2) {
//				    System.out.println("EXCEED!!! Client damage : " + eachd + " Server : " + MaxDamagePerHit);
                                    eachd = (int) (MaxDamagePerHit * 2); // Convert to server calculated damage
                                    FileoutputUtil.logToFile_chr(player, FileoutputUtil.fixdam_ph, " 技能 " + attack.skill + " 怪物 " + monster.getId() + " 预计伤害:" + (long) MaxDamagePerHit + "  实际" + eachd);
                                    player.getCheatTracker().registerOffense(CheatingOffense.魔法伤害过高2);
                                }
                            }
                        } else if (eachd > maxDamagePerHit * 2) {
                            FileoutputUtil.logToFile_chr(player, FileoutputUtil.fixdam_ph, " 技能 " + attack.skill + " 怪物 " + monster.getId() + " 预计伤害:" + (long) MaxDamagePerHit + "  实际" + eachd);
                            eachd = (int) (maxDamagePerHit);
                        }
                    }
                    totDamageToOneMonster += eachd;
                }
                totDamage += totDamageToOneMonster;
                player.checkMonsterAggro(monster);

                double Position_range = player.getPosition().distanceSq(monster.getPosition());
                double Count_range = 700000.0;
                if (Position_range > Count_range) { // 815^2 <-- the most ranged attack in the game is Flame Wheel at 815 range
                    player.getCheatTracker().registerOffense(CheatingOffense.攻击范围过大, " 技能 " + attack.skill + " 范围 : " + (long) Position_range + "正常范围 " + (long) Count_range); // , Double.toString(Math.sqrt(distance))
                    return;
                }
                if (attack.skill == 2_301_002 && !monsterstats.getUndead()) {
                    player.getCheatTracker().registerOffense(CheatingOffense.治愈术攻击非不死系怪物);
                    return;
                }

                if (totDamageToOneMonster > 0) {
                    monster.damage(player, totDamageToOneMonster, true, attack.skill);
                    if (monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) { //test
                        player.addHP(-(7_000 + Randomizer.nextInt(8_000))); //this is what it seems to be?
                    }
                    // effects
                    switch (attack.skill) {
                        case 2_221_003:
                            monster.setTempEffectiveness(Element.FIRE, theSkill.getEffect(player.getSkillLevel(theSkill)).getDuration());
                            break;
                        case 2_121_003:
                            monster.setTempEffectiveness(Element.ICE, theSkill.getEffect(player.getSkillLevel(theSkill)).getDuration());
                            break;
                    }
                    if (effect != null && effect.getMonsterStati().size() > 0) {
                        if (effect.makeChanceResult()) {
                            for (Map.Entry<MonsterStatus, Integer> z : effect.getMonsterStati().entrySet()) {
                                monster.applyStatus(player, new MonsterStatusEffect(z.getKey(), z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), false);
                            }
                        }
                    }
                    if (eaterLevel > 0) {
                        eaterSkill.getEffect(eaterLevel).applyPassive(player, monster);
                    }
                }
            }
        }
        if (attack.skill != 2_301_002) {
            effect.applyTo(player);
        }

        if (totDamage > 1) {
            final CheatTracker tracker = player.getCheatTracker();
            tracker.setAttacksWithoutHit(true);

            if (tracker.getAttacksWithoutHit() > 1_000) {
                tracker.registerOffense(CheatingOffense.人物无敌, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
    }

    private static final double CalculateMaxMagicDamagePerHit(final MapleCharacter chr, final ISkill skill, final MapleMonster monster, final MapleMonsterStats mobstats, final PlayerStats stats, final Element elem, final Integer sharpEye, final double maxDamagePerMonster) {
        final int dLevel = Math.max(mobstats.getLevel() - chr.getLevel(), 0);
        final int Accuracy = (int) (Math.floor((stats.getTotalInt() / 10.0)) + Math.floor((stats.getTotalLuk() / 10.0)));
        final int MinAccuracy = mobstats.getEva() * (dLevel * 2 + 51) / 120;
        // FullAccuracy = Avoid * (dLevel * 2 + 51) / 50

        if (MinAccuracy > Accuracy && skill.getId() != 1_000 && skill.getId() != 10_001_000 && skill.getId() != 20_001_000 && skill.getId() != 20_011_000 && skill.getId() != 30_001_000 && !GameConstants.isPyramidSkill(skill.getId())) { // miss :P or HACK :O
            return 0;
        }
        double elemMaxDamagePerMob;

        switch (monster.getEffectiveness(elem)) {
            case IMMUNE:
                elemMaxDamagePerMob = 1;
                break;
            case NORMAL:
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster, stats);
                break;
            case WEAK:
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster * 1.5, stats);
                break;
            case STRONG:
                elemMaxDamagePerMob = ElementalStaffAttackBonus(elem, maxDamagePerMonster * 0.5, stats);
                break;
            default:
                throw new RuntimeException("Unknown enum constant");
        }
        // Calculate monster magic def
        // Min damage = (MIN before defense) - MDEF*.6
        // Max damage = (MAX before defense) - MDEF*.5
        elemMaxDamagePerMob -= mobstats.getMagicDefense() * 0.5;
        // Calculate Sharp eye bonus
        elemMaxDamagePerMob += ((double) elemMaxDamagePerMob / 100) * sharpEye;
//	if (skill.isChargeSkill()) {
//	    elemMaxDamagePerMob = (float) ((90 * ((System.currentTimeMillis() - chr.getKeyDownSkill_Time()) / 1000) + 10) * elemMaxDamagePerMob * 0.01);
//	}
//      if (skill.isChargeSkill() && chr.getKeyDownSkill_Time() == 0) {
//          return 1;
//      }
        elemMaxDamagePerMob += (elemMaxDamagePerMob * (mobstats.isBoss() ? stats.bossdam_r : stats.dam_r)) / 100;
        switch (skill.getId()) {
            case 1_000:
            case 10_001_000:
            case 20_001_000:
            case 20_011_000:
            case 30_001_000:
                elemMaxDamagePerMob = 40;
                break;
            case 1_020:
            case 10_001_020:
            case 20_001_020:
            case 20_011_020:
            case 30_001_020:
                elemMaxDamagePerMob = 1;
                break;
        }
        if (skill.getId() == 2_301_002) {
            elemMaxDamagePerMob *= 2;
        }
        if (elemMaxDamagePerMob > 199_999) {
            elemMaxDamagePerMob = 199_999;
        } else if (elemMaxDamagePerMob < 0) {
            elemMaxDamagePerMob = 1;
        }
        return elemMaxDamagePerMob;
    }

    private static final double ElementalStaffAttackBonus(final Element elem, double elemMaxDamagePerMob, final PlayerStats stats) {
        switch (elem) {
            case FIRE:
                return (elemMaxDamagePerMob / 100) * stats.element_fire;
            case ICE:
                return (elemMaxDamagePerMob / 100) * stats.element_ice;
            case LIGHTING:
                return (elemMaxDamagePerMob / 100) * stats.element_light;
            case POISON:
                return (elemMaxDamagePerMob / 100) * stats.element_psn;
            default:
                return (elemMaxDamagePerMob / 100) * stats.def;
        }
    }

    private static void handlePickPocket(final MapleCharacter player, final MapleMonster mob, AttackPair oned) {
        int maxmeso = player.getBuffedValue(MapleBuffStat.PICKPOCKET);
        final ISkill skill = SkillFactory.getSkill(4_211_003);
        final MapleStatEffect s = skill.getEffect(player.getSkillLevel(skill));
        for (Pair eachde : oned.attack) {
            Integer eachd = (Integer) eachde.left;
            if (s.makeChanceResult()) {
                player.getMap().spawnMesoDrop(Math.min((int) Math.max(eachd / 20000.0D * maxmeso, 1.0D), maxmeso), new Point((int) (mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50.0D), (int) mob.getTruePosition().getY()), mob, player, false, (byte) 0);
            }
        }
    }

    private static double CalculateMaxWeaponDamagePerHit(final MapleCharacter player, final MapleMonster monster, final AttackInfo attack, final ISkill theSkill, final MapleStatEffect attackEffect, double maximumDamageToMonster, final Integer CriticalDamagePercent) {
        if (player.getMapId() / 1_000_000 == 914) { //aran
            return 199_999;
        }
        List<Element> elements = new ArrayList<>();
        boolean defined = false;
        if (theSkill != null) {
            elements.add(theSkill.getElement());

            switch (theSkill.getId()) {
                case 3_001_004:
                case 33_101_001:
                    defined = true; //can go past 199999
                    break;
                case 1_000:
                case 10_001_000:
                case 20_001_000:
                case 20_011_000:
                case 30_001_000:
                    maximumDamageToMonster = 40;
                    defined = true;
                    break;
                case 1_020:
                case 10_001_020:
                case 20_001_020:
                case 20_011_020:
                case 30_001_020:
                    maximumDamageToMonster = 1;
                    defined = true;
                    break;
                case 4_331_003: //Owl Spirit
                    maximumDamageToMonster = (monster.getStats().isBoss() ? 199_999 : monster.getHp());
                    defined = true;
                    break;
                case 3_221_007: // Sniping
                    maximumDamageToMonster = (monster.getStats().isBoss() ? 199_999 : monster.getMobMaxHp());
                    defined = true;
                    break;
                case 1_221_011://Heavens Hammer
                    maximumDamageToMonster = (monster.getStats().isBoss() ? 199_999 : monster.getHp() - 1);
                    defined = true;
                    break;
                case 4_211_006: // Meso Explosion
                    maximumDamageToMonster = 750_000;
                    defined = true;
                    break;
                case 1_009: // Bamboo Trust
                case 10_001_009:
                case 20_001_009:
                case 20_011_009:
                case 30_001_009:
                    defined = true;
                    maximumDamageToMonster = (monster.getStats().isBoss() ? monster.getMobMaxHp() / 30 * 100 : monster.getMobMaxHp());
                    break;
                case 3_211_006: //Sniper Strafe
                    if (monster.getStatusSourceID(MonsterStatus.FREEZE) == 3_211_003) { //blizzard in effect
                        defined = true;
                        maximumDamageToMonster = monster.getHp();
                    }
                    break;
            }
        }
        if (player.getBuffedValue(MapleBuffStat.WK_CHARGE) != null) {
            int chargeSkillId = player.getBuffSource(MapleBuffStat.WK_CHARGE);

            switch (chargeSkillId) {
                case 1_211_003:
                case 1_211_004:
                    elements.add(Element.FIRE);
                    break;
                case 1_211_005:
                case 1_211_006:
                case 21_111_005:
                    elements.add(Element.ICE);
                    break;
                case 1_211_007:
                case 1_211_008:
                case 15_101_006:
                    elements.add(Element.LIGHTING);
                    break;
                case 1_221_003:
                case 1_221_004:
                case 11_111_007:
                    elements.add(Element.HOLY);
                    break;
                case 12_101_005:
                    elements.clear(); //neutral
                    break;
            }
        }
        if (player.getBuffedValue(MapleBuffStat.LIGHTNING_CHARGE) != null) {
            elements.add(Element.LIGHTING);
        }
        double elementalMaxDamagePerMonster = maximumDamageToMonster;
        if (elements.size() > 0) {
            double elementalEffect;

            switch (attack.skill) {
                case 3_211_003:
                case 3_111_003: // inferno and blizzard
                    elementalEffect = attackEffect.getX() / 200.0;
                    break;
                default:
                    elementalEffect = 0.5;
                    break;
            }
            for (Element element : elements) {
                switch (monster.getEffectiveness(element)) {
                    case IMMUNE:
                        elementalMaxDamagePerMonster = 1;
                        break;
                    case WEAK:
                        elementalMaxDamagePerMonster *= (1.0 + elementalEffect);
                        break;
                    case STRONG:
                        elementalMaxDamagePerMonster *= (1.0 - elementalEffect);
                        break;
                    default:
                        break; //normal nothing
                }
            }
        }
        // Calculate mob def
        final short moblevel = monster.getStats().getLevel();
        final short d = moblevel > player.getLevel() ? (short) (moblevel - player.getLevel()) : 0;
        elementalMaxDamagePerMonster = elementalMaxDamagePerMonster * (1 - 0.01 * d) - monster.getStats().getPhysicalDefense() * 0.5;

        // Calculate passive bonuses + Sharp Eye
        elementalMaxDamagePerMonster += ((double) elementalMaxDamagePerMonster / 100.0) * CriticalDamagePercent;

//	if (theSkill.isChargeSkill()) {
//	    elementalMaxDamagePerMonster = (double) (90 * (System.currentTimeMillis() - player.getKeyDownSkill_Time()) / 2000 + 10) * elementalMaxDamagePerMonster * 0.01;
//	}
        if (theSkill != null && theSkill.isChargeSkill() && player.getKeyDownSkill_Time() == 0) {
            return 0;
        }
        final MapleStatEffect homing = player.getStatForBuff(MapleBuffStat.HOMING_BEACON);
        if (homing != null && player.getLinkMid() == monster.getObjectId() && homing.getSourceId() == 5_220_011) { //bullseye
            elementalMaxDamagePerMonster += (elementalMaxDamagePerMonster * homing.getX());
        }
        final PlayerStats stat = player.getStat();
        elementalMaxDamagePerMonster += (elementalMaxDamagePerMonster * (monster.getStats().isBoss() ? stat.bossdam_r : stat.dam_r)) / 100.0;
        if (player.getDebugMessage()) {
            player.dropMessage("[伤害计算] 属性伤害:" + (int) elementalMaxDamagePerMonster);
        }
        if (elementalMaxDamagePerMonster > 199_999) {
            if (!defined) {
                elementalMaxDamagePerMonster = 199_999;
            }
        } else if (elementalMaxDamagePerMonster < 0) {
            elementalMaxDamagePerMonster = 1;
        }
        return elementalMaxDamagePerMonster;
    }

    /**
     *
     * @param attack
     * @param rate
     * @return
     */
    public static final AttackInfo DivideAttack(final AttackInfo attack, final int rate) {
        attack.real = false;
        if (rate <= 1) {
            return attack; //lol
        }
        for (AttackPair p : attack.allDamage) {
            if (p.attack != null) {
                for (Pair<Integer, Boolean> eachd : p.attack) {
                    eachd.left /= rate; //too ex.
                }
            }
        }
        return attack;
    }

    /**
     *
     * @param attack
     * @param chr
     * @param type
     * @return
     */
    public static final AttackInfo Modify_AttackCrit(final AttackInfo attack, final MapleCharacter chr, final int type) {
        final int CriticalRate = chr.getStat().passive_sharpeye_rate();
        final boolean shadow = (type == 2 && chr.getBuffedValue(MapleBuffStat.SHADOWPARTNER) != null) || (type == 1 && chr.getBuffedValue(MapleBuffStat.MIRROR_IMAGE) != null);
        if (attack.skill != 4_211_006 && attack.skill != 3_211_003 && attack.skill != 4_111_004 && (CriticalRate > 0 || attack.skill == 4_221_001 || attack.skill == 3_221_007)) { //blizz + shadow meso + m.e no crits
            for (AttackPair p : attack.allDamage) {
                if (p.attack != null) {
                    int hit = 0;
                    final int mid_att = p.attack.size() / 2;
                    final List<Pair<Integer, Boolean>> eachd_copy = new ArrayList<>(p.attack);
                    for (Pair<Integer, Boolean> eachd : p.attack) {
                        hit++;
                        if (!eachd.right) {
                            if (attack.skill == 4_221_001) { //assassinate never crit first 3, always crit last
                                eachd.right = (hit == 4 && Randomizer.nextInt(100) < 90);
                            } else if (attack.skill == 3_221_007 || eachd.left > 199_999) { //snipe always crit
                                eachd.right = true;
                            } else if (shadow && hit > mid_att) { //shadowpartner copies second half to first half
                                eachd.right = eachd_copy.get(hit - 1 - mid_att).right;
                            } else {
                                //rough calculation
                                eachd.right = (Randomizer.nextInt(100)/*
                                         * chr.CRand().CRand32__Random_ForMonster()
                                         * % 100
                                         */) < CriticalRate;
                            }
                            eachd_copy.get(hit - 1).right = eachd.right;
                            //System.out.println("CRITICAL RATE: " + CriticalRate + ", passive rate: " + chr.getStat().passive_sharpeye_rate() + ", critical: " + eachd.right);
                        }
                    }
                }
            }
        }
        return attack;
    }

    /**
     *
     * @param lea
     * @param chr
     * @return
     */
    public static final AttackInfo parseDmgMa(final LittleEndianAccessor lea, final MapleCharacter chr) {
        //System.out.println(lea.toString());
        final AttackInfo ret = new AttackInfo();

        lea.skip(1);
        lea.skip(8);
        ret.tbyte = lea.readByte();
        //System.out.println("TBYTE: " + tbyte);
        ret.targets = (byte) ((ret.tbyte >>> 4) & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        lea.skip(8); //?
        ret.skill = lea.readInt();
        lea.skip(12); // ORDER [4] bytes on v.79, [4] bytes on v.80, [1] byte on v.82
        switch (ret.skill) {
            case 2_121_001: // Big Bang
            case 2_221_001:
            case 2_321_001:
            case 22_121_000: //breath
            case 22_151_001:
                ret.charge = lea.readInt();
                break;
            default:
                ret.charge = -1;
                break;
        }
        lea.skip(1);
        ret.unk = 0;
        ret.display = lea.readByte(); // Always zero?
        ret.animation = lea.readByte();
        lea.skip(1); // Weapon subclass
        ret.speed = lea.readByte(); // Confirmed
        ret.lastAttackTickCount = lea.readInt(); // Ticks
//        lea.skip(4); //0

        int oid, damage;
        List<Pair<Integer, Boolean>> allDamageNumbers;
        ret.allDamage = new ArrayList<>();

        for (int i = 0; i < ret.targets; i++) {
            oid = lea.readInt();
            lea.skip(14); // [1] Always 6?, [3] unk, [4] Pos1, [4] Pos2, [2] seems to change randomly for some attack

            allDamageNumbers = new ArrayList<>();

            MapleMonster monster = chr.getMap().getMonsterByOid(oid);
            for (int j = 0; j < ret.hits; j++) {
                damage = lea.readInt();
//                if (ret.skill > 0) {
//                    damage = Damage_SkillPD(chr, damage, ret);
//                } else {
//                    damage = Damage_NoSkillPD(chr, damage);
//                }

                int maxdamage;
                maxdamage = (int) (199_999 + chr.getVip() * 10_000);
                double randomNum = Math.random() * 1.1D;
                randomNum = Math.max(randomNum, 0.9D);
                int tempDamage = 0;

                for (IItem item : chr.getInventory(MapleInventoryType.EQUIPPED)) {
                    int ak = 0;
                    if ((item != null) && ((item instanceof Equip))) {
                        ak = MapleItemInformationProvider.getInstance().getTotalStat((Equip) item);
                    }

                    tempDamage += ak * 15;
                }
                if ((ret.skill != 14_101_006) && (damage >= 199_999)) {
                    if (((chr.getJob() >= 100) && (chr.getJob() <= 132)) || ((chr.getJob() >= 1_100) && (chr.getJob() <= 1_111)) || ((chr.getJob() >= 2_000) && (chr.getJob() <= 2_112)) || ((chr.getJob() >= 3_100) && (chr.getJob() <= 3_112)) || ((chr.getJob() >= 5_000) && (chr.getJob() <= 5_112))) {
                        tempDamage += (int) (chr.getStat().getStr() * 5.0D + (chr.getStat().getDex() + chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 200) && (chr.getJob() <= 232)) || ((chr.getJob() >= 1_200) && (chr.getJob() <= 1_211)) || ((chr.getJob() >= 2_001) && (chr.getJob() <= 2_218)) || ((chr.getJob() >= 3_200) && (chr.getJob() <= 3_212))) {
                        tempDamage += (int) (chr.getStat().getInt() * 5.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 300) && (chr.getJob() <= 322)) || ((chr.getJob() >= 1_300) && (chr.getJob() <= 1_311)) || ((chr.getJob() >= 3_300) && (chr.getJob() <= 3_312)) || ((chr.getJob() >= 2_300) && (chr.getJob() <= 2_312)) || ((chr.getJob() >= 3_500) && (chr.getJob() <= 3_512))) {
                        tempDamage += (int) (chr.getStat().getDex() * 5.0D + (chr.getStat().getStr() + chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 400) && (chr.getJob() <= 422)) || ((chr.getJob() >= 1_400) && (chr.getJob() <= 1_412)) || ((chr.getJob() >= 430) && (chr.getJob() <= 434)) || (chr.getJob() == 2_003) || ((chr.getJob() >= 2_400) && (chr.getJob() <= 2_412))) {
                        tempDamage += (int) (chr.getStat().getLuk() * 5.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getInt()));
                    }

                    if (((chr.getJob() >= 580) && (chr.getJob() <= 592)) || ((chr.getJob() >= 1_500) && (chr.getJob() <= 1_511)) || (chr.getJob() == 508) || ((chr.getJob() >= 570) && (chr.getJob() <= 572))) {
                        tempDamage += (int) ((chr.getStat().getStr() + chr.getStat().getDex()) / 2.0D * 2.0D + (chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if ((chr.getJob() == 501) || ((chr.getJob() >= 530) && (chr.getJob() <= 532))) {
                        tempDamage += (int) (chr.getStat().getStr() * 5.0D + (chr.getStat().getLuk() + chr.getStat().getDex() + chr.getStat().getInt()));
                    }

                    damage = (int) ((tempDamage + 199_999) * randomNum);

                    damage = Math.min(damage, 199_999 + (chr.getVip() * 10_000));

                    chr.dropMessage(-1, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
                    chr.getClient().getSession().write(MaplePacketCreator.sendHint("#e[破攻伤害]:#r" + damage + "#b ", 250, 5));
                }
                if (damage > maxdamage) {
                    damage = maxdamage;
                }

                if ((damage >= 2_000_000) || (damage < 0)) {
                    damage = 2_000_000;
                }

                tempDamage = 0;
                allDamageNumbers.add(new Pair<>(Integer.valueOf(damage), false));
            }
            lea.skip(4); // CRC of monster [Wz Editing]
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        ret.position = lea.readPos();

        return ret;
    }

    /**
     *
     * @param lea
     * @param chr
     * @return
     */
    public static final AttackInfo parseDmgM(final LittleEndianAccessor lea, final MapleCharacter chr) {
        //System.out.println(lea.toString());
        final AttackInfo ret = new AttackInfo();

        lea.skip(1);
        lea.skip(8);
        ret.tbyte = lea.readByte();
        //System.out.println("TBYTE: " + tbyte);
        ret.targets = (byte) ((ret.tbyte >>> 4) & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();
        lea.skip(12); // ORDER [4] bytes on v.79, [4] bytes on v.80, [1] byte on v.82
        switch (ret.skill) {
            case 5_101_004: // Corkscrew
            case 15_101_003: // Cygnus corkscrew
            case 5_201_002: // Gernard
            case 14_111_006: // Poison bomb
            case 4_341_002:
            case 4_341_003:
                ret.charge = lea.readInt();
                break;
            default:
                ret.charge = 0;
                break;
        }
        lea.skip(1);
        ret.unk = 0;
        ret.display = lea.readByte(); // Always zero?
        ret.animation = lea.readByte();
        lea.skip(1); // Weapon class
        ret.speed = lea.readByte(); // Confirmed
        ret.lastAttackTickCount = lea.readInt(); // Ticks
//        lea.skip(4); //0

        ret.allDamage = new ArrayList<>();

        if (ret.skill == 4_211_006) { // Meso Explosion
            return parseMesoExplosion(lea, ret, chr);
        }
        int oid, damage;
        List<Pair<Integer, Boolean>> allDamageNumbers;

        for (int i = 0; i < ret.targets; i++) {
            oid = lea.readInt();
//	    System.out.println(tools.HexTool.toString(lea.read(14)));
            lea.skip(14); // [1] Always 6?, [3] unk, [4] Pos1, [4] Pos2, [2] seems to change randomly for some attack

            allDamageNumbers = new ArrayList<>();

            MapleMonster monster = chr.getMap().getMonsterByOid(oid);
            for (int j = 0; j < ret.hits; j++) {
                damage = lea.readInt();
//                if (ret.skill > 0) {
//                    damage = Damage_SkillPD(chr, damage, ret);
//                } else {
//                    damage = Damage_NoSkillPD(chr, damage);
//                }
                // System.out.println("Damage: " + damage);
                int maxdamage;
                maxdamage = (int) (199_999 + chr.getVip() * 10_000);
                double randomNum = Math.random() * 1.1D;
                randomNum = Math.max(randomNum, 0.9D);
                int tempDamage = 0;

                for (IItem item : chr.getInventory(MapleInventoryType.EQUIPPED)) {
                    int ak = 0;
                    if ((item != null) && ((item instanceof Equip))) {
                        ak = MapleItemInformationProvider.getInstance().getTotalStat((Equip) item);
                    }

                    tempDamage += ak * 15;
                }
                if ((ret.skill != 14_101_006) && (damage >= 199_999)) {
                    if (((chr.getJob() >= 100) && (chr.getJob() <= 132)) || ((chr.getJob() >= 1_100) && (chr.getJob() <= 1_111)) || ((chr.getJob() >= 2_000) && (chr.getJob() <= 2_112)) || ((chr.getJob() >= 3_100) && (chr.getJob() <= 3_112)) || ((chr.getJob() >= 5_000) && (chr.getJob() <= 5_112))) {
                        tempDamage += (int) (chr.getStat().getStr() * 2.0D + (chr.getStat().getDex() + chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 200) && (chr.getJob() <= 232)) || ((chr.getJob() >= 1_200) && (chr.getJob() <= 1_211)) || ((chr.getJob() >= 2_001) && (chr.getJob() <= 2_218)) || ((chr.getJob() >= 3_200) && (chr.getJob() <= 3_212))) {
                        tempDamage += (int) (chr.getStat().getInt() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 300) && (chr.getJob() <= 322)) || ((chr.getJob() >= 1_300) && (chr.getJob() <= 1_311)) || ((chr.getJob() >= 3_300) && (chr.getJob() <= 3_312)) || ((chr.getJob() >= 2_300) && (chr.getJob() <= 2_312)) || ((chr.getJob() >= 3_500) && (chr.getJob() <= 3_512))) {
                        tempDamage += (int) (chr.getStat().getDex() * 2.0D + (chr.getStat().getStr() + chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 400) && (chr.getJob() <= 422)) || ((chr.getJob() >= 1_400) && (chr.getJob() <= 1_412)) || ((chr.getJob() >= 430) && (chr.getJob() <= 434)) || (chr.getJob() == 2_003) || ((chr.getJob() >= 2_400) && (chr.getJob() <= 2_412))) {
                        tempDamage += (int) (chr.getStat().getLuk() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getInt()));
                    }

                    if (((chr.getJob() >= 580) && (chr.getJob() <= 592)) || ((chr.getJob() >= 1_500) && (chr.getJob() <= 1_511)) || (chr.getJob() == 508) || ((chr.getJob() >= 570) && (chr.getJob() <= 572))) {
                        tempDamage += (int) ((chr.getStat().getStr() + chr.getStat().getDex()) / 2.0D * 2.0D + (chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if ((chr.getJob() == 501) || ((chr.getJob() >= 530) && (chr.getJob() <= 532))) {
                        tempDamage += (int) (chr.getStat().getStr() * 2.0D + (chr.getStat().getLuk() + chr.getStat().getDex() + chr.getStat().getInt()));
                    }

                    damage = (int) ((tempDamage + 199_999) * randomNum);

                    damage = Math.min(damage, 199_999 + (chr.getVip() * 10_000));
                    // chr.getClient().getSession().write(MaplePacketCreator.sendHint(new StringBuilder().append("破攻实际伤害:#r").append(damage).toString(), 148, 5));
                    chr.dropMessage(-1, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
                    chr.getClient().getSession().write(MaplePacketCreator.sendHint("#e[破攻伤害]:#r" + damage + "#b ", 250, 5));
                }
                if (damage > maxdamage) {
                    damage = maxdamage;
                }

                if ((damage >= 2_000_000) || (damage < 0)) {
                    damage = 2_000_000;
                }

                tempDamage = 0;
                allDamageNumbers.add(new Pair<>(Integer.valueOf(damage), false));
            }
            lea.skip(4); // CRC of monster [Wz Editing]
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        ret.position = lea.readPos();
        return ret;
    }

    /**
     *
     * @param lea
     * @param chr
     * @return
     */
    public static final AttackInfo parseDmgR(final LittleEndianAccessor lea, final MapleCharacter chr) {
        //System.out.println(lea.toString()); //<-- packet needs revision
        final AttackInfo ret = new AttackInfo();

        lea.skip(1);
        lea.skip(8);
        ret.tbyte = lea.readByte();
        //System.out.println("TBYTE: " + tbyte);
        ret.targets = (byte) ((ret.tbyte >>> 4) & 0xF);
        ret.hits = (byte) (ret.tbyte & 0xF);
        lea.skip(8);
        ret.skill = lea.readInt();

        lea.skip(12); // ORDER [4] bytes on v.79, [4] bytes on v.80, [1] byte on v.82

        switch (ret.skill) {
            case 3_121_004: // Hurricane
            case 3_221_001: // Pierce
            case 5_221_004: // Rapidfire
            case 13_111_002: // Cygnus Hurricane
                lea.skip(4); // extra 4 bytes
                break;
        }
        ret.charge = -1;
        lea.skip(1);
        ret.unk = 0;
        ret.display = lea.readByte(); // Always zero?
        ret.animation = lea.readByte();
        lea.skip(1); // Weapon class
        ret.speed = lea.readByte(); // Confirmed
        ret.lastAttackTickCount = lea.readInt(); // Ticks
//        lea.skip(4); //0
        ret.slot = (byte) lea.readShort();
        ret.csstar = (byte) lea.readShort();
        ret.AOE = lea.readByte(); // is AOE or not, TT/ Avenger = 41, Showdown = 0

        int damage, oid;
        List<Pair<Integer, Boolean>> allDamageNumbers;
        ret.allDamage = new ArrayList<>();

        for (int i = 0; i < ret.targets; i++) {
            oid = lea.readInt();
//	    System.out.println(tools.HexTool.toString(lea.read(14)));
            lea.skip(14); // [1] Always 6?, [3] unk, [4] Pos1, [4] Pos2, [2] seems to change randomly for some attack

            MapleMonster monster = chr.getMap().getMonsterByOid(oid);
            allDamageNumbers = new ArrayList<>();
            for (int j = 0; j < ret.hits; j++) {
                damage = lea.readInt();
                int maxdamage;
                maxdamage = (int) (199_999 + chr.getVip() * 10_000);
                double randomNum = Math.random() * 1.1D;
                randomNum = Math.max(randomNum, 0.9D);
                int tempDamage = 0;

                for (IItem item : chr.getInventory(MapleInventoryType.EQUIPPED)) {
                    int ak = 0;
                    if ((item != null) && ((item instanceof Equip))) {
                        ak = MapleItemInformationProvider.getInstance().getTotalStat((Equip) item);
                    }

                    tempDamage += ak * 15;
                }
                if ((ret.skill != 14_101_006) && (damage >= 199_999)) {
                    if (((chr.getJob() >= 100) && (chr.getJob() <= 132)) || ((chr.getJob() >= 1_100) && (chr.getJob() <= 1_111)) || ((chr.getJob() >= 2_000) && (chr.getJob() <= 2_112)) || ((chr.getJob() >= 3_100) && (chr.getJob() <= 3_112)) || ((chr.getJob() >= 5_000) && (chr.getJob() <= 5_112))) {
                        tempDamage += (int) (chr.getStat().getStr() * 2.0D + (chr.getStat().getDex() + chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 200) && (chr.getJob() <= 232)) || ((chr.getJob() >= 1_200) && (chr.getJob() <= 1_211)) || ((chr.getJob() >= 2_001) && (chr.getJob() <= 2_218)) || ((chr.getJob() >= 3_200) && (chr.getJob() <= 3_212))) {
                        tempDamage += (int) (chr.getStat().getInt() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 300) && (chr.getJob() <= 322)) || ((chr.getJob() >= 1_300) && (chr.getJob() <= 1_311)) || ((chr.getJob() >= 3_300) && (chr.getJob() <= 3_312)) || ((chr.getJob() >= 2_300) && (chr.getJob() <= 2_312)) || ((chr.getJob() >= 3_500) && (chr.getJob() <= 3_512))) {
                        tempDamage += (int) (chr.getStat().getDex() * 2.0D + (chr.getStat().getStr() + chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if (((chr.getJob() >= 400) && (chr.getJob() <= 422)) || ((chr.getJob() >= 1_400) && (chr.getJob() <= 1_412)) || ((chr.getJob() >= 430) && (chr.getJob() <= 434)) || (chr.getJob() == 2_003) || ((chr.getJob() >= 2_400) && (chr.getJob() <= 2_412))) {
                        tempDamage += (int) (chr.getStat().getLuk() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getInt()));
                    }

                    if (((chr.getJob() >= 580) && (chr.getJob() <= 592)) || ((chr.getJob() >= 1_500) && (chr.getJob() <= 1_511)) || (chr.getJob() == 508) || ((chr.getJob() >= 570) && (chr.getJob() <= 572))) {
                        tempDamage += (int) ((chr.getStat().getStr() + chr.getStat().getDex()) / 2.0D * 2.0D + (chr.getStat().getInt() + chr.getStat().getLuk()));
                    }

                    if ((chr.getJob() == 501) || ((chr.getJob() >= 530) && (chr.getJob() <= 532))) {
                        tempDamage += (int) (chr.getStat().getStr() * 2.0D + (chr.getStat().getLuk() + chr.getStat().getDex() + chr.getStat().getInt()));
                    }

                    damage = (int) ((tempDamage + 199_999) * randomNum);

                    damage = Math.min(damage, 199_999 + (chr.getVip() * 10_000));
                    // chr.getClient().getSession().write(MaplePacketCreator.sendHint(new StringBuilder().append("破攻实际伤害:#r").append(damage).toString(), 148, 5));
                    chr.dropMessage(-1, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
                    chr.getClient().getSession().write(MaplePacketCreator.sendHint("#e[破攻伤害]:#r" + damage + "#b ", 250, 5));
                }
                if (damage > maxdamage) {
                    damage = maxdamage;
                }

                if ((damage >= 2_000_000) || (damage < 0)) {
                    damage = 2_000_000;
                }

                tempDamage = 0;
//                if (ret.skill > 0) {
//                    damage = Damage_SkillPD(chr, damage, ret);
//                } else {
//                    damage = Damage_NoSkillPD(chr, damage);
//                }
                allDamageNumbers.add(new Pair<>(Integer.valueOf(damage), false));
                //System.out.println("Hit " + j + " from " + i + " to mobid " + oid + ", damage " + damage);
            }
            lea.skip(4); // CRC of monster [Wz Editing]
//	    System.out.println(tools.HexTool.toString(lea.read(4)));

            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
        }
        lea.skip(4);
        ret.position = lea.readPos();

        return ret;
    }

    /**
     *
     * @param lea
     * @param ret
     * @param chr
     * @return
     */
    public static final AttackInfo parseMesoExplosion(final LittleEndianAccessor lea, final AttackInfo ret, final MapleCharacter chr) {
        //System.out.println(lea.toString(true));
        byte bullets;
        if (ret.hits == 0) {
            lea.skip(4);
            bullets = lea.readByte();
            for (int j = 0; j < bullets; j++) {
                ret.allDamage.add(new AttackPair(lea.readInt(), null));
                lea.skip(1);
            }
            lea.skip(2); // 8F 02
            return ret;
        }

        int oid;
        List<Pair<Integer, Boolean>> allDamageNumbers;

        for (int i = 0; i < ret.targets; i++) {
            oid = lea.readInt();
            lea.skip(12);
            bullets = lea.readByte();
            allDamageNumbers = new ArrayList<>();
            for (int j = 0; j < bullets; j++) {
                int damage = lea.readInt();
                //    damage = Damage_SkillPD(chr, damage, ret);
                allDamageNumbers.add(new Pair<>(Integer.valueOf(damage), false)); //m.e. never crits
            }
            ret.allDamage.add(new AttackPair(oid, allDamageNumbers));
            lea.skip(4); // C3 8F 41 94, 51 04 5B 01
        }
        lea.skip(4);
        bullets = lea.readByte();

        for (int j = 0; j < bullets; j++) {
            ret.allDamage.add(new AttackPair(lea.readInt(), null));
            lea.skip(1);
        }
        lea.skip(2); // 8F 02/ 63 02

        return ret;
    }

    /**
     *
     * @param c
     * @param monster
     * @param ret
     */
    public static void Damage_Position(MapleCharacter c, MapleMonster monster, AttackInfo ret) {
        try {
            if (!GameConstants.不检测技能(ret.skill)) {
                if (c.getJob() >= 1_300 && c.getJob() <= 1_311
                        || c.getJob() >= 1_400 && c.getJob() <= 1_411
                        || c.getJob() >= 400 && c.getJob() <= 422
                        || c.getJob() >= 300 && c.getJob() <= 322
                        || c.getJob() == 500
                        || c.getJob() >= 520 && c.getJob() <= 522) {
                    // if (!GameConstants.不检测全屏技能(ret.skill)) {

                    if (c.getPosition().y - monster.getPosition().y >= 800) {
                        //    c.dropMessage(1, "[技能范围检测-A]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        //damage = 1;
                        String 全屏 = "等级A：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    } else if (c.getPosition().y - monster.getPosition().y <= -800) {
                        //    c.dropMessage(1, "[技能范围检测-A]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        // damage = 1;
                        String 全屏 = "等级B：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    } else if (c.getPosition().x - monster.getPosition().x >= 800) {
                        //    c.dropMessage(1, "[技能范围检测-A]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        // damage = 1;
                        String 全屏 = "等级C：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    } else if (c.getPosition().x - monster.getPosition().x <= -900) {
                        //    c.dropMessage(1, "[技能范围检测-A]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        // damage = 1;
                        String 全屏 = "等级D：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    }
                } else if (c.getJob() >= 200 && c.getJob() < 300) {
                    if (c.getPosition().y - monster.getPosition().y >= 800) {
                        // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        //  damage = 1;
                        String 全屏 = "等级E：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    } else if (c.getPosition().y - monster.getPosition().y <= -800) {
                        // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        //  damage = 1;
                        String 全屏 = "等级F：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    } else if (c.getPosition().x - monster.getPosition().x >= 550) {
                        // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        // damage = 1;
                        String 全屏 = "等级G：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    } else if (c.getPosition().x - monster.getPosition().x <= -550) {
                        // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                        //  damage = 1;
                        String 全屏 = "等级H：" + c.getLevel()
                                + "\r\n" + "职业：" + c.getJob()
                                + "\r\n" + "地图：" + c.getMapId()
                                + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                                + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                                + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                                + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                        FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                    }
                } else if (c.getPosition().y - monster.getPosition().y >= 350) {
                    // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                    // damage = 1;
                    String 全屏 = "等级I：" + c.getLevel()
                            + "\r\n" + "职业：" + c.getJob()
                            + "\r\n" + "地图：" + c.getMapId()
                            + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                            + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                            + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                            + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                } else if (c.getPosition().y - monster.getPosition().y <= -350) {
                    // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                    //  damage = 1;
                    String 全屏 = "等级J：" + c.getLevel()
                            + "\r\n" + "职业：" + c.getJob()
                            + "\r\n" + "地图：" + c.getMapId()
                            + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                            + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                            + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                            + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                } else if (c.getPosition().x - monster.getPosition().x >= 500) {
                    // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                    //  damage = 1;
                    String 全屏 = "等级K：" + c.getLevel()
                            + "\r\n" + "职业：" + c.getJob()
                            + "\r\n" + "地图：" + c.getMapId()
                            + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                            + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                            + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                            + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                } else if (c.getPosition().x - monster.getPosition().x <= -500) {
                    // c.dropMessage(1, "[技能范围检测-B]\r\n非法使用外挂或者修改WZ\r\n导致:技能范围扩大.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
                    //  damage = 1;
                    String 全屏 = "等级L：" + c.getLevel()
                            + "\r\n" + "职业：" + c.getJob()
                            + "\r\n" + "地图：" + c.getMapId()
                            + "\r\n" + "人物坐标：X:" + c.getPosition().x + " Y:" + c.getPosition().y
                            + "\r\n" + "怪物坐标：" + monster.getPosition().x + " Y:" + monster.getPosition().y
                            + "\r\n" + "时间：" + FileoutputUtil.CurrentReadable_Time()
                            + "\r\n" + "IP：" + c.getClient().getSession().getRemoteAddress().toString().split(":")[0];
                    FileoutputUtil.packetLog("log\\全屏检测\\" + c.getName() + ".log", 全屏);

                }
            }

        } catch (Exception e) {
        }
    }

    /*
     * public static final int Damage_NoSkillPD(MapleCharacter c, int damage) {
     * if (c.getJob() == 1000 || c.getJob() == 0 || c.getJob() == 2000) { if
     * (damage >= 150) { damage = 1; return damage; } } else if (c.getJob() ==
     * 2100 || c.getJob() == 2110 || c.getJob() == 2111 || c.getJob() == 2112)
     * {//战神技能 if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 6.8)) {
     * damage = 1; } return damage; } else if (c.getJob() == 100 || c.getJob()
     * == 110 || c.getJob() == 111 || c.getJob() == 112 || c.getJob() == 120 ||
     * c.getJob() == 121 || c.getJob() == 122 || c.getJob() == 130 || c.getJob()
     * == 131 || c.getJob() == 132) { //战士技能 if
     * (c.getStat().getCurrentMaxBaseDamage() <= damage / 6) { damage = 1; }
     * return damage; } else if (c.getJob() == 200 || c.getJob() == 210 ||
     * c.getJob() == 211 || c.getJob() == 212 || c.getJob() == 220 || c.getJob()
     * == 221 || c.getJob() == 222 || c.getJob() == 230 || c.getJob() == 231 ||
     * c.getJob() == 232) {//魔法师技能 if (c.getStat().getCurrentMaxBaseDamage() <=
     * damage / 6) { damage = 1; } return damage; } else if (c.getJob() == 300
     * || c.getJob() == 310 || c.getJob() == 311 || c.getJob() == 312 ||
     * c.getJob() == 320 || c.getJob() == 321 || c.getJob() == 322) {//弓箭手技能 if
     * (c.getStat().getCurrentMaxBaseDamage() <= damage / 7) { damage = 1;
     * return damage; } } else if (c.getJob() == 400 || c.getJob() == 410 ||
     * c.getJob() == 411 || c.getJob() == 412 || c.getJob() == 420 || c.getJob()
     * == 421 || c.getJob() == 422) {//飞侠技能 if
     * (c.getStat().getCurrentMaxBaseDamage() <= damage / 7) { damage = 1;
     * return damage; } } else if (c.getJob() == 500 || c.getJob() == 510 ||
     * c.getJob() == 511 || c.getJob() == 512 || c.getJob() == 520 || c.getJob()
     * == 521 || c.getJob() == 522) {//海盗技能 if
     * (c.getStat().getCurrentMaxBaseDamage() <= damage / 7) { damage = 1;
     * return damage; } } else if (c.getJob() == 1000 || c.getJob() == 1100 ||
     * c.getJob() == 1110 || c.getJob() == 1111) {//魂骑士技能 if
     * (c.getStat().getCurrentMaxBaseDamage() <= damage / 7) { damage = 1;
     * return damage; } } else if (c.getJob() == 1200 || c.getJob() == 1210 ||
     * c.getJob() == 1211) {//炎术士技能 if (c.getStat().getCurrentMaxBaseDamage() <=
     * damage / 7) { damage = 1; return damage; } } else if (c.getJob() == 1300
     * || c.getJob() == 1310 || c.getJob() == 1311) {//风灵使者技能 if
     * (c.getStat().getCurrentMaxBaseDamage() <= damage / 7) { damage = 1;
     * return damage; } } else if (c.getJob() == 1400 || c.getJob() == 1410 ||
     * c.getJob() == 1411) {//夜行者技能 if (c.getStat().getCurrentMaxBaseDamage() <=
     * damage / 7) { damage = 1; return damage; } } else if (c.getJob() == 1500
     * || c.getJob() == 1510 || c.getJob() == 1511) {//奇袭者技能 if
     * (c.getStat().getCurrentMaxBaseDamage() <= damage / 7) { damage = 1;
     * return damage; }
     *
     * }
     * return damage; }
     *
     * public static final int Damage_SkillPD(MapleCharacter c, int damage,
     * final AttackInfo ret) { if (c.getJob() == 2100 || c.getJob() == 2110 ||
     * c.getJob() == 2111 || c.getJob() == 2112) {//战神技能 if
     * (GameConstants.Ares_Skill_350(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 13)) {
     *
     * //
     * c.dropMessage(1,"[战神技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage;
     * }//////System.out.println("战神伤害处理A||输出伤害:"+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage/3.6+" ||实际:"+damage+""); } else if
     * (GameConstants.Ares_Skill_140(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 20)) { //
     * c.dropMessage(1,"[战神技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage;
     * }//////System.out.println("战神伤害处理B||输出伤害:"+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage/3+" ||实际:"+damage+""); } else if
     * (GameConstants.Ares_Skill_1500(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 21)) {
     * //////System.out.println("角色名："+c.getPartner().getName()+"。输出伤害:"+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage/17+"||实际伤害为:"+damage+""); damage = 1; return damage; //
     * c.dropMessage(1,"[战神技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * } } else if (GameConstants.Ares_Skill_800(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 14)) { damage = 1;
     * return damage; //
     * c.dropMessage(1,"[战神技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * }//////System.out.println("战神伤害处理D||输出伤害:"+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage/10+" ||实际:"+damage+""); //
     * c.dropMessage(1,"[战神技能攻击力检测+E]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * }//////System.out.println("战神伤害处理E||输出伤害:"+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage/2+" ||实际:"+damage+""); } else if (c.getJob() == 100 ||
     * c.getJob() == 110 || c.getJob() == 111 || c.getJob() == 112 || c.getJob()
     * == 120 || c.getJob() == 121 || c.getJob() == 122 || c.getJob() == 130 ||
     * c.getJob() == 131 || c.getJob() == 132) { //战士技能 if
     * (GameConstants.Warrior_Skill_450(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 11)) { //
     * c.dropMessage(1,
     * "[战士技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测A] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/7+"\r\n"); damage = 1; return damage; //return
     * ret; } } else if (GameConstants.Warrior_Skill_550(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 18)) { //
     * c.dropMessage(1,
     * "[战士技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测B] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/14+"\r\n"); damage = 1; return damage; //return
     * ret; } } else if (GameConstants.Warrior_Skill_900(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 12)) { //
     * c.dropMessage(1,
     * "[战士技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测C] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/10+"\r\n"); damage = 1; return damage; //return
     * ret;
     *
     * }
     * } else if (GameConstants.Warrior_Skill_2000(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 24)) { //
     * c.dropMessage(1,
     * "[战士技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测D] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/22+"\r\n"); damage = 1; return damage; //return
     * ret;
     *
     * }
     * //return ret; } } else if (c.getJob() == 200 || c.getJob() == 210 ||
     * c.getJob() == 211 || c.getJob() == 212 || c.getJob() == 220 || c.getJob()
     * == 221 || c.getJob() == 222 || c.getJob() == 230 || c.getJob() == 231 ||
     * c.getJob() == 232) {//魔法师技能 if
     * (GameConstants.Magician_Skill_90(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 11)) { //
     * c.dropMessage(1,
     * "[魔法师技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; //return ret; } } else if
     * (GameConstants.Magician_Skill_180(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 18)) {
     *
     * // c.dropMessage(1,
     * "[魔法师技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测B] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/12+"\r\n"); damage = 1; return damage; //return
     * ret; }
     *
     * } else if (GameConstants.Magician_Skill_240(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 20)) { //
     * c.dropMessage(1,
     * "[魔法师技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测C] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/5.6+"\r\n"); damage = 1; return damage;
     * //return ret; } } else if (GameConstants.Magician_Skill_670(ret.skill)) {
     * if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 36)) { //
     * c.dropMessage(1,
     * "[魔法师技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测D] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/45+"\r\n"); damage = 1; return damage; //return
     * ret; } }
     *
     * } else if (c.getJob() == 300 || c.getJob() == 310 || c.getJob() == 311 ||
     * c.getJob() == 312 || c.getJob() == 320 || c.getJob() == 321 || c.getJob()
     * == 322) {//弓箭手技能 if (GameConstants.Bowman_Skill_180(ret.skill) &&
     * (c.getBuffedValue(MapleBuffStat.SHARP_EYES) != null) && (damage > 0)) {
     * //判断火眼 if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 13)) { //
     * c.dropMessage(1,
     * "[弓箭手技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测A] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/1.2+"\r\n"); damage = 1; return damage;
     * //return ret; } } else if (GameConstants.Bowman_Skill_260(ret.skill)) {
     * if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 9)) { //
     * c.dropMessage(1,
     * "[弓箭手技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测B] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/3.5+"\r\n"); damage = 1; return damage;
     * //return ret; } } else if (GameConstants.Bowman_Skill_850(ret.skill)) {
     * if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 12)) { //
     * c.dropMessage(1,
     * "[弓箭手技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测C] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/10+"\r\n"); damage = 1; return damage; //return
     * ret; } } else if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 8.5)
     * && ret.skill == 0) { // c.dropMessage(1,
     * "[弓箭手技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; //return ret; } if
     * (GameConstants.Bowman_Skill_180(ret.skill) && (damage > 0)) { //没有火眼 if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 6.5)) { //
     * c.dropMessage(1,
     * "[弓箭手技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测A] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/1.2+"\r\n"); damage = 1; return damage;
     * //return ret; } } else if (GameConstants.Bowman_Skill_260(ret.skill)) {
     * if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 6)) { //
     * c.dropMessage(1,
     * "[弓箭手技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测B] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/3.5+"\r\n"); damage = 1; return damage;
     * //return ret; } } else if (GameConstants.Bowman_Skill_850(ret.skill)) {
     * if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 8)) { //
     * c.dropMessage(1,
     * "[弓箭手技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * // //System.out.print(" [检测C] "+c.getStat().getCurrentMaxBaseDamage()+"
     * <= "+damage+" || "+damage/10+"\r\n"); damage = 1; return damage; //return
     * ret; } } } else if (c.getJob() == 400 || c.getJob() == 410 || c.getJob()
     * == 411 || c.getJob() == 412 || c.getJob() == 420 || c.getJob() == 421 ||
     * c.getJob() == 422) {//飞侠技能 if (GameConstants.Thief_Skill_180(ret.skill))
     * { if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 11)) { //
     * c.dropMessage(1,
     * "[飞侠技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Thief_Skill_250(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 14)) { //
     * c.dropMessage(1,
     * "[飞侠技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Thief_Skill_500(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 18)) { //
     * c.dropMessage(1,
     * "[飞侠技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } } else if (c.getJob() == 500 || c.getJob()
     * == 510 || c.getJob() == 511 || c.getJob() == 512 || c.getJob() == 520 ||
     * c.getJob() == 521 || c.getJob() == 522) {//海盗技能 if
     * (GameConstants.Pirate_Skill_290(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 8)) { //
     * c.dropMessage(1,
     * "[海盗技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Pirate_Skill_420(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 9.3)) { //
     * c.dropMessage(1,
     * "[海盗技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Pirate_Skill_700(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 13)) { //
     * c.dropMessage(1,
     * "[海盗技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Pirate_Skill_810(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 13.2)) { //
     * c.dropMessage(1,
     * "[海盗技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Pirate_Skill_1200(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 18)) { //
     * c.dropMessage(1,
     * "[海盗技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } } else if (c.getJob() == 1000 ||
     * c.getJob() == 1100 || c.getJob() == 1110 || c.getJob() == 1111) {//魂骑士技能
     * if (GameConstants.Ghost_Knight_Skill_320(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 8.5)) { //
     * c.dropMessage(1,
     * "[魂骑士技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 7) && ret.skill == 0)
     * { // c.dropMessage(1,
     * "[魂骑士技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if (c.getJob() == 1200 || c.getJob()
     * == 1210 || c.getJob() == 1211) {//炎术士技能 if
     * (GameConstants.Fire_Knight_Skill_140(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 13)) { //
     * c.dropMessage(1,
     * "[炎术士技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Fire_Knight_Skill_500(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 8)) { //
     * c.dropMessage(1,
     * "[炎术士技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 7) && ret.skill == 0)
     * { // c.dropMessage(1,
     * "[炎术士技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if (c.getJob() == 1300 || c.getJob()
     * == 1310 || c.getJob() == 1311) {//风灵使者技能 if
     * (GameConstants.Wind_Knight_Skill_160(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 8)) { //
     * c.dropMessage(1,
     * "[风灵使者技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Wind_Knight_Skill_550(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 11)) { //
     * c.dropMessage(1,
     * "[风灵使者技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 7) && ret.skill == 0)
     * { // c.dropMessage(1,
     * "[风灵使者技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if (c.getJob() == 1400 || c.getJob()
     * == 1410 || c.getJob() == 1411) {//夜行者技能 if
     * (GameConstants.Night_Knight_Skill_220(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 9)) {
     * //c.dropMessage(1,
     * "[夜行者技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 7) && ret.skill == 0)
     * { // c.dropMessage(1,
     * "[夜行者技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; }
     *
     * } else if (c.getJob() == 1500 || c.getJob() == 1510 || c.getJob() ==
     * 1511) {//奇袭者技能 if (GameConstants.Thief_Skill_270(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 7.7)) { //
     * player.dropMessage(1,"[奇袭者技能攻击力检测+A]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Thief_Skill_420(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 10.2)) {
     * //c.dropMessage(1,
     * "[奇袭者技能攻击力检测+B]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * (GameConstants.Thief_Skill_650(ret.skill)) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 14)) { //
     * c.dropMessage(1,
     * "[奇袭者技能攻击力检测+C]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } else if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 7) && ret.skill == 0)
     * { // c.dropMessage(1,
     * "[奇袭者技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; }
     *
     * } else if (ret.skill == 4211006) { if
     * ((c.getStat().getCurrentMaxBaseDamage() <= damage / 13)) { //
     * c.dropMessage(1,
     * "[技能攻击力检测+D]\r\n非法使用外挂或者修改WZ\r\n导致:攻击力过高.\r\n攻击力无效！\r\n请勿再次使用后果自负！");
     * damage = 1; return damage; } } return damage; }
     */

    /**
     *
     * @param player
     * @param effect
     * @param attack
     * @param attackCount
     * @return
     */


    public static final String Damage_AttackCount(MapleCharacter player, MapleStatEffect effect, AttackInfo attack, int attackCount) {

        String reason = "null";
        int last = attackCount;
        boolean mirror_fix = false;
        if (player.getJob() >= 411 && player.getJob() <= 412) {
            mirror_fix = false;
        }
        if (mirror_fix) {
            last *= 2;
        }
        if (attack.hits > last) {
            reason = "封包伤害次数 : " + last + " 封包伤害次数: " + attack.skill;
        }
        return reason;
    }

    /**
     *
     * @param player
     * @param effect
     * @param attack
     * @return
     */
    public static final String Damage_HighDamage(MapleCharacter player, MapleStatEffect effect, AttackInfo attack) {
        boolean BeginnerJob = player.getJob() == 0 || player.getJob() == 1_000;
        //  int VipCount=player.getVip();
        String reason = "null";
        int check = 200_000;
        if (player.getLevel() <= 15) {
            check = 10_000;
        } else if (player.getLevel() <= 20) {
            check = 20_000;
        } else if (player.getLevel() <= 30) {
            check = 50_000;
        } else if (player.getLevel() <= 60) {
            check = 200_000;
        } else if (player.getLevel() <= 120) {
            check = 1_000_000;
        } else if (player.getLevel() <= 255) {
            check = 3_000_000;
        }
        for (final AttackPair oned : attack.allDamage) {
            if (player.getMap().getMonsterByOid(oned.objectid) != null) {
                for (Pair<Integer, Boolean> eachde : oned.attack) {
                    if (eachde.left >= 200_000) {
                        reason = "技能 " + attack.skill + " 打怪伤害 " + eachde.left;
                    }
                    if (GameConstants.Novice_Skill(attack.skill) && eachde.left > 40) {
                        reason = "技能 " + attack.skill + " 打怪伤害 " + eachde.left;
                    }
                    if (BeginnerJob) {
                        if (eachde.left > 40) {
                            reason = "技能 " + attack.skill + " 打怪伤害 " + eachde.left;
                        }
                    } else if (eachde.left >= check) {
                        reason = "技能 " + attack.skill + " 打怪伤害 " + eachde.left;
                    }
                }
            }
        }
        if (GameConstants.isElseSkill(attack.skill)) {
            reason = "null";
        }
        return reason;
    }

    /**
     *
     * @param player
     * @param effect
     * @param attack
     * @return
     */
    public static final String Damage_MobCount(MapleCharacter player, MapleStatEffect effect, AttackInfo attack) {
        String reason = "null";
        if (attack.targets > effect.getMobCount()) {
            reason = "打怪数量过多， 封包数量: " + attack.targets + " 正确数量:" + effect.getMobCount();
        }
        return reason;
    }
    //109dc  破攻专用

    /**
     *
     * @param chr
     * @param ret
     * @param damage
     * @return
     */
    public static int maxDamage(MapleCharacter chr, AttackInfo ret, int damage) {
        // int type = LoginServer.getMaxdamageType();
        int maxdamage;
        // damage = 199999;
        int VipCount = chr.getVip();
        maxdamage = (int) (199_999 + VipCount * 10_000);
        double randomNum = Math.random() * 1.1D;
        randomNum = Math.max(randomNum, 0.9D);
        int tempDamage = 0;

        for (IItem item : chr.getInventory(MapleInventoryType.EQUIPPED)) {
            int ak = 0;
            if ((item != null) && ((item instanceof Equip))) {
                ak = MapleItemInformationProvider.getInstance().getTotalStat((Equip) item);
            }

            tempDamage += ak * 15;
        }
        if ((ret.skill != 14_101_006) && (damage >= 199_999)) {
            if (((chr.getJob() >= 100) && (chr.getJob() <= 132)) || ((chr.getJob() >= 1_100) && (chr.getJob() <= 1_111)) || ((chr.getJob() >= 2_000) && (chr.getJob() <= 2_112)) || ((chr.getJob() >= 3_100) && (chr.getJob() <= 3_112)) || ((chr.getJob() >= 5_000) && (chr.getJob() <= 5_112))) {
                tempDamage += (int) (chr.getStat().getStr() * 2.0D + (chr.getStat().getDex() + chr.getStat().getInt() + chr.getStat().getLuk()));
            }

            if (((chr.getJob() >= 200) && (chr.getJob() <= 232)) || ((chr.getJob() >= 1_200) && (chr.getJob() <= 1_211)) || ((chr.getJob() >= 2_001) && (chr.getJob() <= 2_218)) || ((chr.getJob() >= 3_200) && (chr.getJob() <= 3_212))) {
                tempDamage += (int) (chr.getStat().getInt() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getLuk()));
            }

            if (((chr.getJob() >= 300) && (chr.getJob() <= 322)) || ((chr.getJob() >= 1_300) && (chr.getJob() <= 1_311)) || ((chr.getJob() >= 3_300) && (chr.getJob() <= 3_312)) || ((chr.getJob() >= 2_300) && (chr.getJob() <= 2_312)) || ((chr.getJob() >= 3_500) && (chr.getJob() <= 3_512))) {
                tempDamage += (int) (chr.getStat().getDex() * 2.0D + (chr.getStat().getStr() + chr.getStat().getInt() + chr.getStat().getLuk()));
            }

            if (((chr.getJob() >= 400) && (chr.getJob() <= 422)) || ((chr.getJob() >= 1_400) && (chr.getJob() <= 1_412)) || ((chr.getJob() >= 430) && (chr.getJob() <= 434)) || (chr.getJob() == 2_003) || ((chr.getJob() >= 2_400) && (chr.getJob() <= 2_412))) {
                tempDamage += (int) (chr.getStat().getLuk() * 2.0D + (chr.getStat().getStr() + chr.getStat().getDex() + chr.getStat().getInt()));
            }

            if (((chr.getJob() >= 580) && (chr.getJob() <= 592)) || ((chr.getJob() >= 1_500) && (chr.getJob() <= 1_511)) || (chr.getJob() == 508) || ((chr.getJob() >= 570) && (chr.getJob() <= 572))) {
                tempDamage += (int) ((chr.getStat().getStr() + chr.getStat().getDex()) / 2.0D * 2.0D + (chr.getStat().getInt() + chr.getStat().getLuk()));
            }

            if ((chr.getJob() == 501) || ((chr.getJob() >= 530) && (chr.getJob() <= 532))) {
                tempDamage += (int) (chr.getStat().getStr() * 2.0D + (chr.getStat().getLuk() + chr.getStat().getDex() + chr.getStat().getInt()));
            }
        }

        damage = (int) ((tempDamage + 199_999) * randomNum);

        damage = Math.min(damage, 199_999 + (VipCount * 10_000));
        // chr.getClient().getSession().write(MaplePacketCreator.sendHint(new StringBuilder().append("破攻实际伤害:#r").append(damage).toString(), 148, 5));
        chr.dropMessage(-1, new StringBuilder().append("破攻实际伤害: ").append(damage).toString());
        chr.getClient().getSession().write(MaplePacketCreator.sendHint("#e[破攻伤害]:#r" + damage + "#b ", 250, 5));

        if (damage > maxdamage) {
            damage = maxdamage;
        }

        if ((damage >= 2_000_000) || (damage < 0)) {
            damage = 2_000_000;
        }

        tempDamage = 0;
        return damage;
    }
}
