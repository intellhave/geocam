package util;

public class GeoMath {
  private GeoMath() {
  }
  public static double[] LinearEquationsSolver(Matrix A, double[] b) {
    double[] x = new double[A.getNumCols()];
    if(A.getNumRows() != A.getNumCols() || A.getNumRows() != b.length) {
      System.err.println("Incorrect dimensions");
      return null;
    }
    int nDim = x.length;
    double maxElem; // Used to determine row with max element.
    double temp; // Just a temp variable for switching

    int i, j, k, m; // indexing variables
    boolean singular_matrix = false;

    // For each column in matrix...
    for(k=0; k<(nDim-1); k++) {
    // Here k will act as both the starting row and the
    // current column for the sub-matrix.
      
      // Find the row with the max element in column k
      // (ignoring rows above k already completed).
      // NOTE: This is to lessen potential rounding errors.

      maxElem = Math.abs( A.m[k][k] ); // First element
      m = k; // m is the index of the currently found max row
      for(i=k+1; i<nDim; i++) { // Iterate through every row below k.
        if(maxElem < Math.abs(A.m[i][k]) ) { // If this row has the new max element... set values.
          maxElem = Math.abs(A.m[i][k]);
          m = i;
        }
      }
      // Permute the kth row with the row with the max element (if necessary).
      if(m != k) {
        for(i=k; i<nDim; i++) {
          temp = A.m[k][i];
          A.m[k][i] = A.m[m][i];
          A.m[m][i] = temp;
        }
        // Also permute the vector.
        temp = b[k];
        b[k] = b[m];
        b[m] = temp;
      }

      if( Math.abs(A.m[k][k]) < 0.0001) {
        singular_matrix = true; // Matrix has a column of all 0s !!!
      } else {
        // Triangulate matrix by turning every value in column k below row k
        // into a 0 using row operations.
        for(j=(k+1); j<nDim; j++) {// j is each row below row k.
          // The multiplicative value in the row operation.
          temp = - A.m[j][k] / A.m[k][k];
          // For each entry in row j to the right of column k
          for(i=k; i<nDim; i++) { // perform row operation
            A.m[j][i] += temp*A.m[k][i];
          }
          // Also perform operation on the vector
          b[j] = b[j] + temp*b[k];
        }
      }
    }
    
    if(singular_matrix || Math.abs(A.m[nDim-1][nDim-1]) < 0.0001) {
      return solveUndeterminedSystem(A, b);
    }

    // For each row beginning with the bottom
    for(k=(nDim-1); k>=0; k--)
    { // Calculate the solution for variable k.
      x[k] = b[k];
      // Adjust solution using variables already known.
      for(i=(k+1); i<nDim; i++)
      {
        x[k] -= (A.m[k][i]*x[i]);
      }
      x[k] = x[k] / A.m[k][k];
    }
    return x;
  }
  
  public static double[] solveUndeterminedSystem(Matrix A, double[] b) {
    double[] x = new double[A.getNumCols()];
    if(A.getNumRows() != A.getNumCols() || A.getNumRows() != b.length) {
      System.err.println("Incorrect dimensions");
      return null;
    }
        
    int nDim = x.length;
        
    int rank = 0;
    int i, j, k, l;
    boolean[] free_vars = new boolean[nDim];
    
    // Determine rank, build bit array of free variables
    for(i = 0; i < nDim; i++) {
      if(Math.abs(A.m[i][i]) < 0.0001) {
        rank++;
        free_vars[i] = true;
      } else {
        free_vars[i] = false;
      }
    }
    
    // Create matrix of homogeneous solutions that span subspace
    Matrix homo_space = new Matrix(nDim, rank);
    for(i = 0, j = 0; i < nDim; i++) {
      if(free_vars[i]) {
        // Solve with x_j = 1, x_k = 0 for all k != j s.t. free_vars[k] = 1
        for(k=(nDim-1); k>=0; k--)
        {
          if(k == i) {
            homo_space.m[k][j] = 1;
          } else if(free_vars[k]) {
            homo_space.m[k][j] = 0;
          } else {
            homo_space.m[k][j] = 0;
            for(l=(k+1); l<nDim; l++)
            {
              homo_space.m[k][j] -= (A.m[k][l]*homo_space.m[l][j]);
            }
            homo_space.m[k][j] = homo_space.m[k][j] / A.m[k][k];
          }
        }
        j++;
      }
    }
        
    // Turn spanning set into ortho-normal basis using Gramm-Schmidt
    homo_space = Gram_Schmidt(homo_space.transpose()).transpose();
        
    // Get initial solution to non-homogeneous system
    double[] non_homo = new double[nDim];
    for(k=(nDim-1); k>=0; k--)
    {
      if(free_vars[k]) {
        non_homo[k] = 0;
      } else {
        non_homo[k] = b[k];
        for(l=(k+1); l<nDim; l++)
        {
          non_homo[k] -= (A.m[k][l]*non_homo[l]);
        }
        non_homo[k] = non_homo[k] / A.m[k][k];
      }
    }
    
    //Check for correctness
    Matrix test = A.multiply(new Matrix(non_homo));
    for(i = 0; i < nDim; i++) {
      if(Math.abs(test.m[i][0] - b[i]) > 0.00001) {
        System.err.println("Didn't calc non_homogenous solution correctly\n");
        return null;
      }
    }
    
    // Project onto homo_space
    double[] curr_vec = new double[nDim];
    Matrix components = new Matrix(nDim, rank);
    double[] proj;
    for(j = 0; j < rank; j++) {
      for(i = 0; i < nDim; i++) {
        curr_vec[i] = homo_space.m[i][j];
      }
      proj = projection(curr_vec, non_homo);
      for(i = 0; i < nDim; i++) {
        components.m[i][j] = proj[i];
      }
    }
        
    // Set solution equal to non_homo - proj
    for(j = 0; j < rank; j++) {
      for(i = 0; i < nDim; i++) {
        non_homo[i] -= components.m[i][j];
      }
    }
        
    return non_homo;
  }
  
  public static double magnitude(double[] vector) {
    double sum = 0; 
    for(int i = 0; i < vector.length; i++) {
      sum += vector[i] * vector[i];
    }
    return Math.sqrt(sum);
  }
  
  public static void negate(double[] vec) {
    for(int i = 0; i < vec.length; i++) {
      vec[i] = -vec[i];
    }
  }
  
  public static double[] projection(double[] va, double[] vb) {
    if(va.length != vb.length) {
      System.err.println("Incorrect dimensions: va.length must equal vb.length");
      return null;
    }
    double[] vec = new double[vb.length];
    double proj = 0;
    double tude = 0;
  
    for(int i = 0; i < va.length; i++) {
      proj += va[i]*vb[i];
    }
    tude = magnitude(va);
    for(int i = 0; i < va.length; i++) {
      vec[i] = va[i] * (proj / tude);
    }

    return vec;
  }
  
  public static Matrix Gram_Schmidt(Matrix basis) {
    Matrix ortho_basis = new Matrix(basis.getNumRows(), basis.getNumCols());

    double[] va = new double[basis.getNumCols()];
    double[] vb = new double[basis.getNumCols()];
    double[] vc = new double[basis.getNumCols()];
    double[] vd = new double[basis.getNumCols()];

    for (int i = 0; i < basis.getNumRows(); i++) {
      
      if (i == 0) {
        for (int j = 0; j < basis.getNumCols(); j++) {
          vd[j] = basis.m[i][j];
        }

        double temp1 = magnitude(vd);
        
        for(int j = 0; j < vd.length; j++) {
          vd[j] = vd[j] * (1 / temp1);
        }

        for (int j = 0; j < ortho_basis.getNumCols(); j++){
          ortho_basis.m[i][j] = vd[j];
        }
      }

      else {

        for (int j = 0; j < basis.getNumCols(); j++){
          vb[j] = basis.m[i][j];
          vd[j] = 0;
        }

        for (int j = 0; j < i; j++){
          for (int k = 0; k < basis.getNumCols(); k++){
            va[k] = ortho_basis.m[j][k];
          }
          vc = projection (va, vb);
          for(int k = 0; k < vd.length; k++) {
            vd[k] = vd[k] + vc[k];
          }
        }

        negate(vd);
        for(int j = 0; j < vd.length; j++) {
          vd[j] = vd[j] + vb[j];
        }

        double temp = magnitude(vd);
        for(int j = 0; j < vd.length; j++) {
          vd[j] = vd[j] * (1 / temp);
        }

        for (int j = 0; j < basis.getNumCols(); j++){
          ortho_basis.m[i][j] = vd[j];
        }
      } 
    } 

    return ortho_basis;
  }
}
