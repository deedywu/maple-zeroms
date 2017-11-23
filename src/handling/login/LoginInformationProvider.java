package handling.login;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import provider.MapleData;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;

/**
 *
 * @author zjj
 */
public class LoginInformationProvider {

    private final static LoginInformationProvider instance = new LoginInformationProvider();

    /**
     *
     */
    protected final List<String> ForbiddenName = new ArrayList<>();

    /**
     *
     * @return
     */
    public static LoginInformationProvider getInstance() {
        return instance;
    }

    /**
     *
     */
    protected LoginInformationProvider() {
        System.out.println("ZeroMS服务端----------[加载商城系统]成功!");
        final String WZpath = System.getProperty("net.sf.odinms.wzpath");
        final MapleData nameData = MapleDataProviderFactory.getDataProvider(new File(WZpath + "/Etc.wz")).getData("ForbiddenName.img");
        for (final MapleData data : nameData.getChildren()) {
            ForbiddenName.add(MapleDataTool.getString(data));
        }
    }

    /**
     *
     * @param in
     * @return
     */
    public final boolean isForbiddenName(final String in) {
        for (final String name : ForbiddenName) {
            if (in.contains(name)) {
                return true;
            }
        }
        return false;
    }
    /*  public static enum JobType {

        反抗者(0, "Resistance", 3000, 931000000),
        冒险家(1, "", 0, 0),
        骑士团(2, "Premium", 1000, 0),//913040000
        战神(3, "Orient", 2000, 0),//914000000
        龙神(4, "Evan", 2001, 900090000),
        双弩精灵(5, "", 2002, 910150000),
        恶魔猎手(6, "", 3001, 931050310),
        幻影(7, "", 2003, 910000000),
        龙的传人(8, "", 508, 910000000),
        米哈尔(9, "", 5000, 910000000), 
        夜光法师(10, "Luminous", 2004, 910000000), 
        狂龙战士(11, "Kaiser", 6000, 910000000), 
        爆莉萌天使(12, "Angelic", 6500, 910000000), 
        终极冒险家(-1, "Ultimate", 0, 130000000);
        public int type, id, map;
        public String job;
        private boolean 自由市场 = true;

        private JobType(int type, String job, int id, int map) {
            this.type = type;
            this.job = job;
            this.id = id;
            
            
            //新手出生地图设置
            
            this.map = (ServerProperties.getProperty("world.firstMap") != null ? Integer.parseInt(ServerProperties.getProperty("world.firstMap")) : 0);
            //this.map = (this.自由市场 ? 910000000 : map);
            
        }

        public static JobType getByJob(String g) {
            for (JobType e : values()) {
                if ((e.job.length() > 0) && (g.startsWith(e.job))) {
                    return e;
                }
            }
            return 冒险家;
        }

        public static JobType getByType(int g) {
            for (JobType e : values()) {
                if (e.type == g) {
                    return e;
                }
            }
            return 冒险家;
        }

        public static JobType getById(int g) {
            for (JobType e : values()) {
                if (e.id == g) {
                    return e;
                }
            }
            return 冒险家;
        }
    }*/
}
