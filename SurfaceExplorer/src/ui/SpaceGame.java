package ui;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.AbstractList;
import java.util.ArrayList;

import javax.swing.JFrame;


import de.jreality.util.RenderTrigger;

import views.EmbeddedView;
import model.CurvedTorus;
import model.Marker;
import model.Marker.MarkerType;
import model.OpenSquare;
import model.Saddle;
import model.Surface;
import model.Vector;

public class SpaceGame {
	
	public static Surface S;
	
	public static Marker player;
	public static AbstractList<Marker> markers;	
	public static EmbeddedView view;
	
	public static void main(String[] args) {
		initSurface();
		initMarkers();		
		initView();
		initControls();
	}	
	
	public static void initSurface(){
		S = new CurvedTorus();	
		//S = new OpenSquare();
		//S = new Saddle();
	}
	
	public static void initMarkers(){
		markers = new ArrayList<Marker>();
		player = new Marker(MarkerType.Rocket, S.makeCoordinates(10.0,10.0), new Vector(1.0,0.0));
		Marker sattelite1 = new Marker(MarkerType.Sattelite, S.makeCoordinates(2,3), new Vector(0.0,1.0));		
		Marker sattelite2 = new Marker(MarkerType.Sattelite, S.makeCoordinates(4,3), new Vector(0.0,1.0));		
		markers.add( player );
		markers.add( sattelite1 );
		markers.add( sattelite2 );
	}
	
	public static void initView(){
		view = new EmbeddedView( S, player, markers );
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setSize(750,750);
		frame.getContentPane().add( (Component) view.viewer.getViewingComponent() );
		frame.validate();		
		RenderTrigger rt = new RenderTrigger();
		rt.addSceneGraphComponent( view.viewer.getSceneRoot() );
		rt.addViewer( view.viewer );

		frame.addWindowListener(new WindowAdapter() {
		      public void windowClosing(WindowEvent arg0) {
		        System.exit(0);
		      }
			});		    
	}
	
	public static void initControls(){
		Component comp = (Component) view.viewer.getViewingComponent();
		
		comp.addKeyListener( new KeyAdapter() {
			public void keyPressed(KeyEvent e){								
				switch( e.getKeyCode() ){
				
				case KeyEvent.VK_RIGHT:
					player.getFacing().rotate( -0.3 );
					break;
					
				case KeyEvent.VK_LEFT:
					player.getFacing().rotate( 0.3 );
					break;
					
				case KeyEvent.VK_UP:
					player.move( 0.1 );
					break;
					
				case KeyEvent.VK_DOWN:
					player.move( -0.1 );
					break;				
				
				case KeyEvent.VK_R:
					view.toggleRadar();
					break;
					
				case KeyEvent.VK_V:
					view.toggleView();
					break;
					
				case KeyEvent.VK_EQUALS:
					view.moveCameraZ(10);
					break;
				
				case KeyEvent.VK_MINUS:
					view.moveCameraZ(-10);
					break;
				}
				view.updateScene();
			}
		});		
	}	
}