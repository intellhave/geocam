#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include "3DInputOutput.h"
#include "triangulation.h"
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


int main(int argc, char** argv){      
  if( argc < 5 ){
    fprintf( stderr, "USAGE: pnewtons [ manifold file ] [ parameter-file (radii + etas) ] [ # steps ] [ # trials ]\n");
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
  
  int edgeSize = Triangulation::edgeTable.size();
  Eta* etas[ edgeSize ];
  for(int ii = 1; ii <= edgeSize; ii++) {
    etas[ii-1] = Eta::At( Triangulation::edgeTable[ii] );
  }

  int vertSize = Triangulation::vertexTable.size();
  Radius* radii[ vertSize ];
  for(int ii = 1; ii <= vertSize; ii++) {
    radii[ii-1] = Radius::At( Triangulation::vertexTable[ii] );        
  }

  int steps = atoi( argv[3] );
  int trials = atoi( argv[4] );

  while( trials > 0 ){
    fprintf(stdout, "##############################################\n");
    fprintf(stdout, "Running Trial #%d:\n\n", trials);
    fprintf(stdout, "##############################################\n");

    etas[0]->setValue(2.0/trials);
    for(int ii = 1; ii < edgeSize; ii++){
      etas[ii]->setValue(1.0/trials);
    }
    
    radii[0]->setValue(8.0);
    radii[1]->setValue(9.0);
    for(int jj = 2; jj < vertSize; jj++){
      radii[jj]->setValue(10.0);
    }

    Newtons_Method( steps );
    trials--;
  }
  
  return 0;
}
