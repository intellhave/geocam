#ifndef GEOMETRY_H
#define GEOMETRY_H

#define FLIP      1
#define FLOW      2

#include "triangulation/triangulation.h"

typedef enum typenum(Euclidean, Hyperbolic, Spherical) geometry;
typedef enum typenum(2D, 3D) dimension;

class Geometry {
    private:
      double ang(double, double, double);
      double ang(Vertex v, Face f);
      double spherAng(double, double, double, double radius);
      double spherAng(Vertex v, Face f, double radius);
      double hypAng(double, double, double);
      double hypAng(Vertex v, Face f);
      double dihedralAng(Edge *e, Tetra *t);
      double curv(Vertex *v);
      double curv3D(Vertex *v);
    
      static GQIndex gqi;
    
    public:
       Geometry();
       ~Geometry();
       static geometry geo;
       static dimension dim;
       static int mode;
       static double spherRadius;
       
       static void build();
       static void build( int quantType /* ENUM */);
       static void addGQ(TriPosition tp, GeoQuant g);
       
       
       static void setMode(int m);
       static void setGeometry(geometry g);
       static void setDimension(dimension d);
       static void setRadius(double rad); // For Spherical geometries
       
       static void setRadius(const Vertex& v, double rad);
       static void setEta(const Edge& e, double eta);
       static void setLength(const Edge& e, double len);
       
       static double radius(const Vertex& v);
       static double eta(const Edge& e);
       static double length(const Edge& e);
       static double angle(const Vertex& v, const Face& f);
       static double dihedralAngle(const Edge& e, const Tetra& t);
       static double curvature(const Vertex& v);
       static double area(const Face& f);
       static double volume(const Tetra& t);
       
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
};
#endif // GEOMETRY_H
