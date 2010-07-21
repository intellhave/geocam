package triangulation;

public class Face extends Simplex {
  private boolean negative;
  public Face(int index) {
    super(index);
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
}
