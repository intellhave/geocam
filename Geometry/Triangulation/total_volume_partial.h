#ifndef TOTALVOLUMEPARTIAL_H_
#define TOTALVOLUMEPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "volume_partial.h"

class TotalVolumePartial : public virtual GeoQuant {
 private:
  bool wrtRadius;
  vector<VolumePartial*>* volume_partials;

protected:
  TotalVolumePartial( Vertex& v );
  TotalVolumePartial( Edge& e );
  void recalculate();

public:
  ~TotalVolumePartial();
  static TotalVolumePartial* At( Vertex& v );
  static TotalVolumePartial* At( Edge& e );
  static double valueAt(Vertex& v) {
         return TotalVolumePartial::At(v)->getValue();
  }
  static double valueAt(Edge& e) {
         return TotalVolumePartial::At(e)->getValue();
  }
  void remove();
  static void CleanUp();
};

#endif /* TOTALVOLUMEPARTIAL_H_ */
