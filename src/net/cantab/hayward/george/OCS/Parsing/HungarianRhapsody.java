/* 
 * $Id$
 *
 * Copyright (c) 2000-2011 by Rodney Kinney, Joel Uckelman, George Hayward
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */

package net.cantab.hayward.george.OCS.Parsing;

import java.awt.event.InputEvent;
import javax.swing.KeyStroke;
import net.cantab.hayward.george.OCS.OcsCounter;

/**
 *
 * @author George Hayward
 */
public class HungarianRhapsody extends ModuleSpecific {

    KeyStroke transportIncKey;

    HungarianRhapsody() {
        stepLossIncKey = KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK);
        maxHits = 3;
        levelIncKey = stepLossIncKey;
        maxSupply = 5;
        supplyIncKey = stepLossIncKey;
        supplyName = "SP";
        supplyTokenName = null;
        maxTransport = 5;
        transportIncKey = KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK);
        paxEqDifferPerSide = false;
        paxEqFlips = false;
        flipToReduced = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        unitNameFiller = " ";
        divNameFiller = " ";
        twoStageDivLookup = true;
        prefixDivToUnit = false;
        flipOrganic = flipToReduced;
    }

    public void addTransport (String[] type, int size, boolean loaded, boolean isT,
            PieceReader pr) {
        OcsCounter p;
        if (isT) {
            pr.input.writeError(true, "Token transport in module without token pieces");
            return;
        }
        p = pr.match.findPiece(pr.curSide, type);
        if (p == null) {
            return;
        }
        for (int i = 1; i < size; i++) {
            p.keyEvent(transportIncKey);
        }
        pr.addPiece(p);
        if (loaded) {
            pr.addSupply(size, isT);
        }
        return;
    }

        /**
     * Process module specific command
     */
    @Override
    void moduleCommandLine(int side, String[] words, PieceReader pr) {
        if (words.length == 1 && words[0].equals("divoff")) {
            pr.match.noDivCounters = true;
        } else if (words.length == 1 && words[0].equals("divon")) {
            pr.match.noDivCounters = false;
        } else {
            pr.input.writeError(true, "Invalid module specific command ignored");
        }
        return;
    }


}
