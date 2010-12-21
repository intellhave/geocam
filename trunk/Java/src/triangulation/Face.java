package triangulation;

import java.awt.Color;

public class Face extends Simplex {
  private boolean negative;
  private Color color = null;
  
  public Face(int index) {
    super(index);
  }

  public Face(int index, Color c) {
    super(index);
    color = c;
  }
  
  public Color getColor() {
    return color;
  }

  public boolean isNegative() {
    return negative;
  }
  public void setNegativity(boolean negativity) {
    negative = negativity;
  }
  public void switchPolarity() {
    negative = !negative;
  }

  public boolean hasColor() {
    return color != null;
  }
}
