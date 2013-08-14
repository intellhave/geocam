package triangulation;

import java.awt.Color;

import de.jreality.scene.Appearance;

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
  
  public boolean isNegative() {
    return negative;
  }
  
  public void setNegativity(boolean negativity) {
    negative = negativity;
  }
  
  public void switchPolarity() {
    negative = !negative;
  }

  public Color getColor() {
    return color;
  }

  public boolean hasColor() {
    return color != null;
  }
  
  public void setColor(Color c) {
    color = c;
  }
  
  private Appearance app = null;
  public Appearance getAppearance(){
	  return app;
  }
  public boolean hasAppearance(){
	  return (app != null);
  }
  public void setAppearance(Appearance app){
	  this.app = app;
  }
  
  public boolean sameOrientation(Edge e) {
    int i1=0;
    int i2=0;
    Vertex ev=e.getLocalVertices().get(0);
    for (Vertex v: this.getLocalVertices()){
      if (ev.equals(v)) break;
      i1++;
    }
    ev = e.getLocalVertices().get(1);
    for (Vertex v: this.getLocalVertices()){
      if (ev.equals(v)) break;
      i2++;
    }
//    System.out.println("i1 = "+i1);
//    System.out.println("i2 = "+i2);
    if ((i1==0 && i2==1)||(i1==1 && i2==2)||(i1==2 && i2==0)) return false;
    else return true;
  }
}
