package development;

import java.util.List;

import triangulation.Tetra;
import triangulation.Vertex;
import util.Matrix;

public class Development3DComputations {
  
  public static Vector getBarycentricCoords(Vector point, Tetra tetra) {
    List<Vertex> vertices = tetra.getLocalVertices();
    Vector v1 = Coord3D.coordAt(vertices.get(0), tetra);
    Vector v2 = Coord3D.coordAt(vertices.get(1), tetra);
    Vector v3 = Coord3D.coordAt(vertices.get(2), tetra);
    Vector v4 = Coord3D.coordAt(vertices.get(3), tetra);
    
    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double z1 = v1.getComponent(2);
    
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);
    double z2 = v2.getComponent(2);
    
    double x3 = v3.getComponent(0);
    double y3 = v3.getComponent(1);
    double z3 = v3.getComponent(2);
    
    double x4 = v4.getComponent(0);
    double y4 = v4.getComponent(1);
    double z4 = v4.getComponent(2);
    
    Matrix T = new Matrix(new double[][] { { (x1 - x4), (x2 - x4), (x3 - x4) },
                                           { (y1 - y4), (y2 - y4), (y3 - y4) },
                                           { (z1 - z4), (z2 - z4), (z3 - z4) } });
    Matrix Ti = null;
    try {
      Ti = T.inverse();
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    Vector l = Ti.transformVector(Vector.subtract(point, v4));
    double l1 = l.getComponent(0);
    double l2 = l.getComponent(1);
    double l3 = l.getComponent(2);
    double l4 = 1-l1-l2-l3;
    
    return new Vector(new double[] { l1,l2,l3,l4 });
  }

}
