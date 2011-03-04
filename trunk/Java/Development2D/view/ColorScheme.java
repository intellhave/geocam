package view;

import java.awt.Color;

import development.Development.DevelopmentNode;

import triangulation.Face;
import triangulation.Triangulation;


public class ColorScheme {
  public enum schemes {
    DEPTH, FACE
  };

  private schemes scheme;

  public ColorScheme(schemes s) {
    scheme = s;
  }

  public Color getColor(DevelopmentNode node) {
    if (scheme == schemes.FACE) {
      
      Face f = node.getFace();
      if(f.hasColor()) return f.getColor();
      return Color.getHSBColor((float) f.getIndex()
          / (float) Triangulation.faceTable.size(), 0.5f, 0.9f);
      
    } else {
      return Color.getHSBColor((float) node.getDepth() / 15f, 0.5f, 0.9f);
    }
  }

  public schemes getSchemeType() {
    return scheme;
  }
}
