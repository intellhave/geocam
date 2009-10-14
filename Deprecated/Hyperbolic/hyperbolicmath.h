/**************************************************************
File: Hyperbolic Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Hyperbolic Math file holds the functions that perform
calculations on the triangulation under a hyperbolic geometry.
**************************************************************/
#include "triangulation/triangulationmath.h"
#include "math/miscmath.h"

/*
 * Calculates an angle of a hyperbolic triangle given the edge lengths
 * where the first two doubles are the lengths of the edges incident on 
 * the angle.
 */
double hyperbolicAngle(double, double, double);
/*
 * Calculates an angle of a hyperbolic triangle given the vertex and 
 * face.
 */
double hyperbolicAngle(Vertex, Face);

/*
 * Calculates the curvature of a vertex using hyperbolic angles.
 *            K_i = 2*PI - sum of angles at i
 */
double hyperbolicCurvature(Vertex);

void hyperbolicCurvature();
