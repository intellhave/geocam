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

import view.SGCMethods.DevelopmentGeometrySim3D;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.PointLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.Development;
import development.Development.DevelopmentNode;
import development.Vector;

public class DevelopmentView3D extends DevelopmentView {
  private static int INITIAL_HEIGHT = 15;
  private double height = INITIAL_HEIGHT/100.0;
  private static int MAX_HEIGHT = 30;

  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;
  
  private static Color[] colors = { Color.green, Color.yellow, Color.pink, Color.cyan, Color.orange };
  private static int colorIndex = 0;

  private Scene scene;
  private Vector cameraForward = new Vector(-1, 0);;

  public DevelopmentView3D(Development development, ColorScheme colorScheme, double radius) {
    super(development, colorScheme, radius);
    dimension = 3;

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);

    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);

    updateGeometry();

    this.addBasicUI();
    this.registerPlugin(new UIPanel_Options());
    this.setShowPanelSlots(true,false,false,false);

    this.setContent(sgcRoot);
    sgcRoot.addTool(new ShootTool(development));
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
    SceneGraphComponent sgcpLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    PointLight plight = new PointLight();
    light.setIntensity(1.0);
    light.setColor(Color.white);
    plight.setIntensity(1.0);
    plight.setColor(Color.white);
    sgcLight.setLight(light);
    sgcpLight.setLight(plight);
    MatrixBuilder.euclidean().rotate(2.65, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
    MatrixBuilder.euclidean().rotate(2.65, new double[] { 0, 1, 0 })
    .assignTo(sgcpLight);

    sgcRoot.addChild(sgcLight);
//    sgcRoot.addChild(sgcpLight);

    // by default, everything is upside down
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(sgcDevelopment);
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(sgcObjects);

    viewer.setCameraPath(camera_source);
    viewer.render();
  }

  private void updateCamera() {
    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        cameraForward.getComponent(1), 0, cameraForward.getComponent(0), 0,
        -cameraForward.getComponent(0), 0, cameraForward.getComponent(1), 0, 
        0, 1, 0, 0, 
        0, 0, 0, 1);
    M.assignTo(sgc_camera);
  }

  protected void updateGeometry() {
    synchronized(nodeList) {
      nodeList.clear();
    }
    sgcDevelopment.setGeometry(getGeometry());
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(sgcObjects);
    setObjectsSGC();
    updateCamera();
  }

  /*
   * Returns geometry for simulated 3D view of development.
   */
  public Geometry getGeometry() {
    DevelopmentGeometrySim3D geometry = new DevelopmentGeometrySim3D();
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
   * Recursively adds geometry for each face in tree to a DevelopmentGeometrySim3D, 
   * and adds nodes to nodeList (should be empty at start)
   */
  private void computeDevelopment(DevelopmentNode devNode,
      ArrayList<Color> colors, DevelopmentGeometrySim3D geometry) {
    
    for(NodeImage n : devNode.getObjects()) {
      if(!n.getPosition().isZero())
        synchronized(nodeList) {
          nodeList.add(n);
        }
    }

    double[][] face = devNode.getEmbeddedFace().getVectorsAsArray();
    geometry.addFace(face, height);

    // (adding two faces at a time)
    colors.add(colorScheme.getColor(devNode));
    colors.add(colorScheme.getColor(devNode));

    Iterator<DevelopmentNode> itr = devNode.getChildren().iterator();
    while (itr.hasNext()) {
      computeDevelopment(itr.next(), colors, geometry);
    }
  }
  
  // ================== Shooting Tool ==================
  
  private static class ShootTool extends AbstractTool {
    private Development development;
    
    public ShootTool(Development development) {
      super(InputSlot.LEFT_BUTTON);
      this.development = development;
    }
   
    @Override
    public void activate(ToolContext tc) {

      double x = tc.getCurrentPick().getWorldCoordinates()[0];
      double y = tc.getCurrentPick().getWorldCoordinates()[1];
      Vector movement = new Vector(x,-y);
      movement.normalize();
      //movement.scale(0.05);
      development.addBulletAtSource(Color.black,/*colors[colorIndex++]*/ movement);
      colorIndex = colorIndex % colors.length;
    }
    @Override
    public void deactivate(ToolContext tc) { } 
    @Override
    public void perform(ToolContext tc) { }
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
  }
}
