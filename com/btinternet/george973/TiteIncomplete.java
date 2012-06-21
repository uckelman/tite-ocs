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
public class TiteIncomplete extends TiteTrait implements EditablePiece {

  public static final String ID = "tite5;";
  
  private String inc1Image;
  private String inc2Image;
  
  private int incomplete;
  
  private GamePiece inc1View;
  private GamePiece inc2View;
  
  private static GamePiece[] maxApViews = null;
  private static GamePiece[] normalRangeViews = null;
  private static GamePiece[] extendedRangeViews = null;
  
  private KeyStroke theStroke =
          KeyStroke.getKeyStroke( 'I', InputEvent.CTRL_DOWN_MASK);
  
  public TiteIncomplete() {
    this( ID, null );
  }
  
  public TiteIncomplete(String type, GamePiece p) {
    if (maxApViews == null) {
      maxApViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;maxap2.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;maxap3.png;;")
              };
      normalRangeViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;tr3.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;tr4.png;;")
              };
      extendedRangeViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;tr7.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;tr8.png;;")
              };
    }
    setInner(p);
    mySetType(type);
  }
  
    public String getDescription() {
    return "TITE Incomplete";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrai.htm");
  }
  
  public void mySetType ( String type ) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder ( type, ';' );
    st.nextToken(); // discard id
    inc1Image = st.nextToken("");
    inc2Image = st.nextToken("");
    
    if ( ! inc1Image.equals(""))
      inc1View = GameModule.getGameModule().createPiece(BasicPiece.ID + ";;" + inc1Image + ";;");
    if ( ! inc2Image.equals(""))
      inc2View = GameModule.getGameModule().createPiece(BasicPiece.ID + ";;" + inc2Image + ";;");
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append( inc1Image );
    se.append( inc2Image );
    return ID + se.getValue();
  }
  
  public void mySetState ( String type ) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder ( type, ';' );
    incomplete = st.nextInt(0);

  }
  
  public String myGetState () {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append( incomplete );
    return se.getValue();
  }
  
  public Command myKeyEvent ( KeyStroke stroke ) {
    if ( stroke == theStroke ) {
      Command c = Tite.getTite().getStrokeCommand(theStroke, getId(), null);
      incomplete++;
      if ( incomplete == 3 ) incomplete = 0;
      return c;
    }
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[] { new KeyCommand ( "Change Incompleteness", theStroke, this ) };
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
    if ( isConcealed() || isRestricted()) return;
    if ( incomplete != 0 && Tite.getTite().mergeHits()) {
      if ( isExtended() ) {
        extendedRangeViews[incomplete - 1].draw(g, x, y, obs, zoom);
      } else {
        normalRangeViews[incomplete - 1].draw(g, x, y, obs, zoom);
        maxApViews[incomplete-1].draw(g, x, y, obs, zoom);
      }
    }
  }
  
  public String getMyTiteStatus() {
    if ( incomplete != 0 ) {
      return "Incomplete " + incomplete + "\n" +
              ((!isExtended()) ? ( "Max Aps " + (incomplete + 1) + "\n") : "" ) +
              "Range " + ( incomplete - 3 );
    }
    return null;
  }
  
  public String getMyTiteRestrictedStatus() {
    return null;
  }
  
  public int getMyNumberOfMarkers() {
    return (incomplete != 0 && !Tite.getTite().mergeHits() ) ? 1 : 0;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    return 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    if ( incomplete == 0 || Tite.getTite().mergeHits() ) return x;
    ((incomplete == 1) ? inc1View: inc2View).draw(g, x, y, obs, zoom);
    return x + width;
  }

  public int myRestrictedDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  int getMaxAPs() {
    if (incomplete == 0 ) return super.getMaxAPs();
    if ( incomplete == 1 ) return 2;
    return isExtended() ? 2 : 3;
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private ImagePicker picker1;
    private ImagePicker picker2;
    
    private JPanel panel;
    
    public Ed(TiteIncomplete p) {
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      picker1 = new ImagePicker();
      picker1.setImageName(p.inc1Image);
      picker1.setVisible(true);
      panel.add(picker1);
      picker2 = new ImagePicker();
      picker2.setImageName(p.inc2Image);
      picker2.setVisible(true);
      panel.add(picker2);
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(picker1.getImageName());
      se.append(picker2.getImageName());
      return ID + se.getValue();
    }
    
    public String getState() {
      return "0";
    }
  }
}

