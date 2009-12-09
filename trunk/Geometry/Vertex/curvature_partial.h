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
#include "dih_angle_partial.h"
#include "partial_edge.h"
#include "partial_edge_partial.h"
#include "curvature3D.h"

class CurvaturePartial : public virtual GeoQuant {
 private:
  bool wrtRadius;       
         
  /* Vertex, Vertex variables */ 
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

  /* Vertex, Edge variables */
  DihedralAngleSum* dihSum;
  vector<vector<DihedralAnglePartial*>*>* dihPartials;
  vector<PartialEdge*>* dijs;
  PartialEdgePartial* dij_partial;
  
  double calculateEtaCase();

protected:
  CurvaturePartial( Vertex& v, Vertex& w );
  CurvaturePartial( Vertex& v, Edge& e );
  void recalculate();

public:
  ~CurvaturePartial();
  static CurvaturePartial* At( Vertex& v, Vertex& w );
  static CurvaturePartial* At(Vertex& v, Edge& e);
  static double valueAt( Vertex& v, Vertex& w ) {
         return CurvaturePartial::At(v, w)->getValue();
  }
  static double valueAt( Vertex& v, Edge& e ) {
         return CurvaturePartial::At(v, e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* CURVATUREPARTIAL_H_ */
