/*
* In this class, we accumulate a number of useful tools for running a flow
* simulation. Typically, when carrying out one of these experiments, 
* one wants to run a few steps of the experiment, and then record some 
* information about the state of the triangulation. For example, a user
* (or debugging programmer) might ask: "How do the dual-areas in my
* triangulation vary as I carry out flow X?" This module is a tool designed
* for this sort of problem.
*
* Basically, it allows you to do three things:
* 1) Specify a given triangulation and system of differential equations.
* 2) Run a step of the flow (or several steps until a given condition is
*    satisfied).
* 3) Record information about the triangulation during the run.
*/




#ifndef APPROXIMATOR_H_
#define APPROXIMATOR_H_

#include <vector>
//#include "sysdiffeq.h"
typedef void (*sysdiffeq)(double derivs[]);

using namespace std;

class Approximator{
 public:
 // These variables save information about the
 // geometry of the triangulation during the run.
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
 
  // Carry out numSteps steps of the flow, each of the input stepsize. 
  int run(int numSteps, double stepsize);
//  void run(double precision, double accuracy, double stepsize);

  // Run the flow until it converges to a given precision, using steps of
  // size stepsize.
  int run(double precision, double stepsize);
  
  // Run the flow until it converges to a given precision or maxNumSteps
  // steps have been used, each of size stepsize.
  int run(double precision, int maxNumSteps, double stepsize);
       
  // Run one step of the flow, of input size.
  virtual void step(double stepsize) = 0;
  
  // Record information about a particular part of the geometry.
  void recordState();
  void recordRadii();
  void record2DCurvs();
  void record3DCurvs();
  void recordAreas();
  void recordVolumes();
  
  // Inspect the recorded histories of quantities.
  void getLatest(double radii[], double curvs[]);
  
  // Clear the recorded histories of quantities.
  void clearHistories();
};

#endif // APPROXIMATOR_H_
