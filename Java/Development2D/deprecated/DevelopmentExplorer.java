package deprecated;

import frontend.AssetManager;
import frontend.SimulationManager;

public class DevelopmentExplorer {
  public static void main(String[] args) {
	
	String defaultPath = AssetManager.getAssetPath("surfaces/dodec2.off");	
    SimulationManager dui = new SimulationManager(defaultPath);
    dui.launchExponentialView();
    SimulationSettingsFrame vc = new SimulationSettingsFrame(dui);
        
    while (true) {
      dui.run(); // We only return from this call once ViewerController has
                 // signaled that we should quit the simulation.
      
      if(vc.sessionEnded()) break;
      
      dui = new SimulationManager(vc.getPath());
      dui.launchExponentialView();
      
      // Now we have a fresh, initialized DevelopmentUI instance. We're ready to
      // allow user input from vc again.
      vc.setSimulation(dui);
      vc.setEnabled(true);
    }
  }
}
