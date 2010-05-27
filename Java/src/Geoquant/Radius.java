package Geoquant;

import java.util.HashMap;
import Triangulation.Vertex;

public class Radius extends Geoquant {
  private static HashMap<TriPosition, Radius> Index = new HashMap<TriPosition, Radius>();
  
  private Radius(Vertex v) {
    super(); // ALWAYS have to call this first
    
  }
  
  public static Radius At(Vertex v) {
    TriPosition T = new TriPosition( v.getSerialNumber() );
    Radius q = Index.get(T);
    if(q == null) {
      q = new Radius(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return At(v).getValue();
  }
  
  protected void recalculate() {
    // This is empty for a radius
  }

 
  protected void remove() {
    deleteDependents();
    Index.remove(pos);
  }

}
