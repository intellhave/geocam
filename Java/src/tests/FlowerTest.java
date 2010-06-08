package tests;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.plugin.JRViewer;

public class FlowerTest {

  public static int[][] makeFlower(int n) {
    int[][] flower = new int[n][3];
    
    for (int i = 0; i < n; i++) {
      flower[i][0] = i;
      flower[i][1] = (i + 1) % n;
      flower[i][2] = n;
    }
    return flower;
  }

  public static void main(String[] args) {
    
    int R = 1;
    double sum;
    //double[] gamma =new double[16];
    //double[] gamma = {.6,.7,.9,.51,.8,.9,.9,1.1,1.5,.5};
     // for (int i = 0; i < gamma.length; i++) {
    ////  gamma[i] = .8;
    //} 
    
     double[] gamma = new double[10];
      for (int i = 0; i < gamma.length; i++) {
        gamma[i] = Math.random() + 2;
      }
      sum = 0;
      for (int i = 0; i < gamma.length; i++) {
        sum += gamma[i];
      }
      double gammaSum = 10; //change this to alter the curvature of the flower
      for (int i = 0; i < gamma.length; i++) {
        gamma[i] = gammaSum * gamma[i] / sum;
        //gamma[i] = 1;
      } 
      
    
    double minGamma = gamma[0];
    for (int i = 0; i < gamma.length; i++) {
      if (gamma[i] < minGamma) {
        minGamma = gamma[i];
      }
    }
    
    double cMax = Math.sqrt((2 * R*R - 2 * R * R * Math.cos(minGamma))/(2 + 2 * Math.cos(minGamma)));
    
    // gammas should be picked at this point
    int n = gamma.length;
    sum = 0;
    for (int i = 0; i < n; i++) {
      sum += gamma[i];
    }
    System.out.println(sum + "what");

    double[][] v = new double[n + 1][3];
    // negative curvature
    if (sum > 2 * Math.PI) {
      Newton newton = new Newton();
      double c = newton.newtonsMethod(cMax, new HypFunction(gamma, R));
      //System.out.println("Found value c " + c);
      
      double[] hypPsi = new double[n];
      double[] h = new double[n];
      double[] hypH = new double[n];
      double[] theta = new double[n];
      
      for (int i = 0; i < n; i++) {
        h[i] = Math.sqrt(2 * c * c + 2 * R * R - 2 * (c * c + R * R)
            * Math.cos(gamma[i]));
      }
      for(int i = 0; i < n; i++) {
        hypH[i] = Math.sqrt(h[i]*h[i] - 2*c*2*c);
      }
      for (int i = 0; i < n; i++) {
        hypPsi[i] = Math.acos((2*R*R - hypH[i]*hypH[i])/(2*R*R));
      }
      
      for (int i = 0; i < n; i++) {
        if (i == 0) {
          theta[i] = 0;
        } else {
          theta[i] = theta[i - 1] + hypPsi[i];
        }
      }
      
      for (int i = 0; i <= n; i++) {
        if (i == n) {
          v[i][0] = 0;
          v[i][1] = 0;
          v[i][2] = 0;
        } else {
          v[i][0] = R * Math.cos(theta[i]);
          v[i][1] = R * Math.sin(theta[i]);
          if (i % 2 == 0) {
            v[i][2] = -c;
          } else {
            v[i][2] = c;
          }
        }
      }
      
    } else { // positive curvature

      Newton newton = new Newton();
      double c = newton.newtonsMethod(1, new Function(gamma, R));
      //System.out.println("Found value c " + c);

      double[] psi = new double[n];
      double[] h = new double[n];
      double[] theta = new double[n];

      for (int i = 0; i < n; i++) {
        h[i] = Math.sqrt(2 * c * c + 2 * R * R - 2 * (c * c + R * R)
            * Math.cos(gamma[i]));
      }
      for (int i = 0; i < n; i++) {
        psi[i] = Math.acos((2 * R * R - (h[i] * h[i])) / (2 * R * R));
      }

      for (int i = 0; i < n; i++) {
        if (i == 0) {
          theta[i] = 0;
        } else {
          theta[i] = theta[i - 1] + psi[i];
        }
      }

      for (int i = 0; i <= n; i++) {
        if (i == n) {
          v[i][0] = 0;
          v[i][1] = 0;
          v[i][2] = 0;
        } else {
          v[i][0] = R * Math.cos(theta[i]);
          v[i][1] = R * Math.sin(theta[i]);
          v[i][2] = -c;
        }
      }

    }
    
    for (int i = 0; i < n; i++) {
      double tmpRand = Math.random();
      v[i][0] = v[i][0] * (tmpRand * .25 + .5);
      v[i][1] = v[i][1] * (tmpRand * .25 + .5);
      v[i][2] = v[i][2] * (tmpRand * .25 + .5);
    }

    int[][] faces = makeFlower(n);

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(n + 1);
    ifsf.setFaceCount(n);
    ifsf.setVertexCoordinates(v);
    ifsf.setFaceIndices(faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setGenerateFaceNormals(true);
    ifsf.update();
    JRViewer.display(ifsf.getIndexedFaceSet());

  }

}
