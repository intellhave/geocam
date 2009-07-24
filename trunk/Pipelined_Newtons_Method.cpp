#include "delaunay.h"
#include "3DTriangulation/3DInputOutput.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include "triangulation/smallMorphs.h"
#include "3DTriangulation/3DTriangulationMorph.h"
#include <ctime>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"
#include <map>

#include "radius.h"
#include "curvature3D.h"
#include "ehr_partial.h"
#include "ehr_second_partial.h"

#define PI 	3.141592653589793238

int SolveLinearEquation(int nDim, double* pfMatr, double* pfVect, double* pfSolution);

void Newtons_Method() {
  int V = Triangulation::vertexTable.size();

  Curvature3D* Curvatures[ V ];
  Radius* Radii[ V ];
  double log_Radii[ V ];
       
  EHRSecondPartial* hessianGenerator[ V ][ V ];
  double hessian[ V ][ V ];

  EHRPartial* gradientGenerator[ V ];
  double negative_gradient[ V ];

  double soln[V];  
  double* matrix;


  /*** Initialize Quantities ***/

  map<int, Vertex>::iterator vit, vit2;
  int ii = 0;;
  for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++, ii++) {
    Vertex& v = vit->second;
    
    Radii[ii] = Radius::At( v );
    Curvatures[ii] = Curvature3D::At( v );
    gradientGenerator[ii] = EHRPartial::At( v );

    int jj = ii;
    for( vit2 = vit; vit2 != Triangulation::vertexTable.end(); vit2++, jj++ ){
      Vertex& w = vit2->second;
      hessianGenerator[ii][jj] = EHRSecondPartial::At( v, w );
      hessianGenerator[jj][ii] = hessianGenerator[ii][jj];
    }
  }

  /*** Run Newton's Method ***/

  while(true) {            
    for(int ii = 0; ii < V; ii++){
      double curv = Curvatures[ii]->getValue();
      double radius = Radii[ii]->getValue(); 
      printf("vertex %3d: radius = %f\t  curvature = %.10f\n", ii, radius, curv/radius); 
    }            

    // Obtain a copy of the current hessian from the hessianGenerator
    for(int i = 0; i < V; i++) {
      for(int j = i; j < V; j++) {
	  hessian[i][j] = hessianGenerator[i][j]->getValue();
	  hessian[j][i] = hessian[i][j];
      }
    }

    // Likewise, obtain a copy of the current graident.
    for(int i=0; i < V; i++)
      negative_gradient[i] = -1.0 * gradientGenerator[i]->getValue();

    SolveLinearEquation( V, (double*) EHRhessian, negative_gradient, soln);

    double temp;

    for(int ii - 0; ii < V; ii++){
      log_radii[ii] += soln[ii];
      Radii[ii]->setValue( exp( log_radii[ii] ) );
    }   
  }
}

//==============================================================================
// return 1 if system not solving
// nDim - system dimension
// pfMatr - matrix with coefficients
// pfVect - vector with free members
// pfSolution - vector with system solution
// pfMatr becames trianglular after function call
// pfVect changes after function call
//
// Developer: Henry Guennadi Levkin
//
//==============================================================================
int SolveLinearEquation(int nDim, double* pfMatr, double* pfVect, double* pfSolution)
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
        fMaxElem = pfMatr[i*nDim + k];
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

  for(k=(nDim-1); k>=0; k--)
  {
    pfSolution[k] = pfVect[k];
    for(i=(k+1); i<nDim; i++)
    {
      pfSolution[k] -= (pfMatr[k*nDim + i]*pfSolution[i]);
    }
    pfSolution[k] = pfSolution[k] / pfMatr[k*nDim + k];
  }

  return 0;
}

