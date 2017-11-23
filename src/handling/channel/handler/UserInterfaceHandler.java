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
package handling.channel.handler;

import client.MapleCharacterUtil;
import client.MapleClient;
import constants.ServerConstants;
import scripting.EventManager;
import scripting.NPCScriptManager;
import tools.MaplePacketCreator;
import tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author zjj
 */
public class UserInterfaceHandler {

    /**
     *
     * @param c
     */
    public static final void CygnusSummon_NPCRequest(final MapleClient c) {
        if (c.getPlayer().getJob() == 2_000) {
            NPCScriptManager.getInstance().start(c, 1_202_000);
        } else if (c.getPlayer().getJob() == 1_000) {
            NPCScriptManager.getInstance().start(c, 1_101_008);
        }
    }

    /**
     *
     * @param slea
     * @param c
     */
    public static final void InGame_Poll(final SeekableLittleEndianAccessor slea, final MapleClient c) {
        if (ServerConstants.PollEnabled) {
            c.getPlayer().updateTick(slea.readInt());
            final int selection = slea.readInt();

            if (selection >= 0 && selection <= ServerConstants.Poll_Answers.length) {
                if (MapleCharacterUtil.SetPoll(c.getAccID(), selection)) {
                    c.getSession().write(MaplePacketCreator.getPollReply("Thank you."));
                    //idk what goes here lol
                }
            }
        }
    }

    /**
     *
     * @param mapid
     * @param c
     */
    public static final void ShipObjectRequest(final int mapid, final MapleClient c) {
        // BB 00 6C 24 05 06 00 - Ellinia
        // BB 00 6E 1C 4E 0E 00 - Leafre

        EventManager em;
        int effect = 3; // 1 = Coming, 3 = going, 1034 = balrog

        switch (mapid) {
            case 101_000_300: // Ellinia Station >> Orbis
            case 200_000_111: // Orbis Station >> Ellinia
                em = c.getChannelServer().getEventSM().getEventManager("Boats");
                if (em != null && em.getProperty("docked").equals("true")) {
                    effect = 1;
                }
                break;
            case 200_000_121: // Orbis Station >> Ludi
            case 220_000_110: // Ludi Station >> Orbis
                em = c.getChannelServer().getEventSM().getEventManager("Trains");
                if (em != null && em.getProperty("docked").equals("true")) {
                    effect = 1;
                }
                break;
            case 200_000_151: // Orbis Station >> Ariant
            case 260_000_100: // Ariant Station >> Orbis
                em = c.getChannelServer().getEventSM().getEventManager("Geenie");
                if (em != null && em.getProperty("docked").equals("true")) {
                    effect = 1;
                }
                break;
            case 240_000_110: // Leafre Station >> Orbis
            case 200_000_131: // Orbis Station >> Leafre
                em = c.getChannelServer().getEventSM().getEventManager("Flight");
                if (em != null && em.getProperty("docked").equals("true")) {
                    effect = 1;
                }
                break;
            case 200_090_010: // During the ride to Orbis
            case 200_090_000: // During the ride to Ellinia
                em = c.getChannelServer().getEventSM().getEventManager("Boats");
                if (em != null && em.getProperty("haveBalrog").equals("true")) {
                    effect = 1;
                } else {
                    return; // shyt, fixme!
                }
                break;
            default:
                System.out.println("Unhandled ship object, MapID : " + mapid);
                break;
        }
        c.getSession().write(MaplePacketCreator.boatPacket(effect));
    }
}
