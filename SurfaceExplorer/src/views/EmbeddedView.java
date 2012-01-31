package views;

import java.awt.Color;
import java.awt.peer.SystemTrayPeer;
import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;


import model.Coordinates;
import model.Marker;
import model.Marker.MarkerType;
import model.Surface;
import model.Vector;
import de.jreality.geometry.ParametricSurfaceFactory;
import de.jreality.geometry.TubeFactory;

import de.jreality.math.MatrixBuilder;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentTools;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Light;
import de.jreality.scene.PointLight;
import de.jreality.scene.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;

import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.RenderingHintsShader;
import de.jreality.shader.ShaderUtility;

import de.jreality.util.SceneGraphUtility;

public class EmbeddedView extends JRViewer {
	public Viewer temp_v;
	
	private Surface surface;
	private Marker player;
	private AbstractList<Marker> markers;

	private SceneGraphComponent world;
	private SceneGraphComponent surfaceSGC;
	private AbstractList<SceneGraphComponent> markerSGCs;		
	
	private enum View{ Global, Attached, Closeup };
	private View viewSelection = View.Attached;
	
	private SceneGraphComponent freeCameraSGC;
	private SceneGraphPath freeCamera;

	private SceneGraphComponent attachedCameraSGC;
	private SceneGraphPath attachedCamera;	
	
	private Light attachedLight;
	private SceneGraphComponent attachedLightSGC;
	
	private Light freeLight;
	private SceneGraphComponent freeLightSGC;
	
	private double surfaceNormScalar = 10.0; 
	
	public EmbeddedView(Surface s, Marker player, AbstractList<Marker> markers){
		this.surface = s;
		this.player = player;
		this.markers = markers;
		
		initViewerProperties(); // Initialize the window and toolbars around the scene.		
		initSceneGraph(); 		// Construct the root of the scene graph, and global display preferences.
		initSurfaceSGC(); 		// Initialize the 2D surface, add it to the scene graph.
		initMarkerSGCs(); 		// Initialize the markers scene graph components, add them to the scene graph.
		updateScene(); 			// Position markers, camera, and light		
	}
	
	public void initViewerProperties(){
		addBasicUI();
        //addVRSupport();
        addContentSupport(ContentType.TerrainAligned);
        registerPlugin(new ContentAppearance());
        registerPlugin(new ContentTools());        
	}
	
	public void initSceneGraph(){
		world = SceneGraphUtility.createFullSceneGraphComponent("world");
		//setContent( world );		
		
		Viewer vv = display( world );		
		freeCamera = vv.getCameraPath();
		
		temp_v = vv;
		
		Camera cam = new Camera();
		cam.setNear(0.015);
		cam.setFar(100.0);
		cam.setFieldOfView(90);		
		freeCameraSGC = new SceneGraphComponent("attached_camera");
		freeCameraSGC.setCamera(cam);
		world.addChild(freeCameraSGC);
		attachedCamera = SceneGraphUtility.getPathsBetween(vv.getSceneRoot(), freeCameraSGC).get(0);
		attachedCamera.push(freeCameraSGC.getCamera());		
				
		freeLightSGC = new SceneGraphComponent("light_psn");
		freeCameraSGC.addChild(freeLightSGC); //For now, light goes where camera goes.
		//freeLightSGC.setVisible(true);				
		freeLight = new DirectionalLight();
		freeLightSGC.setLight(freeLight);	
		
		Appearance ap = world.getAppearance();
		DefaultGeometryShader dgs;
		DefaultPolygonShader dfs;
		RenderingHintsShader rhs;		
		
		dgs = ShaderUtility.createDefaultGeometryShader(ap, true);
		dfs = (DefaultPolygonShader) dgs.createPolygonShader("default");
		rhs = ShaderUtility.createDefaultRenderingHintsShader(ap, true);
				
		dfs.setDiffuseColor(new Color(0, 0, 200));
		dfs.setSpecularColor(Color.white);
		
		dgs.setShowLines(false);
		dgs.setShowPoints(false);
		
		dfs.setSmoothShading(false);
		rhs.setTransparencyEnabled(false);
		
		dfs.setAmbientCoefficient(0.0); // Default: 0.0
		dfs.setDiffuseCoefficient(1.0); // Default: 1.0
		dfs.setSpecularCoefficient(0.7); // Default: 0.7
		dfs.setSpecularExponent(120.0); // Default: 60 
	    
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
        
        world.addChild(surfaceSGC);
	}

	private SceneGraphComponent makeMarkerSGC( MarkerType mt ){
		File ff;
		if( mt == MarkerType.Rocket ){
			ff = new File("/home/jthomas/eclipse/SpaceGame/Data/rocket.3ds");
		} else {
			ff = new File("/home/jthomas/eclipse/SpaceGame/Data/sattelite.3ds");
		}
		SceneGraphComponent sgc = null;
		try{
			sgc = Readers.read( ff );
		} catch (Exception ee){
			ee.printStackTrace();
			System.exit(1);
		}
		return sgc;
	}
			
	private void initMarkerSGCs() {
		markerSGCs = new ArrayList<SceneGraphComponent>();
		for( Marker m : markers ){
			SceneGraphComponent sgc = makeMarkerSGC( m.getMarkerType() );
			world.addChild(sgc);
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
						.times(mat).assignTo(freeCameraSGC);
	}

	public void toggleView() {		
		switch( viewSelection ){
		case Global:
			viewSelection = View.Attached;
			Scene.executeWriter( temp_v.getSceneRoot(), new Runnable(){
				
				public void run() {
					freeLightSGC.setVisible( false );
					temp_v.setCameraPath(freeCamera);
					System.out.println("Global View Assigned!");
				}
				
			});			
			break;
			
		case Attached:
			viewSelection = View.Closeup;
			Scene.executeWriter( temp_v.getSceneRoot(), new Runnable(){
				
				public void run() {
					freeLightSGC.setVisible( true );
					temp_v.setCameraPath(attachedCamera);
					System.out.println("Attached View Assigned!");
				}
				
			});
			break;
			
		case Closeup:
			viewSelection = View.Global;
			
			Scene.executeWriter( temp_v.getSceneRoot(), new Runnable(){
				
				public void run() {
					freeLightSGC.setVisible( true );
					temp_v.setCameraPath(attachedCamera);
					System.out.println("Closeup View Assigned!");
				}
				
			});			
			break;
		}
		
	}

	public void moveCameraZ(double d) {
		this.surfaceNormScalar += d;
	} 	
}
