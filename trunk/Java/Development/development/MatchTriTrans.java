package development;

import util.Matrix;

public class MatchTriTrans extends Matrix {
  
  //Takes the two entire triangles and matches up the edges corresponding to the
  //first two vertices in each triangle. This differs from the MatchTetraTrans
  //method in that we only specify triangular faces there, not the entire tetrahedron.
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
  
  

  
}