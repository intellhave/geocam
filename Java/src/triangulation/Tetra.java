package triangulation;

import java.awt.Color;

public class Tetra extends Simplex {

  private Color color = null;
  
  public Tetra(int index) {
    super(index);
  }

  public Tetra(int index, Color c) {
    super(index);
    color = c;
  }
  
  public boolean hasColor() {
    return color != null;
  }
  
  public Color getColor() {
    return color;
  }
  
  public void setColor(Color c) {
    color = c;
  }
  
}
