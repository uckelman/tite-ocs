/* 
 *
 * Copyright (c) 2000-2012 by Rodney Kinney, Joel Uckelman, George Hayward
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
package net.cantab.hayward.george.MAP;

import VASSAL.build.GameModule;
import VASSAL.build.module.GlobalOptions;
import VASSAL.configure.IntConfigurer;
import VASSAL.preferences.Prefs;
import VASSAL.tools.ComponentSplitter;
import VASSAL.tools.menu.MenuManager;
import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

/**
 *
 * @author George Hayward
 */
public class MainWindowImplement implements MainWindowImpl {

    /**
     * The MainSheet this is the implementation of.
     */
    protected MainSheet theMaster;

    /**
     * This is true if the main map is displayed in the same window as the
     * control panel.
     */
    protected boolean sharesWindow = GlobalOptions.getInstance().isUseSingleWindow();

    /**
     * The pane on the splitter in which the main map display will take place.
     */
    protected ComponentSplitter.SplitPane mainWindowDock;

    /**
     * The layered pane which covers the whole map area regardless of how it is
     * divided into sheets.
     */
    protected JLayeredPane layeredPane;

    /**
     * The internal name of the main window height option.
     */
    protected static final String MAIN_WINDOW_HEIGHT = "mainWindowHeight";

    /**
     * The top window if not sharing a window with the control panel.
     */
    protected Window topWindow;

    /**
     * Create a new implementation.
     */
    protected MainWindowImplement(MainSheet master) {
        theMaster = master;
    }

    /**
     * Create the swing objects needed to realise the main map window.
     */
    @Override
    public void realise() {
        if (sharesWindow) {
            /*
             * The main map display will share the same window as the control
             * panel. So need to split the control panel and add the map as the
             * hidden component at the bottom of the split. But first check to
             * see if the split has already been done by inspecting the parent
             * of the cotrol panel to see if it is a split pane.
             */
            JComponent controlPanel = GameModule.getGameModule().getControlPanel();
            Container panelParent = controlPanel.getParent();
            if (panelParent instanceof ComponentSplitter.SplitPane) {
                /*
                 * Already split so the hidden component of this split pane is a
                 * layered pane already
                 */
                mainWindowDock = (ComponentSplitter.SplitPane) panelParent;
                Component pane = mainWindowDock.getHideableComponent();
                if (!(pane instanceof JLayeredPane)) {
                    throw new RuntimeException("Unexpected hideable component");
                }
                layeredPane = (JLayeredPane) pane;
            } else {
                /*
                 * Split the control panal and add a layered pane at the bottom
                 * for the map.
                 */
                layeredPane = new JLayeredPane();
                ComponentSplitter theSplitter = new ComponentSplitter();
                mainWindowDock = theSplitter.splitBottom(controlPanel, layeredPane, true);
            }
            /*
             * Get the height option and if there is none create a default one.
             * If there is a non default value for the option then resize the
             * window to the given size
             */
            Object heightPreference = Prefs.getGlobalPrefs().getValue(MAIN_WINDOW_HEIGHT);
            if (heightPreference == null) {
                final IntConfigurer config =
                        new IntConfigurer(MAIN_WINDOW_HEIGHT, null, -1);
                Prefs.getGlobalPrefs().addOption(null, config);
            } else {
                int height = ((Integer) heightPreference).intValue();
                if (height > 0) {
                    final Container top = mainWindowDock.getTopLevelAncestor();
                    top.setSize(top.getWidth(), height);
                }
            }
        } else {
            JFrame d = new JFrame();
            d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            d.setTitle(theMaster.theMaster.getDefaultWindowTitle()); //TODO: get title
            d.setJMenuBar(MenuManager.getInstance().getMenuBarFor(d));
            topWindow = d;
            topWindow.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    GameModule.getGameModule().getGameState().setup(false);
                }
            });
            layeredPane = new JLayeredPane();
            ((RootPaneContainer) topWindow).setContentPane(layeredPane);
        }
        layeredPane.add((JComponent) theMaster.getLowerRealiser(), JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * Remove the swing objects used in this window implementation.
     */
    @Override
    public void unRealise() {
        if (sharesWindow) {
            if (mainWindowDock.getHideableComponent().isShowing()) {
                Prefs.getGlobalPrefs().getOption(MAIN_WINDOW_HEIGHT).setValue(mainWindowDock.getTopLevelAncestor().getHeight());
            }
            mainWindowDock.hideComponent();
        } else {
            topWindow.dispose();
        }
        mainWindowDock = null;
        layeredPane = null;
        topWindow = null;
    }
}
