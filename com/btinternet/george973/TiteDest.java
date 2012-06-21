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
import VASSAL.counters.ImagePicker;

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
public class TiteDest extends Decorator implements EditablePiece {
  
  public static final String ID = "tited;";
  
  private int baseNation;
  private int attachArmy;
  private int attachCorps;
  
  public TiteDest() {
    this (ID, null );
  }
  
  public TiteDest( String type, GamePiece p ) {
    setInner(p);
    mySetType(type);
  }
  
  public String getDescription() {
    return "TITE Destination";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrait.htm");
  }

  public void mySetType(String type ) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    st.nextToken(); // discard ID
    baseNation = st.nextInt(-1);
    attachArmy = st.nextInt(-1);
    attachCorps = st.nextInt(-1);
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(baseNation);
    se.append(attachArmy);
    se.append(attachCorps);
    return ID + se.getValue();
  }
  
  public void mySetState(String type ) {
  }
  
  public String myGetState() {
    return "";
  }
  
  public Command myKeyEvent(KeyStroke stroke ) {
    return null;
  }
  
  public KeyCommand[] myGetKeyCommands() {
    return new KeyCommand[0];
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
    if (TiteTraitBase.TITE.equals(key))
      return getName();
    if (Properties.INVISIBLE_TO_ME.equals(key))
      return Tite.getTite().isRestricted( baseNation, attachArmy, attachCorps );
    return super.getProperty(key);
  }
  
  public Object getLocalizedProperty(Object key) {
    if (TiteTraitBase.TITE.equals(key))
      return getName();
    return super.getLocalizedProperty(key);
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private JPanel panel;
    
    public Ed(TiteDest p) {
      
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
