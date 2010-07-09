
package development;

import util.Matrix;

public class DevelopmentApp {

  /**
   * @param args
   */
  public static void main(String[] args)  {

    /*Vector a = new Vector(new double[] {2,3,4});
    Vector b = new Vector(1,0,0);
    try{
      a.add(b);
      System.out.print(a);
    }catch(Exception e){
      System.err.print(e.getMessage());
    }*/
    
    Point p = new Point(new double[] {1,1});
    Vector v = new Vector(new double[] {1,1});
    Vector c = new Vector(new double[] {0,1});
    Matrix t = new Matrix(2,2);
    t.setEntry(0,0,0);
    t.setEntry(0,1,-1);
    t.setEntry(1,0,1);
    t.setEntry(1,1,0);
    try{
      //AffineTransformation s = new AffineTransformation(t,c);
      //AffineTransformation s = new AffineTransformation(t);
      AffineTransformation s = new AffineTransformation(c);
      Point q = new Point(s.affineTransPoint(p));
      //Vector q = new Vector(s.affineTransVector(v));
      System.out.print(s);
      //System.out.print(q);
      System.out.print(q);
    }catch(Exception e){
      e.printStackTrace();
    
    
    
  
  }
  
  
  }
  
  
  

}

  
