/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/
#include <cstdlib>
#include <iostream>
#include <vector>
#include "simplex/simplex.h"
#include <map>
#include <fstream>
#include <string>
#include "simplex/vertex.h"
#include <sstream>
#include "triangulation.h"
#include "triangulationmath.h"
#include "triangulationmorph.h"
#include "triangulationInputOutput.h"
#include <ctime>
#include <iomanip>
#include "makeTriangulationFile.h"

using namespace std;


int main(int argc, char *argv[])
{   
    
char from[] = "C:/Dev-Cpp/geocam/Triangulations/manifold.txt";
char to[] = "C:/Dev-Cpp/geocam/Triangulations/manifold converted.txt";
makeTriangulationFile(from, to);
   readTriangulationFile("C:/Dev-Cpp/geocam/Triangulations/manifold converted.txt");
   
   
   int faceSize = Triangulation::faceTable.size();
   
   srand(time(NULL));
   
    
    int vertexSize = Triangulation::vertexTable.size();
    double weights[vertexSize];
     for(int i = 1; i <= vertexSize; i++)
   {
          //weights[i - 1] = 1;
         weights[i - 1] = rand() % 10 + 1;
   }
   
   char fileName[] = "C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt";
   
   time_t start, end;
   time(&start);
   calcFlow(fileName, 0.04, weights,100,true);
   time(&end);
   cout << difftime(end, start) << " seconds" << endl;

   system("PAUSE");
   return 0;
}






