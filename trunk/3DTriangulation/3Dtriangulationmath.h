#include "triangulation/triangulation.h"

double solidAngle(double, double, double);
double solidAngle(Vertex, Tetra);
double dihedralAngle(Vertex v, Edge e, Tetra t);
double volumeSq(Tetra);
double volumeSq(double, double, double, double);
bool isDegenerate(Tetra);
void curvature3D();
void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt ,
                double *initRadii,int numSteps, bool adjF);
void yamabeFlow(double dt, double *initRadii,int numSteps, bool adjF);
void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt, double *initRadii, 
                       double accuracy, double precision, bool adjF);
void yamabeFlow(double dt, double *initRadii, 
                       double accuracy, double precision, bool adjF);                   
double calcNormalization();
