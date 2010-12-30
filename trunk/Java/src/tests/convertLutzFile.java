package tests;

import inputOutput.TriangulationIO;

public class convertLutzFile {

 
  public static void main(String[] args){
    TriangulationIO.read2DLutzFile("Data/2DManifolds/LutzFormat/owl.txt");
    TriangulationIO.writeTriangulation("Data/Triangulations/2DManifolds/owl.xml");      
  }
  
}
