#ifndef GEOMETRY_H_
#define GEOMETRY_H_

/* The geometry enumerator is used to indicate what the current geometry is.
   This affects how angles are calculated. The default is Euclidean. */
typedef enum geonum{Euclidean, Hyperbolic, Spherical} Geometry;
/* The dimension enumerator is used to indicate what the current dimension is.
   This affects what quantities are built as well as how curvatures are
   calculated. The default is TwoD. */
typedef enum dimnum{TwoD, ThreeD} Dimension;

Geometry geometry;
Dimension dimension;

#endif
