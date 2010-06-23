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

  static int total;
  static int vertices;
  
  public static void main(String[] args){
//    for(int j = 4; j <= 8; j++){
//      vertices = j;
//      writeFiles();
//      convertFiles();
//    }
    convert3DLutzToXML("Data/3DManifolds/LutzFormat/weber-seifert.txt");
  }
  
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
      
      if(line.contains("=")){
        s = line.substring(line.indexOf("="));
        if(line.contains("]]")){
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
      }
      else if(line.contains("]]"))
      {
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
  
  public static void convert2DStandardToXML(String standardPath) {
    TriangulationIO.read2DTriangulationFile(standardPath);
    String name = standardPath.substring(standardPath.lastIndexOf('/') + 1, 
                                         standardPath.lastIndexOf('.'));
    TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/" + name + ".xml");
  }
  
  public static void convert2DLutzToXML(String standardPath) {
    TriangulationIO.read2DLutzFile(standardPath);
    String name = standardPath.substring(standardPath.lastIndexOf('/') + 1, 
                                         standardPath.lastIndexOf('.'));
    TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/" + name + ".xml");
  }
  
  public static void convert3DStandardToXML(String standardPath) {
    TriangulationIO.read3DTriangulationFile(standardPath);
    String name = standardPath.substring(standardPath.lastIndexOf('/') + 1, 
                                         standardPath.lastIndexOf('.'));
    TriangulationIO.writeTriangulation("Data/Triangulations/3DManifolds/" + name + ".xml");
  }
  
  public static void convert3DLutzToXML(String standardPath) {
    TriangulationIO.read3DLutzFile(standardPath);
    String name = standardPath.substring(standardPath.lastIndexOf('/') + 1, 
                                         standardPath.lastIndexOf('.'));
    TriangulationIO.writeTriangulation("Data/Triangulations/3DManifolds/" + name + ".xml");
  }
}
