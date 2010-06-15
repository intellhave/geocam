package Geoquant;

import java.util.LinkedList;
import java.util.List;

import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class Geometry {
  
  private Geometry() {
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
  
  public static List<NEHR.Partial> getNEHRPartials() {
    LinkedList<NEHR.Partial> list = new LinkedList<NEHR.Partial>();
    NEHR.Partial partial;
    for(Vertex v : Triangulation.vertexTable.values()) {
      partial = NEHR.partialAt(v);
      list.add(partial);
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      partial = NEHR.partialAt(e);
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
  
  public static List<NEHR.SecondPartial> getNEHRSecondPartials() {
    LinkedList<NEHR.SecondPartial> list = new LinkedList<NEHR.SecondPartial>();
    NEHR.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      for (Vertex w : Triangulation.vertexTable.values()) {
        secondPartial = NEHR.secondPartialAt(v, w);
        list.add(secondPartial);
      }
    }
    for (Vertex v : Triangulation.vertexTable.values()) {
      for (Edge e : Triangulation.edgeTable.values()) {
        secondPartial = NEHR.secondPartialAt(v, e);
        list.add(secondPartial);
      }
    }
   for (Edge e : Triangulation.edgeTable.values()) {
     for (Edge f : Triangulation.edgeTable.values()) { 
       secondPartial = NEHR.secondPartialAt(e, f);
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
}
