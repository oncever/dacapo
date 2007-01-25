/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.awt.Color;
import java.awt.Stroke;

import edu.anu.thylacine.graph.graphs.formats.LineColour;
import edu.anu.thylacine.graph.graphs.formats.Lines;

class PerfLineColour extends LineColour {
  
  private static final Color[] colours = new Color[] {
    Color.RED,
    Color.BLUE,
    Color.GREEN,
    Color.BLACK,
    Color.ORANGE,
    Color.YELLOW,
    Color.PINK,
    Color.DARK_GRAY,
    Color.LIGHT_GRAY
  };
  
  private static final Stroke[] strokes =  new Stroke[] {
    Lines.SOLID,
    Lines.DOTTED,
    Lines.DASH1
  };
  
  private final int groupSize;
  
  PerfLineColour(int groupSize) {
    this.groupSize = groupSize;
  }
  
  public Color getColour(int column) {
    int group = column / groupSize;
    Color colour = colours[group % colours.length];
    return colour;
  }
  public Stroke getStroke(int column) {
    int member = column % groupSize;
    return strokes[member % strokes.length];
  }
}