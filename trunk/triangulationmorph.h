/**************************************************************
File: Triangulation Morph
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 20, 2008
***************************************************************
The Triangulation Morph file holds the functions that manipulates
the Triangulation in some way.
**************************************************************/

void addNewVertex(Face, double);
void flip(Edge);
void removeVertex(Vertex);
void addLeaf(Edge, double);
void addHandle(Face, double);
void addCrossCap(Face, double);
