/**************************************************************
Class: Vertex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#ifndef VERTEX_H
#define VERTEX_H

#include "simplex.h" // inheriting class's header file

/*
 * The Vertex class is derived from the Simplex class. It is the
 * zero-dimensional simplex. Every vertex also has a radius, which is
 * the basis for providing all measurements (length, curvature, etc).
 * Radii are initialized to zero and set by the user after building
 * the triangulation. For when orientation is a concern, a vertex has
 * x- and y-coordinates.
 */
class Vertex : public Simplex {
 public:
 Vertex();
 Vertex(int setIndex) : Simplex(setIndex) {};
 ~Vertex();
 int getDegree();
};

#endif // VERTEX_H



