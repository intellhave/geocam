package view;

import geoquant.Length;
import inputOutput.TriangulationIO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import triangulation.Edge;
import triangulation.Tetra;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme3D.schemes;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.AxisState;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.Coord3D;
import development.Development3D;
import development.Node3D;
import development.Vector;

public class Development3DGUI extends JRViewer {
  private static Vector cameraPosition_ = new Vector(0, 0, 0);
  private static Vector cameraUp_ = new Vector(0, 1, 0);
  private static Vector cameraForward_ = new Vector(0, 0, -1);
  private static Vector cameraRight_ = new Vector(1, 0, 0);
  private static SceneGraphComponent sgcCamera_;
  private static SceneGraphPath cameraFree_;
  private static SceneGraphPath cameraSource_;
  private static Viewer viewer_;
  
  private static Development3D development_;
  private static Vector sourcePoint_;
  private static int currentDepth_ = 2;
  private static final int MAX_DEPTH_ = 5;
  private static double stepSize_ = 0.01;
  private static double radius_ = 0.1;
  private static final double movement_seconds_per_rotation_ = 2.0;

  private static SceneGraphComponent sgcRoot_ = new SceneGraphComponent();
  private static SceneGraphComponent sgcRootExternal_ = new SceneGraphComponent();
  private static SceneGraphComponent sgcDevelopment_ = new SceneGraphComponent();
  private static SceneGraphComponent sgcObjects_ = new SceneGraphComponent();
  private static SceneGraphComponent sgcObjectsExternal_ = new SceneGraphComponent();

  private static Scene scene_;
  private static Scene sceneExternal_;
  private static ColorScheme3D colorScheme_;
  
  private static Timer moveTimer_; // timer for moving objects

  public static void main(String[] args) {
 //   loadSurface("Data/Triangulations/3DManifolds/pentachoron.xml");
    loadSurface("Data/Triangulations/3DManifolds/FlatTorus.xml");
    colorScheme_ = new ColorScheme3D(schemes.FACE);

    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);

    sgcDevelopment_.setAppearance(SGCMethods.getDevelopment3DAppearance());

    sgcCamera_ = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot_.addChild(sgcCamera_);
    sgcRoot_.addChild(sgcObjects_);
    sgcRoot_.addChild(sgcDevelopment_);
    sgcRoot_.addTool(new CameraRotationTool_UpAxis());
    sgcRoot_.addTool(new CameraRotationTool_RightAxis());
    sgcRootExternal_.addChild(sgcDevelopment_);
    sgcRootExternal_.addChild(sgcObjectsExternal_);
    sgcCamera_.setCamera(camera);

    updateGeometry();

    updateCamera();

    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgcRoot_);
    jrv.registerPlugin(new UIPanel_Options());
    jrv.setShowPanelSlots(true, false, false, false);
    jrv.startup();

    JRViewer jrvExternal = new JRViewer();
    jrvExternal.addBasicUI();
    jrvExternal.setContent(sgcRootExternal_);
    sgcRootExternal_.addTool(new RotateTool());
    jrvExternal.startup();

    viewer_ = jrv.getViewer();
    Viewer viewer2_ = jrvExternal.getViewer();
    cameraFree_ = viewer_.getCameraPath();
    cameraSource_ = SceneGraphUtility.getPathsBetween(viewer_.getSceneRoot(),
        sgcCamera_).get(0);
    cameraSource_.push(sgcCamera_.getCamera());

    viewer_.setCameraPath(cameraSource_);
    viewer2_.setCameraPath(cameraFree_);

    scene_ = jrv.getPlugin(Scene.class);
    CameraUtility.encompass(scene_.getAvatarPath(), scene_.getContentPath(),
        scene_.getCameraPath(), 1.75, Pn.EUCLIDEAN);

    sceneExternal_ = jrvExternal.getPlugin(Scene.class);
    CameraUtility.encompass(sceneExternal_.getAvatarPath(),
        sceneExternal_.getContentPath(), sceneExternal_.getCameraPath(), 1.75,
        Pn.EUCLIDEAN);
    
    moveTimer_ = new Timer(50, null);
    moveTimer_.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        development_.moveObjects();
        updateGeometry();
      }
    });
    moveTimer_.start();
  }

  private static void loadSurface(String filename) {
    TriangulationIO.readTriangulation(filename);

    Iterator<Integer> i = null;

    // set edge lengths
    i = Triangulation.edgeTable.keySet().iterator();
    while (i.hasNext()) {
      Integer key = i.next();
      Edge e = Triangulation.edgeTable.get(key);
      Length.at(e).setValue(3);
    }

    // pick some arbitrary tetra and source point, and compute geometry
    i = Triangulation.tetraTable.keySet().iterator();
    Tetra sourceTetra_ = Triangulation.tetraTable.get(i.next());

    sourcePoint_ = new Vector(0, 0, 0);
    Iterator<Vertex> iv = sourceTetra_.getLocalVertices().iterator();
    while (iv.hasNext()) {
      sourcePoint_.add(Coord3D.coordAt(iv.next(), sourceTetra_));
    }
    sourcePoint_.scale(1.0f / 4.0f);

    if (development_ == null)
      development_ = new Development3D(sourceTetra_, sourcePoint_,
          currentDepth_, stepSize_, radius_);
    else
      development_.rebuild(sourceTetra_, sourcePoint_, currentDepth_);
  }

  private static void rotateCameraRightAxis(double theta){

    double c = Math.cos(theta);
    double s = Math.sin(theta);

    //f'=fc+us, u'=-fs+uc
    Vector new_forward = Vector.add(Vector.scale(cameraForward_, c), Vector.scale(cameraUp_, s));
    Vector new_up = Vector.add(Vector.scale(cameraForward_, -s), Vector.scale(cameraUp_, c));
    cameraForward_ = Vector.normalize(new_forward);
    cameraUp_ = Vector.normalize(new_up);
  }

  private static void rotateCameraUpAxis(double theta){
  
    double c = Math.cos(theta);
    double s = Math.sin(theta);
    
    //f'=fc-rs, r'=fs+rc
    Vector new_forward = Vector.add(Vector.scale(cameraForward_, c), Vector.scale(cameraRight_, -s));
    Vector new_right = Vector.add(Vector.scale(cameraForward_, s), Vector.scale(cameraRight_, c));
    cameraForward_ = Vector.normalize(new_forward);
    cameraRight_ = Vector.normalize(new_right);
  }
  
  private static void updateCamera() {
    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        cameraRight_.getComponent(0), cameraUp_.getComponent(0),
        cameraForward_.getComponent(0), cameraPosition_.getComponent(0),
        cameraRight_.getComponent(1), cameraUp_.getComponent(1),
        cameraForward_.getComponent(1), cameraPosition_.getComponent(1),
        cameraRight_.getComponent(2), cameraUp_.getComponent(2),
        cameraForward_.getComponent(2), cameraPosition_.getComponent(2), 0, 0,
        0, 1);
    M.assignTo(sgcCamera_);
  }

  private static void updateGeometry() {
    sgcDevelopment_.setGeometry(development_.getGeometry(colorScheme_));
    sgcRoot_.removeChild(sgcObjects_);
    sgcRootExternal_.removeChild(sgcObjectsExternal_);
    sgcObjects_ = new SceneGraphComponent();
    sgcObjectsExternal_ = new SceneGraphComponent();
    for(Node3D node : development_.getObjects()) {
      sgcObjectsExternal_.addChild(SGCMethods.sgcFromNode3D(node));
      if(!node.getPosition().isZero())
        sgcObjects_.addChild(SGCMethods.sgcFromNode3D(node));
    }
    sgcRoot_.addChild(sgcObjects_);
    sgcRootExternal_.addChild(sgcObjectsExternal_);
  }

  
  
  // USER INTERFACE
  // ==============================
  static class UIPanel_Options extends ViewShrinkPanelPlugin {

    TitledBorder depthBorder_ = BorderFactory
        .createTitledBorder("Recursion Depth (" + currentDepth_ + ")");

    private void makeUIComponents() {
      JPanel sliderPanel = new JPanel();
      sliderPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

      JSlider depthSlider = new JSlider(1, MAX_DEPTH_, currentDepth_);
      depthSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          currentDepth_ = ((JSlider) e.getSource()).getValue();
          development_.setDepth(currentDepth_);
          depthBorder_.setTitle("Recursion Depth (" + currentDepth_ + ")");
          updateGeometry();
        }
      });

      depthSlider.setBorder(depthBorder_);
      sliderPanel.add(depthSlider);
      shrinkPanel.add(sliderPanel);

      JPanel colorPanel = new JPanel();
       ButtonGroup group = new ButtonGroup();
       JRadioButton button;
      
       // face scheme button
       button = new JRadioButton("Face");
       button.setSelected(colorScheme_.getSchemeType() == schemes.FACE);
       button.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e){
         colorScheme_ = new ColorScheme3D(schemes.FACE);
         updateGeometry();
       }
       });
       colorPanel.add(button);
       group.add(button);
      
       // depth scheme button
       button = new JRadioButton("Recursion Depth");
       button.setSelected(colorScheme_.getSchemeType() == schemes.DEPTH);
       button.addActionListener(new ActionListener() {
       public void actionPerformed(ActionEvent e){
         colorScheme_ = new ColorScheme3D(schemes.DEPTH);
         updateGeometry();
       }
       });
       colorPanel.add(button);
       group.add(button);
       
       colorPanel.setBorder(BorderFactory.createTitledBorder("Color Scheme"));
       shrinkPanel.add(colorPanel);
       
      shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),
          BoxLayout.Y_AXIS));
      
      JPanel checkPanel = new JPanel();
      checkPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      
      JCheckBox drawEdgesBox = new JCheckBox("Draw edges");
      drawEdgesBox.setSelected(true);
      drawEdgesBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean value = ((JCheckBox)e.getSource()).isSelected();
          sgcDevelopment_.getAppearance().setAttribute(CommonAttributes.EDGE_DRAW, value);
        }
      });
      
      JCheckBox drawFacesBox = new JCheckBox("Draw faces");
      drawFacesBox.setSelected(true);
      drawFacesBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean value = ((JCheckBox)e.getSource()).isSelected();
          sgcDevelopment_.getAppearance().setAttribute(CommonAttributes.FACE_DRAW, value);
        }
      });
      checkPanel.setBorder(BorderFactory.createTitledBorder("Visibility options"));
      checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
      checkPanel.add(drawEdgesBox);
      checkPanel.add(drawFacesBox);
      shrinkPanel.add(checkPanel);
    }

    @Override
    public void install(Controller c) throws Exception {
      makeUIComponents();
      super.install(c);
    }

    @Override
    public PluginInfo getPluginInfo() {
      return new PluginInfo("View Options", "");
    }
  };
  
  //TOOL(S) FOR MOVEMENT
  //==============================
  
  static class CameraRotationTool_RightAxis extends AbstractTool {
    
    private static long time; 
    private static final double radians_per_millisecond = Math.PI/(movement_seconds_per_rotation_*500);
    private static final InputSlot FORWARD_BACKWARD = InputSlot.getDevice("ForwardBackwardAxis");
    private static final InputSlot SYSTEM_TIMER = InputSlot.SYSTEM_TIME;  
    
    public CameraRotationTool_RightAxis() {
      super(FORWARD_BACKWARD); //'activate' tool on F/B or L/R
      addCurrentSlot(SYSTEM_TIMER); //'perform' tool on tick
    }

    //set initial time
    @Override public void activate(ToolContext tc) { time = tc.getTime(); }

    @Override public void perform(ToolContext tc) {
      //get axis state
      AxisState as_fb = tc.getAxisState(FORWARD_BACKWARD);
      
      //get dt and update time
      long newtime = tc.getTime();
      long dt = newtime - time;
      time = newtime;

      //move forward/backward
      if(as_fb.isPressed()){
        rotateCameraRightAxis(radians_per_millisecond*dt*as_fb.doubleValue());
        updateCamera();
      }
    }  
  };
  
  static class CameraRotationTool_UpAxis extends AbstractTool {
    
    private static long time; 
    private static final double radians_per_millisecond = Math.PI/(movement_seconds_per_rotation_*500);
    private static final InputSlot LEFT_RIGHT = InputSlot.getDevice("LeftRightAxis");
    private static final InputSlot SYSTEM_TIMER = InputSlot.SYSTEM_TIME;  
    
    public CameraRotationTool_UpAxis() {
      super(LEFT_RIGHT); //'activate' tool on F/B or L/R
      addCurrentSlot(SYSTEM_TIMER); //'perform' tool on tick
    }
    
    //set initial time
    @Override public void activate(ToolContext tc) { time = tc.getTime(); } 
    
    @Override public void perform(ToolContext tc) {
      //get axis state
      AxisState as_lr = tc.getAxisState(LEFT_RIGHT);
      
      //get dt and update time
      long newtime = tc.getTime();
      long dt = newtime - time;
      time = newtime;
      
      //move left/right
      if(as_lr.isPressed()){ 
        rotateCameraUpAxis(-radians_per_millisecond*dt*as_lr.doubleValue());
        updateCamera();
      }
    }
  };
}
