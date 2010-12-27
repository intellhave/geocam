package view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.SGCMethods.DevelopmentGeometry3D;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.Development;
import development.Development.DevelopmentNode;
import development.Node;
import development.Vector;

public class DevelopmentView3D extends DevelopmentView {
  private static int INITIAL_HEIGHT = 15;
  private double height = INITIAL_HEIGHT/100.0;
  private static int MAX_HEIGHT = 30;

  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;

  private Scene scene;
  private Vector cameraForward = new Vector(-1, 0);;

  public DevelopmentView3D(Development development, ColorScheme colorScheme) {
    super(development, colorScheme);

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);

    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);
   // updateCamera();

    updateGeometry();

    this.addBasicUI();
    this.registerPlugin(new UIPanel_Options());
    this.setShowPanelSlots(true,false,false,false);

    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    this.startup();
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);

    viewer = this.getViewer();
    camera_source = SceneGraphUtility.getPathsBetween(viewer.getSceneRoot(),
        sgc_camera).get(0);
    camera_source.push(sgc_camera.getCamera());

    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.0);
    light.setColor(Color.white);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.65, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
    sgcRoot.addChild(sgcLight);

    // by default, everything is upside down
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(sgcDevelopment);
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(objects);

    viewer.setCameraPath(camera_source);
    viewer.render();
  }

  private void updateCamera() {
    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        cameraForward.getComponent(1), 0, cameraForward.getComponent(0), 0,
        -cameraForward.getComponent(0), 0, cameraForward.getComponent(1), 0, 0,
        1, 0, 0, 0, 0, 0, 1);
    M.assignTo(sgc_camera);
  }

  protected void updateGeometry() {
    nodeList = new ArrayList<Node>();
    sgcDevelopment.setGeometry(getGeometry());
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(objects);
    setObjectsSGC();
    updateCamera();
  }

  public Geometry getGeometry() {
    DevelopmentGeometry3D geometry = new DevelopmentGeometry3D();
    ArrayList<Color> colors = new ArrayList<Color>();
    computeDevelopment(development.getRoot(), colors, geometry);
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

    Color[] colorList = new Color[colors.size()];
    for (int i = 0; i < colors.size(); i++) {
      colorList[i] = colors.get(i);
    }

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();
    int[][] ifsf_edges = geometry.getEdges();

    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setEdgeCount(ifsf_edges.length);
    ifsf.setEdgeIndices(ifsf_edges);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setFaceColors(colorList);
    ifsf.update();
    return ifsf.getGeometry();
  }

  /*
   * Adds appropriate source point objects to objects SGC
   */
  private void computeDevelopment(DevelopmentNode node,
      ArrayList<Color> colors, DevelopmentGeometry3D geometry) {
    
    Iterator<Node> iterator = node.getObjects().iterator();
    while(iterator.hasNext()) {
      Node n = iterator.next();
      if(!n.getPosition().isZero())
        nodeList.add(n);
    }

    double[][] face = node.getEmbeddedFace().getVectorsAsArray();
    geometry.addFace(face, height);

    // (adding two faces at a time)
    colors.add(colorScheme.getColor(node));
    colors.add(colorScheme.getColor(node));

    Iterator<DevelopmentNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      computeDevelopment(itr.next(), colors, geometry);
    }
  }


  
  // ================== Options Panel ==================
  
  class UIPanel_Options extends ViewShrinkPanelPlugin {

    TitledBorder heightBorder = BorderFactory.createTitledBorder("");
    
    private void makeUIComponents() {
      
      JSlider heightSlider = new JSlider(0, MAX_HEIGHT, INITIAL_HEIGHT);
      heightSlider.addChangeListener(new ChangeListener(){
          public void stateChanged(ChangeEvent e) {
            height = ((JSlider)e.getSource()).getValue()/100.0;
            updateGeometry();
            heightBorder.setTitle(String.format("Height (%1.3f)", height));
          }
      });
      
      heightSlider.setMaximumSize(new Dimension(300,100));
      heightSlider.setAlignmentX(0.0f);
      heightBorder.setTitle(String.format("Height (%1.3f)", height));
      heightSlider.setBorder(heightBorder);
      shrinkPanel.add(heightSlider);
      
      //specify layout
      shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6)); //a little padding
      shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),BoxLayout.Y_AXIS));
    }
    
    @Override
    public void install(Controller c) throws Exception {
      makeUIComponents();
      super.install(c);
    }
    
    @Override
    public PluginInfo getPluginInfo(){
      PluginInfo info = new PluginInfo("Set Simulated 3D Height", "");
      return info;
    }
  };
}
