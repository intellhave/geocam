#ifndef PARTIALEDGE_H_
#define PARTIALEDGE_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "radius.h"
#include "length.h"
#include "alpha.h"

class PartialEdge : public virtual GeoQuant {
private:
  Radius* ri;
  Radius* rj;
  Alpha* ai;
  Alpha* aj;
  Length* Lij;

protected:
  PartialEdge( Vertex& v, Edge& e );
  void recalculate();

public:
  ~PartialEdge();
  static PartialEdge* At( Vertex& v, Edge& e  );
  static double valueAt(Vertex& v, Edge& e) {
         return PartialEdge::At(v, e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* PARTIALEDGE_H_ */
