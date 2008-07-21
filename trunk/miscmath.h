#include <cmath>
#include "pointline.h"
#include "triangulationmath.h"
#include <vector>


vector<double> quadratic(double, double, double);

double distancePoint(Point, Point);

vector<Point> circleIntersection(Point, double, Point, double);

vector<Point> circleIntersection(Circle, Circle);

vector<double> quadratic(double, double, double);
