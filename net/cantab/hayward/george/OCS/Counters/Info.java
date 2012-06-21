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
 * This class is the basis for all the counters which modify the state of the
 * unit they are associated with, such as DG mode, step loss, OOS and low ammo
 * counters
 * @author George Hayward
 */
public abstract class Info extends Marker {

    /**
     * Constructor
     */
    Info(GamePiece p) {
        super(p);
    }

    /**
     * These counters can never be concealed but are hidden instead
     */
    @Override
    protected void setSecurity( int newLevel ) {
        if ( newLevel == CONCEALED ) newLevel = HIDDEN;
        super.setSecurity(newLevel);
    }
}
