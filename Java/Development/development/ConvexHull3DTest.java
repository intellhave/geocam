package development;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import de.jreality.geometry.CoordinateSystemFactory;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.tools.RotateTool;
import de.jreality.util.SceneGraphUtility;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

public class ConvexHull3DTest {
  private static SceneGraphComponent sgc_root, sgc_point;
  private static ConvexHull3D hull;
  private static CoordinateSystemFactory factory;
  // private static Stack<ConvexHull3D> done = new Stack<ConvexHull3D>();
  // private static Stack<ConvexHull3D> undone = new Stack<ConvexHull3D>();
  private static Stack<List<SceneGraphComponent>> done = new Stack<List<SceneGraphComponent>>();
  private static Stack<List<SceneGraphComponent>> undone = new Stack<List<SceneGraphComponent>>();

  public static void main(String[] args) {
    ArrayList<Vector> list = new ArrayList<Vector>();
    // list.add(new Vector3D(1, 0, 0));
    // list.add(new Vector3D(0, 1, 0));
    // list.add(new Vector3D(0, 0, 1));
    // list.add(new Vector3D(-1, 0, 0));
    // list.add(new Vector3D(0, -1, 0));
    // list.add(new Vector3D(0, 0, -.5));

    // dodecahedron
    double p = (1 + Math.sqrt(5)) / 2;
    // order matters:
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

//    list.add(new Vector3D(1, -1, -1));
//    list.add(new Vector3D(1, -1, 1));
//    list.add(new Vector3D(1, 1, -1));
//    list.add(new Vector3D(1, 1, 1));
//    list.add(new Vector3D(-1, -1, 1));
//    list.add(new Vector3D(-1, -1, -1));
//    list.add(new Vector3D(-1, 1, -1));
//    list.add(new Vector3D(-1, 1, 1));
//
//    list.add(new Vector3D(-1 / p, -p, 0));
//    list.add(new Vector3D(1 / p, p, 0));
//    list.add(new Vector3D(-1 / p, p, 0));
//    list.add(new Vector3D(1 / p, -p, 0));
//
//    list.add(new Vector3D(0, -1 / p, p));
//    list.add(new Vector3D(0, -1 / p, -p));
//    list.add(new Vector3D(0, 1 / p, p));
//    list.add(new Vector3D(0, 1 / p, -p));
//
//    list.add(new Vector3D(-p, 0, -1 / p));
//    list.add(new Vector3D(-p, 0, 1 / p));
//    list.add(new Vector3D(p, 0, -1 / p));
//    list.add(new Vector3D(p, 0, 1 / p));

    // cube with hat
    // list.add(new Vector3D(0, 0, 0));
    // list.add(new Vector3D(1, 0, 0));
    // list.add(new Vector3D(0, 0, 1));
    // list.add(new Vector3D(0, 1, 0));
    // list.add(new Vector3D(1, 0, 1));
    // list.add(new Vector3D(1, 1, 1));
    // list.add(new Vector3D(0, 1, 1));
    // list.add(new Vector3D(1, 1, 0));
    // list.add(new Vector3D(.5, .5, 2));

 //    Collections.shuffle(list);

    sgc_root = SceneGraphUtility
    .createFullSceneGraphComponent();
    sgc_root.addTool(new RotateTool());
    sgc_point = new SceneGraphComponent();
    sgc_root.addChild(sgc_point);
    hull = new ConvexHull3D(list);

    drawHull(hull);
    done.push(copyOf(sgc_root.getChildComponents()));

    for (int i = 0; i < hull.getNumberFaces(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(hull.getFaceAt(i).getGeometry(Color.blue));
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

  public static void removeChildren(SceneGraphComponent sgc) {
    boolean finished = false;
    while (!finished) {
      try {
        sgc.removeChild(sgc.getChildComponent(0));
      } catch (IndexOutOfBoundsException e) {
        finished = true;
      }
    }
  }

  public static void drawHull(ConvexHull3D hull) {
    removeChildren(sgc_root);
    sgc_root.addChild(sgc_point);

    for (int i = 0; i < hull.getNumberFaces(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(hull.getFaceAt(i).getGeometry(Color.blue));
      sgc_root.addChild(sgc);
    }
  }

  public static void colorVisible(ConvexHull3D hull) {
    ArrayList<EmbeddedFace> hiddenFaces = hull.getHiddenFaces();
    ArrayList<EmbeddedFace> visibleFaces = hull.getVisibleFaces();

    removeChildren(sgc_root);
    sgc_root.addChild(sgc_point);

    for (int i = 0; i < hiddenFaces.size(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(hiddenFaces.get(i).getGeometry(Color.blue));
      sgc_root.addChild(sgc);
    }
    for (int i = 0; i < visibleFaces.size(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(visibleFaces.get(i).getGeometry(Color.yellow));
      sgc_root.addChild(sgc);
    }
  }

  public static void colorNewFaces(ConvexHull3D hull) {
    ArrayList<EmbeddedFace> hiddenFaces = hull.getHiddenFaces();
    ArrayList<EmbeddedFace> newFaces = hull.getNewFaces();

    removeChildren(sgc_root);
    sgc_root.addChild(sgc_point);

    for (int i = 0; i < hiddenFaces.size(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(hiddenFaces.get(i).getGeometry(Color.blue));
      sgc_root.addChild(sgc);
    }
    for (int i = 0; i < newFaces.size(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(newFaces.get(i).getGeometry(Color.red));
      sgc_root.addChild(sgc);
    }
  }

  public static List<SceneGraphComponent> copyOf(List<SceneGraphComponent> list) {
    ArrayList<SceneGraphComponent> copy = new ArrayList<SceneGraphComponent>();
    for (int i = 0; i < list.size(); i++) {
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(list.get(i).getGeometry());
      copy.add(sgc);
    }
    return copy;
  }

  // the user interface as a plugin
  // see
  // http://www3.math.tu-berlin.de/jreality/api/de/jreality/plugin/basic/ViewShrinkPanelPlugin.html
  // ===========================================================================================
  static class UIPanel_Model extends ViewShrinkPanelPlugin {
    private enum Steps {
      drawPoint, colorVisible, addNewFaces, mergeFaces
    }

    private Steps lastStep = Steps.mergeFaces;
    private JButton forwardButton, backButton;
    private JButton stepButton;

    private void makeUIComponents() {
      class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          if (!done.firstElement().equals(done.peek())) {
            System.out.println("undoing");
            undone.push(done.pop());
            List<SceneGraphComponent> children = done.peek();
            System.out.println("children.size() = " + children.size());
            removeChildren(sgc_root);
            for (int i = 0; i < children.size(); i++) {
              sgc_root.addChild(children.get(i));
            }
          }
        }
      }

      class StepButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          if (!undone.isEmpty()) {
            System.out.println("redoing");
            done.push(undone.pop());
            List<SceneGraphComponent> children = done.peek();
            removeChildren(sgc_root);
            for (int i = 0; i < children.size(); i++)
              sgc_root.addChild(children.get(i));
          } else {
            if (lastStep == Steps.mergeFaces) {
              hull.addPoint();
              sgc_point.setGeometry(getLineGeometry(hull.lastPoint()));
              lastStep = Steps.drawPoint;
              colorVisible(hull);
              done.push(copyOf(sgc_root.getChildComponents()));
              lastStep = Steps.colorVisible;
            } else if (lastStep == Steps.colorVisible) {
              colorNewFaces(hull);
              done.push(copyOf(sgc_root.getChildComponents()));
              lastStep = Steps.addNewFaces;
            } else if (lastStep == Steps.addNewFaces) {
              drawHull(hull);
              done.push(copyOf(sgc_root.getChildComponents()));
              lastStep = Steps.mergeFaces;
            }
          }
        }

        private Geometry getLineGeometry(Vector vector) {
          ArrayList<Vector> vertices = hull.getVertices();
          vertices.add(0, vector);
          double[][] ilsf_verts = new double[vertices.size()][3];
          int[][] ilsf_edges = new int[vertices.size() - 1][2];

          for (int i = 0; i < vertices.size(); i++) {
            ilsf_verts[i] = vertices.get(i).getVectorAsArray();
            if (i > 0)
              ilsf_edges[i - 1] = new int[] { 0, i };
          }

          IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
          ilsf.setVertexCount(ilsf_verts.length);
          ilsf.setVertexCoordinates(ilsf_verts);
          ilsf.setEdgeCount(ilsf_edges.length);
          ilsf.setEdgeIndices(ilsf_edges);

          ilsf.update();
          return ilsf.getGeometry();
        }
      }

      // class ForwardButtonListener implements ActionListener {
      // @Override
      // public void actionPerformed(ActionEvent arg0) {
      // if (!undone.isEmpty()) {
      // done.push(undone.pop());
      // drawHull(done.peek());
      // } else {
      // if (hull.addPoint()) {
      // drawHull(hull);
      // done.push(new ConvexHull3D(hull));
      // }
      // }
      // }
      // }
      //      
      // class BackButtonListener implements ActionListener {
      // @Override
      // public void actionPerformed(ActionEvent arg0) {
      // if(!done.firstElement().equals(done.peek())) {
      // undone.push(done.pop());
      // drawHull(done.peek());
      // }
      // }
      // }
      //
      // forwardButton = new JButton("forward");
      // forwardButton.addActionListener(new ForwardButtonListener());
      // shrinkPanel.add(forwardButton);
      //      
      // backButton = new JButton("back");
      // backButton.addActionListener(new BackButtonListener());
      // shrinkPanel.add(backButton);
      backButton = new JButton("back");
      backButton.addActionListener(new BackButtonListener());
      shrinkPanel.add(backButton);
      stepButton = new JButton("step");
      stepButton.addActionListener(new StepButtonListener());
      shrinkPanel.add(stepButton);

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
