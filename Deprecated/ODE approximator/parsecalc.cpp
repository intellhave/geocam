#include <fstream>

#include "flow/sysdiffeq.h"
#include "flow/approximator.h"
#include "flow/rungaApprox.h"
#include "flow/eulerApprox.h"
#include "flow/parsecalc.h"

void calcSpecs::loadTokens(char* filename){
  ifstream indata; 
  char word[500];  // A sloppy buffer for input strings. 

  indata.open(filename);
  if(!indata) { 
    printf("Unable to open %s\n", filename);
    exit(1);
  }

  while ( !indata.eof() ) { // keep reading until end-of-file
    indata >> word;         // sets EOF flag if no value found
    tokens.push_back(string(word));
  }
  
  indata.close();
}

static void errorMessage(char* type){
  printf("Error in %s specification.\n", type);
  exit(1);
}


int calcSpecs::findToken(char* label){
  for(int ii = 0; ii < tokens.size() - 1; ii++)
    if((tokens[ii]).compare(label) == 0)
      return ii;
  
  return -1;
}

/*****************************************************/

void calcSpecs::parseTriangulation(){
  int ii = findToken("TRIANGULATION:");
  if(ii != -1){
    tri_filename = tokens[ii+1];  
    return;
  }
  errorMessage("TRIANGULATION");
}

void calcSpecs::parseRadii(){
  char* endLabel = "DIFFEQS:";

  // First, find startLabel
  int ii = findToken("RADII:");
  int jj = findToken("DIFFEQS:");

  double rad;
  for(ii++; ii < jj; ii++){
    rad = atof(tokens[ii].c_str());
    radii.push_back(rad);
  }
  //errorMessage("RADII");
}

void calcSpecs::parseDiffEqs(){
  int ii = findToken("DIFFEQS:");
  string system = tokens[ii+1];
  
  char* tokens[4] = {"std_ricci", "adj_ricci", "hyp_ricci", "adj_hyp_ricci"};
  sysdiffeq funcs[4] = {StdRicci, AdjRicci, HypRicci, AdjHypRicci};
  
  for(ii = 0; ii < 4; ii++){
      if(system.compare(tokens[ii]) == 0){
          diffEqs = funcs[ii];
          return;                              
      }       
  }

  errorMessage("DIFFEQS");
}

void calcSpecs::parseApproximator(){
  int ii = findToken("APPROXIMATOR:");
  string name = tokens[ii+1];
  
  if(name.compare("runga") == 0){
    approx = new RungaApprox(diffEqs);
  } else if(name.compare("euler") == 0){
    approx = new EulerApprox(diffEqs);
  } else { 
    errorMessage("APPROXIMATOR");
  }
}

bool calcSpecs::findDouble(char* label, double* num){
  int ii = findToken(label);
  if(ii != -1){
    *num = atof(tokens[ii+1].c_str()); 
    return true;
  }
  
  *num = -666;
  return false;
}

void calcSpecs::parseCalcType(){
  bool check1 = findDouble("ACCURACY:", &accuracy) &&
    findDouble("PRECISION:", &precision);

  bool check2 = findDouble("NUMSTEPS:", &numsteps); 
  bool check3 = findDouble("STEPSIZE:", &stepsize);

  if(check1 && check3){
    ctype = BY_ACCURACY;
  } else if(check2 && check3){
    ctype = BY_STEPS;
  } else {
    errorMessage("CALC-TYPE");
  }
}

