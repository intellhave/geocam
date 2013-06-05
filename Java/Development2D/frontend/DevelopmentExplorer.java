package frontend;

public class DevelopmentExplorer {
  public static void main(String[] args) {
    String defaultPath = "Data/Triangulations/2DManifolds/tetrahedron4.xml";

    DevelopmentUI dui = new DevelopmentUI(defaultPath);
    ViewerController vc = new ViewerController(dui);

    while (true) {
      dui.run(); // We only return from this call once ViewerController has
                 // signaled that we should quit the simulation.
      
      if(vc.sessionEnded()) break;
      
      dui = new DevelopmentUI(vc.getPath());
      // Now we have a fresh, initialized DevelopmentUI instance. We're ready to
      // allow user input from vc again.
      vc.setSimulation(dui);
      vc.setEnabled(true);
    }
  }
}
