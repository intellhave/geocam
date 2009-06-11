#include <cmath>

#include "sysdiffeq.h"
#include "Geometry/Geometry.h"
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

double calcNormalization()
{
   double result = 0;
   double denom = 0;
   double V=0;
   map<int, Tetra>::iterator tit;
   for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
   {
      V=Volume::valueAt(tit->second);
      result += VolumePartial::valueAt(tit->second);
      
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
