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
#include "triangulation/smallMorphs.h"
#include "3DTriangulation/3DTriangulationMorph.h"
#include <ctime>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"
#define PI 	3.141592653589793238

//double plot();
double Hessian();

using namespace std;
int main(int argc, char *argv[])
{
    char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/pentachron.txt";
    //char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/s3-6.txt";
    char modified[] = "Triangulation Files/manifold converted.txt";
    
    make3DTriangulationFile(filename, modified);
    read3DTriangulationFile(modified);
    map<int, Vertex>::iterator vit;
    map<int, Edge>::iterator eit;
    for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
    {
        vit->second.setRadius(1.00);

        //Triangulation::vertexTable[i].setRadius((0.4 + i/5.0));
    }
    
//    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
//    {
//       eit->second.setEta(1.00);
       //eit->second.setEta(0.84 + i*.03);
//    }
    
//    for(int i = -10; i < 10; i++)
//    {
//      //Triangulation::vertexTable[1].setRadius(1 + i * 0.05);
//      //double vol = volumeSq(Triangulation::tetraTable[1]);
//      double cay = CayleyvolumeSq(Triangulation::tetraTable[1]);
//     // printf("VolumeSq: %f\n", vol);
//      printf("CayleyVolumeSq: %f\n", cay);
//      //printf("Ratio: %f\n\n", vol / cay);
//      
//    }
//    for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
//    {
//        if(Triangulation::edgeTable[i].getLocalFaces()->size() == 4)
//        {
//           Triangulation::edgeTable[i].setEta(1.311246);
//        } else {
//           Triangulation::edgeTable[i].setEta(0.733409);
//        }
//        //Triangulation::edgeTable[i].setEta(1.00);
////        //Triangulation::edgeTable[i].setEta(0.84 + i*.03);
////        
//    }
    
//Triangulation::vertexTable[1].setRadius(0.5629304134);
//Triangulation::vertexTable[2].setRadius(0.5629306936);
//Triangulation::vertexTable[3].setRadius(1.3348690690);
//Triangulation::vertexTable[4].setRadius(1.3348699564);
//Triangulation::vertexTable[5].setRadius(1.3348697240);



//
Triangulation::edgeTable[1].setEta(1.050);
Triangulation::edgeTable[2].setEta(1.05);
Triangulation::edgeTable[3].setEta(1.00);
Triangulation::edgeTable[4].setEta(1.00);
Triangulation::edgeTable[5].setEta(1.00);
Triangulation::edgeTable[6].setEta(1.00);
Triangulation::edgeTable[7].setEta(1.00);
Triangulation::edgeTable[8].setEta(1.00);
Triangulation::edgeTable[9].setEta(1.00);
Triangulation::edgeTable[10].setEta(1.00);

//    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
//    {
//       eit->second.setEta(.6+(eit->first)*.01);
//       //eit->second.setEta(0.84 + i*.03);
//    }

    
//   time_t start,end;
//      FILE* file = fopen("Triangulation Files/dummy.txt", "w");
//      for(int i = 0; i < 100; i++)
//      {
//          fprintf(file, "%.8f\n", cos((double) i));
//      }
//      fclose(file);
//      system("PAUSE");

    double deltaEta = 0.0001;
    double deltaRadius = 0.00001;
    double a = 0.01;  //fixed length of gradient flow step
    double b = 10.0;  //gradient flow scale factor
    
//readEtas("Triangulation Files/MinMax Results/temp.txt"); 
//    
////readEtas("Triangulation Files/MinMax Results/temp.txt");    
//    //MinMax(deltaRadius, a, deltaEta, b);
    MinMax(deltaEta, b, a);


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
    //MinMax(deltaEta, b, a);
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
