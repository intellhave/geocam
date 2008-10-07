#include "3Dtriangulationmath.h"
#include "triangulation/triangulation.h"
#include "spherical/sphericalmath.h"
#include "miscmath.h"
#define PI 	3.141592653589793238
double dihedralAngle(double angle1, double angle2, double angle3)
{
       double beta1 = sphericalAngle(angle1, angle2, angle3);
       double beta2 = sphericalAngle(angle1, angle3, angle2);
       double beta3 = sphericalAngle(angle2, angle3, angle1);
       return beta1 + beta2 + beta3 - PI;      
}       
double dihedralAngle(Vertex v, Tetra t)
{
       // Find the three faces local to v and part of the t.
       vector<int> sameAs = listIntersection(v.getLocalFaces(), t.getLocalFaces());
 
       Face f1 = Triangulation::faceTable[sameAs[0]];
       Face f2 = Triangulation::faceTable[sameAs[1]];
       Face f3 = Triangulation::faceTable[sameAs[2]];
       
       // Find the angle of each face on vertex v.
       double angle1 = angle(v, f1);
       double angle2 = angle(v, f2);
       double angle3 = angle(v, f3);
       
       return dihedralAngle(angle1, angle2, angle3);
}
bool isDegenerate(Tetra t)
{
     vector<int> localV = *(t.getLocalVertices());
     double sum = 0;
     for(int i = 0; i < localV.size(); i++)
     {
        Vertex v = Triangulation::vertexTable[localV[i]];
        sum += v.getRadius() * v.getRadius();
        sum -= 2*v.getRadius();
     }
     return sum < 0;
}
double curvature3D(Vertex v)
{
       // For each tetra containing v, get the dihedral angle at v.
       double sum = 0.0;
       for(int i = 0; i < v.getLocalTetras()->size(); i++)
       {
           Tetra t = Triangulation::tetraTable[(*(v.getLocalTetras()))[i]];
           sum += dihedralAngle(v, t);
       }
       // Subtract sum from 4*PI
       return 4*PI - sum;
}
