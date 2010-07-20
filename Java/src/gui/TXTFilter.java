package gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class TXTFilter extends FileFilter {
  private static TXTFilter filter = new TXTFilter();
  
  public static TXTFilter getFilter() {
    return filter;
  }
  
  public boolean accept(File f) {
    if (f.isDirectory()) {
      return true;
    }
    
    String extension = f.getName();
    int index = extension.lastIndexOf('.');
    if(index < 0) {
      return false;
    }

    extension = extension.substring(index).toLowerCase();
    return extension.equals(".txt");
  }
  public String getDescription() {
    return "Text Files (.txt)";
  }

}
