package frontend;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import development.Development;

public class GeoquantViewerExplorer extends Thread {

	private SimulationManager simMan;
	
	private JFrame frame;
	
	private ViewMenu viewMenu;
	private DevelopmentSettingsPanel dsp;
	private ManifoldDisplaySettingsPanel mdsp;
	private ZoomSlider expZoom;

	public GeoquantViewerExplorer( Development dev ) {
		simMan = new SimulationManager( dev );

		frame = new JFrame();

		dsp = new DevelopmentSettingsPanel(simMan.getDevelopment());
		mdsp = new ManifoldDisplaySettingsPanel();

		viewMenu = new ViewMenu(simMan);
		expZoom = new ZoomSlider();
		
		viewMenu.addAllViewsController(mdsp);
		viewMenu.addExponentialViewController(expZoom);
		
		assembleSwingComponents();
	}

	public void assembleSwingComponents() {
		frame.addWindowListener(new CloseListener());
		Container cp = frame.getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.Y_AXIS));

		// We're not allowing the user to choose views in this explorer, so
		// so we're not going to incorporate viewMenu into the UI --- it serves
		// only to launch views.

		// JMenuBar jmb = new JMenuBar();
		// frame.setJMenuBar(jmb);
		// jmb.add(viewMenu);

		frame.add(dsp);
		frame.add(mdsp);

		JPanel camPanel = new JPanel();
		camPanel.setLayout(new BoxLayout(camPanel, BoxLayout.Y_AXIS));
		camPanel.setBorder(BorderFactory.createTitledBorder("Camera Distances"));
		camPanel.add(expZoom);

		frame.add(camPanel);
		frame.pack();		
	}

	private class CloseListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			simMan.terminate();
		}
	}
	
	public void run(){
		frame.setVisible(true); // Enable user input.
		viewMenu.createExponentialView(); // Load a viewer.
		simMan.run();
	}
	
	public void terminate(){
		simMan.terminate();
		frame.setVisible(false);
		viewMenu.closeViews();
	}
}
