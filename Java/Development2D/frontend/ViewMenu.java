package frontend;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import view.EmbeddedView;
import view.ExponentialView;
import view.FirstPersonView;

public class ViewMenu extends JMenu implements ViewCreator {
	private static final long serialVersionUID = 1L;

	private SimulationManager sim;

	private List<ViewController> expConts;
	private List<ViewController> embConts;
	private List<ViewController> fpConts;

	private ExponentialView exp;
	private ViewFrame expFrame;

	private EmbeddedView emb;
	private ViewFrame embFrame;

	private FirstPersonView fp;
	private ViewFrame fpFrame;

	private JMenuItem embViewLauncher;
	
	public ViewMenu(SimulationManager sim) {
		this.sim = sim;
		super.setText("Views");
		
		expConts = new LinkedList<ViewController>();
		embConts = new LinkedList<ViewController>();
		fpConts = new LinkedList<ViewController>();

		JMenuItem jmi = new JMenuItem();
		jmi.setText("Launch Exponential Map View");
		super.add(jmi);
		jmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createExponentialView();
			}
		});

		jmi = new JMenuItem();
		jmi.setText("Launch First Person View");
		super.add(jmi);
		jmi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createFirstPersonView();
			}
		});

		embViewLauncher = new JMenuItem();
		embViewLauncher.setText("Launch Embedded View");
		super.add(embViewLauncher);
		embViewLauncher.setEnabled(sim.isCurrentManifoldEmbedded());
		embViewLauncher.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createEmbeddedView();
			}
		});
	}

	public ExponentialView createExponentialView() {
		if (exp != null)
			return exp;
				
		exp = new ExponentialView(sim.getDevelopment(), sim.getMarkerHandler());
		exp.initializeNewManifold();
		exp.update(null, null);
		
		expFrame = new ViewFrame(exp.getViewer());
		expFrame.setTitle("Exponential (Map) View");
		expFrame.addWindowListener( new ViewClosingListener() );
		
		sim.addObserver(exp);
		
		for( ViewController vc : expConts ){
			vc.addView( exp );
		}
		
		return exp;
	}

	public EmbeddedView createEmbeddedView() {
		if (emb != null)
			return emb;
				
		emb = new EmbeddedView(sim.getDevelopment(), sim.getMarkerHandler());
		emb.initializeNewManifold();
		emb.update(null, null);
		
		embFrame = new ViewFrame(emb.getViewer());
		embFrame.setTitle("Embedded View");
		embFrame.addWindowListener( new ViewClosingListener() );
		
		sim.addObserver(emb);
		
		for( ViewController vc : embConts ){
			vc.addView( emb );
		}
		
		return emb;
	}

	public FirstPersonView createFirstPersonView() {
		if (fp != null)
			return fp;
				
		fp = new FirstPersonView(sim.getDevelopment(), sim.getMarkerHandler());
		fp.initializeNewManifold();
		fp.update(null, null);
		
		fpFrame = new ViewFrame(fp.getViewer());
		fpFrame.setTitle("First Person View");
		fpFrame.addWindowListener( new ViewClosingListener() );
		
		sim.addObserver(fp);
		
		for( ViewController vc : fpConts ){
			vc.addView( fp );
		}
		
		return fp;
	}

	@Override
	public void addAllViewsController(ViewController vc) {
		addExponentialViewController(vc);
		addEmbeddedViewController(vc);
		addFirstPersonViewController(vc);
	}

	@Override
	public void addExponentialViewController(ViewController vc) {
		expConts.add(vc);
		if (exp != null)
			vc.addView(exp);
	}

	@Override
	public void addEmbeddedViewController(ViewController vc) {
		embConts.add(vc);
		if (emb != null)
			vc.addView(emb);
	}

	@Override
	public void addFirstPersonViewController(ViewController vc) {
		fpConts.add(vc);
		if (fp != null)
			vc.addView(fp);
	}
	
	private class ViewClosingListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			Object o = e.getSource();
			if (o == expFrame) {
				sim.deleteObserver(exp);
				for (ViewController vc : expConts)
					vc.removeView(exp);
				expFrame.shutdown();
				exp = null;
			} else if (o == embFrame) {
				sim.deleteObserver(emb);
				for (ViewController vc : embConts)
					vc.removeView(emb);
				embFrame.shutdown();
				emb = null;
			} else {
				sim.deleteObserver(fp);
				for (ViewController vc : fpConts)
					vc.removeView(fp);
				fpFrame.shutdown();
				fp = null;
			}
		}
	}

	public void setSimulationManager(SimulationManager other) {
		this.sim = other;
		closeViews();
		embViewLauncher.setEnabled(sim.isCurrentManifoldEmbedded());
	}

	public void closeViews() {
		if( expFrame != null ){
			expFrame.shutdown();
		}
		
		if( embFrame != null ){
			embFrame.shutdown();
		}
		
		if( fpFrame != null ){
			fpFrame.shutdown();
		}
		
		exp = null;
		emb = null;
		fp = null;		
	}
}
