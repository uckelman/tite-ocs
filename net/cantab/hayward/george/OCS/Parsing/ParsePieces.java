/* 
 * $Id$
 *
 * Copyright (c) 2000-2011 by Rodney Kinney, Joel Uckelman, George Hayward
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */
package net.cantab.hayward.george.OCS.Parsing;

import VASSAL.build.GameModule;
import VASSAL.build.widget.BoxWidget;
import VASSAL.build.widget.ListWidget;
import VASSAL.counters.BasicPiece;
import VASSAL.counters.Embellishment;
import VASSAL.counters.GamePiece;
import VASSAL.counters.UsePrototype;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import net.cantab.hayward.george.OCS.Counters.Airbase;
import net.cantab.hayward.george.OCS.Counters.Aircraft;
import net.cantab.hayward.george.OCS.Counters.Artillery;
import net.cantab.hayward.george.OCS.Counters.AttackCapable;
import net.cantab.hayward.george.OCS.Counters.Defensive;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.Counters.Fighter;
import net.cantab.hayward.george.OCS.Counters.GameMarker;
import net.cantab.hayward.george.OCS.Counters.HeadQuarters;
import net.cantab.hayward.george.OCS.Counters.Reserve;
import net.cantab.hayward.george.OCS.Counters.Ship;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.PieceSlotOverride;

/**
 * Parse Piece definitions from a text file and create pieces. This was TBL
 * specific code but has been generalised for all OCS modules.
 *
 * Format:
 *
 * # comment 
 * 
 * % image directory (default ./images) 
 * 
 * = side (0 or 1) 
 * 
 * ! top level 
 * 
 * ? second level
 * 
 * $+ prefix 
 * 
 * $- postfix 
 *
 * prototype 
 * 
 * -piece definition
 * 
 * position, piece definition
 *
 *
 * @author George Hayward
 */
public class ParsePieces extends LineReader {

    class PiecePosition {

        int sheet;
        int column;
        int pair;
        int row;
        int piece;

        PiecePosition(String s) {
            sheet = s.charAt(0) - '0';
            column = s.charAt(1) - 'A';
            pair = s.charAt(2) - '0';
            row = s.charAt(3) - 'a';
            piece = s.charAt(4) - '0';
        }

        void increment() {
            if (++piece < 10) return;
            piece = 0;
            if (++row < 2) return;
            row = 0;
            if (++pair < 7) return;
            pair = 0;
            if (++column < 2) return;
            column = 0;
            sheet++;
        }

        String frontFileName() {
            String s;
            s = sheet + "F-" + (column == 0 ? "A-" : "B-") + pair
                + (row == 0 ? "-a-" : "-b-") + piece + ".png";
            return s;
        }

        String backFileName() {
            String s;
            s = sheet + "B-" + (column == 0 ? "B-" : "A-") + pair
                + (row == 0 ? "-a-" : "-b-") + (9 - piece) + ".png";
            return s;
        }
    }
    PiecePosition pos = new PiecePosition("1A0a0");
    String postfix = "";
    String prefix = "";
    String top = "";
    String bottom = null;
    String directory = "./images/";
    String proto = "";
    int side = 0;
    String lastDiv = "";
    String [] curDivs = new String[100];

    public ParsePieces(File f) {
        super(f);
        StringBuffer line;
        for (;;) {
            line = readPhysicalLine(false, true);
            if (line == null) break;
            if (line.charAt(0) == '#') {
                continue;
            } else if (line.charAt(0) == '%') {
                line.deleteCharAt(0);
                directory = line.toString() + "/";
                continue;
            } else if (line.charAt(0) == '=') {
                side = (line.length() == 1 || line.charAt(1) == '0') ? 0 : 1;
                continue;
            } else if (line.charAt(0) == '!') {
                line.deleteCharAt(0);
                top = line.toString();
                bottom = null;
                continue;
            } else if (line.charAt(0) == '?') {
                line.deleteCharAt(0);
                bottom = (line.length() == 0) ? null : line.toString();
                continue;
            } else if (line.charAt(0) == '*') {
                line.deleteCharAt(0);
                proto = line.toString();
                continue;
            } else if (line.charAt(0) == '$') {
                line.deleteCharAt(0);
                if (line.length() == 0) {
                    prefix = "";
                    postfix = "";
                } else {
                    if (line.charAt(0) == '+') {
                        prefix = line.substring(1);
                    } else {
                        postfix = line.substring(1);
                    }
                }
                continue;
            }
            int i;
            if (line.charAt(0) == '-') {
                pos.increment();
                line.deleteCharAt(0);
            } else {
                i = line.indexOf(",");
                if (i < 0) {
                    writeError(true, "Bad line: " + line);
                    continue;
                }
                pos = new PiecePosition(line.substring(0, i));
                line.delete(0, i + 1);
            }
            /*
             * Piece definition type,name[,factors[,division]]
             */
            i = line.indexOf(",");
            if (i < 0) {
                writeError(true, "Bad line: " + line);
                continue;
            }
            String type = line.substring(0, i);
            line.delete(0, i + 1);
            i = line.indexOf(",");
            String name = i >= 0 ? line.substring(0, i) : line.toString();
            String factors = "";
            String division = "";
            if (i >= 0) {
                line.delete(0, i + 1);
                i = line.indexOf(",");
                factors = i >= 0 ? line.substring(0, i) : line.toString();
                if (i >= 0) {
                    line.delete(0, i + 1);
                    if (line.indexOf(",") >= 0) {
                        writeError(true, "Bad line: " + line);
                        continue;
                    }
                    division = line.toString();
                }
            }
            if (type.equals("ac") || type.equals("do") || type.equals("art") || type.equals("brk")) {
                if (factors.equals("")) {
                    writeError(true, "Factors must be present for ac or do types");
                } else {
                    if (type.equals("do")) {
                        Matcher A = factorsB.matcher(factors);
                        if (!A.lookingAt()) {
                            writeError(true, "Invalid factors for defensive unit:" + factors);
                        }
                    } else {
                        Matcher A = factorsA.matcher(factors);
                        if (!A.lookingAt()) {
                            writeError(true, "Invalid factors for attack capable unit or artillery or breakdown:" + factors);
                        }
                    }
                }
            } else {
                if (!factors.equals("")) {
                    writeError(false, "Factors not expected for type: " + type);
                }
            }
            if (!division.equals("")) {
                if (type.equals("div")) {
                    for (i = 0; i < 100; i++) {
                        if (curDivs[i] == null) break;
                        if (division.equals(curDivs[i])) {
                            writeError(true, "Division code already used");
                            break;
                        }
                    }
                    curDivs[i] = division;
                    lastDiv = division;
                } else {
                    if (!division.equals(lastDiv)) {
                        writeError(true, "Not the expected division code");
                    }
                }
            } else {
                if (type.equals("div")) {
                    writeError(false, "Expected division code for div type");
                }
                lastDiv = "";
            }
            String atop = top;
            String abottom = bottom;
            String aproto = proto;
            if (type.equals("brk")) {
                type = "ac";
                atop = "Breakdowns";
                abottom = null;
                String aname = "Breakdown " + factors + " " + postfix;
                if (!name.equals("")) {
                    aname = aname + " " + name;
                }
                name = aname;
            }
//            if (type.equals("org") || type.startsWith("trans")) {
//                name += " " + place.substring(0, 2);
//                aplace = "Transport";
//            } else if (type.equals("strip")) {
//                aplace = "Airstrips/Flotilla";
//            } else if (type.equals("flotilla")) {
//                aplace = "Airstrips/Flotilla";
//            } else if (type.equals("f") && name.equals("Spit.I")) {
//                aplace = "Airstrips/Flotilla";
//            }
            GamePiece g;
            if (type.startsWith("trans")) {
                String[] images = new String[6];
                int found = 0;
                int j;
                for (j = 5; j < type.length(); j++) {
                    switch (type.charAt(j)) {
                    case '5':
                        if ((found & 4) != 0) {
                            writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 4;
                        images[4] = pos.frontFileName();
                        images[5] = pos.backFileName();
                        break;
                    case 'e':
                        if ((found & 4) != 0) {
                            writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 4;
                        images[4] = pos.backFileName();
                        images[5] = pos.frontFileName();
                        break;
                    case '3':
                        if ((found & 2) != 0) {
                            writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 2;
                        images[2] = pos.frontFileName();
                        images[3] = pos.backFileName();
                        break;
                    case '4':
                        if ((found & 2) != 0) {
                            writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 2;
                        images[2] = pos.backFileName();
                        images[3] = pos.frontFileName();
                        break;
                    case '1':
                        if ((found & 1) != 0) {
                            writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 1;
                        images[0] = pos.frontFileName();
                        images[1] = pos.backFileName();
                        break;
                    case '2':
                        if ((found & 1) != 0) {
                            writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 1;
                        images[0] = pos.backFileName();
                        images[1] = pos.frontFileName();
                        break;
                    case '-':
                        break;
                    default:
                        writeError(true, "Invalid transport images: " + type);
                        found = -1;
                        break;
                    }
                    if (found == -1) break;
                    pos.increment();
                }
                if (found == -1) continue;
                if (found != 1 && found != 3 && found != 7) {
                    writeError(true, "Invalid transport images: " + type);
                    continue;
                }
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + images[0], images[0]);
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + images[1], images[1]);
                if (found != 1) {
                    GameModule.getGameModule()
                        .getArchiveWriter()
                        .addImage(directory + images[2], images[2]);
                    GameModule.getGameModule()
                        .getArchiveWriter()
                        .addImage(directory + images[3], images[3]);
                    if (found != 3) {
                        GameModule.getGameModule()
                            .getArchiveWriter()
                            .addImage(directory + images[4], images[4]);
                        GameModule.getGameModule()
                            .getArchiveWriter()
                            .addImage(directory + images[5], images[5]);
                    }
                }
                g = new BasicPiece(";\0;\0;" + images[0] + ";" + name);
                if (found == 1) {
                    g = new Embellishment(Embellishment.ID + ";;;Increase;" + InputEvent.CTRL_MASK + ";A;Decrease;" + InputEvent.CTRL_MASK + ";Z;;;;false;0;0;,"
                                          + images[1] + ";,;true;;;;false;;", g);
                } else if (found == 3) {
                    g = new Embellishment(Embellishment.ID + ";;;Increase;" + InputEvent.CTRL_MASK + ";A;Decrease;" + InputEvent.CTRL_MASK + ";Z;;;;false;0;0;,"
                                          + images[1] + "," + images[2] + "," + images[3] + ";,,,;true;;;;false;;", g);
                } else {
                    g = new Embellishment(Embellishment.ID + ";;;Increase;" + InputEvent.CTRL_MASK + ";A;Decrease;" + InputEvent.CTRL_MASK + ";Z;;;;false;0;0;,"
                                          + images[1] + "," + images[2] + "," + images[3] + "," + images[4] + "," + images[5] + ";,,,,,;true;;;;false;;", g);
                }
                type = "org";
            } else if (type.equals("res")) {
                atop = "Reserve Markers";
                abottom = null;
                boolean back = name.length() > 0 && name.charAt(0) == 'b';
                name = prefix + "Reserve " + postfix;
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + (back ? pos.backFileName() : pos.frontFileName()), back ? pos.backFileName() : pos.frontFileName());
                g = new BasicPiece(";\0;\0;" + (back ? pos.backFileName() : pos.frontFileName()) + ";" + name);
            } else if (type.equals("strip")) {
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + pos.frontFileName(), pos.frontFileName());
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + pos.backFileName(), pos.backFileName());
                g = new BasicPiece(";\0;\0;" + pos.backFileName() + ";" + name);
                g = new Embellishment(Embellishment.ID + "Flip;" + InputEvent.CTRL_MASK + ";F;;;;;;;;;;false;0;0;"
                                      + pos.frontFileName() + ";;false;;;;false;;", g);
            } else if (type.equals("flotilla")) {
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + pos.frontFileName(), pos.frontFileName());
                g = new BasicPiece(";\0;\0;" + (pos.frontFileName()) + ";" + name);
            } else {
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + pos.frontFileName(), pos.frontFileName());
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage(directory + pos.backFileName(), pos.backFileName());
                g = new BasicPiece(";\0;\0;" + pos.frontFileName() + ";" + prefix + name + postfix);
                g = new Embellishment(Embellishment.ID + "Flip;" + InputEvent.CTRL_MASK + ";F;;;;;;;;;;false;0;0;"
                                      + pos.backFileName() + ";;false;;;;false;;", g);
            }
            g = new UsePrototype("prototype;" + aproto, g);
            if (type.equals("ac")) {
                g = new AttackCapable(";" + side + ";" + factors + ";" + division, g);
            } else if (type.equals("do")) {
                g = new Defensive(";" + side + ";" + factors + ";" + division, g);
            } else if (type.equals("div")) {
                g = new Division(";" + side + ";" + division, g);
            } else if (type.equals("hq")) {
                g = new HeadQuarters(";" + side, g);
            } else if (type.equals("a")) {
                g = new Aircraft(";" + side, g);
            } else if (type.equals("f")) {
                g = new Fighter(";" + side, g);
            } else if (type.equals("art")) {
                g = new Artillery(";" + side + ";" + factors + ";" + division, g);
            } else if (type.equals("org")) {
                g = new Transport(";" + side + ";" + division, g);
            } else if (type.equals("res")) {
                g = new Reserve(";" + side, g);
            } else if (type.equals("ship")) {
                g = new Ship(";" + side, g);
            } else if (type.equals("gm")) {
                g = new GameMarker(";", g);
            } else if (type.equals("strip") || type.equals("flotilla")) {
                g = new Airbase(";-1", g);
            } else {
                writeError(true, "Unknown type: " + type);
            }
            List<ListWidget> pr = null;
            if (abottom == null) {
                pr = GameModule.getGameModule().getAllDescendantComponentsOf(ListWidget.class);
            } else {
                List<BoxWidget> tr = GameModule.getGameModule().getAllDescendantComponentsOf(BoxWidget.class);
                tr = tr.get(0).getAllDescendantComponentsOf(BoxWidget.class);
                for (BoxWidget trx : tr) {
                    if (trx.getAttributeValueString("entryName").equals(atop)) {
                        pr = trx.getAllDescendantComponentsOf(ListWidget.class);
                        break;
                    }
                }
                if (pr == null) {
                    writeError(true, "Unknown top level: " + atop);
                    continue;
                }
                atop = abottom;
            }
            boolean found = false;
            for (ListWidget l : pr) {
                if (l.getAttributeValueString("entryName").equals(atop)) {
                    PieceSlotOverride fred = new PieceSlotOverride();
                    fred.setPiece(g);
                    fred.updateGpId(GameModule.getGameModule());
                    l.add(fred);
                    found = true;
                    break;
                }
            }
            if (!found) {
                writeError(true, "Scrollable list not found: " + atop);
            }
        }
        try {
            theOutput.close();
        } catch (IOException e) {
        }
    }
}
