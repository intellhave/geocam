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


void initTriangulation(){
    map<int, Vertex>::iterator vit;
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();

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
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
    {
       eit->second.setEta(1.00);
       //eit->second.setEta(0.84 + i*.03);
    }


    
    for(int ii = 0; ii < 2; ii++){
        initTriangulation();
        app[ii]->run(0.001, 0.001, 0.01); // Precision + Accuracy
        printResultsStep(filenames[ii], &(app[ii]->radiiHistory), &(app[ii]->curvHistory));               
    }

    system("PAUSE");
}
