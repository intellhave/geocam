package triangulation;

public class Edge extends Simplex {

  public Edge(int index) {
    super(index);
  }
  
  public boolean isBorder() {
    return localFaces.size() == 1;
  }
}
