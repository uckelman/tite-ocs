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
import java.awt.Point;
import javax.swing.KeyStroke;
import java.util.Iterator;

import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceEditor;
import VASSAL.counters.BasicPiece;
import VASSAL.counters.Stack;
import VASSAL.counters.Properties;

import VASSAL.build.module.Map;
       

import VASSAL.build.module.documentation.HelpFile;
import VASSAL.tools.SequenceEncoder;
import VASSAL.command.Command;
import VASSAL.build.GameModule;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JLabel;
import VASSAL.configure.IntConfigurer;
import VASSAL.configure.StringConfigurer;
import VASSAL.counters.ImagePicker;

/**
 *
 * @author george
 */
public class TiteConceal extends TiteTrait implements EditablePiece {
  
  public static final String ID = "titec;";
  public static final String CONCEALED = "Concealed";
  
  private String mapSetter;
  private int concealRange;
  private String concealImage;
  
  protected GamePiece view;
  
  public TiteConceal() {
    this(ID, null);
  }
  
  public TiteConceal(String type, GamePiece p) {
    setInner(p);
    mySetType(type);
  }
  
  public String getDescription() {
    return "Concealed Unit";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("AutomaticHiding.htm");
  }
  
  public void mySetType(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';');
    st.nextToken(); // Discard ID
    mapSetter=st.nextToken("");
    concealRange = st.nextInt(0);
    concealImage = st.nextToken("");
    view = GameModule.getGameModule().createPiece(BasicPiece.ID 
              +";;" + concealImage + ";;");
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(mapSetter);
    se.append(concealRange);
    se.append(concealImage);
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
    if ( Tite.getTite().conceal() && isConcealed() ) {
      view.draw(g, x, y, obs, zoom);
      return;
    }
    piece.draw(g,x,y,obs,zoom);
  }
 
   public Object getProperty(Object key) {
    if (CONCEALED.equals(key)) {
      if ( Tite.getTite().conceal() )
              return isConcealed();
    }
    return super.getProperty(key);
  }
  
  public boolean isConcealed() {
    int cr = concealRange;
    if ( cr == 0 ) cr = Tite.getTite().concealRange();
    GamePiece q = getOutermost ( this );
    if (!Boolean.TRUE.equals(q.getProperty(TiteTraitBase.RESTRICTED))) return false;
    Stack s = q.getParent();
    Map map;
    Point st;
    if ( s == null ) {
      map =q.getMap();
      st = q.getPosition();
    } else {
      map = s.getMap();
      st = s.getPosition();
    }
    if ( map == null ) return false;
    String sx = (String) map.getProperty(mapSetter);
    if ( sx != null ) return Boolean.valueOf(sx);
    GamePiece [] allPieces = map.getAllPieces();
    for ( int i = 0; i < allPieces.length; ++i) {
      Point pt = allPieces[i].getPosition();
      if ( st.distance( pt ) > cr ) continue;
      if ( allPieces[i].getClass() == Stack.class) {
        Stack sub = (Stack)allPieces[i];
        for (Iterator<GamePiece> e = sub.getPiecesIterator();
                e.hasNext();) {
          GamePiece p = e.next();
          if (Boolean.FALSE.equals(p.getProperty(TiteTraitBase.RESTRICTED))) {
            int un;
            Object u = p.getProperty(Blind.EYESIGHT);
            if ( u != null && u.getClass() == Integer.class ) {
                un = (Integer)u;
                if ( st.distance( pt ) > un ) continue;
            }
            return false;
          }          
        }
      } else {
        if (Boolean.FALSE.equals(allPieces[i].getProperty(TiteTraitBase.RESTRICTED))) {
          int un;
          Object u = allPieces[i].getProperty(Blind.EYESIGHT);
          if ( u != null && u.getClass() == Integer.class ) {
              un = (Integer)u;
              if ( st.distance( pt ) > un ) continue;
          }
          return false;
        }
      }
    }
    return true;


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
  
  public int myDrawMarkers (Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  public int myRestrictedDrawMarkers (Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    public StringConfigurer mapSetterConfig;
    public IntConfigurer concealRangeConfig;
    private ImagePicker picker;
    
    private JPanel panel;
    
    public Ed(TiteConceal p) {
      mapSetterConfig = new StringConfigurer( null,
              "Map Property to set value: ", p.mapSetter );
      concealRangeConfig = new IntConfigurer( null, 
              "Maximum Distance to unhide: ", 
              p.concealRange );
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      panel.add(mapSetterConfig.getControls());
      panel.add(concealRangeConfig.getControls());
      
      Box box = Box.createHorizontalBox();
      box.add(new JLabel("Image to display when concealed: "));
      picker = new ImagePicker();
      picker.setImageName(p.concealImage);
      picker.setVisible(true);
      box.add(picker);
      panel.add(box);
      
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(mapSetterConfig.getValueString());
      se.append(concealRangeConfig.getValueString());
      se.append(picker.getImageName());
      return ID + se.getValue();
    }
    
    public String getState() {
      return "";
    }
  }
}
