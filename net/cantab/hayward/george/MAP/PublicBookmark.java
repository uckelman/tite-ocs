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
import java.awt.Point;
import java.awt.Rectangle;

/**
 * Instances of this class represent bookmarks created by the editor of a
 * scenario which are available to all the players of a game. Typically these allow
 * quick access to charts, entry points, turn tables etc.  These bookmarks can be
 * disabled in which case they do appear outside of edit mode. This is just a
 * convenience for the scenario developer as he can create all the possible
 * public bookmarks in a single scenario file and then just disable those irrelevant
 * to each individual scenario as he creates it.
 * 
 * @author George Hayward
 */
public class PublicBookmark extends Bookmark {
    
    /**
     * This is true if the bookmark is disabled and not to be shown to players.
     */
    boolean disabled;
    
    /**
     * Create an instance from all the required parameters. Public Bookmarks are
     * always created enabled. The scenario developer can change this subsequently.
     */
    PublicBookmark(NewMap aMap, String aName, Point centre, double zoom, int orient,
            Rectangle limits) {
        super(aMap, aName, centre, zoom, orient, limits);
        disabled = false;
    }
    
    /**
     * Create a new bookmark by reading the data from a sequence used for saving the
     * game state. The {@code Map} will have been resolved earlier as there may
     * be several marks being read with a common Map.
     */
    PublicBookmark(SequenceEncoder.Decoder t) {
        super(t);
        disabled = t.nextBoolean(false);
    }
}
