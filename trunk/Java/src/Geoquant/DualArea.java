package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.Edge;
import Triangulation.StdTetra;
import Triangulation.Tetra;

public class DualArea extends Geoquant {
  //Index map
  private static HashMap<TriPosition, DualArea> Index = new HashMap<TriPosition, DualArea>();
   
  // Needed geoquants
  private LinkedList<Segment> segments;
  
  public DualArea(Edge e) {
    super();
    Segment s;
    segments = new LinkedList<Segment>();
    for(Tetra t : e.getLocalTetras()) {
      s = Segment.At(e, t);
      s.addDependent(this);
      segments.add(s);
    }
  }
  
  protected void recalculate() {
    value = 0;
    for(Segment s : segments) {
      value += s.getValue();
    }
  }
 
  protected void remove() {
    deleteDependents();
    for(Segment s : segments) {
      s.removeDependent(this);
    }
    Index.remove(pos);
  }
  
  public static DualArea At(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    DualArea p = Index.get(T);
    if (p == null) {
      p = new DualArea(e);
      p.pos = T;
      Index.put(T, p);
    }
    return p;
  }
  
  public static double valueAt(Edge e) {
    return At(e).getValue();
  }

  public static class Segment extends Geoquant {
    private static HashMap<TriPosition, Segment> SegmentIndex = new HashMap<TriPosition, Segment>();
    private EdgeHeight hij_k;
    private EdgeHeight hij_l;
    private FaceHeight hijk_l;
    private FaceHeight hijl_k;
    
    public Segment(Edge e, Tetra t) {
      StdTetra st = new StdTetra(t, e);
      hij_k = EdgeHeight.At(e, st.f123);
      hij_l = EdgeHeight.At(e, st.f124);
      hijk_l = FaceHeight.At(st.f123, t);
      hijl_k = FaceHeight.At(st.f124, t);
      
      hij_k.addDependent(this);
      hij_l.addDependent(this);
      hijk_l.addDependent(this);
      hijl_k.addDependent(this);
    }
    
    protected void recalculate() {
      double Hij_k = hij_k.getValue();
      double Hijk_l = hijk_l.getValue();
      double Hij_l = hij_l.getValue();
      double Hijl_k = hijl_k.getValue();

      value = 0.5*(Hij_k * Hijk_l + Hij_l * Hijl_k);
    }

    protected void remove() {
      deleteDependents();
      hij_k.removeDependent(this);
      hij_l.removeDependent(this);
      hijk_l.removeDependent(this);
      hijl_k.removeDependent(this);
      SegmentIndex.remove(pos);
    }
    
    public static Segment At(Edge e, Tetra t) {
      TriPosition T = new TriPosition(t.getSerialNumber());
      Segment s = SegmentIndex.get(T);
      if(s == null) {
        s = new Segment(e, t);
        s.pos = T;
        SegmentIndex.put(T, s);
      }
      return s;
    }
    
    public static double valueAt(Edge e, Tetra t) {
      return At(e, t).getValue();
    }
  }
}
