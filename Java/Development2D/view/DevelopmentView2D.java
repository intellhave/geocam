package view;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

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
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.util.CameraUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.Development;
import development.Development.DevelopmentNode;
import development.Vector;

public class DevelopmentView2D extends JRViewer implements Observer {
  private SceneGraphComponent sgcRoot;
  private SceneGraphComponent sgcDevelopment;
  private SceneGraphComponent objects = new SceneGraphComponent();
  private Scene scene;
  private ColorScheme colorScheme;
  private Development development;
  private double radius = 0.03; // radius of sourcePoint objects
  private double lineRadius = 0.005; // radius of direction line
  private double lineLength = INITIAL_LINE_LENGTH; // length of direction line
  
  private Vector cameraForward = new Vector(1, 0);
  private SceneGraphComponent viewingDirection = new SceneGraphComponent();

  public DevelopmentView2D(Development dev, ColorScheme scheme) {
    development = dev;
    colorScheme = scheme;
    sgcRoot = new SceneGraphComponent();
    sgcDevelopment = new SceneGraphComponent();
    sgcDevelopment.addChild(objects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());

    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.10);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.0, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
    sgcRoot.addChild(sgcLight);

    //sgcRoot.addTool(new RotateTool());

    updateGeometry();
    sgcRoot.addChild(sgcDevelopment);
    sgcRoot.addChild(viewingDirection);
    setViewingDirection(cameraForward);
    this.addBasicUI();
    this.registerPlugin(new UIPanel_Options());
    this.setShowPanelSlots(true,false,false,false);

    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    this.startup();

  }

  @Override
  public void update(Observable dev, Object arg) {
    development = (Development) dev;
    String whatChanged = (String) arg;
    updateGeometry();
    if (whatChanged.equals("surface") || whatChanged.equals("depth")) {
      CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
          scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    }

  }

  private void updateGeometry() {
    sgcDevelopment.removeChild(objects);
    objects = new SceneGraphComponent();
    sgcDevelopment.setGeometry(getGeometry());
    sgcDevelopment.addChild(objects);
  }

  public Geometry getGeometry() {
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    ArrayList<Color> colors = new ArrayList<Color>();
    computeDevelopment(development.getRoot(), colors, geometry);
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
    return ifsf.getGeometry();
  }
  
  public void setPointRadius(double r) {
    radius = r;
    updateGeometry();
  }

  /*
   * Adds appropriate source point objects to objects SGC
   */
  private void computeDevelopment(DevelopmentNode node,
      ArrayList<Color> colors, DevelopmentGeometry geometry) {
    if (node.faceIsSource()) {
      Vector sourcePoint = development.getSourcePoint();
      Vector newSource = new Vector(sourcePoint.getComponent(0),
          sourcePoint.getComponent(1), 1);
      Vector transSourcePoint = node.getAffineTransformation().transformVector(
          newSource);
      Vector transSourcePoint2d = new Vector(transSourcePoint.getComponent(0),
          transSourcePoint.getComponent(1));

      if (node.getEmbeddedFace().contains(transSourcePoint2d) || node.isRoot()) {
        // containment alg doesn't work for root
        objects.addChild(SGCMethods.sgcFromPoint(transSourcePoint, radius));
      }
    }

    double[][] face = node.getEmbeddedFace().getVectorsAsArray();
    geometry.addFace(face);
    colors.add(colorScheme.getColor(node));

    Iterator<DevelopmentNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      computeDevelopment(itr.next(), colors, geometry);
    }
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    updateGeometry();
  }
  
  public void setLineLength(double length) {
    lineLength = length;
    setViewingDirection(cameraForward);
  }

  public void setViewingDirection(Vector v) {
    Vector vector = new Vector(v);
    vector.normalize();
    vector.scale(lineLength);
    sgcRoot.removeChild(viewingDirection);
    viewingDirection = SGCMethods.sgcFromVector(vector, lineRadius);
    sgcRoot.addChild(viewingDirection); 
  }

  public void rotate(double angle) {
    double cos = Math.cos(-angle);
    double sin = Math.sin(-angle);
    double x = cameraForward.getComponent(0);
    double y = cameraForward.getComponent(1);

    double x_new = cos * x - sin * y;
    double y_new = sin * x + cos * y;
    cameraForward = new Vector(x_new, y_new);
    sgcRoot.removeChild(viewingDirection);
    setViewingDirection(cameraForward);
    sgcRoot.addChild(viewingDirection);
  }
  
  private static int MAX_LINE_LENGTH = 20;
  private static int INITIAL_LINE_LENGTH = 2;
  private static int MAX_POINT_SIZE = 20;
  private static int INITIAL_POINT_SIZE = 3;
  private static int MAX_LINE_RADIUS = 50;
  private static int INITIAL_LINE_RADIUS = 5;
  
  class UIPanel_Options extends ViewShrinkPanelPlugin {

    TitledBorder border_size = BorderFactory.createTitledBorder("");
    TitledBorder border_len = BorderFactory.createTitledBorder("");
    TitledBorder border_lineRadius = BorderFactory.createTitledBorder("");

    
    private void makeUIComponents() {
      
      JSlider pointSizeSlider = new JSlider(0, MAX_POINT_SIZE, INITIAL_POINT_SIZE);
      pointSizeSlider.addChangeListener(new ChangeListener(){
          public void stateChanged(ChangeEvent e) {
            radius = ((JSlider)e.getSource()).getValue()/100.0;
            updateGeometry();
            border_size.setTitle(String.format("Point Radius (%1.3f)", radius));
          }
      });
      
      pointSizeSlider.setMaximumSize(new Dimension(300,100));
      pointSizeSlider.setAlignmentX(0.0f);
      border_size.setTitle(String.format("Point Radius (%1.3f)", radius));
      pointSizeSlider.setBorder(border_size);
      shrinkPanel.add(pointSizeSlider);
      
      JSlider lengthSlider = new JSlider(0, MAX_LINE_LENGTH, INITIAL_LINE_LENGTH);
      lengthSlider.addChangeListener(new ChangeListener(){
          public void stateChanged(ChangeEvent e) {
            double lineLength = ((JSlider)e.getSource()).getValue();
            setLineLength(lineLength);
            border_len.setTitle(String.format("Line Length (%1.3f)",lineLength));
          }
      });
      
      lengthSlider.setMaximumSize(new Dimension(300,100));
      lengthSlider.setAlignmentX(0.0f);
      border_len.setTitle(String.format("Line Length (%1.3f)",lineLength));
      lengthSlider.setBorder(border_len);
      shrinkPanel.add(lengthSlider);
      
      JSlider lineRadSlider = new JSlider(0, MAX_LINE_RADIUS, INITIAL_LINE_RADIUS);
      lineRadSlider.addChangeListener(new ChangeListener(){
          public void stateChanged(ChangeEvent e) {
            lineRadius = ((JSlider)e.getSource()).getValue()/1000.0;
            setViewingDirection(cameraForward);
            border_lineRadius.setTitle(String.format("Line Radius (%1.3f)", lineRadius));
          }
      });
      
      lineRadSlider.setMaximumSize(new Dimension(300,100));
      lineRadSlider.setAlignmentX(0.0f);
      border_lineRadius.setTitle(String.format("Line Radius (%1.3f)", lineRadius));
      lineRadSlider.setBorder(border_lineRadius);
      shrinkPanel.add(lineRadSlider);
      
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
      PluginInfo info = new PluginInfo("Line and Point Options", "");
      return info;
    }
    
    //these lines would add a help page for the tool
    //@Override public String getHelpDocument() { return "BeanShell.html"; }
    //@Override public String getHelpPath() { return "/de/jreality/plugin/help/"; }
  };

}
