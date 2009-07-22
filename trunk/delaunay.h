/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Delaunay file holds all functions that check triangulations
locally for the condition of being Delaunay or weighted Delaunay.
**************************************************************/

#include "triangulation/triangulation.h"

/*
 * Indicates whether or not a particular edge has the quality
 * of being Delaunay.
 */
bool isDelaunay(Edge);

/*
 * Indicates whether or not a particular edge has the quality 
 * of being weighted Delaunay.
 */
bool isWeightedDelaunay(Edge);

/*
 * Returns the partial dual of a particular edge, as indicated
 * by the given face.
 */
double getHeight(Face, Edge);

/*
 * Returns the dual of a particular edge.
 */
double getDual(Edge);

/*
 * Returns the portion of an edge from a given vertex to the
 * edge's center.
 */
double getPartialEdge(Edge, Vertex);

//computes the dirichlet energy of the triangulation
double dirichletEnergy();
