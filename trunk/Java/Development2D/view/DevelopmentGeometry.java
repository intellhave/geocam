package view;

import java.util.ArrayList;

class DevelopmentGeometry {

  private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
  private ArrayList<double[]> geometry_texCoords = new ArrayList<double[]>();
  private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();

  void addFace(double[][] faceverts, double[][] texCoords, double zvalue) {

    int nverts = faceverts.length;
    int vi = geometry_verts.size();

    int[] newface = new int[nverts];
    
    for (int k = 0; k < nverts; k++) {
      double[] newvert = new double[3];
      double[] texCoord = new double[3];
      newvert[0] = faceverts[k][0]; texCoord[0] = texCoords[k][0];
      newvert[1] = faceverts[k][1]; texCoord[1] = texCoords[k][1];
      if(faceverts[k].length > 2){
        newvert[2] = faceverts[k][2]; texCoord[2] = texCoords[k][2];
      } else {
        newvert[2] = zvalue; texCoord[2] = zvalue;
      }
      geometry_verts.add(newvert);
      geometry_texCoords.add(texCoord);
      newface[k] = vi++;
    }
    geometry_faces.add(newface);
  }

  double[][] getVerts() {
    return (double[][]) geometry_verts.toArray(new double[0][0]);
  }
  
  double[][] getTexCoords(){
    return (double[][]) geometry_texCoords.toArray(new double[0][0]);
  }

  int[][] getFaces() {
    return (int[][]) geometry_faces.toArray(new int[0][0]);
  }
}