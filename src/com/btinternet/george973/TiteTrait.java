/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.btinternet.george973;

import java.awt.Component;
import java.awt.Graphics;

import VASSAL.counters.Decorator;

/**
 *
 * @author george
 */
public abstract class TiteTrait extends Decorator  {
  
  abstract String getMyTiteStatus();
  
  abstract String getMyTiteRestrictedStatus();
  
  abstract int getMyNumberOfMarkers();
  
  abstract int myDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width );
  
  abstract int getMyRestrictedNumberOfMarkers();
  
  abstract int myRestrictedDrawMarkers(Graphics g, int x, int y, Component obs, double zoom, int width );
  
  String getTiteStatus () {
    String s = getMyTiteStatus();
    String t = ( piece instanceof TiteTrait )
             ? ((TiteTrait)piece).getTiteStatus() : null;
    if ( s == null ) return t;
    if ( t == null ) return s;
    return s + "\n" + t;
  }

  String getTiteRestrictedStatus () {
    String s = getMyTiteRestrictedStatus();
    String t = ( piece instanceof TiteTrait )
             ? ((TiteTrait)piece).getTiteRestrictedStatus() : null;
    if ( s == null ) return t;
    if ( t == null ) return s;
    return s + "\n" + t;
  }
  
  int getNumberOfMarkers() {
    int a = getMyNumberOfMarkers();
    int b = ( piece instanceof TiteTrait )
            ? ((TiteTrait)piece).getNumberOfMarkers() : 0;
    return a + b;
  }
  
  int drawMarkers( Graphics g, int x, int y, Component obs, double zoom, int width ) {
    int b = ( piece instanceof TiteTrait )
            ? ((TiteTrait)piece).drawMarkers(g, x, y, obs, zoom, width) : x;
    return myDrawMarkers(g, b, y, obs, zoom, width);
  }

  int getRestrictedNumberOfMarkers() {
    int a = getMyRestrictedNumberOfMarkers();
    int b = ( piece instanceof TiteTrait )
            ? ((TiteTrait)piece).getRestrictedNumberOfMarkers() : 0;
    return a + b;
  }
  
  int drawRestrictedMarkers( Graphics g, int x, int y, Component obs, double zoom, int width ) {
    int b = ( piece instanceof TiteTrait )
            ? ((TiteTrait)piece).drawRestrictedMarkers(g, x, y, obs, zoom, width) : x;
    return myRestrictedDrawMarkers(g, b, y, obs, zoom, width);
  }

  boolean isRestricted() {
    return (getOuter() instanceof TiteTrait)
            ? ((TiteTrait) getOuter()).isRestricted() : false;
  }

  boolean isConcealed() {
    if (getOuter() instanceof TiteTrait) {
      return ((TiteTrait) getOuter()).isConcealed();
    }
    return false;
  }

  void drawNation(Graphics g, int x, int y, Component obs, double zoom) {
    if (getOuter() instanceof TiteTrait) {
      ((TiteTrait) getOuter()).drawNation(g, x, y, obs, zoom);
    }
  }

  void drawArmy(Graphics g, int x, int y, Component obs, double zoom) {
    if (getOuter() instanceof TiteTrait) {
      ((TiteTrait) getOuter()).drawArmy(g, x, y, obs, zoom);
    }
  }

  void drawCorps(Graphics g, int x, int y, Component obs, double zoom) {
    if (getOuter() instanceof TiteTrait) {
      ((TiteTrait) getOuter()).drawCorps(g, x, y, obs, zoom);
    }
  }

  void drawOrigHits(Graphics g, int x, int y, Component obs, double zoom) {
    if (getOuter() instanceof TiteTrait) {
      ((TiteTrait) getOuter()).drawOrigHits(g, x, y, obs, zoom);
    }
  }

  int getCEL() {
    if (piece instanceof TiteTrait) {
      return ((TiteTrait) piece).getCEL();
    }
    return 0;
  }

  int getMaxAPs() {
    if (piece instanceof TiteTrait) {
      return ((TiteTrait) piece).getMaxAPs();
    }
    return 0;
  }

  boolean isExtended() {
    if (piece instanceof TiteTrait) {
      return ((TiteTrait) piece).isExtended();
    }
    return false;
  }
  
  void setAttachment( int army, int corps ) {
    if ( piece instanceof TiteTrait ) {
      ((TiteTrait)piece).setAttachment( army, corps);
    }
  }

  boolean isReduced() {
    if (getOuter() instanceof TiteTrait) {
      return ((TiteTrait) getOuter()).isReduced();
    }
    return false;
  }

  boolean isEntrained() {
    if (getOuter() instanceof TiteTrait) {
      return ((TiteTrait) getOuter()).isEntrained();
    }
    return false;
  }

  int getNation() {
    if (getOuter() instanceof TiteTrait) {
      return ((TiteTrait) getOuter()).getNation();
    }
    return 0;
  }

  int getArmy() {
    if (getOuter() instanceof TiteTrait) {
      return ((TiteTrait) getOuter()).getArmy();
    }
    return 0;
  }

  int getCorps() {
    if (getOuter() instanceof TiteTrait) {
      return ((TiteTrait) getOuter()).getCorps();
    }
    return 0;
  }
  
  boolean isDoing() {
    if ( getOuter() instanceof TiteTrait) {
      return ((TiteTrait) getOuter()).isDoing();
    }
    return false;
  }
  
  void setDoing( boolean now) {
    if ( getOuter() instanceof TiteTrait) {
      ((TiteTrait) getOuter()).setDoing( now );
    }
  }

  void setMoved( boolean now) {
    if ( getOuter() instanceof TiteTrait) {
      ((TiteTrait) getOuter()).setMoved( now );
    }
  }

}
