/*
 * $Id: BasicCommandEncoder.java 2893 2008-01-27 20:15:23Z uckelman $
 *
 * Copyright (c) 2000-2003 by Rodney Kinney
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

package com.btinternet.george973;

import VASSAL.build.module.BasicCommandEncoder;
import VASSAL.counters.Decorator;
import VASSAL.counters.GamePiece;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author george
 */
public class BasicCommandEncoderOverride  extends BasicCommandEncoder {
  
  private Map<String,DecoratorFactory> decoratorFactoriesa =
  new HashMap<String,DecoratorFactory>();

  public BasicCommandEncoderOverride () {
    super();
    decoratorFactoriesa.put(Blind.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new Blind(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteCel.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteCel(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteConceal.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteConceal(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteHide.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteHide(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteHits.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteHits(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteTraitBase.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteTraitBase(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteSupply.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteSupply(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteIncomplete.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteIncomplete(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteAps.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteAps(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteRail.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteRail(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteDest.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteDest(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteFlipSupply.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteFlipSupply(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteArtDisp.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteArtDisp(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteErsatz.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteErsatz(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteBuild.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteBuild(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteMarkerHide.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteMarkerHide(type, inner);
      }
    });
    decoratorFactoriesa.put(TiteMarkerConceal.ID, new DecoratorFactory() {
      public Decorator createDecorator(String type, GamePiece inner) {
        return new TiteMarkerConceal(type, inner);
      }
    });
    
  }


  public Decorator createDecorator(String type, GamePiece inner) {
    Decorator d = null;
    String prefix = type.substring(0,type.indexOf(';')+1);
    if (prefix.length() == 0) {
      prefix = type;
    }
    DecoratorFactory f  = decoratorFactoriesa.get(prefix);
    if (f != null) {
      return f.createDecorator(type, inner);
    }
    return super.createDecorator( type, inner);
  }
  
}
