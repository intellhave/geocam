#include <triangulationmath.h>

double sphericalAngle(double, double, double);
double sphericalAngle(Edge, Edge, Edge);
double sphericalAngle(Vertex, Face);
double sphericalCurvature(Vertex);
void sphericalCalcFlow(vector<double>*, vector<double>*, double, double*, int, bool);
double spherStdDiffEQ(int);
double spherAdjDiffEQ(int, double);
