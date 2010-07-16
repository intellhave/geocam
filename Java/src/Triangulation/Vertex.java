package Triangulation;

public class Vertex extends Simplex {

  public Vertex(int index) {
    super(index);
  }

  public int getDegree() {
    return localEdges.size();
  }
  
  public Edge getEdge(Vertex v){
    Edge e = null;
      for(Edge e1 : this.getLocalEdges()){
        for(Edge e2 : v.getLocalEdges()){
          if(e1.equals(e2)){
            e = e1;
          }
        }
      }
    return e;
  }
}
