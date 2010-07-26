package development;

import geoquant.*;
import inputOutput.TriangulationIO;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;

import triangulation.*;

public class DevelopmentApp2D {
  
  public static void main(String[] args){
    
    EmbeddedTriangulation.readEmbeddedSurface("models/cone.off");
    //TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/icosahedron.xml");
    
    Iterator<Integer> i = null;
    
    //set edge lengths randomly from [2,3)
    /*i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Edge e = Triangulation.edgeTable.get(key);
      double rand = Math.random();
      Length.At(e).setValue(2+rand); //random return value is in [0,1)
    }*/
    
    //print some debug data
    //EmbeddedTriangulation.printTriangulationData();
    //EmbeddedTriangulation.printCoordinateData();
    //EmbeddedTriangulation.printGeometricData();
    
    //pick some arbitrary face and source point
    i = Triangulation.faceTable.keySet().iterator();
    Face source_face = null;
    for(int k=0; k<5; k++){
      source_face = Triangulation.faceTable.get(i.next());
    }
    
    Vector source = new Vector(0,0);
    Iterator<Vertex> iv = source_face.getLocalVertices().iterator();
    while(iv.hasNext()){
      source.add(Coord2D.coordAt(iv.next(), source_face));
    }
    source.scale(1.0f/3.0f);
    
    //get initial affine transformation moving source to the origin
    AffineTransformation T = new AffineTransformation(Vector.scale(source,-1));
    
    //root sgc
    SceneGraphComponent sgc_root = new SceneGraphComponent();
    //SceneGraphComponent sgc_face1 = sgcFromFace(source_face, T, Color.RED);
    //SceneGraphComponent sgc_points = sgcFromPoints2D(new Vector[] { new Vector(0,0) });
    //sgc_root.addChild(sgc_face1);
    //sgc_root.addChild(sgc_points);
    
    //find 'development edge info' for this face, and iterate development
    iterateDevelopment(0,2,sgc_root,source_face,null,null,T);
    
    //jrviewer(s)
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root);
    jrv.registerPlugin(new ContentAppearance());
    jrv.registerPlugin(new ContentLoader());
    jrv.registerPlugin(new ContentTools());
    jrv.startup();
    
    /*JRViewer jrv_embedded = new JRViewer();
    jrv_embedded.addBasicUI();
    jrv_embedded.registerPlugin(new ContentTools());
    jrv_embedded.setContent(EmbeddedTriangulation.getSGC());
    jrv_embedded.startup();*/
    
  }
  
  //struct holding info neccessary to develop off an edge
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
  
  public static void iterateDevelopment(int depth, int maxdepth, SceneGraphComponent sgc_root, Face cur_face, Edge source_edge, Frustum2D current_frustum, AffineTransformation current_trans){
    
    //note: source_face and current_frustum may be null
    
    //get new affine transformation
    AffineTransformation new_trans = new AffineTransformation(2);
    if(source_edge != null){
      AffineTransformation coord_trans = CoordTrans2D.affineTransAt(cur_face, source_edge);
      new_trans.leftMultiply(coord_trans);
    }
    new_trans.leftMultiply(current_trans);
    
    //get transformed points from new_face
    ArrayList<Vector> efpts = new ArrayList<Vector>();
    
    Iterator<Vertex> i = cur_face.getLocalVertices().iterator();
    while(i.hasNext()){
      Vertex vert = i.next();
      Vector pt = Coord2D.coordAt(vert, cur_face);
      try{ efpts.add(new_trans.affineTransPoint(pt)); }
      catch(Exception e1){ e1.printStackTrace(); }
    }
    
    //make clipped embeddedface
    EmbeddedFace origface = new EmbeddedFace(efpts);
    EmbeddedFace clipface = null;
    if(current_frustum != null){ clipface = current_frustum.clipFace(origface); }
    else{ clipface = origface; }
    
    //quit if face is completely obscured
    if(clipface == null){ return; }
    
    //add clipped face to display
    SceneGraphComponent sgc_new_face = new SceneGraphComponent();
    Color color = Color.getHSBColor((float)depth/(float)maxdepth, 1.0f, 0.5f);
    //Color color = Color.getHSBColor((float)new_face.getIndex()/(float)Triangulation.faceTable.size(), 1.0f, 0.5f);
    sgc_new_face.setGeometry(clipface.getGeometry(color));//, Math.random()));
    sgc_new_face.setAppearance(getFaceAppearance(0.6f));
    sgc_root.addChild(sgc_new_face);
    
    //see which faces to continue developing on
    if(depth >= maxdepth){ return; }
    
    
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
      iterateDevelopment(depth+1, maxdepth, sgc_root, deInfo.next_face_, deInfo.next_source_edge_, new_frust, new_trans);
    }
    
  }
  
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
    dls.setDiffuseColor(Color.BLACK);
    
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(Color.WHITE);
    dps.setTransparency(transparency);
    
    return app_face;
  }

  /*
  public static SceneGraphComponent sgcFromPoints2D(Vector[] points){
    
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
      vertlist[i] = new double[]{ points[i].getComponent(0), points[i].getComponent(1), 0 };
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
  }
  
  public static SceneGraphComponent sgcFromFace(Face face, AffineTransformation affineTrans, Color color){
    
    //create a sgc for the face, after applying specified affine transformation
    SceneGraphComponent sgc_face = new SceneGraphComponent();
    
    //create appearance
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
    dls.setDiffuseColor(Color.BLACK);
    
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(color);
    dps.setTransparency(0.6d);
    
    //set appearance
    sgc_face.setAppearance(app_face);

    //create list of verts
    double[][] vertlist = new double[3][3];
    
    int count = 0;
    Iterator<Vertex> i = face.getLocalVertices().iterator();
    
    while(i.hasNext()){
      Vertex v = i.next();
      
      Vector pt = null;
      try { pt = affineTrans.affineTransPoint(Coord2D.coordAt(v,face)); } 
      catch (Exception e) { e.printStackTrace(); }
      
      vertlist[count] = new double[] { pt.getComponent(0), pt.getComponent(1), 0 };
      count++;
    }
    
    //set combinatorics
    int[][] facelist = new int[][] { new int[] {0,1,2} };
    
    int[][] edgelist = new int[][] { new int[] {0,1}, new int[] {1,2}, new int[] {2,0} };
    
    //use face factory to create geometry
    IndexedFaceSetFactory ifsf_face = new IndexedFaceSetFactory();
    
    ifsf_face.setVertexCount(3);
    ifsf_face.setVertexCoordinates(vertlist);
    
    ifsf_face.setFaceCount(1);
    ifsf_face.setFaceIndices(facelist);
    
    ifsf_face.setEdgeCount(3);
    ifsf_face.setEdgeIndices(edgelist);
    
    ifsf_face.update();
    
    //set geometry
    sgc_face.setGeometry(ifsf_face.getGeometry());
    
    //return
    return sgc_face;
  }*/
  
  
}
