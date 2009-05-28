
/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "edge.h" // class's header file

Edge::Edge() : Simplex() {}
Edge::Edge(int setindex) : Simplex(setindex) {}
Edge::~Edge(){}

bool Edge::isBorder() {
  return getLocalFaces()->size() == 1;
}


