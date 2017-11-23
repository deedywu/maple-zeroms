package server.life;

import constants.GameConstants;
import java.awt.*;
import java.util.*;
import java.util.List;
import server.life.MapleLifeFactory.loseItem;
import tools.Pair;
import tools.Triple;

/**
 *
 * @author zjj
 */
public class MapleMonsterStats {

    private final int id;
    private final EnumMap<Element, ElementalEffectiveness> resistance = new EnumMap<>(Element.class);
    private final Map<String, Integer> animationTimes = new HashMap<>();
    private final List<Triple<Integer, Integer, Integer>> skills = new ArrayList<>();
    private final List<MobAttackInfo> mai = new ArrayList<>();
    private byte cp, selfDestruction_action, tagColor, tagBgColor, rareItemDropLevel, HPDisplayType, summonType, category;
    private short level, charismaEXP;
    private long hp;
    private int exp;
    private int mp;
    private int removeAfter;
    private int buffToGive;
    private int fixedDamage;
    private int selfDestruction_hp;
    private int dropItemPeriod;
    private int point;
    private int eva;
    private int acc;
    private int PhysicalAttack, PhysicalDefense, MagicDefense;
    private int MagicAttack;
    private int speed;
    private int partyBonusR;
    private int pushed;
    private int link;
    private int weaponPoint;
    private int PDRate;
    private int MDRate;
    private int smartPhase;
    private boolean boss, undead, ffaLoot, firstAttack, isExplosiveReward, mobile, fly, onlyNormalAttack, friendly, noDoom, invincible, partyBonusMob, changeable, escort, removeOnMiss, skeleton;
    private String name, mobType, hitParts;
    private List<Integer> revives = new ArrayList<>();
    private List<Pair<Point, Point>> mobZone = new ArrayList<>();
    private Pair<Integer, Integer> cool = null;
    private List<loseItem> loseItem = null;
    private BanishInfo banish;
    private TransMobs transMobs;

    /**
     *
     * @param id
     */
    public MapleMonsterStats(int id) {
        this.id = id;
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
    public int getExp() {
        return exp;
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
     * @return
     */
    public long getHp() {
        return hp;
    }

    /**
     *
     * @param hp
     */
    public void setHp(long hp) {
        this.hp = hp;
    }

    /**
     *
     * @return
     */
    public int getMp() {
        return mp;
    }

    /**
     *
     * @param mp
     */
    public void setMp(int mp) {
        this.mp = mp;
    }

    /**
     *
     * @return
     */
    public short getLevel() {
        return level;
    }

    /**
     *
     * @param level
     */
    public void setLevel(short level) {
        this.level = level;
    }

    /**
     *
     * @return
     */
    public int getWeaponPoint() {
        return weaponPoint;
    }

    /**
     *
     * @param wp
     */
    public void setWeaponPoint(int wp) {
        this.weaponPoint = wp;
    }

    /**
     *
     * @return
     */
    public short getCharismaEXP() {
        return charismaEXP;
    }

    /**
     *
     * @param leve
     */
    public void setCharismaEXP(short leve) {
        this.charismaEXP = leve;
    }

    /**
     *
     * @return
     */
    public byte getSelfD() {
        return selfDestruction_action;
    }

    /**
     *
     * @param selfDestruction_action
     */
    public void setSelfD(byte selfDestruction_action) {
        this.selfDestruction_action = selfDestruction_action;
    }

    /**
     *
     * @param selfDestruction_hp
     */
    public void setSelfDHP(int selfDestruction_hp) {
        this.selfDestruction_hp = selfDestruction_hp;
    }

    /**
     *
     * @return
     */
    public int getSelfDHp() {
        return selfDestruction_hp;
    }

    /**
     *
     * @return
     */
    public int getFixedDamage() {
        return fixedDamage;
    }

    /**
     *
     * @param damage
     */
    public void setFixedDamage(int damage) {
        this.fixedDamage = damage;
    }

    /**
     *
     * @return
     */
    public int getPushed() {
        return pushed;
    }

    /**
     *
     * @param damage
     */
    public void setPushed(int damage) {
        this.pushed = damage;
    }

    /**
     *
     * @return
     */
    public int getPhysicalAttack() {
        return PhysicalAttack;
    }

    /**
     *
     * @param PhysicalAttack
     */
    public void setPhysicalAttack(int PhysicalAttack) {
        this.PhysicalAttack = PhysicalAttack;
    }

    /**
     *
     * @return
     */
    public int getMagicAttack() {
        return MagicAttack;
    }

    /**
     *
     * @param MagicAttack
     */
    public void setMagicAttack(int MagicAttack) {
        this.MagicAttack = MagicAttack;
    }

    /**
     *
     * @param MagicDefense
     */
    public final void setMagicDefense(final int MagicDefense) {
        this.MagicDefense = MagicDefense;
    }

    /**
     *
     * @return
     */
    public final int getMagicDefense() {
        return MagicDefense;
    }

    /**
     *
     * @param PhysicalDefense
     */
    public void setPhysicalDefense(final int PhysicalDefense) {
        this.PhysicalDefense = PhysicalDefense;
    }

    /**
     *
     * @return
     */
    public int getPhysicalDefense() {
        return PhysicalDefense;
    }

    /**
     *
     * @return
     */
    public int getEva() {
        return eva;
    }

    /**
     *
     * @param eva
     */
    public void setEva(int eva) {
        this.eva = eva;
    }

    /**
     *
     * @return
     */
    public int getAcc() {
        return acc;
    }

    /**
     *
     * @param acc
     */
    public void setAcc(int acc) {
        this.acc = acc;
    }

    /**
     *
     * @return
     */
    public int getSpeed() {
        return speed;
    }

    /**
     *
     * @param speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     *
     * @return
     */
    public int getPartyBonusRate() {
        return partyBonusR;
    }

    /**
     *
     * @param speed
     */
    public void setPartyBonusRate(int speed) {
        this.partyBonusR = speed;
    }

    /**
     *
     * @param onlyNormalAttack
     */
    public void setOnlyNormalAttack(boolean onlyNormalAttack) {
        this.onlyNormalAttack = onlyNormalAttack;
    }

    /**
     *
     * @return
     */
    public boolean getOnlyNoramlAttack() {
        return onlyNormalAttack;
    }

    /**
     *
     * @return
     */
    public BanishInfo getBanishInfo() {
        return banish;
    }

    /**
     *
     * @param banish
     */
    public void setBanishInfo(BanishInfo banish) {
        this.banish = banish;
    }

    /**
     *
     * @return
     */
    public int getRemoveAfter() {
        return removeAfter;
    }

    /**
     *
     * @param removeAfter
     */
    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }

    /**
     *
     * @return
     */
    public byte getrareItemDropLevel() {
        return rareItemDropLevel;
    }

    /**
     *
     * @param rareItemDropLevel
     */
    public void setrareItemDropLevel(byte rareItemDropLevel) {
        this.rareItemDropLevel = rareItemDropLevel;
    }

    /**
     *
     * @return
     */
    public boolean isBoss() {
        return boss;
    }

    /**
     *
     * @param boss
     */
    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    /**
     *
     * @return
     */
    public boolean isFfaLoot() {
        return ffaLoot;
    }

    /**
     *
     * @param ffaLoot
     */
    public void setFfaLoot(boolean ffaLoot) {
        this.ffaLoot = ffaLoot;
    }

    /**
     *
     * @return
     */
    public boolean isEscort() {
        return escort;
    }

    /**
     *
     * @param ffaL
     */
    public void setEscort(boolean ffaL) {
        this.escort = ffaL;
    }

    /**
     *
     * @return
     */
    public boolean isExplosiveReward() {
        return isExplosiveReward;
    }

    /**
     *
     * @param isExplosiveReward
     */
    public void setExplosiveReward(boolean isExplosiveReward) {
        this.isExplosiveReward = isExplosiveReward;
    }

    /**
     *
     * @param name
     * @param delay
     */
    public void setAnimationTime(String name, int delay) {
        animationTimes.put(name, delay);
    }

    /**
     *
     * @param name
     * @return
     */
    public int getAnimationTime(String name) {
        Integer ret = animationTimes.get(name);
        if (ret == null) {
            return 500;
        }
        return ret;
    }

    /**
     *
     * @param mobile
     */
    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    /**
     *
     * @return
     */
    public boolean getMobile() {
        return mobile;
    }

    /**
     *
     * @return
     */
    public boolean isMobile() {
        return animationTimes.containsKey("move") || animationTimes.containsKey("fly");
    }

    /**
     *
     * @param fly
     */
    public void setFly(boolean fly) {
        this.fly = fly;
    }

    /**
     *
     * @return
     */
    public boolean getFly() {
        return fly;
    }

    /**
     *
     * @return
     */
    public List<Integer> getRevives() {
        return revives;
    }

    /**
     *
     * @param revives
     */
    public void setRevives(List<Integer> revives) {
        this.revives = revives;
    }

    /**
     *
     * @return
     */
    public boolean getUndead() {
        return undead;
    }

    /**
     *
     * @param undead
     */
    public void setUndead(boolean undead) {
        this.undead = undead;
    }

    /**
     *
     * @return
     */
    public byte getSummonType() {
        return summonType;
    }

    /**
     *
     * @param selfDestruction
     */
    public void setSummonType(byte selfDestruction) {
        this.summonType = selfDestruction;
    }

    /**
     *
     * @return
     */
    public byte getCategory() {
        return category;
    }

    /**
     *
     * @param selfDestruction
     */
    public void setCategory(byte selfDestruction) {
        this.category = selfDestruction;
    }

    /**
     *
     * @return
     */
    public int getPDRate() {
        return PDRate;
    }

    /**
     *
     * @param selfDestruction
     */
    public void setPDRate(int selfDestruction) {
        this.PDRate = selfDestruction;
    }

    /**
     *
     * @return
     */
    public int getMDRate() {
        return MDRate;
    }

    /**
     *
     * @param selfDestruction
     */
    public void setMDRate(int selfDestruction) {
        this.MDRate = selfDestruction;
    }

    /**
     *
     * @return
     */
    public EnumMap<Element, ElementalEffectiveness> getElements() {
        return resistance;
    }

    /**
     *
     * @param e
     * @param ee
     */
    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        resistance.put(e, ee);
    }

    /**
     *
     * @param e
     */
    public void removeEffectiveness(Element e) {
        resistance.remove(e);
    }

    /**
     *
     * @param e
     * @return
     */
    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = resistance.get(e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.NORMAL;
        } else {
            return elementalEffectiveness;
        }
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
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return mobType;
    }

    /**
     *
     * @param mobt
     */
    public void setType(String mobt) {
        this.mobType = mobt;
    }

    /**
     *
     * @return
     */
    public String getHitParts() {
        return hitParts;
    }

    /**
     *
     * @param hitParts
     */
    public void setHitParts(String hitParts) {
        this.hitParts = hitParts;
    }

    /**
     *
     * @return
     */
    public byte getTagColor() {
        return tagColor;
    }

    /**
     *
     * @param tagColor
     */
    public void setTagColor(int tagColor) {
        this.tagColor = (byte) tagColor;
    }

    /**
     *
     * @return
     */
    public byte getTagBgColor() {
        return tagBgColor;
    }

    /**
     *
     * @param tagBgColor
     */
    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = (byte) tagBgColor;
    }

    /**
     *
     * @param skill_
     */
    public void setSkills(List<Triple<Integer, Integer, Integer>> skill_) {
        for (Triple<Integer, Integer, Integer> skill : skill_) {
            skills.add(skill);
        }
    }

    /**
     *
     * @return
     */
    public List<Triple<Integer, Integer, Integer>> getSkills() {
        return Collections.unmodifiableList(this.skills);
    }

    /**
     *
     * @return
     */
    public byte getNoSkills() {
        return (byte) skills.size();
    }

    /**
     *
     * @param skillId
     * @param level
     * @return
     */
    public boolean hasSkill(int skillId, int level) {
        for (Triple<Integer, Integer, Integer> skill : skills) {
            if (skill.getLeft() == skillId && skill.getRight() == level) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isFirstAttack() {
        return firstAttack;
    }

    /**
     *
     * @param firstAttack
     */
    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    /**
     *
     * @return
     */
    public byte getCP() {
        return cp;
    }

    /**
     *
     * @param cp
     */
    public void setCP(byte cp) {
        this.cp = cp;
    }

    /**
     *
     * @return
     */
    public int getPoint() {
        return point;
    }

    /**
     *
     * @param cp
     */
    public void setPoint(int cp) {
        this.point = cp;
    }

    /**
     *
     * @return
     */
    public boolean isFriendly() {
        return friendly;
    }

    /**
     *
     * @param friendly
     */
    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
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
     * @param invin
     */
    public void setInvincible(boolean invin) {
        this.invincible = invin;
    }

    /**
     *
     * @return
     */
    public int getSmartPhase() {
        return smartPhase;
    }

    /**
     *
     * @param smartPhase
     */
    public void setSmartPhase(int smartPhase) {
        this.smartPhase = smartPhase;
    }

    /**
     *
     * @param invin
     */
    public void setChange(boolean invin) {
        this.changeable = invin;
    }

    /**
     *
     * @return
     */
    public boolean isChangeable() {
        return changeable;
    }

    /**
     *
     * @return
     */
    public boolean isPartyBonus() {
        return partyBonusMob;
    }

    /**
     *
     * @param invin
     */
    public void setPartyBonus(boolean invin) {
        this.partyBonusMob = invin;
    }

    /**
     *
     * @return
     */
    public boolean isNoDoom() {
        return noDoom;
    }

    /**
     *
     * @param doom
     */
    public void setNoDoom(boolean doom) {
        this.noDoom = doom;
    }

    /**
     *
     * @return
     */
    public int getBuffToGive() {
        return buffToGive;
    }

    /**
     *
     * @param buff
     */
    public void setBuffToGive(int buff) {
        this.buffToGive = buff;
    }

    /**
     *
     * @return
     */
    public int getLink() {
        return link;
    }

    /**
     *
     * @param link
     */
    public void setLink(int link) {
        this.link = link;
    }

    /**
     *
     * @return
     */
    public byte getHPDisplayType() {
        return HPDisplayType;
    }

    /**
     *
     * @param HPDisplayType
     */
    public void setHPDisplayType(byte HPDisplayType) {
        this.HPDisplayType = HPDisplayType;
    }

    /**
     *
     * @return
     */
    public int getDropItemPeriod() {
        return dropItemPeriod;
    }

    /**
     *
     * @param d
     */
    public void setDropItemPeriod(int d) {
        this.dropItemPeriod = d;
    }

    /**
     *
     * @param removeOnMiss
     */
    public void setRemoveOnMiss(boolean removeOnMiss) {
        this.removeOnMiss = removeOnMiss;
    }

    /**
     *
     * @return
     */
    public boolean removeOnMiss() {
        return removeOnMiss;
    }

    /**
     *
     * @return
     */
    public Pair<Integer, Integer> getCool() {
        return cool;
    }

    /**
     *
     * @param cool
     */
    public void setCool(Pair<Integer, Integer> cool) {
        this.cool = cool;
    }

    /**
     *
     * @return
     */
    public List<Pair<Point, Point>> getMobZone() {
        return mobZone;
    }

    /**
     *
     * @param mobZone
     */
    public void setMobZone(List<Pair<Point, Point>> mobZone) {
        this.mobZone = mobZone;
    }

    /**
     *
     * @return
     */
    public boolean isSkeleton() {
        return skeleton;
    }

    /**
     *
     * @param skeleton
     */
    public void setSkeleton(boolean skeleton) {
        this.skeleton = skeleton;
    }

    /**
     *
     * @return
     */
    public List<loseItem> loseItem() {
        return loseItem;
    }

    /**
     *
     * @param li
     */
    public void addLoseItem(loseItem li) {
        if (loseItem == null) {
            loseItem = new LinkedList<>();
        }
        loseItem.add(li);
    }

    /**
     *
     * @param ma
     */
    public void addMobAttack(MobAttackInfo ma) {
        this.mai.add(ma);
    }

    /**
     *
     * @param attack
     * @return
     */
    public MobAttackInfo getMobAttack(int attack) {
        if (attack >= this.mai.size() || attack < 0) {
            return null;
        }
        return this.mai.get(attack);
    }

    /**
     *
     * @return
     */
    public List<MobAttackInfo> getMobAttacks() {
        return this.mai;
    }

    /**
     *
     * @return
     */
    public int dropsMesoCount() {
        if (getRemoveAfter() != 0 || isInvincible() || getOnlyNoramlAttack() || getDropItemPeriod() > 0 || getCP() > 0 || getPoint() > 0 || getFixedDamage() > 0 || getSelfD() != -1 || getPDRate() <= 0 || getMDRate() <= 0) {
            return 0;
        }
        int mobId = getId() / 100_000;
        if (GameConstants.getPartyPlayHP(getId()) > 0 || mobId == 97 || mobId == 95 || mobId == 93 || mobId == 91 || mobId == 90) {
            return 0;
        }
        if (isExplosiveReward()) {
            return 7;
        }
        if (isBoss()) {
            return 2;
        }
        return 1;
    }

    /**
     *
     * @return
     */
    public TransMobs getTransMobs() {
        return transMobs;
    }

    /**
     *
     * @param transMobs
     */
    public void setTransMobs(TransMobs transMobs) {
        this.transMobs = transMobs;
    }

    /**
     *
     */
    public static class TransMobs {

        private List<Integer> mobids;
        private List<Pair<Integer, Integer>> skills;
        private int time;
        private int cooltime;
        private int hpTriggerOn;
        private int hpTriggerOff;
        private int withMob = 0;

        /**
         *
         * @param mobids
         * @param skills
         * @param time
         * @param cooltime
         * @param hpTriggerOn
         * @param hpTriggerOff
         * @param withMob
         */
        public TransMobs(List<Integer> mobids, List<Pair<Integer, Integer>> skills, int time, int cooltime, int hpTriggerOn, int hpTriggerOff, int withMob) {
            this.mobids = mobids;
            this.skills = skills;
            this.time = time;
            this.cooltime = cooltime;
            this.hpTriggerOn = hpTriggerOn;
            this.hpTriggerOff = hpTriggerOff;
            this.withMob = withMob;
        }

        /**
         *
         * @return
         */
        public List<Integer> getMobids() {
            return mobids;
        }

        /**
         *
         * @return
         */
        public List<Pair<Integer, Integer>> getSkills() {
            return skills;
        }

        /**
         *
         * @return
         */
        public int getTime() {
            return time;
        }

        /**
         *
         * @return
         */
        public int getCooltime() {
            return cooltime;
        }

        /**
         *
         * @return
         */
        public int getHpTriggerOn() {
            return hpTriggerOn;
        }

        /**
         *
         * @return
         */
        public int getHpTriggerOff() {
            return hpTriggerOff;
        }

        /**
         *
         * @return
         */
        public int getWithMob() {
            return withMob;
        }
    }
}
