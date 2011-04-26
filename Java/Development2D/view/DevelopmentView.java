package view;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.AxisState;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.Tool;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.CommonAttributes;
import development.Development;
import development.TimingStatistics;

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

public abstract class DevelopmentView extends JRViewer{
  
  //events to be observed
  public static final Integer EVENT_REBUILD = new Integer(0); //development tree was rebuilt
  
  /*TODO (Timing)*/ protected static final int TASK_GET_DEVELOPMENT_GEOMETRY = TimingStatistics.generateTaskTypeID("Generate Development Geometry");
  /*TODO (Timing)*/ protected static final int TASK_GET_OBJECT_GEOMETRY = TimingStatistics.generateTaskTypeID("Generate Object Geometry");
  
  protected SceneGraphComponent sgcRoot = new SceneGraphComponent("Root");
  protected SceneGraphComponent sgcDevelopment = new SceneGraphComponent("Development");
  protected SceneGraphComponent sgcObjects = new SceneGraphComponent("Objects");
  protected Scene scene;
  protected ColorScheme colorScheme;
  protected Development development;
  protected int dimension;
  
  private static final double movement_seconds_per_rotation_ = 4.0;
  private double units_per_millisecond = 0.004;
  
  public DevelopmentView(Development development, ColorScheme colorScheme, boolean useMovementTool) {
    this.development = development;
    this.colorScheme = colorScheme;
    
    scene = this.getPlugin(Scene.class);

    sgcDevelopment.addChild(sgcObjects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());
    sgcRoot.addChild(sgcDevelopment);
    
    if(useMovementTool){ 
      sgcRoot.addTool(new ManifoldMovementToolFB()); 
      sgcRoot.addTool(new ManifoldMovementToolLR());
    }
    //add whatever extra tools are specified
  }
  
  public void addTool(Tool tool){ sgcRoot.addTool(tool); }
  
  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    updateGeometry(true,false);
  }
  
  public void setDrawEdges(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.EDGE_DRAW, value);
  }
  
  public void setDrawFaces(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.FACE_DRAW, value);
  }
  
  protected abstract void generateManifoldGeometry();
  protected abstract void generateObjectGeometry();
  
  //used after new geometry has been generated, can put camera encompass here for example
  protected abstract void initializeNewManifold(); 
  
  protected void updateGeometry(boolean dev, boolean obj){
    if(dev){ generateManifoldGeometry(); }
    if(obj){ generateObjectGeometry(); }
  }
  
  /*public void addCircles(){
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
  }*/
  
  //TOOL(S) FOR MOVEMENT
  //==============================
  class ManifoldMovementToolFB extends AbstractTool {
    
    private long time; 
    
    public ManifoldMovementToolFB() {
      super(InputSlot.getDevice("ForwardBackwardAxis")); //'activate' tool on F/B or L/R
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
      
      //get dt and update time
      long newtime = tc.getTime();
      long dt = newtime - time;

      //move forward/backward
      if(as_fb.isPressed()){
        development.translateSourcePoint(-dt * as_fb.doubleValue() * units_per_millisecond, 0);
      }

      time = tc.getTime();
    }  
  };
  
  class ManifoldMovementToolLR extends AbstractTool {
    
    private long time; 
    private final double radians_per_millisecond = Math.PI/(movement_seconds_per_rotation_*500);
    
    public ManifoldMovementToolLR() {
      super(InputSlot.getDevice("LeftRightAxis")); //'activate' tool on F/B or L/R
      addCurrentSlot(InputSlot.SYSTEM_TIME); //'perform' tool on tick
    }
    
    @Override
    public void activate(ToolContext tc) {
      time = tc.getTime(); //set initial time
    }
    
    @Override
    public void perform(ToolContext tc) {
      
      //get axis state
      AxisState as_lr = tc.getAxisState(InputSlot.getDevice("LeftRightAxis"));
      
      //get dt and update time
      long newtime = tc.getTime();
      long dt = newtime - time;

      //rotation
      if(as_lr.isPressed()){ 
        development.rotate(-dt * radians_per_millisecond * as_lr.doubleValue());
      }

      time = tc.getTime();
    }  
  };

}
