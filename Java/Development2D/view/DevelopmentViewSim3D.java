package view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import objects.ObjectAppearance;
import objects.VisibleObject;
import view.SGCMethods.DevelopmentGeometrySim3D;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.PointLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.Development;
import development.DevelopmentNode;
import development.Vector;

public class DevelopmentViewSim3D extends DevelopmentView {

  private static int INITIAL_HEIGHT = 25;
  private double height = INITIAL_HEIGHT / 100.0;
  private static int MAX_HEIGHT = 50;

  private boolean showAvatar = true;
  
  // private static final boolean USE_SHOOT_TOOL = false;

  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;

  // don't add objects which are within this radius of the origin; obstructs
  // camera
  private static final double CLIP_NEAR_RADIUS = 0.1; // TODO: re-implement this

  private Scene scene;
  private Vector cameraForward = new Vector(-1, 0);

  private HashMap<VisibleObject, LinkedList<SceneGraphComponent>> sgcpools;

  public DevelopmentViewSim3D(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, true);
    dimension = 3;

    sgcpools = new HashMap<VisibleObject, LinkedList<SceneGraphComponent>>();

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.11); // make big enough so that your own ball is clipped
    // camera.setNear(.015);
    camera.setFieldOfView(60);

    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);

    // this.addBasicUI(); //scene graph inspector causes deadlock (?)
    this.registerPlugin(new UIPanel_Options());
    this.setShowPanelSlots(true, false, false, false);
    this.startup();

    this.setContent(sgcRoot);
    // if(USE_SHOOT_TOOL){ sgcRoot.addTool(new
    // ShootTool(shootingGame,development.getSource())); }
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
    // sgcRoot.addChild(sgcpLight);

    // by default, everything is upside down
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(sgcDevelopment);
    // MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
    // .assignTo(sgcObjects);

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

  /*
   * protected void updateGeometry() {
   * 
   * sgcDevelopment.setGeometry(getGeometry()); updateCamera(); }
   */

  protected void initializeNewManifold() {
    for (LinkedList<SceneGraphComponent> pool : sgcpools.values()) {
      while (!pool.isEmpty()) {
        SceneGraphComponent sgc = pool.remove();        
        sgcObjects.removeChild(sgc);        
      }
    }
    sgcpools.clear();    
    updateCamera();
    updateGeometry(true,true);    
  }

  /*
   * Returns geometry for simulated 3D view of development.
   */
  protected void generateManifoldGeometry() {

    DevelopmentGeometrySim3D geometry = new DevelopmentGeometrySim3D();
    ArrayList<Color> colors = new ArrayList<Color>();
    generateManifoldGeometry(development.getRoot(), colors, geometry);
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

    sgcDevelopment.setGeometry(ifsf.getGeometry());

    updateCamera();
  }

  /*
   * Recursively adds geometry for each face in tree to a
   * DevelopmentGeometrySim3D, and adds nodes to nodeList (should be empty at
   * start)
   */
  private void generateManifoldGeometry(DevelopmentNode devNode,
      ArrayList<Color> colors, DevelopmentGeometrySim3D geometry) {
    double[][] face = devNode.getClippedFace().getVectorsAsArray();
    geometry.addFace(face, height);

    // (adding two faces at a time)
    colors.add(colorScheme.getColor(devNode));
    colors.add(colorScheme.getColor(devNode));

    Iterator<DevelopmentNode> itr = devNode.getChildren().iterator();
    while (itr.hasNext()) {
      generateManifoldGeometry(itr.next(), colors, geometry);
    }
  }

  protected void generateObjectGeometry() {
    
    HashMap<VisibleObject, ArrayList<Vector[]>> objectImages = new HashMap<VisibleObject, ArrayList<Vector[]>>();
    CommonViewMethods.getDevelopmentObjectImagesAndOrientations(development.getRoot(), objectImages);

    for (VisibleObject vo : objectImages.keySet()) {
      LinkedList<SceneGraphComponent> pool = sgcpools.get(vo);

      if (pool == null) {
        pool = new LinkedList<SceneGraphComponent>();
        sgcpools.put(vo, pool);
      }

      ArrayList<Vector[]> images = objectImages.get(vo);
      if (images == null)
        continue;

      if (images.size() > pool.size()) {
        int sgcCount = images.size() - pool.size();
        for (int jj = 0; jj < 2 * sgcCount; jj++) {
          ObjectAppearance oa = vo.getAppearance();
          SceneGraphComponent sgc = oa.prepareNewSceneGraphComponent();
          pool.add(sgc);
          sgcObjects.addChild(sgc);
        }
      }

      int counter = 0;
      for (SceneGraphComponent sgc : pool) {
        if (counter >= images.size()) {
          sgc.setVisible(false);
        } else {
          Vector[] triple = images.get(counter);
          Vector position = triple[0];
          Vector forward = triple[1];
          forward.normalize();
          //Vector left = triple[2];
          //left.normalize();                              

          double[] matrix = new double[16];
          matrix[0*4+0] = forward.getComponent(0); matrix[0*4+1] = -forward.getComponent(1); matrix[0*4+2] = 0.0; matrix[0*4+3] = 0.0;
          matrix[1*4+0] = forward.getComponent(1); matrix[1*4+1] =  forward.getComponent(0); matrix[1*4+2] = 0.0; matrix[1*4+3] = 0.0;
          matrix[2*4+0] = 0.0; matrix[2*4+1] = 0.0; matrix[2*4+2] = 1.0; matrix[2*4+3] = 0.0;
          matrix[3*4+0] = 0.0; matrix[3*4+1] = 0.0; matrix[3*4+2] = 0.0; matrix[3*4+3] = 1.0;
                                   
          MatrixBuilder.euclidean()
              .translate(position.getComponent(0), position.getComponent(1), height)
              .times(matrix)
              .rotate(Math.PI, new double[] { 1, 0, 0 })
              .scale(vo.getAppearance().getScale())
              .assignTo(sgc);
          
          // This is a hack to find the SGC that displays the avatar.
          // In the future, we should have a dedicated SGC pointer for the avatar. 
          double epsilon = 0.05;
          if( position.lengthSquared() < epsilon ){
            sgc.setVisible( this.showAvatar );
          } else {
            sgc.setVisible(true);
          }
        }
        counter++;
      }
    }
  }


  public void setDrawAvatar(boolean showAvatar) {
     this.showAvatar = showAvatar; 
     this.refreshView();
  }
  
  // ================== Shooting Tool ==================

  /*
   * private static class ShootTool extends AbstractTool { private
   * ManifoldPosition sourcePos; private ShootingGame shootingGame;
   * 
   * public ShootTool(ShootingGame shootingGame, ManifoldPosition sourcePos) {
   * super(InputSlot.LEFT_BUTTON); this.shootingGame = shootingGame; }
   * 
   * @Override public void activate(ToolContext tc) {
   * 
   * double x = tc.getCurrentPick().getWorldCoordinates()[0]; double y =
   * tc.getCurrentPick().getWorldCoordinates()[1]; Vector movement = new
   * Vector(x,-y); movement.normalize();
   * //shootingGame.addBullet(sourcePos,movement); colorIndex = colorIndex %
   * colors.length; }
   * 
   * @Override public void deactivate(ToolContext tc) { }
   * 
   * @Override public void perform(ToolContext tc) { } }
   */

  // ================== Options Panel ==================

  class UIPanel_Options extends ViewShrinkPanelPlugin {

    TitledBorder heightBorder = BorderFactory.createTitledBorder("");

    private void makeUIComponents() {

      JSlider heightSlider = new JSlider(0, MAX_HEIGHT, INITIAL_HEIGHT);
      heightSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          height = ((JSlider) e.getSource()).getValue() / 100.0;
          generateManifoldGeometry();
          heightBorder.setTitle(String.format("Height (%1.3f)", height));
        }
      });

      heightSlider.setMaximumSize(new Dimension(300, 100));
      heightSlider.setAlignmentX(0.0f);
      heightBorder.setTitle(String.format("Height (%1.3f)", height));
      heightSlider.setBorder(heightBorder);
      shrinkPanel.add(heightSlider);

      // specify layout
      shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),
          BoxLayout.Y_AXIS));
    }

    @Override
    public void install(Controller c) throws Exception {
      makeUIComponents();
      super.install(c);
    }

    @Override
    public PluginInfo getPluginInfo() {
      PluginInfo info = new PluginInfo("Set Simulated 3D Height", "");
      return info;
    }
  }

}
