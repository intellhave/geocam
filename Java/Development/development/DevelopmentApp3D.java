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
  
//struct holding info neccessary to develop off an edge
  private static class DevelopmentFaceInfo{
    public Vector vect0_,vect1_,vect2_;
    public Vertex vert0_,vert1_,vert2_;
    Face next_source_face_;
    Tetra next_tetra_;

    public DevelopmentFaceInfo(Vertex vert0, Vertex vert1, Vertex vert2, Vector vect0, Vector vect1, Vector vect2, Tetra next_tetra, Face next_source_face){
      vect0_ = vect0; vect1_ = vect1; vect2_ = vect2;
      vert0_ = vert0; vert1_ = vert1; vert2_ = vert2;
      next_source_face_ = next_source_face;
      next_tetra_ = next_tetra;
    }
  };
  
  public static ArrayList<DevelopmentFaceInfo> getDevelopmentFaceInfo(Tetra cur_tetra, Face source_face, AffineTransformation T){

    //for each localFace f of cur_tetra, determine the transformed coordinates of
    //vertices making up f, put them in CCW order (looking at f outside of cur_tetra)
    //and find tetra incident to cur_tetra along f.
    //put all of this information in a DevelopmentFaceInfo, and return list of all of these.

    ArrayList<DevelopmentFaceInfo> retinfo = new ArrayList<DevelopmentFaceInfo>();
    
    Iterator<Face> fi = cur_tetra.getLocalFaces().iterator();
    while(fi.hasNext()){
      Face f = fi.next();
      //don't do anything with source_face
      if(f == source_face){ continue; }
      
      //find incident tetra
      Iterator<Tetra> fit = f.getLocalTetras().iterator();
      Tetra next_tetra = fit.next();
      if(next_tetra == cur_tetra){ next_tetra = fit.next(); }
      
      //get shared vertices of cur_tetra and next_tetra (i.e. verts of f)
      Vertex vert[] = new Vertex[4];
      Iterator<Vertex> vi = f.getLocalVertices().iterator();
      for(int i=0; i<3; i++){ vert[i] = vi.next(); }

      //get non-common vertex on cur_tetra
      LinkedList<Vertex> leftover = new LinkedList<Vertex>(cur_tetra.getLocalVertices());
      leftover.removeAll(f.getLocalVertices());
      vert[3] = leftover.get(0);
      
      //apply T to coordinates
      Vector[] vect = new Vector[4];
      for(int i=0; i<4; i++){
        try { vect[i] = T.affineTransPoint(Coord3D.coordAt(vert[i], cur_tetra)); }
        catch (Exception e) { e.printStackTrace(); }
      }
      
      //make orientation of verts 0-1-2 is CCW
      Vector u = Vector.subtract(vect[1], vect[0]);
      Vector v = Vector.subtract(vect[2], vect[0]);
      Vector w = Vector.subtract(vect[3], vect[0]);
      //want cross(u,v) in opposite direction of w, i.e. (uxv).w < 0
      double z = Vector.dot(Vector.cross(u,v),w);
      if(z > 0){ //flip orientation
        Vertex tempvert = vert[2];
        Vector tempvect = vect[2];
        vert[2] = vert[1];
        vect[2] = vect[1];
        vert[1] = tempvert;
        vect[1] = tempvect;
      }
      
      //add to list of dfinfos
      retinfo.add(new DevelopmentFaceInfo(vert[0], vert[1], vert[2], vect[0], vect[1], vect[2], next_tetra, f));
    }
    
    return retinfo;
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
    
    //get transformed points from cur_tetra
    ArrayList<Vector> efpts = new ArrayList<Vector>();
    
    Iterator<Vertex> i = cur_tetra.getLocalVertices().iterator();
    while(i.hasNext()){
      Vertex vert = i.next();
      Vector pt = Coord3D.coordAt(vert, cur_tetra);
      try{ efpts.add(new_trans.affineTransPoint(pt)); }
      catch(Exception e1){ e1.printStackTrace(); }
    }
    
    //make sure we are within bounding box
    //if(isOutsideBoundingBox(efpts)){ return; }
    
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
    
    ArrayList<DevelopmentFaceInfo> dfInfoList = getDevelopmentFaceInfo(cur_tetra, source_face, new_trans);
    for(int j=0; j<dfInfoList.size(); j++){
      DevelopmentFaceInfo dfInfo = dfInfoList.get(j);
      
      //intersect frustums
      Frustum3D frust = new Frustum3D(dfInfo.vect0_,dfInfo.vect1_,dfInfo.vect2_);
      Frustum3D new_frust = null;
      if(current_frustum == null){ new_frust = frust; }
      else{ new_frust = Frustum3D.intersect(frust, current_frustum); }
      if(new_frust == null){ continue; }
      
      //iterate
      iterateDevelopment(sgc_devmap, depth+1, dfInfo.next_tetra_, dfInfo.next_source_face_, new_frust, new_trans);
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
    app_tetra.setAttribute(CommonAttributes.FACE_DRAW, false);
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
