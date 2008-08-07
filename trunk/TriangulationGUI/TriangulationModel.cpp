#include "TriangulationModel.h"


vector<double> TriangulationModel::weights;
vector<double> TriangulationModel::curvs;
int TriangulationModel::numSteps;
double TriangulationModel::stepSize;
bool TriangulationModel::loaded;
bool TriangulationModel::flow;

TriangulationModel::TriangulationModel()
{
    loaded = false;
    flow = false;
}
TriangulationModel::~TriangulationModel()
{
}
void TriangulationModel::clearSystem()
{
   weights.clear();
   curvs.clear();
   numSteps = 0;
   stepSize = 0.0;
   loaded = false;
   Triangulation::resetTriangulation();
}
void TriangulationModel::clearData()
{
    weights.clear();
    curvs.clear();
    numSteps = 0;
    stepSize = 0.0;
}
void TriangulationModel::setNumSteps(int steps)
{
    numSteps = steps;
}
void TriangulationModel::setStepSize(double size)
{
    stepSize = size;
}
void TriangulationModel::setFlowFunction(bool flowF)
{
     flow = flowF;
}
bool TriangulationModel::isLoaded()
{
     return loaded;
}
bool TriangulationModel::runCalcFlow(double* weightArr, int type)
{
     if(!loaded || numSteps <= 0 || stepSize <= 0.0)
     {
        return false;
     }
     switch(type)
     {
        case ID_RUN_FLOW_EUCLIDEAN:
        {
             calcFlow(&weights, &curvs, stepSize, weightArr, numSteps, flow);
        }
        break;
        case ID_RUN_FLOW_SPHERICAL:
        {
             sphericalCalcFlow(&weights, &curvs, stepSize, weightArr, numSteps, flow);
        }
        break;
        case ID_RUN_FLOW_HYPERBOLIC:
        {
             hyperbolicCalcFlow(&weights, &curvs, stepSize, weightArr, numSteps, flow);
        }
        break;
        default: return false;
     }
     return true;
}

bool TriangulationModel::loadFile(char* filename, int format)
{
    switch(format) 
    {
       case IDSTANDARD:
       {
            readTriangulationFile(filename);
            loaded = true;
       }
       break;
       case IDLUTZ:
       {
            makeTriangulationFile(filename, "C:/Dev-Cpp/geocam/Triangulations/manifold converted.txt");
            readTriangulationFile("C:/Dev-Cpp/geocam/Triangulations/manifold converted.txt");
            loaded = true;
       }
       break;
       default: return false;
    }
    return true;
}
bool TriangulationModel::printResults(int printType)
{
     if(!loaded) 
     {
        return false;
     }
     switch(printType)
     {
         case IDPRINTSTEP:
         {
              printResultsStep("C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt", &weights, &curvs);
         }
         break;
         case IDPRINTVERTEX:
         {
              printResultsVertex("C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt", &weights, &curvs);
         }
         break;
         case IDPRINTNUM:
         {
              printResultsNum("C:/Dev-Cpp/geocam/Triangulations/ODE Result.txt", &weights, &curvs);
         }
         break;
         default: return false;
     }
     return true;
}
