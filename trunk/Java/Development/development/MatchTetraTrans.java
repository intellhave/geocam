package development;

import util.Matrix;

public class MatchTetraTrans extends Matrix {
  //Make this a constructor in affine transformation. Update 'this'
  public static AffineTransformation MatchTetraTrans(Point[] p, Point[] q) throws Exception{
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
     //Translate triangles P and Q to origin
      Vector d = new Vector(new double[P0.getDimension()]);
      d = Vector.pointToVector(P0);
      Vector minus_d = d.scale(-1);
      Vector e = new Vector(new double[Q0.getDimension()]);
      Vector minus_e = e.scale(-1);
      e = Vector.pointToVector(Q0);
     P0 = Vector.translatePoint(P0, minus_d);
     P1 = Vector.translatePoint(P1, minus_d);
     P2 = Vector.translatePoint(P2, minus_d);
     Q0 = Vector.translatePoint(Q0, minus_e);
     Q1 = Vector.translatePoint(Q1, minus_e);
     Q2 = Vector.translatePoint(Q2, minus_e);
    //Flip order of other two points in 2nd triangle
      Point[] r = new Point[] {Q0, Q2, Q1};
      Point R0 = r[0];
      Point R1 = r[1];
      Point R2 = r[2];
      Vector u1 = new Vector(P1.getComponent(0)-P0.getComponent(0),
          P1.getComponent(1)-P0.getComponent(1),
          P1.getComponent(2)-P0.getComponent(2));
      Vector u2 = new Vector(P2.getComponent(0)-P0.getComponent(0),
          P2.getComponent(1)-P0.getComponent(1),
          P2.getComponent(2)-P0.getComponent(2));
      Vector v1 = new Vector(R2.getComponent(0)-R0.getComponent(0),
          R2.getComponent(1)-R0.getComponent(1),
          R2.getComponent(2)-R0.getComponent(2));
      //System.out.print(v1);
      Vector v2 = new Vector(R1.getComponent(0)-R0.getComponent(0),
          R1.getComponent(1)-R0.getComponent(1),
          R1.getComponent(2)-R0.getComponent(2));
      //System.out.print(v2);
      Vector u3 = Vector.cross(u1, u2);
      Vector v3 = Vector.cross(v1, v2);
      //System.out.print(v3);
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
      System.out.print(trans);
      trans = changeBasis(trans, U, stdBasis);
      System.out.print(trans);
      //Here is the sequence of transformations:
      // x -> x-P0
      //   -> T(x-P0)
      //   -> T(x-P0) + P0 + c
      Vector finalTranslate = new Vector(new double[p.length]);
      finalTranslate = Vector.add_better(trans.transformVector(minus_d), Vector.add_better(d, c));
      //Make c a matrix and multiply by T. Then turn back into
      // a vector to create affineTransformation. We made a
      // note to write a procedure that applies matrices to vectors
      // within the vector class
      
      // c as a matrix
      //  c -> Tc (as a vector)
      // then return AffineTransformation(T, Tc)
      AffineTransformation result = new AffineTransformation(trans, finalTranslate);
      return result;
    }
  }
  
//Change basis
    public static Matrix changeBasis(Matrix T, Vector[] U, Vector[] W) throws Exception{
      if ((T.determinant() == 0) || (U.length != W.length) || (T.numRows() != T.numCols()) || (T.numCols() != U.length)){
        throw new Exception("Dimension mismatch");
        //Don't for get to check linear independence by taking determinant
      }
      else{
        Matrix A = new Matrix(U.length,U.length);
        for(int i=0; i < U.length; i++){
          for(int j=0; j< U.length; j++){
            A.setEntry(i, j, U[j].getComponent(i));
          }
        }
        Matrix B = new Matrix(U.length ,U.length);
        for(int i=0; i < U.length; i++){
          for(int j=0; j < U.length; j++){
            B.setEntry(i, j, W[j].getComponent(i));
          } 
        }
        if(A.determinant()*B.determinant() == 0){
          throw new Exception("Vectors in one of the bases are not linearly independent");
        }
        Matrix C = new Matrix(U.length, U.length);
        C = T.multiply(A.inverse());
        C = B.inverse().multiply(C.multiply(B));
        return C;
        }
    }
    
    
    
    
    }




