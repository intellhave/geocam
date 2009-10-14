#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>
#include <cstdio>
#include <cerrno>

#include "triangulation/triangulation.h"
#include "Pipelined_Newtons_Method.h"
#include "utilities.h"

/********** Quantities we explicitly use **********/
#include "radius.h"
#include "curvature3D.h"
#include "ehr_partial.h"
#include "ehr_second_partial.h"

/**** Quantities we need to examine ****/
#include "total_volume_partial.h"
/*****************************************/
void printFunc(FILE* out);

double EHR();

//#define PI = 3.141592653589793238;

void Newtons_Method(double stopping_threshold) {
     Newtons_Method(stopping_threshold, NULL);
}

void Newtons_Method( double stopping_threshold, char* filename ) {
     
     
//  double matr[5][5] = {{1, 2, 3, 4, 6}, {2, 4, 1, 3, 5}, {5, 3, 1, 2, 4}, 
//                       {2, 2, 4, 6, 7}, {8, 3, 1, 2, 4}};
//  double vect[] = {0, 0, 0, 0, 0};
//  double solution[5];
//  LinearEquationsSolving(5, (double*) matr, vect, solution);  
//  pause();
  int V = Triangulation::vertexTable.size();
  FILE* results;
  Curvature3D* Curvatures[ V ];
  Radius* Radii[ V ];
  TotalVolumePartial* TVPs[ V ];

  double log_radii[ V ];
       
  EHRSecondPartial* hessianGenerator[ V ][ V ];
  double hessian[ V ][ V ];

  EHRPartial* gradientGenerator[ V ];
  double negative_gradient[ V ];

  double soln[V];  
  double* matrix;

  /*** Initialize Quantities ***/
  map<int, Vertex>::iterator vit, vit2;
  int ii = 0;
  for(vit = Triangulation::vertexTable.begin();
      vit != Triangulation::vertexTable.end(); vit++, ii++) {
    Vertex& v = vit->second;

    Radii[ii] = Radius::At( v );
    Curvatures[ii] = Curvature3D::At( v );
    TVPs[ii] = TotalVolumePartial::At( v );

    gradientGenerator[ii] = EHRPartial::At( v );
    
    int jj = ii;
    for( vit2 = vit; vit2 != Triangulation::vertexTable.end(); vit2++, jj++ ){
      Vertex w = vit2->second;
      hessianGenerator[ii][jj] = EHRSecondPartial::At( v, w );
      hessianGenerator[jj][ii] = hessianGenerator[ii][jj];
    }
  }

  /*** Run Newton's Method ***/
  TotalVolume* totVol = TotalVolume::At();
  double init_totVol = 4.71404520791;
  
  bool converged = false;
  
  if(filename != NULL) {
    results = fopen(filename, "w"); 
  }
  while(!converged && !errno){

    for(int ii = 0; ii < V; ii++){ 
      log_radii[ii] = log( Radii[ii]->getValue() );
    }

    // Obtain a copy of the current hessian from the hessianGenerator
    for(int i = 0; i < V; i++) {
      for(int j = i; j < V; j++) {
	  hessian[i][j] = hessianGenerator[i][j]->getValue();
	  hessian[j][i] = hessian[i][j];
      }
    }
    
    // Likewise, obtain a copy of the current graident.
    for(int ii = 0 ; ii < V; ii++)
      negative_gradient[ii] = -1.0 * gradientGenerator[ii]->getValue();
       
    LinearEquationsSolving( V, (double*) hessian, negative_gradient, soln);

    //maxDelta = 0.0;
    for(int ii = 0; ii < V; ii++){
      log_radii[ii] += soln[ii];
      
      Radii[ii]->setValue( exp( log_radii[ii] ) );
    }

    double radius_scaling_factor = pow( init_totVol/totVol->getValue(), 1.0/3.0 );
 
    for(int ii = 0; ii < V; ii++){
      Radii[ii]->setValue( radius_scaling_factor * Radii[ii]->getValue() );
    }
  
    /***** CHECK STOPPING CONDITION HERE *****/
    
// *************** Printing Data ********************
// map<int, Vertex>::iterator vit;
//     map<int, Edge>::iterator eit;
////     fprintf(out, "\n");
//     for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end();
//             vit++) {
////        printf("Radius %d = %.10f\n", vit->first, Radius::valueAt(vit->second));
//          printf("CSC %d = %.10f\n", vit->first, Curvatures[vit->first-1]->getValue()/TVPs[vit->first-1]->getValue());
//     }
//     for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
//             eit++) {
////        printf("Eta %d = %.10f\n", eit->first, Eta::valueAt(eit->second));
//        printf("Length %d = %.10f\n", eit->first, Length::valueAt(eit->second));
//     }
//     printf("-----------------\nEHR: %.10f\n", EHR());

// ***************** End Printing Data ************ 
    
    
    
    converged = true;
    double K_prev = Curvatures[0]->getValue();
    double V_prev = TVPs[0]->getValue();
    double K_curr, V_curr;
//    printf("K_0 = %f, V_0 = %f, K / V = %f\n", K_prev, V_prev, K_prev/V_prev);
//    map<int, Edge>::iterator eit;
    for(int ii = 1; (ii < V) && converged; ii++){
      K_curr = Curvatures[ii]->getValue();
      V_curr = TVPs[ii]->getValue();
//      printf("K_%d = %f, V_%d = %f, K / V = %f\n", ii, K_curr, ii, V_curr, K_prev/V_prev);
      converged = converged && (abs(K_curr/V_curr - K_prev/V_prev) < stopping_threshold);
      K_prev = K_curr;
      V_prev = V_curr;      
    }

//    for(int ii = 1; (ii < V) && converged; ii++){
//      K_curr = Curvatures[ii]->getValue();
//      V_curr = TVPs[ii]->getValue();
//      printf("K_%d = %f, V_%d = %f, K / V = %f\n", ii, K_curr, ii, V_curr, K_prev/V_prev);
//      }
//      
//    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();
//             eit++) {
//                    printf("length = %.12f\n",Length::valueAt(eit->second));
//                    }
    //system("PAUSE");
    /***** FINISHED CHECKING STOPPING CONDITION *****/ 
    if (filename != NULL) {
       printFunc(results);
    }
              
  }
  if(filename != NULL) {
    fclose(results);
  }
}

int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution)
//int LinearEquationsSolving(int nDim, double pfMatr[3][3], double pfVect[3], double pfSolution[3])
{
  double fMaxElem;
  double fAcc;

  int i , j, k, m;


  for(k=0; k<(nDim-1); k++) // base row of matrix
  {
//   printf("\nStep %d:\n", k);
//   for(i = 0; i < nDim; i++) {
//     for(j = 0; j < nDim; j++) {
//       printf("%f, ", pfMatr[i*nDim + j]);
//     }
//     printf(" | %f\n", pfVect[i]);
//     }
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

//  printf("\nAFTER:\n");
//  for(k = 0; k < nDim; k++) {
//     for(j = 0; j < nDim; j++) {
//       printf("%.16f, ", pfMatr[k*nDim + j]);
//     }
//     printf(" | %.16f\n", pfVect[k]);
//  }

  for(k=(nDim-1); k>=0; k--)
  {
    pfSolution[k] = pfVect[k];
    for(i=(k+1); i<nDim; i++)
    {
      pfSolution[k] -= (pfMatr[k*nDim + i]*pfSolution[i]);
    }
    if(fabs(pfMatr[k*nDim + k]) < 0.000000001) {
       pfSolution[k] = 0;
    } else {
      pfSolution[k] = pfSolution[k] / pfMatr[k*nDim + k];
    }
  }

  return 0;
}
