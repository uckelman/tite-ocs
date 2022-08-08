/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS;

import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.GameComponent;
import VASSAL.build.module.Map;
import VASSAL.build.module.PlayerRoster;
import VASSAL.build.module.map.BoardPicker;
import VASSAL.build.module.map.PieceMover;
import VASSAL.build.module.map.StackMetrics;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.build.module.properties.ChangePropertyCommandEncoder;
import VASSAL.command.Command;
import VASSAL.configure.BooleanConfigurer;
import VASSAL.configure.CompoundValidityChecker;
import VASSAL.configure.IntConfigurer;
import VASSAL.configure.MandatoryComponent;
import VASSAL.counters.DragBuffer;
import VASSAL.i18n.Resources;
import VASSAL.preferences.Prefs;
import VASSAL.tools.AdjustableSpeedScrollPane;
import VASSAL.tools.ComponentSplitter;
import VASSAL.tools.KeyStrokeSource;
import VASSAL.tools.SequenceEncoder;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 * This is a special version of map which will switch between different "views"
 * of the map. These are implemented by using a scroll pane with a special
 * viewport that constrains scrolling to a restricted area of the map
 * @author george
 */
public class MapOverride extends Map {

    /**
     * The current restricted rectangle which the map view is restricted to
     */
    Rectangle currentRestriction;
    /**
     * The active viewport
     */
    myViewport activeViewport;
    /**
     * The active restrict definition
     */
    aRestrict activeRestrict;
    /**
     * All the current restrictions
     */
    List<aRestrict> allRestricts;
    /**
     * Button to launch menu for views
     */
    JButton viewLaunch;
    /**
     * Default zoom for new views
     */
    double defaultZoom = 1.0;
    /**
     * Multiple view enabled
     */
    boolean viewsEnabled = false;

    /**
     * Create a new map override
     */
    public MapOverride() {
        super();
        viewLaunch = new myButton("Views...");
        viewLaunch.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                viewCommandMenu();
            }
        });
        viewLaunch.setFocusable(false);
        viewLaunch.setToolTipText("Select View of map");
        toolBar.add(viewLaunch);
        allRestricts = new ArrayList<aRestrict>();
        allRestricts.add(new aRestrict("Main", null, 1.0, null));
        allRestricts.get(0).makeActive();
    }

    /**
     * Create a new view related menu
     */
    public void viewCommandMenu() {
        JMenu theMenu = new JMenu();
        JMenuItem mi;
        if (activeRestrict != null && activeRestrict.initialLocation != null
                && (this.getZoom() != activeRestrict.startZoom
                || !activeViewport.getRealViewPosition().equals(activeRestrict.initialLocation))) {
            mi = new JMenuItem("Recentre this View");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    zoom.setZoomFactor(activeRestrict.startZoom);
                    activeViewport.setRealViewPosition(activeRestrict.initialLocation);
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
        }
        for (aRestrict a : allRestricts) {
            if (a.owner == null || a.owner.equals(GameModule.getUserId())) {
                final aRestrict b = a;
                mi = new JMenuItem("Switch to View " + a.name);
                mi.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        b.makeActive();
                    }
                });
                mi.setEnabled(a != activeRestrict);
                theMenu.add(mi);
            }
        }
        if (activeRestrict != null &&
                (activeRestrict.owner != null
                   || GameModule.getGameModule().getArchiveWriter() != null)) {
            mi = new JMenuItem("Close this View");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    allRestricts.remove(activeRestrict);
                    if (allRestricts.isEmpty()) {
                        allRestricts.add(new aRestrict("main", null, 1.0, null));
                    }
                    activeRestrict = null;
                    allRestricts.get(0).makeActive();
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
        }
        if (GameModule.getGameModule().getArchiveWriter() != null) {
            mi = new JMenuItem("Create a new View here");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Point p = mapCoordinates(activeViewport.getRealViewPosition());
                    Dimension d = mapSize();
                    aRestrict b = new aRestrict("new",
                            new Rectangle(p.x, p.y, d.width - p.x, d.height - p.y),
                            getZoom(),
                            activeViewport.getRealViewPosition());
                    allRestricts.add(b);
                    b.makeActive();
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
            mi = new JMenuItem("Restrict this view here");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    Point p = mapCoordinates(activeViewport.getRealViewPosition());
                    Dimension d = activeViewport.getExtentSize();
                    d.width /= getZoom();
                    d.height /= getZoom();
                    p.x += d.width;
                    p.y += d.height;
                    activeRestrict.here.width = p.x - activeRestrict.here.x;
                    activeRestrict.here.height = p.y - activeRestrict.here.y;
                    activeRestrict.setCurrentRestriction();
                    activeViewport.nowFireStateChanged();
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
            mi = new JMenuItem("Name this View");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    activeRestrict.name = "";
                    theMap.addKeyListener(new KeyAdapter() {

                        @Override
                        public void keyTyped(KeyEvent e) {
                            if (e.getKeyChar() == KeyEvent.CHAR_UNDEFINED) return;
                            char c = e.getKeyChar();
                            if (c == '\r' || c == '\n') {
                                theMap.removeKeyListener(this);
                                return;
                            }
                            activeRestrict.name += c;
                            return;
                        }
                    });
                    theMap.requestFocus();
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
            mi = new JMenuItem("Make this the first view");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    allRestricts.remove(activeRestrict);
                    allRestricts.add(0, activeRestrict);
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
            mi = new JMenuItem("Make this default zoom for new user views");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    defaultZoom = zoom.getZoomFactor();
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
            mi = new JMenuItem("Reset start zoom/location");
            mi.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    activeRestrict.initialLocation
                            = activeViewport.getRealViewPosition();
                    activeRestrict.startZoom = getZoom();
                }
            });
            mi.setEnabled(true);
            theMenu.add(mi);
        }
        if (theMenu.getSubElements().length == 0) return;
        theMenu.getPopupMenu().show(viewLaunch, 0, viewLaunch.getHeight());
    }

    /**
     * Create a new view for a reinforcement board
     * @param e name
     * @param e current height at which to break view
     */
    public void createViewHere(String name, int e, int w) {
        if (!viewsEnabled || (allRestricts.size() == 1 && allRestricts.get(0).here == null)) {
            viewsEnabled = true;
            allRestricts.clear();
            allRestricts.add(new aRestrict(name, new Rectangle(0,0,w,e),1.0,new Point(0,0)));
            allRestricts.get(0).makeActive();
            return;
        }
        aRestrict a = allRestricts.get(allRestricts.size()-1);
        int z = a.here.y + a.here.height;
        aRestrict b = new aRestrict(name, new Rectangle(0,z,w, e-z), 1.0, new Point(0,z));
        allRestricts.add(b);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isShiftDown() && activeRestrict == allRestricts.get(0)) return;
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isShiftDown() && activeRestrict == allRestricts.get(0)) {
            Point p = mapCoordinates(e.getPoint());
            String loc = myLocationName(new Point(p));
            aRestrict a = new aRestrict(loc, activeRestrict.here, defaultZoom, new Point(0, 0));
            allRestricts.add(a);
            a.makeActive();
            super.centerAt(p, 0, 0);
            a.initialLocation = activeViewport.getRealViewPosition();
            a.lastLocation = a.initialLocation;
            GameModule.getGameModule().sendAndLog(new NewRestrict(a));
            return;
        }
        super.mouseReleased(e);
    }

    public String myLocationName(Point p) {
        Board b = findBoard(p);
        if (b == null) return "Unknown";
        p.x -= b.bounds().x;
        p.y -= b.bounds().y;
        if (b != null) {
            if (Statics.theStatics.isCaseBlue()
                    || Statics.theStatics.isTunisia()) {
                Zone z = findZone(p);
                if (z != null) return z.locationName(
                        new Point(p));
            }
            return b.locationName(new Point(p));
        }
        return "Unknown";
    }

    @Override
    /**
     * Ensure the active restriction has the rectangle to be made visible
     */
    public void ensureVisible(Rectangle r) {
        if (activeRestrict.here == null || activeRestrict.here.contains(r)) {
            super.ensureVisible(r);
        }
        aRestrict b = null;
        for (aRestrict a: allRestricts) {
            if (a.owner != null) break;
            if (a.here == null || a.here.contains(r)) {
                b = a;
                break;
            }
        }
        if (b != null) b.makeActive();
        super.ensureVisible(r);
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        Point p = dtde.getLocation();
        if (currentRestriction != null) {
            p.x -= currentRestriction.x;
            p.y -= currentRestriction.y;
        }
        scrollAtEdge(p, SCROLL_ZONE);
    }



    /**
     * Center the map at the given map coordinates, if the point is not
     * already within (dx,dy) of the center.
     */
    @Override
    public void centerAt(Point p, int dx, int dy) {
        if (scroll != null) {
            p = componentCoordinates(p);

            final Rectangle r = theMap.getVisibleRect();
            r.x = p.x - r.width / 2;
            r.y = p.y - r.height / 2;

            if (activeRestrict != null) {
                activeRestrict.setCurrentRestriction();
            }
            final Dimension d = getRestrictedSize();
            if (r.x + r.width > d.width) {
                r.x = d.width - r.width;
            }
            if (r.y + r.height > d.height) {
                r.y = d.height - r.height;
            }

            r.width = dx > r.width ? 0 : r.width - dx;
            r.height = dy > r.height ? 0 : r.height - dy;

            theMap.scrollRectToVisible(r);
        }
    }

    public Dimension getRestrictedSize() {
        if (currentRestriction != null)
            return currentRestriction.getSize();
        return super.getPreferredSize();
    }

    /**
     * Scrolls the map in the containing JScrollPane.
     *
     * @param dx number of pixels to scroll horizontally
     * @param dy number of pixels to scroll vertically
     */
    @Override
    public void scroll(int dx, int dy) {
        Rectangle r = scroll.getViewport().getViewRect();
        r.translate(dx, dy);
        r = r.intersection(new Rectangle(getRestrictedSize()));
        if (currentRestriction != null) {
            r.x += currentRestriction.x;
            r.y += currentRestriction.y;
        }
        theMap.scrollRectToVisible(r);
    }

    public Command createNewRestrict(SequenceEncoder.Decoder st) {
        return new NewRestrict(st);
    }

    public class NewRestrict extends Command {

        aRestrict newOne;

        public NewRestrict(SequenceEncoder.Decoder st) {
            newOne = new aRestrict(st);
        }

        public NewRestrict(aRestrict done) {
            newOne = done;
        }

        public Command myUndoCommand() {
            return null;
        }

        public void executeCommand() {
            allRestricts.add(newOne);
        }

        public void appendTo(SequenceEncoder se) {
            se.append(getIdentifier());
            newOne.appendTo(se);
        }
    }

    public Command createMovedRestrict(SequenceEncoder.Decoder se) {
        return new MovedRestrict(se);
    }

    public class MovedRestrict extends Command {

        aRestrict newOne;

        public MovedRestrict(SequenceEncoder.Decoder st) {
            newOne = new aRestrict(st);
        }

        public MovedRestrict(aRestrict done) {
            newOne = done;
        }

        public Command myUndoCommand() {
            return null;
        }

        public void executeCommand() {
            for( aRestrict a: allRestricts) {
                if (a.name.equals(newOne.name)
                        && ((a.owner != null && a.owner.equals(newOne.owner))
                             || (a.owner == null && newOne.owner == null))
                        && ((a.here != null && a.here.equals(newOne.here))
                             || (a.here == null && newOne.here == null))) {
                    a.lastLocation = newOne.lastLocation;
                    a.lastZoom = newOne.lastZoom;
                }
            }
        }

        public void appendTo(SequenceEncoder se) {
            se.append(getIdentifier());
            newOne.appendTo(se);
        }
    }

    @Override
    public Command getRestoreCommand() {
        activeRestrict.saveRestrict();
        return new RestoreRestrict();
    }


    public Command createRestoreRestrict(SequenceEncoder.Decoder st) {
        return new RestoreRestrict(st);
    }

    public class RestoreRestrict extends Command {

        aRestrict[] restricts;
        double defZoom;

        public RestoreRestrict() {
            restricts = new aRestrict[allRestricts.size()];
            int i = 0;
            for (aRestrict a : allRestricts) {
                restricts[i++] = new aRestrict(a);
            }
            defZoom = defaultZoom;
        }

        public RestoreRestrict(SequenceEncoder.Decoder st) {
            restricts = new aRestrict[st.nextInt(0)];
            for (int i = 0; i < restricts.length; i++) {
                restricts[i] = new aRestrict(st);
            }
            defZoom = st.nextDouble(1.0);
        }

        public Command myUndoCommand() {
            return null;
        }

        public void executeCommand() {
            allRestricts.clear();
            activeRestrict = null;
            allRestricts.addAll(Arrays.asList(restricts));
            restricts[0].makeActive();
            defaultZoom = defZoom;
        }

        public void appendTo(SequenceEncoder se) {
            se.append(getIdentifier());
            se.append(restricts.length);
            for (aRestrict a : restricts) {
                a.appendTo(se);
            }
            se.append(defZoom);
        }
    }

    public class aRestrict {

        /**
         * The name of this restriction for display purposes
         */
        String name;
        /**
         * The owner of this restriction. Only restrictions belonging to the
         * player or public ones (owner is null) are available
         */
        String owner;
        /**
         * The rectangle of the map to which viewing is to be restricted
         */
        Rectangle here;
        /**
         * The zoom at which this was last displayed
         */
        double lastZoom;
        /**
         * The location at which this was last displayed
         */
        Point lastLocation;
        /**
         * The initial zoom at which this was displayed
         */
        double startZoom;
        /**
         * The initial location at which this was displayed
         */
        Point initialLocation;

        /**
         * Create a new restriction
         */
        aRestrict(String name, Rectangle here, double zoom, Point start) {
            this.name = name;
            if (GameModule.getGameModule().getArchiveWriter() != null) {
                owner = null;
            } else {
                owner = GameModule.getUserId();
            }
            this.here = here;
            lastZoom = zoom;
            startZoom = zoom;
            lastLocation = start;
            initialLocation = start;
        }

        /**
         * Create a new restriction from another one
         */
        aRestrict(aRestrict a) {
            name = a.name;
            owner = a.owner;
            here = a.here;
            lastZoom = a.lastZoom;
            startZoom = a.startZoom;
            initialLocation = a.initialLocation;
            lastLocation = a.lastLocation;
        }
        
        /**
         * Create a new restriction from a sequence
         */
        aRestrict(SequenceEncoder.Decoder st) {
            name = st.nextToken();
            if (st.nextBoolean(false)) {
                owner = st.nextToken();
            }
            if (st.nextBoolean(false)) {
                here = new Rectangle (st.nextInt(0), st.nextInt(0),
                                      st.nextInt(0), st.nextInt(0));
            }
            lastZoom = st.nextDouble(1.0);
            if (st.nextBoolean(false)) {
                lastLocation = new Point (st.nextInt(0), st.nextInt(0));
            }
            startZoom = st.nextDouble(1.0);
            if (st.nextBoolean(false)) {
                initialLocation = new Point (st.nextInt(0), st.nextInt(0));
            }
        }

        /**
         * Append this restriction to a sequence encoder
         */
        public void appendTo(SequenceEncoder se) {
            se.append(name);
            if (owner != null) {
                se.append(true);
                se.append(owner);
            } else {
                se.append(false);
            }
            if (here != null) {
                se.append(true);
                se.append(here.x);
                se.append(here.y);
                se.append(here.width);
                se.append(here.height);
            } else {
                se.append(false);
            }
            se.append(lastZoom);
            if (lastLocation != null) {
                se.append(true);
                se.append(lastLocation.x);
                se.append(lastLocation.y);
            } else {
                se.append(false);
            }
            se.append(startZoom);
            if (initialLocation != null) {
                se.append(true);
                se.append(initialLocation.x);
                se.append(initialLocation.y);
            } else {
                se.append(false);
            }
        }

        /**
         * Set up current restriction from this definition
         */
        public void setCurrentRestriction() {
            if (here == null) {
                currentRestriction = null;
                return;
            }
            double zoom = getZoom();
            currentRestriction = new Rectangle((int)(here.x * zoom),
                                               (int)(here.y * zoom),
                                               (int)(here.width*zoom),
                                               (int)(here.height*zoom));
        }

        /**
         * Save current zoom and position
         */
        public void saveRestrict() {
            lastLocation = activeViewport.getRealViewPosition();
            lastZoom = getZoom();
            if (owner != null) {
                GameModule.getGameModule().sendAndLog(new MovedRestrict(this));
            }
        }

        /**
         * Make this the active restrict
         */
        public void makeActive() {
            if (activeRestrict != null) {
                activeRestrict.saveRestrict();
            }
            activeRestrict = this;
            setCurrentRestriction();
            if (zoom != null) {
                zoom.setZoomFactor(lastZoom);
            }
            if (lastLocation != null) {
                activeViewport.setRealViewPosition(lastLocation);
            } else {
                activeViewport.setViewPosition(new Point(0,0));
            }
        }
    }
    
    public class myMenuItem extends JMenuItem implements DragReaction {
        
        aRestrict forThis;
        
        myMenuItem(String s, aRestrict p) {
            super(s);
            forThis = p;
            setDropTarget(PieceMover.AbstractDragHandler.makeDropTarget(
                this, DnDConstants.ACTION_MOVE, null));
        }
        
        public boolean startDragReaction() {
            forThis.makeActive();
//            scroll.validate();
//            scroll.paintImmediately(scroll.getVisibleRect());
            scroll.invalidate();
            scroll.repaint();
            return false;
        }
        
        public void endDragReaction() {
        }
    }

    public class myButton extends JButton implements DragReaction {

        JPopupMenu thePopup;

        public myButton(String s) {
            super(s);
            setDropTarget(PieceMover.AbstractDragHandler.makeDropTarget(
                this, DnDConstants.ACTION_MOVE, null));
        }

        public boolean startDragReaction() {
            if (thePopup != null) {
                thePopup.setVisible(true);
                return false;
            }
            JMenu theMenu = new JMenu();
            for (aRestrict a : allRestricts) {
                if (a.owner == null || a.owner.equals(GameModule.getUserId())) {
                    JMenuItem m = new myMenuItem(a.name, a);
                    m.setEnabled(true);
                    theMenu.add(m);
                }
            }
            thePopup = theMenu.getPopupMenu();
            thePopup.show(viewLaunch, 0, viewLaunch.getHeight());
            return true;
        }

        public void endDragReaction() {
            if (thePopup == null) return;
            thePopup.setVisible(false);
            thePopup = null;
        }

    }

    public class myScrollPane extends AdjustableSpeedScrollPane {

        /**
         * Creates an AdjustableSpeedScrollPane that displays the contents of the
         *  specified component, where both horizontal and vertical scrollbars
         * appear whenever the component's contents are larger than the view.
         *
         *  @param view the component to display in the scrollpane's viewport
         */
        public myScrollPane(Component view) {
            this(view, VERTICAL_SCROLLBAR_AS_NEEDED,
                 HORIZONTAL_SCROLLBAR_AS_NEEDED);
        }

        /**
         * Creates an AdjustableSpeedScrollPane that displays the view component
         *  in a viewport with the specified scrollbar policies. The available
         * policy settings are listed at
         * {@link JScrollPane#setVerticalScrollBarPolicy} and
         * {@link JScrollPane#setHorizontalScrollBarPolicy}.
         *
         * @param view the component to display in the scroll pane's viewport
         * @param vsbPolicy an integer that specifies the vertical scrollbar policy
         * @param hsbPolicy an integer that specifies the horizontal scrollbar
         * policy
         */
        public myScrollPane(Component view, int vsbPolicy,
                            int hsbPolicy) {
            super(view, vsbPolicy, hsbPolicy);
        }

        /**
         * Returns a new <code>JViewport</code> by default.
         * Used to create the
         * viewport (as needed) in <code>setViewportView</code>,
         * <code>setRowHeaderView</code>, and <code>setColumnHeaderView</code>.
         * Subclasses may override this method to return a subclass of
         * <code>JViewport</code>.
         *
         * @return a new <code>JViewport</code>
         */
        @Override
        protected JViewport createViewport() {
            activeViewport =  new myViewport();
            return activeViewport;
        }
    }

    public class myViewport extends JViewport {

        /**
         * Returns the view coordinates that appear in the upper left
         * hand corner of the viewport, or 0,0 if there's no view.
         *
         * @return a <code>Point</code> object giving the upper left coordinates
         */
        @Override
        public Point getViewPosition() {
            Component view = getView();
            if (view != null) {
                Point p = super.getViewPosition();
                if (currentRestriction == null) return p;
                p.x -= currentRestriction.x;
                p.y -= currentRestriction.y;
                return p;
            }
            else {
                return new Point(0,0);
            }
        }

        /**
         * Get the real view position for own code
         */
        public Point getRealViewPosition() {
            return super.getViewPosition();
        }

        /**
         * If the view's size hasn't been explicitly set, return the
         * preferred size, otherwise return the view's current size.
         * If there is no view, return 0,0.
         *
         * @return a <code>Dimension</code> object specifying the size of the view
         */
        @Override
        public Dimension getViewSize() {
            Component view = getView();

            if (view == null) {
                return new Dimension(0,0);
            }
            Dimension d = super.getViewSize();
            if (currentRestriction != null) {
                d.width -= currentRestriction.x;
                if (d.width > currentRestriction.width)
                    d.width = currentRestriction.width;
                d.height -= currentRestriction.y;
                if (d.height > currentRestriction.height)
                    d.height = currentRestriction.height;
            }
            return d;
        }

        /**
         * Sets the view coordinates that appear in the upper left
         * hand corner of the viewport, does nothing if there's no view.
         *
         * @param p  a <code>Point</code> object giving the upper left coordinates
         */
        @Override
        public void setViewPosition(Point p)
        {
            Component view = getView();
            if (view == null) {
                return;
            }
            if (currentRestriction != null) {
                p = new Point(p.x + currentRestriction.x,
                              p.y + currentRestriction.y);
            }
            super.setViewPosition(p);
        }

        /**
         * Set the real view position directly
         */
        public void setRealViewPosition(Point p) {
            super.setViewPosition(p);
        }

        /**
         * Fire a state change
         */
        public void nowFireStateChanged() {
            super.fireStateChanged();
        }
    }

    public static class myView extends Map.View {

        MapOverride map;

        myView(MapOverride m) {
            super(m);
            map = m;
        }

        @Override
        public void paint(Graphics g) {
            // Don't draw the map until the game is updated.
            if (GameModule.getGameModule().getGameState().isUpdating()) {
                return;
            }
            Rectangle r = getVisibleRect();
            g.setColor(map.bgColor);
            g.fillRect(r.x, r.y, r.width, r.height);
            if (map.currentRestriction != null) {
                if (map.currentRestriction.width < r.width)
                    r.width = map.currentRestriction.width;
                if (map.currentRestriction.height < r.height)
                    r.height = map.currentRestriction.height;
            }
            g.setClip(r);
            map.paintRegion(g, r);
        }


    }

    /** @return the Swing component representing the map */
    @Override
    public JComponent getView() {
        if (theMap == null) {
            theMap = new myView(this);

            scroll = new myScrollPane(
                    theMap,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scroll.unregisterKeyboardAction(
                    KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0));
            scroll.unregisterKeyboardAction(
                    KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0));
            scroll.setAlignmentX(0.0f);
            scroll.setAlignmentY(0.0f);

            layeredPane.setLayout(new InsetLayout(layeredPane, scroll));
            layeredPane.add(scroll, JLayeredPane.DEFAULT_LAYER);
        }
        return theMap;
    }

    /**
     * Expects to be added to a {@link GameModule}. Determines a unique id for
     * this Map. Registers itself as {@link KeyStrokeSource}. Registers itself
     * as a {@link GameComponent}. Registers itself as a drop target and drag
     * source.
     *
     * @see #getId
     * @see DragBuffer
     */
    @Override
    public void addTo(Buildable b) {
        useLaunchButton = useLaunchButtonEdit;
        viewLaunch.setEnabled(viewsEnabled);
        viewLaunch.setVisible(viewsEnabled);
        idMgr.add(this);
        GameModule.getGameModule().addCommandEncoder(
                new ChangePropertyCommandEncoder(this));

        validator = new CompoundValidityChecker(
                new MandatoryComponent(this, BoardPicker.class),
                new MandatoryComponent(this, StackMetrics.class)).append(idMgr);

        final DragGestureListener dgl = new DragGestureListener() {

            public void dragGestureRecognized(DragGestureEvent dge) {
                if (mouseListenerStack.isEmpty() && dragGestureListener != null) {
                    dragGestureListener.dragGestureRecognized(dge);
                }
            }
        };

        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(
                theMap, DnDConstants.ACTION_MOVE, dgl);
        theMap.setDropTarget(PieceMover.AbstractDragHandler.makeDropTarget(
                theMap, DnDConstants.ACTION_MOVE, this));
        GameModule.getGameModule().getGameState().addGameComponent(this);
        GameModule.getGameModule().getToolBar().add(launchButton);
        if (shouldDockIntoMainWindow()) {
            final IntConfigurer config =
                    new IntConfigurer(MAIN_WINDOW_HEIGHT, null, -1);
            Prefs.getGlobalPrefs().addOption(null, config);
            final ComponentSplitter splitter = new ComponentSplitter();

            /*
            final JXLayer<JComponent> jxl = new JXLayer<JComponent>(layeredPane);
            final DebugPainter<JComponent> dp = new DebugPainter<JComponent>();
            jxl.setPainter(dp);
            mainWindowDock = splitter.splitBottom(splitter.getSplitAncestor(GameModule.getGameModule().getControlPanel(), -1), jxl, true);
             */

            mainWindowDock = splitter.splitBottom(
                    splitter.getSplitAncestor(
                    GameModule.getGameModule().getControlPanel(), -1),
                    layeredPane, true);

            GameModule.getGameModule().addKeyStrokeSource(
                    new KeyStrokeSource(theMap, JComponent.WHEN_FOCUSED));
        } else {
            GameModule.getGameModule().addKeyStrokeSource(
                    new KeyStrokeSource(theMap,
                                        JComponent.WHEN_IN_FOCUSED_WINDOW));
        }
        // Fix for bug 1630993: toolbar buttons not appearing
        toolBar.addHierarchyListener(new HierarchyListener() {

            public void hierarchyChanged(HierarchyEvent e) {
                Window w;
                if ((w = SwingUtilities.getWindowAncestor(toolBar)) != null) {
                    w.validate();
                }
                if (toolBar.getSize().width > 0) {
                    toolBar.removeHierarchyListener(this);
                }
            }
        });

        PlayerRoster.addSideChangeListener(this);
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"), //$NON-NLS-1$
                new IntConfigurer(
                PREFERRED_EDGE_DELAY,
                Resources.getString("Map.scroll_delay_preference"), //$NON-NLS-1$
                PREFERRED_EDGE_SCROLL_DELAY));

        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"), //$NON-NLS-1$
                new BooleanConfigurer(
                MOVING_STACKS_PICKUP_UNITS,
                Resources.getString("Map.moving_stacks_preference"), //$NON-NLS-1$
                Boolean.FALSE));
    }

    public static final String VIEW_ENABLE = "ViewsEnabled";

    @Override
    public Class<?>[] getAttributeTypes() {
        Class<?>[] os = super.getAttributeTypes();
        Class<?>[] ns = new Class<?>[os.length+1];
        System.arraycopy(os, 0, ns, 0, os.length);
        ns[os.length] = Boolean.class;
        return ns;
    }
    @Override
    public String getAttributeValueString(String key) {
        if (VIEW_ENABLE.equals(key)) {
          return String.valueOf(viewsEnabled);
        }
        return super.getAttributeValueString(key);
    }
    @Override
    public void setAttribute(String key, Object val) {
        if (VIEW_ENABLE.equals(key)) {
            if (val instanceof String) {
                val = Boolean.valueOf((String) val);
            }
            if (val instanceof Boolean) {
                viewsEnabled = ((Boolean) val).booleanValue();
                viewLaunch.setEnabled(viewsEnabled);
                viewLaunch.setVisible(viewsEnabled);
            }
            return;
        }
        super.setAttribute(key, val);
    }
    @Override
    public String[] getAttributeNames() {
        String [] os = super.getAttributeNames();
        String [] ns = new String[os.length+1];
        System.arraycopy(os, 0, ns, 0, os.length);
        ns[os.length] = VIEW_ENABLE;
        return ns;
    }
    @Override
    public String[] getAttributeDescriptions() {
        String [] os = super.getAttributeNames();
        String [] ns = new String[os.length+1];
        System.arraycopy(os, 0, ns, 0, os.length);
        ns[os.length] = "Multiple Views Possible: ";
        return ns;
    }
}
