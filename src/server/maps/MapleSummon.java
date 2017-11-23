
package server.maps;

import client.MapleCharacter;
import client.MapleClient;
import client.anticheat.CheatingOffense;
import constants.GameConstants;
import java.awt.Point;
import server.MapleStatEffect;
import tools.MaplePacketCreator;

/**
 *
 * @author zjj
 */
public class MapleSummon extends AbstractAnimatedMapleMapObject {

    private final int ownerid, skillLevel, ownerLevel, skill;
    private int fh;
    private MapleMap map; //required for instanceMaps
    private short hp;
    private boolean changedMap = false;
    private SummonMovementType movementType;
    // Since player can have more than 1 summon [Pirate] 
    // Let's put it here instead of cheat tracker
    private int lastSummonTickCount;
    private byte Summon_tickResetCount;
    private long Server_ClientSummonTickDiff;

    /**
     *
     * @param owner
     * @param skill
     * @param pos
     * @param movementType
     */
    public MapleSummon(final MapleCharacter owner, final MapleStatEffect skill, final Point pos, final SummonMovementType movementType) {
        super();
        this.ownerid = owner.getId();
        this.ownerLevel = owner.getLevel();
        this.skill = skill.getSourceId();
        this.map = owner.getMap();
        this.skillLevel = skill.getLevel();
        this.movementType = movementType;
        setPosition(pos);
        try {
            this.fh = owner.getMap().getFootholds().findBelow(pos).getId();
        } catch (NullPointerException e) {
            this.fh = 0; //lol, it can be fixed by movement
        }

        if (!isPuppet()) { // Safe up 12 bytes of data, since puppet doesn't attack.
            lastSummonTickCount = 0;
            Summon_tickResetCount = 0;
            Server_ClientSummonTickDiff = 0;
        }
    }

    /**
     *
     * @param client
     */
    @Override
    public final void sendSpawnData(final MapleClient client) {
    }

    /**
     *
     * @param client
     */
    @Override
    public final void sendDestroyData(final MapleClient client) {
        client.getSession().write(MaplePacketCreator.removeSummon(this, false));
    }

    /**
     *
     * @param map
     */
    public final void updateMap(final MapleMap map) {
        this.map = map;
    }

    /**
     *
     * @return
     */
    public final MapleCharacter getOwner() {
        return map.getCharacterById(ownerid);
    }

    /**
     *
     * @return
     */
    public final int getFh() {
        return fh;
    }

    /**
     *
     * @param fh
     */
    public final void setFh(final int fh) {
        this.fh = fh;
    }

    /**
     *
     * @return
     */
    public final int getOwnerId() {
        return ownerid;
    }

    /**
     *
     * @return
     */
    public final int getOwnerLevel() {
        return ownerLevel;
    }

    /**
     *
     * @return
     */
    public final int getSkill() {
        return skill;
    }

    /**
     *
     * @return
     */
    public final short getHP() {
        return hp;
    }

    /**
     *
     * @param delta
     */
    public final void addHP(final short delta) {
        this.hp += delta;
    }

    /**
     *
     * @return
     */
    public final SummonMovementType getMovementType() {
        return movementType;
    }

    /**
     *
     * @return
     */
    public final boolean isPuppet() {
        switch (skill) {
            case 3_111_002:
            case 3_211_002:
            case 13_111_004:
            case 4_341_006:
            case 33_111_003:
                return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final boolean isGaviota() {
        return skill == 5_211_002;
    }

    /**
     *
     * @return
     */
    public final boolean isBeholder() {
        return skill == 1_321_007;
    }

    /**
     *
     * @return
     */
    public final boolean isMultiSummon() {
        return skill == 5_211_002 || skill == 5_211_001 || skill == 5_220_002 || skill == 32_111_006;
    }

    /**
     *
     * @return
     */
    public final boolean isSummon() {
        switch (skill) {
            case 12_111_004:
            case 1_321_007: //beholder
            case 2_311_006:
            case 2_321_003:
            case 2_121_005:
            case 2_221_005:
            case 5_211_001: // Pirate octopus summon
            case 5_211_002:
            case 5_220_002: // wrath of the octopi
            case 13_111_004:
            case 11_001_004:
            case 12_001_004:
            case 13_001_004:
            case 14_001_005:
            case 15_001_004:
                return true;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public final int getSkillLevel() {
        return skillLevel;
    }

    /**
     *
     * @return
     */
    public final int getSummonType() {
        if (isPuppet()) {
            return 0;
        }
        switch (skill) {
            case 1_321_007:
                return 2;
            case 35_111_001: //satellite.
            case 35_111_009:
            case 35_111_010:
                return 3;
            case 35_121_009: //bots n. tots
                return 4;
            //case 4111007: //TEMP
            //	return 6; //TEMP
        }
        return 1;
    }

    /**
     *
     * @return
     */
    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    /**
     *
     * @param chr
     * @param tickcount
     */
    public final void CheckSummonAttackFrequency(final MapleCharacter chr, final int tickcount) {
        final int tickdifference = (tickcount - lastSummonTickCount);
        if (tickdifference < GameConstants.getSummonAttackDelay(skill)) {
            chr.getCheatTracker().registerOffense(CheatingOffense.召唤兽快速攻击);
        }
        final long STime_TC = System.currentTimeMillis() - tickcount;
        final long S_C_Difference = Server_ClientSummonTickDiff - STime_TC;
        if (S_C_Difference > 200) {
            chr.getCheatTracker().registerOffense(CheatingOffense.召唤兽快速攻击);
        }
        Summon_tickResetCount++;
        if (Summon_tickResetCount > 4) {
            Summon_tickResetCount = 0;
            Server_ClientSummonTickDiff = STime_TC;
        }
        lastSummonTickCount = tickcount;
    }

    /**
     *
     * @return
     */
    public final boolean isChangedMap() {
        return changedMap;
    }

    /**
     *
     * @param cm
     */
    public final void setChangedMap(boolean cm) {
        this.changedMap = cm;
    }
}
