/**************************************************************
Class: Face
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#ifndef FACE_H
#define FACE_H

#include "simplex.h" // inheriting class's header file
#include <map>
/*
 * The Face class is derived from the Simplex class. It is the
 * two-dimensional simplex. As a rule, every face has three vertices 
 * and three edges. A face can be negative and occurs during flips.
 * For these cases, the face class has a boolean flag isNegative().
 */
class Face : public Simplex
{
      
      bool negative;
      map<int, double> angles;
	public:
		// class constructor
		Face();
		Face(int setIndex) : Simplex(setIndex) {negative = false;};
		// class destructor
		~Face();
		
		
		double getArea();
		bool isNegative();
		void setNegativity(bool);
		void switchSide();
		void setAngles();
		double getAngle(int index);
};

#endif // FACE_H
