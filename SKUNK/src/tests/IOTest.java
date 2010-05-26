package tests;

import java.io.File;

import InputOutput.TriangulationIO;

public class IOTest {

  public static void main(String[] args) {
    File testFile = new File("../Data/2DManifolds/StandardFormat/tetrahedron.txt");
    if(!testFile.exists()) {
      System.out.println("File does not exist");
    } else {
      System.out.println("Exists!");
    }
    //TriangulationIO.read2DTriangulationFile("../Data/2DManifolds/StandardFormat/tetrahedron.txt");
  }

}
