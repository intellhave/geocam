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
  
  //creates (n+1)x(n+1) identity matrix (i.e., identity n-dimension affine trans)
  public AffineTransformation(int n){ 
    
    m = new double[n+1][n+1];
    for(int i=0; i<=n; i++){
      for(int j=0; j<=n; j++){
        if(i == j){ m[i][j] = 1; }
        else{ m[i][j] = 0; }
      }
    }
    rows = n+1;
    cols = n+1;
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
    
    int n = b.getDimension();
    Matrix id = new Matrix(n,n);
    for(int i=0; i<n; i++){
      for(int j=0; j<n; j++){
        if(i == j){ id.setEntry(i,j,1); }
        else{ id.setEntry(i,j,0); }
      }
    }
    setData(id,b);
  }
  
  //Applies affine transform with specified w value
  private Vector affineTransProjective(Vector v, double w) throws Exception{

    //check dimension
    int d = v.getDimension();
    if (numRows()-1 != d){
      throw new Exception("Dimension Mismatch");
    }
    
    //store point as a matrix, with last entry = w
    Matrix n = new Matrix(d+1,1);
    for(int i=0; i<d; i++){
      n.setEntry(i,0,v.getComponent(i));
    }
    n.setEntry(d,0,w);
    
    //multiply matrices
    Matrix np = multiply(n);
    
    //convert product back to point
    double[] result = new double[d];
    for(int i=0; i<d; i++){
      result[i] = np.getEntry(i,0);
    }
    return new Vector(result);
  }
      
      
  //Applies affine transformation to points
  public Vector affineTransPoint(Vector v) throws Exception{
    return affineTransProjective(v,1);
  }
  
  //Applies affine transformation to vectors
  public Vector affineTransVector(Vector v) throws Exception{
    return affineTransProjective(v,0);
  }
  
  //Creates a matrix from a list of Vectors
  //should really be in Matrix class, but Matrix/Vector are in different packages...
  public static Matrix createMatrixFromColumnVectors(Vector[] U){
    
    int rows = U[0].getDimension();
    int cols = U.length;
    
    Matrix M = new Matrix(rows,cols);
    for(int i=0; i<rows; i++){
      for(int j=0; j<cols; j++){
        M.setEntry(i,j,U[j].getComponent(i));
      }
    }
    
    return M;
  }
  
  public void leftMultiply(AffineTransformation lhs){
    
    //assumes this and lhs are both nxn matrices
    int n = rows;
    if((cols != n) || (lhs.rows != n) || (lhs.cols != n)){
      System.err.print("Matrices cannot be multiplied: dimensions don't match.");
      return;
    }
    
    //multiply
    double[][] newm = new double[n][n];
    for(int i=0; i<n; i++){
      for(int j=0; j<n; j++){
        newm[i][j] = 0; 
        for(int k=0; k<n; k++){
          newm[i][j] += lhs.getEntry(i,k) * this.getEntry(k,j);
        }
      }
    }
    m = newm;
  }

  private static void setLastNormal(Vector[] U) throws Exception{
    
    //should receive a list of d vectors of dimension d
    //the first d-1 vectors span a subspace S
    //set the last entry in U to be a unit normal vector to S
    //compute by special cases for 2d and 3d for now, since that is all we need
    //(can use the generalized 'cross product determinant' for any dimension)
    
    int d = U[0].getDimension();
    if(d == 2){
      U[1] = new Vector(U[0].getComponent(1), -U[0].getComponent(0));
    }else if(d == 3){
      U[2] = Vector.cross(U[0], U[1]);
    }
    U[d-1].normalize();
  }
  
  public static AffineTransformation MatchSimplexTrans(Vector[] p, Vector[] q) throws Exception{

    //p and q are points making up a simplex with n vertices
    //should produce an affine transformation taking p[i] -> q[i], for i = 0 to d-2
    //and p[d-1], q[d-1] are the points not contained in the common sub-simplex
    //assumes that p,q consist of vectors all of dim d, and p.length == q.length == d+1
    int d = p[0].getDimension();
    
    //get vectors for change of basis
    Vector[] U = new Vector[d];
    Vector[] V = new Vector[d];
    for(int i=0; i<d-1; i++){
      U[i] = Vector.subtract(p[i+1], p[0]);
      V[i] = Vector.subtract(q[i+1], q[0]);
    }
    //make the last element of the bases (U[d-1] and V[d-1]) normal to the common subsimplex
    //generated by the vectors U[0] up to U[d-2]
    setLastNormal(U);
    setLastNormal(V);
    
    //adjust normals to common face to point in the correct direction
    Vector Un = Vector.subtract(p[d], p[0]);
    Vector Vn = Vector.subtract(q[d], q[0]);
    if(Vector.dot(Un,U[d-1]) < 0){ U[d-1].scale(-1); } //face U[d-1] toward non-common point
    if(Vector.dot(Vn,V[d-1]) > 0){ V[d-1].scale(-1); } //face V[d-1] away from non-common point
    
    //changes of basis
    Matrix std_to_U = createMatrixFromColumnVectors(U);
    Matrix std_to_V = createMatrixFromColumnVectors(V);
    Matrix U_to_std = std_to_U.inverse();
    
    //compose and return
    AffineTransformation ret = new AffineTransformation(d); //identity transformation on R^d
    ret.leftMultiply(new AffineTransformation(p[0].scale_better(-1))); //move p[0] to origin
    ret.leftMultiply(new AffineTransformation(U_to_std)); //U -> standard basis
    ret.leftMultiply(new AffineTransformation(std_to_V)); //standard basis -> V
    ret.leftMultiply(new AffineTransformation(q[0])); //move origin to q[0]
    return ret;
  }
  
  

  //DEPRECATED CODE
  
  //Change basis
  /*public static Matrix changeBasis(Matrix T, Vector[] U, Vector[] W) throws Exception{
    
    if ((U.length != W.length) || (T.numRows() != T.numCols()) || (T.numCols() != U.length)){
      throw new Exception("Dimension mismatch");
    }
    
    else{
      Matrix A = createMatrixFromColumnVectors(U);
      Matrix B = createMatrixFromColumnVectors(W);

      //don't compute B.determinant, for performance reasons
      //let A.inverse() throw the exception if there is one
      //if((A.determinant() == 0) || (B.determinant() == 0)){
      //  throw new Exception("Vectors in one of the bases are not linearly independent");
      //}
      
      Matrix C = new Matrix(U.length, U.length);
      C = T.multiply(A.inverse());
      C = B.inverse().multiply(C.multiply(B));
      return C;
    }
  }*/
  
  // Method to find normal to a triangle along edge with vertices indexed locally by
  // i and j. Our convention is to have the normal pointing away from the triangle.
  
  // moved to setLastNormal 
  
  /*public static Vector findNormal(Vector[] p, int i, int j) throws Exception{
    if ((p.length != 3)){
      throw new Exception("Must specify a triangle and an edge");
    }
    else{
      Vector S = p[i];
      Vector T = p[j];
      Vector R = p[findOtherLocalIndex(i,j)];
      Vector u = Vector.subtract(T,S);
      Vector v = Vector.subtract(R,S);
      Vector n = new Vector(u.getComponent(1),-u.getComponent(0));
      if(Vector.dot(v,n) > 0){
        return n.scale_better(-1);
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

    }*/
  
  /*
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
      Vector minus_d = d.scale_better(-1);
      Vector e = new Vector(new double[Q0.getDimension()]);
      Vector minus_e = e.scale_better(-1);
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
        trans = changeBasis(trans, U, stdBasis);
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
  }*/
  
//Make this a constructor in affine transformation. Update 'this'
  //The array of points has length 4. THE FIRST THREE POINTS CORRESPONDS TO THE
  //TRIANGULAR FACES THAT WE WANT TO MATCH UP (WITH VERTICES IN CORRESPONDING ORDER).
  /*public static AffineTransformation MatchTetraTrans_Oldver(Point[] p, Point[] q) throws Exception{
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
      Vector minus_d = d.scale_better(-1);
      Vector e = new Vector(new double[Q0.getDimension()]);
      Vector minus_e = e.scale_better(-1);
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
      if(dot_test1 > 0){
        u3 = u3.scale_better(-1);
      }
      
      if(dot_test2 > 0){
        v3 = v3.scale_better(-1);
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
  }*/
 
}