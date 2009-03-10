#ifndef _SYSDIFFEQ_H_
#define _SYSDIFFEQ_H_

// Definition for a system of differential equations.
typedef void (*sysdiffeq)(double derivs[]);

/* 
 * The functions listed below each describe a system of differential equations
 * for changing the radii on a given triangulation. To use one of these,
 * the user passes in an empty array of doubles, with one entry for each 
 * vertex. For each vertex v, the function computes the derivative at that
 * vertex, and stores the result in derivs[v]. (Note: We chose to let these
 * functions be systems of linear equations, versus single-vertex differential
 * equations, because in some cases a shared variable, like average curvature,
 * needs to be computed to find the derivative at each vertex. Rather than
 * trying to store this value in the data structure or recompute it many times,  
 * we compute this locally in the function, and reuse the value as we iterate
 * over the vertices.
 */

void StdRicci(double derivs[]);
void AdjRicci(double derivs[]);

void SpherRicci(double derivs[]);
void AdjSpherRicci(double derivs[]);

void HypRicci(double derivs[]);
void AdjHypRicci(double derivs[]); 

#endif /* _SYSDIFFEQ_H_ */
