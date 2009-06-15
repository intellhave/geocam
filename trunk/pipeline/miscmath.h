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

#include "triangulation.h"

#include <vector>
using namespace std;

/* Returns a list of simplex indices that are common between the two lists
 * given. */
vector<int> listIntersection(vector<int>* list1, vector<int>* list2);

/* Returns a list of simplex indices that are in the first list and not in
 * the second.*/
vector<int> listDifference(vector<int>* list1, vector<int>* list2);

struct stand_psn_edge{
  Vertex v1;
  Vertex v2;
};
typedef struct stand_psn_edge StdEdge;

struct stand_psn_face{
  Vertex v1;
  Vertex v2;
  Vertex v3;
  
  Edge e12;
  Edge e13;
  Edge e23;
};
typedef struct stand_psn_face StdFace;

struct stand_psn_tetra{
  Vertex v1;
  Vertex v2;
  Vertex v3;
  Vertex v4;
  
  Edge e12;
  Edge e13;
  Edge e14;
  Edge e23;
  Edge e24;
  Edge e34;
};
typedef struct stand_psn_tetra StdTetra;

StdEdge labelEdge(Vertex& v, Edge& e);

StdFace labelFace(Vertex& v, Face& f);
StdFace labelFace(Edge& e, Face& f);

StdTetra labelTetra(Edge& e, Tetra& t);
StdTetra labelTetra(Vertex& v, Tetra& t);
StdTetra labelTetra( Tetra& t );
#endif /* MISCMATH_H_ */
