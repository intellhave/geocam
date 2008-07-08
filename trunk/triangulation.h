/**************************************************************
Class: Triangulation
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 25, 2008
**************************************************************/

#ifndef TRIANGULATION_H
#define TRIANGULATION_H


#include <cstdlib>
#include <vector>
#include <map>
#include "simplex/vertex.h"
#include "simplex/edge.h"
#include "simplex/face.h"
#include "hyperbolic/hyperbolicmath.h"

/*
 * The Triangulation class stores the information about the running
 * triangulation. A triangulation has three static public members:
 *                 
 *                vertexTable, edgeTable, faceTable
 * 
 * All three are maps from integer values to the respective simplex.
 * These maps are expected to be used constantly to access the 
 * elements of the triangulation. The triangulation class is designed
 * to only exist as one instance during the running of the program.
 * Since it is used in virtually every function call, this avoids the
 * need of passing a pointer along a chain of function calls.
 */
class Triangulation
{
	public:
	// class constructor
		Triangulation();
	// class destructor
		~Triangulation();
      static map<int, Vertex> vertexTable;
      static map<int, Edge> edgeTable;
      static map<int, Face> faceTable;
      
      /*
       * Returns the largest integer in the vertex table.
       */
      static int greatestVertex();
      /*
       * Returns the largest integer in the edge table.
       */
      static int greatestEdge();
      /*
       * Returns the largest integer in the face table.
       */
      static int greatestFace();
	
	/*
	 * Adds a mapping from the given integer to the given Vertex in
	 * the vertexTable. Called statically.
	 */
     static void putVertex(int, Vertex);
     /*
	 * Adds a mapping from the given integer to the given Edge in the
     * edgeTable. Called statically.
	 */
     static void putEdge(int, Edge);
    /*
	 * Adds a mapping from the given integer to the given Face in the
     * faceTable. Called statically.
	 */
     static void putFace(int, Face);
     
     /*
      * Removes the mapping from the given integer to its referenced vertex.
      * It also removes all local references to the integer in any simplices.
      */
     static void eraseVertex(int);
     
     /*
      * Removes the mapping from the given integer to its referenced edge.
      * It also removes all local references to the integer in any simplices.
      */
     static void eraseEdge(int);
     
     /*
      * Removes the mapping from the given integer to its referenced face.
      * It also removes all local references to the integer in any simplices.
      */
     static void eraseFace(int);

     /*
      * Calculates the sum of the curvatures over all vertices.
      */
     static double netCurvature();
     static double netSphericalCurvature();
     static double netHyperbolicCurvature();
     static void setWeights(double*);
     static void getWeights(double*);
};



#endif // TRIANGULATION_H
