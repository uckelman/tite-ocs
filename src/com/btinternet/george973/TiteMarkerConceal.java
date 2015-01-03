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
public class TiteMarkerConceal extends TiteConceal {

  public static final String ID = "titeMC;";
  
  public TiteMarkerConceal() {
    this (ID, null );
  }
  
  public TiteMarkerConceal( String type, GamePiece p ) {
    setInner(p);
    mySetType(type);
  }
  
  public void mySetType(String type) {
  }

  public String myGetType() {
    return ID;
  }
  
  public void mySetState( String type) {
  }
  
  public String myGetState() {
    return "";
  }
  
  public Command myKeyEvent(KeyStroke stroke) {
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[0] ;
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


  public boolean isConcealed() {
    int cr = Tite.getTite().concealRange();
    GamePiece q = getOutermost ( this );
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
    GamePiece [] allPieces = map.getAllPieces();
    for ( int i = 0; i < allPieces.length; ++i) {
      Point pt = allPieces[i].getPosition();
      if ( st.distance( pt ) > cr ) continue;
      if ( allPieces[i].getClass() == Stack.class) {
        Stack sub = (Stack)allPieces[i];
        for (Iterator<GamePiece> e = sub.getPiecesIterator();
                e.hasNext();) {
          GamePiece p = e.next();
          if (Boolean.FALSE.equals(p.getProperty(Properties.RESTRICTED))) {
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
        if (Boolean.FALSE.equals(allPieces[i].getProperty(Properties.RESTRICTED))) {
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
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private JPanel panel;
    
    public Ed(TiteMarkerConceal p) {
      
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
