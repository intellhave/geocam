/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "simplex.h"
#include "pointline.h"

#ifndef EDGE_H
#define EDGE_H

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
        double intersectAngle;
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
        double getAngle();
        void setAngle(double);
        bool isBorder();

};

#endif // EDGE_H
