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

/**
 * This class creates the standard implementations. There is a static pointer
 * to the sole instance of this class which is used by everything wishing to
 * create an implementer. There is a public method to reset this static pointer
 * for any code which wants to provide a different implementation.
 * 
 * @author George Hayward
 */
public class StandardImplementers implements GetImplementers {
    
    protected static GetImplementers implementations = new StandardImplementers();
    
    public void setImplementations(GetImplementers g) {
        implementations = g;
    }

    @Override
    public AuxiliaryWindowImpl getAuxiliaryImplementation() {
        return new AuxiliaryWindowImplement();
    }

    @Override
    public MainWindowImpl getMainWindowImplementation() {
        return new MainWindowImplement();
    }

    @Override
    public MapImpl getMapSheetImplementation() {
        return new MapImplement();
    }

    @Override
    public SheetImpl getSheetImplementation() {
        return new SheetImplement();
    }

}
