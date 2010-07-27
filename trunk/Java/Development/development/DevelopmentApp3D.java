package development;

import geoquant.*;
import inputOutput.TriangulationIO;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.JRViewer.ContentType;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.proxy.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;

import triangulation.*;
import util.Matrix;

public class DevelopmentApp3D {
  
  //position info
  private static Tetra source_tetra_;
  private static Vector source_point_;
  //private static Vector source_direction_;
  
  //options
  private static final int max_recursion_depth_ = 3;
  //private static final double movement_units_per_second_ = 1.0;
  //private static final double movement_seconds_per_rotation_ = 2.0;
  //private static final double bounding_cube_side_length_ = 4.0;
  
  //data
  private static SceneGraphComponent sgc_root_; 
  private static SceneGraphComponent sgc_devmap_;
  
  //MAIN
  //==============================
  public static void main(String[] args){
    
    TriangulationIO.readTriangulation("Data/Triangulations/3DManifolds/pentachoron.xml");
    
    Iterator<Integer> i = null;
    
    //set edge lengths to 1
    i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Edge e = Triangulation.edgeTable.get(key);
      double rand = Math.random(); //random return value is in [0,1)
      Length.at(e).setValue(1.5+rand); 
    }
    
    //pick some arbitrary tetra and source point
    i = Triangulation.tetraTable.keySet().iterator();
    source_tetra_ = Triangulation.tetraTable.get(i.next());
    
    source_point_ = new Vector(0,0,0);
    Iterator<Vertex> iv = source_tetra_.getLocalVertices().iterator();
    while(iv.hasNext()){
      source_point_.add(Coord3D.coordAt(iv.next(), source_tetra_));
    }
    source_point_.scale(1.0f/4.0f);
    
    //set up sgc_root
    sgc_root_ = new SceneGraphComponent();
    //sgc_root_.addTool(new ManifoldMovementTool());
    //sgc_root_.addChild(sgc_origin);
    
    //compute geometry
    computeDevelopmentMap();

    //jrviewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root_);
    jrv.addVRSupport();
    jrv.addContentSupport(ContentType.TerrainAligned);
    jrv.registerPlugin(new ContentAppearance());
    jrv.registerPlugin(new ContentLoader());
    jrv.registerPlugin(new ContentTools());
    jrv.startup();
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
    
    //long t = System.currentTimeMillis();
    iterateDevelopment(sgc_devmap_,0,source_tetra_,null,null,T);
    //System.out.printf("Time to calculate: %d ms\n", System.currentTimeMillis() - t);
    
    sgc_root_.addChild(sgc_devmap_);

  }

  public static void iterateDevelopment(SceneGraphComponent sgc_devmap, int depth, Tetra cur_tetra, Face source_face, Frustum3D current_frustum, AffineTransformation current_trans){
    
    //note: source_face and current_frustum may be null
    
    //get new affine transformation
    AffineTransformation new_trans = new AffineTransformation(3);
    if(source_face != null){
      AffineTransformation coord_trans = CoordTrans3D.affineTransAt(cur_tetra, source_face);
      new_trans.leftMultiply(coord_trans);
    }
    new_trans.leftMultiply(current_trans);
    
    //apply new_trans to tetra and orient the faces
    OrientedTetra oriented_tetra = new OrientedTetra(cur_tetra, new_trans);
    
    //make sure we are within bounding box
    //if(isOutsideBoundingBox(oriented_tetra)){ return; }
    
    //clip the tetra by the current frustum
    ArrayList<EmbeddedFace> cliptetra = new ArrayList<EmbeddedFace>();
    
    if(current_frustum != null){
      //get list of EmbeddedFaces by clipping with Frustum3D and using ConvexHull3D
      ArrayList<Vector> cliptetra_points = current_frustum.clipFace(oriented_tetra);
      if(cliptetra_points.size() < 4){ return; } //quit this branch if cliptetra has no faces
      ConvexHull3D ch3d = new ConvexHull3D(cliptetra_points);
      cliptetra = ch3d.getFaces();
      
    }else{
      //get list of EmbeddedFaces directly from oriented_tetra
      for(int j=0; j<4; j++){
        OrientedFace of = oriented_tetra.getOrientedFace(j);
        cliptetra.add(new EmbeddedFace(of.getVector(0), of.getVector(1), of.getVector(2)));
      }
    }
    
    //add clipped tetra to display
    Color color = Color.getHSBColor((float)cur_tetra.getIndex()/(float)Triangulation.tetraTable.size(), 0.5f, 0.9f);
    if(cur_tetra == source_tetra_){ color = Color.WHITE; }
    SceneGraphComponent sgc_new_tetra = sgcFromEFList(cliptetra, color);
    sgc_devmap.addChild(sgc_new_tetra);
    
    //see which faces to continue developing on, if any
    if(depth >= max_recursion_depth_){ return; }
    
    for(int j=0; j<4; j++){
      OrientedFace of = oriented_tetra.getOrientedFace(j);
      
      Face f = of.getFace();
      if(f == source_face){ continue; }
      
      //find tetra incident to cur_tetra through this face
      Iterator<Tetra> it = f.getLocalTetras().iterator();
      Tetra next_tetra = it.next();
      if(next_tetra == cur_tetra){ next_tetra = it.next(); }
      
      //intersect frustums
      Frustum3D frust = new Frustum3D(of.getVector(0), of.getVector(1), of.getVector(2));
      Frustum3D new_frust = null;
      if(current_frustum == null){ new_frust = frust; }
      else{ new_frust = Frustum3D.intersect(frust, current_frustum); }
      if(new_frust == null){ continue; }
      
      //develop off of oriented_face
      iterateDevelopment(sgc_devmap, depth+1, next_tetra, f, new_frust, new_trans);
    }
  }
  
  public static SceneGraphComponent sgcFromEFList(ArrayList<EmbeddedFace> eflist, Color color){
    
    //create a sgc for the tetra, after applying specified affine transformation
    SceneGraphComponent sgc = new SceneGraphComponent();
    
    //create appearance
    Appearance app = new Appearance();
    
    //set some basic attributes
    app.setAttribute(CommonAttributes.FACE_DRAW, true);
    app.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    
    //set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app, true);
    
    //line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setLineWidth(0.0);
    dls.setDiffuseColor(Color.BLACK);
    
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(color);
    dps.setTransparency(0.9d);
    
    //set appearance
    sgc.setAppearance(app);

    Iterator<EmbeddedFace> ief = eflist.iterator();
    while(ief.hasNext()){
      EmbeddedFace ef = ief.next();
      SceneGraphComponent sgc_child = new SceneGraphComponent();
      sgc_child.setGeometry(ef.getGeometry(color));
      sgc.addChild(sgc_child);
    }
    
    return sgc;
  }
}
