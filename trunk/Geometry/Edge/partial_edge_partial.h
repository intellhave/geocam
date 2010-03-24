#ifndef PARTIALEDGEPARTIAL_H_
#define PARTIALEDGEPARTIAL_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "length.h"
#include "eta.h"

class PartialEdgePartial : public virtual GeoQuant {
private:
  Radius* ri;
  Radius* rj;
  Alpha* ai;
  Alpha* aj;
  Eta* eij;
  
  bool equal;

protected:
  PartialEdgePartial( Vertex& v, Edge& e, Edge& f );
  void recalculate();

public:
  ~PartialEdgePartial();
  static PartialEdgePartial* At( Vertex& v, Edge& e, Edge& f );
  static double valueAt( Vertex& v, Edge& e, Edge& f ) {
         return PartialEdgePartial::At(v, e, f)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* PARTIALEDGEPARTIAL_H_ */
