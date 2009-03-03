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
double Hessian();

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
    
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
    {
       eit->second.setEta(1.00);
       //eit->second.setEta(0.84 + i*.03);
    }
  
// specify specific eta values here for the pentachron:
//Triangulation::edgeTable[1].setEta(1.05);
//Triangulation::edgeTable[2].setEta(1.00);
//Triangulation::edgeTable[3].setEta(1.00);
//Triangulation::edgeTable[4].setEta(1.00);
//Triangulation::edgeTable[5].setEta(1.00);
//Triangulation::edgeTable[6].setEta(1.00);
//Triangulation::edgeTable[7].setEta(1.00);
//Triangulation::edgeTable[8].setEta(1.00);
//Triangulation::edgeTable[9].setEta(1.00);
//Triangulation::edgeTable[10].setEta(1.00);

    double deltaEta = 0.000000001;
    double deltaRadius = 0.00001;

    double a = 0.010;  //fixed length of gradient flow step
    double b = 0.1;  //gradient flow scale factor
       
    readEtas("Triangulation Files/MinMax Results/temp.txt");    
    MinMax(deltaEta, b, a);

//    Hessian();

    system("PAUSE");
}
