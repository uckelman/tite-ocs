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

import VASSAL.build.GameModule;
import VASSAL.build.module.BasicCommandEncoder;
import VASSAL.command.AddPiece;
import VASSAL.command.ChangePiece;
import VASSAL.command.Command;
import VASSAL.command.MovePiece;
import VASSAL.command.NullCommand;
import VASSAL.command.PlayAudioClipCommand;
import VASSAL.command.RemovePiece;
import VASSAL.counters.Decorator;
import VASSAL.counters.Embellishment;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Obscurable;
import VASSAL.counters.Stack;
import VASSAL.tools.SequenceEncoder;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
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
 *
 * @author George Hayward
 */
public class BasicCommandEncoderOverride extends BasicCommandEncoder {

    private Map<String, DecoratorFactory> decoratorFactoriesa =
            new HashMap<String, DecoratorFactory>();

    public BasicCommandEncoderOverride() {
        super();
        decoratorFactoriesa.put(Airbase.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Airbase(type, inner);
            }
        });
        decoratorFactoriesa.put(Aircraft.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Aircraft(type, inner);
            }
        });
        decoratorFactoriesa.put(Fighter.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Fighter(type, inner);
            }
        });
        decoratorFactoriesa.put(Aircraft.oldID, new DecoratorFactory() {

            @Override
            public Decorator createDecorator(String type, GamePiece inner) {
                if (inner != null) {
                    String n = inner.getName();
                    for(int i = 0; i < fighters.length; i++) {
                        if (n.equals(fighters[i])) {
                            return new Fighter(type, inner);
                        }
                    }
                }
                return new Aircraft(type, inner);
            }
            
            public String[] fighters = {
                /* Baltic Gap */
                "La-5", "Yak-9", "Gd La-5", "Gd Yak-9", "Bf-109", "Bf-109 (inexperienced)", "Fw-190", "Fw-190 (inexperienced)", "Me-262",
                /* case blue */
                "Bf.109e", "Bf.109e7", "Bf.109f", "Bf.109g", "Bf.110", "Fw.190a", "Fw.190a5", "Hs.123",
                "Hurri.iic MMD", "Hurri.IIc", "I.15", "I.16 MMD", "I.16", "La-5 (Gd)", "La.5 MMD", "La.5 (Gd)",
                "LaGG.3 (Gd)", "LaGG.3", "LaGG.3 MMD", "MiG.1", "MiG.3 MMD", "MiG.3",
                "P.39 (Gd)", "P.39", "P.40 MMD", "P.40", "Spit.V", "Yak-1 (Gd)", "Yak-1 MMD", "Yak-1", "Yak-7b (Gd)", "Yak-7b MMD", "Yak-7b",
                "Yak-9 (Gd)", "Yak-9 MMD", "Yak-9", "Hun Bf.109f", "Hun Bf.109g", "Hun CR.42", "Hun Re.2000", "It MC.200", "It MC.202",
                "Croat Bf.109g", "Slovak B.534", "Slovak Bf.109e", "Rum Bf.109e", "Slovak Bf.109f",
                /* DAk */
                "Gladiator", "Hurri1", "HurriI", "HurriIIa", "HurriIIc", "HurriIId", "Ktyhk", "SpitVb", "P-40",
                "FliegerKorpsBf.110", "G.50", "MC.202", "MC.200", "CR.42",
                /* Korea */
                "Chinese La-9", "Chinese MiG-15","Chinese Yak-9", "NK La-9", "NK MiG-15", "NK Yak Mix", "Russian MiG-15",  "Russian MiG-15b",
                "US F-4U", "US F-9f", "US F-51", "US F-80c", "US F-84", "US F-86e", "CW Mx",
                /* Tunisia */
                "DAF Hurri. II", "Hurri. II", "DAF Ktyhk. I", "DAF Ktyhk. II", "Me.109f", "Me.109g", "Me.110f", "US P-38", "US P-39", "US DAF P-40",
                "US P-40", "DAF Spit. Vb", "Spit. Vb", "US Spit. Vb"
            };
        });
        decoratorFactoriesa.put(AttackCapable.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new AttackCapable(type, inner);
            }
        });
        decoratorFactoriesa.put(AttackMarker.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new AttackMarker(type, inner);
            }
        });
        decoratorFactoriesa.put(Artillery.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Artillery(type, inner);
            }
        });
        decoratorFactoriesa.put(Defensive.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Defensive(type, inner);
            }
        });
        decoratorFactoriesa.put(Division.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Division(type, inner);
            }
        });
        decoratorFactoriesa.put(GameMarker.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new GameMarker(type, inner);
            }
        });
        decoratorFactoriesa.put(HeadQuarters.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new HeadQuarters(type, inner);
            }
        });
        decoratorFactoriesa.put(Hedgehog.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Hedgehog(type, inner);
            }
        });
        decoratorFactoriesa.put(Over.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Over(type, inner);
            }
        });
        decoratorFactoriesa.put(Replacement.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Replacement(type, inner);
            }
        });
        decoratorFactoriesa.put(Reserve.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Reserve(type, inner);
            }
        });
        decoratorFactoriesa.put(Ship.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Ship(type, inner);
            }
        });
        decoratorFactoriesa.put(SupplyMarker.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new SupplyMarker(type, inner);
            }
        });
        decoratorFactoriesa.put(Transport.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Transport(type, inner);
            }
        });
        decoratorFactoriesa.put(Under.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Under(type, inner);
            }
        });
        decoratorFactoriesa.put(Obscurable.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new ObscurableOverride(type, inner);
            }
        });
        decoratorFactoriesa.put(ReplaceCard.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new ReplaceCard(type, inner);
            }
        });
        decoratorFactoriesa.put(Leader.ID, new DecoratorFactory() {

            public Decorator createDecorator(String type, GamePiece inner) {
                return new Leader(type, inner);
            }
        });
    }

    @Override
    public Decorator createDecorator(String type, GamePiece inner) {
        Decorator d = null;
        String prefix = type.substring(0, type.indexOf(';') + 1);
        if (prefix.length() == 0) {
            prefix = type;
        }
        DecoratorFactory f = decoratorFactoriesa.get(prefix);
        if (f != null) {
            return f.createDecorator(type, inner);
        }
        return super.createDecorator(type, inner);
    }

    @Override
    protected GamePiece createBasic(String type) {
        GamePiece p = null;
        String prefix = type.substring(0, type.indexOf(';') + 1);
        if (prefix.length() == 0) {
            prefix = type;
        }
        if (prefix.equals(Stack.TYPE)) {
            return new StackOverride();
        }
        return super.createBasic(type);
    }

    private String unwrapNull(String s) {
        return "null".equals(s) ? null : s; //$NON-NLS-1$
    }

    private static final char PARAM_SEPARATOR = '/';

    @Override
    public Command decode(String command) {
        if (command.length() == 0) {
            return new NullCommand();
        }
        SequenceEncoder.Decoder st;
        if (command.startsWith(ADD)) {
            command = command.substring(ADD.length());
            st = new SequenceEncoder.Decoder(command, PARAM_SEPARATOR);
            String id = unwrapNull(st.nextToken());
            String type = st.nextToken();
            String state = st.nextToken();
            GamePiece p = createPiece(type);
            if ( !(p instanceof OcsCounter) && !(p instanceof StackOverride)) {
                OcsCounter q = Statics.convertOldPiece(p);
                if ( q != null ) {
                    p = q;
                    SequenceEncoder sb = new SequenceEncoder('\t');
                    sb.append(-1);
                    sb.append(state);
                    state = sb.getValue();
                    if ( q instanceof Transport || q instanceof SupplyMarker
                            || q instanceof Unit) {
                        q.setState(state);
                        convertSpecial(q);
                        state = p.getState();
                    }
                }
            }
            if (p == null) {
                return null;
            } else {
                p.setId(id);
                return new AddPiece(p, state);
            }
        } else if (command.startsWith(REMOVE)) {
            String id = command.substring(REMOVE.length());
            GamePiece target = GameModule.getGameModule().getGameState().getPieceForId(id);
            if (target == null) {
                return new RemovePiece(id);
            } else {
                return new RemovePiece(target);
            }
        } else if (command.startsWith(CHANGE)) {
            command = command.substring(CHANGE.length());
            st = new SequenceEncoder.Decoder(command, PARAM_SEPARATOR);
            String id = st.nextToken();
            String newState = st.nextToken();
            String oldState = st.hasMoreTokens() ? st.nextToken() : null;
            return new ChangePiece(id, oldState, newState);
        } else if (command.startsWith(MOVE)) {
            command = command.substring(MOVE.length());
            st = new SequenceEncoder.Decoder(command, PARAM_SEPARATOR);
            String id = unwrapNull(st.nextToken());
            String newMapId = unwrapNull(st.nextToken());
            int newX = Integer.parseInt(st.nextToken());
            int newY = Integer.parseInt(st.nextToken());
            String newUnderId = unwrapNull(st.nextToken());
            String oldMapId = unwrapNull(st.nextToken());
            int oldX = Integer.parseInt(st.nextToken());
            int oldY = Integer.parseInt(st.nextToken());
            String oldUnderId = unwrapNull(st.nextToken());
            String playerid = st.nextToken(GameModule.getUserId());
            return new MovePiece(id, newMapId, new Point(newX, newY), newUnderId, oldMapId, new Point(oldX, oldY), oldUnderId, playerid);
        } else {
            return PlayAudioClipCommand.decode(command);
        }
    }

    void convertSpecial( OcsCounter old ) {
        if ( old instanceof SupplyMarker && !Statics.theStatics.isCaseBlue()) {
            GamePiece q = Statics.lastConverted;
            for (;;) {
                if ( !(q instanceof Decorator)) return;
                if ( q instanceof ObscurableOverride ) break;
                q = ((Decorator)q).getInner();
            }
            GamePiece t = ((Decorator)q).getInner();
            GamePiece p = old;
            GamePiece r = null;
            for (;;) {
                if ( !(p instanceof Decorator)) return;
                if ( p instanceof ObscurableOverride) break;
                if ( p.getClass() == t.getClass()) break;
                r = p;
                p = ((Decorator)p).getInner();
            }
            if ( r == null ) return;
            if (p instanceof ObscurableOverride ) {
                p = ((Decorator)p).getInner();
            }
            GamePiece s = new ObscurableOverride( ((Decorator)q).myGetType(), p);
            ((Decorator)r).setInner(s);
        } else if ( Statics.theStatics.isBalticGap() && old instanceof Transport ) {
            GamePiece t = old.getInner();
            if ( !(t instanceof Embellishment)) return;
            GamePiece p = old;
            GamePiece r = null;
            for (;;) {
                if ( !(p instanceof Decorator)) break;
                r = p;
                p = ((Decorator)p).getInner();
            }
            if ( r == null ) return;
            GamePiece s = ((Decorator)t).getInner();
            old.setInner(s);
            ((Decorator)t).setInner(p);
            ((Decorator)r).setInner(t);
        } else if ( Statics.theStatics.isKorea() && old instanceof Unit ) {
            GamePiece p = old;
            GamePiece q;
            for (;;) {
                q = ((Decorator)p).getInner();
                if ( !(q instanceof Decorator)) return;
                if ( q instanceof Embellishment) break;
                p = q;
            }
            GamePiece r = ((Decorator)q).getInner();
            if (!(r instanceof Decorator)) return;
            ((Decorator)p).setInner(r);
            GamePiece s;
            for (;;) {
                s = ((Decorator)r).getInner();
                if ( !(s instanceof Decorator)) break;
                r = s;
            }
            ((Decorator)r).setInner(q);
            ((Decorator)q).setInner(s);
       }
    }
}
