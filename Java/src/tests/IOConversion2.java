// Converts files containing header information ##

package tests;

import inputOutput.TriangulationIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import triangulation.Triangulation;


public class IOConversion2 {

  public static void main(String[] args){
      writeFiles();
  }
  
  public static void writeFiles()
  {
    Scanner scanner = null;
    String s = "";
    String name = "";
    
    try {
      scanner = new Scanner(new File(""));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    while(scanner.hasNextLine()) {
      String line = scanner.nextLine();
      if(line.contains(".") && !line.contains("...")){
        int spaceIndex;
        spaceIndex = line.substring(4).indexOf(" ");
        name = line.substring(4, 4 + spaceIndex);
      }
      if(line.contains(":=")){
        s = line.substring(line.indexOf(":="));
        }
      
      else if(line.contains(";"))
      {
        s = s.concat(line.substring(0, line.indexOf(";")));
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
        
        TriangulationIO.read3DLutzFile(lutzPath);
        TriangulationIO.writeTriangulation(standardPath);
        Triangulation.reset();
      }
      else if(line.contains("[")){
        s = s.concat(line);
      }
    }
  }
}
