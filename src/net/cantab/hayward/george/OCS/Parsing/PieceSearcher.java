/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cantab.hayward.george.OCS.Parsing;

import VASSAL.build.GameModule;
import VASSAL.build.widget.PieceSlot;
import VASSAL.counters.Decorator;
import VASSAL.counters.Embellishment;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceCloner;
import VASSAL.tools.SequenceEncoder;
import java.util.ArrayList;
import java.util.List;
import net.cantab.hayward.george.OCS.Counters.Division;
import net.cantab.hayward.george.OCS.OcsCounter;
import net.cantab.hayward.george.OCS.Statics;

/**
 *
 * @author george
 */
public class PieceSearcher {

    /**
     * Module specific data
     */
    ModuleSpecific data;
    /**
     * Input source
     */
    LineReader input;
    /**
     * Current active piece reading class
     */
    PieceReader reader;

    /**
     * The structure of a piece pointer
     */
    static class PiecePtr {

        String name;
        String image;
        String factors;
        String division;
        OcsCounter piece;
        boolean added;

        PiecePtr() {
            name = "";
            image = "";
            factors = "";
            division = "";
            added = false;
        }
    }
    /**
     * Table of available pieces
     */
    PiecePtr[] thePieces;
    /**
     * No error report required
     */
    boolean noErrorReport = false;
    /**
     * No Divisional counter to be placed
     */
    boolean noDivCounters = false;

    /**
     * Define synonyms between names
     */
    static class Synonym {

        String[] start;
        String[] restriction;
        String[] replace;

        Synonym(String[] a, String[] b, String[] c) {
            start = a;
            replace = b;
            restriction = c;
        }
    }
    /**
     * Synonyms for division names
     */
    List<Synonym> divs = new ArrayList<Synonym>();
    /**
     * Synonyms for unit names
     */
    List<Synonym> unit = new ArrayList<Synonym>();

    /**
     * Define a possible terminal string to be matched
     */
    static class Terminal {

        List<List<String[]>> matches = new ArrayList<List<String[]>>();
    }
    /**
     * Terminals which are dross
     */
    List<Terminal> dross = new ArrayList<Terminal>();

    /**
     * Find a matching synonym if any
     *
     * @param a list to search
     * @param b synonym to match
     * @return the matched value or null if none
     */
    String[] findMatchingSynonym(List<Synonym> a, String[] b, String[] d) {
        for (Synonym c : a) {
            if (c.start.length != b.length) continue;
            int i;
            for (i = 0; i < b.length; i++) {
                if (!b[i].equalsIgnoreCase(c.start[i])) break;
            }
            if (i < b.length) continue;
            if (c.restriction == null) {
                return c.replace;
            }
            if (d == null) continue;
            if (c.restriction.length != d.length) continue;
            for (i = 0; i < d.length; i++) {
                if (!d[i].equalsIgnoreCase(c.restriction[i])) break;
            }
            if (i < d.length) continue;
            return c.replace;
        }
        return null;
    }

    /**
     * Remove any matching strings from a string array
     *
     * @param words string array from which to remove strings
     * @param k where to look for matches
     * @return new array if any match removed else null
     */
    String[] removeDrossAt(String[] words, int k) {
        if (k < 0 || k >= words.length) return null;
        for (Terminal t : dross) {
            int i = k;
            int m;
            int n;
            int j;
            for (m = 0; m < t.matches.size(); m++) {
                List<String[]> u = t.matches.get(m);
                for (n = 0; n < u.size(); n++) {
                    String[] v = u.get(n);
                    if (i + 1 < v.length) continue;
                    for (j = 0; j < v.length; j++) {
                        if (!v[j].equals(words[i + 1 - v.length + j])) {
                            break;
                        }
                    }
                    if (j == v.length) {
                        i -= v.length;
                        break;
                    }
                }
                if (n == u.size()) {
                    break;
                }
            }
            if (m == t.matches.size()) {
                String[] result = new String[words.length + i - k];
                System.arraycopy(words, 0, result, 0, i + 1);
                if (i < result.length - 1)
                    System.arraycopy(words, k + 1, result, i + 1, result.length - 1 - i);
                return result;
            }
        }
        return null;
    }

    PieceSearcher(LineReader a, ModuleSpecific b) {
        data = b;
        input = a;
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
            if (Statics.theStatics.isCaseBlue()) {
                if (h instanceof Embellishment) {
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
                if (thePieces[i].piece.factors != null)
                    thePieces[i].factors = thePieces[i].piece.factors;
                if (thePieces[i].piece.division != null)
                    thePieces[i].division = thePieces[i].piece.division;
            }
            i++;
        }
    }

    void setReader(PieceReader pr) {
        reader = pr;
    }

    OcsCounter findMarker(String name) {
        return findPiece(-1, null, null, null, new String[]{name}, null, null);
    }

    OcsCounter findMarker(Class<?> ocsType) {
        return findPiece(-1, ocsType, null, null, null, null, null);
    }

    OcsCounter findMarker(String name, Class<?> ocsType) {
        return findPiece(-1, ocsType, null, null, new String[]{name}, null, null);
    }

    OcsCounter findPiece(int curSide, Class<?> ocsType, String[] ids) {
        return findPiece(curSide, ocsType, null, null, ids, null, null);
    }

    OcsCounter findPiece(int curSide, Class<?> ocsType1, Class<?> ocsType2, String[] ids) {
        return findPiece(curSide, ocsType1, ocsType2, null, ids, null, null);
    }

    OcsCounter findPiece(int curSide, String[] ids) {
        return findPiece(curSide, null, null, null, ids, null, null);
    }

    OcsCounter findPiece(int curSide, String[] div, String[] ids) {
        return findPiece(curSide, null, null, null, ids, div, null);
    }

    OcsCounter findPiece(int curSide, String[] div, String factors, String[] ids) {
        return findPiece(curSide, null, null, factors, ids, div, null);
    }

    OcsCounter findPiece(int curSide, String factors, String[] ids) {
        return findPiece(curSide, null, null, factors, ids, null, null);
    }

    OcsCounter findPiece(int curSide, String factors, String[] ids, String[] type) {
        return findPiece(curSide, null, null, factors, ids, null, type);
    }

    OcsCounter findPiece(int curSide, Class<?> ocsType1, Class<?> ocsType2,
                         String factors, String[] ids, String[] div, String[] type) {
        String id = null;
        String dName = null;
        String dNameFull = null;
        String altId = null;
        String altId2 = null;
        String[] origDiv = div;
        if (div != null) {
            String[] t = findMatchingSynonym(divs, div, null);
            if (t != null) {
                div = t;
            }
            if (t != null && t[0].equals("-")) {
                String[] newi = new String[ids.length + t.length - 1];
                System.arraycopy(t, 1, newi, 0, t.length - 1);
                System.arraycopy(ids, 0, newi, t.length - 1, ids.length);
                ids = newi;
                div = null;
                t = removeDrossAt(ids, ids.length - 1);
                if (t != null) ids = t;
            }
            if (ids != null && Statics.theStatics.isDAK()) {
                t = removeDrossAt(ids, ids.length - 1);
                if (t != null) ids = t;
            }
        }
        if (ids != null) {
            String[] t = findMatchingSynonym(unit, ids, div);
            if (t != null) {
                if (t[0].charAt(0) == '!') {
                    factors = t[0].substring(1);
                    t = PieceReader.remove(t, 0);
                }
                ids = t;
            }
            if (origDiv != null && Statics.theStatics.isCaseBlue()) {
                t = removeDrossAt(ids, ids.length - 1);
                if (t != null) {
                    if (t.length != 0) {
                        ids = t;
                    }
                }
            }
        }
        if (ids != null) {
            id = reader.merge(ids, data.unitNameFiller);
            if (id.length() != 0 && id.charAt(0) == '#') id = id.substring(1);
        }
        if (div != null) {
            OcsCounter q = findPiece(curSide, Division.class, null, null, div, null, null);
            if (q == null) {
                input.writeError(true, "Unable to match division name");
                return null;
            }
            if (data.twoStageDivLookup) {
                if (Statics.theStatics.isDAK() && curSide == 1) {
                    String[] d = div;
                    div = new String[d.length];
                    System.arraycopy(d, 0, div, 0, d.length);
                    if (div[div.length - 1].equals("Division"))
                        div[div.length - 1] = "Div";
                    if (div.length == 3 && div[1].equals("Armored"))
                        div[1] = "";
                    if (div.length == 3 && div[1].equals("Infantry"))
                        div[1] = "";
                    if (div.length == 3 && div[1].equals("Inf")) div[1] = "";
                    if (div.length == 3 && div[1].equals("Para")) div[1] = "";
                    if (div.length == 3 && div[1].equals("Motorized"))
                        div[1] = "";
                    if (div[0].charAt(div[0].length() - 1) == '.') {
                        div[0] = div[0].substring(0, div[0].length() - 1);
                    }
                }
                dNameFull = reader.merge(div, data.divNameFiller);
                dName = q.division;
            } else {
                dName = reader.merge(div, data.divNameFiller);
                dNameFull = dName;
            }
            if (data.prefixDivToUnit) {
                id = dName + data.prefixEnd + id;
            }
            if (data.prefixFirstPartDivName) {
                if (data.altFromDiv && div.length > 1) {
                    altId = div[0] + data.prefixFill + div[1]
                            + data.prefixEnd + id;
                    if (div.length > 2) {
                        altId2 = div[0] + data.prefixFill + div[2]
                                 + data.prefixEnd + id;
                    }
                }
                id = div[0] + data.prefixEnd + id;
            }
            id = data.finalUnitName(curSide, id, dNameFull);
        }
        if (id != null) {
            id = data.finalName(curSide, id, type);
        }
        List<PiecePtr> matches = new ArrayList<PiecePtr>();
        List<PiecePtr> partial = new ArrayList<PiecePtr>();
        for (PiecePtr p : thePieces) {
            if (p == null) continue;
            if (p.piece == null) continue;
            if (curSide != -1 && p.piece.theSide != curSide) continue;
            if (ocsType1 != null) {
                if (!ocsType1.isInstance(p.piece)) {
                    if (ocsType2 == null) continue;
                    if (!ocsType2.isInstance(p.piece)) continue;
                }
            }
            if (factors != null && !factors.equals(p.factors)) continue;
            if (dName != null && !dName.equals(p.division)) continue;
            if (Statics.theStatics.isBlitzkriegLegend() && dName == null 
                && ocsType1 != Division.class && (p.division == null || !p.division.equals("")))
                continue;
            if (id == null || p.name.equals(id)
                || (altId != null && p.name.equals(altId))
                || altId2 != null && p.name.equals(altId2)) {
                matches.add(p);
                continue;
            }
            if (dName != null && data.postfixDivToUnit) {
                if (p.name.length() >= dNameFull.length() + id.length()
                    && p.name.startsWith(id)
                    && p.name.endsWith(dNameFull)) {
                    matches.add(p);
                    continue;
                }
            }
            if (dName != null && id.endsWith("Organic Truck")) {
                if (p.name.endsWith("Truck")) {
                    matches.add(p);
                    continue;
                }
            }
            if (p.name.startsWith(id) && p.name.charAt(id.length()) == ' '
                || id.startsWith(p.name) && id.charAt(p.name.length()) == ' ') {
                partial.add(p);
                continue;
            }
            if (altId != null) {
                if (p.name.startsWith(altId) && p.name.charAt(altId.length()) == ' '
                    || altId.startsWith(p.name) && altId.charAt(p.name.length()) == ' ') {
                    partial.add(p);
                    continue;
                }
            }
            if (altId2 != null) {
                if (p.name.startsWith(altId2) && p.name.charAt(altId2.length()) == ' '
                    || altId2.startsWith(p.name) && altId2.charAt(p.name.length()) == ' ') {
                    partial.add(p);
                    continue;
                }
            }
        }
        if (matches.isEmpty()) matches = partial;
        if (matches.isEmpty()) {
            if (noErrorReport) return null;
            input.writeError(true, "Unable to find matching piece");
            input.writeError(false, id + (factors == null ? "" : (" (" + factors + ")"))
                                    + (dName == null ? "" : (" in " + dNameFull)));
            return null;
        }
        if (matches.size() == 1) {
            PiecePtr r = matches.get(0);
            if (r.piece instanceof Division && ocsType1 != Division.class) {
                input.writeError(true, "Division counter found instead of unit");
            }
            input.writeError(false, id + (factors == null ? "" : (" (" + factors + ")"))
                                    + (dName == null ? "" : (" in " + dNameFull)) + " matched by");
            input.writeError(false, "Name = " + r.name + " image = " + r.image
                                    + " (" + r.factors + ") " + r.division);
            if (r.piece instanceof Division && !r.added && !this.noDivCounters) {
                reader.addPiece((OcsCounter) PieceCloner.getInstance().clonePiece(r.piece));
                r.added = true;
                if (Statics.theStatics.isCaseBlue()) {
                    if (r.name.equals("LAH")) {
                        reader.addPiece(findPiece(curSide, new String[]{"LAH"}, "4-4-3", new String[]{"LAH"}));
                    }
                }
            }
            return (OcsCounter) PieceCloner.getInstance().clonePiece(r.piece);
        }
        if (!Statics.theStatics.isBlitzkriegLegend()) {
            int i;
            for (i = 1; i < matches.size(); i++) {
                PiecePtr s = matches.get(i - 1);
                PiecePtr t = matches.get(i);
                if (!t.name.equals(s.name)) break;
                if (!t.factors.equals(s.factors)) break;
                if (!t.division.equals(s.division)) break;
                if (t.piece.theSide != s.piece.theSide) break;
            }
            if (i == matches.size()) {
                PiecePtr r = matches.get(0);
                if (r.piece instanceof Division && ocsType1 != Division.class) {
                    input.writeError(true, "Division counter found instead of unit");
                }
                input.writeError(false, id + (factors == null ? "" : (" (" + factors + ")"))
                                        + (dName == null ? "" : (" in " + dNameFull)) + " matched by");
                input.writeError(false, "Name = " + r.name + " image = " + r.image
                                        + " (" + r.factors + ") " + r.division);
                return (OcsCounter) PieceCloner.getInstance().clonePiece(r.piece);
            }
        }
        input.writeError(true, "Multiple matching pieces found for Name = " + id
                               + (factors == null ? "" : (" (" + factors + ")"))
                               + (dName == null ? "" : (" in " + dNameFull)) + ":");
        for (PiecePtr r : matches) {
            input.writeError(false, "Name = " + r.name + " image = " + r.image
                                    + " (" + r.factors + ") " + r.division);
        }
        return null;
    }

    public boolean findDivision(int side, String[] div) {
        if (div != null) {
            String[] t = findMatchingSynonym(divs, div, null);
            if (t != null) {
                div = t;
            }
            noDivCounters = true;
            noErrorReport = true;
            OcsCounter q = findPiece(side, Division.class, null, null, div, null, null);
            noDivCounters = false;
            noErrorReport = false;
            if (q != null) return true;
        }
        return false;
    }

    public void addWholeDivision(int curSide, String[] div, PieceReader pr) {
        if (div == null) return;
        String[] t = findMatchingSynonym(divs, div, null);
        if (t != null) {
            div = t;
        }
        OcsCounter q = findPiece(curSide, Division.class, null, null, div, null, null);
        if (q == null) return;
        for (PiecePtr p : thePieces) {
            if (q == null) return;
            if (p == null) continue;
            if (p.piece == null) continue;
            if (curSide != -1 && p.piece.theSide != curSide) continue;
            if (p.piece instanceof Division) continue;
            if (q.division.equals(p.division)) {
                pr.addPiece(p.piece);
                input.writeError(false, "Added divisional unit: Name = " + p.name + " image = " + p.image
                                        + " (" + p.factors + ") " + p.division);
            }
        }
    }
}
