/**************************************************************
Class: Face
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 9, 2008
**************************************************************/

#include "face.h" // class's header file
#include <iostream>

// class constructor
Face::Face() : Simplex()
{
             negative = false;
	// insert your code here
}

// class destructor
Face::~Face()
{
	// insert your code here
}


void Face::switchSide()
{
     negative = !negative;
}

bool Face::isNegative()
{
     return negative;
}
