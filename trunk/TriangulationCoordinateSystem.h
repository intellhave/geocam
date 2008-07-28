/**************************************************************
Class: TriangulationCoordinateSystem
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
**************************************************************/
#include "pointlinecircle.h"
#include "triangulation.h"

#ifndef TRIANGULATIONCOORDINATESYSTEM_H
#define TRIANGULATIONCOORDINATESYSTEM_H

/*
 * A TriangulationCoordinateSystem combines a triangulation with
 * points and lines to create a coordinate system that can allow
 * the triangulation to be viewed. It functions very similarly to
 * the Triangulation class, 
 */
class TriangulationCoordinateSystem
{
      int generatePlaneHelper(Edge, int);
      public:
      TriangulationCoordinateSystem();
      ~TriangulationCoordinateSystem();
      map<int, Line> lineTable;
      map<int, Point> pointTable;
      void putLine(int, Line);
      void putPoint(int, Point);
      bool containsLine(int);
      bool containsPoint(int);
      void clearSystem();
      void generatePlane();
      void printToFile(char*);
      Point getPoint(int);
      Line getLine(int);
};

#endif // TRIANGULATIONCOORDINATESYSTEM_H
