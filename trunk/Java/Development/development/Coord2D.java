package development;

import java.util.HashMap;

import Triangulation.Face;
import Triangulation.Vertex;
import Triangulation.StdFace;
import Geoquant.*;

public class Coord2D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Coord2D> Index = new HashMap<TriPosition, Coord2D>();

  private Length lij;
  private Length ljk;
  private Length lik;
  private Angle ai;

  //fix an ordering (the one from stdface)
  //coords computed differently depending on the index in this ordering (stdFIndex)
  private int stdFIndex; 
  //2d point holding coords for this vertex/face pair
  private Point coord;
  
  public Coord2D(Vertex v, Face f) {
    super(v,f);
    StdFace stdF = new StdFace(f);
    lij = Length.At(stdF.e12);
    ljk = Length.At(stdF.e23);
    lik = Length.At(stdF.e13);
    ai = Angle.At(stdF.v1, f);
    
    if(stdF.v1 == v){ stdFIndex = 0; }
    else if(stdF.v2 == v){ stdFIndex = 1; }
    else if(stdF.v3 == v){ stdFIndex = 2; }
    
    lij.addObserver(this);
    ljk.addObserver(this);
    lik.addObserver(this);
    ai.addObserver(this);
  }
  
  protected void recalculate() {

    if(stdFIndex == 0){
      
      coord = new Point(new double[] {0, 0});
      
    }else if(stdFIndex == 1){
      
      double l1 = lij.getValue();
      coord = new Point(new double[] {l1, 0});
      
    }else if(stdFIndex == 2){
      
      double l3 = lik.getValue();
      double t = ai.getValue();
      coord = new Point(new double[] {l3*Math.cos(t), l3*Math.sin(t)});
    }
    
    value = 0; //unused
  }

  public void remove() {
    deleteDependents();
    lij.deleteObserver(this);
    ljk.deleteObserver(this);
    lik.deleteObserver(this);
    ai.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static Coord2D At(Vertex v, Face f) {
    TriPosition T = new TriPosition(v.getSerialNumber(), f.getSerialNumber());
    Coord2D q = Index.get(T);
    if(q == null) {
      q = new Coord2D(v, f);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v, Face f) {
    return At(v, f).getValue();
  }
  
  //like getValue(), but returns coordinate point
  public Point getCoord() {
    double d = getValue(); //used to invoke recalculate if invalid
    return coord; 
  }
  //like valueAt(), but returns coordinate point
  public static Point coordAt(Vertex v, Face f) {
    return At(v,f).getCoord();
  }
  
  
  

}
