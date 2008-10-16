#include "triangulation/triangulation.h"

double dihedralAngle(double, double, double);
double dihedralAngle(Vertex, Tetra);
double volumeSq(Tetra);
double volumeSq(double, double, double, double);
bool isDegenerate(Tetra);
double curvature3D(Vertex);
void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt ,
                double *initRadii,int numSteps, bool adjF);
double stdDiffEQ3D(int);
double adjDiffEQ3D(int vertex, double totalCurv, double totalRadii);
