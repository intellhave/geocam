package triangulation;

public class StdEdge {
  public Vertex v1;
  public Vertex v2;
  
  public StdEdge(Edge e, Vertex v) {
    v1 = v;
    for(Vertex w : e.getLocalVertices()) {
      if(w != v) {
        v2 = w;
      }
    }
  }
  
  public StdEdge(Edge e) {
    this(e, e.getLocalVertices().get(0));
  }
}
