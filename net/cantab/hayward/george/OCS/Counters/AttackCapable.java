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

import VASSAL.counters.AreaOfEffect;
import VASSAL.counters.Decorator;
import VASSAL.counters.GamePiece;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import net.cantab.hayward.george.OCS.ZoneDrawer;
import net.cantab.hayward.george.OCS.OcsCounter;
import net.cantab.hayward.george.OCS.Statics;

/**
 * Attack capable Combat Units
 *
 * @author George Hayward
 */
public class AttackCapable extends Land {

    /**
     * The string which identifies this string type in save files and also the
     * piece definition file. It ends with a semicolon because that is the
     * delimiter for the subfields within a definition
     */
    public static final String ID = "ca;";

    /**
     * Return the ID
     */
    protected String myID() {
        return ID;
    }

    /**
     * Parameterless constructor
     */
    public AttackCapable() {
        this(ID, null);
    }
    ZoneDrawer drawer = new ZoneDrawer(Color.RED, false);

    /**
     * Construct an artillery counter from its type string
     *
     * @param type
     */
    public AttackCapable(String type, GamePiece p) {
        super(type, p);
    }

    public String getDescription() {
        return "Attack Capable Combat Unit";
    }
    public boolean hasZOC = false;

    public void draw(Graphics g, int x, int y, Component obs, double zoom) {
        if (theSide < 0 ? false : (security >= OcsCounter.VISIBLE & hasZOC
                                   & Statics.showZOCs[theSide])) {
            drawer.draw(g, x, y, obs, zoom, getMap(), getPosition());
        }
        super.draw(g, x, y, obs, zoom);
    }
}
