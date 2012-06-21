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

import VASSAL.counters.Decorator;
import VASSAL.counters.BasicPiece;
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
public class TiteTraitBase extends TiteTrait implements EditablePiece {
  
  public static final String ID="tite0;";
  
  public static final String TITE = "TITE";
  public static final String ZOOM = "Zooming";
  public static final String NATION = "nation";
  public static final String ARMY = "army";
  public static final String RESTRICTED = "tite-restrict";
  
  private int baseNation;
  private int attachSize;
  private int attachArmy;
  private int attachCorps;
  private boolean canAttach;
  private boolean  attachArmyOnly;
  private boolean doing;
  private boolean hasMoved;
  private boolean fixedAttach;
  private static GamePiece movedFlag;
  private static int halfMovedWidth;
  
  protected boolean zooming = false;
  
  private static KeyStroke markMoved
          = KeyStroke.getKeyStroke( 'M', InputEvent.CTRL_DOWN_MASK);
  
  public TiteTraitBase() {
    this( ID, null );
  }
  
  public TiteTraitBase( String type, GamePiece p ) {
    if ( movedFlag == null ) {
      movedFlag = GameModule.getGameModule().createPiece(BasicPiece.ID + ";;moved.gif;;");
      Rectangle r = movedFlag.boundingBox();
      halfMovedWidth = r.width/2;
    }
    setInner(p);
    mySetType(type);
  }
  
  public String getDescription() {
    return "TITE Base Trait";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrait.htm");
  }

  public void mySetType(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    st.nextToken(); // discard ID
    baseNation = st.nextInt(-1);
    attachSize = st.nextInt(-1);
    canAttach = st.nextBoolean(false);
    attachArmyOnly = st.nextBoolean(false);
    fixedAttach = st.nextBoolean(false);
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(baseNation);
    se.append(attachSize);
    se.append(canAttach);
    se.append(attachArmyOnly);
    se.append(fixedAttach);
    return ID + se.getValue();
  }
  
  public String myGetState() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(attachArmy);
    se.append(attachCorps);
    se.append(doing);
    se.append(hasMoved);
    return se.getValue();
  }
  
  public void mySetState(String newState) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (newState, ';' );
    attachArmy = st.nextInt(-1);
    attachCorps = st.nextInt(-1);
    doing = st.nextBoolean(false);
    hasMoved = st.nextBoolean(false);
  }
  
  public Command myKeyEvent(KeyStroke stroke) {
    if ( stroke == markMoved ) {
      Command c = Tite.getTite().getStrokeCommand( markMoved, getId(), null );
      hasMoved = !hasMoved;
      return c;
    }
    return null;
  }
  
  class AttachKeyCommand extends KeyCommand {
    
    private JMenu attachMenu;
    
    public AttachKeyCommand( String name, GamePiece target, JMenu m ) {
      super(name, (javax.swing.KeyStroke)null, target);
      attachMenu = m;
    }
    
    public JMenu getMenu() {
      return attachMenu;
    }
    
    public void actionPerformed( java.awt.event.ActionEvent evt) {
      
    }
      
  }
  
  public KeyCommand[] myGetKeyCommands() {
    if ( !canAttach || fixedAttach) return new KeyCommand[] { new KeyCommand( "(Un)mark moved", markMoved, this ) };
    KeyCommand k = new AttachKeyCommand ( "Attach to ... ", 
            this, 
            Tite.getTite().getAttachMenu( baseNation, attachSize, this, attachArmyOnly ) );
    return new KeyCommand[]{k, new KeyCommand( "(Un)mark moved", markMoved, this )};
  }

  protected KeyCommand[] getKeyCommands() {
    if (!isVeryRestricted() || ( !canAttach && !isRestricted() )) {
      return super.getKeyCommands();
    } else if ( canAttach  && !isRestricted() && !fixedAttach) {
      return new KeyCommand[] { new AttachKeyCommand ( "Attach to ... ",
              this,
              Tite.getTite().getAttachMenu(baseNation, attachSize, this, attachArmyOnly)) };
    } else {
      return new KeyCommand[0];
    }
  }
  
  public void setMoved( boolean now ) {
    hasMoved = now;
  }

  public String getName() {
    return piece.getName();
  }

  public Shape getShape() {
    if ( zooming && Tite.getTite().displayMarkers() ) return boundingBox();
    return piece.getShape();
  }

  public Rectangle boundingBox() {
    Rectangle r = piece.boundingBox();
    if ( zooming  && Tite.getTite().displayMarkers() ) {
      if (!Boolean.TRUE.equals(Decorator.getOutermost(this).getProperty(Conceal.CONCEALED))) {
        r.width *= ( ( isRestricted() && !Tite.getTite().othersSeeStatus() ) 
                                 ? getRestrictedNumberOfMarkers()
                                 : getNumberOfMarkers() ) + 1;
      }
    }
    if ( hasMoved && !(Tite.getTite().conceal() && isConcealed())) {
      r.width += 2 * halfMovedWidth;
    }
    return r;
  }

  public void draw(Graphics g, int x, int y, Component obs, double zoom) {
    Rectangle r = piece.boundingBox();
    if ( zooming  && Tite.getTite().displayMarkers() ) {
      if (Boolean.TRUE.equals(Decorator.getOutermost(this).getProperty(Conceal.CONCEALED))) {
        x = 0;
      } else {
        x = ( isRestricted() && !Tite.getTite().othersSeeStatus() )
              ? drawRestrictedMarkers( g, x, y, obs, zoom, r.width)
              : drawMarkers( g, x, y, obs, zoom, r.width );
      }
      piece.draw ( g, x, y, obs, zoom );
    } else {
      piece.draw(g, x, y, obs, zoom);
    }
    if (hasMoved ) {
      movedFlag.draw(g, x + (int)((r.width / 2 + halfMovedWidth) * zoom), y, obs, zoom );
    }
  }

   public Object getProperty(Object key) {
    if (Properties.RESTRICTED.equals(key))
      return isVeryRestricted();
    if (RESTRICTED.equals(key))
      return isRestricted();
    if (TITE.equals(key))
     return status();
    if (NATION.equals(key))
      return baseNation;
    if (ARMY.equals(key)) {
      if (attachArmy >= 0 ) return attachArmy;
      if ( attachCorps < 0 ) return -1;
      return Tite.getTite().getArmyForCorps( baseNation, attachCorps );
    }
    if (Properties.MOVED.equals(key)) {
      return hasMoved;
    }
    if (Blind.EYESIGHT.equals(key) && isVeryRestricted() ) {
      return 0;
    }
    if (Properties.SELECTED.equals(key)) {
      if ( isRestricted() ) return false;
    }
    return super.getProperty(key);
  }
  
  public Object getLocalizedProperty(Object key) {
    if (Properties.RESTRICTED.equals(key))
      return isVeryRestricted();
    if (RESTRICTED.equals(key))
      return isRestricted();
    if (TITE.equals(key))
      return status();
    return super.getLocalizedProperty(key);
  }
  
  public void setProperty(Object key, Object val) {
    if ( ZOOM.equals(key)) {
      zooming = Boolean.TRUE.equals(val);
      return;
    }
    if (Properties.MOVED.equals(key)) {
      hasMoved = Boolean.TRUE.equals(val);
    }
    super.setProperty(key, val);
  }
  
  public int getMyNumberOfMarkers() {
    return 0;
  }
  
  public int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    return 0;
  }
  
  public int myRestrictedDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width) {
    return x;
  }
  
  public boolean isRestricted() {
    return Tite.getTite().isRestricted( baseNation, getArmy(), attachCorps );
  }
  
  public boolean isVeryRestricted() {
    return Tite.getTite().isVeryrestricted(baseNation, attachArmy, attachCorps);
  }
  
  public String getMyTiteStatus() {
    if ( !canAttach ) return getName();
    return getName() + " " + Tite.getTite().attachmentString( baseNation, getArmy(), attachCorps );
  }
  
  public String getMyTiteRestrictedStatus() {
    return null;
  }
  
  public String status() {
    if (Boolean.TRUE.equals(Decorator.getOutermost(this).getProperty(Conceal.CONCEALED))) return null;
    if ( Tite.getTite().displayMarkers())
      return (!isRestricted() || Tite.getTite().othersSeeStatus() ) ?  getName()  
              + ( attachArmyOnly ? " " + Tite.getTite().attachmentString(baseNation, attachArmy) : ""  ) : null ;
    if (!isRestricted()) return getTiteStatus();
    if ( Tite.getTite().othersSeeStatus()) return getTiteStatus();
    return getTiteRestrictedStatus();
  }
  
  public void setAttachment( int a, int c ) {
    attachArmy = a;
    attachCorps = c;
  }
  
  public int getNation() {
    return baseNation;
  }
  
  public int getArmy () {
    return attachArmy;
  }
  
  public int getCorps() {
    return attachCorps;
  }
  
  public int getAttachSize() {
    return attachSize;
  }
  
  public boolean attachable() {
    return canAttach && !attachArmyOnly;
  }
  
  public boolean isDoing() {
    return doing;
  }
  
  public void setDoing( boolean now ) {
    doing = now;
  }
  
  public void drawNation(Graphics g, int x, int y, Component obs, double zoom) {
    GamePiece p = Tite.getTite().getNationView( baseNation );
    p.draw(g, x, y, obs, zoom);
  }
  
  public void drawArmy(Graphics g, int x, int y, Component obs, double zoom) {
    GamePiece p;
    if ( attachArmy < 0 ) {
      if ( attachCorps < 0) return;
      p = Tite.getTite().getArmyViewFromCorps( baseNation, attachCorps );
      if ( p == null ) return;
    } else {
      p = Tite.getTite().getArmyView( baseNation, attachArmy );
    }
    p.draw(g, x, y, obs, zoom);
  }
  
  public void drawCorps(Graphics g, int x, int y, Component obs, double zoom) {
    if ( attachCorps < 0 ) return;
    GamePiece p = Tite.getTite().getCorpsView( baseNation, attachCorps );
    p.draw(g, x, y, obs, zoom);
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private IntConfigurer bnConfig;
    private IntConfigurer asConfig;
    private IntConfigurer aaConfig;
    private IntConfigurer acConfig;
    private BooleanConfigurer enable;
    private BooleanConfigurer nearest;
    private BooleanConfigurer fixed;
    
    private JPanel panel;
    
    public Ed(TiteTraitBase p) {
      bnConfig = new IntConfigurer ( null, "Base nation: ", p.baseNation );
      asConfig = new IntConfigurer ( null, "Attachment Size: ", p.attachSize);
      aaConfig = new IntConfigurer ( null, "Attached Army: ", p.attachArmy );
      acConfig = new IntConfigurer ( null, "Attached Corps: ", p.attachCorps );
      enable = new BooleanConfigurer (null, "Can Attach at all: ", p.canAttach );
      nearest = new BooleanConfigurer (null, "Attach to army only: ", p.attachArmyOnly );
      fixed = new BooleanConfigurer ( null, "Attachment is fixed: ", p.fixedAttach );
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      panel.add(bnConfig.getControls());
      panel.add(asConfig.getControls());
      panel.add(aaConfig.getControls());
      panel.add(acConfig.getControls());
      panel.add(enable.getControls());
      panel.add(nearest.getControls());
      panel.add(fixed.getControls());
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(bnConfig.getValueString());
      se.append(asConfig.getValueString());
      se.append(enable.getValueString());
      se.append(nearest.getValueString());
      se.append(fixed.getValueString());
      return ID + se.getValue();
    }
    
     public String getState() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(aaConfig.getValueString());
      se.append(acConfig.getValueString());
      return se.getValue();
    }
  }
}
