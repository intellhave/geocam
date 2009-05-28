#include "resources.h"
#include <windows.h>
#include "triangulation/triangulation.h"
#include "Geometry/Geometry.h"
#include "triangulation/triangulationInputOutput.h"
#include "3DTriangulation/3DInputOutput.h"
#include "flow/approximator.h"
#include "flow/eulerApprox.h"
#include "flow/sysdiffeq.h"
class TriangulationModel 
{
      static bool loaded;
      static vector<double> weights;
      static vector<double> curvs;
      static int numSteps;
      static double stepSize;
      static bool flow;
      static double acc;
      static bool smart;
      static Approximator *app;
      public:
      
      TriangulationModel();
      ~TriangulationModel();
      static void clearSystem();
      static void clearData();
      static void setNumSteps(int);
      static void setStepSize(double);
      static void setAccuracy(double);
      static void setFlowFunction(bool);
      static void setSmartFlow(bool);
      static void setRadii(vector<double>*);
      static void setRadius(int, double);
      static void setEta(int, double);
      static bool runFlow(int);
      static bool loadFile(char*, int);
      static bool load3DFile(char*, int);
      static bool saveFile(char*);
      static bool printResults(int);
      static bool isLoaded();
       
};
