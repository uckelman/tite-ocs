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

import VASSAL.counters.AreaOfEffect;
import VASSAL.counters.Decorator;
import VASSAL.counters.GamePiece;
import com.sun.org.apache.bcel.internal.classfile.InnerClass;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import net.cantab.hayward.george.OCS.AreaOfEffectOverride;
import net.cantab.hayward.george.OCS.Statics;

/**
 *
 * @author George Hayward
 */
public class Airbase extends Controlled {

    /**
     * The string which identifies this string type in save files and also the
     * piece definition file. It ends with a semicolon because that is the
     * delimiter for the subfields within a definition
     */
    public static final String ID = "ab;";

    /**
     * Return the ID
     */
    protected String myID() {
        return ID;
    }

    /**
     * Parameterless constructor
     */
    public Airbase() {
        this(ID, null);
    }

    public boolean hasPZ = false;
    
    AreaOfEffectOverride drawer = new AreaOfEffectOverride(Color.GREEN);
    
    /**
     * Construct an airbase counter from its type string
     * @param type
     */
    public Airbase(String type, GamePiece p ) {
        super(p);
        drawer.setRadius(10);
    }
    
    @Override
    public void setInner(GamePiece p) {
        super.setInner(p);
        if (drawer != null) drawer.setInner(p);
    }

    public String getDescription() {
        return "Airbase";
    }
    
    public void draw(Graphics g, int x, int y, Component obs, double zoom) {
        drawer.setInner(piece);
        drawer.setActive(theSide < 0 ? false :(hasPZ & Statics.showPZs[theSide]));
        drawer.draw(g, x, y, obs, zoom);
    }
    
    public Rectangle boundingBox() {
        drawer.setInner(piece);
        drawer.setActive(theSide < 0 ? false :(hasPZ & Statics.showPZs[theSide]));
        return drawer.boundingBox();
    }
    
    public Shape getShape() {
        drawer.setInner(piece);
        drawer.setActive(theSide < 0 ? false :(hasPZ & Statics.showPZs[theSide]));
        return drawer.getShape();
    }
}
