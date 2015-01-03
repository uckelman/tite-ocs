/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.btinternet.george973;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.InputEvent;
import javax.swing.KeyStroke;

import VASSAL.counters.BasicPiece;
import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Properties;
import VASSAL.counters.Stack;
import VASSAL.counters.PieceEditor;

import VASSAL.build.module.documentation.HelpFile;
import VASSAL.tools.SequenceEncoder;
import VASSAL.command.Command;
import VASSAL.build.GameModule;

import javax.swing.JPanel;
import javax.swing.BoxLayout;
import VASSAL.configure.IntConfigurer;
import VASSAL.counters.PieceCloner;

/**
 *
 * @author george
 */
public class TiteRail extends TiteTrait implements EditablePiece {
  
  public static final String ID = "tite6;";
  
  private int railSize;
  private int reducedRailSize;
  
  private boolean entrained;
  private int entrainingMps;
  private int depot;
  private int halved;
  private boolean transShipping;
  
  private static GamePiece[] entrainViews = null;
  private static GamePiece entrainedView = null;
  private static GamePiece[] halfStrength = null;
  private static GamePiece[] depots = null;
  
  private static KeyStroke entrain
          = KeyStroke.getKeyStroke( 'T', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke emergency
          = KeyStroke.getKeyStroke( 'E', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke halfFlip
          = KeyStroke.getKeyStroke( 'F', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke endConsol
          = KeyStroke.getKeyStroke( 'N', InputEvent.CTRL_DOWN_MASK);
  private static KeyStroke transShip
          = KeyStroke.getKeyStroke('W', InputEvent.CTRL_DOWN_MASK);
  
  public TiteRail() {
    this ( ID, null );
  }
  
  public TiteRail( String type, GamePiece p) {
    if ( entrainViews == null ) {
      entrainViews = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;rr1.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;rr2.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;rr3.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;rr4.png;;")
              };
      halfStrength = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;half1.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;half2.png;;")
              };
      depots = new GamePiece[]{
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;depot_reloc.png;;"),
                GameModule.getGameModule().createPiece(BasicPiece.ID + ";;depot_consol.png;;")
              };
      entrainedView = GameModule.getGameModule().createPiece(BasicPiece.ID + ";;rail.png;;");
    }
    
    setInner (p);
    mySetType ( type );
  }
  
  public String getDescription() {
    return "TITE Rail";
  }
  
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("TiteTrait.htm");
  }

  public void mySetType(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    st.nextToken(); // discard ID
    railSize = st.nextInt(4);
    reducedRailSize = st.nextInt(2);
  }
  
  public String myGetType() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(railSize);
    se.append(reducedRailSize);
    return ID + se.getValue();
  }
  
  public void mySetState(String type) {
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (type, ';' );
    entrained = st.nextBoolean(false);
    entrainingMps = st.nextInt(0);
    depot = st.nextInt(0);
    halved = st.nextInt(0);
    transShipping = st.nextBoolean(false);
  }
  
  public String myGetState() {
    SequenceEncoder se = new SequenceEncoder(';');
    se.append(entrained);
    se.append(entrainingMps);
    se.append(depot);
    se.append(halved);
    se.append(transShipping);
    return se.getValue();
  }
  
  public Command myKeyEvent(KeyStroke stroke ) {
    if ( stroke == halfFlip ) {
      Command c = Tite.getTite().getStrokeCommand(halfFlip, getId(), null);
      if ( halved != 0 ) {
        halved++;
        if ( halved == 3 ) halved = 0;
      }
      return c;
    }
    if ( stroke == emergency ) {
      Command c = Tite.getTite().getStrokeCommand(emergency, getId(), null);
      if ( entrained  && entrainingMps != 0 && !transShipping ) {
        entrained = false;
      }
      entrainingMps = 0;
      halved = 1;
      setDoing( false );
      return c;
    }
    if ( stroke == endConsol ) {
      Command c = Tite.getTite().getStrokeCommand(endConsol, getId(), null);
      depot = ( depot == 2 ) ? 0 : depot;
      return c;
    }
    if ( stroke == entrain ) {
      Command c = Tite.getTite().getStrokeCommand(entrain, getId(), null);
      if ( railSize == 0 ) {
        depot = ( depot == 1 ) ? 2 : 1 ;
      } else {
        entrainingMps++;
        setDoing ( true );
        int a = isReduced() ? reducedRailSize : railSize;
        if ( entrainingMps >= a ) {
          entrainingMps = 0;
          if ( entrained ) {
            entrained = false;
            setDoing ( false );
          } else {
            entrained = true;
            createDestination();
          }
        }
      }
      setMoved( true );
      return c;
    }
    if ( stroke == transShip) {
      Command c = Tite.getTite().getStrokeCommand(transShip, getId(), null);
      transShipping = true;
      entrainingMps++;
      int a = isReduced() ? reducedRailSize : railSize;
      if ( entrainingMps >= a ) {
        entrainingMps = 0;
        transShipping = false;
      }
      setMoved( true );
      return c;
    }
    return null;
  }
  
  private void createDestination() {
    Stack s = getParent();
    if (s == null) return;
    GamePiece p = GameModule.getGameModule().createPiece( 
            TiteDest.ID + getNation() + ";" + getArmy() + ";" 
                        + getCorps() + "\t" +
            "delete;Delete;49,130\\\t" +
            BasicPiece.ID + ";;dest.png;Destination for " + piece.getName() + ";" );
    p = PieceCloner.getInstance().clonePiece(p);
    String newGpId = GameModule.getGameModule().getGpIdSupport().generateGpId();
    p.setProperty(Properties.PIECE_ID, newGpId);
    GameModule.getGameModule().getGameState().addPiece(p);
    s.insert(p, s.indexOf(Decorator.getOutermost(this)) + 1);
    p.setMap ( s.getMap());
    p.setPosition( s.getPosition());
    p.setProperty(Properties.SELECTED, Boolean.TRUE);
    setProperty(Properties.SELECTED, Boolean.FALSE);
    s.getMap().repaint();
  }
  
  public KeyCommand[] myGetKeyCommands() {
    if (railSize == 0) {
      switch (depot) {
        case 0:
          return new KeyCommand[]{
                    new KeyCommand("Relocate Depot", entrain, this)
                  };
        case 1:
          return new KeyCommand[]{
                    new KeyCommand("End Depot Relocation", entrain, this)
                  };
        case 2:
          return new KeyCommand[]{
                    new KeyCommand("Relocate Depot", entrain, this),
                    new KeyCommand("End Depot Consolidation", endConsol, this)
                  };
      }
    }
    KeyCommand [] c = new KeyCommand [selectKeyCommands( null )];
    selectKeyCommands(c);
    return c;
  }
  
  public int selectKeyCommands( KeyCommand[] command ) {
    int i = 0;
    if ( transShipping ) {
      if ( command != null )
        command[i] = new KeyCommand("Spend Mp Transshipping", transShip, this);
      i++;
      if ( reducedRailSize >= 0 ) {
        if ( command != null )
          command[i] = new KeyCommand("Emergency detrain", emergency, this);
        i++;
      }
    } else if ( entrained ) {
      if ( command != null )
        command[i] = new KeyCommand("Spend Mp detraining", entrain, this);
      i++;
      if ( reducedRailSize >= 0 ) {
        if ( command != null )
          command[i] = new KeyCommand("Emergency detrain", emergency, this);
        i++;
      }
      if ( this.getNation() == 1 && entrainingMps == 0 ) {
        if ( command != null )
          command[i] = new KeyCommand("Spend Mp Transshipping", transShip, this);
        i++;
      }
    } else {
      if ( !isDoing() || entrainingMps != 0 ) {
        if ( command != null )
          command[i] = new KeyCommand("Spend Mp entraining", entrain, this);
        i++;
      }
      if ( reducedRailSize >= 0 && entrainingMps != 0 ) {
        if ( command != null )
          command[i] = new KeyCommand("Emergency detrain", emergency, this);
        i++;
      }
    }
    if ( halved == 1 ) {
      if ( command != null )
        command[i] = new KeyCommand("Flip Half Strength Counter", halfFlip, this);
      i++;
    } else if ( halved == 2 ) {
      if ( command != null )
        command[i] = new KeyCommand("Remove Half Strength Counter", halfFlip, this);
      i++;
    }
    return i;
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
    if ( depot != 0 ) {
      return (depot == 1) ? "Relocating" : "Consolidating";
    }
    String s = entrained ? "railmode" : null;
    if ( entrainingMps != 0 ) {
      if ( entrained ) s += "\nDetrain " + entrainingMps;
      else s = "Entrain " + entrainingMps;
    }
    if ( halved != 0 ) {
      String t = (halved == 1 ) ? "1/2 Str Inital" : "1/2 Str Final";
      if ( s== null ) s = t;
      else s += "\n" + t;
    }
    return s;
  }
  
  public String getMyTiteRestrictedStatus() {
    if ( entrained ) return "In Railmode";
    return null;
  }
  
  public int getMyNumberOfMarkers() {
    if ( depot != 0 ) return 1;
    int n = 0;
    if ( entrained ) n++;
    if ( entrainingMps != 0 ) n++;
    if ( halved != 0 ) n++;
    return n;
  }
  
  public int getMyRestrictedNumberOfMarkers() {
    if ( entrained ) return 1;
    return 0;
  }
  
  public int myDrawMarkers (Graphics g, int x, int y, Component obs, double zoom, int width) {
    if ( depot != 0 ) {
      depots[depot-1].draw(g, x, y, obs, zoom);
      return x + width;
    }
    if ( entrained ) {
      entrainedView.draw(g, x, y, obs, zoom);
      x += width;
    }
    if ( entrainingMps != 0 ) {
      entrainViews[entrainingMps - 1].draw(g, x, y, obs, zoom);
      x += width;
    }
    if ( halved != 0 ) {
      halfStrength[halved - 1].draw(g, x, y, obs, zoom);
      x += width;
    }
    return x;
  }
  
  public int myRestrictedDrawMarkers (Graphics g, int x, int y, Component obs, double zoom, int width) {
    if ( entrained ) {
      entrainedView.draw(g, x, y, obs, zoom);
      x += width;
    }
    return x;
  }
  
  public boolean isEntrained() {
    return entrained;
  }
  
  public PieceEditor getEditor() {
    return new Ed(this);
  }
  
  public static class Ed implements PieceEditor {
    private IntConfigurer rsConfig;
    private IntConfigurer rssConfig;
    private JPanel panel;
    
    public Ed(TiteRail p) {
      rsConfig = new IntConfigurer ( null, "Rail Size: ", p.railSize );
      rssConfig = new IntConfigurer ( null, "Rail Size (Reduced): ", p.reducedRailSize);
      
      panel = new JPanel();
      panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS));
      
      panel.add(rsConfig.getControls());
      panel.add(rssConfig.getControls());
    }
    
    public Component getControls() {
      return panel;
    }
    
    public String getType() {
      SequenceEncoder se = new SequenceEncoder(';');
      se.append(rsConfig.getValueString());
      se.append(rssConfig.getValueString());
      return ID + se.getValue();
    }
    
     public String getState() {
      return "false;0;0;0";
    }
  }
  
}
