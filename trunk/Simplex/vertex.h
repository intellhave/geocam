/**************************************************************
Class: Vertex
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#ifndef VERTEX_H
#define VERTEX_H

#include "simplex.h" // inheriting class's header file
#include <vector>
#include "edge.h"
#include "face.h"
#include "line.h"

/*
 * The Vertex class is derived from the Simplex class. It is the
 * zero-dimensional simplex. Every vertex also has a weight, which is
 * the basis for providing all measurements (length, curvature, etc).
 * Weights are initialized to zero and set by the user after building
 * the triangulation.
 */
class Vertex : public Simplex
{
        double weight;
        double xCoord;
        double yCoord;
	public:
           
		// default class constructor
		Vertex();
		Vertex(int setIndex) : Simplex(setIndex) {};
		// class destructor
		~Vertex();
		
        /*
		 * Returns the weight of this vertex.
		 */
        double getWeight()
        {
            return weight;
        }
        
        	/*
		 * Sets the weight to the given weight.
		 */
		void setWeight(double);
		void setWeight(double, char);
		void setPosition(double, double);
		double getXpos();
		double getYpos();
		int getDegree();
		Point convertToPoint();
};

#endif // VERTEX_H



