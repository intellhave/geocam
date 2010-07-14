package development;

import java.util.ArrayList;

import de.jreality.plugin.JRViewer;
import de.jreality.scene.SceneGraphComponent;
import development.Frustum3DTest.UIPanel_Model;

public class ConvexHull3DTest {
  private static SceneGraphComponent sgc_root, sgcf1, sgcf2, sgcf3;

  
  public static void main(String[] args) {
    ArrayList<Vector3D> list = new ArrayList<Vector3D>();
    list.add(new Vector3D(1, 0, 0));
    list.add(new Vector3D(0, 1, 0));
    list.add(new Vector3D(0, 0, 1));
    list.add(new Vector3D(0, 0, 0));
//    list.add(new Vector3D(0, -1, 0));
 //   list.add(new Vector3D(0, 0, -1));
 //   list.add(new Vector3D(.75, .75, .75));

    ConvexHull3D hull = new ConvexHull3D(list); 
    
    sgc_root = new SceneGraphComponent();

    for(int i = 0; i < hull.getNumberFaces(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(hull.getFaceAt(i).getGeometry());
      sgc_root.addChild(sgc);
    }
    
 // set up the main JRViewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.registerPlugin(new UIPanel_Model());
    jrv.setContent(sgc_root);
    jrv.startup();
  }

}
