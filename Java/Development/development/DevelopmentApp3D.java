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
      Length.at(e).setValue(2); 
    }
    
    /*//print some tetra info
    System.out.printf("\n\nTOP DIM SIMPLEX INFO\n");
    
    i = Triangulation.tetraTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Tetra t = Triangulation.tetraTable.get(key);
      
      System.out.printf("Tetra %d: \n",key);
      System.out.printf("   Num local tetras: %d\n", t.getLocalTetras().size());
      System.out.printf("   Volume: %f\n",Volume.valueAt(t)); 
      
      //coords
      System.out.printf("   Coords: ");
      Iterator<Vertex> j = t.getLocalVertices().iterator();
      while(j.hasNext()){
        Vertex v = j.next();
        System.out.printf("[v%d: (",v.getIndex());
        System.out.print(Coord3D.coordAt(v,t));
        System.out.print(")]");
      }
      System.out.printf("\n");
    }*/
    
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
    
    //make clipped embeddedface
    //EmbeddedFace origtetra = new EmbeddedFace(efpts);
    //EmbeddedFace cliptetra = null;
    //if(current_frustum != null){ cliptetra = current_frustum.clip(origtetra); }
    //else{ cliptetra = origtetra; }
    
    //quit if tetra is completely obscured
    //if(cliptetra == null){ return; }
    
    //add clipped tetra to display
    Color color = Color.getHSBColor((float)cur_tetra.getIndex()/(float)Triangulation.tetraTable.size(), 0.5f, 0.9f);
    if(cur_tetra == source_tetra_){ color = Color.WHITE; }
    SceneGraphComponent sgc_new_tetra = sgcFromTetra(cur_tetra,new_trans,color);
    //sgc_new_tetra.setGeometry(clipface.getGeometry(color));
    //sgc_new_tetra.setAppearance(getFaceAppearance(0.5f));
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
      Frustum3D frust = new Frustum3D(of.getVect(0), of.getVect(1), of.getVect(2));
      Frustum3D new_frust = null;
      if(current_frustum == null){ new_frust = frust; }
      else{ new_frust = Frustum3D.intersect(frust, current_frustum); }
      if(new_frust == null){ continue; }
      
      //develop off of oriented_face
      iterateDevelopment(sgc_devmap, depth+1, next_tetra, f, new_frust, new_trans);
    }
    
  }
  
  /*public static SceneGraphComponent sgcFromPoints(Vector...points){
    
    //create the sgc
    SceneGraphComponent sgc_points = new SceneGraphComponent();
    
    //create appearance
    Appearance app_points = new Appearance();
    
    //set some basic attributes
    app_points.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app_points.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    
    //set point shader
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(0.05);
    dps.setDiffuseColor(Color.BLUE);
    
    //set appearance
    sgc_points.setAppearance(app_points);
    
    //set vertlist
    double[][] vertlist = new double[points.length][3];
    for(int i=0; i<points.length; i++){
      vertlist[i] = new double[]{ points[i].getComponent(0), points[i].getComponent(1), points[i].getComponent(2) };
    }
    
    //create geometry with pointsetfactory
    PointSetFactory psf = new PointSetFactory();
    psf.setVertexCount(points.length);
    psf.setVertexCoordinates(vertlist);
    psf.update();
    
    //set geometry
    sgc_points.setGeometry(psf.getGeometry());
    
    //return
    return sgc_points;
  }*/
  
  public static SceneGraphComponent sgcFromTetra(Tetra tetra, AffineTransformation affineTrans, Color color){
    
    //create a sgc for the tetra, after applying specified affine transformation
    SceneGraphComponent sgc_tetra = new SceneGraphComponent();
    
    //create appearance
    Appearance app_tetra = new Appearance();
    
    //set some basic attributes
    app_tetra.setAttribute(CommonAttributes.FACE_DRAW, true);
    app_tetra.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_tetra.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_tetra.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_tetra.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    
    //set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app_tetra, true);
    
    //line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setDiffuseColor(Color.BLACK);
    
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(color);
    dps.setTransparency(0.6d);
    
    //set appearance
    sgc_tetra.setAppearance(app_tetra);

    //create list of verts
    double[][] vertlist = new double[4][3];
    
    int count = 0;
    Iterator<Vertex> i = tetra.getLocalVertices().iterator();
    
    while(i.hasNext()){
      Vertex v = i.next();
      
      Vector pt = null;
      try { pt = affineTrans.affineTransPoint(Coord3D.coordAt(v,tetra)); } 
      catch (Exception e) { e.printStackTrace(); }
      
      vertlist[count] = new double[] { pt.getComponent(0), pt.getComponent(1), pt.getComponent(2)};
      count++;
    }
    
    //set combinatorics
    int[][] facelist = new int[][] {
        new int[] {0,2,1}, new int[] {0,2,3},
        new int[] {2,1,3}, new int[] {1,0,3}};
    
    int[][] edgelist = new int[][] {
        new int[] {0,2}, new int[] {2,1}, new int[] {1,0},
        new int[] {0,3}, new int[] {1,3}, new int[] {2,3}};
    
    //use face factory to create geometry
    IndexedFaceSetFactory ifsf_tetra = new IndexedFaceSetFactory();
    
    ifsf_tetra.setVertexCount(4);
    ifsf_tetra.setVertexCoordinates(vertlist);
    
    ifsf_tetra.setFaceCount(4);
    ifsf_tetra.setFaceIndices(facelist);
    
    ifsf_tetra.setEdgeCount(6);
    ifsf_tetra.setEdgeIndices(edgelist);
    
    ifsf_tetra.update();
    
    //set geometry
    sgc_tetra.setGeometry(ifsf_tetra.getGeometry());
    
    //return
    return sgc_tetra;
  }
}
