// Just a little app to play around with loading triangulations and using geoquants
// and eventually test the coordinate and coordinate transform geoquants

package development;

import java.util.Iterator;

import InputOutput.TriangulationIO;
import Triangulation.Triangulation;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Vertex;
import Triangulation.StdFace;
import Geoquant.Length;
import Geoquant.Area;

public class GeocamTest {

  public static void main(String[] args){
    
    TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/tetrahedron.xml");

    Integer key = 0;
    Iterator i = null;
    
    //print some edge info
    System.out.printf("\n\nEDGE INFO\n");
    System.out.printf("Edge count: %d\n",Triangulation.edgeTable.size());
  
    i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      key = (Integer)i.next();
      Edge e = Triangulation.edgeTable.get(key);
      Length.At(e).setValue(1);
      
      System.out.printf("Edge %d: \n",key);
      System.out.printf("   Num local faces: %d\n", e.getLocalFaces().size());
      System.out.printf("   Length: %f\n",Length.valueAt(e)); 
    }
    
    //print some face info
    System.out.printf("\n\nFACE INFO\n");
    System.out.printf("Face count: %d\n",Triangulation.faceTable.size());

    i = Triangulation.faceTable.keySet().iterator();
    while(i.hasNext()){
      key = (Integer)i.next();
      Face f = Triangulation.faceTable.get(key);
      
      System.out.printf("Face %d: \n",key);
      System.out.printf("   Num local faces: %d\n", f.getLocalFaces().size());
      System.out.printf("   Area: %f\n",Area.valueAt(f)); 
    }
    
    
  }
}
