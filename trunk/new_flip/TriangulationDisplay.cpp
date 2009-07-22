#include "new_flip/TriangulationDisplay.h"
#include "new_flip/flip_algorithm.h"

TriangulationDisplay::TriangulationDisplay(char *f)
{
    showWeights = false;
    voronoi = 0;
    if (readTriangulationFile(f)) {
        fileName = f;
        //load up the specified file into the maps contained in triangulation.cpp

        //construct the coordinates
        coordSystem.generatePlane();

        //this will be used to display the triangles
        listOfTriangles = coordSystem.getTriangles();
    
        //this will be used to show which edge is currently selected
        selectedEdge = Triangulation::edgeTable.begin();
    } else {
        cout << "BAD FILE NAME!!!\n";
        system("PAUSE");
    }

}

TriangulationDisplay::TriangulationDisplay()
{
}

TriangulationDisplay::~TriangulationDisplay()
{
}

void TriangulationDisplay::changeFileTo(char* f)
{
    //load up the specified file into the maps contained in triangulation.cpp
    if (readTriangulationFile(f)) {
        fileName = f;
        //construct a
        coordSystem.generatePlane();

        listOfTriangles = coordSystem.getTriangles();
    } else {
        readTriangulationFile(fileName);
        //construct a
        coordSystem.generatePlane();

        listOfTriangles = coordSystem.getTriangles();
    }
}

char * TriangulationDisplay::getCurrentFile()
{
    return fileName;
}

void TriangulationDisplay::update() {
    coordSystem.update();
    listOfTriangles = coordSystem.getTriangles();
}

Line TriangulationDisplay::currentEdgeToLine() {
    int edgeIndex;
    edgeIndex = (selectedEdge->second).getIndex();
    return coordSystem.getLine(edgeIndex);
}

Edge TriangulationDisplay::currentEdge(void) {
    return selectedEdge->second;
}

Edge TriangulationDisplay::nextEdge(void) {
    selectedEdge++;
    if (Triangulation::edgeTable.end() == selectedEdge) {
        selectedEdge = Triangulation::edgeTable.begin();
    }
    return selectedEdge->second;
}

Edge TriangulationDisplay::previousEdge(void) {
    if (Triangulation::edgeTable.begin() == selectedEdge) {
        selectedEdge = Triangulation::edgeTable.end();
        selectedEdge--;
    } else {
        selectedEdge--;
    }
    return selectedEdge->second;
}

void TriangulationDisplay::flipCurrentEdge() {
    flip(Triangulation::edgeTable[(selectedEdge->second).getIndex()]);
}

vector<triangle_parts> TriangulationDisplay::getTriangles(void) {
    return listOfTriangles;
}

vector<Line> TriangulationDisplay::getDuals(void) {
    //map<int, Edge>::iterator eit;
    //for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
    //    coordSystem.addDual(eit->second);
    //}
    return coordSystem.TriangulationCoordinateSystem::getDuals();
}

Point TriangulationDisplay::getPoint(int index){
    return coordSystem.getPoint(index);
}

