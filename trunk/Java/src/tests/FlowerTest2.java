package tests;

import java.lang.Thread;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Geoquant.Geoquant;
import Geoquant.Angle;
import Geoquant.Length;
import InputOutput.TriangulationIO;
import Triangulation.Simplex;
import Triangulation.Triangulation;
import Triangulation.Vertex;
import Triangulation.Edge;
import Triangulation.Face;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.*;

public class FlowerTest2 {

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

    Viewer viewer = null;
    boolean firstTime = true;
    IndexedFaceSetFactory ifsf = null;
    TriangulationIO
        .read2DTriangulationFile("Data/2DManifolds/StandardFormat/icosahedron.txt");

    for (Edge edge : Triangulation.edgeTable.values()) {
      Length.At(edge).setValue(Math.random() * .5 + .5);
    }

    double[] gamma = null;
    // gamma = new double[vertex.getLocalFaces().size()];
    // int count = 0;
    // for (Face face : vertex.getLocalFaces()) {
    // gamma[count] = Angle.valueAt(vertex, face);
    // count += 1;
    // }
    // make cyclic orderings of faces and edges
    Hashtable<Vertex, List<Face>> cycleFaces = new Hashtable<Vertex, List<Face>>();
    Hashtable<Vertex, List<Edge>> cycleEdges = new Hashtable<Vertex, List<Edge>>();
    for (Vertex vertex : Triangulation.vertexTable.values()) {
      List<Face> faceList = new LinkedList<Face>();
      List<Edge> edgeList = new LinkedList<Edge>();
      Face tmpFace = ((LinkedList<Face>) vertex.getLocalFaces()).getFirst();
      Edge tmpEdge = null;
      for (Edge e : tmpFace.getLocalEdges()) {
        if (vertex.getLocalEdges().contains(e) && !(edgeList.contains(e))) {
          tmpEdge = e;
          break;
        }
      }
      while (true) {
        faceList.add(tmpFace);
        edgeList.add(tmpEdge);
        // now find the face that is far from this edge
        for (Face f : tmpFace.getLocalFaces()) {
          if (vertex.getLocalFaces().contains(f)
              && !(tmpEdge.getLocalFaces().contains(f))) {
            // this is the face we want
            for (Edge edge : tmpFace.getLocalEdges()) {
              if (f.getLocalEdges().contains(edge)) {
                tmpEdge = edge;
                break;
              }
            }

            tmpFace = f;
            break;
          }

        }
        if (faceList.contains(tmpFace)) { // when we hit a face we've seen
          // before we stop
          break; // we dont want to loop forever
        }
      }
      cycleFaces.put(vertex, faceList);
      cycleEdges.put(vertex, edgeList);
    }

    for (Vertex vertex : Triangulation.vertexTable.values()) {
      System.out.println("Vertex " + vertex.getIndex());
      System.out.println("Faces ");
      for (Face f : cycleFaces.get(vertex)) {
        System.out.println("\t" + f.getIndex());
      }
      System.out.println("Edges ");
      for (Edge e : cycleEdges.get(vertex)) {
        System.out.println("\t" + e.getIndex());
      }
    }

    System.out.println("#########################################");

    //start showing the flowers
    Iterator<Vertex> vit = Triangulation.vertexTable.values().iterator();
    Vertex vertex = null;
    while (true) {
      if (vit.hasNext()) {
        vertex = vit.next();
      } else {
        vit = Triangulation.vertexTable.values().iterator();
        vertex = vit.next();
      }
      
      // move on to the gamma computation using the facelist
      gamma = new double[vertex.getLocalFaces().size()];
      List<Face> faces = cycleFaces.get(vertex);
      for (int i = 0; i < faces.size(); i++) {
        gamma[i] = Angle.valueAt(vertex, faces.get(i));
      }

      int R = 1;
      double sum;

      double minGamma = gamma[0];
      for (int i = 0; i < gamma.length; i++) {
        if (gamma[i] < minGamma) {
          minGamma = gamma[i];
        }
      }

      double cMax = Math.sqrt((2 * R * R - 2 * R * R * Math.cos(minGamma))
          / (2 + 2 * Math.cos(minGamma)));

      // gammas should be picked at this point
      int n = gamma.length;
      sum = 0;
      for (int i = 0; i < n; i++) {
        sum += gamma[i];
      }

      double[][] v = new double[n + 1][3];
      // negative curvature
      if (sum > 2 * Math.PI) {
        Newton newton = new Newton();
        double c = newton.newtonsMethod(cMax, new HypFunction(gamma, R));
        // System.out.println("Found value c " + c);

        double[] hypPsi = new double[n];
        double[] h = new double[n];
        double[] hypH = new double[n];
        double[] theta = new double[n];

        for (int i = 0; i < n; i++) {
          h[i] = Math.sqrt(2 * c * c + 2 * R * R - 2 * (c * c + R * R)
              * Math.cos(gamma[i]));
        }
        for (int i = 0; i < n; i++) {
          hypH[i] = Math.sqrt(h[i] * h[i] - 2 * c * 2 * c);
        }
        for (int i = 0; i < n; i++) {
          hypPsi[i] = Math.acos((2 * R * R - hypH[i] * hypH[i]) / (2 * R * R));
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
        double c = newton.newtonsMethod(cMax, new Function(gamma, R));
        // System.out.println("Found value c " + c);

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

      // this loop randomly scaled edges
      /*
       * for (int i = 0; i < n; i++) { double tmpRand = Math.random(); v[i][0] =
       * v[i][0] * (tmpRand * .25 + .5); v[i][1] = v[i][1] * (tmpRand * .25 +
       * .5); v[i][2] = v[i][2] * (tmpRand * .25 + .5); }
       */

      List<Edge> edges = cycleEdges.get(vertex);
      for (int i = 0; i < n - 1; i++) { // first normalize the edge to unit
        // length double mag =
        double mag = Math.sqrt(Math.pow(v[i][0], 2) + Math.pow(v[i][1], 2)
            + Math.pow(v[i][2], 2));
        v[i][0] = v[i][0] * Length.valueAt(edges.get(i)) / mag;
        v[i][1] = v[i][1] * Length.valueAt(edges.get(i)) / mag;
        v[i][2] = v[i][2] * Length.valueAt(edges.get(i)) / mag;
      }

      int[][] faceComb = makeFlower(n);

      if (firstTime) {
        firstTime = false;
        ifsf = new IndexedFaceSetFactory();
        ifsf.setVertexCount(n + 1);
        ifsf.setFaceCount(n);
        ifsf.setVertexCoordinates(v);
        ifsf.setFaceIndices(faceComb);
        ifsf.setGenerateEdgesFromFaces(true);
        ifsf.setGenerateFaceNormals(true);
        ifsf.update();
        viewer = JRViewer.display(ifsf.getIndexedFaceSet());
      } else {
        ifsf.setVertexCount(n + 1);
        ifsf.setFaceCount(n);
        ifsf.setVertexCoordinates(v);
        ifsf.setFaceIndices(faceComb);
        ifsf.setGenerateEdgesFromFaces(true);
        ifsf.setGenerateFaceNormals(true);
        ifsf.update();
        viewer.render();
      }

      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }
}
