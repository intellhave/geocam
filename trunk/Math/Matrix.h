#ifndef MATRIX_H_
#define MATRIX_H_
#include <stdlib.h>
#include <iostream>

template<class T>
class Matrix{
  private:
    T ** mat;
    int col, row;

    void freeMat() {
      for (int i = 0; i < row; i++) {
        free(mat[i]);
      }
      free(mat);
    }

  public:
    Matrix(int row, int col) {
      if (row == 0 || col == 0) {
        cout << "you need to give positive row and column values\n";
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
      free(mat);
    }

  public:
    Matrix(int row, int col) {
      if (row == 0 || col == 0) {
        cout << "you need to give positive row and column values\n";
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

    Matrix operator+(Matrix& m);
    Matrix operator*(Matrix& m);

    double * operator[](int i) {return mat[i];}

    int getRow() {return row;}
    int getCol() {return col;}
};

Matrix<double> Matrix<double>::operator+(Matrix<double>& m) {
  if(m.row != row || m.col != col) {
    cout << "you can only add matrices of the same size\n";
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

Matrix<double> Matrix<double>::operator*(Matrix<double>& m) {
  if(col != m.row) {
    cout << "the matrix on the right must have the same number of rows as the number of columns";
    cout << "of the matrix on the left\n";
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
#endif
