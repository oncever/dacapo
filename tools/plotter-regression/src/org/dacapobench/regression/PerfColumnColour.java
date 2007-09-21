/*
 * (C) Copyright Department of Computer Science,
 * Australian National University. 2006
 */

package org.dacapobench.regression;

import java.awt.Color;
import java.awt.Stroke;

import edu.anu.thylacine.graph.graphs.formats.LineColour;
import edu.anu.thylacine.graph.graphs.formats.Lines;

class PerfColumnColour extends LineColour {
  
  private static final Color[] colours = new Color[] {
    Color.RED,
    Color.BLUE,
    Color.GREEN,
    Color.BLACK,
    Color.ORANGE,
    Color.YELLOW,
    Color.PINK,
    Color.DARK_GRAY,
    Color.LIGHT_GRAY,
    Color.CYAN,
    Color.MAGENTA,
    Color.GRAY
  };
  
  @Override
  public Color getColour(int row, int column) {
    Color colour = colours[row % colours.length];
    return colour;
  }
}