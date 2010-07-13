package development;
import java.lang.reflect.Array;

import util.Matrix;

//Creates Matrix representing affine transformation
public class AffineTransformation extends Matrix {
  
  
  //Private method used by all constructors
  private void setData(Matrix a, Vector b){
    rows = b.getDimension()+1;
    cols = b.getDimension()+1;
    m = new double[rows][cols];
    for(int i = 0; i < b.getDimension(); i++){
      for(int j = 0; j < b.getDimension(); j++){
        setEntry(i,j,a.getEntry(i,j));
      }
    }
    for(int j=0; j < b.getDimension(); j++){
      setEntry(b.getDimension(),j,0);
    }
    for(int i=0; i < b.getDimension(); i++){
      setEntry(i,b.getDimension(),b.getComponent(i));
    }
    setEntry(b.getDimension(),b.getDimension(),1);
  }
  
  //General constructor
  public AffineTransformation(Matrix a, Vector b) throws Exception{
    if ((a.numRows()!= b.getDimension())
        || (a.numRows() != a.numCols())){
      throw new Exception("Dimension Mismatch");
    }
    setData(a,b);
  }

//Create linear transformation as special case above
public AffineTransformation(Matrix a){
  double[] temp = new double[a.numRows()];
  Vector b = new Vector(temp);
  setData(a,b);
}

//Create translation as special case of above
public AffineTransformation(Vector b){
  Matrix temp = new Matrix(b.getDimension(), b.getDimension());
  for (int i=0; i< temp.numRows(); i++){
    temp.setEntry(i,i,1);
  }
  setData(temp,b);
}
      
      
//Applies affine transformation to points
  public Point affineTransPoint(Point p) throws Exception{
    if (numRows()-1 != p.getDimension()){
      throw new Exception("Dimension Mismatch");
    }
    Matrix n = new Matrix(p.getDimension()+1,1);
    for(int i=0; i < p.getDimension(); i++){
      n.setEntry(i,0,p.getComponent(i));
    }
    n.setEntry(n.numRows()-1,0,1);
    Matrix np = multiply(n);
    double[] result = new double[p.getDimension()];
    for(int i=0; i < p.getDimension(); i++){
      result[i] = np.getEntry(i,0);
    }
    Point finalPoint = new Point(result);
    return finalPoint;
    
  }
  
//Applies affine transformation to vectors
  public Vector affineTransVector(Vector c) throws Exception{
    if (numRows()-1 != c.getDimension()){
      throw new Exception("Dimension Mismatch");
    }
    Matrix n = new Matrix(c.getDimension()+1,1);
    for(int i=0; i < c.getDimension(); i++){
      n.setEntry(i,0,c.getComponent(i));
    }
    n.setEntry(n.numRows()-1,0,0);
    Matrix newVector = multiply(n);
    double[] result = new double[c.getDimension()];
    for(int i=0; i < c.getDimension(); i++){
      result[i] = newVector.getEntry(i,0);
    }
    Vector finalVector = new Vector(result);
    return finalVector;
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
  
//Method to find normal to a triangle along edge with vertices indexed locally by
  // i and j. Our convention is to have the normal pointing away from the triangle.
  public static Vector findNormal(Point[] p, int i, int j) throws Exception{
    if ((p.length != 3)){
      throw new Exception("Must specify a triangle and an edge");
    }
    else{
      Point S = p[i];
      Point T = p[j];
      Point R = p[findOtherLocalIndex(i,j)];
      Vector u = new Vector(T.getComponent(0)-S.getComponent(0),
          T.getComponent(1)-S.getComponent(1));
      Vector v = new Vector(R.getComponent(0)-S.getComponent(0),
          R.getComponent(1)-S.getComponent(1));
      Vector n = new Vector(u.getComponent(1),(-1)*u.getComponent(0));
      if(Vector.dot(v,n) > 0){
        return n.scale(-1);
      }
      else{
        return n;
      }
      
      
    }
  }
  
  
  public static int findOtherLocalIndex(int i, int j){
    int k = 0;
    if(i+j == 1){
      k = 2;
    }
    else if(i+j == 2){
      k = 1;
    }
    return k;

    }
  
  public static AffineTransformation MatchTriTrans(Point[] p, Point[] q) throws Exception{
    if ((p.length != 3) || (q.length != 3)){
      throw new Exception("Need two vertices for each edge of the triangles");
  }
    else{
      Point P0 = p[0];
      Point P1 = p[1];
      Point P2 = p[2];
      Point Q0 = q[0];
      Point Q1 = q[1];
      Point Q2 = q[2];
      Vector c = new Vector(Q0.getComponent(0)-P0.getComponent(0),
          Q0.getComponent(1)-P0.getComponent(1));
      Vector d = new Vector(new double[P0.getDimension()]);
      d = Vector.pointToVector(P0);
      Vector minus_d = d.scale(-1);
      Vector e = new Vector(new double[Q0.getDimension()]);
      Vector minus_e = e.scale(-1);
      P0 = Vector.translatePoint(P0, minus_d);
      P1 = Vector.translatePoint(P1, minus_d);
      P2 = Vector.translatePoint(P2, minus_d);
      Q0 = Vector.translatePoint(Q0, minus_e);
      Q1 = Vector.translatePoint(Q1, minus_e);
      Q2 = Vector.translatePoint(Q2, minus_e);
      
      Vector u1 = new Vector(P1.getComponent(0)-P0.getComponent(0),
          P1.getComponent(1)-P0.getComponent(1));
      Vector v1 = new Vector(Q1.getComponent(0)-Q0.getComponent(0),
          Q1.getComponent(1)-Q0.getComponent(1));
      Vector u1_3d = new Vector(P1.getComponent(0)-P0.getComponent(0),
          P1.getComponent(1)-P0.getComponent(1), 0);
      Vector v1_3d = new Vector(Q1.getComponent(0)-Q0.getComponent(0),
          Q1.getComponent(1)-Q0.getComponent(1), 0);
      Vector n1 = findNormal(p, 0, 1);
      Vector n1_3d = new Vector(n1.getComponent(0), n1.getComponent(1), 0);
      Vector n2 = findNormal(q, 0, 1);
      Vector n2_3d = new Vector(n2.getComponent(0), n2.getComponent(1), 0);
      Vector c1 = Vector.cross(u1_3d, n1_3d);
      Vector c2 = Vector.cross(v1_3d, n2_3d);
      Matrix trans = new Matrix(2,2);
      if( Vector.dot(c1, c2) < 0){
        //find rotation needed and done
        double theta = Vector.findAngle2D(u1, v1);
        Matrix rot_matrix = new Matrix(2,2);
        rot_matrix.setEntry(0,0, Math.cos(theta));
        rot_matrix.setEntry(1,0, Math.sin(theta));
        rot_matrix.setEntry(0,1, (-1)*Math.sin(theta));
        rot_matrix.setEntry(1,1, Math.cos(theta));
        Matrix final_matrix = new Matrix(2,2);
        final_matrix = rot_matrix;
        Vector final_translate = new Vector(new double[2]);
        final_translate = Vector.add_better(final_matrix.transformVector(minus_d), Vector.add_better(d, c));
        AffineTransformation result = new AffineTransformation(final_matrix, final_translate);
        return result;
      }
      else{
        //reflect about axis spanned by n1 then find rotation
        //Vector minus_u1 = u1.scale(-1);
        trans.setEntry(0,0,(-1)*u1.getComponent(0));
        trans.setEntry(1,0,(-1)*u1.getComponent(1));
        trans.setEntry(0,1,n1.getComponent(0));
        trans.setEntry(1,1,n1.getComponent(1));
        System.out.print(trans);
        //Prepare input for changeBasis and then apply it
        Vector[] U = new Vector[] {u1,n1};
        Vector e1 = new Vector(new double[] {1,0});
        Vector e2 = new Vector(new double[] {0,1});
        Vector[] stdBasis = new Vector[] {e1,e2};
        trans = MatchTetraTrans.changeBasis(trans, U, stdBasis);
        System.out.print(trans);
        //Find angle between u1 and v1. get rotation transformation, then compose
        u1 = trans.transformVector(u1);
        double theta = Vector.findAngle2D(u1, v1);
        System.out.print(theta);
        Matrix rot_matrix = new Matrix(2,2);
        rot_matrix.setEntry(0,0, Math.cos(theta));
        rot_matrix.setEntry(1,0, Math.sin(theta));
        rot_matrix.setEntry(0,1, (-1)*Math.sin(theta));
        rot_matrix.setEntry(1,1, Math.cos(theta));
        Matrix final_matrix = new Matrix(2,2);
        final_matrix = rot_matrix.multiply(trans);
        Vector final_translate = new Vector(new double[2]);
        final_translate = Vector.add_better(final_matrix.transformVector(minus_d), Vector.add_better(d, c));
        AffineTransformation result = new AffineTransformation(final_matrix, final_translate);
        return result;
        }
        
      }
  }
  
//Make this a constructor in affine transformation. Update 'this'
  //The array of points has length 4. THE FIRST THREE POINTS CORRESPONDS TO THE
  //TRIANGULAR FACES THAT WE WANT TO MATCH UP (WITH VERTICES IN CORRESPONDING ORDER).
  public static AffineTransformation MatchTetraTrans(Point[] p, Point[] q) throws Exception{
    if ((p.length != 4) || (q.length != 4)){
      throw new Exception("Need four vertices for each tetrahedron");
    } 
    else{
      Point P0 = p[0];
      Point P1 = p[1];
      Point P2 = p[2];
      Point P3 = p[3];
      Point Q0 = q[0];
      Point Q1 = q[1];
      Point Q2 = q[2];
      Point Q3 = q[3];
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
     P3 = Vector.translatePoint(P3, minus_d);
     Q0 = Vector.translatePoint(Q0, minus_e);
     Q1 = Vector.translatePoint(Q1, minus_e);
     Q2 = Vector.translatePoint(Q2, minus_e);
     Q3 = Vector.translatePoint(Q3, minus_e);
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
      //Check if you need to invert u3 and v3 so that they're pointing away
      //from their respective tetrahedron!!
      Vector test1 = new Vector(P3.getComponent(0)-P0.getComponent(0),
          P3.getComponent(1)-P0.getComponent(1),
          P3.getComponent(2)-P0.getComponent(2));
      Vector test2 = new Vector(Q3.getComponent(0)-Q0.getComponent(0),
          Q3.getComponent(1)-Q0.getComponent(1),
          Q3.getComponent(2)-Q0.getComponent(2));
      double dot_test1 = Vector.dot(u3, test1);
      double dot_test2 = Vector.dot(v3, test2);
      if(dot_test1*dot_test2 > 0){
        throw new Exception("Cannot connect the tetrahedra without overlap despite having isometric faces");
      }
      else{
      if(dot_test1 > 0){
        u3 = u3.scale(-1);
      }
      
      if(dot_test2 > 0){
        v3 = v3.scale(-1);
      }
      
      //System.out.print(v3);
      Vector[] U = new Vector[] {u1,u2,u3};
      Vector[] V = new Vector[] {v1,v2,v3};
      Vector e1 = new Vector(new double[] {1,0,0});
      Vector e2 = new Vector(new double[] {0,1,0});
      Vector e3 = new Vector(new double[] {0,0,1});
      Vector[] stdBasis = new Vector[] {e1,e2,e3};
      Matrix trans = new Matrix(3,3);
      //Get the transformation matrix in the u1, u2, u3 basis
      
      //WITH THE NEW WAY OF DOING IT, USE MATRIX [v1 v2 -v3] in the u1, u2, u3 basis
      for(int i=0; i < 3; i++){
        for(int j=0; j<3; j++){
          if(j == 2){
            trans.setEntry(i, j, (-1)*V[j].getComponent(i));
          }
          else{
          trans.setEntry(i, j, V[j].getComponent(i)); 
        }
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
      AffineTransformation result = new AffineTransformation(trans, finalTranslate);
      return result;
    }
    }
  }
  
 
  
   
 
}