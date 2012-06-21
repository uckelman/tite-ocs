/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import VASSAL.build.GameModule;
import VASSAL.build.module.Map;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.MapGrid;
import VASSAL.build.module.map.boardPicker.board.MapGrid.BadCoords;
import VASSAL.build.module.map.boardPicker.board.SquareGrid;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceCloner;
import VASSAL.counters.Properties;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cantab.hayward.george.OCS.SetupBoard;
import net.cantab.hayward.george.OCS.StackOverride;
import net.cantab.hayward.george.OCS.Statics;

/**
 *
 * @author george
 */
public class MarkerReader extends PieceReader {

    /**
     * initialise reader
     */
    MarkerReader(PieceSearcher a, LineReader b, ModuleSpecific c, int d) {
        super(a, b, c, d);
    }

    /**
     * Parse the whole setup
     */
    void parse() {
        StringBuffer curLine;
        for (;;) {
            curLine = input.nextLine();
            if (curLine == null) break;
            if (curLine.charAt(0) == '!') break;
            if (curLine.charAt(0) == '*') {
                curLine.deleteCharAt(0);
                String [] words = ReadAndLogInput.bufferToWords(curLine, null, false);
                data.moduleCommandLine(curSide, words, this);
            } else if (curLine.charAt(curLine.length() - 1) == ':') {
                parseMapLocation(curLine);
            } else {
                input.writeError(true, "Line ignored");
            }
        }
        input.repeatThisLine();
    }

    Pattern hexRef = Pattern.compile("(.+?)(\\d+)\\.(\\d+)");
    Pattern hexRefCB = Pattern.compile("(.+?)(\\d+)\\.(\\d+)\\)*");

    /**
     * Parse a map location
     */
    void parseMapLocation(StringBuffer curLine) {
        locationName = null;
        char [] specials = new char [] {':', ',', '@', '/'};
        String[] words = ReadAndLogInput.bufferToWords(curLine, specials,
                                                       false);
        Matcher m = hexRef.matcher(words[0]);
        if (words.length == 2 && m.matches()) {
            String mapRef = m.group(1);
            int row = Integer.parseInt(m.group(2));
            int col = Integer.parseInt(m.group(3));
            if (resolveMapLocation(mapRef, row, col)) return;
            locationName = words[0];
            transportPlaced = false;
            supplyPlaced = false;
        } else if (m.matches() && words.length== 4 && Statics.theStatics.isCaseBlue()
                && (words[1].equals("(") || words[1].equals("/"))) {
            Matcher n = hexRefCB.matcher(words[2]);
            if (!n.matches()) {
                input.writeError(true, "Expected double location");
                return;
            }
            String mapRef = m.group(1);
            int row = Integer.parseInt(m.group(2));
            int col = Integer.parseInt(m.group(3));
            if (resolveMapLocation(mapRef, row, col)) return;
            Point p = curPoint;
            mapRef = n.group(1);
            row = Integer.parseInt(n.group(2));
            col = Integer.parseInt(n.group(3));
            if (resolveMapLocation(mapRef, row, col)) return;
            if (p.distance(curPoint) > 15) {
                input.writeError(true, "Locations are not the same hex?");
                return;
            }
            locationName = words[0];
            transportPlaced = false;
            supplyPlaced = false;
        } else {
            int i;
            words = top(words, words.length - 1);
            int j = words.length;
            for (i = 0; i < j; i++) {
                if (words[i].equals("@")) break;
            }
            if (i < j) {
                if (i == 0 || i == j-1) {
                    input.writeError(true, "Bad '@' syntax");
                    return;
                }
                String [] loc = tail(words, j - 1 - i);
                words = top(words, i);
                if (resolveLocationInBox(merge(words, " "), merge(loc, " "))) return;
            } else {
                Point within =null;
                if (j > 3) {
                    if (isNumber(words[j-1]) && words[j-2].equals(",") && isNumber(words[j-3])) {
                        within = new Point(Integer.parseInt(words[j-3]), Integer.parseInt(words[j-1]));
                        words = top(words, words.length - 3);
                    }
                }
                if (resolveBoxLocation(merge(words, " "), within)) return;
            }
        }
        parsePieces();
        placePiecesOnMap();
    }

    /**
     * Parse the pieces
     */
    void parsePieces() {
        boolean piecesDone = false;
        bumpLocation = false;
        for (;;) {
            StringBuffer curLine;
            curLine = input.nextLine();
            if (curLine == null) break;
            if (curLine.charAt(0) == '*') break;
            if(curLine.charAt(0) == '!'
                    || curLine.charAt(0) == '?'
                    || curLine.charAt(curLine.length() - 1) == ':')
                break;
            if (data.isModuleSpecificLine(curSide, curLine, this)) break;
            if (curMap == setupMap && bumpLocation) {
                placePiecesOnMap();
                curPoint = curBoard.bumpLocation(curPoint);
                bumpLocation = false;
            }
            parsePieces(curLine);
        }
        input.repeatThisLine();
    }

    /**
     * Move the current set of pieces to the correct place
     */
    void placePiecesOnMap() {
        placePiecesOnMap(false);
    }

    /**
     * Place the current set of pieces at the correct location
     * @param bottom true if to be placed a bottom of stack
     */
    void placePiecesOnMap(boolean bottom) {
        if (curMap == null) return;
        if (pieces.empty()) return;
        GamePiece[] ps = curMap.getPieces();
        StackOverride s = null;
        for (int i = 0; i < ps.length; i++) {
            if (ps[i] instanceof StackOverride
                    && ps[i].getPosition().x == curPoint.x
                    && ps[i].getPosition().y == curPoint.y) {
                s = (StackOverride)ps[i];
                break;
            }
        }
        if (s == null) {
            s = new StackOverride();
            s.setMap(curMap);
            s.setPosition(curPoint);
            s.setId(GameModule.getGameModule().generateGpId());
            GameModule.getGameModule().getGameState().addPiece(s);
            curMap.addPiece(s);
        }
        while (!pieces.empty()) {
            GamePiece p = PieceCloner.getInstance().clonePiece(pieces.pop());
            if (bottom) {
                s.insert(p, 0);
            } else {
                s.add(p);
            }
            p.setProperty(Properties.PIECE_ID, GameModule.getGameModule().generateGpId());
            if (GameModule.getGameModule().getGameState().getPieceForId(p.getId()) == null) {
                GameModule.getGameModule().getGameState().addPiece(p);
            }
        }
        data.piecesPlaced();
     }

    /**
     * Find named zone
     */
    static Zone findZone(String zoneName) {
        Collection<Board> bs = Statics.theMap.getBoards();
        for ( Board b : bs ) {
            List<Zone> zs = b.getAllDescendantComponentsOf(Zone.class);
            for ( Zone z : zs ) {
                if ( z.getName().equalsIgnoreCase(zoneName)) {
                    return z;
                }
            }
        }
        return null;
    }

    /**
     * Find the best zone for resolving a location. Preferably the zone the
     * locaton is in else the nearest zone
     */
    static Zone findBestZone(String zoneName, String location) {
        Zone best = null;
        int dist = 0;
        Collection<Board> bs = Statics.theMap.getBoards();
        for ( Board b : bs ) {
            List<Zone> zs = b.getAllDescendantComponentsOf(Zone.class);
            for ( Zone z : zs ) {
                if ( z.getName().equalsIgnoreCase(zoneName)) {
                    Point p;
                    try {
                        p = z.getGrid().getLocation(location);
                    } catch (BadCoords e) {
                        if (best == null) best = z;
                        continue;
                    }
                    if (z.contains(p)) return z;
                    Rectangle r = z.getBounds();
                    int dx;
                    if (p.x < r.x) {
                        dx = r.x - p.x;
                    } else if (p.x > r.x + r.width) {
                        dx = p.x - (r.x + r.width);
                    } else {
                        dx = 0;
                    }
                    int dy;
                    if (p.y < r.y) {
                        dy = r.y - p.y;
                    } else if (p.y > r.y + r.height) {
                        dy = p.y - (r.y + r.height);
                    } else {
                        dy = 0;
                    }
                    if (best != null && dist <= (dx*dx + dy*dy)) continue;
                    dist = dx*dx + dy*dy;
                    best = z;
                }
            }
        }
        return best;
    }

    /**
     * Find named board (or nearest matching board)
     */
    static Board findBoard(String boardName, boolean partial) {
        Collection<Board> bs = Statics.theMap.getBoards();
        for ( Board b : bs ) {
            if ( b.getName().equalsIgnoreCase(boardName)) {
                return b;
            }
        }
        if (!partial) return null;
        for ( Board b : bs ) {
            if ( b.getName().startsWith(boardName)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Find a named map
     */
    Map findMap (String mapName) {
        List<Map> ms = GameModule.getGameModule().getComponentsOf(Map.class);
        for ( Map m : ms ) {
            if (m.getMapName().equalsIgnoreCase(mapName)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Resolve a map location
     */
    boolean resolveMapLocation(String mapId, int row, int col) {
        Point q = new Point();
        String s = resolveMapHex(mapId, row, col, q, data.zonePrefix, data.boardPrefix);
        if (s == null) {
            curPoint = q;
            curMap = Statics.theMap;
            return false;
        }
        input.writeError(true, s);
        return true;
    }

    /**
     * Resolve a map location - static method for multiple use
     * @return null if OK otherwise error message
     */
    public static String resolveMapHex(String mapId, int row, int col, Point q) {
        return resolveMapHex(mapId, row, col, q, "", 
                (Statics.theStatics.isDAK() || Statics.theStatics.isKorea())? "Board " : "");
    }

    public static String resolveMapHex(String mapId, int row, int col, Point q,
            String zonePrefix, String boardPrefix) {
        String loc;
        String s = Integer.toString(row);
        loc = (s.length() > 1) ? s : "0" + s;
        loc += ".";
        s = Integer.toString(col);
        loc += (s.length() > 1) ? s : "0" + s;
        Zone z;
        if (Statics.theStatics.isCaseBlue()) {
            z = findBestZone(zonePrefix + mapId, loc);
        } else {
            z = findZone(zonePrefix + mapId);
        }
        if (z != null) {
            Point p;
            try {
                p = z.getGrid().getLocation(((Statics.theStatics.isHubes()) ? mapId : "") + loc);
            } catch (BadCoords e) {
                return "Failed to translate: " + loc +
                        " in zone " + z.getName() + " of Board " +
                        z.getBoard().getName();
            }
            Rectangle r = z.getBounds();
//            p.x += r.x;
//            p.y += r.y;
            r = z.getBoard().bounds();
            p.x += r.x;
            p.y += r.y;
            p = Statics.theMap.snapTo(p);
            q.x = p.x;
            q.y = p.y;
            return null;
        }
        Board b = findBoard(boardPrefix + mapId, true);
        if (b == null) {
            return "Unable to find map " + mapId;
        }
        Point p;
        try {
            p = b.getGrid().getLocation(((Statics.theStatics.isDAK() || Statics.theStatics.isKorea()) ? mapId : "") + loc);
        } catch(BadCoords e) {
            return "Failed to translate *** " + loc +
                    " on board " + b.getName();
        }
        Rectangle r = b.bounds();
        p.x += r.x;
        p.y += r.y;
        p = Statics.theMap.snapTo(p);
        q.x = p.x;
        q.y = p.y;
        return null;
    }

    /**
     * Resolve a location within a box
     */
    boolean resolveLocationInBox(String box, String location) {
        Zone z = findZone(box);
        if (z == null && curSide >= 0) {
            z = findZone(Statics.theSides[curSide].name + " " + box);
        }
        if (z == null) {
            input.writeError(true, "Box " + box + " not found");
            return true;
        }
        Point p;
        try {
            p =z.getLocation(location);
        } catch (BadCoords e) {
            p = null;
        }
        if (p == null) {
            try {
                p = z.getLocation(box + " " + location);
            } catch (BadCoords e) {
                input.writeError(true, location + " not found in box " + box);
                return true;
            }
        }
        Rectangle r = z.getBoard().bounds();
        p.x += r.x;
        p.y += r.y;
        curPoint = p;
        curMap = Statics.theMap;
        return false;
    }

    /**
     * Resolve a box location
     */
    boolean resolveBoxLocation(String aName, Point within) {
        Zone z = findZone(aName);
        if (z == null && curSide >= 0) {
            z = findZone(Statics.theSides[curSide].name + " " + aName);
        }
        if ( z != null) {
            Rectangle r = z.getBounds();
            Point p;
            if ( within != null) {
                MapGrid g = z.getGrid();
                if ( g != null && g instanceof SquareGrid) {
                    SquareGrid s = (SquareGrid)g;
                    p = new Point ((int)(within.x * s.getDx() + s.getDx()/2),
                            (int)(within.y * s.getDy() + s.getDy()/2));
                } else {
                    p = within;
                }
            } else {
                p = new Point(r.width/2, r.height/2);
            }
            p.x += r.x;
            p.y += r.y;
            r = z.getBoard().bounds();
            p.x += r.x;
            p.y += r.y;
            curPoint = p;
            curMap = Statics.theMap;
            return false;
        }
        Map m = findMap(aName);
        if (m == null && curSide >= 0) {
            m = findMap(Statics.theSides[curSide].name + " " + aName);
        }
        if (m != null) {
            Point p;
            curMap = m;
            if ( within != null) {
                p = within;
            } else {
                Dimension d = m.mapSize();
                p = new Point(d.width/2, d.height/2);
            }
            curPoint = p;
            return false;
        }
        input.writeError(true, "Unable to find box: " + aName);
        return true;
    }

}
