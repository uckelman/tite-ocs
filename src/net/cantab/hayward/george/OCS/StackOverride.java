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

import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.HexGrid;
import VASSAL.build.module.map.boardPicker.board.MapGrid;
import VASSAL.build.module.map.boardPicker.board.ZonedGrid;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.counters.Decorator;
import VASSAL.counters.Embellishment;
import VASSAL.counters.GamePiece;
import VASSAL.counters.Stack;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.KeyStroke;
import net.cantab.hayward.george.OCS.Counters.Airbase;
import net.cantab.hayward.george.OCS.Counters.Aircraft;
import net.cantab.hayward.george.OCS.Counters.AttackCapable;
import net.cantab.hayward.george.OCS.Counters.AttackMarker;
import net.cantab.hayward.george.OCS.Counters.Controlled;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.Counters.Fighter;
import net.cantab.hayward.george.OCS.Counters.GameMarker;
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
 *
 * @author George Hayward
 */
public class StackOverride extends Stack {

    public StackOverride() {
        super();
        Statics.check++;
    }

    public StackOverride(GamePiece p) {
        super(p);
        Statics.check++;
    }

    @Override
    public void setPosition(Point p) {
        super.setPosition(p);
        Statics.check++;
    }

    @Override
    public void remove(GamePiece p) {
        super.remove(p);
        Statics.check++;
    }

    @Override
    public void add(GamePiece c) {
        checkPieceAdded(c);
        super.add(c);
        Statics.check++;
    }

    @Override
    public void insertChild(GamePiece child, int index) {
        checkPieceAdded(child);
        super.insertChild(child, index);
        Statics.check++;
    }

    @Override
    public void insert(GamePiece p, int pos) {
        checkPieceAdded(p);
        super.insert(p, pos);
        Statics.check++;
    }
    /**
     * When the last check was made. This is compared to the value held within
     * Statics to determinhe if the check is still valid.
     */
    int lastCheck = 0;

    /**
     * For Baltic Gap on the main map replace any cards drawn from the garrison
     * pool by the correct piece
     */
    void replaceCards() {
        if (map != Statics.theMap) return;
        for (int i = 0; i < pieceCount; i++) {
            if (contents[i] instanceof ReplaceCard) {
                contents[i].keyEvent(KeyStroke.getKeyStroke('R', InputEvent.CTRL_DOWN_MASK));
                replaceCards();
                break;
            }
        }
    }

    /**
     * Make all pieces in a stack accessible
     */
    void makeAllAccessible() {
        for (GamePiece p : contents) {
            if (p instanceof OcsCounter) {
                ((OcsCounter) p).setSecurity(OcsCounter.CHANGE);
            }
        }
    }

    /**
     * Check for an Attack Marker in the stack and if present, make every piece
     * accessible
     *
     * @return true if an Attack Marker found.
     */
    boolean isAttackMarkerPresent() {
        for (GamePiece p : contents) {
            if (p instanceof AttackMarker) {
                makeAllAccessible();
                return true;
            }
        }
        return false;
    }

    /**
     * Determine if the given unit is an attack capable unit in COMBAT MODE.
     * First Embellishment nearest the BasicPiece is found which determines
     * which side of the piece is shown. For Dak, Tunisia, Baltic Gap and Korea
     * this is active if the unit is in MOVE MODE. For Case Blue it is always
     * active with the value being non-zero for MOVE MODE.
     */
    boolean isCombatModeAttackCapable(Land u) {
        if (!(u instanceof AttackCapable)) return false;
        Embellishment e = null;
        GamePiece p;
        for (p = u; p instanceof Decorator; p = ((Decorator) p).getInner()) {
            if (p instanceof Embellishment) {
                e = (Embellishment) p;
            }
        }
        if (e == null) return false;
        if (Statics.theStatics.isCaseBlue()) {
            if (e.getValue() != 0) return false;
        } else {
            if (e.isActive()) return false;
        }
        return true;
    }

    /**
     * Compute the visibility of all the pieces in a 'stack'. Any Attack Markers
     * have been resolved before this point. <p> There are four security levels:
     * <p> {@code HIDDEN} - which means the counter is invisible <p>
     * {@code CONCEALED} - which means the counter is visible but displayed as a
     * special mask which conceals anything about the piece <p> {@code VISIBLE}
     * - which means the piece can be seen but not changed or moved <p>
     * {@code CHANGE} - which means the player can move, flip or do anything
     * else with the counter. <P> A piece which can relate to a side either
     * belongs to a side which the player controls when it is said to be
     * friendly or it doesn't belong to such a side when it is said to be
     * hostile <p> The rules are thus: <p> 1) Any Controlled or Unit which is
     * friendly and any Game Marker becomes {@code CHANGE} <p> All Controlled
     * and Units are now considered hostile <p> 2) Any Airbase or Hedgehog
     * becomes {@code VISIBLE} <p> 3) Any SupplyMarker which is above a hostile
     * transport becomes {@code HIDDEN} <p> 4) Other supply markers become
     * {@code HIDDEN} except the lowest which becomes '{@code sec}'. <p> 5)
     * Replacements and Transports become {@code HIDDEN} and '{@code sec}'
     * respectively except if there is nothing else {@code VISIBLE} in the stack
     * the top one becomes {@code VISIBLE} <p> 6) Aircraft which are above any
     * Airbase becomes {@code VISIBLE} whilst those below the Airbase become
     * {@code HIDDEN} <p> 7) Ships become {@code VISIBLE} <p> 8) Leaders become
     * {@code HIDDEN} <p> 9) The topmost COMBAT MODE AttackCapable becomes
     * {@code VISIBLE}. If there is no AttackCapable unit then the topmost Land
     * Unit becomes {@code VISIBLE}. Any other Land Units become value of
     * '{@code sec}'. <p> 10) Any Under Markers become {@code HIDDEN}. <p> 11)
     * Any Over Markers or Reserves become {@code HIDDEN} unless they are over a
     * {@code VISIBLE} hostile Land unit when they become {@code VISIBLE} <p>
     * 12) Any Division Counters are invisible unless the option to use 13.7 is
     * in effect when they take on the most visible attribute of a member of the
     * division below them and all members of the division below them become
     * {@code HIDDEN}.
     *
     * @param sec - security to be applied to those hostile pieces which are not
     * visible
     */
    public void stackVisibility(int sec) {
        /*
         * The first task is to determine which pieces are friendly and to copy
         * them all to an array of OcsCounter to make the code more readable. For Units
         * and Controlled pieces this is determined by whether their side is under
         * the players control or not. GameMarkers are always friendly.
         *
         * Over markers are friendly unless the first Land piece underneath them
         * is hostile.
         *
         * Under markers are friendly unless the first land Piece above them is
         * hostile.
         */
        int i;
        boolean[] friendly = new boolean[pieceCount];
        OcsCounter[] pieces = new OcsCounter[pieceCount];
        for (i = 0; i < pieceCount; i++) {
            if (contents[i] instanceof OcsCounter) {
                pieces[i] = (OcsCounter) contents[i];
                if (pieces[i] instanceof Controlled || pieces[i] instanceof Unit) {
                    friendly[i] = pieces[i].theSide == -1
                                  || Statics.theSides[pieces[i].theSide].controlled;
                } else {
                    // All Markers are friendly to start with
                    friendly[i] = true;
                }
            } else {
                pieces[i] = new GameMarker();
                friendly[i] = true;
            }
        }
        // Now adjust any Under or Over markers
        for (i = 0; i < pieceCount; i++) {
            if (pieces[i] instanceof Over) {
                for (int j = i - 1; j >= 0; j--) {
                    if (pieces[j] instanceof Land) {
                        friendly[i] = friendly[j];
                        break;
                    }
                }
            } else if (pieces[i] instanceof Under) {
                for (int j = i + 1; j < pieceCount; j++) {
                    if (pieces[j] instanceof Land) {
                        friendly[i] = friendly[j];
                        break;
                    }
                }
            }
        }
        /*
         * Now do the main work
         */
        boolean base = false;
        // This is true if a hostile airbase has been found higher up the stack
        OcsCounter topTrans = null;
        // This is the top transport or replacement in the stack
        Land fnd = null;
        // This is the current Land unit to be displayed
        boolean divFound = false;
        // True if a division counter has been found
        boolean supplyPresent = false;
        // True if any supply counters have been found
        for (i = pieceCount - 1; i >= 0; i--) {
            OcsCounter b = pieces[i]; // For convenience
            // Apply Rule (1)
            if (friendly[i]) {
                b.setSecurity(OcsCounter.CHANGE);
            } // Now everything is hostile
            // Apply rule (2) and note presence of Airbase
            else if (b instanceof Airbase) {
                base = true;
                b.setSecurity(OcsCounter.VISIBLE);
            } else if (b instanceof Hedgehog) {
                b.setSecurity(OcsCounter.VISIBLE);
            } // Appply rule (6)
            else if (b instanceof Aircraft) {
                b.setSecurity(base ? OcsCounter.HIDDEN : OcsCounter.VISIBLE);
            } // Apply rule (9)
            else if (b instanceof Land) { //(9)
                Land c = (Land) b;
                // Make the security 'sec' by default. We'll adjust at the end
                // of the loop when we know which unit to make visible
                c.setSecurity(sec);
                if (fnd == null) {
                    fnd = c;
                } else if (!isCombatModeAttackCapable(fnd)
                           && isCombatModeAttackCapable(c)) {
                    fnd = c;
                }
            } // Apply rule (5). Keep track of topmost replacement or transport
            // in case no Land units in hex. Prefer tansport to replacement
            else if (b instanceof Replacement || b instanceof Transport) {
                b.setSecurity((b instanceof Transport) ? sec : OcsCounter.HIDDEN);
                if (topTrans == null
                    || (b instanceof Transport && topTrans instanceof Replacement)) {
                    topTrans = b;
                }
            } // Apply rule (8)
            else if (b instanceof Leader) {
                b.setSecurity(OcsCounter.HIDDEN);
            } // Apply rule (7)
            else if (b instanceof Ship) {
                b.setSecurity(OcsCounter.VISIBLE);
            } // Apply Rule (10)
            else if (b instanceof Under) {
                b.setSecurity(OcsCounter.HIDDEN);
            } // Apply Rule (11)
            else if (b instanceof Over || b instanceof Reserve) {
                b.setSecurity(OcsCounter.HIDDEN);
            } // Apply Rule (12) assuming 13.7 not in force
            else if (b instanceof Division) {
                b.setSecurity(OcsCounter.HIDDEN);
                divFound = true;
            } // Just mark supply counters as sec for now and
            else if (b instanceof SupplyMarker) {
                supplyPresent = true;
                b.setSecurity(sec);
            } // Should never reach here
            else {
                b.setSecurity(OcsCounter.CHANGE);
            }
        }
        /*
         * Adjust top combat unit or transport/replacement if no combat units present
         */
        if (fnd != null) {
            fnd.setSecurity(OcsCounter.VISIBLE);
        } else if (topTrans != null) {
            topTrans.setSecurity(OcsCounter.VISIBLE);
        }
        /*
         * Adjust supply markers (only if they are concealed since if they are
         * hidden there's nothing to do
         */
        if (supplyPresent && sec == OcsCounter.CONCEALED) {
            boolean supplyFound = false;
            for (i = 0; i < pieceCount; i++) {
                if (friendly[i]) continue;
                if (pieces[i] instanceof SupplyMarker) {
                    // Apply rule (3)
                    // Notice check if transport is hostile
                    if (i > 0 && !friendly[i - 1] && pieces[i - 1] instanceof Transport) {
                        pieces[i].setSecurity(OcsCounter.HIDDEN);
                    } // Apply rule (4)
                    else {
                        if (supplyFound) {
                            pieces[i].setSecurity(OcsCounter.HIDDEN);
                        } else {
                            supplyFound = true;
                        }
                    }
                }
            }
        }
        /*
         * Now adjust the security level of any over counters
         */
        boolean overDone = false;
        for (i = 0; i < pieceCount; i++) { // (11)
            if (fnd != null) {
                if (fnd == pieces[i]) fnd = null;
            } else {
                if (friendly[i]) continue;
                OcsCounter b = pieces[i];
                if (!(b instanceof Over) && !(b instanceof Reserve)) continue;
                if (!(b instanceof OOS)) {
                    if (overDone) continue;
                    overDone = true;
                }
                b.setSecurity(OcsCounter.VISIBLE);
            }
        }
        /*
         * Now adjust any division markers if that option in play
         */
        if (divFound && Statics.useFormations) {
            for (i = pieceCount - 1; i >= 1; i--) {
                if (friendly[i]) continue;
                OcsCounter b = pieces[i];
                if (!(b instanceof Division) || b.division == null) continue;
                for (int j = i - 1; j >= 0; j--) {
                    if (friendly[j]) continue;
                    OcsCounter c = ((OcsCounter) contents[j]);
                    if (!(c instanceof Land) || c.division == null) continue;
                    if (c.division.equals(b.division)) {
                        if (c.security == OcsCounter.VISIBLE) {
                            b.setSecurity(OcsCounter.VISIBLE);
                            c.setSecurity(OcsCounter.HIDDEN);
                        } else {
                            if (b.security != OcsCounter.VISIBLE)
                                b.setSecurity(c.security);
                            c.setSecurity(OcsCounter.HIDDEN);
                        }
                    }
                }
            }
        }
        /*
         * Now turn on ZOCs for all attack capable units
         */
        for (i = pieceCount - 1; i >= 0; i--) {
            if (!(pieces[i] instanceof AttackCapable)) continue;
            AttackCapable a = (AttackCapable) pieces[i];
            a.hasZOC = a.security >= OcsCounter.VISIBLE && isCombatModeAttackCapable(a);
        }
        /*
         * Now turn on PZs for all airbases
         */
        for (int j = 0; j < 2; j++) {
            boolean fighterFound = false;
            for (i = pieceCount - 1; i >= 0; i--) {
                if (pieces[i].theSide != j) continue;
                if (pieces[i] instanceof Airbase) {
                    if (Statics.theStatics.isBlitzkriegLegend() && pieces[i].getName().equals("Flotilla")) {
                        ((Airbase) pieces[i]).hasPZ = false;
                        GamePiece[] allPieces = map.getAllPieces();
                        for (int k = 0; k < allPieces.length; ++k) {
                            if (!(allPieces[k] instanceof StackOverride))
                                continue;
                            if (!((StackOverride)allPieces[k]).hasSpitfire()) continue;
                            Point pt = allPieces[k].getPosition();
                            Zone z = map.findZone(new Point(pt));
                            if (z.getName().equals("England.Active")) {
                                ((Airbase) pieces[i]).hasPZ = true;
                                break;
                            }
                        }
                    } else {
                        ((Airbase) pieces[i]).hasPZ = fighterFound;
                    }
                } else if (pieces[i] instanceof Fighter) {
                    fighterFound = true;
                }
            }
        }
    }
    
    /**
     * Special function only used within The Blitzkrieg legend. Returns true
     * if there is a Spitfire in the stack
     */
    boolean hasSpitfire() {
        for (int i = 0; i < pieceCount; i++) {
            if (!(contents[i] instanceof Fighter)) continue;
            if (contents[i].getName().equals("Spit.I")) return true;
        }
        return false;
    }

    /**
     * Change the control of any {@code Controlled} unist in the stack if all
     * the {@code Land} units in the stack belong to one side
     */
    void changeControl() {
        int control = -1;
        for (GamePiece p : contents) {
            if (!(p instanceof Land)) continue;
            Land b = (Land) p;
            if (control == -1) {
                control = b.theSide;
            } // If hex has both sides in it then do nothing
            else if (b.theSide != control) return;
        }
        if (control >= 0) {
            for (GamePiece p : contents) {
                if (!(p instanceof Controlled)) continue;
                ((Controlled) p).theSide = control;
            }
            return;
        }
        // If no controller check for replacements/transports but only for
        // supply units
        for (GamePiece p : contents) {
            if (!(p instanceof Transport) && !(p instanceof Replacement))
                continue;
            OcsCounter b = (OcsCounter) p;
            if (control == -1) {
                control = b.theSide;
            } // If hex has both sides in it then do nothing
            else if (b.theSide != control) return;
        }
        if (control >= 0) {
            for (GamePiece p : contents) {
                if (!(p instanceof SupplyMarker)) continue;
                ((Controlled) p).theSide = control;
            }
        }
    }

    /**
     * Find the side which is in this hex
     *
     * @return the side which is this hex, -1 means neither side and -2 means
     * both sides
     */
    int sideInHex() {
        int control = -1;
        for (GamePiece p : contents) {
            if (!(p instanceof OcsCounter)) continue;
            int j = ((OcsCounter) p).theSide;
            if (j < 0) continue;
            if (control < 0) {
                control = j;
                continue;
            }
            if (control != j) return -2;
        }
        return control;
    }

    /**
     * Work out any range effects and return the security of hostile pieces
     * which are not visible. If the range is so great that the pieces are
     * totally hidden with the possible exception of aircraft and ships then it
     * applies the result. This assumes that the stack involved is on the main
     * map and not inside a {@code Zone}.
     *
     * @returns the appropriate security level or -1 if it has applied the
     * correct range effects
     */
    int hasRangeEffect() {
        if (!Statics.rangeInUse) return OcsCounter.CONCEALED;
        int side = sideInHex();
        if (side < 0) {
            return Statics.hexRangeFlattened == 0 ? OcsCounter.HIDDEN
                   : OcsCounter.CONCEALED;
        }
        /*
         * First we find the closest hex containing pieces of the other side
         * to the ones in this hex (given by side). It is done this way
         * rather than finding closest friendly hex so that observers see
         * something (since they have no friendly units) - they see that
         * part of each side an opponent would see.
         */
        side = (side == 0) ? 1 : 0;
        int range = -1;
        MapGrid ranger = null;
        if (Statics.hexRanges) {
            Board b = map.findBoard(new Point(pos));
            if (b != null) {
                ranger = b.getGrid();
                if (ranger != null && ranger instanceof ZonedGrid) {
                    ZonedGrid z = (ZonedGrid) ranger;
                    Point r = new Point(pos);
                    Rectangle s = b.bounds();
                    r.translate(-s.x, -s.y);
                    Zone y = z.findZone(r);
                    if (y == null) {
                        ranger = z.getBackgroundGrid();
                    } else {
                        ranger = y.getGrid();
                    }
                }
            }
            if (ranger == null || !(ranger instanceof HexGrid))
                return Statics.hexRangeFlattened == 0 ? OcsCounter.HIDDEN
                       : OcsCounter.CONCEALED;
        }
        GamePiece[] allPieces = map.getAllPieces();
        for (int i = 0; i < allPieces.length; ++i) {
            if (!(allPieces[i] instanceof StackOverride)) continue;
            if (!((StackOverride) allPieces[i]).isFriendlyTo(side)) continue;
            Point pt = allPieces[i].getPosition();
            Zone z = map.findZone(new Point(pt));
            if (z != null && z instanceof OcsHexZone && ((OcsHexZone) z).theHex != null) {
                pt = ((OcsHexZone) z).theHex;
            } else if (z != null && !(z.getGrid() instanceof HexGrid)) continue;
            if (range < 0) {
                range = Statics.hexRanges ? ranger.range(pos, pt)
                        : (int) pos.distance(pt);
                continue;
            }
            int y = Statics.hexRanges ? ranger.range(pos, pt)
                    : (int) pos.distance(pt);
            if (y < range) {
                range = y;
            }
        }
        if (range < 0) {
            // No friendly units on the map!!!
            applyAirHidden();
            return -1;
        }
        if (range > (Statics.hexRanges ? Statics.hexRangeAirHidden
                     : Statics.rangeAirHidden)) {
            applyAirHidden();
            return -1;
        }
        if (range > (Statics.hexRanges ? Statics.hexRangeHidden
                     : Statics.rangeHidden)) {
            applyHidden();
            return -1;
        }
        if (range > (Statics.hexRanges ? Statics.hexRangeConcealed
                     : Statics.rangeConcealed)) {
            applyConcealed(range > (Statics.hexRanges ? Statics.hexRangeFlattened
                                    : Statics.rangeFlattened));
            return -1;
        }
        return (range > (Statics.hexRanges ? Statics.hexRangeFlattened
                         : Statics.rangeFlattened)) ? OcsCounter.HIDDEN
               : OcsCounter.CONCEALED;
    }

    /**
     * See if hex is friendly to a given side. Aircraft, Ships Reserve Markers,
     * Division Markers and Leaders don't count and so will not be used in the
     * range calculations. Replacements, Airbases, Hedgehogs and Supply Markers
     * do dount (on the assumption they all represent some personnel on the
     * ground)
     *
     * @return true if it is friendly to that side
     */
    boolean isFriendlyTo(int aSide) {
        int i;
        for (i = 0; i < pieceCount; i++) {
            if (!(contents[i] instanceof OcsCounter)) continue;
            OcsCounter b = ((OcsCounter) contents[i]);
            if (b instanceof Aircraft) continue;
            if (b instanceof Ship) continue;
            if (b instanceof Reserve) continue;
            if (b instanceof Division) continue;
            if (b instanceof Leader) continue;
            if (b.theSide == aSide) return true;
        }
        return false;
    }

    /**
     * Apply Air Hidden Rules to hex. Everything in the hex is hidden except for
     * pieces controlled by the player.
     *
     * @see #isFriendlyTo
     */
    void applyAirHidden() {
        for (GamePiece p : contents) {
            if (!(p instanceof OcsCounter)) continue;
            if (((OcsCounter) p).theSide == -1
                || Statics.theSides[((OcsCounter) p).theSide].controlled) {
                ((OcsCounter) p).setSecurity(OcsCounter.CHANGE);
            } else {
                ((OcsCounter) p).setSecurity(OcsCounter.HIDDEN);
            }
        }
    }

    /**
     * Apply Hidden Rules to hex. Everything is hidden in the hex except for
     * active aircraft, airbases and ships. Also any pieces controlled by the
     * player.
     *
     * @see #isFriendlyTo
     */
    void applyHidden() {
        boolean base = false;
        for (GamePiece p : contents) {
            if (!(p instanceof OcsCounter)) continue;
            if (((OcsCounter) p).theSide == -1
                || Statics.theSides[((OcsCounter) p).theSide].controlled) {
                ((OcsCounter) p).setSecurity(OcsCounter.CHANGE);
            } else if (p instanceof Airbase) {
                base = true;
                ((OcsCounter) p).setSecurity(OcsCounter.VISIBLE);
            } else if (p instanceof Ship) {
                ((OcsCounter) p).setSecurity(OcsCounter.VISIBLE);
            } else if (!base && p instanceof Aircraft) {
                ((OcsCounter) p).setSecurity(OcsCounter.VISIBLE);
            } else {
                ((OcsCounter) p).setSecurity(OcsCounter.HIDDEN);
            }
        }
    }

    /**
     * Apply Concealed Rules. This is the same as Hidden rules except that
     * combat units are concealed instead of hidden. Also if beyond flattening
     * range only the top land unit is concealed the remainder remain hidden. If
     * option 13.7 is in effect a division marker will become concealed and all
     * its divisional units below the marker will remain invisible. This only
     * needs to be done in flattening is not in effect.
     */
    void applyConcealed(boolean flat) {
        boolean base = false;
        boolean topped = false;
        boolean noDiv = true;
        for (GamePiece p : contents) {
            if (!(p instanceof OcsCounter)) continue;
            if (((OcsCounter) p).theSide == -1
                || Statics.theSides[((OcsCounter) p).theSide].controlled) {
                ((OcsCounter) p).setSecurity(OcsCounter.CHANGE);
            } else if (p instanceof Airbase) {
                base = true;
                ((OcsCounter) p).setSecurity(OcsCounter.VISIBLE);
            } else if (p instanceof Ship) {
                ((OcsCounter) p).setSecurity(OcsCounter.VISIBLE);
            } else if (!base && p instanceof Aircraft) {
                ((OcsCounter) p).setSecurity(OcsCounter.VISIBLE);
            } else if ((!flat || !topped) && p instanceof Land) {
                topped = true;
                ((OcsCounter) p).setSecurity(OcsCounter.CONCEALED);
            } else if (p instanceof Division) {
                noDiv = false;
                ((OcsCounter) p).setSecurity(OcsCounter.HIDDEN);
            } else {
                ((OcsCounter) p).setSecurity(OcsCounter.HIDDEN);
            }
        }
        if (flat || noDiv) return;
        for (int i = 0; i < pieceCount; i++) {
            if (!(contents[i] instanceof OcsCounter)) continue;
            OcsCounter p = (OcsCounter) contents[i];
            if (p.theSide == -1 || Statics.theSides[p.theSide].controlled)
                continue;
            if (!(p instanceof Division)) continue;
            for (int j = i - 1; j >= 0; j++) {
                if (!(contents[i] instanceof Land)) continue;
                Land q = (Land) contents[i];
                if (q.theSide == -1 || Statics.theSides[q.theSide].controlled)
                    continue;
                if (q.division != null && q.division.equals(p.division)) {
                    p.setSecurity(OcsCounter.CONCEALED);
                    q.setSecurity(OcsCounter.HIDDEN);
                }
            }
        }
    }

    /**
     * This routine calculates the correct security for every piece in the
     * stack. Skip the range check if not on the main map
     *
     * @param notMap - true if this stack is not on the main map and range
     * effects are irrelevant
     */
    void calculateVisibility(boolean notMap) {
        /*
         * First change control of any Controlled pieces if required
         */
        changeControl();
        /*
         * Next check for Attack Marker
         */
        if (isAttackMarkerPresent()) return;
        /*
         * Next see if any range effect has applied
         */
        int sec = Statics.zoneSecurity;
        if (!notMap) {
            sec = hasRangeEffect();
            if (sec < 0) return;
        }
        /*
         * Apply the standard FOW rules to the stack with the default security
         * for the range
         */
        stackVisibility(sec);
    }

    /**
     * Calculate visibility for a hex zone. This is done by arranging the pieces
     * in the zone and in the hex the zone represents into a single stack and
     * then applying the normal rules as though that stack was in the hex of the
     * zone. <p> This is done by collecting all the stacks within the hex zone
     * and sorting them into order on basis of left to right within top to
     * bottom and then adding the stack in the hex if any on top. A fake stack
     * is created from these stacks and is used to do the visibility
     * calculations
     */
    void calculateVisibilityHexZone(Zone z, Point p) {
        List<StackOverride> stacks = new ArrayList<StackOverride>();
        StackOverride inHex = null;
        GamePiece[] allPieces = map.getAllPieces();
        for (GamePiece q : allPieces) {
            if (!(q instanceof StackOverride)) continue;
            StackOverride s = (StackOverride) q;
            if (s.pos.equals(p)) {
                inHex = s;
                continue;
            }
            Zone y = map.findZone(new Point(s.pos));
            if (y == z) {
                stacks.add(s);
            }
        }
        Collections.sort(stacks, new StackOrder());
        StackOverride t = new StackOverride();
        int k = 0;
        for (StackOverride i : stacks) {
            k += i.pieceCount;
        }
        if (inHex != null) k += inHex.pieceCount;
        t.contents = new GamePiece[k];
        t.pieceCount = k;
        t.pos = p;
        t.map = map;
        if (inHex != null) {
            k -= inHex.pieceCount;
            System.arraycopy(inHex.contents, 0, t.contents, k, inHex.pieceCount);
            inHex.lastCheck = Statics.check + 1;
        }
        for (StackOverride i : stacks) {
            k -= i.pieceCount;
            System.arraycopy(i.contents, 0, t.contents, k, i.pieceCount);
            i.lastCheck = Statics.check + 1;
        }
        t.calculateVisibility(false);
    }

    /**
     * Comparator for sorting stack entries into order within a hex zone
     */
    class StackOrder implements Comparator<StackOverride> {

        public int compare(StackOverride s, StackOverride t) {
            if (s.pos.y < t.pos.y) return -1;
            if (s.pos.y > t.pos.y) return 1;
            if (s.pos.x < t.pos.x) return -1;
            if (s.pos.x > t.pos.x) return 1;
            return 0;
        }
    }

    /**
     * Set the security for a stack in a side zone
     */
    void setSideZoneAccess(int sec) {
        for (GamePiece p : contents) {
            if (!(p instanceof OcsCounter)) continue;
            if (p instanceof Aircraft || p instanceof Land
                || p instanceof Ship) {
                ((OcsCounter) p).setSecurity(sec);
            } else if (p instanceof GameMarker) {
                ((OcsCounter) p).setSecurity(OcsCounter.CHANGE);
            } else {
                ((OcsCounter) p).setSecurity(OcsCounter.HIDDEN);
            }
        }
    }

    /**
     * Check if the visibility of the pieces in this stack needs recalculating
     * and if so determine how to calculate the visibility for this stack.
     */
    void checkVisibility() {
        /*
         * For Baltic Gap replace any drawn cards by the pieces they represent
         */
        replaceCards();
        /*
         * First see if the check is necessary. The count in Statics is incremented
         * whenever something happens which might change the visibility criteria
         * for the pieces. If this stack has not been recaclculated since the
         * last move (or whatever) then do the calulation
         */
        if (lastCheck > Statics.check) return;
        lastCheck = Statics.check + 1;
        /*
         * First see if hidden movement is enabled. If it is not then make
         * every piece accessible.
         */
        if (Statics.hiddenMovementOff) {
            makeAllAccessible();
            return;
        }
        /*
         * Ensure that the hex zones are tied to hexes
         */
        Statics.theStatics.buildHexZoneList();
        /*
         * First find out if the stack is in a zone on the main map.
         */
        Zone z = null;
        if (map == Statics.theMap) {
            z = map.findZone(new Point(this.pos));
            if (z == null) {
                // Perhaps the stack is in the hex of a hex zone
                z = Statics.theStatics.isHexZone(pos);
            }
        }
        /*
         * If the zone is a hex zone then calculate its visibility
         */
        if (z instanceof OcsHexZone) {
            Point p = ((OcsHexZone) z).getHex();
            if (p != null) {
                calculateVisibilityHexZone(z, p);
                return;
            }
        }
        if (z instanceof OcsSideZone) {
            int x = ((OcsSideZone) z).getSide();
            if (x < 0 || Statics.theSides[x].controlled) {
                makeAllAccessible();
            } else {
                setSideZoneAccess(Statics.zoneSecurity);
            }
            return;
        }
        /*
         * If it is in an off map window or a non hex grid zone (we assume that
         * just defines a game map) then treat as ordinary hex but not
         */
        calculateVisibility(map != Statics.theMap || z != null && !(z.getGrid() instanceof HexGrid));
    }

    /**
     * Check a GamePiece being added to see if it is an initial supply marker
     * being placed and set its side to that of the one controlled by the player
     */
    void checkPieceAdded(GamePiece p) {
        if (!(p instanceof Controlled)) return;
        Controlled s = (Controlled) p;
        if (s.theSide != -1) return;
        if (Statics.theSides[0].controlled
            && !Statics.theSides[1].controlled) {
            s.theSide = 0;
            return;
        }
        if (!Statics.theSides[0].controlled
            && Statics.theSides[1].controlled) {
            s.theSide = 1;
            return;
        }
    }
}
