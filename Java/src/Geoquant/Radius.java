package Geoquant;

import java.util.HashMap;

import util.GeoMath;
import util.Matrix;

import Triangulation.Edge;
import Triangulation.Vertex;
import Triangulation.Triangulation;

public class Radius extends Geoquant {
  private static HashMap<TriPosition, Radius> Index = new HashMap<TriPosition, Radius>();
  private static NEHR.SecondPartial[][] nehr_rad_rad;
  private static NEHR.SecondPartial[][] nehr_rad_eta;
  private HashMap<TriPosition, Partial> PartialIndex;
  private Vertex v;
  
  private Radius(Vertex v) {
    super(v); // ALWAYS have to call this first
    this.v = v;
    PartialIndex = new HashMap<TriPosition, Partial>();
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
  
  private static void buildPartials() {
    int vertSize = Triangulation.vertexTable.size();
    int edgeSize = Triangulation.edgeTable.size();
    int i, j;
    
    if(nehr_rad_rad == null) {
      nehr_rad_rad = new NEHR.SecondPartial[vertSize][vertSize];
      i = 0;
      for(Vertex v1 : Triangulation.vertexTable.values()) {
        j = 0;
        for(Vertex v2 : Triangulation.vertexTable.values()) {
          nehr_rad_rad[i][j] = NEHR.secondPartialAt(v1, v2);
          j++;
        }
        i++;
      }
    }
    
    if(nehr_rad_eta == null) {
      nehr_rad_eta = new NEHR.SecondPartial[vertSize][edgeSize];
      i = 0;
      for(Vertex v1 : Triangulation.vertexTable.values()) {
        j = 0;
        for(Edge e1 : Triangulation.edgeTable.values()) {
          nehr_rad_eta[i][j] = NEHR.secondPartialAt(v1, e1);
          j++;
        }
        i++;
      }
    }
  }
  
  private static void calculatePartials() {
    int size = nehr_rad_rad.length;
    Matrix nehr_rad_partials = new Matrix(size, size);
    double[] nehr_eta_partials = new double[size];
    double[] values = new double[size];
    
    int j = 0;
    int i;
    for(Edge e : Triangulation.edgeTable.values()) {
      for(i = 0; i < size; i++) {
        nehr_eta_partials[i] = -1 * nehr_rad_eta[i][j].getValue();
      }
      
      for(i = 0; i < size; i++) {
        for(int k = 0; k < size; k++) {
          nehr_rad_partials.m[i][k]= nehr_rad_rad[i][k].getValue();
        }
      }
            
      values = GeoMath.LinearEquationsSolver(nehr_rad_partials, nehr_eta_partials);
      
      i = 0;
      for(Vertex v : Triangulation.vertexTable.values()) {
        Radius.At(v).partialAt(e).setValue(values[i]);
        i++;
      }
      j++;
    }
  }
  
  public Partial partialAt(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(e);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public class Partial extends Geoquant {

    private Partial(Edge e) {
      super(e);
      if(nehr_rad_rad == null || nehr_rad_eta == null) {
        buildPartials();
      }
      for(int i = 0; i < nehr_rad_rad.length; i++) {
        for(int j = 0; j < nehr_rad_rad[i].length; j++) {
          nehr_rad_rad[i][j].addObserver(this);
        }
      }
      for(int i = 0; i < nehr_rad_eta.length; i++) {
        for(int j = 0; j < nehr_rad_eta[i].length; j++) {
          nehr_rad_eta[i][j].addObserver(this);
        }
      }
    }
    
    protected void recalculate() {
      calculatePartials();
    }

    protected void remove() {
      deleteDependents();
      for(int i = 0; i < nehr_rad_rad.length; i++) {
        for(int j = 0; j < nehr_rad_rad[i].length; j++) {
          nehr_rad_rad[i][j].deleteObserver(this);
        }
      }
      for(int i = 0; i < nehr_rad_eta.length; i++) {
        for(int j = 0; j < nehr_rad_eta[i].length; j++) {
          nehr_rad_eta[i][j].deleteObserver(this);
        }
      }
      PartialIndex.remove(pos);
    }
    
    public String toString() {
      return "Radius@[" + v + "]" + "w.r.t" + location + "=" + getValue();
    }
    
  }

}
