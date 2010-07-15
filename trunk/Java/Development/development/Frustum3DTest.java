package development;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

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

public class Frustum3DTest {
  private static Frustum3D f1, f2, f3, f2_initial;
  private static SceneGraphComponent sgc_root, sgcf1, sgcf2, sgcf3;
  
  public static void main(String[] args) throws Exception {
    Vector3D f1a = new Vector3D(1, 1, 1);
    Vector3D f1b = new Vector3D(-1, 1, 1);
    Vector3D f1c = new Vector3D(-1, -1, 1);

    Vector3D f2a = new Vector3D(1, 2, 1);
    Vector3D f2b = new Vector3D(-1, -1, 1);
    Vector3D f2c = new Vector3D(1, -1, 1);
  
    f1 = new Frustum3D(f1a, f1b, f1c);
    f2 = new Frustum3D(f2a, f2b, f2c);
    f2_initial = new Frustum3D(f2a, f2b, f2c);

    f3 = Frustum3D.intersect(f1, f2);
 

    if (f3 == null)
      System.out.println("no intersection");

    sgc_root = new SceneGraphComponent();

    sgcf1 = new SceneGraphComponent();
    sgcf1.setGeometry(getGeometry(f1, Color.blue));
    sgc_root.addChild(sgcf1);

    sgcf2 = new SceneGraphComponent();
    sgcf2.setGeometry(getGeometry(f2, Color.green));
    sgc_root.addChild(sgcf2);

    sgcf3 = new SceneGraphComponent();
    sgcf3.setGeometry(getIntersectionGeometry(f3, Color.red));
    sgc_root.addChild(sgcf3);

    sgc_root.addTool(new RotateTool());

    // set up the main JRViewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setShowPanelSlots(true, false, false, false); // show left panel only
    jrv.registerPlugin(new UIPanel_Model());
    jrv.setShowPanelSlots(true,false,false,false);
    jrv.setContent(sgc_root);
    jrv.startup();
  }
  
  public static void toggleVisible(int index) {
    if(index == 1) sgcf1.setVisible(!sgcf1.isVisible());
    if(index == 2) sgcf2.setVisible(!sgcf2.isVisible());
    if(index == 3) sgcf3.setVisible(!sgcf3.isVisible());

  }
  
  // rotate by angle (in radians) about z axis
  public static void updateOrientation(double angle) throws Exception {
    Matrix Rz = new Matrix( Math.cos(angle),  Math.sin(angle),  0, 0, 
                        -Math.sin(angle), Math.cos(angle), 0, 0,
                        0, 0, 1, 0,
                        0, 0, 0, 1 );
    ArrayList<Vector3D> newVectors = new ArrayList<Vector3D>();
    for(int i = 0; i < f2.getNumberVectors(); i++) {
      double[] vector = f2_initial.getVectorAt(i).getVectorAsArray();
      Rz.transformVector(vector);
      Vector3D normalized = new Vector3D(vector[0], vector[1], vector[2]);
     // normalized.normalize();
      newVectors.add(normalized);
    }
    f2 = new Frustum3D(newVectors);
    f3 = Frustum3D.intersect(f1, f2);
    sgcf2.setGeometry(getGeometry(f2, Color.green));
    sgcf3.setGeometry(getIntersectionGeometry(f3, Color.red));

  }

  private static Geometry getGeometry(Frustum3D f, Color color) {
    double[][] ifsf_verts = new double[f.getNumberVectors() + 1][3];
    int[][] ifsf_faces = new int[f.getNumberVectors()][3];
    ifsf_verts[0] = new double[] { 0, 0, 0 };

    for (int i = 1; i < f.getNumberVectors() + 1; i++) {
      ifsf_verts[i] = f.getVectorAt(i - 1).getVectorAsArray();
      if (i - 1 == f.getNumberVectors() - 1)
        ifsf_faces[i - 1] = new int[] { 0, i, 1 };
      else
        ifsf_faces[i - 1] = new int[] { 0, i, i + 1 };
    }

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    Color[] colors = new Color[ifsf_faces.length];
    for (int i = 0; i < ifsf_faces.length; i++)
      colors[i] = color;
    ifsf.setFaceColors(colors);
    ifsf.update();
    return ifsf.getGeometry();
  }

  private static Geometry getIntersectionGeometry(Frustum3D f, Color color) {
    double[][] ifsf_verts = new double[f.getNumberVectors() + 1][3];
    int[][] ifsf_faces = new int[1][f.getNumberVectors() + 1];
    ifsf_verts = new double[f.getNumberVectors()][3];

    for (int i = 0; i < f.getNumberVectors(); i++) {
      ifsf_verts[i] = f.getVectorAt(i).getVectorAsArray();
      ifsf_faces[0][i] = i;
    }
    ifsf_faces[0][f.getNumberVectors()] = 0;

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    Color[] colors = new Color[ifsf_faces.length];
    for (int i = 0; i < ifsf_faces.length; i++)
      colors[i] = color;
    ifsf.setFaceColors(colors);
    ifsf.update();
    return ifsf.getGeometry();
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
          try {
            updateOrientation(slider.getValue()*Math.PI/180);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      };
      
      class BoxListener implements ActionListener {
        private int index;
        public BoxListener(int i) {
          index = i;
        }
        @Override
        public void actionPerformed(ActionEvent arg0) {
          toggleVisible(index);
        }
      };
      
      slider = new JSlider(0, 360, 0);
      slider.addChangeListener(sliderListener);
      shrinkPanel.add(slider);
      
      box1 = new JCheckBox();
      box1.addActionListener(new BoxListener(1));
      box2 = new JCheckBox();
      box2.addActionListener(new BoxListener(2));
      box3 = new JCheckBox();
      box3.addActionListener(new BoxListener(3));
      
      shrinkPanel.add(new JLabel("Hide blue frustum"));
      shrinkPanel.add(box1);
      shrinkPanel.add(new JLabel("Hide green frustum"));
      shrinkPanel.add(box2);
      shrinkPanel.add(new JLabel("Hide red intersection"));
      shrinkPanel.add(box3);


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
      PluginInfo info = new PluginInfo("Set angle for green frustum", "");
      return info;
    }
  };
}
