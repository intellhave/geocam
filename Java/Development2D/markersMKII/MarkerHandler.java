package markersMKII;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import triangulation.Face;
import triangulation.Triangulation;

/*********************************************************************************
 * MarkerHandler
 * 
 * This class is responsible for managing a collection of markers. It serves two
 * main purposes. First, it maintains a database for looking up which markers
 * inhabit a particular face on the triangulation. Second, it can "evolve" the
 * marker dynamics --- that is, it can update the positions of the markers on
 * the manifold, based upon their velocities and an input time step.
 *********************************************************************************/

public class MarkerHandler {

  private HashMap<Face, HashSet<Marker>> markerDatabase;
  private AbstractSet<Marker> allMarkers;

  /*********************************************************************************
   * MarkerHandler (Constructor)
   * 
   * This constructor instantiates a new MarkerHandler data structure. In future
   * refactorings, we may add an input for which triangulation the MarkerHandler
   * is using. Currently the triangulation is a global object, so we do not do
   * this.
   *********************************************************************************/
  public MarkerHandler() {
    markerDatabase = new HashMap<Face, HashSet<Marker>>();
    allMarkers = new HashSet<Marker>();
  }

  /*********************************************************************************
   * toString
   * 
   * This method returns a string that describes the contents of the database.
   * Specifically, it prints for each face in the database the markers that are
   * recorded as inhabiting that face.
   *********************************************************************************/
  public String toString() {
    String retval = "";

    retval += "MarkerHandler #" + this.hashCode() + " contains:\n";

    HashMap<Integer, Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for (Integer i : faceIndices) {
      Face f = faceTable.get(i);
      Collection<Marker> objectList = getMarkers(f);

      if (objectList == null) {
        retval += "Face " + f.getIndex() + ": null list\n";
        continue;
      }

      int n = objectList.size();
      retval += "Face " + f.getIndex() + ": " + n + " objects:\n";
      synchronized (objectList) {
        for (Marker o : objectList) {
          retval += "\tObject " + o.getIndex() + " (Face "
              + o.getPosition().getFace().getIndex() + ")\n";
        }
      }
    }

    return retval;
  }

  /*********************************************************************************
   * addMarker
   * 
   * This method adds the input marker's positional data to the database. If the
   * marker is already in the database, there is no effect.
   *********************************************************************************/
  public synchronized void addMarker(Marker m) {
    Collection<Marker> markers = getMarkers(m.getPosition().getFace());
    markers.add(m);
    allMarkers.add(m);
  }

  /*********************************************************************************
   * removeMarker
   * 
   * This method removes the input marker's positional data from the database.
   * If the marker is not in the database, there is no change.
   *********************************************************************************/
  public synchronized void removeMarker(Marker m) {

    Collection<Marker> markers = getMarkers(m.getPosition().getFace());
    if (markers == null) {
      return;
    }

    if (!markers.remove(m)) {
      System.err
          .println("(ManifoldObjectHandler.removeObject) Error removing object "
              + m + " from face " + m.getPosition().getFace().getIndex());
    }
    allMarkers.remove(m);
  }

  /*********************************************************************************
   * clearMarkers
   * 
   * This method completely clears the database of all data relating faces to
   * markers.
   *********************************************************************************/
  public synchronized void clearMarkers() {
    markerDatabase.clear();
    allMarkers.clear();
  }

  /*********************************************************************************
   * getMarkers
   * 
   * Given an input f, this method retrieves the collection of all markers in
   * the database that inhabit that face. This method will always return a
   * non-null collection, though of course that collection might be empty.
   * 
   * Users should NOT tamper with the contents of the collection returned. The
   * return value should be treated as read-only.
   *********************************************************************************/
  public Collection<Marker> getMarkers(Face f) {
    HashSet<Marker> markers = markerDatabase.get(f);
    if (markers == null) {
      markers = new HashSet<Marker>();
      synchronized (markerDatabase) {
        markerDatabase.put(f, markers);
      }
    }
    return markers;
  }

  /*********************************************************************************
   * getMarkerSpeed
   * 
   * Returns the speed of markers in the visualization. Requires a reference to
   * the development's source marker so that its speed may be kept independent
   * of the other markers'
   *********************************************************************************/
  public double getMarkerSpeed(Marker source) {
    Iterator<Marker> iter = allMarkers.iterator();
    double speed;
    if (allMarkers.size() > 1) {
      Marker m = iter.next();
      if (!m.equals(source))
        speed = m.getSpeed();
      else {
        m = iter.next();
        speed = m.getSpeed();
      }
    } else
      speed = 0;
    return speed;
        
  }
  
  /*********************************************************************************
   * getMarkerScale
   * 
   * Returns the scale of markers in the visualization. Requires a reference to
   * the development's source marker so that its scale may be kept independent
   * of the other markers'
   *********************************************************************************/
  
  public double getMarkerScale(Marker source){
    double scale = .5;
    Iterator<Marker> i = allMarkers.iterator();
    if(allMarkers.size() > 1){
      Marker m = i.next();
      if(!m.equals(source))
        scale = m.getAppearance().getScale();
      else{
        m = (Marker) i.next();
        scale= m.getAppearance().getScale();
      }
    }
    return scale;
  }
  
  /*********************************************************************************
   * getAllMarkers
   * 
   * Returns a reference to the collection of markers so that it may be modified
   *********************************************************************************/
  public HashSet<Marker> getAllMarkers(){
    return (HashSet<Marker>) allMarkers;
  }
  
  /*********************************************************************************
   * updateMarker
   * 
   * Given an input marker and its previous face, this method is responsible for
   * updating the database to reflect that the marker has departed the old face
   * and entered a new one.
   *********************************************************************************/
  public void updateMarker(Marker o, Face oldFace) {
    if (o == null)
      return;

    // remove the object, which should be in the list for oldFace
    if (oldFace != null) {
      Collection<Marker> markers = markerDatabase.get(oldFace);
      if (markers != null) {
        synchronized (markers) {
          if (!markers.remove(o)) {
            System.err
                .println("(MarkerHandler.updateMarker) Error transferring marker "
                    + o
                    + " from face "
                    + oldFace.getIndex()
                    + " to face "
                    + o.getPosition().getFace().getIndex());
          }
        }
      }
    }

    addMarker(o); // re-add the object
  }

  /*********************************************************************************
   * updateMarkers
   * 
   * Given an input time-step dt, this method updates all of the markers
   * according to their velocities and the time-step. It checks whether each
   * marker moves out of its current face, and updates the database accordingly.
   *********************************************************************************/
  public synchronized void updateMarkers(long dt) {
    for (Marker m : allMarkers) {
      Face prev = m.getPosition().getFace();
      m.updatePosition(dt);
      Face curr = m.getPosition().getFace();
      if (prev != curr) {
        updateMarker(m, prev);
      }
    }
  }
}
