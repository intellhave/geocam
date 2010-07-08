package development;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.SceneGraphComponent;

public class Frustum2DTest {
  public static void main(String[] args) {
    Frustum2D f1 = new Frustum2D(new Vector(0,1), new Vector(2,1));
    Frustum2D f2 = new Frustum2D(new Vector(1,2), new Vector(1,0));
    
    Frustum2D f3 = null;
    try {
      f3 = Frustum2D.intersect(f1, f2);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    double x1 = f3.getLeft().getComponent(0);
    double y1 = f3.getLeft().getComponent(1);
    double x2 = f3.getRight().getComponent(0);
    double y2 = f3.getRight().getComponent(1);
    
    double[][] ifsf_verts = { {0, 0}, {0, 1}, {2, 1}, {1, 2}, {1, 0}, {x1, y1}, {x2, y2} };
    int[][] ifsf_faces = { {0, 1, 2}, {0, 3, 4}, {0, 5, 6} };
    
    SceneGraphComponent sgc_root = new SceneGraphComponent();
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(7);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(3);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.update();
    sgc_root.setGeometry(ifsf.getGeometry());
    
    //set up the main JRViewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root);
    jrv.startup();

  }
}
