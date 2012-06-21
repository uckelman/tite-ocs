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
public class TiteMarkerHide  extends TiteHide {
  
  public static final String ID = "titeMH;";
  
  public TiteMarkerHide() {
    this (ID, null );
  }
  
  public TiteMarkerHide( String type, GamePiece p ) {
    setInner(p);
    mySetType(type);
  }
  
  public String getDescription() {
      return "Automatic Marker Hiding";
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


  public boolean isHidden () {
    GamePiece q = getOutermost ( this );
    int hr = Tite.getTite().hideRange();
    int tr = Tite.getTite().flattenRange();
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
    String sx = map.getMapName();
    if ( sx.contains("Pontoon") ) return false;
    GamePiece [] allPieces = map.getAllPieces();
    for ( int i = 0; i < allPieces.length; ++i) {
      Point pt = allPieces[i].getPosition();
      if ( st.distance( pt ) > hr ) continue;
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
            if (s != null ) {
              if (st.distance(pt) > tr ) {
                if (s.getPieceAbove(q) != null ) {
                  continue;
                }
              }
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
          if (s != null) {
            if (st.distance(pt) > tr) {
              if (s.getPieceAbove(q) != null ) {
                continue;
              }
            }
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
    
    public Ed(TiteMarkerHide p) {
      
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
