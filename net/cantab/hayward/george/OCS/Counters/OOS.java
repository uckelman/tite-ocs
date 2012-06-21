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

package net.cantab.hayward.george.OCS.Counters;

import VASSAL.counters.GamePiece;

/**
 * This is the "out of supply" marker which goes over a unit and is VISIBLE if
 * the unit is VISIBLE. It differs from other {@code Over} markers in that its
 * visibility is unaffected by the others and does not affect the others
 * @author George Hayward
 */
public class OOS extends Over {

    /**
     * The string which identifies this string type in save files and also the
     * piece definition file. It ends with a semicolon because that is the
     * delimiter for the subfields within a definition
     */
    public static final String ID = "oc;";

    /**
     * Return the ID
     */
    @Override
    protected String myID() {
        return ID;
    }

    /**
     * Parameterless constructor
     */
    public OOS() {
        this(ID, null);
    }

    /**
     * Construct a transport counter from its type string
     * @param type
     */
    public OOS(String type, GamePiece p ) {
        super(type, p);
    }

    @Override
    public String getDescription() {
        return "Out of supply marker";
    }
}
