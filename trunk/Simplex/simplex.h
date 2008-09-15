/**************************************************************
Class: Simplex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#ifndef SIMPLEX_H
#define SIMPLEX_H
#include <vector>
#include <cstdlib>


using namespace std;

/*
 * The Simplex class represents the base class for what all simplices
 * have in common. They all have sets of local simplices of different
 * dimensions. In this instance, local means simplices connected in
 * some way to that simplex. For example, the local vertices of a
 * vertex represents the set of vertices that are connected to this
 * vertex by an edge.
 *
 * These local simplices are stored as a list of integers. The
 * integers representing the simplices are just that, a mapping to
 * that simplex where the tables are stored in the Triangulation.
 */
class Simplex
{
      int index;
      vector<int> localVertices;
      vector<int> localEdges;
      vector<int> localFaces;
      vector<int> localTetras;
	public:
		// constructor
		Simplex();
		Simplex(int);
		// destructor
		~Simplex();
	 
	 int getIndex();
     /* Adds a vertex represented by an integer to the list of local 
      * vertices.
	  */
     void setIndex(int);
	  void addVertex(int);
     /* Adds an edge represented by an integer to the list of local 
      * edges.
	  */
	  void addEdge(int);
     /* Adds a face represented by an integer to the list of local 
      * faces.
	  */
	  void addFace(int);
	  
	  void addTetra(int);
	  
	  void removeVertex(int);
	  
	  void removeEdge(int);
	  
	  void removeFace(int);
	  
	  void removeTetra(int);
	  
	  bool isAdjVertex(int);
	  bool isAdjEdge(int);
	  bool isAdjFace(int);
	  bool isAdjTetra(int);
	  
     /* Returns a pointer to the list of local vertices.
	  */
	  vector<int>* getLocalVertices();
     /* Returns a pointer to the list of local edges.
	  */
	  vector<int>* getLocalEdges();
     /* Returns a pointer to the list of local faces.
	  */
	  vector<int>* getLocalFaces();
	  
	  vector<int>* getLocalTetras();
		
	    
};

#endif // SIMPLEX_H
