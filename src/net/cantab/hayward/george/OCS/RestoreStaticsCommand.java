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

import VASSAL.command.Command;

/**
 *
 * @author George Hayward
 */
public class RestoreStaticsCommand extends Command {

    /**
     * True if all forms of hidden movement are disabled
     */
    boolean hiddenMovementOff;
    /**
     * True if the range to a friendly piece affects the display of a stack
     */
    boolean rangeInUse;
    /**
     * Security to be applied to a hostile stack in a zone
     */
    int zoneSecurity;
    /**
     * Range at which opposing stacks including aircraft become invisible
     */
    int rangeAirHidden ;
    /**
     * Range at which opposing stacks become invisble
     */
    int rangeHidden;
    /**
     * Range at which opposing stackes become concealed
     */
    int rangeConcealed;
    /**
     * Range at which opposing stacks become flattened
     */
    int rangeFlattened;
    /**
     * True if 13.7 to be implemented
     */
    boolean useFormations;
    /**
     * True if ranges to be done hexagonally rather than circles
     */
    boolean hexRanges;
    /**
     * An array with the current commanders in the game
     */
    Commander [] theCommanders;
    /**
     * An array with the scenario instructions
     */
    String [] scenInstr;

    RestoreStaticsCommand() {
        hiddenMovementOff = Statics.hiddenMovementOff;
        rangeInUse = Statics.rangeInUse;
        zoneSecurity = Statics.zoneSecurity;
        useFormations = Statics.useFormations;
        rangeAirHidden = Statics.hexRangeAirHidden;
        rangeHidden = Statics.hexRangeHidden;
        rangeConcealed = Statics.hexRangeConcealed;
        rangeFlattened = Statics.hexRangeFlattened;
        hexRanges = Statics.hexRanges;
        int i;
        int j = 0;
        for ( i = 0; i < Statics.theCommanders.length; i++ ) {
            if ( Statics.theCommanders[i].isActive() ) j++;
        }
        theCommanders = new Commander[j];
        j = 0;
        for ( i = 0; i < Statics.theCommanders.length; i++ ) {
            if ( Statics.theCommanders[i].isActive() )
                theCommanders[j++] = new Commander( Statics.theCommanders[i]);
        }
        if (Statics.scenarioInstructions != null) {
            scenInstr = new String[Statics.scenarioInstructions.length];
            System.arraycopy(Statics.scenarioInstructions, 0, scenInstr, 0, Statics.scenarioInstructions.length);
        }
    }

    RestoreStaticsCommand(boolean a, boolean b, int c, int h, int d, int e, int f, int g) {
        hiddenMovementOff = a;
        rangeInUse = b;
        zoneSecurity = c;
        rangeAirHidden = h;
        rangeHidden = d;
        rangeConcealed = e;
        rangeFlattened = f;
        theCommanders = new Commander[g];
    }

    /**
     * Returns the command to undo this command
     * @return
     */
    public Command myUndoCommand() {
        return null;
    }

    /**
     * Executes the command
     */
    @SuppressWarnings("static-access")
    public void executeCommand() {
        Statics.hiddenMovementOff = this.hiddenMovementOff;
        Statics.rangeInUse = this.rangeInUse;
        Statics.zoneSecurity = this.zoneSecurity;
        Statics.rangeAirHidden = Statics.convertHexesToPixels(this.rangeAirHidden);
        Statics.rangeHidden = Statics.convertHexesToPixels(this.rangeHidden);
        Statics.rangeConcealed = Statics.convertHexesToPixels(this.rangeConcealed);
        Statics.rangeFlattened = Statics.convertHexesToPixels(this.rangeFlattened);
        Statics.hexRangeAirHidden = this.rangeAirHidden;
        Statics.hexRangeConcealed = this.rangeConcealed;
        Statics.hexRangeFlattened = this.rangeFlattened;
        Statics.hexRangeHidden = this.rangeHidden;
        Statics.theCommanders = theCommanders;
        Statics.scenarioInstructions = scenInstr;
        Statics.useFormations = useFormations;
        Statics.hexRanges = hexRanges;
    }
}
