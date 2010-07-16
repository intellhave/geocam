package development;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import util.Matrix;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.data.Attribute;
import de.jreality.scene.data.DataList;
import de.jreality.scene.proxy.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.Input;

import InputOutput.TriangulationIO;
import Triangulation.*;
import Triangulation.Face;
import Geoquant.*;

public class DevelopmentApp2D {
  
  //this is data associated to a Face in the case of embedded manifolds
  //-------------------------------------
  private static class EmbeddedFace{
    private Vector origin_;
    private Vector tx_;
    private Vector ty_;
    
    public EmbeddedFace(Vector origin, Vector tx, Vector ty){
      origin_ = new Vector(origin);
      tx_ = new Vector(tx);
      ty_ = new Vector(ty);
    }
    
    public Vector getCoords3D(Vector v){
      Vector ret = new Vector(origin_);
      try { ret.add(Vector.scale(tx_, v.getComponent(0))); } catch (Exception e) {}
      try { ret.add(Vector.scale(ty_, v.getComponent(1))); } catch (Exception e) {}
      return ret;
    }
  };
  static boolean isEmbedded = false;
  static HashMap<Face,EmbeddedFace> EmbeddedManifoldData = null;
  static SceneGraphComponent sgc_embedded = null;
  //-------------------------------------
  
  private static void printTriangulationData(){
    
    Iterator<Integer> i = null;

    //print localSimplices for each Vertex
    System.out.print("\n");
    i = Triangulation.vertexTable.keySet().iterator();
    while(i.hasNext()){
      Vertex v = Triangulation.vertexTable.get(i.next());
      
      System.out.printf("Vertex %d", v.getIndex());
      
      System.out.printf("\n   Local vertices: ");
      Iterator<Vertex> vi = v.getLocalVertices().iterator();
      while(vi.hasNext()){
        Vertex v2 = vi.next();
        System.out.printf(" %d", v2.getIndex());
      }
      
      System.out.printf("\n   Local edges: ");
      Iterator<Edge> ei = v.getLocalEdges().iterator();
      while(ei.hasNext()){
        Edge e2 = ei.next();
        System.out.printf(" %d", e2.getIndex());
      }
      
      System.out.printf("\n   Local faces: ");
      Iterator<Face> fi = v.getLocalFaces().iterator();
      while(fi.hasNext()){
        Face f2 = fi.next();
        System.out.printf(" %d", f2.getIndex());
      }
      
      System.out.printf("\n");
    }
    

    //print localSimplices for each Edge
    System.out.print("\n");
    i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Edge e = Triangulation.edgeTable.get(i.next());
      
      System.out.printf("Edge %d", e.getIndex());
      
      System.out.printf("\n   Local vertices: ");
      Iterator<Vertex> vi = e.getLocalVertices().iterator();
      while(vi.hasNext()){
        Vertex v2 = vi.next();
        System.out.printf(" %d", v2.getIndex());
      }
      
      System.out.printf("\n   Local edges: ");
      Iterator<Edge> ei = e.getLocalEdges().iterator();
      while(ei.hasNext()){
        Edge e2 = ei.next();
        System.out.printf(" %d", e2.getIndex());
      }
      
      System.out.printf("\n   Local faces: ");
      Iterator<Face> fi = e.getLocalFaces().iterator();
      while(fi.hasNext()){
        Face f2 = fi.next();
        System.out.printf(" %d", f2.getIndex());
      }
      
      System.out.printf("\n");
    }
    
    //print localSimplices for each Face
    System.out.print("\n");
    i = Triangulation.faceTable.keySet().iterator();
    while(i.hasNext()){
      Face f = Triangulation.faceTable.get(i.next());
      
      System.out.printf("Face %d", f.getIndex());
      
      System.out.printf("\n   Local vertices: ");
      Iterator<Vertex> vi = f.getLocalVertices().iterator();
      while(vi.hasNext()){
        Vertex v2 = vi.next();
        System.out.printf(" %d", v2.getIndex());
      }
      
      System.out.printf("\n   Local edges: ");
      Iterator<Edge> ei = f.getLocalEdges().iterator();
      while(ei.hasNext()){
        Edge e2 = ei.next();
        System.out.printf(" %d", e2.getIndex());
      }
      
      System.out.printf("\n   Local faces: ");
      Iterator<Face> fi = f.getLocalFaces().iterator();
      while(fi.hasNext()){
        Face f2 = fi.next();
        System.out.printf(" %d", f2.getIndex());
      }
      
      System.out.printf("\n");
    }
    
  }
  
  private static void readEmbeddedSurface(String filename, boolean verbose){
    
    //1) reads in a file containing an embedded surface
    //2) creates sgc_embedded for drawing
    //3) populates Triangulation class
    //4) sets length geoquants for each edge
    //5) populates EmbeddedManifoldData for going from abstract -> embedding
    
    //Note: assumes faces of embedded surface are convex, but not necessarily triangles
    
    //read the data file
    Geometry geom = null;
    try{
      Input input = Input.getInput(filename);
      geom = Readers.read(input).getGeometry();
    }catch(IOException e){
      e.printStackTrace();
      return;
    }
    
    //make sgc_embedded
    sgc_embedded = new SceneGraphComponent();
    
    //create appearance
    Appearance app_embedded = new Appearance();
    
    //set some basic attributes
    app_embedded.setAttribute(CommonAttributes.FACE_DRAW, true);
    app_embedded.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_embedded.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_embedded.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_embedded.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
    
    //set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader)ShaderUtility.createDefaultGeometryShader(app_embedded, true);
    
    //line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setDiffuseColor(Color.BLACK);
    
    //polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(Color.BLUE);
    dps.setTransparency(0.6d);
    
    //set appearance
    sgc_embedded.setAppearance(app_embedded);
    sgc_embedded.setGeometry(geom);
    
    //get the geometry data
    DataList dl_faceindices = geom.getAttributes(Geometry.CATEGORY_FACE,Attribute.INDICES);
    //DataList dl_facenormals = geom.getAttributes(Geometry.CATEGORY_FACE,Attribute.NORMALS);
    DataList dl_edgeindices = geom.getAttributes(Geometry.CATEGORY_EDGE,Attribute.INDICES);
    DataList dl_vertcoords = geom.getAttributes(Geometry.CATEGORY_VERTEX,Attribute.COORDINATES);
    
    int nfaces = dl_faceindices.size();
    int nedges = dl_edgeindices.size();
    int nverts = dl_vertcoords.size();
    
    if(verbose){ System.out.printf("%d verts, %d edges, %d faces\n", nverts,nedges,nfaces); }
    
    //clear Triangulation
    Triangulation.reset();
    
    //first create the Vertex, Edge, and Face objects
    for(int i=0; i<nfaces; i++){ Triangulation.putFace(new Face(i)); }
    for(int i=0; i<nedges; i++){ Triangulation.putEdge(new Edge(i)); }
    for(int i=0; i<nverts; i++){ Triangulation.putVertex(new Vertex(i)); }
    
    //determine localVertices for each face and localFaces for each vertex
    for(int i=0; i<nfaces; i++){
      Face f = Triangulation.faceTable.get(i);
      
      int[] vi = dl_faceindices.item(i).toIntArray(null);
      for(int j=0; j<vi.length; j++){
        Vertex v = Triangulation.vertexTable.get(vi[j]);
        v.addFace(f);
        f.addVertex(v);
      }
    }

    //determine localVertices for each edge, localEdges/localVerts for each vertex
    for(int i=0; i<nedges; i++){
      Edge e = Triangulation.edgeTable.get(i);
      
      int[] vi = dl_edgeindices.item(i).toIntArray(null);
      Vertex v0 = Triangulation.vertexTable.get(vi[0]);
      Vertex v1 = Triangulation.vertexTable.get(vi[1]);
      v0.addVertex(v1);  v0.addEdge(e);  e.addVertex(v0);
      v1.addVertex(v0);  v1.addEdge(e);  e.addVertex(v1);
    }
    
    //determine localFaces for each edge and localEdges for each face
    for(int i=0; i<nedges; i++){
      
      Edge e = Triangulation.edgeTable.get(i);
      int[] ei = dl_edgeindices.item(i).toIntArray(null);

      //loop through each face
      for(int j=0; j<nfaces; j++){
        //reference to face and its vertex indices
        Face f = Triangulation.faceTable.get(j);
        int[] fi = dl_faceindices.item(j).toIntArray(null);
        
        for(int k=0; k<fi.length; k++){
          //vertex indices for each edge along the face's boundary
          int f0 = fi[k];
          int f1 = fi[(k+1)%fi.length];
          if(((ei[0] == f0) && (ei[1] == f1)) || ((ei[1] == f0) && (ei[0] == f1))){
            e.addFace(f);
            f.addEdge(e);
          }
        }
      }
    }
    
    //determine localFaces for each face
    for(int i=0; i<nfaces; i++){
      Face f = Triangulation.faceTable.get(i);
      
      Iterator<Edge> ei = f.getLocalEdges().iterator();
      while(ei.hasNext()){
        Edge e = ei.next();
        //loop through faces of edge, add if not current face
        Iterator<Face> fi = e.getLocalFaces().iterator();
        while(fi.hasNext()){
          Face f2 = fi.next();
          if(f2 != f){ f.addFace(f2); }
        }
      }
    }
    
    //determine localEdges for each edge
    for(int i=0; i<nedges; i++){
      Edge e = Triangulation.edgeTable.get(i);
      
      Iterator<Vertex> vi = e.getLocalVertices().iterator();
      while(vi.hasNext()){
        Vertex v = vi.next();
        //loop through edges of vertex, add if not current edge
        Iterator<Edge> ei = v.getLocalEdges().iterator();
        while(ei.hasNext()){
          Edge e2 = ei.next();
          if(e2 != e){ e.addEdge(e2); }
        }
      }
    }
    
    //print all the determined local simplices
    if(verbose){ printTriangulationData(); }
    
    //determine lengths and set geoquants
    for(int i=0; i<nedges; i++){
      Edge e = Triangulation.edgeTable.get(i);
      
      int[] vi = dl_edgeindices.item(i).toIntArray(null);
      Vector v0 = new Vector(dl_vertcoords.item(vi[0]).toDoubleArray(null)); //3d coords for first vertex
      Vector v1 = new Vector(dl_vertcoords.item(vi[1]).toDoubleArray(null)); //3d coords for next vertex
      
      double edgelength = 1;
      try { edgelength = Vector.distance(v0,v1); }
      catch (Exception e1) { e1.printStackTrace(); }
      
      Length.At(e).setValue(edgelength);
    }
    
    //determine origin, tangent_x, tangent_y using Coord geoquants:
    //[ |  |  | ]   [ |  |  |  ][x0 x1 x2]-1
    //[ tx ty o ] = [ p0 p1 p2 ][y0 y1 y2]
    //[ |  |  | ]   [ |  |  |  ][ 1  1  1]
    //where face vertex i has 3d coords pi and 2d coords (xi,yi)
    EmbeddedManifoldData = new HashMap<Face,EmbeddedFace>();
    
    for(int i=0; i<nfaces; i++){
      Face f = Triangulation.faceTable.get(i);
      
      int[] vi = dl_faceindices.item(i).toIntArray(null);
      
      //matrix of 3d points
      Vector[] pts3d = new Vector[] {
          new Vector(dl_vertcoords.item(vi[0]).toDoubleArray(null)),
          new Vector(dl_vertcoords.item(vi[1]).toDoubleArray(null)),
          new Vector(dl_vertcoords.item(vi[2]).toDoubleArray(null))
      };
      Matrix P = AffineTransformation.createMatrixFromColumnVectors(pts3d);
      
      //matrix of 2d points
      Vector[] pts2d = new Vector[] {
          new Vector(Coord2D.coordAt(Triangulation.vertexTable.get(vi[0]), f), 1),
          new Vector(Coord2D.coordAt(Triangulation.vertexTable.get(vi[1]), f), 1),
          new Vector(Coord2D.coordAt(Triangulation.vertexTable.get(vi[2]), f), 1)
      };
      Matrix X = AffineTransformation.createMatrixFromColumnVectors(pts2d);
      
      //get origin, tx, and ty
      Matrix A = null;
      try { A = P.multiply(X.inverse()); }
      catch (Exception e) { e.printStackTrace(); }
      Vector tx = new Vector(A.getEntry(0, 0), A.getEntry(1, 0), A.getEntry(2, 0));
      Vector ty = new Vector(A.getEntry(0, 1), A.getEntry(1, 1), A.getEntry(2, 1));
      Vector or = new Vector(A.getEntry(0, 2), A.getEntry(1, 2), A.getEntry(2, 2));
      
      //make embeddedface
      EmbeddedFace eFace = new EmbeddedFace(or,tx,ty);
      EmbeddedManifoldData.put(f, eFace);
      
      if(verbose){
        System.out.printf("\n\nFace %d", f.getIndex());
        System.out.print("\nColumn matrix of 3D pts:\n");
        System.out.print(P); System.out.print("\n");
        System.out.print("\nColumn matrix of 2D pts -> w=1:\n");
        System.out.print(X); System.out.print("\n");
        System.out.print("\nColumn matrix tanx|tany|origin:\n");
        System.out.print(A); System.out.print("\n");
      }
    }

    //set flag that this triangulation is embedded, once finished
    isEmbedded = true;
  }
  
  public static void main(String[] args){
    
    readEmbeddedSurface("models/cone.off",true);
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
    System.out.printf("\n\nFACE INFO\n");
    
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
    }
    
    //get points using EmbeddedMfldData
    /*i = Triangulation.faceTable.keySet().iterator();
    
    ArrayList<Vector> temp = new ArrayList<Vector>();
    while(i.hasNext()){
      Integer key = i.next();
      Face f = Triangulation.faceTable.get(key);
      EmbeddedFace ef = EmbeddedManifoldData.get(f);
      Iterator<Vertex> j = f.getLocalVertices().iterator();
      while(j.hasNext()){
        Vertex v = j.next();
        temp.add( ef.getCoords3D(Coord2D.coordAt(v,f)) );
      }
    }
    
    Vector[] temp2 = new Vector[temp.size()];
    for(int k=0; k<temp.size(); k++){
      temp2[k] = temp.get(k);
    }
    SceneGraphComponent sgc_mfld = sgcFromPoints3D( temp2 );*/
    
    //pick some arbitrary face
    i = Triangulation.faceTable.keySet().iterator();
    Face face = Triangulation.faceTable.get(i.next());

    //root sgc
    SceneGraphComponent sgc_root = new SceneGraphComponent();
    SceneGraphComponent sgc_face1 = sgcFromFace(face, new AffineTransformation(2), Color.RED);
    SceneGraphComponent sgc_points = sgcFromPoints2D(new Vector(0,0));
    sgc_root.addChild(sgc_face1);
    sgc_root.addChild(sgc_points);
    //sgc_root.addChild(sgc_mfld);
    sgc_root.addChild(sgc_embedded);
    
    //loop through tetra's neighbors, adding them in the right place
    Iterator<Face> k = face.getLocalFaces().iterator();
    while(k.hasNext()){
      Face face2 = k.next();
      AffineTransformation atTrans12 = CoordTrans2D.affineTransAt(face2,face);
      sgc_root.addChild(sgcFromFace(face2, atTrans12, Color.WHITE));
    }
    
    //jrviewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root);
    jrv.registerPlugin(new ContentAppearance());
    jrv.registerPlugin(new ContentLoader());
    jrv.registerPlugin(new ContentTools());
    jrv.startup();
  }

  public static SceneGraphComponent sgcFromPoints2D(Vector...points){
    
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
  

  public static SceneGraphComponent sgcFromPoints3D(Vector...points){
    
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
