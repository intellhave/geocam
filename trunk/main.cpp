/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include "3DTriangulation/3DInputOutput.h"
#include <iostream>
#include <cmath>
#include "triangulation/smallMorphs.h"
#include "3DTriangulation/3DTriangulationMorph.h"
#include <ctime>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include "triangulation/smallmorphs.h"
#define PI 	3.141592653589793238

using namespace std;

int main(int argc, char *argv[])
{   
//    generateTriangulation(60);
//    
//    char from[] = "Triangulation Files/sphere3.txt";
//    char to[] = "Triangulation Files/sphere3Conv.txt";
//    make3DTriangulationFile(from, to);
//    read3DTriangulationFile(to);
//    system("PAUSE");
//    return 0;
      
//      Vertex v1(1);
//      Triangulation::putVertex(v1.getIndex(), v1);
//      Vertex v2(2);
//      Triangulation::putVertex(v2.getIndex(), v2);
//      Vertex v3(3);
//      Triangulation::putVertex(v3.getIndex(), v3);
//      Vertex v4(4);
//      Triangulation::putVertex(v4.getIndex(), v4);
//      Vertex v5(5);
//      Triangulation::putVertex(v5.getIndex(), v5);
//      
//      Edge e1 = Triangulation::edgeTable[addVertexToVertex(v1, v2)];
//      Edge e2 = Triangulation::edgeTable[addVertexToVertex(v1, v3)];
//      
//      addVertexToEdge(e1, v4);
//      addVertexToEdge(e2, v4);
//      makeFace(v2, v3, v4);
//      addVertexToEdge(e1, v5);
//      
//      write3DTriangulationFile("Triangulation Files/test.txt");
//      
//      threeOneMove(Triangulation::vertexTable[v4.getIndex()]);
      
      
      
      
      Vertex v1(1);
      Triangulation::putVertex(v1.getIndex(), v1);
      Vertex v2(2);
      Triangulation::putVertex(v2.getIndex(), v2);
      Vertex v3(3);
      Triangulation::putVertex(v3.getIndex(), v3);
      Vertex v4(4);
      Triangulation::putVertex(v4.getIndex(), v4);

      Edge e1 = Triangulation::edgeTable[addVertexToVertex(v1.getIndex(), v2.getIndex())];
      Face f1 = Triangulation::faceTable[addVertexToEdge(v3.getIndex(), e1.getIndex())];
      Tetra t1 = Triangulation::tetraTable[addVertexToFace(v4.getIndex(), f1.getIndex())];
      
      Vertex v5(5);
      Triangulation::putVertex(v5.getIndex(), v5);
      
      oneFourMove(Triangulation::tetraTable[t1.getIndex()]);
      
      write3DTriangulationFile("Triangulation Files/alien1.txt");
      
                  
/*
 * Collecting data on different 3-manifolds. Writing out results in a file
 * called "Yamabe Results.txt" in the usual place. Also, volume info is in
 * "Volume Results.txt". "ODE Results" with a 's' is the curvature-radius file.
 * My Observation: Convergent curvature decreasing depending on # of tetrahedron,
 * which is directly affecting starting volume since all radii start as 1.
 */
//    vector<double> radii;
//    vector<double> curvs;
//    
//    time_t start, end;
//    
//    make3DTriangulationFile("Triangulation Files/sphere3.txt",
//                            "Triangulation Files/manifold converted.txt");
//    read3DTriangulationFile("Triangulation Files/manifold converted.txt");
//    write3DTriangulationFile("Triangulation Files/manifold.txt");

//    vector<double> radii;
//    vector<double> curvs;
//    
//    time_t start, end;
//    
//    make3DTriangulationFile("Triangulation Files/sphere3.txt",
//                            "Triangulation Files/manifold converted.txt");
//    read3DTriangulationFile("Triangulation Files/manifold converted.txt");
//    write3DTriangulationFile("Triangulation Files/manifold.txt");
//
//    srand(time(NULL));
//    double weights[Triangulation::vertexTable.size()];
//    for(int i = 0; i < Triangulation::vertexTable.size(); i++) {
////            if(i >= 3 && i <= 12 || i == 14)
////            {
////                 weights[i] = 6;
////            }
////            else { weights[i] = 1; }
//            weights[i] = 1;
//            //weights[i] = 1.5 + (rand() % 100) / 100.0;
//    }
//    double dt = 0.03;
//    int numSteps = 200;
//    
//    time(&start);
//    yamabeFlow(&radii, &curvs, dt, weights, numSteps, false);
//    time(&end);
//    printResultsStep("Triangulation Files/ODE Results.txt", &radii, &curvs);
//    cout << "Elapsed time was " << difftime(end, start) << " seconds.\n";
//    srand(time(NULL));
//    double weights[Triangulation::vertexTable.size()];
//    for(int i = 0; i < Triangulation::vertexTable.size(); i++) {
//    double weights[16] =
//    {2.73186,6.06383,6.74304,6.77510,6.77500,6.73368,6.73483,6.60649,6.60666,
//     6.64489,6.64364,6.65459,5.96322,6.64632,5.51331,5.51328};
//    
//            if(i >= 3 && i <= 12 || i == 14)
//            {
//                 weights[i] = 6;
//            }
//            else { weights[i] = 1; }
//            weights[i] = 1;
//            //weights[i] = 1.5 + (rand() % 100) / 100.0;
//    }
//    double dt = 0.03;
//    int numSteps = 500;
//    
//    time(&start);
//    yamabeFlow(&radii, &curvs, dt, weights, numSteps, false);
//    time(&end);
//    printResultsStep("Triangulation Files/ODE Results.txt", &radii, &curvs);
//    cout << "Elapsed time was " << difftime(end, start) << " seconds.\n";

//            weights[i] = 1;
            //weights[i] = 20 + (rand() % 100) / 20.0;
//    }


//    double dt = 0.03;
//    int numSteps = 200;
//    
//    time(&start);
//    yamabeFlow(&radii, &curvs, dt, weights, numSteps, true);
//    time(&end);
//    printResultsStep("Triangulation Files/ODE Results.txt", &radii, &curvs);
//    cout << "Elapsed time was " << difftime(end, start) << " seconds.\n";












    system("PAUSE");
    return 0;
}
