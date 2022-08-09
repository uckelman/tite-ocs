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

import VASSAL.build.GameModule;
import VASSAL.build.widget.PieceSlot;
import VASSAL.configure.StringConfigurer;
import VASSAL.configure.StringEnumConfigurer;
import VASSAL.counters.Decorator;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceCloner;
import VASSAL.tools.SequenceEncoder;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
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
import net.cantab.hayward.george.OCS.Counters.Land;
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
import net.cantab.hayward.george.OCS.Counters.Unit;

/**
 * Process pieces to quickly add OCS type and info
 * 
 * @author George Hayward
 */
public class PieceProcessor extends JPanel {
    private static final long serialVersionUID = 1L;

    class imageDisplayA extends JPanel {
        private static final long serialVersionUID = 1L;

        GamePiece pce;

        imageDisplayA(GamePiece p) {
            super();
            pce = p;
            Rectangle r = pce.boundingBox();
            setSize(r.width + 10, r.height + 10);
            validate();
        }

        /**
         * just draw image
         */
        @Override
        public void paint(Graphics g) {
            Rectangle r = pce.boundingBox();
            pce.draw(g, r.width / 2 + 5, r.height / 2 + 5, this, 1.0);
        }
    }

    class imageDisplay extends JPanel {
        private static final long serialVersionUID = 1L;

        imageDisplay(PiecePtr p) {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            JComponent j;
            j = new imageDisplayA(p.piece.getPiece());
            add(j);
            JLabel k = new JLabel(p.name);
            add(k);
            k = new JLabel("-----------(" + curPieceNo + ")------------");
            add(k);
        }
    }
    static private OcsCounter[] types = new OcsCounter[21];
    static String lastFactors = "";
    static String lastDivision = "";
    static String lastSide = "";
    static String lastType = "";

    class textDataDisplay extends JPanel {
        private static final long serialVersionUID = 1L;

        private StringEnumConfigurer typeConfig;
        private StringEnumConfigurer sideConfig;
        private StringConfigurer factorsConfig;
        private StringConfigurer divConfig;
        private PieceSlot slot;

        textDataDisplay(PiecePtr thePiece) {
            super();
            slot = thePiece.piece;
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            JComponent j;
            typeConfig = new StringEnumConfigurer(null,
                    "Pick the type of OCS counter",
                    new String[]{
                        types[0].getDescription(),
                        types[1].getDescription(),
                        types[2].getDescription(),
                        types[3].getDescription(),
                        types[4].getDescription(),
                        types[5].getDescription(),
                        types[6].getDescription(),
                        types[7].getDescription(),
                        types[8].getDescription(),
                        types[9].getDescription(),
                        types[10].getDescription(),
                        types[11].getDescription(),
                        types[12].getDescription(),
                        types[13].getDescription(),
                        types[14].getDescription(),
                        types[15].getDescription(),
                        types[16].getDescription(),
                        types[17].getDescription(),
                        types[18].getDescription(),
                        types[19].getDescription(),
                        types[20].getDescription()});
            sideConfig = new StringEnumConfigurer(null,
                    "Pick the side",
                    new String[]{"No Side",
                        Statics.theSides[0].name,
                        Statics.theSides[1].name});
            factorsConfig = new StringConfigurer(null, "Factors: ", lastFactors);
            divConfig = new StringConfigurer(null, "Division: ", lastDivision);
            GamePiece p = thePiece.piece.getPiece();
            if (p instanceof OcsCounter) {
                OcsCounter q = (OcsCounter) p;
                typeConfig.setValue(q.getDescription());
                if (q instanceof Unit) {
                    if (q.theSide != -1) {
                        sideConfig.setValue(Statics.theSides[q.theSide].name);
                    } else {
                        sideConfig.setValue("No Side");
                    }
                    if (q instanceof Land) {
                        factorsConfig.setValue(q.factors);
                        divConfig.setValue(q.division);
                    } else if ( q instanceof Division || q instanceof Transport) {
                        factorsConfig.setValue("");
                        divConfig.setValue(q.division);
                    } else {
                        factorsConfig.setValue("");
                        divConfig.setValue("");
                    }
                } else {
                    factorsConfig.setValue("");
                    divConfig.setValue("");
                }
            } else if (!lastType.equals("")) {
                typeConfig.setValue(lastType);
                if (!lastSide.equals("")) {
                    sideConfig.setValue(lastSide);
                }
            }
            add(typeConfig.getControls());
            add(sideConfig.getControls());
            add(factorsConfig.getControls());
            add(divConfig.getControls());
        }

        void process() {
            BasicCommandEncoderOverride b = new BasicCommandEncoderOverride();
            String x;
            x = typeConfig.getValueString();
            lastType = x;
            OcsCounter q = null;
            for (int i = 0; i < types.length; i++) {
                if (x.equals(types[i].getDescription())) {
                    q = (OcsCounter) b.createDecorator(types[i].myID(), null);
                    break;
                }
            }
            GamePiece p = slot.getPiece();
            if (p.getClass() != q.getClass()) {
                if (p instanceof OcsCounter) {
                    q.setInner(((Decorator) p).getInner());
                } else {
                    q.setInner(p);
                }
                slot.setPiece(q);
            } else {
                q = (OcsCounter) p;
            }
            if (q instanceof Unit) {
                x = sideConfig.getValueString();
                lastSide = x;
                if (x.equals(Statics.theSides[0].name)) {
                    q.theSide = 0;
                }
                if (x.equals(Statics.theSides[1].name)) {
                    q.theSide = 1;
                }
                if (q instanceof Land) {
                    q.factors = factorsConfig.getValueString();
                    lastFactors = q.factors;
                    q.division = divConfig.getValueString();
                    lastDivision = q.division;
                }
                if (q instanceof Division || q instanceof Transport) {
                    q.division = divConfig.getValueString();
                    lastDivision = q.division;
                }
            }
        }
    }

    class pieceDisplay extends JPanel {
        private static final long serialVersionUID = 1L;

        textDataDisplay t;

        pieceDisplay(PiecePtr thePiece) {
            super();
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            JComponent j;
            EmptyBorder b = new EmptyBorder(10, 10, 10, 10);
            j = new imageDisplay(thePiece);
            j.setBorder(b);
            add(j);
            b = new EmptyBorder(5, 5, 5, 5);
            t = new textDataDisplay(thePiece);
            t.setBorder(b);
            add(t);
        }

        void process() {
            t.process();
        }
    }

    /**
     * The structure of a piece pointer
     */
    static class PiecePtr {

        String name;
        String image;
        PieceSlot piece;
    }
    /**
     * The pieces in the game
     */
    PiecePtr[] thePieces;

    /**
     * Build the Piece Ptr data
     */
    void buildPiecePtrs() {
        List<PieceSlot> t = GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlot.class);
        thePieces = new PiecePtr[t.size()];
        if (t.size() == 0) {
            return;
        }
        int i = 0;
        for (PieceSlot q : t) {
            GamePiece p = PieceCloner.getInstance().clonePiece(q.getPiece());
            GamePiece g = p;
            for (;;) {
                if (!(g instanceof Decorator)) {
                    break;
                }
                g = ((Decorator) g).getInner();
            }
            String s = g.getType();
            final SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(s, ';');
            st.nextToken();
            st.nextChar('\0');
            st.nextChar('\0');
            st.nextToken();
            String pname = st.nextToken();
            thePieces[i] = new PiecePtr();
            thePieces[i].name = pname;
            thePieces[i].piece = q;
            i++;
        }
        types[0] = new AttackCapable();
        types[1] = new Defensive();
        types[2] = new Division();
        types[3] = new Airbase();
        types[4] = new Aircraft();
        types[5] = new Artillery();
        types[6] = new AttackMarker();
        types[7] = new GameMarker();
        types[8] = new HeadQuarters();
        types[9] = new Hedgehog();
        types[10] = new Over();
        types[11] = new OOS();
        types[12] = new Replacement();
        types[13] = new Reserve();
        types[14] = new Ship();
        types[15] = new SupplyMarker();
        types[16] = new Transport();
        types[17] = new Under();
        types[18] = new Leader();
        types[19] = new ReplaceCard();
        types[20] = new Fighter();
    }
    JButton nextButton;
    JButton prevButton;
    JButton findButton;
    pieceDisplay curPiece;
    int curPieceNo;

    PieceProcessor() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        curPieceNo = 0;
        nextButton = new JButton("Next Piece");
        nextButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int j = 1;
                if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
                    j *= 10;
                }
                if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
                    j *= 10;
                }
                nextPiece(j);
            }
        });
        nextButton.setFocusable(false);
        prevButton = new JButton("Previous Piece");
        prevButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                int j = 1;
                if ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
                    j *= 10;
                }
                if ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
                    j *= 10;
                }
                previousPiece(j);
            }
        });
        prevButton.setFocusable(false);
        findButton = new JButton("Find unconverted piece");
        findButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                searchPieces();
            }
        });
        findButton.setFocusable(false);
        add(prevButton);
        add(nextButton);
        add(findButton);
        buildPiecePtrs();
        if (thePieces.length != 0) {
            curPiece = new pieceDisplay(thePieces[0]);
            add(curPiece);
            prevButton.setEnabled(false);
            nextButton.setEnabled(thePieces.length > 1);
        } else {
            prevButton.setEnabled(false);
            nextButton.setEnabled(false);
        }
        validate();
    }

    public void searchPieces() {
        for (int i = 0; i < thePieces.length; i++) {
            GamePiece p = thePieces[i].piece.getPiece();
            if (!(p instanceof OcsCounter)) {
                curPieceNo = i;
                remove(curPiece);
                curPiece = new pieceDisplay(thePieces[curPieceNo]);
                add(curPiece);
                nextButton.setEnabled(thePieces.length > curPieceNo + 1);
                prevButton.setEnabled(curPieceNo > 0);
                validate();
                repaint();
                return;
            }
        }
        findButton.setEnabled(false);
    }

    public void nextPiece(int jump) {
        curPieceNo += jump;
        if (curPieceNo >= thePieces.length) {
            curPieceNo = thePieces.length - 1;
        }
        curPiece.process();
        remove(curPiece);
        curPiece = new pieceDisplay(thePieces[curPieceNo]);
        add(curPiece);
        nextButton.setEnabled(thePieces.length > curPieceNo + 1);
        prevButton.setEnabled(curPieceNo > 0);
        validate();
        repaint();
    }

    public void previousPiece(int jump) {
        curPieceNo -= jump;
        if ( curPieceNo < 0 ) curPieceNo = 0;
        curPiece.process();
        remove(curPiece);
        curPiece = new pieceDisplay(thePieces[curPieceNo]);
        add(curPiece);
        nextButton.setEnabled(thePieces.length > curPieceNo + 1);
        prevButton.setEnabled(curPieceNo > 0);
        validate();
        repaint();
    }
}
