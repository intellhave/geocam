#ifndef PARTIALEDGESECONDPARTIAL_H_
#define PARTIALEDGESECONDPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "length.h"
#include "eta.h"

class PartialEdgeSecondPartial : public virtual GeoQuant {
private:
  Radius* ri;
  Radius* rj;
  Alpha* ai;
  Alpha* aj;
  Eta* eij;

protected:
  PartialEdgeSecondPartial( Vertex& v, Edge& e );
  void recalculate();

public:
  ~PartialEdgeSecondPartial();
  static PartialEdgeSecondPartial* At( Vertex& v, Edge& e  );
  static double valueAt(Vertex& v, Edge& e) {
         return PartialEdgeSecondPartial::At(v, e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* PARTIALEDGESECONDPARTIAL_H_ */
