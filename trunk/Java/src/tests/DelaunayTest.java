package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import Geoquant.Length;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Vertex;
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

  @Test
  public void isDelaunayTest() {
    Edge hingeEdge = new Edge(11);
    Length.At(hingeEdge).setValue(20.134);

    Edge[] boundEdges = new Edge[4];

    for (int i = 0; i < 4; i++) {
      boundEdges[i] = new Edge(i);
      //each edge is a little different in length
      Length.At(boundEdges[i]).setValue(20 + i);
    }
    
    Face[] faces = new Face[2];
    Vertex[] vertices = new Vertex[4];
    
    for (int i = 0; i < 2; i ++) {
      faces[i] = new Face(i);
    }
    for (int i = 0; i < 4; i++) {
      vertices[i] = new Vertex(i);
    }
    
  }

}
