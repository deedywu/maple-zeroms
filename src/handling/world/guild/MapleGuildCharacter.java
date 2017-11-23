
package handling.world.guild;

import client.MapleCharacter;

/**
 *
 * @author zjj
 */
public class MapleGuildCharacter implements java.io.Serializable { // alias for a character

    /**
     *
     */

    public static final long serialVersionUID = 2_058_609_046_116_597_760L;
    private byte channel = -1, guildrank, allianceRank;
    private short level;
    private int id, jobid, guildid;
    private boolean online;
    private String name;

    // either read from active character...
    // if it's online

    /**
     *
     * @param c
     */
    public MapleGuildCharacter(final MapleCharacter c) {
        name = c.getName();
        level = (short) c.getLevel();
        id = c.getId();
        channel = (byte) c.getClient().getChannel();
        jobid = c.getJob();
        guildrank = c.getGuildRank();
        guildid = c.getGuildId();
        allianceRank = c.getAllianceRank();
        online = true;
    }

    // or we could just read from the database

    /**
     *
     * @param id
     * @param lv
     * @param name
     * @param channel
     * @param job
     * @param rank
     * @param gid
     * @param allianceRank
     * @param on
     */
    public MapleGuildCharacter(final int id, final short lv, final String name, final byte channel, final int job, final byte rank, final int gid, final byte allianceRank, final boolean on) {
        this.level = lv;
        this.id = id;
        this.name = name;
        if (on) {
            this.channel = channel;
        }
        this.jobid = job;
        this.online = on;
        this.guildrank = rank;
        this.guildid = gid;
        this.allianceRank = allianceRank;
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
     * @param l
     */
    public void setLevel(short l) {
        level = l;
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
     * @param ch
     */
    public void setChannel(byte ch) {
        channel = ch;
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
    public int getJobId() {
        return jobid;
    }

    /**
     *
     * @param job
     */
    public void setJobId(int job) {
        jobid = job;
    }

    /**
     *
     * @return
     */
    public int getGuildId() {
        return guildid;
    }

    /**
     *
     * @param gid
     */
    public void setGuildId(int gid) {
        guildid = gid;
    }

    /**
     *
     * @param rank
     */
    public void setGuildRank(byte rank) {
        guildrank = rank;
    }

    /**
     *
     * @return
     */
    public byte getGuildRank() {
        return guildrank;
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
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param f
     */
    public void setOnline(boolean f) {
        online = f;
    }

    /**
     *
     * @param rank
     */
    public void setAllianceRank(byte rank) {
        allianceRank = rank;
    }

    /**
     *
     * @return
     */
    public byte getAllianceRank() {
        return allianceRank;
    }
}
