package view;

import java.io.File;
import java.io.IOException;

import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.util.CameraUtility;
import development.Development;

public class DevelopmentViewEmbedded extends JRViewer {
  private SceneGraphComponent sgcRoot = new SceneGraphComponent();
  private SceneGraphComponent sgcPolyhedron = new SceneGraphComponent();
  private SceneGraphComponent sgcObjects = new SceneGraphComponent();

  public DevelopmentViewEmbedded(String filename, Development development) {
    Geometry geom = sgcPolyhedron.getGeometry();
    try {
      File file = new File(filename);
      geom = Readers.read(file).getGeometry();
    } catch (IOException e) {
      e.printStackTrace();
    }
    sgcPolyhedron.setGeometry(geom);
    sgcRoot.addChild(sgcPolyhedron);
    sgcRoot.addChild(sgcObjects);
    sgcRoot.addTool(new RotateTool());

    Appearance app_polyhedron = new Appearance();
    app_polyhedron.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_polyhedron.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_polyhedron.setAttribute(CommonAttributes.TUBES_DRAW, false);
    app_polyhedron.setAttribute(CommonAttributes.TUBE_RADIUS, 0.02);
    app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    // app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY, 0.05d);
    app_polyhedron.setAttribute(CommonAttributes.PICKABLE, true);
    sgcPolyhedron.setAppearance(app_polyhedron);

    this.addBasicUI();

    this.setContent(sgcRoot);
    Scene scene = this.getPlugin(Scene.class);
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    this.startup();

  }
  
  public void changeGeometry(String filename) {
    Geometry geom = sgcPolyhedron.getGeometry();
    try {
      geom = Readers.read(new File(filename)).getGeometry();
    } catch (IOException e) {
      e.printStackTrace();
    }
    sgcPolyhedron.setGeometry(geom);
    Scene scene = this.getPlugin(Scene.class);
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
  }
  
}
