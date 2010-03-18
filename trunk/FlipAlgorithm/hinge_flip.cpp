#include "hinge_flip.h"


/*       v2
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
Edge flip(Edge e) {

    struct simps hingeInfo;
    if(!prepForFlip(e, &hingeInfo)) {
        return e;
    }

    double lengthAfterFlip;

  /*//determine if we are in a degenerate case, and what type
  //the edge being flipped has length zero
  if (bucket.e0_len) {
    degenerateFlippedIsZero(bucket);
  //on of the other four edges is zero
  } else if (bucket.e1_len == 0.0 || bucket.e2_len == 0.0 || bucket.e3_len == 0.0 || bucket.e4_len == 0) {
    degenerateNonFlippedIsZero(bucket);
  //lengths are good, but angles could still be bad
  } else if (bucket.e0_len == bucket.e1_len + bucket.e2_len || bucket.e0_len == bucket.e3_len + bucket.e4_len) {
    degenerateOnlyAngleIsZero(bucket);

    //the hinge in nondegenerate and we should proceed with the old flip code that will handle it well
    } else*/
    if (!Triangulation::faceTable[hingeInfo.f0].isNegative() && !Triangulation::faceTable[hingeInfo.f1].isNegative()) {

        flipPP(hingeInfo);

    } else if (Triangulation::faceTable[hingeInfo.f0].isNegative() && Triangulation::faceTable[hingeInfo.f1].isNegative()) {

        flipNN(hingeInfo);

    //note the following || is technically exclusive because the two && cases are checked above
    } else if((Triangulation::faceTable[hingeInfo.f0].isNegative() == true) || (Triangulation::faceTable[hingeInfo.f1].isNegative() == true)) {

        flipPN(hingeInfo);

    } else {
        printf("hmm, no case was found\n");
        //just need to escape return e seems sufficient;
        return e;
    }

    if (notDegenerate(hingeInfo)) {

        topoFlip(e, hingeInfo);

        Length::At(e)->remove();

        //apply necessary changes to Triangulation
        Length::At(Triangulation::edgeTable[hingeInfo.e0])->setValue(hingeInfo.e0_len_after);
        Triangulation::faceTable[hingeInfo.f0].setNegativity(hingeInfo.f0neg);
        Triangulation::faceTable[hingeInfo.f1].setNegativity(hingeInfo.f1neg);

    } else {
        cout << "flip would have made the hinge degenerate\n";
    }

    return Triangulation::edgeTable[e.getIndex()];
} //end of flip


/*
 *      handles both: PP -> PP
 *                    PP -> PN
 */
void flipPP(struct simps &h) {
  //determine the flipped edges new length after flipping
  double e_length;

  double ang = h.a2;
  double len2 = h.e2_len;
  double len3 = h.e3_len;
  if (ang >= PI) {
    ang = h.a0;
    len2 = h.e1_len;
    len3 = h.e4_len;
  }
  
  e_length = sqrt( (len2 * len2) + (len3 * len3) - (2 * len2 * len3 * cos(ang)));

  //Length::At(Triangulation::edgeTable[b.e0])->setValue(e_length);
  h.e0_len_after = e_length;

  //figure out which one is negative
  //in the PP_PN case we need to set one to be negative
  if (h.a0 > PI || h.a2 > PI) {
    if (angle(h.e4_len, h.e1_len, e_length) > angle(h.e3_len, h.e2_len, e_length)) {
      //Triangulation::faceTable[b.f0].setNegativity(true);
      h.f0neg = true;

    } else {
      //Triangulation::faceTable[b.f1].setNegativity(true);
      h.f1neg = true;
    }
  }
}

//name says it all, this might be a bit much but wanted to
//simplify reading the code in flipPN
void swap(int *j, int *k) {
  int temp;
  temp = *j;
  *j = *k;
  *k = temp;
}
void swap(double *j, double *k) {
  double temp;
  temp = *j;
  *j = *k;
  *k = temp;
}
void swap(bool *j, bool *k) {
    bool temp;
    temp = *j;
    *j = *k;
    *k = temp;
}
/*
 *  handles:  PN -> PN
 *            PN -> PP
 *            PN -> NN
 */
 /*           v3
*            / \
*           /   \
*          / f1  \
*         /       \
*     e4 /    v1   \ e3
*       /    / \    \
*      /   /     \   \
*     /  / e1   e2 \  \
*    / /  f0 Neg     \ \
* v0//_________________\\v2
*             e0
*
*     INSIDE
*/
/*
*   v3
*   |\
*   | \        v1
*   |  \e3     /|
*   | f1\  e1/  |
* e4| Pos\ / f0 |e2
*   |    /\ Neg |
*   |  /   \    |
* v0|/______\___|v2
*       e0
*
*   RIGHT
*/
void flipPN(struct simps &h) {
    double e_length;
    double alpha, beta;
    alpha = angle(h.e0_len, h.e1_len, h.e2_len);
    beta  = angle(h.e0_len, h.e4_len, h.e3_len);
    e_length = sqrt(h.e4_len * h.e4_len + h.e1_len * h.e1_len - 2 * h.e4_len * h.e1_len * cos(abs(beta - alpha)));

    //Length::At(Triangulation::edgeTable[b.e0])->setValue(e_length);
    h.e0_len_after = e_length;

    //flip flop everything so that the negative triangle is f0
    if (h.f1neg) {
        swap(&h.v1, &h.v3);
        swap(&h.e1, &h.e4);
        swap(&h.e2, &h.e3);
        swap(&h.f0, &h.f1);
        swap(&h.f0neg,&h.f1neg);
        //next two lines are the important ones but the ones above were swapped
        //as well for consistency
        swap(&h.e1_len, &h.e4_len);
        swap(&h.e2_len, &h.e3_len);
    }

    //now determine which is negative and positive
    //a negative leaning across e1
    if (abs(angle(h.e0_len, h.e1_len, h.e2_len)) < abs(angle(h.e0_len, h.e4_len, h.e3_len)) &&
        abs(angle(h.e0_len, h.e2_len, h.e1_len)) > abs(angle(h.e0_len, h.e3_len, h.e4_len))) { // right case as shown above
        //Triangulation::faceTable[h.f1].setNegativity(true);
        //Triangulation::faceTable[h.f0].setNegativity(false);
        h.f1neg = true;
        h.f0neg = false;
    
    //a negative leaning across e2
    } else if (abs(angle(h.e0_len, h.e1_len, h.e2_len)) > abs(angle(h.e0_len, h.e4_len, h.e3_len)) &&
               abs(angle(h.e0_len, h.e2_len, h.e1_len)) < abs(angle(h.e0_len, h.e3_len, h.e4_len))) { //left case... flip across vert of above picture
        //actually setting these shouldn't be necessary but its nice to be explicit
        //Triangulation::faceTable[h.f1].setNegativity(false);
        //Triangulation::faceTable[h.f0].setNegativity(true);
        h.f1neg = false;
        h.f0neg = true;

    //a negative inside a positie
    } else if (abs(angle(h.e0_len, h.e1_len, h.e2_len)) > abs(angle(h.e0_len, h.e4_len, h.e3_len)) &&
               abs(angle(h.e0_len, h.e2_len, h.e1_len)) > abs(angle(h.e0_len, h.e3_len, h.e4_len))) {
        //Triangulation::faceTable[h.f1].setNegativity(true);
        //Triangulation::faceTable[h.f0].setNegativity(true);
        h.f1neg = true;
        h.f0neg = true;

    //a positive inside a negative
    } else if (abs(angle(h.e0_len, h.e1_len, h.e2_len)) < abs(angle(h.e0_len, h.e4_len, h.e3_len)) &&
               abs(angle(h.e0_len, h.e2_len, h.e1_len)) < abs(angle(h.e0_len, h.e3_len, h.e4_len))) {
        //Triangulation::faceTable[h.f1].setNegativity(false);
        //Triangulation::faceTable[h.f0].setNegativity(false);
        h.f1neg = false;
        h.f0neg = false;

    }
}

/*
 *    handles NN -> NN
 *            NN -> PN
 */
void flipNN(struct simps &h) {
    double e_length;
    e_length = sqrt(h.e2_len*h.e2_len + h.e3_len*h.e3_len - 2 * h.e2_len * h.e3_len * cos(h.a2));

    //Length::At(Triangulation::edgeTable[h.e0])->setValue(e_length);
    h.e0_len_after = e_length;

    if (h.a0 > PI || h.a2 > PI) {
        if (angle(e_length, h.e4_len, h.e1_len) < angle(e_length, h.e3_len, h.e2_len)) {
            //Triangulation::faceTable[h.f0].setNegativity(false);
            h.f0neg = false;
        } else {
            //Triangulation::faceTable[h.f1].setNegativity(false);
            h.f1neg = false;
        }
    }
}

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
*/
/*
 *  reassign the local vertices, edges, and faces for each simplex in the pair
 *  of triangles
 */
 void topoFlip(Edge e, struct simps bucket) {
  //fix f1's adjactent faces, from f1's perpesctive, and the outer faces's perspective
  int i;
  vector<int> *fs = Triangulation::edgeTable[bucket.e4].getLocalFaces();
  for (i = 0; i < (*fs).size(); i++) {
    Triangulation::faceTable[bucket.f1].removeFace((*fs).at(i));
    Triangulation::faceTable[(*fs).at(i)].removeFace(bucket.f1);
  }
  fs = Triangulation::edgeTable[bucket.e2].getLocalFaces();
    for (i = 0; i < (*fs).size(); i++) {
    Triangulation::faceTable[bucket.f1].addFace((*fs).at(i));
    Triangulation::faceTable[(*fs).at(i)].addFace(bucket.f1);
  }
  Triangulation::faceTable[bucket.f1].addFace(bucket.f0);

  //fix f0's adjacent faces, from f0's perspective, and the outer faces perspective
  fs = Triangulation::edgeTable[bucket.e2].getLocalFaces();
  for (i = 0; i < (*fs).size(); i++) {
    Triangulation::faceTable[bucket.f0].removeFace((*fs).at(i));
    Triangulation::faceTable[(*fs).at(i)].removeFace(bucket.f0);
  }
  fs = Triangulation::edgeTable[bucket.e4].getLocalFaces();
  for (i = 0; i < (*fs).size(); i++) {
    Triangulation::faceTable[bucket.f0].addFace((*fs).at(i));
    Triangulation::faceTable[(*fs).at(i)].addFace(bucket.f0);
  }
  Triangulation::faceTable[bucket.f0].addFace(bucket.f1);

  //change f0 and f1 other adjacencies
  Triangulation::faceTable[bucket.f0].removeVertex(bucket.v2);
  Triangulation::faceTable[bucket.f1].removeVertex(bucket.v0);

  Triangulation::faceTable[bucket.f0].removeEdge(bucket.e2);
  Triangulation::faceTable[bucket.f1].removeEdge(bucket.e4);

  Triangulation::faceTable[bucket.f0].addVertex(bucket.v3);
  Triangulation::faceTable[bucket.f1].addVertex(bucket.v1);

  Triangulation::faceTable[bucket.f0].addEdge(bucket.e4);
  Triangulation::faceTable[bucket.f1].addEdge(bucket.e2);

  //now e0 needs its vertices fixed
  Triangulation::edgeTable[bucket.e0].removeVertex(bucket.v0);
  Triangulation::edgeTable[bucket.e0].removeVertex(bucket.v2);
  Triangulation::edgeTable[bucket.e0].addVertex(bucket.v1);
  Triangulation::edgeTable[bucket.e0].addVertex(bucket.v3);
  //note that the edges and faces are the same for e0

  //remove e0 from v2 and v0
  Triangulation::vertexTable[bucket.v2].removeEdge(bucket.e0);
  Triangulation::vertexTable[bucket.v0].removeEdge(bucket.e0);

  //remove v2 and v0 from each other
  Triangulation::vertexTable[bucket.v2].removeVertex(bucket.v0);
  Triangulation::vertexTable[bucket.v0].removeVertex(bucket.v2);

  //add e0 to v1 and v3
  Triangulation::vertexTable[bucket.v1].addEdge(bucket.e0);
  Triangulation::vertexTable[bucket.v3].addEdge(bucket.e0);

  //add v1 and v3 to each other
  Triangulation::vertexTable[bucket.v1].addVertex(bucket.v3);
  Triangulation::vertexTable[bucket.v3].addVertex(bucket.v1);

  //add f1 to e2 and add f0 to e4
  Triangulation::edgeTable[bucket.e2].addFace(bucket.f1);
  Triangulation::edgeTable[bucket.e4].addFace(bucket.f0);

  //add f1 to v1 and add f0 to v3
  Triangulation::vertexTable[bucket.v1].addFace(bucket.f1);
  Triangulation::vertexTable[bucket.v3].addFace(bucket.f0);

  //accidental dup, too scared to remove for now
  //add f1 from v1 and add f0 from v3
  //Triangulation::vertexTable[bucket.v1].addFace(bucket.f1);
  //Triangulation::vertexTable[bucket.v3].addFace(bucket.f0);

  //remove f1 from v0, and remove f0 from v2
  Triangulation::vertexTable[bucket.v0].removeFace(bucket.f1);
  Triangulation::vertexTable[bucket.v2].removeFace(bucket.f0);

  //remove f1 from e4 and remove f0 from e2
  Triangulation::edgeTable[bucket.e4].removeFace(bucket.f1);
  Triangulation::edgeTable[bucket.e2].removeFace(bucket.f0);

  //v2 could be adjacent to a bunch of edges that are no longer
  //adjacent to e0 so we need to mutually remove this adjacency
  //for v2's adjacent edges, and v0's
  vector<int> *es = Triangulation::vertexTable[bucket.v2].getLocalEdges();
  for (i = 0; i < (*es).size(); i++) {
    Triangulation::edgeTable[bucket.e0].removeEdge((*es).at(i));
    Triangulation::edgeTable[(*es).at(i)].removeEdge(bucket.e0);
  }
  es = Triangulation::vertexTable[bucket.v0].getLocalEdges();
  for (i = 0; i < (*es).size(); i++) {
    Triangulation::edgeTable[bucket.e0].removeEdge((*es).at(i));
    Triangulation::edgeTable[(*es).at(i)].removeEdge(bucket.e0);
  }

  //now the reverse of this needs to be done for v1 and v3's adjecent edges
  es = Triangulation::vertexTable[bucket.v1].getLocalEdges();
  for (i = 0; i < (*es).size(); i++) {
    Triangulation::edgeTable[bucket.e0].addEdge((*es).at(i));
    Triangulation::edgeTable[(*es).at(i)].addEdge(bucket.e0);
  }
  es = Triangulation::vertexTable[bucket.v3].getLocalEdges();
  for (i = 0; i < (*es).size(); i++) {
    Triangulation::edgeTable[bucket.e0].addEdge((*es).at(i));
    Triangulation::edgeTable[(*es).at(i)].addEdge(bucket.e0);
  }

  //e0 is accidentally added to itself by these loops
  Triangulation::edgeTable[bucket.e0].removeEdge(bucket.e0);
}
/*       v2
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

bool prepForFlip(Edge e, struct simps * bucket) {

    //check to see if the edge is a border, we can't flip borders, so return false
    if ((*(Triangulation::edgeTable[e.getIndex()].getLocalFaces())).size() <= 1) {
        return false;
    }

  bucket->e0 = e.getIndex();

  bucket->f0 = (*(e.getLocalFaces()))[0];
  bucket->f1 = (*(e.getLocalFaces()))[1];

  bucket->v0 = (*(e.getLocalVertices()))[0];
  bucket->v2 = (*(e.getLocalVertices()))[1];

  //buckets for various set operations
  vector<int> diff;
  vector<int> same;

  //figure out what v3 is
  //diff = listDifference(Triangulation::faceTable[bucket->f1].getLocalVertices(), e.getLocalVertices());
  diff = multiplicityDifference(Triangulation::faceTable[bucket->f1].getLocalVertices(), e.getLocalVertices());
  if (diff.size() == 0) { cout << "v3 couldn't be found"; return false;}
  bucket->v3 = diff[0];

  //figure out what v1 is
  //diff = listDifference(Triangulation::faceTable[bucket->f0].getLocalVertices(), e.getLocalVertices());
  diff = multiplicityDifference(Triangulation::faceTable[bucket->f0].getLocalVertices(), e.getLocalVertices());
  if (diff.size() == 0) { cout << "v1 couldn't be found"; return false;}
  bucket->v1 = diff[0];

  //figure out what e1 is
  //same = listIntersection(Triangulation::vertexTable[bucket->v0].getLocalEdges(), Triangulation::vertexTable[bucket->v1].getLocalEdges());
  same = multiplicityIntersection(Triangulation::vertexTable[bucket->v0].getLocalEdges(), Triangulation::vertexTable[bucket->v1].getLocalEdges());
  if (same.size() == 0) { cout << "v0 and v1 had no edge in common, e1 couldn't be found"; return false;}
  bucket->e1 = same[0];

  //figure out what e2 is
  //same = listIntersection(Triangulation::vertexTable[bucket->v1].getLocalEdges(), Triangulation::vertexTable[bucket->v2].getLocalEdges());
  same = multiplicityIntersection(Triangulation::vertexTable[bucket->v1].getLocalEdges(), Triangulation::vertexTable[bucket->v2].getLocalEdges());
  if (same.size() == 0) { cout << "v1 and v2 had no edge in common, e2 couldn't be found"; return false;}
  bucket->e2 = same[0];

  //figure out what e3 is
  //same = listIntersection(Triangulation::vertexTable[bucket->v2].getLocalEdges(), Triangulation::vertexTable[bucket->v3].getLocalEdges());
  same = multiplicityIntersection(Triangulation::vertexTable[bucket->v2].getLocalEdges(), Triangulation::vertexTable[bucket->v3].getLocalEdges());
  if (same.size() == 0) { cout << "v2 and v3 had no edge in common, e3 couldn't be found"; return false;}
  bucket->e3 = same[0];

  //figure out what e4 is
  //same = listIntersection(Triangulation::vertexTable[bucket->v3].getLocalEdges(), Triangulation::vertexTable[bucket->v0].getLocalEdges());
  same = multiplicityIntersection(Triangulation::vertexTable[bucket->v3].getLocalEdges(), Triangulation::vertexTable[bucket->v0].getLocalEdges());
  if (same.size() == 0) { cout << "v3 and v0 had no edge in common, e4 couldn't be found"; return false;}
  bucket->e4 = same[0];
  
  double e0_len = Length::valueAt(Triangulation::edgeTable[bucket->e0]);
  double e2_len = Length::valueAt(Triangulation::edgeTable[bucket->e2]);
  double e3_len = Length::valueAt(Triangulation::edgeTable[bucket->e3]);
  double e1_len = Length::valueAt(Triangulation::edgeTable[bucket->e1]);
  double e4_len = Length::valueAt(Triangulation::edgeTable[bucket->e4]);
  
  bucket->e0_len = e0_len;
  bucket->e1_len = e1_len;
  bucket->e2_len = e2_len;
  bucket->e3_len = e3_len;
  bucket->e4_len = e4_len;
  
  //determine the angles associated with v0 and v2
  //note that one of these is incorrect and needs to be changed
  //because its at the vertex where the concavity occurs
  //for simplicity i will calculate this later when it is important or not
  double a0, a2;

  //old method of computing angles prior to geoquants, left in for posterity
  //a0 = angle(e0_len, e1_len, e2_len) + angle(e0_len, e4_len, e3_len);
  //a2 = angle(e0_len, e2_len, e1_len) + angle(e0_len, e3_len, e4_len);

  a0 = EuclideanAngle::valueAt(Triangulation::vertexTable[bucket->v0], Triangulation::faceTable[bucket->f0])
        + EuclideanAngle::valueAt(Triangulation::vertexTable[bucket->v0], Triangulation::faceTable[bucket->f1]);

  a2 = EuclideanAngle::valueAt(Triangulation::vertexTable[bucket->v2], Triangulation::faceTable[bucket->f0])
        + EuclideanAngle::valueAt(Triangulation::vertexTable[bucket->v2], Triangulation::faceTable[bucket->f1]);

  bucket->a0 = a0;
  bucket->a2 = a2;

  bucket->f0neg = (Triangulation::faceTable[bucket->f0]).isNegative();
  bucket->f1neg = (Triangulation::faceTable[bucket->f1]).isNegative();

  return true;
}

bool notDegenerate(struct simps &h) {

}

//beyong here degenerate triangles are handled
void degenerateFlippedIsZero(simps b) {
  //calling flipPP should work for now, only problem is that it will result in
  //arbitrary assignment of positive and negative triangles after the flips
  flipPP(b);

}// end of degenerateFlippedIsZero

void degenerateNonFlippedIsZero(simps b) {
  int zeroSideIndex, adjacentIndex;
  double angleAdjacentToZeroEdge;

  //locate a zero length, and the edge that is adjacent to the zero edge and
  //the edge that is going to be flipped
  if (0 == b.e1_len) {
    zeroSideIndex = b.e1;
    adjacentIndex = b.e4;
    angleAdjacentToZeroEdge = b.a0;
  } else if (0 == b.e2_len) {
    zeroSideIndex = b.e2;
    adjacentIndex = b.e3;
    angleAdjacentToZeroEdge = b.a2;
  } else if (0 == b.e3_len) {
    zeroSideIndex = b.e3;
    adjacentIndex = b.e2;
    angleAdjacentToZeroEdge = b.a2;
  } else if (0 == b.e4_len) {
    zeroSideIndex = b.e4;
    adjacentIndex = b.e1;
    angleAdjacentToZeroEdge = b.a0;
  }




}//end of degernateNonFlippedIsZero

void degenerateOnlyAngleIsZero(simps b) {


}// end of degenerateOnlyAngleIsZero
