/*=========================================================================
 ||Class: Matrix
 ||Authors: Kurtis Norwood (knorwood@email.arizona.edu)
 ||         Alex Henniges (henniges@email.arizona.edu)
 ||
 ||Description: This file defines the Matrix class as a fixed size
 ||     two-dimensional array of any generic element T. A user specifies the
 ||     row and column size and the type that the matrix will contain at
 ||     construction. The Matrix class serves the purpose of providing a
 ||     consistent solution to all problems involving fixed-size two-dimensional
 ||     arrays in a way that "hides" memory allocation and access concerns.
 ||
 ||     Once a matrix object, m, is created, access to its elements is the same
 ||     as access to elements of an array. For example, access to an element
 ||     at row i and column j is done by m[i][j]. The assignment operator
 ||     n = m is also granted for every Matrix.
 ||
 ||     This class also utilizes what is known as template specialization.
 ||     The specific case when a matrix is a set of double-precision numbers
 ||     is specialized to include additional functionality. For example, two
 ||     matrices of doubles can be added together and the determinant of such
 ||     a matrix can be calculated.
 ||
 ||Bugs/TODO:
 *=========================================================================*/
#ifndef MATRIX_H_
#define MATRIX_H_
#include <stdlib.h>

template<class T>
class Matrix{
  private:
    T ** mat;
    int col, row;

    void freeMat() {
      for (int i = 0; i < row; i++) {
        free(mat[i]);
      }
      if(mat != NULL) {
        free(mat);
        mat = NULL;
      }
    }

  public:
    Matrix() {
      row = 0;
      col = 0;
      mat = NULL;
    }
    
    Matrix(int row, int col) {
      if (row == 0 || col == 0) {
        printf("you need to give positive row and column values\n");
        system("PAUSE");
        exit(1);
      }

      mat = (T**) malloc(sizeof(T*) * row);
      for (int i = 0; i < row; i++) {
        mat[i] = (T*) malloc(sizeof(T) * col);
      }
      this->row = row;
      this->col = col;
    }

    Matrix(const Matrix &p) {
      mat = (T**) malloc(sizeof(T*) * p.row);
      for (int i = 0; i < p.row; i++) {
        mat[i] = (T*) malloc(sizeof(T) * p.col);
      }
      col = p.col;
      row = p.row;
      for (int i = 0; i < p.row; i++) {
        for (int j = 0; j < p.col; j++) {
          mat[i][j] = p.mat[i][j];
        }
      }
    }

    ~Matrix() {
      freeMat();
    }

    bool operator=(Matrix other) {
      freeMat();

      row = other.row;
      col = other.col;

      mat = (T **) malloc(sizeof(T*) * row);
      for (int i = 0; i < row; i++) {
        mat[i] = (T*) malloc(sizeof(T) * col);
      }

      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          mat[i][j] = other.mat[i][j];
        }
      }

      return true;
    }

    T * operator[](int i) {return mat[i];}

    int getRow() {return row;}
    int getCol() {return col;}
};

template<>
class Matrix<double>{
  private:
    double ** mat;
    int col, row;

    void freeMat() {
      for (int i = 0; i < row; i++) {
        free(mat[i]);
      }
      if(mat != NULL) {
        free(mat);
        mat = NULL;
      }
    }
    
    double subdeterminant(int xrow, int xcol){
      if(row == 2) {
        return mat[(xrow + 1)%2][(xcol + 1)%2];
      }
      if(row == 3) {
        return powM1(xrow + xcol) * (
	         mat[(xrow+1)%3][(xcol+1)%3]*mat[(xrow+2)%3][(xcol+2)%3]
	       - mat[(xrow+1)%3][(xcol+2)%3]*mat[(xrow+2)%3][(xcol+1)%3]);
      } else {
        Matrix<double> temp(row - 1, col - 1);
        int k, l;
        for(int i = 0, k = 0; i < row; i++) {
          if(i != row) {
	         for(int j = 0, l = 0; j < col; j++) {
	           if(j != col) {
	             temp[k][l] = mat[i][j];
	             l++;
	           }
	         }
	         k++;
          }
        }
        double result = 0;
        for(int j = 0; j < row - 1; j++) {
	       result += powM1(j) * temp[0][j] * temp.subdeterminant(0, j);
        }
        return result;
      }
    }
    
    int powM1(int i) {
      return (1 - 2*(i % 2));
    }

  public:
    Matrix() {
      row = 0;
      col = 0;
      mat = NULL;
    }
    
    Matrix(int row, int col) {
      if (row == 0 || col == 0) {
        printf("you need to give positive row and column values\n");
        system("PAUSE");
        exit(1);
      }

      mat = (double**) malloc(sizeof(double*) * row);
      for (int i = 0; i < row; i++) {
        mat[i] = (double*) malloc(sizeof(double) * col);
      }
      this->row = row;
      this->col = col;
    }

    Matrix(const Matrix &p) {
      mat = (double**) malloc(sizeof(double*) * p.row);
      for (int i = 0; i < p.row; i++) {
        mat[i] = (double*) malloc(sizeof(double) * p.col);
      }
      col = p.col;
      row = p.row;
      for (int i = 0; i < p.row; i++) {
        for (int j = 0; j < p.col; j++) {
          mat[i][j] = p.mat[i][j];
        }
      }
    }

    ~Matrix() {
      freeMat();
    }

    bool operator=(Matrix other) {
      freeMat();

      row = other.row;
      col = other.col;

      mat = (double **) malloc(sizeof(double*) * row);
      for (int i = 0; i < row; i++) {
        mat[i] = (double*) malloc(sizeof(double) * col);
      }

      for (int i = 0; i < row; i++) {
        for (int j = 0; j < col; j++) {
          mat[i][j] = other.mat[i][j];
        }
      }

      return true;
    }


    double * operator[](int i) {return mat[i];}
    
    Matrix operator+(Matrix& m) {
      if(m.row != row || m.col != col) {
        printf("you can only add matrices of the same size\n");
        system("PAUSE");
        exit(1);
      }
      Matrix<double> temp(row, col);
      for(int i = 0; i < row; i++) {
        for(int j = 0; j < col; j++) {
          temp[i][j] = mat[i][j] + m[i][j];
        }
      }
      return temp;
    }
    
    Matrix operator-(Matrix& m) {
      if(m.row != row || m.col != col) {
        printf("you can only add matrices of the same size\n");
        system("PAUSE");
        exit(1);
      }
      Matrix<double> temp(row, col);
      for(int i = 0; i < row; i++) {
        for(int j = 0; j < col; j++) {
          temp[i][j] = mat[i][j] - m[i][j];
        }
      }
      return temp;
    }
    
    Matrix operator*(Matrix& m) {
      if(col != m.row) {
        printf("the matrix on the right must have the same number of rows as the number of columns");
        printf("of the matrix on the left\n");
        system("PAUSE");
        exit(1);
      }
      Matrix<double> temp(row, m.col);
      for(int i = 0; i < row; i++) {
        for(int j = 0; j < m.col; j++) {
          temp[i][j] = 0;
          for(int k = 0; k < col; k++) {
            temp[i][j] += mat[i][k] * m[k][j];
          }
        }
      }
      return temp;
    }
    
    Matrix operator*(double scalar){
      Matrix<double> temp(row, col);
      for(int i = 0; i < row; i++) {
        for(int j = 0; j < col; j++) {
          temp[i][j] = scalar * mat[i][j];
        }
      }
      return temp;
    }
    
    Matrix transpose() {
      Matrix temp(col, row);
      for(int i = 0; i < col; i++) {
        for(int j = 0; j < row; j++) {
          temp[i][j] = mat[j][i];
        }
      }
      return temp;
    }
    Matrix adjoint(){
      if(row != col) {
        printf("the matrix must be a square matrix\n");
        system("PAUSE");
        exit(1);
      }
      Matrix<double> adj(row, col);
      int i, j;
      for(i = 0; i < row; i++) {
        for(j = 0; j < col; j++) {
          adj[i][j] = powM1(i + j) * subdeterminant(j, i);
        }
      }
      return adj;
    }
    
    double determinant() {
      if(row != col) {
        printf("the matrix must be a square matrix\n");
        system("PAUSE");
        exit(1);
      }
      if(row == 2) {
        return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
      } else {
        int j;
        double result = 0;
        for(j = 0; j < row; j++) {
	       result += powM1(j) * mat[0][j] * subdeterminant(0, j);
        }
        return result;
      }
    }
    
    void print(FILE* out){
      for(int i = 0; i < row; i++) {
        for(int j = 0; j < col; j++) {
          fprintf(out, "%f   ", mat[i][j]);
        }
        fprintf(out, "\n");
      }
    }

    int getRow() {return row;}
    int getCol() {return col;}
};

#endif
