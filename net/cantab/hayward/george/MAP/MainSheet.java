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
 * This variant of a normal Sheet is used for the first Sheet within the main
 * window. This provides access to the implementation of the main window.
 *
 * @author George Hayward
 */
public class MainSheet extends Sheet {

    /**
     * The implementation of the main window for which this sheet is the top
     * one.
     */
    protected MainWindowImpl window;
    
    /**
     * The master map to which this main sheet belongs
     */
    MasterMap theMaster;

    /**
     * Create a MainSheet for the given MapSheet
     */
    public MainSheet(MapSheet m, MasterMap master) {
        super(m, null);
        theMaster = master;
    }

    /**
     * Create a MainSheet from a sequence used to save or restore games.
     */
    public MainSheet(SequenceEncoder.Decoder t, MasterMap master) {
        super(t, null);
        theMaster = master;
    }

    /**
     * Create a MainSheet as a copy of an existing one.
     */
    public MainSheet(MainSheet other) {
        super(other, null);
        theMaster = other.theMaster;
    }

    /**
     * Create whatever is needed to realise this Window and add all the sheets
     * contained to it.
     */
    public void realise() {
        super.realise();
        if (window == null) {
            window = StandardImplementers.implementations.getMainWindowImplementation(this);
        }
        window.realise();
    }

    /**
     * Delete all the objects created to realise this Window.
     */
    public void unRealise() {
        super.unRealise();
        if (window == null) {
            return;
        }
        window.unRealise();
        window = null;
    }
    
    /**
     * Return the object which realises the contents of this main sheet.
     */
    Realiser getLowerRealiser() {
        return super.getRealiser();
    }
}
