package view;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import view.Development.DevelopmentNode;
import view.SGCTree.SGCNode;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.SceneGraphUtility;
import development.Vector;

public class DevelopmentView2D extends JRViewer implements Observer {
	private SGCTree sgcTree;
	private SceneGraphComponent sgcRoot;
	private DevelopmentNode root;
	private Vector sourcePoint;
	private ColorScheme colorScheme;
	private int maxDepth;
	
	private Vector cameraForward;
	private SceneGraphComponent sgcCamera;
	private Viewer viewer;
	private SceneGraphPath cameraFree;

	public DevelopmentView2D(Development development, ColorScheme scheme) {
		maxDepth = development.getDesiredDepth();
		colorScheme = scheme;
		sgcTree = new SGCTree(development, colorScheme, 2);
		root = development.getRoot();
		sourcePoint = development.getSourcePoint();
		sgcRoot = new SceneGraphComponent();
		updateSGC(sgcTree.getRoot(), 0);
		sgcRoot.addChild(sgcFromPoint(sourcePoint));
		this.setContent(sgcRoot);
		this.startup();
		
		   //make camera and sgc_camera
	    Camera camera = new Camera();
	    camera.setNear(.015);
	    camera.setFieldOfView(60);
	//  camera.setStereo(true);
	    cameraForward = new Vector(1,0);
	    
	    sgcCamera = SceneGraphUtility.createFullSceneGraphComponent("camera");
	    sgcRoot.addChild(sgcCamera);
	    sgcCamera.setCamera(camera);
	    updateCamera();
	    
	    viewer = this.getViewer();
	    cameraFree = viewer.getCameraPath();
	    viewer.setCameraPath(cameraFree);
	}

	private void updateSGC(SGCNode node, int depth) {
		if (depth > maxDepth)
			return;
		if (depth == 0)
			clearSGC();

		sgcRoot.addChild(node.getSGC());
		Iterator<SGCNode> itr = node.getChildren().iterator();
		while (itr.hasNext()) {
			updateSGC(itr.next(), depth + 1);
		}
	}
	
	  private void updateCamera(){
		    
		    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
		        cameraForward.getComponent(1),0,cameraForward.getComponent(0),0,
		        -cameraForward.getComponent(0),0,cameraForward.getComponent(1),0,
		        0,1,0,0,
		        0,0,0,1
		    );
		    M.assignTo(sgcCamera);
		  }

	@Override
	public void update(Observable development, Object arg) {
		root = ((Development) development).getRoot();
		sourcePoint = ((Development) development).getSourcePoint();
		maxDepth = ((Development) development).getDesiredDepth();
		sgcTree.setVisibleDepth(maxDepth);
		sgcRoot.addChild(sgcFromPoint(sourcePoint));
	}

	public void setColorScheme(ColorScheme scheme) {
		colorScheme = scheme;
		sgcTree.setColorScheme(colorScheme);
	}

	private void clearSGC() {
		List<SceneGraphComponent> list = sgcRoot.getChildComponents();
		for (int i = 0; i < list.size(); i++) {
			sgcRoot.removeChild(list.get(i));
		}
	}

	public static SceneGraphComponent sgcFromPoint(Vector point) {

		// create the sgc
		SceneGraphComponent sgc_points = new SceneGraphComponent();

		// create appearance
		Appearance app_points = new Appearance();

		// set some basic attributes
		app_points.setAttribute(CommonAttributes.VERTEX_DRAW, true);
		app_points.setAttribute(CommonAttributes.LIGHTING_ENABLED, true);

		// set point shader
		DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
				.createDefaultGeometryShader(app_points, true);
		DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
		dps.setSpheresDraw(true);
		dps.setPointRadius(0.01);
		dps.setDiffuseColor(Color.BLUE);

		// set appearance
		sgc_points.setAppearance(app_points);

		// set vertlist
		double[][] vertlist = { { point.getComponent(0), point.getComponent(1),
				0 } };

		// create geometry with pointsetfactory
		PointSetFactory psf = new PointSetFactory();

		psf.setVertexCount(1);
		psf.setVertexCoordinates(vertlist);
		psf.update();

		// set geometry
		sgc_points.setGeometry(psf.getGeometry());

		// return
		return sgc_points;
	}

	
}
