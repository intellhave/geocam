package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import view.SGCMethods.DevelopmentGeometrySim3D;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.Primitives;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.PointLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import de.jreality.scene.event.TransformationEvent;
import de.jreality.scene.event.TransformationListener;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.CommonAttributes;

import development.Development;
import development.TimingStatistics;
import development.Development.DevelopmentNode;
import development.Vector;

//minor changes to DevelopmentView needed:
//
// 1) should not use ManifoldMovementTool for this viewer (?)
//    so, should put an option in DevelopmentView constructor bool useMMT
//    cavetransformer should handle the movement
//
// 2) need method in DevelopmentView to translate source point; see CaveTransformer

public class DevelopmentViewCave extends DevelopmentView {
  
  //static settings
  private static final double REDEVELOPMENT_TRESHHOLD = .01;
  private static final double MANIFOLD_UNITS_PER_AMBIENT_UNIT = 0.2;
  private static final double AVATAR_HEIGHT = 1.7;//1.2;
  private static final boolean USE_MANIFOLD_MOVEMENT_TOOL = false;
  private static final boolean USE_SHOOT_TOOL = false;
  //debug settings
  private static final boolean PRINT_TRANSFORMATION_DATA = false;
  private static final boolean PRINT_SHOOT_TOOL_DATA = false;
  /*TODO (Timing)*/ private static final int TASK_GET_GEOMETRY = TimingStatistics.generateTaskTypeID("DevelopmentViewCave.getGeometry");
  
  //other settings
  private static int INITIAL_HEIGHT = 30;
  private double height = INITIAL_HEIGHT/100.0;

  private static Color[] colors = { Color.green, Color.yellow, Color.pink, Color.cyan, Color.orange };
  private static int colorIndex = 0;
  
  private SceneGraphComponent sgcLight = new SceneGraphComponent();
  
  //'forward' is in the basis with (1,0) ~ Development.direction and (0,1) ~ Development.left
  private Vector forward = new Vector(1,0);  

  public DevelopmentViewCave(Development development, ColorScheme colorScheme, double radius) {
    super(development, colorScheme, radius, USE_MANIFOLD_MOVEMENT_TOOL);
    dimension = 3;

    updateGeometry();
    
    //rotate to correct orientation of development
    Matrix M = MatrixBuilder.euclidean().getMatrix();
    M.multiplyOnLeft(MatrixBuilder.euclidean().rotateX(-1.5708).getMatrix());
    M.multiplyOnLeft(MatrixBuilder.euclidean().rotateY(1.5708).getMatrix());
    M.assignTo(sgcDevelopment);
   
    //create light
    //sgcLight.setTransformation(new Transformation(MatrixBuilder.euclidean().getArray()));
    PointLight pLight = new PointLight();
    pLight.setIntensity(1.0);
    pLight.setColor(Color.white);
    sgcLight.setLight(pLight);
    sgcRoot.addChild(sgcLight);
    
    //create identity transformation for the development sgc
    sgcRoot.setTransformation(new Transformation(MatrixBuilder.euclidean().getArray()));
    
    //set picking
    Appearance appRoot = new Appearance();
    appRoot.setAttribute(CommonAttributes.PICKABLE, true);
    sgcRoot.setAppearance(appRoot);
    
    //add shoot tool
    if(USE_SHOOT_TOOL){ sgcRoot.addTool(new ShootTool()); }
    
    //start up Viewer with VR support
    this.addBasicUI();
    this.addVRSupport();
    this.addContentSupport(ContentType.TerrainAligned);
    this.registerPlugin(new ContentAppearance());
    this.registerPlugin(new ContentLoader());
    this.registerPlugin(new ContentTools());
    this.startup();
    
    //get Scene for the viewer
    scene = this.getPlugin(Scene.class);
    
    //set up transformation listener
    SceneGraphComponent sgcAvatar = scene.getAvatarComponent();
    sgcAvatar.getTransformation().addTransformationListener(new CaveTransformer(sgcRoot));
    
    //make terrain invisible
    scene.getBackdropComponent().setVisible(false);
    
    //add content
    scene.getSceneRoot().addChild(sgcRoot);
  }
  
  private class CaveTransformer implements TransformationListener{
 
    private boolean initialized = false;
    private double[] oldtrans = new double[3];
    
    private SceneGraphComponent sgcObject; //object to stick to avatar
    
    public CaveTransformer(SceneGraphComponent _sgcObject){ 
      sgcObject = _sgcObject;
    }
    
    public void transformationMatrixChanged(TransformationEvent ev) {
      
      double[] mData = ev.getTransformationMatrix();
      
      if(PRINT_TRANSFORMATION_DATA){
        System.out.println("Transformation:");
        System.out.println("R0: [" + mData[0] + ", " + mData[1] + ", " + mData[2] + ", " + mData[3] + "]");
        System.out.println("R1: [" + mData[4] + ", " + mData[5] + ", " + mData[6] + ", " + mData[7] + "]");
        System.out.println("R1: [" + mData[8] + ", " + mData[9] + ", " + mData[10] + ", " + mData[11] + "]");
        System.out.println("R1: [" + mData[12] + ", " + mData[13] + ", " + mData[14] + ", " + mData[15] + "]");
      }
      
      //read off translation part of transformation from column 3
      double[] trans = new double[]{ mData[3], mData[7], mData[11] };
      
      //read off forward-pointing direction from x-z submatrix [for shooting]
      forward.setComponent(0,mData[0]);
      forward.setComponent(1,mData[2]);
      
      //translate sgcObject to avatar's head
      MatrixBuilder.euclidean().translate(trans[0],AVATAR_HEIGHT+trans[1],trans[2]).assignTo(sgcObject);
      
      //figure out delta(translation)
      if(!initialized){
        oldtrans[0] = trans[0]; oldtrans[1] = trans[1]; oldtrans[2] = trans[2];
        initialized = true;
        return;
      }
      
      double[] dtrans = new double[]{ trans[0]-oldtrans[0], trans[1]-oldtrans[1], trans[2]-oldtrans[2] };
      
      if( (dtrans[0]*dtrans[0] + dtrans[2]*dtrans[2]) > REDEVELOPMENT_TRESHHOLD ){
        //System.out.println("dtrans: [" + dtrans[0] + ", " + dtrans[2] + "]"); 
        //move source point
        development.building = true;
        development.translateSourcePoint(-MANIFOLD_UNITS_PER_AMBIENT_UNIT*dtrans[2],-MANIFOLD_UNITS_PER_AMBIENT_UNIT*dtrans[0]);
        //changing source point automatically rebuilds tree via development.setSourcePoint
        development.building = false;
        //set 'old' values
        oldtrans[0] = trans[0]; oldtrans[1] = trans[1]; oldtrans[2] = trans[2];
      }
    }
  }

  protected void updateGeometry() {
    synchronized(nodeList) {
      nodeList.clear();
    }
    sgcDevelopment.setGeometry(getGeometry());
    setObjectsSGC();
  }

  /*
   * Returns geometry for simulated 3D view of development.
   */
  public Geometry getGeometry() {
    
    /*TODO (Timing)*/ long taskID = TimingStatistics.startTask(TASK_GET_GEOMETRY);
    
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
    
    /*TODO (Timing)*/ TimingStatistics.endTask(taskID);
    
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
  
  private class ShootTool extends AbstractTool {
   // private Development development;
    
    public ShootTool(){ //Development development) {
      super(InputSlot.POINTER_HIT);
      //this.development = development;
    }
   
    @Override
    public void activate(ToolContext tc) {

      if(PRINT_SHOOT_TOOL_DATA){
        System.out.println("Added node!");
      }
      
      Vector movement = development.getManifoldVector(forward.getComponent(0),forward.getComponent(1));
      development.addNodeAtSource(colors[colorIndex++], movement);
      colorIndex = colorIndex % colors.length;
    }
    @Override
    public void deactivate(ToolContext tc) { } 
    @Override
    public void perform(ToolContext tc) { }
  }

}