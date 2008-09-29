/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include <iostream>
#include <cmath>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dinputoutput.h"
#define PI 	3.141592653589793238

using namespace std;

int main(int argc, char *argv[])
{
    char from[] = "Triangulation Files/sphere3.txt";
    char to[] = "Triangulation Files/sphere3Conv.txt";
    make3DTriangulationFile(from, to);
    read3DTriangulationFile(to);
    system("PAUSE");
    return 0;
}
