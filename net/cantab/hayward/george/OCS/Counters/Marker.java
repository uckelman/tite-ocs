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
import net.cantab.hayward.george.OCS.OcsCounter;

/**
 * This is the abstract class which forms the basis of all neutral markers in
 * OCS.
 * @author George Hayward
 */
public abstract class Marker extends OcsCounter {

    /**
     * Class constructor. Set the default security.
     */
    public Marker( GamePiece p) {
        super(p);
    }
}
