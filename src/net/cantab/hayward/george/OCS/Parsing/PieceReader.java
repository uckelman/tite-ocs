/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS.Parsing;

import VASSAL.build.module.Map;
import VASSAL.counters.PieceCloner;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import net.cantab.hayward.george.OCS.Counters.Aircraft;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.Counters.GameMarker;
import net.cantab.hayward.george.OCS.Counters.HeadQuarters;
import net.cantab.hayward.george.OCS.Counters.Land;
import net.cantab.hayward.george.OCS.Counters.Reserve;
import net.cantab.hayward.george.OCS.Counters.Ship;
import net.cantab.hayward.george.OCS.Counters.SupplyMarker;
import net.cantab.hayward.george.OCS.OcsCounter;
import net.cantab.hayward.george.OCS.SetupBoard;
import net.cantab.hayward.george.OCS.Statics;

/**
 * This reads the definition of one or more pieces from a line and recognises
 * them and adds the correct piece to the current location. It is an abstract
 * class which is completed by SetupReader and ReinforcementReader
 *
 * @author george
 */
public abstract class PieceReader {

    /**
     * The current side being processed
     */
    int curSide;
    /**
     * The Piece Search Object
     */
    PieceSearcher match;
    /**
     * The input being read
     */
    LineReader input;
    /**
     * Any module specific data needed
     */
    ModuleSpecific data;
    /**
     * All the pieces to be added at the current location
     */
    Stack<OcsCounter> pieces = new Stack<OcsCounter>();
    /**
     * True if all normal transport is to be loaded by default
     */
    boolean allLoaded;
    /**
     * True if all organic transport is to be loaded by default
     */
    boolean organicLoaded;
    /**
     * Name of current hex location being filled with pieces
     */
    String locationName;
    /**
     * true if transport in this location
     */
    boolean transportPlaced;
    /**
     * true if supply in this location
     */
    boolean supplyPlaced;
    /**
     * Locations where supply and transport have been placed
     */
    List<String> locations = new ArrayList<String>();
    /**
     * True if setup board location to be bumbed to next line
     */
    public boolean bumpLocation = false;
    /**
     * The map to add current set of pieces to
     */
    Map curMap;
    /**
     * The point on this map at which to add the current set of pieces
     */
    Point curPoint;
    /**
     * Setup Board to which pieces and comments will be added
     */
    SetupBoard curBoard = null;
    /**
     * Map to which this board belongs
     */
    Map setupMap = null;

    /**
     * Construct a piece reader
     */
    PieceReader(PieceSearcher a, LineReader b, ModuleSpecific c, int side) {
        match = a;
        input = b;
        data = c;
        curSide = side;
    }

    /**
     * Is a string all digits?
     */
    boolean isNumber(String test) {
        if (test.length() == 0) return false;
        for (int i = 0; i < test.length(); i++) {
            if (test.charAt(i) < '0') return false;
            if (test.charAt(i) > '9') return false;
        }
        return true;
    }

    /**
     * Is string a number or roman/number
     */
    boolean isNumberOrId(String test) {
        if (isNumber(test)) return true;
        int x = test.indexOf('/');
        if (x < 0) return false;
        if (!isNumber(test.substring(x + 1))) return false;
        for (x--; x > -1; x--) {
            if (test.charAt(x) != 'I') return false;
        }
        return true;
    }

    /**
     * Strip the first entries out of a string array
     */
    static String[] strip(String[] a, int b) {
        if (b >= a.length) {
            return new String[0];
        }
        String[] c = new String[a.length - b];
        System.arraycopy(a, b, c, 0, a.length - b);
        return c;
    }

    /**
     * Get the entries from the end of a string array
     */
    static String[] tail(String[] a, int b) {
        if (b >= a.length) {
            return new String[0];
        }
        String[] c = new String[b];
        System.arraycopy(a, a.length - b, c, 0, b);
        return c;
    }

    /**
     * Get the entries from beginning of a string array
     */
    static String[] top(String[] a, int b) {
        if (b >= a.length) {
            b = a.length;
        }
        String[] c = new String[b];
        System.arraycopy(a, 0, c, 0, b);
        return c;
    }

    /**
     * Merge an array of Strings with given filler string in the gaps
     */
    String merge(String[] a, String b) {
        if (a.length == 0) return "";
        String c = a[0];
        for (int i = 1; i < a.length; i++) {
            if (a[i].length() == 0) continue;
            if (c.charAt(c.length() - 1) == '('
                || a[i].charAt(0) == ')'
                || a[i].equals(",")) {
                c += a[i];
            } else if (a[i].charAt(0) == '^') {
                c += a[i].substring(1);
            } else {
                c += b + a[i];
            }
        }
        return c;
    }

    /**
     * Remove an entry from an array
     */
    static String[] remove(String[] a, int b) {
        if (b < 0 || b > a.length) return a;
        String[] c = new String[a.length - 1];
        if (b != 0) System.arraycopy(a, 0, c, 0, b);
        if (b != a.length - 1) System.arraycopy(a, b + 1, c, b, c.length - b);
        return c;
    }

    /**
     * Parse Pieces
     */
    void parsePieces(StringBuffer line) {
        /*
         * Convert the input buffer into a sequence of 'words'
         */
        char[] special = new char[]{'(', ',', ')'};
        String[] words = ReadAndLogInput.bufferToWords(line, special, true);
        if (words.length == 0) return;
        /*
         * If we are parsing markers then just match the marker
         */
        if (curSide == -1) {
            addPiece(match.findMarker(merge(words, " ")));
            return;
        }
        /*
         * If there is a repeat count at the start of the line then extract it.
         */
        int repeat = 1;
        int i;
        if (words[0].endsWith("x")
            && isNumber(words[0].substring(0, words[0].length() - 1))) {
            repeat = Integer.parseInt(words[0].substring(0, words[0].length() - 1));
            words = strip(words, 1);
        }
        /*
         * Check for module specific counter
         */
        if (data.moduleSpecificLine(curSide, words, repeat, this)) return;
        /*
         * If the line starts with a piece factor word then treat this as
         * a simple single or multiple piece definition
         */
        if (words[0].startsWith("#")) {
            simplePiece(words, repeat);
            return;
        }
        /*
         * If the line starts "Level-" then it is defining an airbase or hedgehog
         */
        if (words[0].length() > 6 && words[0].substring(0, 6).equalsIgnoreCase("Level-")) {
            baseOrHog(words, repeat);
            return;
        }
        /*
         * If the line starts "Level " <number> then it is defining an airbase or hedgehog
         */
        if (words.length > 2 && words[0].equalsIgnoreCase("Level")
            && isNumber(words[1])) {
            baseOrHog2(words, repeat);
            return;
        }
        /*
         * If the line ends "Level " <Number> then it is defining an airbase or hedgehog
         */
        if (words.length == 3 && words[1].equalsIgnoreCase("Level")
            && isNumber(words[2])) {
            baseOrHog3(words, repeat);
            return;
        }
        /*
         * If any word is "point(s)" then this is a transport point
         */
        for (i = 0; i < words.length; i++) {
            if (words[i].equalsIgnoreCase("point")
                || words[i].equalsIgnoreCase("points")) {
                transportPoint(words, i, repeat);
                return;
            }
        }
        /*
         * If the second and last word is "extender" then this is an extender
         */
        if (words.length == 2 && words[1].equalsIgnoreCase("extender")) {
            addExtender(words, repeat);
            return;
        }
        /*
         * If the last word is SP then this is a supply placement
         */
        if (words[words.length - 1].equalsIgnoreCase("SP")
            || words[words.length - 1].equalsIgnoreCase("SPs")) {
            placeSupply(words, repeat);
            return;
        }
        /*
         * If the last word is T then this supply token placement
         */
        if (words[words.length - 1].equals("T")) {
            placeTokenSupply(words, repeat);
            return;
        }
        /*
         * If the last word is "HQ" then this is an HQ unit
         */
        if (words[words.length - 1].equals("HQ")) {
            placeHQ(words, repeat);
            return;
        }
        /*
         * If the last word is "Marker" then place a trainbusting or reserve
         * marker
         */
        if (words[words.length - 1].equalsIgnoreCase("marker")) {
            placeMarker(words, repeat);
            return;
        }
        /*
         * If the last words is Pax then place a pax
         */
        if (words[words.length - 1].equalsIgnoreCase("pax")
            || (words.length > 1
                && (words[words.length - 1].equalsIgnoreCase("repl")
                    || words[words.length - 1].equalsIgnoreCase("repls"))
                && words[words.length - 2].equalsIgnoreCase("pax"))) {
            if (words[words.length - 1].equalsIgnoreCase("repls")) {
                words[words.length - 1] = "Repl";
            }
            placePax(words, repeat);
            return;
        }
        /*
         * If the last words is "Eq" then place an eq piece
         */
        if (words[words.length - 1].equalsIgnoreCase("eq")
            || (words.length > 1
                && (words[words.length - 1].equalsIgnoreCase("repl")
                    || words[words.length - 1].equalsIgnoreCase("repls"))
                && words[words.length - 2].equalsIgnoreCase("eq"))) {
            if (words[words.length - 1].equalsIgnoreCase("repls")) {
                words[words.length - 1] = "Repl";
            }
            placeEq(words, repeat);
            return;
        }
        /*
         * If the line contains "(" and it is not "(reduced)" or "(inexperienced)"
         * then this is a division specification otherwise it is an aircraft or
         * ship specification
         */
        if (words[words.length - 1].equals(ReadAndLogInput.REDUCED)) {
            words = top(words, words.length - 1);
            if (words[words.length - 1].equals(ReadAndLogInput.INEXP)) {
                words = top(words, words.length - 1);
                placeAircraftOrShip(words, repeat, true, true);
                return;
            }
            placeAircraftOrShip(words, repeat, true, false);
            return;
        }
        if (words[words.length - 1].equals(ReadAndLogInput.INEXP)) {
            words = top(words, words.length - 1);
            placeAircraftOrShip(words, repeat, false, true);
            return;
        }
        checkForDiv(words, repeat);
    }

    void checkForDiv(String[] words, int repeat) {
        int i;
        for (i = 0; i < words.length; i++) {
            if (words[i].equals("(")) break;
        }
        if (i == words.length) {
            /*
             * For Hube's this may be the whole division
             */
            if (Statics.theStatics.isHubes()) {
                if (match.findDivision(curSide, words)) {
                    match.addWholeDivision(curSide, words, this);
                    bumpLocation = true;
                    return;
                }
                for (i = 0; i < words.length; i++) {
                    if (words[i].equals(",")) break;
                }
                if (i < words.length) {
                    for (;;) {
                        String[] unit;
                        for (i = 0; i < words.length; i++) {
                            if (words[i].equals(",")) break;
                        }
                        if (i == words.length) break;
                        unit = top(words, i);
                        words = tail(words, words.length - 1 - i);
                        addPiece(match.findPiece(curSide, unit));
                    }
                    String[] t = match.removeDrossAt(words, words.length - 1);
                    if (t != null) words = t;
                    addPiece(match.findPiece(curSide, words));
                    bumpLocation = true;
                    return;
                }
                match.noErrorReport = true;
                OcsCounter r = match.findPiece(curSide, words);
                match.noErrorReport = false;
                if (r != null && r instanceof Land) {
                    addPiece(r);
                    bumpLocation = true;
                    return;
                }
                String[] u = match.removeDrossAt(words, words.length - 1);
                if (u != null) {
                    addPiece(match.findPiece(curSide, u));
                    bumpLocation = true;
                    return;
                }
            }
            placeAircraftOrShip(words, repeat, false, false);
            return;
        }
        if (!words[words.length - 1].equals(")") || repeat != 1) {
            input.writeError(true, "Bad parentheses");
            return;
        }
        String[] dn = top(words, i);
        words = strip(words, i + 1);
        words = top(words, words.length - 1);
        placeDivision(dn, words);
    }

    /**
     * Place an aircraft or ship
     */
    void placeAircraftOrShip(String[] words, int repeat,
                             boolean reduce, boolean inexp) {
        OcsCounter p;
        if (inexp) {
            String[] s = new String[words.length + 1];
            System.arraycopy(words, 0, s, 0, words.length);
            s[words.length] = "(inexperienced)";
            words = s;
        }
        for (; repeat > 0; repeat--) {
            p = match.findPiece(curSide, Aircraft.class, Ship.class, words);
            if (reduce && repeat == 1) {
                if (p == null) continue;
                p.keyEvent(data.flipToReduced);
            }
            addPiece(p);
        }
    }

    /**
     * Place a division - get each unit name from list and process it
     */
    void placeDivision(String[] divName, String[] words) {
        for (;;) {
            if (words.length == 0) break;
            int i;
            int depth = 0;
            boolean parens = false;
            for (i = 0; i < words.length; i++) {
                if (words[i].equals("(")) {
                    depth++;
                    parens = true;
                    continue;
                }
                if (words[i].equals(")")) {
                    depth--;
                    continue;
                }
                if (depth != 0) continue;
                if (words[i].equals(",")) break;
            }
            if (depth != 0) {
                input.writeError(true, "Mismatched parentheses");
                return;
            }
            String[] unit;
            if (i < words.length) {
                unit = top(words, i);
                words = strip(words, i + 1);
                if (Statics.theStatics.isCaseBlue() && unit.length == 1 && isNumberOrId(unit[0])) {
                    String[] w = words;
                    for (;;) {
                        if (w.length == 0) break;
                        for (i = 0; i < w.length; i++) {
                            if (w[i].equals("(")) {
                                depth++;
                                parens = true;
                                continue;
                            }
                            if (w[i].equals(")")) {
                                depth--;
                                continue;
                            }
                            if (depth != 0) continue;
                            if (w[i].equals(",")) break;
                        }
                        if (depth != 0) break;
                        String[] u;
                        if (i < w.length) {
                            u = top(w, i);
                            w = strip(w, i + 1);
                        } else {
                            u = top(w, w.length);
                            w = new String[0];
                        }
                        if (u.length > 1 && isNumberOrId(u[0])) {
                            u[0] = unit[0];
                            unit = u;
                            break;
                        }
                    }
                }
            } else {
                unit = words;
                words = new String[0];
            }
            if (unit.length == 0) {
                input.writeError(true, "Bad unit in division");
                return;
            }
            if (unit.length == 3 && unit[0].equals("all") && unit[2].equals("units") && isNumber(unit[1])) {
                match.addWholeDivision(curSide, divName, this);
                return;
            }
            if (unit.length == 4 && unit[0].equals("all") && unit[3].equals("units") && unit[2].equals("combat")
                && (isNumber(unit[1]) || unit[1].equals("five") || unit[1].equals("nine"))) {
                match.addWholeDivision(curSide, divName, this);
                return;
            }
            int repeat = 1;
            if (unit[0].endsWith("x")
                && isNumber(unit[0].substring(0, unit[0].length() - 1))) {
                repeat = Integer.parseInt(unit[0].substring(0, unit[0].length() - 1));
                unit = strip(unit, 1);
            }
            if (unit.length == 0) {
                input.writeError(true, "Bad unit in division");
                return;
            }
            boolean empty = unit[unit.length - 1].equals(ReadAndLogInput.EMPTY);
            boolean full = unit[unit.length - 1].equals(ReadAndLogInput.LOADED);
            if (empty || full) {
                unit = top(unit, unit.length - 1);
                if (unit.length == 0) {
                    input.writeError(true, "Bad unit in division");
                    return;
                }
            }
            if (unit[unit.length - 1].equalsIgnoreCase("truck")
                || unit[unit.length - 1].equalsIgnoreCase("trucks")) {
                if (!full && !empty) full = organicLoaded;
                for (int k = 0; k < repeat; k++) {
                    OcsCounter q = match.findPiece(curSide, divName, unit);
                    if (q == null) return;
                    if (full) {
                        if (data.flipOrganicFullEmpty) {
                            if (!data.fullDefault) {
                                q.keyEvent(data.flipOrganic);
                            }
                        }
                    } else {
                        if (data.flipOrganicFullEmpty && data.fullDefault) {
                            q.keyEvent(data.flipOrganic);
                        }
                    }
                    addPiece(q);
                    if (!data.flipOrganicFullEmpty && full) {
                        addSupply(1, false);
                    }
                    if (!full) {
                        placedTransport();
                    }
                }
                return;
            } else {
                if (full || empty) {
                    input.writeError(true, "Loaded/Empty only applies to organic trucks");
                    return;
                }
            }
            if (unit[0].charAt(0) == '#') {
                String factors = unit[0].substring(1);
                unit = strip(unit, 1);
                if (parens) {
                    if (repeat != 1) {
                        input.writeError(true, "Bad repeat count inside division");
                        return;
                    }
                    for (i = 0; i < unit.length; i++) {
                        if (unit[i].equals("(")) break;
                    }
                    String[] type = top(unit, i);
                    unit = strip(unit, i + 1);
                    processIds(factors, unit, type, divName);
                } else {
                    for (int k = 0; k < repeat; k++)
                        addPiece(match.findPiece(curSide, divName, factors, unit));
                }
            } else {
                for (int k = 0; k < repeat; k++)
                    addPiece(match.findPiece(curSide, divName, unit));
            }
        }
    }

    /**
     * Placed transport
     */
    void placedTransport() {
        if (locationName == null) return;
        if (!supplyPlaced) {
            transportPlaced = true;
            return;
        }
        locations.add(locationName);
        locationName = null;
    }

    /**
     * Placed supply
     */
    void placedSupply() {
        if (locationName == null) return;
        if (!transportPlaced) {
            supplyPlaced = true;
            return;
        }
        locations.add(locationName);
        locationName = null;
    }

    /**
     * Place a Pax piece
     */
    void placePax(String[] words, int repeat) {
        OcsCounter p;
        if (data.paxEqDifferPerSide) {
            p = match.findPiece(curSide, words);
        } else if (data.paxEqFlips) {
            p = match.findMarker(data.replacementName);
            if (!data.paxDefault) {
                p.keyEvent(data.flipRep);
            }
        } else {
            p = match.findMarker("Pax");
        }
        for (; repeat > 0; repeat--) {
            addPiece(p);
        }
    }

    /**
     * Place a Eq piece
     */
    void placeEq(String[] words, int repeat) {
        OcsCounter p;
        if (data.paxEqDifferPerSide) {
            p = match.findPiece(curSide, words);
        } else if (data.paxEqFlips) {
            p = match.findMarker(data.replacementName);
            if (data.paxDefault) {
                p.keyEvent(data.flipRep);
            }
        } else {
            p = match.findMarker("Eq");
        }
        for (; repeat > 0; repeat--) {
            addPiece(p);
        }
    }

    /**
     * Place a trainbusting or reserve marker
     */
    void placeMarker(String[] words, int repeat) {
        if (words[0].equalsIgnoreCase("Trainbusting")) {
            if (words.length != 2) {
                input.writeError(true, "Bad Marker");
                return;
            }
            if (repeat != 1) {
                input.writeError(true, "Trainbusting marker repeated");
                return;
            }
            addPiece(match.findMarker("Trainbusting"));
        } else if (words[0].equalsIgnoreCase("DG")) {
            if (words.length != 2) {
                input.writeError(true, "Bad Marker");
                return;
            }
            if (repeat != 1) {
                input.writeError(true, "DG marker repeated");
                return;
            }
            addPiece(match.findMarker("DG"));

        } else if (words[words.length - 2].equalsIgnoreCase("reserve")) {
            words = top(words, words.length - 1);
            OcsCounter p = match.findPiece(curSide, words);
            for (; repeat > 0; repeat--) {
                addPiece(p);
            }
        } else {
            if (Statics.theStatics.isBalticGap()
                && words.length == 3
                && words[0].equalsIgnoreCase("Arty")
                && words[1].equalsIgnoreCase("Ammo")) {
                String[] nw = new String[]{"Soviet", "Arty", "Ammo"};
                addPiece(match.findPiece(curSide, Reserve.class, nw));
            } else if (Statics.theStatics.isHungarianRhapsody()
                       && words.length == 3
                       && words[0].equalsIgnoreCase("Arty")
                       && words[1].equalsIgnoreCase("Ammo")) {
                String[] nw = new String[]{"Soviet", "Arty", "Ammo"};
                for (; repeat > 0; repeat--) {
                    addPiece(match.findMarker("Soviet Arty Ammo", GameMarker.class));
                }
            } else if (Statics.theStatics.isBalticGap() && words.length == 3
                       && words[0].equalsIgnoreCase("Sv")
                       && words[1].equalsIgnoreCase("GZ")) {
                String[] ow = new String[]{"Sv", "GZ", "KG"};
                addPiece(match.findPiece(curSide, Division.class, ow));
            } else {
                input.writeError(true, "Bad Marker");
            }
        }
    }

    /**
     * Place the HQ specified on the line
     */
    void placeHQ(String[] words, int repeat) {
        if (words.length == 1) {
            input.writeError(true, "Bad HQ specification");
            return;
        }
        if (!data.postfixHQ) words = top(words, words.length - 1);
        int i;
        for (i = 0; i < words.length; i++) {
            if (words[i].equals(",")) {
                for (int j = words.length - 1;; j--) {
                    if (words[j].equals(",")) {
                        String[] head = top(words, i);
                        j++;
                        if (j + head.length < words.length) j += head.length;
                        String[] tail = strip(words, j);
                        String[] all = new String[head.length + tail.length];
                        System.arraycopy(head, 0, all, 0, head.length);
                        System.arraycopy(tail, 0, all, head.length, tail.length);
                        addPiece(match.findPiece(curSide, HeadQuarters.class, all));
                        words = strip(words, i + 1);
                        i = -1;
                        break;
                    }
                }
            }
        }
        addPiece(match.findPiece(curSide, HeadQuarters.class, words));
    }

    /**
     * Place Token supply according to the line
     */
    void placeTokenSupply(String[] words, int repeat) {
        words = top(words, words.length - 1);
        if (words.length != 1 || !isNumber(words[0]) || repeat != 1) {
            input.writeError(true, "Bad supply token placement");
            return;
        }
        repeat = Integer.parseInt(words[0]);
        if (repeat > 3) {
            input.writeError(true, "Bad supply token placement");
            return;
        }
        addSupply(repeat, true);
    }

    /**
     * Place supply according to the line
     */
    void placeSupply(String[] words, int repeat) {
        int tSupply = 0;
        words = top(words, words.length - 1);
        if (words.length != 0) {
            if (words.length == 1) {
                if (!isNumber(words[0]) || repeat != 1) {
                    input.writeError(true, "Bad supply placement");
                    return;
                }
                repeat = Integer.parseInt(words[0]);
            } else if (words.length == 2) {
                if (!words[1].equals("T") || repeat != 1) {
                    input.writeError(true, "Bad Supply Placement");
                    return;
                }
                if (isNumber(words[0])) {
                    tSupply = Integer.parseInt(words[0]);
                    repeat = 0;
                } else {
                    if (words[0].length() < 2
                        || words[0].charAt(words[0].length() - 2) != '+'
                        || !isNumber(words[0].substring(words[0].length() - 1))
                        || !isNumber(words[0].substring(0, words[0].length() - 2))) {
                        input.writeError(true, "Bad Supply Placement");
                        return;
                    }
                    tSupply = Integer.parseInt(words[0].substring(words[0].length() - 1));
                    repeat = Integer.parseInt(words[0].substring(0, words[0].length() - 2));
                }
                if (tSupply > 3) {
                    input.writeError(true, "Bad Supply Placement");
                    return;
                }
            } else {
                input.writeError(true, "Bad Supply Placement");
                return;
            }
        }
        if (repeat != 0) {
            addGeneralSupply(repeat);
        }
        if (tSupply != 0) {
            addSupply(tSupply, true);
        }
    }

    /**
     * Create an extender of the right sort
     */
    void addExtender(String[] words, int repeat) {
        if (repeat != 1) {
            input.writeError(true, "Extender cannot be repeated");
            return;
        }
        /*
         * Extenders are obtained by incrementing the transport point beyond its
         * highest denomination
         */
        words = top(words, 1);
        addTransport(words, data.maxTransport + 1, false, false);
    }

    /**
     * Parse a transport point which may or may not be loaded
     */
    void transportPoint(String[] words, int point, int repeat) {
        boolean loaded = false;
        if (words.length > point + 1) {
            if (allLoaded) {
                if (words.length != point + 2
                    || !words[point + 1].equals(ReadAndLogInput.EMPTY)) {
                    input.writeError(true, "Bad transport point");
                    return;
                }
            } else {
                if (words.length != point + 2
                    || !words[point + 1].equals(ReadAndLogInput.LOADED)) {
                    input.writeError(true, "Bad transport point");
                    return;
                }
                loaded = true;
            }
        } else if (allLoaded) {
            loaded = true;
        }
        int tTrans = 0;
        int sp = 0;
        words = top(words, point);
        if (words.length == 0) {
            input.writeError(true, "Bad transport placement");
            return;
        }
        if (words.length >= 2 && words[1].equals("T")) {
            if (isNumber(words[0])) {
                tTrans = Integer.parseInt(words[0]);
                words = tail(words, words.length - 2);
                if (tTrans > 3 && !Statics.theStatics.isDAK()) {
                    input.writeError(true, "Bad transport Placement");
                    return;
                }
            } else if (words[0].length() > 2
                       && words[0].charAt(words[0].length() - 2) == '+'
                       && isNumber(words[0].substring(words[0].length() - 1))
                       && isNumber(words[0].substring(0, words[0].length() - 2))) {
                tTrans = Integer.parseInt(words[0].substring(
                    words[0].length() - 1));
                sp = Integer.parseInt(words[0].substring(0,
                                                         words[0].length() - 2));
                words = tail(words, words.length - 2);
                if (tTrans > 3) {
                    input.writeError(true, "Bad transport Placement");
                    return;
                }
            } else {
                sp = 1;
            }
        } else {
            if (isNumber(words[0])) {
                sp = Integer.parseInt(words[0]);
                words = strip(words, 1);
            } else {
                sp = 1;
            }
        }
        for (; repeat > 0; repeat--) {
            if (sp > 0) {
                int size = sp;
                int maxUnit = loaded ? Math.min(data.maxSupply, data.maxTransport)
                              : data.maxTransport;
                for (; size > maxUnit; size -= maxUnit) {
                    addTransport(words, maxUnit, loaded, false);
                }
                addTransport(words, size, loaded, false);
            }
            if (tTrans > 0) {
                addTransport(words, tTrans, loaded, true);
            }
        }
    }

    /**
     * Add a transport point and associated supply point if loaded
     */
    void addTransport(String[] type, int size, boolean loaded, boolean isT) {
        data.addTransport(type, size, loaded, isT, this);
        if (!loaded) {
            placedTransport();
        }
    }

    /**
     * Add supply to given amount (>3T) generating multiple counts if required
     */
    void addGeneralSupply(int size) {
        for (; size > data.maxSupply; size -= data.maxSupply) {
            addSupply(data.maxSupply, false);
        }
        addSupply(size, false);
        placedSupply();
    }

    /**
     * Add a supply counter upto maximum possible size
     */
    void addSupply(int size, boolean isT) {
        OcsCounter p;
        if (data.supplyTokenName == null) {
            p = match.findMarker(data.supplyName, SupplyMarker.class);
        } else {
            p = match.findMarker(isT ? data.supplyTokenName : data.supplyName, SupplyMarker.class);
        }
        if (p == null) {
            return;
        }
        if (!isT && data.supplyTokenName == null) {
            p.keyEvent(data.supplyIncKey); // now 2T
            p.keyEvent(data.supplyIncKey); // now 3T
            p.keyEvent(data.supplyIncKey); // now 4T = 1 SP
        }
        for (int i = 1; i < size; i++) {
            p.keyEvent(data.supplyIncKey);
        }
        p.theSide = curSide;
        addPiece(p);
    }

    /**
     * Parse a hedgehog or air base piece
     */
    void baseOrHog(String[] words, int repeat) {
        if (repeat != 1 && !Statics.theStatics.isHubes()) {
            input.writeError(true, "Repeated hedgehogs/airbases");
            return;
        }
        if (words[0].length() != 7
            || words[0].charAt(6) < '1'
            || words[0].charAt(6) > '4'
            || words.length == 1
            || words.length > 2
            || (!words[1].equalsIgnoreCase("hedgehog")
                && !words[1].equalsIgnoreCase("airbase"))) {
            input.writeError(true, "Invalid hedgehog/airbase piece");
            return;
        }
        int depth = words[0].charAt(words[0].length() - 1) - '0';
        OcsCounter p = match.findMarker(words[1]);
        if (p == null) return;
        for (; depth > 1; depth--) {
            p.keyEvent(data.levelIncKey);
        }
        p.theSide = curSide;
        if (Statics.theStatics.isHubes()) {
            for (; repeat > 1; repeat--) {
                addPiece(p);
            }
        }
        addPiece(p);
    }

    /**
     * Parse a hedgehog or air base piece - variant
     */
    void baseOrHog2(String[] words, int repeat) {
        if (repeat != 1 && !Statics.theStatics.isHubes()) {
            input.writeError(true, "Repeated hedgehogs/airbases");
            return;
        }
        int depth = Integer.parseInt(words[1]);
        if (Statics.theStatics.isHubes() && words[2].equalsIgnoreCase("airbases")) {
            words[2] = "Airbase";
        }
        if (words.length > 3
            || depth > 4
            || (!words[2].equalsIgnoreCase("hedgehog")
                && !words[2].equalsIgnoreCase("airbase"))) {
            input.writeError(true, "Invalid hedgehog/airbase piece");
            return;
        }

        OcsCounter p = match.findMarker(words[2]);
        if (p == null) return;
        for (; depth > 1; depth--) {
            p.keyEvent(data.levelIncKey);
        }
        p.theSide = curSide;
        if (Statics.theStatics.isHubes()) {
            for (; repeat > 1; repeat--) {
                addPiece(p);
            }
        }
        addPiece(p);
    }

    /**
     * Parse a hedgehog or air base piece - variant 2
     */
    void baseOrHog3(String[] words, int repeat) {
        if (repeat != 1 && !Statics.theStatics.isHubes()) {
            input.writeError(true, "Repeated hedgehogs/airbases");
            return;
        }
        int depth = Integer.parseInt(words[2]);
        if (depth > 4
            || (!words[0].equalsIgnoreCase("hedgehog")
                && !words[0].equalsIgnoreCase("airbase"))) {
            input.writeError(true, "Invalid hedgehog/airbase piece");
            return;
        }

        OcsCounter p = match.findMarker(words[0]);
        if (p == null) return;
        for (; depth > 1; depth--) {
            p.keyEvent(data.levelIncKey);
        }
        p.theSide = curSide;
        addPiece(p);
    }

    String[] removeDrossAt(String[] a, int b) {
        return null;
    }

    String[] removeDross(String[] a) {
        return null;
    }

    /**
     * Parse a simple single or multiple piece definition
     */
    void simplePiece(String[] words, int repeat) {
        /*
         * Look for opening parentheses in the line to distinguish type of
         * specification
         */
        int i;
        String factors = words[0].substring(1);
        words = strip(words, 1);
        for (i = 0; i < words.length; i++) {
            if (words[i].equals("(")) {
                if (!Statics.theStatics.isDAK()) break;
                String[] tr = null;
                /*
                 * Test for special indicators
                 */
                if (words.length - i > 2
                    && (words[i + 1].equalsIgnoreCase("red")
                        || words[i + 1].equalsIgnoreCase("yellow"))
                    && words[i + 2].equals(")")) {
                    words = remove(words, i + 2);
                    words = remove(words, i);
                    tr = removeDrossAt(words, i - 1);
                } else if (words.length - i > 3
                           && words[i + 1].equalsIgnoreCase("no")
                           && words[i + 2].equalsIgnoreCase("color")
                           && words[i + 3].equals(")")) {
                    words = remove(words, i + 3);
                    words = remove(words, i);
                    tr = removeDrossAt(words, i - 1);
                } else {
                    break;
                }
                if (tr != null) {
                    i -= words.length - tr.length;
                    words = tr;
                }
            }
        }
        if (i == words.length) {
            if (i == 0) {
                input.writeError(true, "incomplete line??");
                return;
            }
            /*
             * It's of the type <factor> <id> usually breakdown or some other
             * generic unit which can have a repeat count
             */
            String[] tr = removeDross(words);
            if (tr != null) words = tr;
            addPiece(factors, words, repeat);
            return;
        }
        /*
         * It's of the form
         * <factor> <type> ( <id>, <id> )
         */
        for (int j = 0; j < repeat; j++) {
            String[] type = top(words, i);
            String[] nwords = strip(words, i + 1);
            processIds(factors, nwords, type, null);
        }
    }

    void processIds(String factors, String[] words, String[] type, String[] div) {
        Class hqType = null;
        if (type.length > 0 && type[type.length - 1].equals("HQ")) {
            hqType = HeadQuarters.class;
            factors = null;
        }
        /*
         * Find the end of the piece ids and find out how many there are
         */
        int noIds = 1;
        int depth = 0;
        int i;
        for (i = 0; i < words.length; i++) {
            if (words[i].equals("(")) {
                depth++;
                continue;
            }
            if (words[i].equals(")")) {
                if (depth == 0) break;
                depth--;
                continue;
            }
            if (words[i].equals(",")) {
                noIds++;
            }
        }
        if (i == words.length) {
            input.writeError(true, "Unterminated piece list");
            return;
        }
        if (i != words.length - 1) {
            input.writeError(true, "Information after end of parsed line ignored");
        }
        words = top(words, i);
        /*
         * If there a loss distribution to be made then put these pieces in a
         * seperate stack in the side window
         */
        String dist = isLossDistribution(words);
        if (dist != null) {
            input.writeError(true, "Unable to handle: " + dist + " Automatically");
            return;
        }
        /*
         * Now process each piece identifier
         */
        String[] dx = div;
        for (i = 0; i < noIds; i++) {
            int j;
            for (j = words.length - 1; j >= 0; j--) {
                if (words[j].equals(",")) {
                    String[] md = this.strip(words, j);
                    if (isStepLoss(md) != 0 && lossLength + 1 == md.length) {
                        words = remove(words, j);
                        noIds--;
                    }
                    break;
                }
            }
            for (j = 0; j < words.length; j++) {
                if (words[j].equals(",")) break;
            }
            String[] id = top(words, j);
            words = strip(words, j + 1);
            /*
             * Does this id contain a specific step loss instruction
             */
            int losses = isStepLoss(id);
            if (losses != 0) {
                id = top(id, id.length - lossLength);
            } else if (Statics.theStatics.isDAK() && div == null && hqType == null
                       && id[id.length - 1].equals(")")) {
                for (int k = 0; k < id.length; k++) {
                    if (id[k].equals("(")) {
                        id = remove(id, id.length - 1);
                        String[] d = strip(id, k + 1);
                        if (!d[d.length - 1].equals("Div")) {
                            dx = new String[d.length + 1];
                            System.arraycopy(d, 0, dx, 0, d.length);
                            dx[d.length] = "Div";
                        } else {
                            dx = d;
                        }
                        id = top(id, k);
                        break;
                    }
                }
            } else if (hqType != null && data.postfixHQ) {
                id[id.length - 1] += "HQ";
            }
            addPiece(match.findPiece(curSide, hqType, null, factors, id, dx, type));
            if (losses != 0) {
                if (losses > data.maxHits) {
                    input.writeError(false, "Too many losses here - excess ignored");
                    losses = data.maxHits;
                }
                OcsCounter p = match.findMarker("Step Loss");
                if (p == null) {
                    continue;
                }
                for (int k = 1; k < losses; k++) {
                    p.keyEvent(data.stepLossIncKey);
                }
                addPiece(p);
            }
        }
    }
    int lossLength;

    /**
     * Does this array of words end with a specific step loss instruction?
     *
     * @return the number of steps to loses or zero if no instruction
     */
    int isStepLoss(String[] words) {
        if (words.length < 4) return 0;
        final int m = words.length - 1;
        lossLength = 3;
        if ((words[m].equalsIgnoreCase("step")
             || words[m].equalsIgnoreCase("steps"))
            && words[m - 2].equalsIgnoreCase("less")
            && isNumber(words[m - 1])) {
            return Integer.parseInt(words[m - 1]);
        }
        if (words[m].equalsIgnoreCase("step")
            && words[m - 2].equalsIgnoreCase("less")
            && words[m - 1].equalsIgnoreCase("one")) {
            return 1;
        }
        if (words[m].equalsIgnoreCase("steps")
            && words[m - 2].equalsIgnoreCase("less")
            && words[m - 1].equalsIgnoreCase("two")) {
            return 2;
        }
        if (words[m].equalsIgnoreCase("steps")
            && words[m - 2].equalsIgnoreCase("less")
            && words[m - 1].equalsIgnoreCase("three")) {
            return 3;
        }
        if (words.length < 6) return 0;
        lossLength = 5;
        if (!words[m].equals(")") || !words[m - 4].equals("(")) return 0;
        if ((words[m - 1].equalsIgnoreCase("step")
             || words[m - 1].equalsIgnoreCase("steps"))
            && words[m - 3].equalsIgnoreCase("less")
            && isNumber(words[m - 2])) {
            return Integer.parseInt(words[m - 2]);
        }
        if (words[m - 1].equalsIgnoreCase("step")
            && words[m - 3].equalsIgnoreCase("less")
            && words[m - 2].equalsIgnoreCase("one")) {
            return 1;
        }
        if (words[m - 1].equalsIgnoreCase("steps")
            && words[m - 3].equalsIgnoreCase("less")
            && words[m - 2].equalsIgnoreCase("two")) {
            return 2;
        }
        if (words[m - 1].equalsIgnoreCase("steps")
            && words[m - 3].equalsIgnoreCase("less")
            && words[m - 2].equalsIgnoreCase("three")) {
            return 3;
        }
        return 0;
    }

    /**
     * Does this array of words end with a loss distribution instruction?
     *
     * @return the instruction as a string or null if none
     */
    String isLossDistribution(String[] words) {
        if (words.length < 5) return null;
        int m = words.length - 1;
        if ((words[m].equalsIgnoreCase("losses")
             || words[m].equalsIgnoreCase("loss"))
            && words[m - 1].equalsIgnoreCase("step")
            && isNumber(words[m - 2])
            && (words[m - 3].equalsIgnoreCase("distribute")
                || words[m - 3].equalsIgnoreCase("distrib."))) {
            return merge(tail(words, 4), " ");
        }
        return null;
    }

    /**
     * Add a piece with given factors and id the required number of times
     */
    void addPiece(String factors, String[] id, int repeat) {
        id = data.convertSimple(curSide, factors, id);
        OcsCounter p = match.findPiece(curSide, factors, id);
        for (; repeat > 0; repeat--) {
            addPiece(p);
        }
    }

    /**
     * Add a piece to the current location. Piece must be cloned from this
     * definition
     */
    void addPiece(OcsCounter p) {
        if (p == null) return;
        pieces.add((OcsCounter) PieceCloner.getInstance().clonePiece(p));
    }
}
