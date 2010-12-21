package view;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.DevelopmentView2D.DevelopmentGeometry;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.util.CameraUtility;
import development.AffineTransformation;
import development.Coord2D;
import development.CoordTrans2D;
import development.EmbeddedTriangulation;

public class DevelopmentViewEmbedded extends JRViewer {
  private SceneGraphComponent sgcRoot = new SceneGraphComponent();
  private SceneGraphComponent sgcPolyhedron = new SceneGraphComponent();

  public DevelopmentViewEmbedded(String filename) {
    
    //setGeometry(filename);
    
    
    Geometry geom = sgcPolyhedron.getGeometry();
    try {
      File file = new File("/Share/workspace/Geocam/Development2D/" + filename);
      geom = Readers.read(file).getGeometry();
    } catch (IOException e) {
      e.printStackTrace();
    }
    sgcPolyhedron.setGeometry(geom);
    sgcRoot.addChild(sgcPolyhedron);
    sgcRoot.addTool(new RotateTool());

    Appearance app_polyhedron = new Appearance();
    app_polyhedron.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app_polyhedron.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app_polyhedron.setAttribute(CommonAttributes.TUBES_DRAW, false);
    app_polyhedron.setAttribute(CommonAttributes.TUBE_RADIUS, 0.01);
    app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
   // app_polyhedron.setAttribute(CommonAttributes.TRANSPARENCY, 0.1d);
    app_polyhedron.setAttribute(CommonAttributes.PICKABLE, true);
    sgcPolyhedron.setAppearance(app_polyhedron);

    this.addBasicUI();

    this.setContent(sgcRoot);
    Scene scene = this.getPlugin(Scene.class);
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    this.startup();

  }
  
  public void changeGeometry(String filename) {
    Geometry geom = sgcPolyhedron.getGeometry();
    try {
      geom = Readers.read(new File(filename)).getGeometry();
    } catch (IOException e) {
      e.printStackTrace();
    }
    sgcPolyhedron.setGeometry(geom);
    Scene scene = this.getPlugin(Scene.class);
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
  }
  
  private Geometry setGeometry(String filename) {
    EmbeddedTriangulation.readEmbeddedSurface(filename);

    Iterator<Integer> itr = null;
    // pick some arbitrary face and source point
    itr = Triangulation.faceTable.keySet().iterator();
    DevelopmentGeometry geometry = new DevelopmentGeometry();

      Face sourceFace = Triangulation.faceTable.get(itr.next());
      List<Vertex> vertices = sourceFace.getLocalVertices();
      double[][] faceverts = new double[3][2];
      for(int j = 0; j < vertices.size(); j++) {
        faceverts[j] = Coord2D.coordAt(vertices.get(j), sourceFace).getVectorAsArray();
      }
      geometry.addFace(faceverts);
      
      
    ArrayList<Color> colors = new ArrayList<Color>();
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

    Color[] colorList = new Color[colors.size()];
    for (int i = 0; i < colors.size(); i++) {
      colorList[i] = colors.get(i);
    }

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();

    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setFaceColors(colorList);
    ifsf.update();
    return ifsf.getGeometry();
    
  }
  
  // class designed to make it easy to use an IndexedFaceSetFactory
  public class DevelopmentGeometry {

    private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
    private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();

    public void addFace(double[][] faceverts) {

      int nverts = faceverts.length;
      int vi = geometry_verts.size();

      int[] newface = new int[nverts];
      for (int k = 0; k < nverts; k++) {
        double[] newvert = new double[3];
        newvert[0] = faceverts[k][0];
        newvert[1] = faceverts[k][1];
        newvert[2] = 1.0;
        geometry_verts.add(newvert);
        newface[k] = vi++;
      }
      geometry_faces.add(newface);
    }

    public double[][] getVerts() {
      return (double[][]) geometry_verts.toArray(new double[0][0]);
    }

    public int[][] getFaces() {
      return (int[][]) geometry_faces.toArray(new int[0][0]);
    }
  };
}
