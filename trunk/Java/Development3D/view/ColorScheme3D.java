package view;

import java.awt.Color;

import triangulation.Tetra;
import triangulation.Triangulation;
import development.Development3D.DevelopmentNode3D;


public class ColorScheme3D {
  public enum schemes {
    DEPTH, FACE
  };

  private schemes scheme;

  public ColorScheme3D(schemes s) {
    scheme = s;
  }

  public Color getColor(DevelopmentNode3D node) {
    if (scheme == schemes.FACE) {
      
      Tetra tetra = node.tetra_;
      if(tetra.hasColor()) return tetra.getColor();
      return Color.getHSBColor((float) tetra.getIndex()
          / (float) Triangulation.tetraTable.size(), 0.5f, 0.9f);
      
    } else {
      return Color.getHSBColor((float) node.getDepth() / 10f, 0.5f, 0.9f);
    }
  }

  public schemes getSchemeType() {
    return scheme;
  }
}
