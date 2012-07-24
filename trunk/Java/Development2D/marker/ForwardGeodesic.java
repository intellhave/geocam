package marker;

import java.util.HashSet;
import java.util.Set;

import development.ManifoldPosition;
import development.Vector;


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
   * 
   *********************************************************************************/
  private final double size = .05;
  private final double segmentSize = 0.2;
  private final double segments = 10;

  public ForwardGeodesic(MarkerHandler mh) {
    geodesicMarkers = new HashSet<Marker>();
    markers = mh;
  }
  
  public void generateGeodesic(ManifoldPosition mp){    
    for( Marker m : geodesicMarkers ){
      m.flagForRemoval();      
    }
    geodesicMarkers.clear();
    ManifoldPosition pos = new ManifoldPosition(mp);

    Vector v = pos.getDirectionForward();
    v.normalize();
    v.scale(segmentSize);
    
    // Once we get the arrow marker appearance working, these will be replaced with 
    // arrow body and arrow head respectively.
    MarkerAppearance body = new MarkerAppearance(MarkerAppearance.ModelType.SPHERE, size);
    MarkerAppearance head = new MarkerAppearance(MarkerAppearance.ModelType.SPHERE, size);
    MarkerAppearance ma;
    for( int ii = 0; ii < segments; ii++ ){
      ma = body;
      if( ii == segments - 1 ) ma = head;      
      
      Marker m = new Marker(new ManifoldPosition(pos), ma, Marker.MarkerType.FIXED);
      geodesicMarkers.add(m);
      markers.addMarker(m);
      pos.move(v);      
    }
    
    
  }

  public Set<Marker> getAllGeoMarkers() {
    return geodesicMarkers;
  }
}
