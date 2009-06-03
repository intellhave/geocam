#ifndef _EULERAPPROX_H_
#define _EULERAPPROX_H_

#include "approximator.h"
#include "sysdiffeq.h"

class EulerApprox : public Approximator {
public:
  EulerApprox(sysdiffeq de) : Approximator(de){}
  EulerApprox(sysdiffeq de, char* histories) : Approximator(de, histories){}
  ~EulerApprox(){}
  void step(double dt);
};

#endif /* _EULERAPPROX_H_ */
