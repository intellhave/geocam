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
#include "visualization/pointlinecircle.h"
#include <vector>
using namespace std;

/*
 * Returns a list of simplex indices that are common between the two lists
 * given.
 */
vector<int> listIntersection(vector<int>*, vector<int>*);

/*
 * Returns a list of simplex indices that are in the first list and not in
 * the second.
 */
vector<int> listDifference(vector<int>*, vector<int>*);

/*
 * Returns a set of soultions to the quadratic equation given as
 * quadratic(a, b, c) where:
 *         ax^2 + bx + c = 0
 * The solutions are placed in a vector of doubles. No solutions
 * implies non-real solutions. There are at most two solutions.
 */
vector<double> quadratic(double, double, double);

/*
 * Calculates the distance between given Points.
 */
double distancePoint(Point, Point);

/*
 * Returns a set of Points representing the intersection points of
 * two circles given by their centers and radii. No solutions implies 
 * two circles with improper centers and radii. There are at most two
 * solutions
 */
vector<Point> circleIntersection(Point, double, Point, double);

/*
 * Returns a set of Points representing the intersection points of
 * two given circles. No solutions implies two circles with improper 
 * centers and radii. There are at most two solutions.
 */
vector<Point> circleIntersection(Circle, Circle);

/*
 * Determines the resulting vector after rotating a given vector by a
 * given angle. Vectors are represented as Points, with the x-coordinate
 * as the i-component and the y-coordinate as the j-component. Rotates
 * counter-clockwise.
 */
Point rotateVector(Point, double);

/*
 * Calculates and returns a Point representing a line scaled to a given 
 * length and rotated by a given angle. Useful for finding the third point
 * of a triangle.
 */
Point findPoint(Line, double, double);

double angle(double, double, double);

// This structure describes a labeled edge. 
// When properly initialized, v1 and v2 are indices
// to the vertices at the ends of the edge, these
// can be fed into the Triangulation::VertexTable
// data structure to retrieve the actual vertex objects.
struct stand_psn_edge{
  int v1;
  int v2;
};
typedef struct stand_psn_edge StdEdge;

// Similar to the StdEdge struct, this struct describes
// a labelled face. when initialized, v1...v3 describe
// the face's vertices, and e12...e23 describe its edges.
struct stand_psn_face{
  int v1;
  int v2;
  int v3;
  
  int e12;
  int e13;
  int e23;
};
typedef struct stand_psn_face StdFace;

// This structure describes a labeled tetrahedron. Once
// initialized, the v entries describe the tetrahedron's
// vertices, the e entries its edges, and the f entries
// its faces.
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

// On the project's wiki, we have an extensive
// discussion of the semantics of "labeling" a
// vertex.

// Label edge e with respect to vertex v.
StdEdge labelEdge(Edge& e, Vertex& v);

// Label face f with respect to vertex v.
StdFace labelFace(Face& f, Vertex& v);

// Label face f with respect to edge e.
StdFace labelFace(Face& f, Edge& e);

// Label tetrahedron t with respect to edge e.
StdTetra labelTetra(Tetra& t, Edge& e);

// Label tetrahedron t with respect to vertex v.
StdTetra labelTetra(Tetra& t, Vertex& v);

// Label tetrahedron t with respect to face f.
StdTetra labelTetra(Tetra& t, Face& f);

// Label tetrahedron t in a consistent way.
StdTetra labelTetra(Tetra& t);

#endif /* MISCMATH_H_ */
