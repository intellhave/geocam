package tests;

import static org.junit.Assert.*;
import inputOutput.TriangulationIO;

import org.junit.Test;

import triangulation.*;

import Geoquant.Length;
import FlipAlgorithm.Delaunay;

/*          v2
           /|\
          / | \
     e2  /  |  \ e3
        /   |   \
       /    |e0  \
      /     |     \
  v1  \ f0  | f1  / v3
       \    |    /
        \   |   /
    e1   \  |  / e4
          \ | /
           \|/
            v0

*/
public class DelaunayTest {
  
  public static void setUpHinge(Vertex[] vertices, Edge[] edges, Face[] faces) {
   /* Edge hingeEdge = new Edge(11);
    Length.At(hingeEdge).setValue(20.134);

    Edge[] edges = new Edge[4];

    for (int i = 0; i < 4; i++) {
      edges[i] = new Edge(i);
      //each edge is a little different in length
      Length.At(edges[i]).setValue(20 + i);
    }
    
    Face[] faces = new Face[2];
    Vertex[] vertices = new Vertex[4];
    
    for (int i = 0; i < 2; i ++) {
      faces[i] = new Face(i);
    }
    for (int i = 0; i < 4; i++) {
      vertices[i] = new Vertex(i);
    }

    //vertex 0
    vertices[0].addVertex(vertices[1]);
    vertices[0].addVertex(vertices[3]);
    
    vertices[0].addEdge(edges[0]);
    vertices[0].addEdge(edges[1]);
    vertices[0].addEdge(edges[4]);
    
    vertices[0].addFace(faces[0]);
    vertices[0].addFace(faces[1]);
    
    //vertex 1
    vertices[1].addVertex(vertices[0]);
    vertices[1].addVertex(vertices[2]);
   
    vertices[1].addEdge(edges[1]);
    vertices[1].addEdge(edges[2]);
    
    vertices[1].addFace(faces[0]);

    //vertex 2
    vertices[2].addVertex(vertices[1]);
    vertices[2].addVertex(vertices[3]);
    
    vertices[2].addEdge(edges[0]);
    vertices[2].addEdge(edges[2]);
    vertices[2].addEdge(edges[3]);
    
    vertices[2].addFace(faces[0]);
    vertices[2].addFace(faces[1]);
    
    //vertex 3
    vertices[3].addVertex(vertices[0]);
    vertices[3].addVertex(vertices[2]);
   
    vertices[3].addEdge(edges[3]);
    vertices[3].addEdge(edges[4]);
    
    vertices[3].addFace(faces[1]);
    
    //set edge adjacencies
    for (int i = 0; i < 4; i++) {
      
    }*/
  }

  @Test
  public void isDelaunayTest() {
    TriangulationIO.read2DTriangulationFile("Data/flip_test/convex_pair.txt");
    
    Edge hingeEdge = null;
    for (Edge edge : Triangulation.edgeTable.values()) {
      if (edge.getLocalFaces().size() > 1) {
        hingeEdge = edge;
        break;
      }
    }
    
    assertTrue(Delaunay.isDelaunay(hingeEdge));
    
    
  }

}
