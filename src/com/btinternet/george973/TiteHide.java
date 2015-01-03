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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.Iterator;
import javax.swing.KeyStroke;

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
import VASSAL.build.module.Map;
import VASSAL.build.GameModule;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import VASSAL.configure.BooleanConfigurer;
import VASSAL.configure.IntConfigurer;
import VASSAL.configure.StringConfigurer;


/**
 *
 * @author george
 */
public class TiteHide extends TiteTrait implements EditablePiece {

  public static final String ID = "titeh;";
  
  public static final String HIDEABLE ="canBeHidden";
  
  private int hideRange;
  private int topRange;
  private boolean topEnabled;
  private String mapSetter = "";
  
  public TiteHide() {
      this(ID, null);
  }
  
  public TiteHide(String type, GamePiece p ) {
      setInner(p);
      mySetType(type);
  }
  
  public String getDescription() {
      return "Automatic Hiding";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("AutomaticHiding.htm");
  }
  
  public void mySetType(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(type, ';');
    st.nextToken(); // Discard ID
    mapSetter = st.nextToken("");
    hideRange = st.nextInt(0);
    topRange = st.nextInt(0);
    st.nextBoolean(false);
    topEnabled = true;
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(mapSetter);
    se.append(hideRange);
    se.append(topRange);
    se.append(topEnabled);
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

  public void mySetState(String newState) {
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

  public Object getProperty(Object key) {
    if (Properties.INVISIBLE_TO_ME.equals(key)) {
      if ( Tite.getTite().hide()) return isHidden();
      else return false;
    }
    if ( HIDEABLE.equals(key) ) return Boolean.TRUE;
    return super.getProperty(key);
  }
  
  public boolean isHidden () {
    GamePiece q = getOutermost ( this );
    if (!Boolean.TRUE.equals(q.getProperty(TiteTraitBase.RESTRICTED))) return false;
    int hr = hideRange;
    if ( hr == 0 ) hr = isEntrained() ? 120 : Tite.getTite().hideRange();
    int tr = topRange;
    if ( tr == 0 ) tr = Tite.getTite().flattenRange();
    Stack s = q.getParent();
    Map map;
    Point st;
    boolean aboveMe = false;
    if ( s == null ) {
      map =q.getMap();
      st = q.getPosition();
    } else {
      map = s.getMap();
      st = s.getPosition();
      if ( topEnabled ) {
        GamePiece r =s.getPieceAbove(q);
        while ( r != null ) {
          if ( r.getProperty(HIDEABLE) != null ) break;
          r = s.getPieceAbove(r);
        }
        if ( r != null ) aboveMe = true;
      }
    }
    if ( map == null ) return false;
    String sx = (String)map.getProperty(mapSetter);
    if ( sx != null ) {
      return Boolean.valueOf(sx);
    }
    GamePiece [] allPieces = map.getAllPieces();
    for ( int i = 0; i < allPieces.length; ++i) {
      Point pt = allPieces[i].getPosition();
      if ( st.distance( pt ) > hr ) continue;
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
            if (st.distance(pt) > tr && aboveMe ) continue;
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
          if (st.distance(pt) > tr && aboveMe ) continue;
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
    private BooleanConfigurer topEnabledConfig;
    private IntConfigurer hideRangeConfig;
    private IntConfigurer topRangeConfig;
    private StringConfigurer mapSetterConfig;
    
    private JPanel panel;
    
    public Ed(TiteHide p) {
      mapSetterConfig = new StringConfigurer ( null, "Map Property to set value: ", p.mapSetter );
      hideRangeConfig = new IntConfigurer( null, "Hide Range (in pixels): ", p.hideRange);
      topEnabledConfig = new BooleanConfigurer( null, "Enable Top Unit only",
                                                p.topEnabled);
      topRangeConfig = new IntConfigurer( null, "Top Unit Only Range (in pixels):",
                                                p.topRange );
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      panel.add(mapSetterConfig.getControls());
      panel.add(hideRangeConfig.getControls());
      panel.add(topEnabledConfig.getControls());
      panel.add(topRangeConfig.getControls());
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(mapSetterConfig.getValueString());
      se.append(hideRangeConfig.getValueString());
      se.append(topRangeConfig.getValueString());
      se.append(topEnabledConfig.getValueString());
      return ID + se.getValue();
    }
    
    public String getState() {
      return "";
    }
    
  }

}
