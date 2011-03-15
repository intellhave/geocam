package util;

import development.Vector;


public class Matrix implements Cloneable{
  public double[][] m;
  protected int rows;
  protected int cols;
  
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
  
  public Matrix(double[][] matrix) {
    rows = matrix.length;
    cols = matrix[0].length;
    m = matrix;
  }

  public Matrix scaleMatrix(double c){
    Matrix temp = new Matrix(rows, cols);
    for(int i=0; i < rows; i++){
      for(int j=0; j< cols; j++){
        temp.setEntry(i, j, c*getEntry(i,j));
      }
    }
    return temp;
  }
  
  public int numRows(){
    return rows;
  }
  
  public int numCols(){
    return cols;
  }
  
  public double getEntry(int i, int j){
    return m[i][j];
  }
  
  public void setEntry(int i, int j, double k){
    m[i][j] = k;
  }
  
  public double[] getLinearArray(){
    double[] mlinear = new double[rows*cols];
    for(int i=0; i<rows; i++){
      for(int j=0; j<cols; j++){
        mlinear[j+i*rows] = m[i][j];
      }
    }
    return mlinear;
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
  
  public Vector transformVector(Vector c){
    if(cols != c.getDimension()){
      System.err.print("Matrix and vector dimensions incompatible");
      return null;
    }
    double[] temp = new double[c.getDimension()];
    for(int i=0; i < c.getDimension(); i++){
      double result = 0;
      for(int j=0; j< c.getDimension(); j++){
        result += m[i][j]*c.getComponent(j);
      }
      temp[i] = result;
    }
    Vector vectTemp = new Vector(temp);
    return vectTemp;
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
  
  
  public Matrix matrixMinor(int i, int j){
    if((rows == 1) && (cols == 1)){
      Matrix simple = new Matrix(1,1);
      simple.setEntry(0,0,1);
      return simple; 
    }
    else{
    Matrix T = new Matrix(rows-1, cols-1);
    for(int s = 0; s < rows-1; s++){
      for(int t = 0; t < cols-1; t++){
        if(s < i){
          if(t < j){
            T.setEntry(s,t, getEntry(s,t));
          }
          else{
            T.setEntry(s,t, getEntry(s,t+1));
          }
        }
        else{
          if(t < j){
            T.setEntry(s,t, getEntry(s+1,t));
          }
          else{
            T.setEntry(s,t, getEntry(s+1,t+1));
          }
        }
      }
    }
    return T;
  }
  }
  
  
  
  public double determinant(){
    double result = 0;
    if(rows > 1){
    for(int j=0; j < rows; j++){
      Matrix temp = matrixMinor(0,j);
      result = result + Math.pow(-1,j)*getEntry(0, j)*temp.determinant();
      
    }
    }
    else{
      result = getEntry(0,0);
    }
    return result;
    }
    
    //3 x 3 case
    /*return m[1][1]*m[2][2]*m[3][3]+m[1][2]*m[3][1]*m[2][3]+m[1][3]*m[2][1]*m[3][2]-
    m[1][1]*m[3][2]*m[2][3]-m[1][2]*m[2][1]*m[3][3]-m[1][3]*m[3][1]*m[2][2];
  }*/
  

  public Matrix inverse() throws Exception{
    if((rows == 1) && (cols == 1)){
      Matrix temp = new Matrix(1,1);
      temp.setEntry(0,0, Math.pow(getEntry(0,0),-1));
      return temp;
    }
    else if(determinant() == 0){
      throw new Exception("Matrix must be invertible");
    }
    else{
    Matrix C = new Matrix(rows, cols);
    for(int i=0; i < rows; i++){
      for(int j=0; j < cols; j++){
        C.setEntry(i, j, Math.pow(-1, i+j)*matrixMinor(i,j).determinant());
      }
    }
    C = C.transpose();
    return C.scaleMatrix(Math.pow(determinant(),-1));
  }
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
