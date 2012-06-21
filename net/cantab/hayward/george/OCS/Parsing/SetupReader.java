/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import VASSAL.build.GameModule;
import VASSAL.build.module.Map;
import VASSAL.build.module.map.boardPicker.Board;
import java.util.Collection;
import java.util.List;
import net.cantab.hayward.george.OCS.SetupBoard;
import net.cantab.hayward.george.OCS.Statics;

/**
 * This reads the setup section of a scenario definition and creates the
 * required pieces either on the map or in the correct setup window
 * @author george
 */
public class SetupReader extends MarkerReader {


    /**
     * Construct a setup reader
     */
    SetupReader(PieceSearcher a, LineReader b, ModuleSpecific c, int d) {
        super(a,b,c,d);
        String id = Statics.theSides[d].name + boardType();
        List<Map> ms = GameModule.getGameModule().getComponentsOf(Map.class);
        for (Map m : ms) {
            if (m.getMapName().equalsIgnoreCase(id)) {
                setupMap = m;
                Collection<Board> bs = m.getBoards();
                Object [] bt = bs.toArray();
                if ( bt.length > 0 && bt[0] instanceof SetupBoard ) {
                    if (curBoard != null) {
                        input.writeError(true, "Multiple possible boards found");
                    }
                    curBoard = (SetupBoard)bt[0];
                }
            }
        }
        if (curBoard == null) {
            input.writeError(true, "No matching board found");
        }
        allLoaded = false;
        organicLoaded = false;
    }

    /**
     * Return the type of board we are matching
     */
    String boardType() {
        return " Set Up";
    }

    /**
     * Returns true is schedule
     */
    boolean isSchedule() {
        return false;
    }

    /**
     * Parse an askerisk command line
     */
    void parseAskeriskLine(StringBuffer curLine) {
        curLine.deleteCharAt(0);
        String [] words = ReadAndLogInput.bufferToWords(curLine, null, false);
        if (words.length == 0) {
            input.writeError(true, "Invalid command");
            return;
        }
        if (isSchedule() && words[0].equals("view")) {
            words = strip(words,1);
            curBoard.makeView(merge(words, " "));
        } else if (words[0].equals("organic")) {
            if (words.length != 3 || !words[1].equalsIgnoreCase("loaded")
                    || (!words[2].equalsIgnoreCase("true")
                         && !words[2].equalsIgnoreCase("false"))) {
                input.writeError(true, "invalid organic command");
            } else {
                organicLoaded = Boolean.valueOf(words[2]);
            }
        } else if (words[0].equals("transport")) {
            if (words.length != 3 || !words[1].equalsIgnoreCase("loaded")
                    || (!words[2].equalsIgnoreCase("true")
                         && !words[2].equalsIgnoreCase("false"))) {
                input.writeError(true, "invalid transport command");
            } else {
                allLoaded = Boolean.valueOf(words[2]);
            }
        } else {
            data.moduleCommandLine(curSide, words, this);
        }
    }

    /**
     * Parse the whole setup
     */
    @Override
    void parse() {
        StringBuffer curLine;
        for (;;) {
            curLine = input.nextLine();
            if (curLine == null) break;
            if (curLine.charAt(0) == '!') break;
            if (curLine.charAt(0) == '*') {
                parseAskeriskLine(curLine);
                continue;
            }
            if (data.moduleSpecificLine(curSide, curLine, this)) continue;
            if (curLine.charAt(0) == '?') {
                curLine.deleteCharAt(0);
                parseFixedLocation(curLine);
            } else if (curLine.charAt(curLine.length() - 1) == ':') {
                String [] words = ReadAndLogInput.bufferToWords(new StringBuffer(curLine), null, false);
                int i;
                for (i = 0; i < words.length; i++) {
                    if (words[i].equalsIgnoreCase("within")
                            || words[i].equalsIgnoreCase("w/i")) break;
                }
                if (i < words.length) {
                    parseFixedLocation(curLine);
                } else {
                    parseMapLocation(curLine);
                }
            } else {
                input.writeError(true, "Line ignored");
            }
        }
        input.repeatThisLine();
        if (locations.isEmpty()) return;
        if (curBoard == null) return;
        curBoard.addMajorNote("These hexes have supply and transport in them");
        String t = null;
        int k = 0;
        for (String s : locations) {
            if (k == 4) {
                curBoard.addNote(t);
                k = 0;
                t = s;
                continue;
            }
            if (t == null) {
                t = s;
                continue;
            }
            k++;
            t += ", " + s;
        }
        if (t != null) {
            curBoard.addNote(t);
        }
        curBoard.addMajorNote("You need to decide if some or all supply starts loaded");
    }

    /**
     * Parse a fixed location
     */
    void parseFixedLocation(StringBuffer curLine) {
        locationName = null;
        if (curBoard == null) {
            input.writeError(true, "Unable to use board - not known");
            return;
        }
        if (curLine.length() == 0) {
            curBoard.addNote("");
            return;
        }
        if (curLine.charAt(curLine.length()-1) != ':') {
            if (curLine.charAt(0) != '?') {
                curBoard.addNote(curLine.toString());
            } else if (curLine.length() > 1 && curLine.charAt(1) == '?') {
                curBoard.addTurn(curLine.substring(2));
            } else {
                curBoard.addMajorNote(curLine.substring(1));
            }
            return;
        }
        curMap = setupMap;
        if (curMap != null) {
            curPoint = curBoard.addLocation(curLine.toString());
        }
        parsePieces();
        placePiecesOnMap();
    }

}
