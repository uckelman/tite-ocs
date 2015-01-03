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

import VASSAL.counters.BasicPiece;
import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Properties;
import VASSAL.counters.Restricted;
import VASSAL.counters.Stack;
import VASSAL.counters.PieceEditor;
import VASSAL.counters.ImagePicker;

import VASSAL.build.module.documentation.HelpFile;
import VASSAL.tools.SequenceEncoder;
import VASSAL.command.Command;
import VASSAL.build.module.Map;
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
public class TiteSupply extends TiteTrait implements EditablePiece {

  public static final String ID = "tite3;";
  
  private int maxAPs;
  private int maxAPsExtended;
  
  private String extendedImage;
  
  private GamePiece extendedView;
  
  private boolean extended;
  
  private static KeyStroke theStroke
          = KeyStroke.getKeyStroke('X', InputEvent.CTRL_DOWN_MASK);
  
  public TiteSupply() {
    this ( ID, null );
  }
  
  public TiteSupply( String type, GamePiece p ) {
    setInner (p);
    mySetType(type);
  }
  
  public String getDescription() {
    return "TITE Supply Units";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrait.htm");
  }

  public void mySetType(String type ) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    st.nextToken(); // discard ID
    maxAPs = st.nextInt(0);
    maxAPsExtended = st.nextInt(2);
    extendedImage = st.nextToken("");

    if ( !extendedImage.equals("") )
      extendedView = GameModule.getGameModule().createPiece(BasicPiece.ID 
                +";;" + extendedImage + ";;");
    else
      extendedView = null;
}
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(maxAPs);
    se.append(maxAPsExtended);
    se.append(extendedImage);
    return ID + se.getValue();
  }
  
  public void mySetState(String type) {
    extended = Boolean.valueOf(type);
  }
  
  public String myGetState() {
    return Boolean.toString(extended);
  }
  
  public Command myKeyEvent(KeyStroke stroke ) {
    if ( stroke == theStroke ) {
      Command c = Tite.getTite().getStrokeCommand(theStroke, getId(), theStroke);
      extended = ! extended;
      return c;
    }
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[] {
      new KeyCommand( extended ? "Unextend" : "Extend", theStroke, this )
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
    if ( extended )
      extendedView.draw(g, x, y, obs, zoom);
    else
      piece.draw(g, x, y, obs, zoom);
  }
  
  public String getMyTiteStatus() {
    return null;
  }
  
  public String getMyTiteRestrictedStatus() {
    return null;
  }
  
  public int getMyNumberOfMarkers() {
    return 0;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    return 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  public int myRestrictedDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  public int getMaxAPs () {
    return extended ? maxAPsExtended : maxAPs;
  }
  
  public boolean isExtended() {
    return extended;
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {

    private IntConfigurer maConfig;
    private IntConfigurer mxConfig;
    private ImagePicker picker;
    private JPanel panel;

    public Ed(TiteSupply p) {
      maConfig = new IntConfigurer(null, "Maximum Hits: ", p.maxAPs);
      mxConfig = new IntConfigurer(null, "Current Hits: ", p.maxAPsExtended);

      panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      panel.add(maConfig.getControls());
      panel.add(mxConfig.getControls());

      picker = new ImagePicker();
      picker.setImageName(p.extendedImage);
      picker.setVisible(true);
      panel.add(picker);
    }

    public Component getControls() {
      return panel;
    }

    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(maConfig.getValueString());
      se.append(mxConfig.getValueString());
      se.append(picker.getImageName());
      return ID + se.getValue();
    }

    public String getState() {
      return "false";
    }
  }
}
