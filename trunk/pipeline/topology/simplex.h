/**************************************************************
Class: Simplex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#ifndef SIMPLEX_H
#define SIMPLEX_H
#include <vector>

using namespace std;

static int simplexCounter = 0;

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

class Simplex {
 private:
  int serialNumber;

  int index;
  vector<int> localVertices;
  vector<int> localEdges;
  vector<int> localFaces;
  vector<int> localTetras;

 public:
  Simplex(){ 
    serialNumber = simplexCounter;
    simplexCounter++;
  }

  Simplex(int setIndex){ 
    serialNumber = simplexCounter;
    simplexCounter++;
    index = setIndex;
  }
  ~Simplex(){};
	
  int getSerialNumber(){ return serialNumber; }

  int getIndex(){ return index; }
  void setIndex(int newIndex){ index = newIndex; }
  
  void addVertex(int);
  void addEdge(int);
  void addFace(int);
  void addTetra(int);
	  
  void removeVertex(int);
  void removeEdge(int);
  void removeFace(int); 
  void removeTetra(int);
	  
  void clearLocals();
	  
  bool isAdjVertex(int);
  bool isAdjEdge(int);
  bool isAdjFace(int);
  bool isAdjTetra(int);
	  
  vector<int>* getLocalVertices();
  vector<int>* getLocalEdges();
  vector<int>* getLocalFaces();
  vector<int>* getLocalTetras();
};

#endif // SIMPLEX_H
