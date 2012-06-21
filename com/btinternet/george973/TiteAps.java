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
public class TiteAps extends TiteTrait implements EditablePiece {
  
  public static final String ID = "tite4;";
  
  private int usedAPs;
  
  static protected GamePiece[] APLeftViews;
  
  private static KeyStroke useStroke
          = KeyStroke.getKeyStroke( 'U', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke clearStroke
          = KeyStroke.getKeyStroke( 'Z', InputEvent.CTRL_DOWN_MASK);
  
  public TiteAps() {
    this( ID, null );
  }
  
  public TiteAps( String type, GamePiece p ) {
    if ( APLeftViews == null ) {
      APLeftViews = new GamePiece[] {
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;ap0.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;ap1.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;ap2.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;ap3.png;;")
      };
    }
    
    setInner(p);
    mySetType(type);
  }

  public String getDescription() {
    return "TITE AP Expenditure";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrait.htm");
  }

  public void mySetType(String type) {
  }
  
  public String myGetType() {
    return ID;
  }

  public void mySetState(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    usedAPs = st.nextInt(0);
  }
  
  public String myGetState() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(usedAPs);
    return se.getValue();
  }
  
  public Command myKeyEvent(KeyStroke stroke) {
    if ( stroke == useStroke) {
      Command c = Tite.getTite().getStrokeCommand( useStroke, getId(), null);
      usedAPs++;
      if ( usedAPs > getMaxAPs() ) usedAPs = getMaxAPs();
      return c;
    }
    if ( stroke == clearStroke) {
      Command c = Tite.getTite().getStrokeCommand( clearStroke, getId(), null);
      usedAPs = 0;
      return c;
    }
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    int a = getMaxAPs();
    if ( a == 0 && usedAPs == 0) return new KeyCommand[0];
    if ( (a == 0 && usedAPs != 0)  || ( a != 0 && usedAPs >= a ))
      return new KeyCommand[] { new KeyCommand( "Clear APs Used", clearStroke, this) };
    if ( a != 0 && usedAPs == 0 )
      return new KeyCommand[] { new KeyCommand( "Mark an AP used", useStroke, this ) };
    return new KeyCommand[] { new KeyCommand( "Mark an AP used", useStroke, this ),
                              new KeyCommand( "Clear APs Used", clearStroke, this)
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
    int a = getMaxAPs();
    if ( a == 0 || usedAPs == 0 ) return null;
    return "APs left = " + (a - usedAPs ); 
  }
  
  public String getMyTiteRestrictedStatus() {
    return null;
  }
  
  public int getMyNumberOfMarkers() {
    int a = getMaxAPs();
    if ( usedAPs > a ) usedAPs = a;
    if ( a == 0 || usedAPs == 0 ) return 0;
    return 1;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    return 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    int a = getMaxAPs();
    if ( usedAPs > a ) usedAPs = a;
    if ( a == 0 || usedAPs == 0 ) return x;
    APLeftViews[a - usedAPs].draw(g, x, y, obs, zoom);
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
    
    public Ed(TiteAps p) {
      
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
      return "0";
    }
  }
}
