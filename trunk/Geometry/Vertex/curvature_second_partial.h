#ifndef CURVATURE_SECOND_PARTIAL_H_
#define CURVATURE_SECOND_PARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"
#include "dih_angle.h"
#include "dih_angle_partial.h"
#include "dih_angle_second_partial.h"
#include "partial_edge.h"
#include "partial_edge_partial.h"
#include "partial_edge_second_partial.h"

class CurvatureSecondPartial : public virtual GeoQuant {
 private:
  vector<vector<DihedralAngleSecondPartial*>*>* dih_sec_partials;
  vector<vector<DihedralAnglePartial*>*>* dih_partials_e;
  vector<vector<DihedralAnglePartial*>*>* dih_partials_f;
  vector<vector<DihedralAngle*>*>* dihs;
  
  vector<PartialEdgeSecondPartial*>* dij_sec_partials;
  vector<PartialEdgePartial*>* dij_partials_e;
  vector<PartialEdgePartial*>* dij_partials_f;
  vector<PartialEdge*>* dijs;

protected:
  CurvatureSecondPartial( Vertex& v, Edge& e, Edge& f );
  void recalculate();

public:
  ~CurvatureSecondPartial();
  static CurvatureSecondPartial* At( Vertex& v, Edge& e, Edge& f );
  static double valueAt( Vertex& v, Edge& e, Edge& f ) {
         return CurvatureSecondPartial::At(v, e, f)->getValue();
  }
  static void CleanUp();
  void remove();
};

#endif /* CURVATURE_SECOND_PARTIAL_H_ */
