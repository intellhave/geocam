#ifndef DUALAREA_H_
#define DUALAREA_H_

#include <map>
#include <new>
using namespace std;

#include "geoquant.h"
#include "triposition.h"

#include "dualareasegment.h"

class DualArea : public virtual GeoQuant {
private:
  vector<DualAreaSegment*>* segments;

protected:
  DualArea( Edge& e );
  void recalculate();

public:
  ~DualArea();
  static DualArea* At( Edge& e );
  static double valueAt( Edge& e) {
         return DualArea::At(e)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* DUALAREA_H_ */
