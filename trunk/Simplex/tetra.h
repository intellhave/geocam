/**************************************************************
Class: Tetra
Author: Alex Henniges, Tom Williams
Version: Sept 15, 2008
**************************************************************/

#ifndef TETRA_H
#define TETRA_H

#include "simplex.h" // inheriting class's header file

/*
 * The Tetra class represents the thee-dimensional simplex.
 * In the general case, a Tetra has four local faces, four local edges,
 * four local vertices, and four or less local tetras. Tetras are only
 * used in three-dimensional manifolds.
 */
class Tetra : public Simplex
{
     public:
   		// class constructor
		Tetra();
		Tetra(int setIndex) : Simplex(setIndex) {};
		// class destructor
		~Tetra();
};
#endif // Tetra_H
