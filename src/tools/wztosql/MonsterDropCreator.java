package tools.wztosql;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.Pair;
import tools.StringUtil;

/**
 *
 * @author zjj
 */
public class MonsterDropCreator {

    private static final int COMMON_ETC_RATE = 600_000;
    private static final int SUPER_BOSS_ITEM_RATE = 300_000;
    private static final int POTION_RATE = 20_000;
    private static final int ARROWS_RATE = 25_000;
    private static int lastmonstercardid = 2_388_070;
    private static boolean addFlagData = false;

    /**
     *
     */
    protected static String monsterQueryData = "drop_data";

    /**
     *
     */
    protected static List<Pair<Integer, String>> itemNameCache = new ArrayList();

    /**
     *
     */
    protected static List<Pair<Integer, MobInfo>> mobCache = new ArrayList();

    /**
     *
     */
    protected static Map<Integer, Boolean> bossCache = new HashMap();

    /**
     *
     */
    protected static final MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/String.wz"));

    /**
     *
     */
    protected static final MapleDataProvider mobData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Mob.wz"));

    /**
     *
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NotBoundException
     * @throws InstanceAlreadyExistsException
     * @throws MBeanRegistrationException
     * @throws NotCompliantMBeanException
     * @throws MalformedObjectNameException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException, MalformedObjectNameException {
        //  MapleData data = MapleDataProviderFactory.getDataProvider(new File(new StringBuilder().append(System.getProperty("net.sf.odinms.wzpath")).append("String.wz").toString())).getData("MonsterBook.img");
        System.out.println("準備提取數據!");
        System.out.println("按任意鍵繼續...");
        System.console().readLine();

        long currtime = System.currentTimeMillis();
        // addFlagData = Boolean.parseBoolean(args[0]);
        addFlagData = false;
        System.out.println("載入: 物品名稱.");
        getAllItems();
        System.out.println("載入: 怪物數據.");
        getAllMobs();

        StringBuilder sb = new StringBuilder();
        try (FileOutputStream out = new FileOutputStream("mobDrop.sql", true)) {
            for (Map.Entry e : getDropsNotInMonsterBook().entrySet()) {
                boolean first = true;
                
                sb.append("INSERT INTO ").append(monsterQueryData).append(" VALUES ");
                for (Integer monsterdrop : (List<Integer>) e.getValue()) {
                    int itemid = monsterdrop;
                    int monsterId = ((Integer) e.getKey());
                    int rate = getChance(itemid, monsterId, bossCache.containsKey(monsterId));
                    
                    if (rate <= 100_000) {
                        switch (monsterId) {
                            case 9_400_121:
                                rate *= 5;
                                break;
                            case 9_400_112:
                            case 9_400_113:
                            case 9_400_300:
                                rate *= 10;
                        }
                    }
                    
                    for (int i = 0; i < multipleDropsIncrement(itemid, monsterId); i++) {
                        if (first) {
                            sb.append("(DEFAULT, ");
                            first = false;
                        } else {
                            sb.append(", (DEFAULT, ");
                        }
                        sb.append(monsterId).append(", ");
                        if (addFlagData) {
                            sb.append("'', ");
                        }
                        sb.append(itemid).append(", ");
                        sb.append("1, 1,");
                        sb.append("0, ");
                        int num = IncrementRate(itemid, i);
                        sb.append(num == -1 ? rate : num);
                        sb.append(")");
                        first = false;
                    }
                    sb.append("\n");
                    sb.append("-- Name : ");
                    retriveNLogItemName(sb, itemid);
                    sb.append("\n");
                }
                sb.append(";");
                sb.append("\n");
                
                out.write(sb.toString().getBytes());
                sb.delete(0, 2_147_483_647);
            }
            
            System.out.println("載入: Drops from String.wz/MonsterBook.img.");
            for (MapleData dataz : data.getData("MonsterBook.img").getChildren()) {
                int monsterId = Integer.parseInt(dataz.getName());
                int idtoLog = monsterId;
                boolean first = true;
                
                if (monsterId == 9_400_408) {
                    idtoLog = 9_400_409;
                }
                if (dataz.getChildByPath("reward").getChildren().size() > 0) {
                    sb.append("INSERT INTO ").append(monsterQueryData).append(" VALUES ");
                    for (MapleData drop : dataz.getChildByPath("reward")) {
                        int itemid = MapleDataTool.getInt(drop);
                        int rate = getChance(itemid, idtoLog, bossCache.containsKey(idtoLog));
                        
                        for (Pair Pair : mobCache) {
                            if (((Integer) Pair.getLeft()) == monsterId) {
                                if ((((MobInfo) Pair.getRight()).getBoss() <= 0)
                                        || (rate > 100_000)) {
                                    break;
                                }
                                if (((MobInfo) Pair.getRight()).rateItemDropLevel() == 2) {
                                    rate *= 10;
                                    break;
                                }
                                if (((MobInfo) Pair.getRight()).rateItemDropLevel() == 3) {
                                    switch (monsterId) {
                                        case 8_810_018:
                                            rate *= 48;
                                        case 8_800_002:
                                            rate *= 45;
                                            break;
                                        default:
                                            rate *= 30;
                                            break;
                                    }
                                }
                                switch (monsterId) {
                                    case 8_860_010:
                                    case 9_400_265:
                                    case 9_400_270:
                                    case 9_400_273:
                                        rate *= 10;
                                        break;
                                    case 9_400_294:
                                        rate *= 24;
                                        break;
                                    case 9_420_522:
                                        rate *= 29;
                                        break;
                                    case 9_400_409:
                                        rate *= 35;
                                        break;
                                    case 9_400_287:
                                        rate *= 60;
                                        break;
                                    default:
                                        rate *= 10;
                                        break;
                                }
                                
                            }
                            
                        }
                        
                        for (int i = 0; i < multipleDropsIncrement(itemid, idtoLog); i++) {
                            if (first) {
                                sb.append("(DEFAULT, ");
                                first = false;
                            } else {
                                sb.append(", (DEFAULT, ");
                            }
                            sb.append(idtoLog).append(", ");
                            if (addFlagData) {
                                sb.append("'', ");
                            }
                            sb.append(itemid).append(", ");
                            sb.append("1, 1,");
                            sb.append("0, ");
                            int num = IncrementRate(itemid, i);
                            sb.append(num == -1 ? rate : num);
                            sb.append(")");
                            first = false;
                        }
                        sb.append("\n");
                        sb.append("-- Name : ");
                        retriveNLogItemName(sb, itemid);
                        sb.append("\n");
                    }
                    sb.append(";");
                }
                sb.append("\n");
                
                out.write(sb.toString().getBytes());
                sb.delete(0, 2_147_483_647);
            }
            
            System.out.println("載入: 怪物書數據.");
            StringBuilder SQL = new StringBuilder();
            StringBuilder bookName = new StringBuilder();
            for (Pair Pair : itemNameCache) {
                if ((((Integer) Pair.getLeft()) >= 2_380_000) && (((Integer) Pair.getLeft()) <= lastmonstercardid)) {
                    bookName.append((String) Pair.getRight());
                    
                    if (bookName.toString().contains(" Card")) {
                        bookName.delete(bookName.length() - 5, bookName.length());
                    }
                    for (Pair Pair_ : mobCache) {
                        if (((MobInfo) Pair_.getRight()).getName().equalsIgnoreCase(bookName.toString())) {
                            int rate = 1_000;
                            if (((MobInfo) Pair_.getRight()).getBoss() > 0) {
                                rate *= 25;
                            }
                            SQL.append("INSERT INTO ").append(monsterQueryData).append(" VALUES ");
                            SQL.append("(DEFAULT, ");
                            SQL.append(Pair_.getLeft()).append(", ");
                            if (addFlagData) {
                                sb.append("'', ");
                            }
                            SQL.append(Pair.getLeft()).append(", ");
                            SQL.append("1, 1,");
                            SQL.append("0, ");
                            SQL.append(rate);
                            SQL.append(");\n");
                            SQL.append("-- 物品名 : ").append((String) Pair.getRight()).append("\n");
                            break;
                        }
                    }
                    bookName.delete(0, 2_147_483_647);
                }
            }
            System.out.println("載入: 怪物卡數據.");
            SQL.append("\n");
            int i = 1;
            int lastmonsterbookid = 0;
            for (Pair Pair : itemNameCache) {
                if ((((Integer) Pair.getLeft()) >= 2_380_000) && (((Integer) Pair.getLeft()) <= lastmonstercardid)) {
                    bookName.append((String) Pair.getRight());
                    
                    if (bookName.toString().contains(" Card")) {
                        bookName.delete(bookName.length() - 5, bookName.length());
                    }
                    if (((Integer) Pair.getLeft()) != lastmonsterbookid) {
                        for (Pair Pair_ : mobCache) {
                            if (((MobInfo) Pair_.getRight()).getName().equalsIgnoreCase(bookName.toString())) {
                                SQL.append("INSERT INTO ").append("monstercarddata").append(" VALUES (");
                                SQL.append(i).append(", ");
                                SQL.append(Pair.getLeft());
                                SQL.append(", ");
                                SQL.append(Pair_.getLeft()).append(");\n");
                                lastmonsterbookid = ((Integer) Pair.getLeft());
                                i++;
                                break;
                            }
                        }
                        bookName.delete(0, 2_147_483_647);
                    }
                }
            }
            out.write(SQL.toString().getBytes());
        }
        long time = System.currentTimeMillis() - currtime;
        time /= 1_000L;

        System.out.println(new StringBuilder().append("Time taken : ").append(time).toString());
    }

    private static void retriveNLogItemName(StringBuilder sb, int id) {
        for (Pair Pair : itemNameCache) {
            if (((Integer) Pair.getLeft()) == id) {
                sb.append((String) Pair.getRight());
                return;
            }
        }
        sb.append("MISSING STRING, ID : ");
        sb.append(id);
    }

    private static int IncrementRate(int itemid, int times) {
        if (times == 0) {
            if ((itemid == 1_002_357) || (itemid == 1_002_926) || (itemid == 1_002_927)) {
                return 999_999;
            }
            if (itemid == 1_122_000) {
                return 999_999;
            }
            if (itemid == 1_002_972) {
                return 999_999;
            }
        } else if (times == 1) {
            if ((itemid == 1_002_357) || (itemid == 1_002_926) || (itemid == 1_002_927)) {
                return 999_999;
            }
            if (itemid == 1_122_000) {
                return 999_999;
            }
            if (itemid == 1_002_972) {
                return 300_000;
            }
        } else if (times == 2) {
            if ((itemid == 1_002_357) || (itemid == 1_002_926) || (itemid == 1_002_927)) {
                return 300_000;
            }
            if (itemid == 1_122_000) {
                return 300_000;
            }
        } else if (times == 3) {
            if ((itemid == 1_002_357) || (itemid == 1_002_926) || (itemid == 1_002_927)) {
                return 300_000;
            }
        } else if ((times == 4) && ((itemid == 1_002_357) || (itemid == 1_002_926) || (itemid == 1_002_927))) {
            return 300_000;
        }

        return -1;
    }

    private static int multipleDropsIncrement(int itemid, int mobid) {
        switch (itemid) {
            case 1_002_357:
            case 1_002_390:
            case 1_002_430:
            case 1_002_926:
            case 1_002_927:
                return 5;
            case 1_122_000:
                return 4;
            case 4_021_010:
                return 7;
            case 1_002_972:
                return 2;
            case 4_000_172:
                if (mobid == 7_220_001) {
                    return 8;
                }
                return 1;
            case 4_000_000:
            case 4_000_003:
            case 4_000_005:
            case 4_000_016:
            case 4_000_018:
            case 4_000_019:
            case 4_000_021:
            case 4_000_026:
            case 4_000_029:
            case 4_000_031:
            case 4_000_032:
            case 4_000_033:
            case 4_000_043:
            case 4_000_044:
            case 4_000_073:
            case 4_000_074:
            case 4_000_113:
            case 4_000_114:
            case 4_000_115:
            case 4_000_117:
            case 4_000_118:
            case 4_000_119:
            case 4_000_166:
            case 4_000_167:
            case 4_000_195:
            case 4_000_268:
            case 4_000_269:
            case 4_000_270:
            case 4_000_283:
            case 4_000_284:
            case 4_000_285:
            case 4_000_289:
            case 4_000_298:
            case 4_000_329:
            case 4_000_330:
            case 4_000_331:
            case 4_000_356:
            case 4_000_364:
            case 4_000_365:
                if ((mobid == 2_220_000) || (mobid == 3_220_000) || (mobid == 3_220_001) || (mobid == 4_220_000) || (mobid == 5_220_000) || (mobid == 5_220_002) || (mobid == 5_220_003) || (mobid == 6_220_000) || (mobid == 4_000_119) || (mobid == 7_220_000) || (mobid == 7_220_002) || (mobid == 8_220_000) || (mobid == 8_220_002) || (mobid == 8_220_003)) {
                    return 3;
                }
                return 1;
        }
        return 1;
    }

    private static int getChance(int id, int mobid, boolean boss) {
        switch (id / 10_000) {
            case 100:
                switch (id) {
                    case 1_002_357:
                    case 1_002_390:
                    case 1_002_430:
                    case 1_002_905:
                    case 1_002_906:
                    case 1_002_926:
                    case 1_002_927:
                    case 1_002_972:
                        return 300_000;
                }
                return 1_500;
            case 103:
                switch (id) {
                    case 1_032_062:
                        return 100;
                }
                return 1_000;
            case 105:
            case 109:
                switch (id) {
                    case 1_092_049:
                        return 100;
                }
                return 700;
            case 104:
            case 106:
            case 107:
                switch (id) {
                    case 1_072_369:
                        return 300_000;
                }
                return 800;
            case 108:
            case 110:
                return 1_000;
            case 112:
                switch (id) {
                    case 1_122_000:
                        return 300_000;
                    case 1_122_011:
                    case 1_122_012:
                        return 800_000;
                }
            case 130:
            case 131:
            case 132:
            case 137:
                switch (id) {
                    case 1_372_049:
                        return 999_999;
                }
                return 700;
            case 138:
            case 140:
            case 141:
            case 142:
            case 144:
                return 700;
            case 133:
            case 143:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
                return 500;
            case 204:
                switch (id) {
                    case 2_049_000:
                        return 150;
                }
                return 300;
            case 205:
                return 50_000;
            case 206:
                return 30_000;
            case 228:
                return 30_000;
            case 229:
                switch (id) {
                    case 2_290_096:
                        return 800_000;
                    case 2_290_125:
                        return 100_000;
                }
                return 500;
            case 233:
                switch (id) {
                    case 2_330_007:
                        return 50;
                }
                return 500;
            case 400:
                switch (id) {
                    case 4_000_021:
                        return 50_000;
                    case 4_001_094:
                        return 999_999;
                    case 4_001_000:
                        return 5_000;
                    case 4_000_157:
                        return 100_000;
                    case 4_001_023:
                    case 4_001_024:
                        return 999_999;
                    case 4_000_244:
                    case 4_000_245:
                        return 2_000;
                    case 4_001_005:
                        return 5_000;
                    case 4_001_006:
                        return 10_000;
                    case 4_000_017:
                    case 4_000_082:
                        return 40_000;
                    case 4_000_446:
                    case 4_000_451:
                    case 4_000_456:
                        return 10_000;
                    case 4_000_459:
                        return 20_000;
                    case 4_000_030:
                        return 60_000;
                    case 4_000_339:
                        return 70_000;
                    // case 4000313://黄金枫叶
                    // case 2022034://粽子
                    case 4_007_000:
                    case 4_007_001:
                    case 4_007_002:
                    case 4_007_003:
                    case 4_007_004:
                    case 4_007_005:
                    case 4_007_006:
                    case 4_007_007:
                    case 4_031_456:
                        return 100_000;
                    case 4_001_126:
                        return 50_000;
                }
                switch (id / 1_000) {
                    case 4_000:
                    case 4_001:
                        return 300_000;
                    case 4_003:
                        return 200_000;
                    case 4_004:
                    case 4_006:
                        return 10_000;
                    case 4_005:
                        return 1_000;
                    case 4_002:
                }
            case 401:
            case 402:
                switch (id) {
                    case 4_020_009:
                        return 5_000;
                    case 4_021_010:
                        return 300_000;
                }
                return 9_000;
            case 403:
                switch (id) {
                    case 4_032_024:
                        return 50_000;
                    case 4_032_181:
                        return boss ? 999_999 : 300_000;
                    case 4_032_025:
                    case 4_032_155:
                    case 4_032_156:
                    case 4_032_159:
                    case 4_032_161:
                    case 4_032_163:
                        return 600_000;
                    case 4_032_166:
                    case 4_032_167:
                    case 4_032_168:
                        return 10_000;
                    case 4_032_151:
                    case 4_032_158:
                    case 4_032_164:
                    case 4_032_180:
                        return 2_000;
                    case 4_032_152:
                    case 4_032_153:
                    case 4_032_154:
                        return 4_000;
                }
                return 300;
            case 413:
                return 6_000;
            case 416:
                return 6_000;
        }
        switch (id / 1_000_000) {
            case 1:
                return 999_999;
            case 2:
                switch (id) {
                    case 2_000_004:
                    case 2_000_005:
                        return boss ? 999_999 : 20_000;
                    case 2_000_006:
                        return mobid == 9_420_540 ? 50_000 : boss ? 999_999 : 20_000;
                    case 2_022_345:
                        return boss ? 999_999 : 3_000;
                    case 2_012_002:
                        return 6_000;
                    case 2_020_013:
                    case 2_020_015:
                        return boss ? 999_999 : 20_000;
                    case 2_060_000:
                    case 2_060_001:
                    case 2_061_000:
                    case 2_061_001:
                        return 25_000;
                    case 2_070_000:
                    case 2_070_001:
                    case 2_070_002:
                    case 2_070_003:
                    case 2_070_004:
                    case 2_070_008:
                    case 2_070_009:
                    case 2_070_010:
                        return 500;
                    case 2_070_005:
                        return 400;
                    case 2_070_006:
                    case 2_070_007:
                        return 200;
                    case 2_070_012:
                    case 2_070_013:
                        return 1_500;
                    case 2_070_019:
                        return 100;
                    case 2_210_006:
                        return 999_999;
                }
                return 20_000;
            case 3:
                switch (id) {
                    case 3_010_007:
                    case 3_010_008:
                        return 500;
                }
                return 2_000;
        }
        System.out.println(new StringBuilder().append("未处理的数据, ID : ").append(id).toString());
        return 999_999;
    }

    private static Map<Integer, List<Integer>> getDropsNotInMonsterBook() {
        Map drops = new HashMap();

        List IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(4_000_139);
        IndiviualMonsterDrop.add(2_002_011);
        IndiviualMonsterDrop.add(2_002_011);
        IndiviualMonsterDrop.add(2_002_011);
        IndiviualMonsterDrop.add(2_000_004);
        IndiviualMonsterDrop.add(2_000_004);

        drops.put(9_400_112, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(4_000_140);
        IndiviualMonsterDrop.add(2_022_027);
        IndiviualMonsterDrop.add(2_022_027);
        IndiviualMonsterDrop.add(2_000_004);
        IndiviualMonsterDrop.add(2_000_004);
        IndiviualMonsterDrop.add(2_002_008);
        IndiviualMonsterDrop.add(2_002_008);

        drops.put(9_400_113, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(4_000_141);
        IndiviualMonsterDrop.add(2_000_004);
        IndiviualMonsterDrop.add(2_040_813);
        IndiviualMonsterDrop.add(2_041_030);
        IndiviualMonsterDrop.add(2_041_040);
        IndiviualMonsterDrop.add(1_072_238);
        IndiviualMonsterDrop.add(1_032_026);
        IndiviualMonsterDrop.add(1_372_011);

        drops.put(9_400_300, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(4_000_225);
        IndiviualMonsterDrop.add(2_000_006);
        IndiviualMonsterDrop.add(2_000_004);
        IndiviualMonsterDrop.add(2_070_013);
        IndiviualMonsterDrop.add(2_002_005);
        IndiviualMonsterDrop.add(2_022_018);
        IndiviualMonsterDrop.add(2_040_306);
        IndiviualMonsterDrop.add(2_043_704);
        IndiviualMonsterDrop.add(2_044_605);
        IndiviualMonsterDrop.add(2_041_034);
        IndiviualMonsterDrop.add(1_032_019);
        IndiviualMonsterDrop.add(1_102_013);
        IndiviualMonsterDrop.add(1_322_026);
        IndiviualMonsterDrop.add(1_092_015);
        IndiviualMonsterDrop.add(1_382_016);
        IndiviualMonsterDrop.add(1_002_276);
        IndiviualMonsterDrop.add(1_002_403);
        IndiviualMonsterDrop.add(1_472_027);

        drops.put(9_400_013, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(1_372_049);

        drops.put(8_800_002, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(4_001_094);
        IndiviualMonsterDrop.add(2_290_125);

        drops.put(8_810_018, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(4_000_138);
        IndiviualMonsterDrop.add(4_010_006);
        IndiviualMonsterDrop.add(2_000_006);
        IndiviualMonsterDrop.add(2_000_011);
        IndiviualMonsterDrop.add(2_020_016);
        IndiviualMonsterDrop.add(2_022_024);
        IndiviualMonsterDrop.add(2_022_026);
        IndiviualMonsterDrop.add(2_043_705);
        IndiviualMonsterDrop.add(2_040_716);
        IndiviualMonsterDrop.add(2_040_908);
        IndiviualMonsterDrop.add(2_040_510);
        IndiviualMonsterDrop.add(1_072_239);
        IndiviualMonsterDrop.add(1_422_013);
        IndiviualMonsterDrop.add(1_402_016);
        IndiviualMonsterDrop.add(1_442_020);
        IndiviualMonsterDrop.add(1_432_011);
        IndiviualMonsterDrop.add(1_332_022);
        IndiviualMonsterDrop.add(1_312_015);
        IndiviualMonsterDrop.add(1_382_010);
        IndiviualMonsterDrop.add(1_372_009);
        IndiviualMonsterDrop.add(1_082_085);
        IndiviualMonsterDrop.add(1_332_022);
        IndiviualMonsterDrop.add(1_472_033);

        drops.put(9_400_121, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        IndiviualMonsterDrop.add(4_032_024);
        IndiviualMonsterDrop.add(4_032_025);
        IndiviualMonsterDrop.add(4_020_006);
        IndiviualMonsterDrop.add(4_020_008);
        IndiviualMonsterDrop.add(4_010_001);
        IndiviualMonsterDrop.add(4_004_001);
        IndiviualMonsterDrop.add(2_070_006);
        IndiviualMonsterDrop.add(2_044_404);
        IndiviualMonsterDrop.add(2_044_702);
        IndiviualMonsterDrop.add(2_044_305);
        IndiviualMonsterDrop.add(1_102_029);
        IndiviualMonsterDrop.add(1_032_023);
        IndiviualMonsterDrop.add(1_402_004);
        IndiviualMonsterDrop.add(1_072_210);
        IndiviualMonsterDrop.add(1_040_104);
        IndiviualMonsterDrop.add(1_060_092);
        IndiviualMonsterDrop.add(1_082_129);
        IndiviualMonsterDrop.add(1_442_008);
        IndiviualMonsterDrop.add(1_072_178);
        IndiviualMonsterDrop.add(1_050_092);
        IndiviualMonsterDrop.add(1_002_271);
        IndiviualMonsterDrop.add(1_051_053);
        IndiviualMonsterDrop.add(1_382_008);
        IndiviualMonsterDrop.add(1_002_275);
        IndiviualMonsterDrop.add(1_051_082);
        IndiviualMonsterDrop.add(1_050_064);
        IndiviualMonsterDrop.add(1_472_028);
        IndiviualMonsterDrop.add(1_072_193);
        IndiviualMonsterDrop.add(1_072_172);
        IndiviualMonsterDrop.add(1_002_285);

        drops.put(9_400_545, IndiviualMonsterDrop);

        IndiviualMonsterDrop = new ArrayList();

        return drops;
    }

    private static void getAllItems() {
        // MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(new StringBuilder().append(System.getProperty("wzpath")).append("/String.wz").toString()));

        List itemPairs = new ArrayList();

        MapleData itemsData = data.getData("Cash.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair(itemId, itemName));
        }

        itemsData = data.getData("Consume.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair(itemId, itemName));
        }

        itemsData = data.getData("Eqp.img").getChildByPath("Eqp");
        for (MapleData eqpType : itemsData.getChildren()) {
            for (MapleData itemFolder : eqpType.getChildren()) {
                int itemId = Integer.parseInt(itemFolder.getName());
                String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
                itemPairs.add(new Pair(itemId, itemName));
            }
        }

        itemsData = data.getData("Etc.img").getChildByPath("Etc");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair(itemId, itemName));
        }

        itemsData = data.getData("Ins.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair(itemId, itemName));
        }

        itemsData = data.getData("Pet.img");
        for (MapleData itemFolder : itemsData.getChildren()) {
            int itemId = Integer.parseInt(itemFolder.getName());
            String itemName = MapleDataTool.getString("name", itemFolder, "NO-NAME");
            itemPairs.add(new Pair(itemId, itemName));
        }
        itemNameCache.addAll(itemPairs);
    }

    /**
     *
     */
    public static void getAllMobs() {
        List itemPairs = new ArrayList();
        // MapleDataProvider data = MapleDataProviderFactory.getDataProvider(new File(new StringBuilder().append(System.getProperty("wzpath")).append("/String.wz").toString()));
        //   MapleDataProvider mobData = MapleDataProviderFactory.getDataProvider(new File(new StringBuilder().append(System.getProperty("wzpath")).append("/Mob.wz").toString()));
        MapleData mob = data.getData("Mob.img");

        for (MapleData itemFolder : mob.getChildren()) {
            int id = Integer.parseInt(itemFolder.getName());
            try {
                MapleData monsterData = mobData.getData(StringUtil.getLeftPaddedStr(new StringBuilder().append(Integer.toString(id)).append(".img").toString(), '0', 11));
                int boss = id == 8_810_018 ? 1 : MapleDataTool.getIntConvert("boss", monsterData.getChildByPath("info"), 0);

                if (boss > 0) {
                    bossCache.put(id, true);
                }

                MobInfo mobInfo = new MobInfo(boss, MapleDataTool.getIntConvert("rareItemDropLevel", monsterData.getChildByPath("info"), 0), MapleDataTool.getString("name", itemFolder, "NO-NAME"));

                itemPairs.add(new Pair(id, mobInfo));
            } catch (Exception fe) {
            }
        }
        mobCache.addAll(itemPairs);
    }

    /**
     *
     */
    public static class MobInfo {

        /**
         *
         */
        public int boss;

        /**
         *
         */
        public int rareItemDropLevel;

        /**
         *
         */
        public String name;

        /**
         *
         * @param boss
         * @param rareItemDropLevel
         * @param name
         */
        public MobInfo(int boss, int rareItemDropLevel, String name) {
            this.boss = boss;
            this.rareItemDropLevel = rareItemDropLevel;
            this.name = name;
        }

        /**
         *
         * @return
         */
        public int getBoss() {
            return this.boss;
        }

        /**
         *
         * @return
         */
        public int rateItemDropLevel() {
            return this.rareItemDropLevel;
        }

        /**
         *
         * @return
         */
        public String getName() {
            return this.name;
        }
    }
}
