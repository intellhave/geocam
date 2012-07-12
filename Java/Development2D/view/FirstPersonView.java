package view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphComponent;
import development.Development;
import development.Vector;

/*********************************************************************************
 * FirstPersonView (Previously DevelopmentViewSim3D)
 * 
 * This view visualizes a triangulated 2 dimensional surface using the notion of
 * an exponential map. Starting from the face that contains a designated
 * "source point," this view unfolds pieces of the surface into a plane. Then,
 * it "thickens" the plane by a small amount, so that one can visualize what it
 * would be like to move around in this space as a two dimensional creature.
 *********************************************************************************/
public class FirstPersonView extends ExponentialView {
  private boolean showAvatar = true;
  
  /*********************************************************************************
   * FirstPersonView
   * 
   * This method initializes a new ExponentialView to use a particular
   * development (for calculating the visualization) and color scheme (for
   * coloring the polygons that make up the visualization).
   *********************************************************************************/
  public FirstPersonView(Development dev, MarkerHandler mh,
      FaceAppearanceScheme fas) {
    super(dev, mh, fas); // This call initializes the sgcpools data structure.
    Camera cam = sgcCamera.getCamera();
    cam.setPerspective(true);

  }

  /*********************************************************************************
   * updateCamera
   * 
   * This method is responsible for positioning the camera such that the
   * development can be viewed from the perspective of a small creature on the
   * manifold.
   *********************************************************************************/
  protected void updateCamera() {
    // The angle in rotateY controls the pitch of the camera.
    // Generally, the smaller we make the altitude of the camera, the harder it
    // is to get an idea of the structure of the development in this view.
    MatrixBuilder.euclidean().translate(0.1, 0, 1.3).rotateY(-Math.PI / 3)
        .rotateZ(-Math.PI / 2).assignTo(sgcCamera);
  }

  /*********************************************************************************
   * generateMarkerGeometry
   * 
   * This method is responsible for placing representations of the markers in
   * the visualization. Due to the nature of this particular view, a single
   * marker may appear in multiple places. This is why we need the sgcpools data
   * structure --- it keeps track of the multiple scene graph components needed
   * to depict each marker in the scene.
   *********************************************************************************/
  protected void generateMarkerGeometry() {
    HashMap<Marker, ArrayList<Vector[]>> markerImages = new HashMap<Marker, ArrayList<Vector[]>>();
    super.developMarkers(development.getRoot(), markerImages);
    Set<Marker> allMarkers = markers.getAllMarkers();

    for (Marker m : allMarkers) {
      LinkedList<SceneGraphComponent> pool = sgcpools.get(m);

      if (pool == null) {
        pool = new LinkedList<SceneGraphComponent>();
        sgcpools.put(m, pool);
      }

      ArrayList<Vector[]> images = markerImages.get(m);
      if (images == null) {
        for (SceneGraphComponent sgc : pool)
          sgc.setVisible(false);
        continue;
      }

      if (images.size() > pool.size()) {
        int sgcCount = images.size() - pool.size();
        for (int jj = 0; jj < 2 * sgcCount; jj++) {
          MarkerAppearance oa = m.getAppearance();
          SceneGraphComponent sgc = oa.prepareNewSceneGraphComponent();
          pool.add(sgc);
          sgcMarkers.addChild(sgc);
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

          MatrixBuilder.euclidean()
              .translate(position.getComponent(0), position.getComponent(1), 1)
              .times(matrix).scale(m.getAppearance().getScale()).assignTo(sgc);

          // This is a hack to find the SGC that displays the avatar.
          // In the future, we should have a dedicated SGC pointer for the
          // avatar.
          double epsilon = 0.05;
          if (position.lengthSquared() < epsilon) {
            sgc.setVisible(this.showAvatar);
          } else {
            sgc.setVisible(true);
          }
        }
        counter++;
      }
    }
  }

  /*********************************************************************************
   * setDrawAvatar
   * 
   * Based on the input boolean, this method sets whether or not an avatar (a
   * particular marker) is drawn right in front of the camera (to represent the
   * user/player).
   *********************************************************************************/
  public void setDrawAvatar(boolean showAvatar) {
    this.showAvatar = showAvatar;
  }

}
