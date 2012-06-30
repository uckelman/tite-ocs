/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
 * These are all the methods which must be implemented by an object which does
 * the front end code for the main window.
 * @author George Hayward
 */
public interface MainWindowImpl {

    /**
     * Create whatever is needed to realise this window (or portion of a window)
     * and add all the sheets to it.
     */
    public void realise();
    
    /**
     * Delete all the objects created to realise this window.
     */
    public void unRealise();

}
