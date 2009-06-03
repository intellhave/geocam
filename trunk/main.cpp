/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include "3DTriangulation/3DInputOutput.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>

#include "3DTriangulation/3DTriangulationMorph.h"
#include "3DTriangulation/3Dtriangulationmath.h"

#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"
#include "triangulation/triangulationPlane.h"
#include "triangulation/smallMorphs.h"

#include "flow/approximator.h"
#include "flow/rungaApprox.h"
#include "flow/eulerApprox.h"
#include "flow/sysdiffeq.h"

#define PI 	3.141592653589793238
#include "Geometry/Geometry.h"

double plot();
double Hessian();
double Newtons_Method();
//int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution);

using namespace std;
int main(int argc, char *argv[])
{
//    char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/poincare-16.txt";
    char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/pentachron.txt";
//    char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/s3-6.txt";
//      char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/3-Torus.txt";
      
    char modified[] = "Triangulation Files/manifold converted.txt";
    
    make3DTriangulationFile(filename, modified);
    read3DTriangulationFile(modified);
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;
    
     Geometry::setDimension(ThreeD);
     Geometry::setGeometry(Euclidean);
     Geometry::build();

    
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
    {
        Geometry::setRadius(vit->second, 1.00);
    }


    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
    {
       Geometry::setEta(eit->second, 1.00);
    }

    
//void initTriangulation(){
//    map<int, Vertex>::iterator vit;
//    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
//    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
//    double radii[4] = {0.8, 0.9, 1.00, 1.1};
//    int ii = 0;
//    for(vit = vBegin; vit != vEnd; vit++, ii++)
//        vit->second.setRadius(radii[ii]);
//}


//int main(int argc, char *argv[]) {
//    char filename[] = "Triangulation Files/2D Manifolds/Standard Format/tetrahedron.txt";
//    readTriangulationFile(filename);
    

Geometry::setRadius(Triangulation::vertexTable[1],0.8);
Geometry::setRadius(Triangulation::vertexTable[2],0.5629306936);
//Geometry::setRadius(Triangulation::vertexTable[3],1.3348690690);
//Geometry::setRadius(Triangulation::vertexTable[4],1.3348699564);
//Geometry::setRadius(Triangulation::vertexTable[5],1.3348697240);


      
//      for(int i = -10; i < 10; i++)
//      {
//         Triangulation::vertexTable[1].setRadius(1 + i * 0.05);
//         printf("Cayley1: %f\n", CayleyvolumeSq(Triangulation::tetraTable[1]));
//         printf("Cayley2: %f\n", CayleyvolumeSq2(Triangulation::tetraTable[1]));
//      }
      

// specify specific eta values here for the pentachron:
//Geometry::setEta(Triangulation::edgeTable[1],2.00);
//Geometry::setEta(Triangulation::edgeTable[2],1.7);
//Geometry::setEta(Triangulation::edgeTable[3],1.5);
//Geometry::setEta(Triangulation::edgeTable[4],1.3);
//Geometry::setEta(Triangulation::edgeTable[5],1.2);
//Geometry::setEta(Triangulation::edgeTable[6],1.1);
//Geometry::setEta(Triangulation::edgeTable[7],1.9);
//Geometry::setEta(Triangulation::edgeTable[8],1.8);
//Geometry::setEta(Triangulation::edgeTable[9],1.7);
//Geometry::setEta(Triangulation::edgeTable[10],1.6);






//    char* filenames[2] = {"eulerOut.txt", "rungaOut.txt"};
//
//    
//    Approximator* app[2];
//    app[0] = new EulerApprox(AdjHypRicci);
//    app[1] = new RungaApprox(AdjHypRicci);
//    
    
 

    double a = 0.01;  //fixed length of gradient flow step
    double b = 1.0;  //gradient flow scale factor

    
//    for(int ii = 0; ii < 2; ii++){
//        initTriangulation();
//        app[ii]->run(0.001, 0.001, 0.01); // Precision + Accuracy
//        printResultsStep(filenames[ii], &(app[ii]->radiiHistory), &(app[ii]->curvHistory));               
//    }


//readEtas("Triangulation Files/MinMax Results/temp.txt");    
//    MinMax(deltaRadius, a, deltaEta, b);




//   map<int, double> deltaFE;
//   //map<int, Edge>::iterator eit;
//   //map<int, Edge>::iterator eeit;
//   //for(eeit = Triangulation::edgeTable.begin(); eeit != Triangulation::edgeTable.end(); eeit++)
//   //             {
//   //             printf("Edge %d: %.10f\n", eeit->first, eeit->second.getEta());
//   //             }
//   double initRadii[Triangulation::vertexTable.size()];
//   double dt = 0.010;
//   double accuracy = 0.0001;
//   double precision = 0.0001;
//
//   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
//       deltaFE.insert(pair<int, double>(eit->first, 0));
//   }
//   map<int, double>::iterator dfit;
//   
//   Triangulation::getRadii(initRadii);
//   yamabeFlow(dt, initRadii, accuracy, precision, true);
//calcDeltaFE(&deltaFE, deltaEta);

  
    //MinMax(deltaRadius, a, deltaEta, b);

//    MinMax(deltaEta, b, a);
      Newtons_Method();
//    Hessian();


//time (&start);
//readEtas("Triangulation Files/MinMax Results/Poincare Sphere-16 Final Etas 1-29-09.txt");

//plot();
//time (&end);
//printf("%f\n", difftime(end,start));
//

//  //  vector<double> radii;
//    //vector<double> curvs;
//    double initRadii[Triangulation::vertexTable.size()];
//    Triangulation::getRadii(initRadii);
////    printf("%f, %f, %f, %f, %f\n", initRadii[0], initRadii[1], initRadii[2], initRadii[3], initRadii[4]);
//    double dt = 0.020;
//    //int stepSize = 2;
//    double accuracy = 0.0000001;
//    double precision = 0.0000001;
//    time (&start);
//    yamabeFlow(dt, initRadii, accuracy, precision, true);
//    time (&end);

//    printf("%f\n", difftime(end,start));
//    printResultsStep("Triangulation Files/ODE Result.txt", &radii, &curvs);

//printf("%f\n", F());



    system("PAUSE");
}
