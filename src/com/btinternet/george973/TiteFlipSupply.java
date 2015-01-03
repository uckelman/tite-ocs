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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.util.Iterator;
import javax.swing.KeyStroke;
import javax.swing.JMenu;
import java.util.HashMap;
import java.util.Map;

import VASSAL.counters.BasicPiece;
import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Properties;
import VASSAL.counters.Restricted;
import VASSAL.counters.Stack;
import VASSAL.counters.PieceEditor;

import VASSAL.build.module.documentation.HelpFile;
import VASSAL.tools.SequenceEncoder;
import VASSAL.command.Command;
import VASSAL.build.GameModule;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import VASSAL.configure.BooleanConfigurer;
import VASSAL.configure.IntConfigurer;
import VASSAL.configure.StringConfigurer;
import VASSAL.configure.StringArrayConfigurer;

/**
 *
 * @author george
 */
public class TiteFlipSupply extends TiteTrait implements EditablePiece {

  public static final String ID = "tite7;";
  
  private int supplyState;
  
  private static GamePiece[] supplyViews;
  
  private static KeyStroke myStroke
          = KeyStroke.getKeyStroke('V', InputEvent.CTRL_DOWN_MASK);
  
  public TiteFlipSupply() {
    this (ID, null );
  }
  
  public TiteFlipSupply( String type, GamePiece p ) {
    if ( supplyViews == null ) {
      supplyViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;low.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;oos.png;;")
              };
    }
    setInner(p);
    mySetType(type);
  }
  
  public String getDescription() {
    return "TITE Supply State Flipper";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrait.htm");
  }

  public void mySetType(String type) {
  }

  public String myGetType() {
    return ID;
  }
  
  public void mySetState( String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    supplyState = st.nextInt(0);
  }
  
  public String myGetState() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(supplyState);
    return se.getValue();
  }
  
  public Command myKeyEvent(KeyStroke stroke) {
    if ( stroke == myStroke) {
      Command c = Tite.getTite().getStrokeCommand( myStroke, getId(), null);
      supplyState++;
      if ( supplyState > 2 ) supplyState = 0;
      return c;
    }
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[] { new KeyCommand ( "Change supply state", myStroke, this )
    };
  }
  
  public String getName() {
    return piece.getName();
  }

  public Shape getShape() {
    return piece.getShape();
  }

  public Rectangle boundingBox() {
    return piece.boundingBox();
  }

  public void draw(Graphics g, int x, int y, Component obs, double zoom) {
    piece.draw(g, x, y, obs, zoom);
  }
  
  public String getMyTiteStatus() {
    if ( supplyState == 0 ) return null;
    return supplyState == 1 ? "Low Supply" : "Out of Supply";
  }
  
  public String getMyTiteRestrictedStatus() {
    return null;
  }
  
  public int getMyNumberOfMarkers() {
    return supplyState == 0 ? 0 : 1;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    return 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    if ( supplyState == 0 ) return x;
    supplyViews[supplyState - 1].draw(g, x, y, obs, zoom);
    return x + width;
  }

  public int myRestrictedDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }

  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private JPanel panel;
    
    public Ed(TiteFlipSupply p) {
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      return ID;
    }
    
     public String getState() {
      return "";
    }
  }
}
