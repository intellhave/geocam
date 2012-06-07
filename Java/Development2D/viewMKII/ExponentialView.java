package viewMKII;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import markers.ManifoldPosition;
import markers.MarkerAppearance;
import markersMKII.Marker;
import markersMKII.MarkerHandler;
import view.ColorScheme;
import view.SGCMethods.DevelopmentGeometry;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import development.AffineTransformation;
import development.Development;
import development.DevelopmentNode;
import development.Frustum2D;
import development.Vector;

/*********************************************************************************
 * Exponential View (Previously DevelopmentView2D)
 * 
 * This view visualizes a triangulated 2 dimensional surface using the notion of
 * an exponential map. Starting from the face that contains a designated
 * "source point," this view unfolds pieces of the surface into a plane. The
 * result is a representation of a neighborhood of the source point built out of
 * polygons in the plane.
 *********************************************************************************/

public class ExponentialView extends View {
  protected HashMap<Marker, LinkedList<SceneGraphComponent>> sgcpools;

  /*********************************************************************************
   * ExponentialView
   * 
   * This method initializes a new ExponentialView to use a particular
   * development (for calculating the visualization) and color scheme (for
   * coloring the polygons that make up the visualization).
   *********************************************************************************/
  public ExponentialView(Development d, MarkerHandler mh, ColorScheme cs) {
    super(d, mh, cs);
    this.sgcpools = new HashMap<Marker, LinkedList<SceneGraphComponent>>();

    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.5);
    sgcLight.setLight(light);

    // MatrixBuilder.euclidean().translate(0,0,5).assignTo(sgcLight);
    sgcCamera.addChild(sgcLight);
  }

  /*********************************************************************************
   * updateCamera
   * 
   * This method is responsible for positioning the camera such that the entire
   * development can be viewed.
   * 
   * TODO We still need to determine how far to translate along the z axis. The
   * rotation ensures that the source point's "forward direction" points north.
   *********************************************************************************/
  protected void updateCamera() {
    MatrixBuilder.euclidean().translate(0, 0, 5).rotateZ(-Math.PI / 2)
        .assignTo(sgcCamera);
  }

  /*********************************************************************************
   * generateManifoldGeometry
   * 
   * This method constructs the polygons that will make up the development, and
   * places them in the plane. The polygons are constructed via a recursive
   * procedure outlined in one of the 2010 REU papers.
   *********************************************************************************/
  protected void generateManifoldGeometry() {
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    ArrayList<Color> colors = new ArrayList<Color>();
    generateManifoldGeometry(development.getRoot(), colors, geometry);
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

    sgcDevelopment.setGeometry(ifsf.getGeometry());
  }

  private void generateManifoldGeometry(DevelopmentNode node,
      ArrayList<Color> colors, DevelopmentGeometry geometry) {

    double[][] face = node.getClippedFace().getVectorsAsArray();
    geometry.addFace(face, 1.0);
    colors.add(colorScheme.getColor(node));

    for (DevelopmentNode n : node.getChildren())
      generateManifoldGeometry(n, colors, geometry);
  }

  /*********************************************************************************
   * generateObjectGeometry
   * 
   * This method is responsible for placing representations of the markers in
   * the visualization. Due to the nature of this particular view, a single
   * marker may appear in multiple places. This is why we need the sgcpools data
   * structure --- it keeps track of the multiple scene graph components needed
   * to depict each marker in the scene.
   *********************************************************************************/
  protected void generateMarkerGeometry() {
    HashMap<Marker, ArrayList<Vector[]>> markerImages;
    markerImages = new HashMap<Marker, ArrayList<Vector[]>>();
    developMarkers(development.getRoot(), markerImages);

    for (Marker m : markerImages.keySet()) {
      LinkedList<SceneGraphComponent> pool = sgcpools.get(m);

      if (pool == null) {
        pool = new LinkedList<SceneGraphComponent>();
        sgcpools.put(m, pool);
      }

      ArrayList<Vector[]> images = markerImages.get(m);
      if (images == null)
        continue;

      if (images.size() > pool.size()) {
        int sgcCount = images.size() - pool.size();
        for (int jj = 0; jj < 2 * sgcCount; jj++) {
          MarkerAppearance oa = m.getAppearance();
          SceneGraphComponent sgc = oa.prepareNewSceneGraphComponent();
          pool.add(sgc);
          sgcObjects.addChild(sgc);
        }
      }

      int counter = 0;
      for (SceneGraphComponent sgc : pool) {
        if (counter >= images.size()) {
          sgc.setVisible(false);
        } else {
          Vector[] triple = images.get(counter);
          Vector position = triple[0];
          Vector forward = triple[1];
          forward.normalize();
          // Vector left = triple[2];
          // left.normalize();

          double[] matrix = new double[16];
          matrix[0 * 4 + 0] = forward.getComponent(0);
          matrix[0 * 4 + 1] = -forward.getComponent(1);
          matrix[0 * 4 + 2] = 0.0;
          matrix[0 * 4 + 3] = 0.0;

          matrix[1 * 4 + 0] = forward.getComponent(1);
          matrix[1 * 4 + 1] = forward.getComponent(0);
          matrix[1 * 4 + 2] = 0.0;
          matrix[1 * 4 + 3] = 0.0;

          matrix[2 * 4 + 0] = 0.0;
          matrix[2 * 4 + 1] = 0.0;
          matrix[2 * 4 + 2] = 1.0;
          matrix[2 * 4 + 3] = 0.0;

          matrix[3 * 4 + 0] = 0.0;
          matrix[3 * 4 + 1] = 0.0;
          matrix[3 * 4 + 2] = 0.0;
          matrix[3 * 4 + 3] = 1.0;

          MatrixBuilder
              .euclidean()
              .translate(position.getComponent(0), position.getComponent(1),1.0)
              .times(matrix).scale(m.getAppearance().getScale())
              .assignTo(sgc);

          sgc.setVisible(true);
        }
        counter++;
      }
    }
  }

  /*********************************************************************************
   * initializeNewManifold
   * 
   * This method is responsible for initializing (or reinitializing) the scene
   * graph in the event that we wish to display a different manifold.
   *********************************************************************************/
  protected void initializeNewManifold() {
    for (LinkedList<SceneGraphComponent> pool : sgcpools.values()) {
      while (!pool.isEmpty()) {
        SceneGraphComponent sgc = pool.remove();
        sgcObjects.removeChild(sgc);
      }
    }
    sgcpools.clear();
    updateCamera();
    updateGeometry(true, true);
  }

  /*********************************************************************************
   * developMarkers
   * 
   * Given a development, this method determines where particular markers on the
   * manifold should appear in that development, and with what orientation. Once
   * calculated, this data is stored in the input ArrayList "markerImages",
   * which is used to accumulate results across all of the recursive calls to
   * developMarkers.
   *********************************************************************************/
  protected void developMarkers(DevelopmentNode devNode,
      HashMap<Marker, ArrayList<Vector[]>> markerImages) {
    Collection<Marker> localMarkers = markers.getMarkers(devNode.getFace());
    if (localMarkers != null) {

      Frustum2D frustum = devNode.getFrustum();
      AffineTransformation affineTrans = devNode.getAffineTransformation();

      synchronized (localMarkers) {
        for (Marker m : localMarkers) {
          if (!m.isVisible()) continue;

          ManifoldPosition pos = m.getPosition();
          Vector transPos = affineTrans.affineTransPoint(pos.getPosition());
          // check if object image should be clipped by frustum
          if (frustum != null && !frustum.checkInterior(transPos))
            continue;

          // add to image list
          ArrayList<Vector[]> imageList = markerImages.get(m);
          if (imageList == null) {
            imageList = new ArrayList<Vector[]>();
            markerImages.put(m, imageList);
          }
         
          Vector[] triple = new Vector[3];
          triple[0] = transPos;
          triple[1] = affineTrans.affineTransVector(pos.getDirectionForward());
          triple[2] = affineTrans.affineTransVector(pos.getDirectionLeft());

          imageList.add(triple);
        }
      }
    }

    ArrayList<DevelopmentNode> children = devNode.getChildren();
    for (DevelopmentNode child : children)
      developMarkers(child, markerImages);
  }
}
