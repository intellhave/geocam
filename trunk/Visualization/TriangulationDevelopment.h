/**************************************************************
Class: TriangulationDevelopment
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 28, 2008
**************************************************************/

#include "triangulation/triangulation.h"
#include "projects/flip_2d/delaunay.h"
#include "math/miscmath.h"

#ifndef TriangulationDevelopment_H
#define TriangulationDevelopment_H

struct triangle_parts {
    int faceIndex;
    int negativity;
    float coords[3][2];
    float weight;
};

/*
 * A TriangulationDevelopment combines a triangulation with
 * points and lines to create a coordinate system that can allow
 * the triangulation to be viewed. It functions very similarly to
 * the Triangulation class, having a pointTable and lineTable.
 * However, TriangulationDevelopment does not have static
 * methods. Any methods should be accessed as follows:
 *           
 *          TriangulationDevelopment tcs; // Declaration
 *          tcs.generatePlane();          // Build system
 *          tcs.getPoint(4);
 *            :
 *            :
 * 
 * After building, a point that represents a specific vertex is
 * accessed using that vertex's index. Lines are related to edges
 * in the same way. 
 */
class TriangulationDevelopment
{    
     /*
      * Adds a Line to the lineTable.
      */
      void putLine(int, Line);
      /*
      * Adds a Point to the pointTable.
      */
      void putPoint(int, Point);
      /*
       * Clears the point and line tables.
       */
      void clearSystem();
      /*
       * Helper method for recursive generatePlane().
       */
      int generatePlaneHelper(Edge, int);
      map<int, Line> lineTable;
      map<int, Point> pointTable;
      vector<Line> dualList;
      public:
      TriangulationDevelopment();
      ~TriangulationDevelopment();
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
      /*
       * Generates a coordinate system of the current triangulation
       * on the cartesian plane. Used with TriangulationPlane to create
       * a proper flat triangulation. Each time this method is called,
       * the point and line tables are cleared and rebuilt. Every system
       * places the first line (representing edge 1) onto the the x-axis
       * with the first vertex on the origin. Therefore, flipping this
       * edge can cause a visualization of the triangulation to rotate.
       */
      void generatePlane();
      /*
       * Prints a set of coordinate points representing vertices of 
       * triangles so that the result can be printed by MatLab. The
       * information is printed as follows:
       *              
       *             1
       *             0                       0
       *             10.1365                 0
       *             3.78181                 4.28942
       *             0                       0
       *             1
       *             0                       0
       *             10.1365                 0
       *             6.97122                 -8.61989
       *             0                       0
       *
       * A triangle is represented as four points connected in order
       * where the first and last are the same. The first column is 
       * the x-coordinates and the second column is the y-coordinates.
       * Each triangle is separated by either a 1 or a -1 which tells 
       * whether or not a triangle is a "negative" triangle. A "negative"
       * triangle is filled with a color when visualizing.      
       */
      void printToFile(char*);

      /*
       *added by Kurt for use with displaying the triangulation
       *the contents of the returned vector basically match the printout of the printToFile
       *function included in this class
       */
      vector<triangle_parts> getTriangles(void);

      /*
       *returns a vector of the duals
       */
      vector<Line> getDuals(void);

      void printDuals(char*);
      /*
       * Returns the Point in the pointTable with the given index.
       */
      Point getPoint(int);
      
      /*
       * Returns the Dual of the line with the given index
       */
      Line getDual(int e);
      
      /*
       * Returns the Line in the lineTable with the given index.
       */
      Line getLine(int);
      
      void update();
      
      void addHalfDual(Edge, Face); 
      void addDual(Edge);
      void addDuals(Vertex);
      void addDuals(Face);
};

#endif // TriangulationDevelopment_H
