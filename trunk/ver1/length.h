#ifndef LENGTH_H_
#define LENGTH_H_

#include "geoquant.h"
#include "edge.h"

class Length : public virtual GeoQuant {
 public:
  Length(double val, Edge e);
  ~Length(){};
  virtual void recalculate();
};
#endif /* LENGTH_H_ */
