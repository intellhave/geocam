#include <cstdio>
#include <map>
#include <vector>

#include "triangulationIO.h"
#include "triangulation.h"
#include "Geometry.h"
#include "triposition.h"

using namespace std;

#define PI 3.1415926

vector<GeoQuant*> radii;
vector<GeoQuant*> etas;
vector<GeoQuant*> lengths;
vector<GeoQuant*> angles;
vector<GeoQuant*> curvatures;

void printQuants(){
  int ii;

  fprintf(stdout, "Radius Values:\t");
  for(ii = 0; ii < radii.size(); ii++){
    fprintf(stdout, " %.3f", ii, radii[ii]->getValue()); 
  }
  fprintf(stdout, "\n");


  fprintf(stdout, "Eta Values:\t");
  for(ii = 0; ii < etas.size(); ii++){
    fprintf(stdout, " %.3f", ii, etas[ii]->getValue()); 
  }
  fprintf(stdout, "\n");

  fprintf(stdout, "Length Values:\t");
  for(ii = 0; ii < lengths.size(); ii++){
    fprintf(stdout, " %.3f", ii, lengths[ii]->getValue()); 
  }
  fprintf(stdout, "\n");

  fprintf(stdout, "Angle Values:\t");
  for(ii = 0; ii < angles.size(); ii++){
    fprintf(stdout, " %.3f", ii, angles[ii]->getValue()); 
  }
  fprintf(stdout, "\n");

  fprintf(stdout, "Curvature Values:\t");
  for(ii = 0; ii < curvatures.size(); ii++){
    fprintf(stdout, " %.3f", ii, curvatures[ii]->getValue()); 
  }
  fprintf(stdout, "\n");
}

int main(int argc, char *argv[]){
  fprintf(stdout, "GEOTEST STARTED\n");
  readTriangulationFile("tetrahedron.txt");
  fprintf(stdout, "TOPOLOGY LOADED\n");

  Geometry G; 
  G.build();

  fprintf(stdout, "GEOMETRY LOADED\n");

  radii.clear();
  etas.clear();
  lengths.clear();
  angles.clear();
  curvatures.clear();

  for( GQIndex::iterator iter = G.gqi.begin(); iter != G.gqi.end(); ++iter ){
    GeoQuant* gq = iter->second;
    quantID qid = gq->getType();

    if(qid == RADIUS){
      gq->setValue(10.0);
      radii.push_back(gq);
    } else if(qid == ETA){
      gq->setValue( 1.0 );
      etas.push_back(gq);
    } else if(qid == LENGTH) {
      lengths.push_back(gq);
    } else if(qid == ANGLE) {
      angles.push_back(gq);
    } else if(qid == CURVATURE) {
      curvatures.push_back(gq);
    }
  }

  printQuants();
 
  for(int ii = 0; ii < etas.size(); ii++){
    etas[ii]->setValue(0.5);
  }
  fprintf(stdout, "\nETAs CHANGED! \n");
  printQuants();


  return 0;
}
