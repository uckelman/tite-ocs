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
package net.cantab.hayward.george.OCS;

import VASSAL.build.module.Map;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.GeometricGrid;
import VASSAL.build.module.map.boardPicker.board.MapGrid;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

/**
 *
 * @author George Hayward
 */
public class ZoneDrawer {

    Color colour;
    boolean radiusIs10;
    protected static final float transparencyLevel = 0.3F;

    public ZoneDrawer(Color colour, boolean radiusIs10) {
        this.colour = colour;
        this.radiusIs10 = radiusIs10;
    }

    public void draw(Graphics g, int x, int y, Component obs, double zoom, Map theMap,
                     Point position) {
        if (theMap != Statics.theMap) return;
        final Graphics2D g2d = (Graphics2D) g;

        final Color oldColor = g2d.getColor();
        g2d.setColor(colour);

        final Composite oldComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(
            AlphaComposite.SRC_OVER, transparencyLevel));

        Area a = getArea(theMap, position);

        if (a == null) return;

        AffineTransform t = AffineTransform.getTranslateInstance(x, y);

        if (zoom != 1.0) {
            t.scale(zoom, zoom);
        }

        a = new Area(t.createTransformedShape(a));

        g2d.fill(a);

        g2d.setColor(oldColor);
        g2d.setComposite(oldComposite);
    }
    static Area rad10 = null;
    static Area rad1 = null;

    public Area getArea(Map map, Point mapPosition) {
        Area a;

        a = radiusIs10 ? rad10 : rad1;

        if (a == null) {
            final int myRadius = radiusIs10 ? 10 : 1;

            final Board board = map.findBoard(mapPosition);
            final MapGrid grid = board == null ? null : board.getGrid();

            if (grid instanceof GeometricGrid) {
                final GeometricGrid gGrid = (GeometricGrid) grid;
                final Rectangle boardBounds = board.bounds();
                final Point boardPosition = new Point(
                    mapPosition.x - boardBounds.x, mapPosition.y - boardBounds.y);

                a = gGrid.getGridShape(boardPosition, myRadius); // In board co-ords
                
                AffineTransform t = 
                    AffineTransform.getTranslateInstance(-boardPosition.x, -boardPosition.y);
                
                a = new Area(t.createTransformedShape(a));

            } else {
                return null;
            }
            if (radiusIs10) {
                rad10 = a;
            } else {
                rad1 = a;
            }
        }

        return a;
    }
}
