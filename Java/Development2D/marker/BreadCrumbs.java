package marker;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import marker.MarkerAppearance.ModelType;
import util.Vector;
import development.ManifoldPosition;

/*********************************************************************************
 * BreadCrumbs
 * 
 * When a user is traversing a manifold, they have the option of leaving behind
 * small markers known as bread crumbs that allow them to see where they have
 * already been. BreadCrumbs extends MarkerHandler because each bread crumb is a
 * marker that can be added to and deleted from the handler. For now, the
 * appearance of the bread crumb is a cookie, but eventually we wish to create a
 * crumb appearance.
 *********************************************************************************/
public class BreadCrumbs {

  private MarkerHandler markers;
  private Queue<Marker> trail;  
  private MarkerAppearance crumb = new MarkerAppearance(
      MarkerAppearance.ModelType.FLAG, .045);

  private static int counter = 0;
  
  public BreadCrumbs(MarkerHandler mh) {    
    markers = mh;
    trail = new ConcurrentLinkedQueue<Marker>();
  }

  /*********************************************************************************
   * addMarker
   * 
   * This method is responsible for adding crumbs to the handler and determining
   * when crumbs should become invisible. Right now, crumbs are removed once
   * there are more than 20 markers in the handler. Crumbs are removed according
   * to when they were placed on the manifold by the user.
   *********************************************************************************/
  public void addMarker(ManifoldPosition mp) {
    ManifoldPosition pos = new ManifoldPosition(mp);
    Vector vec = new Vector(pos.getDirectionForward());
    vec.scale(-.25);
    pos.move(vec);
    Marker m = new Marker(pos, crumb, Marker.MarkerType.FIXED);
    trail.add(m);
    markers.addMarker(m);
    
    int numMarkers = trail.size() - 1;
    if (numMarkers >= 5) {
      Marker toRemove = trail.poll();
      toRemove.flagForRemoval();
    }    
  }

  public void clear(){
	  for( Marker m : trail ){
		  m.setVisible(false);
		  m.flagForRemoval();
	  }
	  trail.clear();
  }
}
