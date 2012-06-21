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


package com.btinternet.george973;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.KeyStroke;

import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceEditor;

import VASSAL.build.module.documentation.HelpFile;
import VASSAL.tools.SequenceEncoder;
import VASSAL.command.Command;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import VASSAL.configure.IntConfigurer;

/**
 *
 * @author george
 */
public class Blind extends Decorator implements EditablePiece {
  
  public static final String ID = "blind;";
  
  public static final String EYESIGHT="eyesight";

  private int eyesight;
  
  public Blind() {
    this(ID, null);
  }
  
  public Blind(String type, GamePiece p) {
    setInner(p);
    mySetType(type);
  }
  
  public String getDescription() {
    return "Reduced Vision";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("AutomaticHiding.htm");
  }
  
  public void mySetType(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';');
    st.nextToken(); // Discard ID
    eyesight = st.nextInt(0);
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(eyesight);
    return ID + se.getValue();
  }
  
  public Command myKeyEvent(KeyStroke stroke) {
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[0];
  }
  
  public String myGetState() {
    return "";
  }
  
  public void mySetState(String newState ) {
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
  
  public void draw(Graphics g, int x, int y, Component obs, double zoom ) {
    piece.draw(g,x,y,obs,zoom);
  }
  
  public Object getProperty(Object key) {
    if (EYESIGHT.equals(key)) {
      return eyesight;
    }
    return super.getProperty(key);
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    public IntConfigurer eyesightConfig;
    
    private JPanel panel;
    
    public Ed(Blind p) {
      eyesightConfig = new IntConfigurer( null, "Maximum Distance to unhide: ", p.eyesight );
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      panel.add(eyesightConfig.getControls());
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(eyesightConfig.getValueString());
      return ID + se.getValue();
    }
    
    public String getState() {
      return "";
    }
  }
}
