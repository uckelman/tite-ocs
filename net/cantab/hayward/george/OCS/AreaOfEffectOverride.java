/* 
 * $Id$
 *
 * Copyright (c) 2000-2011 by Rodney Kinney, Joel Uckelman, George Hayward
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

import VASSAL.configure.ColorConfigurer;
import VASSAL.counters.AreaOfEffect;
import VASSAL.counters.GamePiece;
import java.awt.Color;

/**
 *
 * @author George Hayward
 */
public class AreaOfEffectOverride extends AreaOfEffect {

  public AreaOfEffectOverride(Color a) {
    super(ID + ColorConfigurer.colorToString(a), null);
  }

  public void setActive(boolean b) {
      active = b;
      alwaysActive = false;
  }
  
  public void setRadius(int r) {
      radius = r;
  }
  
  @Override
  public void setInner(GamePiece p) {
      piece = p;
  }

}
