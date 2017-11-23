
package server;

import client.MapleCharacter;
import handling.world.MaplePartyCharacter;
import java.lang.ref.WeakReference;

/**
 * TODO : Make this a function for NPC instead.. cleaner
 *
 * @author Rob
 */
public class MapleCarnivalChallenge {

    WeakReference<MapleCharacter> challenger;
    String challengeinfo = "";

    /**
     *
     * @param challenger
     */
    public MapleCarnivalChallenge(MapleCharacter challenger) {
        this.challenger = new WeakReference<>(challenger);
        challengeinfo += "#b";
        for (MaplePartyCharacter pc : challenger.getParty().getMembers()) {
            MapleCharacter c = challenger.getMap().getCharacterById(pc.getId());
            if (c != null) {
                challengeinfo += (c.getName() + " / 等級" + c.getLevel() + " / " + getJobNameById(c.getJob()) + "\r\n");
            }
        }
        challengeinfo += "#k";
    }

    /**
     *
     * @return
     */
    public MapleCharacter getChallenger() {
        return challenger.get();
    }

    /**
     *
     * @return
     */
    public String getChallengeInfo() {
        return challengeinfo;
    }

    /**
     *
     * @param job
     * @return
     */
    public static final String getJobNameById(int job) {
        switch (job) {
            case 0:
                return "初心者";
            case 1_000:
                return "Nobless";
            case 2_000:
                return "Legend";
            case 2_001:
                return "Evan";
            case 3_000:
                return "Citizen";

            case 100:
                return "劍士";// Warrior
            case 110:
                return "狂戰士";
            case 111:
                return "狂戰士";
            case 112:
                return "英雄";
            case 120:
                return "見習騎士";
            case 121:
                return "騎士";
            case 122:
                return "聖戰士";
            case 130:
                return "槍騎兵";
            case 131:
                return "龍騎士";
            case 132:
                return "黑騎士";

            case 200:
                return "法師";
            case 210:
                return "巫師(火,毒)";
            case 211:
                return "魔導士(火,毒)";
            case 212:
                return "大魔導士(火,毒)";
            case 220:
                return "巫師(冰,雷)";
            case 221:
                return "魔導士(冰,雷)";
            case 222:
                return "大魔導士(冰,雷)";
            case 230:
                return "牧师";
            case 231:
                return "祭司";
            case 232:
                return "主教";

            case 300:
                return "弓箭手";
            case 310:
                return "獵人";
            case 311:
                return "遊俠";
            case 312:
                return "箭神";
            case 320:
                return "弩弓手";
            case 321:
                return "狙擊手";
            case 322:
                return "神射手";

            case 400:
                return "盜賊";
            case 410:
                return "刺客";
            case 411:
                return "暗殺者";
            case 412:
                return "夜使者";
            case 420:
                return "俠盜";
            case 421:
                return "神偷";
            case 422:
                return "暗影神偷";
            case 430:
                return "Blade Recruit";
            case 431:
                return "Blade Acolyte";
            case 432:
                return "Blade Specialist";
            case 433:
                return "Blade Lord";
            case 434:
                return "Blade Master";

            case 500:
                return "海盜";
            case 510:
                return "打手";
            case 511:
                return "格鬥家";
            case 512:
                return "拳霸";
            case 520:
                return "槍手";
            case 521:
                return "神槍手";
            case 522:
                return "槍神";

            case 1_100:
            case 1_110:
            case 1_111:
            case 1_112:
                return "Soul Master";

            case 1_200:
            case 1_210:
            case 1_211:
            case 1_212:
                return "Flame Wizard";

            case 1_300:
            case 1_310:
            case 1_311:
            case 1_312:
                return "Wind Breaker";

            case 1_400:
            case 1_410:
            case 1_411:
            case 1_412:
                return "Night Walker";

            case 1_500:
            case 1_510:
            case 1_511:
            case 1_512:
                return "Striker";

            case 2_100:
            case 2_110:
            case 2_111:
            case 2_112:
                return "Aran";

            case 2_200:
            case 2_210:
            case 2_211:
            case 2_212:
            case 2_213:
            case 2_214:
            case 2_215:
            case 2_216:
            case 2_217:
            case 2_218:
                return "Evan";

            case 3_200:
            case 3_210:
            case 3_211:
            case 3_212:
                return "Battle Mage";

            case 3_300:
            case 3_310:
            case 3_311:
            case 3_312:
                return "Wild Hunter";

            case 3_500:
            case 3_510:
            case 3_511:
            case 3_512:
                return "Mechanic";

            default:
                return "Unknown Job";
        }
    }

    /**
     *
     * @param job
     * @return
     */
    public static final String getJobBasicNameById(int job) {
        switch (job) {
            case 0:
            case 1_000:
            case 2_000:
            case 2_001:
            case 3_000:
                return "初心者";

            case 2_100:
            case 2_110:
            case 2_111:
            case 2_112:
            case 1_100:
            case 1_110:
            case 1_111:
            case 1_112:
            case 100:
            case 110:
            case 111:
            case 112:
            case 120:
            case 121:
            case 122:
            case 130:
            case 131:
            case 132:
                return "劍士";

            case 2_200:
            case 2_210:
            case 2_211:
            case 2_212:
            case 2_213:
            case 2_214:
            case 2_215:
            case 2_216:
            case 2_217:
            case 2_218:
            case 3_200:
            case 3_210:
            case 3_211:
            case 3_212:
            case 1_200:
            case 1_210:
            case 1_211:
            case 1_212:
            case 200:
            case 210:
            case 211:
            case 212:
            case 220:
            case 221:
            case 222:
            case 230:
            case 231:
            case 232:
                return "法師";

            case 3_300:
            case 3_310:
            case 3_311:
            case 3_312:
            case 1_300:
            case 1_310:
            case 1_311:
            case 1_312:
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322:
                return "弓箭手";

            case 1_400:
            case 1_410:
            case 1_411:
            case 1_412:
            case 400:
            case 410:
            case 411:
            case 412:
            case 420:
            case 421:
            case 422:
            case 430:
            case 431:
            case 432:
            case 433:
            case 434:
                return "盜賊";

            case 3_500:
            case 3_510:
            case 3_511:
            case 3_512:
            case 1_500:
            case 1_510:
            case 1_511:
            case 1_512:
            case 500:
            case 510:
            case 511:
            case 512:
            case 520:
            case 521:
            case 522:
                return "海盜";

            default:
                return "Unknown Job";
        }
    }
}
