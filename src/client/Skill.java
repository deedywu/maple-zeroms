/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import constants.GameConstants;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataTool;
import server.MapleStatEffect;
import server.life.Element;

/**
 *
 * @author zjj
 */
public class Skill implements ISkill {

    //public static final int[] skills = new int[]{4311003, 4321000, 4331002, 4331005, 4341004, 4341007};
    private String name = "";
    private final List<MapleStatEffect> effects = new ArrayList<>();
    private Element element;
    private byte level;
    private int id, animationTime, requiredSkill, masterLevel;
    private boolean action, invisible, chargeskill, timeLimited;

    /**
     *
     * @param id
     */
    public Skill(final int id) {
        super();
        this.id = id;
    }

    /**
     *
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param id
     * @param data
     * @return
     */
    public static final Skill loadFromData(final int id, final MapleData data) {
        Skill ret = new Skill(id);

        boolean isBuff = false;
        final int skillType = MapleDataTool.getInt("skillType", data, -1);
        final String elem = MapleDataTool.getString("elemAttr", data, null);
        if (elem != null) {
            ret.element = Element.getFromChar(elem.charAt(0));
        } else {
            ret.element = Element.NEUTRAL;
        }
        ret.invisible = MapleDataTool.getInt("invisible", data, 0) > 0;
        ret.timeLimited = MapleDataTool.getInt("timeLimited", data, 0) > 0;
        ret.masterLevel = MapleDataTool.getInt("masterLevel", data, 0);
        final MapleData effect = data.getChildByPath("effect");
        if (skillType != -1) {
            if (skillType == 2) {
                isBuff = true;
            }
        } else {
            final MapleData action_ = data.getChildByPath("action");
            final MapleData hit = data.getChildByPath("hit");
            final MapleData ball = data.getChildByPath("ball");

            boolean action = false;
            if (action_ == null) {
                if (data.getChildByPath("prepare/action") != null) {
                    action = true;
                } else {
                    switch (id) {
                        case 5_201_001:
                        case 5_221_009:
                        case 4_221_001:
                        case 4_321_001:
                        case 4_321_000:
                        case 4_331_001: //o_o
                        case 3_101_005: //or is this really hack
                            action = true;
                            break;
                    }
                }
            } else {
                action = true;
            }
            ret.action = action;
            isBuff = effect != null && hit == null && ball == null;
            isBuff |= action_ != null && MapleDataTool.getString("0", action_, "").equals("alert2");
            switch (id) {
                case 2_301_002: // heal is alert2 but not overtime...
                case 2_111_003: // poison mist
                case 12_111_005: // Flame Gear
                case 2_111_002: // explosion
                case 4_211_001: // chakra
                case 2_121_001: // Big bang
                case 2_221_001: // Big bang
                case 2_321_001: // Big bang
                    isBuff = false;
                    break;
                case 1_004: // monster riding
                case 1_017:
                case 20_001_019:
                case 10_001_019:
                case 10_001_004:
                case 20_001_004:
                case 20_011_004:
                case 30_001_004:
                case 1_026: //Soaring
                case 10_001_026:
                case 20_001_026:
                case 20_011_026:
                case 30_001_026:
                case 9_101_004: // hide is a buff -.- atleast for us o.o"
                case 1_111_002: // combo
                case 4_211_003: // pickpocket
                case 4_111_001: // mesoup
                case 15_111_002: // Super Transformation
                case 5_111_005: // Transformation
                case 5_121_003: // Super Transformation
                case 13_111_005: // Alabtross
                case 21_000_000: // Aran Combo
                case 21_101_003: // Body Pressure
                case 21_110_000:
                case 5_211_001: // Pirate octopus summon
                case 5_211_002:
                case 5_220_002: // wrath of the octopi
                case 5_001_005: //dash
                case 15_001_003:
                case 5_211_006: //homing beacon
                case 5_220_011: //bullseye
                case 5_110_001: //energy charge
                case 15_100_004:
                case 5_121_009: //speed infusion
                case 15_111_005:

                case 22_121_001: //element reset
                case 22_131_001: //magic shield
                case 22_141_002: //magic booster
                case 22_151_002: //killer wing
                case 22_151_003: //magic resist
                case 22_171_000: //maple warrior
                case 22_171_004: //hero will
                case 22_181_000: //onyx blessing
                case 22_181_003: //soul stone
                //case 22121000:
                //case 22141003:
                //case 22151001:
                //case 22161002:
                case 4_331_003: //owl spirit
                case 15_101_006: //spark
                case 15_111_006: //spark
                case 4_321_000: //tornado spin
                case 1_320_009: //beholder's buff.. passive
                case 35_120_000:
                case 35_001_002: //TEMP. mech
                case 9_001_004: // hide
                case 4_341_002:

                case 32_001_003: //dark aura
                case 32_120_000:
                case 32_101_002: //blue aura
                case 32_110_000:
                case 32_101_003: //yellow aura
                case 32_120_001:
                case 35_101_007: //perfect armor
                case 35_121_006: //satellite safety
                case 35_001_001: //flame
                case 35_101_009:
                case 35_111_007: //TEMP
                case 35_121_005: //missile
                case 35_121_013:
                //case 35111004: //siege
                case 35_101_002: //TEMP
                case 33_111_003: //puppet ?
                case 1_211_009:
                case 1_111_007:
                case 1_311_007: //magic,armor,atk crash
                    isBuff = true;
                    break;
            }
        }
        ret.chargeskill = data.getChildByPath("keydown") != null;

        for (final MapleData level : data.getChildByPath("level")) {
            ret.effects.add(MapleStatEffect.loadSkillEffectFromData(level, id, isBuff, Byte.parseByte(level.getName())));
        }
        final MapleData reqDataRoot = data.getChildByPath("req");
        if (reqDataRoot != null) {
            for (final MapleData reqData : reqDataRoot.getChildren()) {
                ret.requiredSkill = Integer.parseInt(reqData.getName());
                ret.level = (byte) MapleDataTool.getInt(reqData, 1);
            }
        }
        ret.animationTime = 0;
        if (effect != null) {
            for (final MapleData effectEntry : effect) {
                ret.animationTime += MapleDataTool.getIntConvert("delay", effectEntry, 0);
            }
        }
        return ret;
    }

    /**
     *
     * @param level
     * @return
     */
    @Override
    public MapleStatEffect getEffect(final int level) {
        if (effects.size() < level) {
            if (effects.size() > 0) { //incAllskill
                return effects.get(effects.size() - 1);
            }
            return null;
        } else if (level <= 0) {
            return effects.get(0);
        }
        return effects.get(level - 1);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean getAction() {
        return action;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isChargeSkill() {
        return chargeskill;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInvisible() {
        return invisible;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean hasRequiredSkill() {
        return level > 0;
    }

    /**
     *
     * @return
     */
    @Override
    public int getRequiredSkillLevel() {
        return level;
    }

    /**
     *
     * @return
     */
    @Override
    public int getRequiredSkillId() {
        return requiredSkill;
    }

    /**
     *
     * @return
     */
    @Override
    public byte getMaxLevel() {
        return (byte) effects.size();
    }

    /**
     *
     * @param job
     * @return
     */
    @Override
    public boolean canBeLearnedBy(int job) {
        int jid = job;
        int skillForJob = id / 10_000;
        if (skillForJob == 2_001 && GameConstants.isEvan(job)) {
            return true; //special exception for evan -.-
        } else if (jid / 100 != skillForJob / 100) { // wrong job
            return false;
        } else if (jid / 1_000 != skillForJob / 1_000) { // wrong job
            return false;
        } else if (GameConstants.isAdventurer(skillForJob) && !GameConstants.isAdventurer(job)) {
            return false;
        } else if (GameConstants.isKOC(skillForJob) && !GameConstants.isKOC(job)) {
            return false;
        } else if (GameConstants.isAran(skillForJob) && !GameConstants.isAran(job)) {
            return false;
        } else if (GameConstants.isEvan(skillForJob) && !GameConstants.isEvan(job)) {
            return false;
        } else if (GameConstants.isResist(skillForJob) && !GameConstants.isResist(job)) {
            return false;
        } else if ((skillForJob / 10) % 10 > (jid / 10) % 10) { // wrong 2nd job
            return false;
        } else if (skillForJob % 10 > jid % 10) { // wrong 3rd/4th job
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isTimeLimited() {
        return timeLimited;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isFourthJob() {
        if (id / 10_000 >= 2_212 && id / 10_000 < 3_000) { //evan skill
            return ((id / 10_000) % 10) >= 7;
        }
        if (id / 10_000 >= 430 && id / 10_000 <= 434) { //db skill
            return ((id / 10_000) % 10) == 4 || getMasterLevel() > 0;
        }
        return ((id / 10_000) % 10) == 2;
    }

    /**
     *
     * @return
     */
    @Override
    public Element getElement() {
        return element;
    }

    /**
     *
     * @return
     */
    @Override
    public int getAnimationTime() {
        return animationTime;
    }

    /**
     *
     * @return
     */
    @Override
    public int getMasterLevel() {
        return masterLevel;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isBeginnerSkill() {
        int jobId = id / 10_000;
        return jobId == 0 || jobId == 1_000 || jobId == 2_000 || jobId == 2_001 || jobId == 3_000;
    }
}
