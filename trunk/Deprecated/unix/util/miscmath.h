/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Miscellaneous Math file holds the functions that perform
calculations on Points, Lines, and Circles. All functions are
done independently of any triangulation.
**************************************************************/
#ifndef MISCMATH_H_
#define MISCMATH_H_

#include "triangulation/triangulation.h"

#include <vector>
using namespace std;

/* Returns a list of simplex indices that are common between the two lists
 * given. */
vector<int> listIntersection(vector<int>* list1, vector<int>* list2);

/* Returns a list of simplex indices that are in the first list and not in
 * the second.*/
vector<int> listDifference(vector<int>* list1, vector<int>* list2);

struct stand_psn_edge{
  int v1;
  int v2;
};
typedef struct stand_psn_edge StdEdge;

struct stand_psn_face{
  int v1;
  int v2;
  int v3;
  
  int e12;
  int e13;
  int e23;
};
typedef struct stand_psn_face StdFace;

struct stand_psn_tetra{
  int v1;
  int v2;
  int v3;
  int v4;
  
  int e12;
  int e13;
  int e14;
  int e23;
  int e24;
  int e34;

  int f123;
  int f124;
  int f134;
  int f234;
};
typedef struct stand_psn_tetra StdTetra;

StdEdge labelEdge(Edge& e, Vertex& v);

StdFace labelFace(Face& f, Vertex& v);
StdFace labelFace(Face& f, Edge& e);

StdTetra labelTetra(Tetra& t, Edge& e);
StdTetra labelTetra(Tetra& t, Vertex& v);
StdTetra labelTetra(Tetra& t, Face& f);
StdTetra labelTetra(Tetra& t);

#endif /* MISCMATH_H_ */
