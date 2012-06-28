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

import VASSAL.tools.SequenceEncoder;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * This class defines the possible transformations of a map from its internal
 * form to the way it is displayed. It can be zoomed to a different scale and/or
 * rotated orthogonally. This class also provides the coordinate conversion
 * methods required between the map coordinates that everything behind the
 * scenes works in and the zoomed and rotated coordinates the system will
 * express drawing requests and mouse events in, for the view displaying the map
 * with this transformation. This class is the base class for all bookmarks and
 * all internal views which link with views actually displaying the map.
 *
 * @author George Hayward
 */
public class MapTransform {

    /**
     * The {@code Map} do which this MapTransform refers.
     */
    protected NewMap map;
    /**
     * This is the factor the map is to be scaled by for display
     */
    protected double zoom;
    /**
     * This is the orientation in which the map is to be displayed. It is
     * specified by the where the top of the map will appear when displayed.
     * Only the bottom two bits of this field are looked at so the value is
     * always valid.
     */
    protected int orient;
    /**
     * This indicates that the top of the map should appear at the top of the
     * display
     */
    static final int TOP = 0;
    /**
     * This indicates that the top of the map should appear at the right of the
     * display
     */
    static final int RIGHT = 1;
    /**
     * This indicates that the top of the map should appear at the bottom of the
     * display
     */
    static final int BOTTOM = 2;
    /**
     * This indicates that the top of the map should appear at the left of the
     * display
     */
    static final int LEFT = 3;
    /**
     * This is the inverse of the zoom factor which is used for conversions from
     * display coordinates to map coordinates. It is calculated from the zoom
     * when set to avoid divides in the conversion methods.
     */
    protected double unZoom;

    /**
     * Initialise a new instance given the initial zoom and orientation.
     */
    public MapTransform(NewMap aMap, double zoom, int orient) {
        map = aMap;
        if (zoom == 0.0) zoom = 1.0;
        this.zoom = zoom;
        this.orient = orient & 3;
        unZoom = 1.0 / zoom;
    }

    /**
     * Initialise an instance by reading the zoom and orientation from a
     * sequence used for saving the game state.
     */
    public MapTransform(SequenceEncoder.Decoder t) {
        this(MasterMap.currentMaster.getMapFrom(t),
                t.nextDouble(1.0), t.nextInt(0));
    }
    
    /**
     * Encode this MapTransform into a sequence for save and restore.
     */
    protected void encode(SequenceEncoder t) {
        MasterMap.currentMaster.encode(map, t);
        t.append(zoom);
        t.append(orient);
    }
    
    /**
     * Initialise an instance by copying another instance
     */
    public MapTransform(MapTransform other) {
        map = other.map;
        zoom = other.zoom;
        orient = other.orient;
    }

    /**
     * Return the current scaling factor
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Return the current orientation
     */
    public int getOrientation() {
        return orient & 3;
    }

    /**
     * Change the scale factor
     */
    void setZoom(double zoom) {
        if (zoom == 0.0) zoom = 1.0;
        this.zoom = zoom;
        unZoom = 1.0 / zoom;
    }

    /**
     * Change the orientation to display at
     */
    void setOrientation(int orient) {
        this.orient = orient & 3;
    }

    /**
     * Convert a Dimension from Map Coordinates to Zoomed Rotated Coordinates.
     *
     * A new Dimension is created and the old one left unchanged.
     *
     * @param d the Dimension to convert
     * @return the converted Dimension.
     */
    public final Dimension convertMCtoZRC(Dimension d) {
        if (orient == LEFT || orient == RIGHT) {
            return new Dimension((int) (zoom * d.height),
                                 (int) (zoom * d.width));
        }
        return new Dimension((int) (zoom * d.width),
                             (int) (zoom * d.height));
    }

    /**
     * Convert a Point from Map Coordinates to Zoomed Rotated Coordinates.
     *
     * A new Point is created and the old one left unchanged.
     *
     * @param p the Point to convert
     * @return the converted Point
     */
    public final Point convertMCtoZRC(Point p) {
        switch (orient) {
        default:
            return new Point((int) (zoom * p.x),
                             (int) (zoom * p.y));
        case 1:
            return new Point((int) (zoom * (map.sizeOfMap.height - p.y)),
                             (int) (zoom * p.x));
        case 2:
            return new Point((int) (zoom * (map.sizeOfMap.width - p.x)),
                             (int) (zoom * (map.sizeOfMap.height - p.y)));
        case 3:
            return new Point((int) (zoom * p.y),
                             (int) (zoom * (map.sizeOfMap.width - p.x)));
        }
    }
    
    /**
     * Convert a vector in map coordinates to the equivalent vector in Zoomed
     * Rotated Coordinates
     */
    public final Point vectorMCtoZRC(Point p) {
        switch (orient) {
        default:
            return new Point((int) (zoom * p.x),
                             (int) (zoom * p.y));
        case 1:
            return new Point((int) (zoom *  -p.y),
                             (int) (zoom * p.x));
        case 2:
            return new Point((int) (zoom * -p.x),
                             (int) (zoom * -p.y));
        case 3:
            return new Point((int) (zoom * p.y),
                             (int) (zoom * -p.x));
        }
    }
    
    /**
     * Convert a Rectangle from Map Coordinates to Zoomed Rotated Coordinates.
     *
     * A new Rectangle is created and the old one left unchanged.
     *
     * @param r the Rectangle to convert
     * @return the converted Rectangle
     */
    public final Rectangle convertMCtoZRC(Rectangle r) {
        switch (orient) {
        default:
            return new Rectangle((int) (zoom * r.x),
                                 (int) (zoom * r.y),
                                 (int) (zoom * r.width),
                                 (int) (zoom * r.height));
        case 1:
            return new Rectangle((int) (zoom * (map.sizeOfMap.height - (r.y + r.height))),
                                 (int) (zoom * r.x),
                                 (int) (zoom * r.height),
                                 (int) (zoom * r.width));
        case 2:
            return new Rectangle((int) (zoom * (map.sizeOfMap.width - (r.x + r.width))),
                                 (int) (zoom * (map.sizeOfMap.height - (r.y + r.height))),
                                 (int) (zoom * r.width),
                                 (int) (zoom * r.height));
        case 3:
            return new Rectangle((int) (zoom * r.y),
                                 (int) (zoom * (map.sizeOfMap.width - (r.x + r.width))),
                                 (int) (zoom * r.height),
                                 (int) (zoom * r.width));
        }
    }

    /**
     * Convert a Dimension from Zoomed Rotated Coordinates to Map Coordinates.
     *
     * A new Dimension is created and the old one left unchanged.
     *
     * @param d the Dimension to convert
     * @return the converted Dimension.
     */
    public final Dimension convertZRCtoMC(Dimension d) {
        if (orient == 1 || orient == 3) {
            return new Dimension((int) (unZoom * d.height),
                                 (int) (unZoom * d.width));
        }
        return new Dimension((int) (unZoom * d.width),
                             (int) (unZoom * d.height));
    }

    /**
     * Convert a Point from Zoomed Rotated Coordinates to Map Coordinates.
     *
     * A new Point is created and the old one left unchanged.
     *
     * @param p the Point to convert
     * @return the converted Point.
     */
    public final Point convertZRCtoMC(Point p) {
        switch (orient) {
        default:
            return new Point((int) (unZoom * p.x),
                             (int) (unZoom * p.y));
        case 1:
            return new Point((int) (unZoom * p.y),
                             (int) (map.sizeOfMap.width - unZoom * p.x));
        case 2:
            return new Point((int) (map.sizeOfMap.width - unZoom * p.x),
                             (int) (map.sizeOfMap.height - unZoom *  p.y));
        case 3:
            return new Point((int) (map.sizeOfMap.height - unZoom *  p.y),
                             (int) (unZoom * p.x));
        }
    }

    /**
     * Convert a Rectangle from Zoomed Rotated Coordinates to Map Coordinates
     *
     * A new Rectangle is created and the old one left unchanged.
     *
     * @param r the Rectangle to convert
     * @return the converted Rectangle
     */
    public final Rectangle convertZRCtoMC(Rectangle r) {
        switch (orient) {
        default:
            return new Rectangle((int) (unZoom * r.x),
                                 (int) (unZoom * r.y),
                                 (int) (unZoom * r.width),
                                 (int) (unZoom * r.height));
        case 1:
            return new Rectangle((int) (unZoom * r.y),
                                 (int) (map.sizeOfMap.width - unZoom * r.x),
                                 (int) (unZoom * r.height),
                                 (int) (unZoom * r.width));
        case 2:
            return new Rectangle((int) (map.sizeOfMap.width - unZoom * r.x),
                                 (int) (map.sizeOfMap.height - unZoom *  r.y),
                                 (int) (unZoom * r.width),
                                 (int) (unZoom * r.height));
        case 3:
            return new Rectangle((int) (map.sizeOfMap.height - unZoom *  r.y),
                                 (int) (unZoom * r.x),
                                 (int) (unZoom * r.height),
                                 (int) (unZoom * r.width));
        }
    }
    
}
