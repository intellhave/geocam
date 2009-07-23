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
  static void CleanUp();
  static void Record( char* filename );
};

#endif /* DUALAREASEGMENT_H_ */
