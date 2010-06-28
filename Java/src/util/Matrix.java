package util;

public class Matrix implements Cloneable{
  public double[][] m;
  private int rows;
  private int cols;
  
  public Matrix() {
    rows = 0;
    cols = 0;
    m = new double[rows][cols];
  }
  
  public Matrix(int numRows, int numCols) {
    rows = numRows;
    cols = numCols;
    m = new double[rows][cols];
  }
  
  public Matrix(Matrix n) {
    rows = n.rows;
    cols = n.cols;
    this.m = n.m.clone();
  }
  
  public Matrix(double[] vector) {
    rows = vector.length;
    cols = 1;
    m = new double[rows][cols];
    for(int i = 0; i < rows; i++) {
      m[i][0] = vector[i];
    }
  }
  
  public Matrix transpose() {
    Matrix t = new Matrix(cols, rows);
    for(int i = 0; i < cols; i++) {
      for(int j = 0; j < rows; j++) {
        t.m[i][j] = this.m[j][i];
      }
    }
    return t;
  }
  
  public Matrix multiply(Matrix other) {
    if(this.cols != other.rows) {
      System.err.print("Matrices cannot be multiplied: dimensions don't match.");
      return null;
    }
    Matrix tmp = new Matrix(this.rows, other.cols);
    for(int i = 0; i < this.rows; i++) {
      for(int j = 0; j < other.cols; j++) {
        tmp.m[i][j] = 0;
        for(int k = 0; k < this.cols; k++) {
          tmp.m[i][j] += this.m[i][k] * other.m[k][j];
        }
      }
    }
    return tmp;
  }
  
  public Matrix add(Matrix other) {
    if(this.rows != other.rows || this.cols != other.cols) {
      System.err.print("Matrices cannot be added: dimensions don't match.");
      return null;     
    }
    Matrix tmp = new Matrix(rows, cols);
    for(int i = 0; i < rows; i++) {
      for(int j = 0; j < cols; j++) {
        tmp.m[i][j] = this.m[i][j] + other.m[i][j];
      }
    }
    return tmp;
  }
  
  public Object clone() {
    return new Matrix(this);
  }
  
  public int getNumRows() {
    return rows;
  }
  
  public int getNumCols() {
    return cols;
  }
  
  public double[] getRow(int row) {
    if(0 <= row && row < rows) {
      return m[row];
    }
    else {
      System.err.println("Bad row value given: " + row);
      double[] tmp = new double[0];
      return tmp;
    }
  }
  
  public double[] getCol(int col) {
    if(0 <= col && col < cols) {
      double[] tmp = new double[rows];
      for(int i = 0; i < rows; i++) {
        tmp[i] = m[i][col];
      }
      return tmp;
    }
    else {
      System.err.println("Bad column value given: " + col);
      double[] tmp = new double[0];
      return tmp;
    }
  }
  
  public String toString() {
    String s = "{";
    for(int i = 0; i < rows; i++) {
      s += "{";
      for(int j = 0; j < cols - 1; j++) {
        s += m[i][j] + ", ";
      }
      if(i != rows - 1) {
        s += m[i][cols - 1] + "},\n";
      } else {
        s += m[i][cols-1] + "}}";
      }
    }
    return s;
  }
  
}
