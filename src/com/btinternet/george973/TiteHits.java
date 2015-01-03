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
public class TiteHits extends TiteTrait implements EditablePiece {

  public static final String ID = "tite1;";
  
  private int maxHits;
  private int flipHits;
  private String flipImage;
  private String[] hitImages;
  
  private int curHits;
  
  protected GamePiece flipView;
  protected GamePiece[] hitViews;
  
  private static Map<String,GamePiece> hitViewHash;
  
  static protected GamePiece[] hitMarkers = null;
  
  private static KeyStroke myStroke 
          = KeyStroke.getKeyStroke( 'H', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke myReverseStroke
          = KeyStroke.getKeyStroke( 'G', InputEvent.CTRL_DOWN_MASK);
  
  public TiteHits() {
    this( ID, null );
  }
  
  public TiteHits( String type, GamePiece p ) {
    if ( hitMarkers == null ) {
      hitMarkers = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;hits1.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;hits2.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;hits3.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;hits4.png;;")
              };
      hitViewHash = new HashMap<String, GamePiece>();
    }
    setInner (p);
    mySetType(type);    
  }
  
  public String getDescription() {
    return "TITE Hits";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrait.htm");
  }

  public void mySetType(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    st.nextToken(); // discard ID
    maxHits = st.nextInt(1);
    flipHits = st.nextInt(0);
    flipImage = st.nextToken("");
    hitImages = st.nextStringArray(0);
    
    if ( !flipImage.equals("") )
      flipView = GameModule.getGameModule().createPiece(BasicPiece.ID 
                +";;" + flipImage + ";;");
    else
      flipView = null;

    hitViews = new GamePiece[ hitImages.length ];
    for ( int i = 0; i < hitImages.length; i ++ ) {
      if ( !hitImages[i].equals("") ) {
        hitViews[i] = hitViewHash.get(hitImages[i]);
        if ( hitViews[i] == null ) {
          hitViews[i] = GameModule.getGameModule().createPiece(BasicPiece.ID +";;" + hitImages[i] + ";;");
          hitViewHash.put(hitImages[i], hitViews[i]);
        }
      } else {
        hitViews[i] = null;
      }
    }
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(maxHits);
    se.append(flipHits);
    se.append(flipImage);
    se.append(hitImages);
    return ID + se.getValue();
  }
  
  public void mySetState(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    curHits = st.nextInt(0);
  }
  
  public String myGetState() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(curHits);
    return se.getValue();
  }
  
  public Command myKeyEvent(KeyStroke stroke) {
    if ( stroke == myStroke) {
      Command c = Tite.getTite().getStrokeCommand( myStroke, getId(), myReverseStroke);
      if ( curHits < maxHits ) {
        curHits++;
      } else {
        curHits = 0;
      }
      return c;
    }
    if ( stroke == myReverseStroke ) {
      Command c = Tite.getTite().getStrokeCommand( myReverseStroke, getId(), myStroke);
      if ( curHits > 0 ) {
        curHits--;
      } else {
        curHits = maxHits;
      }
      return c;
    }
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[] { new KeyCommand ( "Take a hit", myStroke, this ),
                              new KeyCommand ( "Remove a hit", myReverseStroke, this)
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
    if ( flipHits > 0 && flipView != null && curHits >= flipHits )
      flipView.draw(g, x, y, obs, zoom);
    else
      piece.draw(g, x, y, obs, zoom);
    if ( !Tite.getTite().mergeHits()) return;
    if ( isRestricted() && !Tite.getTite().othersSeeHits()) return;
    if ( curHits == 0 || curHits == flipHits ) return;
    if ( curHits < hitViews.length && hitViews[curHits] != null )
      hitViews[curHits].draw(g, x, y, obs, zoom);
  }

  public String getMyTiteStatus() {
    return displayHits();
  }
  
  public String getMyTiteRestrictedStatus() {
    if ( !Tite.getTite().othersSeeHits()) return null;
    if ( isConcealed() ) return null;
    return displayHits();
  }
  
  public int getMyNumberOfMarkers() {
    if ( Tite.getTite().mergeHits()) return 0;
    return (curHits != 0 && curHits != flipHits) ? 1 : 0;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    if ( Tite.getTite().mergeHits()) return 0;
    if ( !Tite.getTite().othersSeeHits()) return 0;
    return (curHits != 0 && curHits != flipHits) ? 1 : 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    if ( Tite.getTite().mergeHits()) return x;
    if ( curHits == 0 || curHits == flipHits) return x;
    int a = curHits;
    if ( a > flipHits) a -= flipHits;
      hitMarkers[a - 1].draw( g, x, y, obs, zoom );
    return x + width;
  }
  
  public int myRestrictedDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    if ( Tite.getTite().mergeHits()) return x;
    if ( !Tite.getTite().othersSeeHits()) return x;
    if ( curHits == 0 || curHits == flipHits) return x;
    int a = curHits;
    if ( a > flipHits) a -= flipHits;
      hitMarkers[a - 1].draw( g, x, y, obs, zoom );
    return x + width;
  }
  
  public String displayHits() {
    if ( Tite.getTite().mergeHits() ) {
      if ( curHits == 0 ) return null;
      return "Hits = " + curHits + ((flipHits != 0 && curHits > flipHits ) 
              ? "(" + ( curHits - flipHits ) + ")" : "" );
    }
    int a = curHits;
    if ( flipHits != 0 && a >= flipHits ) a -= flipHits;
    if ( a == 0 ) return null;
    return "Hits = " + a;
  }
  
  public void drawOrigHits(Graphics g, int x, int y, Component obs, double zoom) {
    if ( flipHits != 0 && curHits >= flipHits ) {
      hitViews[ flipHits ].draw(g, x, y, obs, zoom);
      return;
    } else {
      hitViews[0].draw(g, x, y, obs, zoom);
    }
  }
  
  public boolean isReduced() {
    return ( flipHits != 0 && curHits >= flipHits );
  }

  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private IntConfigurer mhConfig;
    private IntConfigurer chConfig;
    private IntConfigurer flip;
    private StringConfigurer flipI;
    private StringArrayConfigurer hitI;
    
    private JPanel panel;
    
    public Ed(TiteHits p) {
      mhConfig = new IntConfigurer ( null, "Maximum Hits: ", p.maxHits );
      chConfig = new IntConfigurer ( null, "Current Hits: ", p.curHits);
      flip = new IntConfigurer (null, "Flip on  Hits: ", p.flipHits );
      flipI = new StringConfigurer (null, "Flipped Image: ", p.flipImage );
      hitI = new StringArrayConfigurer ( null, "Per Hit Images: ", p.hitImages );
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      panel.add(mhConfig.getControls());
      panel.add(chConfig.getControls());
      panel.add(flip.getControls());
      panel.add(flipI.getControls());
      panel.add(hitI.getControls());
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(mhConfig.getValueString());
      se.append(flip.getValueString());
      se.append(flipI.getValueString());
      se.append(hitI.getValueString());
      return ID + se.getValue();
    }
    
     public String getState() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(chConfig.getValueString());
      return se.getValue();
    }
  }
}
