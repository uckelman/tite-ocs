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
 * This variant of a normal Sheet is used for the first Sheet within an
 * auxiliary window. This provides access to the implementation of the auxiliary
 * window.
 * @author George Hayward
 */
public class TopSheet extends Sheet {
    
    /**
     * The implementation of the auxiliary window for which this sheet is the
     * top one.
     */
    protected AuxiliaryWindowImpl window;
    
    /**
     * Create a TopSheet for the given MapSheet
     */
    public TopSheet(MapSheet m) {
        super(m, null);
    }
    
    /**
     * Create a TopSheet from a sequence used to save or restore games.
     */
    public TopSheet(SequenceEncoder.Decoder t) {
        super(t, null);
    }
    
    /**
     * Create a TopSheet as a copy of an existing one.
     */
    public TopSheet (TopSheet other) {
        super(other, null);
    }

}
