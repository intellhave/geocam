package Triangulation;

public class Vertex extends Simplex {

  public Vertex(int index) {
    super(index);
  }

  public int getDegree() {
    return localEdges.size();
  }
}
