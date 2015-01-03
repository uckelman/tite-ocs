/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.btinternet.george973;

import java.awt.Window;
import java.awt.Graphics;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import VASSAL.build.Buildable;
import VASSAL.build.module.Map;
import VASSAL.build.module.map.BoardPicker;
import VASSAL.build.module.map.MenuDisplayer;
import VASSAL.build.module.GlobalOptions;
import VASSAL.build.module.PlayerRoster;
import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.configure.ConfigureTree;
import VASSAL.configure.StringArrayConfigurer;
import VASSAL.configure.ValidationReport;
import VASSAL.configure.ValidityChecker;
import VASSAL.tools.AdjustableSpeedScrollPane;
import VASSAL.tools.menu.MenuManager;

/**
 *
 * @author george
 */
public class TiteMap extends Map {

  int gameTurn;
  int[] scenarios = new int[0];
  
  boolean inScenario( int x ) {
    for ( int i = 0; i < scenarios.length; i++ ) {
      if (scenarios[i] == x ) return true;
    }
    return false;
  }

  public TiteMap() {
    super();
  }
  
  public void add (Buildable b) {
    if ( b instanceof MenuDisplayer ) {
      b = new MenuDisplayerOverride();
    }
    super.add(b);
  }

  public static final String GAME_TURN="gameTurn";
  public static final String SCENARIOS="scenarios";

  public String[] getAttributeNames() {
    String[] s1 = new String[]{GAME_TURN, SCENARIOS};
    String[] s2 = super.getAttributeNames();
    String[] s = new String[s1.length + s2.length];
    System.arraycopy(s1, 0, s, 0, s1.length);
    System.arraycopy(s2, 0, s, s1.length, s2.length);
    return s;
  }

  public String[] getAttributeDescriptions() {
    String[] s1 = new String[]{"Game Turn:", "Scenarios: "}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    String[] s2 = super.getAttributeDescriptions();
    String[] s = new String[s1.length + s2.length];
    System.arraycopy(s1, 0, s, 0, s1.length);
    System.arraycopy(s2, 0, s, s1.length, s2.length);
    return s;
  }

  public Class<?>[] getAttributeTypes() {
    final Class<?>[] c1 = new Class<?>[]{
      Integer.class,
      String[].class
    };
    final Class<?>[] c2 = super.getAttributeTypes();
    final Class<?>[] c = new Class<?>[c1.length + c2.length];
    System.arraycopy(c1, 0, c, 0, c1.length);
    System.arraycopy(c2, 0, c, c1.length, c2.length);
    return c;
  }
  
  public void setAttribute(String key, Object value) {
    if (GAME_TURN.equals(key)) {
      if (value instanceof String ) value = Integer.valueOf((String)value);
      if (value instanceof Integer ) gameTurn = (Integer)value;
      return;
    }
    if (SCENARIOS.equals(key)) {
      if (value instanceof String[] || value instanceof String) {
        String[] s = (value instanceof String[] ) ? (String[]) value
                : StringArrayConfigurer.stringToArray((String)value);
        scenarios = new int[ s.length];
        for (int i = 0; i < s.length; i++ ) {
          scenarios[i] = Integer.valueOf(s[i]);
        }
      }
      return;
    }
    super.setAttribute(key, value);
  }

  public String getAttributeValueString(String key) {
    if (GAME_TURN.equals(key)) {
      return Integer.toString(gameTurn);
    }
    if (SCENARIOS.equals(key)) {
      String[] s = new String[ scenarios.length];
      for (int i = 0; i < scenarios.length; i++ ) {
        s[i] = Integer.toString(scenarios[i]);
      }
      return StringArrayConfigurer.arrayToString(s);
    }
    return super.getAttributeValueString(key);
  }
  
  public JComponent getView() {
    if (theMap == null) {
      theMap = new View(this);
      scroll = new AdjustableSpeedScrollPane(
            theMap,
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scroll.unregisterKeyboardAction(KeyStroke.getKeyStroke(
            KeyEvent.VK_PAGE_DOWN, 0));
      scroll.unregisterKeyboardAction(KeyStroke.getKeyStroke(
            KeyEvent.VK_PAGE_UP, 0));

      layeredPane.setLayout(new InsetLayout(layeredPane, scroll));
      layeredPane.add(scroll, JLayeredPane.DEFAULT_LAYER);
    }
    return theMap;
  }

  public boolean shouldDockIntoMainWindow() {
    return false;
  }

  public void setup(boolean show) {
    if ( !show ) {
      pieces.clear();
      boards.clear();
    }
  }

  public static String getConfigureTypeName() {
    return "Tite Window";
  }

  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("Tite.htm"); //$NON-NLS-1$
  }

  public static class View extends Map.View {
    private static final long serialVersionUID = 1L;

    private boolean listenersActive;
    private List<KeyListener> keyListeners = new ArrayList<KeyListener>();
    private List<MouseListener> mouseListeners =
      new ArrayList<MouseListener>();
    private List<MouseMotionListener> mouseMotionListeners =
      new ArrayList<MouseMotionListener>();
    private DropTarget dropTarget;

    public View(TiteMap m) {
      super(m);
    }

    public synchronized void setDropTarget(DropTarget dt) {
      if (dt != null) {
        dropTarget = dt;
      }
    }

    public synchronized void addKeyListener(KeyListener l) {
      if (listenersActive) {
        super.addKeyListener(l);
      }
      else {
        keyListeners.add(l);
      }
    }

    public synchronized void addMouseListener(MouseListener l) {
      if (listenersActive) {
        super.addMouseListener(l);
      }
      else {
        mouseListeners.add(l);
      }
    }

    public synchronized void addMouseMotionListener(MouseMotionListener l) {
      if (listenersActive) {
        super.addMouseMotionListener(l);
      }
      else {
        mouseMotionListeners.add(l);
      }
    }

    /**
     * Disable all keyboard and mouse listeners on this component
     */
    protected void disableListeners() {
      for (KeyListener l : keyListeners) {
        removeKeyListener(l);
      }
      for (MouseListener l : mouseListeners) {
        removeMouseListener(l);
      }
      for (MouseMotionListener l : mouseMotionListeners) {
        removeMouseMotionListener(l);
      }
      super.setDropTarget(null);
      listenersActive = false;
    }

    /**
     * Enable all keyboard and mouse listeners on this component
     */
    protected void enableListeners() {
      for (KeyListener l : keyListeners) {
        super.addKeyListener(l);
      }
      for (MouseListener l : mouseListeners) {
        super.addMouseListener(l);
      }
      for (MouseMotionListener l : mouseMotionListeners) {
        super.addMouseMotionListener(l);
      }
      super.setDropTarget(dropTarget);
      listenersActive = true;
    }
  }
  
  public void setBoards(Collection<Board> c) {
    c = ((BoardPickerOverride)picker).getBoards();
    super.setBoards(c);
  }

  public BoardPicker getBoardPicker() {
    if (picker == null) {
      picker = new BoardPickerOverride();
      picker.build(null);
      add(picker);
      picker.addTo(this);
    }
    return picker;
  }


}

