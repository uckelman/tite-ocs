/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author george
 */
public class CaseBlueReader extends ScheduleReader {
    
    static final int GBII = 0;
    
    static final int EATG = 1;
    
    static final int CB = 2;

    static final int ALL = 3;
    
    boolean [] game = new boolean[4];

    boolean noTrans = false;

    CaseBlueReader(PieceSearcher a, LineReader b, ModuleSpecific c, int d, String[] types) {
        super(a, b, c, d);
        for (String s : types) {
            if (s.equals("GBII")) {
                game[GBII] = true;
            } else if (s.equals("EATG")) {
                game[EATG] = true;
            } else if (s.equals("CB")) {
                game[CB] = true;
            } else if (s.equals("Notrans")) {
                noTrans = true;
            } else {
                input.writeError(true, "Unknown game type");
            }
        }
        if (!(game[GBII] || game[EATG] || game[CB])) {
            input.writeError(true, "No game type set!!");
        }
        game[ALL] = true;
    }

    String oldMonthTitle;

    String monthTitle;

    String comment;

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
            if (!isMonthLine(curLine)) {
                input.writeError(true, "Line ignored");
                continue;
            }
            oldMonthTitle = monthTitle;
            curBoard.addTurn(monthTitle);
            if (!comment.equals("")) {
                curBoard.addNote(comment);
            }
            curLine = input.nextLine();
            if (curLine.length() > 11 && curLine.substring(0, 10).equalsIgnoreCase("historical")) {
                curBoard.addNote(curLine.toString());
                curLine = input.nextLine();
            }
            if (curLine.length() > 1 && curLine.charAt(0) == '•') {
                curBoard.addNote(curLine.toString());
            } else {
                input.repeatThisLine();
            }
            parseMonth();
            curBoard.makeView(oldMonthTitle);
        }
        curBoard.addFinalSpace();
        input.repeatThisLine();
    }
    String dayHeader;

    void parseMonth() {
        StringBuffer curLine;
        for (;;) {
            curLine = input.nextLine();
            if (curLine == null) {
                break;
            }
            if (curLine.charAt(0) == '!') {
                break;
            }
            if (isMonthLine(curLine)) break;
            if (!isDayLine(curLine)) {
                input.writeError(true, "Line ignored");
                continue;
            }
            curBoard.addMajorNote(dayHeader);
            parseSection();
        }
        input.repeatThisLine();
    }

    void parseSection() {
        StringBuffer curLine;
        for (;;) {
            curLine = input.nextLine();
            if (curLine == null) {
                break;
            }
            if (curLine.charAt(0) == '!') {
                break;
            }
            if (isMonthLine(curLine)) break;
            if (isDayLine(curLine)) break;
            if (!isSectionLine(curLine)) {
                input.writeError(true, "Line ignored");
                continue;
            }
            if (curLine.length() > 11 && curLine.substring(0, 11).equals("Aufklärungs")) {
                for (;;) {
                    curBoard.addNote(curLine.toString().trim());
                    curLine = input.nextLine();
                    if (curLine == null) {
                        break;
                    }
                    if (curLine.charAt(0) == '!') {
                        break;
                    }
                    if (isMonthLine(curLine)) break;
                }
                break;
            }
            if (curLine.substring(0, 4).equals("Note")) {
                if (!game[CB] && game[EATG]) {
                    input.writeError(false, "Section processed: " + curLine.toString());
                    curBoard.addMajorNote(data.toString());
                } else {
                    input.writeError(false, "Section skipped: " + curLine.toString());
                }
                continue;
            }
            data = curLine;
            int savedIn = in;
            int savedOut = out;
            collectData();
            int k = data.length();
            if (k > 30) k = 30;
            if ((savedIn < 0 && !game[savedOut]) || (savedOut < 0 && !game[savedIn])
                    || (savedIn >= 0 && savedOut >=0 && game[savedIn] == game[savedOut])
                    || (noTrans && ((in == GBII && out == EATG) || (in == EATG && out == GBII)))) {

                input.writeError(false, "Section skipped: " + data.substring(0, k));
                continue;
            }
            items.clear();
            for (int i = 0; i < data.length(); i++) {
                if (data.charAt(i) == ':') {
                    if (!specialExchangeProcessing(i)) {
                        if (i > 7 && data.substring(i - 7, i).equals("Replace")) {
                            for (; i < data.length() - 4; i++) {
                                if (data.substring(i, i + 4).equals("with")) {
                                    i = i + 4;
                                    break;
                                }
                            }
                            if (i > data.length() - 5) {
                                input.writeError(true, "Bad replace");
                                return;
                            }
                        }
                        leader = data.substring(0, i + 1);
                        int depth = 0;
                        for (int j = i + 1; j < data.length(); j++) {
                            if (data.charAt(j) == '(') {
                                depth++;
                                continue;
                            }
                            if (data.charAt(j) == ')') {
                                depth--;
                                continue;
                            }
                            if (depth != 0) continue;
                            if (data.charAt(j) == ',') {
                                items.add(new StringBuffer (data.substring(i + 1, j)));
                                i = j;
                            }
                        }
                        if (depth != 0) {
                            input.writeError(true, "Parentheses mismatch");
                            return;
                        }
                        items.add(new StringBuffer (data.substring(i + 1)));
                        }
                    break;
                }
            }
            input.writeError(false, "Section processed: " + leader);
            for (StringBuffer c : items) {
                input.writeError(false, "item: " + c.toString());
            }
            if (savedIn >= 0 && game[savedIn]  && !allSpecialDead()) {
                curMap = setupMap;
                curPoint = curBoard.addLocation(leader);
                for (StringBuffer b : items) {
                    if (b.toString().endsWith("(if available in dead pile)")) {
                        curBoard.addNote(b.toString().trim());
                    } else {
                        parsePieces(b);
                    }
                }
                placePiecesOnMap();
            } else {
                curBoard.addMajorNote(leader);
                for (StringBuffer b : items) {
                    curBoard.addNote(b.toString().trim());
                }
            }
        }
        input.repeatThisLine();
    }

    boolean allSpecialDead() {
        for (StringBuffer b : items) {
            if (!b.toString().endsWith("(if available in dead pile)")) {
                return false;
            }
        }
        return true;
    }

    boolean specialExchangeProcessing(int i) {
        if (i > 8 && data.substring(i - 8, i).equals("Exchange")) {
            if (curSide == 0) {
                if (data.substring(i + 2, i + 5).equals("LAH")) {
                    int j = data.indexOf("for", i);
                    leader = data.substring(0, i) + " for " + data.substring(i + 1, i + 14)
                            + data.substring(j + 4) + ":";
                    items.add(new StringBuffer(data.substring(i + 1, j) + ")"));
                } else if (data.substring(i + 2, i + 7).equals("1 Rum")) {
                    int j = data.indexOf("with", i);
                    leader = data.substring(0, j + 4) + ":";
                    items.add(new StringBuffer(data.substring(j + 4)));
                } else if (data.substring(i + 2, i + 4).equals("5-")
                        || data.substring(i + 2, i + 4).equals("GD")) {
                    leader = data.substring(0, i);
                    int j = data.indexOf("for", i);
                    leader += " " + data.substring(j) + ":";
                    items.add(new StringBuffer(data.substring(i + 1, j)));
                } else {
                    int j = data.indexOf("omes", i);
                    leader = data.substring(0, j + 4) + ":";
                    items.add(new StringBuffer(data.substring(j + 4)));
                }
            } else {
                if (!data.substring(i - 14, i -9).equals("Corps")) {
                    return false;
                }
                leader = data.substring(0, i);
                int j = data.indexOf("for", i);
                leader += " " + data.substring(j) + ":";
                items.add(new StringBuffer(data.substring(i + 1, j)));
            }
            return true;
        }
        return false;
    }

    int in;
    int out;
    String leader;
    StringBuffer data;
    List<StringBuffer> items = new ArrayList<StringBuffer> ();

    public void collectData() {
        StringBuffer curLine;
        for (;;) {
            curLine = input.nextLine();
            if (curLine == null) {
                break;
            }
            if (curLine.charAt(0) == '!') {
                break;
            }
            if (isMonthLine(curLine)) break;
            if (isDayLine(curLine)) break;
            if (isSectionLine(curLine)) break;
            data.append(curLine);
        }
        input.repeatThisLine();
    }

    boolean isSectionLine(StringBuffer b) {
        in = -1;
        out = -1;
        if (b.length() > 11 && b.substring(0, 11).equals("Aufklärungs")) {
            return true;
        }
        if (b.length() > 4 && b.substring(0, 4).equals("Note")) {
            return true;
        }
        if (b.length() > 11 && b.substring(0, 11).equals("GBII—Remove")) {
            out = GBII;
        } else if (b.length() > 11 && b.substring(0, 11).equals("EatG—Remove")) {
            out = EATG;
        } else if (b.length() > 23 && b.substring(0, 23).equals("EatG—Convert into Repls")) {
            out = EATG;
        } else if (b.length() > 9 && b.substring(0, 9).equals("CB—Remove")) {
            out = CB;
        } else if (b.length() > 6 && b.substring(0, 6).equals("Remove")) {
            out = ALL;
        } else if (b.length() > 10 && b.substring(0, 10).equals("Any—Remove")) {
            out = ALL;
        } else if (b.length() > 12 && b.substring(0, 12).equals("GBII—If not ")) {
            out = GBII;
        } else if (b.length() > 12 && b.substring(0, 12).equals("GBII—Disband")) {
            out = GBII;
        } else if (b.length() > 12 && b.substring(0, 12).equals("EatG—Disband")) {
            out = EATG;
        } else if (b.length() > 10 && b.substring(0, 10).equals("CB—Disband")) {
            out = CB;
        } else if (b.length() > 4 && b.substring(0, 4).equals("GBII")) {
            in = GBII;
        } else if (b.length() > 4 && b.substring(0, 4).equals("EatG")) {
            in = EATG;
        } else if (b.length() > 2 && b.substring(0, 2).equals("CB")) {
            in = CB;
        } else if (b.length() > 14 && b.substring(0, 14).equals("Transfer from ")) {
            int i = 14;
            if (b.length() > i + 4 && b.substring(i, i+4).equals("GBII")) {
                out = GBII;
                i += 4;
            } else if (b.length() > i + 4 && b.substring(i, i+4).equals("EatG")) {
                out = EATG;
                i += 4;
            } else if (b.length() > i + 9 && b.substring(i, i+9).equals("Case Blue")) {
                out = CB;
                i += 9;
            } else {
                return false;
            }
            if (b.length() > i + 4 && b.substring(i, i+4).equals(" to ")) {
                i += 4;
                if (b.length() > i + 4 && b.substring(i, i+4).equals("GBII")) {
                    in = GBII;
                    i += 4;
                } else if (b.length() > i + 4 && b.substring(i, i+4).equals("EatG")) {
                    in = EATG;
                    i += 4;
                } else if (b.length() > i + 9 && b.substring(i, i+9).equals("Case Blue")) {
                    in = CB;
                    i += 9;
                } else {
                    return false;
                }
            } else if (b.length() > i + 29 && b.substring(i, i +29).equals(" Emergency Reinforcements to ")) {
                i += 29;
                if (b.length() > i + 4 && b.substring(i, i+4).equals("GBII")) {
                    in = GBII;
                    i += 4;
                } else if (b.length() > i + 4 && b.substring(i, i+4).equals("EatG")) {
                    in = EATG;
                    i += 4;
                } else if (b.length() > i + 9 && b.substring(i, i+9).equals("Case Blue")) {
                    in = CB;
                    i += 9;
                } else {
                    return false;
                }
            } else if (b.length() > i + 53 && b.substring(i, i + 53).equals(" (release from Emergency Reserves if still there) to ")) {
                i += 53;
                if (b.length() > i + 4 && b.substring(i, i+4).equals("GBII")) {
                    in = GBII;
                    i += 4;
                } else if (b.length() > i + 4 && b.substring(i, i+4).equals("EatG")) {
                    in = EATG;
                    i += 4;
                } else if (b.length() > i + 9 && b.substring(i, i+9).equals("Case Blue")) {
                    in = CB;
                    i += 9;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    boolean isDayLine(StringBuffer b) {
        if (b.charAt(b.length() -1) != '—') return false;
        String a = b.substring(0, b.length() - 1);
        if (!isNumber(a)) return false;
        dayHeader = b.toString();
        return true;
    }

    boolean isMonthLine(StringBuffer b) {
        if (b.length() < 8) return false;
        String c = b.substring(0, 3);
        int curMonth;
        if (c.equals("Jan")) {
            curMonth = 1;
        } else if (c.equals("Feb")) {
            curMonth = 2;
        } else if (c.equals("Mar")) {
            curMonth = 3;
        } else if (c.equals("Apr")) {
            curMonth = 4;
        } else if (c.equals("May")) {
            curMonth = 5;
        } else if (c.equals("Jun")) {
            curMonth = 6;
        } else if (c.equals("Jul")) {
            curMonth = 7;
        } else if (c.equals("Aug")) {
            curMonth = 8;
        } else if (c.equals("Sep")) {
            curMonth = 9;
        } else if (c.equals("Oct")) {
            curMonth = 10;
        } else if (c.equals("Nov")) {
            curMonth = 11;
        } else if (c.equals("Dec")) {
            curMonth = 12;
        } else {
            return false;
        }
        String d;
        String e;
        if (curMonth == 9) {
            if (b.charAt(3) != 't' && b.charAt(4) != ' ') return false;
            if (b.length() < 9) return false;
            e = b.substring(5, 9);
            d = b.substring(9);
            c += "t";
        } else if (curMonth == 3) {
            if (b.charAt(3) != 'c' && b.charAt(4) != 'h' && b.charAt(5) != ' ') return false;
            if (b.length() < 10) return false;
            e = b.substring(6, 10);
            d = b.substring(10);
            c += "ch";
        } else if (curMonth == 4) {
            if (b.charAt(3) != 'i' && b.charAt(4) != 'l' && b.charAt(5) != ' ') return false;
            if (b.length() < 10) return false;
            e = b.substring(6, 10);
            d = b.substring(10);
            c += "il";
        } else if (curMonth == 6) {
            if (b.charAt(3) != 'e' && b.charAt(4) != ' ') return false;
            if (b.length() < 9) return false;
            e = b.substring(5, 9);
            d = b.substring(9);
            c += "e";
        } else if (curMonth == 7) {
            if (b.charAt(3) != 'y' && b.charAt(4) != ' ') return false;
            if (b.length() < 9) return false;
            e = b.substring(5, 9);
            d = b.substring(9);
            c += "y";
        } else {
            if (b.charAt(3) != ' ') return false;
            e = b.substring(4, 8);
            d = b.substring(8);
        }
        int curYear;
        if (!isNumber(e)) return false;
        curYear = Integer.parseInt(e);
        if (curYear < 1941 || curYear > 1943) return false;
        monthTitle = c + " " + e;
        comment = d.trim();
        return true;
    }

    @Override
    String [] removeDrossAt(String[] a, int b) {
        return null;
    }

    @Override
    String [] removeDross(String[] a) {
        return null;
    }


}
