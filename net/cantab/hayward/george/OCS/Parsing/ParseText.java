/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS.Parsing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.cantab.hayward.george.OCS.Statics;

/**
 *
 * @author george
 */
public class ParseText {

    /**
     * Module specific data
     */
    ModuleSpecific data;
    /**
     * Input file
     */
    LineReader input;
    /**
     * Piece Searcher
     */
    PieceSearcher ps;

    public ParseText(File f) {
        input = new LineReader(f);
        if (Statics.theStatics.isBalticGap()) {
            data = new BalticGap();
        } else if (Statics.theStatics.isCaseBlue()) {
            data = new CaseBlue();
        } else if (Statics.theStatics.isDAK()) {
            data = new DAK();
        } else if (Statics.theStatics.isKorea()) {
            data = new Korea();
        } else if (Statics.theStatics.isTunisia()) {
            data = new Tunisia();
        } else if (Statics.theStatics.isHubes()) {
            data = new Hubes();
        } else if (Statics.theStatics.isBlitzkriegLegend()) {
            data = new BlitzkriegLegend();
        }
        ps = new PieceSearcher(input, data);
    }

    /**
     * Parse an input file
     */
    public void parse() {
        for (;;) {
            StringBuffer line;
            line = input.nextLine();
            if (line == null) break;
            if (line.charAt(0) == '!') {
                char[] special = new char[]{'!'};
                String[] spec = ReadAndLogInput.bufferToWords(line, special, false);
                if (spec.length != 3) {
                    if (spec.length == 2 && spec[1].equalsIgnoreCase("instructions")) {
                        parseInstructions();
                        continue;
                    }
                    if (spec.length == 2 && spec[1].equalsIgnoreCase("dross")) {
                        parseDross(ps.dross);
                        continue;
                    }
                    if (Statics.theStatics.isCaseBlue() && spec.length > 3
                            && spec[2].equalsIgnoreCase("reinforcements")) {
                        int side = -1;
                        if (spec[1].equalsIgnoreCase(Statics.theSides[0].name)) {
                            side = 0;
                        } else if (spec[1].equalsIgnoreCase(Statics.theSides[1].name)) {
                            side = 1;
                        } else {
                            input.writeError(true, "Bad side");
                            continue;
                        }
                        ScheduleReader t = new CaseBlueReader(ps, input, data, side, PieceReader.strip(spec, 3));
                        ps.setReader(t);
                        t.parse();
                        continue;
                    }
                    input.writeError(true, "Bad ! command");
                    continue;
                }
                if (spec[1].equalsIgnoreCase("Synonyms")) {
                    if (spec[2].equalsIgnoreCase("divisions")) {
                        parseSynonyms(ps.divs);
                    } else if (spec[2].equals("units")) {
                        parseSynonyms(ps.unit);
                    } else {
                        input.writeError(true, "Unknown type of synonym");
                    }
                    continue;
                }
                if (spec[1].equalsIgnoreCase("marker") && spec[2].equalsIgnoreCase("Information")) {
                    MarkerReader m = new MarkerReader(ps, input, data, -1);
                    ps.setReader(m);
                    m.parse();
                    continue;
                }
                int side = -1;
                if (spec[1].equalsIgnoreCase(Statics.theSides[0].name)) {
                    side = 0;
                } else if (spec[1].equalsIgnoreCase(Statics.theSides[1].name)) {
                    side = 1;
                } else {
                    input.writeError(true, "Bad side");
                    continue;
                }
                if (spec[2].equalsIgnoreCase("Information")) {
                    SetupReader s = new SetupReader(ps, input, data, side);
                    ps.setReader(s);
                    s.parse();
                } else if (spec[2].equalsIgnoreCase("reinforcements")) {
                    ScheduleReader t = new ScheduleReader(ps, input, data, side);
                    ps.setReader(t);
                    t.parse();
                } else {
                    input.writeError(true, "Unrecognised ! command");
                }
            } else {
                input.writeError(false, "line ignored");
            }
        }
        try {
            input.theOutput.close();
        } catch (IOException e) {

        }
    }

    /**
     * Parse scenario instructions
     */
    void parseInstructions() {
        for (;;) {
            StringBuffer curLine = input.nextLine();
            if (curLine == null) break;
            if (curLine.charAt(0) == '!') break;
            Statics.addToScenarioInformation(curLine.toString());
        }
        input.repeatThisLine();
    }

    /**
     * Parse synonym list
     */
    void parseSynonyms(List<PieceSearcher.Synonym> a) {
        for (;;) {
            StringBuffer curLine = input.nextLine();
            if (curLine == null) break;
            if (curLine.charAt(0) == '!') break;
            char[] special = new char[]{'=', '+'};
            String[] spec = ReadAndLogInput.bufferToWords(curLine, special, false);
            int i;
            int j = -1;
            for (i = 0; i < spec.length; i++) {
                if (spec[i].equals("+")) j = i;
                if (spec[i].equals("=")) break;
            }
            if (i >= spec.length || j == i - 1) {
                input.writeError(true, "Bad Synonym");
                continue;
            }
            if (j < 0) {
                a.add(new PieceSearcher.Synonym(PieceReader.top(spec, i), PieceReader.strip(spec, i + 1), null));
            } else {
                String[] t = PieceReader.top(spec, i);
                a.add(new PieceSearcher.Synonym((PieceReader.top(t, j)), PieceReader.strip(spec, i + 1),
                        PieceReader.strip(t, j + 1)));
            }
        }
        input.repeatThisLine();
    }

    void parseDross(List<PieceSearcher.Terminal> a) {
        for (;;) {
            StringBuffer curLine = input.nextLine();
            if (curLine == null) break;
            if (curLine.charAt(0) == '!') break;
            char[] special = new char[]{'|', '+'};
            String[] spec = ReadAndLogInput.bufferToWords(curLine, special, false);
            List<String[]> matches = new ArrayList<String[]>();
            List<String> b = new ArrayList<String>();
            PieceSearcher.Terminal t = new PieceSearcher.Terminal();
            for (int i = 0; i < spec.length; i++) {
                if (spec[i].equals("+")) {
                    String[] c = new String[b.size()];
                    c = b.toArray(c);
                    matches.add(c);
                    b.clear();
                    t.matches.add(matches);
                    matches = new ArrayList<String[]>();
                } else if (spec[i].equals("|")) {
                    String [] c = new String [b.size()];
                    c = b.toArray(c);
                    matches.add(c);
                    b.clear();
                } else {
                    b.add(spec[i]);
                }
            }
            String [] c = new String [b.size()];
            c = b.toArray(c);
            matches.add(c);
            a.add(t);
            t.matches.add(matches);
        }
        input.repeatThisLine();
    }

}
