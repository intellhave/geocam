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

public class BreadCrumbs extends MarkerHandler {
  private Queue<Marker> allMarkers; 
  private MarkerAppearance crumb = new MarkerAppearance(MarkerAppearance.ModelType.COOKIE, .5);
  
 public BreadCrumbs(){
   markerDatabase = new HashMap<Face, Set<Marker>>();
   allMarkers = new ConcurrentLinkedQueue<Marker>();
 }
 
 public void addSourceMarker(Marker m){
   sourceMarker = m;
 }
public void addMarker(){
  ManifoldPosition pos = new ManifoldPosition(sourceMarker.getPosition());
  Vector vec = new Vector(pos.getDirectionForward());
  vec.scale(-.25);
  pos.move(vec);
  Marker m = new Marker(pos,crumb);
  allMarkers.add(m);
  System.out.println("Cookie successfully added");
  Collection<Marker> markers = getMarkers(m.getPosition().getFace());
  markers.add(m);
  System.out.println("NUmber of markers on face is:"+ markers.size());
  int numMarkers = allMarkers.size()-1;
  if(numMarkers >= 20){
    Marker toRemove = allMarkers.poll();
    toRemove.setVisible(false);
    addMarker(toRemove);
  }
}
public Set<Marker> getAllMarkers(){
  Iterator<Marker> i = allMarkers.iterator();
  Set<Marker> toReturn = new HashSet<Marker>();
  while(i.hasNext()){
    toReturn.add(i.next());
  }
  return toReturn;
}

public  double getMarkerSpeed(Marker m){
  return 0.0;
}

public void updateMarker(){
 }
 
public void updateMarkers(){    
  }
}
