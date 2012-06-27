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
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Instances of this class represent the information to redisplay a map as it
 * had been on a previous occasion. Besides saving the transform in use it also
 * saves the point of the map which was the centre of the display and any limits
 * on the scrolling in force.. The central point is saved in order maintain the
 * original display as well as possible despite possible differences in the size
 * available for display. Any scrolling limits are also saved as these will need
 * to be restored. Scrolling limits are in force when the module editor wishes
 * to limit the portion of the underlying boards visible to the player. For
 * example for a scenario which only uses part of a board or boards and for a
 * table, or off map area which is outside of the playing area for a scenario.
 *
 * @author George Hayward
 */
class Mark extends MapTransform {

    /**
     * The central point which is held in Map coordinates.
     */
    protected Point centre;
    /**
     * This rectangle in map coordinates represents the area the player can view
     * and scroll over after going to this bookmark. This may be null if there 
     * is no restriction.
     */
    Rectangle scrollingLimits;

    /**
     * Create a new mark given the required info
     */
    Mark(NewMap aMap, Point centre, double zoom, int orient, Rectangle limits) {
        super(aMap, zoom, orient);
        this.centre = new Point(centre);
        scrollingLimits = limits == null ? null : new Rectangle(limits);
    }

    /**
     * Create a new mark by reading the data from a sequence used for saving the
     * game state. The {@code Map} will have been resolved earlier as there may
     * be several marks being read with a common Map.
     */
    Mark(SequenceEncoder.Decoder t) {
        super(t);
        centre = new Point(t.nextInt(0), t.nextInt(0));
        scrollingLimits = null;
        if (t.nextBoolean(false)) {
            scrollingLimits = new Rectangle(t.nextInt(0), t.nextInt(0),
                                            t.nextInt(0), t.nextInt(0));
        }
    }

    /**
     * Initialise an instance by copying another instance
     */
    Mark(Mark other) {
        super(other);
        centre = new Point(other.centre);
        scrollingLimits = other.scrollingLimits == null ? null
                : new Rectangle(other.scrollingLimits);
    }

    /**
     * Get the {@code Map}
     */
    NewMap getMap() {
        return map;
    }

    /**
     * Get the centre. Note a copy of the existing centre is returned so the
     * caller cannot change the centre of this Mark by accident.
     */
    Point getCentre() {
        return new Point(centre);
    }

    /**
     * Change the centre of this Mark
     */
    void setCentre(Point centre) {
        this.centre = new Point(centre);
    }
}
