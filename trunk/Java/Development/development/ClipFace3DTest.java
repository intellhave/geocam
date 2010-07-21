package development;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.junit.Test;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Matrix;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.ViewShrinkPanelPlugin;
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
import de.jtem.jrworkspace.plugin.Controller;
import de.jtem.jrworkspace.plugin.PluginInfo;
import development.Frustum3DTest.UIPanel_Model;

import Geoquant.Length;
import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class ClipFace3DTest {
  private static SceneGraphComponent sgc_root, sgc_frustum, sgc_result;
  private static Frustum3D frustum, frustum_initial;
  private static Tetra tetra;

  @Test
  public void testSectorIntersection() {
    Frustum3D frustum = new Frustum3D(new Vector(0, 1, 0), new Vector(0, 0, 1),
        new Vector(1, 0, 0));
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(-.5, .5, .5), 0).toString(), "(0.0, 0.5, 0.5)");
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(.5, .5, -.5), 0), null);
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(.5, .5, -.5), 1), null);
    assertEquals(frustum.findSectorIntersection(new Vector(.5, .5, .5),
        new Vector(.5, .5, -.5), 2).toString(), "(0.5, 0.5, 0.0)");
  }

  @Test
  public void testIntersectionWithFace() {
    Frustum3D frustum = new Frustum3D(new Vector(0, 1, 0), new Vector(0, 0, 1),
        new Vector(1, 0, 0));
    EmbeddedFace face = new EmbeddedFace(new Vector(0, 0, 1), new Vector(0, -1,
        0), new Vector(0, 1, 0));
    assertEquals(frustum.findIntersectionWithFace(face, new Vector(-1, 0, 0))
        .toString(), "(0.0, 0.0, 0.0)");
    assertEquals(frustum.findIntersectionWithFace(face, new Vector(0, 1, 1)),
        null);
  }

  public static void main(String[] args) {
    TriangulationIO
        .readTriangulation("Data/Triangulations/3DManifolds/3-torus.xml");
    Iterator<Integer> i = Triangulation.tetraTable.keySet().iterator();

    tetra = Triangulation.tetraTable.get(i.next());

    Iterator<Edge> ie = tetra.getLocalEdges().iterator();
    while (ie.hasNext()) {
      Edge e = ie.next();
      Length.At(e).setValue(2);
    }

    Vector v1 = new Vector(0, 0, 1);
    Vector v2 = new Vector(1, .5, 0);
    Vector v3 = new Vector(.5, 1, 0);
    v1.scale(3);
    v2.scale(3);
    v3.scale(3);

    frustum = new Frustum3D(v1, v2, v3);
    frustum_initial = new Frustum3D(v1, v2, v3);
    ArrayList<Vector> result = frustum.clipFace(tetra);
    System.out.println("found these vertices: ");
    for (Vector v : result) {
      System.out.println(v);
    }

    // root sgc
    sgc_root = new SceneGraphComponent();
    SceneGraphComponent sgc_tetra1 = sgcFromTetra(tetra,
        new AffineTransformation(3), Color.RED);
    ArrayList<Vector> v = new ArrayList<Vector>();
    v.add(new Vector(0, 0, 0));
    sgc_result = sgcFromPoints(result);
    sgc_frustum = sgcFromFrustum(frustum, Color.blue);
    sgc_root.addChild(sgc_tetra1);
    sgc_root.addChild(sgc_result);
    sgc_root.addChild(sgc_frustum);

    // jrviewer
    JRViewer jrv = new JRViewer();
    jrv.addBasicUI();
    jrv.setContent(sgc_root);
    jrv.registerPlugin(new ContentAppearance());
    jrv.registerPlugin(new ContentLoader());
    jrv.registerPlugin(new ContentTools());
    jrv.registerPlugin(new UIPanel_Model());
    jrv.setShowPanelSlots(true,false,false,false);
    jrv.startup();
  }

  public static SceneGraphComponent sgcFromPoints(ArrayList<Vector> points) {

    // create the sgc
    SceneGraphComponent sgc_points = new SceneGraphComponent();

    // create appearance
    Appearance app_points = new Appearance();

    // set some basic attributes
    app_points.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app_points.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);

    // set point shader
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(0.05);
    dps.setDiffuseColor(Color.BLUE);

    // set appearance
    sgc_points.setAppearance(app_points);

    // set vertlist
    double[][] vertlist = new double[points.size()][3];
    for (int i = 0; i < points.size(); i++) {
      vertlist[i] = new double[] { points.get(i).getComponent(0),
          points.get(i).getComponent(1), points.get(i).getComponent(2) };
    }

    // create geometry with pointsetfactory
    PointSetFactory psf = new PointSetFactory();
    psf.setVertexCount(points.size());
    psf.setVertexCoordinates(vertlist);
    psf.update();

    // set geometry
    sgc_points.setGeometry(psf.getGeometry());

    // return
    return sgc_points;
  }

  public static SceneGraphComponent sgcFromTetra(Tetra tetra,
      AffineTransformation affineTrans, Color color) {

    // create a sgc for the tetra, after applying specified affine
    // transformation
    SceneGraphComponent sgc_tetra = new SceneGraphComponent();

    // create appearance
    Appearance app_tetra = new Appearance();

    // set some basic attributes
    app_tetra.setAttribute(CommonAttributes.FACE_DRAW, true);
    app_tetra.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_tetra.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_tetra.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_tetra.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);

    // set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_tetra, true);

    // line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setDiffuseColor(Color.BLACK);

    // polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(color);
    dps.setTransparency(0.6d);

    // set appearance
    sgc_tetra.setAppearance(app_tetra);

    // create list of verts
    double[][] vertlist = new double[4][3];

    int count = 0;
    Iterator<Vertex> i = tetra.getLocalVertices().iterator();

    while (i.hasNext()) {
      Vertex v = i.next();

      Vector pt = null;
      try {
        pt = affineTrans.affineTransPoint(Coord3D.coordAt(v, tetra));
      } catch (Exception e) {
        e.printStackTrace();
      }

      vertlist[count] = new double[] { pt.getComponent(0), pt.getComponent(1),
          pt.getComponent(2) };
      count++;
    }

    // set combinatorics
    int[][] facelist = new int[][] { new int[] { 0, 2, 1 },
        new int[] { 0, 2, 3 }, new int[] { 2, 1, 3 }, new int[] { 1, 0, 3 } };

    int[][] edgelist = new int[][] { new int[] { 0, 2 }, new int[] { 2, 1 },
        new int[] { 1, 0 }, new int[] { 0, 3 }, new int[] { 1, 3 },
        new int[] { 2, 3 } };

    // use face factory to create geometry
    IndexedFaceSetFactory ifsf_tetra = new IndexedFaceSetFactory();

    ifsf_tetra.setVertexCount(4);
    ifsf_tetra.setVertexCoordinates(vertlist);

    ifsf_tetra.setFaceCount(4);
    ifsf_tetra.setFaceIndices(facelist);

    ifsf_tetra.setEdgeCount(6);
    ifsf_tetra.setEdgeIndices(edgelist);

    ifsf_tetra.update();

    // set geometry
    sgc_tetra.setGeometry(ifsf_tetra.getGeometry());

    // return
    return sgc_tetra;
  }

  public static SceneGraphComponent sgcFromFrustum(Frustum3D frustum,
      Color color) {

    SceneGraphComponent sgc_frustum = new SceneGraphComponent();

    // create appearance
    Appearance app_tetra = new Appearance();

    // set some basic attributes
    app_tetra.setAttribute(CommonAttributes.FACE_DRAW, true);
    app_tetra.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_tetra.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_tetra.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
    app_tetra.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);

    // set shaders
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_tetra, true);

    // line shader
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setTubeDraw(false);
    dls.setDiffuseColor(Color.BLACK);

    // polygon shader
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs.getPolygonShader();
    dps.setDiffuseColor(color);
    dps.setTransparency(0.6d);

    // set appearance
    sgc_frustum.setAppearance(app_tetra);

    double[][] vertlist = new double[frustum.getNumberRays() + 1][3];
    int[][] edgelist = new int[frustum.getNumberRays()][3];
    int[][] facelist = new int[frustum.getNumberRays()][3];
    vertlist[0] = new double[] { 0, 0, 0 };

    for (int i = 1; i < frustum.getNumberRays() + 1; i++) {
      vertlist[i] = frustum.getVectorAt(i - 1).getVectorAsArray();
      edgelist[i - 1] = new int[] { 0, i };
      if (i - 1 == frustum.getNumberRays() - 1)
        facelist[i - 1] = new int[] { 0, i, 1 };
      else
        facelist[i - 1] = new int[] { 0, i, i + 1 };
    }

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(vertlist.length);
    ifsf.setVertexCoordinates(vertlist);
    ifsf.setFaceCount(facelist.length);
    ifsf.setFaceIndices(facelist);
    ifsf.setGenerateEdgesFromFaces(true);

    // use face factory to create geometry
    IndexedFaceSetFactory ifsf_frustum = new IndexedFaceSetFactory();

    ifsf_frustum.setVertexCount(vertlist.length);
    ifsf_frustum.setVertexCoordinates(vertlist);

    ifsf_frustum.setFaceCount(facelist.length);
    ifsf_frustum.setFaceIndices(facelist);

    ifsf_frustum.setEdgeCount(edgelist.length);
    ifsf_frustum.setEdgeIndices(edgelist);

    ifsf_frustum.update();

    // set geometry
    sgc_frustum.setGeometry(ifsf_frustum.getGeometry());

    // return
    return sgc_frustum;
  }

  public static void updateOrientation(double angle) throws Exception {
    Matrix Rx = new Matrix(1, 0, 0, 0, 0, Math.cos(angle), Math.sin(angle), 0,
        0, -Math.sin(angle), Math.cos(angle), 0, 0, 0, 0, 1);
    Matrix Rz = new Matrix( Math.cos(angle),  Math.sin(angle),  0, 0, 
        -Math.sin(angle), Math.cos(angle), 0, 0,
        0, 0, 1, 0,
        0, 0, 0, 1 );
    
    ArrayList<Vector> newVectors = new ArrayList<Vector>();
    
    for (int i = 0; i < frustum.getNumberRays(); i++) {
      double[] vector = frustum_initial.getVectorAt(i).getVectorAsArray();
      Rz.transformVector(vector);
      Vector v = new Vector(vector[0], vector[1], vector[2]);
      newVectors.add(v);
    }
    frustum = new Frustum3D(newVectors);
    sgc_root.removeChild(sgc_frustum);
    sgc_root.remove(sgc_result);
    sgc_frustum = sgcFromFrustum(frustum, Color.blue);
    sgc_root.addChild(sgc_frustum);
    ArrayList<Vector> result = frustum.clipFace(tetra);
    sgc_result = sgcFromPoints(result);
    sgc_root.addChild(sgc_result);

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
            updateOrientation(slider.getValue() * Math.PI / 180);
          } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      };

      slider = new JSlider(0, 360, 0);
      slider.addChangeListener(sliderListener);
      shrinkPanel.add(slider);

      // specify layout
      shrinkPanel.setLayout(new BoxLayout(shrinkPanel.getContentPanel(),
          BoxLayout.Y_AXIS));
      shrinkPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    }

    @Override
    public void install(Controller c) throws Exception {
      makeUIComponents();
      super.install(c);
    }

    @Override
    public PluginInfo getPluginInfo() {
      PluginInfo info = new PluginInfo("Set angle for frustum", "");
      return info;
    }
  };
}
