#include "new_flip/TriangulationDisplay.h"

TriangulationDisplay::TriangulationDisplay(char *f)
{
    fileName = f;

    //load up the specified file into the maps contained in triangulation.cpp
    readTriangulationFile(fileName);

    //construct a
    coordSystem.generatePlane();

    listOfTriangles = coordSystem.getTriangles();

}

TriangulationDisplay::~TriangulationDisplay()
{
}

void TriangulationDisplay::changeFileTo(char* f)
{
    fileName = f;

    //load up the specified file into the maps contained in triangulation.cpp
    readTriangulationFile(fileName);

    //construct a
    coordSystem.generatePlane();

    listOfTriangles = coordSystem.getTriangles();
}

char * TriangulationDisplay::getCurrentFile()
{
    return fileName;
}
