package frontend;

import gui.TXTFilter;
import inputOutput.TriangulationIO;

import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileFilter;

import util.AssetManager;

public class ManifoldMenu extends JMenu {
	private static final long serialVersionUID = 1L;

	private SimulationManager sim;
	private List<Component> uis;
	private String currentManifoldPath;
	
	public ManifoldMenu(SimulationManager sim){
		this.sim = sim;
		super.setText("File");
		
		String[][] namesAndPaths = new String[][] {
				{ "Regular Tetrahedron", "surfaces/tetra3.off" },
				// { "Regular Tetrahedron - One Color", "surfaces/tetra.off" },
				{ "Irregular Tetrahedron", "surfaces/tetra2.off" },
				{ "Cube", "surfaces/cube_surf.off" },
				{ "Dodecahedron", "surfaces/dodec2.off" },
				{ "Icosahedron", "off/icosa.off" },
				{ "Cone", "surfaces/scaledCone.off" },
				{ "Neckpinch", "surfaces/large_neckpinch.off" },
				{ "Nonembedded Tetrahedron with Negative Curvature",
						"Triangulations/2DManifolds/tetrahedronnonembed2.xml" },
				{ "Suspension of a Triangle (nonembedded)",
						"Triangulations/2DManifolds/triangularPrism.xml" } };

		for (String[] pair : namesAndPaths) {
			String name = pair[0];
			String path = pair[1];
			JMenuItem jmi = new JMenuItem();
			super.add(jmi);
			jmi.setText(name);
			SurfaceLoader sl = new SurfaceLoader(AssetManager.getAssetPath(path));
			jmi.addActionListener(sl);
		}
		
		JMenuItem other = new JMenuItem();
		
		other.setText("Select a file from disk...");
		super.add(other);
		other.addActionListener( new SurfaceLoaderDialogListener( this ) );
		
		this.uis = new LinkedList<Component>();
	}

	public void setSimulationManager(SimulationManager other) {
		this.sim = other;		
	}
	
	public ManifoldMenu(SimulationManager sim, Component userInterface){
		this(sim);
		this.uis.add(userInterface);
		
	}
	
	public ManifoldMenu(SimulationManager sim, List<Component> userInterfaces) {		
		this(sim);
		for(Component jc : userInterfaces){
			this.uis.add(jc);
		}		
	}

	public void setInterfacesEnabled( boolean enable ){
		for( Component jc : uis )
			jc.setEnabled(enable);	
	}
	
	public String getManifoldPath(){
		return this.currentManifoldPath;
	}
	
	private class SurfaceLoader implements ActionListener {
		private String path;

		public SurfaceLoader(String surfaceFilePath) {			
			this.path = surfaceFilePath;
		}

		public void actionPerformed(ActionEvent arg0) {
			currentManifoldPath = this.path;			
			setInterfacesEnabled(false);
			// Next, signal the current simulation to terminate.
			sim.terminate();
		}
	}
	
	private class SurfaceLoaderDialogListener implements ActionListener {
		private Component parent;
		
		public SurfaceLoaderDialogListener(Component parent){
			this.parent = parent;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			JFileChooser jfc = new JFileChooser(AssetManager.getAssetPath("."));
			jfc.setFileFilter( new ManifoldFileFilter() );			
			int retVal = jfc.showOpenDialog(parent);
			
			if (retVal == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				currentManifoldPath = file.getAbsolutePath();
				setInterfacesEnabled(false);
				sim.terminate();
			}
		}
	}
	
	private class ManifoldFileFilter extends FileFilter {
		  		  
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
		    return extension.equals(".txt") || extension.equals(".off") || extension.equals(".xml");
		  }
		  public String getDescription() {
		    return "Text Files (.txt), OFF files, and XML files";
		  }
	}
}
