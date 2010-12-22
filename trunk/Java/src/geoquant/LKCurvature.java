package geoquant;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import triangulation.Face;
import triangulation.Simplex;
import triangulation.Triangulation;
import triangulation.Vertex;
import triangulation.Edge;
import triangulation.Tetra;
import geoquant.CurvatureTube;

public class LKCurvature extends Geoquant {
  //Index map
  private static HashMap<TriPosition, LKCurvature> Index = new HashMap<TriPosition, LKCurvature>();
  
  private Vertex v;
  private List<Edge> edges;
  private List<Face> faces;
  
  
  public LKCurvature(Vertex v) {
    super(v);
    this.v = v;
    this.edges = v.getLocalEdges();
    this.faces = v.getLocalFaces();
  }
  
  protected void recalculate() {

    value = CurvatureTube.valueAt(v, v);

    for(Edge e : edges) {
      value += CurvatureTube.valueAt(v, e);
    }
    
    for(Face f : faces) {
      value += CurvatureTube.valueAt(v, f);
    }
  }
 
  public void remove() {
    deleteDependents();
  }
  
  public static LKCurvature at(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    LKCurvature q = Index.get(T);
    if(q == null) {
      q = new LKCurvature(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static LKCurvature At(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    LKCurvature q = Index.get(T);
    if(q == null) {
      q = new LKCurvature(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return at(v).getValue();
  }
}
