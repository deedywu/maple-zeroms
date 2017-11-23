package client;

import server.MapleStatEffect;
import server.life.Element;

/**
 *
 * @author zjj
 */
public interface ISkill {

    /**
     *
     * @return
     */
    int getId();

    /**
     *
     * @param level
     * @return
     */
    MapleStatEffect getEffect(int level);

    /**
     *
     * @return
     */
    byte getMaxLevel();

    /**
     *
     * @return
     */
    int getAnimationTime();

    /**
     *
     * @param job
     * @return
     */
    public boolean canBeLearnedBy(int job);

    /**
     *
     * @return
     */
    public boolean isFourthJob();

    /**
     *
     * @return
     */
    public boolean getAction();

    /**
     *
     * @return
     */
    public boolean isTimeLimited();

    /**
     *
     * @return
     */
    public int getMasterLevel();

    /**
     *
     * @return
     */
    public Element getElement();

    /**
     *
     * @return
     */
    public boolean isBeginnerSkill();

    /**
     *
     * @return
     */
    public boolean hasRequiredSkill();

    /**
     *
     * @return
     */
    public boolean isInvisible();

    /**
     *
     * @return
     */
    public boolean isChargeSkill();

    /**
     *
     * @return
     */
    public int getRequiredSkillLevel();

    /**
     *
     * @return
     */
    public int getRequiredSkillId();

    /**
     *
     * @return
     */
    public String getName();
}
