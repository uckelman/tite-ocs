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

import java.awt.Point;

/**
 * These are all the methods which must be implemented by an object which does
 * the front end code for a Map Sheet.
 *
 * @author George Hayward
 */
public interface MapImpl {
    
    /**
     * Get the current centre of the displayed MapSheet
     */
    public Point getCurrentCentre();

    /**
     * Create whatever is needed to realise this MapSheet and add all the sheets
     * contained to it.
     */
    public void realise();
    
    /**
     * Delete all the objects created to realise this MapSheet.
     */
    public void unRealise();

    /**
     * Retrieve the object which realises this map sheet.
     */
    public Object getRealiser();

}
