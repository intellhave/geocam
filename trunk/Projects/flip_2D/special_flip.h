#include "hinge_flip.h"
#include "triangulation.h"

#ifndef SPECIAL_FLIP_H
#define SPECIAL_FLIP_H
//a vertex with only three adjacent faces/edges will return true
bool isThreeHinge(Vertex v);

//given a non-convex hinge, there is a way of flipping the hinges
//in the non-convex area s.t. the vertex of non-convexity becomes
//a 3-hinge, this function will perform the necessary combinatorial
//changes to achieve this
void makeThreeFace(Edge e, Vertex v);

//removes a vertex and it's three faces from the local lists of
//their local faces, edges, and vertices
bool flip3to1(Vertex v);

#endif
