
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
 * Instances of this class represent {@code Marks} saved by a user which differ
 * from normal Marks by having a name through which the user can recognise them..
 * 
 * @author George Hayward
 */
public class Bookmark extends Mark {
    
    /**
     * The name of this Bookmark as displayed to the user
     */
    protected String name;
    
    /**
     * Initialise an instance given all of the values needed
     */
    public Bookmark(NewMap aMap, String aName, Point centre, double zoom, int orient,
            Rectangle limits) {
        super(aMap, centre, zoom, orient, limits);
        name = aName;
    }

    /**
     * Create a new bookmark by reading the data from a sequence used for saving the
     * game state. The {@code Map} will have been resolved earlier as there may
     * be several marks being read with a common Map.
     */
    public Bookmark( SequenceEncoder.Decoder t) {
        super(t);
        name = t.nextToken("name");
    }
    
    /**
     * Encode this Bookmark into a sequence for save and restore.
     */
    protected void encode(SequenceEncoder t) {
        super.encode(t);
        t.append(name);
    }
    
    /**
     * Initialise an instance by copying another instance
     */
    public Bookmark(Bookmark other) {
        super(other);
        name = other.name;
    }

    /**
     * Get the name of this book mark
     */
    public String getName() {
        return name;
    }
    
    /**
     * Change the name of this bookmark
     */
    void setName(String aName) {
        name = aName;
    }
}
