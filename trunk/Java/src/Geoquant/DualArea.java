package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.Edge;
import Triangulation.StdTetra;
import Triangulation.Tetra;

public class DualArea extends Geoquant {
  //Index map
  private static HashMap<TriPosition, DualArea> Index = new HashMap<TriPosition, DualArea>();
  private static HashMap<TriPosition, Segment> SegmentIndex;
  
  // Needed geoquants
  private LinkedList<Segment> segments;
  private Edge e;
  
  public DualArea(Edge e) {
    super(e);
    this.e = e;
    SegmentIndex = new HashMap<TriPosition, Segment>();
    Segment s;
    segments = new LinkedList<Segment>();
    for(Tetra t : e.getLocalTetras()) {
      s = this.segment(t);
      s.addObserver(this);
      segments.add(s);
    }
  }
  
  protected void recalculate() {
    value = 0;
    for(Segment s : segments) {
      value += s.getValue();
    }
  }
 
  public void remove() {
    deleteDependents();
    for(Segment s : segments) {
      s.deleteObserver(this);
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
  
  public DualArea.Segment segment(Tetra t) {
    TriPosition T = new TriPosition(t.getSerialNumber());
    Segment s = SegmentIndex.get(T);
    if(s == null) {
      s = new Segment(t);
      s.pos = T;
      SegmentIndex.put(T, s);
    }
    return s;
  }

  public class Segment extends Geoquant {
    private EdgeHeight hij_k;
    private EdgeHeight hij_l;
    private FaceHeight hijk_l;
    private FaceHeight hijl_k;
    
    private Segment(Tetra t) {
      super(t);
      StdTetra st = new StdTetra(t, e);
      hij_k = EdgeHeight.At(e, st.f123);
      hij_l = EdgeHeight.At(e, st.f124);
      hijk_l = FaceHeight.At(st.f123, t);
      hijl_k = FaceHeight.At(st.f124, t);
      
      hij_k.addObserver(this);
      hij_l.addObserver(this);
      hijk_l.addObserver(this);
      hijl_k.addObserver(this);
    }
    
    protected void recalculate() {
      double Hij_k = hij_k.getValue();
      double Hijk_l = hijk_l.getValue();
      double Hij_l = hij_l.getValue();
      double Hijl_k = hijl_k.getValue();

      value = 0.5*(Hij_k * Hijk_l + Hij_l * Hijl_k);
    }

    public void remove() {
      deleteDependents();
      hij_k.deleteObserver(this);
      hij_l.deleteObserver(this);
      hijk_l.deleteObserver(this);
      hijl_k.deleteObserver(this);
      SegmentIndex.remove(pos);
    }
    
    public String toString() {
      return "DualArea@[" + e + "]" + "Segment" + location + "=" + getValue();
    }
  }
}
