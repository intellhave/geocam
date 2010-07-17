package development;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Matrix;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.tools.RotateTool;
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;

public class Frustum2DTest {
  private static SceneGraphComponent sgc_root, sgc_face, sgc_frustum, sgc_initial_face;
  private static Frustum2D frustum, frustum_initial;
  private static Face face_initial, face;

  public static void main(String[] args) {
    frustum = new Frustum2D(new Vector(0, 4), new Vector(4, 4));
    frustum_initial = new Frustum2D(frustum);
    face_initial = new Face(new Vector(0, 1), new Vector(2, 1),
        new Vector(1, 2));
    face = frustum.clipFace(face_initial);

    sgc_face = new SceneGraphComponent();
    if (face == null) {
      sgc_face.setGeometry(null);
      System.out.println("intersection is empty");
    } else {
      sgc_face.setGeometry(face.getGeometry(Color.red));
    }
    sgc_frustum = new SceneGraphComponent();
    sgc_frustum.setGeometry(getGeometry(frustum, Color.green));
    
    sgc_initial_face = new SceneGraphComponent();
    sgc_initial_face.setGeometry(face_initial.getGeometry(Color.blue));

    sgc_root = new SceneGraphComponent();
    sgc_root.addChild(sgc_face);
    sgc_root.addChild(sgc_frustum);
    sgc_root.addChild(sgc_initial_face);

    sgc_root.addTool(new RotateTool());

    // set up the main JRViewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setShowPanelSlots(true, false, false, false); // show left panel only
    jrv.registerPlugin(new UIPanel_Model());
    jrv.setContent(sgc_root);
    jrv.startup();

  }

  // public static void toggleVisible(int index) {
  // if (index == 1)
  // sgc_face.setVisible(!sgc_face.isVisible());
  // if (index == 2)
  // sgc_frustum.setVisible(!sgc_frustum.isVisible());
  // if (index == 3)
  // sgcf3.setVisible(!sgcf3.isVisible());
  // }

  private static Geometry getGeometry(Frustum2D f, Color color) {
    double[][] ifsf_verts = new double[3][3];
    int[][] ifsf_faces = new int[1][3];

    ifsf_verts[0] = new double[] { 0, 0, 0 };
    ifsf_verts[1][0] = f.getRight().getVectorAsArray()[0];
    ifsf_verts[1][1] = f.getRight().getVectorAsArray()[1];
    ifsf_verts[1][2] = 0;

    ifsf_verts[2][0] = f.getLeft().getVectorAsArray()[0];
    ifsf_verts[2][1] = f.getLeft().getVectorAsArray()[1];
    ifsf_verts[2][2] = 0;

    ifsf_faces = new int[][] { { 0, 1, 2 } };

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(3);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(1);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    Color[] colors = new Color[ifsf_faces.length];
    for (int i = 0; i < ifsf_faces.length; i++)
      colors[i] = color;
    ifsf.setFaceColors(colors);
    ifsf.update();
    return ifsf.getGeometry();
  }

  public static void updateOrientation(double angle) {
    Matrix Rz = new Matrix(Math.cos(angle), Math.sin(angle), 0, 0, -Math
        .sin(angle), Math.cos(angle), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    double[] newLeft = new double[3];
    newLeft[0] = frustum_initial.getLeft().getVectorAsArray()[0];
    newLeft[1] = frustum_initial.getLeft().getVectorAsArray()[1];
    newLeft[2] = 0;

    double[] newRight = new double[3];
    newRight[0] = frustum_initial.getRight().getVectorAsArray()[0];
    newRight[1] = frustum_initial.getRight().getVectorAsArray()[1];
    newRight[2] = 0;
    Rz.transformVector(newLeft);
    Rz.transformVector(newRight);

    frustum = new Frustum2D(newLeft, newRight);
    sgc_frustum.setGeometry(getGeometry(frustum, Color.green));

    face = frustum.clipFace(face_initial);

    if (face == null) {
      System.out.println("intersection is empty");
      sgc_face.setGeometry(null);
    } else {
      sgc_face.setGeometry(face.getGeometry(Color.red));
    }
  }

  // the user interface as a plugin
  // see
  // http://www3.math.tu-berlin.de/jreality/api/de/jreality/plugin/basic/ViewShrinkPanelPlugin.html
  // ===========================================================================================
  static class UIPanel_Model extends ViewShrinkPanelPlugin {
    private JCheckBox box1, box2, box3;
    private JSlider slider;

    private void makeUIComponents() {
      ChangeListener sliderListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent arg0) {
          updateOrientation(slider.getValue() * Math.PI / 180);
        }
      };

      class BoxListener implements ActionListener {
        private int index;

        public BoxListener(int i) {
          index = i;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
          // toggleVisible(index);
        }
      }
      ;

      slider = new JSlider(0, 360, 0);
      slider.addChangeListener(sliderListener);
      shrinkPanel.add(slider);

      box1 = new JCheckBox();
      box1.addActionListener(new BoxListener(1));
      box2 = new JCheckBox();
      box2.addActionListener(new BoxListener(2));
      box3 = new JCheckBox();
      box3.addActionListener(new BoxListener(3));

      // shrinkPanel.add(new JLabel("Hide blue frustum"));
      // shrinkPanel.add(box1);
      // shrinkPanel.add(new JLabel("Hide green frustum"));
      // shrinkPanel.add(box2);
      // shrinkPanel.add(new JLabel("Hide red intersection"));
      // shrinkPanel.add(box3);

      // specify layout
      shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
      shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),
          BoxLayout.Y_AXIS));
    }

    @Override
    public void install(Controller c) throws Exception {
      makeUIComponents();
      super.install(c);
    }

    @Override
    public PluginInfo getPluginInfo() {
      PluginInfo info = new PluginInfo("Rotate green frustum", "");
      return info;
    }
  };
}
