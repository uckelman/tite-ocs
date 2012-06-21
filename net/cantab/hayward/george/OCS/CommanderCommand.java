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

import net.cantab.hayward.george.OCS.Commander;
import net.cantab.hayward.george.OCS.Statics;
import VASSAL.command.Command;

/**
 *
 * @author George Hayward
 */
public class CommanderCommand extends Command {

    String name;
    String password;
    int command;
    int side;
    boolean state;

    CommanderCommand( String a, String b, int c, int d, boolean e ) {
        name = a;
        password = b;
        command = c;
        side = d;
        state = e;
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
    public void executeCommand() {
        /*
         * Does the new user match an existing user
         */
        int i;
        for (i = 0; i < Statics.theCommanders.length; i++) {
            if (Statics.theCommanders[i].name.equals(name)
                    && Statics.theCommanders[i].password.equals(password)) {
                Statics.theCommanders[i].doCommand(command, side, state);
                return;
            }
        }
        Commander c = new Commander( name, password);
        Statics.addNewCommander(c);
        c.doCommand(command, side, state);
    }
}
