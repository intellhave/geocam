/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/
#include "3DTriangulation\3DInputOutput.h"
#include <iostream>
#include <cmath>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dinputoutput.h"
#include "triangulation/smallmorphs.h"
#define PI 	3.141592653589793238

using namespace std;

int main(int argc, char *argv[])
{   
    readTriangulationFile("Triangulation Files/Tetrahedron.txt");
    oneThreeMove(Triangulation::faceTable[1]);
    writeTriangulationFile("Triangulation Files/test.txt");


    system("PAUSE");
    return 0;
}
