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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS;

import javax.swing.JToolBar;

import VASSAL.build.AbstractConfigurable;
import VASSAL.build.AutoConfigurable;
import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.GameComponent;
import VASSAL.build.module.Map;
import VASSAL.build.module.PlayerRoster;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.build.module.gamepieceimage.StringEnumConfigurer;
import VASSAL.build.module.map.BoardPicker;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.HexGrid;
import VASSAL.build.widget.PieceSlot;
import VASSAL.command.Command;
import VASSAL.command.CommandEncoder;
import VASSAL.command.NullCommand;
import VASSAL.configure.BooleanConfigurer;
import VASSAL.configure.Configurer;
import VASSAL.configure.ConfigurerFactory;
import VASSAL.configure.IntConfigurer;
import VASSAL.counters.BasicPiece;
import VASSAL.counters.Decorator;
import VASSAL.counters.Embellishment;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceCloner;
import VASSAL.counters.PieceDefiner;
import VASSAL.i18n.Resources;
import VASSAL.tools.SequenceEncoder;
import VASSAL.tools.ToolBarComponent;
import VASSAL.tools.filechooser.FileChooser;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;
import net.cantab.hayward.george.OCS.Counters.Airbase;
import net.cantab.hayward.george.OCS.Counters.Aircraft;
import net.cantab.hayward.george.OCS.Counters.Artillery;
import net.cantab.hayward.george.OCS.Counters.AttackCapable;
import net.cantab.hayward.george.OCS.Counters.AttackMarker;
import net.cantab.hayward.george.OCS.Counters.Defensive;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.Counters.Fighter;
import net.cantab.hayward.george.OCS.Counters.GameMarker;
import net.cantab.hayward.george.OCS.Counters.HeadQuarters;
import net.cantab.hayward.george.OCS.Counters.Hedgehog;
import net.cantab.hayward.george.OCS.Counters.Leader;
import net.cantab.hayward.george.OCS.Counters.OOS;
import net.cantab.hayward.george.OCS.Counters.Over;
import net.cantab.hayward.george.OCS.Counters.ReplaceCard;
import net.cantab.hayward.george.OCS.Counters.Replacement;
import net.cantab.hayward.george.OCS.Counters.Reserve;
import net.cantab.hayward.george.OCS.Counters.Ship;
import net.cantab.hayward.george.OCS.Counters.SupplyMarker;
import net.cantab.hayward.george.OCS.Counters.Transport;
import net.cantab.hayward.george.OCS.Counters.Under;
import net.cantab.hayward.george.OCS.Parsing.ParseText;
import org.apache.commons.io.filefilter.FalseFileFilter;

/**
 * This defines all the static data needed by the module
 *
 * @author George Hayward
 */
public class Statics extends AbstractConfigurable
        implements CommandEncoder,
        GameComponent {

    /**
     * The structure of a piece pointer
     */
    static class PiecePtr {
        String name;
        String image;
        OcsCounter piece;
    }
    /**
     * Which module
     */
    int module;
    final static int BalticGap = 1;
    final static int CaseBlue = 2;
    final static int DAK = 3;
    final static int Korea = 4;
    final static int Tunisia = 5;
    final static int Hubes = 6;
    final static int Sicily = 7;
    final static int Burma = 8;
    /**
     * Returns true if game is Baltic Gap
     */
    public boolean isBalticGap() {
        return module == BalticGap;
    }
    /**
     * True if this game is DAK
     */
    public boolean isDAK() {
        return module == DAK;
    }
    /**
     * True if this game is Case Blue
     */
    public boolean isCaseBlue() {
        return module == CaseBlue;
    }
    /**
     * True if this game is Korea
     */
    public boolean isKorea() {
        return module == Korea;
    }
    /**
     * True if this game is Tunisia
     */
    public boolean isTunisia() {
        return module == Tunisia;
    }
    /**
     * True if this game is Hubes
     */
    public boolean isHubes() {
        return module == Hubes;
    }
    /**
     * True if this game is Sicily
     */
    public boolean isSicily() {
        return module == Sicily;
    }
    /**
     * True if this game is Burma
     */
    public boolean isBurma() {
        return module == Burma;
    }
    /**
     * An array of all the piece definitions used in the module
     */
    static PiecePtr[] thePieces = null;
    /**
     * An array with the two sides in the game.
     */
    static public Side[] theSides = new Side[2];
    /**
     * An array with the current commanders in the game
     */
    static Commander[] theCommanders = new Commander[0];
    /**
     * The current user's Commander object
     */
    static Commander curCommander = null;
    /**
     * Pointer to the current object
     */
    public static Statics theStatics;
    /**
     *  Whether to display PZs for a side
     */
    public static boolean[] showPZs = {false,false}; 
    /**
     * Whether to show ZOCs for a side
     */
    public static boolean[] showZOCs = {false, false};
    /**
     * A value which is updated whenever a piece is moved. It is used to determine
     * whether the security states of pieces within a stack need to be recalculated
     */
    static int check = 1;
    /**
     * True if reading scenario text file
     */
    static public boolean readingTextFile = false;

    /*
     * The following are preferences set by the user. Some can only be set at the
     * start of a scenario or when loading a save file earlier than version 3.
     */
    /**
     * True if all forms of hidden movement are disabled
     */
    public static boolean hiddenMovementOff = false;
    /**
     * True if the range to a friendly piece affects the display of a stack
     */
    static boolean rangeInUse = false;
    /**
     * True if nearest AEP to be used for flight distance calculations rather then
     * one which gives minimal distance
     */
    static boolean useNearest = false;
    /**
     * Security to be applied to a hostile stack in a zone
     */
    static int zoneSecurity = OcsCounter.VISIBLE;
    /**
     * Range at which opposing stacks including aircraft become invisible in pixels
     */
    static int rangeAirHidden = 0;
    /**
     * Same range in hexes
     */
    static int hexRangeAirHidden = 0;
    /**
     * Range at which opposing stacks become invisble
     */
    static int rangeHidden = 0;
    /**
     * Same range in hexes
     */
    static int hexRangeHidden = 0;
    /**
     * Range at which opposing stackes become concealed
     */
    static int rangeConcealed = 0;
    /**
     * Same range in hexes
     */
    static int hexRangeConcealed = 0;
    /**
     * Range at which opposing stacks become flattened
     */
    static int rangeFlattened = 0;
    /**
     * Same range in hexes
     */
    static int hexRangeFlattened = 0;
    /**
     * True if Formation Markers are to be implemented as in 13.7
     */
    static boolean useFormations = false;
    /**
     * True if ranges are to be done in hexagaonal fashion
     */
    static boolean hexRanges = false;
    /**
     * The main map for this module
            st.nextInt(0);
     */
    public static Map theMap;
    /**
     * The hex size for the main map
     */
    static double hexSize;
    /**
     * The instructions for the current scenario
     */
    static String[] scenarioInstructions;
    /**
     * The toolbar for module menus
     */
    JToolBar toolbar;
    /**
     * The command button
     */
    JButton commandLaunch;
    /**
     * The process pieces button
     */
    JButton piecesLaunch;
    /**
     * The piece processing window
     */
    JDialog piecesWindow;
    /**
     * The read text file & create scenario button
     */
    JButton textLaunch;
    /**
     * Display the scenario instructions
     */
    JButton instructions;
    /**
     * Check the mask/layers decorators in the right order
     */
    JButton checkOrder;

    /**
     * Create an object and thus initialise all the static data.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public Statics() {

        theSides[0] = new Side("Axis");
        theSides[1] = new Side("Soviet");
        theStatics = this;

        PieceDefiner.addDefinition(new Airbase());
        PieceDefiner.addDefinition(new Aircraft());
        PieceDefiner.addDefinition(new Artillery());
        PieceDefiner.addDefinition(new AttackCapable());
        PieceDefiner.addDefinition(new AttackMarker());
        PieceDefiner.addDefinition(new Defensive());
        PieceDefiner.addDefinition(new Division());
        PieceDefiner.addDefinition(new GameMarker());
        PieceDefiner.addDefinition(new HeadQuarters());
        PieceDefiner.addDefinition(new Hedgehog());
        PieceDefiner.addDefinition(new Over());
        PieceDefiner.addDefinition(new OOS());
        PieceDefiner.addDefinition(new Replacement());
        PieceDefiner.addDefinition(new Reserve());
        PieceDefiner.addDefinition(new Ship());
        PieceDefiner.addDefinition(new SupplyMarker());
        PieceDefiner.addDefinition(new Transport());
        PieceDefiner.addDefinition(new Under());
        PieceDefiner.addDefinition(new ReplaceCard());
        PieceDefiner.addDefinition(new Leader());
        PieceDefiner.addDefinition(new Fighter());

    }

    /**
     * Update the scenario information
     */
    static public void addToScenarioInformation(String s) {
        if (scenarioInstructions == null) {
            scenarioInstructions = new String[1];
            scenarioInstructions[0] = s;
        } else {
            String[] t = new String[scenarioInstructions.length + 1];
            System.arraycopy(scenarioInstructions, 0, t, 0, scenarioInstructions.length);
            t[scenarioInstructions.length] = s;
            scenarioInstructions = t;
        }
        GameModule.getGameModule().getChatter().show(s);
    }

    /**
     * Build the Piece Ptr data
            st.nextInt(0);
     */
    static void buildPiecePtrs() {
        if (thePieces != null) {
            return;
        }
        List<PieceSlot> t = GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlot.class);
        if (t.isEmpty()) {
            thePieces = new PiecePtr[0];
            return;
        }
        thePieces = new PiecePtr[t.size()];
        int i = 0;
        for (PieceSlot q : t) {
            GamePiece p = PieceCloner.getInstance().clonePiece(q.getPiece());
            GamePiece g = p;
            GamePiece h = p;
            for (;;) {
                if (!(g instanceof Decorator)) {
                    break;
                }
                h = g;
                g = ((Decorator) g).getInner();
            }
            String s = g.getType();
            final SequenceEncoder.Decoder sty = new SequenceEncoder.Decoder(s, ';');
            sty.nextToken();
            sty.nextChar('\0');
            sty.nextChar('\0');
            String front = sty.nextToken();
            String pname = sty.nextToken();
            if (theStatics.isCaseBlue() && h instanceof Embellishment) {
                s = h.getType();
                SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(s, ';');
                st.nextToken();
                st.nextToken();
                st.nextInt(0);
                st.nextToken();
                st.nextToken();
                st.nextInt(0);
                st.nextToken();
                st.nextToken();
                st.nextInt(0);
                st.nextToken();
                st.nextToken();
                st.nextKeyStroke('r');
                st.nextToken();
                st.nextBoolean(false);
                st.nextInt(0);
                st.nextInt(0);
                String[] imageName = st.nextStringArray(0);
                front = imageName[0];
            }
            thePieces[i] = new PiecePtr();
            thePieces[i].name = pname;
            thePieces[i].image = front;
            if (!(p instanceof OcsCounter)) {
                GameModule.getGameModule().getChatter().show("*** Not an OCS Counter "
                        + pname + " - image = " + front);
                thePieces[i].piece = null;
            } else {
                thePieces[i].piece = (OcsCounter) p;
            }
            i++;
        }
    }
    static OcsCounter lastConverted = null;

    /**
     * Find a piece which matches the given name and image name. Return  the
     * correct type of BasicCounter pointing at this piece
     */
    static OcsCounter findMatching(String name, String imageName, GamePiece p) {
        int j;
        int imageCount = 0;
        for (j = 0; j < 2; j++) {
            buildPiecePtrs();
            if (thePieces == null) {
                return null;
            }
            int i;
            for (i = 0; i < thePieces.length; i++) {
                if (thePieces[i] == null) {
                    continue;
                }
                if (!imageName.equals(thePieces[i].image)) {
                    continue;
                }
                imageCount++;
                if (name.equals(thePieces[i].name)) {
                    lastConverted = thePieces[i].piece;
                    BasicCommandEncoderOverride b = new BasicCommandEncoderOverride();
                    return (OcsCounter) b.createDecorator(thePieces[i].piece.myGetType(), p);
                }
            }
            if (imageCount == 1) {
                for (i = 0; i < thePieces.length; i++) {
                    if (thePieces[i] == null) {
                        continue;
                    }
                    if (!imageName.equals(thePieces[i].image)) {
                        continue;
                    }
                    lastConverted = thePieces[i].piece;
                    BasicCommandEncoderOverride b = new BasicCommandEncoderOverride();
                    return (OcsCounter) b.createDecorator(thePieces[i].piece.myGetType(), p);
                }
            }
            if (j == 0) {
                thePieces = null;
            }
        }
        return null;
    }

    /**
     * Convert a pre 3.0 version piece to the current version
     */
    static OcsCounter convertOldPiece(GamePiece p) {
        if (theStatics == null) {
            return null;
        }
        GamePiece g = p;
        GamePiece h = p;
        for (;;) {
            if (!(g instanceof Decorator)) {
                break;
            }
            h = g;
            g = ((Decorator) g).getInner();
        }
        if (!(g instanceof BasicPiece)) {
            return null;
        }
        String s = g.getType();
        final SequenceEncoder.Decoder sty = new SequenceEncoder.Decoder(s, ';');
        sty.nextToken();
        sty.nextChar('\0');
        sty.nextChar('\0');
        String front = sty.nextToken();
        String pname = sty.nextToken();
        if (theStatics.isCaseBlue() && h instanceof Embellishment) {
            s = h.getType();
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(s, ';');
            st.nextToken();
            st.nextToken();
            st.nextInt(0);
            st.nextToken();
            st.nextToken();
            st.nextInt(0);
            st.nextToken();
            st.nextToken();
            st.nextInt(0);
            st.nextToken();
            st.nextToken();
            st.nextKeyStroke('r');
            st.nextToken();
            st.nextBoolean(false);
            st.nextInt(0);
            st.nextInt(0);
            String[] imageName = st.nextStringArray(0);
            front = imageName[0];
        }
        if (front.equals("") && pname.equals("")) {
            return null;
        }
        OcsCounter b = findMatching(pname, front, p);
        if (thePieces == null) {
            return null;
        }
        if (b == null) {
            GameModule.getGameModule().getChatter().show("*** Unable to convert " + pname + " - image = " + front);
            b = new AttackCapable( AttackCapable.ID + "1;;", p);
        }
        return b;
    }

    /**
     * Convert a range in hexes to pixels
     */
    static int convertHexesToPixels(int hexes) {
        return (int) (hexes * hexSize + hexSize * 0.5);
    }

    /**
     * Convert a range in pixels to hexes
     */
    static int convertPixelsToHexes(int pixels) {
        return (int) (pixels / hexSize);
    }

    /**
     * Create the debug Toolbar entry. This is a null method which is overriden
     * in @class Debug which is a subclass of this one. When testing the system the
     * debug classes are used and for release the buildfile is editted to replace
     * the debug classes with the normal classes. The debug classes are always
     * subclasses of the classes they debug.
     */
    void createDebugEntries() {
    }

    /**
     * Create the normal toolbar entries
     */
    void createNormalEntries() {
        commandLaunch = new JButton("Command...");
        commandLaunch.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                launchCommandMenu();
            }
        });
        commandLaunch.setFocusable(false);
        commandLaunch.setToolTipText("Take/Resign command of a side");
        toolbar.add(commandLaunch);
        instructions = new JButton("Scenario Notes");
        instructions.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                displayInstructions();
            }
        });
        instructions.setFocusable(false);
        toolbar.add(instructions);
        /*
         * If in edit mode add option to process pieces to OCS module form quickly
         */
        if (GameModule.getGameModule().getArchiveWriter() != null) {
            piecesLaunch = new JButton("Piece Definitions");
            piecesLaunch.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    piecesProcess();
                }
            });
            piecesLaunch.setFocusable(false);
            toolbar.add(piecesLaunch);
            piecesWindow = new JDialog(GameModule.getGameModule().getFrame());
            piecesWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            piecesWindow.setTitle("Piece definitions");
            piecesWindow.setSize(700, 500);
            piecesWindow.addWindowListener(new WindowAdapter() {

                @Override
                public void windowClosing(WindowEvent e) {
                    piecesWindow.setVisible(false);
                }
            });
            piecesWindow.add(new PieceProcessor());
            textLaunch = new JButton("Read Scenario from Text File");
            textLaunch.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    readTextFile();
                }
            });
            textLaunch.setFocusable(false);
            textLaunch.setEnabled(false);
            toolbar.add(textLaunch);
            checkOrder = new JButton("Check Layers/Mask Order");
            checkOrder.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    checkLayerOrders();
                }
            });
            checkOrder.setFocusable(false);
            checkOrder.setEnabled(true);
            toolbar.add(checkOrder);
        }
    }

    /**
     * Check decorator order in all pieces
     */
    public void checkLayerOrders() {
        List<PieceSlot> t = GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlot.class);
        for (PieceSlot q : t) {
            GamePiece p = q.getPiece();
            GamePiece r = p;
            for (;;) {
                p = ((Decorator) p).getInner();
                if (p instanceof Embellishment || !(p instanceof Decorator)) {
                    break;
                }
                r = p;
            }
            if (p instanceof Embellishment) {
                GamePiece v = p;
                p = ((Decorator) p).getInner();
                if (p instanceof Decorator) {
                    GamePiece w = p;
                    GamePiece u = p;
                    for (;;) {
                        p = ((Decorator) p).getInner();
                        if (!(p instanceof Decorator)) {
                            break;
                        }
                        u = p;
                    }
                    ((Decorator) r).setInner(w);
                    ((Decorator) u).setInner(v);
                    ((Decorator) v).setInner(p);
                    String s = p.getType();
                    final SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(s, ';');
                    st.nextToken();
                    st.nextChar('\0');
                    st.nextChar('\0');
                    String front = st.nextToken();
                    String pname = st.nextToken();
                    GameModule.getGameModule().getChatter().show(pname + ": image = " + front + " - decorators in wrong order - fixed");
                }
            }
        }
    }

    /**
     * Display thye scenario instructions
     */
    public void displayInstructions() {
        if (scenarioInstructions != null) {
            for (int i = 0; i < scenarioInstructions.length; i++) {
                GameModule.getGameModule().getChatter().show(scenarioInstructions[i]);
            }
        }
    }

    /**
     * Read a scenario definition from a text file (which will be a straight
     * conversion of the PDFs of the game rules)
     */
    public void readTextFile() {
        final FileChooser fc = GameModule.getGameModule().getFileChooser();
        if (fc.showOpenDialog() != FileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fc.getSelectedFile();

        readingTextFile = true;
        ParseText p = new ParseText(file);


        p.parse();
        readingTextFile = false;

        instructions.setEnabled(scenarioInstructions != null);
    }

    /**
     * Just flip the visibility of the window
     */
    public void piecesProcess() {
        piecesWindow.setVisible(!piecesWindow.isShowing());
    }

    /**
     * Display the command menu
     */
    public void launchCommandMenu() {
        if (curCommander == null) {
            return;
        }
        int i;
        JMenu theMenu = new JMenu();
        JMenuItem mi;
        for (i = 0; i < 2; i++) {
            int j;
            int k = 0;
            for (j = 0; j < theCommanders.length; j++) {
                if (theCommanders[j].sidesCommanded[i]) {
                    k++;
                }
            }
            if (k != 0) {
                JMenu com = new JMenu("Commanders of the " + theSides[i].name + " side");
                for (j = 0; j < theCommanders.length; j++) {
                    if (theCommanders[j].sidesCommanded[i]) {
                        mi = new JMenuItem(theCommanders[j].name);
                        mi.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                            }
                        });
                        mi.setEnabled(true);
                        com.add(mi);
                    }
                }
                theMenu.add(com);
            }
            if (curCommander.sidesCommanded[i]) {
                final int m = i;
                mi = new JMenuItem("Resign from " + theSides[i].name + " side");
                mi.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        resignSide(m);
                    }
                });
                mi.setEnabled(true);
                theMenu.add(mi);
                int n = 0;
                for (j = 0; j < theCommanders.length; j++) {
                    if (theCommanders[j].sidesRequested[i]) {
                        n++;
                    }
                }
                if (n != 0) {
                    JMenu app = new JMenu("Approve to join " + theSides[i].name + " side");
                    JMenu rej = new JMenu("Reject to join " + theSides[i].name + " side");
                    for (j = 0; j < theCommanders.length; j++) {
                        if (theCommanders[j].sidesRequested[i]) {
                            mi = new JMenuItem(theCommanders[j].name);
                            final int p = j;
                            mi.addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    acceptSide(m, p);
                                }
                            });
                            mi.setEnabled(true);
                            app.add(mi);
                            mi = new JMenuItem(theCommanders[j].name);
                            mi.addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    rejectSide(m, p);
                                }
                            });
                            mi.setEnabled(true);
                            rej.add(mi);
                        }
                    }
                    theMenu.add(app);
                    theMenu.add(rej);
                }
            } else {
                final int m = i;
                if (k != 0) {
                    mi = new JMenuItem("Join " + theSides[i].name + " side");
                    mi.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            joinSide(m);
                        }
                    });
                    mi.setEnabled(true);
                    theMenu.add(mi);
                } else {
                    mi = new JMenuItem("Command " + theSides[i].name + " side");
                    mi.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            commandSide(m);
                        }
                    });
                    mi.setEnabled(true);
                    theMenu.add(mi);
                }
            }
        }
        if (curCommander.sidesCommanded[0] || curCommander.sidesCommanded[1]) {
            for (i = 0; i < 2; i++) {
                final int m = i;
                mi = new JMenuItem((showPZs[i] ? "Hide " : "Show ") + theSides[i].name + " PZs");
                mi.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        displayPZs(m);
                    }
                });
                mi.setEnabled(true);
                theMenu.add(mi);
                mi = new JMenuItem((showZOCs[i] ? "Hide " : "Show ") + theSides[i].name + " ZOCs");
                mi.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        displayZOCs(m);
                    }
                });
                mi.setEnabled(true);
                theMenu.add(mi);
            }
        }
        theMenu.getPopupMenu().show(commandLaunch, 0, commandLaunch.getHeight());
    }
    
    /**
     * Flip display of PZs
     */
    public void displayPZs(int i) {
        showPZs[i] = !showPZs[i];
        theMap.repaint();
    }

    /**
     * Flip display of ZOCs
     */
    public void displayZOCs(int i) {
        showZOCs[i] = !showZOCs[i];
        theMap.repaint();
    }

    /**
     * Resign a given side
     */
    void resignSide(int aSide) {
        curCommander.logDoCommand(0, aSide, false);
    }

    /**
     * Command a given side
     */
    void commandSide(int aSide) {
        curCommander.logDoCommand(0, aSide, true);
    }

    /**
     * Request to join a given side
     */
    void joinSide(int aSide) {
        curCommander.logDoCommand(1, aSide, true);
    }

    /**
     * Accept for a side
     */
    void acceptSide(int aSide, int com) {
        theCommanders[com].logDoCommand(0, aSide, true);
    }

    /**
     * Reject for a side
     */
    void rejectSide(int aSide, int com) {
        theCommanders[com].logDoCommand(1, aSide, false);
    }

    /*
     * Abstract methods from AbstractConfigurable which are implemented here
     */
    public String[] getAttributeDescriptions() {
        return new String[]{"Name of first side", "Name of second side",
        "OCS Module"};
    }

    public Class<?>[] getAttributeTypes() {
        return new Class<?>[]{String.class, String.class, ModuleConfig.class};
    }

    public static class ModuleConfig implements ConfigurerFactory {

        public Configurer getConfigurer(AutoConfigurable c, String key, String name) {
            return new StringEnumConfigurer(key, name, new String[]{
                        "Baltic Gap",
                        "Case Blue",
                        "DAK",
                        "Korea",
                        "Tunisia",
                        "Hube's Pocket",
                        "Sicily",
                        "Burma"
                    });
        }
    }

    /*
     * Abstract methods from AbstractBuildable which are implemented here
     */
    public String[] getAttributeNames() {
        return new String[]{"FIRST", "SECOND", "MODULE"};
    }
    
    public void setAttribute(String key, Object value) {
        if (value instanceof String) {
            String s = (String) value;
            if (key.equals("FIRST")) {
                theSides[0].name = s;
            } else if (key.equals("SECOND")) {
                theSides[1].name = s;
            } else if (key.equals("BG")) {
                if (Boolean.valueOf(s)) module = BalticGap;
            } else if (key.equals("CB")) {
                if (Boolean.valueOf(s)) module = CaseBlue;
            } else if (key.equals("DAK")) {
                if (Boolean.valueOf(s)) module = DAK;
            } else if (key.equals("K")) {
                if(Boolean.valueOf(s)) module = Korea;
            } else if (key.equals("T")) {
                if(Boolean.valueOf(s)) module = Tunisia;
            } else if (key.equals("MODULE")) {
                if (value.equals("Baltic Gap")) {
                    module = BalticGap;
                } else if (value.equals("Case Blue")) {
                    module = CaseBlue;
                } else if (value.equals("DAK")) {
                    module = DAK;
                } else if (value.equals("Korea")) {
                    module = Korea;
                } else if (value.equals("Tunisia")) {
                    module = Tunisia;
                } else if (value.equals("Hube's Pocket")) {
                    module = Hubes;
                } else if (value.equals("Sicily")) {
                    module = Sicily;
                } else if (value.equals("Burma")) {
                    module = Burma;
                } else {
                    module = BalticGap;
                }
            }
        }
    }

    public String getAttributeValueString(String key) {
        if (key.equals("FIRST")) {
            return theSides[0].name;
        } else if (key.equals("SECOND")) {
            return theSides[1].name;
        } else if (key.equals("MODULE")) {
            switch (module) {
            default:
            case BalticGap:
                return "Baltic Gap";
            case CaseBlue:
                return "Case Blue";
            case DAK:
                return "DAK";
            case Korea:
                return "Korea";
            case Tunisia:
                return "Tunisia";
            case Hubes:
                return "Hube's Pocket";
            case Sicily:
                return "Sicily";
            case Burma:
                return "Burma";
            }
        }
        return null;
    }

    /*
     * Abstract methods from Buildable which are implemented here
     */
    /**
     * Create all the objects which are going to hang off the task bar here.
     * @param parent
     */
    public void addTo(Buildable parent) {
        if (parent instanceof ToolBarComponent) {
            toolbar = ((ToolBarComponent) parent).getToolBar();
            createNormalEntries();
            createDebugEntries();
        }
        /*
         * Add this object to lists of those which encode/decode commands and
         * to list of those that save game information
         */
        GameModule.getGameModule().addCommandEncoder(this);
        GameModule.getGameModule().getGameState().addGameComponent(this);
        /*
         * Create the preferences in case they don't exist
         */
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new BooleanConfigurer(HIDDEN_MOVEMENT_OFF,
                "OCS - Disable all Fog Of War", Boolean.FALSE));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new StringEnumConfigurer(ZONE_SECURITY,
                "OCS - Display of pieces in off-map boxes",
                new String[]{"Visible", "Masked", "Invisible"}));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new BooleanConfigurer(RANGE_IN_USE,
                "OCS - Enable range based extra Fog Of War", Boolean.FALSE));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new IntConfigurer(RANGE_AIR_HIDDEN,
                "OCS - Range at which air units become invisible", new Integer(40)));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new IntConfigurer(RANGE_HIDDEN,
                "OCS - Range at which land/sea units become invisible", new Integer(20)));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new IntConfigurer(RANGE_CONCEALED,
                "OCS - Range at which units become masked", new Integer(10)));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new IntConfigurer(RANGE_FLAT,
                "OCS - Range at which stacks are shown as a single counter", new Integer(5)));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new BooleanConfigurer(USE_FORMATIONS,
                "OCS - Enable Formation Markers as in 13.7", Boolean.FALSE));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new BooleanConfigurer(HEX_RANGES,
                "OCS - Range Options use hexes rather than radii", Boolean.FALSE));
        GameModule.getGameModule().getPrefs().addOption(
                Resources.getString("Prefs.general_tab"),
                new BooleanConfigurer(USE_NEAREST_AEP,
                "OCS - Use nearest AEP (rather than minimal distance)", Boolean.FALSE));
        /*
         * Listen for chnages to user name / password
         */
        GameModule.getGameModule().getPrefs().getOption(GameModule.REAL_NAME).addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent e) {
                userIdChanged();
            }
        });
        GameModule.getGameModule().getPrefs().getOption(GameModule.SECRET_NAME).addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent e) {
                userIdChanged();
            }
        });
        /*
         * Get the main map
         */
        List<Map> a = GameModule.getGameModule().getAllDescendantComponentsOf(Map.class);
        theMap = a.get(0);
        /*
         * Get the hex size from the main map
         */
        List<BoardPicker> b = theMap.getAllDescendantComponentsOf(BoardPicker.class);
        if (!b.isEmpty()) {
            BoardPicker c = b.get(0);
            String[] boards = c.getAllowableBoardNames();
            if (boards.length != 0) {
                Board d = c.getBoard(boards[0]);
                List<HexGrid> e = d.getAllDescendantComponentsOf(HexGrid.class);
                if (!e.isEmpty()) {
                    HexGrid h = e.get(0);
                    hexSize = h.getDy();
                }
            }
        }
    }

    /*
     * Names of the extra preferences needed for OCS FOW
     */
    public final static String HIDDEN_MOVEMENT_OFF = "OCS-hiddenoff";
    public final static String RANGE_IN_USE = "OCS-userange";
    public final static String ZONE_SECURITY = "OCS-zonesecurity";
    public final static String RANGE_AIR_HIDDEN = "OCS-rangeairhide";
    public final static String RANGE_HIDDEN = "OCS-rangehide";
    public final static String RANGE_CONCEALED = "OCS-rangeconceal";
    public final static String RANGE_FLAT = "OCS-rangeFlat";
    public final static String USE_NEAREST_AEP="OCS-usenearestaep";
    public final static String USE_FORMATIONS = "OCS-useformations";
    public final static String HEX_RANGES = "OCS-usehexranges";

    /*
     * Abstract methods from Configurable which are implemented here
     */
    public Class<?>[] getAllowableConfigureComponents() {
        return new Class[0];
    }

    public HelpFile getHelpFile() {
        return null;
    }

    public void removeFrom(Buildable parent) {
    }

    /*
     * Methods from the GameComponent interface which are implemented here
     */
    /**
     * Notify the GameComponent that a game has started/ended
     * @param gameStarting if true, a game is starting.  If false, then a game is ending
     */
    public void setup(boolean gameStarting) {
        hexZones = null;
        if (!gameStarting) {
            /*
             * Read the preferences for hidden movement. If this is a save game
             * about to be loaded then these will be overridden from the save file
             */
            hiddenMovementOff = Boolean.TRUE.equals(
                    GameModule.getGameModule().getPrefs().getValue(HIDDEN_MOVEMENT_OFF));
            rangeInUse = Boolean.TRUE.equals(
                    GameModule.getGameModule().getPrefs().getValue(RANGE_IN_USE));
            useFormations = Boolean.TRUE.equals(
                    GameModule.getGameModule().getPrefs().getValue(USE_FORMATIONS));
            hexRanges = Boolean.TRUE.equals(
                    GameModule.getGameModule().getPrefs().getValue(HEX_RANGES));
            Object o = GameModule.getGameModule().getPrefs().getValue(ZONE_SECURITY);
            if (o instanceof String) {
                String a = ((String) o);
                if (a.equals("Visible")) {
                    zoneSecurity = OcsCounter.VISIBLE;
                } else if (a.equals("Masked")) {
                    zoneSecurity = OcsCounter.CONCEALED;
                } else if (a.equals("Invisible")) {
                    zoneSecurity = OcsCounter.HIDDEN;
                }
            }
            o = GameModule.getGameModule().getPrefs().getValue(RANGE_AIR_HIDDEN);
            if (o instanceof Integer) {
                hexRangeAirHidden = ((Integer) o).intValue();
                rangeAirHidden = convertHexesToPixels(hexRangeAirHidden);
            }

            o = GameModule.getGameModule().getPrefs().getValue(RANGE_HIDDEN);
            if (o instanceof Integer) {
                hexRangeHidden = ((Integer) o).intValue();
                rangeHidden = convertHexesToPixels(hexRangeHidden);
            }
            o = GameModule.getGameModule().getPrefs().getValue(RANGE_CONCEALED);
            if (o instanceof Integer) {
                hexRangeConcealed = ((Integer) o).intValue();
                rangeConcealed = convertHexesToPixels(hexRangeConcealed);
            }
            o = GameModule.getGameModule().getPrefs().getValue(RANGE_FLAT);
            if (o instanceof Integer) {
                hexRangeFlattened = ((Integer) o).intValue();
                rangeFlattened = convertHexesToPixels(hexRangeFlattened);
            }
            useNearest = Boolean.TRUE.equals(
                    GameModule.getGameModule().getPrefs().getValue(USE_NEAREST_AEP));
            curCommander = null;
            theSides[0].controlled = false;
            theSides[1].controlled = false;
            theCommanders = new Commander[0];
            if (textLaunch != null) {
                textLaunch.setEnabled(false);
            }
            instructions.setEnabled(false);
        } else {
            userIdChanged();
            if (textLaunch != null) {
                textLaunch.setEnabled(true);
            }
            instructions.setEnabled(scenarioInstructions != null);
        }
    }

    /**
     * Read the current value of the preference
     */
    static void readUseNearest() {
            useNearest = Boolean.TRUE.equals(
                    GameModule.getGameModule().getPrefs().getValue(USE_NEAREST_AEP));
    }

    /**
     * When saving a game, each GameComponent should return a {@link
     * Command} that, when executed, restores the GameComponent to its
     * state when the game was saved
     * If this component has no persistent state, return null
     */
    public Command getRestoreCommand() {
        if (GameModule.getGameModule().getArchiveWriter() != null) {
            return new RestoreStaticsScenarioCommand();
        }
        return new RestoreStaticsCommand();
    }

    /*
     * Encode/decode OCS Special commands
     */
    static public final String OCS_RESTORE = "OCS-res\t";
    static public final String OCS_SCENARIO = "OCS-scen\t";
    static public final String OCS_BOARD = "OCS-board\t";
    static public final String OCS_COMMANDER = "OCS-cmd\t";
    static public final String OCS_VIEWS = "OCS-views\t";
    static public final String OCS_NEW_VIEW = "OCS-new-view\t";
    static public final String OCS_MVD_VIEW = "OCS-moved-view\t";

    public String encode(Command c) {
        if (c instanceof MapOverride.RestoreRestrict) {
            MapOverride.RestoreRestrict d = (MapOverride.RestoreRestrict)c;
            SequenceEncoder se = new SequenceEncoder('\t');
            d.appendTo(se);
            return OCS_VIEWS + se.getValue();
        }
        if (c instanceof MapOverride.NewRestrict) {
            MapOverride.NewRestrict d = (MapOverride.NewRestrict)c;
            SequenceEncoder se = new SequenceEncoder('\t');
            d.appendTo(se);
            return OCS_NEW_VIEW + se.getValue();
        }
        if (c instanceof MapOverride.MovedRestrict) {
            MapOverride.MovedRestrict d = (MapOverride.MovedRestrict)c;
            SequenceEncoder se = new SequenceEncoder('\t');
            d.appendTo(se);
            return OCS_MVD_VIEW + se.getValue();
        }
        if (c instanceof RestoreBoardCommand) {
            RestoreBoardCommand d = (RestoreBoardCommand) c;
            SequenceEncoder se = new SequenceEncoder('\t');
            se.append(d.mapName);
            se.append(d.boardName);
            se.append(d.lines.length);
            for (int i = 0; i < d.lines.length; i++) {
                se.append(d.lines[i].text);
                se.append(d.lines[i].font);
                se.append(d.lines[i].height);
                se.append(d.lines[i].y);
                se.append(d.lines[i].width);
            }
            return OCS_BOARD + se.getValue();
        }
        if (c instanceof RestoreStaticsCommand) {
            RestoreStaticsCommand d = (RestoreStaticsCommand) c;
            SequenceEncoder se = new SequenceEncoder('\t');
            se.append(d.hiddenMovementOff);
            se.append(d.rangeInUse);
            se.append(d.zoneSecurity);
            se.append(d.rangeAirHidden);
            se.append(d.rangeHidden);
            se.append(d.rangeConcealed);
            se.append(d.rangeFlattened);
            se.append(d.theCommanders.length);
            for (int i = 0; i < d.theCommanders.length; i++) {
                se.append(d.theCommanders[i].name);
                se.append(d.theCommanders[i].password);
                se.append(d.theCommanders[i].sidesCommanded[0]);
                se.append(d.theCommanders[i].sidesCommanded[1]);
                se.append(d.theCommanders[i].sidesRequested[0]);
                se.append(d.theCommanders[i].sidesRequested[1]);
                se.append(d.theCommanders[i].sidesRejected[0]);
                se.append(d.theCommanders[i].sidesRejected[1]);
                se.append(d.theCommanders[i].sidesAccepted[0]);
                se.append(d.theCommanders[i].sidesAccepted[1]);
            }
            if (d.scenInstr == null) {
                se.append(0);
            } else {
                se.append(d.scenInstr.length);
                for (int i = 0; i < d.scenInstr.length; i++) {
                    se.append(d.scenInstr[i]);
                }
            }
            se.append(d.useFormations);
            se.append(d.hexRanges);
            return OCS_RESTORE + se.getValue();
        }
        if (c instanceof RestoreStaticsScenarioCommand) {
            RestoreStaticsScenarioCommand d = (RestoreStaticsScenarioCommand) c;
            SequenceEncoder se = new SequenceEncoder('\t');
            if (d.scenInstr == null) {
                se.append(0);
            } else {
                se.append(d.scenInstr.length);
                for (int i = 0; i < d.scenInstr.length; i++) {
                    se.append(d.scenInstr[i]);
                }
            }
            return OCS_SCENARIO + se.getValue();
        }
        if (c instanceof CommanderCommand) {
            CommanderCommand d = (CommanderCommand) c;
            SequenceEncoder se = new SequenceEncoder('\t');
            se.append(d.name);
            se.append(d.password);
            se.append(d.command);
            se.append(d.side);
            se.append(d.state);
            return OCS_COMMANDER + se.getValue();
        }
        return null;
    }

    public Command decode(String command) {
        if (command.startsWith(OCS_VIEWS)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            String m = st.nextToken();
            Map n = Map.getMapById(m);
            if (n == null) return null;
            return ((MapOverride)n).createRestoreRestrict(st);
        }
        if (command.startsWith(OCS_NEW_VIEW)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            String m = st.nextToken();
            Map n = Map.getMapById(m);
            return ((MapOverride)n).createNewRestrict(st);
        }
        if (command.startsWith(OCS_MVD_VIEW)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            String m = st.nextToken();
            Map n = Map.getMapById(m);
            return ((MapOverride)n).createMovedRestrict(st);
        }
        if (command.startsWith(OCS_BOARD)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            String m = st.nextToken();
            String b = st.nextToken();
            int k = st.nextInt(0);
            Line[] l;
            l = new Line[k];
            for (int i = 0; i < k; i++) {
                l[i] = new Line();
                l[i].text = st.nextToken();
                l[i].font = st.nextInt(0);
                l[i].height = st.nextInt(32);
                l[i].y = st.nextInt(24);
                l[i].width = st.nextInt(500);
            }
            return new RestoreBoardCommand(m, b, l);
        }
        if (command.startsWith(OCS_RESTORE)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            RestoreStaticsCommand c = new RestoreStaticsCommand(st.nextBoolean(false),
                    st.nextBoolean(false), st.nextInt(OcsCounter.VISIBLE),
                    st.nextInt(40),
                    st.nextInt(20), st.nextInt(10), st.nextInt(5), st.nextInt(0));
            for (int i = 0; i < c.theCommanders.length; i++) {
                c.theCommanders[i] = new Commander(st.nextToken(), st.nextToken());
                c.theCommanders[i].sidesCommanded[0] = st.nextBoolean(false);
                c.theCommanders[i].sidesCommanded[1] = st.nextBoolean(false);
                c.theCommanders[i].sidesRequested[0] = st.nextBoolean(false);
                c.theCommanders[i].sidesRequested[1] = st.nextBoolean(false);
                c.theCommanders[i].sidesRejected[0] = st.nextBoolean(false);
                c.theCommanders[i].sidesRejected[1] = st.nextBoolean(false);
                c.theCommanders[i].sidesAccepted[0] = st.nextBoolean(false);
                c.theCommanders[i].sidesAccepted[1] = st.nextBoolean(false);
            }
            int k = st.nextInt(0);
            if (k != 0) {
                c.scenInstr = new String[k];
                for (int i = 0; i < k; i++) {
                    c.scenInstr[i] = st.nextToken();
                }
            }
            c.useFormations = st.nextBoolean(false);
            c.hexRanges = st.nextBoolean(false);
            return c;
        }
        if (command.startsWith(OCS_SCENARIO)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            RestoreStaticsScenarioCommand c = new RestoreStaticsScenarioCommand(true);
            int k = st.nextInt(0);
            if (k != 0) {
                c.scenInstr = new String[k];
                for (int i = 0; i < k; i++) {
                    c.scenInstr[i] = st.nextToken();
                }
            }
            return c;
        }
        if (command.startsWith(OCS_COMMANDER)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            return new CommanderCommand(st.nextToken(), st.nextToken(),
                    st.nextInt(-1), st.nextInt(0), st.nextBoolean(false));
        }
        if (command.startsWith(PlayerRoster.COMMAND_PREFIX)) {
            SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(command, '\t');
            st.nextToken();
            st.nextToken();
            st.nextToken();
            st.nextToken();
            return new NullCommand();
        }
        return null;
    }

    /*
     * Special methods relating to commanders
     */
    /**
     * This is called whenever the user changes his name and/or password
     */
    void userIdChanged() {
        /*
         * Get the user's name and password
         */
        check++;
        Object o;
        String nam = "test";
        String password = "xyz";
        o = GameModule.getGameModule().getPrefs().getValue(GameModule.REAL_NAME);
        if (o instanceof String) {
            nam = (String) o;
        }
        o = GameModule.getGameModule().getPrefs().getValue(GameModule.SECRET_NAME);
        if (o instanceof String) {
            password = (String) o;
        }
        /*
         * Remove old user
         */
        curCommander = null;
        theSides[0].controlled = false;
        theSides[1].controlled = false;
        /*
         * If special user then controls both sides ( only in debug mode )
         */
        if (this instanceof Debug && nam.equals("Sauron") && password.equals("TheDarkLord")) {
            theSides[0].controlled = true;
            theSides[1].controlled = true;
            return;
        }
        /*
         * Does the new user match an existing user
         */
        int i;
        for (i = 0; i < theCommanders.length; i++) {
            if (theCommanders[i].name.equals(nam)
                    && theCommanders[i].password.equals(password)) {
                theSides[0].controlled = theCommanders[i].sidesCommanded[0];
                theSides[1].controlled = theCommanders[i].sidesCommanded[1];
                curCommander = theCommanders[i];
                for (int j = 0; j < 2; j++) {
                    if (curCommander.sidesRejected[j]) {
                        GameModule.getGameModule().getChatter().show("Sorry but you have been rejected by the "
                                + theSides[j].name + " side");
                        curCommander.sidesRejected[j] = false;
                    } else if (curCommander.sidesAccepted[j]) {
                        GameModule.getGameModule().getChatter().show("You have successfully joined the "
                                + theSides[j].name + " side");
                        curCommander.sidesAccepted[j] = false;
                    }
                }
                return;
            }
        }
        curCommander = new Commander(nam, password);
        addNewCommander(curCommander);
    }

    /**
     * Add a new commander to the list
     */
    static void addNewCommander(Commander c) {
        Commander[] x = new Commander[theCommanders.length + 1];
        if (theCommanders.length != 0) {
            System.arraycopy(theCommanders, 0, x, 0, theCommanders.length);
        }
        x[theCommanders.length] = c;
        theCommanders = x;
        Statics.check++;
        theMap.repaint();
    }

    /**
     * List of hex zones on the current map
     */
    List<OcsHexZone> hexZones;

    /**
     * Build list of hex zones
     */
    void buildHexZoneList() {
        if (hexZones != null) return;
        Collection<Board> bs = theMap.getBoards();
        hexZones = new ArrayList<OcsHexZone>();
        for (Board b : bs) {
            hexZones.addAll(b.getAllDescendantComponentsOf(OcsHexZone.class));
        }
        for (OcsHexZone h : hexZones) {
            h.convertHexNameToPoint();
        }
    }

    /**
     * Returns hex zone if point is a hex shadowed by a hex zone
     */
    OcsHexZone isHexZone(Point p) {
        buildHexZoneList();
        for (OcsHexZone h : hexZones) {
            if (h.theHex != null && p.equals(h.theHex)) return h;
        }
        return null;
    }
}
