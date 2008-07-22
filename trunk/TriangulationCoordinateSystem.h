#include "pointlinecircle.h"
#include "triangulation.h"

#ifndef TRIANGULATIONCOORDINATESYSTEM_H
#define TRIANGULATIONCOORDINATESYSTEM_H
class TriangulationCoordinateSystem
{
      map<int, Line> lineTable;
      map<int, Point> pointTable;
      int generatePlaneHelper(Edge, int);
      public:
      TriangulationCoordinateSystem();
      ~TriangulationCoordinateSystem();
      void putLine(int, Line);
      void putPoint(int, Point);
      bool containsLine(int);
      bool containsPoint(int);
      void generatePlane();
      void printToFile(char*);
      Point getPoint(int);
      Line getLine(int);
};

#endif // TRIANGULATIONCOORDINATESYSTEM_H
