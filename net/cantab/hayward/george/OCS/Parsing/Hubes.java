/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import java.awt.event.InputEvent;
import javax.swing.KeyStroke;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.OcsCounter;

/**
 *
 * @author george
 */
public class Hubes extends ModuleSpecific {

    Hubes() {
        stepLossIncKey = KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK);
        maxHits = 3;
        levelIncKey = KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK);
        maxSupply = 20;
        supplyIncKey = KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK);
        supplyName = "SP";
        supplyTokenName = "Supply Token";
        maxTransport = 5;
        paxEqDifferPerSide = false;
        paxEqFlips = true;
        paxDefault = true;
        replacementName = "Repl Unit";
        flipRep = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        flipToReduced = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        unitNameFiller = " ";
        divNameFiller = " ";
        twoStageDivLookup = true;
        prefixDivToUnit = false;
        prefixFirstPartDivName = false;
        prefixEnd = " ";
        altFromDiv = true;
        prefixFill = ".";
        flipOrganicFullEmpty = false;
    }

    public void addTransport(String[] type, int size, boolean loaded, boolean isT,
                             PieceReader pr) {
        OcsCounter p;
        if (isT) {
            pr.input.writeError(true,
                                "Token transport in module without token pieces");
            return;
        }
        p = pr.match.findPiece(pr.curSide, Transport.class,
                                       new String[]{type[0]});
        if (p == null) {
            pr.input.writeError(true, "Unable to find transport " + type[0]);
            return;
        }
        for (; size > 1; size--) {
            p.keyEvent(this.levelIncKey);
        }
         pr.addPiece(p);
    }

    @Override
    boolean moduleSpecificLine (int side, String [] words, int repeat, PieceReader pr) {
        if (words.length == 3 && words[1].equalsIgnoreCase("hedgehog")
                && words[2].equalsIgnoreCase("points")&& pr.isNumber(words[0])) {
            pr.curBoard.addMajorNote("Place " + words[0] + " " + "Hedgehog Points");
            return true;
        }
        if (words.length == 3 && words[1].equalsIgnoreCase("gd")
                && words[2].equalsIgnoreCase("army") && pr.isNumber(words[0])) {
            return true;
        }
        if (words.length == 4 && words[1].equalsIgnoreCase("gd")
                && words[2].equalsIgnoreCase("tank")
                && words[3].equalsIgnoreCase("army") && pr.isNumber(words[0])) {
            return true;
        }
        if (words.length == 3 && words[1].equalsIgnoreCase("pz")
                && words[2].equalsIgnoreCase("corps")&& pr.isNumber(words[0])) {
            return true;
        }
        if (words.length == 2
                && words[1].equalsIgnoreCase("corps")&& pr.isNumber(words[0])) {
            return true;
        }
        if (words.length == 2 && words[0].equals("RR")
                && words[1].startsWith("unit")) {
            OcsCounter p = pr.match.findPiece(side, new String[] { "Railroad" });
            for (; repeat > 0; repeat--) {
                pr.addPiece(p);
            }
            return true;
        }
        if (words.length == 2 && words[0].equals("RR")
                && words[1].startsWith("Unit")) {
            OcsCounter p = pr.match.findPiece(side, new String[] { "2", "UF", "Railroad" });
            for (; repeat > 0; repeat--) {
                pr.addPiece(p);
            }
            return true;
        }
        if (words.length == 2 && words[0].equals("Pontoon")
                && words[1].startsWith("Unit")) {
            OcsCounter p = pr.match.findPiece(side, new String[] { "1", "UF", "Bridge"  });
            pr.addPiece(p);
            if (repeat > 1) {
                p = pr.match.findPiece(side, new String[] { "2", "UF", "Bridge"  });
                pr.addPiece(p);
            }
            return true;
        }
        return false;
    }
}
