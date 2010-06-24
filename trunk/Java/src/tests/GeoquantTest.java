package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Flows.CrossConformalFlow;
import Flows.RadiusOptNEHR;
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
import Geoquant.Geometry;
import Geoquant.Length;
import Geoquant.NEHR;
import Geoquant.PartialEdge;
import Geoquant.Radius;
import Geoquant.SectionalCurvature;
import Geoquant.Volume;
import InputOutput.TriangulationIO;
import Solvers.WrongDirectionException;
import Triangulation.*;

public class GeoquantTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    initializeQuantities();
        
    RadiusOptNEHR minRad = new RadiusOptNEHR();
    double[] radii = CrossConformalFlow.getLogRadii();
    
    minRad.setStoppingCondition(0.0);
    minRad.setStepRatio(1.0);
    try {
      double[] log_radii = CrossConformalFlow.getLogRadii();
      for(int i = 0; i < 100; i++) {
        minRad.step(log_radii);
        CrossConformalFlow.setLogRadii(log_radii);
      }
    } catch (Exception e) {
      return;
    }
  
 
  radii = CrossConformalFlow.getLogRadii();
  for(int i = 0; i < radii.length; i++) {
    System.out.print(Math.exp(radii[i]) + ", ");
  }
  System.out.println();
//    
  
  double sum;
  System.out.print("{");
  for(Edge e1: Triangulation.edgeTable.values()) {
    System.out.print("{");
    for(Edge e2: Triangulation.edgeTable.values()) {
      sum = 0;
      for(Vertex v : Triangulation.vertexTable.values()) {
        sum += NEHR.secondPartialAt(v, e1).getValue() * Radius.At(v).partialAt(e2).getValue();
        sum += NEHR.secondPartialAt(v, e2).getValue() * Radius.At(v).partialAt(e1).getValue();
      }
      System.out.print(sum + NEHR.secondPartialAt(e1, e2).getValue() + ", ");
    }
    System.out.println("},");
  }
  System.out.println("}");
    
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
    testPartialEdgePartial();
    testPartialEdgeSecondPartial();
    testCurvature3DPartial();
    testCurvature3DSecondPartial();
    testRadiiPartial();
    testNEHRPartial();
    testNEHRSecondPartial();
    testTotalVolumeSecondPartial();
    testTotalVolumePartial();
  }
    
  private static void initializeQuantities() {
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_test.xml");
//    for(Eta e : Geometry.getEtas()) {
//      e.setValue(1.0);
//    }
  }
  
  private static void testLengths() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Lengths.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Length l;
    for(Edge e : Triangulation.edgeTable.values()) {
      l = Length.At(e);
      out.println(l);
    }
  }

  private static void testRadii() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Radii.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Radius r;
    for (Vertex v : Triangulation.vertexTable.values()){
      r = Radius.At(v);
      out.println(r);
    }
  }
  private static void testAlpha() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Alphas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Alpha al;
    for (Vertex v: Triangulation.vertexTable.values()){
      al = Alpha. At(v);
      out.println(al);
    }
  }
  private static void testEta() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Etas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Eta et;
    for (Edge e : Triangulation.edgeTable.values()){
      et = Eta.At(e);
      out.println(et);
    }
  }
  private static void testAngle() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Angles.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Angle a;
    for (Face f : Triangulation.faceTable.values()){
      for (Vertex v : f.getLocalVertices()) {
        a = Angle.At(v, f);
        out.println(a);
      }
    }
  }
  private static void testArea() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Areas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Area ar;
    for (Face f : Triangulation.faceTable.values()){
      ar = Area.At(f);
      out.println(ar);
    }
  }
  private static void testConeAngle() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/ConeAngles.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    ConeAngle c;
    for (Edge e : Triangulation.edgeTable.values()){
      c = ConeAngle.At(e);
      out.println(c);
    }
  }
  private static void testCurvature2D() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature2D.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature2D cu2;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu2 = Curvature2D.At(v);
      out.println(cu2);
    }
  }
  private static void testCurvature3D() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature3D.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature3D cu3;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu3 = Curvature3D.At(v);
      out.println(cu3);
    }
  }
  private static void testDihedralAngle() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DihedralAngles.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DihedralAngle d;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
      d = DihedralAngle.At(e, t);
      out.println(d);
    }
   }
  }
  private static void testDualArea() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DualAreas.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DualArea du;
    for (Edge e : Triangulation.edgeTable.values()){
      du = DualArea.At(e);
      out.println(du);
    }
  }
  
  private static void testEdgeHeight() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/EdgeHeights.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    EdgeHeight eh;
    for (Face f : Triangulation.faceTable.values()){
      for (Edge e : f.getLocalEdges()) {
        eh = EdgeHeight.At(e, f);
        out.println(eh);
      }
    }
  }
  private static void testFaceHeight() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/FaceHeights.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    FaceHeight fh;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Face f : t.getLocalFaces()) {
        fh = FaceHeight.At(f, t);
        out.println(fh);
      }
    }
  }
  private static void testSectionalCurvature(){
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/SectionalCurvatures.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }    
    SectionalCurvature sc;
    for (Edge e : Triangulation.edgeTable.values()){
      sc = SectionalCurvature.At(e);
      out.println(sc);
    }
  }
  private static void testVolume() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Volumes.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Volume vol;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      out.println(vol);
    }
  }
  private static void testPartialEdge() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/PartialEdges.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    PartialEdge pe;
    for (Edge edge : Triangulation.edgeTable.values()){
      for (Vertex vertex : edge.getLocalVertices()) {
        pe = PartialEdge.At(vertex, edge);
        out.println(pe);
      }
    }
  }
  
  private static void testNEHR() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/NEHR.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    out.println(NEHR.getInstance());
  }
  
  private static void testVolumePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Volume_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Volume vol;
    Volume.Partial partial;
    for(Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
       partial = vol.partialAt(v); 
       out.println(partial);
      }
      for (Edge e : t.getLocalEdges()){
        partial = vol.partialAt(e);
        out.println(partial);
      }
    }
  }
  private static void testVolumeSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Volume_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Volume vol;
    Volume.SecondPartial secondPartial;
    for (Tetra t : Triangulation.tetraTable.values()){
      vol = Volume.At(t);
      for (Vertex v : t.getLocalVertices()){
        for (Vertex w : t.getLocalVertices()){
          secondPartial = vol.secondPartialAt(v, w);
          out.println(secondPartial);
        }
        for (Edge e : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(v, e);
          out.println(secondPartial);
        }
      }
      for (Edge e : t.getLocalEdges()){
        for (Edge f : t.getLocalEdges()){
          secondPartial = vol.secondPartialAt(e, f);
          out.println(secondPartial);
        }
      }
    }
  }
  private static void testDihedralAnglePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DihedralAngle_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DihedralAngle d;
    DihedralAngle.Partial partial;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
        d = DihedralAngle.At(e, t);
        for (Edge f : Triangulation.edgeTable.values()) {
          partial = d.partialAt(f);
          out.println(partial);
       }
     }
   }
  }
  private static void testDihedralAngleSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/DihedralAngle_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    DihedralAngle d;
    DihedralAngle.SecondPartial secondPartial;
    for (Tetra t : Triangulation.tetraTable.values()){
      for (Edge e : t.getLocalEdges()) {
        d = DihedralAngle.At(e, t);
        for (Edge nm : t.getLocalEdges()) {
          for (Edge op : t.getLocalEdges()) {
            secondPartial = d.secondPartialAt(nm, op);
            out.println(secondPartial);
          }
        }
      }
    }
  }
  

  private static void testPartialEdgePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/PartialEdge_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    PartialEdge pe;
    PartialEdge.Partial partial;
    for(Edge e : Triangulation.edgeTable.values()) {
      for(Vertex v : e.getLocalVertices()) {
        pe = PartialEdge.At(v, e);
        for(Edge f: Triangulation.edgeTable.values()) {
          partial = pe.partialAt(f);
          out.println(partial);
        }
      }
    }
  }
  
  private static void testPartialEdgeSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/PartialEdge_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    PartialEdge pe;
    PartialEdge.SecondPartial secondPartial;
    for(Edge e : Triangulation.edgeTable.values()) {
      for(Vertex v : e.getLocalVertices()) {
        pe = PartialEdge.At(v, e);
        secondPartial = pe.secondPartialAt(e, e);
        out.println(secondPartial);
      }
    }
  }
  
  private static void testCurvature3DPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature3D_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature3D c;
    Curvature3D.Partial partial;
    for (Vertex v : Triangulation.vertexTable.values()){
      c = Curvature3D.At(v);
      for (Vertex w : Triangulation.vertexTable.values()) {
        partial = c.partialAt(w);
        out.println(partial);
      }
      for (Edge e : Triangulation.edgeTable.values()) {
        partial = c.partialAt(e);
        out.println(partial);
      }
    }
  }
  private static void testCurvature3DSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Curvature3D_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature3D c;
    Curvature3D.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      c = Curvature3D.At(v);
      for (Edge e : Triangulation.edgeTable.values()) {
        for (Edge f : Triangulation.edgeTable.values()) {
          secondPartial = c.secondPartialAt(e, f);
          out.println(secondPartial);
        }
      }
    }
  }
  private static void testRadiiPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Radius_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    Radius r;
    Radius.Partial partial;
    for (Vertex v : Triangulation.vertexTable.values()){
      r = Radius.At(v);
      for (Edge e : Triangulation.edgeTable.values()){
        partial = r.partialAt(e);
        out.println(partial);
      }
    }
  }
  private static void testNEHRPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/NEHR_Partials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    NEHR.Partial partial;
    for (Vertex  v : Triangulation.vertexTable.values()) {
      partial = NEHR.partialAt(v);
      out.println(partial);
    }
    for (Edge e : Triangulation.edgeTable.values()) {
      partial = NEHR.partialAt(e);
      out.println(partial);
    }
  }
  
  private static void testNEHRSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/NEHR_SecondPartials.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    NEHR.SecondPartial secondPartial;
    for (Vertex v : Triangulation.vertexTable.values()) {
      for (Vertex w : Triangulation.vertexTable.values()) {
        secondPartial = NEHR.secondPartialAt(v, w);
        out.println(secondPartial);
      }
      for (Edge e : Triangulation.edgeTable.values()) {
        secondPartial = NEHR.secondPartialAt(v, e);
        out.println(secondPartial);
      }
    }
   for (Edge e : Triangulation.edgeTable.values()) {
     for (Edge f : Triangulation.edgeTable.values()) { 
       secondPartial = NEHR.secondPartialAt(e, f);
       out.println(secondPartial);
     }
   }
  }
  
  private static void testTotalVolumeSecondPartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/TotalVolume_SecondPartial.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(Volume.SecondPartialSum p : Geometry.getVolumeSecondPartialSums()) {
      out.println(p);
    }
  }
  
  private static void testTotalVolumePartial() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/TotalVolume_Partial.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    for(Volume.PartialSum p : Geometry.getVolumePartialSums()) {
      out.println(p);
    }
  }
}
