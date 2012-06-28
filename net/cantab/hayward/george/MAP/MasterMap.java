/* 
 *
 * Copyright (c) 2000-2012 by Rodney Kinney, Joel Uckelman, George Hayward
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
package net.cantab.hayward.george.MAP;

import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.command.Command;
import VASSAL.tools.SequenceEncoder;
import java.util.List;

/**
 * This class has the implementation of all methods and fields belonging solely
 * to the master map which the one initially display and from which all other
 * maps are reached. This class is responsible for saving and restoring the
 * windows and sheets being displayed for all the users.
 *
 * @author George Hayward
 */
public class MasterMap extends NewMap {

    /**
     * The current master map object. Every map as created will be the
     * subordinate of this one. The MasterMap must be the first one in the
     * module to be created.
     */
    protected static MasterMap currentMaster;

    /**
     * The list of subordinate maps.
     */
    protected List<NewMap> subordinateMaps;

    /**
     * The version of the current map layout in the module. This is incremented
     * every time a subordinate Map is removed. This is because this will change
     * the map identification within the game save and restore sequences making
     * restoration of existing games impossible.
     */
    protected int version;

    /**
     * The base of the tree of sheets being displayed in the main window.
     */
    protected Sheet mainWindowBase;

    /**
     * A list of the base sheet for each auxiliary window open
     */
    protected List<TopSheet> auxWindowBases;
    
    /**
     * This is the structure used to save the open window/sheet definitions for
     * each player
     */
    protected static class PlayerState {
        
        /**
         * The player's id which is his password
         */
        protected String playerId;
        
        /**
         * His main window tree of Sheets
         */
        protected Sheet mainWindowBase;
        
        /**
         * His auxiliary window base sheets
         */
        protected TopSheet[] auxWindowBases;
        
        /**
         * Create a new PlayerState for the given player id using the supplied
         * main window sheet and list of auxiliary window top sheets
         */
        protected PlayerState(String playerId, Sheet main, List<TopSheet> aux) {
            this.playerId = playerId;
            mainWindowBase = new Sheet(main, null);
            auxWindowBases = new TopSheet[aux.size()];
            int i = 0;
            for (TopSheet t : aux) {
                auxWindowBases[i++] = new TopSheet(t);
            }
        }
        
        /**
         * Create a new PlayerState from a sequence used to save and restore
         * games.
         */
        protected PlayerState(SequenceEncoder.Decoder t) {
            playerId = t.nextToken();
            mainWindowBase = new Sheet(t, null);
            int i, j;
            j = t.nextInt(0);
            auxWindowBases = new TopSheet[j];
            for (i = 0; i < j; i++) {
                auxWindowBases[i] = new TopSheet(t);
            }
        }
        
        /**
         * Encode this playerState into q sequence used to save and restore
         * games.
         */
        protected void encode(SequenceEncoder t) {
            t.append(playerId);
            mainWindowBase.encode(t);
            t.append(auxWindowBases.length);
            for (TopSheet a : auxWindowBases) {
                a.encode(t);
            }
        }
    }
    
    /**
     * All the player window/sheet definitions known to the game currently
     */
    protected List<PlayerState> allPlayers;
    
    
    
    /**
     * The external game controller
     */
    protected GameControlForMap controller;
    
    /**
     * Set the game controller
     */
    public void setGameControl(GameControlForMap c) {
        if (controller != null && controller != c) {
            throw new RuntimeException("Multiple Game Controllers in Master Map");
        }
        controller = c;
    }

    /**
     * This method determines the correct map from a reference in a sequence
     * used to save and restore games.
     */
    protected NewMap getMapFrom(SequenceEncoder.Decoder t) {
        int v = t.nextInt(-1);
        if (v != version) {
            throw new RuntimeException("Module differs from save file creator");
        }
        v = t.nextInt(-1);
        if (v < 0) {
            return this;
        }
        if (v >= subordinateMaps.size()) {
            throw new RuntimeException("Invalid Map Reference in Save File");
        }
        return subordinateMaps.get(v);
    }

    /**
     * This method determines the correct reference for a map and encodes it
     * into a sequence for game save and restore
     */
    protected void encode(NewMap aMap, SequenceEncoder t) {
        t.append(version);
        if (aMap == this) {
            t.append(-1);
        } else {
            int u = subordinateMaps.indexOf(aMap);
            if (u < 0) {
                throw new RuntimeException("Map not controlled in save");
            }
            t.append(u);
        }
    }

    /**
     * Add this object to its parent in the tree of defined objects in the
     * module. At this point any initialisation is done linking the object to
     * others in the module.
     *
     * @param b - The parent object
     */
    @Override
    public void addTo(Buildable b) {
        if (MasterMap.currentMaster != null) {
            throw new RuntimeException("Master Map Duplicated");
        }
        currentMaster = this;
        super.addTo(b);
        GameModule.getGameModule().getGameState().addGameComponent(this);
    }

    /**
     * Notify the GameComponent that a game has started/ended
     *
     * @param gameStarting if true, a game is starting. If false, then a game is
     * ending
     */
    @Override
    public void setup(boolean gameStarting) {
        //TODO: Write setup functionality master map + other maps
    }

    /**
     * When saving a game, each GameComponent should return a {@link
     * Command} that, when executed, restores the GameComponent to its state
     * when the game was saved If this component has no persistent state, return
     * null
     */
    @Override
    public Command getRestoreCommand() {
        // TODO: write save/restore functionality master + other maps
        return null;
    }
}
