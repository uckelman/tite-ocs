/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS.Parsing;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.regex.Matcher;
import javax.swing.KeyStroke;
import net.cantab.hayward.george.OCS.Counters.Aircraft;
import net.cantab.hayward.george.OCS.Counters.AttackCapable;
import net.cantab.hayward.george.OCS.Counters.HeadQuarters;
import net.cantab.hayward.george.OCS.Counters.Reserve;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.OcsCounter;

/**
 *
 * @author george
 */
public class CaseBlue extends ModuleSpecific {

    CaseBlue() {
        stepLossIncKey = KeyStroke.getKeyStroke(']', InputEvent.CTRL_DOWN_MASK);
        maxHits = 3;
        levelIncKey = KeyStroke.getKeyStroke(']', InputEvent.CTRL_DOWN_MASK);
        maxSupply = 20;
        supplyIncKey = KeyStroke.getKeyStroke(']', InputEvent.CTRL_DOWN_MASK);
        supplyName = "Supply";
        supplyTokenName = null;
        maxTransport = 5;
        paxEqDifferPerSide = false;
        paxEqFlips = true;
        paxDefault = false;
        replacementName = "Generic Repl";
        flipRep = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        flipToReduced = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        unitNameFiller = " ";
        divNameFiller = " ";
        twoStageDivLookup = true;
        prefixDivToUnit = false;
        prefixFirstPartDivName = true;
        prefixEnd = " ";
        flipOrganic = flipToReduced;
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
            pr.input.writeError(true, "Bad Transport Specification");
            return;
        }
        switch (size) {
        case 1:
            p = pr.match.findPiece(pr.curSide, Transport.class,
                                   new String[]{type[0] + " 1"});
            if (p == null) {
                return;
            }
            p.keyEvent(flipRep);
            break;
        case 2:
            p = pr.match.findPiece(pr.curSide, Transport.class,
                                   new String[]{type[0] + " 1"});
            if (p == null) {
                return;
            }
            break;
        case 3:
            p = pr.match.findPiece(pr.curSide, Transport.class,
                                   new String[]{type[0] + " 3"});
            if (p == null) {
                return;
            }
            p.keyEvent(flipRep);
            break;
        case 4:
            p = pr.match.findPiece(pr.curSide, Transport.class,
                                   new String[]{type[0] + " 3"});
            if (p == null) {
                return;
            }
            break;
        case 5:
            p = pr.match.findPiece(pr.curSide, Transport.class,
                                   new String[]{type[0] + " 5"});
            if (p == null) {
                return;
            }
            break;
        case 6:
            p = pr.match.findPiece(pr.curSide, Transport.class,
                                   new String[]{type[0] + " 5"});
            if (p == null) {
                return;
            }
            p.keyEvent(flipRep);
            break;
        default:
            return;
        }
        pr.addPiece(p);
        if (loaded) {
            pr.addSupply(size, isT);
        }
    }

    @Override
    String [] convertSimple(int side, String factors, String[] id) {
        if (side == 1 && factors.equals("26-1-1")) {
            if (id.length == 2 && id[0].equalsIgnoreCase("arty")
                    &&id[1].equalsIgnoreCase("bde")) {
                return new String [] {"26-1-1"};
            }
        }
        if (side == 1 && factors.equals("75-1-0")) {
            if (id.length == 2 && id[0].equalsIgnoreCase("katy")
                    &&id[1].equalsIgnoreCase("bde")) {
                return new String [] {"75-1-0"};
            }
        }
        if (side == 1 && factors.equals("3-2-2")) {
            if (id.length == 2 && id[0].equalsIgnoreCase("breakdown")
                    &&id[1].equalsIgnoreCase("rgt")) {
                return new String [] {"Breakdown", "3-2-2"};
            }
        }
        if (side == 1 && factors.equals("3-3-3")) {
            if (id.length == 2 && id[0].equalsIgnoreCase("breakdown")
                    &&id[1].equalsIgnoreCase("rgt")) {
                return new String [] {"Breakdown", "3-3-3"};
            }
        }
        if (side == 1 && factors.equals("3-4-3")) {
            if (id.length == 2 && id[0].equalsIgnoreCase("breakdown")
                    &&id[1].equalsIgnoreCase("rgt")) {
                return new String [] {"Breakdown", "3-4-3"};
            }
        }
        if (side == 0 && factors.equals("3-2-2")) {
            if (id.length == 2 && id[0].equalsIgnoreCase("breakdown")
                    &&id[1].equalsIgnoreCase("rgt")) {
                return new String [] {"Breakdown", "3-2-2"};
            }
        }
        if (side == 0 && factors.equals("4-3-3")) {
            if ((id.length == 2 && id[0].equalsIgnoreCase("breakdown")
                    &&id[1].equalsIgnoreCase("rgt"))
                    || (id.length == 1 && id[0].equalsIgnoreCase("breakdown"))) {
                return new String [] {"Breakdown", "4-3-3"};
            }
        }
        if (side == 0 && factors.equals("4-4-3")) {
            if (id.length == 2 && id[0].equalsIgnoreCase("breakdown")
                    &&id[1].equalsIgnoreCase("rgt")) {
                return new String [] {"Breakdown", "4-4-3"};
            }
        }
        if (side == 0 && factors.equals("4-5-3")) {
            if ((id.length == 2 && id[0].equalsIgnoreCase("breakdown")
                    &&id[1].equalsIgnoreCase("rgt"))
                    || (id.length == 1 && id[0].equalsIgnoreCase("breakdown"))) {
                return new String [] {"Breakdown", "4-5-3"};
            }
        }
        return id;
    }

        /**
     * Process module specific command
     */
    @Override
    void moduleCommandLine(int side, String[] words, PieceReader pr) {
        if (words.length == 2 && words[1].equalsIgnoreCase("maps")) {
            if (words[0].equalsIgnoreCase("gbii")) {
                pr.data.zonePrefix = "GBII ";
            } else {
                pr.data.zonePrefix = "";
            }
        }
        return;
    }

    @Override
    boolean moduleSpecificLine (int side, String [] words, int repeat, PieceReader pr) {
        if (words.length == 6 && words[0].equals("(") && words[5].equals(")")
                && words[1].equals("MMD") && words[2].equals("Air")
                && words[3].equals("Units") && words[4].equals("only")) {
            MMDonlyAir = true;
            return true;
        }
        if (words.length == 4 && words[0].equals("Out")
                && words[1].equals("of") && words[2].equals("Supply")
                && words[3].equals("Marker")) {
            pr.addPiece(pr.match.findMarker("Out Of Supply"));
            return true;
        }
        if (words.length == 2 && words[0].equals("Exhausted")
                && words[1].equals("Internals")) {
            OcsCounter q = pr.match.findMarker("Stocks");
            q.keyEvent(levelIncKey);
            pr.addPiece(q);
            return true;
        }
        if (words.length == 3 && words[0].equals("AGS")
                && words[1].equals("Progress")
                && words[2].equals("Marker")) {
            pr.addPiece(pr.match.findMarker("AGS Progress"));
            return true;
        }
        if (words.length == 1 && words[0].equals("Stalin")) {
            pr.addPiece(pr.match.findPiece(side, HeadQuarters.class, words));
            return true;
        }
        if (words.length > 2 && words[words.length-1].equals("Co")
                && words[words.length - 2].equals("Commando")
                && side == 0) {
            pr.addPiece(pr.match.findPiece(side, AttackCapable.class, PieceReader.top(words, words.length - 2)));
            return true;
        }
        if (words.length == 2 && words[0].equals("Reserve")
                && words[1].startsWith("Marker")) {
            OcsCounter p = pr.match.findPiece(side, Reserve.class, new String[] {"Reserve"});
            for (; repeat > 0; repeat--) pr.addPiece(p);
            return true;
        }
        if (words.length == 5 && words[0].equals("#1-2-3")
                && words[1].equals("Separate") && words[2].equals("Ski")
                && words[3].equals("Battalions") && words[4].equals("Available")) {
            OcsCounter p = pr.match.findPiece(side, "1-2-3", new String[] {"Sep", "Ski"});
            for (; repeat > 0; repeat--) pr.addPiece(p);
            return true;
        }
        if (words.length == 9 && words[0].equals("#3-2-3")
                && words[2].equals("Bde") && words[1].equals("Ski")
                && words[3].equals("(") && words[4].equals("Sep") && words[5].equals(")")
                && words[8].equals("Building") && words[6].equals("Available")
                &&words[7].equals("for")) {
            OcsCounter p = pr.match.findPiece(side, "3-2-3", new String[] {"Sep", "Ski"});
            for (; repeat > 0; repeat--) pr.addPiece(p);
            return true;
        }
        if (words.length == 4 && (words[0].equals("459") || words[0].equals("688") || words[0].equals("Dora"))
                && words[1].equals("RR")
                && words[2].equals("Gun") && words[3].equals("Bn")) {
            pr.addPiece(pr.match.findPiece(side, words));
            return true;
        }
        if (words.length == 4 && words[0].equals("He.111zb") && words[1].equals("V")
                && words[2].equals("w/") && words[3].equals("Glider")) {
            pr.addPiece(pr.match.findPiece(side, Aircraft.class, new String[]{"He.111zbV+Glider"}));
            return true;
        }
        if (words.length == 3 && words[0].equals("He.111zvb")
                && words[1].equals("w/") && words[2].equals("Glider")) {
            pr.addPiece(pr.match.findPiece(side, Aircraft.class, new String[]{"He.111zbV+Glider"}));
            return true;
        }
        if (words.length == 2 && words[0].equals("Do.17w/") && words[1].equals("Glider")) {
            pr.addPiece(pr.match.findPiece(side, Aircraft.class, new String[]{"Do.17z+Glider"}));
            return true;
        }
        if (words.length == 3 && words[0].equals("He.46") && words[2].equals("Glider")
                && words[1].equals("w/")) {
            pr.addPiece(pr.match.findPiece(side, Aircraft.class, new String[]{"He.46+Glider"}));
            return true;
        }
        if (words.length == 3 && words[0].equals("He.111z") && words[2].equals("Glider")
                && words[1].equals("w/")) {
            pr.addPiece(pr.match.findPiece(side, Aircraft.class, new String[]{"He.111z+Glider"}));
            return true;
        }
        if (words.length == 2 && words[0].equals("Terek") && words[1].equals("Camels")) {
            pr.addPiece(pr.match.findPiece(side, Transport.class, words));
            return true;
        }
        if (words.length == 3 && words[0].equals("Terek") && words[1].equals("Camels")
                &&words[2].equals(" empty")) {
            OcsCounter q = pr.match.findPiece(side, Transport.class, words);
            q.keyEvent(flipRep);
            pr.addPiece(q);
            return true;
        }
        if (words.length == 3 && words[0].equals("Terek") && words[1].equals("Camels")
                &&words[2].equals(" loaded")) {
            OcsCounter q = pr.match.findPiece(side, Transport.class, words);
            pr.addPiece(q);
            return true;
        }
        if (words.length == 3 && words[1].equals("Turk")
                &&words[2].equals("Porters")) {
            pr.addPiece(pr.match.findPiece(side, Transport.class, new String[]{"Turk -", words[0]}));
            return true;
        }
        if (repeat == 2 && words.length > 1 && words[0].equals("Porter")) {
            pr.addPiece(pr.match.findPiece(side, Transport.class, new String[]{"Turk - 1000"}));
            pr.addPiece(pr.match.findPiece(side, Transport.class, new String[]{"Turk - 1001"}));
            return true;
        }
        return false;
    }

    boolean MMDonlyAir = false;

    @Override
    String finalName(int side, String name, String [] type) {
        if ("7 Est".equals(name) || "249 Est".equals(name)
                || "201 Lat".equals(name))
            return name;
        if ("Irrg Bn".equals(name)) return "Partisan";
        if (side == 1 && type == null && MMDonlyAir)
            return name + " MMD";
        if (type != null && type.length > 0 && type[0].equals("Cossack")) {
            return "Cossack - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Turk")) {
            return "Turk - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Don")) {
            return "Don - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Kuban")) {
            return "Kuban - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Azerb")) {
            return "Azerb - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Georg")) {
            return "Georg - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Kalmuck")) {
            return "Kalmuck - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Russian")) {
            return "Russian - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Armen")) {
            return "Armen - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Ukrainian")) {
            return "Ukraine - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("N.Cauc")) {
            return "N.Cauc - " + name;
        }
        if (type != null && type.length > 1 && type[0].equals("N.") && type[1].equals("Cauc")) {
            return "N.Cauc - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Lithuanian")) {
            return "Lithuanian - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Estonian")) {
            return "Estonian - " + name;
        }
        if (type != null && type.length > 0 && type[0].equals("Latvian")) {
            return "Latvian - " + name;
        }
        return name;
    }

    @Override
    void piecesPlaced() {
        MMDonlyAir = false;
    }


    @Override
    boolean moduleSpecificLine (int side, StringBuffer b, SetupReader pr) {
        if (isModuleSpecificLine(side, b, pr)) {
            if (pr.curBoard == null) {
                pr.input.writeError(true, "Unable to use board - not known");
                return true;
            }
            int count;
            switch (lineType) {
                case Mark_Avail:
                    if (specWords[4].equalsIgnoreCase("none")) {
                        pr.curBoard.addMajorNote(b.toString());
                        return true;
                    }
                    if (!pr.isNumber(specWords[4])) {
                        pr.input.writeError(true, "Bad reserve marker specification");
                        return true;
                    }
                    count = Integer.parseInt(specWords[4]);
                    if (count == 0) {
                        pr.curBoard.addMajorNote(b.toString());
                    } else {
                        pr.curMap = pr.setupMap;
                        if (pr.curMap != null) {
                            pr.curPoint = pr.curBoard.addLocation(pr.merge(SetupReader.top(specWords, 4), " "));
                        }
                        for (; count > 0; count--) {
                            pr.addPiece(pr.match.findPiece(side, new String[] {"Reserve"}));
                        }
                        pr.placePiecesOnMap();
                    }
                    break;
                case Mark_Avail_Short:
                    if (specWords[3].equalsIgnoreCase("none")) {
                        pr.curBoard.addMajorNote(b.toString());
                        return true;
                    }
                    if (!pr.isNumber(specWords[3])) {
                        pr.input.writeError(true, "Bad reserve marker specification");
                        return true;
                    }
                    count = Integer.parseInt(specWords[3]);
                    if (count == 0) {
                        pr.curBoard.addMajorNote(b.toString());
                    } else {
                        pr.curMap = pr.setupMap;
                        if (pr.curMap != null) {
                            pr.curPoint = pr.curBoard.addLocation(pr.merge(SetupReader.top(specWords, 3), " "));
                        }
                        for (; count > 0; count--) {
                            pr.addPiece(pr.match.findPiece(side, new String[] {"Reserve"}));
                        }
                        pr.placePiecesOnMap();
                    }
                    break;
                case Mass_Asst:
                case Dead_None:
                case Sea_Asst:
                case Partisan_Attacks:
                case Info:
                    pr.curBoard.addMajorNote(b.toString());
                    break;
                case Long_Info:
                    do {
                        pr.curBoard.addMajorNote(b.toString());
                        b = pr.input.nextLine();
                    } while (!isModuleSpecificLine(side, b, pr));
                    pr.input.repeatThisLine();
                    break;
                case Tree_Bark:
                    if (specWords[4].equalsIgnoreCase("none")) {
                        pr.curBoard.addMajorNote(b.toString());
                        return true;
                    }
                    if (!pr.isNumber(specWords[4])) {
                        pr.input.writeError(true, "Bad tree-bark soup marker specification");
                        return true;
                    }
                    count = Integer.parseInt(specWords[4]);
                    if (count == 0) {
                        pr.curBoard.addMajorNote(b.toString());
                    } else {
                        pr.curMap = pr.setupMap;
                        if (pr.curMap != null) {
                            pr.curPoint = pr.curBoard.addLocation(pr.merge(SetupReader.top(specWords, 4), " "));
                        }
                        for (; count > 0; count--) {
                            pr.addPiece(pr.match.findMarker("Tree Bark Soup"));
                        }
                        pr.placePiecesOnMap();
                    }
                    break;
                case Dead:
                    if (pr.resolveBoxLocation("Dead Pile", new Point(31, 31))) {
                        return true;
                    }
                    pr.match.noDivCounters = true;
                    for (;;) {
                        StringBuffer curLine;
                        curLine = pr.input.nextLine();
                        if (curLine == null) break;
                        if (curLine.charAt(0) == '*') break;
                        if(curLine.charAt(0) == '!'
                                || curLine.charAt(0) == '?'
                                || curLine.charAt(curLine.length() - 1) == ':')
                            break;
                        if (this.isModuleSpecificLine(side, curLine, pr)) break;
                        pr.parsePieces(curLine);
                        pr.placePiecesOnMap();
                        pr.curPoint = new Point(pr.curPoint);
                        pr.curPoint.x += 62;
                        if (pr.curPoint.x > 372) {
                            pr.curPoint.x = 31;
                            pr.curPoint.y += 62;
                        }
                    }
                    pr.match.noDivCounters = false;
                    pr.input.repeatThisLine();
                    break;
                case MMD:
                    while (b.indexOf(":") < 0) {
                        b.append(pr.input.nextLine());
                    }
                    pr.curMap = pr.setupMap;
                    if (pr.curMap != null) {
                        pr.curPoint = pr.curBoard.addLocation(b.toString());
                    }
                    pr.parsePieces();
                    pr.placePiecesOnMap();
                    break;
                case Avail:
                    if (specWords.length > 2) {
                        pr.curMap = pr.setupMap;
                        if (pr.curMap != null) {
                            pr.curPoint = pr.curBoard.addLocation(specWords[0] + ":");
                        }
                        pr.parsePieces(new StringBuffer(pr.merge(SetupReader.strip(specWords, 2), " ")));
                        pr.placePiecesOnMap();
                        break;
                    }
                case Emer:
                case Sov_Air:
                case Axis_Air:
                    pr.curMap = pr.setupMap;
                    if (pr.curMap != null) {
                        pr.curPoint = pr.curBoard.addLocation(b.toString());
                    }
                    pr.parsePieces();
                    pr.placePiecesOnMap();
                    break;
                case Railheads:
                    pr.curBoard.addMajorNote(pr.merge(specWords, " "));
                    for (;;) {
                        b = pr.input.nextLine();
                        if (b == null) return true;
                        pr.curBoard.addNote(b.toString());
                        if (b.charAt(b.length() - 1) == ':') break;
                    }
                    for (;;) {
                        StringBuffer curLine;
                        curLine = pr.input.nextLine();
                        if (curLine == null) break;
                        if (curLine.charAt(0) == '*') break;
                        if(curLine.charAt(0) == '!'
                                || curLine.charAt(0) == '?'
                                || curLine.charAt(curLine.length() - 1) == ':') {
                            break;
                        }
                        pr.curBoard.addNote(curLine.toString());
                        if (curLine.indexOf(" ") >= 0) {
                            curLine = new StringBuffer(curLine.substring(0, curLine.indexOf(" ")));
                        }
                        Matcher m = pr.hexRef.matcher(curLine.toString());
                        if (m.matches()) {
                            String mapRef = m.group(1);
                            int row = Integer.parseInt(m.group(2));
                            int col = Integer.parseInt(m.group(3));
                            if (pr.resolveMapLocation(mapRef, row, col)) continue;
                            pr.addPiece(pr.match.findMarker("Railhead - German"));
                            pr.placePiecesOnMap();
                        }

                    }
                    pr.input.repeatThisLine();
                    break;
                case Sausages:
                    if (!pr.isNumber(specWords[3])) {
                        pr.curBoard.addMajorNote(pr.merge(specWords, " "));
                        return true;
                    }
                    count = Integer.parseInt(specWords[3]);
                    if (count > 10) {
                        pr.input.writeError(true, "Bad statement");
                        return true;
                    }
                    if (count == 10) {
                        pr.curBoard.addMajorNote(pr.merge(specWords, " "));
                        return true;
                    }
                    count = 10 - count;
                    pr.curMap = pr.setupMap;
                    pr.curPoint = pr.curBoard.addLocation(pr.merge(specWords, " "));
                    for (int j = 0; j < count; j++) {
                        pr.addPiece(pr.match.findMarker("Sausage"));
                    }
                    pr.placePiecesOnMap();
                    break;
                case Other_Map_Set_Up:
                    pr.data.zonePrefix = "";
                case Map_Set_Up:
                    break;
                case GBII_Map_Set_Up:
                    pr.data.zonePrefix = "GBII ";
                    break;
            }
            return true;
        }
        return false;
    }

    static final int Mark_Avail = 1;
    static final int Mass_Asst = 2;
    static final int Tree_Bark = 3;
    static final int Dead = 4;
    static final int Sausages = 5;
    static final int Avail = 6;
    static final int Emer = 7;
    static final int Railheads = 8;
    static final int Dead_None = 9;
    static final int Sea_Asst = 10;
    static final int Mark_Avail_Short = 11;
    static final int Sov_Air = 12;
    static final int Map_Set_Up = 13;
    static final int Axis_Air = 14;
    static final int GBII_Map_Set_Up = 15;
    static final int Other_Map_Set_Up = 16;
    static final int Partisan_Attacks = 17;
    static final int MMD = 18;
    static final int Info = 19;
    static final int Long_Info = 20;

    int lineType;
    String[] specWords;

    @Override
    boolean isModuleSpecificLine (int side, StringBuffer b, PieceReader pr) {
        char [] special = new char [] { ':' };
        specWords = LineReader.bufferToWords(new StringBuffer(b), special, false);
        if (specWords.length == 7 && specWords[0].equalsIgnoreCase("set")
                && specWords[1].equalsIgnoreCase("up")
                && specWords[2].equals("(")
                && specWords[3].equalsIgnoreCase("case")
                && specWords[4].equalsIgnoreCase("blue")
                && specWords[5].equalsIgnoreCase("maps)")
                && specWords[6].equals(":")) {
            lineType = Other_Map_Set_Up;
            return true;
        }
        if (specWords.length == 6 && specWords[0].equalsIgnoreCase("set")
                && specWords[1].equalsIgnoreCase("up")
                && specWords[2].equals("(")
                && specWords[3].equalsIgnoreCase("GBII")
                && specWords[4].equalsIgnoreCase("maps)")
                && specWords[5].equals(":")) {
            lineType = GBII_Map_Set_Up;
            return true;
        }
        if (specWords.length > 4 && specWords[0].equalsIgnoreCase("set")
                && specWords[1].equalsIgnoreCase("up")
                && specWords[2].equals("(")
                && specWords[3].equals("Eat")) {
            lineType = Other_Map_Set_Up;
            return true;
        }
        if (specWords.length == 3 && specWords[0].equalsIgnoreCase("set")
                && specWords[1].equalsIgnoreCase("up")
                && specWords[2].equals(":")) {
            lineType = Map_Set_Up;
            return true;
        }
        if (specWords.length > 3 && specWords[0].equalsIgnoreCase("Incoming")
                && specWords[1].equalsIgnoreCase("SPs")
                && specWords[2].equals(":")) {
            lineType = Info;
            return true;
        }
        if (specWords.length > 3 && specWords[0].equalsIgnoreCase("Supply")
                && specWords[1].equalsIgnoreCase("Sources")
                && specWords[2].equals(":")) {
            lineType = Info;
            return true;
        }
        if (specWords.length > 2 && specWords[0].equalsIgnoreCase("Rebuilds")
                && specWords[1].equals(":")) {
            lineType = Info;
            return true;
        }
        if (specWords.length > 2 && specWords[0].equalsIgnoreCase("Railcap")
                && specWords[1].equals(":")) {
            lineType = Long_Info;
            return true;
        }
        if (specWords.length > 4 && specWords[0].equals("At")
                && specWords[1].equals("Any")
                && specWords[2].equals("Moscow")
                && specWords[3].equals("Military")
                && specWords[4].equals("District")) {
            lineType = MMD;
            return true;
        }
        if (specWords.length == 4 && specWords[0].equals("MMD")
                && specWords[1].equals("Air")
                && specWords[2].equals("Units")
                && specWords[3].equals(":")) {
            lineType = MMD;
            MMDonlyAir = true;
            return true;
        }
        if (specWords.length == 5 && specWords[0].equalsIgnoreCase("reserve")
                && specWords[1].equalsIgnoreCase("markers")
                && specWords[2].equalsIgnoreCase("available")
                && specWords[3].equals(":")) {
            lineType = Mark_Avail;
            return true;
        }
        if (specWords.length == 5 && specWords[0].equalsIgnoreCase("partisan")
                && specWords[1].equalsIgnoreCase("attacks")
                && specWords[2].equalsIgnoreCase("remaining")
                && specWords[3].equals(":")) {
            lineType = Partisan_Attacks;
            return true;
        }
        if (specWords.length == 4 && specWords[0].equalsIgnoreCase("reserve")
                && specWords[1].equalsIgnoreCase("markers")
                && specWords[2].equals(":")) {
            lineType = Mark_Avail_Short;
            return true;
        }
        if (specWords.length == 4 && specWords[0].equalsIgnoreCase("soviet")
                && specWords[1].equalsIgnoreCase("air")
                && specWords[2].equalsIgnoreCase("units")
                && specWords[3].equals(":")) {
            lineType = Sov_Air;
            return true;
        }
        if (specWords.length == 6 && specWords[0].equalsIgnoreCase("at")
                && specWords[1].equalsIgnoreCase("any")
                && specWords[2].equalsIgnoreCase("russian")
                && specWords[3].equalsIgnoreCase("controlled")
                && specWords[4].equalsIgnoreCase("airbase")
                && specWords[5].equals(":")) {
            lineType = Sov_Air;
            return true;
        }
        if (specWords.length == 6 && specWords[0].equalsIgnoreCase("at")
                && specWords[1].equalsIgnoreCase("any")
                && specWords[2].equalsIgnoreCase("axis")
                && specWords[3].equalsIgnoreCase("controlled")
                && specWords[4].equalsIgnoreCase("airbase")
                && specWords[5].equals(":")) {
            lineType = Axis_Air;
            return true;
        }
        if (specWords.length == 3 && specWords[0].equalsIgnoreCase("air")
                && specWords[1].equalsIgnoreCase("units")
                && specWords[2].equals(":")) {
            lineType = Axis_Air;
            return true;
        }
        if (specWords.length == 5 && specWords[0].equalsIgnoreCase("massive")
                && specWords[1].equalsIgnoreCase("assaults")
                && specWords[2].equalsIgnoreCase("available")
                && specWords[3].equals(":")) {
            lineType = Mass_Asst;
            return true;
        }
        if (specWords.length == 5 && specWords[0].equalsIgnoreCase("seaborne")
                && specWords[1].equalsIgnoreCase("assaults")
                && specWords[2].equalsIgnoreCase("available")
                && specWords[3].equals(":")) {
            lineType = Sea_Asst;
            return true;
        }
        if (specWords.length == 5 && specWords[0].equalsIgnoreCase("tree-bark")
                && specWords[1].equalsIgnoreCase("soup")
                && specWords[2].equalsIgnoreCase("markers")
                && specWords[3].equals(":")) {
            lineType = Tree_Bark;
            return true;
        }
        if (specWords.length == 5 && specWords[0].equalsIgnoreCase("tree")
                && specWords[1].equalsIgnoreCase("bark")
                && specWords[2].equalsIgnoreCase("soup")
                && specWords[3].equals(":")) {
            lineType = Tree_Bark;
            return true;
        }
        if (specWords.length == 6 && specWords[0].equalsIgnoreCase("tree")
                && specWords[1].equalsIgnoreCase("bark")
                && specWords[2].equalsIgnoreCase("soup")
                && specWords[3].equalsIgnoreCase("available")
                && specWords[4].equals(":")) {
            lineType = Tree_Bark;
            specWords[4] = specWords[5];
            return true;
        }
        if (specWords.length == 3 && specWords[0].equalsIgnoreCase("dead")
                && specWords[1].equalsIgnoreCase("units")
                && specWords[2].equals(":")) {
            lineType = Dead;
            return true;
        }
        if (specWords.length == 4 && specWords[0].equalsIgnoreCase("dead")
                && (specWords[1].equalsIgnoreCase("pile")
                    || specWords[1].equalsIgnoreCase("units"))
                && specWords[2].equals(":")
                && specWords[3].equalsIgnoreCase("none")) {
            lineType = Dead_None;
            return true;
        }
        if (specWords.length == 4 && specWords[0].equalsIgnoreCase("sausages")
                && specWords[1].equalsIgnoreCase("used")
                && specWords[2].equals(":")) {
            lineType = Sausages;
            return true;
        }
        if (specWords.length > 3 && specWords[0].equalsIgnoreCase("available")
                && specWords[1].equalsIgnoreCase("for")
                && specWords[2].equalsIgnoreCase("use")
                && specWords[3].equals(":")) {
            lineType = Emer;
            return true;
        }
        if (specWords.length > 1 && specWords[0].equalsIgnoreCase("available")
                && specWords[1].equals(":")) {
            lineType = Avail;
            return true;
        }
        if (specWords.length > 2 && specWords[0].equalsIgnoreCase("emergency")
                && specWords[1].equalsIgnoreCase("reinforcements")
                && specWords[specWords.length - 1].equals(":")) {
            lineType = Emer;
            return true;
        }
        if (specWords.length == 4 && specWords[0].equalsIgnoreCase("german")
                && specWords[1].equalsIgnoreCase("gauge")
                && specWords[2].equalsIgnoreCase("railroads")
                && specWords[3].equals(":")) {
            lineType = Railheads;
            return true;
        }
        if (specWords.length == 3 && specWords[0].equalsIgnoreCase("german")
                && specWords[1].equalsIgnoreCase("railheads")
                && specWords[2].equals(":")) {
            lineType = Railheads;
            return true;
        }
        return false;
    }

}
