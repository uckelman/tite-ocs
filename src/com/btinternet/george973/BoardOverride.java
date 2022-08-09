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

import VASSAL.build.Buildable;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.module.map.boardPicker.board.mapgrid.GridNumbering;
import VASSAL.build.module.map.boardPicker.board.mapgrid.RegularGridNumbering;
import VASSAL.build.module.map.boardPicker.board.HexGrid;
import VASSAL.build.module.map.boardPicker.board.MapGrid;
import VASSAL.build.module.map.boardPicker.board.RegionGrid;
import VASSAL.build.module.map.boardPicker.board.SquareGrid;
import VASSAL.build.module.map.boardPicker.board.ZonedGrid;

import VASSAL.configure.VisibilityCondition;
import VASSAL.configure.StringArrayConfigurer;

import VASSAL.tools.ErrorDialog;
import VASSAL.tools.imageop.ImageOp;
import VASSAL.tools.imageop.Op;
import VASSAL.tools.imageop.Repainter;
import VASSAL.tools.imageop.ScaleOp;
import VASSAL.tools.imageop.SourceOp;

import VASSAL.tools.image.LabelUtils;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author george
 */
public class BoardOverride extends Board {
  
  protected Rectangle selection = new Rectangle(0,0,500,500);
  
  String[] labels = new String[0];
  
  public Rectangle bounds() {
    if (imageFile != null && boardImageOp != null && !fixedBoundaries) {
      boundaries.setSize(boardImageOp.getSize());
      boundaries = boundaries.intersection(selection);
      boundaries.setLocation(0, 0);

      if (magnification != 1.0) {
        boundaries.setSize((int)Math.round(magnification*boundaries.width),
                           (int)Math.round(magnification*boundaries.height));
      }

      fixedBoundaries = true;
    } else {
      boundaries.width = selection.width;
      boundaries.height = selection.height;
    }
    return new Rectangle(boundaries);
  }

  public static final String X_ORIGIN = "xOrigin";
  public static final String Y_ORIGIN = "yOrigin";
  public static final String LABELS = "labels";

  public String[] getAttributeNames() {
    return new String[] {
      NAME,
      IMAGE,
      REVERSIBLE,
      WIDTH,
      HEIGHT,
      COLOR,
      X_ORIGIN,
      Y_ORIGIN,
      LABELS
    };
  }

  public String[] getAttributeDescriptions() {
    return new String[] {
      "Board name:  ",
      "Board image:  ",
      "Reversible:  ",
      "Board width:  ",
      "Board height:  ",
      "Background color:  ",
      "X Origin in image: ",
      "Y Origin in image: ",
      "Labels"
    };
  }
  
  public static String getConfigureTypeName() {
    return "Board Part Image";
  }

  public Class<?>[] getAttributeTypes() {
    return new Class<?>[] {
      String.class,
      Image.class,
      Boolean.class,
      Integer.class,
      Integer.class,
      Color.class,
      Integer.class,
      Integer.class,
      String[].class
    };
  }
  
  public Class<?>[] getAllowableConfigureComponents() {
    return new Class<?>[] {
      HexGrid.class,
      SquareGrid.class,
      RegionGrid.class,
      ZonedGrid.class
    };
  }



  public VisibilityCondition getAttributeVisibility(String name) {
    if (REVERSIBLE.equals(name)) {
      return new VisibilityCondition() {
        public boolean shouldBeVisible() {
          return imageFile != null;
        }
      };
    }
    else if (COLOR.equals(name)) {
      return new VisibilityCondition() {
        public boolean shouldBeVisible() {
          return imageFile == null;
        }
      };
    }
    else {
      return null;
    }
  }

 public String getAttributeValueString(String key) {
    if (WIDTH.equals(key)) {
      return String.valueOf(selection.width);
    }
    else if (HEIGHT.equals(key)) {
      return String.valueOf(selection.height);
    }
    if (X_ORIGIN.equals(key)) {
      return String.valueOf(selection.x);
    }
    else if (Y_ORIGIN.equals(key)) {
      return String.valueOf(selection.y);
    }
    else if (LABELS.equals(key)) {
      return StringArrayConfigurer.arrayToString(labels);
    }
    if (Board.COLOR.equals(key) && color == null ) color = Color.WHITE;
    return super.getAttributeValueString(key);
  }

  public void setAttribute(String key, Object val) {
    if (WIDTH.equals(key)) {
      if (val instanceof String) {
        val = Integer.valueOf((String) val);
      }
      if (val != null) {
        selection.setSize(((Integer) val).intValue(), selection.height);
      }
      return;
    } else if (HEIGHT.equals(key)) {
      if (val instanceof String) {
        val = Integer.valueOf((String) val);
      }
      if (val != null) {
        selection.setSize(selection.width, ((Integer) val).intValue());
      }
      return;
    }
    if (X_ORIGIN.equals(key)) {
      if (val instanceof String) {
        val = Integer.valueOf((String) val);
      }
      if (val != null) {
        selection.setLocation(((Integer) val).intValue(), selection.y);
      }
      return;
    } else if (Y_ORIGIN.equals(key)) {
      if (val instanceof String) {
        val = Integer.valueOf((String) val);
      }
      if (val != null) {
        selection.setLocation(selection.x, ((Integer) val).intValue());
      }
      return;
    } else if (LABELS.equals(key)) {
      if ( val instanceof String[] ) labels = (String[])val;
      else if ( val instanceof String ) labels = StringArrayConfigurer.stringToArray((String)val);
      return;
    }
    super.setAttribute(key, val);
  }
  
  private ConcurrentMap<Point,Future<BufferedImage>> requested =
    new ConcurrentHashMap<Point,Future<BufferedImage>>();


  private java.util.Map<Point,Float> alpha =
    new ConcurrentHashMap<Point,Float>();
  
  private static Comparator<Point> tileOrdering = new Comparator<Point>() {
    public int compare(Point t1, Point t2) {
      if (t1.y < t2.y) return -1;
      if (t1.y > t2.y) return  1;
      return t1.x - t2.x;
    }
  };

  public void drawRegion(final Graphics g,
                         final Point location,
                         Rectangle visibleRect,
                         double zoom,
                         final Component obs) {
    zoom *= magnification;
    final Rectangle bounds =
      new Rectangle(location.x, location.y,
                    Math.round(boundaries.width * (float) zoom),
                    Math.round(boundaries.height * (float) zoom));

    final Point zSelection = new Point (Math.round(selection.x * (float)zoom),
                                        Math.round(selection.y * (float)zoom));
    
    
    if (visibleRect.intersects(bounds)) {
      visibleRect = visibleRect.intersection(bounds);
      if (boardImageOp != null) {
        final ImageOp op;
        if (zoom == 1.0 && !reversed) {
          op = boardImageOp;
        }
        else {
          if (scaledImageOp == null || scaledImageOp.getScale() != zoom) {
            scaledImageOp = Op.scale(boardImageOp, zoom);
          }
          op = reversed ? Op.rotate(scaledImageOp, 180) : scaledImageOp;
        }

        final Rectangle r = new Rectangle(visibleRect.x - location.x + zSelection.x,
                                          visibleRect.y - location.y + zSelection.y,
                                          visibleRect.width,
                                          visibleRect.height);
        final int ow = op.getTileWidth();
        final int oh = op.getTileHeight();

        final Point[] tiles = op.getTileIndices(r);

        for (Point tile : tiles) {
          // find tile position
          final int tx = location.x - zSelection.x + tile.x*ow;
          final int ty = location.y - zSelection.y + tile.y*oh;

          // find actual tile size
          final int tw = Math.min(ow, bounds.width + zSelection.x - tile.x*ow);
          final int th = Math.min(oh, bounds.height + zSelection.y - tile.y*oh);
          
          final Repainter rep = obs == null ? null :
            new Repainter(obs, tx, ty, tw, th);

          try {
            final Future<BufferedImage> fim =
              op.getFutureTile(tile.x, tile.y, rep);

            if (obs == null) {
              drawTile(g, fim, tx, ty, obs);
            }
            else {
              if (fim.isDone()) {
                if (requested.containsKey(tile)) {
                  requested.remove(tile);
                  final Point t = tile;

                  final Animator a = new Animator(100,
                    new TimingTargetAdapter() {
                      @Override
                      public void timingEvent(float fraction) {
                        alpha.put(t, fraction);
                        obs.repaint(tx, ty, tw, th); 
                      }
                    }
                  );

                  a.setResolution(20);
                  a.start();
                }
                else {
                  Float a = alpha.get(tile);
                  if (a != null && a < 1.0f) {
                    final Graphics2D g2d = (Graphics2D) g;
                    final Composite oldComp = g2d.getComposite();
                    g2d.setComposite(
                      AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a));
                    drawTile(g2d, fim, tx, ty, obs);
                    g2d.setComposite(oldComp);
                  }
                  else {
                    alpha.remove(tile);                   
                    drawTile(g, fim, tx, ty, obs);
                  } 
                }
              }
              else {
                requested.putIfAbsent(tile, fim);
              }
            }
          }
// FIXME: should getTileFuture() throw these? Yes, probably, because it's
// synchronous when obs is null.
          catch (CancellationException e) {
            // FIXME: bug until we permit cancellation
            ErrorDialog.bug(e);
          }
          catch (ExecutionException e) {
            // FIXME: bug until we figure out why getTileFuture() throws this
            ErrorDialog.bug(e);
          }
        }

        for (Point tile : requested.keySet().toArray(new Point[0])) {
          if (Arrays.binarySearch(tiles, tile, tileOrdering) < 0) {
            requested.remove(tile);
          }
        }

/*
        final StringBuilder sb = new StringBuilder();
        for (Point tile : requested.keySet().toArray(new Point[0])) {
          if (Arrays.binarySearch(tiles, tile, tileOrdering) < 0) {
            final Future<Image> fim = requested.remove(tile);
            if (!fim.isDone()) {
              sb.append("(")
                .append(tile.x)
                .append(",")
                .append(tile.y)
                .append(") ");
            }
          }
        }
        if (sb.length() > 0) {
          sb.insert(0, "cancelling: ").append("\n");
          System.out.print(sb.toString());
        }
*/
      }
      else {
        if (color != null) {
          g.setColor(color);
          g.fillRect(visibleRect.x, visibleRect.y,
                     visibleRect.width, visibleRect.height);
        }
        else {
          g.clearRect(visibleRect.x, visibleRect.y,
                      visibleRect.width, visibleRect.height);
        }
      }

      if (grid != null) {
        grid.draw(g, bounds, visibleRect, zoom, reversed);
        for ( int z = 0; z < labels.length; z++ ) {
          GridNumbering a = grid.getGridNumbering();
          Point pt = null;
          boolean Hdr = labels[z].charAt(0) == '+';
          if ( a instanceof RegularGridNumbering ) {
            pt = ((RegularGridNumbering)a).getCenterPoint( Hdr ? 1 : 2, z+1);
            LabelUtils.drawLabel(g, labels[z].substring(1), pt.x, pt.y,
                    new Font("Dialog", Font.PLAIN, (int)((Hdr ? 20 : 16 ) * zoom )), LabelUtils.RIGHT, LabelUtils.CENTER,
                    Color.BLACK, color != null ? color : Color.WHITE,
                    color != null ? color : Color.WHITE);
          }
        }
      }
    }
  }


}
