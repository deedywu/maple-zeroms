package server.quest;

import client.MapleCharacter;
import client.MapleQuestStatus;
import constants.GameConstants;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.NPCScriptManager;
import tools.FileoutputUtil;
import tools.MaplePacketCreator;
import tools.Pair;

/**
 *
 * @author zjj
 */
public class MapleQuest implements Serializable {

    private static final long serialVersionUID = 9_179_541_993_413_738_569L;
    private static Map<Integer, MapleQuest> quests = new LinkedHashMap<>();

    /**
     *
     */
    protected int id;

    /**
     *
     */
    protected List<MapleQuestRequirement> startReqs;

    /**
     *
     */
    protected List<MapleQuestRequirement> completeReqs;

    /**
     *
     */
    protected List<MapleQuestAction> startActs;

    /**
     *
     */
    protected List<MapleQuestAction> completeActs;

    /**
     *
     */
    protected Map<String, List<Pair<String, Pair<String, Integer>>>> partyQuestInfo; //[rank, [more/less/equal, [property, value]]]

    /**
     *
     */
    protected Map<Integer, Integer> relevantMobs;
    private boolean autoStart = false;
    private boolean autoPreComplete = false;
    private boolean repeatable = false, customend = false;
    private int viewMedalItem = 0, selectedSkillID = 0;

    /**
     *
     */
    protected String name = "";
    private static MapleDataProvider questData;
    private static MapleData actions;
    private static MapleData requirements;
    private static MapleData info;
    private static MapleData pinfo;

    /**
     *
     * @param id
     */
    protected MapleQuest(final int id) {
        relevantMobs = new LinkedHashMap<>();
        startReqs = new LinkedList<>();
        completeReqs = new LinkedList<>();
        startActs = new LinkedList<>();
        completeActs = new LinkedList<>();
        partyQuestInfo = new LinkedHashMap<>();
        this.id = id;
    }

    /**
     * Creates a new instance of MapleQuest
     */
    private static boolean loadQuest(MapleQuest ret, int id) throws NullPointerException {
        // read reqs
        final MapleData basedata1 = requirements.getChildByPath(String.valueOf(id));
        final MapleData basedata2 = actions.getChildByPath(String.valueOf(id));

        if (basedata1 == null || basedata2 == null) {
            return false;
        }
        //-------------------------------------------------
        final MapleData startReqData = basedata1.getChildByPath("0");
        if (startReqData != null) {
            final List<MapleData> startC = startReqData.getChildren();
            if (startC != null && startC.size() > 0) {
                for (MapleData startReq : startC) {
                    final MapleQuestRequirementType type = MapleQuestRequirementType.getByWZName(startReq.getName());
                    if (type.equals(MapleQuestRequirementType.interval)) {
                        ret.repeatable = true;
                    }
                    final MapleQuestRequirement req = new MapleQuestRequirement(ret, type, startReq);
                    if (req.getType().equals(MapleQuestRequirementType.mob)) {
                        for (MapleData mob : startReq.getChildren()) {
                            ret.relevantMobs.put(
                                    MapleDataTool.getInt(mob.getChildByPath("id")),
                                    MapleDataTool.getInt(mob.getChildByPath("count"), 0));
                        }
                    }
                    ret.startReqs.add(req);
                }
            }
        }
        //-------------------------------------------------
        final MapleData completeReqData = basedata1.getChildByPath("1");
        if (completeReqData != null) {
            final List<MapleData> completeC = completeReqData.getChildren();
            if (completeC != null && completeC.size() > 0) {
                for (MapleData completeReq : completeC) {
                    MapleQuestRequirement req = new MapleQuestRequirement(ret, MapleQuestRequirementType.getByWZName(completeReq.getName()), completeReq);
                    if (req.getType().equals(MapleQuestRequirementType.mob)) {
                        for (MapleData mob : completeReq.getChildren()) {
                            ret.relevantMobs.put(
                                    MapleDataTool.getInt(mob.getChildByPath("id")),
                                    MapleDataTool.getInt(mob.getChildByPath("count"), 0));
                        }
                    } else if (req.getType().equals(MapleQuestRequirementType.endscript)) {
                        ret.customend = true;
                    }
                    ret.completeReqs.add(req);
                }
            }
        }
        // read acts
        final MapleData startActData = basedata2.getChildByPath("0");
        if (startActData != null) {
            final List<MapleData> startC = startActData.getChildren();
            for (MapleData startAct : startC) {
                ret.startActs.add(new MapleQuestAction(MapleQuestActionType.getByWZName(startAct.getName()), startAct, ret));
            }
        }
        final MapleData completeActData = basedata2.getChildByPath("1");

        if (completeActData != null) {
            final List<MapleData> completeC = completeActData.getChildren();
            for (MapleData completeAct : completeC) {
                ret.completeActs.add(new MapleQuestAction(MapleQuestActionType.getByWZName(completeAct.getName()), completeAct, ret));
            }
        }
        final MapleData questInfo = info.getChildByPath(String.valueOf(id));
        if (questInfo != null) {
            ret.name = MapleDataTool.getString("name", questInfo, "");
            ret.autoStart = MapleDataTool.getInt("autoStart", questInfo, 0) == 1;
            ret.autoPreComplete = MapleDataTool.getInt("autoPreComplete", questInfo, 0) == 1;
            ret.viewMedalItem = MapleDataTool.getInt("viewMedalItem", questInfo, 0);
            ret.selectedSkillID = MapleDataTool.getInt("selectedSkillID", questInfo, 0);
        }

        final MapleData pquestInfo = pinfo.getChildByPath(String.valueOf(id));
        if (pquestInfo != null) {
            for (MapleData d : pquestInfo.getChildByPath("rank")) {
                List<Pair<String, Pair<String, Integer>>> pInfo = new ArrayList<>();
                //LinkedHashMap<String, List<Pair<String, Pair<String, Integer>>>>
                for (MapleData c : d) {
                    for (MapleData b : c) {
                        pInfo.add(new Pair<>(c.getName(), new Pair<>(b.getName(), MapleDataTool.getInt(b, 0))));
                    }
                }
                ret.partyQuestInfo.put(d.getName(), pInfo);
            }
        }

        return true;
    }

    /**
     *
     * @param rank
     * @return
     */
    public List<Pair<String, Pair<String, Integer>>> getInfoByRank(final String rank) {
        return partyQuestInfo.get(rank);
    }

    /**
     *
     * @return
     */
    public final int getSkillID() {
        return selectedSkillID;
    }

    /**
     *
     * @return
     */
    public final String getName() {
        return name;
    }

    /**
     *
     */
    public static void initQuests() {
        questData = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/Quest.wz"));
        actions = questData.getData("Act.img");
        requirements = questData.getData("Check.img");
        info = questData.getData("QuestInfo.img");
        pinfo = questData.getData("PQuest.img");
    }

    /**
     *
     */
    public static void clearQuests() {
        quests.clear();
        initQuests(); //test
    }

    /**
     *
     * @param id
     * @return
     */
    public static MapleQuest getInstance(int id) {
        MapleQuest ret = quests.get(id);
        if (ret == null) {
            ret = new MapleQuest(id);
            try {
                if (GameConstants.isCustomQuest(id) || !loadQuest(ret, id)) {
                    ret = new MapleCustomQuest(id);
                }
                quests.put(id, ret);
            } catch (Exception ex) {
                ex.printStackTrace();
                FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
                FileoutputUtil.log(FileoutputUtil.ScriptEx_Log, "Caused by questID " + id);
                System.out.println("Caused by questID " + id);
                return new MapleCustomQuest(id);
            }
        }
        return ret;
    }

    /**
     *
     * @param c
     * @param npcid
     * @return
     */
    public boolean canStart(MapleCharacter c, Integer npcid) {
        if (c.getQuest(this).getStatus() != 0 && !(c.getQuest(this).getStatus() == 2 && repeatable)) {
            return false;
        }
        for (MapleQuestRequirement r : startReqs) {
            if (!r.check(c, npcid)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param c
     * @param npcid
     * @return
     */
    public boolean canComplete(MapleCharacter c, Integer npcid) {
        if (c.getQuest(this).getStatus() != 1) {
            return false;
        }
        for (MapleQuestRequirement r : completeReqs) {
            if (!r.check(c, npcid)) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param c
     * @param itemid
     */
    public final void RestoreLostItem(final MapleCharacter c, final int itemid) {
        for (final MapleQuestAction a : startActs) {
            if (a.RestoreLostItem(c, itemid)) {
                break;
            }
        }
    }

    /**
     *
     * @param c
     * @param npc
     */
    public void start(MapleCharacter c, int npc) {
        if ((autoStart || checkNPCOnMap(c, npc)) && canStart(c, npc)) {
            for (MapleQuestAction a : startActs) {
                if (!a.checkEnd(c, null)) { //just in case
                    return;
                }
            }
            for (MapleQuestAction a : startActs) {
                a.runStart(c, null);
            }
            if (!customend) {
                forceStart(c, npc, null);
            } else {
                NPCScriptManager.getInstance().endQuest(c.getClient(), npc, getId(), true);
            }
        }
    }

    /**
     *
     * @param c
     * @param npc
     */
    public void complete(MapleCharacter c, int npc) {
        complete(c, npc, null);
    }

    /**
     *
     * @param c
     * @param npc
     * @param selection
     */
    public void complete(MapleCharacter c, int npc, Integer selection) {
        if ((autoPreComplete || checkNPCOnMap(c, npc)) && canComplete(c, npc)) {
            if (npc != 9_010_000) {
                for (MapleQuestAction a : completeActs) {
                    if (!a.checkEnd(c, selection)) {
                        return;
                    }
                }
                forceComplete(c, npc);
                for (MapleQuestAction a : completeActs) {
                    a.runEnd(c, selection);
                }
            }
            // we save forfeits only for logging purposes, they shouldn't matter anymore
            // completion time is set by the constructor

            c.getClient().getSession().write(MaplePacketCreator.showSpecialEffect(9)); // Quest completion
            c.getMap().broadcastMessage(c, MaplePacketCreator.showSpecialEffect(c.getId(), 9), false);
        } else {
            if (npc != 9_010_000) {
                for (MapleQuestAction a : completeActs) {
                    if (!a.checkEnd(c, selection)) {
                        return;
                    }
                }
                forceComplete(c, npc);
                for (MapleQuestAction a : completeActs) {
                    a.runEnd(c, selection);
                }
            }
            // we save forfeits only for logging purposes, they shouldn't matter anymore
            // completion time is set by the constructor

            c.getClient().getSession().write(MaplePacketCreator.showSpecialEffect(9)); // Quest completion
            c.getMap().broadcastMessage(c, MaplePacketCreator.showSpecialEffect(c.getId(), 9), false);
        }
    }

    /**
     *
     * @param c
     */
    public void forfeit(MapleCharacter c) {
        if (c.getQuest(this).getStatus() != (byte) 1) {
            return;
        }
        final MapleQuestStatus oldStatus = c.getQuest(this);
        final MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 0);
        newStatus.setForfeited(oldStatus.getForfeited() + 1);
        newStatus.setCompletionTime(oldStatus.getCompletionTime());
        c.updateQuest(newStatus);
    }

    /**
     *
     * @param c
     * @param npc
     * @param customData
     */
    public void forceStart(MapleCharacter c, int npc, String customData) {
        final MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 1, npc);
        newStatus.setForfeited(c.getQuest(this).getForfeited());
        newStatus.setCompletionTime(c.getQuest(this).getCompletionTime());
        newStatus.setCustomData(customData);
        c.updateQuest(newStatus);
    }

    /**
     *
     * @param c
     * @param npc
     */
    public void forceComplete(MapleCharacter c, int npc) {
        final MapleQuestStatus newStatus = new MapleQuestStatus(this, (byte) 2, npc);
        newStatus.setForfeited(c.getQuest(this).getForfeited());
        c.updateQuest(newStatus);
        c.getClient().getSession().write(MaplePacketCreator.showSpecialEffect(9)); // Quest completion
        c.getMap().broadcastMessage(c, MaplePacketCreator.showSpecialEffect(c.getId(), 9), false);
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
    public Map<Integer, Integer> getRelevantMobs() {
        return relevantMobs;
    }

    private boolean checkNPCOnMap(MapleCharacter player, int npcid) {
        //mir = 1013000
        return (GameConstants.isEvan(player.getJob()) && npcid == 1_013_000) || (player.getMap() != null && player.getMap().containsNPC(npcid));
    }

    /**
     *
     * @return
     */
    public int getMedalItem() {
        return viewMedalItem;
    }

    /**
     *
     */
    public static enum MedalQuest {

        /**
         *
         */
        新手冒险家(29_005, 29_015, 15, new int[]{104_000_000, 104_010_001, 100_000_006, 104_020_000, 100_000_000, 100_010_000, 100_040_000, 100_040_100, 101_010_103, 101_020_000, 101_000_000, 102_000_000, 101_030_104, 101_030_406, 102_020_300, 103_000_000, 102_050_000, 103_010_001, 103_030_200, 110_000_000}),

        /**
         *
         */
        ElNath(29_006, 29_012, 50, new int[]{200_000_000, 200_010_100, 200_010_300, 200_080_000, 200_080_100, 211_000_000, 211_030_000, 211_040_300, 211_040_400, 211_040_401}),

        /**
         *
         */
        LudusLake(29_007, 29_012, 40, new int[]{222_000_000, 222_010_400, 222_020_000, 220_000_000, 220_020_300, 220_040_200, 221_020_701, 221_000_000, 221_030_600, 221_040_400}),

        /**
         *
         */
        Underwater(29_008, 29_012, 40, new int[]{230_000_000, 230_010_400, 230_010_200, 230_010_201, 230_020_000, 230_020_201, 230_030_100, 230_040_000, 230_040_200, 230_040_400}),

        /**
         *
         */
        MuLung(29_009, 29_012, 50, new int[]{251_000_000, 251_010_200, 251_010_402, 251_010_500, 250_010_500, 250_010_504, 250_000_000, 250_010_300, 250_010_304, 250_020_300}),

        /**
         *
         */
        NihalDesert(29_010, 29_012, 70, new int[]{261_030_000, 261_020_401, 261_020_000, 261_010_100, 261_000_000, 260_020_700, 260_020_300, 260_000_000, 260_010_600, 260_010_300}),

        /**
         *
         */
        MinarForest(29_011, 29_012, 70, new int[]{240_000_000, 240_010_200, 240_010_800, 240_020_401, 240_020_101, 240_030_000, 240_040_400, 240_040_511, 240_040_521, 240_050_000}),

        /**
         *
         */
        Sleepywood(29_014, 29_015, 50, new int[]{105_040_300, 105_070_001, 105_040_305, 105_090_200, 105_090_300, 105_090_301, 105_090_312, 105_090_500, 105_090_900, 105_080_000});
        public int questid, 

        /**
         *
         */
        level, 

        /**
         *
         */
        lquestid;

        /**
         *
         */
        public int[] maps;

        private MedalQuest(int questid, int lquestid, int level, int[] maps) {
            this.questid = questid; //infoquest = questid -2005, customdata = questid -1995
            this.level = level;
            this.lquestid = lquestid;
            this.maps = maps; //note # of maps
        }
    }
}
