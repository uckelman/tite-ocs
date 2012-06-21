/*
 *
 * Copyright (c) 2010 by George Hayward
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Counters;

import VASSAL.counters.GamePiece;

/**
 * Ship Counters
 * @author George Hayward
 */
public class Ship extends Unit {

    /**
     * The string which identifies this string type in save files and also the
     * piece definition file. It ends with a semicolon because that is the
     * delimiter for the subfields within a definition
     */
    public static final String ID = "sc;";

    /**
     * Return the ID
     */
    protected String myID() {
        return ID;
    }

    /**
     * Parameterless constructor
     */
    public Ship() {
        this(ID, null);
    }

    /**
     * Construct a ship counter from its type string
     * @param type
     */
    public Ship(String type, GamePiece p ) {
        super(type, p);
    }

    public String getDescription() {
        return "Ship";
    }
}
