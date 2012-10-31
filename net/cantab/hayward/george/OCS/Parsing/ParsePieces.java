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
import VASSAL.build.widget.ListWidget;
import VASSAL.counters.BasicPiece;
import VASSAL.counters.Embellishment;
import VASSAL.counters.GamePiece;
import VASSAL.counters.UsePrototype;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import net.cantab.hayward.george.OCS.Counters.Airbase;
import net.cantab.hayward.george.OCS.Counters.Aircraft;
import net.cantab.hayward.george.OCS.Counters.Artillery;
import net.cantab.hayward.george.OCS.Counters.AttackCapable;
import net.cantab.hayward.george.OCS.Counters.Defensive;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.Counters.Fighter;
import net.cantab.hayward.george.OCS.Counters.HeadQuarters;
import net.cantab.hayward.george.OCS.Counters.Reserve;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.PieceSlotOverride;

/**
 * Parse Piece definitions from a text file and create pieces. This is TBL
 * specific code.
 *
 * @author George Hayward
 */
public class ParsePieces {

    LineReader input;

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
    String proto = "german";
    String place = "German";
    int side = 0;

    public ParsePieces(File f) {
        input = new LineReader(f);
        StringBuffer line;
        for (;;) {
            line = input.nextLine();
            if (line == null) break;
            if (line.charAt(0) == '!') {
                continue;
            }
            if (line.charAt(0) == '+') {
                if (line.substring(1).equals("ge")) {
                    proto = "german";
                    place = "German";
                    side = 0;
                } else if (line.substring(1).equals("be")) {
                    proto = "belgian";
                    place = "Belgian";
                    side = 1;
                } else if (line.substring(1).equals("br")) {
                    proto = "british";
                    place = "British";
                    side = 1;
                } else if (line.substring(1).equals("fr")) {
                    proto = "french";
                    place = "French";
                    side = 1;
                } else if (line.substring(1).equals("du")) {
                    proto = "dutch";
                    place = "Dutch";
                    side = 1;
                } else {
                    input.writeError(true, "Unknown nationality: " + line);
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
                    input.writeError(true, "Bad line: " + line);
                    continue;
                }
                pos = new PiecePosition(line.substring(0, i));
                line.delete(0, i + 1);
            }
            i = line.indexOf(",");
            if (i < 0) {
                input.writeError(true, "Bad line: " + line);
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
                        input.writeError(true, "Bad line: " + line);
                        continue;
                    }
                    division = line.toString();
                }
            }
            String aplace = place;
            String aproto = proto;
            if (type.equals("brk")) {
                type = "ac";
                aplace = "Breakdowns";
                String aname = "Breakdown " + factors + " " + place.substring(0, 2);
                if (!name.equals("")) {
                    aname = aname + " " + name;
                }
                name = aname;
            }
            if (type.equals("org") || type.startsWith("trans")) {
                name += " " + place.substring(0, 2);
                aplace = "Transport";
            } else if (type.equals("strip")) {
                aplace = "Airstrips/Flotilla";
            } else if (type.equals("flotilla")) {
                aplace = "Airstrips/Flotilla";
            } else if (type.equals("f") && name.equals("Spit.I")) {
                aplace = "Airstrips/Flotilla";
            }
            GamePiece g;
            if (type.startsWith("trans")) {
                String[] images = new String[6];
                int found = 0;
                int j;
                for (j = 5; j < type.length(); j++) {
                    switch (type.charAt(j)) {
                    case '5':
                        if ((found & 4) != 0) {
                            input.writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 4;
                        images[4] = pos.frontFileName();
                        images[5] = pos.backFileName();
                        break;
                    case 'e':
                        if ((found & 4) != 0) {
                            input.writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 4;
                        images[4] = pos.backFileName();
                        images[5] = pos.frontFileName();
                        break;
                    case '3':
                        if ((found & 2) != 0) {
                            input.writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 2;
                        images[2] = pos.frontFileName();
                        images[3] = pos.backFileName();
                        break;
                    case '4':
                        if ((found & 2) != 0) {
                            input.writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 2;
                        images[2] = pos.backFileName();
                        images[3] = pos.frontFileName();
                        break;
                    case '1':
                        if ((found & 1) != 0) {
                            input.writeError(true, "Duplicate transport images: " + type);
                            found = -1;
                            break;
                        }
                        found |= 1;
                        images[0] = pos.frontFileName();
                        images[1] = pos.backFileName();
                        break;
                    case '2':
                        if ((found & 1) != 0) {
                            input.writeError(true, "Duplicate transport images: " + type);
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
                        input.writeError(true, "Invalid transport images: " + type);
                        found = -1;
                        break;
                    }
                    if (found == -1) break;
                    pos.increment();
                }
                if (found == -1) continue;
                if (found != 1 && found != 3 && found != 7) {
                    input.writeError(true, "Invalid transport images: " + type);
                    continue;
                }
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + images[0], images[0]);
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + images[1], images[1]);
                if (found != 1) {
                    GameModule.getGameModule()
                        .getArchiveWriter()
                        .addImage("./200dpi/images/" + images[2], images[2]);
                    GameModule.getGameModule()
                        .getArchiveWriter()
                        .addImage("./200dpi/images/" + images[3], images[3]);
                    if (found != 3) {
                        GameModule.getGameModule()
                            .getArchiveWriter()
                            .addImage("./200dpi/images/" + images[4], images[4]);
                        GameModule.getGameModule()
                            .getArchiveWriter()
                            .addImage("./200dpi/images/" + images[5], images[5]);
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
                aplace = "Reserve Markers";
                boolean back = name.length() > 0 && name.charAt(0) == 'b';
                name = "Reserve " + place.substring(0, 2);
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + (back ? pos.backFileName() : pos.frontFileName()), back ? pos.backFileName() : pos.frontFileName());
                g = new BasicPiece(";\0;\0;" + (back ? pos.backFileName() : pos.frontFileName()) + ";" + name);
            } else if (type.equals("strip")) {
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + pos.frontFileName(), pos.frontFileName());
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + pos.backFileName(), pos.backFileName());
                g = new BasicPiece(";\0;\0;" + pos.backFileName() + ";" + name);
                g = new Embellishment(Embellishment.ID + "Flip;" + InputEvent.CTRL_MASK + ";F;;;;;;;;;;false;0;0;"
                                      + pos.frontFileName() + ";;false;;;;false;;", g);
            } else if (type.equals("flotilla")) {
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + pos.frontFileName(), pos.frontFileName());
                g = new BasicPiece(";\0;\0;" + (pos.frontFileName()) + ";" + name);
            } else {
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + pos.frontFileName(), pos.frontFileName());
                GameModule.getGameModule()
                    .getArchiveWriter()
                    .addImage("./200dpi/images/" + pos.backFileName(), pos.backFileName());
                g = new BasicPiece(";\0;\0;" + pos.frontFileName() + ";" + name);
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
                g = new Transport(";" + side + ";;" + division, g);
            } else if (type.equals("res")) {
                g = new Reserve(";" + side, g);
            } else if (type.equals("strip") || type.equals("flotilla")) {
                g = new Airbase(";-1", g);
            } else {
                input.writeError(true, "Unknown type: " + type);
            }
            List<ListWidget> pr = GameModule.getGameModule().getAllDescendantComponentsOf(ListWidget.class);
            for (ListWidget l : pr) {
                if (l.getAttributeValueString("entryName").equals(aplace)) {
                    PieceSlotOverride fred = new PieceSlotOverride();
                    fred.setPiece(g);
                    fred.updateGpId(GameModule.getGameModule());
                    l.add(fred);
                }
            }
        }
        try {
            input.theOutput.close();
        } catch (IOException e) {
        }
    }
}
