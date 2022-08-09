/*
 *
 * Copyright (c) 2008 by George Hayward
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License (LGPL) as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, copies are available
 * at http://www.opensource.org.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.btinternet.george973;

import VASSAL.build.module.map.CounterDetailViewer;
import VASSAL.tools.SequenceEncoder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import java.util.List;

import javax.swing.JComponent;


import VASSAL.counters.GamePiece;
import VASSAL.tools.image.LabelUtils;

import VASSAL.counters.Properties;

/**
 *
 * @author george
 */
public class CounterDetailViewerOverride  extends CounterDetailViewer {

  protected void drawGraphics(Graphics g, Point pt, JComponent comp, List<GamePiece> pieces) {

    fixBounds(pieces);

    if (bounds.width > 0) {

      Rectangle visibleRect = comp.getVisibleRect();
      bounds.x = Math.min(bounds.x, visibleRect.x + visibleRect.width - bounds.width);
      if (bounds.x < visibleRect.x)
        bounds.x = visibleRect.x;
      bounds.y = Math.min(bounds.y, visibleRect.y + visibleRect.height - bounds.height) - (isTextUnderCounters() ? 15 : 0);
      int minY = visibleRect.y + (textVisible ? g.getFontMetrics().getHeight() + 6 : 0);
      if (bounds.y < minY)
        bounds.y = minY;

      if (bgColor != null) {
        g.setColor(bgColor);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
      }
      if (fgColor != null) {
        g.setColor(fgColor);
        g.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 1, bounds.height + 1);
        g.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 3, bounds.height + 3);
      }
      Shape oldClip = g.getClip();

      int borderOffset = borderWidth;
      double graphicsZoom = graphicsZoomLevel;
      for (int i = 0; i < pieces.size(); i++) {
        // Draw the next piece
        // pt is the location of the left edge of the piece
        GamePiece piece = pieces.get(i);
        Rectangle pieceBounds = getBounds(piece);
        if (unrotatePieces) piece.setProperty(Properties.USE_UNROTATED_SHAPE, Boolean.TRUE);
        g.setClip(bounds.x - 3, bounds.y - 3, bounds.width + 5, bounds.height + 5);
        piece.setProperty(TiteTraitBase.ZOOM, Boolean.TRUE);
        piece.draw(g, bounds.x - (int) (pieceBounds.x * graphicsZoom) + borderOffset, bounds.y - (int) (pieceBounds.y * graphicsZoom) + borderWidth, comp,
            graphicsZoom);
        piece.setProperty(TiteTraitBase.ZOOM, Boolean.FALSE);
        if (unrotatePieces) piece.setProperty(Properties.USE_UNROTATED_SHAPE, Boolean.FALSE);
        g.setClip(oldClip);

        if (isTextUnderCounters()) {
          String text = counterReportFormat.getLocalizedText(piece);
          if (text.length() > 0) {
        	  int x = bounds.x - (int) (pieceBounds.x * graphicsZoom) + borderOffset;
        	  int y = bounds.y + bounds.height + 10;
        	  drawLabel(g, new Point(x, y), text, LabelUtils.CENTER, LabelUtils.CENTER);
          }
        }

        bounds.translate((int) (pieceBounds.width * graphicsZoom), 0);
        borderOffset += borderWidth;
      }
    }
  }

  protected Rectangle getBounds(GamePiece piece) {
    if (unrotatePieces) piece.setProperty(Properties.USE_UNROTATED_SHAPE, Boolean.TRUE);
    piece.setProperty(TiteTraitBase.ZOOM, Boolean.TRUE);
    Rectangle pieceBounds = piece.getShape().getBounds();
    piece.setProperty(TiteTraitBase.ZOOM, Boolean.FALSE);
    if (unrotatePieces) piece.setProperty(Properties.USE_UNROTATED_SHAPE, Boolean.FALSE);
    return pieceBounds;
  }
  
  protected void drawLabel(Graphics g, Point pt, String label, int hAlign, int vAlign) {

    if (label != null) {
      SequenceEncoder.Decoder st = new SequenceEncoder.Decoder (label, '\n' );
      Color labelFgColor = fgColor == null ? Color.black : fgColor;
      Graphics2D g2d = ((Graphics2D) g);
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      while ( st.hasMoreTokens()) {
        String s = st.nextToken();
        LabelUtils.drawLabel(g, s, pt.x, pt.y, new Font("Dialog", Font.PLAIN, fontSize), hAlign, vAlign, labelFgColor, bgColor, labelFgColor);
        pt.y += g.getFontMetrics().getHeight();
      }
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
  }



}
