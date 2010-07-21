package geoquant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import triangulation.*;


public class LCSC extends Geoquant {
  // Index map
  private static HashMap<TriPosition, LCSC> Index = new HashMap<TriPosition, LCSC>();
  
  // Needed geoquants
  private Curvature3D Curv;
  private LEHR LEHRtemp1;
  private LinkedList<Length> lengthList;
  
  public LCSC(Vertex v) {
    super(v);
    
    lengthList = new LinkedList<Length>();
    
    Curv = Curvature3D.At(v);
    Curv.addObserver(this);
    LEHRtemp1 = LEHR.getInstance();
    LEHRtemp1.addObserver(this);
    Length l;
    for(Edge e : v.getLocalEdges()) {
      l = Length.At(e);
      l.addObserver(this);
      lengthList.add(l);
    }
    
  }
  
  protected void recalculate() {
    double K_i = Curv.getValue();
    double LEHRtemp2 = LEHRtemp1.getValue();
    double L_i = 0;
    for(Length l : lengthList) {
      L_i += l.getValue();
    }

    value = K_i-LEHRtemp2*L_i / 2;

  }
 
  public void remove() {
    deleteDependents();
    Curv.deleteObserver(this);
    LEHRtemp1.deleteObserver(this);
    for(Length l : lengthList) {
      l.deleteObserver(this);
    }
    Index.remove(pos);
  }
  
  public static LCSC At(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    LCSC q = Index.get(T);
    if(q == null) {
      q = new LCSC(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return At(v).getValue();
  }

}
