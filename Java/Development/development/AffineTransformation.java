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
   
  
  
}