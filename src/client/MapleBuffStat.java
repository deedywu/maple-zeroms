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

import java.io.Serializable;

/**
 *
 * @author zjj
 */
public enum MapleBuffStat implements Serializable {
    //DICE = 0x100000, the roll is determined in the long-ass packet itself as buffedvalue
    //ENERGY CHARGE = 0x400000
    //teleport mastery = 0x800000
    //PIRATE'S REVENGE = 0x4000000
    //DASH = 0x8000000 and 0x10000000
    //SPEED INFUSION = 0x40000000
    //MONSTER RIDER = 0x20000000
    //COMBAT ORDERS = 0x1000000

    /**
     *
     */

    ENHANCED_WDEF(0x1, true),

    /**
     *
     */
    ENHANCED_MDEF(0x2, true),

    /**
     *
     */
    PERFECT_ARMOR(0x4, true),

    /**
     *
     */
    SATELLITESAFE_PROC(0x8, true),

    /**
     *
     */
    SATELLITESAFE_ABSORB(0x10, true),

    /**
     *
     */
    CRITICAL_RATE_BUFF(0x40, true),

    /**
     *
     */
    MP_BUFF(0x80, true),

    /**
     *
     */
    DAMAGE_TAKEN_BUFF(0x100, true),

    /**
     *
     */
    DODGE_CHANGE_BUFF(0x200, true),

    /**
     *
     */
    CONVERSION(0x400, true),

    /**
     *
     */
    REAPER(0x800, true),

    /**
     *
     */
    MECH_CHANGE(0x2000, true), //determined in packet by [skillLevel or something] [skillid] 1E E0 58 52???

    /**
     *
     */
    DARK_AURA(0x8000, true),

    /**
     *
     */
    BLUE_AURA(0x1_0000, true),

    /**
     *
     */
    YELLOW_AURA(0x2_0000, true),
    //ENERGY_CHARGE(0x80000000000L, true),

    /**
     *
     */
    ENERGY_CHARGE(0x20_0000_0000L, true),

    /**
     *
     */
    DASH_SPEED(549_755_813_888L),

    /**
     *
     */
    DASH_JUMP(0x40_0000_0000L),
    //DASH_SPEED(0x100000000000L, true),
    // DASH_JUMP(0x200000000000L, true),
    //疾驰移动(549755813888L),
    //疾驰跳跃(0x4000000000L),
    // MONSTER_RIDING(0x400000000000L, true),
    // SPEED_INFUSION(0x800000000000L, true),
    // HOMING_BEACON(0x1000000000000L, true),

    /**
     *
     */
    MONSTER_RIDING(0x100_0000_0000L, true),

    /**
     *
     */
    SPEED_INFUSION(/*
     * 0x40000000000L
             */ /*
     * 0x40000000000L
     */ 140_737_488_355_328L, true),

    /**
     *
     */
    HOMING_BEACON(0x800_0000_0000L, true),

    /**
     *
     */
    ELEMENT_RESET(0x200_0000_0000_0000L, true),

    /**
     *
     */
    ARAN_COMBO(0x1000_0000_0000_0000L, true),

    /**
     *
     */
    COMBO_DRAIN(0x2000_0000_0000_0000L, true),

    /**
     *
     */
    COMBO_BARRIER(0x4000_0000_0000_0000L, true),
    // BODY_PRESSURE(0x8000000000000000L, true),

    /**
     *
     */
    BODY_PRESSURE(17_592_186_044_416L, true),

    /**
     *
     */
    SMART_KNOCKBACK(0x1, false),

    /**
     *
     */
    PYRAMID_PQ(0x2, false),

    /**
     *
     */
    LIGHTNING_CHARGE(0x4, false),
    //POST BB
    //DUMMY_STAT0     (0x8000000L, true), //appears on login
    //DUMMY_STAT1     (0x10000000L, true),
    //DUMMY_STAT2     (0x20000000L, true),
    //DUMMY_STAT3     (0x40000000L, true),
    //DUMMY_STAT4     (0x80000000L, true),

    /**
     *
     */

    SOUL_STONE(0x200_0000_0000L, true), //same as pyramid_pq

    /**
     *
     */
    MAGIC_SHIELD(0x8000_0000_0000L, true),

    /**
     *
     */
    MAGIC_RESISTANCE(0x1_0000_0000_0000L, true),

    /**
     *
     */
    SOARING(0x4_0000_0000_0000L, true),
    //    LIGHTNING_CHARGE(0x10000000000000L, true),
    //db stuff

    /**
     *
     */
    MIRROR_IMAGE(0x20_0000_0000_0000L, true),

    /**
     *
     */
    OWL_SPIRIT(0x40_0000_0000_0000L, true),

    /**
     *
     */
    FINAL_CUT(0x100_0000_0000_0000L, true),

    /**
     *
     */
    THORNS(0x200_0000_0000_0000L, true),

    /**
     *
     */
    DAMAGE_BUFF(0x400_0000_0000_0000L, true),

    /**
     *
     */
    RAINING_MINES(0x1000_0000_0000_0000L, true),

    /**
     *
     */
    ENHANCED_MAXHP(0x2000_0000_0000_0000L, true),

    /**
     *
     */
    ENHANCED_MAXMP(0x4000_0000_0000_0000L, true),

    /**
     *
     */
    ENHANCED_WATK(0x8000_0000_0000_0000L, true),

    /**
     *
     */
    MORPH(0x2),

    /**
     *
     */
    RECOVERY(0x4),

    /**
     *
     */
    MAPLE_WARRIOR(0x8),

    /**
     *
     */
    STANCE(0x10),

    /**
     *
     */
    SHARP_EYES(0x20),

    /**
     *
     */
    MANA_REFLECTION(0x40),

    /**
     *
     */
    DRAGON_ROAR(0x80), // Stuns the user

    /**
     *
     */

    SPIRIT_CLAW(0x100),

    /**
     *
     */
    INFINITY(0x200),

    /**
     *
     */
    HOLY_SHIELD(0x400),

    /**
     *
     */
    HAMSTRING(0x800),

    /**
     *
     */
    BLIND(0x1000),

    /**
     *
     */
    CONCENTRATE(0x2000),

    /**
     *
     */
    ECHO_OF_HERO(0x8000),

    /**
     *
     */
    UNKNOWN3(0x1_0000),

    /**
     *
     */
    GHOST_MORPH(0x2_0000),

    /**
     *
     */
    ARIANT_COSS_IMU(0x4_0000), // The white ball around you

    /**
     *
     */

    DROP_RATE(0x10_0000),

    /**
     *
     */
    MESO_RATE(0x20_0000),

    /**
     *
     */
    EXPRATE(0x40_0000),

    /**
     *
     */
    ACASH_RATE(0x80_0000),

    /**
     *
     */
    GM_HIDE(0x100_0000),

    /**
     *
     */
    UNKNOWN7(0x200_0000),

    /**
     *
     */
    ILLUSION(0x400_0000),

    /**
     *
     */
    BERSERK_FURY(0x800_0000),

    /**
     *
     */
    DIVINE_BODY(0x1000_0000),

    /**
     *
     */
    SPARK(0x2000_0000),

    /**
     *
     */
    ARIANT_COSS_IMU2(0x4000_0000), // no idea, seems the same

    /**
     *
     */
    FINALATTACK(0x8000_0000L),

    /**
     *
     */
    WATK(0x1_0000_0000L),

    /**
     *
     */
    WDEF(0x2_0000_0000L),

    /**
     *
     */
    MATK(0x4_0000_0000L),

    /**
     *
     */
    MDEF(0x8_0000_0000L),

    /**
     *
     */
    ACC(0x10_0000_0000L),

    /**
     *
     */
    AVOID(0x20_0000_0000L),

    /**
     *
     */
    HANDS(0x40_0000_0000L),

    /**
     *
     */
    SPEED(0x80_0000_0000L),

    /**
     *
     */
    JUMP(0x100_0000_0000L),

    /**
     *
     */
    MAGIC_GUARD(0x200_0000_0000L),

    /**
     *
     */
    DARKSIGHT(0x400_0000_0000L),

    /**
     *
     */
    BOOSTER(0x800_0000_0000L),

    /**
     *
     */
    POWERGUARD(0x1000_0000_0000L),

    /**
     *
     */
    MAXHP(0x2000_0000_0000L),

    /**
     *
     */
    MAXMP(0x4000_0000_0000L),

    /**
     *
     */
    INVINCIBLE(0x8000_0000_0000L),

    /**
     *
     */
    SOULARROW(0x1_0000_0000_0000L),

    /**
     *
     */
    COMBO(0x20_0000_0000_0000L),

    /**
     *
     */
    SUMMON(0x20_0000_0000_0000L), //hack buffstat for summons ^.- (does/should not increase damage... hopefully <3)

    /**
     *
     */
    WK_CHARGE(0x40_0000_0000_0000L),

    /**
     *
     */
    DRAGONBLOOD(0x80_0000_0000_0000L),

    /**
     *
     */
    HOLY_SYMBOL(0x100_0000_0000_0000L),

    /**
     *
     */
    MESOUP(0x200_0000_0000_0000L),

    /**
     *
     */
    SHADOWPARTNER(0x400_0000_0000_0000L),

    /**
     *
     */
    PICKPOCKET(0x800_0000_0000_0000L),

    /**
     *
     */
    PUPPET(0x800_0000_0000_0000L), // HACK - shares buffmask with pickpocket - odin special ^.-

    /**
     *
     */

    能量(0x20_0000_0000L, true),

    /**
     *
     */
    能量获取(0x20_0000_0000L, true),

    /**
     *
     */
    骑宠技能(1_099_511_627_776L),

    /**
     *
     */
    MESOGUARD(0x1000_0000_0000_0000L),
    /**
     * ***<战神技能mask>****
     */
    矛连击强化(4_294_967_296L, 3),//攻击力的mask

    /**
     *
     */
    矛连击强化2(8_589_934_592L, true),

    /**
     *
     */
    矛连击强化防御(8_589_934_592L, true),//防御的mask

    /**
     *
     */
    矛连击强化魔法防御(34_359_738_368L, true),//魔法防御的mask
    //抗压(17592186044416L),//伤害反击的mask 17592186044416

    /**
     *
     */
    连环吸血(18_014_398_509_481_984L),//吸血技能的mask

    /**
     *
     */
    灵巧击退(140_737_488_355_328L), //减少怪物碰撞伤害百分比的mask

    /**
     *
     */
    战神之盾(140_737_488_355_328L),//减少怪物碰撞伤害百分比的mask
    /**
     * ***<无法识别的技能>*****
     */
    //矛连击强化(0x40, 3),
    /**
     * *************************
     */
    WEAKEN(0x4000_0000_0000_0000L),;
    private static final long serialVersionUID = 0L;
    private final long buffstat;
    private final long maskPos;
    private final boolean first;

    private MapleBuffStat(long buffstat) {
        this.buffstat = buffstat;
        this.maskPos = 4L;
        first = false;
    }

    private MapleBuffStat(long buffstat, long maskPos) {
        this.buffstat = buffstat;
        this.maskPos = maskPos;
        this.first = false;
    }

    private MapleBuffStat(long buffstat, boolean first) {
        this.buffstat = buffstat;
        this.maskPos = 4L;
        this.first = first;
    }

    /**
     *
     * @return
     */
    public long getMaskPos() {
        return this.maskPos;
    }

    /**
     *
     * @return
     */
    public final boolean isFirst() {
        return first;
    }

    /**
     *
     * @return
     */
    public final long getValue() {
        return buffstat;
    }

}
