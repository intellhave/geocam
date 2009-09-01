#ifndef DUALAREASEGMENT_H_
#define DUALAREASEGMENT_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "edge_height.h"
#include "face_height.h"

class DualAreaSegment : public virtual GeoQuant {
private:
  EdgeHeight* hij_k;
  EdgeHeight* hij_l;
  FaceHeight* hijk_l;
  FaceHeight* hijl_k;

protected:
  DualAreaSegment( Edge& e, Tetra& t );
  void recalculate();

public:
  ~DualAreaSegment();
  static DualAreaSegment* At( Edge& e, Tetra& t );
  static double valueAt( Edge& e, Tetra& t ) {
         return DualAreaSegment::At(e, t)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* DUALAREASEGMENT_H_ */
