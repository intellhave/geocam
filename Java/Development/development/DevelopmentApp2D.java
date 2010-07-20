package development;

import java.awt.Color;
import java.util.Iterator;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;

import InputOutput.TriangulationIO;
import Triangulation.*;
import Triangulation.Face;
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
    Face face = Triangulation.faceTable.get(i.next());

    //root sgc
    SceneGraphComponent sgc_root = new SceneGraphComponent();
    SceneGraphComponent sgc_face1 = sgcFromFace(face, new AffineTransformation(2), Color.RED);
    SceneGraphComponent sgc_points = sgcFromPoints2D(new Vector[] { new Vector(0,0) });
    sgc_root.addChild(sgc_face1);
    sgc_root.addChild(sgc_points);
    //sgc_root.addChild(sgc_mfld);
    //sgc_root.addChild(EmbeddedTriangulation.getSGC());
    
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
