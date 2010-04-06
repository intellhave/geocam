#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <iostream>
#include <cmath>
#include <vector>

#include "Matrix.h"

double magnitude (Matrix<double> vd){

  double mag = 0;
  Matrix<double> vdt = vd.transpose();
  
  vdt = vd * vdt;
  mag = vdt[0][0];
  mag = sqrt(mag);
  return mag;
}

Matrix<double> projection (Matrix<double> va, Matrix<double> vb){

  Matrix<double> vec(1, vb.getCol());
  double proj = 0;
  double tude = 0;
  Matrix<double> vat = va.transpose();
  Matrix<double> vbt = vb.transpose();
  
//  Matrix<double> vatemp(1,1);
//  Matrix<double> vbtemp (1,1);
  
  
  vbt = va * vbt;
  vat = va * vat;
  proj = vbt[0][0];
  tude = vat[0][0];
  vec = va * (proj / tude);
  
  return vec;
}

Matrix<double> Gram_Schmidt (Matrix<double> u, Matrix<double> v){

  Matrix<double> you(v.getRow(), v.getCol());

  //Matrix<double> vt = v.transpose();
  //Matrix<double> ut = u.transpose();

  Matrix<double> va(1, v.getCol());
  Matrix<double> vb(1, v.getCol());
  Matrix<double> vc(1, v.getCol());
  Matrix<double> vd(1, v.getCol());

  for (int i = 0; i < v.getRow(); i++){
   

    if (i == 0){


      for (int q = 0; q < v.getCol(); q++){
        vd[0][q] = v[i][q];
        }

      double temp1 = magnitude(vd);

      vd = vd * (1 / temp1);

      for (int s = 0; s < u.getCol(); s++){
        u[i][s] = vd[0][s];
      }

    }
    
    else {
  
    for (int j = 0; j < v.getCol(); j++){
      vb[0][j] = v[i][j];
//      vc[0][j] = u[i][j];
      vd[0][j] = 0;
    }
    
    for (int k = 0; k < i; k++){
      for (int m = 0; m < v.getCol(); m++){
        va[0][m] = u[k][m];
      }
      vc = projection (va, vb);
      vd = vd + vc;
    }
    
  //  for (int n = 0; n < vt.getRow(); n++){
  //    double * vd = vt[n];
  //  }
    
    vd = vd * (-1);
    vd = vd + vb;
    
    double temp = magnitude(vd);
    vd = vd * (1 / temp);
    
    for (int p = 0; p < v.getCol(); p++){
      u[i][p] = vd[0][p];
   }
    
  } //close else
  } //close i
  
  you = u;

  
  //v = vt.transpose();
  //u = ut.transpose();
  return you;
} //close GS

int main(){

//Matrix<double> m(2,2);
//m[0][0] = 1;
//m[0][1] = 2;
//
//
//double * n = m[0];
//printf ("%f %f \n", n[0], n[1]);

  srand ( time(NULL) );

  int EYE = 1000;
  int tempt = rand();

  Matrix<double> mat(EYE,EYE);

  for(int i = 0; i < EYE; i++){
    for(int j = 0; j < EYE; j++){
      mat[i][j] = (double)rand() / 32767.001;
    }
  }

  Matrix<double> v(EYE,EYE);
  Matrix<double> u(EYE,EYE);
  Matrix<double> yall(EYE,EYE);
  Matrix<double> vosotros(EYE,EYE);

  for (int i = 0; i < EYE; i++) {
    for (int j = 0; j < EYE; j++) {
      v[i][j] = mat[i][j];
    }
  }

 // Matrix<double> v{EYE, EYE, mat3};
 // Matrix<double> u = {EYE, EYE, mat4};

  yall = Gram_Schmidt(u, v);
  vosotros = yall.transpose();
  
  
  vosotros = vosotros * yall;
  
  
  double temp87=0;
  double temp88=0;
  printf ("Orthanormal basis = \n");
  for (int i = 0; i < vosotros.getRow(); i++){
    temp87+=vosotros[i][i];
    for (int j = 0; j < vosotros.getCol(); j++){
      //printf ("%.5f", vosotros[i][j]);
      temp88+=vosotros[i][j];
    }
    //printf("\n");
  }
  printf("%.12f %.12f\n",temp87,temp88);

  system("PAUSE");
  return 0;
}
