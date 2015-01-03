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

import VASSAL.build.module.ObscurableOptions;
import VASSAL.command.ChangeTracker;
import VASSAL.command.Command;
import VASSAL.counters.Decorator;
import VASSAL.counters.GamePiece;
import VASSAL.counters.KeyBuffer;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.Obscurable;
import VASSAL.counters.Properties;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

/**
 *
 * @author George Hayward
 */
public class ObscurableOverride extends Obscurable {
    
    public ObscurableOverride() {
        super();
    }
    
    public ObscurableOverride(String type, GamePiece inner ) {
        super(type, inner);
    }

    @Override
    public boolean isMaskable() {
        if ( !Statics.hiddenMovementOff ) return false;
        return true;
    }

    @Override
    public boolean obscuredToMe() {
        GamePiece c = getOutermost(this);
        if ( !(c instanceof OcsCounter)) return false;
        OcsCounter b = (OcsCounter)c;
        if ( !Statics.hiddenMovementOff ) {
            return (b.security == OcsCounter.CONCEALED );
        }
        return obscuredBy != null;
    }

    @Override
    public KeyCommand[] myGetKeyCommands() {
        if ( !Statics.hiddenMovementOff ) {
            return new KeyCommand[0];
        }
        return super.myGetKeyCommands();
    }

    @Override
    public Command myKeyEvent(KeyStroke stroke) {
    if ( !Statics.hiddenMovementOff ) {
        return null;
    }
    Command retVal = null;
    myGetKeyCommands();

    if (hide.matches(stroke)) {
      final ChangeTracker c = new ChangeTracker(this);
      if (obscuredToOthers() || obscuredToMe()) {
        obscuredBy = null;
      }
      else if (!obscuredToMe()) {
        obscuredBy = Statics.curCommander.password;
      }

      retVal = c.getChangeCommand();
    }
    else if (peek.matches(stroke)) {
      if (obscuredToOthers() &&
          Boolean.TRUE.equals(getProperty(Properties.SELECTED))) {
        peeking = true;
      }
    }

    // For the "peek" display style with no key command (i.e. appears
    // face-up whenever selected).
    //
    // It looks funny if we turn something face down but we can still see it.
    // Therefore, un-select the piece if turning it face down
    if (retVal != null && PEEK == displayStyle &&
        peekKey == null && obscuredToOthers()) {
// FIXME: This probably causes a race condition. Can we do this directly?
      Runnable runnable = new Runnable() {
        public void run() {
          KeyBuffer.getBuffer().remove(Decorator.getOutermost(ObscurableOverride.this));
        }
      };
      SwingUtilities.invokeLater(runnable);
    }
    return retVal;
  }

}
