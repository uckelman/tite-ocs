/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS;

import VASSAL.build.AbstractConfigurable;
import VASSAL.build.Buildable;
import VASSAL.build.module.Map;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.build.module.map.PieceMover;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.HexGrid;
import VASSAL.build.module.map.boardPicker.board.MapGrid;
import VASSAL.build.module.map.boardPicker.board.ZonedGrid;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.counters.DragBuffer;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceIterator;
import VASSAL.counters.Properties;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import net.cantab.hayward.george.OCS.Counters.Aircraft;

/**
 *
 * @author george
 */
public class DragHandlerReplace extends AbstractConfigurable {

    /**
     * Return an array of Strings describing the attributes of this object.
     * These strings are used as prompts in the Properties window for this
     * object. The order of descriptions should be the same as the order of
     * names in {@link AbstractBuildable#getAttributeNames}
     */
    public String[] getAttributeDescriptions() {
        return new String[0];
    }

    /**
     * Return the Class for the attributes of this object. Valid classes are:
     * String, Integer, Double, Boolean, Image, Color, and KeyStroke
     *
     * The order of classes should be the same as the order of names in
     * {@link AbstractBuildable#getAttributeNames}
     */
    public Class<?>[] getAttributeTypes() {
        return new Class<?>[0];
    }

    /**
     * @return a list of all attribute names for this component
     */
    public String[] getAttributeNames() {
        return new String[0];
    }

    /**
     * Sets an attribute value for this component. The
     * <code>key</code> parameter will be one of those listed in
     * {@link #getAttributeNames}. If the
     * <code>value</code> parameter is a String, it will be the value returned
     * by {@link #getAttributeValueString} for the same
     * <code>key</code>. If the implementing class extends
     * {@link AbstractConfigurable}, then
     * <code>value</code> will be an instance of the corresponding Class listed
     * in {@link AbstractConfigurable#getAttributeTypes}
     *
     * @param key the name of the attribute. Will be one of those listed in
     * {@link #getAttributeNames}
     */
    public void setAttribute(String key, Object value) {
    }

    /**
     * Return a String representation of the attribute with the given name. When
     * initializing a module, this String value will be passed to
     * {@link #setAttribute}.
     *
     * @param key the name of the attribute. Will be one of those listed in
     * {@link #getAttributeNames}
     */
    public String getAttributeValueString(String key) {
        return null;
    }

    public Class<?>[] getAllowableConfigureComponents() {
        return new Class<?>[0];
    }

    /**
     * @return a HelpFilte describing how to use and configure this component
     */
    public HelpFile getHelpFile() {
        return null;
    }

    /**
     * Remove this component from its parent
     */
    public void removeFrom(Buildable parent) {
    }

    /**
     * Adds this component to its parent. In order to make Buildable objects
     * extensible, the child is reponsible for adding itself to the parent. That
     * way, Buildable subcomponents can be defined in an extension package
     * without needing to modify the containing class.
     */
    public void addTo(Buildable parent) {
        PieceMover.AbstractDragHandler.setTheDragHandler(new myDragHandler());
    }

    static public class myDragHandler extends PieceMover.DragHandlerNoImage {

        boolean isFlight = false;
        List<OcsAirZone.EntryPoint> flightFrom;
        MapGrid ranger;
        Map baseMap;

        public void dragGestureRecognized(DragGestureEvent dge) {
            // Ensure the user has dragged on a counter before starting the drag.
            final DragBuffer db = DragBuffer.getBuffer();
            if (db.isEmpty()) {
                return;
            }

            // Remove any Immovable pieces from the DragBuffer that were
            // selected in a selection rectangle, unless they are being
            // dragged from a piece palette (i.e., getMap() == null).
            final List<GamePiece> pieces = new ArrayList<GamePiece>();
            isFlight = true;
            Point origin = null;
            OcsAirZone starting = null;
            MapGrid h = null;
            Map base = null;
            for (PieceIterator i = db.getIterator();
                i.hasMoreElements();) {
                GamePiece p = i.nextPiece();
                if (p.getMap() != null
                    && Boolean.TRUE.equals(p.getProperty(
                    Properties.NON_MOVABLE))) {
                    continue;
                }
                pieces.add(p);
                if (!isFlight) {
                    continue;
                }
                if (p instanceof StackOverride) {
                    Iterator<GamePiece> k = ((StackOverride) p).getPiecesIterator();
                    while (k.hasNext()) {
                        GamePiece q = k.next();
                        if (!(q instanceof Aircraft)) {
                            isFlight = false;
                            break;
                        }
                    }
                    if (!isFlight) {
                        continue;
                    }
                } else if (!(p instanceof Aircraft)) {
                    isFlight = false;
                    continue;
                }
                Map m = p.getMap();
                if (m == null) {
                    isFlight = false;
                    continue;
                }
                if (base != null && base != m) {
                    isFlight = false;
                    continue;
                }
                if (base == null) {
                    base = m;
                }
                Point q = p.getPosition();
                Zone z = m.findZone(new Point(q));
                MapGrid n = null;
                if (z != null) {
                    if (z instanceof OcsAirZone) {
                        if (origin != null || (starting != null && starting != z)) {
                            isFlight = false;
                            continue;
                        }
                        starting = (OcsAirZone) z;
                        continue;
                    }
                    if (z instanceof OcsHexZone) {
                        q = ((OcsHexZone) z).getHex();
                        if (q == null) {
                            isFlight = false;
                            continue;
                        }
                    }
                }
                if (starting != null) {
                    isFlight = false;
                    continue;
                }
                if (origin != null && !origin.equals(q)) {
                    isFlight = false;
                    continue;
                }
                if (origin == null) {
                    origin = q;
                }
            }
            if (pieces.isEmpty()) {
                return;
            }
            if (isFlight) {
                if (starting != null) {
                    flightFrom = starting.getEntryPoints();
                } else {
                    flightFrom = new ArrayList<OcsAirZone.EntryPoint>();
                    flightFrom.add(new OcsAirZone.EntryPoint(origin, 0));
                }
                if (flightFrom.isEmpty()) {
                    isFlight = false;
                } else {
                    Board b = base.findBoard(new Point(flightFrom.get(0).coords));
                    if (b != null) {
                        h = b.getGrid();
                        if (h == null) {
                            isFlight = false;
                        } else if (h instanceof ZonedGrid) {
                            ZonedGrid z = (ZonedGrid) h;
                            Point r = new Point(flightFrom.get(0).coords);
                            Rectangle s = b.bounds();
                            r.translate(-s.x, -s.y);
                            Zone y = z.findZone(r);
                            if (y == null) {
                                h = z.getBackgroundGrid();
                            } else {
                                h = y.getGrid();
                            }
                            if (h == null || !(h instanceof HexGrid)) {
                                isFlight = false;
                            }
                        } else if (!(h instanceof HexGrid)) {
                            isFlight = false;
                        }
                    } else {
                        isFlight = false;
                    }
                }
            }

            if (isFlight) {
                ranger = h;
                baseMap = base;
            }

            super.dragGestureRecognized(dge);

            if (isFlight) {
                updateRange(dge.getDragOrigin());
            }

        }

        protected void makeDragCursor(double zoom) {
            super.makeDragCursor(zoom);
            dragCursor.setText(null);
            if (isFlight) {
                dragCursor.setVerticalTextPosition(SwingConstants.BOTTOM);
                dragCursor.setHorizontalTextPosition(SwingConstants.CENTER);
                int w = dragCursor.getIcon().getIconWidth();
                int h = dragCursor.getIcon().getIconHeight();
                dragCursor.setSize(w, h + 20);
            } else {
                int w = dragCursor.getIcon().getIconWidth();
                int h = dragCursor.getIcon().getIconHeight();
                dragCursor.setSize(w, h);
            }
        }
        DragReaction last;

        public void dragEnter(DropTargetDragEvent e) {
            final Component newDropWin = e.getDropTargetContext().getComponent();
            if (newDropWin instanceof DragReaction) {
                DragReaction cur = (DragReaction) newDropWin;
                if (cur.startDragReaction()) {
                    if (last != null) last.endDragReaction();
                    last = cur;
                }
            }
            super.dragEnter(e);
        }

        public void dragMouseMoved(DragSourceDragEvent e) {
            if (!e.getLocation().equals(lastDragLocation)) {
                lastDragLocation = e.getLocation();
                if (isFlight) {
                    Point p = new Point(lastDragLocation);
                    SwingUtilities.convertPointFromScreen(p, baseMap.getView());
                    updateRange(baseMap.mapCoordinates(p));
                }
                moveDragCursor(e.getX(), e.getY());
                if (dragCursor != null && !dragCursor.isVisible()) {
                    dragCursor.setVisible(true);
                }
            }
        }

        public void updateRange(Point p) {
            int r = 100000;
            Zone z = baseMap.findZone(new Point(p));
            if (z != null && z instanceof OcsHexZone) {
                Point q = ((OcsHexZone) z).getHex();
                if (q != null) {
                    p = q;
                }
            } else if (z != null && z instanceof OcsAirZone) {
                List<OcsAirZone.EntryPoint> exs = ((OcsAirZone) z).getEntryPoints();
                if (!exs.isEmpty()) {
                    for (OcsAirZone.EntryPoint e : flightFrom) {
                        for (OcsAirZone.EntryPoint f : exs) {
                            int d;
                            if (f.coords.equals(e.coords)) {
                                d = Math.abs(f.distance - e.distance);
                            } else {
                                d = e.distance + f.distance + ranger.range(
                                    e.coords, f.coords);
                            }
                            if (d < r) {
                                r = d;
                            }
                        }
                    }
                    dragCursor.setText(Integer.toString(r));
                    return;
                }
            }
            int s = 1000000;
            Statics.readUseNearest();
            for (OcsAirZone.EntryPoint e : flightFrom) {
                int d = ranger.range(e.coords, p) + (Statics.useNearest ? 0 : e.distance);
                if (d < s) {
                    s = d;
                    r = d + (Statics.useNearest ? e.distance : 0);
                }
            }
            dragCursor.setText(Integer.toString(r));
        }
    }
}
