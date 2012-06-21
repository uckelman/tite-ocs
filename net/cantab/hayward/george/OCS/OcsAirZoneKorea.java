/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.cantab.hayward.george.OCS;

import VASSAL.build.module.Map;
import VASSAL.counters.GamePiece;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author george
 */
public class OcsAirZoneKorea extends OcsAirZone {

    @Override
    public List<EntryPoint> getEntryPoints() {
        List<EntryPoint> ps = new ArrayList<EntryPoint>();
        for (String s: entries) {
            Point q = null;
            Map m = Statics.theMap;
            GamePiece[] h = m.getAllPieces();
            for ( GamePiece g : h) {
                if (g.getName().equals(s)) {
                    if (g.getParent() != null) {
                        q = g.getParent().getPosition();
                    } else {
                        q = g.getPosition();
                    }
                    ps.add(new EntryPoint(q, 0));
                }
            }
        }
        return ps;
    }

}
