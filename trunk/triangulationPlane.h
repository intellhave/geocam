/**************************************************************
File: Triangulation Plane
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Triangulation Plane file holds the functions that perform
calculations on the triangulation.
**************************************************************/

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

void setCoordinates();

void generateTriangulation(int);
