/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS;

import VASSAL.build.GameModule;
import VASSAL.configure.StringArrayConfigurer;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cantab.hayward.george.OCS.Parsing.MarkerReader;

/**
 *
 * @author george
 */
public class OcsAirZone extends OcsSideZone {

    public static final String ENTRY_POINTS = "entrypoints";

    public OcsAirZone() {
        super();
    }

    @Override
    public String[] getAttributeNames() {
        String [] s = super.getAttributeNames();
        String [] t = new String[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = ENTRY_POINTS;
        return t;
    }

    @Override
    public String[] getAttributeDescriptions() {
        String [] s = super.getAttributeDescriptions();
        String [] t  = new String[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = "Entry Points: ";
        return t;
    }

    @Override
    public Class<?>[] getAttributeTypes() {
        Class<?>[] s = super.getAttributeTypes();
        Class<?>[] t = new Class<?>[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = String[].class;
        return t;
    }

    String [] entries = new String[0];

    @Override
    public String getAttributeValueString(String key) {
        if (key.equals(ENTRY_POINTS))
            return StringArrayConfigurer.arrayToString(entries);
        return super.getAttributeValueString(key);
    }

    @Override
    public void setAttribute(String key, Object val) {
        if (key.equals(ENTRY_POINTS)) {
            if (val instanceof String[]) {
                entries = (String [])val;
                return;
            }
            if (!(val instanceof String)) return;
            entries = StringArrayConfigurer.stringToArray((String)val);
        } else {
            super.setAttribute(key, val);
        }
    }

    public static String getConfigureTypeName() {
        return "OCS Air Zone";
    }

    public static class EntryPoint {
        public Point coords;
        public int distance;

        public EntryPoint(Point p, int d) {
            coords = new Point(p);
            distance = d;
        }
    }

    public List<EntryPoint> getEntryPoints() {
        List<EntryPoint> ps = new ArrayList<EntryPoint>();
        for (String s: entries) {
            int dist = 0;
            Point q;
            if (s.indexOf('=') >= 0) {
                dist = Integer.parseInt(s.substring(s.indexOf('=')+1));
                s = s.substring(0, s.indexOf('='));
            }
            Pattern hexRef = Pattern.compile("(.+?)(\\d+)\\.(\\d+)");
            Matcher m = hexRef.matcher(s);
            if (m.matches()) {
                String mapId = m.group(1);
                int row = Integer.parseInt(m.group(2));
                int col = Integer.parseInt(m.group(3));
                q = new Point();
                String t = MarkerReader.resolveMapHex(mapId, row, col, q);
                if (t != null) {
                    GameModule.getGameModule().getChatter().show("Ocs Air Zone "
                    + getConfigureName() + ": " + t);
                    continue;
                }
            } else {
                GameModule.getGameModule().getChatter().show("Ocs Air Zone "
                + getConfigureName() + ": Invalid hex - " + s);
                continue;
            }
            ps.add(new EntryPoint(q, dist));
        }
        return ps;
    }
}
