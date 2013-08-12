package frontend;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import util.AssetManager;

public class ResearcherDevelopmentExplorer {

	private SimulationManager simMan;

	private JFrame frame;
	
	private boolean sessionEnded;
	
	private ManifoldMenu manMenu;
	private ViewMenu viewMenu;

	private DevelopmentSettingsPanel dsp;
	private MarkerSettingsPanel msp;
	private ManifoldDisplaySettingsPanel mdsp;
	private DecorationsPanel dp;
	private ZoomSlider expZoom;
	private ZoomSlider embZoom;

	public static void main(String[] args){
		new ResearcherDevelopmentExplorer();
	}
	
	public ResearcherDevelopmentExplorer(){
		String defaultPath = AssetManager.getAssetPath("off/tetra2.off");

		simMan = new SimulationManager(defaultPath);

		frame = new JFrame();
		manMenu = new ManifoldMenu(simMan, frame);
		viewMenu = new ViewMenu(simMan);

		dsp = new DevelopmentSettingsPanel(simMan.getDevelopment());
		msp = new MarkerSettingsPanel(simMan.getMarkerHandler());
		mdsp = new ManifoldDisplaySettingsPanel();
		dp = new DecorationsPanel( simMan.getMarkerHandler() );
		
		expZoom = new ZoomSlider("Exponential Map View");
		embZoom = new ZoomSlider("Embedded View");
		embZoom.setMaximum(800); // Allow the embedded camera to move a little further away.

		viewMenu.addAllViewsController(mdsp);
		viewMenu.addExponentialViewController(expZoom);
		viewMenu.addEmbeddedViewController(embZoom);

		assembleSwingComponents();

		viewMenu.createExponentialView();

		while (true) {
			simMan.run(); // We only return from this call once ManifoldMenu
							// signals a new simulation is needed.			
			manMenu.setInterfacesEnabled(false);

			if( sessionEnded ){
				viewMenu.closeViews();
				break;
			}

			simMan = new SimulationManager(manMenu.getManifoldPath());
			manMenu.setSimulationManager(simMan);
			viewMenu.setSimulationManager(simMan);

			// Note: mdsp, expZoom, and embZoom don't need to be updated,
			// because they are ViewControllers and work through the ViewMenu
			// object (which gets updated).
			dp.setMarkerHandler(simMan.getMarkerHandler());
			dsp.setDevelopment(simMan.getDevelopment());
			msp.setMarkerHandler(simMan.getMarkerHandler());

			viewMenu.createExponentialView();
			
			manMenu.setInterfacesEnabled(true);
		}
		
		System.exit(0);
	}

	public void assembleSwingComponents() {		
		frame.addWindowListener( new CloseListener() );
		Container cp = frame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

		JMenuBar jmb = new JMenuBar();
		frame.setJMenuBar(jmb);

		jmb.add(manMenu);
		jmb.add(viewMenu);

		frame.add(dsp);
		frame.add(msp);		
		frame.add(mdsp);
		frame.add(dp);
		
		JPanel camPanel = new JPanel();
		camPanel.setLayout(new BoxLayout(camPanel, BoxLayout.Y_AXIS));
		camPanel.setBorder(BorderFactory.createTitledBorder("Camera Distances"));
		camPanel.add(expZoom);
		camPanel.add(embZoom);
		frame.add(camPanel);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private class CloseListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			sessionEnded = true;
			simMan.terminate();
		}
	}
}