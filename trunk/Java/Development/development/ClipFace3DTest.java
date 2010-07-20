package development;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import Triangulation.Edge;
import Triangulation.Tetra;
import Triangulation.Vertex;

public class ClipFace3DTest {

  @Test
  public void testSectorIntersection() {
    Frustum3D frustum = new Frustum3D(new Vector(0, 1, 0), new Vector(0, 0, 1),
        new Vector(1, 0, 0));
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(-.5, .5, .5), 0).toString(), "(0.0, 0.5, 0.5)");
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(.5, .5, -.5), 0), null);
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(.5, .5, -.5), 1), null);
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(.5, .5, -.5), 2).toString(), "(0.5, 0.5, 0.0)");
  }

  @Test
  public void testIntersectionWithFace() {
    Frustum3D frustum = new Frustum3D(new Vector(0, 1, 0), new Vector(0, 0, 1),
        new Vector(1, 0, 0));
    EmbeddedFace face = new EmbeddedFace(new Vector(0, 0, 1), new Vector(0, -1, 0), new Vector(
        0, 1, 0));
    assertEquals(frustum.findIntersectionWithFace(face, new Vector(-1, 0, 0))
        .toString(), "(0.0, 0.0, 0.0)");
    assertEquals(frustum.findIntersectionWithFace(face, new Vector(0, 1, 1)),
        null);
  }
  
  @Test
  public void testClipFace() {
    Tetra tetra = new Tetra(0);
    Vertex v1 = new Vertex(0);
    Vertex v2 = new Vertex(1);
    Vertex v3 = new Vertex(2);
    Vertex v4 = new Vertex(3);
    
    Edge e1 = new Edge(0);
    Edge e2 = new Edge(1);
    Edge e3 = new Edge(2);
    Edge e4 = new Edge(3);
    Edge e5 = new Edge(4);
    Edge e6 = new Edge(5);
    
    Triangulation.Face f1 = new Triangulation.Face(0);
    Triangulation.Face f2 = new Triangulation.Face(1);
    Triangulation.Face f3 = new Triangulation.Face(2);
    Triangulation.Face f4 = new Triangulation.Face(3);
    
    e1.addVertex(v1);
    e1.addVertex(v2);
    
    e2.addVertex(v2);
    e2.addVertex(v4);
    
    e3.addVertex(v1);
    e3.addVertex(v4);

    e4.addVertex(v2);
    e4.addVertex(v3);
    
    e5.addVertex(v3);
    e5.addVertex(v4);
   
    e6.addVertex(v1);
    e6.addVertex(v3);
    
    f1.addVertex(v1);
    f1.addVertex(v2);
    f1.addVertex(v4);

    f2.addVertex(v1);
    f2.addVertex(v4);
    f2.addVertex(v3);

    f3.addVertex(v2);
    f3.addVertex(v1);
    f3.addVertex(v3);
    
    f4.addVertex(v2);
    f4.addVertex(v3);
    f4.addVertex(v4);

    v1.addEdge(e1);
    v1.addEdge(e3);
    v1.addEdge(e6);
    v1.addFace(f1);
    v1.addFace(f2);
    v1.addFace(f3);
    
    v2.addEdge(e1);
    v2.addEdge(e2);
    v2.addEdge(e4);
    v2.addFace(f1);
    v2.addFace(f3);
    v2.addFace(f4);
    
    v3.addEdge(e6);
    v3.addEdge(e4);
    v3.addEdge(e5);
    v3.addFace(f4);
    v3.addFace(f2);
    v3.addFace(f3);
    
    v4.addEdge(e3);
    v4.addEdge(e2);
    v4.addEdge(e5);
    v4.addFace(f1);
    v4.addFace(f2);
    v4.addFace(f4);

    tetra.addVertex(v1);
    tetra.addVertex(v2);
    tetra.addVertex(v3);
    tetra.addVertex(v4);
    
    tetra.addEdge(e1);
    tetra.addEdge(e2);
    tetra.addEdge(e3);
    tetra.addEdge(e4);
    tetra.addEdge(e5);
    tetra.addEdge(e6);
    
    tetra.addFace(f1);
    tetra.addFace(f2);
    tetra.addFace(f3);
    tetra.addFace(f4);


    Frustum3D frustum = new Frustum3D(new Vector(0, 0, 1), new Vector(0, -.5, -1), new Vector(-.5, -.5, .5));
    ArrayList<Vector> result = frustum.clipFace(tetra);
    System.out.println("results:");
    for(int i =0 ; i < result.size(); i++) {
      System.out.println(result.get(i));
    }
  }
}
