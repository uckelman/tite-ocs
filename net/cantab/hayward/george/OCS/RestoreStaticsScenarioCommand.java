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
public class RestoreStaticsScenarioCommand extends Command {

    /**
     * An array with the scenario instructions
     */
    String [] scenInstr;

    RestoreStaticsScenarioCommand() {
        if (Statics.scenarioInstructions != null) {
            scenInstr = new String[Statics.scenarioInstructions.length];
            System.arraycopy(Statics.scenarioInstructions, 0, scenInstr, 0, Statics.scenarioInstructions.length);
        }
    }

    RestoreStaticsScenarioCommand(boolean a) {
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
        Statics.scenarioInstructions = scenInstr;
    }
}
