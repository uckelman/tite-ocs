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

package net.cantab.hayward.george.OCS;

/**
 *
 * @author George Hayward
 */
public class Side {

    /**
     * The name of the side
     */
    public String name;

    /**
     * True if this side is controlled by the current player
     */
    public boolean controlled;

    /**
     * Create a side with the given name
     */
    Side (String newName ) {
        name = newName;
    }
}
