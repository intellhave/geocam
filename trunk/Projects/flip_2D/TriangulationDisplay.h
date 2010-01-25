#include <cmath>
#include <iostream>
#include <cstdlib>
#include "delaunay.h"
#include "triangulationmorph.h"
#include "triangulationInputOutput.h"
#include "TriangulationDevelopment.h"

class TriangulationDisplay {

    TriangulationDevelopment coordSystem;

    //this string should be the path to the file that contains the triangulation information
    char* fileName;

    vector<triangle_parts> listOfTriangles;

    map<int,Edge>::iterator selectedEdge;

    map<int, Point> points;

    public:

    int flat;
    bool showWeights;
    bool tickMarks;
    int voronoi; // 0 is nothing, 1 is the whole voronoi diagram, 2 is the hinge only

    TriangulationDisplay(void);
    TriangulationDisplay(char* f);
    ~TriangulationDisplay(void);

    //changes the file that the triangulation represents and should reset everything
    //within this class to represent that change (i.e. the coordSystem should be updated)
    void changeFileTo(char * f);

    void reGeneratePlane();

    char* getCurrentFile(void);

    //the line object representing the currently selected edge
    Line currentEdgeToLine();
    
    //returns the currently selected edge
    Edge getCurrentEdge(void);
    
    void setCurrentEdge(int cei);

    //increments the currently selected edge to another edge in the triangulations
    Edge nextEdge(void);

    //decrements the currently selected edge, returning to the previously selected edge
    Edge previousEdge(void);

    //calls the flip function on the currently selected edge
    void flipCurrentEdge(void);

    //returns a vector of structs containing the information necessary for
    //displaying the triangulation in opengl
    vector<triangle_parts> getTriangles(void);
    
    //return a vector of the duals
    vector<Line> getDuals(void);
    
    Line getDualAtEdge(int e);
    
    Point getPoint(int index);
    
    //updates the underlying coordinateSystem object to represent the current state
    //of the triangulation
    void update();

        //populates the Points map with index -> Point pairs
    void makePoints(void);

    Point getVertexCoords(int index);

};
