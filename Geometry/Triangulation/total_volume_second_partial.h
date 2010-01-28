#ifndef TOTALVOLUMESECONDPARTIAL_H_
#define TOTALVOLUMESECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "volume_second_partial.h"

class TotalVolumeSecondPartial : public virtual GeoQuant {
 private:
  bool wrtRadius;
  vector<VolumeSecondPartial*>* volume_partials;

protected:
  TotalVolumeSecondPartial( Vertex& v, Edge& e );
  void recalculate();

public:
  ~TotalVolumeSecondPartial();
  static TotalVolumeSecondPartial* At( Vertex& v, Edge& e );
  static double valueAt(Vertex& v, Edge& e) {
         return TotalVolumeSecondPartial::At(v, e)->getValue();
  }
  void remove();
  static void CleanUp();
};

#endif /* TOTALVOLUMESECONDPARTIAL_H_ */
