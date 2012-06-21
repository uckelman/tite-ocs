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
public class Tunisia extends ModuleSpecific {

    Tunisia() {
        stepLossIncKey = KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK);
        maxHits = 0;
        levelIncKey = KeyStroke.getKeyStroke(']', InputEvent.CTRL_DOWN_MASK);
        maxSupply = 20;
        supplyIncKey = KeyStroke.getKeyStroke(']', InputEvent.CTRL_DOWN_MASK);
        supplyName = "Supply";
        supplyTokenName = null;
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
        prefixFirstPartDivName = true;
        prefixEnd = " ";
        altFromDiv = true;
        prefixFill = ".";
        flipOrganicFullEmpty = false;
    }

    @Override
    String finalName(int side, String name, String[] type) {
        if (name.charAt(0) == '#') {
            return name.substring(1);
        }
        return name;
    }


    public void addTransport(String[] type, int size, boolean loaded, boolean isT,
                             PieceReader pr) {
        OcsCounter p;
        if (isT) {
            pr.input.writeError(true,
                                "Token transport in module without token pieces");
            return;
        }
        if (type.length != 1) {
            if (type.length != 2 || !type[0].equalsIgnoreCase("charcoal")) {
                pr.input.writeError(true, "Bad Transport Specification");
                return;
            }
            switch (size) {
            case 1:
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.theSide = pr.curSide;
                pr.addPiece(p);
                break;
            case 2:
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.theSide = pr.curSide;
                p.keyEvent(flipRep);
                pr.addPiece(p);
                break;
            case 3:
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.theSide = pr.curSide;
                p.keyEvent(flipRep);
                pr.addPiece(p);
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                p.theSide = pr.curSide;
                pr.addPiece(p);
                break;
            case 4:
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.theSide = pr.curSide;
                p.keyEvent(flipRep);
                pr.addPiece(p);
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.theSide = pr.curSide;
                p.keyEvent(flipRep);
                pr.addPiece(p);
                break;
            case 5:
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.keyEvent(flipRep);
                p.theSide = pr.curSide;
                pr.addPiece(p);
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.theSide = pr.curSide;
                p.keyEvent(flipRep);
                pr.addPiece(p);
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});;
                p.theSide = pr.curSide;
                pr.addPiece(p);
                break;
            case 6:
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.keyEvent(flipRep);
                pr.addPiece(p);
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.keyEvent(flipRep);
                pr.addPiece(p);
                p = pr.match.findPiece(1, Transport.class, new String[]{"Fr Truck"});
                if (p == null) {
                    return;
                }
                p.keyEvent(flipRep);
                pr.addPiece(p);
                break;
            default:
                break;
            }
        } else {
            switch (size) {
            case 1:
                p = pr.match.findPiece(pr.curSide, Transport.class,
                                       new String[]{type[0] + " 12"});
                if (p == null) {
                    return;
                }
                if (pr.curSide == 1) p.keyEvent(flipRep);
                break;
            case 2:
                p = pr.match.findPiece(pr.curSide, Transport.class,
                                       new String[]{type[0] + " 12"});
                if (p == null) {
                    return;
                }
                if (pr.curSide == 0) p.keyEvent(flipRep);
                break;
            case 3:
                p = pr.match.findPiece(pr.curSide, Transport.class,
                                       new String[]{type[0] + " 34"});
                if (p == null) {
                    return;
                }
                p.keyEvent(flipRep);
                break;
            case 4:
                p = pr.match.findPiece(pr.curSide, Transport.class,
                                       new String[]{type[0] + " 34"});
                if (p == null) {
                    return;
                }
                break;
            case 5:
                p = pr.match.findPiece(pr.curSide, Transport.class,
                                       new String[]{type[0] + " 5Ext"});
                if (p == null) {
                    return;
                }
                break;
            case 6:
                p = pr.match.findPiece(pr.curSide, Transport.class,
                                       new String[]{type[0] + " 5Ext"});
                if (p == null) {
                    return;
                }
                p.keyEvent(flipRep);
                break;
            default:
                return;
            }
            pr.addPiece(p);
        }
    }
}
