package frontend;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import de.jreality.jogl.JOGLViewer;

public class ViewFrame extends JFrame {
	private static final long serialVersionUID = 1L;	
	private JOGLViewer jv;

	public ViewFrame(JOGLViewer view) {		
		this.jv = view;
		
		super.setVisible(true);
		super.setResizable(true);

		Dimension size = new Dimension(400, 400);
		Container contentPane = super.getContentPane();		
		contentPane.add((Component) jv.getViewingComponent());
		contentPane.setMinimumSize(size);
		contentPane.setPreferredSize(size);
		contentPane.setMaximumSize(size);
		super.pack();
		super.validate();
		super.setVisible(true);
		super.addComponentListener( new ViewResizedListener() );
	}
	
	public void shutdown() {
		super.setVisible(false);
		super.dispose();
	}

	private class ViewResizedListener extends ComponentAdapter {
		public void componentResized(ComponentEvent arg0) {
			// We insist that if the component is resized, it should be square.
			Component c = arg0.getComponent();
			Rectangle r = c.getBounds();
			int m = Math.max(Math.max(r.height, r.width), 50);
			//c.setBounds(r.x, r.y, m, m);
			setSize(m, m);
			validate();
		}
	}
}
