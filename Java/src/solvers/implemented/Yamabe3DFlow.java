package solvers.implemented;

import geoquant.Alpha;
import geoquant.Curvature2D;
import geoquant.Curvature3D;
import geoquant.Eta;
import geoquant.Geometry;
import geoquant.Length;
import geoquant.Radius;
import geoquant.Volume;
import solvers.Solver;
import triangulation.StdTetra;
import triangulation.Tetra;
import triangulation.Triangulation;

public class Yamabe3DFlow extends Solver{

  @Override
  public double[] calcSlopes(double[] x) {
    int i = 0;
    for(Radius r : Geometry.getRadii()){
      r.setValue(x[i]);
      i++;
    }
    double[] slopes = new double[x.length];
    i = 0;
    double norm = calcNormalization();
    for(Curvature3D K : Geometry.getCurvature3D()){
      slopes[i] = norm * x[i] - K.getValue();
      i++;
    }
    return slopes;
  }

  //The calculation below is the derivative of the volume function (not squared)
  //with respect to time.
  private double cayleyVolumeDeriv(Tetra t)
  {
    double result=0.0;
 
    StdTetra st = new StdTetra(t);
    
    double L12 = Length.valueAt(st.e12);
    double L13 = Length.valueAt(st.e13);
    double L14 = Length.valueAt(st.e14);
    double L23 = Length.valueAt(st.e23);
    double L24 = Length.valueAt(st.e24);
    double L34 = Length.valueAt(st.e34);
  
    double  Eta12 = Eta.valueAt(st.e12);
    double  Eta13 = Eta.valueAt(st.e13);
    double  Eta14 = Eta.valueAt(st.e14);
    double  Eta23 = Eta.valueAt(st.e23);
    double  Eta24 = Eta.valueAt(st.e24);
    double  Eta34 = Eta.valueAt(st.e34);
   
    double  K1 =  Curvature3D.valueAt(st.v1);
    double  K2 =  Curvature3D.valueAt(st.v2);
    double  K3 =  Curvature3D.valueAt(st.v3);
    double  K4 =  Curvature3D.valueAt(st.v4);
  
    double  R1 =  Radius.valueAt(st.v1);
    double  R2 =  Radius.valueAt(st.v2);
    double  R3 =  Radius.valueAt(st.v3);
    double  R4 =  Radius.valueAt(st.v4);
    
    double A1 = Alpha.valueAt(st.v1);
    double A2 = Alpha.valueAt(st.v2);
    double A3 = Alpha.valueAt(st.v3);
    double A4 = Alpha.valueAt(st.v4);
    

    result=(((Eta12* K2* Math.pow(L13, 2)* Math.pow(L23, 2)* A1*R1 - Eta12* K2* Math.pow(L14, 2)* Math.pow(L23, 2)* A1*R1 +
       K1* Math.pow(L23, 4)* A1*R1 - Eta12* K2* Math.pow(L13, 2)* Math.pow(L24, 2)* A1*R1 +
       Eta12* K2* Math.pow(L14, 2)* Math.pow(L24, 2)* A1*R1 - 2* K1* Math.pow(L23, 2)* Math.pow(L24, 2)* A1*R1 +
       K1* Math.pow(L24, 4)* A1*R1 + 2* Eta12* K2* Math.pow(L12, 2)* Math.pow(L34, 2)* A1*R1 -
       Eta12* K2* Math.pow(L13, 2)* Math.pow(L34, 2)* A1*R1 - Eta12* K2* Math.pow(L14, 2)* Math.pow(L34, 2)* A1*R1
-
       2* K1* Math.pow(L23, 2)* Math.pow(L34, 2)* A1*R1 - Eta12* K2* Math.pow(L23, 2)* Math.pow(L34, 2)* A1*R1 -
       2* K1* Math.pow(L24, 2)* Math.pow(L34, 2)* A1*R1 - Eta12* K2* Math.pow(L24, 2)* Math.pow(L34, 2)* A1*R1 +
       K1* Math.pow(L34, 4)* A1*R1 + Eta12* K2* Math.pow(L34, 4)* A1*R1 +
       Eta23* K3* Math.pow(L12, 2)* Math.pow(L13, 2)* A2*R2 - Eta24* K4* Math.pow(L12, 2)* Math.pow(L13, 2)* A2*R2
+
       K2* Math.pow(L13, 4)* A2*R2 + Eta24* K4* Math.pow(L13, 4)* A2*R2 -
       Eta23* K3* Math.pow(L12, 2)* Math.pow(L14, 2)* A2*R2 + Eta24* K4* Math.pow(L12, 2)* Math.pow(L14, 2)* A2*R2
-
       2* K2* Math.pow(L13, 2)* Math.pow(L14, 2)* A2*R2 - Eta23* K3* Math.pow(L13, 2)* Math.pow(L14, 2)* A2*R2 -
       Eta24* K4* Math.pow(L13, 2)* Math.pow(L14, 2)* A2*R2 + K2* Math.pow(L14, 4)* A2*R2 +
       Eta23* K3* Math.pow(L14, 4)* A2*R2 + Eta12* K1* Math.pow(L13, 2)* Math.pow(L23, 2)* A2*R2 -
       Eta24* K4* Math.pow(L13, 2)* Math.pow(L23, 2)* A2*R2 - Eta12* K1* Math.pow(L14, 2)* Math.pow(L23, 2)* A2*R2
+
       2* Eta23* K3* Math.pow(L14, 2)* Math.pow(L23, 2)* A2*R2 - Eta24* K4* Math.pow(L14, 2)* Math.pow(L23, 2)*
A2*R2 -
       Eta12* K1* Math.pow(L13, 2)* Math.pow(L24, 2)* A2*R2 - Eta23* K3* Math.pow(L13, 2)* Math.pow(L24, 2)* A2*R2
+
       2* Eta24* K4* Math.pow(L13, 2)* Math.pow(L24, 2)* A2*R2 + Eta12* K1* Math.pow(L14, 2)* Math.pow(L24, 2)*
A2*R2 -
       Eta23* K3* Math.pow(L14, 2)* Math.pow(L24, 2)* A2*R2 + 2* Eta12* K1* Math.pow(L12, 2)* Math.pow(L34, 2)*
A2*R2 -
       Eta23* K3* Math.pow(L12, 2)* Math.pow(L34, 2)* A2*R2 - Eta24* K4* Math.pow(L12, 2)* Math.pow(L34, 2)* A2*R2
-
       Eta12* K1* Math.pow(L13, 2)* Math.pow(L34, 2)* A2*R2 - 2* K2* Math.pow(L13, 2)* Math.pow(L34, 2)* A2*R2 -
       Eta24* K4* Math.pow(L13, 2)* Math.pow(L34, 2)* A2*R2 - Eta12* K1* Math.pow(L14, 2)* Math.pow(L34, 2)* A2*R2
-
       2* K2* Math.pow(L14, 2)* Math.pow(L34, 2)* A2*R2 - Eta23* K3* Math.pow(L14, 2)* Math.pow(L34, 2)* A2*R2 -
       Eta12* K1* Math.pow(L23, 2)* Math.pow(L34, 2)* A2*R2 + Eta24* K4* Math.pow(L23, 2)* Math.pow(L34, 2)* A2*R2
-
       Eta12* K1* Math.pow(L24, 2)* Math.pow(L34, 2)* A2*R2 + Eta23* K3* Math.pow(L24, 2)* Math.pow(L34, 2)* A2*R2
+
       Eta12* K1* Math.pow(L34, 4)* A2*R2 + K2* Math.pow(L34, 4)* A2*R2 + K3* Math.pow(L12, 4)* A3*R3 +
       Eta34* K4* Math.pow(L12, 4)* A3*R3 + Eta23* K2* Math.pow(L12, 2)* Math.pow(L13, 2)* A3*R3 -
       Eta34* K4* Math.pow(L12, 2)* Math.pow(L13, 2)* A3*R3 - Eta23* K2* Math.pow(L12, 2)* Math.pow(L14, 2)* A3*R3
-
       2* K3* Math.pow(L12, 2)* Math.pow(L14, 2)* A3*R3 - Eta34* K4* Math.pow(L12, 2)* Math.pow(L14, 2)* A3*R3 -
       Eta23* K2* Math.pow(L13, 2)* Math.pow(L14, 2)* A3*R3 + Eta34* K4* Math.pow(L13, 2)* Math.pow(L14, 2)* A3*R3
+
       Eta23* K2* Math.pow(L14, 4)* A3*R3 + K3* Math.pow(L14, 4)* A3*R3 -
       Eta34* K4* Math.pow(L12, 2)* Math.pow(L23, 2)* A3*R3 + 2* Eta23* K2* Math.pow(L14, 2)* Math.pow(L23, 2)*
A3*R3 -
       Eta34* K4* Math.pow(L14, 2)* Math.pow(L23, 2)* A3*R3 - 2* K3* Math.pow(L12, 2)* Math.pow(L24, 2)* A3*R3 -
       Eta34* K4* Math.pow(L12, 2)* Math.pow(L24, 2)* A3*R3 - Eta23* K2* Math.pow(L13, 2)* Math.pow(L24, 2)* A3*R3
-
       Eta34* K4* Math.pow(L13, 2)* Math.pow(L24, 2)* A3*R3 - Eta23* K2* Math.pow(L14, 2)* Math.pow(L24, 2)* A3*R3
-
       2* K3* Math.pow(L14, 2)* Math.pow(L24, 2)* A3*R3 + Eta34* K4* Math.pow(L23, 2)* Math.pow(L24, 2)* A3*R3 +
       K3* Math.pow(L24, 4)* A3*R3 - Eta23* K2* Math.pow(L12, 2)* Math.pow(L34, 2)* A3*R3 +
       2* Eta34* K4* Math.pow(L12, 2)* Math.pow(L34, 2)* A3*R3 - Eta23* K2* Math.pow(L14, 2)* Math.pow(L34, 2)*
A3*R3 +
       Eta23* K2* Math.pow(L24, 2)* Math.pow(L34, 2)* A3*R3 +
       Eta13* ((Math.pow(L12, 2)* ((Math.pow(L23, 2) - Math.pow(L24, 2) - Math.pow(L34, 2))) +
             Math.pow(L24, 2)* ((2* Math.pow(L13, 2) - Math.pow(L23, 2) + Math.pow(L24, 2) - Math.pow(L34, 2))) -
             Math.pow(L14, 2)* ((Math.pow(L23, 2) + Math.pow(L24, 2) - Math.pow(L34, 2)))))* ((K3* A1*R1 +
             K1* A3*R3)) + ((K4* ((L12 - L13 - L23))* ((L12 + L13 -
                   L23))* ((L12 - L13 + L23))* ((L12 + L13 + L23)) +
             Eta34* K3* ((Math.pow(L12, 4) + ((L13 - L23))* ((L13 +
                         L23))* ((L14 - L24))* ((L14 + L24)) -
                   Math.pow(L12, 2)* ((Math.pow(L13, 2) + Math.pow(L14, 2) + Math.pow(L23, 2) + Math.pow(L24, 2) -
                         2* Math.pow(L34, 2))))) +
             Eta24* K2* ((Math.pow(L13, 4) + Math.pow(L23, 2)* (((-Math.pow(L14, 2)) + Math.pow(L34, 2))) -
                   Math.pow(L12, 2)* ((Math.pow(L13, 2) - Math.pow(L14, 2) + Math.pow(L34, 2))) -
                   Math.pow(L13, 2)* ((Math.pow(L14, 2) + Math.pow(L23, 2) - 2* Math.pow(L24, 2) +
                         Math.pow(L34, 2)))))))* A4*R4 -
       Eta14* ((Math.pow(L13, 2)* ((Math.pow(L23, 2) + Math.pow(L24, 2) - Math.pow(L34, 2))) +
             Math.pow(L12, 2)* ((Math.pow(L23, 2) - Math.pow(L24, 2) + Math.pow(L34, 2))) +
             Math.pow(L23, 2)* (((-2)* Math.pow(L14, 2) - Math.pow(L23, 2) + Math.pow(L24, 2) +
                   Math.pow(L34, 2)))))* ((K4* A1*R1 +
             K1* A4*R4))))/((12* Math.sqrt(((-Math.pow(L13, 4))* Math.pow(L24, 2) -
             Math.pow(L12, 4)* Math.pow(L34, 2) +
             Math.pow(L12, 2)* (((((-Math.pow(L13, 2)) + Math.pow(L14, 2)))* ((L23 -
                         L24))* ((L23 + L24)) + ((Math.pow(L13, 2) + Math.pow(L14, 2) +
                         Math.pow(L23, 2) + Math.pow(L24, 2)))* Math.pow(L34, 2) - Math.pow(L34, 4))) -
             Math.pow(L23, 2)* ((Math.pow(L14, 4) + Math.pow(L24, 2)* Math.pow(L34, 2) +
                   Math.pow(L14, 2)* ((Math.pow(L23, 2) - Math.pow(L24, 2) - Math.pow(L34, 2))))) +
             Math.pow(L13, 2)* ((Math.pow(L14, 2)* ((Math.pow(L23, 2) + Math.pow(L24, 2) - Math.pow(L34, 2))) +
                   Math.pow(L24, 2)* ((Math.pow(L23, 2) - Math.pow(L24, 2) + Math.pow(L34, 2))))))))));
    
    return result;
  }

  private double calcNormalization()
  {
    double result = 0;
    double denom = 0;
    double V=0;
    for(Tetra t : Triangulation.tetraTable.values()) {
      V=Volume.valueAt(t);
      result += cayleyVolumeDeriv(t);
     
      denom += V;
   
    }
    return (-1.0/3.0)*result / denom;
  }

}
