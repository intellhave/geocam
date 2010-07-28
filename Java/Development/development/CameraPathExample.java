package development;

import static de.jreality.shader.CommonAttributes.POLYGON_SHADER;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.Timer;

import de.jreality.geometry.FrameFieldType;
import de.jreality.geometry.PolygonalTubeFactory;
import de.jreality.geometry.Primitives;
import de.jreality.geometry.TubeFactory;
import de.jreality.geometry.TubeUtility;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.Camera;
import de.jreality.scene.IndexedFaceSet;
import de.jreality.scene.IndexedLineSet;
import de.jreality.scene.Light;
import de.jreality.scene.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ImageData;
import de.jreality.shader.ShaderUtility;
import de.jreality.shader.Texture2D;
import de.jreality.shader.TextureUtility;
import de.jreality.tutorial.util.SimpleTextureFactory;
import de.jreality.util.SceneGraphUtility;

public class CameraPathExample {

	//private static List<SceneGraphPath> lightPaths;
	//private static SceneGraphComponent movingLightSGC;
	private static PolygonalTubeFactory polygonalTubeFactory;
	
	public static void main(String[] args) {
		
		SceneGraphComponent world = SceneGraphUtility.createFullSceneGraphComponent("world");
		final SceneGraphComponent child1 = SceneGraphUtility.createFullSceneGraphComponent("knot");
		final SceneGraphComponent child2 = SceneGraphUtility.createFullSceneGraphComponent("point");
		
		world.addChildren(child1, child2);
		
		//child1
		IndexedLineSet torus1 = Primitives.discreteTorusKnot(1, .4, 2, 3, 120);
		polygonalTubeFactory = new PolygonalTubeFactory(torus1, 0);
		polygonalTubeFactory.setClosed(true);
		polygonalTubeFactory.setMatchClosedTwist(true);
		polygonalTubeFactory.setGenerateTextureCoordinates(true);
		polygonalTubeFactory.setRadius(.1);
		polygonalTubeFactory.setGenerateEdges(true);
		polygonalTubeFactory.setFrameFieldType(FrameFieldType.PARALLEL);
		polygonalTubeFactory.update();
		child1.setGeometry(polygonalTubeFactory.getTube());
		DefaultGeometryShader dgs = ShaderUtility.createDefaultGeometryShader(child1.getAppearance(), true);
		dgs.setShowFaces(false);
		dgs.setShowPoints(false);
		dgs.setShowLines(true);
		DefaultLineShader dls = (DefaultLineShader) dgs.createLineShader("default");
		dls.setTubeDraw(false);
		dls.setLineWidth(0.0);
		dls.setTubeRadius(.003);
		dls.setDiffuseColor(Color.black);
		
		//timer to move point
		final TubeUtility.FrameInfo[] frames = polygonalTubeFactory.getFrameField();
		final Timer movepoint = new Timer(40, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent e) {
				MatrixBuilder.euclidean(new Matrix(frames[count].frame.clone())).
					rotateZ(Math.PI/2 + (frames[count].phi)).
					scale(1,1,-1).assignTo(child2);
				count = (count+1)%frames.length;
			}
		});
		//movepoint.start();
		
		child2.add
		
		//make camera
		Camera camera = new Camera();
		camera.setNear(.015);
		camera.setFieldOfView(90);
		child2.setCamera(camera);
		
		//set camera
		final Viewer viewer = JRViewer.display(world);
		final SceneGraphPath campath = viewer.getCameraPath();
		final SceneGraphPath campath2 = SceneGraphUtility.getPathsBetween(viewer.getSceneRoot(), child2).get(0);
		campath2.push(child2.getCamera());
		viewer.setCameraPath(campath2);


	}

}
