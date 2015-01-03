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
import net.cantab.hayward.george.OCS.Counters.Airbase;
import net.cantab.hayward.george.OCS.Counters.HeadQuarters;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.OcsCounter;

/**
 *
 * @author George Hayward
 */
public class BlitzkriegLegend extends ModuleSpecific {

    KeyStroke transportIncKey;

    BlitzkriegLegend() {
        stepLossIncKey = KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK);
        maxHits = 3;
        levelIncKey = stepLossIncKey;
        maxSupply = 5;
        supplyIncKey = stepLossIncKey;
        supplyName = "SP";
        supplyTokenName = null;
        maxTransport = 5;
        transportIncKey = KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK);
        paxEqDifferPerSide = false;
        paxEqFlips = false;
        flipToReduced = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        unitNameFiller = " ";
        divNameFiller = " ";
        twoStageDivLookup = true;
        prefixDivToUnit = false;
        flipOrganic = flipToReduced;
    }

    @Override
    public void addTransport(String[] type, int size, boolean loaded, boolean isT,
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

    @Override
    boolean moduleSpecificLine(int side, String[] words, int repeat, PieceReader pr) {
        if (words.length == 3 && words[0].equals("Strat") && words[1].equals("Mode") && words[2].equals("Marker")) {
            for (int i = 0; i < repeat; i++) {
                pr.addPiece(pr.match.findMarker("French Strat Move"));
            }
            return true;
        }
        if (words.length == 5 && words[0].equals("Strat") && words[1].equals("Mode") && words[2].equals("(")
            && words[3].equals("optional") && words[4].equals(")")) {
            for (int i = 0; i < repeat; i++) {
                pr.addPiece(pr.match.findMarker("French Strat Move"));
            }
            return true;
        }
        if (words.length == 5 && words[0].equals("Kleist") && words[1].equals("Truck") && words[2].equals("(")
            && words[4].equals(")")) {
            boolean flip = !words[3].equals("full");
            for (int i = 0; i < repeat; i++) {
                OcsCounter q = pr.match.findPiece(side, Transport.class, new String[]{"Kleist", "Truck", "Ge"});
                if (flip) {
                    q.keyEvent(flipOrganic);
                }
                pr.addPiece(q);
            }
            return true;
        }
        if (words.length == 3 && words[0].equals("LW") && words[1].equals("Surge") && words[2].equals("Marker")) {
            pr.addPiece(pr.match.findMarker("LW Surge Marker"));
            return true;
        }
        if (words.length == 3 && words[0].equals("Reserve") && words[1].equals("Marker") && words[2].equals("Fr")) {
            for (int i = 0; i < repeat; i++) {
                pr.addPiece(pr.match.findPiece(side, new String[]{"Reserve", "Fr"}));
            }
            return true;
        }
        if (words.length == 3 && words[0].equals("Reserve") && words[1].equals("Marker") && words[2].equals("Du")) {
            for (int i = 0; i < repeat; i++) {
                pr.addPiece(pr.match.findPiece(side, new String[]{"Reserve", "Du"}));
            }
            return true;
        }
        if (words.length == 3 && words[0].equals("Reserve") && words[1].equals("Marker") && words[2].equals("Ge")) {
            for (int i = 0; i < repeat; i++) {
                pr.addPiece(pr.match.findPiece(side, new String[]{"Reserve", "Ge"}));
            }
            return true;
        }
        if (words.length == 2 && words[0].equals("Sturmgruppe")) {
            pr.addPiece(pr.match.findMarker(words[1]));
            return true;
        }
        if (words.length == 2 && words[1].equals("Brkdwn")) {
            for (int i = 0; i < repeat; i++) {
                pr.addPiece(pr.match.findPiece(side, words[0].substring(1), new String[] { "Breakdown", words[0].substring(1), nation}));
            }
            return true;
        }
        if (words.length == 3 && words[1].equals("Brkdwn") && words[2].equals("Rgt")) {
            for (int i = 0; i < repeat; i++) {
                pr.addPiece(pr.match.findPiece(side, words[0].substring(1), new String[] { "Breakdown", words[0].substring(1), nation}));
            }
            return true;
        }
        if (words.length == 2 && words[1].equals("Setup")) {
            if (words[0].equals("BEF")) {
                nation = "Br";
            } else {
                nation = words[0].substring(0, 2);
            }
            return true;
        }
        if (words.length == 3 && words[0].equals("air") && words[1].equals("strip")) {
            pr.addPiece(pr.match.findMarker(words[2] + " Corps Strip", Airbase.class));
            return true;
        }
        if (side == 1 && words.length ==3 && words[2].equals("HQ") && words[1].equals("Corps")) {
            pr.addPiece(pr.match.findPiece(side, HeadQuarters.class, new String[]{ words[0], words[1], nation}));
            return true;
        }
        return false;
    }
    
    String nation = "Un";
}
