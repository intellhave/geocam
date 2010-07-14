package Geoquant;

import java.util.LinkedList;
import java.util.List;

import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class Geometry {
  
  public static enum Dimension{
    twoD, threeD;
  }
  
  private Geometry() {
  }
  
  public static List<Geoquant> getGeoquants(Class<? extends Geoquant> c) {
    List<Geoquant> list = new LinkedList<Geoquant>();
    if(c.isAssignableFrom(Alpha.class)) {
      list.addAll(getAlphas());
    } else if(c.isAssignableFrom(Angle.class)) {
      list.addAll(getAngles());
    } else if(c.isAssignableFrom(Area.class)){
      list.addAll(getAreas());
    }else if(c.isAssignableFrom(ConeAngle.class)) { 
      list.addAll(getConeAngles());
    } else if(c.isAssignableFrom(Curvature2D.class)) {
      list.addAll(getCurvature2D());
    } else if(c.isAssignableFrom(Curvature3D.class)) {
      list.addAll(getCurvature3D());
    } else if(c.isAssignableFrom(EdgeCurvature.class)) {
      list.addAll(getEdgeCurvatures());
    } else if(c.isAssignableFrom(Curvature3D.Sum.class)) {
      list.add(Curvature3D.sum());
    } else if(c.isAssignableFrom(DihedralAngle.class)) {
      list.addAll(getDihedralAngles());
    } else if(c.isAssignableFrom(DualArea.class)) {
      list.addAll(getDualAreas());
    } else if(c.isAssignableFrom(DualArea.Segment.class)){
      list.addAll(getDualAreaSegments());
    } else if(c.isAssignableFrom(EdgeHeight.class)) {
      list.addAll(getEdgeHeights());
    } else if(c.isAssignableFrom(Eta.class)) {
      list.addAll(getEtas());
    } else if(c.isAssignableFrom(FaceHeight.class)) {
      list.addAll(getFaceHeights());
    } else if(c.isAssignableFrom(Length.class)) {
      list.addAll(getLengths());
    } else if(c.isAssignableFrom(VEHR.class)) {
      list.add(VEHR.getInstance());
    } else if(c.isAssignableFrom(PartialEdge.class)) {
      list.addAll(getPartialEdges());
    } else if(c.isAssignableFrom(Radius.class)) {
      list.addAll(getRadii());
    } else if(c.isAssignableFrom(SectionalCurvature.class)) {
      list.addAll(getSectionalCurvatures());
    } else if(c.isAssignableFrom(Volume.class)) {
      list.addAll(getVolumes());
    } else if(c.isAssignableFrom(Volume.Sum.class)) {
      list.add(Volume.sum());
    } else if(c.isAssignableFrom(Curvature3D.Partial.class)) {
      list.addAll(getCurvaturePartials());
    } else if(c.isAssignableFrom(DihedralAngle.Partial.class)) {
      list.addAll(getDihedralAnglePartials());
    } else if(c.isAssignableFrom(VEHR.Partial.class)) {
      list.addAll(getVEHRPartials());
    } else if(c.isAssignableFrom(PartialEdge.Partial.class)) {
      list.addAll(getPartialEdgePartials());
    } else if(c.isAssignableFrom(Radius.Partial.class)) {
      list.addAll(getRadiusPartials());
    } else if(c.isAssignableFrom(Volume.Partial.class)) {
      list.addAll(getVolumePartials());
    } else if(c.isAssignableFrom(Volume.PartialSum.class)) {
      list.addAll(getVolumePartialSums());
    } else if(c.isAssignableFrom(Curvature3D.SecondPartial.class)) {
      list.addAll(getCurvatureSecondPartials());
    } else if(c.isAssignableFrom(DihedralAngle.SecondPartial.class)) {
      list.addAll(getDihedralAngleSecondPartials());
    } else if(c.isAssignableFrom(VEHR.SecondPartial.class)) {
      list.addAll(getVEHRSecondPartials());
    } else if(c.isAssignableFrom(PartialEdge.SecondPartial.class)) {
      list.addAll(getPartialEdgeSecondPartials());
    } else if(c.isAssignableFrom(Volume.SecondPartial.class)) {
      list.addAll(getVolumeSecondPartials());
    } else if(c.isAssignableFrom(Volume.SecondPartialSum.class)) {
      list.addAll(getVolumeSecondPartialSums());
    } else if(c.isAssignableFrom(LCSC.class)) {
      list.addAll(getLCSC());
    } else if(c.isAssignableFrom(VCSC.class)) {
      list.addAll(getVCSC());
    } else if(c.isAssignableFrom(LEinstein.class)) {
      list.addAll(getLEinsteins());
    } else if(c.isAssignableFrom(VEinstein.class)) {
      list.addAll(getVEinsteins());
    } else if(c.isAssignableFrom(LEHR.class)) {
      list.add(LEHR.getInstance());
    } else {
      return null;
    }
      
    return list;
  }
  
  public static List<Radius> getRadii() {
    LinkedList<Radius> list = new LinkedList<Radius>();
    Radius r;
    for (Vertex v : Triangulation.vertexTable.values()){
      r = Radius.At(v);
      list.add(r);
    }
    return list;
  }
  
  public static List<Alpha> getAlphas() {
    LinkedList<Alpha> list = new LinkedList<Alpha>();
    Alpha al;
    for (Vertex v: Triangulation.vertexTable.values()){
      al = Alpha. At(v);
      list.add(al);
    }
    return list;
  }
  
  public static List<Eta> getEtas() {
    LinkedList<Eta> list = new LinkedList<Eta>();
    Eta et;
    for (Edge e : Triangulation.edgeTable.values()){
      et = Eta.At(e);
      list.add(et);
    }
    return list;
  }
  
  public static List<Length> getLengths() {
    LinkedList<Length> list = new LinkedList<Length>();
    Length l;
    for(Edge e : Triangulation.edgeTable.values()) {
      l = Length.At(e);
      list.add(l);
    }
    return list;
  }
  
  public static List<Angle> getAngles() {
    LinkedList<Angle> list = new LinkedList<Angle>();
    Angle a;
    for (Face f : Triangulation.faceTable.values()){
      for (Vertex v : f.getLocalVertices()) {
        a = Angle.At(v, f);
        list.add(a);
      }
    }
    return list;
  }
  
  public static List<Area> getAreas() {
    LinkedList<Area> list = new LinkedList<Area>();
    Area ar;
    for (Face f : Triangulation.faceTable.values()){
      ar = Area.At(f);
      list.add(ar);
    }
    return list;
  }
  
  public static List<ConeAngle> getConeAngles() {
    LinkedList<ConeAngle> list = new LinkedList<ConeAngle>();
    ConeAngle c;
    for (Edge e : Triangulation.edgeTable.values()){
      c = ConeAngle.At(e);
      list.add(c);
    }
    return list;
  }
  
  public static List<Curvature2D> getCurvature2D() {
    LinkedList<Curvature2D> list = new LinkedList<Curvature2D>();
    Curvature2D cu2;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu2 = Curvature2D.At(v);
      list.add(cu2);
    }
    return list;
  }
  
  public static List<Curvature3D> getCurvature3D() {
    LinkedList<Curvature3D> list = new LinkedList<Curvature3D>();
    Curvature3D cu3;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu3 = Curvature3D.At(v);
      list.add(cu3);
    }
    return list;
  }
  
  public static List<EdgeCurvature> getEdgeCurvatures() {
    LinkedList<EdgeCurvature> list = new LinkedList<EdgeCurvature>();
    EdgeCurvature c;
    for (Edge e : Triangulation.edgeTable.values()){
      c = EdgeCurvature.At(e);
      list.add(c);
    }
    return list;
  }
  
  public static List<DihedralAngle> getDihedralAngles() {
    LinkedList<DihedralAngle> list = new LinkedList<DihedralAngle>();
    DihedralAngle d;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
        d = DihedralAngle.At(e, t);
        list.add(d);
      }
    }
    return list;
  }
  
  public static List<DualArea> getDualAreas() {
    LinkedList<DualArea> list = new LinkedList<DualArea>();
    DualArea du;
    for (Edge e : Triangulation.edgeTable.values()){
      du = DualArea.At(e);
      list.add(du);
    }
    return list;
  }
  
  public static List<DualArea.Segment> getDualAreaSegments() {
    LinkedList<DualArea.Segment> list = new LinkedList<DualArea.Segment>();
    DualArea da;
    DualArea.Segment s;
    for(Edge e : Triangulation.edgeTable.values()) {
      da = DualArea.At(e);
      for(Tetra t : e.getLocalTetras()) {
        s = da.segment(t);
        list.add(s);
      }
    }
    return list;
  }
  
  public static List<EdgeHeight> getEdgeHeights() {
    LinkedList<EdgeHeight> list = new LinkedList<EdgeHeight>();
    EdgeHeight eh;
    for (Face f : Triangulation.faceTable.values()){
      for (Edge e : f.getLocalEdges()) {
        eh = EdgeHeight.At(e, f);
        list.add(eh);
      }
    }
    return list;
  }
  
  public static List<FaceHeight> getFaceHeights() {
    LinkedList<FaceHeight> list = new LinkedList<FaceHeight>();
    FaceHeight fh;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Face f : t.getLocalFaces()) {
        fh = FaceHeight.At(f, t);
        list.add(fh);
      }
    }
    return list;
  }
  
  public static List<PartialEdge> getPartialEdges() {
    LinkedList<PartialEdge> list = new LinkedList<PartialEdge>();
    PartialEdge pe;
    for (Edge edge : Triangulation.edgeTable.values()){
      for (Vertex vertex : edge.getLocalVertices()) {
        pe = PartialEdge.At(vertex, edge);
        list.add(pe);
      }
    }
    return list;
  }
  
  public static List<SectionalCurvature> getSectionalCurvatures() {
    LinkedList<SectionalCurvature> list = new LinkedList<SectionalCurvature>();
    SectionalCurvature sc;
    for (Edge e : Triangulation.edgeTable.values()){
      sc = SectionalCurvature.At(e);
      list.add(sc);
    }
    return list;
  }
  
  public static List<Volume> getVolumes() {
    LinkedList<Volume> list = new LinkedList<Volume>();
    Volume vol;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      list.add(vol);
    }
    return list;
  }
  
  public static List<Radius.Partial> getRadiusPartials() {
    LinkedList<Radius.Partial> list = new LinkedList<Radius.Partial>();
    Radius.Partial partial;
    for(Radius r : getRadii()) {
      for (Edge e : Triangulation.edgeTable.values()){
        partial = r.partialAt(e);
        list.add(partial);
      }
    }
    return list;
  }
  
  public static List<Curvature3D.Partial> getCurvaturePartials() {
    LinkedList<Curvature3D.Partial> list = new LinkedList<Curvature3D.Partial>();
    Curvature3D.Partial partial;
    for(Curvature3D c : getCurvature3D()) {
      for(Vertex w : Triangulation.vertexTable.values()) {
        partial = c.partialAt(w);
        list.add(partial);
      }
    }
    for(Curvature3D c : getCurvature3D()) {
      for(Edge e : Triangulation.edgeTable.values()) {
        partial = c.partialAt(e);
        list.add(partial);
      }
    }
    return list;
  }
  
  public static List<DihedralAngle.Partial> getDihedralAnglePartials() {
    LinkedList<DihedralAngle.Partial> list = new LinkedList<DihedralAngle.Partial>();
    DihedralAngle.Partial partial;
    for(DihedralAngle a : getDihedralAngles()) {
      for(Edge e : Triangulation.edgeTable.values()) {
        partial = a.partialAt(e);
        list.add(partial);
      }
    }
    return list;
  }
  
  public static List<VEHR.Partial> getVEHRPartials() {
    LinkedList<VEHR.Partial> list = new LinkedList<VEHR.Partial>();
    VEHR.Partial partial;
    for(Vertex v : Triangulation.vertexTable.values()) {
      partial = VEHR.partialAt(v);
      list.add(partial);
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      partial = VEHR.partialAt(e);
      list.add(partial);
    }
    return list;
  }
  
  public static List<PartialEdge.Partial> getPartialEdgePartials() {
    LinkedList<PartialEdge.Partial> list = new LinkedList<PartialEdge.Partial>();
    PartialEdge.Partial partial;
    for(PartialEdge a : getPartialEdges()) {
      for(Edge e : Triangulation.edgeTable.values()) {
        partial = a.partialAt(e);
        list.add(partial);
      }
    }
    return list;
  }
  
  public static List<Volume.Partial> getVolumePartials() {
    LinkedList<Volume.Partial> list = new LinkedList<Volume.Partial>();
    Volume vol;
    Volume.Partial partial;
    for(Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
       partial = vol.partialAt(v); 
       list.add(partial);
      }
    }
    for(Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Edge e : t.getLocalEdges()){
        partial = vol.partialAt(e);
        list.add(partial);
      }
    }
    return list;
  }
  
  public static List<Volume.PartialSum> getVolumePartialSums() {
    LinkedList<Volume.PartialSum> list = new LinkedList<Volume.PartialSum>();
    Volume.PartialSum partial;
    for (Vertex v : Triangulation.vertexTable.values()){
      partial = Volume.partialSumAt(v); 
      list.add(partial);
     }
     for (Edge e : Triangulation.edgeTable.values()){
       partial = Volume.partialSumAt(e);
       list.add(partial);
     }
     return list;
  }
  
  public static List<Curvature3D.SecondPartial> getCurvatureSecondPartials() {
    LinkedList<Curvature3D.SecondPartial> list = new LinkedList<Curvature3D.SecondPartial>();
    Curvature3D.SecondPartial secondPartial;
    for(Curvature3D c : getCurvature3D()) {
      for (Edge e : Triangulation.edgeTable.values()) {
        for (Edge f : Triangulation.edgeTable.values()) {
          secondPartial = c.secondPartialAt(e, f);
          list.add(secondPartial);
        }
      }
    }
    return list;
  }
  
  public static List<DihedralAngle.SecondPartial> getDihedralAngleSecondPartials() {
    LinkedList<DihedralAngle.SecondPartial> list = new LinkedList<DihedralAngle.SecondPartial>();
    DihedralAngle.SecondPartial secondPartial;
    for(DihedralAngle a : getDihedralAngles()) {
      for (Edge e : Triangulation.edgeTable.values()) {
        for (Edge f : Triangulation.edgeTable.values()) {
          secondPartial = a.secondPartialAt(e, f);
          list.add(secondPartial);
        }
      }
    }
    return list;
  }
  
  public static List<PartialEdge.SecondPartial> getPartialEdgeSecondPartials() {
    LinkedList<PartialEdge.SecondPartial> list = new LinkedList<PartialEdge.SecondPartial>();
    PartialEdge.SecondPartial secondPartial;
    for(PartialEdge a : getPartialEdges()) {
      for (Edge e : Triangulation.edgeTable.values()) {
        for (Edge f : Triangulation.edgeTable.values()) {
          secondPartial = a.secondPartialAt(e, f);
          list.add(secondPartial);
        }
      }
    }
    return list;
  }
  
  public static List<VEHR.SecondPartial> getVEHRSecondPartials() {
    LinkedList<VEHR.SecondPartial> list = new LinkedList<VEHR.SecondPartial>();
    VEHR.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      for (Vertex w : Triangulation.vertexTable.values()) {
        secondPartial = VEHR.secondPartialAt(v, w);
        list.add(secondPartial);
      }
    }
    for (Vertex v : Triangulation.vertexTable.values()) {
      for (Edge e : Triangulation.edgeTable.values()) {
        secondPartial = VEHR.secondPartialAt(v, e);
        list.add(secondPartial);
      }
    }
   for (Edge e : Triangulation.edgeTable.values()) {
     for (Edge f : Triangulation.edgeTable.values()) { 
       secondPartial = VEHR.secondPartialAt(e, f);
       list.add(secondPartial);
     }
   }
    return list;
  }
  
  public static List<Volume.SecondPartial> getVolumeSecondPartials() {
    LinkedList<Volume.SecondPartial> list = new LinkedList<Volume.SecondPartial>();
    Volume vol;
    Volume.SecondPartial secondPartial;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
        for (Vertex w : t.getLocalVertices()){
          secondPartial = vol.secondPartialAt(v, w);
          list.add(secondPartial);
        }
      }
    }
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
        for (Edge e : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(v, e);
          list.add(secondPartial);
        }
      }
    }
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Edge e : t.getLocalEdges()){
        for (Edge f : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(e, f);
          list.add(secondPartial);
        }
      }
    }
    return list;
  }
  
  public static List<Volume.SecondPartialSum> getVolumeSecondPartialSums() {
    LinkedList<Volume.SecondPartialSum> list = new LinkedList<Volume.SecondPartialSum>();
    Volume.SecondPartialSum secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()){
      for (Vertex w : Triangulation.vertexTable.values()){
        secondPartial = Volume.secondPartialSumAt(v, w);
        list.add(secondPartial);
      }
    }
    for (Vertex v : Triangulation.vertexTable.values()){
      for (Edge e : Triangulation.edgeTable.values()){
        secondPartial = Volume.secondPartialSumAt(v, e);
        list.add(secondPartial);
      }
    }
    for (Edge e : Triangulation.edgeTable.values()){
      for (Edge f : Triangulation.edgeTable.values()){
        secondPartial = Volume.secondPartialSumAt(e, f);
        list.add(secondPartial);
      }
    }
    return list;
  }
  
  public static List<LCSC> getLCSC() {
    LinkedList<LCSC> list = new LinkedList<LCSC>();
    LCSC l;
    for(Vertex v : Triangulation.vertexTable.values()) {
      l = LCSC.At(v);
      list.add(l);
    }
    return list;
  }
  
  public static List<VCSC> getVCSC() {
    LinkedList<VCSC> list = new LinkedList<VCSC>();
    VCSC vc;
    for(Vertex v : Triangulation.vertexTable.values()) {
      vc = VCSC.At(v);
      list.add(vc);
    }
    return list;
  }
  
  public static List<LEinstein> getLEinsteins() {
    LinkedList<LEinstein> list = new LinkedList<LEinstein>();
    LEinstein le;
    for(Edge e : Triangulation.edgeTable.values()) {
      le = LEinstein.At(e);
      list.add(le);
    }
    return list;
  }
  
  public static List<VEinstein> getVEinsteins() {
    LinkedList<VEinstein> list = new LinkedList<VEinstein>();
    VEinstein ve;
    for(Edge e : Triangulation.edgeTable.values()) {
      ve = VEinstein.At(e);
      list.add(ve);
    }
    return list;
  }
  
  public static List<LEHR> getLEHR() {
    LinkedList<LEHR> list = new LinkedList<LEHR>();
    list.add(LEHR.getInstance());
    return list;
  }
}
