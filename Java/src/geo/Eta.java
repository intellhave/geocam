package geo;

import java.util.HashMap;

import triangulation.Edge;


public class Eta extends Geoquant {
  private static HashMap<TriPosition, Eta> Index = new HashMap<TriPosition, Eta>();
  
  private Eta(Edge e) {
    super(e); // ALWAYS have to call this first
    
  }
  
  public static Eta At(Edge e) {
    TriPosition T = new TriPosition( e.getSerialNumber() );
    Eta q = Index.get(T);
    if(q == null) {
      q = new Eta(e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e) {
    return At(e).getValue();
  }
  
  protected void recalculate() {
    // This is empty for an eta
  }

 
  public void remove() {
    deleteDependents();
    Index.remove(pos);
  }

}