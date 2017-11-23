
package server.maps;

import client.ISkill;
import client.MapleCharacter;
import client.MapleClient;
import client.SkillFactory;
import handling.MaplePacket;
import java.awt.Point;
import java.awt.Rectangle;
import server.MapleStatEffect;
import server.life.MapleMonster;
import server.life.MobSkill;
import tools.MaplePacketCreator;

/**
 *
 * @author zjj
 */
public class MapleMist extends AbstractMapleMapObject {

    private Rectangle mistPosition;
    private MapleStatEffect source;
    private MobSkill skill;
    private boolean isMobMist;
    private int skillDelay, skilllevel, isPoisonMist, ownerId;

    /**
     *
     * @param mistPosition
     * @param mob
     * @param skill
     */
    public MapleMist(Rectangle mistPosition, MapleMonster mob, MobSkill skill) {
        this.mistPosition = mistPosition;
        this.ownerId = mob.getId();
        this.skill = skill;
        this.skilllevel = skill.getSkillLevel();

        isMobMist = true;
        isPoisonMist = 0;
        skillDelay = 0;
    }

    /**
     *
     * @param mistPosition
     * @param owner
     * @param source
     */
    public MapleMist(Rectangle mistPosition, MapleCharacter owner, MapleStatEffect source) {
        this.mistPosition = mistPosition;
        this.ownerId = owner.getId();
        this.source = source;
        this.skillDelay = 8;
        this.isMobMist = false;
        this.skilllevel = owner.getSkillLevel(SkillFactory.getSkill(source.getSourceId()));

        switch (source.getSourceId()) {
            case 4_221_006: // Smoke Screen
                isPoisonMist = 0;
                break;
            case 14_111_006:
            case 2_111_003: // FP mist
            case 12_111_005: // Flame wizard, [Flame Gear]
                isPoisonMist = 1;
                break;
            case 22_161_003: //Recovery Aura
                isPoisonMist = 2;
                break;
        }
    }

    //fake

    /**
     *
     * @param mistPosition
     * @param owner
     */
    public MapleMist(Rectangle mistPosition, MapleCharacter owner) {
        this.mistPosition = mistPosition;
        this.ownerId = owner.getId();
        this.source = new MapleStatEffect();
        this.source.setSourceId(2_111_003);
        this.skilllevel = 30;
        isMobMist = false;
        isPoisonMist = 0;
        skillDelay = 8;
    }

    /**
     *
     * @return
     */
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.MIST;
    }

    /**
     *
     * @return
     */
    @Override
    public Point getPosition() {
        return mistPosition.getLocation();
    }

    /**
     *
     * @return
     */
    public ISkill getSourceSkill() {
        return SkillFactory.getSkill(source.getSourceId());
    }

    /**
     *
     * @return
     */
    public boolean isMobMist() {
        return isMobMist;
    }

    /**
     *
     * @return
     */
    public int isPoisonMist() {
        return isPoisonMist;
    }

    /**
     *
     * @return
     */
    public int getSkillDelay() {
        return skillDelay;
    }

    /**
     *
     * @return
     */
    public int getSkillLevel() {
        return skilllevel;
    }

    /**
     *
     * @return
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     *
     * @return
     */
    public MobSkill getMobSkill() {
        return this.skill;
    }

    /**
     *
     * @return
     */
    public Rectangle getBox() {
        return mistPosition;
    }

    /**
     *
     * @return
     */
    public MapleStatEffect getSource() {
        return source;
    }

    /**
     *
     * @param position
     */
    @Override
    public void setPosition(Point position) {
    }

    /**
     *
     * @param level
     * @return
     */
    public MaplePacket fakeSpawnData(int level) {
        return MaplePacketCreator.spawnMist(this);
    }

    /**
     *
     * @param c
     */
    @Override
    public void sendSpawnData(final MapleClient c) {
        c.getSession().write(MaplePacketCreator.spawnMist(this));
    }

    /**
     *
     * @param c
     */
    @Override
    public void sendDestroyData(final MapleClient c) {
        c.getSession().write(MaplePacketCreator.removeMist(getObjectId(), false));
    }

    /**
     *
     * @return
     */
    public boolean makeChanceResult() {
        return source.makeChanceResult();
    }
}
