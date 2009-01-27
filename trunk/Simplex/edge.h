/**************************************************************
Class: Edge
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: July 16, 2008
**************************************************************/

#include "simplex.h"
#include <map>
#ifndef EDGE_H
#define EDGE_H

/*
 * The Edge class is derived from the Simplex class. It is the
 * one-dimensional simplex. Every edge also has a length, which
 * is calculated based on the radii of its two local vertices.
 * Therefore, every length is initialized and then either set to
 * a length specified by the user or calculated from the radii.
 */
class Edge : public Simplex
{
        double length;
        double eta;
        map<int, double> dihedralAngles;
	public:
		// class constructor
		Edge();
		Edge(int setIndex) : Simplex(setIndex) {};
		// class destructor
		~Edge();
		
		/*
		 * Sets the length based on the radii of the vertices.
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
        double getEta();
        double getIntersectAngle();
        
        void setEta(double);
        void setIntersectAngle(double);
        bool isBorder();
        void setDihedralAngles();
        double getDihedralAngle(int index);

};

#endif // EDGE_H
