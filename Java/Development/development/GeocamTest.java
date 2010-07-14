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
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.content.ContentAppearance;
import de.jreality.plugin.content.ContentLoader;
import de.jreality.plugin.content.ContentTools;
import de.jreality.scene.Appearance;
import de.jreality.scene.proxy.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
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
    SceneGraphComponent sgc_tetra = sgcFromTetra(tetra);
    sgc_root.addChild(sgc_tetra);
    //sgc_root.addChild(sgc_frust);
    //sgc_root.addChild(sgc_intpoints);
    
    //jrviewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root);
    jrv.registerPlugin(new ContentAppearance());
    jrv.registerPlugin(new ContentLoader());
    jrv.registerPlugin(new ContentTools());
    jrv.startup();
  }
  
  public static SceneGraphComponent sgcFromTetra(Tetra tetra){
    
    //create a sgc for the tetra
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
    dps.setDiffuseColor(Color.WHITE);
    dps.setTransparency(0.6d);
    
    //set appearance
    sgc_tetra.setAppearance(app_tetra);

    //create list of verts
    double[][] vertlist = new double[4][3];
    
    int count = 0;
    Iterator j = tetra.getLocalVertices().iterator();
    
    while(j.hasNext()){
      Vertex v = (Vertex)j.next();
      Point vpoint = Coord3D.coordAt(v,tetra);

      vertlist[count] = new double[] {
          vpoint.getComponent(0), 
          vpoint.getComponent(1), 
          vpoint.getComponent(2)};
      count++;
    }
    
    //set combinatorics
    int[][] facelist = new int[4][3];
    facelist[0] = new int[] {0,2,1};
    facelist[1] = new int[] {0,2,3};
    facelist[2] = new int[] {2,1,3};
    facelist[3] = new int[] {1,0,3};
    
    int[][] edgelist = new int[6][2];
    edgelist[0] = new int[] {0,2};
    edgelist[1] = new int[] {2,1};
    edgelist[2] = new int[] {1,0};
    edgelist[3] = new int[] {0,3};
    edgelist[4] = new int[] {1,3};
    edgelist[5] = new int[] {2,3};
    
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
