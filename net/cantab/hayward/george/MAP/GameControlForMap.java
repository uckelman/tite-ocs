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

/**
 * This defines all the methods required from some external object controlling
 * the game so the map system can work.
 * 
 * @author George Hayward
 */
public interface GameControlForMap {
    
    /**
     * This tests if a player has a part in this game or is just a kibitzer.
     * This determines if the player's bookmarks and open Sheets/Windows are
     * preserved in a save file.
     * @param playerId - the identity of the player as his password string.
     * @return True if the player is part of the game.
     */
    public boolean isRealPlayer(String playerId);
    
    /**
     * This returns the id of the current player.
     * @return the String holding the id of the current player.
     */
    public String getCurrentPlayer();

}
