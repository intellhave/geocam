package marker;

import java.util.HashSet;
import java.util.Set;

import util.Vector;
import development.ManifoldPosition;

/*********************************************************************************
 * ForwardGeodesics
 * 
 * This class is responsible for constructing geodesics that can be displayed on
 * the manifold. The geodesic consists of a series of line segments that are
 * calculated using data structures in ManifoldPath and the method
 * "moveWithTrail" in ManifoldPosition. These line segments are then used to
 * create markers with the appearance of an arrow that are displayed.
 *********************************************************************************/
public class ForwardGeodesic {

  private MarkerHandler markers;
  private Set<Marker> geodesicMarkers;

  /*********************************************************************************
   * These parameters control the appearance of a geodesic on the manifold.
   *********************************************************************************/
  private final double size = .05;
  private final double segmentSize = 0.2;
  private  double segments = 10;

  /*********************************************************************************
   * ForwardGeodesic
   * 
   * Given a marker handler, this constructor makes a new "ForwardGeodesic"
   * object, which is responsible for creating new markers to represent a
   * geodesic and then adding them to the manifold.
   *********************************************************************************/
  public ForwardGeodesic(MarkerHandler mh) {
    geodesicMarkers = new HashSet<Marker>();
    markers = mh;
  }

  /*********************************************************************************
   * generateGeodesic
   * 
   * Given a manifold position p, this method adds markers to the marker handler
   * to represent a geodesic specified by p (recall that p carries with it an
   * orientation with a designated "forward" vector). If a geodesic has been
   * created already, this old geodesic is marked for removal from the manifold.
   *********************************************************************************/
  public void generateGeodesic(ManifoldPosition mp) {
    for (Marker m : geodesicMarkers) {
      m.flagForRemoval();
    }
    geodesicMarkers.clear();
    ManifoldPosition pos = new ManifoldPosition(mp);

    Vector v = pos.getDirectionForward();
    v.normalize();
    v.scale(segmentSize);

    // Once we get the arrow marker appearance working, these will be replaced
    // with
    // arrow body and arrow head respectively.
    MarkerAppearance body = new MarkerAppearance(
        MarkerAppearance.ModelType.SPHERE, size);
    MarkerAppearance head = new MarkerAppearance(
        MarkerAppearance.ModelType.SPHERE, size);
    MarkerAppearance ma;
    for (int ii = 0; ii < segments; ii++) {
      ma = body;
      if (ii == segments - 1)
        ma = head;

      Marker m = new Marker(new ManifoldPosition(pos), ma,
          Marker.MarkerType.FIXED);
      geodesicMarkers.add(m);
      markers.addMarker(m);
      pos.move(v);
    }
  }

  /*********************************************************************************
   * getMarkers
   * 
   * This method returns all of the markers that have been used to represent the
   * geodesic.
   *********************************************************************************/
  public Set<Marker> getMarkers() {
    return geodesicMarkers;
  }
  public double getLength(){
    return segments;
  }
  public void setLength(double newLength){
    segments = newLength;
  }
  
  public void clear() {
	  for( Marker m : geodesicMarkers ){
		  m.setVisible(false);
		  m.flagForRemoval();
	  }	  
	  geodesicMarkers.clear();
  }
}
