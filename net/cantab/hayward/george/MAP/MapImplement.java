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
 *
 * @author George Hayward
 */
public class MapImplement implements MapImpl {
    
    protected MapImplement(MapSheet master) {
        
    }

    @Override
    public Point getCurrentCentre() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void realise() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void unRealise() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object getRealiser() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
