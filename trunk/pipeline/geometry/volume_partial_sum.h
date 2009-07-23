#ifndef VOLUMEPARTIALSUM_H_
#define VOLUMEPARTIALSUM_H_

#include <vector>
#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "volume_partial.h"


class VolumePartialSum : public virtual GeoQuant {
private:
  vector<VolumePartial*>* volPartials;

protected:
  VolumePartialSum( Vertex& v );
  void recalculate();

public:
  ~VolumePartialSum();
  static VolumePartialSum* At( Vertex& v );
  static void CleanUp();
};

#endif /* VOLUMEPARTIALSUM_H_ */
