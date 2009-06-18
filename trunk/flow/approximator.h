#ifndef APPROXIMATOR_H_
#define APPROXIMATOR_H_

#include <vector>
#include "sysdiffeq.h"

using namespace std;

class Approximator{
 public:
  vector<double> radiiHistory; bool radii;
  vector<double> curvHistory; bool curvs2D, curvs3D;
  vector<double> areaHistory; bool areas;
  vector<double> volumeHistory; bool volumes;
  sysdiffeq local_derivs;
 Approximator(sysdiffeq de) {
      local_derivs = de;
      radii = curvs2D = curvs3D = areas = volumes = false;
      
      vector<double> radiiHistory(); 
      vector<double> curvHistory();
      vector<double> areaHistory(); 
      vector<double> volumeHistory();
 }
 Approximator(sysdiffeq de, char* histories){
      local_derivs = de;
      
      radii = curvs2D = curvs3D = areas = volumes = false;
      int i, len = strlen(histories);
      for(i = 0; i < len; i++) {
         switch(histories[i]) {
            case 'r': case 'R':
                 radii = true;
                 break;
            case '2':
                 curvs2D = true;
                 break;
            case '3':
                 curvs3D = true;
            case 'a': case 'A':
                 areas = true;
                 break;
            case 'v': case 'V':
                 volumes = true;
                 break;                        
         }
      }
      vector<double> radiiHistory(); 
      vector<double> curvHistory();
      vector<double> areaHistory(); 
      vector<double> volumeHistory();
  }  
  ~Approximator(){}
  
  int run(int numSteps, double stepsize);
//  void run(double precision, double accuracy, double stepsize);
  int run(double precision, double stepsize);
  int run(double precision, int maxNumSteps, double stepsize);
       
  virtual void step(double stepsize) = 0;
  void recordState();
  void recordRadii();
  void record2DCurvs();
  void record3DCurvs();
  void recordAreas();
  void recordVolumes();
  
  void getLatest(double radii[], double curvs[]);
  void clearHistories();
};

#endif // APPROXIMATOR_H_
