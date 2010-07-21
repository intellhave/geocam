package development;

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

import InputOutput.TriangulationIO;
import triangulation.*;
import Geoquant.*;

public class DevelopmentApp2D {
  
  public static void main(String[] args){
    
    EmbeddedTriangulation.readEmbeddedSurface("models/cone.off");
    //TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/octahedron.xml");
    
    Iterator<Integer> i = null;
    
    //set edge lengths randomly from [2,3)
    /*i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Edge e = Triangulation.edgeTable.get(key);
      Length.At(e).setValue(2+Math.random()); //random return value is in [0,1)
    }*/
    
    //print some face info
    /*System.out.printf("\n\nFACE INFO\n");
    
    i = Triangulation.faceTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = i.next();
      Face f = Triangulation.faceTable.get(key);
      
      System.out.printf("Face %d: \n",key);
      System.out.printf("   Num local faces: %d\n", f.getLocalFaces().size());
      System.out.printf("   Area: %f\n",Area.valueAt(f)); 
      
      //coords
      System.out.printf("   Coords: ");
      Iterator<Vertex> j = f.getLocalVertices().iterator();
      while(j.hasNext()){
        Vertex v = j.next();
        System.out.printf("[v%d: (",v.getIndex());
        System.out.print(Coord2D.coordAt(v,f));
        System.out.print(")]");
      }
      System.out.printf("\n");
    }*/
    
    //get points using EmbeddedMfldData
    /*i = Triangulation.faceTable.keySet().iterator();
    
    ArrayList<Vector> temp = new ArrayList<Vector>();
    while(i.hasNext()){
      Integer key = i.next();
      Face f = Triangulation.faceTable.get(key);
      Iterator<Vertex> j = f.getLocalVertices().iterator();
      while(j.hasNext()){
        Vertex v = j.next();
        temp.add( EmbeddedTriangulation.getCoords3D(f,Coord2D.coordAt(v,f)) );
      }
    }
    
    Vector[] temp2 = new Vector[temp.size()];
    for(int k=0; k<temp.size(); k++){
      temp2[k] = temp.get(k);
    }
    SceneGraphComponent sgc_mfld = sgcFromPoints3D( temp2 );*/
    
    //pick some arbitrary face
    i = Triangulation.faceTable.keySet().iterator();
    Face source_face = Triangulation.faceTable.get(i.next());

    Vector source = new Vector(0,0);
    Iterator<Vertex> iv = source_face.getLocalVertices().iterator();
    while(iv.hasNext()){
      source.add(Coord2D.coordAt(iv.next(), source_face));
    }
    source.scale(0.3333333333333333333333);
    //get initial affine transformation moving source to the origin
    AffineTransformation T = new AffineTransformation(Vector.scale(source,-1));

    //root sgc
    SceneGraphComponent sgc_root = new SceneGraphComponent();
    SceneGraphComponent sgc_face1 = sgcFromFace(source_face, T, Color.RED);
    SceneGraphComponent sgc_points = sgcFromPoints2D(new Vector[] { new Vector(0,0) });
    sgc_root.addChild(sgc_face1);
    sgc_root.addChild(sgc_points);
    //sgc_root.addChild(sgc_mfld);
    //sgc_root.addChild(EmbeddedTriangulation.getSGC());
    
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
    dps.setTransparency(0.6d);
    
    //find 'development edge info' for this face, and iterate development
    ArrayList<DevelopmentEdgeInfo> deInfoList = getDevelopmentEdgeInfo(source_face, T);
    for(int j=0; j<deInfoList.size(); j++){
      DevelopmentEdgeInfo deInfo = deInfoList.get(j);
      Frustum2D frust = new Frustum2D(deInfo.vect0_,deInfo.vect1_);
      iterateDevelopment(sgc_root,app_face,deInfo.F_,source_face,frust,T);
    }
    
    //jrviewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root);
    jrv.registerPlugin(new ContentAppearance());
    jrv.registerPlugin(new ContentLoader());
    jrv.registerPlugin(new ContentTools());
    jrv.startup();
    
    
    JRViewer jrv_embedded = new JRViewer();
    jrv_embedded.addBasicUI();
    jrv_embedded.registerPlugin(new ContentTools());
    jrv_embedded.setContent(EmbeddedTriangulation.getSGC());
    //jrv_embedded.startup();
    
  }
  
  //struct holding info necc to develop off an edge
  private static class DevelopmentEdgeInfo{
    public Vector vect0_,vect1_;
    public Vertex vert0_,vert1_;
    Face F_;

    public DevelopmentEdgeInfo(Vertex vert0, Vertex vert1, Vector vect0, Vector vect1, Face F){
      vect0_ = vect0; vect1_ = vect1;
      vert0_ = vert0; vert1_ = vert1;
      F_ = F;
    }
  };
  
  public static ArrayList<DevelopmentEdgeInfo> getDevelopmentEdgeInfo(Face F, AffineTransformation T){
    ArrayList<DevelopmentEdgeInfo> retinfo = new ArrayList<DevelopmentEdgeInfo>();
    
    //get verts
    Vertex[] vert = new Vertex[3];
    Iterator<Vertex> iv = F.getLocalVertices().iterator();
    for(int i=0; i<3; i++){
      vert[i] = iv.next();
    }
    
    //get vects
    Vector[] vect = new Vector[3];
    for(int i=0; i<3; i++){
      try { vect[i] = T.affineTransPoint(Coord2D.coordAt(vert[i], F)); }
      catch (Exception e) { e.printStackTrace(); }
    }
    
    //get faces
    Face[] face = new Face[3];
    Iterator<Face> fi = F.getLocalFaces().iterator();
    while(fi.hasNext()){
      Face ftemp = fi.next();
      //see which verts are in common with F
      boolean[] vmatch = new boolean[3];
      vmatch[0] = false; vmatch[1] = false; vmatch[2] = false;
      Iterator<Vertex> vi = ftemp.getLocalVertices().iterator();
      while(vi.hasNext()){
        Vertex vtemp = vi.next();
        if(vtemp == vert[0]){ vmatch[0] = true; }
        else if(vtemp == vert[1]){ vmatch[1] = true; }
        else if(vtemp == vert[2]){ vmatch[2] = true; }
      }
      //now figure out based on vmatch which order to list the incident faces
      //we want face[k] to be the one sharing vert[k] and vert[(k+1)%3]
      if(vmatch[0] == false){ face[1] = ftemp; }
      else if(vmatch[1] == false){ face[2] = ftemp; }
      else if(vmatch[2] == false){ face[0] = ftemp; }
    }
    
    //see if CCW
    Vector u = Vector.subtract(vect[1], vect[0]);
    Vector v = Vector.subtract(vect[2], vect[0]);
    double z = u.getComponent(0)*v.getComponent(1) - u.getComponent(1)*v.getComponent(0);
    if(z < 0){ //flip orientation
      Vertex tempvert = vert[2];
      Vector tempvect = vect[2];
      Face tempface = face[2];
      vert[2] = vert[1];
      vect[2] = vect[1];
      face[2] = face[1];
      vert[1] = tempvert;
      vect[1] = tempvect;
      face[1] = tempface;
    }
    
    //add to return list
    retinfo.add(new DevelopmentEdgeInfo(vert[0],vert[1],vect[0],vect[1],face[0]));
    retinfo.add(new DevelopmentEdgeInfo(vert[1],vert[2],vect[1],vect[2],face[1]));
    retinfo.add(new DevelopmentEdgeInfo(vert[2],vert[0],vect[2],vect[0],face[2]));
    
    return retinfo;
  }
  
  public static void iterateDevelopment(SceneGraphComponent sgc_root, Appearance app, Face new_face, Face source_face, Frustum2D current_frustum, AffineTransformation current_trans){
    
    AffineTransformation new_trans = new AffineTransformation(CoordTrans2D.affineTransAt(new_face, source_face));
    new_trans.leftMultiply(current_trans);
    
    //get transformed points from new_face
    ArrayList<Vector> efpts = new ArrayList<Vector>();
    Iterator<Vertex> i = new_face.getLocalVertices().iterator();
    while(i.hasNext()){
      Vertex vert = i.next();
      Vector pt = Coord2D.coordAt(vert, new_face);
      try{ pt = new_trans.affineTransPoint(pt); }catch(Exception e1){ e1.printStackTrace(); }
      efpts.add(pt);
    }

    //make embeddedface
    EmbeddedFace ef = new EmbeddedFace(efpts);
    
    //add clipped face to display
    Geometry g = ef.getGeometry(Color.WHITE);//current_frustum.clipFace(ef).getGeometry(Color.WHITE);
    SceneGraphComponent sgc_new_face = new SceneGraphComponent();
    sgc_new_face.setGeometry(g);
    
    //add to sgc_root
    sgc_new_face.setAppearance(app);
    sgc_root.addChild(sgc_new_face);
    
    //see which faces to continue developing on
    
  }

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
  }
  
  
}
