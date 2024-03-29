package view;

import static de.jreality.shader.CommonAttributes.BACKGROUND_COLOR;
import static de.jreality.shader.CommonAttributes.DIFFUSE_COLOR;
import static de.jreality.shader.CommonAttributes.EDGE_DRAW;
import static de.jreality.shader.CommonAttributes.FACE_DRAW;
import static de.jreality.shader.CommonAttributes.LIGHTING_ENABLED;
import static de.jreality.shader.CommonAttributes.LINE_SHADER;
import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;
import static de.jreality.shader.CommonAttributes.SMOOTH_SHADING;
import static de.jreality.shader.CommonAttributes.TRANSPARENCY_ENABLED;
import static de.jreality.shader.CommonAttributes.VERTEX_DRAW;

import java.awt.Color;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import marker.Marker;
import marker.MarkerHandler;
import triangulation.Face;
import util.Vector;

import com.jogamp.opengl.swt.GLCanvas;

import de.jreality.jogl.JOGLRenderer;
import de.jreality.jogl.JOGLViewer;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.proxy.scene.Camera;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import development.Development;

/*********************************************************************************
 * View
 * 
 * This class encapsulates the data and methods needed to set up a basic
 * visualization of a triangulated surface with markers. This entails setting up
 * a scene graph, adding a camera to the graph, and giving the camera a position
 * in space. Classes that extend View need to specify methods that define how
 * the geometry of the surface and markers should be presented in that
 * particular visualization.
 *********************************************************************************/
public abstract class View implements Observer {

	/*********************************************************************************
	 * Model Data and Viewing Parameters
	 * 
	 * These protected variables hold references to the parts of the model views
	 * must inspect in order to depict the model and variables which specify how
	 * to render the model (for example, how faces should be colored).
	 *********************************************************************************/
	protected Development development;
	protected MarkerHandler markers;
	protected FaceAppearanceScheme faceAppearanceScheme;
	protected boolean showTexture;
	protected boolean showFaceLabels;
	protected boolean animated = false;
	protected double zoom;

	/*********************************************************************************
	 * Scene Graph Data
	 * 
	 * These protected variables hold the important components of the scene
	 * graph that makes up the view. Namely, these parts are the root of the
	 * scene graph, the subgraph consisting of the polygons that will represent
	 * the development at a given point, and the subgraph consisting of the
	 * "objects" or "markers" that will move around in the scene.
	 *********************************************************************************/
	public SceneGraphComponent sgcRoot;
	protected SceneGraphComponent sgcDevelopment;
	protected SceneGraphComponent sgcMarkers;
	protected SceneGraphComponent sgcCamera;
	protected SceneGraphPath camPath;
	protected HashMap<Face, SceneGraphComponent> faceSGCs;

	/*********************************************************************************
	 * JOGL Data
	 * 
	 * These protected variables hold the objects that JOGL needs to display the
	 * scene. This include the "viewer," an object we use to transform the scene
	 * graph into an actual image.
	 *********************************************************************************/
	protected JOGLRenderer renderer;
	protected GLCanvas canvas;
	protected JOGLViewer viewer;
	protected Appearance defaultAppearance;

	/*********************************************************************************
	 * View
	 * 
	 * This constructor is responsible for setting up the scene graph and
	 * appearance settings that will be used by any instance of this class.
	 *********************************************************************************/
	public View(Development development, MarkerHandler markers) {
		this.development = development;
		this.markers = markers;
		
		showTexture = true;
		zoom = 3.0;

		initSceneGraph();
		initAppearances();
		initViewer();
	}

	/*********************************************************************************
	 * initSceneGraph
	 * 
	 * This method is responsible for initializing the scene graph components
	 * that will hold the basic parts of any scene, namely the surface, the
	 * markers, and the camera.
	 *********************************************************************************/
	public void initSceneGraph() {
		sgcRoot = new SceneGraphComponent("Root");
		sgcDevelopment = new SceneGraphComponent("Development");
		sgcMarkers = new SceneGraphComponent("Objects");
		sgcRoot.addChild(sgcMarkers);
		sgcDevelopment.setAppearance(defaultAppearance);
		sgcRoot.addChild(sgcDevelopment);

		// Initialize the camera.
		Camera cam = new Camera();
		cam.setFar(200.0);
		cam.setNear(0.015);
		cam.setFieldOfView(90);
		sgcCamera = new SceneGraphComponent("Camera");
		sgcRoot.addChild(sgcCamera);
		sgcCamera.setCamera(cam);
		camPath = new SceneGraphPath(sgcRoot, sgcCamera);
		camPath.push(cam);
		MatrixBuilder.euclidean().translate(0, 0, 5).assignTo(sgcCamera); // FIX
																			// THIS
	}

	/*********************************************************************************
	 * initViewers
	 * 
	 * TODO : Documentation
	 *********************************************************************************/
	public void initViewer() {
		viewer = new JOGLViewer();
		viewer.setCameraPath(camPath);
		viewer.setSceneRoot(sgcRoot);

		// ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
		// toolSystem.initializeSceneTools();
		// viewer.getViewingComponent();
	}

	/*********************************************************************************
	 * getViewer()
	 * 
	 * TODO : Documentation
	 *********************************************************************************/
	public JOGLViewer getViewer() {
		return viewer;
	}

	/*********************************************************************************
	 * setDrawEdges
	 * 
	 * This method uses the input boolean value to determine whether the edges
	 * in the development should be explicitly drawn.
	 *********************************************************************************/
	public void setDrawEdges(boolean value) {
		setDrawEdgesHelper(sgcDevelopment, value);
	}

	private void setDrawEdgesHelper(SceneGraphComponent sgc, boolean value) {
		Appearance app = sgc.getAppearance();
		if (app != null) {
			app.setAttribute(CommonAttributes.EDGE_DRAW, value);
		}
		for (SceneGraphComponent child : sgc.getChildComponents())
			setDrawEdgesHelper(child, value);
	}

	/*********************************************************************************
	 * setDrawFaces
	 * 
	 * This method uses the input boolean value to determine whether the faces
	 * in the development should be explicitly drawn.
	 *********************************************************************************/
	public void setDrawFaces(boolean value) {
		Appearance app = sgcDevelopment.getAppearance();
		app.setAttribute(CommonAttributes.FACE_DRAW, value);
	}

	/*********************************************************************************
	 * initAppearances
	 * 
	 * This method is responsible for initializing the default appearance
	 * settings for the viewer. The protected variable defaultAppearance stores
	 * instructions for JOGL that determine how vertices, edges, and faces
	 * should be depicted.
	 * 
	 * For example, if edges should be depicted by colored tubes of a certain
	 * diameter and vertices should be depicted with spheres of a certain
	 * diameter, that should be specified here.
	 *********************************************************************************/
	protected void initAppearances() {
		defaultAppearance = new Appearance();
		defaultAppearance.setAttribute(VERTEX_DRAW, false);
		defaultAppearance.setAttribute(EDGE_DRAW, false);
		defaultAppearance.setAttribute(FACE_DRAW, true);
		defaultAppearance.setAttribute(LIGHTING_ENABLED, true);
		defaultAppearance.setAttribute(TRANSPARENCY_ENABLED, false);
		defaultAppearance.setAttribute(BACKGROUND_COLOR, Color.gray);
		defaultAppearance.setAttribute(DIFFUSE_COLOR, Color.white);
		defaultAppearance.setAttribute(LINE_SHADER + "." + POLYGON_SHADER + "." + SMOOTH_SHADING,
				true);

		DefaultGeometryShader dgs;
		dgs = (DefaultGeometryShader) ShaderUtility.createDefaultGeometryShader(defaultAppearance,
				true);
		DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
		dls.setDiffuseColor(Color.black);

		DefaultPolygonShader dps;
		dps = (DefaultPolygonShader) dgs.createPolygonShader("default");
		dps.setDiffuseColor(Color.white);

		sgcDevelopment.setAppearance(defaultAppearance);
	}

	/*********************************************************************************
	 * updateGeometry
	 * 
	 * This method is responsible for updating the geometry in the view. When a
	 * player moves or re-orients his marker on the surface, it will usually be
	 * necessary to recompute the development and markers are visible to the
	 * player in the view.
	 * 
	 * The "dev" boolean indicates whether the geometry of the development needs
	 * to be recomputed. The "obj" boolean indicates whether the geometry of the
	 * markers needs to be recalculated. Recalculating the geometry of the
	 * development is more expensive than recalculating the placement of the
	 * markers, so these booleans need to be used judiciously.
	 *********************************************************************************/
	public void updateGeometry(boolean dev, boolean obj) {
		if (dev) {
			generateManifoldGeometry();
		}
		if (obj) {
			generateMarkerGeometry();
		}
	}

	public void updateGeometry() {
		updateGeometry(true, true);
	}

	/*********************************************************************************
	 * update
	 * 
	 * The arguments arg0 and arg1 are not important here, since we already know
	 * what we're observing. The Observable in question is typically going to be
	 * the object that manages the simulation, and fires its "notify Observers"
	 * method when it is time to render a new frame.
	 *********************************************************************************/
	public void update(Observable arg0, Object arg1) {
		for( Marker m : this.markers.getAllMarkers() )
			if( m.isRemoved() )
				removeMarker( m );

		updateGeometry();
		updateCamera();
 		viewer.render();
	}

	/*********************************************************************************
	 * generateManifoldGeometry
	 * 
	 * Classes which extend View will need to supply this method, which explains
	 * how to display the manifold in this particular viewpoint. When this
	 * method is called, it should update the "sgcDevelopment" part of the scene
	 * graph to reflect the part of the manifold that is being viewed.
	 *********************************************************************************/
	protected abstract void generateManifoldGeometry();

	/*********************************************************************************
	 * generateObjectGeometry
	 * 
	 * Classes which extend View will need to supply this method, which explains
	 * how to display the markers on the manifold in this particular viewpoint.
	 * When this method is called, it should update the "sgcObjects" part of the
	 * scene graph, to reflect how the markers appear in this particular view of
	 * the manifold.
	 *********************************************************************************/
	protected abstract void generateMarkerGeometry();

	/*********************************************************************************
	 * initializeNewManifold
	 * 
	 * Classes which extend View will need to supply this method, which explains
	 * how to reinitialize the view when the old manifold is replaced with a new
	 * one.
	 *********************************************************************************/
	public abstract void initializeNewManifold();

	protected abstract void updateCamera();

	/*********************************************************************************
	 * removeMarker
	 * 
	 * This method is responsible for removing the input marker from the view.
	 * Since a marker may have many scene graph components attached to it, this
	 * often requires a nontrivial modification of the view's data structures
	 * that is specific to the view in question.
	 *********************************************************************************/
	public abstract void removeMarker(Marker m);

	public boolean isTextured() {
		return showTexture;
	}

	public void setDrawTextures(boolean texture) {
		showTexture = texture;
	}

	public void setAnimated(boolean animated) {
		this.animated = animated;
	}
	
	public void setLabelFaces(boolean faceLabels) {
		showFaceLabels = faceLabels;
	}

	public abstract void setZoom(double zoomValue);

	/*********************************************************************************
	 * lookAt
	 * 
	 * This utility method produces a certain transformation normally provided
	 * by openGL. Given a position and a target position in object coordinates,
	 * and possibly an "up" vector, this method returns an affine transformation
	 * that will move a camera/light from its default orientation to the input
	 * position and point it at the target, with the prescribed orientation.
	 * 
	 * If an up vector is not given, then one is chosen arbitrarily among
	 * vectors perpendicular to the vector pointing in the direction (psn_target
	 * - psn). This can be useful when positioning a light (we only care about
	 * the direction it points, not the orientation).
	 *********************************************************************************/
	protected static MatrixBuilder lookAt(Vector psn, Vector target) {
		Vector forward = Vector.subtract(target, psn);
		Vector up = new Vector(forward.getComponent(1), -forward.getComponent(0), 0);
		return lookAt(psn, target, up);
	}

	protected static MatrixBuilder lookAt(Vector psn, Vector target, Vector up) {
		Vector forward = Vector.subtract(target, psn);
		forward.normalize();
		Vector forwardScaled = new Vector(forward);
		forwardScaled.scale(Vector.dot(forward, up));
		Vector upUnit = new Vector(up);
		upUnit.subtract(forwardScaled);
		upUnit.normalize();

		Vector v = Vector.cross(forward, up);
		v.normalize();

		double[] matrix = new double[16];
		matrix[0 * 4 + 0] = v.getComponent(0);
		matrix[0 * 4 + 1] = upUnit.getComponent(0);
		matrix[0 * 4 + 2] = -forward.getComponent(0);
		matrix[0 * 4 + 3] = 0.0;

		matrix[1 * 4 + 0] = v.getComponent(1);
		matrix[1 * 4 + 1] = upUnit.getComponent(1);
		matrix[1 * 4 + 2] = -forward.getComponent(1);
		matrix[1 * 4 + 3] = 0.0;

		matrix[2 * 4 + 0] = v.getComponent(2);
		matrix[2 * 4 + 1] = upUnit.getComponent(2);
		matrix[2 * 4 + 2] = -forward.getComponent(2);
		matrix[2 * 4 + 3] = 0.0;

		matrix[3 * 4 + 0] = 0.0;
		matrix[3 * 4 + 1] = 0.0;
		matrix[3 * 4 + 2] = 0.0;
		matrix[3 * 4 + 3] = 1.0;

		return MatrixBuilder.euclidean().translate(psn.getVectorAsArray()).times(matrix);
	}
}
