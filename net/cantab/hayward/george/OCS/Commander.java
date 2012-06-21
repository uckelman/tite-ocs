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

import net.cantab.hayward.george.OCS.Statics;
import VASSAL.build.GameModule;

/**
 *
 * This defines a user in the game who is a commander on one side or the other
 * @author George Hayward
 */
public class Commander {

    /**
     * The name of the user
     */
    String name;

    /**
     * The password of the user
     */
    String password;
    /**
     * sides commanded by this user
     */
    boolean [] sidesCommanded;
    /**
     * sides this user wishes to command
     */
    boolean [] sidesRequested;
    /**
     * sides this user has been rejected from
     */
    boolean [] sidesRejected;
    /**
     * sides this user has been accepted for
     */
    boolean [] sidesAccepted;

    /**
     * Construct a basic commander
     */
    Commander ( String a, String b) {
        name = a;
        password = b;
        sidesCommanded = new boolean [2];
        sidesCommanded[0] = false;
        sidesCommanded[1] = false;
        sidesRequested = new boolean [2];
        sidesRequested[0] = false;
        sidesRequested[1] = false;
        sidesRejected = new boolean [2];
        sidesRejected[0] = false;
        sidesRejected[1] = false;
        sidesAccepted = new boolean [2];
        sidesAccepted[0] = false;
        sidesAccepted[1] = false;
    }

    /**
     *  Construct a commander from an existing commander
     */
    Commander ( Commander c ) {
        name = c.name;
        password = c.password;
        sidesCommanded = new boolean [2];
        sidesCommanded[0] = c.sidesCommanded[0];
        sidesCommanded[1] = c.sidesCommanded[1];
        sidesRequested = new boolean [2];
        sidesRequested[0] = c.sidesRequested[0];
        sidesRequested[1] = c.sidesRequested[1];
        sidesRejected = new boolean [2];
        sidesRejected[0] = c.sidesRejected[0];
        sidesRejected[1] = c.sidesRejected[1];
        sidesAccepted = new boolean [2];
        sidesAccepted[0] = c.sidesAccepted[0];
        sidesAccepted[1] = c.sidesAccepted[1];
    }

    /**
     * Returns true if the commander is active
     */
    boolean isActive() {
        return sidesCommanded[0] || sidesCommanded[1]
                || sidesRequested[0] || sidesRequested[1]
                || sidesRejected[0] || sidesRejected[1]
                || sidesAccepted[0] || sidesAccepted[1];
    }
    
    /**
     * Execute a command for this commander
     * @param command 0 commanded, 1 requested
     * @param side
     * @param state
     */
    void doCommand( int command, int side, boolean state) {
        switch (command) {
            case 0:
                sidesCommanded[side] = state;
                if ( state ) {
                    if ( sidesRequested[side] ) {
                        sidesRequested[side] = false;
                        sidesRejected[side] = false;
                        sidesAccepted[side] = true;
                        GameModule.getGameModule().getChatter()
                                .show(name + " has been accepted as a commander"
                                + " of the " + Statics.theSides[side].name
                                + " side");
                    } else {
                        sidesRequested[side] = false;
                        sidesRejected[side] = false;
                        sidesAccepted[side] = false;
                        GameModule.getGameModule().getChatter()
                                .show(name + " now commands the "
                                + Statics.theSides[side].name + " side" );
                    }
                } else {
                    sidesRequested[side] = false;
                    sidesRejected[side] = false;
                    sidesAccepted[side] = false;
                    GameModule.getGameModule().getChatter()
                            .show(name + " has resigned the "
                            + Statics.theSides[side].name + " side" );
                }
                if ( this == Statics.curCommander ) {
                    Statics.theSides[side].controlled = state;
                }
                break;
            case 1:
                sidesRequested[side] = state;
                if ( state ) {
                    sidesRejected[side] = false;
                    sidesAccepted[side] = false;
                    GameModule.getGameModule().getChatter()
                            .show(name + " has requested to join the "
                            + Statics.theSides[side].name + " side" );

                } else {
                    sidesRejected[side] = true;
                    sidesAccepted[side] = false;
                    GameModule.getGameModule().getChatter()
                            .show(name + " has been rejected by the "
                            + Statics.theSides[side].name + " side" );
                }
            default:
        }
        Statics.check++;
        Statics.theMap.repaint();
    }

    /**
     * Log and do a command
     */
    void logDoCommand ( int command, int side, boolean state ) {
        doCommand( command, side, state );
        GameModule.getGameModule().sendAndLog(
                new CommanderCommand( name, password,
                command, side, state ));
    }
}
