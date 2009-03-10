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

double plot();
double Hessian();

using namespace std;
<<<<<<< .mine
=======
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
>>>>>>> .r658
<<<<<<< .mine
void initTriangulation(){
    map<int, Vertex>::iterator vit;
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
=======
        //Triangulation::vertexTable[i].setRadius((0.4 + i/5.0));
    }
>>>>>>> .r658
    
<<<<<<< .mine
    double radii[4] = {0.8, 0.9, 1.00, 1.1};
    int ii = 0;
    for(vit = vBegin; vit != vEnd; vit++, ii++)
        vit->second.setRadius(radii[ii]);
}

int main(int argc, char *argv[]) {
    char filename[] = "Triangulation Files/2D Manifolds/Standard Format/tetrahedron.txt";
    readTriangulationFile(filename);
    
    char* filenames[2] = {"eulerOut.txt", "rungaOut.txt"};
    
    Approximator* app[2];
    app[0] = new EulerApprox(AdjHypRicci);
    app[1] = new RungaApprox(AdjHypRicci);
=======
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
>>>>>>> .r658
<<<<<<< .mine
    
    for(int ii = 0; ii < 2; ii++){
        initTriangulation();
        app[ii]->run(0.001, 0.001, 0.01); // Precision + Accuracy
        printResultsStep(filenames[ii], &(app[ii]->radiiHistory), &(app[ii]->curvHistory));               
    }
=======
       
    readEtas("Triangulation Files/MinMax Results/temp.txt");    
    MinMax(deltaEta, b, a);

//    Hessian();

    system("PAUSE");
>>>>>>> .r658
}
