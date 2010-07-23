package experiments;
import geoquant.Curvature3D;
import geoquant.LEHR;
import geoquant.Length;
import geoquant.SectionalCurvature;
import geoquant.VEHR;
import inputOutput.TriangulationIO;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Random;

import triangulation.Edge;
import triangulation.Triangulation;
import triangulation.Vertex;


public class RandomPentachoron {
  
  public static void main(String[] args) throws FileNotFoundException{
    PrintStream ecurvature = new PrintStream("src/Experiments/ecurvature_data.txt");
    PrintStream vcurvature = new PrintStream("src/Experiments/vcurvature_data.txt");
    PrintStream lehr = new PrintStream("src/Experiments/lehr_data.txt");
    PrintStream vehr = new PrintStream("src/Experiments/vehr_data.txt");
    
    ecurvature.println("ecurvature");
    vcurvature.println("vcurvature");
    lehr.println("lehr");
    vehr.println("vehr");
    
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_regular.xml");
    
    for(int a = 1; a < 10000; a++)
    {
    
    
    double[][] vertices = new double[5][4];
    double distance;
    Random generator = new Random();
    
    for(int i=0; i < 5; i++){
      distance = 0;
      for(int j=0; j < 4; j++){
        vertices[i][j] = generator.nextGaussian();
      }
      
      for(int j=0; j < 4; j++){
        distance += Math.pow(vertices[i][j], 2);
      }
      
      distance = Math.pow(distance, .5);
      
      for(int j=0; j < 4; j++){
        vertices[i][j] = vertices[i][j]/distance;
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

      for(Edge e: Triangulation.edgeTable.values()){
        ecurvature.println(SectionalCurvature.At(e).getValue());
      }
      
      for(Vertex v: Triangulation.vertexTable.values()){
        vcurvature.println(Curvature3D.At(v).getValue());
      }
      
      lehr.println(LEHR.value());
      vehr.println(VEHR.value());
  }
  }
}
