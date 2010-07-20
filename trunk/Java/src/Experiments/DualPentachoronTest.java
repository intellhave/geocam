package Experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import Geoquant.Alpha;
import Geoquant.Angle;
import Geoquant.DihedralAngle;
import Geoquant.FaceHeight;
import Geoquant.Geometry;
import Geoquant.Length;
import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.StdEdge;
import Triangulation.Triangulation;

public class DualPentachoronTest {

  public static void main(String[] args) {
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_test2.xml");
    for(Radius r : Geometry.getRadii()) {
      r.setValue(1.0);
    }
    
    for(Alpha a : Geometry.getAlphas()) {
      a.setValue(0.0);
    }
    
    PrintStream out = System.out;
//    try {
//      out = new PrintStream(new File("Data/Tests/DualPentachoronTest/results.txt"));
//    } catch (FileNotFoundException e1) {
//      System.out.println("In here");
//      return;
//    }
    
    for(DihedralAngle q : Geometry.getDihedralAngles()) {
      out.println(q);
    }
    
    for(Angle q : Geometry.getAngles()) {
      out.println(q);
    }
    
    double[] lengths = new double[10];
    
    int i = 0;
    for(Edge e : Triangulation.edgeTable.values()) {
      StdEdge se = new StdEdge(e);
      Face f = null;
      for(Face f2 : Triangulation.faceTable.values()) {
        if(se.v1.isAdjFace(f2) || se.v2.isAdjFace(f2)) {
          
        } else {
          f = f2;
          break;
        }
      }
      lengths[i] = FaceHeight.valueAt(f, f.getLocalTetras().get(0))
                  +FaceHeight.valueAt(f, f.getLocalTetras().get(1));
      i++;
    }
    
    i = 0;
    for(Edge e : Triangulation.edgeTable.values()) {
      Length.At(e).setValue(lengths[i]);
      i++;
    }
    
    out.println("\n--------------------------------------");
    
    for(DihedralAngle q : Geometry.getDihedralAngles()) {
      out.println(q);
    }
    
    for(Angle q : Geometry.getAngles()) {
      out.println(q);
    }
  }

}
