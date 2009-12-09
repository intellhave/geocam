#include <map>
#include <vector>
#include "partials.h"
#include "triangulation.h"

#include "dih_angle.h"
#include "partial_edge.h"
#include "dih_angle_partial.h"
#include "partial_edge_partial.h"

#define PI 	3.141592653589793238

void mixedNEHRPartial(int eta, double* gradient) {
     map<int, Vertex>::iterator vit;
     vector<int> edges;
     vector<int> tetras;
     Edge ij;
     Edge mn = Triangulation::edgeTable[eta];
     Tetra ijkl;
     int i;
     double partial;
     double dihedral_partialSum;
     double dij_partialSum;
     bool sameEdge; // If mn != ij, partial of d_ij w.r.t eta_mn = 0.
     for(vit = Triangulation::vertexTable.begin(), i = 0; vit != Triangulation::vertexTable.end(); vit++, i++) {
             partial = 0;
             
             edges = *(vit->second.getLocalEdges());
             for(int j = 0; j < edges.size(); j++) {
                 dihedral_partialSum = 0;
                 dij_partialSum = 2 * PI;
                 ij = Triangulation::edgeTable[edges[j]];
                 sameEdge = mn.getIndex() == ij.getIndex();
                 
                 tetras = *(ij.getLocalTetras());
                 for(int kl = 0; kl < tetras.size(); kl++) {
                    ijkl = Triangulation::tetraTable[tetras[kl]];
                    
                    dihedral_partialSum -= DihedralAnglePartial::valueAt(mn,ij,ijkl);
                    
                    if(sameEdge) {
                       dij_partialSum -= DihedralAngle::valueAt(ij, ijkl);
                    }
                 }
                // printf("\tdih_partial_sum %d = %f\n", j, dihedral_partialSum);
                 partial += (dihedral_partialSum) * PartialEdge::valueAt(vit->second,ij);
                 if(sameEdge) {
                      //printf("\tdih_sum = %f\n", dij_partialSum);
                      partial += (dij_partialSum) * PartialEdgePartial::valueAt(vit->second, mn);
                 }
             }
             gradient[i] = partial;
     }    
}

void radiusPartial(int eta, double* soln, double* hessian) {
     int vSize = Triangulation::vertexTable.size();
     double gradient[vSize];
     mixedNEHRPartial(eta, gradient);
     for(int i = 0; i < vSize; i++) {
        gradient[i] = -gradient[i];     
     }
     
     if(LinearEquationsSolving(vSize, hessian, gradient, soln)) {
        //ERROR: NON-INVERTIBLE MATRIX
        printf("Matrix not invertible\n");
     }
}

int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution)
{
  double fMaxElem;
  double fAcc;

  int i , j, k, m;


  for(k=0; k<(nDim-1); k++) // base row of matrix
  {
    // search of line with max element
    fMaxElem = fabs( pfMatr[k*nDim + k] );
    m = k;
    for(i=k+1; i<nDim; i++)
    {
      if(fMaxElem < fabs(pfMatr[i*nDim + k]) )
      {
        fMaxElem = fabs(pfMatr[i*nDim + k]);
        m = i;
      }
    }
    
    // permutation of base line (index k) and max element line(index m)
    if(m != k)
    {
      for(i=k; i<nDim; i++)
      {
        fAcc               = pfMatr[k*nDim + i];
        pfMatr[k*nDim + i] = pfMatr[m*nDim + i];
        pfMatr[m*nDim + i] = fAcc;
      }
      fAcc = pfVect[k];
      pfVect[k] = pfVect[m];
      pfVect[m] = fAcc;
    }
    if( pfMatr[k*nDim + k] == 0.) return 1; // needs improvement !!!

    // triangulation of matrix with coefficients
    for(j=(k+1); j<nDim; j++) // current row of matrix
    {
      fAcc = - pfMatr[j*nDim + k] / pfMatr[k*nDim + k];
      for(i=k; i<nDim; i++)
      {
        pfMatr[j*nDim + i] = pfMatr[j*nDim + i] + fAcc*pfMatr[k*nDim + i];
      }
      pfVect[j] = pfVect[j] + fAcc*pfVect[k]; // free member recalculation
    }
  }
  
    printf("PFVECT: <");
  for(k=0; k<nDim; k++) {
    printf("%f, ", pfVect[k]);
  }
  printf(">\n");
  
  for(k=(nDim-1); k>=0; k--)
  {
    pfSolution[k] = pfVect[k];
    for(i=(k+1); i<nDim; i++)
    {
      pfSolution[k] -= (pfMatr[k*nDim + i]*pfSolution[i]);
      if(k == nDim - 2) {
           printf("CHECK: %f, %f, %f, %f\n", pfVect[k], pfMatr[k*nDim + i], pfSolution[i], pfSolution[k]);
      }
    }
    if(fabs(pfMatr[k*nDim + k]) < 0.000000001) {
       pfSolution[k] = 1;
    } else {
      pfSolution[k] = pfSolution[k] / pfMatr[k*nDim + k];
    }
  }

  // Noramlize the solution
  double length = 0;
  for(i = 0; i < nDim; i++) {
   length += pow(pfSolution[i], 2);
  }
  length = sqrt(length);
  for(i = 0; i < nDim; i++) {
   pfSolution[i] = pfSolution[i] / length;
  }
  return 0;
}
