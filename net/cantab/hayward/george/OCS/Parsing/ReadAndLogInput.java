/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS.Parsing;

import VASSAL.build.GameModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cantab.hayward.george.OCS.Statics;

/**
 * This class reads a specified input file, logging the contents in another file
 * and returning each line to the caller.
 * Lines being with '#' are treated as comments and skipped
 * Lines beginning with $ have a special meaning for this program
 * $= <file> <id> includes the specified file. If <id> is not null then just the
 * part of the file where the <id> is active is included.
 * $+ <id1> .... makes <id1> etc active
 * $- <id1> .... makes <id1> etc inactive
 * @author george
 */
public class ReadAndLogInput {

    /**
     * The file which is being read
     */
    File theFile;
    /**
     * The input file
     */
    InputStreamReader theInput;
    /**
     * A log file
     */
    OutputStreamWriter theOutput;
    /**
     * Current line number in input file
     */
    int lineNo = 0;
    /**
     * The id of the marker which indicates which lines are relevant
     */
    String id;
    /**
     * The file being included at this point
     */
    ReadAndLogInput included;
    /**
     * True of this object has been initialised.
     */
    boolean initialised = false;
    /**
     * True if log file I/O errors already flagged
     */
    boolean IOErrorFlagged = false;
    /**
     * True if warnings to be skipped
     */
    boolean skipWarnings = false;

    /**
     * Test if parentheses are balanced.
     *
     * @param sb - string buffer to check
     * @return true - if there are at least as many closing parentheses as opening ones
     */
    static boolean balancedParentheses(StringBuffer sb) {
        int j = 0;
        for ( int i = 0; i < sb.length(); i++ ) {
            if (sb.charAt(i) == '(') j++;
            else if (sb.charAt(i) == ')') j--;
        }
        return j < 1;
    }

    /**
     * Returns true if the character is found in the given array
     */
    private static boolean characterIsSpecial(char c, char[]d) {
        if (d == null ) return false;
        for (char f : d) {
            if (f == c) return true;
        }
        return false;
    }

    static final Pattern factorsA = Pattern.compile("\\d+-\\d+-\\d+");
    static final Pattern factorsB = Pattern.compile("\\(\\d+\\)-\\d+-\\d+");
    static final Pattern factorsC = Pattern.compile("\\d+-\\d+");
    static final Pattern factorsD = Pattern.compile("\\d+-\\d+-[rR][rR]");
    static final Pattern factorsE = Pattern.compile("\\*-\\d+-[rR][rR]");
    static final Pattern emptySpec = Pattern.compile("\\(\\s*[eE]mpty\\s*\\)");
    static final Pattern loadedSpec = Pattern.compile("\\(\\s*[lL]oaded\\s*\\)");
    static final Pattern fullSpec = Pattern.compile("\\(\\s*Full\\s*\\)");
    static final Pattern reducedSpec = Pattern.compile("\\(\\s*reduced\\s*\\)");
    static final Pattern inexpSpec = Pattern.compile("\\(\\s*inexperienced\\s*\\)");
    static final Pattern redMechSpec = Pattern.compile("\\(\\s*Red\\s*Mech\\s*Symbol\\s*\\)");
    static final Pattern redSpec = Pattern.compile("\\(\\s*red\\s*\\)");
    static final Pattern redSymbolSpec = Pattern.compile("\\(\\s*Red\\s*Symbol\\s*\\)");
    static final Pattern mmdSpec = Pattern.compile("\\(\\s*MMD\\s*\\)");
    static final Pattern iiSpec = Pattern.compile("\\(\\s*II\\s*\\)");
    static final Pattern gdSpec = Pattern.compile("\\(\\s*[Gg][Dd][s]*\\s*\\)");
    static final Pattern AbnSpec = Pattern.compile("“Abn”");
    static final String EMPTY = " empty";
    static final String LOADED = " loaded";
    static final String REDUCED = " reduced";
    static final String INEXP = " inexp";
    static final String REDMECH = " redmech";
    static final String RED = " red";
    static final String MMD = "MMD";
    static final String GD = "(Gd)";
    static final String Abn = "Abn";
    static final String ii = "ii";

    /**
     * Turn a string buffer or portion thereof into an array of words . Words
     * are delineated by whitespace except that uppercase letters start a new
     * word unless preceded by another upper case letter.
     * This is because quite often the PDF conversion runs separate words into
     * each other if there isn't a lot of space between them.
     * '#' is treated as the start of a comment and terminates the input line
     *
     * @param sb - buffer to be converted into word array
     * @param special - an array of characters to be as forming a word by
     * themselves
     * @param matchSpecs - true if piece factor strings are to be recognised
     * as words
     */
    static String [] bufferToWords(StringBuffer sb, char[] special, boolean matchSpecs) {
        String [] result = new String[sb.length()];
        int nextWord = 0;
        for (;;) {
            removeWhitespace(sb);
            if (sb.length() == 0) break;
            if (sb.charAt(0) == '#') break;
            char c = sb.charAt(0);
            boolean noMatch = false;
            if (c == '\'') {
                noMatch = true;
                sb.deleteCharAt(0);
                if (sb.length() == 0) break;
                if(sb.charAt(0) == '#') break;
            }
            if (matchSpecs && !noMatch) {
                Matcher A = factorsA.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = "#" + A.group();
                    sb.delete(0, A.end());
                    continue;
                }
                A = factorsB.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = "#" + A.group();
                    sb.delete(0, A.end());
                    continue;
                }
                A = factorsD.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = '#' + A.group();
                    sb.delete(0, A.end());
                    continue;
                }
                if (Statics.theStatics.isCaseBlue()) {
                    A = factorsE.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = '#' + A.group();
                        sb.delete(0, A.end());
                        continue;
                    }
                }
                A = factorsC.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = '#' + A.group();
                    sb.delete(0, A.end());
                    continue;
                }
                A = emptySpec.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = EMPTY;
                    sb.delete(0, A.end());
                    continue;
                }
                A = loadedSpec.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = LOADED;
                    sb.delete(0, A.end());
                    continue;
                }
                A = fullSpec.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = LOADED;
                    sb.delete(0, A.end());
                    continue;
                }
                A = reducedSpec.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = REDUCED;
                    sb.delete(0, A.end());
                    continue;
                }
                A = inexpSpec.matcher(sb);
                if (A.lookingAt()) {
                    result[nextWord++] = INEXP;
                    sb.delete(0, A.end());
                    continue;
                }
                if (Statics.theStatics.isCaseBlue()) {
                    A = redMechSpec.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = REDMECH;
                        sb.delete(0, A.end());
                        continue;
                    }
                    A = redSpec.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = RED;
                        sb.delete(0, A.end());
                        continue;
                    }
                    A = redSymbolSpec.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = RED;
                        sb.delete(0, A.end());
                        continue;
                    }
                    A = mmdSpec.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = MMD;
                        sb.delete(0, A.end());
                        continue;
                    }
                    A = gdSpec.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = GD;
                        sb.delete(0, A.end());
                        continue;
                    }
                    A = AbnSpec.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = Abn;
                        sb.delete(0, A.end());
                        continue;
                    }
                    A = iiSpec.matcher(sb);
                    if (A.lookingAt()) {
                        result[nextWord++] = ii;
                        sb.delete(0, A.end());
                        continue;
                    }
                }
            }
            if (characterIsSpecial(c, special)) {
                    result[nextWord++] = Character.toString(c);
                    sb.deleteCharAt(0);
            } else {
                boolean lastUpper;
                int k = 1;
                while ( k < sb.length()) {
                    lastUpper = Character.isUpperCase(c) || c == '-' || c == '^'
                            || c == '_'
                            || (Statics.theStatics.isCaseBlue() && c == '.');
                    c = sb.charAt(k);
                    if (c == '_') {
                        sb.replace(k, k+1, " ");
                    }
                    if (characterIsSpecial(c, special)) break;
                    if (Character.isWhitespace(c)) break;
                    if (Character.isUpperCase(c) && !lastUpper) break;
                    if (c == '^') {
                        sb.deleteCharAt(k);
                    } else {
                        k++;
                    }
                }
                /*
                 * If last word was "air" abd current word is "base" then make
                 * the last word "airbase" and discard the current word. Just
                 * to make thing consistent
                 */
                if (nextWord > 0 && sb.substring(0, k).equalsIgnoreCase("base")
                        && result[nextWord - 1].equalsIgnoreCase("air")) {
                    result[nextWord - 1] += "base";
                } else {
                    result[nextWord++] = sb.substring(0, k);
                }
                sb.delete(0, k);
                if (sb.length() == 0) break;
            }
        }
        String [] done = new String[nextWord];
        System.arraycopy(result, 0, done, 0, nextWord);
        return done;
    }

    /**
     * Remove whitespace from the beginning of a StringBuffer
     */
    static void removeWhitespace(StringBuffer sb) {
        while (sb.length() != 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
    }

    /**
     * Write a string to the log file
     *
     * @throws IOException
     */
    void writeString(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            theOutput.write(s.charAt(i));
        }
    }

    /**
     * Write an error or warning
     * @param warning - true if this is a warning rather than an error
     * @param msg - the warning or error message
     */
    void writeError(boolean error, String msg)  {
        if (included != null) {
            included.writeError(error, msg);
            return;
        }
        if (!error && skipWarnings) return;
        String p = !error ? "        ---" : "***";
        try {
            writeString("    ");
            writeString(p);
            writeString(" ");
            writeString(msg);
            writeString("\n");
        } catch (IOException c) {
            if (!IOErrorFlagged) {
                IOErrorFlagged = true;

                GameModule.getGameModule().getChatter().show
                        ("Error writing to log file:" + c.getLocalizedMessage());
            }
        }
        if (!error) return;
        GameModule.getGameModule().getChatter().show(p + " ("
                + theFile.getName() + ": " + lineNo
                + ") " + msg);
    }

    /**
     * Read a single physical line of text, echoing it to the log file if the
     * id has been matched. Repeat until a non-empty line has been read.
     *
     * @param continuation - this is true if the line is considered a continuation
     * of the previous line. This is flagged in the log file
     * @param matched - this is true if the id has been matched and every line
     * is valid for input.
     * @return the line just read or null if at the end of the input file
     * @throws IOException
     */
    private StringBuffer readPhysicalLine(boolean continuation, boolean matched) {
        StringBuffer sb = new StringBuffer();
        int b;
        try {
            for (;;) {
                /*
                 * Read in the next line.
                 */
                for (;;) {
                    b = theInput.read();
                    if (b == -1) break;
                    if (b == '\n') break;
                    sb.append((char) b);
                }
                /*
                 * If it is the end of file and nothing read return EOF
                 */
                if ( b == -1 && sb.length() == 0) {
                    return null;
                }
                /*
                 * Output the current line number to the log file as a six digit number
                 * and follow with a separator and a copy of the input line
                 */
                if (matched) {
                    String t = Integer.toString(++lineNo);
                    int k = 6 - t.length();
                    for (int i = 0; i < 6; i++) {
                        theOutput.write((i < k ) ? ' ' : t.charAt(i - k));
                    }
                    theOutput.write(continuation ? '+' : ':');
                    theOutput.write(' ');
                    for (int i = 0; i < sb.length(); i++) {
                        theOutput.write(sb.charAt(i));
                    }
                    theOutput.write('\n');
                } else {
                    lineNo++;
                }
                /*
                 * Empty line then ignore it
                 */
                if (sb.length() == 0) continue;
                /*
                 * If the line ends in a carriage return then discard it.
                 */
                if (sb.charAt(sb.length()-1) == '\r') {
                    sb.deleteCharAt(sb.length()-1);
                }
                /*
                 * If the line is not empty break out of the loop
                 */
                if (sb.length() != 0) break;
            }
        } catch (IOException e) {
            GameModule.getGameModule().getChatter().show(
                        "Error reading input/writing log: "
                        + e.getLocalizedMessage());
        }
        /*
         * Return the line read
         */
        return sb;
    }

    /**
     * Read the next line of input
     */
    StringBuffer nextLine(boolean continuation) {
        StringBuffer b;
        if (included != null) {
            b = included.nextLine(continuation);
            if (b != null) return b;
            if (continuation) {
                included.writeError(true, "Unterminated line(s) at end of file");
                return new StringBuffer();
            }
            included = null;
        }
        for (;;) {
            b = readPhysicalLine(continuation, true);
            if (b == null) {
                if (continuation) {
                    writeError(true, "Unterminated line(s) at end of file");
                }
                return null;
            }
            if (b.charAt(0) == '#') {
                continue;
            }
            if (b.charAt(0) != '$') {
                break;
            }
            if (b.length() < 2 || b.charAt(1) == '+') {
                continue;
            }
            if (b.charAt(1) == '=') {
                b.delete(0, 2);
                String [] wrds = bufferToWords(b, null, false);
                if (wrds.length == 0 || wrds.length > 2) {
                    writeError(true, "Bad include directive");
                    continue;
                }
                File f = new File(theFile.getParentFile(), wrds[0]);
                included = new ReadAndLogInput(f, theOutput,
                        wrds.length == 1 ? null : wrds[1]);
                return included.nextLine(continuation);
            } else if (b.charAt(1) == '-' && id != null) {
                b.delete(0, 1);
                String [] ids = bufferToWords(b, null, false);
                int i;
                for (i = 0; i < ids.length; i++) {
                    if (ids[i].equals(id)) break;
                }
                if (i < ids.length) {
                    scanUntilId();
                }
            } else if (b.charAt(1) == '0') {
                skipWarnings = true;
            } else if (b.charAt(1) == '1') {
                skipWarnings = false;
            }
        }
        return b;
    }

    /**
     * Scan lines until id enabled
     */
    private void scanUntilId() {
        for (;;) {
            StringBuffer line;
            line = readPhysicalLine(false, false);
            if (line == null) break;
            if (line.length() < 2 ||
                    line.charAt(0) != '$' || line.charAt(1) != '+') {
                continue;
            }
            line.delete(0, 2);
            String [] ids = bufferToWords(line, null, false);
            int i;
            for (i = 0; i < ids.length; i++) {
                if (ids[i].equals(id)) break;
            }
            if (i < ids.length) break;
        }
    }

    /**
     * Create a new object
     */
    ReadAndLogInput(File in, OutputStreamWriter log, String match) {
        try {
            theFile = in;
            theInput = new InputStreamReader(new FileInputStream(theFile), Charset.forName(
                    "UTF-8")) ;
        } catch (IOException e) {
            GameModule.getGameModule().getChatter().show(
                    "Error opening input " + theFile.getName() + " : "
                    + e.getLocalizedMessage());
            return;
        }
        if (log != null ) {
            theOutput = log;
        } else {
            File curLog = null;
            try {
                String s = theFile.getName();
                for (int i = s.length() - 1; i > 0; i--) {
                    if (s.charAt(i) == '.') {
                        s = s.substring(0, i);
                        break;
                    }
                }
                curLog = new File(theFile.getParent(), s + ".log");
                theOutput = new OutputStreamWriter(new FileOutputStream(curLog), Charset.forName("UTF-8"));
            } catch (IOException e) {
                GameModule.getGameModule().getChatter().show(
                        "Error opening log " + curLog.getName() + " : "
                        + e.getLocalizedMessage());
            }
        }
        if (match != null) {
            id = match;
            scanUntilId();
        }
    }

}
