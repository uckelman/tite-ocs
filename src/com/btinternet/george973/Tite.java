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

import VASSAL.build.AbstractBuildable;
import VASSAL.build.AbstractConfigurable;
import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.Chatter;
import VASSAL.build.module.GameComponent;
import VASSAL.build.module.GlobalOptions;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.build.module.Map;

import VASSAL.command.Command;
import VASSAL.command.CommandEncoder;

import VASSAL.counters.BasicPiece;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Decorator;
import VASSAL.counters.Stack;

import VASSAL.configure.BooleanConfigurer;
import VASSAL.configure.StringArrayConfigurer;
import VASSAL.configure.IntConfigurer;

import VASSAL.i18n.Resources;

import VASSAL.tools.ToolBarComponent;
import VASSAL.tools.SequenceEncoder;
import VASSAL.tools.IconButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;


/**
 *
 * @author george
 */
public class Tite extends AbstractConfigurable
               implements GameComponent,
                          CommandEncoder {
  
  class Corps {
    protected String id;
    protected int[] scenarios;
    protected int maxAttached;
    protected int attachedTo;
    protected Nation nation;
    protected int no;
    protected int curSize;
    protected GamePiece view;
    protected int noAttached;
    protected String[] attachedNames;
    protected int[] attachedCEL;
           
    Corps( String n, int[] p, int q) {
      this ( n, p, q, -1 );
    }
    
    Corps ( String n, int[]p, int q, int r ) {
      id = n;
      scenarios = p;
      maxAttached = q;
      attachedTo = r;
    }
    
    void initialise ( int i, Nation n ) {
      no = i;
      nation = n;
      view = GameModule.getGameModule().createPiece(BasicPiece.ID
            + ";;" + nation.name + id + ".png;;" );
    }
            
    boolean inScenario(int scenario ) {
      for ( int i = 0; i < scenarios.length; i++) {
        if ( scenarios[i] == scenario ) return true;
      }
      return false;
    }
    
    void countAttachment() {
      if ( attachedTo >= 0) {
        nation.armies[attachedTo].curCorps++;
      }
    }
    
    boolean testCorpsAccessible( String user ) {
      if ( attachedTo >= 0 ) {
        if ( !nation.commander.equals(user)
                && !nation.armies[attachedTo].commander.equals(user))
          return false;
        return true;
      }
      if ( nation.commander.equals(user)) return true;
      for ( int i = 0; i < nation.armies.length; i++ ) {
        if (nation.armies[i].commander.equals(user)) return true;
      }
      return false;
    }
    
    boolean armyFull( Army a ) {
      return  ( a.curCorps +1 > a.maxCorps);
    }
    
    void addToAttachMenu( String user ) {
      if ( !inScenario(curScenario)) return;
      if ( !testCorpsAccessible( user ) ) return;
      JMenu subMenu = new JMenu( nation.name + " " + id + " Corps");
      buildAttachMenu ( user, subMenu );
      menu.add(subMenu);
    }
    
    void buildAttachMenu( String user, JMenu subMenu ) {
      for ( int i = 0; i < nation.armies.length; i++ ) {
        if ( !nation.armies[i].inScenario(curScenario))
          continue;
        if ( !nation.armies[i].commander.equals("")
                && (!nation.armies[i].commander.equals(user)
                && !nation.commander.equals(user) ))
          continue;
        if ( i == attachedTo) continue;
        if ( armyFull( nation.armies[i] )) {
          JMenuItem mj = new JMenuItem( nation.name + " " + nation.armies[i].id + " Army - full");
          mj.setEnabled( false );
          subMenu.add( mj );
          continue;
        }
        JMenuItem mi = new JMenuItem ( "Attach to " + nation.name + " "
                + nation.armies[i].id + " Army");
        mi.setEnabled( true );
        final Army a = nation.armies[i];
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            attachCorps( nation.no, no, a );
          }
        });
        subMenu.add( mi );
      }
      if ( !nation.commander.equals(user) && attachedTo >= 0 ) {
         JMenuItem mi = new JMenuItem ( "Detach From " + nation.name + " "
                + nation.armies[attachedTo].id + " Army");
        mi.setEnabled( true ); 
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            attachCorps( nation.no, no, null );
          }
        });
        subMenu.add(mi);
      }
    }
    
    public void attachCorps( int nationNumber, int corpsNumber, Army a ) {
      Command c = new AttachCorpsCommand( nationNumber, corpsNumber, a == null ? -1 : a.no , attachedTo);
      doAttachCorps( a == null ? -1 : a.no );
      GameModule.getGameModule().sendAndLog(c);      
    }
    
    public void doAttachCorps( int armyNumber ) {
      attachedTo = armyNumber;
    }
    
    int getMaxAttached() {
      return maxAttached;
    }
    
    void addToUnitAttachMenu ( JMenu m, int size, final GamePiece p) {
      if ( !inScenario(curScenario)) return;
      JMenuItem mi = new JMenuItem( nation.name + " " + id
            + " Corps" + (curSize + size > getMaxAttached() ? " - full" : "" ));
      if ( curSize + size > getMaxAttached() ) {
        mi.setEnabled ( false );
      } else {
        mi.setEnabled ( true );
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            attachUnit ( nation.no, no, p );
          }
        });
      }
      if ( attachedTo >= 0 ) {
        if ( !nation.armies[attachedTo].commander.equals(GameModule.getUserId())
                && !nation.commander.equals(GameModule.getUserId())) {
          mi.setBackground(Color.ORANGE);
        }
      } else {
        mi.setBackground(Color.yellow);
      }
      m.add(mi);      
    }
      
    public void attachUnit( int n, int c, GamePiece p ) {
      TiteTraitBase t = (TiteTraitBase) p;
      Command x = new AttachUnitCommand ( n, -1, c,
              t.getArmy(), t.getCorps(), p.getId());
      doUnitAttach( p );
      GameModule.getGameModule().sendAndLog(x);
    }
    
    public void doUnitAttach( GamePiece p ) {
      TiteTrait t = (TiteTrait) p;
      t.setAttachment ( -1, no);
    }
    
    void addToAttachmentMenu ( JMenu submenu ) {
      JMenuItem mi = new JMenuItem( id + " corps" ); 
      mi.setEnabled ( true );
      mi.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e) {
        }
      });
      mi.setBackground(Color.WHITE);
      submenu.add( mi );
     for ( int i = 0; i < noAttached; i++ ) {
        if ( attachedNames[i] == null ) continue;
        mi = new JMenuItem( attachedNames[i]);
        mi.setEnabled ( true );
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
          }
        });
        if ( attachedCEL[i] != 0 )
          mi.setBackground(celColours[attachedCEL[i] - 1]);
        submenu.add( mi );
      }
    }
  }
  
  class CavCorps extends Corps {
    
    CavCorps( String n, int[]p, int q) {
      super (n, p, q);
    }
    
    CavCorps ( String n, int[] p, int q, int r ) {
      super ( n, p, q, r );
    }

    boolean armyFull( Army a ) {
      return  ( a.curCorps + a.curCavCorps +1 > a.maxCorps + a.maxCavCorps );
    }
    
    void countAttachment() {
      if ( attachedTo >= 0) {
        nation.armies[attachedTo].curCavCorps++;
      }
    }
  }
  
  class FixedCorps extends Corps {
    
    FixedCorps( String n, int[]p, int q) {
      super (n, p, q);
    }
    
    FixedCorps ( String n, int[] p, int q, int r ) {
      super ( n, p, q, r );
    }

    void addToAttachMenu( String user ) {
      return;
    }
  }
  
  class LW3Corps extends Corps {
    
    LW3Corps( String n, int[]p, int  q) {
      super ( n, p, q );
    }

    boolean testCorpsAccessible( String user ) {
      if ( attachedTo >= 0 ) {
        if ( !nation.commander.equals(user)
                && !nation.armies[attachedTo].commander.equals(user))
          return false;
        return true;
      }
      if ( nations[0].commander.equals(user)) return true;
      for ( int i = 0; i < nations[0].armies.length; i++ ) {
        if (nations[0].armies[i].commander.equals(user)) return true;
      }
      if ( nations[2].commander.equals(user)) return true;
      for ( int i = 0; i < nations[2].armies.length; i++ ) {
        if (nations[2].armies[i].commander.equals(user)) return true;
      }
      return false;
    }
    
    boolean armyFull( Army a ) {
      return  ( a.curCorps +1 > a.maxCorps);
    }
    
    void addToAttachMenu( String user ) {
      if ( !inScenario(curScenario)) return;
      if ( !testCorpsAccessible( user ) ) return;
      Nation save = nation;
      int aSave = attachedTo;
      nation = nations[0];
      if ( save != nation ) attachedTo = -1;
      JMenu subMenu = new JMenu( nation.name + " " + id + " Corps");
      if ( save != nation ) attachedTo = -1;
      buildAttachMenu ( user, subMenu );
      attachedTo = aSave;
      nation = nations[2];
      if ( save != nation ) attachedTo = -1;
      buildAttachMenu( user, subMenu );
      attachedTo = aSave;
      nation = save;
      menu.add(subMenu);
    }
        
    public void attachCorps( int nationNumber, int corpsNumber, Army a ) {
      int an;
      int aq;      
      if ( a == null ) {
        an = -1;
      } else if ( a.nation == nations[0] ) {
        an = a.no;
      } else {
        an = a.no + nations[0].armies.length;
      }
      aq = attachedTo;
      if ( nation != nations[0] ) aq += nations[0].armies.length;
      Command c = new AttachCorpsCommand( nationNumber, corpsNumber, an , aq);
      doAttachCorps( an );
      GameModule.getGameModule().sendAndLog(c);      
    }
    
    public void doAttachCorps( int armyNumber ) {
      if ( armyNumber < nations[0].armies.length ) {
        attachedTo = armyNumber;
        nation = nations[0];
      } else {
        attachedTo = armyNumber - nations[0].armies.length;
        nation = nations[2];
      }
    }
    
    int getMaxAttached() {
      if ( gameTurn < 30 ) return 4;
      return 8;
    }
    
  }
  
  class Army {
    protected String id;
    protected int[] scenarios;
    protected int maxCorps;
    protected int maxCavCorps;
    protected String commander;
    protected String commanderName;
    protected int curCorps;
    protected int curCavCorps;
    protected int no;
    protected Nation nation;
    protected GamePiece view;
    protected int noAttached;
    protected String[] attachedNames;
    protected int[] attachedCEL;
               
    Army ( String n, int[] p, int q, int r) {
      id = n;
      scenarios = p;
      maxCorps = q;
      maxCavCorps = r;
      commander = "";
    }
    
    void initialise ( int i, Nation n ) {
      no = i;
      nation = n;
      view = GameModule.getGameModule().createPiece(BasicPiece.ID
            + ";;" + id + ".png;;" );
    }
            
    boolean inScenario(int scenario ) {
      for ( int i = 0; i < scenarios.length; i++) {
        if ( scenarios[i] == scenario ) return true;
      }
      return false;
    }
       
    boolean addToCommandMenu() {
      if ( !inScenario(curScenario)) return false;
      if ( commander.equals("")) {
        JMenuItem mi = new JMenuItem( "Command " + nation.name + " " + id
                + " Army" );
        mi.setEnabled ( true );
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            takeCommand( nation.no, no );
          }
        });
        menu.add(mi);
        return false;
      }
      if ( commander.equals(GameModule.getUserId())) {
        JMenuItem mi = new JMenuItem( "Resign " + nation.name + " " + id
                + " Army" );
        mi.setEnabled ( true );
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            resignCommand( nation.no, no );
          }
        });
        menu.add(mi);
        return true;
      }
      JMenuItem mi = new JMenuItem( nation.name + " " + id
                + " Army - " + commanderName );
      mi.setEnabled ( false );
      menu.add(mi);
      return true;
    }
    
    void takeCommand ( int nationNumber, int armyNumber ) {
      doTakeCommand( GameModule.getUserId(),(String)GameModule.getGameModule()
              .getProperty(GlobalOptions.PLAYER_NAME) );
      Command c = new TakeCommand ( nationNumber, armyNumber, commander,
              commanderName );
      GameModule.getGameModule().sendAndLog( c );
    }
    
    void doTakeCommand( String c, String d) {
      commander = c;
      commanderName = d;
      List <Map> a = GameModule.getGameModule().getAllDescendantComponentsOf(Map.class);
      Map m = a.get(0);
      m.repaint();
    }
    
    void resignCommand( int nationNumber, int armyNumber ) {
      Command c = new ResignCommand ( nationNumber, armyNumber, commander,
              commanderName );
      doResignCommand();
      GameModule.getGameModule().sendAndLog(c);
    }
    
    void doResignCommand() {
      commander = "";
      commanderName = "";
      List <Map> a = GameModule.getGameModule().getAllDescendantComponentsOf(Map.class);
      Map m = a.get(0);
      m.repaint();
    }
    
    void addToUnitAttachMenu ( JMenu m, final GamePiece p, boolean armyOnly) {
      if ( !inScenario(curScenario)) return;
      JMenuItem mi;
      mi = new JMenuItem( nation.name + " " + id
              + " Army" + ( armyOnly ? "" : " as independent") );
      mi.setEnabled ( true );
      mi.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          attachUnit ( nation.no, no, p );
        }
      });
      if ( !armyOnly && !commander.equals(GameModule.getUserId())
              && !nation.commander.equals(GameModule.getUserId()))
        mi.setBackground(Color.orange);
      m.add(mi);      
    }
    
    public void attachUnit( int n, int a, GamePiece p ) {
      TiteTraitBase t = (TiteTraitBase) p;
      Command x = new AttachUnitCommand ( n, a, -1,
              t.getArmy(), t.getCorps(), p.getId());
      doUnitAttach( p );
      GameModule.getGameModule().sendAndLog(x);
     }

    public void doUnitAttach( GamePiece p ) {
      TiteTrait t = (TiteTrait) p;
      t.setAttachment ( no, -1);
    }
    
    JMenu buildAttachmentMenu () {
      JMenu submenu = new JMenu( nation.name + " " + id + " Army" );
      for ( int i = 0; i < noAttached; i++ ) {
        if ( attachedNames[i] == null ) continue;
        JMenuItem mi = new JMenuItem( attachedNames[i]);
        mi.setEnabled ( true );
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
          }
        });
        if ( attachedCEL[i] != 0 )
          mi.setBackground(celColours[attachedCEL[i] - 1]);
        submenu.add( mi );
      }
      for ( int i = 0 ; i < nation.corps.length; i++ ) {
        if ( nation.corps[i].attachedTo == no && nation == nation.corps[i].nation )
          nation.corps[i].addToAttachmentMenu( submenu);
      }
      if ( nation == nations[2] && LW3Corps.nation == nation
              && LW3Corps.attachedTo == no ) {
        LW3Corps.addToAttachmentMenu(submenu);
      }
      return submenu;
    }
  }
  
  class Nation {
    protected String name;
    protected int[] scenarios;
    protected Army[] armies;
    protected Corps[] corps;
    protected String commander;
    protected String commanderName;
    protected int no;
    protected GamePiece view;
    
    Nation( String n, int[] p, Army[] q, Corps[] r) {
      name = n;
      scenarios = p;
      armies = q;
      corps = r;
      commander = "";
      commanderName = "";
    }
    
    void initialise( int n ) {
      no = n;
      for (int i = 0; i < armies.length; i++ ) {
        armies[i].initialise( i, this );
      }
      for (int i = 0; i < corps.length; i++ ) {
        corps[i].initialise( i, this );
      }
      view = GameModule.getGameModule().createPiece(BasicPiece.ID
            + ";;" + name  + ".png;;" );
    }
    
    boolean inScenario(int scenario ) {
      for ( int i = 0; i < scenarios.length; i++) {
        if ( scenarios[i] == scenario ) return true;
      }
      return false;
    }
    
    void prepareToAttach() {
      for ( int i = 0; i < armies.length; i++ ) {
        armies[i].curCorps = 0;
        armies[i].curCavCorps = 0;
      }
    }
    
    void countAttachments() {
      for ( int i = 0; i < corps.length; i++ ) {
        corps[i].countAttachment();
      }
    }
    
    void addToAttachMenu( String user ) {
      if ( !inScenario(curScenario)) return;
      if ( !commander.equals("") && !commander.equals(user)) return;
      for ( int i = 0; i < corps.length; i++ ) {
        corps[i].addToAttachMenu( user );
      }
    }
    
    void addToCommandMenu() {
      if ( !inScenario(curScenario)) return;
      if ( !commander.equals("") ) {
        addNationToCommandMenu();
        return;
      }
      boolean noCommanders = true;
      for ( int i = 0; i < armies.length; i++ ) {
        if ( !armies[i].commander.equals("") ) {
          noCommanders = false;
          break;
        }
      }
      if ( noCommanders ) addNationToCommandMenu();
      for ( int i = 0; i < armies.length; i++ ) {
        armies[i].addToCommandMenu();
      }
    }
    
    boolean isCommander() {
      if ( commander.equals(GameModule.getUserId())) return true;
      for ( int i = 0; i < armies.length; i++ ) {
        if ( armies[i].commander.equals(GameModule.getUserId())) return true;
      }
      return false;
    }
    
    void addToAttachmentMenu() {
      if ( commander.equals(GameModule.getUserId())) {
        for ( int i = 0; i < armies.length; i++ ) {
          if ( !armies[i].inScenario(curScenario)) continue;
          menu.add( armies[i].buildAttachmentMenu() );
        }
      } else {
        for ( int i = 0; i < armies.length; i++ ) {
          if ( !armies[i].inScenario(curScenario)) continue;
          if ( armies[i].commander.equals(GameModule.getUserId()))
            menu.add( armies[i].buildAttachmentMenu() );
        }
        
      }
    }
    
    void addNationToCommandMenu() {
      if ( commander.equals("")) {
        JMenuItem mi = new JMenuItem( "Command the " + name +  " Nation" );
        mi.setEnabled ( true );
        mi.setBackground( Color.ORANGE );
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            takeCommand( no );
          }
        });
        menu.add(mi);
        return;
      }
      if ( commander.equals(GameModule.getUserId())) {
        JMenuItem mi = new JMenuItem( "Resign the " + name + " Nation" );
        mi.setEnabled ( true );
        mi.setBackground( Color.ORANGE );
        mi.addActionListener( new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            resignCommand( no );
          }
        });
        menu.add(mi);
        return;
      }
      JMenuItem mi = new JMenuItem( "All " + name + " Armies - " + commanderName );
      mi.setEnabled ( false );
      menu.add(mi);
    }
    
    void takeCommand ( int nationNumber ) {
      doTakeCommand( GameModule.getUserId(),(String)GameModule.getGameModule()
              .getProperty(GlobalOptions.PLAYER_NAME) );
      Command c = new TakeCommand ( nationNumber, -1, commander,
              commanderName );
      GameModule.getGameModule().sendAndLog( c );
    }
    
    void doTakeCommand( String c, String d) {
      commander = c;
      commanderName = d;
       List <Map> a = GameModule.getGameModule().getAllDescendantComponentsOf(Map.class);
      Map m = a.get(0);
      m.repaint();
      updateToolbar();
   }
    
    void resignCommand( int nationNumber ) {
      Command c = new ResignCommand ( nationNumber, -1, commander,
              commanderName );
      doResignCommand();
      GameModule.getGameModule().sendAndLog(c);
    }
    
    void doResignCommand() {
      commander = "";
      commanderName = "";
      List <Map> a = GameModule.getGameModule().getAllDescendantComponentsOf(Map.class);
      Map m = a.get(0);
      m.repaint();
      updateToolbar();
    }

    public void resignArmyCommand( int army ) {
      if ( army == -1 ) {
        doResignCommand();
        return;
      }
      armies[army].doResignCommand();
    }
    
    public void takeArmyCommand( int army, String c, String d ) {
      if ( army == -1 ) {
        doTakeCommand( c, d );
        return;
      }
      armies[army].doTakeCommand(c, d);
    }
    
    public void processPiece3( GamePiece q ) {
      TiteTraitBase t = (TiteTraitBase) Decorator.getDecorator(q, TiteTraitBase.class );
      if ( t == null ) return;
      if ( t.getNation() != no ) return;
      int c = t.getCorps();
      if ( c >= 0 ) {
        corps[c].curSize += t.getAttachSize();
      }
    }
    
    public JMenu buildUnitAttachMenu( int size, GamePiece p, boolean armyOnly ) {
      JMenu m = new JMenu("Attach to ... ");
      for ( int i = 0 ; i < armies.length; i++ ) {
        armies[i].addToUnitAttachMenu( m, p, armyOnly );
      }
      if ( armyOnly ) return m;
      for ( int i = 0 ; i < corps.length; i++ ) {
        corps[i].curSize = 0;
      }
      List <Map> a = GameModule.getGameModule().getAllDescendantComponentsOf(Map.class);
      for (Map mx: a) {
        GamePiece[] pieces = mx.getAllPieces();
        for ( int i = 0; i < pieces.length; i++ ) {
          GamePiece q = pieces[i];
          if (q.getClass() == Stack.class ) {
            Stack sub = (Stack)q;
            for (Iterator<GamePiece> e = sub.getPiecesIterator();
                    e.hasNext();) {
              GamePiece r = e.next();
              processPiece3( r );
            }
          } else {
            processPiece3( q );
          }
        }
      }
      for ( int i = 0 ; i < corps.length; i++ ) {
        corps[i].addToUnitAttachMenu( m, size, p );
      }
      return m;
    }
    
    boolean testArmyOnly() {
      return !commander.equals(GameModule.getUserId());
    }
    
    boolean testDetached() {
      if ( commander.equals(GameModule.getUserId())) return false;
      for ( int i = 0; i < armies.length; i++ ) {
        if ( armies[i].commander.equals(GameModule.getUserId())) return false;
      }
      return true;
    }
    
    boolean testAttached( int army ) {
      if ( commander.equals(GameModule.getUserId())) return false;
      if ( armies[army].commander.equals(GameModule.getUserId())) return false;
      return true;
    }
  }
  
  final static Color celColours[] = new Color[] {
    Color.GREEN,
    Color.YELLOW,
    Color.ORANGE,
    Color.RED
  };
    
  static Nation[] nations;
  
  static Corps LW3Corps;
  
  private void initialiseNations() {
    nations = new Nation[3];
    
    int [] s1 = { 1, 5, 7, 6};
    int [] s2 = { 7, 6 };
    int [] s3 = { 5, 7, 6};
    int [] s4 = { 1, 2, 3, 4, 5, 6, 7};
    int [] s5 = { 1, 7, 6 };
    int [] s6 = { 2, 3, 4, 6, 7};
    int [] s7 = { 3, 4, 6, 7};
    
    
    Army[] a = new Army[4];
    a[0] = new Army( "EIGHTH", s1, 8, 2);
    a[1] = new Army( "NINTH", s2, 10, 2);
    a[2] = new Army( "AGF", s3, 2, 0);
    a[3] = new Army( "AGW", s2, 2, 0);
    
    Corps[] c = new Corps[22];
    c[0] = new Corps( "GDR", s1, 6);
    c[1] = new Corps( "1", s1, 7, 0 );
    c[2] = new Corps( "2", s2, 6 );
    c[3] = new Corps("11", s1, 6 );
    c[4] = new Corps("13", s2, 6 );
    c[5] = new Corps("17", s1, 6, 0 );
    c[6] = new Corps("20", s1, 6, 0 );
    c[7] = new CavCorps("1C", s2, 6, 0 );
    c[8] = new CavCorps("3C", s2, 6, 0 );
    c[9] = new CavCorps("4C", s2, 6, 0 );
    c[10] = new Corps("1R", s1, 6, 0 );
    c[11] = new Corps( "3R", s2, 6 );
    c[12] = new Corps( "24R", s2, 6 );
    c[13] = new Corps( "25R", s2, 6 );
    c[14] = new Corps( "BU", s2, 4 );
    c[15] = new Corps( "FR", s2, 6 );
    c[16] = new Corps( "GZ", s2, 6 );
    c[17] = new Corps( "KG", s1, 8, 0 );
    c[18] = new Corps( "MO", s5, 5 );
    c[19] = new Corps( "PN", s2, 6 );
    c[20] = new Corps( "TN", s2, 4 );
    c[21] = new LW3Corps( "3LW", s6, 4);
    
    LW3Corps = c[21];
    
    nations[0] = new Nation( "German", s1, a, c );
    
    a = new Army[11];
    a[0] = new Army( "FIRST", s1, 5, 1);
    a[1] = new Army( "SECOND", s3, 6, 1);
    a[2] = new Army( "TENTH", s1, 6, 1);
    a[3] = new Army( "NINTH", s6, 5, 1);
    a[4] = new Army( "THIRD", s7, 6, 1);
    a[5] = new Army( "FOURTH", s6, 5, 1);
    a[6] = new Army( "FIFTH", s6, 5, 1 );
    a[7] = new Army( "EIGHTH", s7, 5, 1 );
    a[8] = new Army( "DN", s7, 1, 1);
    a[9] = new Army( "ELEVENTH", s2, 3, 0);
    a[10] = new Army( "WARSAW", s2, 10, 0 );
    
    c = new Corps[45];
    c[0] = new Corps("1", s3, 6, 1);
    c[1] = new Corps("2", s1, 6, 0);
    c[2] = new Corps("3", s1, 6, 0);
    c[3] = new Corps("4", s1, 6, 0);
    c[4] = new Corps("6", s3, 6, 1);
    c[5] = new Corps("13", s3, 6, 1);
    c[6] = new Corps("15", s3, 6, 1);
    c[7] = new Corps("20", s1, 6, 0 );
    c[8] = new Corps("22", s1, 6 );
    c[9] = new Corps("23", s3, 6, 1);  
    c[10] = new CavCorps("1C", s1, 8);
    c[11] = new Corps("26R", s1, 6 );
    c[12] = new Corps("27R", s2, 6 );
    c[13] = new Corps("3S", s2, 6 );
    c[14] = new Corps("1TK", s2, 6 );
    c[15] = new Corps("GD", s6, 6, 3);
    c[16] = new Corps("GR", s6, 6, 5);
    c[17] = new Corps("5", s6, 6, 6);
    c[18] = new Corps("7", s7, 6, 7);
    c[19] = new Corps("8", s7, 6, 7);
    c[20] = new Corps("9", s7, 6, 4);
    c[21] = new Corps("10", s7, 6, 4);
    c[22] = new Corps("11", s7, 6, 4);
    c[23] = new Corps("12", s7, 6, 7);
    c[24] = new Corps("14", s6, 6, 5);
    c[25] = new Corps("16", s6, 6, 5);
    c[26] = new Corps("17", s6, 6, 6);
    c[27] = new Corps("18", s6, 6, 3);
    c[28] = new Corps("19", s6, 6, 6);
    c[29] = new Corps("21", s7, 6, 4);
    c[30] = new Corps("24", s7, 6, 8);
    c[31] = new Corps("25", s6, 6, 6);
    c[32] = new CavCorps("2C", s7, 5);
    c[33] = new CavCorps("3C", s7, 5);
    c[34] = new CavCorps("4C", s7, 5);
    c[35] = new Corps("3CN", s6, 6);
    c[36] = new Corps("2CN", s2, 6);
    c[37] = new Corps("27R", s2, 6);
    c[38] = new Corps("28R", s2, 6);
    c[39] = new Corps("29R", s2, 6);
    c[40] = new Corps("30R", s2, 6);
    c[41] = new Corps("1S", s2, 6);
    c[42] = new Corps("2S", s2, 6);
    c[43] = new Corps("5S", s2, 6);
    c[44] = new Corps("6S", s2, 6);
    
    nations[1] = new Nation( "Russian", s4, a, c);
    
    a = new Army[5];
    a[0] = new Army( "FIRST", s6, 5, 0);
    a[1] = new Army( "SECOND", s7, 5, 0);
    a[2] = new Army( "THIRD", s7, 5, 0);
    a[3] = new Army( "FOURTH", s6, 5, 0);
    a[4] = new Army( "AG P-B", s2, 2, 0);
    
    c = new Corps[19];
    c[0] = new Corps("1", s6, 8, 0);
    c[1] = new Corps("2", s6, 8, 3);
    c[2] = new Corps("3", s7, 8, 2);
    c[3] = new Corps("4", s7, 8);
    c[4] = new Corps("5", s6, 8, 0);
    c[5] = new Corps("6", s6, 8, 3);
    c[6] = new Corps("7", s7, 8);
    c[7] = new Corps("9", s6, 8, 3);
    c[8] = new Corps("10", s6, 8, 0);
    c[9] = new Corps("11", s7, 8, 2);
    c[10] = new Corps("12", s7, 8, 1);
    c[11] = new Corps("14", s6, 8, 2);
    c[12] = new Corps("17", s6, 6);
    c[13] = new Corps("18", s2, 6);
    c[14] = new CavCorps("1C", s2, 6);
    c[15] = new CavCorps("2C", s2, 6);
    c[16] = new Corps("HN", s2, 6);
    c[17] = new Corps("Kummer", s6, 7);
    c[18] = new Corps("SY", s7, 6);
    
    nations[2] = new Nation("Austrian", s6, a, c);
    
    for ( int i = 0; i < nations.length; i++ ) {
      nations[i].initialise(i);
    }
  }

  int curScenario = 1;
  int gameTurn = 11;
  int player = 1;
  int phase = 0;
  boolean interphase = false;
  boolean starting = false;
  boolean finishing = false;

  int VPs = 0;

  int[] wSize = new int[7];
  
  int weather = 1;

  static final String[] turnNames = {
    "Aug 16-22", "Aug 23-28", "Aug 29 - Sept 3", "Sept 4-10", "Sept 11-17", "Sept 18-25",
    "Sept 26 - Oct 3", "Oct 4-11", "Oct 12-20", "Oct 21-29", "Oct 30 - Nov 7",
    "Nov 8-16", "Nov 17-25", "Nov 26 - Dec 4", "Dec 5-19", "Dec 5-19"
  };

  static final String[] phaseNames = {
    "Reinforcement etc Phase", "Attachment Phase", "Pontoon Bridge Phase",
    "Supply Phase", "RR Engineering Phase", "Movement Phase", "Counter Movement Phase",
    "Attack Phase", "Counter Attack Phase", "Post-Combat",
    "Administrative Segment", "Replacement Segment"
  };

  static final String[] playerNames = {
    "Russian", "German", "Austrian", "Central Powers"
  };

  static final int[] scenariosStart = { 0, 11, 3, 3, 3, 0, 0, 3 };

  static final int[] scenariosEnd = { 0, 15, 11, 14, 14, 14, 46, 46 };

  static final int[] scenariosFirstPlayer = { 0, 0, 0, 0, 0, 0, 0, 0};

  static final int[] scenariosSecondPlayer = { 0, 1, 2, 2, 2, 1, 3, 3};

  int[] actualPlayers = new int[] { 0, 1};

  static int[][][] RPs =new int[][][] {
    { null, null, null, { 0, 20, 20, 20, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      { 0, 4, 4, 12, 20,  0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      { 0, 9, 10, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      { 0, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40},
      { 0, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40},
    },
    { null, null, null, { 0, 0, 8, 12, 14,   0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 8, 12, 14,   0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      {0, 0, 0, 8, 8,   0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
      { 0, 0, 8, 12, 14, 16, 16, 18, 18, 18, 18, 18, 18, 18, 18},
      { 0, 0, 8, 12, 14, 16, 16, 18, 18, 18, 18, 18, 18, 18, 18}
    },
    { null, null, null, null, null, null,
      {0, 14, 14, 14, 16, 16, 16, 18, 18, 18, 18, 18, 18, 18, 18},
      {0, 14, 14, 14, 16, 16, 16, 18, 18, 18, 18, 18, 18, 18, 18}
    },
    { null, null, null, null, null,
      {12, 40, 40,40,40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40},
      {12, 40, 40,40,40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40},
      {12, 14, 40,40,40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40}
    },
    { null, null, null,
      { 0, 0, 10, 20, 30, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      { 0, 0, 10, 16, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
      null,
      { 0, 10, 20, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
      { 0, 10, 20, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30},
    }
  };

  Widget[] widgets = new Widget[] {
    new TurnWidget (),
    new VPWidget (),
    new RailWidget( "Russian Guage RPs", 0),
    new RailWidget( "Russian European Guage RPs", 1),
    new RailWidget( "Russian Warsaw RPs", 2),
    new RailWidget( "German RPs", 3),
    new RailWidget( "Austrian RPs", 4),
  };

  TiteMap curWindow = null;
  TiteMap curConditionalWindow = null;

  String getTurnString() {
    if (starting ) return "Game Setup";
    if (finishing) return "Game Finished";
    if (interphase)return "Interphase " + ( gameTurn / 3) + " " +
                phaseNames[phase];
    return turnNames[gameTurn/3] + " GT" + (gameTurn + 1) + ": " +
                ((phase == 6 || phase == 8 ) ? playerNames[actualPlayers[1 - player]]
                  : playerNames[actualPlayers[player]]) + " " +
                  phaseNames[phase];
  }
  
  void advanceTurn() {
    if (finishing) return;
    if (starting ) {
      starting = false;
      return;
    }
    if (phase != 9 && phase != 11) {
      phase++;
      return;
    }
    if ( !interphase && player == 0) {
      player = 1;
      phase = 0;
      return;
    }
    if (interphase ) {
      interphase = false;
      player = 0;
      phase = 0;
      return;
    }
    if ( gameTurn == scenariosEnd[curScenario]) {
      finishing = true;
      return;
    }
    gameTurn++;
    player = 0;
    if ( gameTurn < 45 && gameTurn % 3 == 0 && curScenario != 1 ) {
      interphase = true;
      phase = 10;
      return;
    }
    phase = 0;
    weather = ( gameTurn < 16 || gameTurn > 22 ) ? 1 : 3;
  }

  void retardTurn() {
    if (starting) return;
    if (finishing) {
      finishing = false;
      return;
    }
    if (phase != 0 && phase != 10 ) {
      phase--;
      return;
    }
    if ( !interphase && player == 1) {
      if ( curScenario == 1 && scenariosStart[1] == gameTurn ) {
        starting = true;
        return;
      }
      player = 0;
      phase = 9;
      return;
    }
    if ( curScenario != 7 ) {
      if ( gameTurn == scenariosStart[curScenario]) {
        starting = true; return;
      }
    }
    if ( !interphase && gameTurn < 45 && gameTurn % 3  == 0 && curScenario != 1) {
      interphase = true;
      phase = 11;
      player = 0;
      return;
    }
    if ( curScenario == 7 && gameTurn == scenariosStart[curScenario]) {
      starting = true;
      return;
    }
    interphase = false;
    player = 1;
    phase = 9;
    gameTurn--;
    weather = ( gameTurn < 16 || gameTurn > 22 ) ? 1 : 3;
  }

  void setStart() {
    actualPlayers[0] = scenariosFirstPlayer[curScenario];
    actualPlayers[1] = scenariosSecondPlayer[curScenario];
    starting = true;
    interphase = false;
    player = 0;
    phase = 0;
    finishing = false;
    gameTurn = scenariosStart[curScenario];
    if (curScenario == 1) player = 1;
    if (curScenario == 7) {
      interphase = true;
      phase = 10;
    }
    VPs = 0;
    weather = 1;
    updateToolbar();
  }
  
  void updateToolbar() {
    if (toolbar == null) return;
    chooseWindow();
    chooseConditionalWindow();
    widgets[0].setControls();
    widgets[1].setControls();
    widgets[2].setControls();
    widgets[3].setControls();
    widgets[4].setControls();
    widgets[5].setControls();
    widgets[6].setControls();
    if( curScenario < 2 ) {
      widgets[1].setVisible(false);
      widgets[1].setSize( 0, widgets[1].getHeight());
      widgets[2].setVisible(false);
      widgets[2].setSize( 0, widgets[2].getHeight());
      widgets[3].setVisible(false);
      widgets[3].setSize( 0, widgets[3].getHeight());
      widgets[4].setVisible(false);
      widgets[4].setSize( 0, widgets[4].getHeight());
      widgets[5].setVisible(false);
      widgets[5].setSize( 0, widgets[5].getHeight());
      widgets[6].setVisible(false);
      widgets[6].setSize( 0, widgets[6].getHeight());
    } else {
      widgets[1].setVisible(true);
      widgets[1].setSize( wSize[1], widgets[1].getHeight());
      if (RPs[0][curScenario] != null && nations[1].isCommander() ) {
        widgets[2].setVisible(true);
        widgets[2].setSize( wSize[2], widgets[2].getHeight());
      } else {
        widgets[2].setVisible(false);
        widgets[2].setSize( 0, widgets[2].getHeight());
      }
      if (RPs[1][curScenario] != null && nations[1].isCommander() ) {
        widgets[3].setVisible(true);
        widgets[3].setSize( wSize[3], widgets[3].getHeight());
      } else {
        widgets[3].setVisible(false);
        widgets[3].setSize( 0, widgets[3].getHeight());
      }
      if (RPs[2][curScenario] != null && nations[1].isCommander() ) {
        widgets[4].setVisible(true);
        widgets[4].setSize( wSize[4], widgets[4].getHeight());
      } else {
        widgets[4].setVisible(false);
        widgets[4].setSize( 0, widgets[4].getHeight());
      }
      if (RPs[3][curScenario] != null && nations[0].isCommander() ) {
        widgets[5].setVisible(true);
        widgets[5].setSize( wSize[5], widgets[5].getHeight());
      } else {
        widgets[5].setVisible(false);
        widgets[5].setSize( 0, widgets[5].getHeight());
      }
      if (RPs[4][curScenario] != null && nations[2].isCommander() ) {
        widgets[6].setVisible(true);
        widgets[6].setSize( wSize[6], widgets[6].getHeight());
      } else {
        widgets[6].setVisible(false);
        widgets[6].setSize( 0, widgets[6].getHeight());
      }
    }
    switch (weather ) {
      case 1: weatherLaunch.setText("Weather Normal"); break;
      case 2: weatherLaunch.setText("Weather Summer Heat"); break;
      case 3: weatherLaunch.setText("Weather High Water"); break;
      case 4: weatherLaunch.setText("Weather Freeze"); break;
    }
    toolbar.repaint();
  }


  protected Font getDisplayFont() {
    int style = getFontStyle();
    int size = getFontSize();
    return new Font("Dialog", style, size);
  }

  protected int getFontSize() {
    return 14;
  }

  protected int getFontStyle() {
    return 0;
  }

  abstract class Widget extends JPanel {
    private static final long serialVersionUID = 1L;

    IconButton nextButton;
    IconButton prevButton;
  
    protected final int BUTTON_SIZE = 22;

    protected JLabel label = new JLabel();
    int index;

    protected Widget( int i) {
      super();
      index = i;
      initComponents();
    }

    public void setLabelFont(Font displayFont) {
      label.setFont(displayFont);
    }

    public void setLabelWidth(int length) {
      if (length > 0) {
        label.setMinimumSize(new Dimension(length, BUTTON_SIZE));
        label.setPreferredSize(new Dimension(length, BUTTON_SIZE));
      }
      else {
        label.setMinimumSize(null);
        label.setPreferredSize(null);
      }      
    }

    public void setLabelToolTipText(String tooltip) {
      label.setToolTipText(tooltip);
    }

    public String getLabelToolTipText() {
      return label.getToolTipText();
    }
    
    public Color getColor() {
      return label.getBackground();
    }

    public int getTextWidth(String text) {
      return label.getGraphics().getFontMetrics().stringWidth(text);
    }

    abstract String getNextText();

    abstract String getPrevText();

    abstract void doNext();

    abstract void doPrev();
    
    protected void initComponents() {

      setLayout(new BorderLayout(5, 5)); 
  
      nextButton = new IconButton(IconButton.PLUS_ICON, BUTTON_SIZE);
      nextButton.setToolTipText( getNextText() );
      nextButton.setAlignmentY(Component.TOP_ALIGNMENT);
      nextButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          doNext();
          Command c = new IncDecCommand ( index, true );
          GameModule.getGameModule().sendAndLog( c );
        }});

      prevButton = new IconButton(IconButton.MINUS_ICON, BUTTON_SIZE);
      prevButton.setToolTipText( getPrevText() );
      prevButton.setAlignmentY(Component.TOP_ALIGNMENT);
      prevButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          doPrev();
          Command c = new IncDecCommand ( index, false );
          GameModule.getGameModule().sendAndLog( c );
        }});
      
      // Next, the Label containing the Turn Text
      label.setFont(getDisplayFont());
      label.setFocusable(false);
      label.setHorizontalTextPosition(JLabel.CENTER);
      label.setHorizontalAlignment(SwingConstants.CENTER);
      label.setBackground(Color.WHITE);
      label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
      
 
      add(prevButton, BorderLayout.LINE_START);
      add(label, BorderLayout.CENTER);
      add(nextButton, BorderLayout.LINE_END);
      
    }

    abstract void setControls();

  }

  class TurnWidget extends Widget {
    private static final long serialVersionUID = 1L;

    public TurnWidget() {
      super(0);
    }

    public void setControls() {
      label.setText(getTurnString());
    }

    String getNextText() {
      return "Click to advance turn";
    }

    String getPrevText() {
      return "Click to retard turn";
    }

    void doNext() {
      advanceTurn();
      updateToolbar();
    }

    void doPrev() {
      retardTurn();
      updateToolbar();
    }
  }

  String getVPString() {
    if ( VPs == 0) return "Game is drawn";
    if (VPs > 0 ) return playerNames[actualPlayers[1]] + " leads by " + VPs;
    return playerNames[actualPlayers[0]] + " leads by " + (-VPs);
  }

  class RailWidget extends Widget {
    private static final long serialVersionUID = 1L;

    String type;
    int value;
    int index;
    JButton reset;

    RailWidget (String type,  final int index ) {
      super( index+2);
      this.type = type;
      value = 0;
      this.index = index;

      reset = new JButton( "Set RPs");
      reset.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          SetRPsCommand c = new SetRPsCommand( index+2, RPs[index][curScenario][gameTurn/3], value);
          value = c.newValue;
          setControls();
          GameModule.getGameModule().sendAndLog( c );
        }
      });
    }

    void setControls() {
      if ( phase == 10) {
        remove(nextButton);
        remove(prevButton);
        add(reset, BorderLayout.LINE_END);
      } else {
        remove(reset);
        add(prevButton, BorderLayout.LINE_START);
        add(nextButton, BorderLayout.LINE_END);
      }
      label.setText(type + " = " + (value/2) + (value%2!=0? ".5":""));
    }

    void doPrev() {
      if ( value > 0 ) value--;
      setControls();
    }

    void doNext() {
      value++;
      setControls();
    }

    String getPrevText() {
      return "Decrease " + type;
    }

    String getNextText() {
      return "Increase " + type;
    }
  }

  class VPWidget extends Widget {
    private static final long serialVersionUID = 1L;

    public VPWidget() {
      super(1);
    }

    void setControls() {
      label.setText(getVPString());
    }

    String getNextText() {
      return "Add VP for " + playerNames[actualPlayers[1]];
    }

    String getPrevText() {
      return "Add VP for " + playerNames[actualPlayers[0]];
    }

    void doNext() {
      VPs++;
      setControls();
    }

    void doPrev() {
      VPs--;
      setControls();
    }
  }

  void chooseWindow() {
    List<TiteMap> reinforcements= this.getComponentsOf(TiteMap.class);
    for (TiteMap m : reinforcements ) {
      if (m.gameTurn == gameTurn ) {
        for ( int i = 0; i < m.scenarios.length; i++ ) {
          if (m.scenarios[i] == curScenario ) {
            if ( curWindow == m ) return;
            if ( curWindow != null) {
              ((TiteMap.View)curWindow.getView()).disableListeners();
              ((RootPaneContainer) window).getContentPane().remove(curWindow.getLayeredPane());
            }
            curWindow = m;
            ((RootPaneContainer) window).getContentPane().add(m.getLayeredPane()); //$NON-NLS-1$
            window.validate();
            ((TiteMap.View)curWindow.getView()).enableListeners();
            window.setTitle(m.getMapName());
            windowLaunch.setEnabled(true);
            return;
          }
        }
      }
    }
    if ( curWindow != null) {
      ((TiteMap.View)curWindow.getView()).disableListeners();
      ((RootPaneContainer) window).getContentPane().remove(curWindow.getLayeredPane());
      curWindow = null;
      window.setVisible(false);
      windowLaunch.setEnabled(false);
    }
  }

  void chooseConditionalWindow() {
    List<TiteMap> reinforcements= this.getComponentsOf(TiteMap.class);
    for (TiteMap m : reinforcements ) {
      if (m.gameTurn == -1 ) {
        for ( int i = 0; i < m.scenarios.length; i++ ) {
          if (m.scenarios[i] == curScenario ) {
            if ( curConditionalWindow == m ) return;
            if ( curConditionalWindow != null) {
              ((TiteMap.View)curConditionalWindow.getView()).disableListeners();
              ((RootPaneContainer) conditionalWindow).getContentPane().remove(conditionalWindow.getLayeredPane());
            }
            curConditionalWindow = m;
            ((RootPaneContainer) conditionalWindow).getContentPane().add(m.getLayeredPane()); //$NON-NLS-1$
            conditionalWindow.setTitle(m.getMapName());
            ((TiteMap.View)curConditionalWindow.getView()).enableListeners();
            conditionalWindowLaunch.setEnabled(true);
            return;
          }
        }
      }
    }
    if ( curConditionalWindow != null) {
      ((TiteMap.View)curConditionalWindow.getView()).disableListeners();
      ((RootPaneContainer) conditionalWindow).getContentPane().remove(curConditionalWindow.getLayeredPane());
      curConditionalWindow = null;
      conditionalWindow.setVisible(false);
      conditionalWindowLaunch.setEnabled(false);
      }
  }

  
  public static final String CURRENT_SCENARIO = "currentScenario";
  public static final String SAVING_SCENARIO = "savingScenario";
    
   /**
   * Return an array of Strings describing the attributes of this object. These strings are used as prompts in the
   * Properties window for this object. The order of descriptions should be the same as the order of names in
   * {@link AbstractBuildable#getAttributeNames}
   */
  public String[] getAttributeDescriptions() {
    String [] s = { 
      "Current scenario: ",
      "Saving Scenario?: "
   };
     return s;
  }

  /**
   * Return the Class for the attributes of this object. Valid classes are: String, Integer, Double, Boolean, Image,
   * Color, and KeyStroke
   * 
   * The order of classes should be the same as the order of names in {@link AbstractBuildable#getAttributeNames}
   */
  public Class<?>[] getAttributeTypes() {
    Class<?>[] c =  {
      Integer.class,
      Boolean.class
    };
    return c;
  }

  public String getAttributeValueString(String key) {
    if (CURRENT_SCENARIO.equals(key)) {
      return String.valueOf(curScenario);
    }
    if ( SAVING_SCENARIO.equals(key)) {
      return String.valueOf(scenarioFlag);
    }
    return null; 
  }
 
  public void setAttribute(String key, Object value) {
    if ( SAVING_SCENARIO.equals(key)) {
      if ( value.getClass() == String.class ) {
        value = Boolean.valueOf((String) value);
      }
      if ( value instanceof Boolean ) {
        scenarioFlag = (Boolean) value;
      }
      return;
    }
    if ( CURRENT_SCENARIO.equals(key)) {
      if ( value.getClass() == String.class ) {
        value = Integer.valueOf((String) value);
      }
      if ( value instanceof Integer ) {
        curScenario = (Integer) value;
        setStart();
      }
      return;
    }
    return;
  }

  public String[] getAttributeNames() {
    String[] s = {
      CURRENT_SCENARIO,
      SAVING_SCENARIO
    };
    return s;
  }
  
  public Class<?>[] getAllowableConfigureComponents() {
    return new Class<?>[] {
      TiteMap.class
    };
  }

  public class DuplicateTite extends Throwable {
     private static final long serialVersionUID = 1L; 
  }
  
  protected JToolBar toolbar;
  protected JMenu menu;
  protected JButton commandLaunch;
  protected JButton attachLaunch;
  protected JButton attachmentLaunch;
  protected JButton windowLaunch;
  protected JButton conditionalWindowLaunch;
  protected JDialog window;
  protected JDialog conditionalWindow;
  protected JButton weatherLaunch;
  
  protected static Tite theTite = null;

  public static Tite getTite() {
    return theTite;
  }
                                            
  public Tite() throws DuplicateTite {
    
    if ( theTite != null ) {
      throw new DuplicateTite();
    }
    theTite = this;
    
    initialiseNations();
    
    window = new JDialog(GameModule.getGameModule().getPlayerWindow());

    window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    window.setTitle("Reinforcements");
    window.setSize(510, 500);

    window.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        window.setVisible(false);
      }
    });

    conditionalWindow = new JDialog(GameModule.getGameModule().getPlayerWindow());

    conditionalWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    conditionalWindow.setTitle("Reinforcements");
    conditionalWindow.setSize( 500, 710);

    conditionalWindow.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        conditionalWindow.setVisible(false);
      }
    });

    commandLaunch = new JButton( "Commanders");
    commandLaunch.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        launch();
      }
    });
    commandLaunch.setFocusable( false );
    commandLaunch.setToolTipText("Take or resign command");
    
    attachLaunch = new JButton( "Attach Corps" );
    attachLaunch .addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        launchAttach();
      }
    });
    attachLaunch.setFocusable( false );
    attachLaunch.setToolTipText("Attach Corps to Army");
    
    attachmentLaunch = new JButton( "Show Attached Units" );
    attachmentLaunch.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        launchAttachment();
      }
    });

    windowLaunch = new JButton( "Setup/Reinforcements");
    windowLaunch.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openWindow();
      }
    });
    windowLaunch.setEnabled(false);

    conditionalWindowLaunch = new JButton("Conditional Reinforcements");
    conditionalWindowLaunch.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openConditionalWindow();
      }
    });
    conditionalWindowLaunch.setEnabled(false);
    
    weatherLaunch = new JButton("Weather Normal");
    weatherLaunch.addActionListener( new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        changeWeather();
      }
    });

    menu = new JMenu();

    GameModule.getGameModule().getGameState().addGameComponent(this);

  }
  
  public void changeWeather() {
    JMenuItem mi;
    menu.removeAll();
    if ( gameTurn < 16 || gameTurn > 22 ) {
      mi = new JMenuItem("Normal Weather");
      mi.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e ) {
          Command c = new WeatherCommand(1, weather);
          GameModule.getGameModule().sendAndLog(c);
          weather = 1;
          updateToolbar();
        }
      });
      menu.add(mi);
    }
    if ( gameTurn < 10 ) {
      mi = new JMenuItem("Summer Heat Weather");
      mi.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e ) {
          Command c = new WeatherCommand(2, weather);
          GameModule.getGameModule().sendAndLog(c);
          weather = 2;
          updateToolbar();
        }
      });
      menu.add(mi);
    }
    if ( gameTurn > 15 && gameTurn < 23 ) {
      mi = new JMenuItem("High Water Weather");
      mi.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e ) {
          Command c = new WeatherCommand(3, weather);
          GameModule.getGameModule().sendAndLog(c);
          weather = 3;
          updateToolbar();
        }
      });
      menu.add(mi);
    }
    if ( gameTurn > 32 ) {
      mi = new JMenuItem("Freeze Weather");
      mi.addActionListener( new ActionListener() {
        public void actionPerformed(ActionEvent e ) {
          Command c = new WeatherCommand(4, weather);
          GameModule.getGameModule().sendAndLog(c);
          weather = 4;
          updateToolbar();
        }
      });
      menu.add(mi);
    }
    menu.getPopupMenu().show( weatherLaunch, 0, weatherLaunch.getHeight() );
  }
    
  public void launch() {
      LookAndFeel f = null;
          try {
              f=UIManager.getLookAndFeel();
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {

    }
    

    buildCommandMenu();
    menu.getPopupMenu().show( commandLaunch, 0, commandLaunch.getHeight() );
    try {
    UIManager.setLookAndFeel(f);
    } catch (Exception e) {}
  }
  
  public void launchAttach() {
      LookAndFeel f = null;
          try {
              f=UIManager.getLookAndFeel();
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {

    }
    

    buildAttachMenu();
    menu.getPopupMenu().show( attachLaunch, 0, attachLaunch.getHeight() );
    try {
    UIManager.setLookAndFeel(f);
    } catch (Exception e) {}
  }
  
  public void launchAttachment() {
      LookAndFeel f = null;
          try {
              f=UIManager.getLookAndFeel();
    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (Exception e) {

    }
    

    buildAttachmentMenu();
    menu.getPopupMenu().show(attachmentLaunch, 0, attachmentLaunch.getHeight());
    try {
    UIManager.setLookAndFeel(f);
    } catch (Exception e) {}
  }

  public void openWindow() {
    window.setVisible(!window.isShowing());
  }

  public void openConditionalWindow() {
    conditionalWindow.setVisible(!conditionalWindow.isShowing());
  }

  public void buildAttachMenu() {
    String user = GameModule.getUserId();
    menu.removeAll();
    for ( int i = 0; i < nations.length; i++ ) {
      nations[i].prepareToAttach();
    }
    for ( int i = 0; i < nations.length; i++ ) {
      nations[i].countAttachments();
    }
    for ( int i = 0; i < nations.length; i++ ) {
      nations[i].addToAttachMenu( user );
    }
    if ( curScenario == 2 || curScenario == 3 ) {
      LW3Corps.addToAttachMenu( user );
    }
    menu = sizeMenu ( menu);
  }
  
  public void buildCommandMenu() {
    menu.removeAll();
    for ( int i = 0; i < nations.length; i++ ) {
      nations[i].addToCommandMenu();
    }
    menu = sizeMenu ( menu);
  }
  
  public void processPiece ( GamePiece q ) {
    TiteTraitBase t = (TiteTraitBase) Decorator.getDecorator(q, TiteTraitBase.class );
    if ( t == null ) return;
    if ( t.attachable() ) {
      int n = t.getNation();
      if ( n < 0 ) return;
      int c = t.getCorps();
      if ( c < 0 ) {
        int r = t.getArmy();
        if ( r >= 0 ) nations[n].armies[r].noAttached++;
      } else {
        nations[n].corps[c].noAttached++;
      }
    }
  }
  
  public void processPiece2 ( GamePiece q, String mapName ) {
    TiteTraitBase t = (TiteTraitBase) Decorator.getDecorator(q, TiteTraitBase.class );
    if ( t == null ) return;
    if ( t.attachable() ) {
      int n = t.getNation();
      if ( n < 0 ) return;
      int c = t.getCorps();
      if ( c < 0 ) {
        int r = t.getArmy();
        if ( r >= 0 ) {
          nations[n].armies[r].attachedNames[nations[n].armies[r].noAttached] =
                  t.getName() + "    (" + ( mapName != null ? mapName 
                  : q.getMap().localizedLocationName(q.getPosition())) +")";
          nations[n].armies[r].attachedCEL[nations[n].armies[r].noAttached] = t.getCEL();
          nations[n].armies[r].noAttached++;
        }
      } else {
        nations[n].corps[c].attachedNames[nations[n].corps[c].noAttached] =
                t.getName() + "    (" + ( mapName != null ? mapName 
                : q.getMap().localizedLocationName(q.getPosition())) +")";
        nations[n].corps[c].attachedCEL[nations[n].corps[c].noAttached] = t.getCEL();
        nations[n].corps[c].noAttached++;
      }
    }
  }
  
  public void buildAttachmentMenu() {
    menu.removeAll();
    for ( int j = 0; j < nations.length; j++ ) {
      for ( int i = 0 ; i < nations[j].armies.length; i++ ) {
        nations[j].armies[i].noAttached = 0;
      }
      for ( int i = 0 ; i < nations[j].corps.length; i++ ) {
        nations[j].corps[i].noAttached = 0;
      }
    }
    List <Map> a = GameModule.getGameModule().getAllDescendantComponentsOf(Map.class);
    for (Map mx : a) {
      if ( mx instanceof TiteMap ) {
        if ( !((TiteMap)mx).inScenario(curScenario)) continue;
      }
      GamePiece[] pieces = mx.getAllPieces();
      for ( int i = 0; i < pieces.length; i++ ) {
        GamePiece q = pieces[i];
        if (q.getClass() == Stack.class ) {
          Stack sub = (Stack)q;
          for (Iterator<GamePiece> e = sub.getPiecesIterator();
                  e.hasNext();) {
            GamePiece p = e.next();
            processPiece( p );
          }
        } else {
          processPiece( q );
        }
      }
    }
    for ( int j = 0; j < nations.length; j++ ) {
      for ( int i = 0 ; i < nations[j].armies.length; i++ ) {
        nations[j].armies[i].attachedNames = new String [ nations[j].armies[i].noAttached ];
        nations[j].armies[i].attachedCEL = new int [ nations[j].armies[i].noAttached ];
        nations[j].armies[i].noAttached = 0;
      }
      for ( int i = 0 ; i < nations[j].corps.length; i++ ) {
        nations[j].corps[i].attachedNames = new String [ nations[j].corps[i].noAttached ];
        nations[j].corps[i].attachedCEL = new int [ nations[j].corps[i].noAttached ];
        nations[j].corps[i].noAttached = 0;
      }
    }
    for (Map mx : a) {
      if ( mx instanceof TiteMap ) {
        if ( !((TiteMap)mx).inScenario(curScenario)) continue;
      }
      GamePiece[] pieces = mx.getAllPieces();
      for ( int i = 0; i < pieces.length; i++ ) {
        GamePiece q = pieces[i];
        if (q.getClass() == Stack.class ) {
          Stack sub = (Stack)q;
          for (Iterator<GamePiece> e = sub.getPiecesIterator();
                  e.hasNext();) {
            GamePiece p = e.next();
            processPiece2( p, mx instanceof TiteMap ? mx.getMapName() : null );
          }
        } else {
          processPiece2( q, mx instanceof TiteMap ? mx.getMapName() : null );
        }
      }
    }
    for ( int j = 0; j < nations.length; j++ ) {
      nations[j].addToAttachmentMenu();
    }
    menu = sizeMenu ( menu);
  }
  
  public JMenu getAttachMenu ( int nation, int size, GamePiece p, boolean armyOnly ) {
    if ( nation < 0 ) return null;
    return sizeMenu(nations[nation].buildUnitAttachMenu( size, p, armyOnly ));
  }
  
  public JMenu sizeMenu( JMenu initialMenu ) {
    int size = initialMenu.getItemCount();
    if ( size < 21 ) return initialMenu;
    JMenu m = new JMenu( initialMenu.getText() );
    int k = 0;
    JMenu s = new JMenu( "Part 1");
    for ( int j = 0; j < size; j++) {
      String t = initialMenu.getItem(0).getText();
      s.add(initialMenu.getItem(0) );
      k++;
      if ( k == 20 ) {
        m.add(s);
        s = new JMenu( "Part " + (m.getItemCount() + 1));
        k = 0;
      }
    }
    if ( k != 0 ) m.add(s);
    return m;
  }
  
  public boolean isRestricted ( int nation, int army, int corps ) {
    if ( nation < 0 ) return false;
    if ( corps < 0 && army < 0 ) {
      return nations[nation].testDetached();
    }
    if ( army < 0 ) army = nations[nation].corps[corps].attachedTo;
    if ( army < 0 ) return nations[nation].testDetached();
    if ( nation == 0 && corps == 21) nation = LW3Corps.nation.no;
    return nations[nation].testAttached( army );
  }
  
  public boolean isVeryrestricted( int nation, int army, int corps ) {
    if ( nation < 0 ) return false;
    if ( corps < 0 && army < 0 ) {
      return nations[nation].testArmyOnly();
    }
    if ( army < 0 ) army = nations[nation].corps[corps].attachedTo;
    if ( army < 0 ) return nations[nation].testArmyOnly();
    if ( nation == 0 && corps == 21) nation = LW3Corps.nation.no;
    return nations[nation].testAttached( army );
  }

  public int getArmyForCorps( int nation, int corps ) {
    return nations[nation].corps[corps].attachedTo;
  }
  
  public String attachmentString( int nation, int army, int corps ) {
    if ( nation < 0 ) return "";
    if ( army < 0 && corps < 0 ) return "-/-";
    if ( corps < 0) return  "-/" + nations[nation].armies[army].id;
    army = nations[nation].corps[corps].attachedTo;
    if ( army < 0 ) return nations[nation].corps[corps].id + "/-";
    int nationa = nation;
    if ( nation == 0 && corps == 21 ) nationa = LW3Corps.nation.no;
    return nations[nation].corps[corps].id + "/" + nations[nationa].armies[army].id;
  }
  
  public String attachmentString( int nation, int army ) {
    if ( nation < 0 ) return "";
    if ( army < 0  ) return "";
    return nations[nation].armies[army].id;
  }
  
  public void addTo ( Buildable parent ) {
    if ( parent instanceof ToolBarComponent ) {
      toolbar = ((ToolBarComponent) parent).getToolBar();
      toolbar.add(commandLaunch);
      toolbar.add(attachLaunch);
      toolbar.add(attachmentLaunch);
      toolbar.add(weatherLaunch);
      toolbar.add(widgets[0]);
      toolbar.add(widgets[1]);
      toolbar.add(windowLaunch);
      toolbar.add(conditionalWindowLaunch);
      toolbar.add(widgets[2]);
      toolbar.add(widgets[3]);
      toolbar.add(widgets[4]);
      toolbar.add(widgets[5]);
      toolbar.add(widgets[6]);
      widgets[0].setLabelWidth(widgets[0].getTextWidth(turnNames[2] + " grtxxxxx    " + phaseNames[6]
                + playerNames[3]));
      widgets[1].setLabelWidth(widgets[1].getTextWidth("Central Powers lead by 9999999"));
      widgets[2].setLabelWidth(widgets[2].getTextWidth("Russian Guage RPs = 99999"));
      widgets[3].setLabelWidth(widgets[3].getTextWidth("Russian European Guage RPs = 99999"));
      widgets[4].setLabelWidth(widgets[4].getTextWidth("Russian Warsaw RPs = 99999"));
      widgets[5].setLabelWidth(widgets[5].getTextWidth("German RPs = 99999"));
      widgets[6].setLabelWidth(widgets[6].getTextWidth("Austrian RPs = 99999"));
      
      wSize[1] = widgets[1].getWidth();
      wSize[2] = widgets[2].getWidth();
      wSize[3] = widgets[3].getWidth();
      wSize[4] = widgets[4].getWidth();
      wSize[5] = widgets[5].getWidth();
      wSize[6] = widgets[6].getWidth();
    }
    GameModule.getGameModule().addCommandEncoder(this);

    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new BooleanConfigurer(DISPLAY_MARKERS, "TITE - display markers: ", Boolean.TRUE));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new BooleanConfigurer(MERGE_HITS, "TITE - merge hits into counters: ", Boolean.TRUE));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new BooleanConfigurer(OTHERS_SEE_HITS, "TITE - non-owners can see hits (Scenario Load Only): ", Boolean.FALSE));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new BooleanConfigurer(OTHERS_SEE_STATUS, "TITE - non-owners can see status (Scenario Load Only): ", Boolean.FALSE));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new BooleanConfigurer(CONCEALED, "TITE - conceal units (Scenario Load Only): ", Boolean.TRUE));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new BooleanConfigurer(HIDDEN, "TITE - hidden units (Scenario Load Only): ", Boolean.TRUE));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new IntConfigurer(HIDE_RANGE, "TITE - range to hide units in hexes (Scenario Load Only): ", 6 ));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new IntConfigurer(STACK_FLATTEN_RANGE, "TITE - range to flatten stack in hexes (Scenario Load Only): ", 4 ));
    GameModule.getGameModule().getPrefs().addOption(Resources.getString("Prefs.general_tab"),
        new IntConfigurer(CONCEAL_RANGE, "TITE - range to conceal units in hexes (Scenario Load Only): ", 2 ));

    setStart();

  }
  
  public GamePiece getNationView( int n ) {
    return nations[n].view;
  }
  
  public GamePiece getArmyView( int n, int a ) {
    return nations[n].armies[a].view;
  }
  
  public GamePiece getCorpsView( int n, int c ) {
    return nations[n].corps[c].view;
  }
  
  public GamePiece getArmyViewFromCorps( int n, int c) {
    int a = nations[n].corps[c].attachedTo;
    if ( a < 0 ) return null;
    if ( n == 0 && c == 21 ) n = LW3Corps.nation.no;
    return nations[n].armies[a].view;
  }
  
  public final static String DISPLAY_MARKERS = "displayMarkers";
  
  public final static String MERGE_HITS = "mergeHits";
  
  public final static String OTHERS_SEE_HITS = "othersSeeHits";
  
  public final static String OTHERS_SEE_STATUS = "othersSeeStatus";
  
  public final static String CONCEALED = "autoconceal";
  
  public final static String HIDDEN = "autohide";
  
  public final static String HIDE_RANGE = "hideRange";
  
  public final static String STACK_FLATTEN_RANGE = "stackFlattenRange";
  
  public final static String CONCEAL_RANGE = "concealRange";
  
  private boolean scenarioFlag = false;
  
  private boolean seeHits = false;
  
  private boolean conceal = true;
  
  private boolean hide = true;
  
  private boolean seeStatus = false;
  
  private int hideRange = 520;
  
  private int flattenRange = 360;
  
  private int concealRange = 200;
  
  public boolean displayMarkers() {
    return Boolean.TRUE.equals(GameModule.getGameModule().getPrefs().getValue(DISPLAY_MARKERS));
  }
  
  public boolean othersSeeStatus() {
    return seeStatus;
  }
    
  public boolean mergeHits() {
    return Boolean.TRUE.equals(GameModule.getGameModule().getPrefs().getValue(MERGE_HITS));
  }
    
  public boolean othersSeeHits() {
    return seeHits;
  }
  
  public boolean conceal() {
    return conceal;
  }
  
  public boolean hide() {
    return hide;
  }
  
  public int hideRange() {
    return hideRange;
  }
  
  public int flattenRange() {
    return flattenRange;
  }
  
  public int concealRange() {
    return concealRange;
  }
    
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("Tite.htm");
  }
  
  public void removeFrom(Buildable parent) {
    toolbar.remove(commandLaunch);
    toolbar.remove(attachLaunch);
    toolbar.remove(attachmentLaunch);
    toolbar.remove(weatherLaunch);
    toolbar.remove(widgets[0]);
    toolbar.remove(widgets[1]);
    toolbar.remove(windowLaunch);
    toolbar.remove(conditionalWindowLaunch);
    toolbar.remove(widgets[2]);
    toolbar.remove(widgets[3]);
    toolbar.remove(widgets[4]);
    toolbar.remove(widgets[5]);
    toolbar.remove(widgets[6]);
    if ( curWindow != null) {
      ((TiteMap.View)curWindow.getView()).disableListeners();
      ((RootPaneContainer) window).getContentPane().remove(curWindow.getLayeredPane());
    }
    if ( curConditionalWindow != null) {
      ((TiteMap.View)curConditionalWindow.getView()).disableListeners();
      ((RootPaneContainer) conditionalWindow).getContentPane().remove(conditionalWindow.getLayeredPane());
    }
  }
  
  public Command getRestoreCommand() {
    return new TiteCommand();
  }
  
  public void setup(boolean gameStarting) {
    if ( !gameStarting ) {
      curScenario = 1;
      for ( int i = 0; i < nations.length; i++ ) {
        nations[i].commander = "";
        nations[i].commanderName = "";
        for ( int j = 0; j < nations[i].armies.length; j++ ) {
          nations[i].armies[j].commander = "";
          nations[i].armies[j].commanderName = "";
        }
        for ( int j = 0; j < nations[i].corps.length; j++ ) {
          nations[i].corps[j].attachedTo = -1;
        }
      }
    } else {
      updateToolbar();
    }
  }
  
  public class TiteCommand extends Command {
    int scenario;
    String[] commanders;
    String[] commanderNames;
    int[] attaches;
    boolean sf;
    boolean osh;
    boolean c;
    boolean h;
    int hr;
    int fr;
    int cr;
    int gameTurnx;
    int playerx;
    int phasex;
    int lw3attach;
    boolean interphasex;
    boolean startingx;
    boolean finishingx;
    boolean oss;

    int VPsx;
    int[] RPs;
    int w;
    
    TiteCommand( SequenceEncoder.Decoder st) {
      scenario = st.nextInt(0);
      sf = st.nextBoolean( false );
      osh = st.nextBoolean(false);
      c = st.nextBoolean(true);
      h = st.nextBoolean(true);
      hr = st.nextInt(520);
      fr = st.nextInt(360);
      cr = st.nextInt(200);
      gameTurnx = st.nextInt(11);
      playerx=st.nextInt(1);
      phasex =st.nextInt(0);
      interphasex=st.nextBoolean(false);
      startingx=st.nextBoolean(true);
      finishingx =st.nextBoolean(false);
      VPsx=st.nextInt(0);
      RPs = stringToIntArray(st.nextToken());
      w = st.nextInt(1);
      commanders = StringArrayConfigurer.stringToArray(st.nextToken());
      commanderNames = StringArrayConfigurer.stringToArray(st.nextToken());
      attaches = stringToIntArray(st.nextToken());
      if ( scenario == 2 || scenario == 3 ) {
        lw3attach = st.nextInt(-1);
      }
      oss = st.nextBoolean( false );
    }
    
    TiteCommand() {
      scenario = curScenario;
      sf = scenarioFlag;
      osh = seeHits;
      oss = seeStatus;
      c = conceal;
      h = hide;
      hr = hideRange;
      fr = flattenRange;
      cr = concealRange;
      gameTurnx = gameTurn;
      playerx = player;
      phasex = phase;
      interphasex = interphase;
      startingx = starting;
      finishingx = finishing;
      VPsx = VPs;
      RPs = new int[5];
      w = weather;
      RPs[0] = ((RailWidget)widgets[2]).value;
      RPs[1] = ((RailWidget)widgets[3]).value;
      RPs[2] = ((RailWidget)widgets[4]).value;
      RPs[3] = ((RailWidget)widgets[5]).value;
      RPs[4] = ((RailWidget)widgets[6]).value;
      int k = 0;
      for ( int i = 0; i < nations.length; i++ ) {
        if ( nations[i].inScenario(scenario)) {
          for ( int j = 0; j < nations[i].armies.length; j++ )
            if ( nations[i].armies[j].inScenario(scenario)) k++;
          k++;
        }
      }
      commanders = new String[k];
      commanderNames = new String[k];
      k = 0;
      for ( int i = 0; i < nations.length; i++ ) {
        if ( nations[i].inScenario(scenario)) {
          for ( int j = 0; j < nations[i].armies.length; j++ ) {
            if ( nations[i].armies[j].inScenario(scenario)) {
              commanders[k] = nations[i].armies[j].commander;
              commanderNames[k] = nations[i].armies[j].commanderName;
              k++;
            }
          }
          commanders[k] = nations[i].commander;
          commanderNames[k] = nations[i].commanderName;
          k++;
        }
      }
      k = 0;
      for ( int i = 0; i < nations.length; i++ ) {
        if ( nations[i].inScenario(scenario)) {
           for ( int j = 0; j < nations[i].corps.length; j++ ) {
            if ( nations[i].corps[j].inScenario(scenario)) k++;
           }
        }
      }
      attaches = new int[k];
      k = 0;
      for ( int i = 0; i < nations.length; i++ ) {
        if ( nations[i].inScenario(scenario)) {
           for ( int j = 0; j < nations[i].corps.length; j++ ) {
            if ( nations[i].corps[j].inScenario(scenario))
              if ( nations[i].corps[j] == LW3Corps && LW3Corps.nation != nations[0] ) {
                attaches[k++] = nations[i].corps[j].attachedTo + 1000;
              } else {
                attaches[k++] = nations[i].corps[j].attachedTo;
              }
           }
        }
      }
      if ( scenario == 2 || scenario == 3 ) {
        lw3attach = LW3Corps.attachedTo;
        if ( LW3Corps.nation != nations[0] ) lw3attach += 1000;
      }
    }
    
    public Command myUndoCommand() {
      return null;
    }
    
    public void executeCommand() {
      curScenario = scenario;
      int k = 0;
      for ( int i = 0; i < nations.length; i++ ) {
        if ( nations[i].inScenario(scenario)) {
          for ( int j = 0; j < nations[i].armies.length; j++ ) {
            if ( nations[i].armies[j].inScenario(scenario)) {
              nations[i].armies[j].commander = commanders[k];
              nations[i].armies[j].commanderName = commanderNames[k];
              k++;
            }
          }
          nations[i].commander = commanders[k];
          nations[i].commanderName = commanderNames[k];
          k++;
        }
      }
      k = 0;
      for ( int i = 0; i < nations.length; i++ ) {
        if ( nations[i].inScenario(scenario)) {
           for ( int j = 0; j < nations[i].corps.length; j++ ) {
             if ( nations[i].corps[j].inScenario(scenario)) {
               if ( attaches[k] > 999 ) {
                 nations[i].corps[j].attachedTo = attaches[k++] - 1000;
                 LW3Corps.nation = nations[2];
               } else {
                 nations[i].corps[j].attachedTo = attaches[k++];
               }
             }
           }
        }
      }
      if ( scenario == 2 || scenario == 3 ) {
        if ( lw3attach > 999 ) {
          LW3Corps.nation = nations[2];
          LW3Corps.attachedTo = lw3attach - 1000;
        } else {
          LW3Corps.nation = nations[0];
          LW3Corps.attachedTo = lw3attach;
        }
      }
      if ( sf ) {
        int a;
        seeHits = Boolean.TRUE.equals(GameModule.getGameModule().getPrefs().getValue(OTHERS_SEE_HITS));
        seeStatus = Boolean.TRUE.equals(GameModule.getGameModule().getPrefs().getValue(OTHERS_SEE_STATUS));
        conceal = Boolean.TRUE.equals(GameModule.getGameModule().getPrefs().getValue(CONCEALED));
        hide = Boolean.TRUE.equals(GameModule.getGameModule().getPrefs().getValue(HIDDEN));
        Object o = GameModule.getGameModule().getPrefs().getValue(HIDE_RANGE);
        if ( o instanceof Integer) {
          a = ((Integer)o).intValue();
          if ( a < 2 ) a = 2;
          if ( a > 15 ) a = 15;
          hideRange = a * 80 + 40;
        }
        o = GameModule.getGameModule().getPrefs().getValue(STACK_FLATTEN_RANGE);
        if ( o instanceof Integer) {
          a = ((Integer)o).intValue();
          if ( a < 2 ) a = 2;
          if ( a > 15 ) a = 15;
          flattenRange = a * 80 + 40;
        }
        o = GameModule.getGameModule().getPrefs().getValue(CONCEAL_RANGE);
        if ( o instanceof Integer) {
          a = ((Integer)o).intValue();
          if ( a < 2 ) a = 2;
          if ( a > 15 ) a = 15;
          concealRange = a * 80 + 40;
        }
      } else {
        seeHits = osh;
        conceal = c;
        hide = h;
        hideRange = hr;
        flattenRange = fr;
        concealRange = cr;
        seeStatus = oss;
      }
      scenarioFlag = false;
      gameTurn = gameTurnx;
      player=playerx;
      phase=phasex;
      interphase=interphasex;
      starting = startingx;
      finishing=finishingx;
      VPs=VPsx;
      weather = w;
      ((RailWidget)widgets[2]).value=RPs[0];
      ((RailWidget)widgets[3]).value=RPs[1];
      ((RailWidget)widgets[4]).value=RPs[2];
      ((RailWidget)widgets[5]).value=RPs[3];
      ((RailWidget)widgets[6]).value=RPs[4];
      chooseWindow();
      chooseConditionalWindow();
      updateToolbar();
    }
  }
  
  public class TakeCommand extends Command {
    private int nation;
    private int army;
    private String newCommander;
    private String newCommanderName;
    private Command undo;

    TakeCommand ( int n, int a, String c, String d) {
      nation = n;
      army = a;
      newCommander = c;
      newCommanderName = d;
    }

    public Command myUndoCommand() {
      if ( undo == null ) {
        undo = new ResignCommand( nation, army, newCommander, newCommanderName );
      }
      return undo;
    }

    public void executeCommand() {
      nations[nation].takeArmyCommand(army, newCommander, newCommanderName );
    }
  }

  public class ResignCommand extends Command {
    private int nation;
    private int army;
    private String oldCommander;
    private String oldCommanderName;
    private Command undo;

    ResignCommand ( int n, int a, String c, String d) {
      nation = n;
      army = a;
      oldCommander = c;
      oldCommanderName = d;
    }

    public Command myUndoCommand() {
      if ( undo == null ) {
        undo = new TakeCommand( nation, army, oldCommander, oldCommanderName );
      }
      return undo;
    }

    public void executeCommand() {
      nations[nation].resignArmyCommand(army);
    }
  }
  
  public class AttachCorpsCommand extends Command {
    private int nation;
    private int corps;
    private int newArmy;
    private int oldArmy;
    private Command undo;
    
    AttachCorpsCommand( int n, int c, int na, int oa ) {
      nation = n;
      corps = c;
      newArmy = na;
      oldArmy = oa;
    }
    
    public Command myUndoCommand() {
      if ( undo == null ) {
        undo = new AttachCorpsCommand( nation, corps, oldArmy, newArmy );
      }
      return undo;
    }
    
    public void executeCommand() {
      if ( nation == 2 && corps == 21 )
        Tite.LW3Corps.doAttachCorps( newArmy );
      else
        nations[nation].corps[corps].doAttachCorps(newArmy);
    }
  }
  
  public class AttachUnitCommand extends Command {
    private int nation;
    private int army;
    private int corps;
    private int oldArmy;
    private int oldCorps;
    private String targetId;
    private Command undo;
    
    AttachUnitCommand ( int n, int a, int c, int oa, int oc, String t ) {
      nation = n;
      army = a;
      corps = c;
      oldArmy = oa;
      oldCorps = oc;
      targetId = t;
    }
    
    public Command myUndoCommand() {
      if ( undo == null ) {
        undo = new AttachUnitCommand ( nation, oldArmy, oldCorps, army,
                corps, targetId );
      }
      return undo;
    }
    
    public void executeCommand() {
      GamePiece p =GameModule.getGameModule().getGameState().getPieceForId(targetId);
      if ( army >= 0 ) {
        nations[nation].armies[army].doUnitAttach( p);
      }
      if ( corps >= 0 ) {
        nations[nation].corps[corps].doUnitAttach( p );
      }
    }
  }
  
  public class StrokeCommand extends Command {
    private KeyStroke doit;
    private KeyStroke undoit;
    private String targetId;
    
    StrokeCommand ( KeyStroke k, String t, KeyStroke r ) {
      doit = k;
      undoit = r;
      targetId = t;
    }
    
    public Command myUndoCommand() {
      if ( undoit == null ) return null;
      return new StrokeCommand( undoit, targetId, doit );
    }
    
    public void executeCommand( ) {
      GamePiece p =GameModule.getGameModule().getGameState().getPieceForId(targetId);
      p.keyEvent(doit);
    }
    
  }
  
  public StrokeCommand getStrokeCommand( KeyStroke k, String t, KeyStroke r) {
    return new StrokeCommand( k, t, r );
  }
  
  public class IncDecCommand extends Command {
    private int index;
    private boolean increment;
    
    IncDecCommand( int i, boolean b) {
      index = i;
      increment = b;
    }
    
    public Command myUndoCommand() {
      return new IncDecCommand (index, !increment);
    }
    
    public void executeCommand() {
      if (increment) widgets[index].doNext();
      else widgets[index].doPrev();
      updateToolbar();
    }
  }

  public class SetRPsCommand extends Command {
    private int oldValue;
    private int newValue;
    private int index;

    SetRPsCommand( int i, int n, int o) {
      index = i;
      newValue = n;
      oldValue = o;
    }

    public Command myUndoCommand() {
      return new SetRPsCommand( index, oldValue, newValue);
    }

    public void executeCommand() {
      ((RailWidget)widgets[index]).value = newValue;
      updateToolbar();
    }
  }
  
  public class WeatherCommand extends Command {
    private int oldValue;
    private int newValue;
    
    WeatherCommand(int n, int o) {
      oldValue = o;
      newValue = n;
    }
    
    public Command myUndoCommand() {
      return new WeatherCommand( oldValue, newValue );
    }
    
    public void executeCommand() {
      weather = newValue;
      updateToolbar();
    }
  }

  static String intArrayToString(int [] n ) {
    if ( n == null || n.length == 0 ) {
      return "";
    }
    SequenceEncoder se = new SequenceEncoder(',');
    for (int i = 0; i < n.length; ++i) {
      se.append(n[i]);
    }
    return se.getValue();
  }
  
  static int[] stringToIntArray( String s ) {
    if (s == null
        || s.length() == 0) {
      return new int[0];
    }
    SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(s, ',');
    ArrayList<Integer> l = new ArrayList<Integer>();
    while (st.hasMoreTokens()) {
      l.add(st.nextInt(0));
    }
    int [] n = new int[l.size()];
    for (int i = 0; i < l.size(); i++ ) {
      n[i] = l.get(i);
    }
    return n;
  }
         
  static public final String TITE_SAVE = "TITE-save\t";
  static public final String ARMY_RESIGN = "TITE-army-resign\t";
  static public final String ARMY_COMMAND = "TITE-armycommand\t";
  static public final String CORPS_ATTACH = "TITE-corps-attach\t";
  static public final String UNIT_ATTACH = "TITE-unit-attach\t";
  static public final String TITE_STROKE = "TITE-stroke\t";
  static public final String TITE_INC_DEC = "TITE-inc-dec\t";
  static public final String TITE_SET_RPS="TITE-set-rps\t";
  static public final String TITE_WEATHER ="TITE-weather\t";
  
  public String encode(Command c) {
    if ( c instanceof WeatherCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((WeatherCommand)c).newValue);
      se.append(((WeatherCommand)c).oldValue);
      return TITE_WEATHER + se.getValue();
    }
    if ( c instanceof IncDecCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((IncDecCommand)c).index);
      se.append(((IncDecCommand)c).increment);
      return TITE_INC_DEC + se.getValue();
    }
    if ( c instanceof SetRPsCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((SetRPsCommand)c).index);
      se.append(((SetRPsCommand)c).newValue);
      se.append(((SetRPsCommand)c).oldValue);
      return TITE_SET_RPS + se.getValue();
    }
    if ( c instanceof StrokeCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((StrokeCommand)c).doit);
      se.append(((StrokeCommand)c).targetId);
      if (((StrokeCommand)c).undoit == null ) {
        se.append( false );
      } else {
        se.append( true );
        se.append(((StrokeCommand)c).undoit);
      }
      return TITE_STROKE + se.getValue();
    }
    if (c instanceof AttachUnitCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((AttachUnitCommand)c).nation);
      se.append(((AttachUnitCommand)c).army);
      se.append(((AttachUnitCommand)c).corps);
      se.append(((AttachUnitCommand)c).oldArmy);
      se.append(((AttachUnitCommand)c).oldCorps);
      se.append(((AttachUnitCommand)c).targetId);
      return UNIT_ATTACH + se.getValue();
    }
    if (c instanceof AttachCorpsCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((AttachCorpsCommand)c).nation);
      se.append(((AttachCorpsCommand)c).corps);
      se.append(((AttachCorpsCommand)c).newArmy);
      se.append(((AttachCorpsCommand)c).oldArmy);
      return CORPS_ATTACH + se.getValue();
    }
    if (c instanceof TiteCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((TiteCommand) c).scenario);
      se.append(((TiteCommand) c).sf);
      se.append(((TiteCommand) c).osh);
      se.append(((TiteCommand) c).c);
      se.append(((TiteCommand) c).h);
      se.append(((TiteCommand) c).hr);
      se.append(((TiteCommand) c).fr);
      se.append(((TiteCommand) c).cr);
      se.append(((TiteCommand) c).gameTurnx);
      se.append(((TiteCommand) c).playerx);
      se.append(((TiteCommand) c).phasex);
      se.append(((TiteCommand) c).interphasex);
      se.append(((TiteCommand) c).startingx);
      se.append(((TiteCommand) c).finishingx);
      se.append(((TiteCommand) c).VPsx);
      se.append(intArrayToString(((TiteCommand) c ).RPs));
      se.append(((TiteCommand) c).w);
      se.append(StringArrayConfigurer.arrayToString(((TiteCommand) c).commanders));
      se.append(StringArrayConfigurer.arrayToString(((TiteCommand) c).commanderNames));
      se.append(intArrayToString(((TiteCommand) c ).attaches));
      if ( ((TiteCommand)c).scenario == 2 || ((TiteCommand)c).scenario == 3 ) {
        se.append( ((TiteCommand)c).lw3attach );
      }
      se.append(((TiteCommand)c).oss);
      return TITE_SAVE + se.getValue();
    }
    if (c instanceof ResignCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((ResignCommand) c).nation);
      se.append(((ResignCommand) c).army);
      se.append(((ResignCommand) c).oldCommander);
      se.append(((ResignCommand) c).oldCommanderName);
      return ARMY_RESIGN + se.getValue();
    }
    if (c instanceof TakeCommand) {
      SequenceEncoder se = new SequenceEncoder('\t');
      se.append(((TakeCommand) c).nation);
      se.append(((TakeCommand) c).army);
      se.append(((TakeCommand) c).newCommander);
      se.append(((TakeCommand) c).newCommanderName);
      return ARMY_COMMAND + se.getValue();
    }
    return null;
  }
  
  public Command decode (String command) {
    if ( command.startsWith(TITE_WEATHER)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new WeatherCommand( st.nextInt(1), st.nextInt(1));
    }
    if ( command.startsWith(TITE_INC_DEC)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new IncDecCommand( st.nextInt(0), st.nextBoolean(true));
    }
    if ( command.startsWith(TITE_SET_RPS)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new SetRPsCommand( st.nextInt(0), st.nextInt(0), st.nextInt(0));
    }
    if ( command.startsWith(TITE_STROKE)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new StrokeCommand( st.nextKeyStroke(' '), st.nextToken(), 
              (st.nextBoolean(false) ? st.nextKeyStroke(' ') : null ) );
    }
    if ( command.startsWith(UNIT_ATTACH)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new AttachUnitCommand( st.nextInt(0), st.nextInt(0), st.nextInt(0),
              st.nextInt(0), st.nextInt(0), st.nextToken() );
    }
    if ( command.startsWith(CORPS_ATTACH)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new AttachCorpsCommand( st.nextInt(0), st.nextInt(0), st.nextInt(0),
              st.nextInt(0));
    }
    if ( command.startsWith(TITE_SAVE)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new TiteCommand( st );
    }
    if ( command.startsWith(ARMY_RESIGN)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new ResignCommand( st.nextInt(0), st.nextInt(0), st.nextToken(),
              st.nextToken());
    }
    if ( command.startsWith(ARMY_COMMAND)) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder( command, '\t');
      st.nextToken();
      return new TakeCommand( st.nextInt(0), st.nextInt(0), st.nextToken(),
              st.nextToken());
    }
    return null;
  }
}
