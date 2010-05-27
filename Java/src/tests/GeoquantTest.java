package tests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import Geoquant.Alpha;
import Geoquant.Eta;
import Geoquant.Length;
import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Triangulation.*;

public class GeoquantTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    initializeQuantities();
    
    testInvoker();
  }
  
  private static void testInvoker() {
    Vertex v = Triangulation.vertexTable.get(1);
    Radius r = Radius.At(Triangulation.vertexTable.get(1));
    Method m = null;
    try {
      m = r.getClass().getDeclaredMethod("valueAt", v.getClass());
      
    } catch (SecurityException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try {
      System.out.println("The answer is: " + m.invoke(null, v));
    } catch (IllegalArgumentException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private static void initializeQuantities() {
    TriangulationIO.read2DTriangulationFile("Data/2DManifolds/StandardFormat/tetrahedron.txt");
    for(Vertex v : Triangulation.vertexTable.values()) {
      Radius.At(v).setValue(1.0);
      Alpha.At(v).setValue(1.0);
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      Eta.At(e).setValue(1.0);
    }
  }
  
  private static void testLengths() {
    Length l;
    for(Edge e: Triangulation.edgeTable.values()) {
      l = Length.At(e);
      System.out.println(l + " at " + e + " = " + l.getValue());
    }
  }

}
