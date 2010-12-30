package tests;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import geoquant.LKCurvature;
import geoquant.Eta;
import geoquant.GeoRecorder;
import geoquant.Geometry;
import geoquant.Geoquant;
import geoquant.Radius;
import inputOutput.TriangulationIO;

import solvers.Solver;
import solvers.implemented.conformaldiskflow;
import triangulation.Boundary;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;


public class ConformalDiskflowTest {

  public static void main(String[] args) {
    initializeQuantities();
    TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/domain1plus.xml");
//    testFlow();

  }
  
  private static void initializeQuantities() {
    TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/domain1.xml");
    
    for(Radius r : Geometry.getRadii()) {
      r.setValue(1.0);
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
    }
    
    Boundary.makeBoundary();
 
    for (Edge e: Boundary.boundaryEdgeTable.values()){
      e.setMultiplicity(0);
    }
    
    // New Vertex
    Vertex newV = new Vertex(Triangulation.greatestVertex()+1);
//    newV.setIndex(Triangulation.greatestVertex()+1);
    newV.setMultiplicity(-1);
    Triangulation.putVertex(newV);
        
    // New Edges
    System.out.println(Boundary.boundaryVertexTable.values());
    
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
      newV.addEdge(newE);
      newV.addVertex(v);
      Triangulation.putEdge(newE);
      System.out.println(newE);
      System.out.println(Triangulation.edgeTable.values());
    }
    // At this point, the vertex has neighbor vertices and edges
    //  new edges have neighbor vertices, some neighbor edges
    System.out.println(Triangulation.vertexTable.values());
    
    // New Faces
    for (Edge e: Boundary.boundaryEdgeTable.values()){
      Face newF = new Face(Triangulation.greatestFace()+1);
 //     newF.setIndex(Triangulation.greatestFace()+1);
      newF.setMultiplicity(-1);
      newF.addVertex(newV);
      newF.addEdge(e);
      e.addFace(newF);
      newV.addFace(newF);
      
      for (Vertex v: e.getLocalVertices()){
        newF.addVertex(v);
        v.addFace(newF);
        for (Edge ed: v.getLocalEdges()){
          if (ed.isAdjVertex(newV)){
            newF.addEdge(ed);
            ed.addFace(newF);
            for (Vertex vert: ed.getLocalVertices()){
              if (!vert.equals(newV)){
                newF.addVertex(vert);
                vert.addFace(newF);
              }
            }
          }
        }
      }
      Triangulation.putFace(newF);   
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
      radii[i] = r.getValue() + i/5.0;
      i++;
    }
    
    solver.setStepsize(0.1);
    
    radii = solver.run(radii, 50);
    
    System.out.println("RADII:");
    for(List<Double> values : rec.getValueHistory(Radius.class)) {
      System.out.println(values);
    }
    
 //  System.out.println("CURVATURES:");
//    for(List<Double> values : rec.getValueHistory(Curvature2D.class)) {
//      System.out.println(values);
//    }
  }
}
