package view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import objects.ManifoldObjectHandler;
import objects.ManifoldPosition;
import objects.ShootingGame;
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
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.AffineTransformation;
import development.Development;
import development.Frustum2D;
import development.Development.DevelopmentNode;
import development.Vector;

public class DevelopmentView3D extends DevelopmentView {
  
  private static int INITIAL_HEIGHT = 25;
  private double height = INITIAL_HEIGHT/100.0;
  private static int MAX_HEIGHT = 30;
  
  private static final boolean USE_SHOOT_TOOL = false;

  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;
  
  private static Color[] colors = { Color.green, Color.yellow, Color.pink, Color.cyan, Color.orange };
  private static int colorIndex = 0;
  
  //don't add objects which are within this radius of the origin; obstructs camera
  private static final double CLIP_NEAR_RADIUS = 0.1;

  private Scene scene;
  private Vector cameraForward = new Vector(-1, 0);

  public DevelopmentView3D(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, true);
    dimension = 3;

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);

    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);

    //this.addBasicUI(); //scene graph inspector causes deadlock (?)
    this.registerPlugin(new UIPanel_Options());
    this.setShowPanelSlots(true,false,false,false);
    this.startup();

    this.setContent(sgcRoot);
    //if(USE_SHOOT_TOOL){ sgcRoot.addTool(new ShootTool(shootingGame,development.getSource())); }
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

  /*protected void updateGeometry() {

    sgcDevelopment.setGeometry(getGeometry());
    updateCamera();
  }*/

  protected void initializeNewManifold(){ }
  
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
   * Recursively adds geometry for each face in tree to a DevelopmentGeometrySim3D, 
   * and adds nodes to nodeList (should be empty at start)
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
  

  protected void generateObjectGeometry(){
    
    //instead of vector, use something which has a basis (forward, left) also
    HashMap<VisibleObject,ArrayList<Vector>> objectImages = new HashMap<VisibleObject,ArrayList<Vector>>();
    generateObjectGeometry(development.getRoot(), objectImages);

    //generate sgc's for the objects
    SceneGraphComponent sgcNewObjects = new SceneGraphComponent("Objects");
    
    Set<VisibleObject> objectList = objectImages.keySet();
    for(VisibleObject o : objectList){
      sgcNewObjects.addChild(SGCMethods.sgcFromImageList(objectImages.get(o), 0, o.getAppearance()));
    }
    
    sgcDevelopment.removeChild(sgcObjects);
    sgcObjects = sgcNewObjects;
    sgcDevelopment.addChild(sgcObjects);
  }

  /*
   * Recursively adds geometry for each face in tree to a DevelopmentGeometrySim3D, 
   * and adds nodes to nodeList (should be empty at start)
   */
  private void generateObjectGeometry(DevelopmentNode devNode, HashMap<VisibleObject,ArrayList<Vector>> objectImages) {
        
    //look for objects
    LinkedList<VisibleObject> objectList = ManifoldObjectHandler.getObjects(devNode.getFace());
    if(objectList != null){
      
      Frustum2D frustum = devNode.getFrustum();
      AffineTransformation affineTrans = devNode.getAffineTransformation();
      
      for(VisibleObject o : objectList){

        Vector transPos = affineTrans.affineTransPoint(o.getPosition());
        if(frustum != null){
          //check if object should be clipped
          if(!frustum.checkInterior(transPos)){ continue; }
        }
        
        //don't add if it is too close to the origin, gets in the way of the camera
        if(transPos.length() < CLIP_NEAR_RADIUS){ continue; }
        
        //add to image list
        ArrayList<Vector> imageList = objectImages.get(o);
        if(imageList == null){
          imageList = new ArrayList<Vector>();
          objectImages.put(o,imageList);
        }
        imageList.add(transPos);
      }
    }

    Iterator<DevelopmentNode> itr = devNode.getChildren().iterator();
    while (itr.hasNext()) {
      generateObjectGeometry(itr.next(), objectImages);
    }
  }
  
  
  // ================== Shooting Tool ==================
  
  private static class ShootTool extends AbstractTool {
    private ManifoldPosition sourcePos;
    private ShootingGame shootingGame;
    
    public ShootTool(ShootingGame shootingGame, ManifoldPosition sourcePos) {
      super(InputSlot.LEFT_BUTTON);
      this.shootingGame = shootingGame;
    }
   
    @Override
    public void activate(ToolContext tc) {

      double x = tc.getCurrentPick().getWorldCoordinates()[0];
      double y = tc.getCurrentPick().getWorldCoordinates()[1];
      Vector movement = new Vector(x,-y);
      movement.normalize();
      //shootingGame.addBullet(sourcePos,movement);
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
            generateManifoldGeometry();
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
