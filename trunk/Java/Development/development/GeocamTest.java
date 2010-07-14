// Just a little app to play around with loading triangulations and using geoquants
// and eventually test the coordinate and coordinate transform geoquants

package development;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

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

import InputOutput.TriangulationIO;
import Triangulation.*;
import Geoquant.*;

public class GeocamTest {

  public static void main(String[] args){
    
    TriangulationIO.readTriangulation("Data/Triangulations/3DManifolds/3-torus.xml");
    
    Iterator i = null;
    
    //set edge lengths to 1
    i = Triangulation.edgeTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = (Integer)i.next();
      Edge e = Triangulation.edgeTable.get(key);
      Length.At(e).setValue(2+Math.random()); //random return value is in [0,1)
    }
    
    //print some tetra info
    System.out.printf("\n\nTOP DIM SIMPLEX INFO\n");
    
    i = Triangulation.tetraTable.keySet().iterator();
    while(i.hasNext()){
      Integer key = (Integer)i.next();
      Tetra t = Triangulation.tetraTable.get(key);
      
      System.out.printf("Tetra %d: \n",key);
      System.out.printf("   Num local tetras: %d\n", t.getLocalTetras().size());
      System.out.printf("   Volume: %f\n",Volume.valueAt(t)); 
      
      //coords
      System.out.printf("   Coords: ");
      Iterator j = t.getLocalVertices().iterator();
      while(j.hasNext()){
        Vertex v = (Vertex)j.next();
        System.out.printf("[v%d: (",v.getIndex());
        System.out.print(Coord3D.coordAt(v,t));
        System.out.print(")]");
      }
      System.out.printf("\n");
    }
    
    //pick some arbitrary tetra
    i = Triangulation.tetraTable.keySet().iterator();
    Tetra tetra = Triangulation.tetraTable.get((Integer)i.next());

    //root sgc
    SceneGraphComponent sgc_root = new SceneGraphComponent();
    SceneGraphComponent sgc_tetra1 = sgcFromTetra(tetra, new AffineTransformation(3), Color.RED);
    SceneGraphComponent sgc_points = sgcFromPoints(new Vector(0,0,0));
    sgc_root.addChild(sgc_tetra1);
    sgc_root.addChild(sgc_points);
    
    //loop through tetra's neighbors, adding them in the right place
    i = tetra.getLocalTetras().iterator();
    while(i.hasNext()){
      Tetra tetra2 = (Tetra)i.next();
      AffineTransformation atTrans12 = CoordTrans3D.affineTransAt(tetra2,tetra);
      sgc_root.addChild(sgcFromTetra(tetra2, atTrans12, Color.WHITE));
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
  
  public static SceneGraphComponent sgcFromPoints(Vector...points){
    
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
      
      Point pt = null;
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
