#include <cmath>
#include <iostream>
#include "delaunay.h"
#include "triangulation/triangulationmorph.h"
#include "triangulation/triangulationInputOutput.h"
#include "triangulation/TriangulationCoordinateSystem.h"

class TriangulationDisplay {

    TriangulationCoordinateSystem coordSystem;

    //this string should be the path to the file that contains the triangulation information
    char* fileName;

    vector<triangle_parts> listOfTriangles;

    Edge* selectedEdge;

    public:
    TriangulationDisplay(char* f);
    ~TriangulationDisplay(void);

    void changeFileTo(char * f);

    char* getCurrentFile(void);

    Edge nextEdge(void);

    Edge previousEdge(void);

    Edge flipSelectedEdge(void);

    vector<triangle_parts> getTriangles(void);
};
