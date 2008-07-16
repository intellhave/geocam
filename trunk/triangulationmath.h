/**************************************************************
File: Triangulation Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
***************************************************************
The Triangulation Math file holds the functions that perform
calculations on the triangulation.
**************************************************************/
 
#include "simplex/edge.h"
#include "simplex/vertex.h"
#include "simplex/face.h"
#include "triangulation.h"


/*
 * Calculates the angle between two edges of a triangle, where the
 * first two doubles represent the lengths of those edges and the
 * third represents the length of the edge opposite the angle.
 */
double angle(double, double, double);

/*
 * Calculates the angle between two edges of a triangle, given the
 * vertex where the two edges meet and the face of the triangle.
 */
double angle(Vertex, Face);

/*
 * Calculates the curvature at a vertex i. The curvature is calculated by
 * subtracting the sum of all the angles at i from 2*PI.
 */
double curvature(Vertex);

/*
 * Returns a list of simplex indices that are common between the two lists
 * given.
 */
vector<int> listIntersection(vector<int>*, vector<int>*);

/*
 * Returns a list of simplex indices that are in the first list and not in
 * the second.
 */
vector<int> listDifference(vector<int>*, vector<int>*);

/*
 * Calculates the sum of the angles at the given vertex.
 */ 
double getAngleSum(Vertex);

/*
 * Calculates the Ricci flow of the current Triangulation using the 
 * Runge-Kutta method. Results from the steps are written into vectors of
 * doubles provided. The parameters are:
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
 *            ***Credit for the algorithm goes to J-P Moreau.***
 */
void calcFlow(vector<double>*, vector<double>*, double, double*, int, bool);

/*
 * Calculates the dr/dt of the vertex given by its index according to the
 * formula:
 *         dr_i/dt = -K_i * r_i
 */
double stdDiffEQ(int);

/*
 * Calculates the dr/dt of the vertex given by its index according to the
 * adjusted formula:
 *         dr_i/dt = -K_i * r_i + total K / (# vertices) * r_i.
 * The double represents the net curvature (for faster processing time).
 */
double adjDiffEQ(int, double);

/*
 * Calculates the radius of the circle embedded in the face given. Uses the
 * formula:
 *               ________________________  
 *           _  |  r_1 * r_2 * ... * r_n
 *     R =    \ |  -----------------------
 *             \|  r_1 + r_2 + ... + r_n
 */
double inRadius(Face);

/*
 * Calculates the dual length between the two faces local to the given edge.
 * Adds the two in-Radii of the adjacent faces together.
 */
double dualLength(Edge);

/*
 * Calculates the total area of the polygon whose sides are all the dual lengths
 * of the edges local to the given vertex. 
 */
double dualArea(Vertex);

