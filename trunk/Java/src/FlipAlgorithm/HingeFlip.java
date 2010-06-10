package FlipAlgorithm;

import FlipAlgorithm.HingeInfo.HingeType;
import Geoquant.Angle;
import Geoquant.Length;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Vertex;

public class HingeFlip {

//the names of the various values correspond to the pictures below
/*       v2
         /|\
        / | \
  e2   /  |  \ e3
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

         v2
         / \
        /   \
  e2   /     \ e3
      /       \
     /    f1   \
    /           \
    -------------
v1  \    e0     / v3
     \         /
      \  f0   /
  e1   \     / e4
        \   /
         \ /
         v0
*/

//performs a topological flip and determines the geometric configuration
//of the adjacent triangles of Edge e so that e's length can be changed appropriately
public static Edge flip(Edge edge) {
  
  HingeInfo info = new HingeInfo();
  
  if (!prepForFlip(edge, info)) {
    System.err.println("The flip likely failed because prepForFlip did NOT succeed");
    return null;
  }
  
  HingeType type = determineHingeType(edge);
  if (type == HingeType.NegativeNegative) {
    flipNN(info);
  } else if (type == HingeType.PositiveNegative) {
    flipPN(info);
  } else {
    flipPP(info);
  }
  
  return null;
}

public static HingeType determineHingeType(Edge edge) {
  int numPositive = 0;
  for (Face face : edge.getLocalFaces()) {
    if (face.isNegative()) {
      //don't increment
    } else {
      numPositive += 1;
    }
  }
  
  switch(numPositive) {
    case 0 :
      return HingeType.NegativeNegative;
    case 1 :
      return HingeType.PositiveNegative;
    case 2 :
      return HingeType.PositivePositive;
  };
  return HingeType.PositivePositive;
}

//performs the recalculation of the length of edge e0 for a flip that starts
//as positive positive
public static void flipPP(HingeInfo info) {
  
  double ang0 =   Angle.valueAt(info.vertices[0], info.faces[0])
                + Angle.valueAt(info.vertices[0], info.faces[1]);
  
  double ang2 =   Angle.valueAt(info.vertices[2], info.faces[0])
                + Angle.valueAt(info.vertices[2], info.faces[1]);
  
  double len1,len4;
  len1 = Length.valueAt(info.edges[1]);
  len4 = Length.valueAt(info.edges[4]);
  
  //ang0 and ang2 will be used later so we will switch over to ang for the computation
  double ang = ang0;
  if (ang == Math.PI) { //if the edges are colinear 
    Length.At(info.edges[0]).setValue(len1 + len4);
    return;
  } else if (ang0 > Math.PI) { //this can happen when flipping a non-convex hinge
    ang =   ang2;
    len1 = Length.valueAt(info.edges[2]);
    len4 = Length.valueAt(info.edges[3]);
  }
  
  double edgeLength = Math.sqrt( (len1 * len1) + (len4 * len4) - (2 * len1 * len4 * Math.cos(ang)));
  
  Length.At(info.edges[0]).setValue(edgeLength);
  
  if (ang0 > Math.PI) {
    info.faces[0].setNegativity(true);
  } else if (ang2 > Math.PI) {
    info.faces[1].setNegativity(true);
  }
}

//performs the recalculation of the length of edge e0 for a flip that starts
//as positive negative
public static void flipPN(HingeInfo info) {
  /*double e_length;
  double alpha, beta;
  alpha = angle(h.e0_len, h.e1_len, h.e2_len);
  beta  = angle(h.e0_len, h.e4_len, h.e3_len);
  e_length = sqrt(h.e4_len * h.e4_len + h.e1_len * h.e1_len - 2 * h.e4_len * h.e1_len * cos(abs(beta - alpha)));

  //Length::At(Triangulation::edgeTable[b.e0])->setValue(e_length);
  h.e0_len_after = e_length;*/
  double alpha, beta;
  alpha = Angle.valueAt(info.vertices[0], info.faces[0]);
  beta = Angle.valueAt(info.vertices[0], info.faces[1]);
  
  double len1 = Length.valueAt(info.edges[1]);
  double len4 = Length.valueAt(info.edges[4]);
  
  double edgeLength = Math.sqrt(len4 * len4 + len1 * len1
                      - 2 * len4 * len1 * Math.cos(Math.abs(beta - alpha)));
  
  Face negFace = null;
  Face posFace = null;
  for (Face face : info.edges[0].getLocalFaces()) {
    if (face.isNegative()) {
      negFace = face;
    } else {
      posFace = face;
    }
  }
  
  //safety check
  if (negFace == null || posFace == null) {
    System.err.println("Hinge might not have been Pos/Neg afterall");
  }
  
  Vertex v0 = info.vertices[0];
  Vertex v2 = info.vertices[2];
  if (  Angle.valueAt(v0, negFace) < Angle.valueAt(v0, posFace)
        && Angle.valueAt(v2, negFace) < Angle.valueAt(v2, posFace)) {
    //negative face is inside the positive face
    info.faces[0].setNegativity(false);
    info.faces[1].setNegativity(false);
  } else if (Angle.valueAt(v0, negFace) > Angle.valueAt(v0, posFace)
              && Angle.valueAt(v2, negFace) > Angle.valueAt(v2, posFace)) {
    info.faces[0].setNegativity(true);
    info.faces[1].setNegativity(true);
  } else if (Angle.valueAt(v0, negFace) > Angle.valueAt(v0, posFace)
              && Angle.valueAt(v2, negFace) < Angle.valueAt(v2, posFace)) {
    //they stay the same... for now
  } else if (Angle.valueAt(v0, negFace) < Angle.valueAt(v0, posFace)
              && Angle.valueAt(v2, negFace) > Angle.valueAt(v2, posFace)) {
    //they stay the same... for now
  }
}

//performs the recalculation of the length of edge e0 for a flip that starts
//as a negative negative
public static void flipNN(HingeInfo info) {
  
  double ang0 =   Angle.valueAt(info.vertices[0], info.faces[0])
                + Angle.valueAt(info.vertices[0], info.faces[1]);
  
  double ang2 =   Angle.valueAt(info.vertices[2], info.faces[0])
                + Angle.valueAt(info.vertices[2], info.faces[1]);
  
  double len1,len4;
  len1 = Length.valueAt(info.edges[1]);
  len4 = Length.valueAt(info.edges[4]);
  
  //ang0 and ang2 will be used later so we will switch over to ang for the computation
  double ang = ang0;
  if (ang == Math.PI) { //if the edges are colinear 
    Length.At(info.edges[0]).setValue(len1 + len4);
    return;
  } else if (ang0 > Math.PI) { //this can happen when flipping a non-convex hinge
    ang =   ang2;
    len1 = Length.valueAt(info.edges[2]);
    len4 = Length.valueAt(info.edges[3]);
  }
  
  double edgeLength = Math.sqrt( (len1 * len1) + (len4 * len4) - (2 * len1 * len4 * Math.cos(ang)));
  
  Length.At(info.edges[0]).setValue(edgeLength);
  
  if (ang0 > Math.PI) {
    info.faces[0].setNegativity(false);
  } else if (ang2 > Math.PI) {
    info.faces[1].setNegativity(false);
  }
}

/*
         v2
         / \
        /   \
  e2   /     \ e3
      /       \
     /    f1   \
    /           \
    -------------
v1  \    e0     / v3
     \         /
      \  f0   /
  e1   \     / e4
        \   /
         \ /
          v0
*/
//performs the topological changes for the flip
public static void topoFlip(HingeInfo info) {
  Edge hingeEdge = info.edges[0];
  
  //first we'll remove any adjacent edges and the two adjacent vertices
  for (Vertex vertex : hingeEdge.getLocalVertices()) {
    vertex.removeEdge(hingeEdge);
    hingeEdge.removeVertex(vertex);
    for (Edge edge : vertex.getLocalEdges()) {
      boolean skipThisEdge = false;
      //if this edge is part of the hinge there is no reason to remove it
      for (int i = 0; i < 5; i++) {
        if (info.edges[i].equals(edge)) {
          skipThisEdge = true;
          break;
        }
      }
      if (skipThisEdge) {
        continue;
      }
      //mutual removal
      edge.removeEdge(hingeEdge);
      hingeEdge.removeEdge(edge);
    }
  }

  Face e2AdjFace = null;
  for (Face face : info.edges[2].getLocalFaces()) {
    if (!face.equals(info.faces[0])) {
      e2AdjFace = face;
      break;
    }
  }

  Face e4AdjFace = null;
  for (Face face : info.edges[4].getLocalFaces()) {
    if (!face.equals(info.faces[1])) {
      e4AdjFace = face;
      break;
    }
  }

  //remove e2 v2 with f0
  //also e2's adj face
  info.edges[2].removeFace(info.faces[0]);
  info.vertices[2].removeFace(info.faces[0]);
  e2AdjFace.removeFace(info.faces[0]);
  
  info.faces[0].removeEdge(info.edges[2]);
  info.faces[0].removeVertex(info.vertices[2]);
  info.faces[0].removeFace(e2AdjFace);
  
  //remove e4 v0 with f1
  //also e4's adj face
  info.edges[4].removeFace(info.faces[1]);
  info.vertices[0].removeFace(info.faces[1]);
  e4AdjFace.removeFace(info.faces[1]);
  
  info.faces[1].removeEdge(info.edges[4]);
  info.faces[1].removeVertex(info.vertices[0]);
  info.faces[1].removeFace(e4AdjFace);
  
  //add e2 v2 with f1
  //also e2's adj face
  info.edges[2].addFace(info.faces[1]);
  info.vertices[2].addFace(info.faces[1]);
  e2AdjFace.addFace(info.faces[1]);
  
  info.faces[1].addEdge(info.edges[2]);
  info.faces[1].addVertex(info.vertices[2]);
  info.faces[1].addFace(e2AdjFace);
  
  //add e4 v0 with f0
  //also e4's adj face
  info.edges[4].removeFace(info.faces[0]);
  info.vertices[0].removeFace(info.faces[0]);
  e4AdjFace.removeFace(info.faces[0]);
  
  info.faces[0].removeEdge(info.edges[4]);
  info.faces[0].removeVertex(info.vertices[0]);
  info.faces[0].removeFace(e4AdjFace);
  
  //add e0 with v1 and v3 and all of their adjacent edges
  info.vertices[1].addEdge(hingeEdge);
  info.vertices[3].addEdge(hingeEdge);
  
  hingeEdge.addVertex(info.vertices[1]);
  hingeEdge.addVertex(info.vertices[3]);
  
  for (Edge edge : info.vertices[1].getLocalEdges()) {
    edge.addEdge(hingeEdge);
    hingeEdge.addEdge(edge);
  }
  for (Edge edge : info.vertices[3].getLocalEdges()) {
    edge.addEdge(hingeEdge);
    hingeEdge.addEdge(edge);
  }
  
}

//puts the relevant Simplices into the arrays in "info"
//their indices will match the indices/combinatorics of the above pictures
public static boolean prepForFlip(Edge edge, HingeInfo info) {
  
  //can't flip borders
  if (edge.isBorder()) {
    return false;
  }
  
  info.edges[0] = edge;
  
  int index = 0; //just a counter, gonna use it all over the place
  for (Face face : edge.getLocalFaces()) {
    info.faces[index] = face;
    index += 1;
  }
  
  index = 0;
  for (Vertex vertex : edge.getLocalVertices()) {
    info.vertices[index] = vertex;
    index += 2; //very important, we want the second vertex to be v_2
  }
  
  for (int i = 0; i < 2; i++) {
    for (Vertex vertex : info.faces[i].getLocalVertices()) {
      if (vertex.equals(info.vertices[0]) || vertex.equals(info.vertices[2])) {
        continue; //we already have these assigned so skip 'em
      } else if (i == 0) {
        info.vertices[1] = vertex;
      } else if (i == 1) {
        info.vertices[3] = vertex;
      }
    }
  }
  
  index = 0;
  for (int i = 0; i < 2; i++) {
    for (Edge e : info.faces[i].getLocalEdges()) {
      if (e.equals(edge)) {
        continue;
      } else if (i == 0 && e.isAdjVertex(info.vertices[0])) { // next to f_0
        //e1
        info.edges[1] = e;
      } else if (i == 0 && e.isAdjVertex(info.vertices[2])) {
        //e2
        info.edges[2] = e;
      } else if (i == 1 && e.isAdjVertex(info.vertices[0])) { // next to f_1
        //e4
        info.edges[4] = e;
      } else if (i == 1 && e.isAdjVertex(info.vertices[2])) {
        //e3
        info.edges[3] = e;
      }
    }
  }
  
  //check that all the values got filled out
  boolean success = true;
  for (int i = 0; i < 4; i++) {
    if (info.vertices[i] == null) {
      System.err.println("vertex " + i + "was never found!");
      success = false;
    }
  }
  for (int i = 0; i < 5; i++) {
    if (info.edges[i] == null) {
      System.err.println("edge " + i + "was never found!");
      success = false;
    }
  }
  for (int i = 0; i < 2; i++) {
    if (info.faces[i] == null) {
      System.err.println("face " + i + "was never found!");
      success = false;
    }
  }
  
  return success;
}

//public boolean isDegenerate(struct simps &h);

//the edge being flipped is zero
//public void degenerateFlippedIsZero(simps b);

//the edge being flipped is NONzero but one of the other four edges is zero
//public void degenerateNonFlippedIsZero(simps b);

//all the edges have nonzero length, but one of the angles is zero
//public void degenerateOnlyAngleIsZero(simps b);
}
