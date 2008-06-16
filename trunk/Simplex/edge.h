/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#ifndef EDGE_H
#define EDGE_H

#include "simplex.h" // inheriting class's header file

/*
 * The Edge class is derived from the Simplex class. It is the
 * one-dimensional simplex. Every edge also has a length, which
 * is calculated based on the weights of its two local vertices.
 * Therefore, every length is initialized and then either set to
 * a length specified by the user or calculated from the weights.
 */
class Edge : public Simplex
{
        double length;
	public:
		// class constructor
		Edge();
		Edge(int setIndex) : Simplex(setIndex) {};
		// class destructor
		~Edge();
		
		/*
		 * Sets the length based on the weights of the vertices.
		 */
		void setLength();
		/*
		 * Sets the length to the given length.
	     */
		void setLength(double);
		/*
		 * Returns the length of this edge.
		 */
		double getLength() 
        {
               return length;
        };
};

#endif // EDGE_H
