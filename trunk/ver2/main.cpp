/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include "triangulationIO.h"
#include "triangulation.h"
#include "length.h"
#include "area.h"
#include "geoquant.h"
#include "triposition.h"

using namespace std;

map<int, Vertex>::iterator vit;
map<int, Edge>::iterator eit;
map<int, Face>::iterator fit;

using namespace std;

void printAreas(GQIndex& gqi){
  GeoQuant* a;
  int ii = 1;
  for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++){
    TriPosition t(AREA, 1, (fit->second).getSerialNumber());
    a = gqi[ t ];
    printf("Area %d = %f\n", ii, a->getValue()); 
    ii++; 
  }
}

void printLengths(GQIndex& gqi){
  GeoQuant* a;
  int ii = 1;
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++){
    TriPosition t(LENGTH, 1, (eit->second).getSerialNumber());
    a = gqi[ t ];
    fprintf(stdout, "Length %d = %f\n", ii, a->getValue());
    ii++; 
  }
}

void bootGeometry(GQIndex& gqi){
  fprintf(stdout, "BOOTING LENGTHS\n");
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++){
    Length* l = new Length(1.0, eit->second);
    gqi[ l->getPosition() ] = l;
  }

  fprintf(stdout, "BOOTING AREAS:\n");
  for(fit = Triangulation::faceTable.begin(); fit != Triangulation::faceTable.end(); fit++){
    Area* a = new Area(fit->second, gqi);
    gqi[ a->getPosition() ] =  a;
  }
}

int main(int argc, char *argv[]){
  fprintf(stdout, "GEOTEST STARTED\n");
  readTriangulationFile("tetrahedron.txt");
  fprintf(stdout, "TOPOLOGY LOADED\n");
  GQIndex gqi;
  bootGeometry(gqi);

  printLengths(gqi);
  printAreas(gqi);

  fprintf(stdout, "\n SCALING EDGES BY TWO \n");

  GeoQuant* l;
  for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++){
    TriPosition t(LENGTH, 1, (eit->second).getSerialNumber());
    l = gqi[ t ];
    l->setValue(2.0);
  }

  printLengths(gqi);
  printAreas(gqi);

  return 0;
  
}
