/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 11, 2008
***************************************************************
The Triangulation Math file holds the functions that perform
calculations on the triangulation.
**************************************************************/
 
#include "vertex.h"
#include "edge.h"
#include "face.h"

/*
 * Calculates the angle between two edges of a triangle, where the
 * first two doubles represent the lengths of those edges and the
 * third represents the length of the edge opposite the angle.
 */
double angle(double, double, double);

/*
 * Calculates the angle between two edges of a triangle, where the
 * last edge given is the edge opposite the angle.
 */
double angle(Edge, Edge, Edge);

/*
 * Calculates the angle between two edges of a triangle, given the
 * vertex where the two edges meet and the face of the triangle.
 */
double angle(Vertex, Face);

/*
 * Calculates the curvature at a vertex i. The curvature is calculated by
 * subtracting the sum of all the angles at i from 2*PI.
 */
double curvature(Vertex);

vector<int> listIntersection(vector<int>*, vector<int>*);

void calcFlow(double, double, double*, int, int, bool);

double stdDiffEQ(int, double);

double adjDiffEQ(int, double);
