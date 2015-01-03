/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.KeyStroke;
import net.cantab.hayward.george.OCS.Counters.Airbase;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.OcsCounter;
import net.cantab.hayward.george.OCS.Statics;

/**
 *
 * @author george
 */
public class Korea extends ModuleSpecific {

    Korea() {
        stepLossIncKey = KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK);
        maxHits = 3;
        levelIncKey = KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK);
        maxSupply = 20;
        supplyIncKey = KeyStroke.getKeyStroke('Z', InputEvent.CTRL_DOWN_MASK);
        supplyName = "Supply Points";
        supplyTokenName = "Supply Tokens";
        maxTransport = 2;
        paxEqDifferPerSide = false;
        paxEqFlips = false;
        flipToReduced = KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK);
        unitNameFiller = " ";
        divNameFiller = " ";
        twoStageDivLookup = true;
        prefixDivToUnit = false;
        flipOrganic = flipToReduced;
        boardPrefix = "Board ";
    }

    public void addTransport (String[] type, int size, boolean loaded, boolean isT,
            PieceReader pr) {
        OcsCounter p;
        if (isT) {
            pr.input.writeError(true,
                                "Token transport in module without token pieces");
            return;
        }
        int i = type.length - 1;
        if (type[i].equalsIgnoreCase("porter")) {
            p = pr.match.findPiece(pr.curSide, Transport.class, type);
            if (p == null) return;
            if (!loaded) {
                p.keyEvent(flipToReduced);
            }
            pr.addPiece(p);
        } else {
            String [] m;
            if (i == 0) {
                m = new String[]{"1", type[i]};
            } else {
                m = new String[]{type[0], "1", type[i]};
            }
            switch (size) {
            case 1:
                p = pr.match.findPiece(pr.curSide, Transport.class, m);
                if (p == null) {
                    return;
                }
                break;
            case 2:
                p = pr.match.findPiece(pr.curSide, Transport.class, m);
                if (p == null) {
                    return;
                }
                p.keyEvent(flipToReduced);
                break;
            default:
                return;
            }
            pr.addPiece(p);
            if (loaded) {
                pr.addSupply(size, isT);
            }
        }
    }

    @Override
    boolean moduleSpecificLine (int side, String [] words, int repeat, PieceReader pr) {
        if (words.length == 2 && words[0].equals("Rail")
                && words[1].equals("Head")) {
            return true;
        }
        if (words.length > 3 && words[0].equals("NK") && words[1].equals("Sec")
                && words[2].equals("Bde") && words[3].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.tail(words, words.length - 4);
            words = pr.top(words, words.length - 1);
            int i;
            for (i = 0; i < words.length; i += 3) {
                String [] m = new String [5];
                m[0] = "NK";
                m[1] = words[i];
                m[2] = words[i + 1];
                m[3] = "Sec";
                m[4] = "brg";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            return true;
        }
        if (words.length > 3 && words[0].equals("NK") && words[1].equals("NG")
                && words[2].equals("Bn") && words[3].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.tail(words, words.length - 4);
            words = pr.top(words, words.length - 1);
            int i;
            for (i = 0; i < words.length; i += 2) {
                String [] m = new String [4];
                m[0] = "NK";
                m[1] = words[i];
                m[2] = "NG";
                m[3] = "bn";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            return true;
        }
        if (words.length > 3 && words[0].equals("NK") && words[1].equals("Inf")
                && words[2].equals("Rgt") && words[3].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.tail(words, words.length - 4);
            words = pr.top(words, words.length - 1);
            int i;
            for (i = 0; i < words.length; i += 2) {
                String [] m = new String [4];
                m[0] = "NK";
                m[1] = words[i];
                m[2] = "inf";
                m[3] = "reg";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            return true;
        }
        if (words.length > 3 && words[0].equals("NK") && words[1].equals("Marine")
                && words[2].equals("Rgt") && words[3].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.tail(words, words.length - 4);
            words = pr.top(words, words.length - 1);
            int i;
            for (i = 0; i < words.length; i += 2) {
                String [] m = new String [4];
                m[0] = "NK";
                m[1] = words[i];
                m[2] = "Mar";
                m[3] = "reg";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            return true;
        }
        if (words.length > 3 && words[0].equals("NK") && words[1].equals("MC")
                && words[2].equals("Rgt") && words[3].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.tail(words, words.length - 4);
            words = pr.top(words, words.length - 1);
            int i;
            for (i = 0; i < words.length; i += 2) {
                String [] m = new String [4];
                m[0] = "NK";
                m[1] = words[i];
                m[2] = "M/C";
                m[3] = "reg";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            return true;
        }
        if (words.length > 3 && words[0].equals("NK") && words[1].equals("Tank")
                && words[2].equals("Bn") && words[3].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.tail(words, words.length - 4);
            words = pr.top(words, words.length - 1);
            int i;
            for (i = 0; i < words.length; i += 2) {
                String [] m = new String [4];
                m[0] = "NK";
                m[1] = words[i];
                m[2] = "Arm";
                m[3] = "bn";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            return true;
        }
        if (words.length > 3 && words[0].equals("NK") && words[1].equals("Arty")
                && words[2].equals("Rgt") && words[3].equals("(")
                && words[words.length - 1].equals(")")) {
            words = pr.tail(words, words.length - 4);
            words = pr.top(words, words.length - 1);
            int i;
            {
                String [] m = new String [5];
                m[0] = "NK";
                m[1] = words[0];
                m[2] = "mot";
                m[3] = "art";
                m[4] = "reg";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            for (i = 2; i < words.length; i += 2) {
                String [] m = new String [6];
                m[0] = "NK";
                m[1] = words[i];
                m[2] = "Corps";
                m[3] = "mot";
                m[4] = "art";
                m[5] = "reg";
                pr.addPiece(pr.match.findPiece(side, m));
            }
            return true;
        }
        return false;
    }

    int lineType;
    String[] specWords;

    static final int Map_Set_Up = 1;
    static final int Mark_Avail_Short = 2;
    static final int Dead = 3;
    static final int Dead_None = 4;
    static final int Air = 5;
    static final int Bases = 6;
    static final int Air_None = 7;
    
    Pattern hexRef = Pattern.compile("(.+?)(\\d+)\\.(\\d+)");

    @Override
    boolean moduleSpecificLine (int side, StringBuffer b, SetupReader pr) {
        if (isModuleSpecificLine(side, b, pr)) {
            if (pr.curBoard == null) {
                pr.input.writeError(true, "Unable to use board - not known");
                return true;
            }
            int count;
            switch (lineType) {
                case Map_Set_Up:
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
                case Dead_None:
                case Air_None:
                    pr.curBoard.addMajorNote(b.toString());
                    break;
                case Bases:
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
                        char [] special = new char[]{ ':', ','};
                        String [] s = ReadAndLogInput.bufferToWords(curLine, special, false);
                        if (s.length < 4 || !s[0].equalsIgnoreCase("level")
                                || !s[2].equals(":") || !pr.isNumber(s[1])) break;
                        count = Integer.parseInt(s[1]);
                        OcsCounter p = pr.match.findMarker("Air Base", Airbase.class);
                        for (; count > 1; count--) {
                            p.keyEvent(levelIncKey);
                        }
                        s = pr.tail(s, s.length - 3);
                        for (String t: s) {
                            if (t.equals(",")) continue;
                            Matcher m = hexRef.matcher(t);
                            if (m.matches()) {
                                String mapRef = m.group(1);
                                int row = Integer.parseInt(m.group(2));
                                int col = Integer.parseInt(m.group(3));
                                if (pr.resolveMapLocation(mapRef, row, col)) {
                                    pr.input.writeError(true, "failed to resolve air base hex: " + t);
                                } else {
                                    pr.addPiece(p);
                                    pr.placePiecesOnMap(true);
                                }
                            } else {
                                pr.input.writeError(true, "Bad hex for air base: " + t);
                            }
                        }
                    }
                    pr.input.repeatThisLine();
                    break;
                case Dead:
                    if (pr.resolveBoxLocation(Statics.theSides[side].name + " Dead Pile", new Point(26, 26))) {
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
                        pr.curPoint.x += 52;
                        if (pr.curPoint.x > 180) {
                            pr.curPoint.x = 26;
                            pr.curPoint.y += 52;
                        }
                    }
                    pr.match.noDivCounters = false;
                    pr.input.repeatThisLine();
                    break;
                case Air:
                    pr.curMap = pr.setupMap;
                    if (pr.curMap != null) {
                        pr.curPoint = pr.curBoard.addLocation(b.toString());
                    }
                    parsePieces(pr);
                    pr.placePiecesOnMap();
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * Parse the pieces
     */
    void parsePieces(SetupReader pr) {
        for (;;) {
            StringBuffer curLine;
            curLine = pr.input.nextLine();
            if (curLine == null) break;
            if (curLine.charAt(0) == '*') break;
            if(curLine.charAt(0) == '!'
                    || curLine.charAt(0) == '?'
                    || curLine.charAt(curLine.length() - 1) == ':')
                break;
            if (isModuleSpecificLine(pr.curSide, curLine, pr)) break;
            int i;
            for (;;) {
                i = curLine.indexOf(",");
                if (i < 0 ) break;
                pr.parsePieces(new StringBuffer(curLine.substring(0, i)));
                curLine.delete(0, i + 1);
            }
            pr.parsePieces(curLine);
        }
        pr.input.repeatThisLine();
    }



    @Override
    boolean isModuleSpecificLine (int side, StringBuffer b, PieceReader pr) {
        char [] special = new char [] { ':' };
        specWords = LineReader.bufferToWords(new StringBuffer(b), special, false);
        if (specWords.length> 2 && specWords[0].equalsIgnoreCase("set")
                && specWords[1].equalsIgnoreCase("up")
                && specWords[2].equals(":")) {
            lineType = Map_Set_Up;
            return true;
        }
        if (specWords.length == 4 && specWords[0].equalsIgnoreCase("reserve")
                && specWords[1].equalsIgnoreCase("markers")
                && specWords[2].equals(":")) {
            lineType = Mark_Avail_Short;
            return true;
        }
        if (specWords.length == 3 && specWords[0].equalsIgnoreCase("dead")
                && (specWords[1].equalsIgnoreCase("pile")
                    || specWords[1].equalsIgnoreCase("units"))
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
        if (specWords.length == 4 && specWords[0].equalsIgnoreCase("air")
                && specWords[1].equalsIgnoreCase("units")
                && specWords[2].equals(":")
                && specWords[3].equalsIgnoreCase("none")) {
            lineType = Air_None;
            return true;
        }
        if (specWords.length == 3 && specWords[0].equalsIgnoreCase("air")
                && specWords[1].equalsIgnoreCase("units")
                && specWords[2].equals(":")) {
            lineType = Air;
            return true;
        }
        if (specWords.length == 3 && specWords[0].equalsIgnoreCase("air")
                && specWords[1].equalsIgnoreCase("bases")
                && specWords[2].equals(":")) {
            lineType = Bases;
            return true;
        }
        return false;
    }

    String [] defaults = new String[] { "NK", "US", "ROK", "CW", "UN",
                     "Chinese", "Russian"};

    String [] unNames = new String[] { "Phil", "Turk", "Thai", "French", "Dutch", "Greek", "Belg", "Eth" };

    @Override
    String finalName(int side, String name, String [] type) {
        if (side < 0) return name;
        String p = defaults[side] + " ";
        for (String q : defaults) {
            if (name.startsWith(q)) {
                p = "";
                break;
            }
            int i = name.indexOf(q);
            if (i > 0) {
                name = name.substring(0, i - 1) + name.substring(i + q.length());
                p = q + " ";
                break;
            }
            if (type != null && type.length > 0 && type[0].equals(q)) {
                type = PieceReader.tail(type, type.length - 1);
                p = q + " ";
                break;
            }
        }
        for (String q : unNames) {
            if (q.equals(name)) {
                p = "UN ";
                break;
            }
        }
        String s = name + getAppendText(type, p.equals("US ") || p.equals("CW "));
        if (p.equals("ROK ") && type != null && type.length > 0 && type[0].equals("Arty")) {
            s += s.endsWith("Corps") ? " art reg" : " Corps art reg";
        }
        return p + s;
    }
    
    String getAppendText(String [] type, boolean mot) {
        if (type != null && type.length == 2) {
            if (type[0].equals("Sec") && type[1].equals("Bde")) {
                return " Sec brg";
            }
            if (type[0].equals("NG") && type[1].equals("Bn")) {
                return " NG bn";
            }
            if (type[0].equals("Tank") && type[1].equals("Bn")) {
                return " Arm bn";
            }
            if (type[0].equals("Inf") && type[1].equals("Div")) {
                return " ID";
            }
            if (type[0].equals("Inf") && type[1].equals("Rgt")) {
                return mot ? " mot reg" : " inf reg";
            }
            if (type[0].equals("Motorcycle") && type[1].equals("Rgt")) {
                return " M/C reg";
            }
            if (type[0].equals("Marine") && type[1].equals("Rgt")) {
                return " Mar reg";
            }
        }
        return "";
    }

    @Override
    String finalUnitName(int side, String uName, String dName) {
        char [] special = new char[0];
        String[] x = ReadAndLogInput.bufferToWords(new StringBuffer(uName), special, false);
        String[] y = ReadAndLogInput.bufferToWords(new StringBuffer(dName), special, false);
        if (y[0].equals("1") && y[1].equals("Cav")) {
            y[0] = "1 Cav";
        } else if (y[0].equals("1") && y[1].equals("Mar")) {
            y[0] = "1 Mar";
        }
        if (uName.equals("Organic Truck")) {
            return y[0] + " Truck";
        }
        String[] z = new String[x.length - 1];
        if(uName.endsWith("(FEC)")) {
            y[0] += " fec";
        } else if (x[0].equals("Divarty")) {
            y[0] += " arty";
        }
        System.arraycopy(x, 1, z, 0, z.length);
        boolean mot = side == 1;
        if (y[1].equals("ROK") || y[1].equals("UN")) mot = false;
        String v = x[0] + " " + y[0] + getAppendText(z, mot);
        for (String u : defaults) {
            if (u.equals(y[1])) {
                v = y[1] + " " + v;
                break;
            }
        }
        return v;
    }
}
