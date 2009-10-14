#ifndef TOTALVOLUME_H_
#define TOTALVOLUME_H_

#include <vector>
#include <new>
using namespace std;

#include "triangulation/triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "volume.h"

class TotalVolume : public virtual GeoQuant {
private:
  vector<Volume*>* volumes;

protected:
  TotalVolume();
  void recalculate();

public:
  ~TotalVolume();
  static TotalVolume* At();
  static double valueAt() {
         return TotalVolume::At()->getValue();
  }
  static void CleanUp();
  void remove();
};

#endif /* TOTALVOLUME_H_ */
