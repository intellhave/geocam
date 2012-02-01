package views;

import java.awt.Color;

import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import model.Coordinates;
import model.Marker;
import model.Marker.MarkerType;
import model.Surface;
import model.Vector;
import de.jreality.geometry.ParametricSurfaceFactory;
import de.jreality.geometry.Primitives;
import de.jreality.geometry.TubeFactory;


import de.jreality.math.MatrixBuilder;
import de.jreality.math.P3;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentTools;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Light;
import de.jreality.scene.PointLight;
import de.jreality.scene.SpotLight;

import de.jreality.scene.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.jogl.Viewer;

import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.util.SceneGraphUtility;

public class EmbeddedView {
	public Viewer viewer;
	
	/* Model-Related Variables */
	private Surface surface;
	private Marker player;
	private AbstractList<Marker> markers;
	private SceneGraphComponent surfaceSGC;
	private AbstractList<SceneGraphComponent> markerSGCs;		
	
	/* Display-Related Variables */
	private enum ViewType{ Global, Attached };
	private ViewType viewSelection = ViewType.Global;
	
	private SceneGraphComponent root;
	
	/* "Free" indicates a camera/light distant from the scene, 
	 * "attached" denotes a camera/light that follows the player's 
	 * marker. */
	private SceneGraphComponent freeCameraSGC;
	private SceneGraphPath freeCamera;
	private SceneGraphComponent attachedCameraSGC;
	private SceneGraphPath attachedCamera;	
		
	private double surfaceNormScalar = 10.0; 
	
	public EmbeddedView(Surface s, Marker player, AbstractList<Marker> markers){
		this.surface = s;
		this.player = player;
		this.markers = markers;
		
		initSceneGraph(); 		// Construct the root of the scene graph, and global display preferences.
		initSurfaceSGC(); 		// Initialize the 2D surface, add it to the scene graph.
		initMarkerSGCs(); 		// Initialize the markers scene graph components, add them to the scene graph.
		updateScene(); 			// Position markers, camera, and light		
	}
		
	public void initSceneGraph(){
		root = new SceneGraphComponent("root");
				
		SceneGraphComponent origin = TubeFactory.getXYZAxes();
		root.addChild(origin);
		MatrixBuilder.euclidean().scale(10).assignTo(origin);		
		
		freeCameraSGC = new SceneGraphComponent("free_camera");
		root.addChild(freeCameraSGC);		
		Camera freecam = new Camera();
		freecam.setFar(200.0);
		freecam.setNear(0.015);
		freecam.setFieldOfView(90);
		freeCameraSGC.setCamera(freecam);
		freeCamera = new SceneGraphPath(root,freeCameraSGC);
		freeCamera.push(freecam);
		MatrixBuilder.euclidean().translate(0,0,75).assignTo(freeCameraSGC);
		
		attachedCameraSGC = new SceneGraphComponent("attached_camera");				
		root.addChild(attachedCameraSGC);		
		Camera attachedcam = new Camera();
		attachedcam.setFar(200.0);
		attachedcam.setNear(0.015);
		attachedcam.setFieldOfView(90);		
		attachedCameraSGC.setCamera(attachedcam);
		attachedCamera = new SceneGraphPath(root,attachedCameraSGC);
		attachedCamera.push(attachedcam);
		
		viewer = new Viewer();
		viewer.setSceneRoot(root);
		viewer.setCameraPath(freeCamera);
				
		Appearance ap = new Appearance();
		ap.setAttribute(CommonAttributes.BACKGROUND_COLOR, new Color(0f,0.1f,0.1f));				
		root.setAppearance( ap );
		
		SceneGraphUtility.removeLights(viewer);
		initLights();
	}
	
	public void initLights(){
		double R = 150;
		
		Light l = new DirectionalLight();
		SceneGraphComponent sgc = new SceneGraphComponent();
		sgc.setLight( l );
		MatrixBuilder.euclidean()
					 .translate( R, 0, 0 )
					 .rotateY(-Math.PI/2)					 
					 .assignTo(sgc);
		root.addChild(sgc);
	
		l = new DirectionalLight();
		l.setIntensity(1);
		sgc = new SceneGraphComponent();
		sgc.setLight( l );
		MatrixBuilder.euclidean()
					 .translate( -R, 0, 0 )
					 .rotateY(Math.PI/2)					 
					 .assignTo(sgc);
		root.addChild(sgc);	
	}
	
	private void initSurfaceSGC() {
		ParametricSurfaceFactory psf = new ParametricSurfaceFactory(this.surface);
        psf.setUMin( surface.getUMin() );
        psf.setUMax( surface.getUMax() );
        psf.setVMin( surface.getVMin() );
        psf.setVMax( surface.getVMax() );
        psf.setULineCount(40);
        psf.setVLineCount(40);
        psf.setGenerateEdgesFromFaces(true);
        psf.update();
        
        surfaceSGC = new SceneGraphComponent("surface");
        surfaceSGC.setGeometry(psf.getIndexedFaceSet());
        
        Appearance ap = new Appearance();        
        ap.setAttribute(CommonAttributes.DIFFUSE_COLOR, new Color(90,0,0));
        ap.setAttribute(CommonAttributes.AMBIENT_COLOR, new Color(50,0,0));		
		ap.setAttribute(CommonAttributes.AMBIENT_COEFFICIENT, 0.5);
		
		surfaceSGC.setAppearance(ap);        
        root.addChild(surfaceSGC);
	}

	private SceneGraphComponent makeMarkerSGC( MarkerType mt ){
		File ff;
		if( mt == MarkerType.Rocket ){
			ff = new File("/home/jthomas/eclipse/SurfaceExplorer/Data/rocket.3ds");
		} else {
			ff = new File("/home/jthomas/eclipse/SurfaceExplorer/Data/sattelite.3ds");
		}
		SceneGraphComponent sgc = null;
		try{
			sgc = Readers.read( ff );
		} catch (Exception ee){
			ee.printStackTrace();
			System.exit(1);
		}
		Appearance ap = new Appearance();
		ap.setAttribute(CommonAttributes.VERTEX_DRAW, false);
		ap.setAttribute(CommonAttributes.EDGE_DRAW, false);
		sgc.setAppearance(ap);
		return sgc;
	}
			
	private void initMarkerSGCs() {
		markerSGCs = new ArrayList<SceneGraphComponent>();
		for( Marker m : markers ){
			SceneGraphComponent sgc = makeMarkerSGC( m.getMarkerType() );
			root.addChild(sgc);
			markerSGCs.add(sgc);			
		}		
	}
	
	// TODO: Clean up this method to avoid duplicate code!
	public void updateScene() {		
		double[] R3Point = new double[3];		
		double[][] vec = new double[3][3];
		
		Vector du = new Vector(1,0);
		Vector dv = new Vector(0,1);
		
		double[] x_axis = {1.0, 0.0, 0.0};
		double[] vec_other = new double[3];
	
		double[] mat = new double[16];		
		
		for( int ii = 0; ii < markers.size(); ii++ ){	
			Marker m = markers.get(ii);
			SceneGraphComponent sgc = markerSGCs.get(ii);
			
			Coordinates c = m.getPosition();			
			surface.immersePoint(c, R3Point);
			
			Vector f0 = surface.immerseVector(c, du);
			Vector f1 = surface.immerseVector(c, dv);
			f0.normalize();
			f1.normalize();
			Vector f2 = f0.crossProduct(f1);
			
			vec[0] = f0.components;
			vec[1] = f1.components;
			vec[2] = f2.components;
			
			mat[4*0+0] = vec[0][0]; mat[4*0+1] = vec[1][0]; mat[4*0+2] = vec[2][0]; mat[4*0+3] = 0.0; 
			mat[4*1+0] = vec[0][1]; mat[4*1+1] = vec[1][1]; mat[4*1+2] = vec[2][1]; mat[4*1+3] = 0.0;
			mat[4*2+0] = vec[0][2]; mat[4*2+1] = vec[1][2]; mat[4*2+2] = vec[2][2]; mat[4*2+3] = 0.0;				
			mat[4*3+0] = 0.0; 		mat[4*3+1] = 0.0; 		mat[4*3+2] = 0.0; 		mat[4*3+3] = 1.0; 
			
			//double phi = m.getFacing().angle( du );
			
			double[] facing = m.getFacing().components;
			vec_other[0] = facing[0];
			vec_other[1] = facing[1];
			vec_other[2] = 0.0;
			MatrixBuilder.euclidean()
						.translate(R3Point)
						.times(mat)
						.rotateFromTo(x_axis,vec_other).assignTo(sgc);			
		
		}

		Coordinates c = player.getPosition();
		surface.immersePoint(c, R3Point);
		Vector norm = surface.getSurfaceNormal(c);
		norm.scale(surfaceNormScalar);
		
		Vector f0 = surface.immerseVector(c, du);
		Vector f1 = surface.immerseVector(c, dv);
		f0.normalize();
		f1.normalize();
		Vector f2 = f0.crossProduct(f1);
		
		vec[0] = f0.components;
		vec[1] = f1.components;
		vec[2] = f2.components;
		
		mat[4*0+0] = vec[0][0]; mat[4*0+1] = vec[1][0]; mat[4*0+2] = vec[2][0]; mat[4*0+3] = 0.0; 
		mat[4*1+0] = vec[0][1]; mat[4*1+1] = vec[1][1]; mat[4*1+2] = vec[2][1]; mat[4*1+3] = 0.0;
		mat[4*2+0] = vec[0][2]; mat[4*2+1] = vec[1][2]; mat[4*2+2] = vec[2][2]; mat[4*2+3] = 0.0;				
		mat[4*3+0] = 0.0; 		mat[4*3+1] = 0.0; 		mat[4*3+2] = 0.0; 		mat[4*3+3] = 1.0; 
		
		MatrixBuilder.euclidean()
						.translate(norm.getComponents())
						.translate(R3Point)
						.times(mat).assignTo(attachedCameraSGC);
		viewer.render();
	}

	public void toggleView() {		
		switch( viewSelection ){
		case Global:
			viewSelection = ViewType.Attached;
			Scene.executeWriter( viewer.getSceneRoot(), new Runnable(){
				
				public void run() {
					//freeLightSGC.setVisible( false );
					viewer.setCameraPath(freeCamera);
					System.out.println("Global View Assigned!");
				}
				
			});			
			break;
			
		case Attached:
			viewSelection = ViewType.Global;
			Scene.executeWriter( viewer.getSceneRoot(), new Runnable(){
				
				public void run() {
					//freeLightSGC.setVisible( true );
					viewer.setCameraPath(attachedCamera);
					System.out.println("Attached View Assigned!");
				}
				
			});
			break;			
		}
		
	}

	public void moveCameraZ(double d) {
		this.surfaceNormScalar += d;
	} 	
}
