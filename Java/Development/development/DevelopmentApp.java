
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
    
    /*
    Point p = new Point(new double[] {1,1});
    Vector v = new Vector(new double[] {1,1});
    Vector c = new Vector(new double[] {0,1});
    Matrix t = new Matrix(2,2);
    t.setEntry(0,0,0);
    t.setEntry(0,1,-1);
    t.setEntry(1,0,1);
    t.setEntry(1,1,0);
    */
    
    //Matrix testing
    /*
    Matrix t = new Matrix(3,3);
    t.setEntry(0,0,1);
    t.setEntry(0,1,2);
    t.setEntry(0,2,3);
    t.setEntry(1,0,2);
    t.setEntry(1,1,0);
    t.setEntry(1,2,-2);
    t.setEntry(2,0,-2);
    t.setEntry(2,1,-2);
    t.setEntry(2,2,-1);
    Matrix T = new Matrix(2,2);
    T.setEntry(0,0,2);
    T.setEntry(0,1,0);
    T.setEntry(1,0,0);
    T.setEntry(1,1,1);
    Vector u1 = new Vector(new double[] {1/Math.pow(2,.5),1/Math.pow(2,.5)});
    Vector u2 = new Vector(new double[] {-1/Math.pow(2,.5),1/Math.pow(2,.5)});
    Vector w1 = new Vector(new double[] {1,0});
    Vector w2 = new Vector(new double[] {0,1});
    Vector[] U = new Vector[] {u1,u2};
    Vector[] W = new Vector[] {w1,w2};
    Matrix S = new Matrix(2,2);
    */
    /*MatchTetraTrans TEST1
    Point P0 = new Point(new double[] {0,0,0});
    Point P1 = new Point(new double[] {1,0,0});
    Point P2 = new Point(new double[] {0,1,0});
    Point Q0 = new Point(new double[] {2,0,1});
    Point Q1 = new Point(new double[] {2,0,0});
    Point Q2 = new Point(new double[] {2,1,1});
    Point[] U = new Point[] {P0,P1,P2};
    Point[] V = new Point[] {Q0,Q1,Q2};
    */
    /*MatchTetraTrans TEST2
    Point P0 = new Point(new double[] {-2,-4,-1});
    Point P1 = new Point(new double[] {-1,-4,-1});
    Point P2 = new Point(new double[] {-2,-4,-3});
    Point Q0 = new Point(new double[] {3,1,0});
    Point Q1 = new Point(new double[] {4,1,0});
    Point Q2 = new Point(new double[] {3,3,0});
    Point[] U = new Point[] {P0,P1,P2};
    Point[] V = new Point[] {Q0,Q1,Q2};
    */
    /*findNormal Test
    Point P0 = new Point(new double[] {0,0,0});
    Point P1 = new Point(new double[] {1,0,0});
    Point P2 = new Point(new double[] {0,1,0});
    Point[] P = new Point[] {P0,P1,P2};
    */
    //findAngle2D Test
    //Vector u = new Vector(new double[] {1,0});
    //Vector v = new Vector(new double[] {-1,0});
    //MatchTriTrans Test
    /*
    Point P0 = new Point(new double[] {-2,1});
    Point P1 = new Point(new double[] {-3,2});
    Point P2 = new Point(new double[] {-4,1});
    Point Q0 = new Point(new double[] {3,2});
    Point Q1 = new Point(new double[] {4,3});
    Point Q2 = new Point(new double[] {5,1});
    Point[] p = new Point[] {P1,P0,P2};
    Point[] q = new Point[] {Q0,Q1,Q2};
    */
    
    
    Vector P0 = new Vector(new double[] {-2,-4,-1});
    Vector P1 = new Vector(new double[] {-1,-4,-1});
    Vector P2 = new Vector(new double[] {-2,-4,-3});
    Vector P3 = new Vector(new double[] {-2,-6,-2});
    Vector Q0 = new Vector(new double[] {3,1,0});
    Vector Q1 = new Vector(new double[] {4,1,0});
    Vector Q2 = new Vector(new double[] {3,3,0});
    Vector Q3 = new Vector(new double[] {3,2,2});
    Vector[] P = new Vector[] {P0,P1,P2,P3};
    Vector[] Q = new Vector[] {Q0,Q1,Q2,Q3};
    
    /*try {
      S = MatchTetraTrans.changeBasis(T, U, W);
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    */
    Vector c = new Vector(new double[] {0,1});
    
    
    try{
      //AffineTransformation s = new AffineTransformation(t,c);
      //AffineTransformation s = new AffineTransformation(t);
      //AffineTransformation s = new AffineTransformation(c);
      //Point q = new Point(s.affineTransPoint(p));
      //Vector q = new Vector(s.affineTransVector(v));
      //System.out.print(S);
      //System.out.print(q);
      //System.out.print(t.inverse());
      //System.out.print(t.multiply(t.inverse()));
      //System.out.print(t.transformVector(c));
      //System.out.print(AffineTransformation.MatchTetraTrans(U,V));
      //System.out.print(AffineTransformation.findNormal(P,2,1));
      //System.out.print(Vector.findAngle2D(v,u));
      System.out.print(AffineTransformation.MatchSimplexTrans(P, Q));
    }catch(Exception e){
      e.printStackTrace();
    
    
    
  
  }
  
  
  }
  
  
  

}

  
