/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/
#include <cstdlib>
#include <iostream>
#include <vector>
#include "simplex.h"
#include <map>
#include <fstream>
#include <string>
#include "vertex.h"
#include <sstream>
#include "triangulation.h"
#include "triangulationmath.h"
#include "triangulationmorph.h"
#include "triangulationInputOutput.h"

using namespace std;


int main(int argc, char *argv[])
{   
    
   readTriangulationFile("c:/Documents and Settings/student/Desktop/Triangulations/torus.txt");
   int vertexSize = Triangulation::vertexTable.size();
   int edgeSize = Triangulation::edgeTable.size();
   double weights[vertexSize];
   double weightsChange[vertexSize];
   double curvatures[vertexSize];
   for(int i = 1; i <= vertexSize; i++)
   {
           weights[i - 1] = i;
   }
   


      calcFlow(0, 10, weights,100, 1, true);
     
   system("PAUSE");
   return 0;
}






