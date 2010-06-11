package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class IOConversion {

  
  public static void main(String[] args){
    writeFiles();
    convertFiles();
  }
  
  static int total;
  static int vertices = 7;
  
  public static void writeFiles()
  {
    Scanner scanner = null;
    String s = "";
    int i = 1;
    
    try {
      scanner = new Scanner(new File("Data/Conversion/manifolds_lex_d2_n" + vertices + ".txt"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      int j = line.indexOf("=");
      
      if(line.contains("=")){
        s = line.substring(line.indexOf("="));
      }
      else if(line.contains("]]"))
      {
        s = s.concat(line);
        String filename = "Data/Conversion/lutz_n" + vertices + "_" + i + ".txt";
        PrintStream file = null;
        try {
          file = new PrintStream(new File(filename));
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        file.print(s);
        file.close();
        i++;
      }
      else{
        s = s.concat(line);
      }
      total = i - 1;
    }
    
    
  }
  
  public static void convertFiles()
  {
    for(int i = 1; i <= total; i++){
      TriangulationIO.read2DLutzFile("Data/Conversion/lutz_n" + vertices + "_" + i + ".txt");
      TriangulationIO.write2DTriangulationFile("Data/Conversion/standard_n" + vertices + "_" + i + ".txt");
      Triangulation.reset();
    }
  }
}
