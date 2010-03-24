#include "Triangulation/triangulation.h"
#include "radius.h"
#include "rungaApprox.h"

/*
 * This is a correct implementation of the RK4 approximation method.
 *
 * Author: Joe Thomas
 * Verification: Alex Henniges
 */
 
void RungaApprox::step(double dt){
    int vertexCount = Triangulation::vertexTable.size();
    
    Radius* radii[vertexCount];                       
    double radii_vals[vertexCount];           // A copy of the original radii 
    double slopes[vertexCount];          // Buffer for slope calculations.
    double samples[4][vertexCount];      // Sample values at each vertex.
    double weight[4] = {1, 1/2, 1/2, 1}; // RK4 sampling factors

    // Variables for iterating over the vertecies
    map<int, Vertex>::iterator vIter; 
    map<int, Vertex>::iterator vBegin = Triangulation::vertexTable.begin();
    map<int, Vertex>::iterator vEnd = Triangulation::vertexTable.end();
     
    int kk, ii;
    
    // Initialize our copy of the radii...
    for(kk = 0, vIter = vBegin; vIter != vEnd; kk++, vIter++) {
      radii[kk] = Radius::At(vIter->second);
      radii_vals[kk] = radii[kk]->getValue();
    }
    
    // Compute 4 samples, per the RK4 formula...
    for(ii = 0; ii < 4; ii++){
      local_derivs(slopes); // Store derivatives into slopes

      // Compute the next sample...
      for(kk = 0, vIter = vBegin; vIter != vEnd; kk++, vIter++){
          samples[ii][kk] = slopes[kk] * dt;
          radii[kk]->setValue(radii_vals[kk] + samples[ii][kk] * weight[ii]);
      }
    }

    double avg;
    for(kk = 0, vIter = vBegin; vIter != vEnd; kk++, vIter++){
      // Compute a weighted average of the samples at this vertex.
      avg = samples[0][kk] + 2 * samples[1][kk];
      avg += 2 * samples[2][kk] + samples[3][kk];
      avg = avg * (0.1666666); // avg * 1/6;
     
      radii[kk]->setValue(radii_vals[kk] + avg);
    }
};
