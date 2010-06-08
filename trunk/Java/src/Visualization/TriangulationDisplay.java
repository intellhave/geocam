package Visualization;

import java.util.Hashtable;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.plugin.JRViewer;

import Triangulation.Vertex;
import Triangulation.Face;
import Triangulation.Triangulation;
import Visualization.PlanarDevelopment;

public class TriangulationDisplay {
  public static void showTriangulation() {
    
    //generate the coordinates
    PlanarDevelopment pd = new PlanarDevelopment();
    pd.generatePlane();
    
    //Need to make a mapping from Vertices to 0 through n where n is the number of vertices
    Hashtable<Vertex, Integer> labeling = new Hashtable<Vertex, Integer>();
    int index = 0;
    for (Vertex vertex : Triangulation.vertexTable.values()) {
      labeling.put(vertex, index);
      index += 1;
    }
    int numVertices = index;
    double[][] vertCoords = new double[numVertices][3];
    for (Vertex vertex : Triangulation.vertexTable.values()) {
      Point point =  pd.getPoint(vertex);
      vertCoords[labeling.get(vertex)][0] = point.x;
      vertCoords[labeling.get(vertex)][1] = point.y;
      vertCoords[labeling.get(vertex)][2] = 0;
    }
    
    int numFaces = Triangulation.faceTable.size();
    int[][] combinatorics = new int[numFaces][3];
    int faceIndex = 0; //keeps track of which face we're on
    //note that it doesn't matter if this index relates to the face's index in any way
    for (Face face : Triangulation.faceTable.values()) {
      int vertIndex = 0;
      for (Vertex vertex : face.getLocalVertices()) {
        combinatorics[faceIndex][vertIndex] = labeling.get(vertex);
        vertIndex += 1;
      }
      faceIndex += 1;
    }
    
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(Triangulation.vertexTable.keySet().size());
    ifsf.setFaceCount(Triangulation.faceTable.keySet().size());
    ifsf.setVertexCoordinates(vertCoords);
    ifsf.setFaceIndices(combinatorics);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setGenerateFaceNormals(true);
    ifsf.update();
    JRViewer.display(ifsf.getIndexedFaceSet());
  }
}
