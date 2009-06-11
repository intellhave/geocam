#include <cmath>

#include "Triangulation/triangulation.h"
#include "Geometry/Geometry.h"
#include "Simplex/vertex.h"
#include "approximator.h"

void Approximator :: recordState(){  
    recordRadii();
  if(curvs3D) {
    record3DCurvs();
  } else {
    record2DCurvs();
  }
  if(areas) {
    recordAreas();
  }
  if(volumes) {
    recordVolumes();            
  }
}

void Approximator::recordRadii() {
  map<int, Vertex>::iterator vIt;    
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  for(vIt = vBegin; vIt != vEnd; vIt++){
    radiiHistory.push_back( Radius::valueAt(vIt->second) );
  }
}

void Approximator::record2DCurvs() {
  map<int, Vertex>::iterator vIt;    
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  for(vIt = vBegin; vIt != vEnd; vIt++){
     curvHistory.push_back( Curvature2D::valueAt(vIt->second));
  }    
}

void Approximator::record3DCurvs() {
  map<int, Vertex>::iterator vIt;    
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  
  for(vIt = vBegin; vIt != vEnd; vIt++){
     curvHistory.push_back( Curvature3D::valueAt(vIt->second) 
                            / Radius::valueAt(vIt->second) );
  }    
}

void Approximator::recordAreas() {
     map<int, Face>::iterator fIt;
     map<int, Face>::iterator fBegin = Triangulation::faceTable.begin();
     map<int, Face>::iterator fEnd = Triangulation::faceTable.end();
     
     for(fIt = fBegin; fIt != fEnd; fIt++) {
             areaHistory.push_back( Area::valueAt(fIt->second) );        
     }
}

void Approximator::recordVolumes() {
     map<int, Tetra>::iterator tIt;
     map<int, Tetra>::iterator tBegin = Triangulation::tetraTable.begin();
     map<int, Tetra>::iterator tEnd = Triangulation::tetraTable.end();
     
     for(tIt = tBegin; tIt != tEnd; tIt++) {
             volumeHistory.push_back( Volume::valueAt(tIt->second) );
     }  
}

void Approximator::getLatest(double radii[], double curvs[]){
  map<int, Vertex>::iterator vIt;    
  map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
  map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
  int ii = 0;
  for(vIt = vBegin; vIt != vEnd; vIt++, ii++){
    radii[ii] = Radius::valueAt(vIt->second);
    if(curvs3D) {
       curvs[ii] = Curvature3D::valueAt(vIt->second) 
                            / Radius::valueAt(vIt->second);
    } else {
        curvs[ii] = Curvature2D::valueAt(vIt->second);
    }
  }
}

void Approximator::clearHistories(){
  radiiHistory.clear();
  curvHistory.clear();
  areaHistory.clear();
  volumeHistory.clear();
}

void Approximator::run(int numSteps, double stepsize){
  map<int, Vertex> M = Triangulation::vertexTable;   
  map<int, Vertex>::iterator vBegin = M.begin();
  
  recordState();
  for(int ii = 0; ii < numSteps; ii++){
    step(stepsize);
    recordState();
  }
}

static bool isPrecise(double precision, double curvs[]){
    int numverts = Triangulation::vertexTable.size();
    
    double delta;
    bool precise = true;
    for(int ii = 0; ii < numverts; ii++){
      delta = fabs(curvs[ii] - curvs[ii]);
      precise = precise && (delta < precision);
    }     
    return precise;
}

static bool isAccurate(double accuracy, double curvs[]){
  int numVerts = Triangulation::vertexTable.size();
  double avgProp = 0;
  double norms[numVerts];
  int ii;
  
  for(ii = 0; ii < numVerts; ii++){
    norms[ii] = curvs[ii];
    avgProp += norms[ii];
  }
  avgProp = avgProp/numVerts;
 
  bool accurate = true; 
  double delta;
  for(ii = 0; ii < numVerts; ii++){
    delta = fabs(norms[ii] - avgProp);
    accurate = accurate && (delta < accuracy);
  }

  return accurate;
}


void Approximator :: run(double precision, double accuracy, double stepsize){
  if(precision <= 0 || accuracy <= 0) return;

  int numverts = Triangulation::vertexTable.size();
  double curvs[2][numverts];
  double radii[2][numverts];

  int prev = 0;
  int curr;

  recordState(); 
  getLatest(radii[prev], curvs[prev]);
  
  do{
    step(stepsize);
    recordState();
    curr = (prev + 1)%2;
    getLatest(radii[curr], curvs[curr]);
    prev = curr;
  } while(! isPrecise(precision, curvs[curr]) ||
          ! isAccurate(accuracy, curvs[curr]));
}
