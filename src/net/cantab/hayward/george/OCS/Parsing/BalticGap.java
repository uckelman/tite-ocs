/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import java.awt.event.InputEvent;
import javax.swing.KeyStroke;
import net.cantab.hayward.george.OCS.OcsCounter;

/**
 *
 * @author george
 */
public class BalticGap extends ModuleSpecific {

    KeyStroke transportIncKey;

    BalticGap() {
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

}
