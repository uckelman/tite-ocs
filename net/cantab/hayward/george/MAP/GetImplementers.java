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
 * These are the methods which return the implementations needed to realise
 * the windows and sheets within a particular window system or with a
 * particular style of interface.
 * 
 * @author George Hayward
 */
public interface GetImplementers {
    
    public AuxiliaryWindowImpl getAuxiliaryImplementation();
    
    public MainWindowImpl getMainWindowImplementation();
    
    public MapImpl getMapSheetImplementation();
    
    public SheetImpl getSheetImplementation();

}
