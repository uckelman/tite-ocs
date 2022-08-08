/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS;

import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.GameComponent;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.command.Command;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * This is a special class of board which is intended to exist as the sole board
 * of a separate map window and holds setup/reinforcement instructions and the
 * required pieces. When setting up a scenario the boards of this class will be
 * automatically loaded from the text files. Each board consists of a series of
 * line which are normal text, bold text or bold text followed by a stack of
 * pieces.
 *
 * This is done this way as only maps can hold collections of pieces and maps
 * are always displayed as a collection of one or more boards.
 * @author George Hayward
 */
public class SetupBoard extends Board implements GameComponent {

    /**
     * The lines to be drawn
     */
    Line[] lines = new Line[200];
    /**
     * Number of lines used
     */
    int noLines = 0;
    /**
     * The width of a piece (used for laying out the window).
     */
    int pieceWidth = 100;
    /**
     * The height of a piece (used for laying out the window).
     */
    int pieceHeight = 100;
    /**
     * The three fonts to be used for notes, locations and Headers
     */
    Font[] fonts = new Font[] { new Font("Dialog", Font.PLAIN, 16),
                                new Font("Dialog", Font.BOLD, 16),
                                new Font("Dialog", Font.BOLD, 20)
    };
    /**
     * The maximum width of any line
     */
    int maxWidth = 0;
    /**
     * When adding to buildable, register this as a game component so it can
     * save its contents
     */
    @Override
    public void addTo(Buildable b) {
        super.addTo(b);
        GameModule.getGameModule().getGameState().addGameComponent(this);
    }
    /**
     * Override the configurable interface methods to add pieceWidth and
     * pieceHeight as new parameters
     */
    final static String PIECE_WIDTH = "PieceWidth";
    final static String PIECE_HEIGHT = "PieceHeight";
    @Override
    public Class<?>[] getAttributeTypes() {
        Class<?>[] os = super.getAttributeTypes();
        Class<?>[] ns = new Class<?>[os.length+2];
        System.arraycopy(os, 0, ns, 0, os.length);
        ns[os.length] = Integer.class;
        ns[os.length+1] = Integer.class;
        return ns;
    }
    @Override
    public String getAttributeValueString(String key) {
        if (PIECE_WIDTH.equals(key)) {
          return String.valueOf(pieceWidth);
        }
        if (PIECE_HEIGHT.equals(key)) {
          return String.valueOf(pieceHeight);
        }
        return super.getAttributeValueString(key);
    }
    @Override
    public void setAttribute(String key, Object val) {
        if (PIECE_WIDTH.equals(key)) {
            if (val instanceof String) {
                val = Integer.valueOf((String)val);
            }
            if (val instanceof Integer) {
                pieceWidth = ((Integer) val).intValue();
            }
            return;
        }
        if (PIECE_HEIGHT.equals(key)) {
            if (val instanceof String) {
                val = Integer.valueOf((String)val);
            }
            if (val instanceof Integer) {
                pieceHeight = ((Integer) val).intValue();
            }
            return;
        }
        super.setAttribute(key, val);
    }
    @Override
    public String[] getAttributeNames() {
        String [] os = super.getAttributeNames();
        String [] ns = new String[os.length+2];
        System.arraycopy(os, 0, ns, 0, os.length);
        ns[os.length] = PIECE_WIDTH;
        ns[os.length+1] = PIECE_HEIGHT;
        return ns;
    }
    @Override
    public String[] getAttributeDescriptions() {
        String [] os = super.getAttributeNames();
        String [] ns = new String[os.length+2];
        System.arraycopy(os, 0, ns, 0, os.length);
        ns[os.length] = "Piece Width: ";
        ns[os.length+1] = "Piece Height: ";
        return ns;
    }
    /**
     * Draw the required text after drawing the region as normal
     */
    @Override
    public void drawRegion(final Graphics g,
                         final Point location,
                         Rectangle visibleRect,
                         double zoom,
                         final Component obs) {
        super.drawRegion(g, location, visibleRect, zoom, obs);
        for ( int z = 0; z < noLines; z++ ) {
            g.setFont(fonts[lines[z].font]);
            g.setColor(Color.BLACK);
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                              RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawString(lines[z].text, 10, lines[z].y);
        }
    }
    /**
     * Add a new line to the array
     */
    void addLine (Line a) {
        if (noLines >= lines.length) {
            Line[] ls = new Line[lines.length+100];
            System.arraycopy(lines, 0, ls, 0, lines.length);
            lines = ls;
        }
        lines[noLines++] = a;
        super.setAttribute(HEIGHT, a.height);
    }
    /**
     * Add a new line to the text
     * @param s - text of the line
     * @param f - the font to be used
     * @param height - minimal height to be used for line
     * @return the width of the line
     */
    int addLine ( String s, int f, int height, boolean pieces ) {
        Line x = new Line();
        x.text = s;
        x.font = f;
        x.height = (noLines == 0) ? 0 : lines[noLines-1].height;
        FontMetrics m = super.getMap().getView().getGraphics().getFontMetrics(fonts[f]);
        int h = m.getHeight();
        if ( h >= height ) {
            x.height += h;
            x.y = x.height - m.getDescent();
        } else {
            x.height += height;
            x.y = x.height - ( m.getDescent() + (height - h)/2);
        }
        int w =  m.stringWidth(s) + 20;
        int y = pieces ? (w + pieceWidth + 70) : w + 50;
        if ( y > maxWidth) {
            maxWidth = y;
            super.setAttribute(WIDTH, y);
        }
        x.width= y;
        addLine(x);
        return w;
    }

    public void addNote(String s) {
        addLine(s, 0, 0, false);
    }

    public void addMajorNote(String s) {
        addLine(s, 1, 0, false);
    }

    public Point addLocation(String s) {
        int a = addLine(s, 1, pieceHeight, true);
        return new Point(a + pieceWidth/2, lines[noLines-1].height - pieceHeight/2);
    }

    public Point bumpLocation(Point p) {
        addLine("", 0, pieceHeight, true);
        return new Point(p.x, lines[noLines-1].height - pieceHeight/2);
    }

    public void addTurn(String s) {
        addLine(s, 2, 0, false);
    }

    public Command getRestoreCommand() {
        if ( noLines >= lines.length) {
            return new RestoreBoardCommand(this.map.getMapName(), this.getName(), lines);
        }
        Line[] ls = new Line[noLines];
        System.arraycopy(lines, 0, ls, 0, noLines);
        return new RestoreBoardCommand(this.map != null ? this.map.getMapName() : "", this.getName(), ls);
    }

    public void setup(boolean gameStarting) {
        if (!gameStarting) {
            lines = new Line[200];
            noLines = 0;
        }
    }

    public void setLines(Line[] ls) {
        lines = ls;
        noLines = lines.length;
        if (noLines != 0 ) {
            if (map instanceof MapOverride && ((MapOverride)map).viewsEnabled) {
                super.setAttribute(HEIGHT, lines[noLines-1].height + 2000);
            } else {
                super.setAttribute(HEIGHT, lines[noLines-1].height);
            }
            maxWidth = 0;
            for (Line l : lines) {
                if (l.width > maxWidth) {
                    maxWidth = l.width;
                }
            }
            if (maxWidth < 500) maxWidth = 500;
            super.setAttribute(WIDTH, maxWidth);
        } else {
            lines = new Line[200];
            super.setAttribute(HEIGHT, 500);
            super.setAttribute(WIDTH, 500);
        }
    }

    public void makeView(String name) {
        if(!(map instanceof MapOverride)) {
            return;
        }
        MapOverride m = (MapOverride) map;
        m.createViewHere(name, bounds().height, (maxWidth > 450) ? maxWidth + 50 : 500);
    }

    public void addFinalSpace() {
        super.setAttribute(HEIGHT, bounds().height + 2000);
    }
}
