/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS;

import VASSAL.build.GameModule;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import java.awt.Point;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cantab.hayward.george.OCS.Parsing.MarkerReader;

/**
 *
 * @author george
 */
public class OcsHexZone extends Zone {

    public static final String HEX = "hex";

    public OcsHexZone() {
        super();
    }

    @Override
    public String[] getAttributeNames() {
        String [] s = super.getAttributeNames();
        String [] t = new String[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = HEX;
        return t;
    }

    @Override
    public String[] getAttributeDescriptions() {
        String [] s = super.getAttributeDescriptions();
        String [] t  = new String[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = "Hex: ";
        return t;
    }

    @Override
    public Class<?>[] getAttributeTypes() {
        Class [] s = super.getAttributeTypes();
        Class [] t = new Class[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = String.class;
        return t;
    }

    public static String getConfigureTypeName() {
        return "OCS Hex Zone";
    }

    Point theHex = null;
    String hexName = "";

    @Override
    public String getAttributeValueString(String key) {
        if (key.equals(HEX)) {
            return hexName;
        }
        return super.getAttributeValueString(key);
    }

    @Override
    public void setAttribute(String key, Object val) {
        if (key.equals(HEX)) {
            if (!(val instanceof String)) return;
            hexName = (String)val;
        } else {
            super.setAttribute(key, val);
        }
    }

    public Point getHex() {
        Statics.theStatics.buildHexZoneList();
        return theHex;
    }

    void convertHexNameToPoint() {
        String h = hexName;
        if (Statics.theStatics.isReluctantEnemies()) {
            h = "Z" + h;
        }
        Pattern hexRef = Pattern.compile("(.+?)(\\d+)\\.(\\d+)");
        Matcher m = hexRef.matcher(h);
        if (m.matches()) {
            String mapId = m.group(1);
            int row = Integer.parseInt(m.group(2));
            int col = Integer.parseInt(m.group(3));
            Point q = new Point();
            String s = MarkerReader.resolveMapHex(mapId, row, col, q);
            if (s == null) {
                theHex = q;
                return;
            }
            GameModule.getGameModule().getChatter().show("Ocs Hex Zone "
                    + getConfigureName() + ": " + s);
            theHex = null;
        }
        GameModule.getGameModule().getChatter().show("Ocs Hex Zone "
                + getConfigureName() + ": Invalid hex - " + hexName);
    }

}
