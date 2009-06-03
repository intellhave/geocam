#ifndef _RUNGAAPPROX_H_
#define _RUNGAAPPROX_H_

#include "approximator.h"
#include "sysdiffeq.h"

class RungaApprox : public Approximator {
public:
  RungaApprox(sysdiffeq de) : Approximator(de){}
  RungaApprox(sysdiffeq de, char* histories) : Approximator(de, histories){}
  ~RungaApprox(){}
  void step(double dt);
};

#endif /* _RUNGAAPPROX_H_ */
