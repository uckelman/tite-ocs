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
     * The current master map object.  Every map as created will be the 
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
     * every time a subordinate Map is removed. This is because this will
     * change the map identification within the game save and restore sequences
     * making restoration of existing games impossible.
     */
    protected int version;
    
    /**
     * The base of the tree of sheets being displayed in the main window.
     */
    Sheet mainWindowBase;
    
    /**
     * A list of the base sheet for each auxiliary window open
     */
    List<TopSheet> auxWindowBases;
    
    /**
     * This method determines the correct map from a reference in a sequence 
     * used to  save and restore games.
     */
    protected NewMap getMapFrom(SequenceEncoder.Decoder t) {
        int v = t.nextInt(-1);
        if (v != version) {
            throw new RuntimeException("Module differs from save file creator");
        }
        v = t.nextInt(-1);
        if (v < 0) return this;
        if (v >= subordinateMaps.size()) {
            throw new RuntimeException("Invalid Map Reference in Save File");
        }
        return subordinateMaps.get(v);
    }
    

}
