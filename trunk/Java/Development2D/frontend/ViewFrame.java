package frontend;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import sun.awt.WindowClosingListener;
import view.View;

public class ViewFrame extends JFrame 
						 implements WindowClosingListener,
						 			 ComponentListener {
	private SimulationManager simulation;
	private View view;

	public ViewFrame(SimulationManager dui, View view) {
		simulation = dui;
		this.view = view;
	}

	@Override
	public RuntimeException windowClosingDelivered(WindowEvent arg0) {
		simulation.removeView(this.view);
		return null;
	}

	@Override
	public RuntimeException windowClosingNotify(WindowEvent arg0) {
		return null;
	}

	public void componentShown(ComponentEvent arg0) {
	}

	public void componentHidden(ComponentEvent arg0) {
	}

	public void componentMoved(ComponentEvent arg0) {
	}

	public void componentResized(ComponentEvent arg0) {
		Component c = arg0.getComponent();
		Rectangle r = c.getBounds();
		int m = Math.max(Math.max(r.height, r.width), 50);
		c.setBounds(r.x, r.y, m, m);
	}
}
