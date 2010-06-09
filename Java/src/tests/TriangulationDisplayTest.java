package tests;

import InputOutput.TriangulationIO;
import Visualization.TriangulationDisplay;

public class TriangulationDisplayTest {

  public static void main(String[] args) {
    
    TriangulationIO.read2DTriangulationFile("Data/flip_test/eight_triangles_redux.txt");
    
    TriangulationDisplay.showTriangulation();
  }
}
