/*
 *
 * Copyright (c) 2008 by George Hayward
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

package com.btinternet.george973;

import java.util.Collection;

import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.BoardPicker;

/**
 *
 * @author george
 */
public class BoardPickerOverride extends BoardPicker {
  
  public Collection<Board> getBoards() {
    return possibleBoards;
  }

}
