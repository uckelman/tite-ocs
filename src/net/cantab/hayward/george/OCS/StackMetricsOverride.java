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

import net.cantab.hayward.george.OCS.StackOverride;
import VASSAL.build.module.Map;
import VASSAL.build.module.map.StackMetrics;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Stack;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author George Hayward
 */
public class StackMetricsOverride  extends StackMetrics {

    @Override
    public StackOverride createStack(GamePiece p, boolean force) {
        return isStackingEnabled() || force ? new StackOverride(p) : null;
    }

    @Override
    public void draw(Stack stack, Point location, Graphics g, Map map, double zoom, Rectangle visibleRect) {
        if ( stack instanceof StackOverride ) {
            ( (StackOverride) stack ).checkVisibility();
        }
        super.draw(stack, location, g, map, zoom, visibleRect);
    }

    @Override
    public void draw(Stack stack, Graphics g, int x, int y, Component obs, double zoom) {
        if ( stack instanceof StackOverride ) {
            ( (StackOverride) stack ).checkVisibility();
        }
        super.draw(stack, g, x, y, obs, zoom);
    }

    public int getUnexSepX() {
        return unexSepX;
    }

    public int getUnexSepY() {
        return unexSepY;
    }
}
