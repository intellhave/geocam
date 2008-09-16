/**************************************************************
File: Miscellaneous Math
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
***************************************************************
The Miscellaneous Math file holds the functions that perform
calculations on Points, Lines, and Circles. All functions are
done independently of any triangulation.
**************************************************************/

#include "triangulation.h"
#include "pointlinecircle.h"


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
 * Returns a set of soultions to the quadratic equation given as
 * quadratic(a, b, c) where:
 *         ax^2 + bx + c = 0
 * The solutions are placed in a vector of doubles. No solutions
 * implies non-real solutions. There are at most two solutions.
 */
vector<double> quadratic(double, double, double);

/*
 * Calculates the distance between given Points.
 */
double distancePoint(Point, Point);

/*
 * Returns a set of Points representing the intersection points of
 * two circles given by their centers and radii. No solutions implies 
 * two circles with improper centers and radii. There are at most two
 * solutions
 */
vector<Point> circleIntersection(Point, double, Point, double);

/*
 * Returns a set of Points representing the intersection points of
 * two given circles. No solutions implies two circles with improper 
 * centers and radii. There are at most two solutions.
 */
vector<Point> circleIntersection(Circle, Circle);

/*
 * Determines the resulting vector after rotating a given vector by a
 * given angle. Vectors are represented as Points, with the x-coordinate
 * as the i-component and the y-coordinate as the j-component. Rotates
 * counter-clockwise.
 */
Point rotateVector(Point, double);

/*
 * Calculates and returns a Point representing a line scaled to a given 
 * length and rotated by a given angle. Useful for finding the third point
 * of a triangle.
 */
Point findPoint(Line, double, double);
