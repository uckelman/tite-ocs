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
