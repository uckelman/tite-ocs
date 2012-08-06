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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JSplitPane;

/**
 *
 * @author George Hayward
 */
public class SheetImplement implements SheetImpl, LayoutManager {
    
    /**
     * The sheet of which this is the implementation.
     */
    protected Sheet theMaster;
    
    /**
     * The split pane which implements the system in Swing.
     */
    protected mySplitPane splitter;
    
    /**
     * The layout manager of the split pane.
     */
    protected LayoutManager theManager;
    
    /**
     * Add layout component. Just pass through to real manager.
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {
        theManager.addLayoutComponent(name, comp);
    }
    
    /**
     * Minimum size. Just pass through to real manager
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return theManager.minimumLayoutSize(parent);
    }
    
    /**
     * Preferred size. Just pass through to real manager
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return theManager.preferredLayoutSize(parent);
    }
    
    /**
     * Remove a component
     */
    @Override
    public void removeLayoutComponent(Component comp) {
        theManager.removeLayoutComponent(comp);
    }
    
    /**
     * Layout the split pane. On first layout set the divider location after the
     * layout then remove this as a layout manager.
     */
    @Override
    public void layoutContainer(Container parent) {
        theManager.layoutContainer(parent);
        splitter.setDividerLocation(((double)theMaster.topOrLeftPercent)/100.0);
        theManager.layoutContainer(parent);
        splitter.setLayout(theManager);
    }
    
    /**
     * Create a new implementation.
     * @param master - the sheet which is to be implemented.
     */
    protected SheetImplement(Sheet master) {
        theMaster = master;
    }

    /**
     * Get the percentage of the split pane used by the top/left portion.
     * @return an integer from 0-100 which is the percentage of the width
     * taken up by the left portion or the percentage of the height taken up by
     * the top portion.
     */
    @Override
    public int getPercentage() {
        if (splitter == null) 
            return 50;
        int loc = splitter.getDividerLocation();
        int sze;
        Dimension size = splitter.getSize();
        if (theMaster.horizontal) {
            sze = size.width;
        } else {
            sze = size.height;
        }
        return (loc * 100) / sze;
    }

    /**
     * Create the split pane required to implement the sheet.
     */
    @Override
    public void realise() {
        splitter = new mySplitPane(theMaster.horizontal 
                                  ? JSplitPane.HORIZONTAL_SPLIT
                                  : JSplitPane.VERTICAL_SPLIT, true,
                                  (Component)theMaster.getTopLeft(),
                                  (Component)theMaster.getBottomRight());
        theManager = splitter.getLayout();
        splitter.setLayout(this);
        splitter.setResizeWeight(0.5);
    }

    @Override
    public void unRealise() {
        splitter = null;
        theManager = null;
    }

    @Override
    public Realiser getRealiser() {
        return splitter;
    }

    /**
     * Wrapper class for Split Pane.
     */
    public class mySplitPane extends JSplitPane implements Realiser {
        
        mySplitPane(int orient, boolean contin, Component l, Component r) {
            super (orient, contin, l, r);
        }
    }
}
