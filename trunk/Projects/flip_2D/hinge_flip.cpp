#include "hinge_flip.h"

Edge flip(Edge e) {

    struct simps bucket;
    if(!prep_for_flip(e, &bucket)) {
        return e;
    }

    topo_flip(e, bucket);
    Length::At(e)->remove();
    if (!Triangulation::faceTable[bucket.f0].isNegative() && !Triangulation::faceTable[bucket.f1].isNegative()) {
        flipPP(bucket);
    } else if (Triangulation::faceTable[bucket.f0].isNegative() && Triangulation::faceTable[bucket.f1].isNegative()) {
        flipNN(bucket);
        //note the following || is technically exclusive because the two && cases are checked above
    } else if((Triangulation::faceTable[bucket.f0].isNegative() == true) || (Triangulation::faceTable[bucket.f1].isNegative() == true)) {

      flipPN(bucket);

    } else {
        printf("hmm, no case was found\n");
        //just need to escape return e seems sufficient;
        return e;
    }

    return Triangulation::edgeTable[e.getIndex()];
} //end of flip

//compute the height perpendicular to x
double perpHeight(double x, double y, double z) {
  //cout << "x : " << x << "\n";
  //cout << "y : " << y << "\n";
  //cout << "z : " << z << "\n";
  double insideSqrt = -((x-y-z) * (y+x-z) * (x+z-y) * (y+x+z))/(4 * x * x);
  //cout << "insideSqrt : " << insideSqrt << "\n";
  if (insideSqrt < 0) {
    printf("the new length going to be imaginary, this is probably wrong,");
    printf("something is wrong in perpendicularHeight in hinge_flip.cpp");
    system("PAUSE");
  }
  return sqrt(insideSqrt);
}

/*
 *      handles both: PP -> PP
 *                    PP -> PN
 */
void flipPP(struct simps b) {
  //determine the flipped edges new length after flipping
  double e_length;
  
  e_length = sqrt( (b.e2_len*b.e2_len) + (b.e3_len * b.e3_len) - (2*b.e2_len*b.e3_len*cos(b.a2)) );
  //e_length = perpHeight(b.e0_len, b.e2_len, b.e1_len) + perpHeight(b.e0_len, b.e3_len, b.e4_len);

  Length::At(Triangulation::edgeTable[b.e0])->setValue(e_length);

  //figure out which one is negative
  //in the PP_PN case we need to set one to be negative
  if (b.a0 > PI || b.a2 > PI) {
    if (angle(b.e4_len, b.e1_len, e_length) > angle(b.e3_len, b.e2_len, e_length)) {
      Triangulation::faceTable[b.f0].setNegativity(true);
    } else {
      Triangulation::faceTable[b.f1].setNegativity(true);
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
void flipPN(struct simps b) {
    double e_length;
    double alpha, beta;
    alpha = angle(b.e0_len, b.e1_len, b.e2_len);
    beta  = angle(b.e0_len, b.e4_len, b.e3_len);
    e_length = sqrt(b.e4_len * b.e4_len + b.e1_len * b.e1_len - 2 * b.e4_len * b.e1_len * cos(abs(beta - alpha)));
    //e_length = perpHeight(b.e0_len, b.e2_len, b.e1_len) + perpHeight(b.e0_len, b.e3_len, b.e4_len);

    Length::At(Triangulation::edgeTable[b.e0])->setValue(e_length);
    //assignment removed 'cause the rest of procedure is written in terms of the
    //before-flip picture
    //b.e0_len = e_length;

    //flip flop everything so that the negative triangle is f0
    if (b.f1neg) {
        swap(&b.v1, &b.v3);
        swap(&b.e1, &b.e4);
        swap(&b.e2, &b.e3);
        swap(&b.f0, &b.f1);
        //next two lines are the important ones but the ones above were swapped
        //as well for consistency
        swap(&b.e1_len, &b.e4_len);
        swap(&b.e2_len, &b.e3_len);
    }

    //now determine which is negative and positive
    //a negative leaning across e1
    if (abs(angle(b.e0_len, b.e1_len, b.e2_len)) < abs(angle(b.e0_len, b.e4_len, b.e3_len)) &&
        abs(angle(b.e0_len, b.e2_len, b.e1_len)) > abs(angle(b.e0_len, b.e3_len, b.e4_len))) { // right case as shown above
        Triangulation::faceTable[b.f1].setNegativity(true);
        Triangulation::faceTable[b.f0].setNegativity(false);
    
    //a negative leaning across e2
    } else if (abs(angle(b.e0_len, b.e1_len, b.e2_len)) > abs(angle(b.e0_len, b.e4_len, b.e3_len)) &&
               abs(angle(b.e0_len, b.e2_len, b.e1_len)) < abs(angle(b.e0_len, b.e3_len, b.e4_len))) { //left case... flip across vert of above picture
        //actually setting these shouldn't be necessary but its nice to be explicit
        Triangulation::faceTable[b.f1].setNegativity(false);
        Triangulation::faceTable[b.f0].setNegativity(true);

    //a negative inside a positie
    } else if (abs(angle(b.e0_len, b.e1_len, b.e2_len)) > abs(angle(b.e0_len, b.e4_len, b.e3_len)) &&
               abs(angle(b.e0_len, b.e2_len, b.e1_len)) > abs(angle(b.e0_len, b.e3_len, b.e4_len))) {
        Triangulation::faceTable[b.f1].setNegativity(true);
        Triangulation::faceTable[b.f0].setNegativity(true);

    //a positive inside a negative
    } else if (abs(angle(b.e0_len, b.e1_len, b.e2_len)) < abs(angle(b.e0_len, b.e4_len, b.e3_len)) &&
               abs(angle(b.e0_len, b.e2_len, b.e1_len)) < abs(angle(b.e0_len, b.e3_len, b.e4_len))) {
        Triangulation::faceTable[b.f1].setNegativity(false);
        Triangulation::faceTable[b.f0].setNegativity(false);

    }
}

/*
 *    handles NN -> NN
 *            NN -> PN
 */
void flipNN(struct simps b) {
    double e_length;
    e_length = sqrt(b.e2_len*b.e2_len + b.e3_len*b.e3_len - 2 * b.e2_len * b.e3_len * cos(b.a2));
    //e_length = perpHeight(b.e0_len, b.e2_len, b.e1_len) + perpHeight(b.e0_len, b.e3_len, b.e4_len);

    Length::At(Triangulation::edgeTable[b.e0])->setValue(e_length);

    if (b.a0 > PI || b.a2 > PI) {
        if (angle(e_length, b.e4_len, b.e1_len) < angle(e_length, b.e3_len, b.e2_len)) {
            Triangulation::faceTable[b.f0].setNegativity(false);
        } else {
            Triangulation::faceTable[b.f1].setNegativity(false);
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
 void topo_flip(Edge e, struct simps bucket) {
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

  //dup
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

bool prep_for_flip(Edge e, struct simps * bucket) {

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
  diff = listDifference(Triangulation::faceTable[bucket->f1].getLocalVertices(), e.getLocalVertices());
  if (diff.size() == 0) { cout << "v3 couldn't be found"; return false;}
  bucket->v3 = diff[0];

  //figure out what v1 is
  diff = listDifference(Triangulation::faceTable[bucket->f0].getLocalVertices(), e.getLocalVertices());
  if (diff.size() == 0) { cout << "v1 couldn't be found"; return false;}
  bucket->v1 = diff[0];

  //figure out what e1 is
  same = listIntersection(Triangulation::vertexTable[bucket->v0].getLocalEdges(), Triangulation::vertexTable[bucket->v1].getLocalEdges());
  if (same.size() == 0) { cout << "v0 and v1 had no edge in common, e1 couldn't be found"; return false;}
  bucket->e1 = same[0];

  //figure out what e2 is
  same = listIntersection(Triangulation::vertexTable[bucket->v1].getLocalEdges(), Triangulation::vertexTable[bucket->v2].getLocalEdges());
  if (same.size() == 0) { cout << "v1 and v2 had no edge in common, e2 couldn't be found"; return false;}
  bucket->e2 = same[0];

  //figure out what e3 is
  same = listIntersection(Triangulation::vertexTable[bucket->v2].getLocalEdges(), Triangulation::vertexTable[bucket->v3].getLocalEdges());
  if (same.size() == 0) { cout << "v2 and v3 had no edge in common, e3 couldn't be found"; return false;}
  bucket->e3 = same[0];

  //figure out what e4 is
  same = listIntersection(Triangulation::vertexTable[bucket->v3].getLocalEdges(), Triangulation::vertexTable[bucket->v0].getLocalEdges());
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
  double a0 = angle(e0_len, e1_len, e2_len) + angle(e0_len, e4_len, e3_len);
  double a2 = angle(e0_len, e2_len, e1_len) + angle(e0_len, e3_len, e4_len);

  bucket->a0 = a0;
  bucket->a2 = a2;

  bucket->f0neg = (Triangulation::faceTable[bucket->f0]).isNegative();
  bucket->f1neg = (Triangulation::faceTable[bucket->f1]).isNegative();

  return true;
}
