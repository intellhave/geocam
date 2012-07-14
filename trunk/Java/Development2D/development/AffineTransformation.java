package development;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import triangulation.Face;
import triangulation.Vertex;
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
  private void setAsIdentity(int n){
    
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
  
  public AffineTransformation(int n){ 
    setAsIdentity(n);
  }
  
  //copy constructor
  public AffineTransformation(AffineTransformation T){
    
    rows = T.rows;
    cols = T.cols;
    m = new double[rows][cols];
    for(int i=0; i<rows; i++){
      for(int j=0; j<cols; j++){
        m[i][j] = T.m[i][j];
      }
    }
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
  private Vector affineTransProjective(Vector v, double w) {

    //check dimension
    int d = v.getDimension();
    
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
  public Vector affineTransPoint(Vector v) {
    return affineTransProjective(v,1);
  }
  
  //Applies affine transformation to vectors
  public Vector affineTransVector(Vector v) {
    return affineTransProjective(v,0);
  }
  
  public EmbeddedFace affineTransFace(Face f) {
      ArrayList<Vector> efpts = new ArrayList<Vector>();
      ArrayList<Vector> texCoords = (ArrayList<Vector>) TextureCoords.getCoords(f);
      
      Iterator<Vertex> i = f.getLocalVertices().iterator();
      while(i.hasNext()){
        Vertex vert = i.next();
        Vector pt = Coord2D.coordAt(vert, f);
        try{ 
          efpts.add(this.affineTransPoint(pt));
        }
        catch(Exception e1){ e1.printStackTrace(); }
      }
      return new EmbeddedFace(efpts,texCoords);
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
  
    
  public AffineTransformation(Vector[] p, Vector[] q, boolean flip) throws Exception{

    //p and q are points making up a simplex with n vertices
    //should produce an affine transformation taking p[i] -> q[i], for i = 0 to d-2
    //and p[d-1], q[d-1] are the points not contained in the common sub-simplex
    //assumes that p,q consist of vectors all of dim d, and p.length == q.length == d+1
    int d = p[0].getDimension();
    setAsIdentity(d); //identity transformation on R^d
    
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
    leftMultiply(new AffineTransformation(Vector.scale(p[0],-1))); //move p[0] to origin
    leftMultiply(new AffineTransformation(U_to_std)); //U -> standard basis
    leftMultiply(new AffineTransformation(std_to_V)); //standard basis -> V
    leftMultiply(new AffineTransformation(q[0])); //move origin to q[0]
  }
  
  public AffineTransformation(Vector[] p, Vector[] q) throws Exception{

    //p and q are points making up a simplex with n vertices
    //should produce an affine transformation taking p[i] -> q[i], for i = 0 to d-2
    //and p[d-1], q[d-1] are the points not contained in the common sub-simplex
    //assumes that p,q consist of vectors all of dim d, and p.length == q.length == d+1
    int d = p[0].getDimension();
    setAsIdentity(d); //identity transformation on R^d
    
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
    leftMultiply(new AffineTransformation(Vector.scale(p[0],-1))); //move p[0] to origin
    leftMultiply(new AffineTransformation(U_to_std)); //U -> standard basis
    leftMultiply(new AffineTransformation(std_to_V)); //standard basis -> V
    leftMultiply(new AffineTransformation(q[0])); //move origin to q[0]
  }
}