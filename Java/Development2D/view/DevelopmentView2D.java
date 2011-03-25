package view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.SGCMethods.DevelopmentGeometry;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.util.CameraUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.Development;
import development.DevelopmentNode;
import development.TimingStatistics;
import development.Vector;

public class DevelopmentView2D extends DevelopmentView {

  private static int MAX_LINE_LENGTH = 20;
  private static int INITIAL_LINE_LENGTH = 2;
  private double lineLength = INITIAL_LINE_LENGTH; // length of direction line
  
  private static int MAX_LINE_RADIUS = 50;
  private static int INITIAL_LINE_RADIUS = 10;
  private double lineRadius = INITIAL_LINE_RADIUS/1000.0; // radius of direction line

  private Vector cameraForward = new Vector(1, 0);
  private SceneGraphComponent viewingDirection = new SceneGraphComponent();
  //private Development development;
  
  public DevelopmentView2D(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, true);
    dimension = 2;
    
    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.10);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.0, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
    sgcRoot.addChild(sgcLight);
    
    sgcRoot.addChild(viewingDirection);
    setViewingDirection(cameraForward);
    //this.addBasicUI(); //scene graph inspector causes deadlock (?)
    this.registerPlugin(new UIPanel_Options());
    this.setShowPanelSlots(true, false, false, false);
    
    sgcRoot.addTool(new PickingTool());

    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    updateCamera();

    this.startup();
  }

  private void updateCamera(){
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
  }
  
  protected void initializeNewManifold(){
    updateCamera();
  }
  /*protected void updateGeometry() {
    
    sgcDevelopment.setGeometry(getGeometry());
  }*/

  /*
   * Returns the geometry of the development, adding clipped faces as calculated
   * in computeDevelopment()
   */
  protected void generateManifoldGeometry() {
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    ArrayList<Color> colors = new ArrayList<Color>();
    generateManifoldGeometry(development.getRoot(), colors, geometry);
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

    Color[] colorList = new Color[colors.size()];
    for (int i = 0; i < colors.size(); i++) {
      colorList[i] = colors.get(i);
    }

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();

    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setFaceColors(colorList);
    ifsf.update();

    sgcDevelopment.setGeometry(ifsf.getGeometry());
  }

  /*
   * recursively adds geometry for each face in tree to DevelopmentGeometry, 
   * and adds nodes and trails to corresponding lists (should be empty at start)
   */
  private void generateManifoldGeometry(DevelopmentNode node,
      ArrayList<Color> colors, DevelopmentGeometry geometry) {
    
    double[][] face = node.getClippedFace().getVectorsAsArray();
    geometry.addFace(face,1.0);
    colors.add(colorScheme.getColor(node));

    for(DevelopmentNode n : node.getChildren())
      generateManifoldGeometry(n, colors, geometry);
  }
  

  protected void generateObjectGeometry(){
    
    /*TODO (TIMING)*/ long taskID = TimingStatistics.startTask(TASK_GET_OBJECT_GEOMETRY);
    SceneGraphComponent sgcNewObjects = CommonViewMethods.generateDevelopmentObjectGeometry(development.getRoot(), false, 0);
    sgcDevelopment.removeChild(sgcObjects);
    sgcObjects = sgcNewObjects;
    sgcDevelopment.addChild(sgcObjects);
    /*TODO (TIMING)*/ TimingStatistics.endTask(taskID);
  }

  public void setLineLength(double length) {
    lineLength = length;
    setViewingDirection(cameraForward);
    updateCamera();
  }

  public void setViewingDirection(Vector v) {
    Vector vector = new Vector(v);
    vector.normalize();
    vector.scale(lineLength);
    sgcRoot.removeChild(viewingDirection);
    viewingDirection = SGCMethods.sgcFromVector(vector, lineRadius);
    sgcRoot.addChild(viewingDirection);
  }
  
  // ================== Picking Tool ==================
  
  private static class PickingTool extends AbstractTool {
    
    public PickingTool() {
      super(InputSlot.LEFT_BUTTON);
    }
   
    @Override
    public void activate(ToolContext tc) {

      double x = tc.getCurrentPick().getWorldCoordinates()[0];
      double y = tc.getCurrentPick().getWorldCoordinates()[1];
      
      Vector v = new Vector(x,y);
      System.out.println(v);

    }
    @Override
    public void deactivate(ToolContext tc) { } 
    @Override
    public void perform(ToolContext tc) { }
  }

  // ================== Options Panel ==================

  class UIPanel_Options extends ViewShrinkPanelPlugin {

    TitledBorder border_len = BorderFactory.createTitledBorder("");
    TitledBorder border_lineRadius = BorderFactory.createTitledBorder("");

    private void makeUIComponents() {
      // -------- LINE LENGTH SLIDER --------
      JSlider lengthSlider = new JSlider(0, MAX_LINE_LENGTH,
          INITIAL_LINE_LENGTH);
      lengthSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          double lineLength = ((JSlider) e.getSource()).getValue();
          setLineLength(lineLength);
          border_len.setTitle(String.format("Line Length (%1.3f)", lineLength));
        }
      });
      lengthSlider.setMaximumSize(new Dimension(300, 100));
      lengthSlider.setAlignmentX(0.0f);
      border_len.setTitle(String.format("Line Length (%1.3f)", lineLength));
      lengthSlider.setBorder(border_len);
      shrinkPanel.add(lengthSlider);

      
      // -------- LINE RADIUS SLIDER --------
      JSlider lineRadSlider = new JSlider(0, MAX_LINE_RADIUS,
          INITIAL_LINE_RADIUS);
      lineRadSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          lineRadius = ((JSlider) e.getSource()).getValue() / 1000.0;
          setViewingDirection(cameraForward);
          border_lineRadius.setTitle(String.format("Line Radius (%1.3f)",
              lineRadius));
        }
      });
      lineRadSlider.setMaximumSize(new Dimension(300, 100));
      lineRadSlider.setAlignmentX(0.0f);
      border_lineRadius.setTitle(String.format("Line Radius (%1.3f)",
          lineRadius));
      lineRadSlider.setBorder(border_lineRadius);
      shrinkPanel.add(lineRadSlider);

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
      PluginInfo info = new PluginInfo("Line and Point Options", "");
      return info;
    }
  };
}
