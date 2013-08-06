package tests;

import geoquant.Alpha;
import geoquant.Eta;
import geoquant.GeoRecorder;
import geoquant.Geometry;
import geoquant.Geoquant;
import geoquant.LKCurvature;
import geoquant.Length;
import geoquant.Radius;
import inputOutput.TriangulationIO;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import solvers.Solver;
import solvers.implemented.conformaldiskflow;
import triangulation.Boundary;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Vector;
import visualization.TriangulationDisplay;

public class ConformalDiskflowTest {
 // private static final long serialVersionUID = 1L;
  
  
  public static void main(String[] args) {
    initializeQuantities();
  //  TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/trydomain.xml");
 //   EmbeddedTriangulation.readEmbeddedSurface("Development2D/surfaces/saddle.off");
    //   System.out.println(Geometry.getRadii());
 //   System.out.println(Geometry.getLKCurvatures());
    System.out.println("rad1 " + Length.at(Triangulation.edgeTable.get(1)));

    testFlow();
    System.out.println("rade " + Length.at(Triangulation.edgeTable.get(1)));
    TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/trydomainrun.xml");
    
    System.out.println("numfaces2 = "+Triangulation.faceTable.size());
    for(Vertex v: Triangulation.vertexTable.values())
//     if (v.getMultiplicity() == -1){
//        v.remove();
 //       break;
 //     }
 
    System.out.println("numfaces3 = "+Triangulation.faceTable.size());
    int i=1;
   
    Face sourceFace = Triangulation.faceTable.get(1);
//    System.out.println(sourceFace);
    Vector sourcePoint = new Vector(0.2, 0.2);
    int currentDepth = 10;
    double stepSize = 0.1;   
    
    TriangulationDisplay tD = new TriangulationDisplay();
//    tD.showTriangulation();
    

    //   System.out.println("hi1");
  //  Development development = new Development(new ManifoldPosition(sourceFace, sourcePoint), currentDepth, stepSize);
 //   Development development = null;
 //   System.err.println(development.getNodeList());
 //   System.out.println("hi2");
  //  development.addNodeAtSource(Color.green, new Vector(.01,.3));
 //   System.err.println(development.getNodeList());
 //   System.out.println("hi3");
 //   for(Vertex v: Triangulation.vertexTable.values()){
 //     System.out.println("hi4"+v);
  //    development.addVertexNode(Color.cyan, v);
  //  }
//    System.err.println("hehe "+development.getNodeList().get(4).getRadius());
 //   ColorScheme colorScheme = new ColorScheme(schemes.FACE);

 //   DevelopmentGUI viewGUI;
//    JFrame window = new DevelopmentGUI("Data/Triangulations/2DManifolds/trydomainrun.xml");
//    JFrame window = new DevelopmentGUI();
//    window.setVisible(true);
//    DevelopmentView2D view2D = null;
//    
//    view2D = new DevelopmentView2D(development, colorScheme);
//    view2D.updateDevelopment();
//    view2D.updateGeometry(true,false);
//    view2D.initializeNewManifold();

    //   DevelopmentView2D view2D = new DevelopmentView2D(development, colorScheme);
 //   development.addObserver(view2D);
//    System.err.println("hoho "+view2D.getNodeList().get(4).getRadius());
//      view2D.display(Primitives.regularPolygon(20));
//    view2D.updateGeometry(true, true);
    
 //   view2D.addCircles();
 //   view2D.updateGeometry();
 //   SceneGraphComponent sgcme = new SceneGraphComponent();
 //   sgcme.setGeometry(Primitives.regularPolygon(10,5));
 //   view2D.setContent(sgcme);
    
    
    for(Vertex v: Triangulation.vertexTable.values()) {
      LKCurvature lk = new LKCurvature(v);
      System.out.println(lk);
    }
    
    
//    view2D.addCircles();
    for(Edge e: Triangulation.edgeTable.values()) {
      System.out.println("len="+Length.valueAt(e));
      double radsum=0;
      for (Vertex v: e.getLocalVertices()){
        radsum+=Radius.valueAt(v);
      }
      System.out.println("radsum="+radsum);      
    }
    
    System.out.println("Done.");
    
    
  }
  
  private static void initializeQuantities() {
 //   TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/owl.xml");
    TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/trydomainbefore.xml");
    
    //   EmbeddedTriangulation.readEmbeddedSurface("Data/off/square2.off");
  //  EmbeddedTriangulation.readEmbeddedSurface("Data/off/square.off");
    // TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/square2.xml");
    System.out.println("numfaces1 = "+Triangulation.faceTable.size());
 //   System.err.println(Triangulation.greatestFace());
    for(Radius r : Geometry.getRadii()) {
      r.setValue(1.0);
    }
    for(Alpha a : Geometry.getAlphas()) {
      a.setValue(1.0);
    }
    for(Eta e : Geometry.getEtas()) {
      e.setValue(1.0);
    }
    for(Vertex v: Triangulation.vertexTable.values()) {
      v.setMultiplicity(1);
    }
    for(Edge e: Triangulation.edgeTable.values()) {
      e.setMultiplicity(-1);
    }
    for(Face f: Triangulation.faceTable.values()) {
      f.setMultiplicity(1);
      f.setColor(Color.YELLOW);
    }
    
    Boundary.makeBoundary();
 
    for (Vertex v: Boundary.boundaryVertexTable.values()){
      v.setMultiplicity(0);
    }
    
    for (Edge e: Boundary.boundaryEdgeTable.values()){
      e.setMultiplicity(0);
    }
    
    // New Vertex
    Vertex newV = new Vertex(Triangulation.greatestVertex()+1);
//    newV.setIndex(Triangulation.greatestVertex()+1);
    newV.setMultiplicity(-1);
    Triangulation.putVertex(newV);

    Radius.at(newV).setValue(9);
    Alpha.at(newV).setValue(1);
    
    // New Edges
//    System.out.println(Boundary.boundaryVertexTable.values());
    
    for (Vertex v: Boundary.boundaryVertexTable.values()){
      Edge newE= new Edge(Triangulation.greatestEdge()+1);
//      newE.setIndex(Triangulation.greatestEdge()+1);
      newE.setMultiplicity(1);
      newE.addVertex(v);
      newE.addVertex(newV);

      for(Edge e: v.getLocalEdges()){
        e.addEdge(newE);
        newE.addEdge(e);
      }
      for (Edge e: newV.getLocalEdges()){
        e.addEdge(newE);
        newE.addEdge(e);
      }
      System.out.println(newE);
      Triangulation.putEdge(newE);
      
      newV.addEdge(newE);
      newV.addVertex(v);
      v.addVertex(newV);
      v.addEdge(newE);
      
      Eta.at(newE).setValue(-1);
//      System.out.println(Eta.valueAt(newE));
//      System.out.println(Geometry.getEtas());
//      System.out.println(Geometry.getRadii());
//      System.out.println(Geometry.getLengths());
//      System.out.println(Geometry.getAngles());
      
//      System.out.println(newE);
//      System.out.println(Triangulation.edgeTable.values());
    }
    // At this point, the vertex has neighbor vertices and edges
    //  new edges have neighbor vertices, some neighbor edges
//    System.out.println(Triangulation.vertexTable.values());
    
    // New Faces

    for (Edge e: Boundary.boundaryEdgeTable.values()){
      Face newF = new Face(Triangulation.greatestFace()+1);
 //     System.out.println(newF.getIndex());
 //     newF.setIndex(Triangulation.greatestFace()+1);
      newF.setMultiplicity(-1);
      newF.addVertex(newV);
      newF.addEdge(e);
      for (Face f: e.getLocalFaces()){
        newF.addFace(f);
        f.addFace(newF);
        newF.setColor(Color.RED);
      }
      e.addFace(newF);
      newV.addFace(newF);
      
      for (Vertex v: e.getLocalVertices()){
        newF.addVertex(v);
        v.addFace(newF);
        
        
        
        for (Edge ed: v.getLocalEdges()){
          if (ed.isAdjVertex(newV)){
            newF.addEdge(ed);
            for (Face fa: ed.getLocalFaces()){
              newF.addFace(fa); 
              fa.addFace(newF);
            }
            ed.addFace(newF);
          }
        }
      }
      Triangulation.putFace(newF);
 //     TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/saddle.xml");
      
      //      System.err.println("yess"+newF);
   }
  }

  private static void testFlow() {
    Solver solver = new conformaldiskflow();
    
    List<Class<? extends Geoquant>> list = new LinkedList<Class<? extends Geoquant>>();
    list.add(Radius.class);
    list.add(LKCurvature.class);
    GeoRecorder rec = new GeoRecorder(list);
    solver.addObserver(rec);
    double[] radii = new double[Triangulation.vertexTable.size()];
    int i = 0;
    for(Radius r : Geometry.getRadii()) {
      radii[i] = r.getValue();
      i++;
    }
    
    solver.setStepsize(0.05);
    
    radii = solver.run(radii, 100);
  
    int j=0;
    for(Vertex v: Triangulation.vertexTable.values()) {
//      System.out.println("rad1 = "+Radius.at(v).getValue());
//      System.out.println("rad2 = "+radii[j]);
      Radius.at(v).setValue(radii[j]);   
      j++;
    }
    
   
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/flowdata.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    System.err.println(out);
    
    out.println("RADII:");
    for(List<Double> values : rec.getValueHistory(Radius.class)) {
//      System.out.println(values);
    }
    
    
  System.out.println("CURVATURES:");
    for(List<Double> values : rec.getValueHistory(LKCurvature.class)) {
      System.out.println(values);
    }
 /*   out.println(values);
*/
  }
  
}


