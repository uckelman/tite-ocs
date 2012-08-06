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
import VASSAL.command.CommandEncoder;
import VASSAL.tools.SequenceEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class has the implementation of all methods and fields belonging solely
 * to the master map which the one initially display and from which all other
 * maps are reached. This class is responsible for saving and restoring the
 * windows and sheets being displayed for all the users.
 *
 * @author George Hayward
 */
public class MasterMap extends NewMap implements CommandEncoder {

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
    protected MainSheet mainWindowBase;

    /**
     * A list of the base sheet for each auxiliary window open
     */
    protected List<TopSheet> auxWindowBases;

    /**
     * This is the structure used to save the open window/sheet definitions for
     * each player
     */
    protected class PlayerState {

        /**
         * The player's id which is his password
         */
        protected String playerId;

        /**
         * His main window tree of Sheets
         */
        protected MainSheet mainWindowBase;

        /**
         * His auxiliary window base sheets
         */
        protected TopSheet[] auxWindowBases;

        /**
         * Create a new PlayerState for the given player id using the supplied
         * main window sheet and list of auxiliary window top sheets
         */
        protected PlayerState(String playerId, MainSheet main, List<TopSheet> aux) {
            this.playerId = playerId;
            mainWindowBase = new MainSheet(main);
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
            mainWindowBase = new MainSheet(t, MasterMap.this);
            int i, j;
            j = t.nextInt(0);
            auxWindowBases = new TopSheet[j];
            for (i = 0; i < j; i++) {
                auxWindowBases[i] = new TopSheet(t);
            }
        }

        /**
         * Create a new PlayerState from and existing one.
         */
        protected PlayerState(PlayerState other) {
            playerId = other.playerId;
            mainWindowBase = new MainSheet(other.mainWindowBase);
            auxWindowBases = new TopSheet[other.auxWindowBases.length];
            for (int i = 0; i < other.auxWindowBases.length; i++) {
                auxWindowBases[i] = new TopSheet(other.auxWindowBases[i]);
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
     * All the player window/sheet definitions known to the game currently.
     */
    protected List<PlayerState> allPlayers;

    /**
     * Save the current set of windows and sheets.
     */
    protected void saveCurrentSheets() {
        String b = MasterMap.currentMaster.controller.getCurrentPlayer();
        for (PlayerState a : allPlayers) {
            if (b.equals(a.playerId)) {
                a.mainWindowBase = new MainSheet(mainWindowBase);
                a.auxWindowBases = new TopSheet[auxWindowBases.size()];
                int i = 0;
                for (TopSheet c : auxWindowBases) {
                    a.auxWindowBases[i++] = new TopSheet(c);
                }
                return;
            }
        }
        allPlayers.add(new PlayerState(b, mainWindowBase, auxWindowBases));
    }

    /**
     * Unrealise all the existing sheets and windows.
     */
    protected void unrealiseCurrent() {
        mainWindowBase.unRealise();
        for (TopSheet t : auxWindowBases) {
            t.unRealise();
        }
    }

    /**
     * Realise all the existing sheets and windows.
     */
    protected void realiseCurrent() {
        mainWindowBase.realise();
        for (TopSheet t : auxWindowBases) {
            t.realise();
        }
    }

    /**
     * Setup the default window if none present for this user.
     */
    protected void setDefaultWindow() {
        mainWindowBase = new MainSheet(new MapSheet(publicBookmarks.get(0)), this);
    }

    /**
     * Make the windows and sheets for the given player the active ones.
     */
    protected void activateSheetsForPlayer(String playerId) {
        unrealiseCurrent();
        auxWindowBases = new ArrayList<TopSheet>();
        for (PlayerState a : allPlayers) {
            if (playerId.equals(a.playerId)) {
                mainWindowBase = new MainSheet(a.mainWindowBase);
                auxWindowBases = Arrays.asList(a.auxWindowBases);
                realiseCurrent();
                return;
            }
        }
        setDefaultWindow();
        realiseCurrent();
    }

    /**
     * Player has changed. Change the windows and sheet to fit.
     */
    @Override
    protected void playerChanged() {
        super.playerChanged();
        saveCurrentSheets();
        activateSheetsForPlayer(controller.getCurrentPlayer());
    }

    /**
     * This is the method called from the game controller when it detects that
     * the player has changed. It calls every NewMap in the module to do what is
     * required.
     */
    public void doPlayerChanged() {
        for (NewMap m : subordinateMaps) {
            m.playerChanged();
        }
        playerChanged();
    }

    /**
     * This is the command used to restore the windows and sheets that are open
     * for each player.
     */
    public class myCommand extends Command {

        /**
         * The copy of all the player states in the Map.
         */
        protected PlayerState[] myAllPlayers;

        /**
         * Create a new Command to restore the current player sheet and window
         * information.
         */
        protected myCommand() {
            myAllPlayers = new PlayerState[allPlayers.size()];
            int i = 0;
            for (PlayerState a : allPlayers) {
                myAllPlayers[i++] = new PlayerState(a);
            }
        }

        /**
         * Create a new Command from a sequence used for restoring and saving
         * games.
         */
        protected myCommand(SequenceEncoder.Decoder t) {
            int i, j;
            j = t.nextInt(0);
            myAllPlayers = new PlayerState[j];
            for (i = 0; i < j; i++) {
                myAllPlayers[i] = new PlayerState(t);
            }
        }

        /**
         * Encode this Command in a sequence used to save and restore games.
         */
        protected void encode(SequenceEncoder t) {
            t.append(myAllPlayers.length);
            for (PlayerState a : myAllPlayers) {
                a.encode(t);
            }
        }

        @Override
        protected void executeCommand() {
            allPlayers = Arrays.asList(myAllPlayers);
        }

        @Override
        protected Command myUndoCommand() {
            return null;
        }
    }
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
        GameModule.getGameModule().addCommandEncoder(this);
        GameModule.getGameModule().getGameState().addGameComponent(this);
    }

    /**
     * Initialise the Map for a Game.
     */
    @Override
    protected void gameStarting() {
        for (NewMap m : subordinateMaps) {
            m.gameStarting();
        }
        super.gameStarting();
        doPlayerChanged();
        mainWindowBase.realise();
        for (TopSheet a : auxWindowBases) {
            a.realise();
        }
    }

    /**
     * Clean up the Map after a Game.
     */
    @Override
    protected void gameFinishing() {
        mainWindowBase.unRealise();
        for (TopSheet a : auxWindowBases) {
            a.unRealise();
        }
        for (NewMap m : subordinateMaps) {
            m.gameFinishing();
        }
        super.gameFinishing();
    }

    /**
     * Notify the GameComponent that a game has started/ended
     *
     * @param gameStarting if true, a game is starting. If false, then a game is
     * ending
     */
    @Override
    public void setup(boolean gameStarting) {
        if (gameStarting) {
            this.gameStarting();
        } else {
            gameFinishing();
        }
    }

    /**
     * When saving a game, each GameComponent should return a {@link
     * Command} that, when executed, restores the GameComponent to its state
     * when the game was saved If this component has no persistent state, return
     * null.
     *
     * First get the restore state for the underlying Map and then add all the
     * other maps restore commands as subcommands and final add the restore
     * command for the master map as the final subcommand.
     */
    @Override
    public Command getRestoreCommand() {
        Command c = super.getRestoreCommand();
        for (NewMap m : subordinateMaps) {
            c.append(m.getRestoreCommand());
        }
        c.append(new myCommand());
        return null;
    }
    /*
     * Known encoded command object identifiers
     */
    private static final String newMapCommand = "GWH_NMC\t";

    private static final String masterMapCommand = "GWH_MMC\t";

    /**
     * Decode a string into a command object.
     *
     * @param command - string to decode.
     * @return Command object if we know how to decode string otherwise null.
     */
    @Override
    public Command decode(String command) {
        if (command.startsWith(newMapCommand)) {
            SequenceEncoder.Decoder t = new SequenceEncoder.Decoder(command, '\t');
            t.nextToken();
            return getMapFrom(t).createCommand(t);
        }
        if (command.startsWith(masterMapCommand)) {
            SequenceEncoder.Decoder t = new SequenceEncoder.Decoder(command, '\t');
            t.nextToken();
            return new myCommand(t);
        }
        return null;
    }

    /**
     * Encode a command into a string
     *
     * @param c the command to be encoded
     * @return the string which is the encoded command or null if we don't know
     * how to encode this command.
     */
    @Override
    public String encode(Command c) {
        if (c instanceof NewMap.myCommand) {
            SequenceEncoder s = new SequenceEncoder('\t');
            ((NewMap.myCommand) c).encode(s);
            return newMapCommand + s.getValue();
        }
        if (c instanceof myCommand) {
            SequenceEncoder s = new SequenceEncoder('\t');
            ((myCommand) c).encode(s);
            return masterMapCommand + s.getValue();
        }
        return null;
    }
}
