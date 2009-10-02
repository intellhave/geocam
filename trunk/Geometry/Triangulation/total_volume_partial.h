#ifndef TOTALVOLUMEPARTIAL_H_
#define TOTALVOLUMEPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "face_height.h"
#include "area.h"

class TotalVolumePartial : public virtual GeoQuant {
 private:
  vector<FaceHeight*>* face_heights;
  vector<Area*>* areas;

protected:
  TotalVolumePartial( Vertex& v );
  void recalculate();

public:
  ~TotalVolumePartial();
  static TotalVolumePartial* At( Vertex& v );
  static double valueAt(Vertex& v) {
         return TotalVolumePartial::At(v)->getValue();
  }
  void remove();
  static void CleanUp();
};

#endif /* TOTALVOLUMEPARTIAL_H_ */
