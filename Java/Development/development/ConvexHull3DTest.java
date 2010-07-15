package development;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.tools.RotateTool;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

public class ConvexHull3DTest {
  private static SceneGraphComponent sgc_root;
  private static ConvexHull3D hull;
  private static Stack<ConvexHull3D> done = new Stack<ConvexHull3D>();
  private static Stack<ConvexHull3D> undone = new Stack<ConvexHull3D>();

  public static void main(String[] args) {
    ArrayList<Vector3D> list = new ArrayList<Vector3D>();
//    list.add(new Vector3D(1, 0, 0));
//    list.add(new Vector3D(0, 1, 0));
//    list.add(new Vector3D(0, 0, 1));
//    list.add(new Vector3D(-1, 0, 0));
//    list.add(new Vector3D(0, -1, 0));
//    list.add(new Vector3D(0, 0, -.5));
    
// dodecahedron    
    double p = (1+Math.sqrt(5))/2;

    list.add(new Vector3D(-1/p, -p, 0));
    list.add(new Vector3D(1, -1, -1));
    list.add(new Vector3D(-1, -1, 1));
    list.add(new Vector3D(1/p, p, 0));

    
    list.add(new Vector3D(0, -1/p, p));
    list.add(new Vector3D(0, -1/p, -p));
    list.add(new Vector3D(-1, -1, -1));
    list.add(new Vector3D(-1, 1, -1));
    list.add(new Vector3D(0, 1/p, p));
    list.add(new Vector3D(-1, 1, 1));
    list.add(new Vector3D(p, 0, -1/p));
    list.add(new Vector3D(0, 1/p, -p));
    list.add(new Vector3D(-1/p, p, 0));
    list.add(new Vector3D(-p, 0, 1/p));
    list.add(new Vector3D(-p, 0, -1/p));
    list.add(new Vector3D(1/p, -p, 0));
    list.add(new Vector3D(p, 0, 1/p));
    list.add(new Vector3D(1, -1, 1));
    list.add(new Vector3D(1, 1, -1));
    list.add(new Vector3D(1, 1, 1));

     
    // cube with hat
//    list.add(new Vector3D(0, 1, 1));
//    list.add(new Vector3D(1, 1, 0));
//    list.add(new Vector3D(0, 1, 0));
//    list.add(new Vector3D(.5, .5, 2));
//    list.add(new Vector3D(0, 0, 1));
//    list.add(new Vector3D(0, 0, 0));
//    list.add(new Vector3D(1, 0, 1));
//    list.add(new Vector3D(1, 1, 1));
//    list.add(new Vector3D(1, 0, 0));
    
//    Collections.shuffle(list);

    sgc_root = new SceneGraphComponent();
    sgc_root.addTool(new RotateTool());
    hull = new ConvexHull3D(list);

    drawHull(hull);
    done.push(new ConvexHull3D(hull));

    for (int i = 0; i < hull.getNumberFaces(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(hull.getFaceAt(i).getGeometry());
      sgc_root.addChild(sgc);
    }
    sgc_root.addTool(new RotateTool());

    // set up the main JRViewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.registerPlugin(new UIPanel_Model());
    jrv.setShowPanelSlots(true, false, false, false); // show left panel only
    jrv.setContent(sgc_root);
    jrv.startup();
  }

  public static void drawHull(ConvexHull3D hull) {
    boolean finished = false;
    while (!finished) {
      try {
        sgc_root.removeChild(sgc_root.getChildComponent(0));
      } catch (IndexOutOfBoundsException e) {
        finished = true;
      }
    }

    for (int i = 0; i < hull.getNumberFaces(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(hull.getFaceAt(i).getGeometry());
      sgc_root.addChild(sgc);
    }
  }

  // the user interface as a plugin
  // see
  // http://www3.math.tu-berlin.de/jreality/api/de/jreality/plugin/basic/ViewShrinkPanelPlugin.html
  // ===========================================================================================
  static class UIPanel_Model extends ViewShrinkPanelPlugin {
    private JButton forwardButton, backButton;

    private void makeUIComponents() {

      class ForwardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          if (!undone.isEmpty()) {
            done.push(undone.pop());
            drawHull(done.peek());
          } else {
            if (hull.addPoint()) {
              drawHull(hull);
              done.push(new ConvexHull3D(hull));
            }
          }
        }
      }
      
      class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          if(!done.firstElement().equals(done.peek())) {
            undone.push(done.pop());
            drawHull(done.peek());
          }
        }
      }

      forwardButton = new JButton("forward");
      forwardButton.addActionListener(new ForwardButtonListener());
      shrinkPanel.add(forwardButton);
      
      backButton = new JButton("back");
      backButton.addActionListener(new BackButtonListener());
      shrinkPanel.add(backButton);

      shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    }

    @Override
    public void install(Controller c) throws Exception {
      makeUIComponents();
      super.install(c);
    }

    @Override
    public PluginInfo getPluginInfo() {
      PluginInfo info = new PluginInfo("Add next point to hull", "");
      return info;
    }
  };

}
