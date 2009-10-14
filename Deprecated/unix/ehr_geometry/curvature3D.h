#ifndef THREEDCURVATURE_H_
#define THREEDCURVATURE_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"

#include "geoquant.h"
#include "triposition.h"

#include "sectional_curvature.h"
#include "partial_edge.h"

class Curvature3D : public virtual GeoQuant {
private:
  vector<GeoQuant*>* sectionalCurvs;
  vector<GeoQuant*>* partials;

protected:
  Curvature3D( Vertex& v );
  void recalculate();
  
public:
  ~Curvature3D();
  static Curvature3D* At( Vertex& v );
  static double valueAt( Vertex& v ) { 
     return Curvature3D::At(v)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* THREEDCURVATURE_H_ */
