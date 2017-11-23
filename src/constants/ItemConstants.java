
package constants;

import client.inventory.Equip;
import client.inventory.MapleInventoryType;
import java.util.*;
import server.MapleItemInformationProvider;

/**
 * @author PlayDK
 */
public class ItemConstants {

    /*
     * 内在能力系统
     */

    /**
     *
     */

    public static final int[] rankC = {70_000_000, 70_000_001, 70_000_002, 70_000_003, 70_000_004, 70_000_005, 70_000_006, 70_000_007, 70_000_008, 70_000_009, 70_000_010, 70_000_011, 70_000_012, 70_000_013};

    /**
     *
     */
    public static final int[] rankB = {70_000_014, 70_000_015, 70_000_017, 70_000_018, 70_000_021, 70_000_022, 70_000_023, 70_000_024, 70_000_025, 70_000_026};

    /**
     *
     */
    public static final int[] rankA = {70_000_027, 70_000_028, 70_000_029, 70_000_030, 70_000_031, 70_000_032, 70_000_033, 70_000_034, 70_000_035, 70_000_036};

    /**
     *
     */
    public static final int[] rankS = {70_000_048, 70_000_049, 70_000_050, 70_000_051, 70_000_052, 70_000_053, 70_000_054, 70_000_055, 70_000_056, 70_000_057, 70_000_058, 70_000_059, 70_000_060, 70_000_061, 70_000_062};

    /**
     *
     */
    public static final int[] circulators = {2_700_000, 2_700_100, 2_700_200, 2_700_300, 2_700_400, 2_700_500, 2_700_600, 2_700_700, 2_700_800, 2_700_900, 2_701_000};

    /**
     *
     */
    public static final int[] rankBlock = {
        70_000_016, //攻击速度提升 - rankB
    70_000_037, //阿斯旺解放战，攻击塔时，伤害增加x% - rankA
    70_000_038, //攻击解放战补给模式的普通怪物时，有x%的概率造成一击必杀效果 - rankA
    70_000_039, //攻击昏迷，黑暗，冻结的状态异常对象时，伤害增加x% - rankA
    70_000_040, //命中值提升伤害 - 根据物理命中值和魔法命中值中较高数值的x%，增加格外伤害 - rankA
    70_000_041, //物防提升伤害 - 增加物理防御力的x%的伤害 - rankA
    70_000_042, //魔防提升伤害 - 增加魔法防御力的x%的伤害 - rankA
    70_000_043, //受到魔攻减少伤害 - 受到魔法攻击时，无视相当于物理防御力x%的伤害 - rankS
    70_000_044, //受到物功减少伤害 - 受到物理攻击时，无视相当于魔法防御力x%的伤害 - rankS
    70_000_045, //有一定概率无冷却时间 - 使用技能后，有x%概率无冷却时间。使用无冷却时间的技能时无效。 - rankS
    70_000_046, //被动技能等级加1 - 被动技能的技能等级增加1级。但对既有主动效果，又有被动效果的技能无效。 - rankS
    70_000_047 //增加群功技能对象数 - 群攻技能的攻击对象数量增加1 - rankS
    };

    /**
     *
     */
    public static final int[] 灵魂结晶 = new int[]{2_591_010, 2_591_011, 2_591_012, 2_591_013, 2_591_014, 2_591_015, 2_591_016, 2_591_017, 2_591_018, 2_591_019, 2_591_020, 2_591_021, 2_591_022, 2_591_023, 2_591_024, 2_591_025, 2_591_026, 2_591_027, 2_591_028, 2_591_029, 2_591_030, 2_591_031, 2_591_032, 2_591_033, 2_591_034, 2_591_035, 2_591_036, 2_591_037, 2_591_038, 2_591_039, 2_591_040, 2_591_041, 2_591_042, 2_591_043, 2_591_044, 2_591_045, 2_591_046, 2_591_047, 2_591_048, 2_591_049, 2_591_050, 2_591_051, 2_591_052, 2_591_053, 2_591_054, 2_591_055, 2_591_056, 2_591_057, 2_591_058, 2_591_059, 2_591_060, 2_591_061, 2_591_062, 2_591_063, 2_591_064, 2_591_065, 2_591_066, 2_591_067, 2_591_068, 2_591_069, 2_591_070, 2_591_071, 2_591_072, 2_591_073, 2_591_074, 2_591_075, 2_591_076, 2_591_077, 2_591_078, 2_591_079, 2_591_080, 2_591_081, 2_591_082, 2_591_085, 2_591_086, 2_591_087, 2_591_088, 2_591_089, 2_591_090, 2_591_091, 2_591_092, 2_591_093, 2_591_094, 2_591_095, 2_591_096, 2_591_097, 2_591_098, 2_591_099, 2_591_100, 2_591_101, 2_591_102, 2_591_103, 2_591_104, 2_591_105, 2_591_106, 2_591_107, 2_591_108, 2_591_109, 2_591_110, 2_591_111, 2_591_112, 2_591_113, 2_591_114, 2_591_115, 2_591_116, 2_591_117, 2_591_118, 2_591_119, 2_591_120, 2_591_121, 2_591_122, 2_591_123, 2_591_124, 2_591_125, 2_591_126, 2_591_127, 2_591_128, 2_591_129, 2_591_130, 2_591_131, 2_591_132, 2_591_133, 2_591_134, 2_591_135, 2_591_136, 2_591_137, 2_591_138, 2_591_139, 2_591_140, 2_591_141, 2_591_142, 2_591_143, 2_591_144, 2_591_145, 2_591_146, 2_591_147, 2_591_148, 2_591_149, 2_591_150, 2_591_151, 2_591_152, 2_591_153, 2_591_154, 2_591_155, 2_591_156, 2_591_157, 2_591_158, 2_591_159, 2_591_160, 2_591_161, 2_591_162, 2_591_163, 2_591_164, 2_591_165, 2_591_166, 2_591_167, 2_591_168, 2_591_169, 2_591_170, 2_591_171, 2_591_172, 2_591_173, 2_591_174, 2_591_175, 2_591_176, 2_591_177, 2_591_178, 2_591_179, 2_591_180, 2_591_181, 2_591_182, 2_591_183, 2_591_184, 2_591_185, 2_591_186, 2_591_187, 2_591_188, 2_591_189, 2_591_190, 2_591_191, 2_591_192, 2_591_193, 2_591_194, 2_591_195, 2_591_196, 2_591_197, 2_591_198, 2_591_199, 2_591_200, 2_591_201, 2_591_202, 2_591_203, 2_591_204, 2_591_205, 2_591_206, 2_591_207, 2_591_208, 2_591_209, 2_591_210, 2_591_211, 2_591_212, 2_591_213, 2_591_214, 2_591_215, 2_591_216, 2_591_217, 2_591_218, 2_591_219, 2_591_220, 2_591_221, 2_591_222, 2_591_223, 2_591_224, 2_591_225, 2_591_226, 2_591_227, 2_591_228, 2_591_229, 2_591_230, 2_591_231, 2_591_232, 2_591_233, 2_591_234, 2_591_235, 2_591_236, 2_591_237, 2_591_238, 2_591_239, 2_591_240, 2_591_241, 2_591_242, 2_591_243, 2_591_244, 2_591_245, 2_591_246, 2_591_247, 2_591_248, 2_591_249, 2_591_250, 2_591_251, 2_591_252, 2_591_253, 2_591_254, 2_591_255, 2_591_256, 2_591_257, 2_591_258, 2_591_259, 2_591_260, 2_591_261, 2_591_262, 2_591_263, 2_591_264, 2_591_265, 2_591_266, 2_591_267, 2_591_268, 2_591_269, 2_591_270, 2_591_271, 2_591_272, 2_591_273, 2_591_274, 2_591_275, 2_591_276, 2_591_277, 2_591_278, 2_591_279, 2_591_288, 2_591_289, 2_591_290, 2_591_291, 2_591_292, 2_591_293, 2_591_294, 2_591_295, 2_591_296, 2_591_297, 2_591_298, 2_591_299, 2_591_300, 2_591_301, 2_591_302, 2_591_303, 2_591_304, 2_591_305, 2_591_306, 2_591_307, 2_591_308, 2_591_309, 2_591_310, 2_591_311, 2_591_312, 2_591_313, 2_591_314, 2_591_315, 2_591_316, 2_591_317, 2_591_318, 2_591_319, 2_591_320, 2_591_321, 2_591_322, 2_591_323, 2_591_324, 2_591_325, 2_591_326, 2_591_327, 2_591_328, 2_591_329, 2_591_330, 2_591_331, 2_591_332, 2_591_333, 2_591_334, 2_591_335, 2_591_336, 2_591_337, 2_591_338, 2_591_339, 2_591_340, 2_591_341, 2_591_342, 2_591_343, 2_591_344, 2_591_345, 2_591_346, 2_591_347, 2_591_348, 2_591_349, 2_591_350, 2_591_351, 2_591_352, 2_591_353, 2_591_354, 2_591_355, 2_591_356, 2_591_357, 2_591_358, 2_591_359, 2_591_360, 2_591_361, 2_591_362, 2_591_363, 2_591_364, 2_591_365, 2_591_366, 2_591_367, 2_591_368, 2_591_369, 2_591_370, 2_591_371, 2_591_372, 2_591_373, 2_591_374, 2_591_375, 2_591_376, 2_591_377, 2_591_378, 2_591_379, 2_591_380, 2_591_381};

    /**
     *
     */
    public static final short[] 灵魂结晶技能 = new short[]{177, 102, 103, 104, 131, 132, 201, 101, 102, 103, 104, 131, 132, 201, 105, 106, 107, 108, 133, 134, 202, 105, 106, 107, 108, 133, 134, 202, 109, 110, 111, 112, 135, 136, 203, 113, 114, 115, 116, 204, 151, 152, 137, 403, 603, 121, 122, 123, 124, 206, 155, 156, 139, 403, 603, 117, 118, 119, 120, 207, 153, 154, 138, 403, 603, 167, 168, 169, 170, 208, 171, 172, 177, 0, 0, 0, 0, 101, 102, 103, 104, 131, 132, 201, 101, 102, 103, 104, 131, 132, 201, 105, 106, 107, 108, 133, 134, 202, 105, 106, 107, 108, 133, 134, 202, 109, 110, 111, 112, 135, 136, 203, 113, 114, 115, 116, 204, 151, 152, 137, 117, 118, 119, 120, 207, 153, 154, 138, 121, 122, 123, 124, 206, 155, 156, 139, 101, 102, 103, 104, 131, 132, 201, 163, 164, 165, 166, 210, 151, 152, 175, 0, 101, 102, 103, 104, 131, 132, 201, 163, 164, 165, 166, 210, 151, 152, 175, 167, 168, 169, 170, 208, 171, 172, 177, 179, 180, 181, 182, 183, 184, 201, 185, 186, 187, 188, 205, 153, 154, 189, 0, 179, 180, 181, 182, 183, 184, 201, 185, 186, 187, 188, 205, 153, 154, 189, 109, 110, 111, 112, 135, 136, 203, 117, 118, 119, 120, 207, 153, 154, 138, 0, 109, 110, 111, 112, 135, 136, 203, 117, 118, 119, 120, 205, 153, 154, 138, 101, 102, 103, 104, 131, 132, 201, 167, 168, 169, 170, 208, 173, 172, 177, 0, 101, 102, 103, 104, 131, 132, 201, 167, 168, 169, 170, 208, 173, 172, 177, 167, 168, 169, 170, 208, 171, 172, 177, 0, 121, 186, 187, 188, 205, 153, 154, 189, 0, 185, 186, 187, 188, 207, 153, 154, 189, 0, 185, 186, 187, 188, 205, 153, 154, 189, 0, 185, 186, 187, 188, 207, 153, 154, 189, 0, 185, 186, 187, 188, 206, 153, 154, 189, 0, 121, 186, 187, 188, 205, 153, 154, 189, 185, 186, 187, 188, 205, 153, 154, 189, 185, 186, 187, 188, 205, 153, 154, 189, 185, 186, 187, 188, 207, 153, 154, 189, 185, 186, 187, 188, 206, 153, 154, 189};

    /**
     *
     */
    public static final int[] 航海材料 = new int[]{3_100_000, 3_100_001, 3_100_002, 3_100_003, 3_100_004, 3_100_005, 3_100_006, 3_100_007, 3_100_008, 3_100_010, 3_100_011};

    // 150套装

    /**
     *
     * @return
     */
    public static int[] fa() {
        return new int[]{2_510_538, 2_510_539, 2_510_540, 2_510_541, 2_510_542, 2_510_543, 2_510_544, 2_510_545, 2_510_546, 2_510_547, 2_510_548, 2_510_549, 2_510_550, 2_510_551, 2_510_552, 2_510_553, 2_510_554, 2_510_555, 2_510_556, 2_510_557, 2_510_558, 2_510_559, 2_510_560, 2_510_561, 2_510_562, 2_510_563, 2_510_564, 2_510_565, 2_510_566, 2_510_567, 2_510_621, 2_510_255, 2_510_256, 2_510_257, 2_510_258, 2_510_259, 2_510_528, 2_510_529, 2_510_530, 2_510_531, 2_510_532, 2_510_533, 2_510_534};
    }

    // 君主英勇配方

    /**
     *
     * @return
     */
    public static int[] fb() {
        return new int[]{2_510_483, 2_510_484, 2_510_485, 2_510_486, 2_510_487, 2_510_488, 2_510_489, 2_510_490, 2_510_491, 2_510_492, 2_510_493, 2_510_494, 2_510_495, 2_510_496, 2_510_497, 2_510_498, 2_510_499, 2_510_500, 2_510_501, 2_510_502, 2_510_503, 2_510_504, 2_510_505, 2_510_506, 2_510_507, 2_510_508, 2_510_509, 2_510_510, 2_510_511, 2_510_512, 2_510_513, 2_510_514, 2_510_515, 2_510_516, 2_510_517, 2_510_518, 2_510_519, 2_510_520, 2_510_521, 2_510_522, 2_510_523, 2_510_524, 2_510_525, 2_510_526, 2_510_527};
    }

    // 矿石

    /**
     *
     * @return
     */
    public static int[] fc() {
        return new int[]{4_021_000, 4_021_001, 4_021_002, 4_021_003, 4_021_004, 4_021_005, 4_021_006, 4_021_007, 4_021_008, 4_021_009, 4_021_012, 4_021_010, 4_021_013, 4_021_014, 4_021_015, 4_021_016, 4_021_019, 4_021_020, 4_021_021, 4_021_022, 4_021_031, 4_021_032, 4_021_033, 4_021_034, 4_021_035, 4_021_036, 4_021_037, 4_021_038, 4_021_039, 4_021_040, 4_021_041, 4_021_042, 4_023_023, 4_023_024, 4_023_025, 4_023_026, 4_023_021, 4_023_022};
    }

    /**
     *
     * @return
     */
    public static List<Integer> getNewGoldenMapleItem() {
        return Arrays.asList(1_462_116, 1_342_039, 1_402_109, 1_472_139, 1_332_147, 1_322_105, 1_442_135, 1_452_128, 1_312_071, 1_382_123, 1_492_100, 1_372_099, 1_432_098, 1_422_072, 1_302_172, 1_482_101, 1_412_070);
    }

    /**
     *
     * @return
     */
    public static List<Integer> getPlatinumItem() {
        return Arrays.asList(1_003_243, 1_052_358, 1_072_522, 1_082_315, 1_102_295, 1_132_093, 1_152_061, 1_332_145, 1_402_107, 1_442_133, 1_462_114, 1_472_137, 1_532_070, 1_522_066, 1_452_126, 1_312_069, 1_382_121, 1_492_098, 1_372_097, 1_362_058, 1_432_096, 1_422_070, 1_302_170, 1_482_099, 1_412_068);
    }

    /**
     *
     * @return
     */
    public static List<Integer> getZijinItem() {
        return Arrays.asList(1_462_159, 1_462_156, 1_402_145, 1_402_151, 1_052_461, 1_052_457, 1_532_073, 1_532_074, 1_472_177, 1_472_179, 1_332_186, 1_332_193, 1_322_154, 1_322_162, 1_442_173, 1_442_182, 1_522_068, 1_522_071, 1_452_165, 1_312_114, 1_312_116, 1_382_160, 1_132_154, 1_132_151, 1_072_666, 1_072_660, 1_212_069, 1_212_068, 1_492_152, 1_492_138, 1_372_139, 1_372_131, 1_222_063, 1_222_064, 1_082_433, 1_082_430, 1_362_060, 1_362_067, 1_432_138, 1_432_135, 1_152_088, 1_152_089, 1_003_529, 1_003_552, 1_422_107, 1_422_105, 1_232_070, 1_232_063, 1_302_227, 1_302_212, 1_113_036, 1_113_035, 1_112_743, 1_112_742, 1_482_140, 1_482_138, 1_242_048, 1_242_075, 1_412_102, 1_412_014, 1_102_394, 1_102_441);
    }

    /**
     *
     * @return
     */
    public static List<Integer> get11thItem() {
        return Arrays.asList(1_004_172, 1_012_471, 1_052_758, 1_102_691, 1_122_280, 1_212_095, 1_222_089, 1_232_089, 1_242_095, 1_302_304, 1_312_179, 1_322_230, 1_332_254, 1_342_094, 1_362_115, 1_372_201, 1_382_239, 1_402_229, 1_412_158, 1_422_165, 1_432_194, 1_442_248, 1_452_232, 1_462_219, 1_472_241, 1_482_196, 1_492_205, 1_522_118, 1_532_124);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isHarvesting(int itemId) {
        return itemId >= 1_500_000 && itemId < 1_520_000;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is飞镖道具(int itemId) {
        return itemId / 10_000 == 207;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is子弹道具(int itemId) {
        return itemId / 10_000 == 233;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isRechargable(int itemId) {
        return is飞镖道具(itemId) || is子弹道具(itemId);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isOverall(int itemId) {
        return itemId / 10_000 == 105;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isPet(int itemId) {
        return itemId / 10_000 == 500;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is弩矢道具(int itemId) {
        return itemId >= 2_061_000 && itemId < 2_062_000;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is弓矢道具(int itemId) {
        return itemId >= 2_060_000 && itemId < 2_061_000;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isMagicWeapon(int itemId) {
        int type = itemId / 10_000;
        return type == 137 || type == 138 || type == 121;
    }

    /*
     * 检测装备是否是武器
     * 夜光武器: 1212000 开始
     * 萝莉武器: 1222000 开始
     * 双弩武器: 1522000 - 1522054
     * 火炮武器: 1532000 - 1532058
     * 复仇武器: 1232000 - 1232056
     * 尖兵武器: 1242000 - 1242059
     * 驯兽武器: 1252000 - 1252063
     */

    /**
     *
     * @param itemid
     * @return
     */

    public static boolean isWeapon(int itemid) {
        return is双头杖(itemid) || is手铳(itemid) || is恶魔剑(itemid) || is能量剑(itemid) || is魔法棒(itemid) || isESP限制器(itemid) || is单手剑(itemid) || is单手斧(itemid) || is单手钝器(itemid) || is短刀(itemid) || is手杖(itemid) || is短杖(itemid) || is长杖(itemid) || is双手剑(itemid) || is双手斧(itemid) || is双手钝器(itemid) || is长枪(itemid) || is长矛(itemid) || is弓(itemid) || is弩(itemid) || is拳套(itemid) || is指节(itemid) || is短枪(itemid) || is双弩枪(itemid) || is火炮(itemid) || is太刀(itemid) || is扇子(itemid) || is锋利之影(itemid) || is阔影剑(itemid) || is爆破拳套(itemid);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static MapleInventoryType getInventoryType(int itemId) {
        byte type = (byte) (itemId / 1_000_000);
        if ((type < 1 || type > 5) && type != 9 && type != 36) {
            return MapleInventoryType.UNDEFINED;
        }
        return MapleInventoryType.getByType(type);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isCap(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 0;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is脸饰(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 1;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is眼饰(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 2;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is耳环(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 3;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isCoat(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 4;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isLongcoat(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 5;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isPants(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 6;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isShoes(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 7;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isGlove(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 8;
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
    public static boolean isCape(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 10;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is戒指(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 11;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is项链(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 12;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is腰带(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 13;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is勋章(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 14;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isShoulders(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 15;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is口袋(final int itemId) {
        int cat = itemId / 10_000;
        cat %= 100;
        return cat == 16;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is徽章(int itemid) {
        return itemid / 10_000 == 118;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is纹章(int itemid) {
        return itemid / 10_000 == 119;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is图腾(int itemid) {
        return itemid / 10_000 == 120;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is双头杖(int itemid) {
        return itemid / 10_000 == 121;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is手铳(int itemid) {
        return itemid / 10_000 == 122;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is恶魔剑(int itemid) {
        return itemid / 10_000 == 123;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is能量剑(int itemid) {
        return itemid / 10_000 == 124;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is魔法棒(int itemid) {
        return itemid / 10_000 == 125;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean isESP限制器(int itemid) {
        return itemid / 10_000 == 126;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is单手剑(int itemid) {
        return itemid / 10_000 == 130;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is单手斧(int itemid) {
        return itemid / 10_000 == 131;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is单手钝器(int itemid) {
        return itemid / 10_000 == 132;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is短刀(int itemid) {
        return itemid / 10_000 == 133;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is双手刀(int itemid) {
        return itemid / 10_000 == 134;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is手杖(int itemid) {
        return itemid / 10_000 == 136;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is短杖(int itemid) {
        return itemid / 10_000 == 137;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is长杖(int itemid) {
        return itemid / 10_000 == 138;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is双手剑(int itemid) {
        return itemid / 10_000 == 140;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is双手斧(int itemid) {
        return itemid / 10_000 == 141;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is双手钝器(int itemid) {
        return itemid / 10_000 == 142;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is长枪(int itemid) {
        return itemid / 10_000 == 143;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is长矛(int itemid) {
        return itemid / 10_000 == 144;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is弓(int itemid) {
        return itemid / 10_000 == 145;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is弩(int itemid) {
        return itemid / 10_000 == 146;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is拳套(int itemid) {
        return itemid / 10_000 == 147;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is指节(int itemid) {
        return itemid / 10_000 == 148;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is短枪(int itemid) {
        return itemid / 10_000 == 149;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is双弩枪(int itemid) {
        return itemid / 10_000 == 152;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is火炮(int itemid) {
        return itemid / 10_000 == 153;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is太刀(int itemid) {
        return itemid / 10_000 == 154;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is扇子(int itemid) {
        return itemid / 10_000 == 155;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is进化装备(int itemid) {
        return itemid / 100_000 == 36;
    }

    /**
     * 神之子主武器
     *
     * @param itemid
     * @return
     */
    public static boolean is阔影剑(int itemid) {
        return itemid / 10_000 == 156;
    }

    /**
     * 神之子副武器
     *
     * @param itemid
     * @return
     */
    public static boolean is锋利之影(int itemid) {
        return itemid / 10_000 == 157;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is爆破拳套(int itemid) {
        return itemid / 10_000 == 158;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean isPetEquip(int itemid) {
        return itemid / 10_000 == 180;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is饰品(int itemid) {
        return is脸饰(itemid) || is眼饰(itemid) || is耳环(itemid) || is戒指(itemid) || is项链(itemid) || is腰带(itemid) || is勋章(itemid) || isShoulders(itemid) || is口袋(itemid) || is徽章(itemid) || is纹章(itemid) || is图腾(itemid);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isEquip(int itemId) {
        return itemId / 1_000_000 == 1;
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
        return !(itemId >= 2_049_105 && itemId <= 2_049_110) && itemId / 100 == 20_491;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isSealItem(int itemId) {
        return itemId / 100 == 20_495;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isSealAddItem(int itemId) {
        return itemId >= 2_048_200 && itemId <= 2_048_304;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isLimitBreakScroll(int itemId) {
        return itemId / 100 == 26_140;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static int getChaosNumber(int itemId) {
        switch (itemId) {
            case 2_049_116: //2049116 - 强化混沌卷轴 - 随机变换装备的属性。不能用于现金道具。\n成功率：60%
                return 10;
            case 2_049_119: //2049119 - 惊人混沌卷轴 - 和混沌卷轴相比，可以把装备当前属性变得更好或更坏。不能用于现金道具。\n成功率：60%
            case 2_049_132: //2049132 - 惊人混沌卷轴30% - 将装备的当前属性调整的比混沌卷轴更好或更坏。
            case 2_049_133: //2049133 - 惊人混沌卷轴50% - 将装备的当前属性调整的比混沌卷轴更好或更坏。
            case 2_049_134: //2049134 - 惊人混沌卷轴70% - 将装备的当前属性调整的比混沌卷轴更好或更坏。
                return 8;
            case 2_049_135: //2049135 - 惊人正义混沌卷轴 20% - 可不降低装备的当前属性，将装备的当前属性调整的比混沌卷轴更好或更坏。现金道具无法使用。
            case 2_049_136: //2049136 - 惊人正义混沌卷轴 20% - 可不降低装备的当前属性，将装备的当前属性调整的比混沌卷轴更好或更坏。现金道具无法使用。
            case 2_049_137: //2049137 - 惊人正义混沌卷轴 40% - 可不降低装备的当前属性，将装备的当前属性调整的比混沌卷轴更好或更坏。现金道具无法使用。
                return 7;
            default:
                return 5;
        }
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isChaosForGoodness(int itemId) {
        if (!isChaosScroll(itemId)) {
            return false;
        }
        switch (itemId) {
            case 2_049_122: //2049122 - 正向混沌卷轴 - 可以使装备的属性变得更好。无法用于现金物品。
            case 2_049_124: //2049124 - 正向混沌卷轴 - 可以使装备的属性变得更好。无法用于现金物品。
            case 2_049_127: //2049127 - 冒险勇士的肯定混沌卷轴 5% - 在不减少冒险勇士防具当前属性的前提下重新调整。
            case 2_049_129: //2049129 - 正义混沌卷轴 50% - 可不降低装备的当前属性，进行再调节。现金道具无法使用。
            case 2_049_130: //2049130 - 正义混沌卷轴 30% - 可不降低装备的当前属性，进行再调节。现金道具无法使用。
            case 2_049_131: //2049131 - 正义混沌卷轴 20% - 可不降低装备的当前属性，进行再调节。现金道具无法使用。
            case 2_049_135: //2049135 - 惊人正义混沌卷轴 20% - 可不降低装备的当前属性，将装备的当前属性调整的比混沌卷轴更好或更坏。现金道具无法使用。
            case 2_049_136: //2049136 - 惊人正义混沌卷轴 20% - 可不降低装备的当前属性，将装备的当前属性调整的比混沌卷轴更好或更坏。现金道具无法使用。
            case 2_049_137: //2049137 - 惊人正义混沌卷轴 40% - 可不降低装备的当前属性，将装备的当前属性调整的比混沌卷轴更好或更坏。现金道具无法使用。
            case 2_049_140: //
            case 2_049_155: //2049155 - 惊人正义混沌卷轴 50% - 可不降低装备的当前属性，将装备的当前属性调整的比混沌卷轴更好或更坏。现金道具无法使用。
                return true;
        }
        return false;
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
    public static boolean isAdvancedEquipScroll(int scrollId) {
        return scrollId == 2_049_323;
    }

    /*
     * 还原卷轴
     */

    /**
     *
     * @param scrollId
     * @return
     */

    public static boolean isResetScroll(int scrollId) {
        return scrollId / 100 == 20_496;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isPotentialScroll(int scrollId) {
        return scrollId / 100 == 20_494 || scrollId / 100 == 20_497 || scrollId == 5_534_000;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isPotentialAddScroll(int scrollId) {
        switch (scrollId) {
            case 2_048_305: //附加潜能附加卷轴 - 不减少可升级次数，为装备增加附加潜能。\n#c只能用于没有附加潜能的道具#
            case 2_048_306: //特殊附加潜能附加古卷 - 不减少可升级次数，为装备增加附加潜能#c3个#。\n#c只能用于没有附加潜能的道具#
            case 2_048_307: //特殊附加潜能附加卷轴 - 不减少可升级次数，为装备增加附加潜能。 \n#c只能用于没有附加潜能的道具#
            case 2_048_308: //附加潜能附加卷轴 - 不减少可升级次数，为装备增加附加潜能。 \n#c只能用于没有附加潜能的道具#
            case 2_048_309: //附加潜能附加卷轴 - 不减少可升级次数，为装备增加附加潜能。 \n#c只能用于没有附加潜能的道具#
            case 2_048_310: //附加潜能附加卷轴 - 不减少可升级次数，为装备增加附加潜能。 \n#c只能用于没有附加潜能的道具#
            case 2_048_314: //附加潜能附加卷轴 - 可以在不扣减升级次数的情况下，在装备道具上增加附加潜能。\n#c只能在拥有带有潜能，但没有附加潜能的道具上使用#
            case 2_048_315: //特殊附加潜能卷轴 - 可以在不扣减升级次数的情况下，在装备道具上增加附加潜能。\n#c只能在拥有带有潜能，但没有附加潜能的道具上使用#
                return true;
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is真觉醒冒险之心(int itemId) {
        switch (itemId) {
            case 1_122_122: //真·觉醒冒险之心 - (无描述)
            case 1_122_123: //真·觉醒冒险之心 - (无描述)
            case 1_122_124: //真·觉醒冒险之心 - (无描述)
            case 1_122_125: //真·觉醒冒险之心 - (无描述)
            case 1_122_126: //真·觉醒冒险之心 - (无描述)
                return true;
        }
        return false;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isSpecialScroll(int scrollId) {
        switch (scrollId) {
            case 2_040_727: //鞋子防滑卷轴 - 给鞋子增加防滑功能.成功率:10%, 对强化次数没有影响
            case 2_041_058: //披风防寒卷轴 - 给披肩增加防寒功能.成功率:10%, 对强化次数没有影响
            case 2_530_000: //幸运日卷轴 - 使接下去使用的卷轴的成功率提高10%。潜能附加卷轴、强化卷轴无效
            case 2_530_001: //快乐日幸运卷轴 - 使接下去使用的卷轴的成功率提高10%。对潜能附加卷轴、强化卷轴无效
            case 2_531_000: //防爆卷轴 - 在装备物品上使用，可以在使用卷轴失败时防止装备物品损坏，仅限1次。但是使用卷轴成功时，防御效果也会消失，强化12星以上的物品无法使用
            case 5_063_100: //幸运保护之盾 - 保护物品，以及提升成功概率的魔法卷轴。在装备物品上使用，可以提升使用卷轴的成功率10%，并且可以防止装备物品损坏，#c仅限1次#。但是使用卷轴成功时，魔法卷轴效果也会随之消失，#c强化12星以上的物品无法使用#。
            case 5_064_000: //防爆卷轴 - 保护物品的魔法盾。在装备物品上使用，可以在使用卷轴失败时防止装备物品损坏，#c仅限1次#。但是使用卷轴成功时，防御效果也会消失，#c强化12星以上的物品无法使用。# \n可以和#c安全之盾、复原之盾#一起使用。
            case 5_064_003: //极真保护之盾 - #极真道具专用#防爆卷轴。用在极真装备后，可以在使用卷轴失败时防止装备物品损坏，#c仅限1次#。但是使用卷轴成功时，防御效果也会消失，#c强化7星以上的物品无法使用。# \n可以和#c保护卷轴、卷轴防护卷轴#一起使用。
            case 5_064_100: //保护卷轴 - 保护物品的魔法盾。在装备物品上使用，可以在使用卷轴失败时防止装备物品#c可升级次数#减少，#c仅限1次#。但是使用卷轴成功时，防御效果也会消失。\n可以和#c安全之盾、复原之盾#一起使用。
            case 5_064_200: //完美还原卷轴 - 除了潜在能力的其他属性都#c初始化#为标准能力值，只能使用在装备道具,如果是成长性道具可以复原为成长之前的状态。
            case 5_064_300: //卷轴防护卷轴 - 卷轴使用失败时，可以保护卷轴不消失的魔法防护卷轴. \n使用在装备道具上时 #c添加一次保护机会#，如果卷轴使用失败时#c使用的卷轴不会消失#。但是,卷轴使用成功时也会消耗保护效果。\n可以和#c保护卷轴,防爆卷轴#一起使用。
            case 5_068_100: //宠物专用保护卷轴 - 可保护道具的魔法盾。对 #c宠物装备#使用后可在使用卷轴失败时不减少装备道具的#c强化次数#,#c只限1次#。 但是使用卷轴成功时，防御效果也会消失。\n可以和#c安全之盾、复原之盾#一起使用。
            case 5_068_200: //宠物专用卷轴防护卷轴 - 卷轴使用失败时，可以保护卷轴不消失的魔法防护卷轴. \n使用在#c宠物装备道具#上时 #c添加一次保护机会#，如果卷轴使用失败时#c使用的卷轴不会消失#。但是,卷轴使用成功时也会消耗保护效果。\n可以和#c保护卷轴,防爆卷轴#一起使用。
                return true;
        }
        return false;
    }

    /**
     *
     * @param scrollId
     * @return
     */
    public static boolean isAzwanScroll(int scrollId) {
        switch (scrollId) {
            case 2_046_060: //阿斯旺单手武器攻击力卷轴 - 为单手武器增加攻击力属性。\n成功率：60%，物理攻击力+4，消耗升级次数2
            case 2_046_061: //阿斯旺单手武器攻击力卷轴 - 为单手武器增加攻击力属性。\n成功率：50%，物理攻击力+6，消耗升级次数3
            case 2_046_062: //阿斯旺单手武器攻击力卷轴 - 为单手武器增加攻击力属性。\n成功率：40%，物理攻击力+8，消耗升级次数4
            case 2_046_063: //阿斯旺单手武器攻击力卷轴 - 为单手武器增加攻击力属性。\n成功率：30%，物理攻击力+10，消耗升级次数5
            case 2_046_064: //阿斯旺单手武器攻击力卷轴 - 为单手武器增加攻击力属性。\n成功率：20%，物理攻击力+12，消耗升级次数6

            case 2_046_065: //阿斯旺单手武器魔力卷轴 - 为单手武器增加魔法攻击力属性。\n成功率：60%，魔法攻击力+4，消耗升级次数2
            case 2_046_066: //阿斯旺单手武器魔力卷轴 - 为单手武器增加魔法攻击力属性。\n成功率：50%，魔法攻击力+6，消耗升级次数3
            case 2_046_067: //阿斯旺单手武器魔力卷轴 - 为单手武器增加魔法攻击力属性。\n成功率：40%，魔法攻击力+8，消耗升级次数4
            case 2_046_068: //阿斯旺单手武器魔力卷轴 - 为单手武器增加魔法攻击力属性。\n成功率：30%，魔法攻击力+10，消耗升级次数5
            case 2_046_069: //阿斯旺单手武器魔力卷轴 - 为单手武器增加魔法攻击力属性。\n成功率：20%，魔法攻击力+12，消耗升级次数6

            case 2_046_141: //阿斯旺双手武器攻击力卷轴 - 为双手武器增加攻击力属性。\n成功率：60%，物理攻击力+4，消耗升级次数2
            case 2_046_142: //阿斯旺双手武器攻击力卷轴 - 为双手武器增加攻击力属性。\n成功率：50%，物理攻击力+6，消耗升级次数3
            case 2_046_143: //阿斯旺双手武器攻击力卷轴 - 为双手武器增加攻击力属性。\n成功率：40%，物理攻击力+8，消耗升级次数4
            case 2_046_144: //阿斯旺双手武器攻击力卷轴 - 为双手武器增加攻击力属性。\n成功率：30%，物理攻击力+10，消耗升级次数5
            case 2_046_145: //阿斯旺双手武器攻击力卷轴 - 为双手武器增加攻击力属性。\n成功率：20%，物理攻击力+12，消耗升级次数6

            case 2_046_519: //阿斯旺防具力量卷轴 - 为防具增加力量属性。\n成功率：60%，力量+6，消耗升级次数3
            case 2_046_520: //阿斯旺防具力量卷轴 - 为防具增加力量属性。\n成功率：50%，力量+8，消耗升级次数4
            case 2_046_521: //阿斯旺防具力量卷轴 - 为防具增加力量属性。\n成功率：40%，力量+10，消耗升级次数5
            case 2_046_522: //阿斯旺防具敏捷卷轴 - 为防具增加敏捷属性。\n成功率：60%，敏捷+6，消耗升级次数3
            case 2_046_523: //阿斯旺防具敏捷卷轴 - 为防具增加敏捷属性。\n成功率：50%，敏捷+8，消耗升级次数4
            case 2_046_524: //阿斯旺防具敏捷卷轴 - 为防具增加敏捷属性。\n成功率：40%，敏捷+10，消耗升级次数5
            case 2_046_525: //阿斯旺防具智力卷轴 - 为防具增加智力属性。\n成功率：60%，智力+6，消耗升级次数3
            case 2_046_526: //阿斯旺防具智力卷轴 - 为防具增加智力属性。\n成功率：50%，智力+8，消耗升级次数4
            case 2_046_527: //阿斯旺防具智力卷轴 - 为防具增加智力属性。\n成功率：40%，智力+10，消耗升级次数5
            case 2_046_528: //阿斯旺防具运气卷轴 - 为防具增加运气属性。\n成功率：60%，运气+6，消耗升级次数3
            case 2_046_529: //阿斯旺防具运气卷轴 - 为防具增加运气属性。\n成功率：50%，运气+8，消耗升级次数4
            case 2_046_530: //阿斯旺防具运气卷轴 - 为防具增加运气属性。\n成功率：40%，运气+10，消耗升级次数5

            case 2_046_701: //阿斯旺饰品力量卷轴 - 为饰品增加力量属性。\n成功率：60%，力量+6，消耗升级次数3
            case 2_046_702: //阿斯旺饰品力量卷轴 - 为饰品增加力量属性。\n成功率：50%，力量+8，消耗升级次数4
            case 2_046_703: //阿斯旺饰品力量卷轴 - 为饰品增加力量属性。\n成功率：40%，力量+10，消耗升级次数5
            case 2_046_704: //阿斯旺饰品敏捷卷轴 - 为饰品增加敏捷属性。\n成功率：60%，敏捷+6，消耗升级次数3
            case 2_046_705: //阿斯旺饰品敏捷卷轴 - 为饰品增加敏捷属性。\n成功率：50%，敏捷+8，消耗升级次数4
            case 2_046_706: //阿斯旺饰品敏捷卷轴 - 为饰品增加敏捷属性。\n成功率：40%，敏捷+10，消耗升级次数5
            case 2_046_707: //阿斯旺饰品智力卷轴 - 为饰品增加智力属性。\n成功率：60%，智力+6，消耗升级次数3
            case 2_046_708: //阿斯旺饰品智力卷轴 - 为饰品增加智力属性。\n成功率：50%，智力+8，消耗升级次数4
            case 2_046_709: //阿斯旺饰品智力卷轴 - 为饰品增加智力属性。\n成功率：40%，智力+10，消耗升级次数5
            case 2_046_710: //阿斯旺饰品运气卷轴 - 为饰品增加运气属性。\n成功率：60%，运气+6，消耗升级次数3
            case 2_046_711: //阿斯旺饰品运气卷轴 - 为饰品增加运气属性。\n成功率：50%，运气+8，消耗升级次数4
            case 2_046_712: //阿斯旺饰品运气卷轴 - 为饰品增加运气属性。\n成功率：40%，运气+10，消耗升级次数5
                return true;
        }
        return false;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean is回城卷轴(int id) {
        return id >= 2_030_000 && id < 2_040_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean is升级卷轴(int id) {
        return id >= 2_040_000 && id < 2_050_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean is短枪道具(int id) {
        return id >= 1_492_000 && id < 1_500_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isUse(int id) {
        return id >= 2_000_000 && id < 3_000_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean is怪物召唤包(int id) {
        return id / 10_000 == 210;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean is怪物卡片(int id) {
        return id / 10_000 == 238;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isBoss怪物卡(int id) {
        return id / 1_000 >= 2_388;
    }

    /**
     *
     * @param id
     * @return
     */
    public static int getCardShortId(int id) {
        return id % 10_000;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean is强化宝石(int id) {
        return id >= 4_250_000 && id <= 4_251_402;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isUpgradeItemEx(int id) {
        return id / 100 == 20_487;
    }

    /**
     *
     * @param id
     * @return
     */
    public static boolean isOtherGem(int id) {
        switch (id) {
            case 4_001_174: //练习用鞋子
            case 4_001_175: //儿童鞋
            case 4_001_176: //作业用铲子
            case 4_001_177: //白色棉T恤
            case 4_001_178: //沙滩鞋
            case 4_001_179: //训练用光线枪
            case 4_001_180: //外出用手套
            case 4_001_181: //连指手套
            case 4_001_182: //清扫用拖把
            case 4_001_183: //修炼服
            case 4_001_184: //结实的耙子
            case 4_001_185: //温暖的皮靴
            case 4_001_186: //王的头巾
            case 4_031_980: //黄金砧子
            case 2_041_058: //披风防寒卷轴
            case 2_040_727: //鞋子防滑卷轴
            case 1_032_062: //元素耳环
            case 4_032_334: //狼的生命水
            case 4_032_312: //红珠玉
            case 1_142_156: //龙神
            case 1_142_157: //传说中的龙神
                return true; //mostly quest items
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isNoticeItem(int itemId) {

        switch (itemId) {
            case 1_012_438:// Lv160:// 漩涡文身(无描述)
            case 1_022_211:// Lv160:// 漩涡眼镜(无描述)
            case 1_032_224:// Lv160:// 漩涡耳环(无描述)
            case 1_122_269:// Lv160:// 漩涡吊坠(无描述)
            case 1_132_247:// Lv160:// 漩涡腰带(无描述)
            case 1_152_160:// Lv160:// 漩涡护肩(无描述)
            case 1_003_976:// Lv160:// 漩涡帽子(无描述)
            case 1_102_623:// Lv160:// 漩涡披风(无描述)
            case 1_082_556:// Lv160:// 漩涡手套(无描述)
            case 1_052_669:// Lv160:// 漩涡皇家外套(无描述)
            case 1_072_870:// Lv160:// 漩涡鞋(无描述)
            case 1_212_089:// Lv160:// 漩涡双头杖(无描述)
            case 1_222_084:// Lv160:// 漩涡灵魂手铳(无描述)
            case 1_232_084:// Lv160:// 漩涡恶魔剑(无描述)
            case 1_242_090:// Lv160:// 漩涡锁链剑(无描述)
            case 1_302_297:// Lv160:// 漩涡剑(无描述)
            case 1_312_173:// Lv160:// 漩涡斧(无描述)
            case 1_322_223:// Lv160:// 漩涡锤(无描述)
            case 1_332_247:// Lv160:// 漩涡匕首(无描述)
            case 1_342_090:// Lv160:// 漩涡刀(无描述)
            case 1_362_109:// Lv160:// 漩涡手杖(无描述)
            case 1_372_195:// Lv160:// 漩涡短杖(无描述)
            case 1_382_231:// Lv160:// 漩涡长杖(无描述)
            case 1_402_220:// Lv160:// 漩涡双手剑(无描述)
            case 1_412_152:// Lv160:// 漩涡双手战斧(无描述)
            case 1_422_158:// Lv160:// 漩涡巨锤(无描述)
            case 1_432_187:// Lv160:// 漩涡矛(无描述)
            case 1_442_242:// Lv160:// 漩涡戟(无描述)
            case 1_452_226:// Lv160:// 漩涡弓(无描述)
            case 1_462_213:// Lv160:// 漩涡弩(无描述)
            case 1_472_235:// Lv160:// 漩涡拳甲(无描述)
            case 1_482_189:// Lv160:// 漩涡冲拳(无描述)
            case 1_492_199:// Lv160:// 漩涡手铳(无描述)
            case 1_522_113:// Lv160:// 漩涡双翼短杖(无描述)
            case 1_532_118:// Lv160:// 漩涡手炮(无描述)
            case 1_252_033:// Lv160:// 漩涡虎梳魔法棒(无描述)
            case 1_312_065:// Lv140:// 狮心勇士斧(无描述)
            case 1_322_096:// Lv140:// 狮心震雷钉(无描述)
            case 1_402_095:// Lv140:// 狮心战斗弯刀(无描述)
            case 1_412_065:// Lv140:// 狮心战斗斧(无描述)
            case 1_422_066:// Lv140:// 狮心巨锤(无描述)
            case 1_432_086:// Lv140:// 狮心长枪(无描述)
            case 1_442_116:// Lv140:// 狮心矛(无描述)
            case 1_232_014:// Lv140:// 狮心痛苦命运(无描述)
            case 1_302_152:// Lv140:// 狮心弯刀(无描述)
            case 1_212_014:// Lv140:// 龙尾黑甲凶灵(无描述)
            case 1_372_084:// Lv140:// 龙尾精灵短杖(无描述)
            case 1_382_104:// Lv140:// 龙尾战斗长杖(无描述)
            case 1_452_111:// Lv140:// 鹰翼组合弓(无描述)
            case 1_462_099:// Lv140:// 鹰翼重弩(无描述)
            case 1_522_018:// Lv140:// 龙翼巨弩枪(无描述)
            case 1_242_042:// Lv140:// 渡鸦之魂女王意志之剑(无描述)
            case 1_332_130:// Lv140:// 渡鸦之魂短刀(无描述)
            case 1_222_014:// Lv140:// 鲨齿灵魂吸取者(无描述)
            case 1_242_014:// Lv140:// 鲨齿女王意志之剑(无描述)
            case 1_482_084:// Lv140:// 鲨齿巨鹰爪(无描述)
            case 1_492_085:// Lv140:// 鲨齿锐利手铳(无描述)
            case 1_532_018:// Lv140:// 鲨齿火焰炮(无描述)
            case 1_152_108:// Lv140:// 狮心战斗护肩(无描述)
            case 1_152_110:// Lv140:// 龙尾法师护肩(无描述)
            case 1_152_111:// Lv140:// 鹰翼哨兵护肩(无描述)
            case 1_152_112:// Lv140:// 渡鸦之魂猎人护肩(无描述)
            case 1_152_113:// Lv140:// 鲨齿船长护肩(无描述)
            case 1_003_172:// Lv140:// 狮心战斗头盔(无描述)
            case 1_003_173:// Lv140:// 龙尾法师帽子(无描述)
            case 1_003_174:// Lv140:// 鹰翼哨兵便帽(无描述)
            case 1_003_175:// Lv140:// 渡鸦之魂追踪者帽(无描述)
            case 1_003_176:// Lv140:// 鲨齿船长帽(无描述)
            case 1_102_275:// Lv140:// 狮心战斗披风(无描述)
            case 1_102_276:// Lv140:// 龙尾法师披风(无描述)
            case 1_102_277:// Lv140:// 鹰翼哨兵披风(无描述)
            case 1_102_278:// Lv140:// 渡鸦之魂猎人披风(无描述)
            case 1_102_279:// Lv140:// 鲨齿船长披风(无描述)
            case 1_082_295:// Lv140:// 狮心战斗护腕(无描述)
            case 1_082_296:// Lv140:// 龙尾法师手套(无描述)
            case 1_082_297:// Lv140:// 鹰翼哨兵手套(无描述)
            case 1_082_298:// Lv140:// 渡鸦之魂追踪者手套(无描述)
            case 1_082_299:// Lv140:// 鲨齿船长手套(无描述)
            case 1_052_314:// Lv140:// 狮心战斗锁子甲(无描述)
            case 1_052_315:// Lv140:// 龙尾法师长袍(无描述)
            case 1_052_316:// Lv140:// 鹰翼哨兵服(无描述)
            case 1_052_317:// Lv140:// 渡鸦之魂追踪者盔甲(无描述)
            case 1_052_318:// Lv140:// 鲨齿船长外套(无描述)
            case 1_072_485:// Lv140:// 狮心战斗鞋(无描述)
            case 1_072_486:// Lv140:// 龙尾法师鞋(无描述)
            case 1_072_487:// Lv140:// 鹰翼哨兵鞋(无描述)
            case 1_072_488:// Lv140:// 渡鸦之魂追踪者鞋(无描述)
            case 1_072_489:// Lv140:// 鲨齿船长鞋(无描述)
            case 1_112_915:// Lv0:// 蓝调戒指
            case 1_112_793:// Lv0:// 快乐指环
            case 5_062_000:// 神奇魔方
            case 5_062_002:// 高级神奇魔方
            case 2_340_000:// 祝福卷轴
            case 5_062_500:// 大师附加神奇魔方
            case 2_614_000:// 突破一万之石
            case 2_614_001:// 突破十万之石
            case 2_614_002:// 突破百万之石
            case 2_614_003:// 突破一万之石
            case 2_614_004:// 突破十万之石
            case 2_614_005:// 突破百万之石
            case 2_614_006:// 突破一万之石
            case 2_614_007:// 突破十万之石
            case 2_614_008:// 突破百万之石
            case 2_614_009:// 突破一万之石
            case 2_614_010:// 突破十万之石
            case 2_614_011:// 突破百万之石
            case 2_614_012:// 突破一万之石
            case 2_614_013:// 突破十万之石
            case 2_614_014:// 突破百万之石
            case 2_614_015:// 突破一万之石
            case 2_614_016:// 突破十万之石
            case 2_614_017:// 突破百万之石
            case 2_431_738:// 抵用券500商品券
            case 2_431_739:// 抵用券1000商品券
            case 2_431_740:// 抵用券1500商品券
            case 2_431_741:// 抵用券3000商品券
            case 2_431_742:// 抵用券4000商品券
            case 2_431_743:// 抵用券10000商品券
//            case 4021011: //纯洁灵魂的火花 - 锻造重生装备时的必要材料。
//            case 4021012: //强烈灵魂的净水 - 锻造永恒装备时的必要材料。
//            case 4020013: //梦碎片 - 充满了梦的碎片。
//            case 4021019: //梦之石 - 黑魔法师的梦凝聚而成的石头
//            case 4021020: //混沌碎片 - 含有黑暗混沌力量的金属。打猎140级以上怪物时，有非常低的概率可以获得。
//            case 4021021: //贤者之石 - 含有炼金术的精髓的矿物。乍一看像是液体。分解105级以上装备时偶尔可以发现。
//            case 4021022: //太初精髓 - 含有世界起源时期的纯粹气息的神秘石头。运气好的话，可以在跳动的心脏和金色花堆中发现。
//            case 4310015: //斗神证物 - 战争之神送给勇敢者的证物。可以感觉到未知的力量。
//
//            case 2430112: //神奇魔方碎片 - 从神奇魔方上掉落的碎块。双击使用物品，可以交换有用的东西。
//            case 2028061: //不可思议的卷轴卷 - 使用后封印解除，变成卷轴。谁也不知道会变成什么卷轴。
//            case 2028062: //不可思议的配方卷 - 使用后封印解除，变成配方。谁也不知道会变成什么配方。
//            case 2290285: //[能手册]神秘能手册 - 使用后可以变成特定技能能手册的神秘能手册。
                return true;
            default:
                return false;
        }
    }

    /*
     * 2048200 - 低级潜能附加印章 - #c双击#后，对开放潜能的道具使用，有一定概率开放1个附加潜能。无法对已开放附加潜能的道具使用。
     * 2048201 - 中级潜能附加印章 - #c双击#后，对开放潜能的道具使用，有一定概率开放1个附加潜能。无法对已开放附加潜能的道具使用。
     * 2048202 - 高级潜能附加印章 - #c双击#后，对开放潜能的道具使用，有一定概率开放1个附加潜能。无法对已开放附加潜能的道具使用。
     * 2048203 - 特殊潜能附加印章 - #c双击#后，对开放潜能的道具使用，有一定概率开放1个附加潜能。无法对已开放附加潜能的道具使用。
     * 2048204 - 最高级潜能附加印章 - #c双击#后，对开放潜能的道具使用，有一定概率开放1个附加潜能。无法对已开放附加潜能的道具使用。
     * 2048300 - 银光潜能附加印章 - #c双击#后，对开放潜能的道具使用，有一定概率开放1个附加潜能。#c每个装备最多开放2个#附加潜能，2次附加潜能只能通过在开放了1个附加潜能的道具上使用金色潜能附加印章和银色潜能附加印章的方法开放。附加潜在属性无法用于未鉴定状态的道具。
     * 2048301 - 金光潜能附加印章 - #c双击#后，对开放潜能的道具使用，有一定概率开放1个附加潜能。#c每个装备最多开放2个#附加潜能，2次附加潜能只能通过在开放了1个附加潜能的道具上使用金色潜能附加印章和银色潜能附加印章的方法开放。附加潜在属性无法用于未鉴定状态的道具。
     * 2048302 - 金色附加刻印之印 - #c双击#后在附加潜能开放了2个以下的道具上使用，可以增加1个附加潜能。
     * 2048303 - 银色附加刻印之印 - #c双击#后在附加潜能开放了2个以下的道具上使用，可以增加1个附加潜能。
     * 2048304 - 完美附加刻印之印 - #c双击#后在附加潜能开放了2个以下的道具上使用，可以增加1个附加潜能。
     */

    /**
     *
     * @param itemId
     * @return
     */

    public static int getAdditionalSuccess(int itemId) {
        switch (itemId) {
            case 2_048_200:
                return 5;
            case 2_048_201:
            case 2_048_202:
                return 10;
            case 2_048_203:
                return 100;
            case 2_048_204:
                return 20;
            case 2_048_300:
            case 2_048_303:
                return 60;
            case 2_048_301:
            case 2_048_302:
                return 80;
            case 2_048_304:
                return 100;
            default:
                break;
        }
        return 0;
    }

    /*
     * 星岩系统
     */

    /**
     *
     * @param id
     * @return
     */

    public static int getNebuliteGrade(int id) {
        if (id / 10_000 != 306) {
            return -1;
        }
        if (id >= 3_060_000 && id < 3_061_000) {
            return 0; //[D]级星岩
        } else if (id >= 3_061_000 && id < 3_062_000) {
            return 1; //[C]级星岩
        } else if (id >= 3_062_000 && id < 3_063_000) {
            return 2; //[B]级星岩
        } else if (id >= 3_063_000 && id < 3_064_000) {
            return 3; //[A]级星岩
        }
        return 4; //[S]级星岩
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is机甲装备(int itemId) {
        return itemId >= 1_610_000 && itemId < 1_660_000;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is龙龙装备(int itemId) {
        return itemId >= 1_940_000 && itemId < 1_980_000; //194 = 面罩, 195 = 吊坠, 196 = 飞翼, 197 = 尾巴
    }

    /*
     * 是否能砸卷的装备
     * itemId / 100000
     * 16 为机械装备和安卓装备
     * 19 为龙龙装备
     * 1672030 - 能量全开触发器 Plus 1
     * 1672031 - 无限能量全开触发器
     * 1672032 - 能量全开触发器 Plus 2
     */

    /**
     *
     * @param itemId
     * @return
     */

    public static boolean canScroll(int itemId) {
        return itemId / 100_000 != 19 && itemId / 100_000 != 16 || (itemId / 1_000 == 1_672 && itemId != 1_672_030 && itemId != 1_672_031 && itemId != 1_672_032);
    }

    /*
     * 是否能上金锤子的装备
     */

    /**
     *
     * @param itemId
     * @return
     */

    public static boolean canHammer(int itemId) {
        switch (itemId) {
            case 1_122_000: //黑龙项环
            case 1_122_076: //进阶黑暗龙王项链
                return false;
        }
        return canScroll(itemId);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static int getLowestPrice(int itemId) {
        switch (itemId) {
            case 2_340_000: //祝福卷轴
            case 2_531_000: //防爆卷轴
            case 2_530_000: //幸运日卷轴
                return 50_000_000;
        }
        return -1;
    }

    /**
     *
     * @param itemId
     * @param up
     * @return
     */
    public static int getModifier(int itemId, int up) {
        if (up <= 0) {
            return 0;
        }
        switch (itemId) {
            case 2_022_459: //星缘的奖励1
            case 2_860_179:
            case 2_860_193:
            case 2_860_207:
                return 130;
            case 2_022_460: //佳佳的报答1
            case 2_022_462: //佳佳的报答3
            case 2_022_730: //丰收的冬天
                return 150;
            case 2_860_181:
            case 2_860_195:
            case 2_860_209:
                return 200;
        }
        if (itemId / 10_000 == 286) { //familiars
            return 150;
        }
        return 200;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static short getSlotMax(int itemId) {
        switch (itemId) {
            case 4_030_003: //俄罗斯方块
            case 4_030_004: //俄罗斯方块
            case 4_030_005: //俄罗斯方块
                return 1;
            case 4_001_168: //金枫叶
            case 4_031_306:
            case 4_031_307:
            case 3_993_000: //吉祥装饰
            case 3_993_002: //竹子吉祥装饰
            case 3_993_003: //红色福袋
                return 100;
            case 5_220_010: //高级快乐百宝券
            case 5_220_013:
                return 1_000;
            case 5_220_020:
                return 2_000;
        }
        return 0;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isDropRestricted(int itemId) {
        return itemId == 3_012_000
                || itemId == 4_030_004 //俄罗斯方块
        
                || itemId == 1_052_098 //海盗套装
        
                || itemId == 1_052_202;//玩具品克缤套服
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean isPickupRestricted(int itemId) {
        return itemId == 4_030_003 //俄罗斯方块
        
                || itemId == 4_030_004; //俄罗斯方块
    }

    /**
     *
     * @param itemId
     * @param def
     * @return
     */
    public static short getStat(int itemId, int def) {
        switch (itemId) {
            case 1_002_419: //枫叶帽
                return 5;
            case 1_002_959:
                return 25;
            case 1_142_002: //任务狂人勋章
                return 10;
            case 1_122_121:
                return 7;
        }
        return (short) def;
    }

    /**
     *
     * @param itemId
     * @param def
     * @return
     */
    public static short getHpMp(int itemId, int def) {
        switch (itemId) {
            case 1_122_121:
                return 500;
            case 1_142_002: //任务狂人勋章
            case 1_002_959:
                return 1_000;
        }
        return (short) def;
    }

    /**
     *
     * @param itemId
     * @param def
     * @return
     */
    public static short getATK(int itemId, int def) {
        switch (itemId) {
            case 1_122_121:
                return 3;
            case 1_002_959:
                return 4;
            case 1_142_002: //任务狂人勋章
                return 9;
        }
        return (short) def;
    }

    /**
     *
     * @param itemId
     * @param def
     * @return
     */
    public static short getDEF(int itemId, int def) {
        switch (itemId) {
            case 1_122_121:
                return 250;
            case 1_002_959:
                return 500;
        }
        return (short) def;
    }

    /**
     *
     * @param itemid
     * @param closeness
     * @return
     */
    public static int getRewardPot(int itemid, int closeness) {
        switch (itemid) {
            case 2_440_000: //道具橘子宝宝
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2_028_041 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2_028_046 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2_028_049 + (closeness / 10);
                }
                return 2_028_057; //非常甜美的果实
            case 2_440_001: //道具钻石宝宝
                switch (closeness / 10) {
                    case 0:
                    case 1:
                    case 2:
                        return 2_028_044 + (closeness / 10);
                    case 3:
                    case 4:
                    case 5:
                        return 2_028_049 + (closeness / 10);
                    case 6:
                    case 7:
                    case 8:
                        return 2_028_052 + (closeness / 10);
                }
                return 2_028_060; //非常灿烂的钻石
            case 2_440_002: //福满月妙
                return 2_028_069; //可爱的福满月妙
            case 2_440_003: //迷你西瓜盆景
                return 2_430_278; //黄金枫叶果实
            case 2_440_004: //第一个西瓜花盆
                return 2_430_381; //第一个佳佳牌西瓜
            case 2_440_005: //第二个西瓜花盆
                return 2_430_393; //第二个佳佳牌西瓜
        }
        return 0;
    }

    /*
     * 是否是需要记录日志的道具
     */

    /**
     *
     * @param itemId
     * @return
     */

    public static boolean isLogItem(int itemId) {
        switch (itemId) {
            case 4_000_463: // 国庆纪念币
            case 2_340_000: // 祝福卷轴
            case 2_049_000: // 白医卷轴
            case 2_049_001: // 白医卷轴
            case 2_049_002: // 白医卷轴
            case 2_040_006: // 诅咒白医卷轴
            case 2_040_007: // 诅咒白医卷轴
            case 2_040_303: // 耳环智力必成卷
            case 2_040_403: // 上衣防御必成卷
            case 2_040_506: // 全身盔甲敏捷必成卷
            case 2_040_507: // 全身盔甲防御必成卷
            case 2_040_603: // 裤裙防御必成卷
            case 2_040_709: // 鞋子敏捷必成卷
            case 2_040_710: // 鞋子跳跃必成卷
            case 2_040_711: // 鞋子速度必成卷
            case 2_040_806: // 手套敏捷必成卷
            case 2_040_903: // 盾牌防御必成卷
            case 2_041_024: // 披风魔法防御必成卷
            case 2_041_025: // 披风物理防御必成卷
            case 2_043_003: // 单手剑攻击必成卷
            case 2_043_103: // 单手斧攻击必成卷
            case 2_043_203: // 单手钝器攻击必成卷
            case 2_043_303: // 短剑攻击必成卷
            case 2_043_703: // 短杖攻击必成卷
            case 2_043_803: // 长杖攻击必成卷
            case 2_044_003: // 双手剑攻击必成卷
            case 2_044_103: // 双手斧攻击必成卷
            case 2_044_203: // 双手钝器攻击必成卷
            case 2_044_303: // 枪攻击必成卷
            case 2_044_403: // 矛攻击必成卷
            case 2_044_503: // 弓攻击必成卷
            case 2_044_603: // 弩攻击必成卷
            case 2_044_908: // 短枪攻击必成卷
            case 2_044_815: // 指节攻击必成卷
            case 2_044_019: // 双手剑魔力必成卷
            case 2_044_703: // 拳套攻击必成卷
                return true;
        }
        return false;
    }

    /**
     *
     * @param rank
     * @return
     */
    public static int[] getInnerSkillbyRank(int rank) {
        switch (rank) {
            case 0:
                return rankC;
            case 1:
                return rankB;
            case 2:
                return rankA;
            case 3:
                return rankS;
            default:
                return null;
        }
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
    public static boolean isGeneralScroll(int itemId) {
        return itemId / 1_000 == 2_046;
    }

    /**
     *
     * @param scrollId
     * @param level
     * @return
     */
    public static int getSuccessTablet(int scrollId, int level) {
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
    public static int getCurseTablet(int scrollId, int level) {
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
    public static boolean isAccessory(int itemId) {
        return (itemId >= 1_010_000 && itemId < 1_040_000) || (itemId >= 1_122_000 && itemId < 1_153_000) || (itemId >= 1_670_000 && itemId < 1_680_000);
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
        return is好友戒指(itemid) || is恋人戒指(itemid) || is结婚戒指(itemid);
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is结婚戒指(int itemId) {
        switch (itemId) {
            case 1_112_300: //月长石戒指1克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_301: //月长石戒指2克拉 -  爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_302: //月长石戒指3克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_303: //闪耀新星戒指1克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_304: //闪耀新星戒指2克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_305: //闪耀新星戒指3克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_306: //金心戒指1克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_307: //金心戒指2克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_308: //金心戒指3克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_309: //银翼戒指1克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_310: //银翼戒指2克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_311: //银翼戒指3克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指将会#c消失#。
            case 1_112_312: //永恒真爱戒指 - 使恋人的心灵相通的神奇纪念戒指，祝福相爱的人们永远幸福！冒险岛，永远有真爱！\n\n#c结婚的2人#同时佩戴，靠近时会有#c相爱一生#的效果。\n但如果#c离婚#了，真爱戒指可能会#c消失#。
            case 1_112_315: //恩爱夫妻结婚戒指1克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指可能会#c消失#。
            case 1_112_316: //恩爱夫妻结婚戒指2克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指可能会#c消失#。
            case 1_112_317: //恩爱夫妻结婚戒指3克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指可能会#c消失#。
            case 1_112_318: //鸳鸯夫妻结婚戒指1克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指可能会#c消失#。
            case 1_112_319: //鸳鸯夫妻结婚戒指2克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指可能会#c消失#。
            case 1_112_320: //鸳鸯夫妻结婚戒指3克拉 - 爱情与婚姻的象征。\n注：结婚人士如果#c离婚#，该戒指可能会#c消失#。
            case 1_112_804: //中式结婚戒指 - 表示夫妻之间爱情的戒指。\n#c中式结婚，2012绝版纪念。#
                return true;
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is好友戒指(int itemId) {
        switch (itemId) {
            case 1_112_800: //四叶挚友戒指
            case 1_112_801: //雏菊挚友戒指
            case 1_112_802: //闪星挚友戒指
            case 1_112_810: //圣诞夜响叮当
            case 1_112_811: //圣诞华丽派对
            case 1_112_812: //我的麻吉好友
            case 1_112_817: //蝴蝶挚友戒指30天权
            case 1_112_822: //梦幻悬浮戒指

            case 1_049_000: //友情T恤
                return true;
        }
        return false;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is恋人戒指(int itemId) {
        switch (itemId) {
            case 1_112_001: //恋人戒指
            case 1_112_002: //纯爱恋人戒指
            case 1_112_003: //丘比特戒指
            case 1_112_005: //维纳斯戒指
            case 1_112_006: //圣十字架戒指
            case 1_112_007: //许愿情侣戒指
            case 1_112_012: //红玫瑰戒指
            case 1_112_013: //爱情红线戒指
            case 1_112_014: //热吻情侣戒指
            case 1_112_015: //白金戒指
            case 1_112_816: //雪晶球戒指 -- 以前是放在好友里面的
            case 1_112_820: //龙凤呈祥戒指 - 两个相配的异性角色站在一起会出现龙凤呈祥效果。

            case 1_048_000: //情侣T恤
            case 1_048_001: //兔兔情侣衫
            case 1_048_002: //胡萝卜情侣衫
                return true;
        }
        return false;
    }

    /*
     * 是否为副手武器 也就是盾牌
     */

    /**
     *
     * @param itemId
     * @return
     */

    public static boolean isSubWeapon(int itemId) {
        return itemId / 1_000 == 1_098 || itemId / 1_000 == 1_099 || itemId / 10_000 == 135;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is双刀主手(int itemId) {
        return itemId / 10_000 == 133;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is双刀副手(int itemId) {
        return itemId / 10_000 == 134 && itemId != 1_342_069; //1342069 - 空气刃 - 可以装备在暗影双刀副武器#c刀#上的透明利刃。
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is双弩箭矢(int itemId) {
        return itemId >= 1_352_000 && itemId <= 1_352_007;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is幻影卡片(int itemId) {
        return itemId >= 1_352_100 && itemId <= 1_352_107;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is龙传宝盒(int itemId) {
        return itemId >= 1_352_300 && itemId <= 1_352_304;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is夜光宝珠(int itemId) {
        return itemId >= 1_352_400 && itemId <= 1_352_404;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is狂龙副手(int itemId) {
        return itemId >= 1_352_500 && itemId <= 1_352_504;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is萝莉副手(int itemId) {
        return itemId >= 1_352_600 && itemId <= 1_352_604;
    }

    /**
     *
     * @param itemId
     * @return
     */
    public static boolean is特殊副手(int itemId) {
        return is双弩箭矢(itemId);
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean isSuperiorEqp(final int itemid) {
        return itemid >= 1_122_241 && itemid <= 1_122_245 || itemid >= 1_132_164 && itemid <= 1_132_178 || itemid >= 1_102_471 && itemid <= 1_102_485 || itemid >= 1_082_543 && itemid <= 1_082_547 || itemid >= 1_072_732 && itemid <= 1_072_747;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is随机攻击卷轴(final int itemid) {
        return itemid / 1_000 == 2_612 || itemid / 1_000 == 2_613 || itemid / 1_000 == 2_046 || itemid / 1_000 == 2_615 || itemid / 1_000 == 2_616;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is武器攻击力卷轴(final int itemid) {
        return itemid / 100 == 20_478 || itemid / 100 == 20_469 || itemid / 100 == 20_479;
    }

    /**
     *
     * @param lockLevel
     * @param lockCount
     * @return
     */
    public static int getNeedHonor(int lockLevel, int lockCount) {
        int needHonor = 0;
        switch (lockLevel) {
            case 0: {
                needHonor = 100;
                break;
            }
            case 1: {
                needHonor = lockCount == 0 ? 500 : lockCount == 1 ? 3_500 : 8_500;
                break;
            }
            case 2: {
                needHonor = lockCount == 0 ? 5_100 : lockCount == 1 ? 8_100 : 13_100;
                break;
            }
            case 3: {
                needHonor = lockCount == 0 ? 10_100 : lockCount == 1 ? 13_100 : 18_100;
                break;
            }
        }
        return needHonor;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean isSealedScroll(int itemid) {
        return itemid == 2_610_001;
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static String getEnchantingEquipType(int itemid) {
        /*
        * 1 武器
        * 2 帽子、披风、上衣、裤子、长袍、鞋子、护肩
        * 3 手套
        * 4 吊坠、腰带、耳环、眼饰
         */
        return ItemConstants.isWeapon(itemid) ? "武器" : (ItemConstants.isCap(itemid) || ItemConstants.isCape(itemid) || ItemConstants.isCoat(itemid)
                || ItemConstants.isPants(itemid) || ItemConstants.isLongcoat(itemid) || ItemConstants.isShoes(itemid) || ItemConstants.isShoulders(itemid)) ? "防具"
                : ItemConstants.isGlove(itemid) ? "手套" : "饰品";
    }

    /**
     *
     * @param style
     * @return
     */
    public static int getEnchantingScrollStyle(String style) {
        switch (style.substring(0, 2)) {
            case "蓝色":
                return 0;
            case "灰色":
                return 1;
            case "棕色":
                return 2;
            case "金色":
                return 3;
            default:
                return 0;
        }
    }

    /**
     *
     * @param itemid
     * @return
     */
    public static boolean is灵魂结晶(int itemid) {
        return itemid / 1_000 == 2_591;
    }

    /**
     * 打猎可获得经验的椅子
     */
    public static boolean isSetupExpRate(int itemid) {
        return itemid / 10_000 == 302;
    }

    /**
     *
     * @param equip
     * @return
     */
    public static long getCubeNeedMeso(Equip equip) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int reqLevel = ii.getReqLevel(equip.getItemId());
        int n3 = 0;
        if (reqLevel >= 120) {
            n3 = 20;
        } else if (reqLevel >= 70) {
            n3 = 5;
        } else if (reqLevel >= 30) {
            n3 = 1;
        }
        double d2 = reqLevel;
        int n4 = 2;
        double d3 = 1.0;
        do {
            if ((n4 & 1) != 0) {
                d3 *= d2;
            }
            if ((n4 >>= 1) == 0) {
                break;
            }
            d2 *= d2;
        } while (true);
        int n5 = (int) Math.ceil(d3);
        return (long) ((n3 * n5 <= 0 ? 1 : 0) - 1 & n3 * n5);
    }

    /**
     *
     * @param weapon
     * @return
     */
    public static boolean isZeroWeapon(final int weapon) {
        return (weapon >= 1_562_000 && weapon <= 1_562_007) || (weapon >= 1_572_000 && weapon <= 1_572_007);
    }

    /**
     *
     */
    public static class PotentialConstants {

        /**
         *
         * @param itemid
         * @return
         */
        public static int getDefaultPotentialFlag(final int itemid) {
            int flag = PotentialFlag.B级.getValue() | PotentialFlag.A级.getValue() | PotentialFlag.S级.getValue() | PotentialFlag.SS级.getValue();
            switch (itemid) {
                case 2_711_000:
                case 2_711_001:
                case 2_711_009: {
                    flag -= PotentialFlag.S级.getValue();
                }
                case 2_710_000: {
                    flag = (flag - PotentialFlag.SS级.getValue() | PotentialFlag.等级下降.getValue());
                    break;
                }
                case 2_710_001: {
                    flag -= PotentialFlag.SS级.getValue();
                }
                case 3_994_895: {
                    flag |= PotentialFlag.洗后无法交易.getValue();
                    break;
                }
                case 2_711_005:
                case 2_711_007:
                case 5_062_000: {
                    flag -= PotentialFlag.SS级.getValue();
                    break;
                }
                case 5_062_001: {
                    flag = (flag - PotentialFlag.SS级.getValue() | PotentialFlag.调整潜能条数.getValue());
                    break;
                }
                case 5_062_004: {
                    flag = (flag - PotentialFlag.SS级.getValue() | PotentialFlag.去掉无用潜能.getValue());
                    break;
                }
                case 5_062_013: {
                    flag |= PotentialFlag.去掉无用潜能.getValue();
                }
                case 5_062_005:
                case 5_062_006:
                case 5_062_021: {
                    flag |= PotentialFlag.对等.getValue();
                    break;
                }
                case 5_062_008:
                case 5_062_019: {
                    flag |= PotentialFlag.前两条相同.getValue();
                    break;
                }
                case 10_000:
                case 5_062_500:
                case 5_062_501:
                case 5_062_502:
                case 5_062_503: {
                    flag |= PotentialFlag.附加潜能.getValue();
                    break;
                }
            }
            if (MapleItemInformationProvider.getInstance().isCash(itemid)) {
                flag |= PotentialFlag.点券光环.getValue();
            }
            return flag;
        }

        /**
         *
         * @param itemid
         * @return
         */
        public static int getCubeRate(final int itemid) {
            switch (itemid) {
                case 5_062_000: {
                    return 1;
                }
                case 5_062_001: {
                    return 2;
                }
                case 5_062_002: {
                    return 3;
                }
                case 5_062_005: {
                    return 4;
                }
                case 5_062_006: {
                    return 4;
                }
                case 5_062_009: {
                    return 4;
                }
                case 5_062_010: {
                    return 7;
                }
                case 5_062_090: {
                    return 3;
                }
                case 5_062_100: {
                    return 2;
                }
                case 5_062_102: {
                    return 2;
                }
                case 5_062_103:
                case 5_062_503: {
                    return 2;
                }
                case 5_062_500: {
                    return 4;
                }
                case 5_062_501: {
                    return 4;
                }
                case 5_062_024: {
                    return 3;
                }
                case 5_062_502: {
                    return 5;
                }
                default: {
                    return 1;
                }
            }
        }

        /**
         *
         * @param itemid
         * @return
         */
        public static int getCubeDebris(final int itemid) {
            switch (itemid) {
                case 5_062_000: {
                    return 2_430_112;
                }
                case 5_062_001: {
                    return 2_430_112;
                }
                case 5_062_002:
                case 5_062_022: {
                    return 2_430_481;
                }
                case 5_062_005: {
                    return 2_430_759;
                }
                case 5_062_006: {
                    return 2_431_427;
                }
                case 5_062_009: {
                    return 2_431_893;
                }
                case 5_062_010: {
                    return 2_431_894;
                }
                case 5_062_090: {
                    return 2_431_445;
                }
                case 5_062_100: {
                    return 2_430_112;
                }
                case 5_062_102: {
                    return 2_430_112;
                }
                case 5_062_103: {
                    return 2_430_112;
                }
                case 5_062_500: {
                    return 2_430_915;
                }
                case 5_062_501: {
                    return 2_430_915;
                }
                case 5_062_024: {
                    return 2_434_125;
                }
                case 5_062_502: {
                    return 2_433_547;
                }
                case 5_062_503: {
                    return 2_434_782;
                }
                default: {
                    return 0;
                }
            }
        }

        /**
         *
         */
        public enum PotentialFlag {

            /**
             *
             */
            B级(0x01),

            /**
             *
             */
            A级(0x02),

            /**
             *
             */
            S级(0x04),

            /**
             *
             */
            SS级(0x08),

            /**
             *
             */
            等级下降(0x10),

            /**
             *
             */
            调整潜能条数(0x20),

            /**
             *
             */
            洗后无法交易(0x40),

            /**
             *
             */
            对等(0x80),

            /**
             *
             */
            去掉无用潜能(0x100),

            /**
             *
             */
            前两条相同(0x200),

            /**
             *
             */
            附加潜能(0x400),

            /**
             *
             */
            点券光环(0x800);

            private final int value;

            PotentialFlag(final int value) {
                this.value = value;
            }

            /**
             *
             * @return
             */
            public final int getValue() {
                return this.value;
            }

            /**
             *
             * @param n
             * @return
             */
            public final boolean check(final int n) {
                return (n & this.value) == this.value;
            }
        }
    }

    /**
     *
     */
    public static class UpgradeItemEx {

        /**
         *
         * @param itemid
         * @return
         */
        public static int getValue(final int itemid) {
            switch (itemid) {
                case 2_048_716:
                case 2_048_724: {
                    return 10;
                }
                case 2_048_717:
                case 2_048_721:
                case 2_048_723: {
                    return 15;
                }
                case 2_048_712: {
                    return 5;
                }
                case 2_048_700:
                case 2_048_709:
                case 2_048_727: {
                    return 6;
                }
                case 2_048_701:
                case 2_048_705:
                case 2_048_715:
                case 2_048_728: {
                    return 6;
                }
                case 2_048_702:
                case 2_048_706:
                case 2_048_729: {
                    return 7;
                }
                case 2_048_703:
                case 2_048_707:
                case 2_048_713:
                case 2_048_730: {
                    return 7;
                }
                case 2_048_704:
                case 2_048_708:
                case 2_048_731: {
                    return 8;
                }
                case 2_048_725:
                case 2_048_743: {
                    return 8;
                }
                default: {
                    return 8;
                }
            }
        }
    }
}
