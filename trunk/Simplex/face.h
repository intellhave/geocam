/**************************************************************
Class: Face
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#ifndef FACE_H
#define FACE_H

#include "simplex.h" // inheriting class's header file

/*
 * The Face class is derived from the Simplex class. It is the
 * two-dimensional simplex. The Face class currently has no
 * included properties. As a rule, every face has three vertices 
 * and three edges.
 */
class Face : public Simplex
{
      
      bool negative;
	public:
		// class constructor
		Face();
		Face(int setIndex) : Simplex(setIndex) {};
		// class destructor
		~Face();
		
		bool isNegative();
		
		void switchSide();
};

#endif // FACE_H
