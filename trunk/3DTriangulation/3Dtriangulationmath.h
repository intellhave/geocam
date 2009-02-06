#include "triangulation/triangulation.h"

double solidAngle(double, double, double);
double solidAngle(Vertex, Tetra);
double dihedralAngle(Vertex v, Edge e, Tetra t);
double volumeSq(Tetra);
double volumeSq(double, double, double, double);
double CayleyvolumeSq(Tetra);
bool isDegenerate(Tetra);
void curvature3D();

/********** Yamabe Flows **********/
void yamabeFlow(vector<double>* radii, vector<double>* curvatures, double dt,
                double *initRadii,int numSteps, bool adjF);
void yamabeFlow(vector<double>* radii, vector<double>* curvatures, double dt,
                int numSteps, bool adjF);
void yamabeFlow(double dt, double *initRadii,int numSteps, bool adjF);

void yamabeFlow(double dt, int numSteps, bool adjF);

void yamabeFlow(vector<double>* radii, vector<double>* curvatures, double dt, 
                double *initRadii, double accuracy, double precision, bool adjF);
void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt, 
                double accuracy, double precision, bool adjF);
void yamabeFlow(double dt, double *initRadii, double accuracy, 
                double precision, bool adjF);
void yamabeFlow(double dt, double accuracy, double precision, bool adjF);
/**********************************/
                 
double calcNormalization();
