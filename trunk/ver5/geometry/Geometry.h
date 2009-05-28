#ifndef GEOMETRY_H
#define GEOMETRY_H

#define FLIP      1
#define FLOW      2

#include "triangulation.h"
#include "geoquant.h"

typedef enum geomnum{Euclidean, Hyperbolic, Spherical} geometry;
typedef enum dimnum{TWOD, THREED} dimension;

class Geometry {
 private:
    
    
 public:
  GQIndex gqi;    

  Geometry(){}
  ~Geometry(){
    for( GQIndex::iterator iter = gqi.begin(); iter != gqi.end(); ++iter ){
      GeoQuant* gq = iter->second;
      delete gq;
    } 
  }

  geometry geo;
  dimension dim;
  int mode;
  double spherRadius;
       
  void build();
       
  void setRadius(Vertex& v, double rad);
  void setEta(Edge& e, double eta);
  void setLength(Edge& e, double len);
       
  double radius(Vertex& v);
  double eta(Edge& e);
  double length(Edge& e);
  double angle(Vertex& v, Face& f);
  double dihedralAngle(Edge& e, Tetra& t);
  double curvature(Vertex& v);
       
  /*
   * Sets the vertices to the weights provided by an array of 
   * doubles. This requires that one know the order of the vertices
   * as it does not use the indices. 
   */
  void setRadii(double*);
  /*
   * Places the values of the weights of the vertices into the array
   * provided. This is done in order of the vertices in the map, not
   * necessarily by index. Can be used to easily conjoin flows.
   */
  void getRadii(double*);
  void setLengths(double*);
  double netCurvature();
};
#endif // GEOMETRY_H
