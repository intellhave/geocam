/**************************************************************
Class: Vertex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#ifndef VERTEX_H_
#define VERTEX_H_

#include "vertex.h"

Vertex::Vertex() : Simplex() {}
Vertex::~Vertex(){}
int Vertex::getDegree() {
  return getLocalEdges()->size();
}

#endif /* VERTEX_H_ */

