// Just a little app to play around with loading triangulations and using geoquants
// and eventually test the coordinate and coordinate transform geoquants

package development;

import java.util.Iterator;

import InputOutput.TriangulationIO;
import Triangulation.*;
import Geoquant.*;

public class GeocamTest {

  public static void main(String[] args){
    
    TriangulationIO.readTriangulation("Data/Triangulations/3DManifolds/3-torus.xml");

    Iterator i = null;
    
    //print some edge info
    System.out.printf("\n\nEDGE INFO\n");
    System.out.printf("Edge count: %d\n",Triangulation.edgeTable.size());
    
    i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = (Integer)i.next();
      Edge e = Triangulation.edgeTable.get(key);
      Length.At(e).setValue(1);
      
      System.out.printf("Edge %d: \n",key);
      System.out.printf("   Num local faces: %d\n", e.getLocalFaces().size());
      System.out.printf("   Length: %f\n",Length.valueAt(e)); 
    }
    
    //print some face info
    System.out.printf("\n\nFACE INFO\n");
    System.out.printf("Face count: %d\n",Triangulation.faceTable.size());

    /*
    i = Triangulation.faceTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = (Integer)i.next();
      Face f = Triangulation.faceTable.get(key);
      
      System.out.printf("Face %d: \n",key);
      System.out.printf("   Num local faces: %d\n", f.getLocalFaces().size());
      System.out.printf("   Area: %f\n",Area.valueAt(f)); 
      
      //coords
      System.out.printf("   Coords: ");
      Iterator j = f.getLocalVertices().iterator();
      while(j.hasNext()){
        Vertex v = (Vertex)j.next();
        System.out.print(Coord2D.coordAt(v,f));
      }
      System.out.printf("\n");*/
    
      i = Triangulation.tetraTable.keySet().iterator();
      while(i.hasNext()){
      Integer key = (Integer)i.next();
      Tetra t = Triangulation.tetraTable.get(key);
      
      System.out.printf("Tetra %d: \n",key);
      System.out.printf("   Num local tetras: %d\n", t.getLocalTetras().size());
      System.out.printf("   Volume: %f\n",Volume.valueAt(t)); 
      
      //coords
      System.out.printf("   Coords: ");
      Iterator j = t.getLocalVertices().iterator();
      while(j.hasNext()){
        Vertex v = (Vertex)j.next();
        System.out.print(Coord3D.coordAt(v,t));
      }
      System.out.printf("\n");
    }
    
  }
}
