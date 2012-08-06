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

/**
 * Instances of this class are responsible for drawing portions of maps upon the
 * display.
 *
 * @author George Hayward
 */
public class MapSheet extends Mark {

    /**
     * The Sheet to which this MapSheet belongs
     */
    protected Sheet sheet;

    /**
     * The implementation of this MapSheet
     */
    MapImpl implementer;

    /**
     * Create a new MapSheet for the given Mark
     */
    public MapSheet(Mark m) {
        super(m);
    }

    /**
     * Create a new MapSheet by reading the data from a sequence used for saving
     * and restoring the game state.
     */
    public MapSheet(SequenceEncoder.Decoder t) {
        super(t);
    }

    /**
     * Encode this MapSheet into a sequence for save and restore.
     */
    protected void encode(SequenceEncoder t) {
        centre = implementer.getCurrentCentre();
        super.encode(t);
    }

    /**
     * Create a new map sheet as a copy of an existing one.
     */
    public MapSheet(MapSheet other) {
        super(other);
    }

    /**
     * Create whatever is needed to realise this MapSheet.
     */
    public void realise() {
        if (implementer == null) {
            implementer = StandardImplementers.implementations.getMapSheetImplementation(this);
        }
        implementer.realise();
    }

    /**
     * Delete all the objects created to realise this MapSheet.
     */
    public void unRealise() {
        if (implementer == null) {
            return;
        }
        implementer.unRealise();
        implementer = null;
    }
    
    
    /**
     * Get the object that realises this sheet.
     */
    public Realiser getRealiser() {
        if (implementer == null) {
            return null;
        }
        return implementer.getRealiser();
    }

}
