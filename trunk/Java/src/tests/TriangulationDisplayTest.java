package tests;

//import Geoquant.Length;
import io.TriangulationIO;
import triangulation.Triangulation;
import visualization.TriangulationDisplay;

public class TriangulationDisplayTest {

  public static void main(String[] args) throws InterruptedException {
    
    TriangulationIO.read2DTriangulationFile("Data/flip_test/convex_pair.txt");
    
    TriangulationDisplay.showTriangulation();
    
 //   for (Edge edge : Triangulation.edgeTable.values()) {
 //     if (edge.getIndex() == 0) {
  //      continue;
 //     }
 //     else {
 //       Length.At(edge).setValue(3);
 //     }
 //   }
    
    Thread.sleep(1000);
    
   Triangulation.reset(); 
   TriangulationIO.read2DTriangulationFile("Data/flip_test/eight_triangles_redux.txt");
   
   //six_triangles, six_triangles_out, pair_of_triangles, pair_of_triangles_out, really small --- IllegalStateException
   //pair_for_geo_tests --- NoSuchElementException - StdFace, PlanarDevelopment
   //pair_for_geo_tests_lutz --- NoSuchElementException - TriangulationIO
   //non_convex_pair --- IndexOutOfBoundsException - Length, Length.At, TriangulationIO
   
    System.out.println("About to call update");
    TriangulationDisplay.updateDisplay();
    System.out.println("called update");
  }
}
