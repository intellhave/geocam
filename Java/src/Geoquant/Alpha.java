package Geoquant;

import java.util.HashMap;

import Triangulation.Vertex;

public class Alpha extends Geoquant {

  private static HashMap<TriPosition, Alpha> Index = new HashMap<TriPosition, Alpha>();
  
  private Alpha(Vertex v) {
    super(v); // ALWAYS have to call this first
    value = 0; // default
  }
  
  public static Alpha At(Vertex v) {
    TriPosition T = new TriPosition( v.getSerialNumber() );
    Alpha q = Index.get(T);
    if(q == null) {
      q = new Alpha(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return At(v).getValue();
  }
  
  protected void recalculate() {
    // This is empty for a alpha
    value = 0;
  }
 
  public void remove() {
    deleteDependents();
    Index.remove(pos);
  }

}
