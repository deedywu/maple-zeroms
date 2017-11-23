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
package handling.channel;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import server.Timer;

/**
 *
 * @author zjj
 */
public class MapleGuildRanking {

    private static final MapleGuildRanking instance = new MapleGuildRanking();
    private final List<GuildRankingInfo> ranks = new LinkedList<>();
    private final List<levelRankingInfo> ranks1 = new LinkedList<>();
    private final List<mesoRankingInfo> ranks2 = new LinkedList<>();

    /**
     *
     */
    public void RankingUpdate() {
        System.out.println("ZeroMS服务端----------[加载排行系统]成功!");
        Timer.WorldTimer.getInstance().register(new Runnable() {

            @Override
            public void run() {
                try {
                    reload();
                    showLevelRank();
                    showMesoRank();
                    //   System.out.println("Ranking update");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.err.println("Could not update rankings");
                }
            }
        }, 1 * 60 * 60 * 1_000, 1 * 60 * 60 * 1_000);
    }

    /**
     *
     * @return
     */
    public static MapleGuildRanking getInstance() {
        return instance;
    }

    /**
     *
     * @return
     */
    public List<GuildRankingInfo> getGuildRank() {
        if (ranks.isEmpty()) {
            reload();
        }
        return ranks;
    }

    /**
     *
     * @return
     */
    public List<levelRankingInfo> getLevelRank() {
        if (ranks1.isEmpty()) {
            showLevelRank();
        }
        return ranks1;
    }

    /**
     *
     * @return
     */
    public List<mesoRankingInfo> getMesoRank() {
        if (ranks2.isEmpty()) {
            showMesoRank();
        }
        return ranks2;
    }

    private void reload() {
        ranks.clear();

        Connection con = DatabaseConnection.getConnection();
        ResultSet rs;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM guilds ORDER BY `GP` DESC LIMIT 50")) {
            rs = ps.executeQuery();
            while (rs.next()) {
                final GuildRankingInfo rank = new GuildRankingInfo(
                        rs.getString("name"),
                        rs.getInt("GP"),
                        rs.getInt("logo"),
                        rs.getInt("logoColor"),
                        rs.getInt("logoBG"),
                        rs.getInt("logoBGColor"));

                ranks.add(rank);
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println("家族排行错误" + e);
        }
    }

    private void showLevelRank() {
        ranks1.clear();
        try {
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs;
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE gm < 1 ORDER BY `level` DESC LIMIT 100")) {
                rs = ps.executeQuery();
                while (rs.next()) {
                    final levelRankingInfo rank1 = new levelRankingInfo(
                            rs.getString("name"),
                            rs.getInt("level"),
                            rs.getInt("str"),
                            rs.getInt("dex"),
                            rs.getInt("int"),
                            rs.getInt("luk"));
                    ranks1.add(rank1);
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("人物排行错误");
        }
    }

    private void showMesoRank() {
        ranks2.clear();

        Connection con = DatabaseConnection.getConnection();
        ResultSet rs;
        try (PreparedStatement ps = con.prepareStatement("SELECT *, ( chr.meso + s.meso ) as money FROM `characters` as chr , `storages` as s WHERE chr.gm < 1  AND s.accountid = chr.accountid ORDER BY money DESC LIMIT 20")) {
            rs = ps.executeQuery();
            while (rs.next()) {
                final mesoRankingInfo rank2 = new mesoRankingInfo(
                        rs.getString("name"),
                        rs.getLong("money"),
                        rs.getInt("str"),
                        rs.getInt("dex"),
                        rs.getInt("int"),
                        rs.getInt("luk"));
                ranks2.add(rank2);
            }

            rs.close();
        } catch (SQLException e) {
            System.err.println("金币排行错误");
        }
    }

    /**
     *
     */
    public static class mesoRankingInfo {

        private final String name;
        private final long meso;
        private final int str, dex, _int, luk;

        /**
         *
         * @param name
         * @param meso
         * @param str
         * @param dex
         * @param intt
         * @param luk
         */
        public mesoRankingInfo(String name, long meso, int str, int dex, int intt, int luk) {
            this.name = name;
            this.meso = meso;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
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
         * @return
         */
        public long getMeso() {
            return meso;
        }

        /**
         *
         * @return
         */
        public int getStr() {
            return str;
        }

        /**
         *
         * @return
         */
        public int getDex() {
            return dex;
        }

        /**
         *
         * @return
         */
        public int getInt() {
            return _int;
        }

        /**
         *
         * @return
         */
        public int getLuk() {
            return luk;
        }
    }

    /**
     *
     */
    public static class levelRankingInfo {

        private final String name;
        private final int level, str, dex, _int, luk;

        /**
         *
         * @param name
         * @param level
         * @param str
         * @param dex
         * @param intt
         * @param luk
         */
        public levelRankingInfo(String name, int level, int str, int dex, int intt, int luk) {
            this.name = name;
            this.level = level;
            this.str = str;
            this.dex = dex;
            this._int = intt;
            this.luk = luk;
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
         * @return
         */
        public int getLevel() {
            return level;
        }

        /**
         *
         * @return
         */
        public int getStr() {
            return str;
        }

        /**
         *
         * @return
         */
        public int getDex() {
            return dex;
        }

        /**
         *
         * @return
         */
        public int getInt() {
            return _int;
        }

        /**
         *
         * @return
         */
        public int getLuk() {
            return luk;
        }
    }

    /**
     *
     */
    public static class GuildRankingInfo {

        private final String name;
        private final int gp, logo, logocolor, logobg, logobgcolor;

        /**
         *
         * @param name
         * @param gp
         * @param logo
         * @param logocolor
         * @param logobg
         * @param logobgcolor
         */
        public GuildRankingInfo(String name, int gp, int logo, int logocolor, int logobg, int logobgcolor) {
            this.name = name;
            this.gp = gp;
            this.logo = logo;
            this.logocolor = logocolor;
            this.logobg = logobg;
            this.logobgcolor = logobgcolor;
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
         * @return
         */
        public int getGP() {
            return gp;
        }

        /**
         *
         * @return
         */
        public int getLogo() {
            return logo;
        }

        /**
         *
         * @return
         */
        public int getLogoColor() {
            return logocolor;
        }

        /**
         *
         * @return
         */
        public int getLogoBg() {
            return logobg;
        }

        /**
         *
         * @return
         */
        public int getLogoBgColor() {
            return logobgcolor;
        }
    }
}
