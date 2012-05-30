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
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;

import de.jreality.scene.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.jogl.Viewer;

import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ImageData;
import de.jreality.shader.ShaderUtility;
import de.jreality.shader.Texture2D;
import de.jreality.shader.TextureUtility;
import de.jreality.util.Input;
import de.jreality.util.SceneGraphUtility;

import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;

public class EmbeddedView {
	public Viewer viewer;

	/* Model-Related Variables */
	private Surface surface;
	private Marker player;
	private AbstractList<Marker> markers;
	private SceneGraphComponent surfaceSGC;
	private AbstractList<SceneGraphComponent> markerSGCs;

	private AbstractList<SceneGraphComponent> radarBall;

	/* Display-Related Variables */
	private enum ViewType {
		Global, Moving
	};

	private ViewType viewSelection = ViewType.Global;

	private SceneGraphComponent root;

	/*
	 * "Global" indicates a camera/light distant from the scene, "moving"
	 * denotes a camera/light that follows the player's marker.
	 */
	private SceneGraphComponent globalCameraSGC;
	private SceneGraphPath globalCamera;
	private SceneGraphComponent movingCameraSGC;
	private SceneGraphPath movingCamera;

	private double surfaceNormScalar = 10.0;

	public EmbeddedView(Surface s, Marker player, AbstractList<Marker> markers) {
		this.surface = s;
		this.player = player;
		this.markers = markers;

		// Construct the root of the scene graph, and global display preferences.
		initSceneGraph(); 
		// Initialize the 2D surface, add it to the scene graph.
		initSurfaceSGC();
		// Initialize the markers scene graph components, add them to the scene graph.
		initMarkerSGCs(); 
		// Position markers, camera, and light, then render.
		updateScene(); 	  
	}

	public void initSceneGraph() {
		root = new SceneGraphComponent("root");

		SceneGraphComponent origin = TubeFactory.getXYZAxes();
		root.addChild(origin);
		MatrixBuilder.euclidean().scale(10).assignTo(origin);

		globalCameraSGC = new SceneGraphComponent("free_camera");
		root.addChild(globalCameraSGC);
		Camera freecam = new Camera();
		freecam.setFar(200.0);
		freecam.setNear(0.015);
		freecam.setFieldOfView(90);
		globalCameraSGC.setCamera(freecam);
		globalCamera = new SceneGraphPath(root, globalCameraSGC);
		globalCamera.push(freecam);
		MatrixBuilder.euclidean().translate(0, 0, 75).assignTo(globalCameraSGC);

		movingCameraSGC = new SceneGraphComponent("attached_camera");
		root.addChild(movingCameraSGC);
		Camera attachedcam = new Camera();
		attachedcam.setFar(200.0);
		attachedcam.setNear(0.015);
		attachedcam.setFieldOfView(90);
		movingCameraSGC.setCamera(attachedcam);
		movingCamera = new SceneGraphPath(root, movingCameraSGC);
		movingCamera.push(attachedcam);

		viewer = new Viewer();
		viewer.setSceneRoot(root);
		viewer.setCameraPath(globalCamera);

		Appearance ap = new Appearance();
		Color[] colors = { Color.yellow, Color.yellow, Color.blue, Color.blue };
		ap.setAttribute(CommonAttributes.BACKGROUND_COLORS, colors);
		root.setAppearance(ap);

		SceneGraphUtility.removeLights(viewer);

		SceneGraphComponent freeLight = new SceneGraphComponent();
		freeLight.setLight(new DirectionalLight());
		globalCameraSGC.addChild(freeLight);

		SceneGraphComponent attachedLight = new SceneGraphComponent();
		attachedLight.setLight(new DirectionalLight());
		movingCameraSGC.addChild(attachedLight);
	}

	private void initSurfaceSGC() {
		ParametricSurfaceFactory psf = new ParametricSurfaceFactory(
				this.surface);
		psf.setUMin(surface.getUMin());
		psf.setUMax(surface.getUMax());
		psf.setVMin(surface.getVMin());
		psf.setVMax(surface.getVMax());
		psf.setULineCount(40);
		psf.setVLineCount(40);
		psf.setGenerateEdgesFromFaces(true);
		psf.update();

		surfaceSGC = new SceneGraphComponent("surface");
		surfaceSGC.setGeometry(psf.getIndexedFaceSet());

		Appearance ap = new Appearance();
		surfaceSGC.setAppearance(ap);

		DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
				.createDefaultGeometryShader(ap, true);
		dgs.setShowLines(false);
		dgs.setShowPoints(false);
		DefaultPolygonShader dps = (DefaultPolygonShader) dgs
				.createPolygonShader("default");

		dps.setAmbientColor(Color.white);
		dps.setDiffuseColor(Color.white);
		dps.setAmbientCoefficient(0.3); // These coefficients seem to help the
										// texture look "bright"
		dps.setDiffuseCoefficient(0.8); // when it gets mapped to the surface.

		ImageData id = null;
		try {
			File ff = new File(
					"/home/jthomas/eclipse/SurfaceExplorer/Data/checker.gif");
			id = ImageData.load(Input.getInput(ff));
		} catch (Exception ee) {
			ee.printStackTrace();
			System.exit(1);
		}

		Texture2D tex = TextureUtility.createTexture(ap, POLYGON_SHADER, id);
		tex.setTextureMatrix(MatrixBuilder.euclidean().scale(3.0).getMatrix());

		root.addChild(surfaceSGC);
	}

	private SceneGraphComponent makeMarkerSGC(MarkerType mt) {
		File ff;
		if (mt == MarkerType.Rocket) {
			ff = new File(
					"/home/jthomas/Eclipse/SurfaceExplorer/Data/rocket.3ds");
		} else {
			ff = new File(
					"/home/jthomas/Eclipse/SurfaceExplorer/Data/sattelite.3ds");
		}
		SceneGraphComponent sgc = null;
		try {
			sgc = Readers.read(ff);
		} catch (Exception ee) {
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
		for (Marker m : markers) {
			SceneGraphComponent sgc = makeMarkerSGC(m.getMarkerType());
			// Appearance ap = new Appearance();
			// sgc.setAppearance(ap);

			root.addChild(sgc);
			markerSGCs.add(sgc);
		}
		initRadarBall();
	}

	final static int segments = 20;
	final static double spacing = 0.2;

	private void initRadarBall() {
		radarBall = new ArrayList<SceneGraphComponent>();

		for (int ii = 0; ii < segments; ii++) {
			SceneGraphComponent sgc = new SceneGraphComponent();
			sgc.setGeometry(Primitives.sphere(10));
			Appearance ap = new Appearance();
			sgc.setAppearance(ap);
			ap.setAttribute(CommonAttributes.DIFFUSE_COLOR, Color.red);
			radarBall.add(sgc);
			root.addChild(sgc);
		}
	}

	public void updateMarker(Marker m, SceneGraphComponent sgc) {
		double[] x_axis = { 1.0, 0.0, 0.0 };
		double[] R3Point = new double[3];
		double[] mat = new double[16];
		double[][] vec = new double[3][3];
		double[] vec_other = new double[3];
		Vector du = new Vector(1, 0);
		Vector dv = new Vector(0, 1);

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

		mat[4 * 0 + 0] = vec[0][0];
		mat[4 * 0 + 1] = vec[1][0];
		mat[4 * 0 + 2] = vec[2][0];
		mat[4 * 0 + 3] = 0.0;
		mat[4 * 1 + 0] = vec[0][1];
		mat[4 * 1 + 1] = vec[1][1];
		mat[4 * 1 + 2] = vec[2][1];
		mat[4 * 1 + 3] = 0.0;
		mat[4 * 2 + 0] = vec[0][2];
		mat[4 * 2 + 1] = vec[1][2];
		mat[4 * 2 + 2] = vec[2][2];
		mat[4 * 2 + 3] = 0.0;
		mat[4 * 3 + 0] = 0.0;
		mat[4 * 3 + 1] = 0.0;
		mat[4 * 3 + 2] = 0.0;
		mat[4 * 3 + 3] = 1.0;

		double[] facing = m.getFacing().components;
		vec_other[0] = facing[0];
		vec_other[1] = facing[1];
		vec_other[2] = 0.0;
		MatrixBuilder.euclidean().translate(R3Point).times(mat).rotateFromTo(
				x_axis, vec_other).assignTo(sgc);

	}

	public void updateCamera(SceneGraphComponent sgc) {
		double[] R3Point = new double[3];
		double[][] vec = new double[3][3];
		double[] mat = new double[16];

		Vector du = new Vector(1, 0);
		Vector dv = new Vector(0, 1);

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

		mat[4 * 0 + 0] = vec[0][0];
		mat[4 * 0 + 1] = vec[1][0];
		mat[4 * 0 + 2] = vec[2][0];
		mat[4 * 0 + 3] = 0.0;
		mat[4 * 1 + 0] = vec[0][1];
		mat[4 * 1 + 1] = vec[1][1];
		mat[4 * 1 + 2] = vec[2][1];
		mat[4 * 1 + 3] = 0.0;
		mat[4 * 2 + 0] = vec[0][2];
		mat[4 * 2 + 1] = vec[1][2];
		mat[4 * 2 + 2] = vec[2][2];
		mat[4 * 2 + 3] = 0.0;
		mat[4 * 3 + 0] = 0.0;
		mat[4 * 3 + 1] = 0.0;
		mat[4 * 3 + 2] = 0.0;
		mat[4 * 3 + 3] = 1.0;

		MatrixBuilder.euclidean().translate(norm.getComponents()).translate(
				R3Point).times(mat).assignTo(sgc);
	}

	public void updateRadar() {
		Coordinates c = player.getPosition();
		Vector v = player.getFacing();

		double[] R3Point = new double[3];
		surface.immersePoint(c, R3Point);

		for (int ii = 0; ii < segments; ii++) {
			Coordinates p = surface.move(c, v, ii * spacing);
			surface.immersePoint(p, R3Point);
			MatrixBuilder.euclidean().translate(R3Point).scale(0.5).assignTo(
					radarBall.get(ii));
		}
	}

	public void updateScene() {

		for (int ii = 0; ii < markers.size(); ii++) {
			Marker m = markers.get(ii);
			SceneGraphComponent sgc = markerSGCs.get(ii);
			updateMarker(m, sgc);
		}
		updateRadar();
		updateCamera(movingCameraSGC);
		viewer.render();
	}

	public void toggleView() {
		switch (viewSelection) {
		case Global:
			viewSelection = ViewType.Moving;
			Scene.executeWriter(viewer.getSceneRoot(), new Runnable() {

				public void run() {
					// freeLightSGC.setVisible( false );
					viewer.setCameraPath(movingCamera);
				}

			});
			break;

		case Moving:
			viewSelection = ViewType.Global;
			Scene.executeWriter(viewer.getSceneRoot(), new Runnable() {

				public void run() {
					// freeLightSGC.setVisible( true );
					viewer.setCameraPath(globalCamera);
				}

			});
			break;
		}

	}

	public void moveCameraZ(double d) {
		this.surfaceNormScalar += d;
	}

	public void toggleRadar() {
		for (SceneGraphComponent sgc : radarBall)
			sgc.setVisible(!sgc.isVisible());
	}
}
