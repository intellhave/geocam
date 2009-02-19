#include "3Dtriangulationmath.h"
#include "3DInputOutput.h"
#include "triangulation/triangulation.h"
#include "spherical/sphericalmath.h"
#include "miscmath.h"
#include "delaunay.h"
#include <math.h>
#include <cerrno>
#define PI 	3.141592653589793238
double solidAngle(double angle1, double angle2, double angle3)
{
       // Beta i = dihedral angle
       double beta1 = sphericalAngle(angle1, angle2, angle3);
       double beta2 = sphericalAngle(angle1, angle3, angle2);
       double beta3 = sphericalAngle(angle2, angle3, angle1);
       return beta1 + beta2 + beta3 - PI;      
}       
double solidAngle(Vertex v, Tetra t)
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
       return solidAngle(angle1, angle2, angle3);
}

double dihedralAngle(Vertex v, Edge e, Tetra t)
{
    Face f1, f2, f3;
    vector<int> faces = *(t.getLocalFaces());
    vector<int> edge_faces;
    vector<int> not_edge_faces;
    for(int i = 0; i < 4; i++)
    {
       if( Triangulation::faceTable[faces[i]].isAdjEdge(e.getIndex()) )
       {
          edge_faces.push_back(faces[i]);
       } else
       {
          not_edge_faces.push_back(faces[i]);
       }
    }
    f1 = Triangulation::faceTable[edge_faces[0]];
    f2 = Triangulation::faceTable[edge_faces[1]];
    if(Triangulation::faceTable[not_edge_faces[0]].isAdjVertex(v.getIndex()) ) 
    {
      f3 = Triangulation::faceTable[not_edge_faces[0]];                                                                       
    } else {
      f3 = Triangulation::faceTable[not_edge_faces[1]];
    }
    
    double angle1 = f1.getAngle(v.getIndex());
    double angle2 = f2.getAngle(v.getIndex());
    double angle3 = f3.getAngle(v.getIndex());
    //printf("%f \n", angle1);
    return sphericalAngle(angle1, angle2, angle3);
}

double volumeSq(Tetra t) 
{
   vector<int> localV = *(t.getLocalVertices());
   double radii[4] = {0};
   for(int i = 0; i < localV.size(); i++)
   {
       radii[i] = Triangulation::vertexTable[localV[i]].getRadius();
   }
   return volumeSq(radii[0], radii[1], radii[2], radii[3]);
}
double volumeSq(double r1, double r2, double r3, double r4)
{
   double sum1 = 1/r1 + 1/r2 + 1/r3 + 1/r4;
   double sum2 = 1/(r1 * r1) + 1/(r2 * r2) + 1/(r3 * r3) + 1/(r4 * r4);
   double product = 1/9.0 * (r1 * r1) * (r2 * r2) * (r3 * r3) * (r4 * r4);
   return product * (sum1 * sum1 - 2 * sum2);
}
bool isDegenerate(Tetra t)
{
     return volumeSq(t) > 0;
}

double CayleyvolumeSq(Tetra t) 
{
   int vertex, face;
   vector<int> edges1, edge23, edge24, edge34;
  
   vertex = (*(t.getLocalVertices()))[0];
   edges1 = listIntersection(Triangulation::vertexTable[vertex].getLocalEdges(), t.getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[1]].getLocalFaces())[0];
   edge23 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge24 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[1]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge34 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
    
   double L12 = Triangulation::edgeTable[edges1[0]].getLength();
   double L13 = Triangulation::edgeTable[edges1[1]].getLength();
   double L14 = Triangulation::edgeTable[edges1[2]].getLength();
   double L23 = Triangulation::edgeTable[edge23[0]].getLength();
   double L24 = Triangulation::edgeTable[edge24[0]].getLength();
   double L34 = Triangulation::edgeTable[edge34[0]].getLength();
   
   double CayleyMenger;
   
   CayleyMenger=-pow(L12, 4.0)*pow(L34,2.0)-pow(L13, 4.0)*pow(L24,2.0)-pow(L14, 4.0)*pow(L23,2.0)-pow(L23, 4.0)*pow(L14,2.0)-pow(L24, 4.0)*pow(L13,2.0)-pow(L34, 4.0)*pow(L12,2.0);
   CayleyMenger=CayleyMenger-pow(L12, 2.0)*pow(L13,2.0)*pow(L23,2.0)-pow(L12, 2.0)*pow(L14,2.0)*pow(L24,2.0)-pow(L13, 2.0)*pow(L14,2.0)*pow(L34,2.0)-pow(L23, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger+pow(L12, 2.0)*pow(L13,2.0)*pow(L24,2.0)+pow(L12, 2.0)*pow(L13,2.0)*pow(L34,2.0)+pow(L12, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L12, 2.0)*pow(L14,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger+pow(L13, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L13, 2.0)*pow(L14,2.0)*pow(L24,2.0)+pow(L13, 2.0)*pow(L23,2.0)*pow(L24,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L24,2.0);
   CayleyMenger=CayleyMenger+pow(L12, 2.0)*pow(L23,2.0)*pow(L34,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L34,2.0)+pow(L12, 2.0)*pow(L24,2.0)*pow(L34,2.0)+pow(L13, 2.0)*pow(L24,2.0)*pow(L34,2.0);
   CayleyMenger=CayleyMenger/144.0;


//   CayleyMenger=-pow(L12, 4.0)*pow(L34,2.0)-pow(L13, 4.0)*pow(L24,2.0)-pow(L14, 4.0)*pow(L23,2.0)
//                          -pow(L23, 4.0)*pow(L14,2.0)-pow(L24, 4.0)*pow(L13,2.0)-pow(L34, 4.0)*pow(L12,2.0)
//                          -pow(L12, 2.0)*pow(L13,2.0)*pow(L23,2.0)-pow(L12, 2.0)*pow(L14,2.0)*pow(L24,2.0)
//                          -pow(L13, 2.0)*pow(L14,2.0)*pow(L34,2.0)-pow(L23, 2.0)*pow(L24,2.0)*pow(L34,2.0)
//                          +pow(L12, 2.0)*pow(L13,2.0)*pow(L24,2.0)+pow(L12, 2.0)*pow(L13,2.0)*pow(L34,2.0)
//                          +pow(L12, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L12, 2.0)*pow(L14,2.0)*pow(L34,2.0)
//                          +pow(L13, 2.0)*pow(L14,2.0)*pow(L23,2.0)+pow(L13, 2.0)*pow(L14,2.0)*pow(L24,2.0)
//                          +pow(L13, 2.0)*pow(L23,2.0)*pow(L24,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L24,2.0)
//                          +pow(L12, 2.0)*pow(L23,2.0)*pow(L34,2.0)+pow(L14, 2.0)*pow(L23,2.0)*pow(L34,2.0)
//                          +pow(L12, 2.0)*pow(L24,2.0)*pow(L34,2.0)+pow(L13, 2.0)*pow(L24,2.0)*pow(L34,2.0);
//   CayleyMenger=CayleyMenger/144.0;

   
   return CayleyMenger;
}  

double CayleyvolumeSqDerivative(Tetra t)
{
   int vertex, face;
   vector<int> edges1, edge23, edge24, edge34;
   double result=0.0;
  
   vertex = (*(t.getLocalVertices()))[0];
   edges1 = listIntersection(Triangulation::vertexTable[vertex].getLocalEdges(), t.getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[1]].getLocalFaces())[0];
   edge23 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[0]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge24 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
   face   = listIntersection(Triangulation::edgeTable[edges1[1]].getLocalFaces(), Triangulation::edgeTable[edges1[2]].getLocalFaces())[0];
   edge34 = listDifference(Triangulation::faceTable[face].getLocalEdges(), Triangulation::vertexTable[vertex].getLocalEdges());
    
   double L12 = Triangulation::edgeTable[edges1[0]].getLength();
   double L13 = Triangulation::edgeTable[edges1[1]].getLength();
   double L14 = Triangulation::edgeTable[edges1[2]].getLength();
   double L23 = Triangulation::edgeTable[edge23[0]].getLength();
   double L24 = Triangulation::edgeTable[edge24[0]].getLength();
   double L34 = Triangulation::edgeTable[edge34[0]].getLength();
   
   double  Eta12 = Triangulation::edgeTable[edges1[0]].getEta();
   double  Eta13 = Triangulation::edgeTable[edges1[1]].getEta();
   double  Eta14 = Triangulation::edgeTable[edges1[2]].getEta();
   double  Eta23 = Triangulation::edgeTable[edge23[0]].getEta();
   double  Eta24 = Triangulation::edgeTable[edge24[0]].getEta();
   double  Eta34 = Triangulation::edgeTable[edge34[0]].getEta();
    
   int  V2 =  listIntersection(Triangulation::edgeTable[edge23[0]].getLocalVertices(), Triangulation::edgeTable[edge24[0]].getLocalVertices())[0];
   int  V3 =  listIntersection(Triangulation::edgeTable[edge23[0]].getLocalVertices(), Triangulation::edgeTable[edge34[0]].getLocalVertices())[0];
   int  V4 =  listIntersection(Triangulation::edgeTable[edge24[0]].getLocalVertices(), Triangulation::edgeTable[edge34[0]].getLocalVertices())[0];
   
   double  K1 =  Triangulation::vertexTable[vertex].getCurvature();
   double  K2 =  Triangulation::vertexTable[V2].getCurvature();
   double  K3 =  Triangulation::vertexTable[V3].getCurvature();
   double  K4 =  Triangulation::vertexTable[V4].getCurvature();
   
   double  R1 =  Triangulation::vertexTable[vertex].getRadius();
   double  R2 =  Triangulation::vertexTable[V2].getRadius();
   double  R3 =  Triangulation::vertexTable[V3].getRadius();
   double  R4 =  Triangulation::vertexTable[V4].getRadius();
   
//   result = (((1.0/72.0))*(((-2.0)*pow(L12, 3)*pow(L34, 2)*(((-((R1*K1+R2*K2+((K1*R2+K2*R1))*Eta12)))/L12))
//   -L12*((((pow(L13, 2)-pow(L14, 2)))*((pow(L23, 2)-pow(L24, 2)))-((pow(L13, 2)+pow(L14, 2)
//   +pow(L23, 2)+pow(L24, 2)))*pow(L34, 2)+pow(L34, 4)))*(((-((R1*K1+R2*K2+((K1*R2+K2*R1))
//   *Eta12)))/L12))-2.0*pow(L13, 3)*pow(L24, 2)*(((-((R1*K1+R3*K3+((K1*R3+K3*R1))*Eta13)))/L13))
//   +L13*((pow(L14, 2)*((pow(L23, 2)+pow(L24, 2)-pow(L34, 2)))+pow(L24, 2)*((pow(L23, 2)
//   -pow(L24, 2)+pow(L34, 2)))))*(((-((R1*K1+R3*K3+((K1*R3+K3*R1))*Eta13)))/L13))-pow(L13, 4)
//   *L24*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))-pow(L12, 4)*L34*(((-((R3*K3+R4*K4
//   +((K3*R4+K4*R3))*Eta34)))/L34))+pow(L13, 2)*((L14*((pow(L23, 2)+pow(L24, 2)-pow(L34, 2)))
//   *(((-((R1*K1+R4*K4+((K1*R4+K4*R1))*Eta14)))/L14))+pow(L14, 2)*((L23*(((-((R2*K2+R3*K3
//   +((K2*R3+K3*R2))*Eta23)))/L23))+L24*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))
//   -L34*(((-((R3*K3+R4*K4+((K3*R4+K4*R3))*Eta34)))/L34))))+L24*((L23*L24*(((-((R2*K2+R3*K3
//   +((K2*R3+K3*R2))*Eta23)))/L23))+pow(L23, 2)*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))
//   -2.0*pow(L24, 2)*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))+pow(L34, 2)*(((-((R2*K2+R4*K4
//   +((K2*R4+K4*R2))*Eta24)))/L24))+L24*L34*(((-((R3*K3+R4*K4+((K3*R4+K4*R3))*Eta34)))/L34))))))
//   +pow(L12, 2)*(((-L13)*((pow(L23, 2)-pow(L24, 2)-pow(L34, 2)))*(((-((R1*K1+R3*K3
//   +((K1*R3+K3*R1))*Eta13)))/L13))+L14*((pow(L23, 2)-pow(L24, 2)+pow(L34, 2)))*(((-((R1*K1+R4*K4
//   +((K1*R4+K4*R1))*Eta14)))/L14))+pow(L14, 2)*((L23*(((-((R2*K2+R3*K3+((K2*R3+K3*R2))*Eta23)))/L23))
//   -L24*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))+L34*(((-((R3*K3+R4*K4+((K3*R4+K4*R3))
//   *Eta34)))/L34))))+pow(L13, 2)*(((-L23)*(((-((R2*K2+R3*K3+((K2*R3+K3*R2))*Eta23)))/L23))
//   +L24*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))+L34*(((-((R3*K3+R4*K4+((K3*R4+K4*R3))
//   *Eta34)))/L34))))+L34*((L34*((L23*(((-((R2*K2+R3*K3+((K2*R3+K3*R2))*Eta23)))/L23))
//   +L24*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))))+((pow(L23, 2)+pow(L24, 2)
//   -2.0*pow(L34, 2)))*(((-((R3*K3+R4*K4+((K3*R4+K4*R3))*Eta34)))/L34))))))+L23*(((-2.0)*pow(L14, 3)
//   *L23*(((-((R1*K1+R4*K4+((K1*R4+K4*R1))*Eta14)))/L14))-L14*L23*((pow(L23, 2)-pow(L24, 2)
//   -pow(L34, 2)))*(((-((R1*K1+R4*K4+((K1*R4+K4*R1))*Eta14)))/L14))-pow(L14, 4)*(((-((R2*K2+R3*K3
//   +((K2*R3+K3*R2))*Eta23)))/L23))-L24*L34*((L23*L34*(((-((R2*K2+R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))
//   +L24*((L34*(((-((R2*K2+R3*K3+((K2*R3+K3*R2))*Eta23)))/L23))+L23*(((-((R3*K3+R4*K4+((K3*R4+K4*R3))
//   *Eta34)))/L34))))))+pow(L14, 2)*(((-2.0)*pow(L23, 2)*(((-((R2*K2+R3*K3+((K2*R3+K3*R2))*Eta23)))/L23))
//   +((pow(L24, 2)+pow(L34, 2)))*(((-((R2*K2+R3*K3+((K2*R3+K3*R2))*Eta23)))/L23))+L23*((L24*(((-((R2*K2
//   +R4*K4+((K2*R4+K4*R2))*Eta24)))/L24))+L34*(((-((R3*K3+R4*K4+((K3*R4+K4*R3))*Eta34)))/L34)))))))))));


// The following computation calculates the derivative of the volume of a single tetrahedron.
// The calculation above is for the derivative of the square of the volume.
 
result=(((Eta12* K2* pow(L13, 2)* pow(L23, 2)* R1 - Eta12* K2* pow(L14, 2)* pow(L23, 2)* R1 +
        K1* pow(L23, 4)* R1 - Eta12* K2* pow(L13, 2)* pow(L24, 2)* R1 +
        Eta12* K2* pow(L14, 2)* pow(L24, 2)* R1 - 2* K1* pow(L23, 2)* pow(L24, 2)* R1 +
        K1* pow(L24, 4)* R1 + 2* Eta12* K2* pow(L12, 2)* pow(L34, 2)* R1 -
        Eta12* K2* pow(L13, 2)* pow(L34, 2)* R1 - Eta12* K2* pow(L14, 2)* pow(L34, 2)* R1
-
        2* K1* pow(L23, 2)* pow(L34, 2)* R1 - Eta12* K2* pow(L23, 2)* pow(L34, 2)* R1 -
        2* K1* pow(L24, 2)* pow(L34, 2)* R1 - Eta12* K2* pow(L24, 2)* pow(L34, 2)* R1 +
        K1* pow(L34, 4)* R1 + Eta12* K2* pow(L34, 4)* R1 +
        Eta23* K3* pow(L12, 2)* pow(L13, 2)* R2 - Eta24* K4* pow(L12, 2)* pow(L13, 2)* R2
+
        K2* pow(L13, 4)* R2 + Eta24* K4* pow(L13, 4)* R2 -
        Eta23* K3* pow(L12, 2)* pow(L14, 2)* R2 + Eta24* K4* pow(L12, 2)* pow(L14, 2)* R2
-
        2* K2* pow(L13, 2)* pow(L14, 2)* R2 - Eta23* K3* pow(L13, 2)* pow(L14, 2)* R2 -
        Eta24* K4* pow(L13, 2)* pow(L14, 2)* R2 + K2* pow(L14, 4)* R2 +
        Eta23* K3* pow(L14, 4)* R2 + Eta12* K1* pow(L13, 2)* pow(L23, 2)* R2 -
        Eta24* K4* pow(L13, 2)* pow(L23, 2)* R2 - Eta12* K1* pow(L14, 2)* pow(L23, 2)* R2
+
        2* Eta23* K3* pow(L14, 2)* pow(L23, 2)* R2 - Eta24* K4* pow(L14, 2)* pow(L23, 2)*
R2 -
        Eta12* K1* pow(L13, 2)* pow(L24, 2)* R2 - Eta23* K3* pow(L13, 2)* pow(L24, 2)* R2
+
        2* Eta24* K4* pow(L13, 2)* pow(L24, 2)* R2 + Eta12* K1* pow(L14, 2)* pow(L24, 2)*
R2 -
        Eta23* K3* pow(L14, 2)* pow(L24, 2)* R2 + 2* Eta12* K1* pow(L12, 2)* pow(L34, 2)*
R2 -
        Eta23* K3* pow(L12, 2)* pow(L34, 2)* R2 - Eta24* K4* pow(L12, 2)* pow(L34, 2)* R2
-
        Eta12* K1* pow(L13, 2)* pow(L34, 2)* R2 - 2* K2* pow(L13, 2)* pow(L34, 2)* R2 -
        Eta24* K4* pow(L13, 2)* pow(L34, 2)* R2 - Eta12* K1* pow(L14, 2)* pow(L34, 2)* R2
-
        2* K2* pow(L14, 2)* pow(L34, 2)* R2 - Eta23* K3* pow(L14, 2)* pow(L34, 2)* R2 -
        Eta12* K1* pow(L23, 2)* pow(L34, 2)* R2 + Eta24* K4* pow(L23, 2)* pow(L34, 2)* R2
-
        Eta12* K1* pow(L24, 2)* pow(L34, 2)* R2 + Eta23* K3* pow(L24, 2)* pow(L34, 2)* R2
+
        Eta12* K1* pow(L34, 4)* R2 + K2* pow(L34, 4)* R2 + K3* pow(L12, 4)* R3 +
        Eta34* K4* pow(L12, 4)* R3 + Eta23* K2* pow(L12, 2)* pow(L13, 2)* R3 -
        Eta34* K4* pow(L12, 2)* pow(L13, 2)* R3 - Eta23* K2* pow(L12, 2)* pow(L14, 2)* R3
-
        2* K3* pow(L12, 2)* pow(L14, 2)* R3 - Eta34* K4* pow(L12, 2)* pow(L14, 2)* R3 -
        Eta23* K2* pow(L13, 2)* pow(L14, 2)* R3 + Eta34* K4* pow(L13, 2)* pow(L14, 2)* R3
+
        Eta23* K2* pow(L14, 4)* R3 + K3* pow(L14, 4)* R3 -
        Eta34* K4* pow(L12, 2)* pow(L23, 2)* R3 + 2* Eta23* K2* pow(L14, 2)* pow(L23, 2)*
R3 -
        Eta34* K4* pow(L14, 2)* pow(L23, 2)* R3 - 2* K3* pow(L12, 2)* pow(L24, 2)* R3 -
        Eta34* K4* pow(L12, 2)* pow(L24, 2)* R3 - Eta23* K2* pow(L13, 2)* pow(L24, 2)* R3
-
        Eta34* K4* pow(L13, 2)* pow(L24, 2)* R3 - Eta23* K2* pow(L14, 2)* pow(L24, 2)* R3
-
        2* K3* pow(L14, 2)* pow(L24, 2)* R3 + Eta34* K4* pow(L23, 2)* pow(L24, 2)* R3 +
        K3* pow(L24, 4)* R3 - Eta23* K2* pow(L12, 2)* pow(L34, 2)* R3 +
        2* Eta34* K4* pow(L12, 2)* pow(L34, 2)* R3 - Eta23* K2* pow(L14, 2)* pow(L34, 2)*
R3 +
        Eta23* K2* pow(L24, 2)* pow(L34, 2)* R3 +
        Eta13* ((pow(L12, 2)* ((pow(L23, 2) - pow(L24, 2) - pow(L34, 2))) +
              pow(L24, 2)* ((2* pow(L13, 2) - pow(L23, 2) + pow(L24, 2) - pow(L34, 2))) -
              pow(L14, 2)* ((pow(L23, 2) + pow(L24, 2) - pow(L34, 2)))))* ((K3* R1 +
              K1* R3)) + ((K4* ((L12 - L13 - L23))* ((L12 + L13 -
                    L23))* ((L12 - L13 + L23))* ((L12 + L13 + L23)) +
              Eta34* K3* ((pow(L12, 4) + ((L13 - L23))* ((L13 +
                          L23))* ((L14 - L24))* ((L14 + L24)) -
                    pow(L12, 2)* ((pow(L13, 2) + pow(L14, 2) + pow(L23, 2) + pow(L24, 2) -
                          2* pow(L34, 2))))) +
              Eta24* K2* ((pow(L13, 4) + pow(L23, 2)* (((-pow(L14, 2)) + pow(L34, 2))) -
                    pow(L12, 2)* ((pow(L13, 2) - pow(L14, 2) + pow(L34, 2))) -
                    pow(L13, 2)* ((pow(L14, 2) + pow(L23, 2) - 2* pow(L24, 2) +
                          pow(L34, 2)))))))* R4 -
        Eta14* ((pow(L13, 2)* ((pow(L23, 2) + pow(L24, 2) - pow(L34, 2))) +
              pow(L12, 2)* ((pow(L23, 2) - pow(L24, 2) + pow(L34, 2))) +
              pow(L23, 2)* (((-2)* pow(L14, 2) - pow(L23, 2) + pow(L24, 2) +
                    pow(L34, 2)))))* ((K4* R1 +
              K1* R4))))/((12* sqrt(((-pow(L13, 4))* pow(L24, 2) -
              pow(L12, 4)* pow(L34, 2) +
              pow(L12, 2)* (((((-pow(L13, 2)) + pow(L14, 2)))* ((L23 -
                          L24))* ((L23 + L24)) + ((pow(L13, 2) + pow(L14, 2) +
                          pow(L23, 2) + pow(L24, 2)))* pow(L34, 2) - pow(L34, 4))) -
              pow(L23, 2)* ((pow(L14, 4) + pow(L24, 2)* pow(L34, 2) +
                    pow(L14, 2)* ((pow(L23, 2) - pow(L24, 2) - pow(L34, 2))))) +
              pow(L13, 2)* ((pow(L14, 2)* ((pow(L23, 2) + pow(L24, 2) - pow(L34, 2))) +
                    pow(L24, 2)* ((pow(L23, 2) - pow(L24, 2) + pow(L34, 2))))))))));
  
// The calculation above is the derivative of the volume function (not squared).   
   
   return result;
}


void curvature3D()
{
   map<int, Vertex>::iterator vit;
   map<int, Edge>::iterator eit;
   map<int, Tetra>::iterator tit;
   
   Triangulation::setAngles();
   Triangulation::setDihedralAngles();

   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
           vit++) 
   {
      double curv = 0;
      for(int i = 0; i < vit->second.getLocalEdges()->size(); i++)
      {
        Edge e = Triangulation::edgeTable[(*(vit->second.getLocalEdges()))[i]];
        double betaSum = 2*PI;
        for(int j = 0; j < e.getLocalTetras()->size(); j++)
        {
           betaSum -= e.getDihedralAngle((*(e.getLocalTetras()))[j]);
        }
        curv += betaSum * getPartialEdge(e, vit->second);
      }
      vit->second.setCurvature(curv);
   }
}

void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt ,
               double *initRadii,int numSteps, bool adjF)  
{
  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.
                                     
  double z[p]; // Temporary array to hold data for 
                                      // the intermediate steps in.
  double curCurv;
  double normalization;
  int    i,k; // ints used for "for loops". i is the step number,
              // k is the kth vertex for the arrays.
  map<int, Vertex>::iterator vit; // Iterator to traverse through the vertices.
  // Beginning and Ending pointers. (Used for "for" loops)
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  map<int, Tetra>::iterator tit;
  
   for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
   {
      // Set the radii of the Triangulation.
      z[k]=initRadii[k];
      vit->second.setRadius(z[k]);
   }
   for (i=1; i<numSteps+1; i++) // This is the main loop through each step.
   {
       curvature3D();
       normalization = calcNormalization();
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++) 
       {
           (*radii).push_back( z[k]); // Adds the data to the vector.
           curCurv = vit->second.getCurvature();
           if(curCurv < 0.00005 && curCurv > -0.00005) // Adjusted for small numbers.
           {                                     // We want it to print nicely.
             (*curvatures).push_back(0.); // Adds the data to the vector.
           }
           else {
               (*curvatures).push_back(curCurv);
           }
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * curCurv +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * curCurv;
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
       }
   }
}

void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt ,
               int numSteps, bool adjF)
{
   double initRadii[Triangulation::vertexTable.size()];
   Triangulation::getRadii(initRadii);
   yamabeFlow(radii, curvatures, dt, initRadii, numSteps, adjF);
}

void yamabeFlow(double dt, double *initRadii,int numSteps, bool adjF)
{
  int p = Triangulation::vertexTable.size(); // The number of vertices or 
                                             // number of variables in system.                                  
  double z[p]; // Temporary array to hold data for 
                                      // the intermediate steps in.
  double normalization;
  int    i,k; // ints used for "for loops". i is the step number,
              // k is the kth vertex for the arrays.
  map<int, Vertex>::iterator vit; // Iterator to traverse through the vertices.
  // Beginning and Ending pointers. (Used for "for" loops)
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
   for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
   {
     z[k]=initRadii[k]; // z[k] holds the current radii.
     // Set the radii of the Triangulation.
     vit->second.setRadius(z[k]);
   }
   for (i=1; i<numSteps+1; i++) // This is the main loop through each step.
   {
       curvature3D();
       normalization = calcNormalization();
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++) 
       {  // First "for loop" in whole step calculates everything manually.
          
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * vit->second.getCurvature() +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * vit->second.getCurvature();
       }
       for (k=0, vit = vBegin; k<p && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
       }
   }
}
void yamabeFlow(double dt, int numSteps, bool adjF)
{
   double initRadii[Triangulation::vertexTable.size()];
   Triangulation::getRadii(initRadii);
   yamabeFlow(dt, initRadii, numSteps, adjF);
}


void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt, double *initRadii, 
                       double accuracy, double precision, bool adjF)
{
    
    bool done = false, accurate, precise, firstStep = true;
    int numV = Triangulation::vertexTable.size();
    int k;
    double avgProp;
    double normalization;
    double curCurv;
    double z[numV];
    map<int, double> prevCurvs;
    map<int, Vertex>::iterator vit;
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
    for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++)  
    {
       prevCurvs.insert(pair<int, double>(vit->first, 0.0));
       z[k] = initRadii[k]; // z[k] holds the current radii.
       // Set the radii of the Triangulation.
       vit->second.setRadius(z[k]);
    }
    while(!done && errno == 0)
    {
       curvature3D();
       normalization = calcNormalization();
       for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++) 
       {   
           (*radii).push_back( z[k]); // Adds the data to the vector.
           curCurv = vit->second.getCurvature();
           if(curCurv < 0.00005 && curCurv > -0.00005) // Adjusted for small numbers.
           {                                     // We want it to print nicely.
             (*curvatures).push_back(0.); // Adds the data to the vector.
           }
           else {
               (*curvatures).push_back(curCurv);
           }         
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * curCurv +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * curCurv;
       }
       if(!firstStep) 
       {
         precise = true;
         for (vit = vBegin; vit != vEnd; vit++) 
         {
           precise = precise && ( fabs(prevCurvs[vit->first] - vit->second.getCurvature()) < precision );
         }
         if(precise)
         {
          accurate = true;
          avgProp = 0;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            avgProp += vit->second.getCurvature() / vit->second.getRadius();
          }
          avgProp = avgProp / numV;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            accurate = accurate && ( fabs(avgProp - vit->second.getCurvature() / vit->second.getRadius()) < accuracy );
          }
          done = accurate;     
         }
       } else
       {
         firstStep = false;
       }
       for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++)  
       {
           // Set the radii of the Triangulation.
           vit->second.setRadius(z[k]);
       }
       for (vit = vBegin; vit != vEnd; vit++) 
       {
           prevCurvs[vit->first] = vit->second.getCurvature();
           if(prevCurvs[vit->first] < -10000000)
           {
              exit(1);
           }
       }
    }
}
void yamabeFlow(vector<double>* radii, vector<double>* curvatures,double dt,
                       double accuracy, double precision, bool adjF)
{
   double initRadii[Triangulation::vertexTable.size()];
   Triangulation::getRadii(initRadii);
   yamabeFlow(radii, curvatures, dt, initRadii, accuracy, precision, adjF);
}
void yamabeFlow(double dt, double *initRadii, 
                       double accuracy, double precision, bool adjF)
{
    
    bool done = false, accurate, precise, firstStep = true;
    int numV = Triangulation::vertexTable.size();
    int k;
    double avgProp;
    double normalization;
    double z[numV];
    map<int, double> prevCurvs;
    map<int, Vertex>::iterator vit;
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
    map<int, Edge>::iterator eit;
    for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++)  
    {
       prevCurvs.insert(pair<int, double>(vit->first, 0.0));
       z[k] = initRadii[k]; // z[k] holds the current radii.
       // Set the radii of the Triangulation.
       vit->second.setRadius(z[k]);
    }
    while(!done && errno == 0)
    {
       curvature3D();
       if(errno != 0) {
         for (vit = vBegin; vit != vEnd; vit++) 
         {
             printf("Vertex %3d:\t%12.10f\t%12.10f\n", vit->first, vit->second.getRadius(), vit->second.getCurvature());
         }
         printf("\n");
         for (eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) 
         {
             printf("Edge %3d:\t%12.10f\t%12.10f\n", eit->first, eit->second.getEta(), eit->second.getLength());
         }
       }
       normalization = calcNormalization();
       if(!firstStep) 
       {
         precise = true;
         for (vit = vBegin; vit != vEnd; vit++) 
         {
           precise = precise && ( fabs(prevCurvs[vit->first] - vit->second.getCurvature()) < precision );
         }
         if(precise)
         {
          accurate = true;
          avgProp = 0;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            avgProp += vit->second.getCurvature() / vit->second.getRadius();
          }
          avgProp = avgProp / numV;
          for(vit = vBegin; vit != vEnd; vit++)
          {
            accurate = accurate && ( fabs(avgProp - vit->second.getCurvature() / vit->second.getRadius()) < accuracy );
          }
          done = accurate;     
         }
       } else
       {
         firstStep = false;
       }
       for (k=0, vit = vBegin; k<numV && vit != vEnd; k++, vit++) 
       {      
           // Calculates the differential equation, either normalized or
           // standard.
           if(adjF) z[k]= z[k] + dt * ((-1) * vit->second.getCurvature() +
                           normalization* vit->second.getRadius());
           else     z[k] = z[k] + dt * (-1) * vit->second.getCurvature();
           
           vit->second.setRadius(z[k]);
       }
       for (vit = vBegin; vit != vEnd; vit++) 
       {
           prevCurvs[vit->first] = vit->second.getCurvature();
       }
    }
}

void yamabeFlow(double dt, double accuracy, double precision, bool adjF)
{
   double initRadii[Triangulation::vertexTable.size()];
   Triangulation::getRadii(initRadii);
   yamabeFlow(dt, initRadii, accuracy, precision, adjF);
}

//double calcNormalization()
//{
//   double result = 0;
//   double tempNum;
//   double denom = 0;
//   map<int, Edge>::iterator eit;
//   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
//   {
//      tempNum = 0;
//      Vertex v1 = Triangulation::vertexTable[(*(eit->second.getLocalVertices()))[0]];
//      Vertex v2 = Triangulation::vertexTable[(*(eit->second.getLocalVertices()))[1]];
//      
//      tempNum += v1.getRadius()*v1.getCurvature() + v2.getRadius()*v2.getCurvature();
//      tempNum += eit->second.getEta() * 
//              (v1.getRadius()*v2.getCurvature() + v2.getRadius()*v1.getCurvature());
//      result += tempNum / eit->second.getLength();
//      denom += eit->second.getLength();
//   }
//   return result / denom;
//}

double calcNormalization()
{
   double result = 0;
//   double tempNum1=0;
//   double tempNum2=0;
   double denom = 0;
   double V=0;
   map<int, Tetra>::iterator tit;
   for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
   {
      V=sqrt(CayleyvolumeSq(tit->second));
      
//      result += CayleyvolumeSqDerivative(tit->second)/(2*V);
      result += CayleyvolumeSqDerivative(tit->second);
      
      denom += V;
    
   }
//         printf("total volume = %.10f\n", denom); 
   return (-1.0/3.0)*result / denom;
}
