

package geoquant;

import geoquant.Curvature3D.Sum;

import java.util.HashMap;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Simplex;
import triangulation.Vertex;


public class CurvatureTube extends Geoquant {
  // Index map
  private static HashMap<TriPosition, CurvatureTube> Index = new HashMap<TriPosition, CurvatureTube>();
  private static Sum total = null;
  // Needed geoquants
  private Simplex v;
  private Simplex sim;
  private Angle a;
  
  public CurvatureTube(Simplex v, Simplex sim) {
    super(sim);
    this.v = v;
    this.sim = sim;
    if ((v instanceof Vertex) && (sim instanceof Face)){
      a = Angle.at((Vertex) v, (Face) sim);
      a.addObserver(this);
    }
  }
  
  protected void recalculate() {
    if (v instanceof Vertex){
      if (sim instanceof Vertex)
        value = 2 * Math.PI * sim.getMultiplicity();
      else if (sim instanceof Edge)
        value = Math.PI * sim.getMultiplicity();
      else if (sim instanceof Face)
        value = (Math.PI - a.getValue())*sim.getMultiplicity();
    }
    }
    
  
  public void remove() {
    deleteDependents();
    if ((v instanceof Vertex) && (sim instanceof Face)){
      a.deleteObserver(this);
    }
    Index.remove(pos);
  }
  
  public static CurvatureTube at(Simplex v, Simplex sim) {
    TriPosition T = new TriPosition(v.getSerialNumber(), sim.getSerialNumber());
    CurvatureTube q = Index.get(T);
    if(q == null) {
      q = new CurvatureTube(v,sim);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static CurvatureTube At(Simplex v, Simplex sim) {
    TriPosition T = new TriPosition(v.getSerialNumber(), sim.getSerialNumber());
    CurvatureTube q = Index.get(T);
    if(q == null) {
      q = new CurvatureTube(v,sim);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Simplex v, Simplex sim) {
    return at(v, sim).getValue();
  }
}