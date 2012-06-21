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

import java.util.NoSuchElementException;
import java.util.Scanner;
import net.cantab.hayward.george.OCS.Counters.Airbase;
import net.cantab.hayward.george.OCS.Counters.Aircraft;
import net.cantab.hayward.george.OCS.Counters.Artillery;
import net.cantab.hayward.george.OCS.Counters.AttackCapable;
import net.cantab.hayward.george.OCS.Counters.Defensive;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.Counters.GameMarker;
import net.cantab.hayward.george.OCS.Counters.HeadQuarters;
import net.cantab.hayward.george.OCS.Counters.Hedgehog;
import net.cantab.hayward.george.OCS.Counters.Ship;
import net.cantab.hayward.george.OCS.Counters.SupplyMarker;
import net.cantab.hayward.george.OCS.Counters.Transport;

/**
 *
 * @author George Hayward
 */
public class PieceData {

    /**
     * Name of the piece for display purposes
     */
    String name;

    /**
     * Name of the image for the front of the piece
     */
    String frontImage;

    /**
     * Name of the image for the back of the piece. Null if one sided piece
     */
    String backImage;

    /**
     * The type of the piece encoded as a three character string. This is the
     * same string used to identify the correct piece class in the save file.
     */
    String type;

    /**
     * The RE Size of the piece (int units of 1T )
     */
    int RESize;

    /**
     * Combat strength of the units in combat mode and move mode. Negative if
     * strength is just defensive. Also air-to-air rating for aircraft.
     * Barrage strength for ships and artillery.
     */
    int combatStrength;
    int moveStrength;

    /**
     * AR of the unit in combat and move mode. Also barrage strength for
     * aircraft. Flak for ships.
     */
    int combatAR;
    int moveAR;

    /**
     * Type of movement allowance. 'L' means leg, 'T' means track and 'W' means
     * wheel. In both combat and move mode. Also aircraft type: 'F' means fighter,
     * 'T' means tacitical bomber, 'S' mean strategic bomber and 'V' meand transport.
     */
    char combatType;
    char moveType;

    /**
     * Movement allowance in combat mode and move mode. For aircraft the transport
     * size in units of 1T. Protection for ships.
     */
    int combatMA;
    int moveMA;

    /**
     * Range for Artillery Units, Throw range for HQs, range for aircraft and
     * range for extenders. Range of ships.
     */
    int combatRange;
    int moveRange;

    /**
     * Form of unit: armour = 'A' / mechanised = 'M' /other = 'O'. For transport
     * is 'O' for organic, 'G' for generic
     */
    char form;

    /**
     * The antitank level H=heavy, L=light, N=none
     */
    char antiTank;

    /**
     * The side a piece belongs to. Null for neutral pieces.
     */
    Side theSide;
    
    /**
     * The division to which the counter belongs if any
     */
    PieceData theDivision;

    /**
     * True if the unit cannot be rebuilt
     */
    boolean noRebuild;

    /**
     * True if the unit is a railway engineer
     */
     boolean railwayEngineer;

     /**
      * True if the unit is a pontoon bridge
      */
     boolean pontoon;

     /**
      * Truew if the unit can be airdropped
      */
     boolean airdropable;

    /**
     * The last divisonal counter created
     */
    static PieceData lastDivisional;

   /**
     * Convert a string into a side
     * @param name The name of the side to be found
     * @return The side object which has the name given
     * @throws NoSuchElementException if the name does not match any nation
     */
    Side getSide(String name) throws NoSuchElementException {
        int i;
        for (i = 0; i < Statics.theSides.length; i++) {
            if (name.equalsIgnoreCase(Statics.theSides[i].name)) {
                return Statics.theSides[i];
            }
        }
        throw new NoSuchElementException("Unknown Side: " + name);
    }

    /**
     * Create a piece by reading its definition from the scanner provided
     */
    PieceData( Scanner s ) {
        String p;

        noRebuild = false;
        railwayEngineer = false;
        pontoon = false;
        airdropable = false;

        p = s.next();
        if (p.equals("-")) {
            theSide = null;
        } else {
            theSide = getSide(p);
        }

        /*
         * Read the name of the piece. This may be enclosed in double quotes.
         * First get the next token. If it begins with a double quote get the
         * rest of the string by searching for the next double quote which must be
         * on the same line
         */
        p = s.findInLine("\\p{javaWhitespace}*\"");
        if (p == null) {
            throw new NoSuchElementException("Missing string start");
        }
        p = s.findInLine(".*\"");
        if (p == null) {
            throw new NoSuchElementException("Unterminated string");
        }
        p = p.substring(0, p.length() - 1);
        name = p;

        /*
         * Read the image name for the front of the counter. This is just
         * delimited by whitespace
         */
        frontImage = s.next();

        /*
         * Read the image name for the back of the image. This may be "-" to
         * indicate the counter is one sided.
         */
        p = s.next();
        if ( p.equals("-")) {
            backImage = null;
        } else {
            backImage = p;
        }

        /*
         * Read the type of counter and verify it.
         */
        p = s.next();

        if ( p.equals(Airbase.ID) ) {

        } else if ( p.equals(Aircraft.ID) ) {
            /*
             * front air-to-air, barrage, type, transport size in 1/2 T units
             * (if transport), finally range
             */
            combatStrength = s.nextInt();
            combatAR = s.nextInt();
            combatType = s.next().charAt(0);
            if ( combatType != 'F' && combatType != 'T' && combatType != 'S'
                    && combatType != 'V' ) {
                throw new NoSuchElementException( "Unknown type of aircraft: "
                        + combatType );
            }
            if ( combatType == 'V' ) {
                combatMA = s.nextInt();
            } else {
                combatMA = 0;
            }
            combatRange = s.nextInt();
            /*
             * Same for the back
             */
            moveStrength = s.nextInt();
            moveAR = s.nextInt();
            moveType = s.next().charAt(0);
            if ( moveType != 'F' && moveType != 'T' && moveType != 'S'
                    && moveType != 'V' ) {
                throw new NoSuchElementException( "Unknown type of aircraft: "
                        + moveType );
            }
            if ( moveType == 'V' ) {
                moveMA = s.nextInt();
            } else {
                moveMA = 0;
            }
            moveRange = s.nextInt();
        } else if ( p.equals(Artillery.ID) ) {
            /*
             * RE size
             */
            RESize = s.nextInt();
            /*
             * Front barrage,AR,move type,move allowance and range
             */
            combatStrength = s.nextInt();
            combatAR = s.nextInt();
            combatType = s.next().charAt(0);
            if ( combatType != 'L' && combatType != 'W' && combatType != 'T' ) {
                throw new NoSuchElementException( "Unknown type of movement: "
                        + combatType );
            }
            combatMA = s.nextInt();
            combatRange = s.nextInt();
            /*
             * Same for back
             */
            moveStrength = s.nextInt();
            moveAR = s.nextInt();
            moveType = s.next().charAt(0);
            if ( moveType != 'L' && moveType != 'W' && moveType != 'T' ) {
                throw new NoSuchElementException( "Unknown type of movement: "
                        + moveType );
            }
            moveMA = s.nextInt();
            moveRange = s.nextInt();
            form = 'O';
            antiTank = 'N';
        } else if ( p.equals(Defensive.ID) ) {
            /*
             * RE size
             */
            RESize = s.nextInt();
            /*
             * form
             */
            String r = s.next();
            form = r.charAt(0);
            if ( form != 'A' && form != 'M' && form != 'O') {
                throw new NoSuchElementException( "Unknown type of category: "
                        + form );
            }
            antiTank = r.charAt(1);
            if ( antiTank != 'H' && antiTank != 'L' && antiTank != 'N' ) {
                throw new NoSuchElementException( "Unknown type of anti-tank: "
                        + antiTank );
            }
            for ( int i = 2 ; i < r.length() ; i ++ )  {
                if ( r.charAt(i) == 'O' ) noRebuild = true;
                else if ( r.charAt(i) == 'R' ) railwayEngineer = true;
                else if ( r.charAt(i) == 'P' ) pontoon = true;
                else if ( r.charAt(i) == 'A' ) airdropable = true;
            }
            /*
             * Front barrage,AR,move type,move allowance
             */
            combatStrength = s.nextInt();
            combatAR = s.nextInt();
            combatType = s.next().charAt(0);
            if ( combatType != 'L' && combatType != 'W' && combatType != 'T' ) {
                throw new NoSuchElementException( "Unknown type of movement: "
                        + combatType );
            }
            combatMA = s.nextInt();
            /*
             * Same for back
             */
            moveStrength = s.nextInt();
            moveAR = s.nextInt();
            moveType = s.next().charAt(0);
            if ( moveType != 'L' && moveType != 'W' && moveType != 'T' ) {
                throw new NoSuchElementException( "Unknown type of movement: "
                        + moveType );
            }
            moveMA = s.nextInt();
            if ( combatStrength >= 0 ) p = AttackCapable.ID;
        } else if ( p.equals(Division.ID) ) {
            lastDivisional = this;
        } else if ( p.equals(GameMarker.ID) ) {

        } else if ( p.equals(Hedgehog.ID) ) {
            
        } else if ( p.equals(HeadQuarters.ID) ) {
            /*
             * Front throw,move type and movement allowance
             */
            combatRange = s.nextInt();
            combatType = s.next().charAt(0);
            if ( combatType != 'L' && combatType != 'W' && combatType != 'T' ) {
                throw new NoSuchElementException( "Unknown type of movement: "
                        + combatType );
            }
            combatMA = s.nextInt();
            /*
             * Same for back
             */
            moveRange = s.nextInt();
            moveType = s.next().charAt(0);
            if ( moveType != 'L' && moveType != 'W' && moveType != 'T' ) {
                throw new NoSuchElementException( "Unknown type of movement: "
                        + moveType );
            }
            moveMA = s.nextInt();
        } else if ( p.equals(Ship.ID) ) {
            /*
             * Front barrage, flak, range and protection
             */
            combatStrength = s.nextInt();
            combatAR = s.nextInt();
            combatRange = s.nextInt();
            combatMA = s.nextInt();
            /*
             * Same for back
             */
            moveStrength = s.nextInt();
            moveAR = s.nextInt();
            moveRange = s.nextInt();
            moveMA = s.nextInt();
        } else if ( p.equals(SupplyMarker.ID) ) {
            
        } else if ( p.equals(Transport.ID) ) {
            /*
             * RE Size, move type and movement allowance
             */
            RESize = s.nextInt();
            combatType = s.next().charAt(0);
            if ( combatType != 'L' && combatType != 'W' && combatType != 'T' ) {
                throw new NoSuchElementException( "Unknown type of movement: "
                        + combatType );
            }
            combatMA = s.nextInt();
            form = s.next().charAt(0);
            if ( form != 'G' && form != 'O' ) {
                throw new NoSuchElementException( "Unknown type of transport: "
                        + form );
            }
        } else {
        }
        type = p;
        theDivision = lastDivisional;
    }

}
