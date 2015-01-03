/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS.Parsing;

import java.awt.event.InputEvent;
import javax.swing.KeyStroke;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.Counters.GameMarker;
import net.cantab.hayward.george.OCS.Counters.Leader;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.OcsCounter;

/**
 *
 * @author george
 */
public class DAK extends ModuleSpecific {

    DAK() {
        stepLossIncKey = KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK);
        maxHits = 4;
        levelIncKey = KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK);
        maxSupply = 20;
        supplyIncKey = KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK);
        supplyName = "Supply";
        supplyTokenName = null;
        maxTransport = 5;
        paxEqDifferPerSide = false;
        paxEqFlips = false;
        flipToReduced = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        unitNameFiller = "";
        divNameFiller = "";
        twoStageDivLookup = true;
        prefixDivToUnit = false;
        postfixDivToUnit = true;
        postfixHQ = true;
        flipOrganic = flipToReduced;
        boardPrefix = "Board ";
    }

    @Override
    boolean moduleSpecificLine(int side, String[] words, int repeat, PieceReader pr) {
        if (words.length == 1 && words[0].equalsIgnoreCase("minefield")) {
            pr.addPiece(pr.match.findMarker("Mines"));
            return true;
        }
        if (words.length == 5 && words[0].equals("8") && words[1].equals("Arm")
                && words[2].equals("Dummy") && words[3].equals("Divisional")
                && words[4].equals("Marker")) {
            words = pr.top(words, 2);
            pr.addPiece(pr.match.findPiece(side, Division.class, words));
            return true;
        }
        if (words[words.length - 1].equalsIgnoreCase("training")) {
            pr.match.findPiece(side, Division.class, words); // automatoically added
            return true;
        }
        if (words[words.length - 1].equalsIgnoreCase("leader")) {
            words = pr.top(words, words.length - 1);
            if (words.length == 2 && words[1].equalsIgnoreCase("Connor")) {
                words = new String[]{"OConnor"};
            }
            pr.addPiece(pr.match.findPiece(side, Leader.class, words));
            return true;
        }
        if (words.length == 2 && words[0].equalsIgnoreCase("railhead")
                && words[1].equalsIgnoreCase("marker")) {
            pr.addPiece(pr.match.findMarker("Railhead", GameMarker.class));
            return true;
        }
        if (words.length == 2 && words[0].equalsIgnoreCase("greek")
                && words[1].equalsIgnoreCase("status")) {
            pr.addPiece(pr.match.findMarker("Greek Status"));
            return true;
        }
        if (words.length == 2 && words[0].equalsIgnoreCase("greek")
                && words[1].equalsIgnoreCase("progress")) {
            pr.addPiece(pr.match.findMarker("Greek Progress"));
            return true;
        }
        if (words.length > 4 && words[0].equalsIgnoreCase("KG")
                && words[1].equalsIgnoreCase("Marker")
                && words[2].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.remove(words, words.length - 1);
            words = pr.remove(words, 1);
            words = pr.remove(words, 1);
            pr.addPiece(pr.match.findPiece(side, Leader.class, words));
            return true;
        }
        if (words.length > 4 && words[0].equalsIgnoreCase("KG")
                && words[1].equalsIgnoreCase("Markers")
                && words[2].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.remove(words, words.length - 1);
            words = pr.remove(words, 1);
            words = pr.remove(words, 1);
            for (int i = 2; i < words.length; i++) {
                if (words[i].equals(",")) {
                    pr.addPiece(pr.match.findPiece(side, Leader.class, pr.top(words, i)));
                    for (int j = 1; j <= i; j++) {
                        words = pr.remove(words, 1);
                    }
                    i = 1;
                }
            }
            pr.addPiece(pr.match.findPiece(side, Leader.class, words));
            return true;
        }
        if (words.length > 2 && words[0].equalsIgnoreCase("KG")
                && words[words.length - 1].equalsIgnoreCase("Marker")) {
            words = pr.remove(words, words.length - 1);
            pr.addPiece(pr.match.findPiece(side, Leader.class, words));
            return true;
        }
        if (words.length > 4 && words[0].equalsIgnoreCase("Raggumento")
                && words[1].equalsIgnoreCase("Marker")
                && words[2].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.remove(words, words.length - 1);
            words = pr.remove(words, 1);
            words = pr.remove(words, 1);
            words[0] = "Ragg";
            pr.addPiece(pr.match.findPiece(side, Leader.class, words));
            return true;
        }
        if (words.length == 1 && words[0].equalsIgnoreCase("Rommel")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"Rommel"}));
            return true;
        }
        if (words.length == 3 && words[0].equalsIgnoreCase("Bergonzoli")
                && words[1].equalsIgnoreCase("Ragg")
                && words[2].equalsIgnoreCase("Marker")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggBergonzoli"}));
            return true;
        }
        if (words.length == 3 && words[0].equalsIgnoreCase("Bignami")
                && words[1].equalsIgnoreCase("Ragg")
                && words[2].equalsIgnoreCase("Marker")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggBignami"}));
            return true;
        }
        if (words.length == 3 && words[0].equalsIgnoreCase("RECAM")
                && words[1].equalsIgnoreCase("Ragg")
                && words[2].equalsIgnoreCase("Marker")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggRECAM"}));
            return true;
        }
        if (words.length == 3 && words[0].equalsIgnoreCase("Raggruppamento")
                && words[1].equalsIgnoreCase("Brigata")
                && words[2].equalsIgnoreCase("Corazzata")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggBabini"}));
            return true;
        }
        if (words.length == 7 && words[3].equals("(") && words[5].equals(")")
                && words[0].equalsIgnoreCase("Brigata")
                && words[1].equalsIgnoreCase("Corazzata")
                && words[2].equalsIgnoreCase("Speciale")
                && words[4].equalsIgnoreCase("Babini")
                && words[6].equalsIgnoreCase("Marker")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggBabini"}));
            return true;
        }
        if (words.length == 4 && words[1].equalsIgnoreCase("Brigata")
                && words[2].equalsIgnoreCase("Corazzata")
                && words[3].equalsIgnoreCase("Speciale")
                && words[0].equalsIgnoreCase("Raggruppamento")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggBabini"}));
            return true;
        }
        if (words.length == 4 && words[1].equalsIgnoreCase("Raggruppamento")
                && words[2].equalsIgnoreCase("Carri")
                && words[3].equalsIgnoreCase("Leggeri")
                && words[0].equals("1")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggAresca"}));
            return true;
        }
        if (words.length == 4 && words[1].equalsIgnoreCase("Raggruppamento")
                && words[2].equalsIgnoreCase("Carri")
                && words[3].equalsIgnoreCase("Leggeri")
                && words[0].equals("2")) {
            pr.addPiece(pr.match.findPiece(side, Leader.class, new String[]{"RaggTrivoli"}));
            return true;
        }
        if (words.length == 6 && words[0].equalsIgnoreCase("egyptian")
                && words[1].equalsIgnoreCase("national")
                && words[2].equalsIgnoreCase("army")
                && words[3].equalsIgnoreCase("organic")
                && words[4].equalsIgnoreCase("truck")) {
            boolean full = words[5].equals(ReadAndLogInput.LOADED);
            boolean empty = words[5].equals(ReadAndLogInput.EMPTY);
            if (!full && !empty) {
                full = true;
            }
            OcsCounter q = pr.match.findPiece(0, new String[]{"NatlArmyTruck"});
            if (q == null) {
                return true;
            }
            if (full) {
                if (flipOrganicFullEmpty) {
                    if (!fullDefault) {
                        q.keyEvent(flipOrganic);
                    }
                }
            } else {
                if (flipOrganicFullEmpty && fullDefault) {
                    q.keyEvent(flipOrganic);
                }
            }
            pr.addPiece(q);
            return true;
        }
        return false;
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

    @Override
    String finalName(int side, String name, String[] type) {
        if (type == null || type.length == 0) {
            return name;
        }
        if (side == 0) {
            if (type[type.length - 1].equalsIgnoreCase("red")) {
                return name + "Red";
            }
            if (type[type.length - 1].equalsIgnoreCase("yellow")) {
                return name + "Yellow";
            }
            if (type.length > 1 && type[type.length - 1].equalsIgnoreCase("color")
                    && type[type.length - 2].equalsIgnoreCase("no")) {
                return name;
            }
            for (int i = 0; i < type.length; i++) {
                if (type[i].equalsIgnoreCase("Coastal")) {
                    return name + "Coastal";
                }
            }
        } else if (side == 1) {
            for (int i = 0; i < type.length; i++) {
                if (type[i].equalsIgnoreCase("MG")) {
                    return name + "MG";
                }
                if (type[i].equalsIgnoreCase("IG")
                        || type[i].equalsIgnoreCase("Gun")) {
                    return name + "IG";
                }
                if (type[i].equalsIgnoreCase("GAF")) {
                    return name + "GAF";
                }
                if (type[i].equalsIgnoreCase("Arty")) {
                    return name + "Art";
                }
                if (type[i].equalsIgnoreCase("Para")
                        && !name.endsWith("Para")) {
                    return name + "Para";
                }
            }
        }
        return name;
    }

    @Override
    String finalUnitName(int side, String name, String dName) {
        if (name.equals("OrganicTruck") && dName.endsWith("Div")
                && dName.length() > 3) {
            return dName.substring(0, dName.length() - 3) + "Truck";
        }
        return name;
    }

    public void addTransport(String[] type, int size, boolean loaded, boolean isT,
            PieceReader pr) {
        if (type.length != 1) {
            pr.input.writeError(true, "Bad Transport Specification");
            return;
        }
        OcsCounter p;
        if (isT) {
            while (size > 3) {
                p = pr.match.findPiece(pr.curSide, Transport.class,
                        new String[]{(pr.curSide == 0 ? "3T " : "3T") + type[0]});
                if (!loaded) {
                    p.keyEvent(flipToReduced);
                }
                pr.addPiece(p);
                size -= 3;
            }
            switch (size) {
                case 1:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{(pr.curSide == 0 ? "1T " : "1T") + type[0]});
                    break;
                case 2:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{(pr.curSide == 0 ? "2T " : "2T") + type[0]});
                    break;
                case 3:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{(pr.curSide == 0 ? "3T " : "3T") + type[0]});
                    break;
                default:
                    return;
            }
        } else {
            switch (size) {
                case 1:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{(pr.curSide == 0 ? "1S " : "1S") + type[0]});
                    break;
                case 2:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{(pr.curSide == 0 ? "2S " : "2S") + type[0]});
                    break;
                case 3:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{(pr.curSide == 0 ? "3S " : "3S") + type[0]});
                    break;
                case 4:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{(pr.curSide == 0 ? "4S " : "4S") + type[0]});
                    break;
                case 5:
                    p = pr.match.findPiece(pr.curSide, Transport.class,
                            new String[]{"5S" + type[0]});
                    break;
                default:
                    return;
            }
        }
        if (!loaded) {
            p.keyEvent(flipToReduced);
        }
        pr.addPiece(p);
    }
}
