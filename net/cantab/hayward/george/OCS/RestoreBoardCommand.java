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

import VASSAL.build.GameModule;
import VASSAL.build.module.Map;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.command.Command;
import java.util.List;

/**
 *
 * @author George Hayward
 */
public class RestoreBoardCommand extends Command {

    Line[] lines;

    String mapName;

    String boardName;

    public RestoreBoardCommand( String map, String board, Line[] ls) {
        mapName = map;
        boardName = board;
        lines = ls;
    }

    public Command myUndoCommand() {
        return null;
    }

    public void executeCommand() {
        List<Map> ms = GameModule.getGameModule().getComponentsOf(Map.class);
        for (Map m : ms) {
            if(m.getMapName().equals(mapName)) {
                Board bu = m.getBoardPicker().getBoard(boardName);
                if (bu instanceof SetupBoard) {
                    ((SetupBoard)bu).setLines(lines);
                    return;
                }
            }
        }
    }

}
