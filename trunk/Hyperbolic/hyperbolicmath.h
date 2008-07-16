/**************************************************************
File: Hyperbolic Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Hyperbolic Math file holds the functions that perform
calculations on the triangulation under a hyperbolic geometry.
**************************************************************/
#include <triangulationmath.h>

/*
 * Calculates an angle of a hyperbolic triangle given the edge lengths
 * where the first two doubles are the lengths of the edges incident on 
 * the angle.
 */
double hyperbolicAngle(double, double, double);
/*
 * Calculates an angle of a hyperbolic triangle given the edges, where 
 * the first two edges are incident on the angle.
 */
double hyperbolicAngle(Edge, Edge, Edge);
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

/*
 * Calculates the Ricci flow of the current Triangulation under a 
 * spherical geometry. Results from the steps are written into vectors 
 * of doubles provided. The parameters are:
 *          
 *      vector<double>* weights-
 *                           A vector of doubles to append the results of
 *                           weights, grouped by step, with a total size of
 *                           numSteps * numVertices.
 *      vector<double>* curvatures-
 *                           A vector of doubles to append the results of
 *                           curvatures, grouped by step, with a total size
 *                           of numSteps * numVertices.
 *      double dt -          The time step size. Initial and ending
 *                           times not needed since diff. equations are
 *                           independent of time.
 *      double* initWeights- Array of initial weights of the Vertices 
 *                           in order.
 *      int numSteps -       The number of steps to take. 
 *                           (dt = (tf - ti)/numSteps)
 *      bool adjF -          Boolean of whether or not to use adjusted
 *                           differential equation. True to use adjusted.
 * 
 * The information placed in the vectors are the weights and curvatures for
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
void hyperbolicCalcFlow(vector<double>*, vector<double>*, double, double*, int, bool);
