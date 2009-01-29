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

double plot();

using namespace std;
int main(int argc, char *argv[])
{
    char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/poincare-16.txt";
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
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
    {
       eit->second.setEta(.6+(eit->first)*.01);
       //eit->second.setEta(0.84 + i*.03);
    }




    

//    Triangulation::edgeTable[1].setEta(1.311246);
//    Triangulation::edgeTable[2].setEta();
//    Triangulation::edgeTable[3].setEta(1.00);
//    Triangulation::edgeTable[4].setEta(1.000);
//    Triangulation::edgeTable[5].setEta(1.000);
//    Triangulation::edgeTable[6].setEta(1.311246);
//    Triangulation::edgeTable[7].setEta(1.000);
//    Triangulation::edgeTable[8].setEta(1.00);
//    Triangulation::edgeTable[9].setEta(1.311246);
//    Triangulation::edgeTable[10].setEta(1.00);
//    Triangulation::edgeTable[11].setEta(1.05);
//    Triangulation::edgeTable[12].setEta(1.311246);
//    Triangulation::edgeTable[13].setEta(1.311246);
//    Triangulation::edgeTable[14].setEta(1.311246);
//    Triangulation::edgeTable[15].setEta(1.000);
//    Triangulation::edgeTable[16].setEta(1.05);
//    Triangulation::edgeTable[17].setEta(1.311246);
//    Triangulation::edgeTable[18].setEta(1.00);
//    Triangulation::edgeTable[19].setEta(1.00);
//    Triangulation::edgeTable[20].setEta(1.00);
//        Triangulation::edgeTable[21].setEta(1.311246);
//    Triangulation::edgeTable[22].setEta(1.00);
//    Triangulation::edgeTable[23].setEta(1.00);
//    Triangulation::edgeTable[24].setEta(1.000);
//    Triangulation::edgeTable[25].setEta(1.000);
//    Triangulation::edgeTable[26].setEta(1.05);
//    Triangulation::edgeTable[27].setEta(1.000);
//    Triangulation::edgeTable[28].setEta(1.00);
//    Triangulation::edgeTable[29].setEta(1.00);
//    Triangulation::edgeTable[30].setEta(1.00);
//        Triangulation::edgeTable[31].setEta(1.05);
//    Triangulation::edgeTable[32].setEta(1.00);
//    Triangulation::edgeTable[33].setEta(1.00);
//    Triangulation::edgeTable[34].setEta(1.000);
//    Triangulation::edgeTable[35].setEta(1.000);
//    Triangulation::edgeTable[36].setEta(1.05);
//    Triangulation::edgeTable[37].setEta(1.000);
//    Triangulation::edgeTable[38].setEta(1.00);
//    Triangulation::edgeTable[39].setEta(1.00);
//    Triangulation::edgeTable[40].setEta(1.00);
//        Triangulation::edgeTable[41].setEta(1.05);
//    Triangulation::edgeTable[42].setEta(1.00);
//    Triangulation::edgeTable[43].setEta(1.00);
//    Triangulation::edgeTable[44].setEta(1.000);
//    Triangulation::edgeTable[45].setEta(1.000);
//    Triangulation::edgeTable[46].setEta(1.05);
//    Triangulation::edgeTable[47].setEta(1.000);
//    Triangulation::edgeTable[48].setEta(1.00);
//    Triangulation::edgeTable[49].setEta(1.00);
//    Triangulation::edgeTable[50].setEta(1.00);
//        Triangulation::edgeTable[51].setEta(1.05);
//    Triangulation::edgeTable[52].setEta(1.00);
//    Triangulation::edgeTable[53].setEta(1.00);
//    Triangulation::edgeTable[54].setEta(1.000);
//    Triangulation::edgeTable[55].setEta(1.000);
//    Triangulation::edgeTable[56].setEta(1.05);
//    Triangulation::edgeTable[57].setEta(1.000);
//    Triangulation::edgeTable[58].setEta(1.00);
//    Triangulation::edgeTable[59].setEta(1.00);
//    Triangulation::edgeTable[60].setEta(1.00);
//        Triangulation::edgeTable[61].setEta(1.05);
//    Triangulation::edgeTable[62].setEta(1.00);
//    Triangulation::edgeTable[63].setEta(1.00);
//    Triangulation::edgeTable[64].setEta(1.000);
//    Triangulation::edgeTable[65].setEta(1.000);
//    Triangulation::edgeTable[66].setEta(1.05);
//    Triangulation::edgeTable[67].setEta(1.000);
//    Triangulation::edgeTable[68].setEta(1.00);
//    Triangulation::edgeTable[69].setEta(1.00);
//    Triangulation::edgeTable[70].setEta(1.00);
//        Triangulation::edgeTable[71].setEta(1.05);
//    Triangulation::edgeTable[72].setEta(1.00);
//    Triangulation::edgeTable[73].setEta(1.00);
//    Triangulation::edgeTable[74].setEta(1.000);
//    Triangulation::edgeTable[75].setEta(1.000);
//    Triangulation::edgeTable[76].setEta(1.05);
//    Triangulation::edgeTable[77].setEta(1.000);
//    Triangulation::edgeTable[78].setEta(1.00);
//    Triangulation::edgeTable[79].setEta(1.00);
//    Triangulation::edgeTable[80].setEta(1.00);
//        Triangulation::edgeTable[81].setEta(1.05);
//    Triangulation::edgeTable[82].setEta(1.00);
//    Triangulation::edgeTable[83].setEta(1.00);
//    Triangulation::edgeTable[84].setEta(1.000);
//    Triangulation::edgeTable[85].setEta(1.000);
//    Triangulation::edgeTable[86].setEta(1.05);
//    Triangulation::edgeTable[87].setEta(1.000);
//    Triangulation::edgeTable[88].setEta(1.00);
//    Triangulation::edgeTable[89].setEta(1.00);
//    Triangulation::edgeTable[90].setEta(1.00);
//        Triangulation::edgeTable[91].setEta(1.05);
//    Triangulation::edgeTable[92].setEta(1.00);
//    Triangulation::edgeTable[93].setEta(1.00);
//    Triangulation::edgeTable[94].setEta(1.000);
//    Triangulation::edgeTable[95].setEta(1.000);
//    Triangulation::edgeTable[96].setEta(1.05);
//    Triangulation::edgeTable[97].setEta(1.000);
//    Triangulation::edgeTable[98].setEta(1.00);
//    Triangulation::edgeTable[99].setEta(1.00);
//    Triangulation::edgeTable[100].setEta(1.00);
//        Triangulation::edgeTable[101].setEta(1.05);
//    Triangulation::edgeTable[102].setEta(1.00);
//    Triangulation::edgeTable[103].setEta(1.00);
//    Triangulation::edgeTable[104].setEta(1.000);
//    Triangulation::edgeTable[105].setEta(1.000);
   


    
 //   time_t start,end;


    double deltaEta = 0.00001;
    double deltaRadius = 0.00001;
    double a = 0.0001;
    double b = 100.00;
    
    //MinMax(deltaRadius, a, deltaEta, b);
//    MinMax(deltaEta, b);
//time (&start);
readEtas("Triangulation Files/MinMax Results/Poincare Sphere-16 Final Etas 1-29-09.txt");

plot();
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
