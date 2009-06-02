/**************************************************************
Class: Geometry
Author: Alex Henniges, Joe Thomas
Version: June 2, 2009

 * The Geometry class acts as the box for the GeoQuant system. Users work
 * through the Geometry class to build quantities, set quantities, and
 * later access these quantities. The geometry class has a notion of the type
 * of geometry it currently is (Euclidean, Hyperbolic, or Spherical), its
 * dimension (two or three dimensions), and the mode the user wants to use 
 * that indicates what quantities should be created and what the dependency
 * tree should look like (See GeoQuant.h). Like the Triangulation class,
 * Geometry is static.
**************************************************************/

#ifndef GEOMETRY_H
#define GEOMETRY_H

#include "triangulation/triangulation.h"
#include "geoquant.h"

/* The geometry enumerator is used to indicate what the current geometry is.
   This affects how angles are calculated. The default is Euclidean. */
typedef enum geonum{Euclidean, Hyperbolic, Spherical} geometry;
/* The dimension enumerator is used to indicate what the current dimension is.
   This affects what quantities are built as well as how curvatures are
   calculated. The default is TwoD. */
typedef enum dimnum{TwoD, ThreeD} dimension;
/* The mode enumerator is used to indicate what the current mode is. This
   affects what quantities are built and how dependencies are made. The default
   is Flow. */
typedef enum modenum{Flip, Flow} mode;

class Geometry {
    private:
      /* This is the map which stores all the GeoQuants */
      static GQIndex gqi;
    
    public:
       Geometry();
       ~Geometry();
       /* The current geometry */
       static geometry geo;
       /* The current dimension */
       static dimension dim;
       /* The current type */
       static mode type;
       /* Radius of the sphere for spherical geometries. NOT IMPLEMENTED */
       static double spherRadius;
       
       /* Build the geoemtry and its GeoQuants by first clearing the current
          set of GeoQuants, then adding them in based on the geometry,
          dimension, and mode. */
       static void build();
       /* Clear the current set of GeoQuants. */   
       static void reset();
       
       /* Set the mode of the geometry. */
       static void setMode(mode type);
       /* Set the geometry. */
       static void setGeometry(geometry g);
       /* Set the dimension of the geometry. */
       static void setDimension(dimension d);
       /* Set the radius used for spherical geometries. NOT IMPLEMENTED */
       static void setRadius(double rad);
       
       /* Set the radius of a vertex <v> to <rad>. */
       static void setRadius(Vertex& v, double rad);
       /* Set the eta value of an edge <e> to <eta> */
       static void setEta(Edge& e, double eta);
       /* Set the length value of an edge <e> to <len>. Do not use with 
          flow mode. */
       static void setLength(Edge& e, double len);
       
       /* Get the radius of vertex <v>. */
       static double radius(Vertex& v);
       /* Get the eta value of edge <e>. */
       static double eta(Edge& e);
       /* Get the length of edge <e>. */
       static double length(Edge& e);
       /* Get the angle on face <f> at vertex <v>. The geometry used to 
          calculate the angle depends on the one given during build time. */
       static double angle(Vertex& v, Face& f);
       /* Get the dihedral angle on tetra <t> at edge <e>. Dihedral angles only
          exist if the dimension was ThreeD at build time. */
       static double dihedralAngle(Edge& e, Tetra& t);
       /* Get the curvature at vertex <v>. The curvature is calculated based on
          the dimension given at build time. */
       static double curvature(Vertex& v);
       /* Get the area of a face <f>. */
       static double area(Face& f);
       /* Get the volume of a tetra <t>. Volumes only exist if the dimension
          was ThreeD at build time. */
       static double volume(Tetra& t);
       /* Get the partial edge of edge <e> at vertex <v>. Partial edges do not
          exist in a TwoD, Flow environment. */
       static double partialEdge(Vertex &v, Edge& e);
       /* Get the curvature at an edge <e>. Edge curvatures only exist if the
          dimension was ThreeD at build time. */
       static double edgeCurvature(Edge &e);
       
       /* Sets the radii of the vertices of the Triangulation to the values 
          provided by an array <radii>. This requires that one know the order
          of the vertices in the Triangulation. */
       static void setRadii(double* radii);
       /* Places the radii of the vertices of the Triangulation into the array
          <radii>. This is done in the order of the vertices in the 
          Triangulation. */
       static void getRadii(double* radii);
       /* Sets the lengths of the edges of the Triangulation to the values
          provided by an array <lengths>. This requires that one know the order
          of the edges in the Triangulation. */
       static void setLengths(double* lengths);
       /* Returns the sum of the curvatures of all of the vertices in the 
          Triangulation. The curvatures are calculated based on the dimension
          given at build time. */
       static double netCurvature();
       
       /* Calculates the angle between three lengths using the law of cosines
          for Euclidean geometry. <len3> is the opposite length. */
       static double angle(double len1, double len2, double len3);
       /* Calculates the derivative of the Volume. */
       static double CayleyVolumeDeriv(Tetra& t);
};
#endif // GEOMETRY_H
