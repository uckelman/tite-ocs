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
package net.cantab.hayward.george.OCS;

/**
 * Define the class holding information about the text to draw
 * @author George Hayward
 */
public class Line {

    /**
     * The text to be drawn
     */
    String text;
    /**
     * The height of the text so far including this line
     */
    int height;
    /**
     * The width of this line
     */
    int width;
    /**
     * The co-ordinate at which to draw text
     */
    int y;
    /**
     * The font to be used out of the three available
     */
    int font;
}
