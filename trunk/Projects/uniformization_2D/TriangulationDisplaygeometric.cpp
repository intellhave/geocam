#include "TriangulationDisplaygeometric.h"
#include "triangulationInputOutputgeometric.h"
#include "hinge_flip.h"

TriangulationDisplay::TriangulationDisplay(char *f)
{
    showWeights = true;
    flat = true;
    voronoi = 0;
    if (readTriangulationFileWithData(f)) {

       fileName = f;
        //load up the specified file into the maps contained in triangulation.cpp

        //construct the coordinates
        coordSystem.generatePlane();
        
        //this will be used to display the triangles
        listOfTriangles = coordSystem.getTriangles();
    
        //this will be used to show which edge is currently selected
        selectedEdge = Triangulation::edgeTable.begin();

        //set f values for each vertex
        /*map<int, Vertex>::iterator vit;
        int numVerts = 0;
        for (vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
            numVerts++;
        }
        double range = sqrt(3.0/numVerts);
        srand(time(NULL));
        for (vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
            cout << "ahhhh\n";
            (vit->second).f = 2 * range * ((double) rand()/RAND_MAX) - range;
        }*/
        
    } else {
        cout << "BAD FILE NAME!!!\nPerhaps your file ends in a blank line?";
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
    if (readTriangulationFileWithData(f)) {
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

Edge TriangulationDisplay::getCurrentEdge(void) {
    return selectedEdge->second;
}

void TriangulationDisplay::setCurrentEdge(int cei) {
    map<int,Edge>::iterator eit = Triangulation::edgeTable.begin();
    while (eit != Triangulation::edgeTable.end()) {
        if (cei == (eit->first)) {
            selectedEdge = eit;
            return;
        }
        eit++;
    }
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
    return coordSystem.TriangulationDevelopment::getDuals();
}

Point TriangulationDisplay::getPoint(int index){
    return coordSystem.getPoint(index);
}

Line TriangulationDisplay::getDualAtEdge(int e) {
    return coordSystem.getDual(e);
}

