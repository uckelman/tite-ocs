/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import javax.swing.KeyStroke;

/**
 * Contains module specific data
 * @author george
 */
public abstract class ModuleSpecific {
    /**
     * Key command which increments step loss counter
     */
    KeyStroke stepLossIncKey;
    /**
     * Maximum number of hits which can be applied to nay piece
     */
    int maxHits;
    /**
     * Key command which increments the level of air bases and hedgehogs
     */
    KeyStroke levelIncKey;
    /**
     * Largest denomination of supply counter
     */
    int maxSupply;
    /**
     * Key command to increment supply counter
     */
    KeyStroke supplyIncKey;
    /**
     * Name of supply points piece
     */
    String supplyName;
    /**
     * Name of supply token piece if different
     */
    String supplyTokenName;
    /**
     * Largest denomination of transport counter
     */
    int maxTransport;
    /**
     * True if Pax/Eq counters differ for the two sides
     */
    boolean paxEqDifferPerSide;
    /**
     * True if Pax/Eq counters are flip of each other
     */
    boolean paxEqFlips;
    /**
     * True if pax is default in flip case
     */
    boolean paxDefault;
    /**
     * Name of replacement
     */
    String replacementName;
    /**
     * Keystroke to flip replacement to right type
     */
    KeyStroke flipRep;
    /**
     * Keystroke to flip aircraft/ships to reduced side
     */
    KeyStroke flipToReduced;
    /**
     * Add required transport points - this is module specific
     */
    abstract void addTransport (String[] type, int size, boolean loaded,
            boolean isT, PieceReader pr);
    /**
     * Filler needed for creating unit names
     */
    String unitNameFiller;
    /**
     * Filler needed for creating division names
     */
    String divNameFiller;
    /**
     * Two stage division look up required
     */
    boolean twoStageDivLookup;
    /**
     * True if division name to be prefixed to unit names for units
     * within a division
     */
    boolean prefixDivToUnit;
    /**
     * Fixed string to form last part of prefix
     */
    String prefixEnd;
    /**
     * Do special name conversions for simple pieces
     */
    String [] convertSimple (int side, String factors, String [] ids) {
        return ids;
    }
    /**
     * True if division name to be postfixed to unit names for units
     * within a division
     */
    boolean postfixDivToUnit;
    /**
     * True if HQ needs postfix of HQ
     */
    boolean postfixHQ;
    /**
     * Check for module specific placement
     */
    boolean moduleSpecificLine (int side, String [] words, int repeat, PieceReader pr) {
        return false;
    }
    /**
     * Check for final name processing by type
     */
    String finalName(int side, String name, String [] type) {
        return name;
    }
    /**
     * Process module specific command
     */
    void moduleCommandLine(int side, String[] words, PieceReader pr) {
        pr.input.writeError(true, "Invalid module specific command ignored");
        return;
    }
    /**
     * Prefix first part of division name to id
     */
    boolean prefixFirstPartDivName = false;
    /**
     * Generate alternate name from division name
     */
    boolean altFromDiv = false;
    /**
     * Use this to fill alternative name
     */
    String prefixFill;
    /**
     * Flip organic transport for full/empty
     */
    boolean flipOrganicFullEmpty = true;
    /**
     * The default state is full
     */
    boolean fullDefault = true;
    /**
     * Keystroke to flip organic trucks
     */
    KeyStroke flipOrganic;
    /**
     * Fix unit in division name
     */
    String finalUnitName(int side, String uName, String divName) {
        return uName;
    }
    /**
     * Prefix applied to map names before searching for zone
     */
    String zonePrefix = "";
    /**
     * Prefix applied to map names before searching for board
     */
    String boardPrefix = "";
    /**
     * Check for module specific line at top level
     */
    boolean moduleSpecificLine (int side, StringBuffer b, SetupReader pr) {
        return false;
    }
    /**
     * Pieces placed
     */
    void piecesPlaced() {
    }
    /**
     * Check for module specific line from lower level
     */
    boolean isModuleSpecificLine (int side, StringBuffer b, PieceReader pr) {
        return false;
    }
}
