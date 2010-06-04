package tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Geoquant.Alpha;
import Geoquant.Angle;
import Geoquant.Area;
import Geoquant.ConeAngle;
import Geoquant.Curvature2D;
import Geoquant.Curvature3D;
import Geoquant.DihedralAngle;
import Geoquant.DualArea;
import Geoquant.EdgeHeight;
import Geoquant.Eta;
import Geoquant.FaceHeight;
import Geoquant.Length;
import Geoquant.NEHR;
import Geoquant.PartialEdge;
import Geoquant.Radius;
import Geoquant.SectionalCurvature;
import Geoquant.Volume;
import InputOutput.TriangulationIO;
import Triangulation.*;

public class GeoquantTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    initializeQuantities();
    
//    testLengths();
//    testRadii();
//    testAlpha();
//    testEta();
//    testAngle();
//    testArea();
//    testConeAngle();
//    testCurvature2D();
//    testCurvature3D();
//    testDihedralAngle();
//    testDualArea();
//    testEdgeHeight();
//    testFaceHeight();
//    testSectionalCurvature();
//    testVolume();
//    testPartialEdge();
//    testNEHR();
//    testVolumePartial();
//    testVolumeSecondPartial();
//    testDihedralAnglePartial();
//    testDihedralAngleSecondPartial();
//    testCurvature3DPartial();
//    testCurvature3DSecondPartial();
    testRadiiPartial();
//    testNEHRPartial();
//    testNEHRSecondPartial();
  }
  
  private static void testInvoker() {
    Vertex v = Triangulation.vertexTable.get(1);
    Radius r = Radius.At(Triangulation.vertexTable.get(1));
    Method m = null;
    try {
      m = r.getClass().getDeclaredMethod("valueAt", v.getClass());
      
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      System.out.println("The answer is: " + m.invoke(null, v));
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private static void initializeQuantities() {
    TriangulationIO.read3DTriangulationFile("Data/3DManifolds/StandardFormat/pentachoron.txt");
 
    for(Vertex v : Triangulation.vertexTable.values()) {
      Radius.At(v).setValue(1.0);
      Alpha.At(v).setValue(1.0);
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      Eta.At(e).setValue(1.0);
    }
    for (Vertex vertex : Triangulation.vertexTable.values()){
      Radius.At(vertex).setValue(1.0);
      Alpha.At(vertex).setValue(1.0);
    }
    for (Edge edge : Triangulation.edgeTable.values()){
      Eta.At(edge).setValue(1.0);
    }
    for (Edge f : Triangulation.edgeTable.values()) {
      Eta.At(f).setValue(1.0);
    }
    for (Vertex w : Triangulation.vertexTable.values()) {
      Radius.At(w).setValue(1.0);
      Alpha.At(w).setValue(1.0);
    }
    for (Edge nm : Triangulation.edgeTable.values()) {
      Eta.At(nm).setValue(1.0);
    }
    for (Edge op : Triangulation.edgeTable.values()) {
      Eta.At(op).setValue(1.0);
    }
  }
  
  private static void testLengths() {
    Length l;
    for(Edge e : Triangulation.edgeTable.values()) {
      l = Length.At(e);
      System.out.println(l);
    }
  }

  private static void testRadii() {
    Radius r;
    for (Vertex v : Triangulation.vertexTable.values()){
      r = Radius.At(v);
      System.out.println(r);
    }
  }
  private static void testAlpha() {
    Alpha al;
    for (Vertex v: Triangulation.vertexTable.values()){
      al = Alpha. At(v);
      System.out.println(al);
    }
  }
  private static void testEta() {
    Eta et;
    for (Edge e : Triangulation.edgeTable.values()){
      et = Eta.At(e);
      System.out.println(et);
    }
  }
  private static void testAngle() {
    Angle a;
    for (Face f : Triangulation.faceTable.values()){
      for (Vertex v : f.getLocalVertices()) {
      a = Angle.At(v, f);
      System.out.println(a);
      }
    }
  }
  private static void testArea() {
    Area ar;
    for (Face f : Triangulation.faceTable.values()){
      ar = Area.At(f);
      System.out.println(ar);
      }
  }
  private static void testConeAngle() {
    ConeAngle c;
    for (Edge e : Triangulation.edgeTable.values()){
      c = ConeAngle.At(e);
      System.out.println(c);
    }
  }
  private static void testCurvature2D() {
    Curvature2D cu2;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu2 = Curvature2D.At(v);
      System.out.println(cu2);
    }
  }
  private static void testCurvature3D() {
    Curvature3D cu3;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu3 = Curvature3D.At(v);
      System.out.println(cu3);
    }
  }
  private static void testDihedralAngle() {
    DihedralAngle d;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
      d = DihedralAngle.At(e, t);
      System.out.println(d);
    }
   }
  }
  private static void testDualArea() {
    DualArea du;
    for (Edge e : Triangulation.edgeTable.values()){
      du = DualArea.At(e);
      System.out.println(du);
    }
  }
  private static void testEdgeHeight() {
    EdgeHeight eh;
    for (Face f : Triangulation.faceTable.values()){
      for (Edge e : f.getLocalEdges()) {
      eh = EdgeHeight.At(e, f);
      System.out.println(eh);
      }
    }
  }
  private static void testFaceHeight() {
    FaceHeight fh;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Face f : t.getLocalFaces()) {
      fh = FaceHeight.At(f, t);
      System.out.println(fh);
    }
   }
  }
  private static void testSectionalCurvature(){
    SectionalCurvature sc;
    for (Edge e : Triangulation.edgeTable.values()){
      sc = SectionalCurvature.At(e);
      System.out.println(sc);
    }
  }
  private static void testVolume() {
    Volume vol;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      System.out.println(vol);
    }
  }
  private static void testPartialEdge() {
    PartialEdge pe;
    for (Edge edge : Triangulation.edgeTable.values()){
      for (Vertex vertex : edge.getLocalVertices()) {
        pe = PartialEdge.At(vertex, edge);
        System.out.println(pe);
      }
    }
  }
  private static void testNEHR() {
    System.out.println(NEHR.getInstance());
  }
  private static void testVolumePartial() {
    Volume vol;
    Volume.Partial partial;
    for(Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
       partial = vol.partialAt(v); 
       System.out.println(partial);
      }
      for (Edge e : t.getLocalEdges()){
        partial = vol.partialAt(e);
        System.out.println(partial);
      }
    }
  }
  private static void testVolumeSecondPartial() {
    Volume vol;
    Volume.SecondPartial secondPartial;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
        for (Vertex w : t.getLocalVertices()){
          secondPartial = vol.secondPartialAt(v, w);
          System.out.println(secondPartial);
        }
        for (Edge e : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(v, e);
          System.out.println(secondPartial);
        }
      }
      for (Edge e : t.getLocalEdges()){
        for (Edge f : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(e, f);
          System.out.println(secondPartial);
        }
      }
    }
  }
  private static void testDihedralAnglePartial() {
    DihedralAngle d;
    DihedralAngle.Partial partial;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
        d = DihedralAngle.At(e, t);
        for (Edge f : t.getLocalEdges()) {
          partial = d.partialAt(f);
          System.out.println(partial);
       }
     }
   }
  }
  private static void testDihedralAngleSecondPartial() {
    DihedralAngle d;
    DihedralAngle.SecondPartial secondPartial;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
        d = DihedralAngle.At(e, t);
        for (Edge nm : t.getLocalEdges()) {
          for (Edge op : t.getLocalEdges()) {
            secondPartial = d.secondPartialAt(nm, op);
            System.out.println(secondPartial);
          }
        }
      }
    }
  }
  private static void testCurvature3DPartial() {
    Curvature3D c;
    Curvature3D.Partial partial;
    for (Vertex v : Triangulation.vertexTable.values()){
      c = Curvature3D.At(v);
      for (Vertex w : Triangulation.vertexTable.values()) {
        partial = c.partialAt(w);
        System.out.println(partial);
      }
      for (Edge e : v.getLocalEdges()) {
        partial = c.partialAt(e);
        System.out.println(partial);
      }
    }
  }
  private static void testCurvature3DSecondPartial() {
    Curvature3D c;
    Curvature3D.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      c = Curvature3D.At(v);
      for (Edge e : v.getLocalEdges()) {
        for (Edge f : v.getLocalEdges()) {
          secondPartial = c.secondPartialAt(e, f);
          System.out.println(secondPartial);
        }
      }
    }
  }
  private static void testRadiiPartial() {
    Radius r;
    Radius.Partial partial;
    for (Vertex v : Triangulation.vertexTable.values()){
      r = Radius.At(v);
      for (Edge e : v.getLocalEdges()){
        partial = r.partialAt(e);
        System.out.println(partial);
      }
    }
  }
  private static void testNEHRPartial() {
    NEHR.Partial partial;
    for (Vertex  v : Triangulation.vertexTable.values()) {
      partial = NEHR.partialAt(v);
      System.out.println(partial);
    }
  }
  private static void testNEHRSecondPartial() {
    NEHR.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      for (Vertex w : Triangulation.vertexTable.values()) {
        secondPartial = NEHR.secondPartialAt(v, w);
        System.out.println(secondPartial);
      }
      for (Edge e : v.getLocalEdges()) {
        secondPartial = NEHR.secondPartialAt(v, e);
        System.out.println(secondPartial);
      }
    }
   for (Edge e : Triangulation.edgeTable.values()) {
     for (Edge f : Triangulation.edgeTable.values()) { 
       secondPartial = NEHR.secondPartialAt(e, f);
       System.out.println(secondPartial);
     }
   }
  }
}
