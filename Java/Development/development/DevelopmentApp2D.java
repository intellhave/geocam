package development;

import geoquant.*;
import inputOutput.TriangulationIO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.StereoViewer;
import de.jreality.scene.Viewer;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.AxisState;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

import triangulation.*;
import util.Matrix;


public class DevelopmentApp2D {
  
  //position info
  //------------------------
  private static Face source_face_;
  private static Vector source_point_;
  
  //camera stuff
  //------------------------
  private static Viewer viewer_;
  private static Vector camera_forward_;
  private static Vector camera_right_;
  private static SceneGraphPath camera_source_;
  private static SceneGraphPath camera_free_;
  private static SceneGraphComponent sgc_camera_;
  
  //options
  //------------------------
  //  appearance of faces
  private static boolean show_faces_ = true;
  private static HashMap<Face,Color> color_scheme_;
  //  objects
  private static double object_radius_ = 0.04;
  //  showing embedded mfld
  private static boolean show_embedded_ = false;
  //  simulated 3d mode
  private static boolean simulate_3D_ = true;
  private static boolean simulate_3D_is_default_ = true;
  private static double simulated_3D_height_ = 0.08;
  //  development options
  private static final int max_max_recursion_depth_ = 200; //max for slider
  private static int max_recursion_depth_ = 5;
  private static final int max_bounding_square_side_length_ = 15; //max for slider
  private static int bounding_square_side_length_ = 3;
  //  movement options
  private static final double movement_units_per_second_ = 0.4;
  private static final double movement_seconds_per_rotation_ = 2.0;
  
  //data
  //------------------------
  private static Scene scene_;
  private static JRViewer jrv_embedded_;
  private static SceneGraphComponent sgc_root_;
  private static SceneGraphComponent sgc_devmap_;
  
  //USER INTERFACE
  //==============================
  static class UIPanel_Options extends ViewShrinkPanelPlugin {

    TitledBorder border_depth = BorderFactory.createTitledBorder("");
    TitledBorder border_bounds = BorderFactory.createTitledBorder("");
    
    private void makeUIComponents() {
      
      //slider for recursion depth
      JSlider slider_depth = new JSlider(1,max_max_recursion_depth_,max_recursion_depth_);
      slider_depth.setMaximumSize(new Dimension(400,100));
      slider_depth.setAlignmentX(0.0f);
      border_depth.setTitle(String.format("Max Recursion Depth (%d)",max_recursion_depth_));
      slider_depth.setBorder(border_depth);
      slider_depth.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          int val = ((JSlider)e.getSource()).getValue();
          max_recursion_depth_ = val;
          border_depth.setTitle(String.format("Max Recursion Depth (%d)",max_recursion_depth_));
          computeDevelopmentMap();
        }
      });
      shrinkPanel.add(slider_depth);
      
      //slider for bounding box size
      JSlider slider_bounds = new JSlider(1,max_bounding_square_side_length_,bounding_square_side_length_);
      slider_bounds.setMaximumSize(new Dimension(400,100));
      slider_bounds.setAlignmentX(0.0f);
      border_bounds.setTitle(String.format("Bounding Box Size (%d)",bounding_square_side_length_));
      slider_bounds.setBorder(border_bounds);
      slider_bounds.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          int val = ((JSlider)e.getSource()).getValue();
          bounding_square_side_length_ = val;
          border_bounds.setTitle(String.format("Bounding Box Size (%d)",bounding_square_side_length_));
          computeDevelopmentMap();
        }
      });
      shrinkPanel.add(slider_bounds);
      
      //checkbox for simulating 3d
      JCheckBox check_sim3d = new JCheckBox("Simulate 3D");
      check_sim3d.setSelected(simulate_3D_is_default_);
      check_sim3d.setAlignmentX(0.0f);
      check_sim3d.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean val = ((JCheckBox)e.getSource()).isSelected();
          //change accordingly
          simulate_3D_ = val;
          if(simulate_3D_){ viewer_.setCameraPath(camera_source_); }
          else{ viewer_.setCameraPath(camera_free_); }
          computeDevelopmentMap();
          viewer_.render();
        }
      });
      shrinkPanel.add(check_sim3d);
      
      //checkbox for showing embedded
      JCheckBox check_showembedded = new JCheckBox("Show Embedded Manifold");
      check_showembedded.setSelected(false);
      check_showembedded.setAlignmentX(0.0f);
      check_showembedded.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean val = ((JCheckBox)e.getSource()).isSelected();
          setEmbeddedVisible(val);
        }
      });
      shrinkPanel.add(check_showembedded);
      
      //checkbox for showing faces
      JCheckBox check_showfaces = new JCheckBox("Show Faces");
      check_showfaces.setSelected(false);
      check_showfaces.setAlignmentX(0.0f);
      check_showfaces.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean val = ((JCheckBox)e.getSource()).isSelected();
          show_faces_ = val;
          computeDevelopmentMap();
        }
      });
      shrinkPanel.add(check_showfaces);
      
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
      PluginInfo info = new PluginInfo("Options", "");
      return info;
    }
  };
  
  
  //TOOL(S) FOR MOVEMENT
  //==============================
  static class ManifoldMovementTool extends AbstractTool {
    
    private static long time; 
    private static final double units_per_millisecond = movement_units_per_second_/1000;
    private static final double radians_per_millisecond = Math.PI/(movement_seconds_per_rotation_*500);
    
    private static final InputSlot FORWARD_BACKWARD = InputSlot.getDevice("ForwardBackwardAxis");
    private static final InputSlot LEFT_RIGHT = InputSlot.getDevice("LeftRightAxis");
    private static final InputSlot SYSTEM_TIMER = InputSlot.SYSTEM_TIME;  
    
    public ManifoldMovementTool() {
      super(FORWARD_BACKWARD, LEFT_RIGHT); //'activate' tool on F/B or L/R
      addCurrentSlot(SYSTEM_TIMER); //'perform' tool on tick
    }
    
    @Override
    public void activate(ToolContext tc) {
      time = tc.getTime(); //set initial time
    }
    
    @Override
    public void perform(ToolContext tc) {
      
      //get axis state
      AxisState as_fb = tc.getAxisState(FORWARD_BACKWARD);
      AxisState as_lr = tc.getAxisState(LEFT_RIGHT);
      
      //get dt and update time
      long newtime = tc.getTime();
      long dt = newtime - time;
      time = newtime;

      //move forward/backward
      if(as_fb.isPressed()){
        
        Vector vect_translate = new Vector(camera_forward_);
        vect_translate.scale(dt * units_per_millisecond * as_fb.doubleValue());
        source_point_.add(vect_translate);
        
        computeDevelopmentMap();
      }
      
      //rotation
      if(as_lr.isPressed()){
        
        double dtheta = -dt * radians_per_millisecond * as_lr.doubleValue();
        double cdtheta = Math.cos(dtheta);
        double sdtheta = Math.sin(dtheta);
        double oldx = camera_forward_.getComponent(0);
        double oldy = camera_forward_.getComponent(1);
        camera_forward_ = new Vector(cdtheta*oldx - sdtheta*oldy, sdtheta*oldx + cdtheta*oldy );
      
        updateCamera();
      }
      
    }  
  };

  //MAIN
  //==============================
  public static void main(String[] args){
    
    EmbeddedTriangulation.readEmbeddedSurface("models/cone.off");
    //TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/octahedron.xml");
    
    Iterator<Integer> i = null;
    
    //set edge lengths randomly from [2,3)
    /*i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Edge e = Triangulation.edgeTable.get(key);
      double rand = Math.random();
      Length.at(e).setValue(2); //random return value is in [0,1)
    }*/
    
    //print some debug data
    //EmbeddedTriangulation.printTriangulationData();
    //EmbeddedTriangulation.printCoordinateData();
    //EmbeddedTriangulation.printGeometricData();
    
    //pick some arbitrary face and source point
    i = Triangulation.faceTable.keySet().iterator();
    source_face_ = Triangulation.faceTable.get(i.next());
    
    source_point_ = new Vector(0,0);
    Iterator<Vertex> iv = source_face_.getLocalVertices().iterator();
    while(iv.hasNext()){
      source_point_.add(Coord2D.coordAt(iv.next(), source_face_));
    }
    source_point_.scale(1.0f/3.0f);
    
    //make color scheme
    color_scheme_ = new HashMap<Face, Color>();
    i = Triangulation.faceTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Face f = Triangulation.faceTable.get(key);
      //determine appearance
      Color color = Color.getHSBColor((float)f.getIndex()/(float)Triangulation.faceTable.size(), 0.5f, 0.9f);
      if(f == source_face_){ color = Color.WHITE; }
      color_scheme_.put(f, color);
    }
    
    //set up sgc_root
    sgc_root_ = new SceneGraphComponent();
    sgc_root_.addTool(new ManifoldMovementTool());
    //sgc_root_.addChild(sgc_origin);

    //jrviewer(s)
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root_);
    //jrv.registerPlugin(new ContentAppearance());
    //jrv.registerPlugin(new ContentLoader());
    //jrv.registerPlugin(new ContentTools());
    jrv.registerPlugin(new UIPanel_Options());
    jrv.setShowPanelSlots(true,false,false,false);
    scene_ = jrv.getPlugin(Scene.class);
    jrv.startup();
    
    //compute geometry
    computeDevelopmentMap();
    
    //make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);
//  camera.setStereo(true);
    camera_forward_ = new Vector(1,0);
    
    sgc_camera_ = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgc_root_.addChild(sgc_camera_);
    sgc_camera_.setCamera(camera);
    updateCamera();
    
    //make jrviewer use the camera we set up
    viewer_ = jrv.getViewer();
 //   viewer_.setStereoType(2);
    camera_free_ = viewer_.getCameraPath();
    camera_source_ = SceneGraphUtility.getPathsBetween(viewer_.getSceneRoot(), sgc_camera_).get(0);
    camera_source_.push(sgc_camera_.getCamera());
    
    if(simulate_3D_is_default_){ viewer_.setCameraPath(camera_source_); }
    else{ viewer_.setCameraPath(camera_free_); }
  }
  
  private static void updateCamera(){
    
    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        camera_forward_.getComponent(1),0,camera_forward_.getComponent(0),0,
        -camera_forward_.getComponent(0),0,camera_forward_.getComponent(1),0,
        0,1,0,0,
        0,0,0,1
    );
    M.assignTo(sgc_camera_);
  }
  
  public static void setEmbeddedVisible(boolean val){
    
    if((val == true) && (jrv_embedded_ == null) && (EmbeddedTriangulation.isEmbedded)){
      
      //make jrviewer
      jrv_embedded_ = new JRViewer();
      jrv_embedded_.addBasicUI();
      jrv_embedded_.registerPlugin(new ContentTools());
      //set manifold contents
      SceneGraphComponent sgc_embedded = EmbeddedTriangulation.getSGC(color_scheme_);
      sgc_embedded.setAppearance(getFaceAppearance(0.6f));
      jrv_embedded_.setContent(sgc_embedded);
      //set source point contents
      ArrayList<Vector> sourcept = new ArrayList<Vector>();
      sourcept.add(EmbeddedTriangulation.getCoord3D(source_face_, source_point_));
      sgc_embedded.addChild(sgcFromPoints(sourcept));
      //start the viewer
      jrv_embedded_.startup();
    }
    
    if((val == false) && (jrv_embedded_ != null)){ 
      jrv_embedded_.dispose(); 
      jrv_embedded_ = null;
    }

  }
  
  //ALGORITHM
  //==============================
  private static void computeDevelopmentMap(){

    //rotation matrix sending dir -> (0,1), rot_cw_pi/2(dir) -> (1,0)
    //double x = source_direction_.getComponent(0);
    //double y = source_direction_.getComponent(1);
    //Matrix M = new Matrix(new double[][]{ new double[] {y,-x}, new double[] {x,y} });
    AffineTransformation T = new AffineTransformation(Vector.scale(source_point_,-1));
    //T.leftMultiply(new AffineTransformation(M));
    
    sgc_root_.removeChild(sgc_devmap_);
    sgc_devmap_ = new SceneGraphComponent();
    
    ArrayList<Vector> source_images = new ArrayList<Vector>();
    //long t = System.currentTimeMillis();
    iterateDevelopment(source_images, sgc_devmap_,0,source_face_,null,null,T);
    //System.out.printf("Time to calculate: %d ms\n", System.currentTimeMillis() - t);
    
    if(!source_images.isEmpty()){
      sgc_devmap_.addChild(sgcFromPoints(source_images));
    }
    sgc_root_.addChild(sgc_devmap_);
    
    CameraUtility.encompass(scene_.getAvatarPath(), scene_.getContentPath(), scene_.getCameraPath(), 1.75, Pn.EUCLIDEAN);
  
  }
  
  //struct holding info necessary to develop off an edge
  private static class DevelopmentEdgeInfo{
    public Vector vect0_,vect1_;
    public Vertex vert0_,vert1_;
    Edge next_source_edge_;
    Face next_face_;

    public DevelopmentEdgeInfo(Vertex vert0, Vertex vert1, Vector vect0, Vector vect1, Face next_face, Edge next_source_edge){
      vect0_ = vect0; vect1_ = vect1;
      vert0_ = vert0; vert1_ = vert1;
      next_source_edge_ = next_source_edge;
      next_face_ = next_face;
    }
  };
  
  public static ArrayList<DevelopmentEdgeInfo> getDevelopmentEdgeInfo(Face cur_face, Edge source_edge, AffineTransformation T){
    
    //gets collection of DevelopmentEdgeInfos, which give the info necessary to
    //develop an additional information out of a given face
    
    //get verts
    Vertex[] vert = new Vertex[3];
    Iterator<Vertex> iv = cur_face.getLocalVertices().iterator();
    for(int i=0; i<3; i++){ vert[i] = iv.next(); }
    
    //apply T to coordinates
    Vector[] vect = new Vector[3];
    for(int i=0; i<3; i++){
      try { vect[i] = T.affineTransPoint(Coord2D.coordAt(vert[i], cur_face)); }
      catch (Exception e) { e.printStackTrace(); }
    }
    
    //see if CCW
    Vector u = Vector.subtract(vect[1], vect[0]);
    Vector v = Vector.subtract(vect[2], vect[0]);
    double z = u.getComponent(0)*v.getComponent(1) - u.getComponent(1)*v.getComponent(0);
    if(z < 0){ //flip orientation
      Vertex tempvert = vert[2];
      Vector tempvect = vect[2];
      vert[2] = vert[1];
      vect[2] = vect[1];
      vert[1] = tempvert;
      vect[1] = tempvect;
    }
    
    //get edges
    ArrayList<DevelopmentEdgeInfo> retinfo = new ArrayList<DevelopmentEdgeInfo>();
    
    Iterator<Edge> ie = cur_face.getLocalEdges().iterator();
    while(ie.hasNext()){
      Edge e = ie.next();
      //don't do anything with source_edge
      if(e == source_edge){ continue; }
      
      //find incident face
      Iterator<Face> eif = e.getLocalFaces().iterator();
      Face next_face = eif.next();
      if(next_face == cur_face){ next_face = eif.next(); }
      
      //see which verts this edge belongs to
      Iterator<Vertex> eiv = e.getLocalVertices().iterator();
      Vertex v0 = eiv.next();
      Vertex v1 = eiv.next();
      
      //see which vertices these are in terms of the CCW ones above
      int n = 0;
      if((vert[0] == v0) && (vert[1] == v1)){ n=0; }
      else if((vert[0] == v1) && (vert[1] == v0)){ n=0; }
      else if((vert[1] == v0) && (vert[2] == v1)){ n=1; }
      else if((vert[1] == v1) && (vert[2] == v0)){ n=1; }
      else if((vert[2] == v0) && (vert[0] == v1)){ n=2; }
      else if((vert[2] == v1) && (vert[0] == v0)){ n=2; }
      retinfo.add(new DevelopmentEdgeInfo(vert[n],vert[(n+1)%3],vect[n],vect[(n+1)%3],next_face,e));
    }
    
    return retinfo;
  }
  
  private static boolean isOutsideBoundingBox(ArrayList<Vector> ptlist){
    
    double min_x = -bounding_square_side_length_;
    double max_x = bounding_square_side_length_;
    double min_y = -bounding_square_side_length_;
    double max_y = bounding_square_side_length_;
    
    boolean outside_x_max = true;
    boolean outside_x_min = true;
    boolean outside_y_max = true;
    boolean outside_y_min = true;
    
    Iterator<Vector> iv = ptlist.iterator();
    while(iv.hasNext()){
      Vector v = iv.next();
      if(v.getComponent(0) <= max_x){ outside_x_max = false; }
      if(v.getComponent(0) >= min_x){ outside_x_min = false; }
      if(v.getComponent(1) <= max_y){ outside_y_max = false; }
      if(v.getComponent(1) >= min_y){ outside_y_min = false; }
    }
    
    return (outside_x_max || outside_x_min || outside_y_max || outside_y_min);
  }
  
  public static void iterateDevelopment(ArrayList<Vector> source_images, SceneGraphComponent sgc_devmap, int depth, Face cur_face, Edge source_edge, Frustum2D current_frustum, AffineTransformation current_trans){
    
    //note: source_face and current_frustum may be null
    
    //get new affine transformation
    AffineTransformation new_trans = new AffineTransformation(2);
    if(source_edge != null){
      AffineTransformation coord_trans = CoordTrans2D.affineTransAt(cur_face, source_edge);
      new_trans.leftMultiply(coord_trans);
    }
    new_trans.leftMultiply(current_trans);
    
    //get transformed points from cur_face
    ArrayList<Vector> efpts = new ArrayList<Vector>();
    
    Iterator<Vertex> i = cur_face.getLocalVertices().iterator();
    while(i.hasNext()){
      Vertex vert = i.next();
      Vector pt = Coord2D.coordAt(vert, cur_face);
      try{ efpts.add(new_trans.affineTransPoint(pt)); }
      catch(Exception e1){ e1.printStackTrace(); }
    }
    
    //make sure we are within bounding box
    if(isOutsideBoundingBox(efpts)){ return; }
    
    //add source images, if any
    if(cur_face == source_face_){

      Vector source_image = null;
      try { source_image = new_trans.affineTransPoint(source_point_); }
      catch (Exception e1) { e1.printStackTrace(); }
      
      boolean isVisible = true;
      if(current_frustum != null){
        isVisible = current_frustum.checkInterior(source_image);
      }
      
      if(isVisible && ((depth > 0) || !simulate_3D_)){ source_images.add(source_image); }
    }
    
    if(show_faces_ || (depth == 0)){
      //make clipped embeddedface
      EmbeddedFace origface = new EmbeddedFace(efpts);
      EmbeddedFace clipface = null;
      if(current_frustum != null){ clipface = current_frustum.clipFace(origface); }
      else{ clipface = origface; }
      
      //quit if face is completely obscured
      if(clipface == null){ return; }
      
      //add clipped face to display
      SceneGraphComponent sgc_new_face = new SceneGraphComponent();
      Color color = color_scheme_.get(cur_face);
      if(simulate_3D_){
        sgc_new_face.setGeometry(clipface.getGeometry3D(color,simulated_3D_height_));
        sgc_new_face.setAppearance(getFaceAppearance(0.5f));
      }else{
        sgc_new_face.setGeometry(clipface.getGeometry(color));
        sgc_new_face.setAppearance(getFaceAppearance(0.5f));
      }
      sgc_devmap.addChild(sgc_new_face);
    }
    
    //see which faces to continue developing on, if any
    if(depth >= max_recursion_depth_){ return; }
    
    ArrayList<DevelopmentEdgeInfo> deInfoList = getDevelopmentEdgeInfo(cur_face, source_edge, new_trans);
    for(int j=0; j<deInfoList.size(); j++){
      DevelopmentEdgeInfo deInfo = deInfoList.get(j);
      
      //intersect frustums
      Frustum2D frust = new Frustum2D(deInfo.vect1_,deInfo.vect0_);
      Frustum2D new_frust = null;
      if(current_frustum == null){ new_frust = frust; }
      else{ new_frust = Frustum2D.intersect(frust, current_frustum); }
      if(new_frust == null){ continue; }
      
      //iterate
      iterateDevelopment(source_images, sgc_devmap, depth+1, deInfo.next_face_, deInfo.next_source_edge_, new_frust, new_trans);
    }
    
  }
  
  //AUXILLIARY METHODS
  //==============================
  public static Appearance getFaceAppearance(double transparency){
    
    //create appearance for developed faces
    Appearance app_face = new Appearance();
    
    //set some basic attributes
    app_face.setAttribute(CommonAttributes.FACE_DRAW, true);
    app_face.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_face.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_face.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_face.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    
    //set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app_face, true);
    
    //line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setLineWidth(0.0);
    dls.setDiffuseColor(Color.BLACK);
    
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(Color.WHITE);
    dps.setTransparency(transparency);

    return app_face;
  }

  /*public static Appearance getFaceAppearance3D(){
    
    //create appearance for developed faces
    Appearance app_face = new Appearance();
    
    //set some basic attributes
    app_face.setAttribute(CommonAttributes.FACE_DRAW, false);
    app_face.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_face.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_face.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_face.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    
    //set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app_face, true);
    
    //line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setLineWidth(0.0);
    dls.setDiffuseColor(Color.BLACK);
  
    return app_face;
  }*/
  
  public static SceneGraphComponent sgcFromPoints(ArrayList<Vector> points){
    
    //create the sgc
    SceneGraphComponent sgc_points = new SceneGraphComponent();

    //create appearance
    Appearance app_points = new Appearance();
    
    //set some basic attributes
    app_points.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app_points.setAttribute(CommonAttributes.LIGHTING_ENABLED, true);
    
    //set point shader
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(object_radius_);
    dps.setDiffuseColor(Color.BLUE);
    
    //set appearance
    sgc_points.setAppearance(app_points);
    
    //set vertlist
    double[][] vertlist = new double[points.size()][3];
    for(int i=0; i<points.size(); i++){
      Vector vect = points.get(i);
      if(vect.getDimension() == 2){
        vertlist[i] = new double[]{ vect.getComponent(0), vect.getComponent(1), 0 };
      }else if(vect.getDimension() == 3){
        vertlist[i] = new double[]{ vect.getComponent(0), vect.getComponent(1), vect.getComponent(2) };
      }
    }
    
    //create geometry with pointsetfactory
    PointSetFactory psf = new PointSetFactory();
    psf.setVertexCount(points.size());
    psf.setVertexCoordinates(vertlist);
    psf.update();
    
    //set geometry
    sgc_points.setGeometry(psf.getGeometry());
    
    //return
    return sgc_points;
  }

}
