#ifndef VOLUME_H_
#define VOLUME_H_

#include <cmath>
#include <map>
#include <new>
using namespace std;

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "length.h"

class Volume : public virtual GeoQuant {
private:
  Length* len[6];

protected:
  Volume( Tetra& t );
  void recalculate();

public:
  ~Volume();
  static Volume* At( Tetra& t );
  static double valueAt(Tetra& t) {
         return Volume::At(t)->getValue();
  }
  static void CleanUp();
  void remove();
  static void Record( char* filename );
};

#endif /* VOLUME_H_ */
