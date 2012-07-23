package development;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import triangulation.Face;
import triangulation.Triangulation;

import development.ManifoldPath.Segment;

import marker.Marker;
import marker.MarkerAppearance;

/*********************************************************************************
 * ForwardGeodesics
 * 
 * This class is responsible for constructing geodesics that can be displayed on
 * the manifold. The geodesic consists of a series of line segments that are
 * calculated using data structures in ManifoldPath and the method
 * "moveWithTrail" in ManifoldPosition. These line segments are then used to
 * create markers with the appearance of an arrow that are displayed.
 * 
 *********************************************************************************/
public class ForwardGeodesics {

  private Set<Marker> geodesicMarkers;
  private Marker source;
  ManifoldPosition sourcepos;
  private HashMap<Face, ArrayList<Segment>> segmentList;
  private HashMap<Face, Set<Marker>> faceMarkers;

  /*********************************************************************************
   * TIPSIZE effects how large the tip of the arrow is on the final segment in
   * the geodesic, while GEO_LENGTH controls how long the geodesic is.
   *********************************************************************************/
  private final double TIPSIZE = .25;
  private final double GEO_LENGTH = 2.0;

  public ForwardGeodesics(Marker sourceMarker) {
    geodesicMarkers = new HashSet<Marker>();
    faceMarkers = new HashMap<Face, Set<Marker>>();
    source = sourceMarker;
  }

  /*********************************************************************************
   * generateGeodesic
   * 
   * This is the main method that is used when constructing geodesics for
   * display. The method constructs a list of all of the geodesic segments on a
   * face (faceMarkers) as well as a collection of all geodesics on the manifold
   * (geodesicMarkers). Currently, only one geodesic is displayed at a time.
   *********************************************************************************/
  public void generateGeodesic() {
    for (Marker m : geodesicMarkers)
      m.setVisible(false);
    sourcepos = new ManifoldPosition(source.getPosition());
    Vector direction = source.getPosition().getDirectionForward();
    ManifoldPath mp = new ManifoldPath();
    direction.scale(GEO_LENGTH);
    sourcepos.moveWithTrail(direction, mp);
    segmentList = mp.getSortedSegmentList();
    for (Face f : Triangulation.faceTable.values()) {
      ArrayList<Segment> faceSegs = segmentList.get(f);

      Set<Marker> localMarkers = new HashSet<Marker>();
      if (faceSegs == null)
        continue;
      for (Segment s : faceSegs) {

        Vector start = s.startPos;
        Vector end = s.endPos;
        end.subtract(start);
        double x0 = end.getComponent(0);
        double y0 = end.getComponent(1);
        MarkerAppearance app;
        if (!s.isLast)
          app = new MarkerAppearance(0, 0, x0, y0);
        else {
          double segLength = end.length();
          double pointSize = TIPSIZE / segLength;

          app = new MarkerAppearance(0, 0, x0, y0, pointSize);
        }
        ManifoldPosition pos = new ManifoldPosition(f, start);
        Marker mark = new Marker(pos, app);
        geodesicMarkers.add(mark);
        localMarkers.add(mark);
      }
      faceMarkers.put(f, localMarkers);
    }
  }

  public Set<Marker> getMarkers(Face f) {
    Set<Marker> markers = faceMarkers.get(f);

    if (markers == null) {
      markers = new HashSet<Marker>();
      faceMarkers.put(f, markers);
    }
    return markers;
  }

  public Set<Marker> getAllGeoMarkers() {
    return geodesicMarkers;
  }

}
