#ifndef GEOMETRY_H
#define GEOMETRY_H

#define FLIP      1
#define FLOW      2

#include "triangulation/triangulation.h"
#include "ver2/geometry/geoquant.h"

typedef enum geonum{Euclidean, Hyperbolic, Spherical} geometry;
typedef enum dimnum{TwoD, ThreeD} dimension;

class Geometry {
    private:    
      static GQIndex gqi;
    
    public:
       Geometry();
       ~Geometry();
       static geometry geo;
       static dimension dim;
       static int mode;
       static double spherRadius;
       
       static void build();       
       
       static void setMode(int m);
       static void setGeometry(geometry g);
       static void setDimension(dimension d);
       static void setRadius(double rad); // For Spherical geometries
       
       static void setRadius(Vertex& v, double rad);
       static void setEta(Edge& e, double eta);
       static void setLength(Edge& e, double len);
       
       static double radius(Vertex& v);
       static double eta(Edge& e);
       static double length(Edge& e);
       static double angle(Vertex& v, Face& f);
       static double dihedralAngle(Edge& e, Tetra& t);
       static double curvature(Vertex& v);
       static double area(Face& f);
       static double volume(Tetra& t);
       static double partialEdge(Vertex &v, Edge& e);
       static double edgeCurvature(Edge &e);
       
       /*
        * Sets the vertices to the weights provided by an array of 
        * doubles. This requires that one know the order of the vertices
        * as it does not use the indices.
        */
       static void setRadii(double*);
       /*
        * Places the values of the weights of the vertices into the array
        * provided. This is done in order of the vertices in the map, not
        * necessarily by index. Can be used to easily conjoin flows.
        */
       static void getRadii(double*);
       static void setLengths(double*);
       static double netCurvature();
       
       static double angle(double len1, double len2, double len3);
};
#endif // GEOMETRY_H
