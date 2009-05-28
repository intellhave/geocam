#include "Geometry.h"
#include "triposition.h"
#include "eta.cpp"
#include "radius.cpp"
#include "length.cpp"
#include "euc_angle.cpp"
#include "dih_angle.cpp"
#include "curvature2d.cpp"

#include "miscmath.h"

/******************/
#include <cstdio>
/******************/

/*
  for( GQIndex::iterator iter = gqi.begin(); iter != gqi.end(); ++iter ){
    TriPosition t = iter->first;
    t.print();
  } fprintf(stdout, "\n");
  */


void Geometry::build() {
  gqi.clear();
  fprintf(stdout, "GQI cleared.\n");
  
  Init_Etas(gqi);
  fprintf(stdout, "Etas Loaded.\n");
  fprintf(stdout, "# quantities: %d\n", gqi.size());  
  
  Init_Radii(gqi);
  fprintf(stdout, "Radii Loaded.\n");
  fprintf(stdout, "# quantities: %d\n", gqi.size());

  Init_Lengths(gqi);
  fprintf(stdout, "Lengths Loaded.\n");
  fprintf(stdout, "# quantities: %d\n", gqi.size());

  Init_EuclideanAngles(gqi);
  fprintf(stdout, "Euclidean Angles Loaded.\n");
  fprintf(stdout, "# quantities: %d\n", gqi.size());

  Init_Curvature2Ds(gqi);
  fprintf(stdout, "Curvatures Loaded.\n");
  fprintf(stdout, "# quantities: %d\n", gqi.size());
}
       
void Geometry::setRadius(Vertex& v, double rad) {
  TriPosition tp(RADIUS, 1, v.getSerialNumber());
  GeoQuant* radius = gqi[tp];
     
  radius->setValue(rad);
}

void Geometry::setEta(Edge& e, double eta) {
  TriPosition tp(ETA, 1, e.getSerialNumber());
  GeoQuant* etaGQ = gqi[tp];
     
  etaGQ->setValue(eta);     
}

void Geometry::setLength(Edge& e, double len) {
  TriPosition tp(LENGTH, 1, e.getSerialNumber());
  GeoQuant* length =  gqi[tp];
     
  length->setValue(len);   
}
       
double Geometry::radius(Vertex& v) {
  TriPosition tp(RADIUS, 1, v.getSerialNumber());
  GeoQuant* radius = gqi[tp];
       
  return radius->getValue();
}

double Geometry::eta(Edge& e) {
  TriPosition tp(ETA, 1, e.getSerialNumber());
  GeoQuant* etaGQ = gqi[tp];
     
  return etaGQ->getValue();           
}

double Geometry::length(Edge& e) {
  TriPosition tp(LENGTH, 1, e.getSerialNumber());
  GeoQuant* length =  gqi[tp];
     
  return length->getValue();       
}

double Geometry::angle(Vertex& v,  Face& f) {
  TriPosition tp(ANGLE, 1, v.getSerialNumber(), f.getSerialNumber());
  GeoQuant* angle = gqi[tp];
  return angle->getValue();      
}

double Geometry::dihedralAngle(Edge& e,  Tetra& t) {
  TriPosition tp(DIHEDRALANGLE,2, e.getSerialNumber(), t.getSerialNumber());
  GeoQuant* dihAng = gqi[tp];
       
  return dihAng->getValue();   
}

double Geometry::curvature(Vertex& v) {
  TriPosition tp(CURVATURE, 1, v.getSerialNumber());
  GeoQuant* curv = gqi[tp];
       
  return curv->getValue();     
}

void Geometry::setRadii(double* radii){
  map<int, Vertex>::iterator vit;
  int i = 0;
  for(vit = Triangulation::vertexTable.begin(); 
      vit != Triangulation::vertexTable.end(); vit++){
    setRadius(vit->second, radii[i]);
    i++;
  }
}

void Geometry::getRadii(double* radii){
  map<int, Vertex>::iterator vit;
  int i = 0;
  for(vit = Triangulation::vertexTable.begin(); 
      vit != Triangulation::vertexTable.end(); vit++){
    radii[i] = radius(vit->second);
    i++;
  }
}

void Geometry::setLengths(double* lengths){
  map<int, Edge>::iterator eit;
  int i = 0;
  for(eit = Triangulation::edgeTable.begin(); 
      eit != Triangulation::edgeTable.end(); eit++){
    setLength(eit->second, lengths[i]);
    i++;
  }
}

double Geometry::netCurvature() {
  map<int, Vertex>::iterator vit;
  double sum = 0;
  for(vit = Triangulation::vertexTable.begin(); 
      vit != Triangulation::vertexTable.end(); vit++){
    sum += curvature(vit->second);        
  }
  return sum;
}
/******************************************************************************/
