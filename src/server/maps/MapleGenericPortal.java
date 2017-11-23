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
import client.anticheat.CheatingOffense;
import handling.channel.ChannelServer;
import java.awt.Point;
import scripting.PortalScriptManager;
import server.MaplePortal;
import tools.MaplePacketCreator;

/**
 *
 * @author zjj
 */
public class MapleGenericPortal implements MaplePortal {

    private String name, target, scriptName;
    private Point position;
    private int targetmap, type, id;
    private boolean portalState = true;

    /**
     *
     * @param type
     */
    public MapleGenericPortal(final int type) {
        this.type = type;
    }

    /**
     *
     * @return
     */
    @Override
    public final int getId() {
        return id;
    }

    /**
     *
     * @param id
     */
    public final void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     */
    @Override
    public final String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    @Override
    public final Point getPosition() {
        return position;
    }

    /**
     *
     * @return
     */
    @Override
    public final String getTarget() {
        return target;
    }

    /**
     *
     * @return
     */
    @Override
    public final int getTargetMapId() {
        return targetmap;
    }

    /**
     *
     * @return
     */
    @Override
    public final int getType() {
        return type;
    }

    /**
     *
     * @return
     */
    @Override
    public final String getScriptName() {
        return scriptName;
    }

    /**
     *
     * @param name
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @param position
     */
    public final void setPosition(final Point position) {
        this.position = position;
    }

    /**
     *
     * @param target
     */
    public final void setTarget(final String target) {
        this.target = target;
    }

    /**
     *
     * @param targetmapid
     */
    public final void setTargetMapId(final int targetmapid) {
        this.targetmap = targetmapid;
    }

    /**
     *
     * @param scriptName
     */
    @Override
    public final void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }

    /**
     *
     * @param c
     */
    @Override
    public final void enterPortal(final MapleClient c) {
        if (getPosition().distanceSq(c.getPlayer().getPosition()) > 22_500) {
            c.getPlayer().getCheatTracker().registerOffense(CheatingOffense.使用过远传送点);
        }
        final MapleMap currentmap = c.getPlayer().getMap();
        if (portalState || c.getPlayer().isGM()) {
            if (getScriptName() != null) {
                c.getPlayer().checkFollow();
                try {
                    PortalScriptManager.getInstance().executePortalScript(this, c);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else if (getTargetMapId() != 999_999_999) {
                final MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(getTargetMapId());
                if (!c.getPlayer().isGM()) {
                    if (to == null) {
                        c.getPlayer().dropMessage(5, "本地图目前尚未开放.");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    if (to.getLevelLimit() > 0 && to.getLevelLimit() > c.getPlayer().getLevel()) {
                        c.getPlayer().dropMessage(5, "You are too low of a level to enter this place.");
                        c.getSession().write(MaplePacketCreator.enableActions());
                        return;
                    }
                    //if (to.getForceMove() > 0 && to.getForceMove() < c.getPlayer().getLevel()) {
                    //    c.getPlayer().dropMessage(5, "You are too high of a level to enter this place.");
                    //    c.getSession().write(MaplePacketCreator.enableActions());
                    //    return;
                    //}
                } else if (to == null) {
                    c.getPlayer().dropMessage(5, "本地图目前尚未开放.");
                    c.getSession().write(MaplePacketCreator.enableActions());
                    return;
                }
                c.getPlayer().changeMapPortal(to, to.getPortal(getTarget()) == null ? to.getPortal(0) : to.getPortal(getTarget())); //late resolving makes this harder but prevents us from loading the whole world at once
            }
        }
        if (c != null && c.getPlayer() != null && c.getPlayer().getMap() == currentmap) { // Character is still on the same map.
            c.getSession().write(MaplePacketCreator.enableActions());
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean getPortalState() {
        return portalState;
    }

    /**
     *
     * @param ps
     */
    @Override
    public void setPortalState(boolean ps) {
        this.portalState = ps;
    }
}
