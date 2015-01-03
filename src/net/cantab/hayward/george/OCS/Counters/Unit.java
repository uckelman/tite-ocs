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
package net.cantab.hayward.george.OCS.Counters;

import VASSAL.build.module.BasicCommandEncoder;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.build.module.gamepieceimage.StringEnumConfigurer;
import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceEditor;
import VASSAL.tools.SequenceEncoder;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import net.cantab.hayward.george.OCS.OcsCounter;
import net.cantab.hayward.george.OCS.Statics;

/**
 *
 * @author George Hayward
 */
public abstract class Unit extends OcsCounter implements EditablePiece {

    /**
     * Constructor
     */
    Unit(String type, GamePiece p) {
        super(p);
        setSecurity(VISIBLE);
        SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(type, ';');
        st.nextToken();
        theSide = st.nextInt(-1);
    }

    /** The type information is information that does not change
     * during the course of a game.  All this information has been moved into
     * the @see PieceData class so it is just necessary to save which one this
     * piece uses.
     * @see BasicCommandEncoder */
    @Override
    public String myGetType() {
        return myID() + theSide;
    }

    /*
     * The methods needed by the EditablePiece interface
     */
     /** A plain-English description of this type of piece */

    /** Set the type information for this piece.  See {@link Decorator#myGetType} */
    public void mySetType(String type) {
        SequenceEncoder.Decoder st = new SequenceEncoder.Decoder(type, ';');
        st.nextToken();
        theSide = st.nextInt(-1);
    }

    /** Get the configurer for this trait */
    @Override
    public PieceEditor getEditor() {
        return new Ed();
    }

    public HelpFile getHelpFile() {
        return null;
    }

    public class Ed implements PieceEditor {

        private StringEnumConfigurer sideConfig;
        private int aSide;
        private JPanel panel;

        public Ed() {
            sideConfig = new StringEnumConfigurer(null,
                    "Pick the side",
                    new String[]{Statics.theSides[0].name,
                        Statics.theSides[1].name});
            if ( theSide != -1 )
                sideConfig.setValue(Statics.theSides[theSide].name);

            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            panel.add(sideConfig.getControls());

        }

        public Component getControls() {
            return panel;
        }

        public String getType() {
            aSide = -1;
            String x = sideConfig.getValueString();
            if (x.equals(Statics.theSides[0].name)) {
                aSide = 0;
            }
            if (x.equals(Statics.theSides[1].name)) {
                aSide = 1;
            }
            return myID() + aSide;
        }

        public String getState() {
            return "";
        }
    }
}
