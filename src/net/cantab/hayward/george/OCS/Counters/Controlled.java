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
 * This is used for all markers which although they are neutral can be controlled
 * by a particular side. These are airbases, hedgehogs, supply tokens and
 * replacements. Since the side controlling one of these markers can change during
 * a game the side to which it belongs is STATE information
 * @author George Hayward
 */
public abstract class Controlled extends Marker {

    /**
     * Constructor
     */
    Controlled(GamePiece p) {
        super(p);
    }

    /**
     * Save the side for the current counter
     */
    @Override
    public void mySetState(String newState) {
        if ( newState.equals("")) return;
        theSide = Integer.parseInt(newState);
    }

    /**
     * Restore the side for the current counter
     */
    @Override
    public String myGetState() {
        return Integer.toString(theSide);
    }
}
