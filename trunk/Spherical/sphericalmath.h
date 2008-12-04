/**************************************************************
File: Spherical Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Spherical Math file holds the functions that perform
calculations on the triangulation under a spherical geometry.
**************************************************************/
#include "triangulation/triangulationmath.h"
#include <miscmath.h>

/*
 * Calculates an angle of a spherical triangle given the edge lengths
 * where the first two doubles are the lengths of the edges incident 
 * on the angle.
 */
double sphericalAngle(double, double, double, double = 1);
/*
 * Calculates an angle of a spherical triangle given the vertex and 
 * face.
 */
double sphericalAngle(Vertex, Face, double = 1);

/*
 * Calculates the curvature of a vertex using spherical angles.
 *            K_i = 2*PI - sum of angles at i
 */
double sphericalCurvature(Vertex, double = 1);

/*
 * Calculates the Ricci flow of the current Triangulation under a 
 * spherical geometry. Results from the steps are written into vectors 
 * of doubles provided. The parameters are:
 *          
 *      vector<double>* radii-
 *                           A vector of doubles to append the results of
 *                           radii, grouped by step, with a total size of
 *                           numSteps * numVertices.
 *      vector<double>* curvatures-
 *                           A vector of doubles to append the results of
 *                           curvatures, grouped by step, with a total size
 *                           of numSteps * numVertices.
 *      double dt -          The time step size. Initial and ending
 *                           times not needed since diff. equations are
 *                           independent of time.
 *      double* initRadii-   Array of initial radii of the Vertices 
 *                           in order.
 *      int numSteps -       The number of steps to take. 
 *                           (dt = (tf - ti)/numSteps)
 *      bool adjF -          Boolean of whether or not to use adjusted
 *                           differential equation. True to use adjusted.
 * 
 * The information placed in the vectors are the radii and curvatures for
 * each Vertex at each step point. The data is grouped by steps, so the first
 * vertex of the first step is the beginning element. After n doubles are
 * placed, for an n-vertex triangulation, the first vertex of the next step
 * follows. If the vectors passed in are not empty, the data is added to the
 * end of the vector and the original information is not cleared.
 *
 * Unlike the calcFlow in the Euclidean geometry, the net curvature is
 * calculated at every intermediate step as it changes throughout the flow.
 *
 *            ***Credit for the algorithm goes to J-P Moreau.***
 */
void sphericalCalcFlow(vector<double>*, vector<double>*, double, double*, int, bool);

/*
 * Caluclates the change in the total area with repsect to time of the
 * spherical triangulation.
 */
double delArea();
/*
 * Calculates the area of the spherical triangle of the given face.
 */
double sphericalArea(Face);
/*
 * Caluclates the delta value of the spherical triangle of the given face.
 * The delta is similar to the area of the triangle as if it were Euclidean.
 */
double delta(Face);
/*
 * Calculates the total area of the spherical triangulation.
 */
double sphericalTotalArea();
/*
 * Calculates the change in the curvature with respect to time of the given
 * vertex under a spherical geometry.
 */
double delCurv(Vertex);

double vertexSum(Vertex, double = 1);

double angleTotalSum(double = 1);

double angleDiffSums(Face, double = 1);

double angleDiff(double, double, double, double = 1);
