package view;

import java.util.ArrayList;

public class DevelopmentGeometrySim3D {

  private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
  private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();
  private ArrayList<int[]> geometry_edges = new ArrayList<int[]>();

  public void addFace(double[][] faceverts, double height) {

    int n = faceverts.length;
    double[][] ifsf_verts = new double[2 * n][3];
    int[][] ifsf_edges = new int[3 * n][2];
    int[][] ifsf_faces = new int[2][n];

    for (int i = 0; i < n; i++) {
      // for some reason, switching '-' sign makes light work
      // but colors are flipped either way
      ifsf_verts[i] = new double[] { faceverts[i][0], faceverts[i][1], height };
      ifsf_verts[i + n] = new double[] { faceverts[i][0], faceverts[i][1],
          -height };
    }

    for (int i = 0; i < n; i++) {
      int j = (i + 1) % n;
      ifsf_edges[i] = new int[] { i + geometry_verts.size(),
          j + geometry_verts.size() };
      ifsf_edges[i + n] = new int[] { i + n + geometry_verts.size(),
          j + n + geometry_verts.size() };
      ifsf_edges[i + n + n] = new int[] { i + geometry_verts.size(),
          i + n + geometry_verts.size() };
    }

    for (int i = 0; i < n; i++) {
      ifsf_faces[0][i] = geometry_verts.size() + i;
      ifsf_faces[1][i] = n + (n - 1) - i + geometry_verts.size();
    }

    geometry_faces.add(ifsf_faces[0]);
    geometry_faces.add(ifsf_faces[1]);

    for (int i = 0; i < 2 * n; i++) {
      geometry_verts.add(ifsf_verts[i]);
      geometry_edges.add(ifsf_edges[i]);
    }
    for (int i = 2 * n; i < 3 * n; i++) {
      geometry_edges.add(ifsf_edges[i]);
    }
  }

  public double[][] getVerts() {
    return (double[][]) geometry_verts.toArray(new double[0][0]);
  }

  public int[][] getFaces() {
    return (int[][]) geometry_faces.toArray(new int[0][0]);
  }

  public int[][] getEdges() {
    return (int[][]) geometry_edges.toArray(new int[0][0]);
  }
}