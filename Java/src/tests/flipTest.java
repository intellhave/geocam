package tests;

import triangulation.Edge;
import triangulation.StdFace;
import triangulation.Triangulation;
import Visualization.TriangulationDisplay;
import FlipAlgorithm.HingeFlip;
import InputOutput.TriangulationIO;

public class flipTest {
  public static void main(String[] args) throws InterruptedException {
    TriangulationIO
        .read2DTriangulationFile("Data/flip_test/eight_triangles_redux.txt");

    TriangulationDisplay.showTriangulation();

    //flips the four edges that are diagonal in the eight_triangles_redux file
    for (Edge edge : Triangulation.edgeTable.values()) {
      int ind = edge.getIndex();
      switch(ind) {
      case 1:
      case 6:
      case 8:
      case 15:
      case 16:
      case 13:
      case 12:
      case 4:
      case 3:
      case 5:
      case 10:
      case 9:
        continue;
      }

      Thread.sleep(2000);

      HingeFlip.flip(edge);

      TriangulationDisplay.updateDisplay();
    }

  }
}
