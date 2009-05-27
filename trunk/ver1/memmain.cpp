/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include <iostream>
#include <cstdio>

#include "vertex.h"
#include "triangulation.h"

using namespace std;

int main(int argc, char *argv[]){
  int bound = atoi(argv[1]);
  printf("Main is residing in memory near : %p\n", &bound);
  printf("Vertex Table resides in memory : %p\n",  &(Triangulation::vertexTable));
  printf("Constructing %d vertex objects, stashing them in the Triangulation.\n", bound);

  for(int ii = 1; ii <= bound; ii++){
    Vertex v = Vertex();
    Triangulation::putVertex(ii, v);
    if(ii % 1000 == 0){
      printf("Constructed %d vertex objects so far.\n", ii);
      printf("The vertex table holds %d objects.\n", Triangulation::vertexTable.size());
      printf("New vertex resides in memory %p\n", &v);
    }
  }

  map<int, Vertex>::iterator vit = Triangulation::vertexTable.begin();

  int counter = 1;
  while(vit != Triangulation::vertexTable.end()){
    if(counter % 1000 == 0)
      printf("Inspecting vertex %d. This vertex resides at %p\n", counter, &(vit->second));
    vit++; counter++;
  }
  return 0;
}
