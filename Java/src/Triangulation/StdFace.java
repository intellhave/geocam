package Triangulation;

import java.util.Hashtable;
import java.util.Iterator;

public class StdFace {

  private static Hashtable<Face, StdFace> orientation = new Hashtable<Face, StdFace>();

  public Vertex v1;
  public Vertex v2;
  public Vertex v3;

  public Edge e12;
  public Edge e13;
  public Edge e23;

  public StdFace(Face f) {
    this(f, f.getLocalVertices().get(0));
  }

  // Puts the face's adjacent simplices into the following orientation
  // v1 -> e12 -> v2 -> e23 -> v3 -> e13
  public StdFace(Face f, Vertex v1, Edge e12) {
    if (v1 == null) {
      System.err.println("Not valid to have a null v1");
    }
    if (e12 == null) {
      System.err.println("Not valid to have a null e12");
    }
    this.v1 = v1;
    this.e12 = e12;

    // First we find v2 and v3, these can be used to find
    // e23 and e13 later
    for (Vertex v : this.e12.getLocalVertices()) {
      if (!v.equals(this.v1)) {
        this.v2 = v;
        break;
      }
    }
    if (this.v2 == null) {
      System.err.println("Failed to find v2");
    }
    for (Vertex v : f.getLocalVertices()) {
      if (!v.equals(this.v1) && !v.equals(this.v2)) {
        this.v3 = v;
        break;
      }
    }
    if (this.v3 == null) {
      System.err.println("Failed to find v3");
    }

    // find e13 and e23
    for (Edge e : f.getLocalEdges()) {
      if (e.isAdjVertex(this.v2) && e.isAdjVertex(this.v3)) {
        this.e23 = e;
      } else if (e.isAdjVertex(this.v1) && e.isAdjVertex(this.v3)) {
        this.e13 = e;
      }
    }
    if (this.e23 == null) {
      System.err.println("Failed to find e23" + " with face " + f.getIndex());
    }
    if (this.e13 == null) {
      System.err.println("Failed to find e13" + " with face " + f.getIndex());
    }
  }

  public StdFace(Face f, Vertex v) {
    v1 = v;

    Vertex[] otherVertices = new Vertex[2];
    int count = 0;
    for (Vertex w : f.getLocalVertices()) {
      if (w != v) {
        otherVertices[count] = w;
        count++;
      }
    }
    v2 = otherVertices[0];
    v3 = otherVertices[1];

    fixFaceEdges(f);
  }

  public StdFace(Face f, Edge e) {
    Iterator<Vertex> localVertices = e.getLocalVertices().iterator();

    v1 = localVertices.next();
    v2 = localVertices.next();

    for (Vertex v : f.getLocalVertices()) {
      if (v != v1 && v != v2) {
        v3 = v;
      }
    }

    fixFaceEdges(f);
  }

  private void fixFaceEdges(Face f) {
    for (Edge e : f.getLocalEdges()) {
      boolean b1 = e.isAdjVertex(v1);
      boolean b2 = e.isAdjVertex(v2);
      boolean b3 = e.isAdjVertex(v3);

      if (b1 && b2) {
        e12 = e;
      } else if (b1 && b3) {
        e13 = e;
      } else if (b2 && b3) {
        e23 = e;
      }
    }
  }

  // orientation stuff
  public static StdFace getOrientedFace(Face face) {
    if (orientation.containsKey(face)) {
      return orientation.get(face);
    } else {
      return null;
    }
  }
  
  
  //creates an orientation for the triangulation using recursion over the faces
  //base case of the recursion prevents a face from being handled twice so if a triangulation
  //is non-orientable then no error will occur, instead there will just be some edges where the
  //orientation doesn't line up
  public static void generateOrientation() {
    if (Triangulation.faceTable.isEmpty()) {
      return;
    }
    orientation.clear();

    Face firstFace = Triangulation.faceTable.values().iterator().next();
    Vertex firstVert = firstFace.getLocalVertices().iterator().next();
    Edge firstEdge = firstVert.getLocalEdges().iterator().next();
    StdFace stdFace = new StdFace(firstFace, firstVert, firstEdge);
    orientation.put(firstFace, stdFace);

    for (Face f : firstFace.getLocalFaces()) {
      // figure out the earliest edge shared with this face
      if (stdFace.e12.isAdjFace(f)) {
        firstEdge = stdFace.e12;
        firstVert = stdFace.v2;
      } else if (stdFace.e23.isAdjFace(f)) {
        firstEdge = stdFace.e23;
        firstVert = stdFace.v3;
      } else if (stdFace.e13.isAdjFace(f)) {
        firstEdge = stdFace.e13;
        firstVert = stdFace.v1;
      }

      generateOrientationHelper(f, firstVert, firstEdge);
    }
  }

  private static void generateOrientationHelper(Face face, Vertex firstVert, Edge firstEdge) {
    if (orientation.containsKey(face)) {
      return;
    }

    StdFace stdFace = new StdFace(face, firstVert, firstEdge);
    orientation.put(face, stdFace);

    for (Face f : face.getLocalFaces()) {
      // figure out the earliest vertex shared with this face
      if (stdFace.e12.isAdjFace(f)) {
        firstEdge = stdFace.e12;
        firstVert = stdFace.v2;
      } else if (stdFace.e23.isAdjFace(f)) {
        firstEdge = stdFace.e23;
        firstVert = stdFace.v3;
      } else if (stdFace.e13.isAdjFace(f)) {
        firstEdge = stdFace.e13;
        firstVert = stdFace.v1;
      }

      generateOrientationHelper(f, firstVert, firstEdge);
    }
  }
  
  public String toString() {
    return "" +  v1.getIndex() + "->" + v2.getIndex() + "->" + v3.getIndex();
  }

}
