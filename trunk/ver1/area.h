#ifndef AREA_H_
#define AREA_H_

#include "geoquant.h"
#include "face.h"

#include <map>
using namespace std;

class Area : public virtual GeoQuant {
 private:
  GeoQuant* Len[3];

 public:
  Area(Face f, GQIndex& gqi);
  ~Area(){};
  virtual void recalculate();
};

#endif /* AREA_H_ */
