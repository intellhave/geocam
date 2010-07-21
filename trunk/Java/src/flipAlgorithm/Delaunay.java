package flipAlgorithm;

import geoquant.Angle;
import geoquant.PartialEdge;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;

public class Delaunay {
  /*
   * Indicates whether or not a particular edge is delaunay
   */
  public static boolean isDelaunay(Edge edge) {
    if (edge.isBorder()) {
      return true;
    }
    
    //the sum of the angles that aren't adjacent to the provided edge
    double totalAngle = 0;
    for (Face embeddedFace : edge.getLocalFaces()) {
      for (Vertex vertex : embeddedFace.getLocalVertices()) {
        if (vertex.isAdjEdge(edge)) {
          totalAngle += Angle.valueAt(vertex, embeddedFace);
        }
      }
    }
    
    return totalAngle < Math.PI;
  }

  /*
   * Indicates whether or not a particular edge is weighted Delaunay.
   */
  public static boolean isWeightedDelaunay(Edge edge) {
    if (edge.isBorder()) {
      return true;
    }
    return getDual(edge) > 0;
  }

  /*
   *  Checks whether the entire triangulation is weighted Delaunay
   */
  public static boolean isWeightedDelaunay() {
    for (Edge edge : Triangulation.edgeTable.values()) {
      if (!isWeightedDelaunay(edge)) {
        return false;
      }
    }
    return true;
  }

  /*
   *  true if the faces are both positive or both negative, otherwise false
   */
  public static boolean facesAreTheSame(Edge edge) {
    if (edge.isBorder()) {
      return true;
    }
    //assuming two faces for an edge, this loop will determine
    //b will be true if the two faces were the same boolean value
    boolean b = true;
    for (Face f : edge.getLocalFaces()) {
      b = b ^ f.isNegative();
    }
    return b;
  }

  //determines whether the hinge is a convex quadrilateral
  public static boolean isConvexHinge(Edge edge) {
    
    //compute the angle contributed by this hinge at each vertex
    double angle = 0;
    for (Vertex vertex : edge.getLocalVertices()) {
      for (Face embeddedFace : edge.getLocalFaces()) {
        angle += Angle.valueAt(vertex, embeddedFace);
      }
      //if either angle is too big the hinge must be non-convex there
      if (angle > Math.PI) {
        return false;
      }
      angle = 0;
    }
    
    return true;
  }

  /*
   * Returns the partial dual of a particular edge, as indicated
   * by the given face.
   * 
   * NOTE: USE EdgeHeight GEOQUANT INSTEAD
   */
  public static double getHeight(Face embeddedFace, Edge edge) {
    
    //grabs an arbitrary vertex next to this edge
    Vertex adjVertex = edge.getLocalVertices().iterator().next();
    Angle angle = Angle.At(adjVertex, embeddedFace);
    
    //find the Edge that is next to adjVertex, but is not the edge given
    //as a parameter
    Edge adjEdge = null;
    for (Edge e : embeddedFace.getLocalEdges()) {
      if (adjVertex.isAdjEdge(e) && !edge.equals(e)) {
        adjEdge = e;
        break;
      }
    }
    
    double ang = angle.getValue();
    double distanceAlongEdge = PartialEdge.valueAt(adjVertex, edge);
    double distanceAlongAdjEdge = PartialEdge.valueAt(adjVertex, adjEdge);
    
    return (distanceAlongAdjEdge - distanceAlongEdge * Math.cos(ang)) / (Math.sin(ang));
  }

  /*
   * Returns the dual length of a particular edge.
   */
  public static double getDual(Edge edge) {
    double dualLength = 0;
    for (Face embeddedFace : edge.getLocalFaces()) {
      dualLength += getHeight(embeddedFace, edge);
    }
    return dualLength;
  }

  /*
   * Deprecated: Should use PartialEdge geoquant
   * Returns the portion of an edge from a given vertex to the
   * edge's center.
   */
  public static double getPartialEdge(Edge edge, Vertex vertex) {
    return 0;
  }

  //computes the dirichlet energy of the triangulation
  //TODO: implement this, need a structure for defining functions on vertices
  public static double dirichletEnergy() {
    return 0;
  }

}
