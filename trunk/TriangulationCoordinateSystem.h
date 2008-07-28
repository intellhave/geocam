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
 * the Triangulation class, having a pointTable and lineTable.
 * However, TriangulationCoordinateSystem does not have static
 * methods. Any methods should be accessed as follows:
 *           
 *          TriangulationCoordinateSystem tcs; // Declaration
 *          tcs.generatePlane();          // Build system
 *          tcs.getPoint(4);
 *            :
 *            :
 * 
 * After building, a point that represents a specific vertex is
 * accessed using that vertex's index. Lines are related to edges
 * in the same way. 
 */
class TriangulationCoordinateSystem
{     
      void putLine(int, Line);
      void putPoint(int, Point);
      void clearSystem();
      int generatePlaneHelper(Edge, int);
      map<int, Line> lineTable;
      map<int, Point> pointTable;
      public:
      TriangulationCoordinateSystem();
      ~TriangulationCoordinateSystem();
      /*
       * Returns true if the coordinate system contains the Line
       * given by its index.
       */
      bool containsLine(int);
       /*
       * Returns true if the coordinate system contains the Point
       * given by its index.
       */
      bool containsPoint(int);
      void generatePlane();
      void printToFile(char*);
      Point getPoint(int);
      Line getLine(int);
};

#endif // TRIANGULATIONCOORDINATESYSTEM_H
