#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include "3DInputOutput.h"
#include "triangulation/triangulation.h"
#include "Pipelined_Newtons_Method.h"

#include "radius.h"
#include "eta.h"

using namespace std;

void init_quantities( char* param_filename ){
  FILE* paramfile = fopen( param_filename, "r");

  fprintf( stdout, "Reading from file: %s\n", param_filename ); 

  if( paramfile == NULL ){
    fprintf( stderr, "Error opening parameter file!\n");
    exit(1);
  }
 
  int vertSize = Triangulation::vertexTable.size();
  int edgeSize = Triangulation::edgeTable.size();

  double radius; 
  int check;

  fprintf( stdout, "Input Radii: ");
  for(int i = 1; i <= vertSize; i++) {
    check = fscanf( paramfile, "%lf", &radius );

    if(check != 1){
      fprintf( stderr, "Error reading radii in parameter file!\n");
      exit(1);
    }
    Radius::At( Triangulation::vertexTable[i] )->setValue( radius );        
    fprintf( stdout, " %lf", radius );
  }
  
  fprintf( stdout, "\nInput Etas: ");

  double eta;
  for(int i = 1; i <= edgeSize; i++) {
    check = fscanf( paramfile, "%lf", &eta );

    if(check != 1){
      fprintf( stderr, "Error reading etas in parameter file!\n");
      exit(1);
    }
    Eta::At( Triangulation::edgeTable[i] )->setValue(eta);
    fprintf( stdout, " %lf", eta );
  }
  
  fprintf( stdout, "\n" );
  fclose( paramfile );
}


void printRadii(){
  int vertSize = Triangulation::vertexTable.size();
  fprintf(stdout, "Radii: ");
  for(int i = 1; i <= vertSize; i++) {
    double r_val = Radius::At( Triangulation::vertexTable[i] )->getValue();        
    fprintf( stdout, " %lf", r_val );
  }
  fprintf(stdout, "\n");
}


int main(int argc, char** argv){      
  if( argc < 4 ){
    fprintf( stderr, "USAGE: newtons [ manifold file ] [ parameter-file (radii + etas) ] [ threshold ]\n");
    exit( 1 );
  }

  char* from = argv[1];
  char* to = "manifold_converted.txt";

  make3DTriangulationFile(from, to);
  read3DTriangulationFile(to);
  fprintf( stdout, "Loaded triangulation.\n");

  char* parameterFile = argv[2];
  init_quantities( parameterFile );
  fprintf( stdout, "Initialized Etas and Radii.\n");
  
  double thresh = atof( argv[3] );

  Newtons_Method( thresh );
  printRadii();

  return 0;
}
