package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.Color;

import javax.swing.JFrame;

import marker.ManifoldPosition;


import geoquant.Alpha;
import geoquant.Curvature2D;
import geoquant.Eta;
import geoquant.GeoRecorder;
import geoquant.Geometry;
import geoquant.Geoquant;
import geoquant.Length;
import geoquant.Radius;
import inputOutput.TriangulationIO;

import solvers.Solver;
import solvers.implemented.RicciFlow;
import triangulation.Boundary;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import development.*;
import visualization.*;
import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;

public class RicciflowTest {
// // private static final long serialVersionUID = 1L;
//  
//  private static int delay = 400; 
//  private static int numIterations = 100;
//
//    private static String inputfile = "Data/Triangulations/2DManifolds/tetrahedron3.xml";
// // private static String inputfile =   "Data/Triangulations/2DManifolds/icosahedronrandom.xml";
// // private static String inputfile = "Data/Triangulations/2DManifolds/kleinbottlerandom.xml";
//
//  public static void main(String[] args) {
//    initializeQuantities();
//   //   System.out.println(Geometry.getRadii());
// //   System.out.println(Geometry.getLKCurvatures());
// //   System.out.println("rad1 " + Length.at(Triangulation.edgeTable.get(1)));
//
//    testFlow();
// //   System.out.println("rade " + Length.at(Triangulation.edgeTable.get(1)));
//    TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/tryrfrun.xml");
//    
//    int i=1;
//   
//    Face sourceFace = Triangulation.faceTable.get(1);
////    System.out.println(sourceFace);
//    Vector sourcePoint = new Vector(0.2, 0.2);
//    int currentDepth = 10;
//    double stepSize = 0.1;   
//    
//    TriangulationDisplay tD = new TriangulationDisplay();
////    tD.showTriangulation();
//    
//
//    //   System.out.println("hi1");
//    Development development = new Development(new ManifoldPosition(sourceFace, sourcePoint), currentDepth, stepSize);
// //   Development development = null;
// //   System.err.println(development.getNodeList());
// //   System.out.println("hi2");
//  //  development.addNodeAtSource(Color.green, new Vector(.01,.3));
// //   System.err.println(development.getNodeList());
// //   System.out.println("hi3");
// //   for(Vertex v: Triangulation.vertexTable.values()){
// //     System.out.println("hi4"+v);
//  //    development.addVertexNode(Color.cyan, v);
//  //  }
////    System.err.println("hehe "+development.getNodeList().get(4).getRadius());
//    ColorScheme colorScheme = new ColorScheme(schemes.FACE);
//
// //   DevelopmentGUI viewGUI;
////    JFrame window = new DevelopmentGUI("Data/Triangulations/2DManifolds/trydomainrun.xml");
// 
// /* works */   
////    JFrame window = new DevelopmentGUI();
////    window.setVisible(true);
////    DevelopmentView2D view2D = null;
//
//    
////    view2D = new DevelopmentView2D(development, colorScheme);
////    view2D.updateDevelopment();
////    view2D.updateGeometry(true,false);
////    view2D.initializeNewManifold();
//
//    //   DevelopmentView2D view2D = new DevelopmentView2D(development, colorScheme);
// //   development.addObserver(view2D);
////    System.err.println("hoho "+view2D.getNodeList().get(4).getRadius());
////      view2D.display(Primitives.regularPolygon(20));
////    view2D.updateGeometry(true, true);
//    
// //   view2D.addCircles();
// //   view2D.updateGeometry();
// //   SceneGraphComponent sgcme = new SceneGraphComponent();
// //   sgcme.setGeometry(Primitives.regularPolygon(10,5));
// //   view2D.setContent(sgcme);
//    
//    
// //   for(Vertex v: Triangulation.vertexTable.values()) {
// //     LKCurvature lk = new LKCurvature(v);
// //     System.out.println(lk);
// //   }
//    
//    
////    view2D.addCircles();
////    for(Edge e: Triangulation.edgeTable.values()) {
////      System.out.println("len="+Length.valueAt(e));
////      double radsum=0;
////      for (Vertex v: e.getLocalVertices()){
////        radsum+=Radius.valueAt(v);
////      }
////      System.out.println("radsum="+radsum);      
////    }
//    
//    System.out.println("Done.");
//    
//    
//  }
//  
//  private static void initializeQuantities() {
//    TriangulationIO.readTriangulation(inputfile);
//  }
//
//  private static void testFlow() {
//    Solver solver = new RicciFlow();
//    
//    List<Class<? extends Geoquant>> list = new LinkedList<Class<? extends Geoquant>>();
//    list.add(Radius.class);
//    list.add(Curvature2D.class);
//    GeoRecorder rec = new GeoRecorder(list);
//    solver.addObserver(rec);
//    double[] radii = new double[Triangulation.vertexTable.size()];
//    int i = 0;
//    for(Radius r : Geometry.getRadii()) {
//      radii[i] = r.getValue();
//      i++;
//    }
//    
//    solver.setStepsize(0.05);
//    DevelopmentGUI window = new DevelopmentGUI();
//    window.setVisible(true);
// //   DevelopmentView2D view2D = null;
//    for(int j=1; j<numIterations; j++){
//      radii = solver.run(radii, 1);
//      try {
//        Thread.sleep(delay);
//        } catch(InterruptedException e) {
//        } 
//      window.updateDevelopment();
//      window.refresh2D();
//      window.refresh3D();
//    }
//    
//    int j=0;
//    for(Vertex v: Triangulation.vertexTable.values()) {
////      System.out.println("rad1 = "+Radius.at(v).getValue());
////      System.out.println("rad2 = "+radii[j]);
//      Radius.at(v).setValue(radii[j]);   
//      j++;
//    }
//    
//  
//    PrintStream out = null;
//    try {
//      out = new PrintStream(new File("Data/Tests/flowdata.txt"));
//    } catch (FileNotFoundException e1) {
//      return;
//    }
// /*   out.println("RADII:");
//    for(List<Double> values : rec.getValueHistory(Radius.class)) {
//      out.println(values);
//    }
//    
//*/    
//    out.println("CURVATURES:");
//    for(List<Double> values : rec.getValueHistory(Curvature2D.class)) {
//      out.println(values);
//    }
//
// /*   out.println(values);
//*/
//  }
  
}


