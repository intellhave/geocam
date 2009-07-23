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
  static void CleanUp();
  static void Record( char* filename );
};

#endif /* DUALAREA_H_ */
