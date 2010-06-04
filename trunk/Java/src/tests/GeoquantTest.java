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
    
    testLengths();
    testRadii();
    testAlpha();
    testEta();
    testAngle();
    testArea();
    testConeAngle();
    testCurvature2D();
    testCurvature3D();
    testDihedralAngle();
    testDualArea();
    testEdgeHeight();
    testFaceHeight();
    testSectionalCurvature();
    testVolume();
    testPartialEdge();
    testNEHR();
    testVolumePartial();
    testVolumeSecondPartial();
    testDihedralAnglePartial();
    testDihedralAngleSecondPartial();
    testCurvature3DPartial();
    testCurvature3DSecondPartial();
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
      System.out.println(l + " at " + e + " = " + l.getValue());
    }
  }

  private static void testRadii() {
    Radius r;
    for (Vertex v : Triangulation.vertexTable.values()){
      r = Radius.At(v);
      System.out.println(r + " at " + v + " = " + r.getValue());
    }
  }
  private static void testAlpha() {
    Alpha al;
    for (Vertex v: Triangulation.vertexTable.values()){
      al = Alpha. At(v);
      System.out.println(al + " at " + v + " = " + al.getValue());
    }
  }
  private static void testEta() {
    Eta et;
    for (Edge e : Triangulation.edgeTable.values()){
      et = Eta.At(e);
      System.out.println(et + " at " + e + " = " + et.getValue());
    }
  }
  private static void testAngle() {
    Angle a;
    for (Face f : Triangulation.faceTable.values()){
      for (Vertex v : f.getLocalVertices()) {
      a = Angle.At(v, f);
      System.out.println(a + " at " + v + " = " + a.getValue());
      }
    }
  }
  private static void testArea() {
    Area ar;
    for (Face f : Triangulation.faceTable.values()){
      ar = Area.At(f);
      System.out.println(ar + " at " + f + " = " + ar.getValue());
      }
  }
  private static void testConeAngle() {
    ConeAngle c;
    for (Edge e : Triangulation.edgeTable.values()){
      c = ConeAngle.At(e);
      System.out.println(c + " at " + e + " = " + c.getValue());
    }
  }
  private static void testCurvature2D() {
    Curvature2D cu2;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu2 = Curvature2D.At(v);
      System.out.println(cu2 + " at " + v + " = " + cu2.getValue());
    }
  }
  private static void testCurvature3D() {
    Curvature3D cu3;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu3 = Curvature3D.At(v);
      System.out.println(cu3 + " at " + v + " = " + cu3.getValue());
    }
  }
  private static void testDihedralAngle() {
    DihedralAngle d;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
      d = DihedralAngle.At(e, t);
      System.out.println(d + " at " + e + " = " + d.getValue());
    }
   }
  }
  private static void testDualArea() {
    DualArea du;
    for (Edge e : Triangulation.edgeTable.values()){
      du = DualArea.At(e);
      System.out.println(du + " at " + e + " = " + du.getValue());
    }
  }
  private static void testEdgeHeight() {
    EdgeHeight eh;
    for (Face f : Triangulation.faceTable.values()){
      for (Edge e : f.getLocalEdges()) {
      eh = EdgeHeight.At(e, f);
      System.out.println(eh + " at " + e + " = " + eh.getValue());
      }
    }
  }
  private static void testFaceHeight() {
    FaceHeight fh;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Face f : t.getLocalFaces()) {
      fh = FaceHeight.At(f, t);
      System.out.println(fh + " at " + f + " = " + fh.getValue());
    }
   }
  }
  private static void testSectionalCurvature(){
    SectionalCurvature sc;
    for (Edge e : Triangulation.edgeTable.values()){
      sc = SectionalCurvature.At(e);
      System.out.println(sc + " at " + e + " = " + sc.getValue());
    }
  }
  private static void testVolume() {
    Volume vol;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      System.out.println(vol + " at " + t + " = " + vol.getValue());
    }
  }
  private static void testPartialEdge() {
    PartialEdge pe;
    for (Edge edge : Triangulation.edgeTable.values()){
      for (Vertex vertex : edge.getLocalVertices()) {
        pe = PartialEdge.At(vertex, edge);
        System.out.println(pe + " at " + vertex + " = " + pe.getValue());
      }
    }
  }
  private static void testNEHR() {
  System.out.println("NEHR = " + NEHR.value());
  }
  private static void testVolumePartial() {
    Volume vol;
    Volume.Partial partial;
    for(Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
       partial = vol.partialAt(v); 
       System.out.println(partial + " at " + t + ", " + v + " = " + partial.getValue());
      }
      for (Edge e : t.getLocalEdges()){
        partial = vol.partialAt(e);
        System.out.println(partial + " at " + t + ", " + e + " = " + partial.getValue());
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
          System.out.println(secondPartial + " at " + v + ", " + w + " = " + secondPartial.getValue());
        }
        for (Edge e : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(v, e);
          System.out.println(secondPartial + " at " + v + ", " + e + " = " + secondPartial.getValue());
        }
      }
      for (Edge e : t.getLocalEdges()){
        for (Edge f : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(e, f);
          System.out.println(secondPartial + " at " + e + ", " + f + " = " + secondPartial.getValue());
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
          System.out.println(partial + " at " + f + " = " + d.getValue());
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
            System.out.println(secondPartial + " at " + nm + ", " + op + " = " + d.getValue());
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
        System.out.println(partial + " at " + w + " = " + c.getValue());
      }
      for (Edge e : Triangulation.edgeTable.values()) {
        partial = c.partialAt(e);
        System.out.println(partial + " at " + e + " = " + c.getValue());
      }
    }
  }
  private static void testCurvature3DSecondPartial() {
    Curvature3D c;
    Curvature3D.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      c = Curvature3D.At(v);
      for (Edge e : Triangulation.edgeTable.values()) {
        for (Edge f : Triangulation.edgeTable.values()) {
          secondPartial = c.secondPartialAt(e, f);
          System.out.println(secondPartial + " at " + e + ", " + f + " = " + c.getValue());
        }
      }
    }
  }
  
}
