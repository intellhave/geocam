#include <cmath>

#include "Triangulation/triangulation.h"
#include "Simplex/vertex.h"
#include "approximator.h"

void Approximator :: recordState(){
  int vertCount = Triangulation::vertexTable.size();
    
  map<int, Vertex> M = Triangulation::vertexTable;
  map<int, Vertex>::iterator vIt;    
  map<int, Vertex>::iterator vBegin = M.begin();
  map<int, Vertex>::iterator vEnd = M.end();

  Vertex vert;
  
  for(vIt = vBegin; vIt != vEnd; vIt++){
    vert = vIt->second;
    radiiHistory.push_back( vert.getRadius() );  
    curvHistory.push_back( curvature(vert) );
  }
}

void Approximator :: getLatest(double radii[], double curvs[]){
  int vertCount = Triangulation::vertexTable.size();  
  int start = radiiHistory.size() - vertCount;
  int end = radiiHistory.size();

  for(int kk = start ; kk < end; kk++){
    radii[kk - start] = radiiHistory[kk];
    curvs[kk - start] = curvHistory[kk];
  }
}

void Approximator :: clearHistories(){
  radiiHistory.clear();
  curvHistory.clear();
}

void Approximator :: run(int numSteps, double stepsize){
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
