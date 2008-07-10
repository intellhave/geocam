#include <triangulationmath.h>

double sphericalAngle(double, double, double, double = 1);
double sphericalAngle(Edge, Edge, Edge, double = 1);
double sphericalAngle(Vertex, Face, double = 1);
double sphericalCurvature(Vertex);
void sphericalCalcFlow(vector<double>*, vector<double>*, double, double*, int, bool);
double spherStdDiffEQ(int);
double spherAdjDiffEQ(int, double);
double delArea();
double sphericalArea(Face);
