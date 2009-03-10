#include <cmath>

#include "sysdiffeq.h"
#include "Triangulation/triangulation.h"
#include "spherical/sphericalmath.h"
#include "hyperbolic/hyperbolicmath.h"

void StdRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  Vertex vert;
  
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
    vert = vit->second;
    derivs[ii] = (-1) * curvature(vert) * vert.getRadius();
  }
}

void AdjRicci(double derivs[]){     
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  double Ki, ri;
  double avgK = Triangulation::netCurvature() / Triangulation::vertexTable.size();
  
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
    Ki = curvature(vit->second);     
    ri = vit->second.getRadius(); 
    derivs[ii] = (avgK - Ki) * ri;
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
      Ki = sphericalCurvature(vit->second, radius);
      ri = vit->second.getRadius();
      derivs[ii] = (-1) * Ki * sin(ri / radius);
  }   
}

// Is this the function we want?
void AdjSpherRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();  

  double radius = 1.0; // Very mysterious constant. See sphericalmath.cpp.

  double totK = 0;
  for(vit = vBegin; vit != vEnd; vit++)
      totK += sphericalCurvature(vit->second);        
  double avgK = totK / Triangulation::vertexTable.size();

  double Ki, ri;
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
      Ki = sphericalCurvature(vit->second);
      ri = vit->second.getRadius() / radius;
      derivs[ii] = (avgK * ri) - (Ki * sin(ri));
  }     
}

void HypRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin =  Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();  

  double Ki, ri;

  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
      Ki = hyperbolicCurvature(vit->second);
      ri = vit->second.getRadius();
      derivs[ii] = (-1) * Ki * sinh(ri);
  }     
}

void AdjHypRicci(double derivs[]){
  map<int, Vertex>::iterator vit;
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();  

  double totK = 0;
  for(vit = vBegin; vit != vEnd; vit++)
      totK += hyperbolicCurvature(vit->second);        
  
  double avgK = totK / Triangulation::vertexTable.size();

  double Ki, ri;
  int ii = 0;
  for(vit = vBegin; vit != vEnd; ii++, vit++){
      Ki = hyperbolicCurvature(vit->second);
      ri = vit->second.getRadius();
      derivs[ii] = (avgK * ri) - (Ki * sinh(ri));
  }     
}
