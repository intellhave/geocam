package triangulation;

import java.awt.Color;

public class Face extends Simplex {
  private boolean negative;
  private Color color = null;
  private int metaFace;
  private boolean hasMetaFace;
  
  public Face(int index) {
    super(index);
    hasMetaFace = false;
  }

  public Face(int index, Color c) {
    super(index);
    color = c;
  }
  public void setMetaFace(int mf){
	  this.metaFace = mf;
	  hasMetaFace = true;
  }
  
  public boolean hasMetaFace(){
	  return hasMetaFace;
  }
  
  public int getMetaFace(){
	  return metaFace;
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
  
  public void setColor(Color c) {
    color = c;
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
