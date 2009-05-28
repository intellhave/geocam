#ifndef APPROXIMATOR_H_
#define APPROXIMATOR_H_

#include <vector>
#include "sysdiffeq.h"

using namespace std;

class Approximator{
 public:
  vector<double> radiiHistory;
  vector<double> curvHistory;
  sysdiffeq local_derivs;

 Approximator(sysdiffeq de){
      local_derivs = de;
      vector<double> radiiHistory(); 
      vector<double> curvHistory();
  }  
  ~Approximator(){}
  
  virtual void step(double stepsize) = 0;
  void run(int numSteps, double stepsize);
  void run(double precision, double accuracy, double stepsize);

  void recordState();
  void getLatest(double radii[], double curvs[]);
  void clearHistories();
};

#endif // APPROXIMATOR_H_
