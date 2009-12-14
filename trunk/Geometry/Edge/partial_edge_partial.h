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
  Length* Lij;
  Eta* eij;

protected:
  PartialEdgePartial( Vertex& v, Edge& e );
  void recalculate();

public:
  ~PartialEdgePartial();
  static PartialEdgePartial* At( Vertex& v, Edge& e  );
  static double valueAt(Vertex& v, Edge& e) {
         return PartialEdgePartial::At(v, e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* PARTIALEDGEPARTIAL_H_ */
