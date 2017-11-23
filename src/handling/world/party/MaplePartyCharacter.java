package handling.world.party;

import client.MapleCharacter;
import java.awt.*;
import java.io.Serializable;
import java.util.List;
import server.maps.MapleDoor;

/*
 * 队伍的成员信息设置
 */

/**
 *
 * @author zjj
 */

public class MaplePartyCharacter implements Serializable {

    private static final long serialVersionUID = 6_215_463_252_132_450_750L;
    private final String name;
    private int id;
    private int level;
    private int channel;
    private int jobid;
    private int mapid;
    private int doorTown = 999_999_999;
    private int doorTarget = 999_999_999;
    private int doorSkill = 0;
    private Point doorPosition = new Point(0, 0);
    private boolean online;

    /**
     *
     * @param maplechar
     */
    public MaplePartyCharacter(MapleCharacter maplechar) {
        this.name = maplechar.getName();
        this.level = maplechar.getLevel();
        this.channel = maplechar.getClient().getChannel();
        this.id = maplechar.getId();
        this.jobid = maplechar.getJob();
        this.mapid = maplechar.getMapId();
        this.online = true;

        List<MapleDoor> doors = maplechar.getDoors();
        if (doors.size() > 0) {
            MapleDoor door = doors.get(0);
            this.doorTown = door.getTown().getId();
            this.doorTarget = door.getTarget().getId();
            this.doorSkill = door.getSkill();
            this.doorPosition = door.getTargetPosition();
        } else {
            this.doorPosition = maplechar.getPosition();
        }
    }

    /**
     *
     */
    public MaplePartyCharacter() {
        this.name = "";
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
    public int getChannel() {
        return channel;
    }

    /**
     *
     * @return
     */
    public boolean isOnline() {
        return online;
    }

    /**
     *
     * @param online
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     *
     * @return
     */
    public int getMapid() {
        return mapid;
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
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public int getJobId() {
        return jobid;
    }

    /**
     *
     * @return
     */
    public int getDoorTown() {
        return doorTown;
    }

    /**
     *
     * @return
     */
    public int getDoorTarget() {
        return doorTarget;
    }

    /**
     *
     * @return
     */
    public int getDoorSkill() {
        return doorSkill;
    }

    /**
     *
     * @return
     */
    public Point getDoorPosition() {
        return doorPosition;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MaplePartyCharacter other = (MaplePartyCharacter) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
