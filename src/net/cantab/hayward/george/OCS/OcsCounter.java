/*
 *
 * Copyright (c) 2010 by George Hayward
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

package net.cantab.hayward.george.OCS;

import VASSAL.build.module.BasicCommandEncoder;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.command.Command;
import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.GamePiece;
import VASSAL.counters.KeyCommand;
import VASSAL.counters.PieceEditor;
import VASSAL.counters.Properties;
import VASSAL.counters.Stack;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * This is the base class for all the decorators which represent different sorts
 * of counter in an OCS game. It contains all the required fields and methods
 * so that other classes can access them by casting to this class. The OCS decorators
 * are always the outermost decorators and all pieces in a OCS module have a OCS
 * decorator so that the OCS functionality can be implemented by casting pieces
 * to this class.
 * @author George Hayward
 */
public abstract class OcsCounter extends Decorator implements EditablePiece {

    /**
     * Class constructor. Set the default security.
     */
    public OcsCounter( GamePiece p) {
        setInner(p);
        theSide = -1;
        security = CHANGE;
    }

    /*
     * First the fields which are defined here and accessed by external classes
     * to provide the OCS functionality
     */

    /**
     * The side to which this counter belongs. This is -1 if it belongs to
     * neither side. For units this is a TYPE value but for controlled markers
     * it is a STATE value.
     */
    public int theSide;

    /**
     * The factors which identify this unit. This is in the same format as
     * found in the scenario setups and is intended to allow scenario files to
     * largely be created from the game PDF files.
     */
    public String factors;

    /**
     * The division to which a ground unit belongs if any
     */
    public String division;

    /*
     * Implement the restricted display and access to this piece
     */

    /**
     * This holds the current level of display and access to the piece for the
     * current user. This is a *WORKING* data.
     */
    protected int security;
    
    /**
     * The highest level of security. The counter is not visible to the opposing
     * side in any way.
     */
    protected static final int HIDDEN = 0;

    /**
     * The next level of security. The counter is visible but is displayed as
     * generic counter
     */
    protected static final int CONCEALED = 1;

    /**
     * The next level of security. The counter is visible with its normal display
     * but not the state of its internals or losses
     */
    protected static final int VISIBLE = 2;

    /**
     * The final level of security mean the counter is fully visible and can be
     * moved, changed etc by the current user
     */
    protected static final int CHANGE = 3;

    /**
     * Set a new security level
     */
    protected void setSecurity( int newLevel ) {
        security = newLevel;
    }

    /**
     * This checks that the security setting is uptodate
     */
    void checkSecurity() {
        Stack p;
        p = getParent();
        if ( p != null && p instanceof StackOverride ) {
            ( (StackOverride) p ).checkVisibility();
        }
    }

    /**
     * Returns true if the current user can change the state of this piece.
     * This is always the case if the hidden movement extras are turned off
     */
    boolean userCanChange() {
        if (Statics.hiddenMovementOff) return true;
        if (Statics.readingTextFile) return true;
        if (piece.getMap() == null) {
            if (theSide == -1) {
                return Statics.theSides[0].controlled
                        || Statics.theSides[1].controlled;
            } else {
                return Statics.theSides[theSide].controlled;
            }
        }
        return security == CHANGE;
    }

    /**
     * Returns true if the current user can see this piece
     */
    boolean userCanSee() {
        return security != HIDDEN || piece.getMap() == null;
    }

    /**
     * Return the value of the appropriate system value to make this counter
     * invisible or not. Also to make it changeable or not
     */
    @Override
    public Object getProperty(Object key) {
        if (Properties.INVISIBLE_TO_ME.equals(key)) {
            return !userCanSee();
        }
        if (Properties.RESTRICTED.equals(key) || Properties.RESTRICTED_MOVEMENT.equals(key)) {
            return userCanChange() ? null : true;
        }
        return super.getProperty(key);
    }

    /**
     * Prevent menu from being called up for enemy units
     */
    @Override
    public KeyCommand[] getKeyCommands() {
        if (userCanChange()) return super.getKeyCommands();
        return new KeyCommand[0];
    }

    /**
     * Prevent any key commands going to an enemy stack
     */
    @Override
    public Command keyEvent (KeyStroke k ) {
        if (userCanChange()) return super.keyEvent(k);
        return null;
    }

    /*
     * Abstract methods from decorator which must be implemented here
     */

    /** The type information is information that does not change
     * during the course of a game.  All this information has been moved into
     * the @see PieceData class so it is just necessary to save which one this
     * piece uses.
     * @see BasicCommandEncoder */
    public String myGetType() {
        return myID();
    }

    protected abstract String myID();

    public void mySetState(String newState) {
    }

    public String myGetState() {
        return "";
    }

    protected KeyCommand[] myGetKeyCommands() {
        return new KeyCommand[0];
    }

    public Command myKeyEvent(KeyStroke stroke) {
        return null;
    }

    /*
     * Methods from interface GamePiece which must be implemented here
     */
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

    /*
     * Methods from the EditablePiece interface
     */

    public void mySetType(String type) {
    }

        /** Get the configurer for this trait */
    @Override
    public PieceEditor getEditor() {
        return new Ed0();
    }

    public HelpFile getHelpFile() {
        return null;
    }

    public class Ed0 implements PieceEditor {

        private JPanel panel;

        public Ed0() {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        }

        public Component getControls() {
            return panel;
        }

        public String getType() {
            return "";
        }

        public String getState() {
            return "";
        }
    }

    public String getDescription() {
        return "??";
    }
}
