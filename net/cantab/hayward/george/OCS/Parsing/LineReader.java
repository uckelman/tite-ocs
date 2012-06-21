/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import java.io.File;

/**
 * Processes the reading of a single logical line from the input
 * @author george
 */
public class LineReader extends ReadAndLogInput {

    /**
     * Current line being built
     */
    StringBuffer curLine;

    /**
     * Next line to be processes
     */
    StringBuffer nextLine;

    /**
     * True if current line is to be repeated
     */
    boolean repeatLine;

    /**
     * Create a new line reader
     */
    LineReader (File f) {
        super(f, null, null);
        curLine = null;
        nextLine = null;
        repeatLine = false;
    }

    /**
     * Force the current line to be repeated
     */
    void repeatThisLine() {
        repeatLine = true;
    }

    /**
     * Read the next logical line form the input
     * @return the line read
     */
    StringBuffer nextLine() {
        if (repeatLine) {
            repeatLine = false;
            if (curLine == null) return null;
            return new StringBuffer(curLine);
        }
        if (nextLine != null) {
            curLine = nextLine;
            nextLine = null;
        } else {
            curLine = nextLine(false);
        }
        if (curLine == null) return null;
        if (curLine.charAt(curLine.length() - 1) == ':'
                || curLine.charAt(0) == '!'
                || curLine.charAt(0) == '?') {
            return new StringBuffer(curLine);
        }
        while (!balancedParentheses(curLine)) {
            nextLine = nextLine(true);
            if (nextLine.charAt(nextLine.length() - 1) == ':'
                    || nextLine.charAt(0) == '!'
                    || nextLine.charAt(0) == '?') {
                writeError(true, "Unbalanced line terminated by command");
                break;
            }
            if (curLine.charAt(curLine.length() - 1) == '-') {
                curLine.deleteCharAt(curLine.length() - 1);
            } else {
                curLine.append(' ');
            }
            curLine.append(nextLine);
            nextLine = null;
        }
        return new StringBuffer(curLine);
    }

}
