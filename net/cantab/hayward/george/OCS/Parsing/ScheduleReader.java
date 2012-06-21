/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS.Parsing;

import net.cantab.hayward.george.OCS.Counters.Land;
import net.cantab.hayward.george.OCS.OcsCounter;
import net.cantab.hayward.george.OCS.Statics;

/**
 * This reads a reinforcements schedule
 * @author george
 */
public class ScheduleReader extends SetupReader {

    ScheduleReader(PieceSearcher a, LineReader b, ModuleSpecific c, int d) {
        super(a, b, c, d);
        allLoaded = false;
        organicLoaded = true;
    }

    /**
     * Return the type of board we are matching
     */
    @Override
    String boardType() {
        return " Reinforcements";
    }

    /**
     * Returns true is schedule
     */
    @Override
    boolean isSchedule() {
        return true;
    }

    /**
     * Parse the whole schedule
     */
    @Override
    void parse() {
        StringBuffer curLine;
        for (;;) {
            curLine = input.nextLine();
            if (curLine == null) {
                break;
            }
            if (curLine.charAt(0) == '!') {
                break;
            }
            if (curLine.charAt(0) == '*') {
                parseAskeriskLine(curLine);
            } else if (curLine.charAt(0) == '?') {
                curLine.deleteCharAt(0);
                parseFixedLocation(curLine);
            } else if (curLine.charAt(curLine.length() - 1) == ':') {
                char[] special = new char[]{':'};
                String[] words = ReadAndLogInput.bufferToWords(new StringBuffer(
                        curLine), special, false);
                for (String s : words) {
                    if (s.equalsIgnoreCase("withdraw")) {
                        input.writeError(true,
                                         "Possible withdrawal done as reinforcement stack");
                    }
                }
                parseFixedLocation(curLine);
            } else {
                input.writeError(true, "Line ignored");
            }
        }
        curBoard.addFinalSpace();
        input.repeatThisLine();
    }

    @Override
    void checkForDiv(String[] words, int repeat) {
        if (Statics.theStatics.isDAK()) {
            boolean drossed = false;
            int i;
            for (i = 0; i < words.length; i++) {
                if (words[i].equals("(")) {
                    String [] tr = null;;
                    /*
                     * Test for special indicators
                     */
                    if (words.length - i > 2
                            && (words[i+1].equalsIgnoreCase("red")
                            || words[i+1].equalsIgnoreCase("yellow"))
                            && words[i+2].equals(")")) {
                        words = remove(words, i+2);
                        words = remove(words,i);
                        tr = removeDrossAt(words, i - 1);
                    } else if (words.length - i > 3
                            && words[i+1].equalsIgnoreCase("no")
                            && words[i+2].equalsIgnoreCase("color")
                            && words[i+3].equals(")")) {
                        words = remove(words, i+3);
                        words = remove(words, i);
                        tr = removeDrossAt(words, i - 1);
                    } else {
                        break;
                    }
                    if (tr != null ) {
                        i -= words.length - tr.length;
                        words = tr;
                        drossed = true;
                    }
                }
            }
            if (i == words.length) {
                String[] trimmed = removeDross(words);
                if (trimmed == null && !drossed) {
                    match.noErrorReport = true;
                    OcsCounter p = match.findPiece(curSide, Land.class, words);
                    match.noErrorReport = false;
                    if (p != null) {
                        addPiece(p);
                        return;
                    }
                    placeAircraftOrShip(words, repeat, false, false);
                    return;
                }
                if (trimmed == null) trimmed = words;
                if (repeat != 1) {
                    input.writeError(true, "Unexpected repeat count");
                }
                addPiece(match.findPiece(curSide, Land.class, trimmed));
                return;
            }
            if (i == words.length - 3 &&
                    words[i+1].startsWith("#")
                    && words[i+2].equals(")")) {
                String factors = words[i+1].substring(1);
                words = top(words, i);
                String [] trimmed = removeDross(words);
                if (trimmed != null) words = trimmed;
                addPiece(match.findPiece(curSide, factors, words));
                return;
            }
            if (i == 2 && words[0].equalsIgnoreCase("Organic")
                    && words[1].equalsIgnoreCase("Truck")) {
                String [] dm = strip (words, i+1);
                dm = top(dm, dm.length-1);
                words = top(words, i);
                if (!dm[dm.length-1].startsWith("Div")) {
                    String [] x = new String[dm.length + 1];
                    System.arraycopy(dm, 0, x, 0, dm.length);
                    x[dm.length] = "Div";
                    dm = x;
                }
                addPiece(match.findPiece(curSide, dm, words));
                return;
            }
            String [] dn = top(words, i);
            words = strip(words, i+1);
            words = top(words, words.length - 1);
            placeDakDivision(dn, words);
            return;
        }
        super.checkForDiv(words, repeat);
        return;
    }

    void placeDakDivision(String[] divName, String[] words) {
        if (divName.length > 3
                && divName[divName.length -2].equalsIgnoreCase("inf")) {
            divName = this.remove(divName, divName.length - 2);
        }
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
            String [] unit;
            if (i < words.length) {
                unit = top(words, i);
                words = strip(words, i+1);
            } else {
                unit = words;
                words = new String[0];
            }
            if (unit.length == 0) {
                input.writeError(true, "Bad unit in division");
                return;
            }
            int repeat = 1;
            if(unit[0].endsWith("x")
                    && isNumber(unit[0].substring(0, unit[0].length()-1))) {
                repeat = Integer.parseInt(unit[0].substring(0, unit[0].length()-1));
                unit = strip(unit, 1);
            }
            if (unit.length == 0) {
                input.writeError(true, "Bad unit in division");
                return;
            }
            boolean empty = unit[unit.length-1].equals(ReadAndLogInput.EMPTY);
            boolean full = unit[unit.length-1].equals(ReadAndLogInput.LOADED);
            if (empty || full) {
                unit = top(unit, unit.length-1);
                if (unit.length == 0) {
                    input.writeError(true, "Bad unit in division");
                    return;
                }
            }
            if (unit[unit.length-1].equalsIgnoreCase("truck")
                    || unit[unit.length-1].equalsIgnoreCase("trucks")) {
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
            String [] trimmed = removeDross(unit);
            if (trimmed != null) unit = trimmed;
            if (unit[0].charAt(0) == '#') {
                String factors = unit[0].substring(1);
                unit = strip(unit,1);
                if (parens) {
                    if (repeat != 1) {
                        input.writeError(true, "Bad repeat count inside division");
                        return;
                    }
                    for (i = 0; i < unit.length; i++) {
                        if (unit[i].equals("(")) break;
                    }
                    String [] type = top(unit, i);
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

    String[] removeDross(String[] words) {
        return removeDrossAt(words, words.length - 1);
    }

    String[] removeDrossAt(String[] words, int k) {
        if (words[k].equals("Bn") || words[k].equals("Co")
                || words[k].equals("Bde")
                || words[k].equals("Battery")
                || words[k].equals("Rgt")) {
            String[] trimmed = remove(words, k);
            k--;
            if (k < 1) {
                return trimmed;
            }
            if (trimmed[k].equals("Inf")
                    || trimmed[k].equals("Arty")
                    || trimmed[k].equals("Artillery")) {
                trimmed = remove(trimmed, k);
            } else if (trimmed[k].equals("Cav")) {
                trimmed = remove(trimmed, k);
            } else if (trimmed[k].equals("Marine")) {
                trimmed = remove(trimmed, k);
            } else if (trimmed[k].equals("Commando")) {
                trimmed = remove(trimmed, k);
            } else if (trimmed[k].equals("Arm")) {
                trimmed = remove(trimmed, k);
                k--;
                if (k > 0 && trimmed[k].equals("Lt")) {
                    trimmed = remove(trimmed, k);
                }
            } else if (k > 1 && trimmed[k].equals("Car")
                    && trimmed[k - 1].equals("Arm")) {
                trimmed = remove(trimmed, k);
                trimmed = remove(trimmed, k - 1);
            }
            return trimmed;
        }
        return null;
    }
}
