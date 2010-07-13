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

    //create a sgc for the tetra
    SceneGraphComponent sgc_tetra = new SceneGraphComponent();
    Appearance app_tetra = new Appearance();
    app_tetra.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_tetra.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_tetra.setAttribute(CommonAttributes.TUBES_DRAW, false);
    app_tetra.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    app_tetra.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    //app_tetra.setAttribute(CommonAttributes.TRANSPARENCY, 0.7d);
    sgc_tetra.setAppearance(app_tetra);

    double[][] vertlist = new double[4][3];
    double[] offset = new double[] {5, 0, 0};
    
    int count = 0;
    Iterator j = tetra.getLocalVertices().iterator();
    
    while(j.hasNext()){
      Vertex v = (Vertex)j.next();
      Point vpoint = Coord3D.coordAt(v,tetra);

      vertlist[count] = new double[] {
          vpoint.getComponent(0) + offset[0], 
          vpoint.getComponent(1) + offset[1], 
          vpoint.getComponent(2) + offset[2] };
      count++;
    }
    
    int[][] facelist = new int[4][3];
    facelist[0] = new int[] {0,2,1};
    facelist[1] = new int[] {0,2,3};
    facelist[2] = new int[] {2,1,3};
    facelist[3] = new int[] {1,0,3};
    
    IndexedFaceSetFactory ifsf_tetra = new IndexedFaceSetFactory();
    ifsf_tetra.setVertexCount(4);
    ifsf_tetra.setVertexCoordinates(vertlist);
    ifsf_tetra.setFaceCount(4);
    ifsf_tetra.setFaceIndices(facelist);
    ifsf_tetra.update();
    sgc_tetra.setGeometry(ifsf_tetra.getGeometry());
    
    //root sgc
    SceneGraphComponent sgc_root = new SceneGraphComponent();
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
}
