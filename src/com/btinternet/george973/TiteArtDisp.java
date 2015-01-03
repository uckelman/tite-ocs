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
public class TiteArtDisp extends TiteTrait implements EditablePiece {

  public static final String ID = "tite8;";
  
  private boolean displacedState;
  
  private static GamePiece displacedView;
  
  private static KeyStroke myStroke
          = KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK);
  
  public TiteArtDisp() {
    this (ID, null );
  }
  
  public TiteArtDisp( String type, GamePiece p ) {
    if ( displacedView == null ) {
      displacedView = GameModule.getGameModule().createPiece(BasicPiece.ID + ";;disp.png;;");
    }
    setInner(p);
    mySetType(type);
  }
  
  public String getDescription() {
    return "TITE Artillery Displacer";
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
    displacedState = st.nextBoolean(false);
  }
  
  public String myGetState() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(displacedState);
    return se.getValue();
  }
  
  public Command myKeyEvent(KeyStroke stroke) {
    if ( stroke == myStroke) {
      Command c = Tite.getTite().getStrokeCommand( myStroke, getId(), null);
      displacedState = !displacedState;
      return c;
    }
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[] { new KeyCommand ( displacedState ? "Remove Displaced" : "Displace Artillery", myStroke, this )
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
    if ( !displacedState ) return null;
    return "Displaced";
  }
  
  public String getMyTiteRestrictedStatus() {
    return null;
  }
  
  public int getMyNumberOfMarkers() {
    return displacedState ? 1 : 0;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    return 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    if ( !displacedState ) return x;
    displacedView.draw(g, x, y, obs, zoom);
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
    
    public Ed(TiteArtDisp p) {
      
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
