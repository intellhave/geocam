/**************************************************************
File: Small Morphs
Author: Alex Henniges, Tom Williams
Version: October 3, 2008
***************************************************************
The Small Morphs file holds the functions that perform isolated 
morphs on simplices. These functions are then used in morphs on
a whole Triangulation.
**************************************************************/
#include "triangulation/triangulation.h"


/*
 * This function takes a vertex and adds it to another forming an edge and
 * returns that edge's index. References are made between the vertices and 
 * edge. Both vertices are expected to already be in the Triangulation. If
 * the vertices are already local to each other, then the index of the edge
 * they form is returned.
 *
 * The references made are: V-V, V-E, E-V
 */
int addVertexToVertex(int, int);

/*
 * This function takes a vertex and adds it to an edge forming a face and
 * returns that face's index. All knowable references are made between the
 * simplices. Both the vertex and edge are expected to already be in the 
 * Triangulation. If a face already contains this edge and vertex, then 
 * then the index of that face is returned.
 *
 * The references made are: V-V, V-E, E-V, E-E, V-F, E-F, F-V, F-E.
 */
int addVertexToEdge(int, int);

/*
 * This function takes a vertex and adds it to a face forming a tetra and
 * returns that tetra's index. All knowable references are made between the
 * simplices. Both the vertex and face are expected to already be in the 
 * Triangulation.
 *
 * The references made are: V-V, V-E, E-V, E-E, V-F, E-F, F-V, F-E, F-F, V-T,
 *                          E-T, F-T, T-V, T-E, T-F.
 */
int addVertexToFace(int, int);

int makeFace(int, int, int);
int addEdgeToEdge(int, int);
int makeTetra(int, int, int, int);

