#include<string>
#include<vector>

#include "flow/sysdiffeq.h"
#include "flow/approximator.h"

using namespace std;

typedef enum calc{BY_ACCURACY, BY_STEPS} calc_type;

class calcSpecs{
 private:
  void loadTokens(char* filename);
  void parseTriangulation();
  void parseRadii();
  void parseDiffEqs();
  void parseApproximator();
  void parseCalcType();
  int findToken(char* label);
  bool findDouble(char* label, double* num);
  
 public:
  vector<string> tokens;
  
  calcSpecs(char* filename){
      vector<string> tokens();
      loadTokens(filename);
      parseTriangulation();
      parseRadii();
      parseDiffEqs();
      parseApproximator();
      parseCalcType();
  }
  
  ~calcSpecs(){}

  string tri_filename;
  vector<double> radii;
  sysdiffeq diffEqs;
  
  Approximator* approx;
  calc_type ctype;

  double accuracy;
  double precision;
  double stepsize;
  double numsteps;
};
