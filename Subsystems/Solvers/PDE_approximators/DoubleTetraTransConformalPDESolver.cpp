#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include <ctime>
#include "NMethod.h"

#define PI 	3.141592653589793238
#define Pi  3.141592653589793238

double DoubleTetraNEHR(double radii[4]);
void RK4s(double prev_radii[4], double prev_etas[6], double result_radii[4], double result_etas[6], double h, bool MIN, bool RK4);
void RK4t(double prev_radii[4], double prev_etas[6], double result_radii[4], double result_etas[6], double h, bool MIN, bool RK4);
double C1223(double radii[4],double etas[6]);
double C1323(double radii[4],double etas[6]);
double C1423(double radii[4],double etas[6]);
double C2323(double radii[4],double etas[6]);
double C2423(double radii[4],double etas[6]);
double C3423(double radii[4],double etas[6]);
double C1224(double radii[4],double etas[6]);
double C1324(double radii[4],double etas[6]);
double C1424(double radii[4],double etas[6]);
double C2324(double radii[4],double etas[6]);
double C2424(double radii[4],double etas[6]);
double C3424(double radii[4],double etas[6]);
void derivative_s(double radii[4],double etas[6], double der_etas[6]);
void derivative_t(double radii[4],double etas[6], double der_etas[6]);

double global_etas[6];
// Input the desired value used for the stopping condition on all calls to Newton's method.
double NMaccuracy=.00000000001;
// Set the following boolean to true if you want to run the Runga-Kutta method.
// False will run the frowar Euler method.
bool RK = true;

void TransConformal()
{
     int N=10;
     int i,j,k;
     double delta_st=.1;
     double initial_etas[6];
     double initial_radii[4];
     double MiniNEHRCCW[2*N+1][2*N+1];
     double MiniNEHRCW[2*N+1][2*N+1];
     double MiniNEHR[2*N+1][2*N+1];
     double etasCCW[2*N+1][2*N+1][6];
     double etasCW[2*N+1][2*N+1][6];
     double radiiCCW[2*N+1][2*N+1][4];
     double radiiCW[2*N+1][2*N+1][4];
     double Avg_radii[2*N+1][2*N+1][4];
     double Avg_etas[2*N+1][2*N+1][6];
     double prev_radiiCCW[4];
     double prev_etasCCW[6];
     double prev_radiiCW[4];
     double prev_etasCW[6];
     double prev_etas_avgCCW[6];
     double prev_etas_avgCW[6];
     double prev_radii_avgCCW[4];
     double prev_radii_avgCW[4];
     double cur_radiiCCW[4];
     double cur_etasCCW[6];
     double cur_radiiCW[4];
     double cur_etasCW[6];
     double result_radiiCCW[4];
     double result_radiiCW[4];
     double result_etasCCW[6];
     double result_etasCW[6];
     double result_radii_avgCCW[4];
     double result_radii_avgCW[4];
     double result_etas_avgCCW[6];
     double result_etas_avgCW[6];
     double tempCCW;
     double tempCW;
     double current_radii[4];
     double soln_radii[4];
     
     
     char results[] = "TriangulationFiles/TransConformalResult.txt";
   FILE* result = fopen(results, "w");
   
   initial_radii[0]=1;
   initial_radii[1]=1;
   initial_radii[2]=1;
   initial_radii[3]=1;
   
   initial_etas[0]=1;
   initial_etas[1]=1;
   initial_etas[2]=1;
   initial_etas[3]=1;
   initial_etas[4]=1;
   initial_etas[5]=1;
     

          
     for(i=0; i<=N;i++) {
        for(j=0;j<=N;j++) {
//           printf("begining i=%d, j=%d\n", i,j);
           
           if(i==0 && j==0) {
           
                 for(k=0;k<=5;k++) {
                 etasCCW[N][N][k]=initial_etas[k];
                 etasCW[N][N][k]=initial_etas[k];
                 Avg_etas[N][N][k]=initial_etas[k];
                 global_etas[k]=initial_etas[k];
                 }
                 for(k=0;k<=3;k++) {
                 current_radii[k]=initial_radii[k];
                 }
                 NewtonsMethod *nm = new NewtonsMethod(DoubleTetraNEHR, 4);
                 nm->setStoppingGradientLength(NMaccuracy);
                 nm->optimize(current_radii, soln_radii, NMETHOD_MIN);
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N][N][k]=soln_radii[k];
                 }
                 
                 MiniNEHR[N][N]=DoubleTetraNEHR(soln_radii);
                 printf("ending   i=%d, j=%d i=j=zero\n", i,j);
                 }
                          
           else if(i==0 && j!=0) {
                 for(k=0;k<=5;k++) {
                 prev_etasCCW[k]=Avg_etas[N-(j-1)][N][k];
                 prev_etasCW[k]=Avg_etas[N+(j-1)][N][k];
                 }
                 for(k=0;k<=3;k++) {
                 prev_radiiCCW[k]=Avg_radii[N-(j-1)][N][k];
                 prev_radiiCW[k]=Avg_radii[N+(j-1)][N][k];
                 }
                                  
                 RK4t(prev_radiiCCW, prev_etasCCW, result_radiiCCW, result_etasCCW, delta_st, true, RK);
                 RK4t(prev_radiiCW, prev_etasCW, result_radiiCW, result_etasCW, -delta_st, true, RK);
                 for(k=0;k<=5;k++) {
                 Avg_etas[N-(j)][N][k]=result_etasCCW[k];
                 Avg_etas[N+(j)][N][k]=result_etasCW[k];
                 etasCCW[N-(j)][N][k]=result_etasCCW[k];
                 etasCCW[N+(j)][N][k]=result_etasCW[k];
                 etasCW[N-(j)][N][k]=result_etasCCW[k];
                 etasCW[N+(j)][N][k]=result_etasCW[k];
                 }
                 for(k=0;k<=3;k++) {
                 Avg_radii[N-(j)][N][k]=result_radiiCCW[k];
                 Avg_radii[N+(j)][N][k]=result_radiiCW[k];
                 radiiCCW[N-(j)][N][k]=result_radiiCCW[k];
                 radiiCCW[N+(j)][N][k]=result_radiiCW[k];
                 radiiCW[N-(j)][N][k]=result_radiiCCW[k];
                 radiiCW[N+(j)][N][k]=result_radiiCW[k];
                 }
                 
                 for(k=0;k<=5;k++) {
                      global_etas[k]=result_etasCCW[k];
                      }     
                 MiniNEHR[N-(j)][N]=DoubleTetraNEHR(result_radiiCCW);
                 
                 for(k=0;k<=5;k++) {
                      global_etas[k]=result_etasCW[k];
                      }
                 MiniNEHR[N+(j)][N]=DoubleTetraNEHR(result_radiiCW);
                 printf("ending   i=%d, j=%d i=zero\n", i,j);
                    }
                    
           else if(i!=0 && j==0) {
                 for(k=0;k<=5;k++) {
                 prev_etasCCW[k]=Avg_etas[N][N+(i-1)][k];
                 prev_etasCW[k]=Avg_etas[N][N-(i-1)][k];
                 }
                 for(k=0;k<=3;k++) {
                 prev_radiiCCW[k]=Avg_radii[N][N+(i-1)][k];
                 prev_radiiCW[k]=Avg_radii[N][N-(i-1)][k];
                 }
                 RK4s(prev_radiiCCW, prev_etasCCW, result_radiiCCW, result_etasCCW, delta_st, true, RK);
                 RK4s(prev_radiiCW, prev_etasCW, result_radiiCW, result_etasCW, -delta_st, true, RK);
                 for(k=0;k<=5;k++) {
                 Avg_etas[N][N+i][k]=result_etasCCW[k];
                 Avg_etas[N][N-i][k]=result_etasCW[k];
                 etasCCW[N][N+i][k]=result_etasCCW[k];
                 etasCCW[N][N-i][k]=result_etasCW[k];
                 etasCW[N][N+i][k]=result_etasCCW[k];
                 etasCW[N][N-i][k]=result_etasCW[k];
                 }
                 for(k=0;k<=3;k++) {
                 Avg_radii[N][N+i][k]=result_radiiCCW[k];
                 Avg_radii[N][N-i][k]=result_radiiCW[k];
                 radiiCCW[N][N+i][k]=result_radiiCCW[k];
                 radiiCCW[N][N-i][k]=result_radiiCW[k];
                 radiiCW[N][N+i][k]=result_radiiCCW[k];
                 radiiCW[N][N-i][k]=result_radiiCW[k];
                 }
                                  
                 for(k=0;k<=5;k++) {
                      global_etas[k]=result_etasCCW[k];
                      }      
                 MiniNEHR[N][N+i]=DoubleTetraNEHR(result_radiiCCW);
                 for(k=0;k<=5;k++) {
                      global_etas[k]=result_etasCW[k];
                      }
                 MiniNEHR[N][N-i]=DoubleTetraNEHR(result_radiiCW);
                 printf("ending   i=%d, j=%d j=zero\n", i,j);
                 }
                    
           else if(i>0 && j>0) {
                 //quad I
                 
                 for(k=0;k<=5;k++) {
                 prev_etas_avgCCW[k]=Avg_etas[N-(j-1)][N+i][k];
                 prev_etas_avgCW[k]=Avg_etas[N-(j)][N+i-1][k];
                 prev_etasCCW[k]=etasCCW[N-(j-1)][N+i][k];
                 prev_etasCW[k]=etasCW[N-(j)][N+i-1][k];
                 }
                 for(k=0;k<=3;k++) {
                 prev_radii_avgCCW[k]=Avg_radii[N-(j-1)][N+i][k];
                 prev_radii_avgCW[k]=Avg_radii[N-(j)][N+i-1][k];
                 prev_radiiCCW[k]=radiiCCW[N-(j-1)][N+i][k];
                 prev_radiiCW[k]=radiiCW[N-(j)][N+i-1][k];
                 }
                 RK4t(prev_radii_avgCCW, prev_etas_avgCCW, result_radii_avgCCW, result_etas_avgCCW, delta_st, false, RK);
                 RK4s(prev_radii_avgCW, prev_etas_avgCW, result_radii_avgCW, result_etas_avgCW, delta_st, false, RK);
                 RK4t(prev_radiiCCW, prev_etasCCW, result_radiiCCW, result_etasCCW, delta_st, true, RK);
                 RK4s(prev_radiiCW, prev_etasCW, result_radiiCW, result_etasCW, delta_st, true, RK);
                 
                 for(k=0;k<=5;k++) {
                 Avg_etas[N-(j)][N+i][k]=.5*result_etas_avgCCW[k]+.5*result_etas_avgCW[k];
                 global_etas[k]=Avg_etas[N-(j)][N+i][k];
                 etasCCW[N-(j)][N+i][k]=result_etasCCW[k];
                 etasCW[N-(j)][N+i][k]=result_etasCW[k];
                 }
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N-(j)][N+i][k]=.5*result_radii_avgCCW[k]+.5*result_radii_avgCW[k];
                 current_radii[k]=Avg_radii[N-(j)][N+i][k];
                 radiiCCW[N-(j)][N+i][k]=result_radiiCCW[k];
                 radiiCW[N-(j)][N+i][k]= result_radiiCW[k]; 
                 }
                 
                 NewtonsMethod *nm = new NewtonsMethod(DoubleTetraNEHR, 4);
                 nm->setStoppingGradientLength(NMaccuracy);
                 nm->optimize(current_radii, soln_radii, NMETHOD_MIN);
                 
                 MiniNEHR[N-(j)][N+i]=DoubleTetraNEHR(soln_radii);
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N-(j)][N+i][k]=soln_radii[k];
                 }
//                 printf("ending   i=%d, j=%d i,j!=zero Q1\n", i,j);                  
                            
                 //quad II
                 
                 for(k=0;k<=5;k++) {
                 prev_etas_avgCCW[k]=Avg_etas[N-(j)][N-(i-1)][k];
                 prev_etas_avgCW[k]=Avg_etas[N-(j-1)][N-(i)][k];
                 prev_etasCCW[k]=etasCCW[N-(j)][N-(i-1)][k];
                 prev_etasCW[k]=etasCW[N-(j-1)][N-(i)][k];
                 }
                 for(k=0;k<=3;k++) {
                 prev_radii_avgCCW[k]=Avg_radii[N-(j)][N-(i-1)][k];
                 prev_radii_avgCW[k]=Avg_radii[N-(j-1)][N-(i)][k];
                 prev_radiiCCW[k]=radiiCCW[N-(j)][N-(i-1)][k];
                 prev_radiiCW[k]=radiiCW[N-(j-1)][N-(i)][k];
                 }
                 RK4s(prev_radii_avgCCW, prev_etas_avgCCW, result_radii_avgCCW, result_etas_avgCCW, -delta_st, false, RK);
                 RK4t(prev_radii_avgCW, prev_etas_avgCW, result_radii_avgCW, result_etas_avgCW, delta_st, false, RK);
                 RK4t(prev_radiiCCW, prev_etasCCW, result_radiiCCW, result_etasCCW, -delta_st, true, RK);
                 RK4s(prev_radiiCW, prev_etasCW, result_radiiCW, result_etasCW, delta_st, true, RK);
                 
                 for(k=0;k<=5;k++) {
                 Avg_etas[N-(j)][N-i][k]=.5*result_etas_avgCCW[k]+.5*result_etas_avgCW[k];
                 global_etas[k]=Avg_etas[N-(j)][N-i][k];
                 etasCCW[N-(j)][N-i][k]=result_etasCCW[k];
                 etasCW[N-(j)][N-i][k]=result_etasCW[k];
                 }
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N-(j)][N-i][k]=.5*result_radii_avgCCW[k]+.5*result_radii_avgCW[k];
                 current_radii[k]=Avg_radii[N-(j)][N-i][k];
                 radiiCCW[N-(j)][N-i][k]=result_radiiCCW[k];
                 radiiCW[N-(j)][N-i][k]= result_radiiCW[k]; 
                 }

                 nm->setStoppingGradientLength(NMaccuracy);
                 nm->optimize(current_radii, soln_radii, NMETHOD_MIN);
                 
                 MiniNEHR[N-(j)][N-i]=DoubleTetraNEHR(soln_radii);
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N-(j)][N-i][k]=soln_radii[k];
                 }
//                 printf("ending   i=%d, j=%d i,j!=zero Q2\n", i,j);
                 
                 //quad III
                 
                 for(k=0;k<=5;k++) {
                 prev_etas_avgCCW[k]=Avg_etas[N+(j-1)][N-(i)][k];
                 prev_etas_avgCW[k]=Avg_etas[N+(j)][N-(i-1)][k];
                 prev_etasCCW[k]=etasCCW[N+(j-1)][N-(i)][k];
                 prev_etasCW[k]=etasCW[N+(j)][N-(i-1)][k];
                 }
                 for(k=0;k<=3;k++) {
                 prev_radii_avgCCW[k]=Avg_radii[N+(j-1)][N-(i)][k];
                 prev_radii_avgCW[k]=Avg_radii[N+(j)][N-(i-1)][k];
                 prev_radiiCCW[k]=radiiCCW[N+(j-1)][N-(i)][k];
                 prev_radiiCW[k]=radiiCW[N+(j)][N-(i-1)][k];
                 }
                 RK4t(prev_radii_avgCCW, prev_etas_avgCCW, result_radii_avgCCW, result_etas_avgCCW, -delta_st, false, RK);
                 RK4s(prev_radii_avgCW, prev_etas_avgCW, result_radii_avgCW, result_etas_avgCW, -delta_st, false, RK);
                 RK4t(prev_radiiCCW, prev_etasCCW, result_radiiCCW, result_etasCCW, -delta_st, true, RK);
                 RK4s(prev_radiiCW, prev_etasCW, result_radiiCW, result_etasCW, -delta_st, true, RK);
                 
                 for(k=0;k<=5;k++) {
                 Avg_etas[N+(j)][N-i][k]=.5*result_etas_avgCCW[k]+.5*result_etas_avgCW[k];
                 global_etas[k]=Avg_etas[N+(j)][N-i][k];
                 etasCCW[N+(j)][N-i][k]=result_etasCCW[k];
                 etasCW[N+(j)][N-i][k]=result_etasCW[k];
                 }
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N+(j)][N-i][k]=.5*result_radii_avgCCW[k]+.5*result_radii_avgCW[k];
                 current_radii[k]=Avg_radii[N+(j)][N-i][k];
                 radiiCCW[N-(j)][N-i][k]=result_radiiCCW[k];
                 radiiCW[N-(j)][N-i][k]= result_radiiCW[k]; 
                 }
                 
//                 NewtonsMethod *nm = new NewtonsMethod(DoubleTetraNEHR, 4);
                 nm->setStoppingGradientLength(NMaccuracy);
                 nm->optimize(current_radii, soln_radii, NMETHOD_MIN);
                 
                 MiniNEHR[N+(j)][N-i]=DoubleTetraNEHR(soln_radii);
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N+(j)][N-i][k]=soln_radii[k];
                 }
//                 printf("ending   i=%d, j=%d i,j!=zero Q3\n", i,j); 
                 
                 //quad IV
                 
                 for(k=0;k<=5;k++) {
                 prev_etas_avgCCW[k]=Avg_etas[N+(j)][N+(i-1)][k];
                 prev_etas_avgCW[k]=Avg_etas[N+(j-1)][N+(i)][k];
                 prev_etasCCW[k]=etasCCW[N+(j)][N+(i-1)][k];
                 prev_etasCW[k]=etasCW[N+(j-1)][N+(i)][k];
                 }
                 for(k=0;k<=3;k++) {
                 prev_radii_avgCCW[k]=Avg_radii[N+(j)][N+(i-1)][k];
                 prev_radii_avgCW[k]=Avg_radii[N+(j-1)][N+(i)][k];
                 prev_radiiCCW[k]=radiiCCW[N+(j)][N+(i-1)][k];
                 prev_radiiCW[k]=radiiCW[N+(j-1)][N+(i)][k];
                 }
                 RK4s(prev_radii_avgCCW, prev_etas_avgCCW, result_radii_avgCCW, result_etas_avgCCW, delta_st, false, RK);
                 RK4t(prev_radii_avgCW, prev_etas_avgCW, result_radii_avgCW, result_etas_avgCW, -delta_st, false, RK);
                 RK4t(prev_radiiCCW, prev_etasCCW, result_radiiCCW, result_etasCCW, delta_st, true, RK);
                 RK4s(prev_radiiCW, prev_etasCW, result_radiiCW, result_etasCW, -delta_st, true, RK);
                 
                 
                 for(k=0;k<=5;k++) {
                 Avg_etas[N+(j)][N+i][k]=.5*result_etas_avgCCW[k]+.5*result_etas_avgCW[k];
                 global_etas[k]=Avg_etas[N+(j)][N+i][k];
                 etasCCW[N+(j)][N+i][k]=result_etasCCW[k];
                 etasCW[N+(j)][N+i][k]=result_etasCW[k];
                 }
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N+(j)][N+i][k]=.5*result_radii_avgCCW[k]+.5*result_radii_avgCW[k];
                 current_radii[k]=Avg_radii[N+(j)][N+i][k];
                 radiiCCW[N-(j)][N-i][k]=result_radiiCCW[k];
                 radiiCW[N-(j)][N-i][k]= result_radiiCW[k]; 
                 }
                 
//                 NewtonsMethod *nm = new NewtonsMethod(DoubleTetraNEHR, 4);
                 nm->setStoppingGradientLength(NMaccuracy);
                 nm->optimize(current_radii, soln_radii, NMETHOD_MIN);
                 
                 MiniNEHR[N+(j)][N+i]=DoubleTetraNEHR(soln_radii);
                 
                 for(k=0;k<=3;k++) {
                 Avg_radii[N+(j)][N+i][k]=soln_radii[k];
                 }
                 printf("ending   i=%d, j=%d i,j!=zero Q4\n", i,j); 
                 
                 }// End of logic
                 
           }
        }

// Printing out averaged data        
        int N1,M1;
        
        N1=N;
        M1=N;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n",  N1,M1,k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=0;
        M1=N;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n",  N1,M1,k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=N;
        M1=0;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n",  N1,M1,k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=0;
        M1=0;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n", N1,M1, k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=2*N;
        M1=2*N;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n", N1,M1, k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=2*N;
        M1=N;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n", N1,M1, k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=N;
        M1=2*N;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n", N1,M1, k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=0;
        M1=2*N;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n", N1,M1, k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=2*N;
        M1=0;
        for(k=0;k<=5;k++) {
            fprintf(result, "eta$%d$%d$%d = %.10f\n", N1,M1,k, Avg_etas[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radius$%d$%d$%d = %.10f\n", N1,M1, k, Avg_radii[N1][M1][k]);
        }
        fprintf(result, "\n");
///   printing out CCW -vs- CW
        N1=N;
        M1=N;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=0;
        M1=N;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=N;
        M1=0;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=0;
        M1=0;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=2*N;
        M1=2*N;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=2*N;
        M1=N;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=N;
        M1=2*N;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=0;
        M1=2*N;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        N1=2*N;
        M1=0;
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCCW[N1][M1][k]);
        }
        for(k=0;k<=5;k++) {
            fprintf(result, "etaCW$%d$%d$%d = %.10f\n", N1,M1,k, etasCW[N1][M1][k]);
        } 
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCCW[N1][M1][k]);
        }
        for(k=0;k<=3;k++) {
            fprintf(result, "radiusCW$%d$%d$%d = %.10f\n",  N1,M1,k, radiiCW[N1][M1][k]);
        }
        fprintf(result, "\n");
        
        
     fprintf(result, "{");
     for (i=0; i<2*N+1; ++i) {
         fprintf(result, "{");
            for(j=0; j<2*N+1; ++j) {
              if (j != (2*N))
              fprintf(result, "%12.10f, ", MiniNEHR[i][j]);
              else if (i != (2*N))
              fprintf(result, "%12.10f}, ", MiniNEHR[i][j]);
              else
              fprintf(result, "%12.10f}", MiniNEHR[i][j]);
              }
      }        
      fprintf(result, "}");                     
      fclose(result);
}


double DoubleTetraNEHR(double radii[4]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=global_etas[0];
       eta13=global_etas[1];
       eta14=global_etas[2];
       eta23=global_etas[3];
       eta24=global_etas[4];
       eta34=global_etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(2*pow(3,0.3333333333333333)*(Pi*sqrt(pow(r1,2) + 2*eta12*r1*r2 
+pow(r2,2)) + Pi*sqrt(pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2)) + 
Pi*sqrt(pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2)) +  Pi*sqrt(pow(r1,2) 
+ 2*eta14*r1*r4 + pow(r4,2)) + Pi*sqrt(pow(r2,2) + 2*eta24*r2*r4 
+ pow(r4,2)) + Pi*sqrt(pow(r3,2) + 2*eta34*r3*r4 + pow(r4,2)) - 
sqrt(pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*acos((pow(r1,2)*pow(r3,2) 
- pow(eta13,2)*pow(r1,2)*pow(r3,2) - eta24*pow(r1,2)*r2*r4 
+ eta13*eta14*pow(r1,2)*r3*r4 + 
            eta34*pow(r1,2)*r3*r4 - 2*eta13*eta24*r1*r2*r3*r4 + 
eta14*r1*pow(r3,2)*r4 + 
            eta13*eta34*r1*pow(r3,2)*r4 - eta24*r2*pow(r3,2)*r4 + 
            eta23*r2*r3*(pow(r1,2) + eta13*r1*r3 + eta14*r1*r4 - 
eta34*r3*r4) + 
            eta12*r1*r2*(eta13*r1*r3 + pow(r3,2) - eta14*r1*r4 + 
eta34*r3*r4))/
          (sqrt(pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
            (pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
            sqrt(1 - pow(pow(r1,2) + eta12*r1*r2 + eta13*r1*r3 - 
eta23*r2*r3,2)/
               ((pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
                 (pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))))*
            sqrt(pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2))*
            sqrt(1 - pow(pow(r1,2) + eta13*r1*r3 + eta14*r1*r4 - 
eta34*r3*r4,2)/
               ((pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
                 (pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2)))))) - 
       sqrt(pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))*
        acos((pow(r2,2)*pow(r3,2) - 
pow(eta23,2)*pow(r2,2)*pow(r3,2) - 
            eta14*r1*pow(r2,2)*r4 - 2*eta14*eta23*r1*r2*r3*r4 + 
            eta23*eta24*pow(r2,2)*r3*r4 + eta34*pow(r2,2)*r3*r4 - 
            eta14*r1*pow(r3,2)*r4 + eta24*r2*pow(r3,2)*r4 + 
            eta23*eta34*r2*pow(r3,2)*r4 + 
            eta13*r1*r3*(pow(r2,2) + eta23*r2*r3 + eta24*r2*r4 - 
eta34*r3*r4) + 
            eta12*r1*r2*(eta23*r2*r3 + pow(r3,2) - eta24*r2*r4 + 
eta34*r3*r4))/
          (sqrt(pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
            (pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))*
            sqrt(1 - pow(eta12*r1*r2 + pow(r2,2) - eta13*r1*r3 + 
eta23*r2*r3,2)/
               ((pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
                 (pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))))*
            sqrt(pow(r2,2) + 2*eta24*r2*r4 + pow(r4,2))*
            sqrt(1 - pow(pow(r2,2) + eta23*r2*r3 + eta24*r2*r4 - 
eta34*r3*r4,2)/
               ((pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))*
                 (pow(r2,2) + 2*eta24*r2*r4 + pow(r4,2)))))) - 
       sqrt(pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
        acos((-((eta23*eta24 + eta34)*pow(r2,2)*r3*r4) + 
            pow(r1,2)*(-((-1 + pow(eta12,2))*pow(r2,2)) - (eta13*eta14 
+ eta34)*r3*r4 + 
               r2*(eta12*eta13*r3 + eta23*r3 + eta12*eta14*r4 + eta24*r4)) + 
            r1*r2*(eta14*(r2 + eta23*r3)*r4 + eta13*r3*(r2 + eta24*r4) + 
               eta12*(eta23*r2*r3 + eta24*r2*r4 - 2*eta34*r3*r4)))/
          ((pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
            sqrt(pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
            sqrt(1 - pow(pow(r1,2) + eta12*r1*r2 + eta13*r1*r3 - 
eta23*r2*r3,2)/
               ((pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
                 (pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))))*
            sqrt(pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2))*
            sqrt(1 - pow(pow(r1,2) + eta12*r1*r2 + eta14*r1*r4 - 
eta24*r2*r4,2)/
               ((pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
                 (pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2)))))) - 
       sqrt(pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2))*
        acos((-(eta23*r2*r3*(pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2))) + 
            eta12*r1*r2*(-(eta13*r1*r3) + r4*(eta14*r1 + eta34*r3 + r4)) + 
            r4*(eta24*r2*(pow(r1,2) + eta13*r1*r3 + eta14*r1*r4 - 
eta34*r3*r4) + 
               r1*(-((-1 + pow(eta14,2))*r1*r4) + eta13*r3*(eta14*r1 + r4) 
+ 
                  eta34*r3*(r1 + eta14*r4))))/
          (sqrt(pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
            sqrt(pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
            (pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2))*
            sqrt(1 - pow(pow(r1,2) + eta12*r1*r2 + eta14*r1*r4 - 
eta24*r2*r4,2)/
               ((pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
                 (pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2))))*
            sqrt(1 - pow(pow(r1,2) + eta13*r1*r3 + eta14*r1*r4 - 
eta34*r3*r4,2)/
               ((pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
                 (pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2)))))) - 
       sqrt(pow(r2,2) + 2*eta24*r2*r4 + pow(r4,2))*
        acos((-(eta13*r1*r3*(pow(r2,2) + 2*eta24*r2*r4 + pow(r4,2))) + 
            eta12*r1*r2*(-(eta23*r2*r3) + r4*(eta24*r2 + eta34*r3 + r4)) + 
            r4*(eta14*r1*(pow(r2,2) + eta23*r2*r3 + eta24*r2*r4 - 
eta34*r3*r4) + 
               r2*(-((-1 + pow(eta24,2))*r2*r4) + eta23*r3*(eta24*r2 + r4) 
+ 
                  eta34*r3*(r2 + eta24*r4))))/
          (sqrt(pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
            sqrt(pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))*
            (pow(r2,2) + 2*eta24*r2*r4 + pow(r4,2))*
            sqrt(1 - pow(eta12*r1*r2 + pow(r2,2) - eta14*r1*r4 + 
eta24*r2*r4,2)/
               ((pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
                 (pow(r2,2) + 2*eta24*r2*r4 + pow(r4,2))))*
            sqrt(1 - pow(pow(r2,2) + eta23*r2*r3 + eta24*r2*r4 - 
eta34*r3*r4,2)/
               ((pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))*
                 (pow(r2,2) + 2*eta24*r2*r4 + pow(r4,2)))))) - 
       sqrt(pow(r3,2) + 2*eta34*r3*r4 + pow(r4,2))*
        acos((-(eta12*r1*r2*(pow(r3,2) + 2*eta34*r3*r4 + pow(r4,2))) + 
            eta13*r1*r3*(-(eta23*r2*r3) + r4*(eta24*r2 + eta34*r3 + r4)) + 
            r4*(eta14*r1*(eta23*r2*r3 + pow(r3,2) - eta24*r2*r4 + 
eta34*r3*r4) + 
               r3*(-((-1 + pow(eta34,2))*r3*r4) + eta23*r2*(eta34*r3 + r4) 
+ 
                  eta24*r2*(r3 + eta34*r4))))/
          (sqrt(pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
            sqrt(pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))*
            (pow(r3,2) + 2*eta34*r3*r4 + pow(r4,2))*
            sqrt(1 - pow(eta13*r1*r3 + pow(r3,2) - eta14*r1*r4 + 
eta34*r3*r4,2)/
               ((pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
                 (pow(r3,2) + 2*eta34*r3*r4 + pow(r4,2))))*
            sqrt(1 - pow(eta23*r2*r3 + pow(r3,2) - eta24*r2*r4 + 
eta34*r3*r4,2)/
               ((pow(r2,2) + 2*eta23*r2*r3 + pow(r3,2))*
                 (pow(r3,2) + 2*eta34*r3*r4 + pow(r4,2))))))))/
   pow(-((-1 + pow(eta23,2) + pow(eta24,2) + 2*eta23*eta24*eta34 + 
pow(eta34,2))*
        pow(r2,2)*pow(r3,2)*pow(r4,2)) + 
     2*r1*r2*r3*r4*((eta13 - eta13*pow(eta24,2) + eta14*(eta23*eta24 + 
eta34) + 
           eta12*(eta23 + eta24*eta34))*r2*r4 + 
        r3*((eta14 - eta14*pow(eta23,2) + eta13*(eta23*eta24 + eta34) + 
              eta12*(eta24 + eta23*eta34))*r2 + 
           (eta12 - eta12*pow(eta34,2) + eta14*(eta24 + eta23*eta34) + 
              eta13*(eta23 + eta24*eta34))*r4)) - 
     pow(r1,2)*((-1 + pow(eta13,2) + pow(eta14,2) + 2*eta13*eta14*eta34 
+ 
           pow(eta34,2))*pow(r3,2)*pow(r4,2) - 
        2*r2*r3*r4*((eta13*eta14*eta23 + eta24 - pow(eta13,2)*eta24 + 
eta23*eta34 + 
              eta12*(eta14 + eta13*eta34))*r3 + 
           (eta23 - pow(eta14,2)*eta23 + eta24*(eta13*eta14 + eta34) + 
              eta12*(eta13 + eta14*eta34))*r4) + 
        pow(r2,2)*((-1 + pow(eta12,2) + pow(eta13,2) + 
2*eta12*eta13*eta23 + 
              pow(eta23,2))*pow(r3,2) - 
           2*(eta12*eta14*eta23 + eta23*eta24 + eta13*(eta14 + eta12*eta24) + 
eta34 - 
              pow(eta12,2)*eta34)*r3*r4 + 
           (-1 + pow(eta12,2) + pow(eta14,2) + 2*eta12*eta14*eta24 + 
pow(eta24,2))*
            pow(r4,2))),0.16666666666666666);
            
            return result;
       
       }


                         
void RK4s(double prev_radii[4], double prev_etas[6], double result_radii[4], double result_etas[6], double h, bool MIN, bool RK4) {
       
       double k1[6], k2[6], k3[6], k4[6];
       double temp_radii[4];
       double temp_etas[6];
       int k;
       NewtonsMethod *nm = new NewtonsMethod(DoubleTetraNEHR, 4);
       
       if(RK4) {       
       
///////////////////////////
//  RUNGA-KUTTA METHOD
///////////////////////////

//     K1:
       derivative_s(prev_radii, prev_etas, k1);
       
//     K2:       
       for(k=0;k<=5;k++) {
           global_etas[k]=prev_etas[k]+.5*h*k1[k];
           }
       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, temp_radii, NMETHOD_MIN);
             
       
       derivative_s(temp_radii, global_etas, k2);

//     K3:       
       for(k=0;k<=5;k++) {
           global_etas[k]=prev_etas[k]+.5*h*k2[k];
           }

       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, temp_radii, NMETHOD_MIN);
             
       
       derivative_s(temp_radii, global_etas, k3);

//     K4:       
       for(k=0;k<=5;k++) {
           global_etas[k]=prev_etas[k]+h*k3[k];
           }
       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, temp_radii, NMETHOD_MIN);
             
       
       derivative_s(temp_radii, global_etas, k4);
       
//     RUNGA-KUTTA STEP:
       for(k=0; k<=5; k++) {
       result_etas[k]=prev_etas[k]+(0.166666666666666)*h*(k1[k]+2*k2[k]+2*k3[k]+k4[k]);
//       printf("RK4 step: %.10f\n",(k1[k]+2*k2[k]+2*k3[k]+k4[k]));
       }
       }
       
       else if(!RK4) {

///////////////////////////
//  EULER FORWARD METHOD
///////////////////////////       
       result_etas[0]=prev_etas[0]+h*C1223(prev_radii,prev_etas);
       result_etas[1]=prev_etas[1]+h*C1323(prev_radii,prev_etas);
       result_etas[2]=prev_etas[2]+h*C1423(prev_radii,prev_etas);
       result_etas[3]=prev_etas[3]+h*C2323(prev_radii,prev_etas);
       result_etas[4]=prev_etas[4]+h*C2423(prev_radii,prev_etas);
       result_etas[5]=prev_etas[5]+h*C3423(prev_radii,prev_etas);

///////////////////////////////
//     END EULER FORWARD
///////////////////////////////
       }
                               
                               
       if(MIN) {
       
       for(k=0;k<=5;k++) {
           global_etas[k]=result_etas[k];
           }
       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, result_radii, NMETHOD_MIN);
       }
       
       else if(!MIN) {
            for(k=0;k<=3;k++) {
                  result_radii[k]=prev_radii[k];
                  }
            }
       
       return;
       }
       
       
       
void RK4t(double prev_radii[4], double prev_etas[6], double result_radii[4], double result_etas[6], double h, bool MIN, bool RK4) {

       double k1[6], k2[6], k3[6], k4[6];
       double temp_radii[4];
       double temp_etas[6];
       int k;
       NewtonsMethod *nm = new NewtonsMethod(DoubleTetraNEHR, 4);
       
       if(RK4) {       
       
///////////////////////////
//  RUNGA-KUTTA METHOD
///////////////////////////

//     K1:
       derivative_t(prev_radii, prev_etas, k1);
       
//     K2:       
       for(k=0;k<=5;k++) {
           global_etas[k]=prev_etas[k]+.5*h*k1[k];
           }
       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, temp_radii, NMETHOD_MIN);
       
       derivative_t(temp_radii, global_etas, k2);

//     K3:       
       for(k=0;k<=5;k++) {
           global_etas[k]=prev_etas[k]+.5*h*k2[k];
           }
       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, temp_radii, NMETHOD_MIN);
       
       derivative_t(temp_radii, global_etas, k3);

//     K4:       
       for(k=0;k<=5;k++) {
           global_etas[k]=prev_etas[k]+h*k3[k];
           }
       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, temp_radii, NMETHOD_MIN);
       
       derivative_t(temp_radii, global_etas, k4);
       
//     RUNGA-KUTTA STEP:
       for(k=0; k<=5; k++) {
       result_etas[k]=prev_etas[k]+(0.166666666666666)*h*(k1[k]+2*k2[k]+2*k3[k]+k4[k]);
//       printf("RK4 step: %.10f\n",(k1[k]+2*k2[k]+2*k3[k]+k4[k]));
       }
       }
       
       else if(!RK4)  {
///////////////////////////
//  EULER FORWARD METHOD
/////////////////////////// 
       result_etas[0]=prev_etas[0]+h*C1224(prev_radii,prev_etas);
       result_etas[1]=prev_etas[1]+h*C1324(prev_radii,prev_etas);
       result_etas[2]=prev_etas[2]+h*C1424(prev_radii,prev_etas);
       result_etas[3]=prev_etas[3]+h*C2324(prev_radii,prev_etas);
       result_etas[4]=prev_etas[4]+h*C2424(prev_radii,prev_etas);
       result_etas[5]=prev_etas[5]+h*C3424(prev_radii,prev_etas);
///////////////////////////////
//     END EULER FORWARD
///////////////////////////////
       }
       
       if(MIN) {
       
       for(k=0;k<=5;k++) {
           global_etas[k]=result_etas[k];
           }
       nm->setStoppingGradientLength(NMaccuracy);
       nm->optimize(prev_radii, result_radii, NMETHOD_MIN);
       }
       
       else if(!MIN) {
            for(k=0;k<=3;k++) {
                  result_radii[k]=prev_radii[k];
                  }
            }
       
       return;
       }

void derivative_s(double radii[4],double etas[6], double der_etas[6]) {
     // minimize the radii at this step ???
     
     der_etas[0]=C1223(radii,etas);
     der_etas[1]=C1323(radii,etas);
     der_etas[2]=C1423(radii,etas);
     der_etas[3]=C2323(radii,etas);
     der_etas[4]=C2423(radii,etas);
     der_etas[5]=C3423(radii,etas);
     
     return;
     }
     
void derivative_t(double radii[4],double etas[6], double der_etas[6]) {
     // minimize the radii at this step ???
     
     der_etas[0]=C1224(radii,etas);
     der_etas[1]=C1324(radii,etas);
     der_etas[2]=C1424(radii,etas);
     der_etas[3]=C2324(radii,etas);
     der_etas[4]=C2424(radii,etas);
     der_etas[5]=C3424(radii,etas);
     
     return;
     }


double C1223(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(1/(2*r1*r2))*(-((sqrt(pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
       (r2 + eta23*r3))/
     ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta23*r2*r3 + 
         pow(r3,2)))));
         
       return result;
       
       }
       
       
       
double C1323(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
              
       result=(1/(2*r1*r3))*((sqrt(pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
     (r2*r4*(r2*(eta12*r3 - eta14*eta23*eta34*r3 - 
             eta14*eta23*r4 + eta12*eta34*r4) + 
          r3*(eta12*eta23*r3 - eta14*eta34*r3 - 
             eta14*r4 + eta12*eta23*eta34*r4)) + 
       pow(r1,2)*((eta14*eta23 - eta12*eta34)*
           pow(r3,2) + 
          (-(eta12*eta23) + eta14*eta34)*r2*r4 + 
          r3*(eta14*r2 - eta12*eta23*eta34*r2 - 
             eta12*r4 + eta14*eta23*eta34*r4)) + 
       r1*(pow(r2,2)*(eta12*eta14*r3 - eta23*eta34*r3 - 
             eta23*r4 + eta12*eta14*eta34*r4) + 
          r3*r4*(eta23*r3 - eta12*eta14*eta34*r3 - 
             eta12*eta14*r4 + eta23*eta34*r4) + 
          (eta12*eta14*eta23 - eta34)*r2*
           (pow(r3,2) - pow(r4,2)))))/
   ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta23*r2*r3 + 
       pow(r3,2))*(r3*r4*
        ((eta13 + eta14*eta34)*r3 + 
          (eta14 + eta13*eta34)*r4) + 
       pow(r1,2)*((eta14 + eta13*eta34)*r3 + 
          (eta13 + eta14*eta34)*r4) + 
       r1*((eta13*eta14 + eta34)*pow(r3,2) + 
          (eta13*eta14 + eta34)*pow(r4,2) + 
          2*r3*(r4 + eta13*eta14*eta34*r4)))));
         
       return result;
       }
       
       
       
double C1423(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(1/(2*r1*r4))*(((r2*r3*((eta12 + eta13*eta23)*r2 + 
          (eta13 + eta12*eta23)*r3) + 
       pow(r1,2)*((eta13 + eta12*eta23)*r2 + 
          (eta12 + eta13*eta23)*r3) + 
       r1*((eta12*eta13 + eta23)*pow(r2,2) + 
          (eta12*eta13 + eta23)*pow(r3,2) + 
          2*r2*(r3 + eta12*eta13*eta23*r3)))*
     (eta34*r3 + r4)*sqrt(pow(r1,2) + 2*eta14*r1*r4 + 
       pow(r4,2)))/
   ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta23*r2*r3 + 
       pow(r3,2))*(r3*r4*
        ((eta13 + eta14*eta34)*r3 + 
          (eta14 + eta13*eta34)*r4) + 
       pow(r1,2)*((eta14 + eta13*eta34)*r3 + 
          (eta13 + eta14*eta34)*r4) + 
       r1*((eta13*eta14 + eta34)*pow(r3,2) + 
          (eta13*eta14 + eta34)*pow(r4,2) + 
          2*r3*(r4 + eta13*eta14*eta34*r4)))));
         
       return result;
       }
       
       
       
double C2323(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       result=(1/(2*r2*r3));
         
       return result;
       }
       
       
       
double C2423(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=0;
         
       return result;
       }
       
       
       
double C3423(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(-(1/(2*r3*r4))*(((r2*r3*((eta12 + eta13*eta23)*r2 + 
            (eta13 + eta12*eta23)*r3) + 
         pow(r1,2)*((eta13 + eta12*eta23)*r2 + 
            (eta12 + eta13*eta23)*r3) + 
         r1*((eta12*eta13 + eta23)*pow(r2,2) + 
            (eta12*eta13 + eta23)*pow(r3,2) + 
            2*r2*(r3 + eta12*eta13*eta23*r3)))*
       (eta14*r1 + r4)*sqrt(pow(r3,2) + 2*eta34*r3*r4 + 
         pow(r4,2)))/
     ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta23*r2*r3 + 
         pow(r3,2))*(r3*r4*
          ((eta13 + eta14*eta34)*r3 + 
            (eta14 + eta13*eta34)*r4) + 
         pow(r1,2)*((eta14 + eta13*eta34)*r3 + 
            (eta13 + eta14*eta34)*r4) + 
         r1*((eta13*eta14 + eta34)*pow(r3,2) + 
            (eta13*eta14 + eta34)*pow(r4,2) + 
            2*r3*(r4 + eta13*eta14*eta34*r4))))));
         
       return result;
       }
       
       
       
double C1224(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(1/(2*r1*r2))*(-((sqrt(pow(r1,2) + 2*eta12*r1*r2 + pow(r2,2))*
       (r2 + eta24*r4))/
     ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta24*r2*r4 + 
         pow(r4,2)))));
         
       return result;
       }
       
       
       
double C1324(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(1/(2*r1*r3))*((sqrt(pow(r1,2) + 2*eta13*r1*r3 + pow(r3,2))*
     (r3 + eta34*r4)*(r2*r4*
        ((eta12 + eta14*eta24)*r2 + 
          (eta14 + eta12*eta24)*r4) + 
       pow(r1,2)*((eta14 + eta12*eta24)*r2 + 
          (eta12 + eta14*eta24)*r4) + 
       r1*((eta12*eta14 + eta24)*pow(r2,2) + 
          (eta12*eta14 + eta24)*pow(r4,2) + 
          2*r2*(r4 + eta12*eta14*eta24*r4))))/
   ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta24*r2*r4 + 
       pow(r4,2))*(r3*r4*
        ((eta13 + eta14*eta34)*r3 + 
          (eta14 + eta13*eta34)*r4) + 
       pow(r1,2)*((eta14 + eta13*eta34)*r3 + 
          (eta13 + eta14*eta34)*r4) + 
       r1*((eta13*eta14 + eta34)*pow(r3,2) + 
          (eta13*eta14 + eta34)*pow(r4,2) + 
          2*r3*(r4 + eta13*eta14*eta34*r4)))));
         
       return result;
       }
       
       
       
double C1424(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(1/(2*r1*r4))*((sqrt(pow(r1,2) + 2*eta14*r1*r4 + pow(r4,2))*
     (r2*r3*(r4*(-(eta13*r3) + eta12*eta24*eta34*r3 + 
             eta12*eta24*r4 - eta13*eta34*r4) + 
          r2*(-(eta13*eta24*r3) + eta12*eta34*r3 + 
             eta12*r4 - eta13*eta24*eta34*r4)) + 
       pow(r1,2)*(r4*(eta13*r2 - eta12*eta24*eta34*r2 + 
             eta13*eta24*r4 - eta12*eta34*r4) + 
          r3*(-(eta12*eta24*r2) + eta13*eta34*r2 - 
             eta12*r4 + eta13*eta24*eta34*r4)) + 
       r1*(r3*r4*(-(eta12*eta13*r3) + eta24*eta34*r3 + 
             eta24*r4 - eta12*eta13*eta34*r4) + 
          pow(r2,2)*(-(eta24*r3) + 
             eta12*eta13*eta34*r3 + eta12*eta13*r4 - 
             eta24*eta34*r4) - 
          (eta12*eta13*eta24 - eta34)*r2*
           (pow(r3,2) - pow(r4,2)))))/
   ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta24*r2*r4 + 
       pow(r4,2))*(r3*r4*
        ((eta13 + eta14*eta34)*r3 + 
          (eta14 + eta13*eta34)*r4) + 
       pow(r1,2)*((eta14 + eta13*eta34)*r3 + 
          (eta13 + eta14*eta34)*r4) + 
       r1*((eta13*eta14 + eta34)*pow(r3,2) + 
          (eta13*eta14 + eta34)*pow(r4,2) + 
          2*r3*(r4 + eta13*eta14*eta34*r4)))));
         
       return result;
       }
       
       
       
double C2324(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=0;
         
       return result;
       }
       
       
       
double C2424(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(1/(2*r2*r4));
         
       return result;
       }
       
       
       
double C3424(double radii[4],double etas[6]) {
       double result=0;
       double eta12, eta13, eta14, eta23, eta24, eta34, r1, r2, r3, r4;
       eta12=etas[0];
       eta13=etas[1];
       eta14=etas[2];
       eta23=etas[3];
       eta24=etas[4];
       eta34=etas[5];
       r1=radii[0];
       r2=radii[1];
       r3=radii[2];
       r4=radii[3];
       
       result=(1/(2*r3*r4))*(-(((eta13*r1 + r3)*sqrt(pow(r3,2) + 2*eta34*r3*r4 + 
         pow(r4,2))*(r2*r4*
          ((eta12 + eta14*eta24)*r2 + 
            (eta14 + eta12*eta24)*r4) + 
         pow(r1,2)*((eta14 + eta12*eta24)*r2 + 
            (eta12 + eta14*eta24)*r4) + 
         r1*((eta12*eta14 + eta24)*pow(r2,2) + 
            (eta12*eta14 + eta24)*pow(r4,2) + 
            2*r2*(r4 + eta12*eta14*eta24*r4))))/
     ((eta12*r1 + r2)*sqrt(pow(r2,2) + 2*eta24*r2*r4 + 
         pow(r4,2))*(r3*r4*
          ((eta13 + eta14*eta34)*r3 + 
            (eta14 + eta13*eta34)*r4) + 
         pow(r1,2)*((eta14 + eta13*eta34)*r3 + 
            (eta13 + eta14*eta34)*r4) + 
         r1*((eta13*eta14 + eta34)*pow(r3,2) + 
            (eta13*eta14 + eta34)*pow(r4,2) + 
            2*r3*(r4 + eta13*eta14*eta34*r4))))));
         
       return result;
       }         
