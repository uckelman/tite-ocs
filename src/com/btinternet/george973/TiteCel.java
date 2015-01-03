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
public class TiteCel extends TiteTrait implements EditablePiece {
  
  public static final String ID = "tite2;";
  
  private int origCel;
  private int lostCel;
  private int disorder;
  private int recoverCelMps;
  
  protected static GamePiece[] origCelViews = null;
  protected static GamePiece[] curCelViews = null;
  protected static GamePiece[] lostCelViews = null; 
  protected static GamePiece[] recoverCelViews = null;
  protected static GamePiece[] disorderViews = null;
  
  private static KeyStroke loseCEL
          = KeyStroke.getKeyStroke( 'C', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke regainCEL
          = KeyStroke.getKeyStroke( 'R', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke stepCEL
          = KeyStroke.getKeyStroke( 'S', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke cancelSteps
          = KeyStroke.getKeyStroke( 'Y', InputEvent.CTRL_DOWN_MASK);
  
  public TiteCel () {
    this( ID, null );
  }
  
  public TiteCel( String type, GamePiece p ) {
    if ( origCelViews == null ) {
      origCelViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;orig8.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;orig9.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;orig10.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;orig11.png;;")
              };
      curCelViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur4.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur5.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur6.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur7.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur8.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur9.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur10.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cur11.png;;"),
              };
      lostCelViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cel-0.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cel-1.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cel-2.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cel-3.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;cel-4.png;;"),
              };
      recoverCelViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;celr1.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;celr2.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;celr3.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;celr4.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;celr5.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;celr6.png;;"),
              };
      disorderViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;ds1.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;ds2.png;;"),
              };
    }
    setInner (p);
    mySetType (type);
  }
  
  public String getDescription() {
    return "TITE CEL";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrai.htm");
  }
  
  public void mySetType ( String type ) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder ( type, ';' );
    st.nextToken(); // discard id
    origCel = st.nextInt(9);
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append( origCel );
    return ID + se.getValue();
  }
  
  public void mySetState ( String type ) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder ( type, ';' );
    lostCel = st.nextInt(0);
    disorder = st.nextInt(0);
    recoverCelMps = st.nextInt(0);
  }
  
  public String myGetState () {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append( lostCel );
    se.append( disorder );
    se.append( recoverCelMps );
    return se.getValue();
  }
  
  public Command myKeyEvent ( KeyStroke stroke ) {
    if ( stroke == loseCEL && disorder != 2 ) {
      Command c = Tite.getTite().getStrokeCommand( loseCEL, getId(), null );
      if ( lostCel != 4 ) lostCel++;
      else disorder++;
      return c;
    }
    if ( stroke == regainCEL && lostCel != 0 ) {
      Command c = Tite.getTite().getStrokeCommand( regainCEL, getId(), null );
      recoverCelMps = 0;
      if ( disorder != 0 ) disorder--;
      else lostCel--;
      setDoing( false );
      setMoved( true );
      return c;
    }
    if ( stroke == stepCEL  && lostCel != 0 ) {
      Command c = Tite.getTite().getStrokeCommand( stepCEL, getId(), null );
      if ( recoverCelMps == 6 ) {
        if ( disorder != 0 ) disorder--;
        else lostCel--;
        recoverCelMps = 0;
        setDoing ( false );
      } else {
        recoverCelMps++;
        setDoing ( true );
      }
      setMoved( true );
      return c;
    }
    if ( stroke == cancelSteps && recoverCelMps != 0 ) {
      Command c = Tite.getTite().getStrokeCommand(cancelSteps, getId(), null);
      recoverCelMps = 0;
      setDoing ( false );
      return c;
    }
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    if (lostCel == 0) {
      return new KeyCommand[]{new KeyCommand("Lose CEL", loseCEL, this)};
    }
    if ( !isDoing() ) {
      if (disorder == 2) {
        return new KeyCommand[]{new KeyCommand("Recover from Disorder", regainCEL, this),
                  new KeyCommand("Spend MP recovering", stepCEL, this)
                };
      }
      return new KeyCommand[]{new KeyCommand(lostCel == 4 ? "Become Disordered"
                : "Lose CEL", loseCEL, this),
                new KeyCommand(disorder != 0 ? "Recover from Disorder"
                : "Recover CEL", regainCEL, this),
                new KeyCommand("Spend MP recovering", stepCEL, this)
              };
    } else if ( recoverCelMps != 0 ) {
      if (disorder == 2) {
        return new KeyCommand[]{new KeyCommand("Recover from Disorder", regainCEL, this),
                  new KeyCommand("Spend MP recovering", stepCEL, this),
                  new KeyCommand("Cancel MPs spent recovering", cancelSteps, this)
                };
      }
      return new KeyCommand[]{new KeyCommand(lostCel == 4 ? "Become Disordered"
                : "Lose CEL", loseCEL, this),
                new KeyCommand(disorder != 0 ? "Recover from Disorder"
                : "Recover CEL", regainCEL, this),
                new KeyCommand("Spend MP recovering", stepCEL, this),
                new KeyCommand("Cancel MPs spent recovering", cancelSteps, this)
              };
    }
    return new KeyCommand[]{new KeyCommand("Lose CEL", loseCEL, this)};
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
    return "CEL = " + (origCel - lostCel) +
            ((lostCel != 0) ? ( "(" + origCel + ")" ) : "") +
            ((lostCel == 4) ? " DM" : "" ) +
            ((disorder != 0 ) ? ( "\nDisorder" + disorder ) : "" ) +
            ((recoverCelMps != 0 ) ? ( "\n" + recoverCelMps + "MPs rec CEL") : "");
  }

  public String getMyTiteRestrictedStatus() {
    return null;
  }
  
  public int getMyNumberOfMarkers() {
    int noM = 1;
    if (recoverCelMps != 0 ) noM++;
    if ( disorder != 0 ) noM++;
    return noM;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    return 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    drawNation( g, x, y, obs, zoom );
    drawArmy( g, x, y, obs, zoom );
    drawCorps( g, x, y, obs, zoom );
    if ( Tite.getTite().mergeHits() ) drawOrigHits( g, x, y, obs, zoom );
    if ( origCel > 7 && origCel < 12 )
      origCelViews[origCel - 8].draw(g, x, y, obs, zoom);
    if ( lostCel > -1 && lostCel < 5 )
      lostCelViews[lostCel].draw(g, x, y, obs, zoom);
    if ( origCel > 7 && origCel < 12 && lostCel > -1 && lostCel  < 5)            
      curCelViews[ origCel - (lostCel + 4) ].draw(g, x, y, obs, zoom);
    if ( disorder != 0 ) {
      x += width;
      disorderViews[ disorder - 1].draw(g, x, y, obs, zoom);
    }
    if ( recoverCelMps != 0 ) {
      x += width;
      recoverCelViews[ recoverCelMps - 1].draw(g, x, y, obs, zoom);
    }
    return x + width;
  }
   
  public int myRestrictedDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  public int getCEL() {
    return lostCel;
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private IntConfigurer origConfig;
    
    private JPanel panel;
    
    public Ed(TiteCel p ) {
      origConfig = new IntConfigurer ( null, "Original CEL: ", p.origCel );
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      panel.add(origConfig.getControls());
      
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      return ID + origConfig.getValueString();
    }
    
    public String getState() {
      return "0;0;0";
    }
  }
}
