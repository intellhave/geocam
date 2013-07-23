package frontend;

import java.net.URI;

public class DevelopmentExplorer {
  public static void main(String[] args) {
	
	URI root = null;
	try{
		root = DevelopmentExplorer.class.getProtectionDomain().getCodeSource().getLocation().toURI();
	} catch (Exception ee) {
		 System.err.println("Error determining location of executable. Aborting.\n");
		 System.exit(1);
	}	
    String defaultPath = root.resolve("../Data/surfaces/dodec2.off").getPath();

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
