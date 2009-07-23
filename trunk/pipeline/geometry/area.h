#ifndef AREA_H_
#define AREA_H_

#include "triangulation.h"
#include "geoquant.h"
#include "triposition.h"

#include "length.h"

class Area : public virtual GeoQuant {
private:
  Length* Len[3];

protected:
  Area( Face& f );
  void recalculate();

public:
  ~Area();
  static Area* At( Face& f );
  static void CleanUp();
};
#endif /* AREA_H_ */
