/**************************************************************
File: Triangulation Plane
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Triangulation Plane file holds the functions that perform
calculations on the triangulation.
**************************************************************/

#include "triangulationinputoutput.h"
#include "triangulationMorph.h"
#include "TriangulationCoordinateSystem.h"

/*
 * Initializes the disc by creating the first triangle,
 * taking in its edge lengths as arguments.
 */
void firstTriangle(double, double, double);
/*
 * Adds a triangle by appending it to a given edge, taking
 * in its two new edge lengths as arguments.
 */
void addTriangle(Edge, double, double);

/*
 * Adds a triangle by closing up a concavity in the disc,
 * joining two vertices.
 */
void addTriangle(Edge, Edge);

/*
 * Returns the sum of all angles at a given vertex.
 */
double getAngleSum(Vertex);

/*
 * Creates a planar triangulation with the number of faces given and
 * randomly generated lengths.
 */
void generateTriangulation(int);

/*
 * Generate radii randomly within a set threshold. 
 * Typically accompanies generateTriangulation, when a weighted
 * triangulation is desired.
 */
void generateRadii();

/*
 * Intended to continually traverse the edges of a triangulation and perform
 * a 2-2 flip on any edge that is not Delaunay.
 * Terminates when a traversal is made and every edge is Delaunay.
 */
void flipAlgorithm();

/*
 * Intended to continually traverse the edges of a triangulation and perform
 * a 2-2 flip on any edge that is not weighted Delaunay.
 * Terminates when a traversal is made and every edge is weighted Delaunay.
 */
void weightedFlipAlgorithm();

/*
 * Checks a newly created triangle to make sure it does not cross over an 
 * existing edge.
 * OBSOLETE*****
 */
void checkTriangle(Edge, double, double);

/*
 * Generates the "canonical example". Used to test theories regarding 
 * negative triangles.
 */
void makeSpecialCase();

/*
 * Used to "pop off" any double triangles remaining at the end of a flip algorithm
 */
void removeDoubleTriangle(Vertex);

/*
 * Used to write a different style of output file preferred by Yuliya
 * WRONG CLASS?
 */
void writeYuliyaFile(char*);

int checkForDoubleTriangles();

bool isDoubleTriangle(Vertex v);

int checkFaces();
