// Converts files without header information

package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import triangulation.Triangulation;

import InputOutput.TriangulationIO;

public class IOConversion1 {
  
  public static void main(String[] args){
    writeFiles();
  }
  
  public static void writeFiles()
  {
    Scanner scanner = null;
    String s = "";
    String name = "";
    
    try {
      scanner = new Scanner(new File("Data/Conversion/Surfaces/manifolds_lex_d2_n9.txt"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      line = line.replace(" ", "");
      if(line.contains("=")){
        s = line.substring(line.indexOf("="));
        name = line.substring(0, line.indexOf("="));
        if(line.contains("]]")){
          String standardPath = "Data/Conversion/" + name + ".xml";
          String lutzPath = "Data/Conversion/~lutz_" + name + ".txt";
          PrintStream lutzFile = null;
          try {
            lutzFile = new PrintStream(new File(lutzPath));
          } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          s = s.replace(" ", "");
          lutzFile.print(s);
          lutzFile.close();
          
          TriangulationIO.read2DLutzFile(lutzPath);
          TriangulationIO.writeTriangulation(standardPath);
          Triangulation.reset();
        }
      }
      else if(line.contains("]]"))
      {
        s = s.concat(line);
        String standardPath = "Data/Conversion/" + name + ".xml";
        String lutzPath = "Data/Conversion/~lutz_" + name + ".txt";
        PrintStream lutzFile = null;
        try {
          lutzFile = new PrintStream(new File(lutzPath));
        } catch (FileNotFoundException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        s = s.replace(" ", "");
        lutzFile.print(s);
        lutzFile.close();
        
        TriangulationIO.read2DLutzFile(lutzPath);
        TriangulationIO.writeTriangulation(standardPath);
        Triangulation.reset();
      }
      else{
        s = s.concat(line);
      }
    }
  }
}
