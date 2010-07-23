package experiments;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Tetra;
import triangulation.Triangulation;
import triangulation.Vertex;

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
            // Implements Gale's evenness condition: order the vertices in a cycle, then 
            // a pair of edges on the outer cycle form a tetrahedron iff they are not 
            // local to each other.
            if((((i+1)%n == j%n) && (j%n != k%n) && ((k+1)%n == l%n) && (l%n != i%n)) || 
               (((j+1)%n == k%n) && (k%n != l%n) && ((l+1)%n == i%n) && (i%n != j%n))) {
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

  public static void main(String[] args) {
    int n = 5;
    TriangulationIO.readTriangulation(createPolytope(n));

    /* ## Test
    for (Vertex v : Triangulation.vertexTable.values()) {
      System.out.println(v + ":\n\t" + v.getLocalVertices() + "\n\t"
          + v.getLocalEdges() + "\n\t" + v.getLocalFaces() + "\n\t"
          + v.getLocalTetras());
    }
    System.out.println();
    for (Edge e : Triangulation.edgeTable.values()) {
      System.out.println(e + ":\n\t" + e.getLocalVertices() + "\n\t"
          + e.getLocalEdges() + "\n\t" + e.getLocalFaces() + "\n\t"
          + e.getLocalTetras());
    }
    System.out.println();
    for (Face f : Triangulation.faceTable.values()) {
      System.out.println(f + ":\n\t" + f.getLocalVertices() + "\n\t"
          + f.getLocalEdges() + "\n\t" + f.getLocalFaces() + "\n\t"
          + f.getLocalTetras());
    }
    System.out.println();
    for (Tetra t : Triangulation.tetraTable.values()) {
      System.out.println(t + ":\n\t" + t.getLocalVertices() + "\n\t"
          + t.getLocalEdges() + "\n\t" + t.getLocalFaces() + "\n\t"
          + t.getLocalTetras());
    }
    */
  }
}
