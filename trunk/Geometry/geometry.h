#ifndef GEOMETRY_H_
#define GEOMETRY_H_

#define PI 	3.141592653589793238

#include "geoquant.h"
#include "triposition.h"
#include "area.cpp"
#include "curvature2D.cpp"
#include "curvature3D.cpp"
#include "dih_angle.cpp"
#include "euc_angle.cpp"
#include "edge_curvature.cpp"
#include "eta.cpp"
#include "partial_edge.cpp"
#include "radius.cpp"
#include "volume.cpp"
#include "volume_partial.cpp"


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
