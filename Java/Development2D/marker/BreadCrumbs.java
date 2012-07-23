package marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import triangulation.Face;

import development.ManifoldPosition;
import development.Vector;

/*BreadCrumbs
 * 
 * When a user is traversing a manifold, they have the option of leaving behind small markers known
 * as bread crumbs that allow them to see where they have already been. BreadCrumbs extends 
 * MarkerHandler because each bread crumb is a marker that can be added to and deleted from the 
 * handler. For now, the appearance of the bread crumb is a cookie, but eventually we wish to 
 * create a crumb appearance.
 * 
 */
public class BreadCrumbs extends MarkerHandler {
  
  private Queue<Marker> trail; 
  private MarkerAppearance crumb = new MarkerAppearance(MarkerAppearance.ModelType.COOKIE, .5);
  
 public BreadCrumbs(){
   super();
   trail = new ConcurrentLinkedQueue<Marker>();
 }
 
 public void addSourceMarker(Marker m){
   sourceMarker = m;
 }
 /*addMarker
  * This method is responsible for adding crumbs to the handler and determining when crumbs should
  * become invisible. Right now, crumbs are removed once there are more than 20 markers in the 
  * handler. Crumbs are removed according to when they were placed on the manifold by the user.
  */
public void addMarker(){
  ManifoldPosition pos = new ManifoldPosition(sourceMarker.getPosition());
  Vector vec = new Vector(pos.getDirectionForward());
  vec.scale(-.25);
  pos.move(vec);
  Marker m = new Marker(pos,crumb);
  trail.add(m);
  allMarkers.add(m);
  Collection<Marker> markers = getMarkers(m.getPosition().getFace());
  markers.add(m);
  int numMarkers = trail.size()-1;
  if(numMarkers >= 20){
    Marker toRemove = trail.poll();
    toRemove.setVisible(false);
  }
}

public  double getMarkerSpeed(Marker m){
  return 0.0; // the bread crumbs should not be able to move around the manifold
}

public void updateMarker(){
 }
 
public void updateMarkers(){    
  }
}
