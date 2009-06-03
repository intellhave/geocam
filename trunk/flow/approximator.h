#ifndef APPROXIMATOR_H_
#define APPROXIMATOR_H_

#include <vector>
#include "sysdiffeq.h"

using namespace std;

class Approximator{
 public:
  vector<double> radiiHistory; bool radii;
  vector<double> curvHistory; bool curvs;
  vector<double> areaHistory; bool areas;
  vector<double> volumeHistory; bool volumes;
  sysdiffeq local_derivs;
 Approximator(sysdiffeq de) {
      local_derivs = de;
      radii = curvs = areas = volumes = false;
      
      vector<double> radiiHistory(); 
      vector<double> curvHistory();
      vector<double> areaHistory(); 
      vector<double> volumeHistory();
 }
 Approximator(sysdiffeq de, char* histories){
      local_derivs = de;
      
      radii = curvs = areas = volumes = false;
      int i, len = strlen(histories);
      for(i = 0; i < len; i++) {
         switch(histories[i]) {
            case 'r': case 'R':
                 radii = true;
                 break;
            case 'c': case 'C':
                 curvs = true;
                 break;
            case 'a': case 'A':
                 areas = true;
                 break;
            case 'v': case 'V':
                 volumes = true;
                 break;                        
         }
      }
  }  
  ~Approximator(){}
  
  virtual void step(double stepsize) = 0;
  void run(int numSteps, double stepsize);
  void run(double precision, double accuracy, double stepsize);

  void recordState();
  void recordRadii();
  void recordCurvs();
  void recordAreas();
  void recordVolumes();
  
  void getLatest(double radii[], double curvs[]);
  void clearHistories();
};

#endif // APPROXIMATOR_H_
