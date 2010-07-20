package gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class XMLFilter extends FileFilter {
  private static XMLFilter filter = new XMLFilter();
  
  public static XMLFilter getFilter() {
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
    return extension.equals(".xml");
  }
  public String getDescription() {
    return "XML (.xml)";
  }

}
