package view;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import triangulation.Triangulation;
import triangulation.Vertex;

import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.AxisState;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.CommonAttributes;
import de.jreality.util.CameraUtility;
import development.Development;
import development.Trail;
import development.Development.DevelopmentNode;
import geoquant.Radius;

/*
 * DevelopmentView
 * 
 * Overview: Base viewing object. Extensions give different view methods
 * 
 *  * sgcRoot is the root of the scene tree
 *  * sgcDevelopment is scene graph component displaying embedded (clipped) 
 *    faces
 *  * pointer to development
 *  
 *  Possible upgrades:
 *    * remove circles (make an extension)
 */

public abstract class DevelopmentView extends JRViewer implements Observer{
  protected SceneGraphComponent sgcRoot = new SceneGraphComponent();
  protected SceneGraphComponent sgcDevelopment = new SceneGraphComponent();
  protected SceneGraphComponent sgcObjects = new SceneGraphComponent();
  protected Scene scene;
  protected ColorScheme colorScheme;
  protected Development development;
  protected ArrayList<NodeImage> nodeList = new ArrayList<NodeImage>();
  protected ArrayList<Trail> trailList = new ArrayList<Trail>();
  protected int dimension;
  
  private static final double movement_seconds_per_rotation_ = 4.0;
  
  public DevelopmentView(Development development, ColorScheme colorScheme, double radius) {
    this.development = development;
    this.colorScheme = colorScheme;
    
    scene = this.getPlugin(Scene.class);

    sgcDevelopment.addChild(sgcObjects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());
    sgcRoot.addChild(sgcDevelopment);
    sgcRoot.addTool(new ManifoldMovementTool());
  }
  
  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    updateGeometry();
  }
  
  public void setDrawEdges(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.EDGE_DRAW, value);
  }
  
  public void setDrawFaces(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.FACE_DRAW, value);
  }
  
  protected void setObjectsSGC() {
    sgcDevelopment.removeChild(sgcObjects);
    sgcObjects = new SceneGraphComponent();
    synchronized(nodeList) {
      for(NodeImage n : nodeList) {
        sgcObjects.addChild(SGCMethods.sgcFromNode(n, dimension));
      }
    }
    synchronized(trailList) {
      for(Trail t : trailList) {
        sgcObjects.addChild(SGCMethods.sgcFromTrail(t));
      }
    }
    sgcDevelopment.addChild(sgcObjects);
  }
  
  protected abstract void updateGeometry();
  
  public void addCircles(){
    for (Vertex v: Triangulation.vertexTable.values()){
      PointSetFactory psf = new PointSetFactory();
      double[][] verts = circle(20,Radius.valueAt(v));
      psf.setVertexCount( verts.length );
      psf.setVertexCoordinates( verts );
     
      psf.update();
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(psf.getGeometry());
      this.sgcRoot.addChild(sgc);
      this.setContent(sgc);
    }
    updateGeometry();
    
  }
  
  public static double[][] circle(int n, double r) {
    double[][] verts = new double[n][3];
    double dphi = 2.0*Math.PI/n;
    for (int i=0; i<n; i++) {
      verts[i][0]=r*Math.cos(i*dphi);
      verts[i][1]=r*Math.sin(i*dphi);
    }
    return verts;
  }
  
  @Override
  public void update(Observable dev, Object arg) {
    development = (Development) dev;
    String whatChanged = (String) arg;
    
    if(whatChanged.equals("objects")) {
      synchronized(nodeList) {
        nodeList.clear();
      }
      synchronized(trailList) {
        trailList.clear();
      }
      collectObjects(development.getRoot());
      setObjectsSGC();

    } else {
      updateGeometry();
    }
    if (whatChanged.equals("surface") || whatChanged.equals("depth")) {
      CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
          scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    }
  }
  
  private void collectObjects(DevelopmentNode node) {
    if(dimension < 3 || !node.isRoot()) {
      synchronized(nodeList) {
        nodeList.addAll(node.getObjects());
      }
    }
    if(dimension < 3) {
      synchronized(trailList) {
        trailList.addAll(node.getTrails());
      }
    }

    synchronized(node.getChildren()) {
      for(DevelopmentNode child : node.getChildren()) 
        collectObjects(child);
    }
  }
  
  //TOOL(S) FOR MOVEMENT
  //==============================
  class ManifoldMovementTool extends AbstractTool {
    
    private long time; 
    private final double radians_per_millisecond = Math.PI/(movement_seconds_per_rotation_*500);
    
    public ManifoldMovementTool() {
      super(InputSlot.getDevice("ForwardBackwardAxis"), InputSlot.getDevice("LeftRightAxis")); //'activate' tool on F/B or L/R
      addCurrentSlot(InputSlot.SYSTEM_TIME); //'perform' tool on tick
    }
    
    @Override
    public void activate(ToolContext tc) {
      time = tc.getTime(); //set initial time
    }
    
    @Override
    public void perform(ToolContext tc) {
      
      //get axis state
      AxisState as_fb = tc.getAxisState(InputSlot.getDevice("ForwardBackwardAxis"));
      AxisState as_lr = tc.getAxisState(InputSlot.getDevice("LeftRightAxis"));
      
      //get dt and update time
      long newtime = tc.getTime();
      long dt = newtime - time;

      development.building = true;
      //move forward/backward
      if(as_fb.isPressed()){
        development.translateSourcePoint(-dt * as_fb.doubleValue() * 0.1);
      }
      
      //rotation
      if(as_lr.isPressed()){      
        development.rotate(-dt * radians_per_millisecond * as_lr.doubleValue());
      }
      development.building = false;
      //development.moveObjects(dt);
      time = tc.getTime();
    }  
  };

}
