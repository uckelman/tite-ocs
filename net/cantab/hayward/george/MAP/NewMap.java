/* 
 *
 * Copyright (c) 2000-2012 by Rodney Kinney, Joel Uckelman, George Hayward
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
package net.cantab.hayward.george.MAP;

import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.GameComponent;
import VASSAL.build.module.map.BoardPicker;
import VASSAL.build.module.map.DrawPile;
import VASSAL.build.module.map.StackMetrics;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.MapGrid;
import VASSAL.build.module.map.boardPicker.board.Region;
import VASSAL.build.module.map.boardPicker.board.RegionGrid;
import VASSAL.build.module.map.boardPicker.board.ZonedGrid;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.command.Command;
import VASSAL.i18n.Resources;
import VASSAL.tools.SequenceEncoder;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.w3c.dom.Element;

/**
 * This class is the replacement for VASSAL.build.module.MAP which preserves the
 * back end piece storage but has none of the front end graphics or window
 * manipulation. It is not a complete re-implementation as it only does enough
 * to support the modules I wanted to convert. <P> As part of saving and loading
 * games this object saves and restores it's list of bookmarks.
 *
 * @author George Hayward
 */
public class NewMap extends OldMap implements GameComponent {

    /**
     * This is the size of the map.
     */
    protected Dimension sizeOfMap;

    /**
     * The Public Bookmarks defined in this game.
     */
    protected List<PublicBookmark> publicBookmarks;

    /**
     * The current list of private bookmarks for the current Player.
     */
    protected List<Bookmark> privateBookmarks;

    /**
     * This is the structure used to hold the private bookmarks of a single
     * player.
     */
    protected static class PlayerBookmark {

        /**
         * The player id which is his/her password.
         */
        protected String playerId;

        /**
         * The list of private bookmarks for the player.
         */
        protected Bookmark[] privateBookmarks;

        /**
         * Create new entry with given id and Bookmark arrays
         */
        protected PlayerBookmark(String playerId, Bookmark[] bookmarks) {
            this.playerId = playerId;
            privateBookmarks = bookmarks;
        }

        /**
         * Create a PlayerBookmark from a sequence used to save and restore
         * games.
         */
        protected PlayerBookmark(SequenceEncoder.Decoder t) {
            playerId = t.nextToken();
            int i, j;
            j = t.nextInt(0);
            privateBookmarks = new Bookmark[j];
            for (i = 0; i < j; i++) {
                privateBookmarks[i] = new Bookmark(t);
            }
        }

        /**
         * Encode this PlayerBookmark into a sequence used to save and restore
         * games.
         */
        protected void encode(SequenceEncoder t) {
            t.append(playerId);
            t.append(privateBookmarks.length);
            for (Bookmark b : privateBookmarks) {
                b.encode(t);
            }
        }
    }
    /**
     * All the lists of private bookmarks being held for the game
     */
    protected List<PlayerBookmark> allPrivateBookmarks;

    /**
     * Save the current set of bookmarks.
     */
    protected void saveCurrentBookmarks() {
        String b = MasterMap.currentMaster.controller.getCurrentPlayer();
        for (PlayerBookmark a : allPrivateBookmarks) {
            if (b.equals(a.playerId)) {
                a.privateBookmarks = privateBookmarks.toArray(new Bookmark[0]);
                return;
            }
        }
        allPrivateBookmarks.add(new PlayerBookmark(b, privateBookmarks.toArray(new Bookmark[0])));
    }

    /**
     * Make the private bookmarks for the given player the active ones.
     */
    protected void activateBookmarksForPlayer(String playerId) {
        privateBookmarks = new ArrayList<Bookmark>();
        for (PlayerBookmark a : allPrivateBookmarks) {
            if (playerId.equals(a.playerId)) {
                privateBookmarks = Arrays.asList(a.privateBookmarks);
                return;
            }
        }
    }

    /**
     * Player has changed. Change the private bookmarks to fit.
     */
    protected void playerChanged() {
        saveCurrentBookmarks();
        activateBookmarksForPlayer(MasterMap.currentMaster.controller.getCurrentPlayer());
    }

    /**
     * Get the default public bookmark.
     */
    protected PublicBookmark getDefaultBookmark() {
        if (publicBookmarks.size() == 0) {
            publicBookmarks.add(new PublicBookmark(this, "Main Map",
                    new Point(0, 0), 1.0, MapTransform.TOP, null));
        }
        return publicBookmarks.get(0);
    }

    /**
     * This is the command used to restore the state of the bookmarks within a
     * Map for each user.
     */
    public class myCommand extends Command {

        /**
         * The Public Bookmarks defined in this game.
         */
        protected PublicBookmark[] myPublicBookmarks;

        /**
         * All the lists of private bookmarks being held for the game.
         */
        protected PlayerBookmark[] myAllPrivateBookmarks;

        /**
         * Create a new Command to restore the current BookMarks.
         */
        protected myCommand() {
            myPublicBookmarks = publicBookmarks.toArray(new PublicBookmark[0]);
            saveCurrentBookmarks();
            myAllPrivateBookmarks = allPrivateBookmarks.toArray(new PlayerBookmark[0]);
        }

        /**
         * Create a new Command from a sequence used for restoring and saving
         * games.
         */
        protected myCommand(SequenceEncoder.Decoder t) {
            int i, j;
            j = t.nextInt(0);
            myPublicBookmarks = new PublicBookmark[j];
            for (i = 0; i < j; i++) {
                myPublicBookmarks[i] = new PublicBookmark(t);
            }
            j = t.nextInt(0);
            myAllPrivateBookmarks = new PlayerBookmark[j];
            for (i = 0; i < j; i++) {
                myAllPrivateBookmarks[i] = new PlayerBookmark(t);
            }
        }

        /**
         * Encode this Command into a sequence used to save and restore games.
         */
        protected void encode(SequenceEncoder t) {
            MasterMap.currentMaster.encode(NewMap.this, t);
            t.append(myPublicBookmarks.length);
            for (PublicBookmark a : myPublicBookmarks) {
                a.encode(t);
            }
            t.append(myAllPrivateBookmarks.length);
            for (PlayerBookmark a : myAllPrivateBookmarks) {
                a.encode(t);
            }
        }

        @Override
        protected void executeCommand() {
            publicBookmarks = Arrays.asList(myPublicBookmarks);
            allPrivateBookmarks = Arrays.asList(myAllPrivateBookmarks);
        }

        @Override
        protected Command myUndoCommand() {
            return null;
        }
    }

    /**
     * Create a Command from a sequence used to save and restore games.
     */
    public Command createCommand(SequenceEncoder.Decoder t) {
        return new myCommand(t);
    }

    /**
     * Return a Command to restore the current state of this Map.
     */
    @Override
    public Command getRestoreCommand() {
        return new myCommand();
    }

    /**
     * Initialise the Map for a Game.
     */
    protected void gameStarting() {
    }

    /**
     * Clean up the Map after a Game.
     */
    protected void gameFinishing() {
        pieces.clear();
        boards.clear();
    }

    /**
     * Add this object to its parent in the tree of defined objects in the
     * module. At this point any initialisation is done linking the object to
     * others in the module.
     *
     * @param b - The parent object
     */
    @Override
    public void addTo(Buildable b) {
        super.addTo(b);
        if (MasterMap.currentMaster == null) {
            throw new RuntimeException("Master Map Missing");
        }
        if (MasterMap.currentMaster != this) {
            MasterMap.currentMaster.subordinateMaps.add(this);
        }
    }

    /**
     * Build a new Map object from the module save file.
     */
    @Override
    public void build(Element e) {
        super.build(e);
        if (e != null) {
            // TODO: Check required subordinate object present
        } else {
            // TODO: Create standard set of subordinate elements
        }
    }

    /*
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    /**
     * Get Default window title. This has been carried across from the old map
     * system.
     *
     * @return
     */
    protected String getDefaultWindowTitle() {
        return getLocalizedMapName().length() > 0
                ? getLocalizedMapName()
                : Resources.getString("Map.window_title",
                        GameModule.getGameModule().getLocalizedGameName()); //$NON-NLS-1$
    }

    @Override
    public String getAttributeValueString(String key) {
        return null;
    }

    // TODO: Evaluate setBoardPicker requirement
    /**
     * Every map must include a {@link BoardPicker} as one of its build
     * components. This method is called by a BoardPicker instance as it is
     * added to the map.
     *
     * @param picker
     */
    @Override
    public void setBoardPicker(BoardPicker picker) {
        if (this.picker != null) {
            GameModule.getGameModule().removeCommandEncoder(picker);
            GameModule.getGameModule().getGameState().addGameComponent(picker);
        }
        this.picker = picker;
        if (picker != null) {
            picker.setAllowMultiple(allowMultiple);
            GameModule.getGameModule().addCommandEncoder(picker);
            GameModule.getGameModule().getGameState().addGameComponent(picker);
        }
    }

    // TODO: Evaluate setStackMetrics() requirement
    /**
     * Every map must include a {@link StackMetrics} as one of its build
     * components, which governs the stacking behaviour of GamePieces on the
     * map. This method is called by a StackMetrics instance as it is added to
     * the map.
     */
    public void setStackMetrics(StackMetrics sm) {
        metrics = sm;
    }

    // TODO: Evaluate setPieceMover() requirement
    //TODO: Evaluate setBoards
    /**
     * Set the boards for this map. Each map may contain more than one
     * {@link Board}.
     */
    public synchronized void setBoards(Collection<Board> c) {
        boards.clear();
        for (Board b : c) {
            b.setMap(this);
            boards.add(b);
        }
        setBoardBoundaries();
    }

    /**
     * @return the {@link Board} on this map containing the argument point
     */
    public Board findBoard(Point p) {
        for (Board b : boards) {
            if (b.bounds().contains(p)) {
                return b;
            }
        }
        return null;
    }

    /**
     *
     * @return the {@link Zone} on this map containing the argument point
     */
    public Zone findZone(Point p) {
        Board b = findBoard(p);
        if (b != null) {
            MapGrid grid = b.getGrid();
            if (grid != null && grid instanceof ZonedGrid) {
                Rectangle r = b.bounds();
                p.translate(-r.x, -r.y);  // Translate to Board co-ords
                return ((ZonedGrid) grid).findZone(p);
            }
        }
        return null;
    }

    /**
     * Search on all boards for a Zone with the given name
     *
     * @param Zone name
     * @return Located zone
     */
    public Zone findZone(String name) {
        for (Board b : boards) {
            for (ZonedGrid zg : b.getAllDescendantComponentsOf(ZonedGrid.class)) {
                Zone z = zg.findZone(name);
                if (z != null) {
                    return z;
                }
            }
        }
        return null;
    }

    /**
     * Search on all boards for a Region with the given name
     *
     * @param Region name
     * @return Located region
     */
    public Region findRegion(String name) {
        for (Board b : boards) {
            for (RegionGrid rg : b.getAllDescendantComponentsOf(RegionGrid.class)) {
                Region r = rg.findRegion(name);
                if (r != null) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Return the board with the given name
     *
     * @param name
     * @return null if no such board found
     */
    public Board getBoardByName(String name) {
        if (name != null) {
            for (Board b : boards) {
                if (name.equals(b.getName())) {
                    return b;
                }
            }
        }
        return null;
    }

    /**
     * @return true if the given point may not be a legal location. I.e., if
     * this grid will attempt to snap it to the nearest grid location
     */
    public boolean isLocationRestricted(Point p) {
        Board b = findBoard(p);
        if (b != null) {
            Rectangle r = b.bounds();
            Point snap = new Point(p);
            snap.translate(-r.x, -r.y);
            return b.isLocationRestricted(snap);
        } else {
            return false;
        }
    }

    /**
     * @return the nearest allowable point according to the {@link VASSAL.build.module.map.boardPicker.board.MapGrid}
     * on the {@link Board} at this point
     *
     * @see Board#snapTo
     * @see VASSAL.build.module.map.boardPicker.board.MapGrid#snapTo
     */
    public Point snapTo(Point p) {
        Point snap = new Point(p);

        final Board b = findBoard(p);
        if (b == null) {
            return snap;
        }

        final Rectangle r = b.bounds();
        snap.translate(-r.x, -r.y);
        snap = b.snapTo(snap);
        snap.translate(r.x, r.y);
        // RFE 882378
        // If we have snapped to a point 1 pixel off the edge of the map, move
        // back
        // onto the map.
        if (findBoard(snap) == null) {
            snap.translate(-r.x, -r.y);
            if (snap.x == r.width) {
                snap.x = r.width - 1;
            } else if (snap.x == -1) {
                snap.x = 0;
            }
            if (snap.y == r.height) {
                snap.y = r.height - 1;
            } else if (snap.y == -1) {
                snap.y = 0;
            }
            snap.translate(r.x, r.y);
        }
        return snap;
    }

    /**
     * @return a String name for the given location on the map
     *
     * @see Board#locationName
     */
    public String locationName(Point p) {
        String loc = getDeckNameAt(p);
        if (loc == null) {
            Board b = findBoard(p);
            if (b != null) {
                loc = b.locationName(new Point(p.x - b.bounds().x, p.y - b.bounds().y));
            }
        }
        if (loc == null) {
            loc = Resources.getString("Map.offboard"); //$NON-NLS-1$
        }
        return loc;
    }

    public String localizedLocationName(Point p) {
        String loc = getLocalizedDeckNameAt(p);
        if (loc == null) {
            Board b = findBoard(p);
            if (b != null) {
                loc = b.localizedLocationName(new Point(p.x - b.bounds().x, p.y - b.bounds().y));
            }
        }
        if (loc == null) {
            loc = Resources.getString("Map.offboard"); //$NON-NLS-1$
        }
        return loc;
    }

    /**
     * Return the name of the deck whose bounding box contains p
     */
    public String getDeckNameContaining(Point p) {
        String deck = null;
        if (p != null) {
            for (DrawPile d : getComponentsOf(DrawPile.class)) {
                Rectangle box = d.boundingBox();
                if (box != null && box.contains(p)) {
                    deck = d.getConfigureName();
                    break;
                }
            }
        }
        return deck;
    }

    /**
     * Return the name of the deck whose position is p
     *
     * @param p
     * @return
     */
    public String getDeckNameAt(Point p) {
        String deck = null;
        if (p != null) {
            for (DrawPile d : getComponentsOf(DrawPile.class)) {
                if (d.getPosition().equals(p)) {
                    deck = d.getConfigureName();
                    break;
                }
            }
        }
        return deck;
    }

    public String getLocalizedDeckNameAt(Point p) {
        String deck = null;
        if (p != null) {
            for (DrawPile d : getComponentsOf(DrawPile.class)) {
                if (d.getPosition().equals(p)) {
                    deck = d.getLocalizedConfigureName();
                    break;
                }
            }
        }
        return deck;
    }
}
