/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS;

import VASSAL.build.AutoConfigurable;
import VASSAL.build.module.map.boardPicker.board.mapgrid.Zone;
import VASSAL.configure.Configurer;
import VASSAL.configure.ConfigurerFactory;
import VASSAL.configure.StringEnumConfigurer;

/**
 *
 * @author george
 */
public class OcsSideZone extends Zone {
    
    public static final String SIDE = "side";

    public OcsSideZone() {
        super();
    }

    @Override
    public String[] getAttributeNames() {
        String [] s = super.getAttributeNames();
        String [] t = new String[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = SIDE;
        return t;
    }

    @Override
    public String[] getAttributeDescriptions() {
        String [] s = super.getAttributeDescriptions();
        String [] t  = new String[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = "Side: ";
        return t;
    }

    @Override
    public Class<?>[] getAttributeTypes() {
        Class [] s = super.getAttributeTypes();
        Class [] t = new Class[s.length + 1];
        System.arraycopy(s, 0, t, 0, s.length);
        t[s.length] = SideConfig.class;
        return t;
    }

    public static class SideConfig implements ConfigurerFactory {

        public Configurer getConfigurer(AutoConfigurable c, String key, String name) {
            return new StringEnumConfigurer(key, name, new String[]{
                        "Neutral",
                        Statics.theSides[0].name,
                        Statics.theSides[1].name
                    });
        }
    }

    public static String getConfigureTypeName() {
        return "OCS Side Zone";
    }

    String sideName = "Neutral";

    @Override
    public String getAttributeValueString(String key) {
        if (key.equals(SIDE))
            return sideName;
        return super.getAttributeValueString(key);
    }

    @Override
    public void setAttribute(String key, Object val) {
        if (key.equals(SIDE)) {
            if (!(val instanceof String)) return;
            sideName = (String)val;
        } else {
            super.setAttribute(key, val);
        }
    }

    int getSide() {
        if (sideName.equals(Statics.theSides[0].name))
            return 0;
        if (sideName.equals(Statics.theSides[1].name))
            return 1;
        return -1;
    }
}
