package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import objects.ShootingGame;
import view.SGCMethods.DevelopmentGeometrySim3D;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
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
import development.DevelopmentNode;
import development.TimingStatistics;
import development.Vector;

public class DevelopmentViewCave extends DevelopmentView {
  
  //static settings
  private static final double REDEVELOPMENT_TRESHHOLD = .01;
  private static final double MANIFOLD_UNITS_PER_AMBIENT_UNIT = 0.5;
  private static final double AVATAR_HEIGHT = 1.7;//1.2;
  private static final InputSlot SHOOT_TOOL_ACTIVATION_SLOT = InputSlot.LEFT_BUTTON; //InputSlot.POINTER_HIT;
  private static final double CLIP_NEAR_RADIUS = 0.1; //TODO: re-implement this
  
  //debug settings
  private static final boolean PRINT_TRANSFORMATION_DATA = false;

  //other settings
  private static int INITIAL_HEIGHT = 30;
  private double height = INITIAL_HEIGHT/100.0;

  //private static Color[] colors = { Color.green, Color.yellow, Color.pink, Color.cyan, Color.orange };
  //private static int colorIndex = 0;
  
  private SceneGraphComponent sgcLight = new SceneGraphComponent();
  
  //'forward' is in the basis with (1,0) ~ Development.direction and (0,1) ~ Development.left
  private Vector forward = new Vector(1,0);  
  
  public DevelopmentViewCave(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, false);
    dimension = 3;
    
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

    //start up Viewer with VR support
    //this.addBasicUI(); //scene graph inspector causes deadlock (?)
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
        development.translateSourcePoint(-MANIFOLD_UNITS_PER_AMBIENT_UNIT*dtrans[2],-MANIFOLD_UNITS_PER_AMBIENT_UNIT*dtrans[0]);
        //changing source point automatically rebuilds tree via development.setSourcePoint
        //set 'old' values
        oldtrans[0] = trans[0]; oldtrans[1] = trans[1]; oldtrans[2] = trans[2];
      }
    }
  }

  /*//this function allows one to apply an 2D AffineTransformation to a JReality object like an SGC
  public static de.jreality.math.Matrix getJRealityMatrixFromAffineTrans(AffineTransformation affineTrans){

    //our affineTrans applies the upper 2x2 to [x,y] and adds the last column; z is affine coord
    //this should apply the same 2x2 to [x,y], leave z alone, and add the last column; w is affine coord
    return new de.jreality.math.Matrix(new double[]{
        affineTrans.getEntry(0,0), affineTrans.getEntry(0,1), 0, affineTrans.getEntry(0,2),
        affineTrans.getEntry(1,0), affineTrans.getEntry(1,1), 0, affineTrans.getEntry(1,2),
        0,0,1,0,
        0,0,0,1
    });
  }*/
  
  protected void initializeNewManifold(){ }

  protected void generateManifoldGeometry(){
    
    /*TODO (TIMING)*/ long taskID = TimingStatistics.startTask(TASK_GET_DEVELOPMENT_GEOMETRY);

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
    
    /*TODO (TIMING)*/ TimingStatistics.endTask(taskID);
  }

  /*
   * Recursively adds geometry for each face in tree to a DevelopmentGeometrySim3D, 
   * and adds nodes to nodeList (should be empty at start)
   */
  private void generateManifoldGeometry(DevelopmentNode devNode,
      ArrayList<Color> colors, DevelopmentGeometrySim3D geometry) {

    //double[][] face = devNode.getEmbeddedFace().getVectorsAsArray();
    double[][] clippedFace = devNode.getClippedFace().getVectorsAsArray();
    geometry.addFace(clippedFace, height);
        
    // (adding two faces at a time)
    colors.add(colorScheme.getColor(devNode));
    colors.add(colorScheme.getColor(devNode));

    Iterator<DevelopmentNode> itr = devNode.getChildren().iterator();
    while (itr.hasNext()) {
      generateManifoldGeometry(itr.next(), colors, geometry);
    }
  }
  
  protected void generateObjectGeometry(){
    
    /*TODO (TIMING)*/ long taskID = TimingStatistics.startTask(TASK_GET_OBJECT_GEOMETRY);
    SceneGraphComponent sgcNewObjects = CommonViewMethods.generateDevelopmentObjectGeometry(development.getRoot(), true, CLIP_NEAR_RADIUS);
    sgcDevelopment.removeChild(sgcObjects);
    sgcObjects = sgcNewObjects;
    sgcDevelopment.addChild(sgcObjects);
    /*TODO (TIMING)*/ TimingStatistics.endTask(taskID);
  }
  
  // ================== Shooting Tool ==================
  
  public void installShootTool(ShootingGame shootingGame){
    sgcRoot.addTool(new ShootTool(shootingGame));
  }
  
  private class ShootTool extends AbstractTool {
    private ShootingGame shootingGame;

    public ShootTool(ShootingGame shootingGame) {
      super(SHOOT_TOOL_ACTIVATION_SLOT);
      this.shootingGame = shootingGame;
    }
   
    @Override
    public void activate(ToolContext tc) {

      Vector movement = development.getSource().getDirection(forward.getComponent(0),forward.getComponent(1));
      shootingGame.addBullet(development.getSource(),movement);
    }
    @Override
    public void deactivate(ToolContext tc) { } 
    @Override
    public void perform(ToolContext tc) { }
  }

}