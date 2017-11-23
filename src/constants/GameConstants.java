package constants;

import client.MapleCharacter;
import client.inventory.MapleInventoryType;
import client.inventory.MapleWeaponType;
import client.status.MonsterStatus;
import handling.channel.handler.AttackInfo;
import handling.login.Balloon;
import java.util.*;
import server.MapleStatEffect;
import server.Randomizer;
import server.ServerProperties;
import server.maps.MapleMapObjectType;

/**
 *
 * @author zjj
 */
public class GameConstants {

    /**
     *
     */
    public static final List<MapleMapObjectType> rangedMapobjectTypes = Collections.unmodifiableList(Arrays.asList(
            MapleMapObjectType.ITEM,
            MapleMapObjectType.MONSTER,
            MapleMapObjectType.DOOR,
            MapleMapObjectType.REACTOR,
            MapleMapObjectType.SUMMON,
            MapleMapObjectType.NPC,
            MapleMapObjectType.LOVE,
            MapleMapObjectType.MIST));
    private static final int[] exp = {0, 15, 34, 57, 92, 135, 372, 560, 840, 1_242, 1_716, 2_360, 3_216, 4_200, 5_460, 7_050, 8_840, 11_040, 13_716, 16_680, 20_216, 24_402, 28_980, 34_320, 40_512, 47_216, 54_900, 63_666, 73_080, 83_720, 95_700, 108_480, 122_760, 138_666, 155_540, 174_216, 194_832, 216_600, 240_500, 266_682, 294_216, 324_240, 356_916, 391_160, 428_280, 468_450, 510_420, 555_680, 604_416, 655_200, 709_716, // 51等到這
    748_608, 789_631, 832_902, 878_545, 926_689, 977_471, 1_031_036, 1_087_536, 1_147_132, 1_209_994, 1_276_301, 1_346_242, 1_420_016, 1_497_832, 1_579_913, 1_666_492, 1_757_815, 1_854_143, 1_955_750, 2_062_925, // 71等到這
    2_175_973, 2_295_216, 2_410_993, 2_553_663, 2_693_603, 2_841_212, 2_996_910, 3_161_140, 3_334_370, 3_517_093, 3_709_829, 3_913_127, 4_127_566, 4_353_756, 4_592_341, 4_844_001, 5_109_452, 5_389_449, 5_684_790, 5_996_316, 6_324_914, 6_671_519, 7_037_118, 7_422_752, 7_829_518, 8_258_575, 8_711_144, 9_188_514, 9_692_044, 10_223_168, // 101等到這
    10_783_397, 11_374_327, 11_997_640, 12_655_110, 13_348_610, 14_080_113, 14_851_703, 15_665_576, 16_524_049, 17_429_566, 18_384_706, 19_392_187, 20_454_878, 21_575_805, 22_758_159, 24_005_306, 25_320_796, 26_708_375, 28_171_993, 29_715_818, //121等到這
    31_344_244, 33_061_908, 34_873_700, 36_784_778, 38_800_583, 40_926_854, 43_169_645, 45_535_341, 48_030_677, 50_662_758, //131等到這48030677
    53_439_077, 56_367_538, 59_456_479, 62_714_694, 66_151_459, 69_776_558, 73_600_313, 77_633_610, 81_887_931, 86_375_389, //141等到這
    91_108_760, 96_101_520, 101_367_883, 106_922_842, 112_782_213, 118_962_678, 125_481_832, 132_358_236, 139_611_467, 147_262_175, //151等到這
    155_332_142, 163_844_343, 172_823_012, 182_293_713, 192_283_408, 202_820_538, 213_935_103, 225_658_746, 238_024_845, 251_068_606, //160
    264_827_165, 279_339_693, 294_647_508, 310_794_191, 327_825_712, 345_790_561, 364_739_883, 384_727_628, 405_810_702, 428_049_128, //170
    451_506_220, 476_248_760, 502_347_192, 529_875_818, 558_913_012, 589_541_445, 621_848_316, 655_925_603, 691_870_326, 729_784_819, 769_777_027, 811_960_808, 856_456_260, 903_390_063, 952_895_838, 1_005_114_529, 1_060_194_805, 1_118_293_480, 1_179_575_962, 1_244_216_724, 1_312_399_800, 1_384_319_309, 1_460_180_007, 1_540_197_871, 1_624_600_714, 1_713_628_833, 1_807_535_693, 1_906_588_648, 2_011_069_705, 2_121_276_324};
    private static final int[] closeness = {0, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1_224, 1_642, 2_161, 2_793, 3_557, 4_467, 5_542, 6_801, 8_263, 9_950, 11_882, 14_084, 16_578, 19_391, 22_547, 26_074, 30_000};
    private static final int[] setScore = {0, 10, 100, 300, 600, 1_000, 2_000, 4_000, 7_000, 10_000};
    private static final int[] cumulativeTraitExp = {0, 20, 46, 80, 124, 181, 255, 351, 476, 639, 851, 1_084, 1_340, 1_622, 1_932, 2_273, 2_648, 3_061, 3_515, 4_014, 4_563, 5_128, 5_710, 6_309, 6_926, 7_562, 8_217, 8_892, 9_587, 10_303, 11_040, 11_788, 12_547, 13_307, 14_089, 14_883, 15_689, 16_507, 17_337, 18_179, 19_034, 19_902, 20_783, 21_677, 22_584, 23_505, 24_440, 25_399, 26_362, 27_339, 28_331, 29_338, 30_360, 31_397, 32_450, 33_519, 34_604, 35_705, 36_823, 37_958, 39_110, 40_279, 41_466, 32_671, 43_894, 45_135, 46_395, 47_674, 48_972, 50_289, 51_626, 52_967, 54_312, 55_661, 57_014, 58_371, 59_732, 61_097, 62_466, 63_839, 65_216, 66_597, 67_982, 69_371, 70_764, 72_161, 73_562, 74_967, 76_376, 77_789, 79_206, 80_627, 82_052, 83_481, 84_914, 86_351, 87_792, 89_237, 90_686, 92_139, 93_596, 96_000};
    private static final int[] mountexp = {0, 6, 25, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1_146, 1_394, 1_701, 2_247, 2_543, 2_898, 3_156, 3_313, 3_584, 3_923, 4_150, 4_305, 4_550};

    /**
     *
     */
    public static final int[] itemBlock = {2_340_000, 2_049_100, 4_001_129, 2_040_037, 2_040_006, 2_040_007, 2_040_303, 2_040_403, 2_040_506, 2_040_507, 2_040_603, 2_040_709, 2_040_710, 2_040_711, 2_040_806, 2_040_903, 2_041_024, 2_041_025, 2_043_003, 2_043_103, 2_043_203, 2_043_303, 2_043_703, 2_043_803, 2_044_003, 2_044_103, 2_044_203, 2_044_303, 2_044_403, 2_044_503, 2_044_603, 2_044_908, 2_044_815, 2_044_019, 2_044_703, 1_004_001, 4_007_008, 1_004_002, 5_152_053, 5_150_040};

    /**
     *
     */
    public static final int[] cashBlock = {5_062_000, 5_650_000, 5_431_000, 5_431_001, 5_432_000, 5_450_000, 5_550_000, 5_550_001, 5_640_000, 5_530_013, 5_150_039, 5_150_046, 5_150_054, 1_812_006, 5_650_000, 5_222_000, 5_221_001, 5_220_014, 5_220_015, 5_420_007, 5_451_000, 5_210_000, 5_210_001, 5_210_002, 5_210_003, 5_210_004, 5_210_005, 5_210_006, 5_210_007, 5_210_008, 5_210_009, 5_210_010, 5_210_011, 5_211_000, 5_211_001, 5_211_002, 5_211_003, 5_211_004, 5_211_005, 5_211_006, 5_211_007, 5_211_008, 5_211_009, 5_211_010, 5_211_011, 5_211_012, 5_211_013, 5_211_014, 5_211_015, 5_211_016, 5_211_017, 5_211_018, 5_211_019, 5_211_020, 5_211_021, 5_211_022, 5_211_023, 5_211_024, 5_211_025, 5_211_026, 5_211_027, 5_211_028, 5_211_029, 5_211_030, 5_211_031, 5_211_032, 5_211_033, 5_211_034, 5_211_035, 5_211_036, 5_211_037, 5_211_038, 5_211_039, 5_211_040, 5_211_041, 5_211_042, 5_211_043, 5_211_044, 5_211_045, 5_211_046, 5_211_047, 5_211_048, 5_211_049, 5_211_050, 5_211_051, 5_211_052, 5_211_053, 5_211_054, 5_211_055, 5_211_056, 5_211_057, 5_211_058, 5_211_059, 5_211_060, 5_211_061, //2x exp
    5_360_000, 5_360_001, 5_360_002, 5_360_003, 5_360_004, 5_360_005, 5_360_006, 5_360_007, 5_360_008, 5_360_009, 5_360_010, 5_360_011, 5_360_012, 5_360_013, 5_360_014, 5_360_017, 5_360_050, 5_211_050, 5_360_042, 5_360_052, 5_360_053, 5_360_050, //2x drop
    1_112_810, 1_112_811, 5_530_013, 4_001_431, 4_001_432, 4_032_605, 5_270_000, 5_270_001, 5_270_002, 5_270_003, 5_270_004, 5_270_005, 5_270_006, //2x meso
    9_102_328, 9_102_329, 9_102_330, 9_102_331, 9_102_332, 9_102_333}; //miracle cube and stuff

    /**
     *
     */
    public static final int OMOK_SCORE = 122_200;

    /**
     *
     */
    public static final int MATCH_SCORE = 122_210;

    /**
     *
     */
    public static final int[] blockedSkills = {4_341_003};

    /**
     *
     */
    public static final String MASTER = "%&HYGEomgLOL";

    /**
     *
     */
    public static final String[] RESERVED = {"Rental"};
    private static final int[] mobHpVal = {0, 15, 20, 25, 35, 50, 65, 80, 95, 110, 125, 150, 175, 200, 225, 250, 275, 300, 325, 350, 375, 405, 435, 465, 495, 525, 580, 650, 720, 790, 900, 990, 1_100, 1_200, 1_300, 1_400, 1_500, 1_600, 1_700, 1_800, 1_900, 2_000, 2_100, 2_200, 2_300, 2_400, 2_520, 2_640, 2_760, 2_880, 3_000, 3_200, 3_400, 3_600, 3_800, 4_000, 4_300, 4_600, 4_900, 5_200, 5_500, 5_900, 6_300, 6_700, 7_100, 7_500, 8_000, 8_500, 9_000, 9_500, 10_000, 11_000, 12_000, 13_000, 14_000, 15_000, 17_000, 19_000, 21_000, 23_000, 25_000, 27_000, 29_000, 31_000, 33_000, 35_000, 37_000, 39_000, 41_000, 43_000, 45_000, 47_000, 49_000, 51_000, 53_000, 55_000, 57_000, 59_000, 61_000, 63_000, 65_000, 67_000, 69_000, 71_000, 73_000, 75_000, 77_000, 79_000, 81_000, 83_000, 85_000, 89_000, 91_000, 93_000, 95_000, 97_000, 99_000, 101_000, 103_000, 105_000, 107_000, 109_000, 111_000, 113_000, 115_000, 118_000, 120_000, 125_000, 130_000, 135_000, 140_000, 145_000, 150_000, 155_000, 160_000, 165_000, 170_000, 175_000, 180_000, 185_000, 190_000, 195_000, 200_000, 205_000, 210_000, 215_000, 220_000, 225_000, 230_000, 235_000, 240_000, 250_000, 260_000, 270_000, 280_000, 290_000, 300_000, 310_000, 320_000, 330_000, 340_000, 350_000, 360_000, 370_000, 380_000, 390_000, 400_000, 410_000, 420_000, 430_000, 440_000, 450_000, 460_000, 470_000, 480_000, 490_000, 500_000, 510_000, 520_000, 530_000, 550_000, 570_000, 590_000, 610_000, 630_000, 650_000, 670_000, 690_000, 710_000, 730_000, 750_000, 770_000, 790_000, 810_000, 830_000, 850_000, 870_000, 890_000, 910_000};

    /**
     *
     */
    public static int 单机IP = 1;

    /**
     *
     */
    public static final int 每日签到系统_签到记录 = 7;

    /**
     *
     */
    public static final int 每日签到系统_当前时间 = 9;

    /**
     *
     * @param level
     * @return
     */
    public static int getExpNeededForLevel(final int level) {
        if (level < 0 || level >= exp.length) {
            return Integer.MAX_VALUE;
        }
        return exp[level];
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getClosenessNeededForLevel(final int level) {
        return closeness[level - 1];
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getMountExpNeededForLevel(final int level) {
        return mountexp[level - 1];
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getBookLevel(final int level) {
        return (int) ((5 * level) * (level + 1));
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getTimelessRequiredEXP(final int level) {
        return 70 + (level * 10);
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getReverseRequiredEXP(final int level) {
        return 60 + (level * 5);
    }

    /**
     *
     * @return
     */
    public static int maxViewRangeSq() {
        return 100_000_000; // 800 * 800
        // return 800000; // 800 * 800
    }

    /**
     *
     * @param baseJob
     * @param currentJob
     * @return
     */
    public static boolean isJobFamily(final int baseJob, final int currentJob) {
        return currentJob >= baseJob && currentJob / 100 == baseJob / 100;
    }

    /**
     *
     * @param job
     * @return
     */
    public static boolean isKOC(final int job) {
        return job >= 1_000 && job < 2_000;
    }

    /**
     *
     * @param job
     * @return
     */
    public static boolean isEvan(final int job) {
        return job == 2_001 || (job >= 2_200 && job <= 2_218);
    }

    /**
     *
     * @param job
     * @return
     */
    public static boolean isAran(final int job) {
        return job >= 2_000 && job <= 2_112 && job != 2_001;
    }

    /**
     *
     * @param job
     * @return
     */
    public static boolean isResist(final int job) {
        return job >= 3_000 && job <= 3_512;
    }

    /**
     *
     * @param job
     * @return
     */
    public static boolean isAdventurer(final int job) {
        return job >= 0 && job < 1_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isRecoveryIncSkill(final int id) {
        switch (id) {
            case 1_110_000:
            case 2_000_000:
            case 1_210_000:
            case 11_110_000:
            case 4_100_002:
            case 4_200_001:
                return true;
        }
        return false;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isLinkedAranSkill(final int id) {
        return getLinkedAranSkill(id) != id;
    }

    /**
     *
     * @param s
     * @return
     */
    public static int getMountS(int s) {
        // int s = Randomizer.nextInt(316);
        switch (s) {
            case 1:
                return 1_932_003;
            case 2:
                return 1_932_004;
            case 3:
                return 1_932_005;
            case 4:
                return 1_932_006;
            case 5:
                return 1_932_007;
            case 6:
                return 1_932_008;
            case 7:
                return 1_932_009;
            case 8:
                return 1_932_010;
            case 9:
                return 1_932_011;
            case 10:
                return 1_932_012;
            case 11:
                return 1_932_013;
            case 12:
                return 1_932_014;
            case 13:
                return 1_932_015;
            case 14:
                return 1_932_016;
            case 15:
                return 1_932_017;
            case 16:
                return 1_932_018;
            case 17:
                return 1_932_020;
            case 18:
                return 1_932_021;
            case 19:
                return 1_932_022;
            case 20:
                return 1_932_023;
            case 21:
                return 1_932_025;
            case 22:
                return 1_932_026;
            case 23:
                return 1_932_027;
            case 24:
                return 1_932_028;
            case 25:
                return 1_932_029;
            case 26:
                return 1_932_030;
            case 27:
                return 1_932_031;
            case 28:
                return 1_932_032;
            case 29:
                return 1_932_033;
            case 30:
                return 1_932_034;
            case 31:
                return 1_932_035;
            case 32:
                return 1_932_036;
            case 33:
                return 1_932_038;
            case 34:
                return 1_932_041;
            case 35:
                return 1_932_043;
            case 36:
                return 1_932_044;
            case 37:
                return 1_932_045;
            case 38:
                return 1_932_046;
            case 39:
                return 1_932_047;
            case 40:
                return 1_932_048;
            case 41:
                return 1_932_049;
            case 42:
                return 1_932_050;
            case 43:
                return 1_932_051;
            case 44:
                return 1_932_052;
            case 45:
                return 1_932_053;
            case 46:
                return 1_932_054;
            case 47:
                return 1_932_055;
            case 48:
                return 1_932_056;
            case 49:
                return 1_932_057;
            case 50:
                return 1_932_058;
            case 51:
                return 1_932_059;
            case 52:
                return 1_932_060;
            case 53:
                return 1_932_061;
            case 54:
                return 1_932_062;
            case 55:
                return 1_932_063;
            case 56:
                return 1_932_064;
            case 57:
                return 1_932_065;
            case 58:
                return 1_932_066;
            case 59:
                return 1_932_071;
            case 60:
                return 1_932_072;
            case 61:
                return 1_932_078;
            case 62:
                return 1_932_080;
            case 63:
                return 1_932_081;
            case 64:
                return 1_932_083;
            case 65:
                return 1_932_084;
            case 66:
                return 1_932_001;
            case 67:
                return 1_932_086;
            case 68:
                return 1_932_087;
            case 69:
                return 1_932_088;
            case 70:
                return 1_932_089;
            case 71:
                return 1_932_090;
            case 72:
                return 1_932_091;
            case 73:
                return 1_932_092;
            case 74:
                return 1_932_093;
            case 75:
                return 1_932_094;
            case 76:
                return 1_932_095;
            case 77:
                return 1_932_096;
            case 78:
                return 1_932_097;
            case 79:
                return 1_932_098;
            case 80:
                return 1_932_099;
            case 81:
                return 1_932_100;
            case 82:
                return 1_932_102;
            case 83:
                return 1_932_103;
            case 84:
                return 1_932_105;
            case 85:
                return 1_932_106;
            case 86:
                return 1_932_107;
            case 87:
                return 1_932_108;
            case 88:
                return 1_932_109;
            case 89:
                return 1_932_110;
            case 90:
                return 1_932_112;
            case 91:
                return 1_932_113;
            case 92:
                return 1_932_114;
            case 93:
                return 1_932_115;
            case 94:
                return 1_932_116;
            case 95:
                return 1_932_117;
            case 96:
                return 1_932_118;
            case 97:
                return 1_932_119;
            case 98:
                return 1_932_120;
            case 99:
                return 1_932_121;
            case 100:
                return 1_932_122;
            case 101:
                return 1_932_123;
            case 102:
                return 1_932_124;
            case 103:
                return 1_932_126;
            case 104:
                return 1_932_127;
            case 105:
                return 1_932_128;
            case 106:
                return 1_932_129;
            case 107:
                return 1_932_130;
            case 108:
                return 1_932_131;
            case 109:
                return 1_932_132;
            case 110:
                return 1_932_133;
            case 111:
                return 1_932_134;
            case 112:
                return 1_932_135;
            case 113:
                return 1_932_136;
            case 114:
                return 1_932_137;
            case 115:
                return 1_932_138;
            case 116:
                return 1_932_139;
            case 117:
                return 1_932_140;
            case 118:
                return 1_932_141;
            case 119:
                return 1_932_142;
            case 120:
                return 1_932_143;
            case 121:
                return 1_932_144;
            case 122:
                return 1_932_145;
            case 123:
                return 1_932_146;
            case 124:
                return 1_932_147;
            case 125:
                return 1_932_148;
            case 126:
                return 1_932_149;
            case 127:
                return 1_932_150;
            case 128:
                return 1_932_151;
            case 129:
                return 1_932_152;
            case 130:
                return 1_932_153;
            case 131:
                return 1_932_154;
            case 132:
                return 1_932_155;
            case 133:
                return 1_932_156;
            case 134:
                return 1_932_157;
            case 135:
                return 1_932_158;
            case 136:
                return 1_932_159;
            case 137:
                return 1_932_002;
            case 138:
                return 1_932_161;
            case 139:
                return 1_932_162;
            case 140:
                return 1_932_163;
            case 141:
                return 1_932_164;
            case 142:
                return 1_932_165;
            case 143:
                return 1_932_166;
            case 144:
                return 1_932_167;
            case 145:
                return 1_932_168;
            case 146:
                return 1_932_169;
            case 147:
                return 1_932_170;
            case 148:
                return 1_932_171;
            case 149:
                return 1_932_172;
            case 150:
                return 1_932_173;
            case 151:
                return 1_932_174;
            case 152:
                return 1_932_175;
            case 153:
                return 1_932_176;
            case 154:
                return 1_932_177;
            case 155:
                return 1_932_178;
            case 156:
                return 1_932_179;
            case 157:
                return 1_932_180;
            case 158:
                return 1_932_181;
            case 159:
                return 1_932_182;
            case 160:
                return 1_932_183;
            case 161:
                return 1_932_184;
            case 162:
                return 1_932_185;
            case 163:
                return 1_932_186;
            case 164:
                return 1_932_187;
            case 165:
                return 1_932_188;
            case 166:
                return 1_932_189;
            case 167:
                return 1_932_190;
            case 168:
                return 1_932_191;
            case 169:
                return 1_932_192;
            case 170:
                return 1_932_193;
            case 171:
                return 1_932_194;
            case 172:
                return 1_932_195;
            case 173:
                return 1_932_196;
            case 174:
                return 1_932_197;
            case 175:
                return 1_932_198;
            case 176:
                return 1_932_199;
            case 177:
                return 1_932_200;
            case 178:
                return 1_932_201;
            case 179:
                return 1_932_202;
            case 180:
                return 1_932_203;
            case 181:
                return 1_932_204;
            case 182:
                return 1_932_205;
            case 183:
                return 1_932_206;
            case 184:
                return 1_932_207;
            case 185:
                return 1_932_208;
            case 186:
                return 1_932_211;
            case 187:
                return 1_932_212;
            case 188:
                return 1_932_213;
            case 189:
                return 1_932_214;
            case 190:
                return 1_932_215;
            case 191:
                return 1_932_216;
            case 192:
                return 1_932_217;
            case 193:
                return 1_932_218;
            case 194:
                return 1_932_219;
            case 195:
                return 1_932_220;
            case 196:
                return 1_932_221;
            case 197:
                return 1_932_222;
            case 198:
                return 1_932_223;
            case 199:
                return 1_932_224;
            case 200:
                return 1_932_225;
            case 201:
                return 1_932_226;
            case 202:
                return 1_932_227;
            case 203:
                return 1_932_228;
            case 204:
                return 1_932_230;
            case 205:
                return 1_932_231;
            case 206:
                return 1_932_232;
            case 207:
                return 1_932_234;
            case 208:
                return 1_932_235;
            case 209:
                return 1_932_236;
            case 210:
                return 1_932_237;
            case 211:
                return 1_932_238;
            case 212:
                return 1_932_239;
            case 213:
                return 1_932_240;
            case 214:
                return 1_932_241;
            case 215:
                return 1_932_242;
            case 216:
                return 1_932_243;
            case 217:
                return 1_932_244;
            case 218:
                return 1_932_245;
            case 219:
                return 1_932_246;
            case 220:
                return 1_932_247;
            case 221:
                return 1_932_248;
            case 222:
                return 1_932_249;
            case 223:
                return 1_932_250;
            case 224:
                return 1_932_251;
            case 225:
                return 1_932_252;
            case 226:
                return 1_932_253;
            case 227:
                return 1_932_254;
            case 228:
                return 1_932_255;
            case 229:
                return 1_932_256;
            case 230:
                return 1_932_258;
            case 231:
                return 1_932_259;
            case 232:
                return 1_932_260;
            case 233:
                return 1_932_261;
            case 234:
                return 1_932_262;
            case 235:
                return 1_932_263;
            case 236:
                return 1_932_264;
            case 237:
                return 1_932_265;
            case 238:
                return 1_932_266;
            case 239:
                return 1_932_267;
            case 240:
                return 1_932_268;
            case 241:
                return 1_932_269;
            case 242:
                return 1_932_270;
            case 243:
                return 1_932_271;
            case 244:
                return 1_932_272;
            case 245:
                return 1_932_273;
            case 246:
                return 1_932_274;
            case 247:
                return 1_932_275;
            case 248:
                return 1_932_276;
            case 249:
                return 1_932_277;
            case 250:
                return 1_932_279;
            case 251:
                return 1_932_280;
            case 252:
                return 1_932_281;
            case 253:
                return 1_932_282;
            case 254:
                return 1_932_286;
            case 255:
                return 1_932_287;
            case 256:
                return 1_932_288;
            case 257:
                return 1_932_289;
            case 258:
                return 1_932_290;
            case 259:
                return 1_932_291;
            case 260:
                return 1_932_292;
            case 261:
                return 1_932_293;
            case 262:
                return 1_932_294;
            case 263:
                return 1_932_295;
            case 264:
                return 1_932_296;
            case 265:
                return 1_932_297;
            case 266:
                return 1_932_298;
            case 267:
                return 1_932_299;
            case 268:
                return 1_932_300;
            case 269:
                return 1_932_301;
            case 270:
                return 1_932_302;
            case 271:
                return 1_932_303;
            case 272:
                return 1_932_304;
            case 273:
                return 1_932_305;
            case 274:
                return 1_932_306;
            case 275:
                return 1_932_307;
            case 276:
                return 1_932_308;
            case 277:
                return 1_932_310;
            case 278:
                return 1_932_311;
            case 279:
                return 1_932_313;
            case 280:
                return 1_932_314;
            case 281:
                return 1_932_315;
            case 282:
                return 1_932_316;
            case 283:
                return 1_932_317;
            case 284:
                return 1_932_318;
            case 285:
                return 1_932_319;
            case 286:
                return 1_932_320;
            case 287:
                return 1_932_321;
            case 288:
                return 1_932_322;
            case 289:
                return 1_932_323;
            case 290:
                return 1_932_324;
            case 291:
                return 1_932_325;
            case 292:
                return 1_932_326;
            case 293:
                return 1_932_327;
            case 294:
                return 1_932_329;
            case 295:
                return 1_932_330;
            case 296:
                return 1_932_332;
            case 297:
                return 1_932_334;
            case 298:
                return 1_932_335;
            case 299:
                return 1_932_336;
            case 300:
                return 1_932_337;
            case 301:
                return 1_932_338;
            case 302:
                return 1_932_339;
            case 303:
                return 1_932_341;
            case 304:
                return 1_932_342;
            case 305:
                return 1_932_350;
            case 306:
                return 1_932_351;
            case 307:
                return 1_932_352;
            case 308:
                return 1_932_353;
            case 309:
                return 1_932_355;
            case 310:
                return 1_932_366;
            case 311:
                return 1_939_000;
            case 312:
                return 1_939_001;
            case 313:
                return 1_939_002;
            case 314:
                return 1_939_003;
            case 315:
                return 1_939_004;
            case 316:
                return 1_939_005;
            case 317:
                return 1_932_374;
            case 318:
                return 1_932_376;
            case 319:
                return 1_932_378;
            case 320:
                return 1_939_006;
            default:
                return 1_930_001;
        }

    }

    /**
     *
     * @param id
     * @return
     */
    public static int getLinkedAranSkill(final int id) {
        switch (id) {
            case 21_110_007:
            case 21_110_008:
                return 21_110_002;
            case 21_120_009:
            case 21_120_010:
                return 21_120_002;
            case 4_321_001:
                return 4_321_000;
            case 33_101_006:
            case 33_101_007:
                return 33_101_005;
            case 33_101_008:
                return 33_101_004;
            case 35_101_009:
            case 35_101_010:
                return 35_100_008;
            case 35_111_009:
            case 35_111_010:
                return 35_111_001;
        }
        return id;
    }

    /**
     *
     * @param job
     * @return
     */
    public static int getBOF_ForJob(final int job) {
        if (isAdventurer(job)) {
            return 12;
        } else if (isKOC(job)) {
            return 10_000_012;
        } else if (isResist(job)) {
            return 30_000_012;
        } else if (isEvan(job)) {
            return 20_010_012;
        }
        return 20_000_012;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean isElementAmp_Skill(final int skill) {
        switch (skill) {
            case 2_110_001:
            case 2_210_001:
            case 12_110_001:
            case 22_150_000:
                return true;
        }
        return false;
    }

    /**
     *
     * @param job
     * @return
     */
    public static int getMPEaterForJob(final int job) {
        switch (job) {
            case 210:
            case 211:
            case 212:
                return 2_100_000;
            case 220:
            case 221:
            case 222:
                return 2_200_000;
            case 230:
            case 231:
            case 232:
                return 2_300_000;
        }
        return 2_100_000; // Default, in case GM
    }

    /**
     *
     * @param job
     * @return
     */
    public static int getJobShortValue(int job) {
        if (job >= 1_000) {
            job -= (job / 1_000) * 1_000;
        }
        job /= 100;
        switch (job) {
            case 4:
                // For some reason dagger/ claw is 8.. IDK
                job *= 2;
                break;
            case 3:
                job += 1;
                break;
            case 5:
                job += 11; // 16
                break;
            default:
                break;
        }
        return job;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean isPyramidSkill(final int skill) {
        switch (skill) {
            case 1_020:
            case 10_001_020:
            case 20_001_020:
            case 20_011_020:
            case 30_001_020:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean isMulungSkill(final int skill) {
        switch (skill) {
            case 1_009:
            case 1_010:
            case 1_011:
            case 10_001_009:
            case 10_001_010:
            case 10_001_011:
            case 20_001_009:
            case 20_001_010:
            case 20_001_011:
            case 20_011_009:
            case 20_011_010:
            case 20_011_011:
            case 30_001_009:
            case 30_001_010:
            case 30_001_011:
                return true;
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isThrowingStar(final int itemId) {
        return itemId / 10_000 == 207;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isBullet(final int itemId) {
        return itemId / 10_000 == 233;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isRechargable(final int itemId) {
        return isThrowingStar(itemId) || isBullet(itemId);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isOverall(final int itemId) {
        return itemId / 10_000 == 105;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isPet(final int itemId) {
        return itemId / 10_000 == 500;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isArrowForCrossBow(final int itemId) {
        return itemId >= 2_061_000 && itemId < 2_062_000;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isArrowForBow(final int itemId) {
        return itemId >= 2_060_000 && itemId < 2_061_000;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isMagicWeapon(final int itemId) {
        final int s = itemId / 10_000;
        return s == 137 || s == 138;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isWeapon(final int itemId) {
        return itemId >= 1_300_000 && itemId < 1_500_000;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static MapleInventoryType getInventoryType(final int itemId) {
        final byte type = (byte) (itemId / 1_000_000);
        if (type < 1 || type > 5) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static MapleWeaponType getWeaponType(final int itemId) {
        int cat = itemId / 10_000;
        cat = cat % 100;
        switch (cat) {
            case 30:
                return MapleWeaponType.SWORD1H;
            case 31:
                return MapleWeaponType.AXE1H;
            case 32:
                return MapleWeaponType.BLUNT1H;
            case 33:
                return MapleWeaponType.DAGGER;
            case 34:
                return MapleWeaponType.KATARA;
            case 37:
                return MapleWeaponType.WAND;
            case 38:
                return MapleWeaponType.STAFF;
            case 40:
                return MapleWeaponType.SWORD2H;
            case 41:
                return MapleWeaponType.AXE2H;
            case 42:
                return MapleWeaponType.BLUNT2H;
            case 43:
                return MapleWeaponType.SPEAR;
            case 44:
                return MapleWeaponType.POLE_ARM;
            case 45:
                return MapleWeaponType.BOW;
            case 46:
                return MapleWeaponType.CROSSBOW;
            case 47:
                return MapleWeaponType.CLAW;
            case 48:
                return MapleWeaponType.KNUCKLE;
            case 49:
                return MapleWeaponType.GUN;
        }
        return MapleWeaponType.NOT_A_WEAPON;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isShield(final int itemId) {
        int cat = itemId / 10_000;
        cat = cat % 100;
        return cat == 9;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isEquip(final int itemId) {
        return itemId / 1_000_000 == 1;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is白医卷轴(int itemId) {
        return itemId / 100 == 20_490;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is配饰卷轴(int itemId) {
        return itemId / 100 == 20_492;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is枫叶卷轴(int itemId) {
        if ((itemId >= 2_049_105) && (itemId <= 2_049_110)) {
            return false;
        }
        return (itemId / 100 == 20_491) || (itemId == 2_040_126);
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean is强化卷轴(int scrollId) {
        return scrollId / 100 == 20_493;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean is潜能卷轴(int scrollId) {
        return (scrollId / 100 == 20_494) || (scrollId / 100 == 20_497) || (scrollId == 5_534_000) || (scrollId == 2_049_405);
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean is特殊卷轴(int scrollId) {
        switch (scrollId) {
            case 2_040_727:
            case 2_041_058:
            case 2_530_000:
            case 2_530_001:
            case 2_531_000:
            case 5_063_100:
            case 5_064_000:
            case 5_064_100:
            case 5_064_200:
            case 5_064_300:
                return true;
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isCleanSlate(int itemId) {
        return itemId / 100 == 20_490;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isAccessoryScroll(int itemId) {
        return itemId / 100 == 20_492;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isChaosScroll(int itemId) {
        if (itemId >= 2_049_105 && itemId <= 2_049_110) {
            return false;
        }
        return itemId / 100 == 20_491;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static int getChaosNumber(int itemId) {
        return itemId == 2_049_116 ? 10 : 5;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isEquipScroll(int scrollId) {
        return scrollId / 100 == 20_493;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20_494;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isAzwanScroll(int scrollId) {
        //return MapleItemInformationProvider.getInstance().getEquipStats(scroll.getItemId()).containsKey("tuc");
        //should add this ^ too.
        return scrollId >= 2_046_060 && scrollId <= 2_046_069 || scrollId >= 2_046_141 && scrollId <= 2_046_145 || scrollId >= 2_046_519 && scrollId <= 2_046_530 || scrollId >= 2_046_701 && scrollId <= 2_046_712;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isSpecialScroll(final int scrollId) {
        switch (scrollId) {
            case 2_040_727: // Spikes on show
            case 2_041_058: // Cape for Cold protection
                return true;
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isTwoHanded(final int itemId) {
        switch (getWeaponType(itemId)) {
            case AXE2H:
            case GUN:
            case KNUCKLE:
            case BLUNT2H:
            case BOW:
            case CLAW:
            case CROSSBOW:
            case POLE_ARM:
            case SPEAR:
            case SWORD2H:
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isTyrant(final int itemId) {
        switch (itemId) {
            //Boots
            case 1_072_743:
            case 1_072_744:
            case 1_072_745:
            case 1_072_746:
            case 1_072_747:
            //Capes    
            case 1_102_481:
            case 1_102_482:
            case 1_102_483:
            case 1_102_484:
            case 1_102_485:
            //Belts
            case 1_132_174:
            case 1_132_175:
            case 1_132_176:
            case 1_132_177:
            case 1_132_178:
                //   case 1082543: Warrior Gloves
                //   case 1082544: Mage Gloves
                //   case 1082545: Bowman Gloves
                //   case 1082546: Thief Gloves
                //   case 1082547: Pirate Gloves       
                //Gloves Are not in 144.3 so they're commented out
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isNovaGear(final int itemId) {
        switch (itemId) {
            //Boots
            case 1_072_737: // Nova Hyades Boots
            case 1_072_738: // Nova Hermes Boots
            case 1_072_739: // Nova Charon Boots
            case 1_072_740: // Nova Lycaon Boots
            case 1_072_741: // Nova Altair Boots

            //Cape
            case 1_102_476: // Nova Hyades Cloak
            case 1_102_477: // Nova Hermes Cloak
            case 1_102_478: // Nova Charon Cloak
            case 1_102_479: // Nova Lycaon Cloak
            case 1_102_480: // Nova Altair Cloak

            //Belt
            case 1_132_169: // Nova Hyades Belt
            case 1_132_170: // Nova Hermes Belt
            case 1_132_171: // Nova Charon Belt
            case 1_132_172: // Nova Lycaon Belt
            case 1_132_173: // Nova Altair Belt
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean isSpecialShield(final int itemid) {
        return itemid / 1_000 == 1_098 || itemid / 1_000 == 1_099 || itemid / 10_000 == 135;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isTownScroll(final int id) {
        return id >= 2_030_000 && id < 2_040_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isUpgradeScroll(final int id) {
        return id >= 2_040_000 && id < 2_050_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isGun(final int id) {
        return id >= 1_492_000 && id < 1_500_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isUse(final int id) {
        return id >= 2_000_000 && id < 3_000_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isSummonSack(final int id) {
        return id / 10_000 == 210;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isMonsterCard(final int id) {
        return id / 10_000 == 238;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isSpecialCard(final int id) {
        return id / 1_000 >= 2_388;
    }

    /**
     *
     * @param id
     * @return
     */
    public static int getCardShortId(final int id) {
        return id % 10_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isGem(final int id) {
        return id >= 4_250_000 && id <= 4_251_402;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isOtherGem(final int id) {
        switch (id) {
            case 4_001_174:
            case 4_001_175:
            case 4_001_176:
            case 4_001_177:
            case 4_001_178:
            case 4_001_179:
            case 4_001_180:
            case 4_001_181:
            case 4_001_182:
            case 4_001_183:
            case 4_001_184:
            case 4_001_185:
            case 4_001_186:
            case 4_031_980:
            case 2_041_058:
            case 2_040_727:
            case 1_032_062:
            case 4_032_334:
            case 4_032_312:
            case 1_142_156:
            case 1_142_157:
                return true; //mostly quest items
        }
        return false;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isCustomQuest(final int id) {
        return id > 99_999;
    }

    /**
     *
     * @param meso
     * @return
     */
    public static int getTaxAmount(final int meso) {
        if (meso >= 100_000_000) {
            return (int) Math.round(0.06 * meso);
        } else if (meso >= 25_000_000) {
            return (int) Math.round(0.05 * meso);
        } else if (meso >= 10_000_000) {
            return (int) Math.round(0.04 * meso);
        } else if (meso >= 5_000_000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 1_000_000) {
            return (int) Math.round(0.018 * meso);
        } else if (meso >= 100_000) {
            return (int) Math.round(0.008 * meso);
        }
        return 0;
    }

    /**
     *
     * @param meso
     * @return
     */
    public static int EntrustedStoreTax(final int meso) {
        if (meso >= 100_000_000) {
            return (int) Math.round(0.03 * meso);
        } else if (meso >= 25_000_000) {
            return (int) Math.round(0.025 * meso);
        } else if (meso >= 10_000_000) {
            return (int) Math.round(0.02 * meso);
        } else if (meso >= 5_000_000) {
            return (int) Math.round(0.015 * meso);
        } else if (meso >= 1_000_000) {
            return (int) Math.round(0.009 * meso);
        } else if (meso >= 100_000) {
            return (int) Math.round(0.004 * meso);
        }
        return 0;
    }

    /**
     *
     * @param id
     * @return
     */
    public static short getSummonAttackDelay(final int id) {
        switch (id) {
            case 15_001_004: // Lightning
            case 14_001_005: // Darkness
            case 13_001_004: // Storm
            case 12_001_004: // Flame
            case 11_001_004: // Soul
            case 3_221_005: // Freezer
            case 3_211_005: // Golden Eagle
            case 3_121_006: // Phoenix
            case 3_111_005: // Silver Hawk
            case 2_321_003: // Bahamut
            case 2_311_006: // Summon Dragon
            case 2_221_005: // Infrit
            case 2_121_005: // Elquines
                return 3_030;
            case 5_211_001: // Octopus
            case 5_211_002: // Gaviota
            case 5_220_002: // Support Octopus
                return 1_530;
            case 3_211_002: // Puppet
            case 3_111_002: // Puppet
            case 1_321_007: // Beholder
                return 0;
        }
        return 0;
    }

    /**
     *
     * @param id
     * @return
     */
    public static short getAttackDelay(final int id) {
        switch (id) { // Assume it's faster(2)
            case 4_321_001: //tornado spin
                return 40; //reason being you can spam with final assaulter
            case 3_121_004: // Storm of Arrow
            case 33_121_009:
            case 13_111_002: // Storm of Arrow
            case 5_221_004: // Rapidfire
            case 4_221_001: //Assassinate?
            case 5_201_006: // Recoil shot/ Back stab shot
                return 120;
            case 13_101_005: // Storm Break
                return 360;
            case 5_001_003: // Double Fire
            case 2_301_002: // Heal
                return 390;
            case 5_001_001: // Straight/ Flash Fist
            case 15_001_001: // Straight/ Flash Fist
            case 1_321_003: // Rush
            case 1_221_007: // Rush
            case 1_121_006: // Rush
                return 450;
            case 5_211_004: // Flamethrower
            case 5_211_005: // Ice Splitter
            case 4_201_005: // Savage blow
                return 480;
            case 0: // Normal Attack, TODO delay for each weapon type
            case 5_111_002: // Energy Blast
            case 15_101_005: // Energy Blast
            case 1_001_004: // Power Strike
            case 11_001_002: // Power Strike
            case 1_001_005: // Slash Blast
            case 11_001_003: // Slash Blast
            case 1_311_005: // Sacrifice
                return 570;
            //case 2101004: // Fire Arrow
            case 12_101_002: // Fire Arrow
            case 2_101_005: // Poison Breath
            case 2_121_003: // Fire Demon
            case 2_221_003: // Ice Demon
            case 2_121_006: // Paralyze
            case 3_111_006: // Strafe
            case 311_004: // Arrow Rain
            case 13_111_000: // Arrow Rain
            case 3_111_003: // Inferno
            case 3_101_005: // Arrow Bomb
            case 4_001_344: // Lucky Seven
            case 14_001_004: // Lucky seven
            case 4_121_007: // Triple Throw
            case 14_111_005: // Triple Throw
            case 4_111_004: // Shadow Meso
            case 4_101_005: // Drain
            case 4_211_004: // Band of Thieves
            case 4_201_004: // Steal
            case 4_001_334: // Double Stab
            case 5_221_007: // Battleship Cannon
            case 1_211_002: // Charged blow
            case 1_311_003: // Dragon Fury : Spear
            case 1_311_004: // Dragon Fury : Pole Arm
            case 3_211_006: // Strafe
            case 3_211_004: // Arrow Eruption
            case 3_211_003: // Blizzard Arrow
            case 3_201_005: // Iron Arrow
            case 3_221_001: // Piercing
            case 4_111_005: // Avenger
            case 14_111_002: // Avenger
            case 5_201_001: // Invisible shot
            case 5_101_004: // Corkscrew Blow
            case 15_101_003: // Corkscrew Blow
            case 1_121_008: // Brandish
            case 11_111_004: // Brandish
            case 1_221_009: // Blast
                return 600;
            case 5_201_004: // Blank Shot/ Fake shot
            case 5_211_000: // Burst Fire/ Triple Fire
            case 5_001_002: // Sommersault Kick
            case 15_001_002: // Sommersault Kick
            case 4_221_007: // Boomerang Stab
            case 1_311_001: // Spear Crusher, 16~30 pts = 810
            case 1_311_002: // PA Crusher, 16~30 pts = 810
            case 2_221_006: // Chain Lightning
                return 660;
            case 4_121_008: // Ninja Storm
            case 5_211_006: // Homing Beacon
            case 5_221_008: // Battleship Torpedo
            case 5_101_002: // Backspin Blow
            case 2_001_005: // Magic Claw
            case 12_001_003: // Magic Claw
            case 2_001_004: // Energy Bolt
            case 2_301_005: // Holy Arrow
            case 2_121_001: // Big Bang
            case 2_221_001: // Big Bang
            case 2_321_001: // Big Bang
            case 2_321_007: // Angel's Ray
            case 2_201_005: // Thunderbolt
            case 2_201_004: // Cold Beam
            case 2_211_002: // Ice Strike
            case 4_211_006: // Meso Explosion
            case 5_121_005: // Snatch
            case 12_111_006: // Fire Strike
            case 11_101_004: // Soul Blade
                return 750;
            case 15_111_007: // Shark Wave
            case 2_111_006: // Elemental Composition
            case 2_211_006: // Elemental Composition
                return 810;
            case 13_111_006: // Wind Piercing
            case 4_211_002: // Assaulter
            case 5_101_003: // Double Uppercut
            case 2_111_002: // Explosion
                return 900;
            case 5_121_003: // Energy Orb
            case 2_311_004: // Shining Ray
                return 930;
            case 13_111_007: // Wind Shot
                return 960;
            case 14_101_006: // Vampire
            case 4_121_003: // Showdown
            case 4_221_003: // Showdown
                return 1_020;
            case 12_101_006: // Fire Pillar
                return 1_050;
            case 5_121_001: // Dragon Strike
                return 1_060;
            case 2_211_003: // Thunder Spear
            case 1_311_006: // Dragon Roar
                return 1_140;
            case 11_111_006: // Soul Driver
                return 1_230;
            case 12_111_005: // Flame Gear
                return 1_260;
            case 2_111_003: // Poison Mist
                return 1_320;
            case 5_111_006: // Shockwave
            case 15_111_003: // Shockwave
                return 1_500;
            case 5_121_007: // Barrage
            case 15_111_004: // Barrage
                return 1_830;
            case 5_221_003: // Ariel Strike
            case 5_121_004: // Demolition
                return 2_160;
            case 2_321_008: // Genesis
                return 2_700;
            case 2_121_007: // Meteor Shower
            case 10_001_011: // Meteo Shower
            case 2_221_007: // Blizzard
                return 3_060;
        }
        // TODO delay for final attack, weapon type, swing,stab etc
        return 330; // Default usually
    }

    /**
     *
     * @param id
     * @return
     */
    public static byte gachaponRareItem(final int id) {
        switch (id) {
            case 2_340_000: // White Scroll
            case 2_049_100: // Chaos Scroll
            case 2_049_000: // Reverse Scroll
            case 2_049_001: // Reverse Scroll
            case 2_049_002: // Reverse Scroll
            case 2_040_006: // Miracle
            case 2_040_007: // Miracle
            case 2_040_303: // Miracle
            case 2_040_403: // Miracle
            case 2_040_506: // Miracle
            case 2_040_507: // Miracle
            case 2_040_603: // Miracle
            case 2_040_709: // Miracle
            case 2_040_710: // Miracle
            case 2_040_711: // Miracle
            case 2_040_806: // Miracle
            case 2_040_903: // Miracle
            case 2_041_024: // Miracle
            case 2_041_025: // Miracle
            case 2_043_003: // Miracle
            case 2_043_103: // Miracle
            case 2_043_203: // Miracle
            case 2_043_303: // Miracle
            case 2_043_703: // Miracle
            case 2_043_803: // Miracle
            case 2_044_003: // Miracle
            case 2_044_103: // Miracle
            case 2_044_203: // Miracle
            case 2_044_303: // Miracle
            case 2_044_403: // Miracle
            case 2_044_503: // Miracle
            case 2_044_603: // Miracle
            case 2_044_908: // Miracle
            case 2_044_815: // Miracle
            case 2_044_019: // Miracle
            case 2_044_703: // Miracle
            case 1_372_039: // Elemental wand lvl 130
            case 1_372_040: // Elemental wand lvl 130
            case 1_372_041: // Elemental wand lvl 130
            case 1_372_042: // Elemental wand lvl 130
            case 1_092_049: // Dragon Khanjar
            case 1_382_037: // Blade Staff
                return 2;
            case 1_102_084: // Pink Gaia Cape
            case 1_102_041: // Pink Adventurer Cape
            case 1_402_044: // Pumpkin Lantern
            case 1_082_149: // Brown Work glove
            case 1_102_086: // Purple Gaia Cape
            case 1_102_042: // Purple Adventurer Cape

            case 3_010_065: // Pink Parasol
            case 3_010_064: // Brown Sand Bunny Cushion
            case 3_010_063: // Starry Moon Cushion
            case 3_010_068: // Teru Teru Chair
            case 3_010_054: // Baby Bear's Dream
            case 3_012_001: // Round the Campfire
            case 3_012_002: // Rubber Ducky Bath
            case 3_010_020: // Portable Meal Table
            case 3_010_041: // Skull Throne

            case 1_082_179: //yellow marker
                return 2;
            //1 = wedding msg o.o
        }
        return 0;
    }

    /**
     *
     */
    public final static int[] goldrewards = {
        2_340_000, 1, // white scroll
    2_070_018, 1, // balance fury 沒有的物品
    1_402_037, 1, // Rigbol Sword
    2_290_096, 1, // Maple Warrior 20
    2_290_049, 1, // Genesis 30
    2_290_041, 1, // Meteo 30
    2_290_047, 1, // Blizzard 30
    2_290_095, 1, // Smoke 30
    2_290_017, 1, // Enrage 30
    2_290_075, 1, // Snipe 30
    2_290_085, 1, // Triple Throw 30
    2_290_116, 1, // Areal Strike
    1_302_059, 3, // Dragon Carabella
    2_049_100, 1, // Chaos Scroll
    2_340_000, 1, // White Scroll
    1_092_049, 1, // Dragon Kanjar
    1_102_041, 1, // Pink Cape
    1_432_018, 3, // Sky Ski
    1_022_047, 3, // Owl Mask
    3_010_051, 1, // Chair
    3_010_020, 1, // Portable meal table
    2_040_914, 1, // Shield for Weapon Atk
    1_432_011, 3, // Fair Frozen
    1_442_020, 3, // HellSlayer
    1_382_035, 3, // Blue Marine
    1_372_010, 3, // Dimon Wand
    1_332_027, 3, // Varkit
    1_302_056, 3, // Sparta
    1_402_005, 3, // Bezerker
    1_472_053, 3, // Red Craven
    1_462_018, 3, // Casa Crow
    1_452_017, 3, // Metus
    1_422_013, 3, // Lemonite
    1_322_029, 3, // Ruin Hammer
    1_412_010, 3, // Colonian Axe
    1_472_051, 1, // Green Dragon Sleeve
    1_482_013, 1, // Emperor's Claw
    1_492_013, 1, // Dragon fire Revlover
    1_382_050, 1, // Blue Dragon Staff
    1_382_045, 1, // Fire Staff, Level 105
    1_382_047, 1, // Ice Staff, Level 105
    1_382_048, 1, // Thunder Staff
    1_382_046, 1, // Poison Staff
    1_332_032, 4, // Christmas Tree
    1_482_025, 3, // Flowery Tube
    4_001_011, 4, // Lupin Eraser
    4_001_010, 4, // Mushmom Eraser
    4_001_009, 4, // Stump Eraser
    2_030_008, 5, // Bottle, return scroll
    1_442_018, 3, // Frozen Tuna
    2_040_900, 4, // Shield for DEF
    2_000_005, 10, // Power Elixir
    2_000_004, 10, // Elixir
    4_280_000, 4}; // Gold Box

    /**
     *
     */
    public final static int[] silverrewards = {
        3_010_041, 1, // skull throne
    1_002_452, 3, // Starry Bandana
    1_002_455, 3, // Starry Bandana
    2_290_084, 1, // Triple Throw 20
    2_290_048, 1, // Genesis 20
    2_290_040, 1, // Meteo 20
    2_290_046, 1, // Blizzard 20
    2_290_074, 1, // Sniping 20
    2_290_064, 1, // Concentration 20
    2_290_094, 1, // Smoke 20
    2_290_022, 1, // Berserk 20
    2_290_056, 1, // Bow Expert 30
    2_290_066, 1, // xBow Expert 30
    2_290_020, 1, // Sanc 20
    1_102_082, 1, // Black Raggdey Cape
    1_302_049, 1, // Glowing Whip
    2_340_000, 1, // White Scroll
    1_102_041, 1, // Pink Cape
    1_452_019, 2, // White Nisrock
    4_001_116, 3, // Hexagon Pend
    4_001_012, 3, // Wraith Eraser
    1_022_060, 2, // Foxy Racoon Eye
    1_432_011, 3, // Fair Frozen
    1_442_020, 3, // HellSlayer
    1_382_035, 3, // Blue Marine
    1_372_010, 3, // Dimon Wand
    1_332_027, 3, // Varkit
    1_302_056, 3, // Sparta
    1_402_005, 3, // Bezerker
    1_472_053, 3, // Red Craven
    1_462_018, 3, // Casa Crow
    1_452_017, 3, // Metus
    1_422_013, 3, // Lemonite
    1_322_029, 3, // Ruin Hammer
    1_412_010, 3, // Colonian Axe
    1_002_587, 3, // Black Wisconsin
    1_402_044, 1, // Pumpkin lantern
    2_101_013, 4, // Summoning Showa boss
    1_442_046, 1, // Super Snowboard
    1_422_031, 1, // Blue Seal Cushion
    1_332_054, 3, // Lonzege Dagger
    1_012_056, 3, // Dog Nose
    1_022_047, 3, // Owl Mask
    3_012_002, 1, // Bathtub
    1_442_012, 3, // Sky snowboard
    1_442_018, 3, // Frozen Tuna
    1_432_010, 3, // Omega Spear
    1_432_036, 1, // Fishing Pole 沒用的物品
    2_000_005, 10, // Power Elixir
    2_000_004, 10, // Elixir
    4_280_001, 4}; // Silver Box

    /**
     *
     */
    public static int[] eventCommonReward = {
        0, 40,
        1, 10, //        5060003, 18,
    //        4170023, 18,
    4_031_019, 5, 4_280_000, 3, 4_280_001, 4, 5_490_000, 3, 5_490_001, 4
    };

    /**
     *
     */
    public static int[] eventUncommonReward = {
        2, 4,
        3, 4, 5_160_000, 5, 5_160_001, 5, 5_160_002, 5, 5_160_003, 5, 5_160_004, 5, 5_160_005, 5, 5_160_006, 5, 5_160_007, 5, 5_160_008, 5, 5_160_009, 5, 5_160_010, 5, 5_160_011, 5, 5_160_012, 5, 5_160_013, 5, 5_240_017, 5, 5_240_000, 5, 4_080_000, 5, 4_080_001, 5, 4_080_002, 5, 4_080_003, 5, 4_080_004, 5, 4_080_005, 5, 4_080_006, 5, 4_080_007, 5, 4_080_008, 5, 4_080_009, 5, 4_080_010, 5, 4_080_011, 5, 4_080_100, 5, 4_031_019, 5, 5_121_003, 5, 5_150_000, 5, 5_150_001, 5, 5_150_002, 1, 5_150_003, 1, 5_150_004, 1, 5_150_005, 2, 5_150_006, 2, 5_150_007, 2, 5_150_008, 2, 5_150_009, 14, 2_022_459, 5, 2_022_460, 5, 2_022_461, 5, 2_022_462, 5, 2_022_463, 5, 2_450_000, 2, 5_152_000, 5, 5_152_001, 5
    };

    /**
     *
     */
    public static int[] eventRareReward = {
        4_031_019, 5, 2_049_100, 5, //        1122017, 1,
    2_049_401, 10, 2_049_301, 20, 2_049_400, 3, 2_340_000, 1, 3_010_130, 5, 3_010_131, 5, 3_010_132, 5, 3_010_133, 5, 3_010_136, 5, 3_010_116, 5, 3_010_117, 5, 3_010_118, 5, 1_112_405, 1, 1_112_413, 1, 1_112_414, 1, //        1022097, 1,
    2_040_211, 1, 2_040_212, 1, 2_049_000, 2, 2_049_001, 2, 2_049_002, 2, 2_049_003, 2, 1_012_058, 2, 1_012_059, 2, 1_012_060, 2, 1_012_061, 2
    };

    /**
     *
     */
    public static int[] eventSuperReward = {
        4_031_019, 5, 2_022_121, 10, 4_031_307, 50, 3_010_127, 10, 3_010_128, 10, 3_010_137, 10, 2_049_300, 10, 1_012_139, 10, 1_012_140, 10, 1_012_141, 10
    };

    /**
     *
     */
    public static int[] fishingReward = { //钓鱼物品
        //钓鱼物品
        0, 30, // Meso
        1, 10, // EXP
    // 1302021, 5, // Pico Pico Hammer
    // 1072238, 1, // Voilet Snowshoe
    // 1072239, 1, // Yellow Snowshoe
    2_049_100, 1, // Chaos Scroll
    2_049_301, 1, // Equip Enhancer Scroll
    2_049_401, 1, // Potential Scroll
    // 1302000, 3, // Sword
    // 1442011, 1, // Surfboard
    //  4000517, 8, // Golden Fish
    //  4000518, 25, // Golden Fish Egg
    4_031_630, 10, // White Bait (3cm)
    4_031_637, 8, // Sailfish (120cm)
    4_031_638, 6, // Carp (30cm)
    4_031_639, 4, // Salmon(150cm)
    4_031_640, 2, // Shovel
    4_031_628, 3, // Whitebait (3.6cm)
    4_031_641, 3, // Whitebait (5cm)
    4_031_642, 3, // Whitebait (6.5cm)
    4_031_643, 3, // Whitebait (10cm)
    4_031_644, 3, // Carp (53cm)
    //4031638, 5, // Carp (60cm)
    1_032_004, 5, // Carp (100cm)
    1_032_005, 5, // Carp (100cm)
    1_032_006, 5, // Carp (100cm)
    1_032_007, 5, // Carp (100cm)
    1_032_008, 5, // Carp (100cm)
    1_032_009, 5, // Carp (100cm)
    1_032_012, 5, // Carp (100cm)
    1_032_017, 5, // Carp (100cm)
    1_032_018, 5, // Carp (100cm)
    1_032_019, 5, // Carp (100cm)
    1_032_022, 5, // Carp (100cm)
    1_032_023, 5, // Carp (100cm)
    1_032_025, 5, // Carp (100cm)
    1_032_026, 5, // Carp (100cm)
    1_002_019, 5, // Carp (100cm)
    1_002_020, 5, // Carp (100cm)
    1_002_021, 5, // Carp (100cm)
    1_002_022, 5, // Carp (100cm)
    1_002_023, 5, // Carp (100cm)
    1_002_024, 5, // Carp (100cm)
    1_002_030, 5, // Carp (100cm)
    1_002_033, 5, // Carp (100cm)
    1_002_039, 5, // Carp (100cm)
    1_002_040, 5, // Carp (100cm)
    1_002_085, 5, // Carp (100cm)
    2_000_002, 5, // Carp (100cm)
    2_000_003, 5, // Carp (113cm)
    2_000_006, 5, // Sailfish (128cm)
    2_120_000, 5, // Sailfish (131cm)
    2_000_002, 5, // Sailfish (140cm)
    2_000_002, 5, // Sailfish (148cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_000, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_022_003, 5, // Carp (100cm)
    2_020_013, 5, // Carp (100cm)
    2_020_013, 5, // Carp (100cm)
    2_000_005, 5, // Carp (100cm)
    2_000_005, 5, // Carp (100cm)
    2_000_005, 5, // Carp (100cm)
    1_032_004, 5, // Carp (100cm)
    1_032_005, 5, // Carp (113cm)
    1_032_006, 5, // Sailfish (128cm)
    1_032_007, 5, // Sailfish (131cm)
    1_032_008, 5, // Sailfish (140cm)
    1_032_009, 5, // Sailfish (148cm)
    1_032_012, 5, // Salmon (166cm)
    1_032_017, 5, // Salmon (183cm)
    1_032_018, 5, // Salmon (227cm)
    1_032_019, 5, // Salmon (288cm)
    1_032_022, 5, // Pot
    2_044_901, 5, 2_044_902, 5, 2_044_802, 5, 2_044_801, 5, 2_044_702, 5, 2_044_701, 5, 2_044_602, 5, 2_044_601, 5, 2_044_501, 5, 2_044_502, 5, 2_044_402, 5, 2_044_401, 5, 2_044_302, 5, 2_044_301, 5, 2_044_201, 5, 2_044_202, 5, 2_044_102, 5, 2_044_101, 5, 2_044_002, 5, 2_044_001, 5, 2_043_802, 5, 2_043_801, 5, 2_043_702, 5, 2_043_701, 5, 2_043_302, 5, 2_043_301, 5, 2_043_202, 5, 2_043_201, 5, 2_043_102, 5, 2_043_101, 5, 2_043_002, 5, 2_043_001, 5, 2_040_801, 5, 2_040_814, 5, 2_040_815, 5, 2_040_816, 5, 2_040_817, 5, 2_040_802, 5, 2_040_915, 5, 2_040_914, 5, 2_040_805, 5, 2_040_804, 5, 2_040_532, 5, 2_040_534, 5, 2_040_517, 5, 2_040_516, 5, 2_040_514, 5, 2_040_513, 5, 2_040_502, 5, 2_040_501, 5, 2_040_323, 5, 2_040_321, 5, 2_040_317, 5, 2_040_316, 5, 2_040_302, 5, 2_040_301, 5
    };

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isDragonItem(int itemId) {
        switch (itemId) {
            case 1_372_032:
            case 1_312_031:
            case 1_412_026:
            case 1_302_059:
            case 1_442_045:
            case 1_402_036:
            case 1_432_038:
            case 1_422_028:
            case 1_472_051:
            case 1_472_052:
            case 1_332_049:
            case 1_332_050:
            case 1_322_052:
            case 1_452_044:
            case 1_462_039:
            case 1_382_036:
            case 1_342_010:
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isReverseItem(int itemId) {
        switch (itemId) {
            case 1_002_790:
            case 1_002_791:
            case 1_002_792:
            case 1_002_793:
            case 1_002_794:
            case 1_082_239:
            case 1_082_240:
            case 1_082_241:
            case 1_082_242:
            case 1_082_243:
            case 1_052_160:
            case 1_052_161:
            case 1_052_162:
            case 1_052_163:
            case 1_052_164:
            case 1_072_361:
            case 1_072_362:
            case 1_072_363:
            case 1_072_364:
            case 1_072_365:

            case 1_302_086:
            case 1_312_038:
            case 1_322_061:
            case 1_332_075:
            case 1_332_076:
            case 1_372_045:
            case 1_382_059:
            case 1_402_047:
            case 1_412_034:
            case 1_422_038:
            case 1_432_049:
            case 1_442_067:
            case 1_452_059:
            case 1_462_051:
            case 1_472_071:
            case 1_482_024:
            case 1_492_025:

            case 1_342_012:
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isTimelessItem(int itemId) {
        switch (itemId) {
            case 1_032_031: //shield earring, but technically
            case 1_102_172:
            case 1_002_776:
            case 1_002_777:
            case 1_002_778:
            case 1_002_779:
            case 1_002_780:
            case 1_082_234:
            case 1_082_235:
            case 1_082_236:
            case 1_082_237:
            case 1_082_238:
            case 1_052_155:
            case 1_052_156:
            case 1_052_157:
            case 1_052_158:
            case 1_052_159:
            case 1_072_355:
            case 1_072_356:
            case 1_072_357:
            case 1_072_358:
            case 1_072_359:
            case 1_092_057:
            case 1_092_058:
            case 1_092_059:

            case 1_122_011:
            case 1_122_012:

            case 1_302_081:
            case 1_312_037:
            case 1_322_060:
            case 1_332_073:
            case 1_332_074:
            case 1_372_044:
            case 1_382_057:
            case 1_402_046:
            case 1_412_033:
            case 1_422_037:
            case 1_432_047:
            case 1_442_063:
            case 1_452_057:
            case 1_462_050:
            case 1_472_068:
            case 1_482_023:
            case 1_492_023:
            case 1_342_011:
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isRing(int itemId) {
        return itemId >= 1_112_000 && itemId < 1_113_000;
    }// 112xxxx - pendants, 113xxxx - belts

    //if only there was a way to find in wz files -.-

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean isEffectRing(int itemid) {

        return isFriendshipRing(itemid) || isCrushRing(itemid) || isMarriageRing(itemid);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isFriendshipRing(int itemId) {
        switch (itemId) {
            case 1_112_800:
            case 1_112_801:
            case 1_112_802:
            //  case 1112804:
            case 1_112_810: //new
            case 1_112_811: //new, doesnt work in friendship?
            case 1_112_812: //new, im ASSUMING it's friendship cuz of itemID, not sure.
            case 1_112_015:
            case 1_049_000:

            case 1_112_816:
            case 1_112_817:

                return true;
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isCrushRing(int itemId) {
        switch (itemId) {
            case 1_112_001:
            case 1_112_002:
            case 1_112_003:
            case 1_112_005:
            case 1_112_006:
            case 1_112_007:
            case 1_112_012:
            case 1_112_015:
            //case 1112804:
            case 1_048_000:
            case 1_048_001:
            case 1_048_002:
                return true;
        }
        return false;
    }

    /**
     *
     */
    public static int[] Equipments_Bonus = {1_122_017};

    /**
     *
     * @param itemid
     * @return
     */
    public static int Equipment_Bonus_EXP(final int itemid) { // TODO : Add Time for more exp increase
        switch (itemid) {
            case 1_122_017:
                return 30;
        }
        return 0;
    }

    /**
     *
     */
    public static int[] blockedMaps = {109_050_000, 280_030_000, 240_060_200, 280_090_000, 280_030_001, 240_060_201, 950_101_100, 950_101_010};
    //If you can think of more maps that could be exploitable via npc,block nao pliz!

    /**
     *
     * @param i
     * @param itemId
     * @return
     */
    public static int getExpForLevel(int i, int itemId) {
        if (isReverseItem(itemId)) {
            return getReverseRequiredEXP(i);
        } else if (getMaxLevel(itemId) > 0) {
            return getTimelessRequiredEXP(i);
        }
        return 0;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static int getMaxLevel(final int itemId) {
        if (isTimelessItem(itemId)) {
            return 5;
        } else if (isReverseItem(itemId)) {
            return 3;
        } else {
            switch (itemId) {
                case 1_302_109:
                case 1_312_041:
                case 1_322_067:
                case 1_332_083:
                case 1_372_048:
                case 1_382_064:
                case 1_402_055:
                case 1_412_037:
                case 1_422_041:
                case 1_432_052:
                case 1_442_073:
                case 1_452_064:
                case 1_462_058:
                case 1_472_079:
                case 1_482_035:

                case 1_302_108:
                case 1_312_040:
                case 1_322_066:
                case 1_332_082:
                case 1_372_047:
                case 1_382_063:
                case 1_402_054:
                case 1_412_036:
                case 1_422_040:
                case 1_432_051:
                case 1_442_072:
                case 1_452_063:
                case 1_462_057:
                case 1_472_078:
                case 1_482_036:
                    return 1;

                case 1_072_376:
                    return 2;
            }
        }
        return 0;
    }

    /**
     *
     * @return
     */
    public static int getStatChance() {
        return 25;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static MonsterStatus getStatFromWeapon(final int itemid) {
        switch (itemid) {
            case 1_302_109:
            case 1_312_041:
            case 1_322_067:
            case 1_332_083:
            case 1_372_048:
            case 1_382_064:
            case 1_402_055:
            case 1_412_037:
            case 1_422_041:
            case 1_432_052:
            case 1_442_073:
            case 1_452_064:
            case 1_462_058:
            case 1_472_079:
            case 1_482_035:
                return MonsterStatus.ACC;
            case 1_302_108:
            case 1_312_040:
            case 1_322_066:
            case 1_332_082:
            case 1_372_047:
            case 1_382_063:
            case 1_402_054:
            case 1_412_036:
            case 1_422_040:
            case 1_432_051:
            case 1_442_072:
            case 1_452_063:
            case 1_462_057:
            case 1_472_078:
            case 1_482_036:
                return MonsterStatus.SPEED;
        }
        return null;
    }

    /**
     *
     * @param stat
     * @return
     */
    public static int getXForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC:
                return -70;
            case SPEED:
                return -50;
        }
        return 0;
    }

    /**
     *
     * @param stat
     * @return
     */
    public static int getSkillForStat(MonsterStatus stat) {
        switch (stat) {
            case ACC:
                return 3_221_006;
            case SPEED:
                return 3_121_007;
        }
        return 0;
    }

    /**
     *
     */
    public final static int[] normalDrops = {
        4_001_009, //real
    4_001_010, 4_001_011, 4_001_012, 4_001_013, 4_001_014, //real
    4_001_021, 4_001_038, //fake
    4_001_039, 4_001_040, 4_001_041, 4_001_042, 4_001_043, //fake
    4_001_038, //fake
    4_001_039, 4_001_040, 4_001_041, 4_001_042, 4_001_043, //fake
    4_001_038, //fake
    4_001_039, 4_001_040, 4_001_041, 4_001_042, 4_001_043, //fake
    4_000_164, //start
    2_000_000, 2_000_003, 2_000_004, 2_000_005, 4_000_019, 4_000_000, 4_000_016, 4_000_006, 2_100_121, 4_000_029, 4_000_064, 5_110_000, 4_000_306, 4_032_181, 4_006_001, 4_006_000, 2_050_004, 3_994_102, 3_994_103, 3_994_104, 3_994_105, 2_430_007, //end
    4_000_164, //start
    2_000_000, 2_000_003, 2_000_004, 2_000_005, 4_000_019, 4_000_000, 4_000_016, 4_000_006, 2_100_121, 4_000_029, 4_000_064, 5_110_000, 4_000_306, 4_032_181, 4_006_001, 4_006_000, 2_050_004, 3_994_102, 3_994_103, 3_994_104, 3_994_105, 2_430_007, //end
    4_000_164, //start
    2_000_000, 2_000_003, 2_000_004, 2_000_005, 4_000_019, 4_000_000, 4_000_016, 4_000_006, 2_100_121, 4_000_029, 4_000_064, 5_110_000, 4_000_306, 4_032_181, 4_006_001, 4_006_000, 2_050_004, 3_994_102, 3_994_103, 3_994_104, 3_994_105, 2_430_007}; //end

    /**
     *
     */
    public final static int[] rareDrops = {
        2_049_100, 2_049_301, 2_049_401, 2_022_326, 2_022_193, 2_049_000, 2_049_001, 2_049_002};

    /**
     *
     */
    public final static int[] superDrops = {
        2_040_804, 2_049_400, 2_049_100};

    /**
     *
     * @param job
     * @return
     */
    public static int getSkillBook(final int job) {
        if (job >= 2_210 && job <= 2_218) {
            return job - 2_209;
        }
        switch (job) {
            case 3_210:
            case 3_310:
            case 3_510:
                return 1;
            case 3_211:
            case 3_311:
            case 3_511:
                return 2;
            case 3_212:
            case 3_312:
            case 3_512:
                return 3;
        }
        return 0;
    }

    /**
     *
     * @param skillid
     * @return
     */
    public static int getSkillBookForSkill(final int skillid) {
        return getSkillBook(skillid / 10_000);
    }

    /**
     *
     * @param sourceid
     * @return
     */
    public static int getMountItem(final int sourceid) {
        switch (sourceid) {
            case 5_221_006:
                return 1_932_000;
            case 33_001_001: //temp.
                return 1_932_015;
            case 35_001_002:
            case 35_120_000:
                return 1_932_016;
            case 1_013:
            case 10_001_013:
            case 20_001_013:
            case 20_011_013:
            case 30_001_013:
            case 1_046:
            case 10_001_046:
            case 20_001_046:
            case 20_011_046:
            case 30_001_046:
                return 1_932_001;
            case 1_015:
            case 10_001_015:
            case 20_001_015:
            case 20_011_015:
            case 30_001_015:
            case 1_048:
            case 10_001_048:
            case 20_001_048:
            case 20_011_048:
            case 30_001_048:
                return 1_932_002;
            case 1_016:
            case 10_001_016:
            case 20_001_016:
            case 20_011_016:
            case 30_001_016:
            case 1_017:
            case 1_007:
            case 10_001_017:
            case 20_001_017:
            case 20_011_017:
            case 30_001_017:
            case 1_027:
            case 10_001_027:
            case 20_001_027:
            case 20_011_027:
            case 30_001_027:
                return 1_932_007;
            case 1_018:
            case 10_001_018:
            case 20_001_018:
            case 20_011_018:
            case 30_001_018:
                return 1_932_003;
            case 1_019:
            case 10_001_019:
            case 20_001_019:
            case 20_011_019:
            case 30_001_019:
                return 1_932_005;
            case 1_025:
            case 10_001_025:
            case 20_001_025:
            case 20_011_025:
            case 30_001_025:
                return 1_932_006;
            case 1_028:
            case 10_001_028:
            case 20_001_028:
            case 20_011_028:
            case 30_001_028:
                return 1_932_008;
            case 1_029:
            case 10_001_029:
            case 20_001_029:
            case 20_011_029:
            case 30_001_029:
                return 1_932_009;
            case 1_030:
            case 10_001_030:
            case 20_001_030:
            case 20_011_030:
            case 30_001_030:
                return 1_932_011;
            case 1_031:
            case 10_001_031:
            case 20_001_031:
            case 20_011_031:
            case 30_001_031:
                return 1_932_010;
            case 1_034:
            case 10_001_034:
            case 20_001_034:
            case 20_011_034:
            case 30_001_034:
                return 1_932_014;
            case 1_035:
            case 10_001_035:
            case 20_001_035:
            case 20_011_035:
            case 30_001_035:
                return 1_932_012;
            case 1_036:
            case 10_001_036:
            case 20_001_036:
            case 20_011_036:
            case 30_001_036:
                return 1_932_017;
            case 1_037:
            case 10_001_037:
            case 20_001_037:
            case 20_011_037:
            case 30_001_037:
                return 1_932_018;
            case 1_038:
            case 10_001_038:
            case 20_001_038:
            case 20_011_038:
            case 30_001_038:
                return 1_932_019;
            case 1_039:
            case 10_001_039:
            case 20_001_039:
            case 20_011_039:
            case 30_001_039:
                return 1_932_020;
            case 1_040:
            case 10_001_040:
            case 20_001_040:
            case 20_011_040:
            case 30_001_040:
                return 1_932_021;
            case 1_042:
            case 10_001_042:
            case 20_001_042:
            case 20_011_042:
            case 30_001_042:
                return 1_932_022;
            case 1_044:
            case 10_001_044:
            case 20_001_044:
            case 20_011_044:
            case 30_001_044:
                return 1_932_023;
            case 1_045:
            case 10_001_045:
            case 20_001_045:
            case 20_011_045:
            case 30_001_045:
                return 1_932_030; //wth? helicopter? i didnt see one, so we use hog
            case 1_049:
            case 10_001_049:
            case 20_001_049:
            case 20_011_049:
            case 30_001_049:
                return 1_932_025;
            case 1_050:
            case 10_001_050:
            case 20_001_050:
            case 20_011_050:
            case 30_001_050:
                return 1_932_004;
            case 1_051:
            case 10_001_051:
            case 20_001_051:
            case 20_011_051:
            case 30_001_051:
                return 1_932_026;
            case 1_052:
            case 10_001_052:
            case 20_001_052:
            case 20_011_052:
            case 30_001_052:
                return 1_932_027;
            case 1_053:
            case 10_001_053:
            case 20_001_053:
            case 20_011_053:
            case 30_001_053:
                return 1_932_028;
            case 1_054:
            case 10_001_054:
            case 20_001_054:
            case 20_011_054:
            case 30_001_054:
                return 1_932_029;
            case 1_069:
            case 10_001_069:
            case 20_001_069:
            case 20_011_069:
            case 30_001_069:
                return 1_932_038;
            case 1_096:
            case 10_001_096:
            case 20_001_096:
            case 20_011_096:
            case 30_001_096:
                return 1_932_045;
            case 1_101:
            case 10_001_101:
            case 20_001_101:
            case 20_011_101:
            case 30_001_101:
                return 1_932_046;
            case 1_102:
            case 10_001_102:
            case 20_001_102:
            case 20_011_102:
            case 30_001_102:
                return 1_932_047;
            default:
                return 0;
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isKatara(int itemId) {
        return itemId / 10_000 == 134;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isDagger(int itemId) {
        return itemId / 10_000 == 133;
    }

    /**
     *
     * @param skil
     * @return
     */
    public static boolean isApplicableSkill(int skil) {
        return skil < 40_000_000 && (skil % 10_000 < 8_000 || skil % 10_000 > 8_003); //no additional/decent skills
    }

    /**
     *
     * @param skil
     * @return
     */
    public static boolean isApplicableSkill_(int skil) { //not applicable to saving but is more of temporary
        return skil >= 90_000_000 || (skil % 10_000 >= 8_000 && skil % 10_000 <= 8_003);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isTablet(int itemId) {
        return itemId / 1_000 == 2_047;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is周年庆卷轴(int itemId) {
        return itemId / 1_000 == 2_046;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static int is周年庆卷轴成功率(int itemId) {
        return ((itemId == 2_046_308) || (itemId == 2_046_309) || (itemId == 2_046_213) || (itemId == 2_046_214) || (itemId == 2_046_006) || (itemId == 2_046_007) || (itemId == 2_046_025) || (itemId == 2_046_026) || ((itemId >= 2_046_106) && (itemId <= 2_046_107))) ? 20 : ((itemId == 2_046_008) || (itemId == 2_046_009) || (itemId == 2_046_219) || (itemId == 2_046_220) || (itemId == 2_046_310) || (itemId == 2_046_311)) ? 50 : ((itemId == 2_043_108) || (itemId == 2_043_208) || (itemId == 2_043_308) || (itemId == 2_043_405) || (itemId == 2_043_708) || (itemId == 2_043_808) || (itemId == 2_044_008) || (itemId == 2_044_108) || (itemId == 2_044_208) || (itemId == 2_044_308)) ? 100 : ((itemId == 2_044_408) || (itemId == 2_044_508) || (itemId == 2_044_608) || (itemId == 2_044_708) || (itemId == 2_044_810) || (itemId == 2_044_905)) ? 40 : 1;
    }

    /**
     *
     * @param scrollId
     * @param level
     * @return
     */
    public static int getSuccessTablet(final int scrollId, final int level) {
        switch (scrollId % 1_000 / 100) {
            case 2:
                //2047_2_00 = armor, 2047_3_00 = accessory
                switch (level) {
                    case 0:
                        return 70;
                    case 1:
                        return 55;
                    case 2:
                        return 43;
                    case 3:
                        return 33;
                    case 4:
                        return 26;
                    case 5:
                        return 20;
                    case 6:
                        return 16;
                    case 7:
                        return 12;
                    case 8:
                        return 10;
                    default:
                        return 7;
                }
            case 3:
                switch (level) {
                    case 0:
                        return 70;
                    case 1:
                        return 35;
                    case 2:
                        return 18;
                    case 3:
                        return 12;
                    default:
                        return 7;
                }
            default:
                switch (level) {
                    case 0:
                        return 70;
                    case 1:
                        return 50; //-20
                    case 2:
                        return 36; //-14
                    case 3:
                        return 26; //-10
                    case 4:
                        return 19; //-7
                    case 5:
                        return 14; //-5
                    case 6:
                        return 10; //-4
                    default:
                        return 7;  //-3
                }
        }
    }

    /**
     *
     * @param scrollId
     * @param level
     * @return
     */
    public static int getCurseTablet(final int scrollId, final int level) {
        switch (scrollId % 1_000 / 100) {
            case 2:
                //2047_2_00 = armor, 2047_3_00 = accessory
                switch (level) {
                    case 0:
                        return 10;
                    case 1:
                        return 12;
                    case 2:
                        return 16;
                    case 3:
                        return 20;
                    case 4:
                        return 26;
                    case 5:
                        return 33;
                    case 6:
                        return 43;
                    case 7:
                        return 55;
                    case 8:
                        return 70;
                    default:
                        return 100;
                }
            case 3:
                switch (level) {
                    case 0:
                        return 12;
                    case 1:
                        return 18;
                    case 2:
                        return 35;
                    case 3:
                        return 70;
                    default:
                        return 100;
                }
            default:
                switch (level) {
                    case 0:
                        return 10;
                    case 1:
                        return 14; //+4
                    case 2:
                        return 19; //+5
                    case 3:
                        return 26; //+7
                    case 4:
                        return 36; //+10
                    case 5:
                        return 50; //+14
                    case 6:
                        return 70; //+20
                    default:
                        return 100;  //+30
                }
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isAccessory(final int itemId) {
        return (itemId >= 1_010_000 && itemId < 1_040_000) || (itemId >= 1_122_000 && itemId < 1_153_000) || (itemId >= 1_112_000 && itemId < 1_113_000);
    }

    /**
     *
     * @param potentialID
     * @param newstate
     * @param i
     * @return
     */
    public static boolean potentialIDFits(final int potentialID, final int newstate, final int i) {
        //first line is always the best
        //but, sometimes it is possible to get second/third line as well
        //may seem like big chance, but it's not as it grabs random potential ID anyway
        switch (newstate) {
            case 7:
                return i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 30_000 : potentialID >= 20_000 && potentialID < 30_000;
            case 6:
                return i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 20_000 && potentialID < 30_000 : potentialID >= 10_000 && potentialID < 20_000;
            case 5:
                return i == 0 || Randomizer.nextInt(10) == 0 ? potentialID >= 10_000 && potentialID < 20_000 : potentialID < 10_000;
            default:
                return false;
        }
    }

    /**
     *
     * @param optionType
     * @param itemId
     * @return
     */
    public static boolean optionTypeFits(final int optionType, final int itemId) {
        switch (optionType) {
            case 10: //weapon
                return isWeapon(itemId);
            case 11: //any armor
                return !isWeapon(itemId);
            case 20: //shield??????????
                return itemId / 10_000 == 109; //just a gues
            case 21: //pet equip?????????
                return itemId / 10_000 == 180; //???LOL
            case 40: //face accessory
                return isAccessory(itemId);
            case 51: //hat
                return itemId / 10_000 == 100;
            case 52: //cape
                return itemId / 10_000 == 110;
            case 53: //top/bottom/overall
                return itemId / 10_000 == 104 || itemId / 10_000 == 105 || itemId / 10_000 == 106;
            case 54: //glove
                return itemId / 10_000 == 108;
            case 55: //shoe
                return itemId / 10_000 == 107;
            case 90:
                return false; //half this stuff doesnt even work
            default:
                return true;
        }
    }

    /**
     *
     * @param mountid
     * @param jobid
     * @return
     */
    public static final boolean isMountItemAvailable(final int mountid, final int jobid) {
        if (jobid != 900 && mountid / 10_000 == 190) {
            if (isKOC(jobid)) {
                if (mountid < 1_902_005 || mountid > 1_902_007) {
                    return false;
                }
            } else if (isAdventurer(jobid)) {
                if (mountid < 1_902_000 || mountid > 1_902_002) {
                    return false;
                }
            } else if (isAran(jobid)) {
                if (mountid < 1_902_015 || mountid > 1_902_018) {
                    return false;
                }
            } else if (isEvan(jobid)) {
                if (mountid < 1_902_040 || mountid > 1_902_042) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isEvanDragonItem(final int itemId) {
        return itemId >= 1_940_000 && itemId < 1_980_000; //194 = mask, 195 = pendant, 196 = wings, 197 = tail
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean canScroll(final int itemId) {
        return itemId / 100_000 != 19 && itemId / 100_000 != 16; //no mech/taming/dragon
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean canHammer(final int itemId) {
        switch (itemId) {
            case 1_122_000:
            case 1_122_076: //ht, chaos ht
                return false;
        }
        return canScroll(itemId);
    }

    /**
     *
     */
    public static int[] owlItems = new int[]{
        1_082_002, // work gloves
    2_070_005, 2_070_006, 1_022_047, 1_102_041, 2_044_705, 2_340_000, // white scroll
    2_040_017, 1_092_030, 2_040_804};

    /**
     *
     * @param job
     * @return
     */
    public static int getMasterySkill(final int job) {
        if (job >= 1_410 && job <= 1_412) {
            return 14_100_000;
        } else if (job >= 410 && job <= 412) {
            return 4_100_000;
        } else if (job >= 520 && job <= 522) {
            return 5_200_000;
        }
        return 0;
    }

    /**
     *
     * @param job
     * @return
     */
    public static int getExpRate_Below10(final int job) {
        if (GameConstants.isEvan(job)) {
            return 1;
        } else if (GameConstants.isAran(job) || GameConstants.isKOC(job)) {
            return 5;
        }
        return 1;
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getExpRate_Quest(final int level) {
        return 1;
    }

    /**
     *
     * @param id
     * @return
     */
    public static String getCashBlockedMsg(final int id) {
        switch (id) {
            case 5_062_000:
                //cube
                return "This item may only be purchased at the PlayerNPC in FM.";
        }
        return "This item is blocked from the Cash Shop.";
    }

    /**
     *
     * @param rid
     * @param iid
     * @param original
     * @return
     */
    public static boolean isCustomReactItem(final int rid, final int iid, final int original) {
        if (rid == 2_008_006) { //orbis pq LOL
            return iid == (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 4_001_055);
            //4001056 = sunday. 4001062 = saturday
        } else {
            return iid == original;
        }
    }

    /**
     *
     * @param jobz
     * @return
     */
    public static int getJobNumber(int jobz) {
        int job = (jobz % 1_000);
        if (job / 100 == 0) {
            return 0; //beginner
        } else if (job / 10 == 0) {
            return 1;
        } else {
            return 2 + (job % 10);
        }
    }

    /**
     *
     * @param mapid
     * @return
     */
    public static boolean isForceRespawn(int mapid) {
        switch (mapid) {
            case 925_100_100: //crocs and stuff
                return true;
            default:
                return mapid / 100_000 == 9_800 && (mapid % 10 == 1 || mapid % 1_000 == 100);
        }
    }

    /**
     *
     * @param vip
     * @param gm
     * @return
     */
    public static int getFishingTime(boolean vip, boolean gm) {
        return 60 * 1_000;//钓鱼时间
        //   return gm ? 1000 : (vip ? 10000 : 20000);
    }

    /**
     *
     * @param summoner
     * @param def
     * @return
     */
    public static int getCustomSpawnID(int summoner, int def) {
        switch (summoner) {
            case 9_400_589:
            case 9_400_748: //MV
                return 9_400_706; //jr
            default:
                return def;
        }
    }

    /**
     *
     * @param questid
     * @return
     */
    public static boolean canForfeit(int questid) {
        switch (questid) {
            case 20_000:
            case 20_010:
            case 20_015: //cygnus quests
            case 20_020:
                return false;
            default:
                return true;
        }
    }

    /**
     *
     * @param mapid
     * @return
     */
    public static boolean isEventMap(final int mapid) {
        return (mapid >= 109_010_000 && mapid < 109_050_000) || (mapid > 109_050_001 && mapid < 109_090_000) || (mapid >= 809_040_000 && mapid <= 809_040_100);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is豆豆装备(int itemId) {
        switch (itemId) {
            //帽子
            case 1_123_200:
            case 1_002_695://幽灵帽
            case 1_002_609://兔耳魔法帽
            case 1_002_665://西红柿帽
            case 1_002_985://豆箱帽子
            case 1_002_986://蝙蝠怪面具
            case 1_002_761://枫叶面具
            case 1_002_760://地球帽
            case 1_002_583://蝙蝠客头套
            case 1_002_543://板栗帽
            case 1_002_448://紫色头巾

            //衣服
            case 1_052_137://西红柿外套

            //盾牌
            case 1_092_051://啤酒杯盾牌

            //武器
            case 1_702_232://我的朋友金猎犬
            case 1_702_138://大火腿

            //坐骑
            case 1_902_031:
            case 1_902_032:
            case 1_902_033:
            case 1_902_034:
            case 1_902_035:
            case 1_902_036:
            case 1_902_037:
            //鞍子
            case 1_912_024:
            case 1_912_025:
            case 1_912_026:
            case 1_912_027:
            case 1_912_028:
            case 1_912_029:
            case 1_912_030:
                return true;
        }
        return false;
    }

    /**
     *
     * @param rid
     * @param original
     * @return
     */
    public static int getCustomReactItem(int rid, int original) {
        if (rid == 2_008_006) {
            return Calendar.getInstance().get(7) + 4_001_055;
        }

        return original;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_550(int SkillID) {
        switch (SkillID) {
            case 3_121_006:// 火凤凰     550
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_500(int SkillID) {
        switch (SkillID) {
            case 3_221_005:// 冰凤凰     500
            case 5_220_002:// 超级章鱼炮台     500
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_450(int SkillID) {
        switch (SkillID) {
            case 5_211_002:// 海鸥空袭     450
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_300(int SkillID) {
        switch (SkillID) {
            case 2_221_005:// 火魔兽       300
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_270(int SkillID) {
        switch (SkillID) {
            case 2_121_005:// 冰破魔兽     270
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_250(int SkillID) {
        switch (SkillID) {
            case 12_111_004:// 火魔兽	 250
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_230(int SkillID) {
        switch (SkillID) {
            case 2_321_003:// 强化圣龙     230
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_200(int SkillID) {
        switch (SkillID) {
            case 5_211_001:// 章鱼炮台     200
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_150(int SkillID) {
        switch (SkillID) {
            case 2_311_006:// 圣龙召唤     150
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_100(int SkillID) {
        switch (SkillID) {
            case 3_111_005://银鹰召唤     100
            case 3_211_005://金鹰召唤     100
                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean Summon_Skill_ID_40(int SkillID) {
        switch (SkillID) {
            case 11_001_004:// 魂精灵  	 40
            case 12_001_004:// 炎精灵	 20
            case 13_001_004:// 风精灵	 40
            case 14_001_005:// 夜精灵	 40
            case 15_001_004:// 雷精灵	 40
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @param c
     * @param damage
     * @return
     */
    public static int Novice_Skill(int skill, MapleCharacter c, int damage) {
        switch (skill) {
            case 1_000://新手 蜗牛壳
            case 10_001_000://新手 蜗牛壳
            case 20_001_000://战神  蜗牛壳
                if ((c.getStat().getCurrentMaxBaseDamage() <= damage / 13)) {
                    return 1;
                }
        }
        return damage;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Novice_Skill(int skill) {
        switch (skill) {
            case 1_000://新手 蜗牛壳
            case 10_001_000://新手 蜗牛壳
            case 20_001_000://战神  蜗牛壳
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Ares_Skill_140(int skill) {//战神技能
        switch (skill) {
            case 21_100_002:
            case 21_111_005:
            case 21_110_006:
            case 21_000_002:

                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Ares_Skill_350(int skill) {//战神技能
        switch (skill) {
            case 21_110_003:
            case 21_120_002:
            case 21_100_001:
            case 21_110_002:

                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Ares_Skill_800(int skill) {//战神技能
        switch (skill) {
            case 21_120_005:
            case 21_110_004:
            case 21_100_004:

                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Ares_Skill_1500(int skill) {//战神技能
        switch (skill) {
            case 21_120_006:

                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Thief_Skill_270(int skill) {//奇袭者技能
        switch (skill) {
            case 15_001_001:
            case 15_001_002:
            case 15_101_006:
            case 15_110_000:
            case 15_111_004:
            case 15_111_006:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Thief_Skill_420(int skill) {//奇袭者技能
        switch (skill) {
            case 15_111_001:
            case 15_101_003:
            case 15_111_007:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Thief_Skill_650(int skill) {//奇袭者技能
        switch (skill) {
            case 15_111_003:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Night_Knight_Skill_220(int skill) {//夜行者技能
        switch (skill) {
            case 14_001_004:
            case 14_100_005:
            case 14_101_006:
            case 14_111_002:
            case 14_111_005:
            case 14_111_006:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Wind_Knight_Skill_160(int skill) {//风灵使者技能
        switch (skill) {
            case 13_001_003:
            case 13_101_005:
            case 13_111_000:
            case 13_111_001:
            case 13_111_002:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Wind_Knight_Skill_550(int skill) {//风灵使者技能
        switch (skill) {
            case 13_111_006:
            case 13_111_007:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Fire_Knight_Skill_140(int skill) {//魂骑士技能
        switch (skill) {
            case 12_001_003:
            case 12_101_002:
            case 12_101_006:
            case 12_111_005:
            case 12_111_006:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Fire_Knight_Skill_500(int skill) {//魂骑士技能
        switch (skill) {
            case 12_111_003:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Ghost_Knight_Skill_320(int skill) {//魂骑士技能
        switch (skill) {
            case 11_001_002:
            case 11_001_003:
            case 11_101_004:
            case 11_111_002:
            case 11_111_003:
            case 11_111_004:
            case 11_111_006:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Pirate_Skill_290(int skill) {//海盗技能
        switch (skill) {
            case 5_001_001:
            case 5_001_002:
            case 5_001_003:
            case 5_101_002:
            case 5_101_003:
            case 5_201_001:
            case 5_201_002:
            case 5_201_004:
            case 5_201_006:
            case 5_210_000:
            case 5_211_004:
            case 5_211_005:
            case 5_121_007:
            case 5_221_004:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Pirate_Skill_420(int skill) {//海盗技能
        switch (skill) {
            case 5_221_007:
            case 5_211_006:
            case 5_121_004:
            case 5_101_004:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Pirate_Skill_700(int skill) {//海盗技能
        switch (skill) {
            case 5_111_006:
            case 5_220_011:
            case 5_121_005:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Pirate_Skill_810(int skill) {//海盗技能
        switch (skill) {
            case 5_221_008:
            case 5_121_001:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Pirate_Skill_1200(int skill) {//海盗技能
        switch (skill) {
            case 5_221_003:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Thief_Skill_180(int skill) {//飞侠技能
        switch (skill) {
            case 4_001_334:
            case 4_001_344:
            case 4_101_005:
            case 4_111_004:
            case 4_111_005:
            case 4_121_007:
            case 4_201_004:
            case 4_201_005:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Thief_Skill_250(int skill) {//飞侠技能
        switch (skill) {
            case 4_211_004:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Thief_Skill_500(int skill) {//飞侠技能
        switch (skill) {
            case 4_211_002:
            case 4_221_007:
            case 4_221_001:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Bowman_Skill_180(int skill) {//弓箭手技能
        switch (skill) {
            case 3_001_005:
            case 3_101_005:
            case 3_111_003:
            case 3_111_004:
            case 3_111_006:
            case 3_121_003:
            case 3_121_004:
            case 3_221_003:
            case 3_201_005:
            case 3_211_003:
            case 3_211_004:
            case 3_211_006:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Bowman_Skill_260(int skill) {//弓箭手技能
        switch (skill) {
            case 3_201_003:
            case 3_101_003:
            case 3_000_001:
            case 3_001_004:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Bowman_Skill_850(int skill) {//弓箭手技能
        switch (skill) {
            case 3_221_001:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Magician_Skill_90(int skill) {//魔法师技能
        switch (skill) {
            case 2_001_004:
            case 2_001_005:
            case 2_301_005:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Magician_Skill_180(int skill) {//魔法师技能
        switch (skill) {
            case 2_101_004:
            case 2_111_002:
            case 2_111_006:
            case 2_121_003:
            case 2_201_004:
            case 2_211_003:
            case 2_211_006:
            case 2_221_003:
            case 2_221_006:
            case 2_311_004:
            case 2_211_002:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Magician_Skill_240(int skill) {//魔法师技能
        switch (skill) {
            case 2_121_006:
            case 2_321_007:
            case 2_301_002:
            case 2_201_005:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Magician_Skill_670(int skill) {//魔法师技能
        switch (skill) {
            case 2_121_001:
            case 2_221_001:
            case 2_321_001:
            case 2_121_007:
            case 2_221_007:
            case 2_321_008:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Warrior_Skill_900(int skill) {//战士技能
        switch (skill) {
            case 1_221_011:

                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Warrior_Skill_550(int skill) {//战士技能
        switch (skill) {
            case 1_221_009:

                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Warrior_Skill_450(int skill) {//战士技能
        switch (skill) {
            case 1_001_004:
            case 1_001_005:
            case 1_100_002:
            case 1_100_003:
            case 1_111_002:
            case 1_111_008:
            case 1_121_006:
            case 1_121_008:
            case 1_121_009:
            case 1_211_002:
            case 1_221_007:
            case 1_311_001:
            case 1_311_002:
            case 1_311_003:
            case 1_311_004:
            case 1_311_005:
            case 1_311_006:
            case 1_321_003:
                return true;
        }
        return false;
    }

    /**
     *
     * @param skill
     * @return
     */
    public static boolean Warrior_Skill_2000(int skill) {//战士技能
        switch (skill) {
            case 1_111_003:
            case 1_111_004:
            case 1_111_005:
            case 1_111_006:
                return true;
        }
        return false;
    }

    /**
     *
     * @param MobID
     * @return
     */
    public static boolean No_Mob(int MobID) {
        switch (MobID) {
            case 9_300_028: //艾里葛斯
            case 8_510_000: //皮亚奴斯
            case 8_510_100: //嗜血单眼怪 
            case 8_520_000: //皮亚奴斯
            case 8_800_000: //扎昆1
            case 8_800_001: //扎昆2
            case 8_800_002: //扎昆 
            case 8_800_003: //扎昆手臂1 
            case 8_800_004: //扎昆手臂2 
            case 8_800_005: //扎昆手臂3 
            case 8_800_006: //扎昆手臂4 
            case 8_800_007: //扎昆手臂5 
            case 8_800_008: //扎昆手臂6 
            case 8_800_009: //扎昆手臂7 
            case 8_800_010: //扎昆手臂8 
            case 8_810_000: //暗黑龙王的左侧头颅 
            case 8_810_001: //暗黑龙王的右侧头颅 
            case 8_810_002: //暗黑龙王的头A 
            case 8_810_003: //暗黑龙王的头B 
            case 8_810_004: //暗黑龙王的头C 
            case 8_810_005: //暗黑龙王的左手 
            case 8_810_006: //暗黑龙王的右手 
            case 8_810_007: //暗黑龙王的翅膀 
            case 8_810_008: //暗黑龙王的腿 
            case 8_810_009: //暗黑龙王的尾巴 
            case 8_810_010: //死亡暗黑龙王的头A 
            case 8_810_011: //死亡暗黑龙王的头B 
            case 8_810_012: //死亡暗黑龙王的头C 
            case 8_810_013: //死亡暗黑龙王的左手 
            case 8_810_014: //死亡暗黑龙王的右手 
            case 8_810_015: //死亡暗黑龙王的翅膀 
            case 8_810_016: //死亡暗黑龙王的腿 
            case 8_810_017: //死亡暗黑龙王的尾巴 
            case 8_810_018: //暗黑龙王的灵魂 
            case 8_500_001: //帕普拉图斯的座钟 
            case 8_500_002: //帕普拉图斯 
            case 8_220_003: //大海兽  
            case 8_220_004: // 
            case 8_220_005: //玄冰独角兽 
            case 8_220_006: //雷卡 
            case 8_820_000: //的宠儿－品克缤 
            case 8_820_001: //的宠儿－品克缤 
            case 8_820_002: //神雕像 
            case 8_820_003: //贤者所罗门 
            case 8_820_004: //贤者莱克斯 
            case 8_820_005: //火鹰雕像 
            case 8_820_006: //冰鹰雕像 
            case 8_820_007: //比恩宝宝 
            case 8_820_008: //宝宝BOSS召唤用透明怪物 
            case 8_820_009: //t0透明怪物 
            case 8_820_010: //的宠儿－品克缤 
            case 8_820_011: //的宠儿－品克缤 
            case 8_820_012: //的宠儿－品克缤 
            case 8_820_013: //的宠儿－品克缤 
            case 8_820_014: //的宠儿－品克缤 
            case 8_820_015: //贤者所罗门 
            case 8_820_016: //贤者莱克斯 
            case 8_820_017: //火鹰雕像 
            case 8_820_018: //冰鹰雕像 
            case 8_820_019: //神雕像 
            case 8_820_020: //贤者所罗门 
            case 8_820_021: //贤者莱克斯
            case 9_420_520: //克雷塞尔 [1851574]
            case 9_420_521: //克雷塞尔 [1851613]
            case 9_420_522: //克雷塞尔 
            case 9_420_541:
            case 9_420_542:
            case 9_420_543:
            case 9_420_544:
            case 9_420_545:
            case 9_420_546:
            case 9_420_547:
            case 9_420_548:
            case 9_420_549:
            case 9_420_550:

                return true;
        }
        return false;
    }

    /**
     *
     * @param SkillID
     * @return
     */
    public static boolean 不检测技能(int SkillID) {
        switch (SkillID) {
            case 1_321_001:
            case 1_121_001:
            case 1_221_001:
            case 1_111_003:    //狂乱之剑
            case 1_111_008:
            case 1_121_006:
            case 1_221_007:
            case 1_321_003:
            case 21_100_002:
            case 21_100_004:
            case 21_110_004:
                return true;
        }
        return false;
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getMonsterHP(int level) {
        if ((level < 0) || (level >= mobHpVal.length)) {
            return Integer.MAX_VALUE;
        }
        return mobHpVal[level];
    }

    /**
     *
     */
    public static final List<Balloon> lBalloon = new ArrayList<>();

    /**
     *
     * @return
     */
    public static List<Balloon> getBalloons() {
        //Point 0,0 Coordinates: 232, 107
        //Point 96,0 Coordinates: 328, 107
        if (lBalloon.isEmpty()) {
            lBalloon.add(new Balloon(ServerProperties.getProperty("ZeroMS.WorldName") + "冒险岛\r\n【超强独创，修复完美，欢迎各位玩家入驻】---!", 236, 122));
            // lBalloon.add(new Balloon("蓝蜗牛制作!", 396, 276));
            //lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【守候是最长情的告白--蓝蜗牛】!", 96, 243));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 96, 173));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 96, 113));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 96, 293));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 196, 233));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 196, 173));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 196, 113));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 196, 293));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 296, 233));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 296, 173));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 296, 113));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 296, 293));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 396, 233));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 396, 173));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 396, 113));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 396, 293));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 496, 233));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 496, 173));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 496, 113));
//            lBalloon.add(new Balloon(ServerProperties.getProperty("tms.WorldName")+ "冒险岛\r\n【就是牛就是屌】!", 496, 293));
        }
        return lBalloon;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isMarriageRing(int itemId) {
        switch (itemId) {
            case 1_112_300:
            case 1_112_301:
            case 1_112_302:
            case 1_112_303:
            case 1_112_304:
            case 1_112_305:
            case 1_112_306:
            case 1_112_307:
            case 1_112_308:
            case 1_112_309:
            case 1_112_310:
            case 1_112_311:
            case 1_112_315:
            case 1_112_316:
            case 1_112_317:
            case 1_112_318:
            case 1_112_319:
            case 1_112_320:
            case 1_112_803:
            //   case 1112804:
            case 1_112_806:
            case 1_112_807:
            case 1_112_808:
            case 1_112_809:
            case 1_112_013:
                return true;
        }
        return false;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isElseSkill(int id) {
        switch (id) {
            case 10_001_009:
            case 20_001_009:
            case 1_009:
            case 1_020:
            case 10_001_020:
            case 20_001_020:
            case 4_211_006:
                return true;
        }
        return false;
    }

    /**
     *
     * @param chr
     * @param def
     * @param attack
     * @return
     */
    public static double getAttackRange(MapleCharacter chr, MapleStatEffect def, AttackInfo attack) {
        int rangeInc = 50;//chr.getStat().defRange;// 矪瞶环祘戮穨
        double base = 450.0;// 膀娄
        double defRange = ((base + rangeInc) * (base + rangeInc));// 膀娄絛瞅
        if (def != null) {
            // 璸衡絛瞅((maxX * maxX) + (maxY * maxY)) + (м絛瞅 * м絛瞅))
            //    defRange += def.getMaxDistanceSq() + (def.getRange() * def.getRange());
            if (getAttackRangeBySkill(attack) != 0) {// 钡﹚м絛瞅
                defRange = getAttackRangeBySkill(attack);
            }
        } else {// 炊硄ю阑
            defRange = getAttackRangeByWeapon(chr);// 眖猌竟莉絛瞅
        }
        return defRange;
    }

    private static double getAttackRangeBySkill(AttackInfo attack) {
        double defRange = 0;
        switch (attack.skill) {
            case 21_120_006:
                defRange = 800000.0;
                break;
            case 2_121_007:
            case 2_221_007:
            case 2_321_008:
                defRange = 750000.0;
                break;
            case 2_221_006:
            case 3_101_005:
            case 21_101_003:
                defRange = 600000.0;
                break;
            case 2_111_003:
                defRange = 400000.0;
                break;
            case 4_001_344:
            case 1_121_008:
                defRange = 350000.0;
                break;
            case 2_211_002:
                defRange = 300000.0;
                break;
            case 5_110_001:
            case 2_311_004:
            case 2_211_003:
            case 2_001_005:
                defRange = 250000.0;
                break;
            case 5_221_004:
            case 2_321_007:
                defRange = 200000.0;
                break;
            case 20_001_000:
            case 1_000:
                defRange = 130000.0;
                break;
        }
        return defRange;
    }

    private static double getAttackRangeByWeapon(MapleCharacter chr) {
//        IItem weapon_item = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
//        MapleWeaponType weapon = GameConstants.getWeaponType(weapon_item.getItemId());
//        switch (weapon) {
//            case 簀:       // ベ
//                return 200000;
//            case 甅:     // 甅
//                return 250000;
//            case 簀:     // 簀
//            case └:       // └
//            case :       // 
//                return 180000;
//            default:
        return 100_000;

    }

    /**
     *
     * @param mobID
     * @return
     */
    public static int getPartyPlayHP(int mobID) {
        switch (mobID) {
            case 4_250_000: //苔藓蜗牛
                return 836_000;
            case 4_250_001: //苔藓木妖
                return 924_000;
            case 5_250_000: //苔藓蘑菇
                return 1_100_000;
            case 5_250_001: //石头虫
                return 1_276_000;
            case 5_250_002: //原始野猪
                return 1_452_000;
            case 9_400_661:
                return 15_000_000;
            case 9_400_660:
                return 30_000_000;
            case 9_400_659:
                return 45_000_000;
            case 9_400_658:
                return 20_000_000;
        }
        return 0;
    }

    /**
     *
     * @param mobID
     * @return
     */
    public static int getPartyPlayEXP(int mobID) {
        switch (mobID) {
            case 4_250_000: //苔藓蜗牛
                return 5_770;
            case 4_250_001: //苔藓木妖
                return 6_160;
            case 5_250_000: //苔藓蘑菇
                return 7_100;
            case 5_250_001: //石头虫
                return 7_975;
            case 5_250_002: //原始野猪
                return 8_800;
            case 9_400_661:
                return 40_000;
            case 9_400_660:
                return 70_000;
            case 9_400_659:
                return 90_000;
            case 9_400_658:
                return 50_000;
        }
        return 0;
    }

    /**
     *
     * @param mapId
     * @return
     */
    public static int getPartyPlay(int mapId) {
        switch (mapId) {
            case 300_010_000: //艾琳森林 - 苔藓树丛入口
            case 300_010_100: //艾琳森林 - 苔藓树丛西部森林1
            case 300_010_200: //艾琳森林 - 苔藓树丛西部森林2
            case 300_010_300: //艾琳森林 - 苔藓树丛小路
            case 300_010_400: //艾琳森林 - 石头山入口
            case 300_020_000: //艾琳森林 - 苔藓树丛南部森林1
            case 300_020_100: //艾琳森林 - 苔藓树丛南部森林2
            case 300_020_200: //艾琳森林 - 蘑菇山丘入口
            case 300_030_000: //艾琳森林 - 苔藓树丛东部森林

            case 683_070_400:
            case 683_070_401:
            case 683_070_402:
                return 25;
        }
        return 0;
    }

    /**
     *
     * @param mapId
     * @param def
     * @return
     */
    public static int getPartyPlay(int mapId, int def) {
        int dd = getPartyPlay(mapId);
        if (dd > 0) {
            return dd;
        }
        return def / 2;
    }

    /**
     *
     * @param questid
     * @return
     */
    public static boolean isShareQuestInfo(int questid) {
        switch (questid) {
            case 每日签到系统_签到记录:
            case 每日签到系统_当前时间:
                return true;
            default:
                return false;
        }
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getTraitExpNeededForLevel(int level) {
        if ((level < 0) || (level >= cumulativeTraitExp.length)) {
            return Integer.MAX_VALUE;
        }
        return cumulativeTraitExp[level];
    }

    /**
     *
     * @param level
     * @return
     */
    public static int getSetExpNeededForLevel(int level) {
        if ((level < 0) || (level >= setScore.length)) {
            return Integer.MAX_VALUE;
        }
        return setScore[level];
    }
}
