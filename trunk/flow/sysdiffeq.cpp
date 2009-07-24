#include <cmath>

#include "sysdiffeq.h"
#include "curvature2D.h"
#include "curvature3D.h"
#include "volume.h"
#include "Triangulation/triangulation.h"
#include "spherical/sphericalmath.h"
#include "hyperbolic/hyperbolicmath.h"
#include "3DTriangulation/3Dtriangulationmath.h"

void StdRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  double Ki, ri;
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
    Ki = Curvature2D::valueAt(vit->second);     
    ri = Radius::valueAt(vit->second);     
    derivs[ii] = (-1) * Ki * ri;
  }
}

void AdjRicci(double derivs[]){     
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  int vertSize = Triangulation::vertexTable.size();
  double Ki[vertSize];
  double ri, avgK;
  
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++) {
     Ki[ii] = Curvature2D::valueAt(vit->second);
     avgK += Ki[ii];
  }
  
  avgK = avgK / vertSize;
  
  ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){     
    ri = Radius::valueAt(vit->second); 
    derivs[ii] = (avgK - Ki[ii]) * ri;
  }   
}

/* TODO: Spherical flows depend on the quantity "radius" that 
   is updated over the course of the flow. Currently, the equations
   below don't account for this. */

void SpherRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();  
  
  double Ki, ri;
  double radius = 1.0; // Very mysterious constant. See sphericalmath.cpp.
  
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
      Ki = Curvature2D::valueAt(vit->second);     
      ri = Radius::valueAt(vit->second) / radius; 
      derivs[ii] = (-1) * Ki * sin(ri / radius);
  }   
}

// Is this the function we want?
void AdjSpherRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();  

  double radius = 1.0; // Very mysterious constant. See sphericalmath.cpp.      
  int vertSize = Triangulation::vertexTable.size();
  double Ki[vertSize];
  double ri, avgK;

  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++) {
     Ki[ii] = Curvature2D::valueAt(vit->second);
     avgK += Ki[ii];
  }
  
  
  avgK = avgK / vertSize;
  
  ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){  
      ri = Radius::valueAt(vit->second) / radius; 
      derivs[ii] = (avgK * ri) - (Ki[ii] * sin(ri));
  }     
}

void HypRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();  

  double Ki, ri;
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
      Ki = Curvature2D::valueAt(vit->second);     
      ri = Radius::valueAt(vit->second); 
      derivs[ii] = (-1) * Ki * sinh(ri);
  }     
}

void AdjHypRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();  
  int vertSize = Triangulation::vertexTable.size();
  double Ki[vertSize];
  double ri, avgK;

  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++) {
     Ki[ii] = Curvature2D::valueAt(vit->second);
     avgK += Ki[ii];
  }
  
  
  avgK = avgK / vertSize;
  
  ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){  
      ri = Radius::valueAt(vit->second); 
      derivs[ii] = (avgK * ri) - (Ki[ii] * sinh(ri));
  }     
}

// The calculation below is the derivative of the volume function (not squared)
// with respect to time.
double cayleyVolumeDeriv(Tetra& t)
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
    
   double L12 = Length::valueAt(Triangulation::edgeTable[edges1[0]]);
   double L13 = Length::valueAt(Triangulation::edgeTable[edges1[1]]);
   double L14 = Length::valueAt(Triangulation::edgeTable[edges1[2]]);
   double L23 = Length::valueAt(Triangulation::edgeTable[edge23[0]]);
   double L24 = Length::valueAt(Triangulation::edgeTable[edge24[0]]);
   double L34 = Length::valueAt(Triangulation::edgeTable[edge34[0]]);
   
   double  Eta12 = Eta::valueAt(Triangulation::edgeTable[edges1[0]]);
   double  Eta13 = Eta::valueAt(Triangulation::edgeTable[edges1[1]]);
   double  Eta14 = Eta::valueAt(Triangulation::edgeTable[edges1[2]]);
   double  Eta23 = Eta::valueAt(Triangulation::edgeTable[edge23[0]]);
   double  Eta24 = Eta::valueAt(Triangulation::edgeTable[edge24[0]]);
   double  Eta34 = Eta::valueAt(Triangulation::edgeTable[edge34[0]]);
    
   int  V2 =  listIntersection(Triangulation::edgeTable[edge23[0]].getLocalVertices(), Triangulation::edgeTable[edge24[0]].getLocalVertices())[0];
   int  V3 =  listIntersection(Triangulation::edgeTable[edge23[0]].getLocalVertices(), Triangulation::edgeTable[edge34[0]].getLocalVertices())[0];
   int  V4 =  listIntersection(Triangulation::edgeTable[edge24[0]].getLocalVertices(), Triangulation::edgeTable[edge34[0]].getLocalVertices())[0];
   
   double  K1 =  Curvature3D::valueAt(Triangulation::vertexTable[vertex]);
   double  K2 =  Curvature3D::valueAt(Triangulation::vertexTable[V2]);
   double  K3 =  Curvature3D::valueAt(Triangulation::vertexTable[V3]);
   double  K4 =  Curvature3D::valueAt(Triangulation::vertexTable[V4]);
   
   double  R1 =  Radius::valueAt(Triangulation::vertexTable[vertex]);
   double  R2 =  Radius::valueAt(Triangulation::vertexTable[V2]);
   double  R3 =  Radius::valueAt(Triangulation::vertexTable[V3]);
   double  R4 =  Radius::valueAt(Triangulation::vertexTable[V4]);
 
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
     
   return result;
}
       
double calcNormalization()
{
   double result = 0;
   double denom = 0;
   double V=0;
   map<int, Tetra>::iterator tit;
   for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
   {
      V=Volume::valueAt(tit->second);
      result += cayleyVolumeDeriv(tit->second);
      
      denom += V;
    
   }
   return (-1.0/3.0)*result / denom;
}


void Yamabe(double derivs[]) {
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
   
  double norm = calcNormalization();
  double Ki, ri;
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++) {
      Ki = Curvature3D::valueAt(vit->second);     
      ri = Radius::valueAt(vit->second); 
      derivs[ii] = (norm * ri - Ki);        
  }    
}
