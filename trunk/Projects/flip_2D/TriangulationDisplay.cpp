#include "TriangulationDisplay.h"
#include "hinge_flip.h"

TriangulationDisplay::TriangulationDisplay(char *f)
{
    showWeights = false;
    flat = 0;
    tickMarks = false;
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
        
        //set up the vertex coordinates just this once
        makePoints();
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

void TriangulationDisplay::reGeneratePlane() {
  coordSystem.generatePlane();
  listOfTriangles = coordSystem.getTriangles();
  selectedEdge = Triangulation::edgeTable.begin();
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

void TriangulationDisplay::makePoints(void) {
  map<int, Vertex>::iterator vit;
  vit = Triangulation::vertexTable.begin();
  for (; vit != Triangulation::vertexTable.end(); vit++) {
    Point * p = new Point(coordSystem.getPoint(vit->first).x, coordSystem.getPoint(vit->first).y);
    points.insert(pair<int, Point>(vit->first, *p));
  }
}

Point TriangulationDisplay::getVertexCoords(int index) {
  return points[index];
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
    return coordSystem.TriangulationDevelopment::getDuals();
}

Point TriangulationDisplay::getPoint(int index){
    return coordSystem.getPoint(index);
}

Line TriangulationDisplay::getDualAtEdge(int e) {
    return coordSystem.getDual(e);
}

