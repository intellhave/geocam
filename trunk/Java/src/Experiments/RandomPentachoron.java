package Experiments;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

import Geoquant.Curvature3D;
import Geoquant.Length;
import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class RandomPentachoron {
  
  public static void main(String[] args) throws FileNotFoundException{
    PrintStream output1 = new PrintStream("src/Experiments/output1.txt");
    output1.println("Vertex Curvature");
    
    for(int a = 1; a < 10; a++)
    {
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_regular.xml");
    
    double[][] vertices = new double[5][4];
    double radius = 0;
    Random generator = new Random();
    
    for(int i=0; i < 5; i++){
      for(int j=0; j < 4; j++){
        vertices[i][j] = generator.nextGaussian();
      }
      
      for(int j=0; j < 4; j++){
        radius += Math.pow(vertices[i][j], 2);
      }
      
      radius = Math.pow(radius, .5);
      
      for(int j=0; j < 4; j++){
        vertices[i][j] = vertices[i][j]/radius;
      }
    }
      for(int n = 1; n <= 5; n++){
        for(int m = 1; m <= 5; m++){
          Vertex v = Triangulation.vertexTable.get(n);
          Vertex other = Triangulation.vertexTable.get(m);
          Edge e = v.getEdge(other);
          if(e != null){
            double dist = 
              Math.sqrt(Math.pow(vertices[m-1][0] - vertices[n-1][0], 2)
                + Math.pow(vertices[m-1][1] - vertices[n-1][1], 2)
                + Math.pow(vertices[m-1][2] - vertices[n-1][2], 2)
                + Math.pow(vertices[m-1][3] - vertices[n-1][3], 2));
              Length.At(e).setValue(dist);
          }
        }
      }
 
      for(Vertex v: Triangulation.vertexTable.values()){
        output1.println(Curvature3D.At(v).getValue());
      }
  }
  }
}
