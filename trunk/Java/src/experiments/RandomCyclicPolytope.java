package experiments;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Tetra;
import triangulation.Triangulation;
import triangulation.Vertex;

import geoquant.Length;
import geoquant.Curvature3D;
import geoquant.LEHR;
import geoquant.Length;
import geoquant.SectionalCurvature;
import geoquant.VEHR;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

import inputOutput.TriangulationIO;
import inputOutput.XMLParser;

public class RandomCyclicPolytope {

  public static Document createPolytope(int n) {
    Document doc = XMLParser.createDocument("http://code.google.com/p/geocam",
        "Triangulation");
    Element triangulation = doc.getDocumentElement();
    Element tetra;
    Element vertices;

    // Iterates over all 4-subsets of the n vertices
    int counter = 1;
    for (int i = 1; i <= n; i++) {
      for (int j = i + 1; j <= n; j++) {
        for (int k = j + 1; k <= n; k++) {
          for (int l = k + 1; l <= n; l++) {
            // Implements Gale's evenness condition: order the vertices in a
            // cycle, then
            // a pair of edges on the outer cycle form a tetrahedron iff they
            // are not
            // local to each other.
            if ((((i + 1) % n == j % n) && (j % n != k % n)
                && ((k + 1) % n == l % n) && (l % n != i % n))
                || (((j + 1) % n == k % n) && (k % n != l % n)
                    && ((l + 1) % n == i % n) && (i % n != j % n))) {
              tetra = doc.createElement("Tetra");
              tetra.setAttribute("index", "" + counter);
              counter++;
              vertices = doc.createElement("Vertices");
              vertices.setTextContent("" + i + " " + j + " " + k + " " + l);
              tetra.appendChild(vertices);
              triangulation.appendChild(tetra);
            }
          }
        }
      }
    }
    return doc;
  }

  public static void setLengths(int n, double center, double length) {
    Random generator = new Random();

    double[] t = new double[n];

    // Chooses n points uniformly from the interval [center - length, center +
    // length]
    for (int i = 0; i < n; i++) {
      t[i] = center + length * generator.nextDouble() - length / 2;
    }

    Arrays.sort(t);

    double[][] vertices = new double[n][4];

    // Sets points on the moment curve (t, t^2, ..., t^n)
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < 4; j++) {
        vertices[i][j] = Math.pow(t[i], j + 1);
      }
    }

    for (int l = 1; l <= n; l++) {
      for (int m = 1; m <= n; m++) {
        Vertex v = Triangulation.vertexTable.get(l);
        Vertex other = Triangulation.vertexTable.get(m);
        Edge e = v.getEdge(other);
        if (e != null) {
          double dist = Math.sqrt(Math.pow(vertices[m - 1][0]
              - vertices[l - 1][0], 2)
              + Math.pow(vertices[m - 1][1] - vertices[l - 1][1], 2)
              + Math.pow(vertices[m - 1][2] - vertices[l - 1][2], 2)
              + Math.pow(vertices[m - 1][3] - vertices[l - 1][3], 2));
          Length.at(e).setValue(dist);
        }
      }
    }
  }

  public static void main(String[] args) throws FileNotFoundException {
    int n = 10;
    int center = 0;
    int length = 10;

    PrintStream ecurvature = new PrintStream(
        "src/Experiments/cyclic_ecurvature.txt");
    PrintStream vcurvature = new PrintStream(
        "src/Experiments/cyclic_vcurvature.txt");
    PrintStream lehr = new PrintStream("src/Experiments/cyclic_lehr.txt");
    PrintStream vehr = new PrintStream("src/Experiments/cyclic_vehr.txt");

    ecurvature.println("ecurvature");
    vcurvature.println("vcurvature");
    lehr.println("lehr");
    vehr.println("vehr");

    TriangulationIO.readTriangulation(createPolytope(n));

    for (int a = 1; a <= 10000; a++) {
      setLengths(n, center, length);

      for (Edge e : Triangulation.edgeTable.values()) {
        if (!Double.isNaN(SectionalCurvature.at(e).getValue())) {
          ecurvature.println(SectionalCurvature.at(e).getValue());
        }
      }

      for (Vertex v : Triangulation.vertexTable.values()) {
        if (!Double.isNaN(Curvature3D.at(v).getValue())) {
          vcurvature.println(Curvature3D.at(v).getValue());
        }
      }

      if(!Double.isNaN(LEHR.value())){
        lehr.println(LEHR.value());
      }
      
      if(!Double.isNaN(VEHR.value())){
        vehr.println(VEHR.value());
      }
    }
  }
}
