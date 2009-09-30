#ifndef CURVATUREPARTIAL_H_
#define CURVATUREPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "dualarea.h"
#include "radius.h"
#include "eta.h"
#include "length.h"
#include "dih_angle_sum.h"
#include "curvature3D.h"

class CurvaturePartial : public virtual GeoQuant {
 private:  
  bool verticesMatch, verticesAdjacent;
  pair<int, int> pairPos;
  
  Radius* vRadius;
  Curvature3D* vCurv;

  vector<DualArea*>* dualAreas;
  vector<DihedralAngleSum*>* dihSums;
  vector<Length*>* lengths;
  vector<Eta*>* etas;
  vector<Radius*>* radii;

  double calculateEqualCase();
  double calculateAdjCase();

protected:
  CurvaturePartial( Vertex& v, Vertex& w );
  void recalculate();

public:
  ~CurvaturePartial();
  static CurvaturePartial* At( Vertex& v, Vertex& w );
  static double valueAt( Vertex& v, Vertex& w ) {
         return CurvaturePartial::At(v, w)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* CURVATUREPARTIAL_H_ */
