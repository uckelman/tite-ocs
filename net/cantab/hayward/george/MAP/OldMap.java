/* 
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
package net.cantab.hayward.george.MAP;

import VASSAL.build.Buildable;
import VASSAL.build.module.map.BoardPicker;
import VASSAL.build.module.map.StackMetrics;
import VASSAL.build.module.map.Zoomer;
import VASSAL.build.module.map.boardPicker.Board;
import java.awt.*;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.Enumeration;
import javax.swing.*;
import org.w3c.dom.Element;

/**
 * This class extends the original {code}VASSAL.build.module.Map{/code} and
 * hides all its front end methods so they cannot be called. This is because
 * they are incompatible with the new implementation of Map.Those methods called
 * from standard Vassal are converted into the correct calls for the new
 * implementation.
 *
 * @author George Hayward
 */
public class OldMap extends VASSAL.build.module.Map {

    public OldMap() {
        /*
         * Undo thinds done in old constructor
         */
        toolBar = null;
        theMap = null;
    }

    @Override
    public void setAttribute(String key, Object value) {
        // TODO: Convert old attrbutes to new ones
    }

    // getAttributeValueString handled by new implementation
    @Override
    public void build(Element e) {
        if (e != null) {
            super.build(e);
            launchButton = null; // undo this
        }
    }

    // setBoardpicker handled by new implementation
    @Override
    public BoardPicker getBoardPicker() {
        return null;
    }

    @Override
    public void setZoomer(Zoomer z) {
    }

    @Override
    public Zoomer getZoomer() {
        return null;
    }

    // setStackMetrics handled by new implementation
    @Override
    public StackMetrics getStackMetrics() {
        return null;
    }

    @Override
    public double getZoom() {
        return 1.0;
    }

    @Override
    public JToolBar getToolBar() {
        // TODO: Evaluate uses of getToolBar()
        return null;
    }

    //TODO: evaluate add/remove Draw Components
    @Override
    public void addTo(Buildable b) {
        // Skip the old addTo as it does a lot of front ebd stuff
        try {
            Method m = getClass().getSuperclass().getSuperclass().getDeclaredMethod("addTo", Buildable.class);
            m.invoke(this, b);
        } catch (Exception e) {
        }
    }

    //TODO: setPieceMover() in new implementation
    @Override
    public void removeFrom(Buildable b) {
        // Skip the old removeFrom as it does a lot of front end stuff
        try {
            Method m = getClass().getSuperclass().getSuperclass().getDeclaredMethod("removeFrom", Buildable.class);
            m.invoke(this, b);
        } catch (Exception e) {
        }
    }

    @Override
    public void sideChanged(String oldSide, String newSide) {
        // Evaluate side change listener
    }

    // setBoards in new implementation
    @Override
    public void setBoards(Enumeration<Board> c) {
    }

    // getRestoreCommand in new implementation
    // TODO: Check blocked methods never called
    @Override
    public Dimension getPreferredSize() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public Dimension mapSize() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public Dimension getEdgeBuffer() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public Point mapCoordinates(Point p) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public Rectangle mapRectangle(Rectangle r) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public Point componentCoordinates(Point p) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public Rectangle componentRectangle(Rectangle r) {
        throw new RuntimeException("Should never be called");
    }

    // TODO: Evaluate isvisibleToAll()
    @Override
    public void addLocalMouseListener(MouseListener l) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void addLocalMouseListenerFirst(MouseListener l) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void removeLocalMouseListener(MouseListener l) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void pushMouseListener(MouseListener l) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void popMouseListener() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void enableKeyListeners() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void disableKeyListeners() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void setDragGestureListener(DragGestureListener dragGestureListener) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public DragGestureListener getDragGestureListener() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        throw new RuntimeException("Should never be called");
    }

    // TODO: evaluate scrollAtEdge()
    @Override
    public void repaint(boolean cf) {
        //TODO: Take action on repaint
    }

    @Override
    public void paint(Graphics g) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void paintRegion(Graphics g, Rectangle visibleRect) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void paintRegion(Graphics g, Rectangle visibleRect, Component c) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void drawBoardsInRegion(Graphics g,
            Rectangle visibleRect,
            Component c) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void drawBoardsInRegion(Graphics g, Rectangle visibleRect) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void repaint() {
        //TDO: action on repaint
    }

    @Override
    public void drawPiecesInRegion(Graphics g,
            Rectangle visibleRect,
            Component c) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void drawPiecesInRegion(Graphics g, Rectangle visibleRect) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void drawPieces(Graphics g, int xOffset, int yOffset) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void drawDrawable(Graphics g, boolean aboveCounters) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void paint(Graphics g, int xOffset, int yOffset) {
        throw new RuntimeException("Should never be called");
    }

    //TODO: getHighLoghter
    //TODO: setHighLighter
    //TODO: addHighLighter
    //TODO: removeHighLighter
    //TODO getHighLighters
    //TODO: getBoards, getBoardCount
    //TODO: boundingBoxOf
    //TODO selectionBoundsOf
    //TODO positionOf
    /*
     * Carried forward from old code:
     *
     * getpieces getAllPieces setPieceCollection getPieceCollection
     */
    @Override
    protected void clearMapBorder(Graphics g) {
        throw new RuntimeException("Should never be called");
    }

    /*
     * Carried forward from old code
     *
     * setBoardBoundaries getLocation getLocation
     */
    @Override
    public void drawBoards(Graphics g, int xoffset, int yoffset, double zoom, Component obs) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public void repaint(Rectangle r) {
        //TODO: repaint action
    }

    /*
     * Carried forward from old code setPiecesVisible isPiecesVisible
     * getPieceOpacity setPieceOpacity getProperty getLocalisedProperty
     */
    @Override
    public KeyStroke getMoveKey() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    protected Window createParentFrame() {
        throw new RuntimeException("Should never be called");
    }

    @Override
    public boolean shouldDockIntoMainWindow() {
        throw new RuntimeException("Should never be called");
    }

    //TODO: setup action
    @Override
    public void appendToTitle(String s) {
        throw new RuntimeException("Should never be called");
    }

    @Override
    protected String getDefaultWindowTitle() {
        throw new RuntimeException("Should never be called");
    }

    /*
     * Carried over from old code
     *
     * findPiece findAnyPiece placeAt apply placeOrmerge addPiece reposition
     * indexOf removePiece
     */
    //TODO: evaluate centerAt, ensureVisible, scroll
    /*
     * Carried over from old code getMapName getLocalizedMapName setMapName
     * getHelpFile
     */
    //TODO: getAttributeDescription
    //TODO: getAttributeNames
    //TODO:getAttributeTypes
    /*
     * Carrid over from old code
     *
     * getChangeFormat getMoveToFormat getMoveWithinFormat
     */
    //TODO: getAllowableConfigureComponent
    //TODO: getAttributeVisibility
    /*
     * Carried over from old code
     *
     * setId getMapbyId getMapList getAllMaps getMutableProperty
     * addMutableProperty removeMutableProperty getId getIdentifier
     *
     */
    @Override
    public JComponent getView() {
        theMap = new JPanel(); // needed for call in constructor
        return null;
    }

    @Override
    public JLayeredPane getLayeredPane() {
        throw new RuntimeException("Should never be called");
    }
}
