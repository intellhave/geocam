package development;

import util.Matrix;

public class MatchTetraTrans extends Matrix {
  
  private AffineTransformation MatchTetraTrans(Point[] p, Point[] q) throws Exception{
    if ((p.length != 3) || (q.length != 3)){
      throw new Exception("Need three vertices for each triangular face");
    }
    else{
      Point P0 = p[0];
      Point P1 = p[1];
      Point P2 = p[2];
      Point Q0 = q[0];
      Point Q1 = q[1];
      Point Q2 = q[2];
      //Translation to move P0 to Q0, as a matrix
      Vector c = new Vector(Q0.getComponent(0)-P0.getComponent(0),
          Q0.getComponent(1)-P0.getComponent(1),
          Q0.getComponent(2)-P0.getComponent(2));
    //Flip order of other two points in 2nd triangle
      Point[] r = new Point[] {Q0, Q2, Q1};
      Vector u1 = new Vector(P1.getComponent(0)-P0.getComponent(0),
          P1.getComponent(1)-P0.getComponent(1),
          P1.getComponent(2)-P0.getComponent(2));
      Vector u2 = new Vector(P2.getComponent(0)-P0.getComponent(0),
          P2.getComponent(1)-P0.getComponent(1),
          P2.getComponent(2)-P0.getComponent(2));
      Vector v1 = new Vector(Q2.getComponent(0)-Q0.getComponent(0),
          Q2.getComponent(1)-Q0.getComponent(1),
          Q2.getComponent(2)-Q0.getComponent(2));
      Vector v2 = new Vector(Q1.getComponent(0)-Q0.getComponent(0),
          Q1.getComponent(1)-Q0.getComponent(1),
          Q1.getComponent(2)-Q0.getComponent(2));
      Vector u3 = Vector.cross(u1, u2);
      Vector v3 = Vector.cross(v1, v2);
      Vector[] U = new Vector[] {u1,u2,u3};
      Vector[] V = new Vector[] {v1,v2,v3};
      Vector e1 = new Vector(new double[] {1,0,0});
      Vector e2 = new Vector(new double[] {0,1,0});
      Vector e3 = new Vector(new double[] {0,0,1});
      Vector[] stdBasis = new Vector[] {e1,e2,e3};
      Matrix trans = new Matrix(3,3);
      //Get the transformation matrix in the u1, u2, u3 basis
      
      for(int i=0; i < 3; i++){
        for(int j=0; j<3; j++){
          trans.setEntry(i, j, V[j].getComponent(i)); 
        }
      }
      trans = changeBasis(trans, U, stdBasis);
      //Make c a matrix and multiply by T. Then turn back into
      // a vector to create affineTransformation. We made a
      // note to write a procedure that applies matrices to vectors
      // within the vector class
      
      // c as a matrix
      //  c -> Tc (as a vector)
      // then return AffineTransformation(T, Tc)
      return AffineTransformation()
    }
  }
  
//Change basis
    public Matrix changeBasis(Matrix T, Vector[] U, Vector[] W) throws Exception{
      if ((U.length != 3) || (W.length != 3) || (T.numRows() != 3) || (T.numCols() != 3)){
        throw new Exception("Need 3 vectors in each basis of R, and need matrix to be square");
        //Don't for get to check linear independence by taking determinant
      }
      else{
        Matrix A = new Matrix(3,3);
        for(int i=0; i < 3; i++){
          for(int j=0; j<3; j++){
            A.setEntry(i, j, U[j].getComponent(i));
          }
        }
        Matrix B = new Matrix(3,3);
        for(int i=0; i < 3; i++){
          for(int j=0; j<3; j++){
            B.setEntry(i, j, W[j].getComponent(i));
          } 
        }
        if(A.determinant()*B.determinant() == 0){
          throw new Exception("Vectors in one of the bases are not linearly independent");
        }
        Matrix C = new Matrix(3,3);
        C = A.multiply(T.multiply(A.inverse()));
        C = B.inverse().multiply(C.multiply(B));
        return C;
        }
    }




}
