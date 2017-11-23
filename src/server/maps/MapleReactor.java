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
package server.maps;

import client.MapleClient;
import java.awt.Rectangle;
import scripting.ReactorScriptManager;
import server.Timer.MapTimer;
import tools.MaplePacketCreator;
import tools.Pair;

/**
 *
 * @author zjj
 */
public class MapleReactor extends AbstractMapleMapObject {

    private int rid;
    private MapleReactorStats stats;
    private byte state;
    private int delay;
    private MapleMap map;
    private String name = "";
    private boolean timerActive, alive;

    /**
     *
     * @param stats
     * @param rid
     */
    public MapleReactor(MapleReactorStats stats, int rid) {
        this.stats = stats;
        this.rid = rid;
        alive = true;
    }

    /**
     *
     * @return
     */
    public final byte getFacingDirection() {
        return stats.getFacingDirection();
    }

    /**
     *
     * @param active
     */
    public void setTimerActive(boolean active) {
        this.timerActive = active;
    }

    /**
     *
     * @return
     */
    public boolean isTimerActive() {
        return timerActive;
    }

    /**
     *
     * @return
     */
    public int getReactorId() {
        return rid;
    }

    /**
     *
     * @param state
     */
    public void setState(byte state) {
        this.state = state;
    }

    /**
     *
     * @return
     */
    public byte getState() {
        return state;
    }

    /**
     *
     * @return
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     *
     * @param alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     *
     * @param delay
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     *
     * @return
     */
    public int getDelay() {
        return delay;
    }

    /**
     *
     * @return
     */
    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.REACTOR;
    }

    /**
     *
     * @return
     */
    public int getReactorType() {
        return stats.getType(state);
    }

    /**
     *
     * @return
     */
    public byte getTouch() {
        return this.stats.canTouch(this.state);
    }

    /**
     *
     * @param map
     */
    public void setMap(MapleMap map) {
        this.map = map;
    }

    /**
     *
     * @return
     */
    public MapleMap getMap() {
        return map;
    }

    /**
     *
     * @return
     */
    public Pair<Integer, Integer> getReactItem() {
        return stats.getReactItem(state);
    }

    /**
     *
     * @param client
     */
    @Override
    public void sendDestroyData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.destroyReactor(this));
    }

    /**
     *
     * @param client
     */
    @Override
    public void sendSpawnData(MapleClient client) {
        client.getSession().write(MaplePacketCreator.spawnReactor(this));
    }

    /**
     *
     * @param c
     */
    public void forceStartReactor(MapleClient c) {
        ReactorScriptManager.getInstance().act(c, this);
    }

    /**
     *
     * @param newState
     */
    public void forceHitReactor(final byte newState) {
        setState((byte) newState);
        setTimerActive(false);
        map.broadcastMessage(MaplePacketCreator.triggerReactor(this, (short) 0));
    }

    //hitReactor command for item-triggered reactors

    /**
     *
     * @param c
     */
    public void hitReactor(MapleClient c) {
        hitReactor(0, (short) 0, c);
    }

    /**
     *
     */
    public void forceTrigger() {
        map.broadcastMessage(MaplePacketCreator.triggerReactor(this, (short) 0));
    }

    /**
     *
     * @param delay
     */
    public void delayedDestroyReactor(long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                map.destroyReactor(getObjectId());
            }
        }, delay);
    }

    /**
     *
     * @param charPos
     * @param stance
     * @param c
     */
    public void hitReactor(int charPos, short stance, MapleClient c) {
        if (stats.getType(state) < 999 && stats.getType(state) != -1) {
            //type 2 = only hit from right (kerning swamp plants), 00 is air left 02 is ground left
            final byte oldState = state;
            boolean pass = false;
            if (getReactorId() == 1_072_000) {
                pass = true;
            } else {
                pass = (this.stats.getType(this.state) != 2);
            }
            if (pass || ((charPos != 0) && (charPos != 2))) {
                //  if (!(stats.getType(state) == 2 && (charPos == 0 || charPos == 2))) { // next state
                state = stats.getNextState(state);

                if (stats.getNextState(state) == -1 || stats.getType(state) == 999) { //end of reactor
                    if ((stats.getType(state) < 100 || stats.getType(state) == 999) && delay > 0) { //reactor broken
                        map.destroyReactor(getObjectId());
                    } else { //item-triggered on final step
                        map.broadcastMessage(MaplePacketCreator.triggerReactor(this, stance));
                    }
                    ReactorScriptManager.getInstance().act(c, this);
                } else { //reactor not broken yet
                    boolean done = false;
                    map.broadcastMessage(MaplePacketCreator.triggerReactor(this, stance)); //magatia is weird cause full beaker can be activated by gm hat o.o
                    if (state == stats.getNextState(state) || rid == 2_618_000 || rid == 2_309_000) { //current state = next state, looping reactor
                        if (this.rid > 200_011) {
                            ReactorScriptManager.getInstance().act(c, this);
                        }
                        done = true;
                    }
                    if (stats.getTimeOut(state) > 0) {
                        if ((!done) && (this.rid > 200_011)) {
                            ReactorScriptManager.getInstance().act(c, this);
                        }
                        scheduleSetState(state, oldState, stats.getTimeOut(state));
                    }
                }
            } else {
            }
            /*
             * } else { state++;
             * map.broadcastMessage(MaplePacketCreator.triggerReactor(this,
             * stance)); ReactorScriptManager.getInstance().act(c, this);
             */
        }
    }

    /**
     *
     * @return
     */
    public Rectangle getArea() {
        int height = stats.getBR().y - stats.getTL().y;
        int width = stats.getBR().x - stats.getTL().x;
        int origX = getPosition().x + stats.getTL().x;
        int origY = getPosition().y + stats.getTL().y;

        return new Rectangle(origX, origY, width, height);
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
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Reactor " + getObjectId() + " of id " + rid + " at position " + getPosition().toString() + " state" + state + " type " + stats.getType(state);
    }

    /**
     *
     * @param c
     * @param delay
     */
    public void delayedHitReactor(final MapleClient c, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                hitReactor(c);
            }
        }, delay);
    }

    /**
     *
     * @param oldState
     * @param newState
     * @param delay
     */
    public void scheduleSetState(final byte oldState, final byte newState, long delay) {
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (MapleReactor.this.state == oldState) {
                    forceHitReactor(newState);
                }
            }
        }, delay);
    }
}
