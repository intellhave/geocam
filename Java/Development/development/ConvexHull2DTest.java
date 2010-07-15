package development;

import java.awt.Color;
import java.util.ArrayList;

import de.jreality.plugin.JRViewer;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.tools.RotateTool;
import development.ConvexHull3DTest.UIPanel_Model;

public class ConvexHull2DTest {
  private static SceneGraphComponent sgc_root;
  private static ConvexHull2D hull;

  public static void main(String[] args) {
    ArrayList<Vector2D> list = new ArrayList<Vector2D>();
    list.add(new Vector2D(1, 0));
    list.add(new Vector2D(0, 1));
    list.add(new Vector2D(-1, 0));
    list.add(new Vector2D(1, 1));

    sgc_root = new SceneGraphComponent();
    sgc_root.addTool(new RotateTool());
    hull = new ConvexHull2D(list);

    Face f = hull.getAsFace();
    sgc_root.setGeometry(f.getGeometry(Color.blue));

    // set up the main JRViewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.registerPlugin(new UIPanel_Model());
    jrv.setContent(sgc_root);
    jrv.startup();
  }
}
