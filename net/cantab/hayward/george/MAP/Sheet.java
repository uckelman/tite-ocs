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
 * Instances of this class define a view in a window which is either a MapSheet
 * displaying a portion of the Map or is divided (vertically or horizontally)
 * into two separate sheets.
 *
 * @author George Hayward
 */
public class Sheet {

    /**
     * This is the sheet which contains this sheet as a subdivision. If it is
     * null then the sheet is the top one in a window.
     */
    protected Sheet parent;

    /**
     * The MapSheet which is this Sheet if any. If this is null then it implies
     * the sheet is divided into two
     */
    protected MapSheet mapSheet;

    /**
     * This is true if the Sheet is divided horizontally. Otherwise it is
     * vertically divided.
     */
    protected boolean horizontal;

    /**
     * This is the size of the top (or left) subdivision as a percentage of the
     * height (or width).
     */
    protected int topOrLeftPercent;

    /**
     * This is the sheet which occupies the top (or left) subdivision.
     */
    protected Sheet topOrLeft;

    /**
     * This is the sheet which occupies the bottom (or right) subdivision.
     */
    protected Sheet bottomOrRight;
    
    /**
     * This is the object which is implementing the Sheet (if divided) within
     * the actual window system. If this is null then the Sheet is not actually
     * implemented yet and is only potential.
     */
    protected SheetImpl implementer;
    
    /**
     * Create a new sheet for the given MapSheet.
     */
    Sheet(MapSheet mapSheet) {
        this.mapSheet = mapSheet;
        mapSheet.sheet = this;
    }
    
    /**
     * Create a new sheet from the information from a sequence used to save and
     * restore the game state.
     */
    Sheet (SequenceEncoder.Decoder t) {
        switch (t.nextChar('M')) {
            case 'M':
                mapSheet = new MapSheet(t);
                mapSheet.sheet = this;
                break;
            case 'H':
                horizontal = true;
            case 'V':
                topOrLeftPercent = t.nextInt(50);
                topOrLeft = new Sheet(t);
                bottomOrRight = new Sheet(t);
                break;
            default:
                throw new RuntimeException("Bad Game State Sequence");
        }
    }

}
