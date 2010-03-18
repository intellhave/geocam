#include <cmath>
#include <iostream>
#include <cstdlib>
#include <string.h>
#include "delaunay.h"
#include "triangulationmorph.h"
#include "triangulationInputOutput.h"
#include "TriangulationDevelopment.h"

#ifdef __APPLE__
#include <OpenGL/OpenGL.h>
#include <GLUT/glut.h>
#else
#include <GL/glut.h>
#endif

#ifndef Display_H
#define Display_H

/*
 * TriangulationDisplay is a namespace containing many global variables,
 *  and function used by opengl, and code using opengl, the design of
 *  opengl lends itself to a more C style of programming without object
 *  orientation. To reduce complications of having a class interact and
 *  be heavily dependent on global state, this namespace has been created
 *  to allow for a class-like separation of global variables from the std
 *  namespace where user code typically resides, while keeping the graphics
 *  code organized in a reasonable way.
 *
 *
 *  Functions of Note:
 *
 *    ShowTriangulation()
 *    ShowTriangulation(char * f)
 *    ShowTriangulation(char * f, int argc, char * argv[])
 *
 *    these can be used to view a 2D triangulation
 *
 */
namespace TriangulationDisplay {


//opengl functions
void startGLStuff(int argc, char * argv[]);
void display(void);
void handleKeypress(unsigned char key, int x, int y);
void handleSpecialKeypress(int key, int x, int y);
void initRendering(void);
void handleResize(int w, int h);
void drawTicks(double x1, double y1, double x2, double y2, double x3, double y3, int depth);
void drawSceneFlatAlt(void);
void drawSceneFlat(void);
void drawSceneStacked(void);
void drawScene(void);
void setup_view_parameters(void);

//old TriangulationDisplay area below here

void ShowTriangulation(void);
void ShowTriangulation(char * f);
void ShowTriangulation(char* f, int argc, char * argv[]);

//changes the file that the triangulation represents and should reset everything
//within this class to represent that change (i.e. the coordSystem should be updated)
void setFile(char * f);

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

}//end of namespace Display

#endif
