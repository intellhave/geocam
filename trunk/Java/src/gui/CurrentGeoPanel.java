package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Geoquant.Geometry;
import Geoquant.Geoquant;

public class CurrentGeoPanel extends GeoPolygonPanel {
  private HashMap<Class<? extends Geoquant>, List<Geoquant>> geoList;
  
  public CurrentGeoPanel() {
    super();

    geoList = new HashMap<Class<? extends Geoquant>, List<Geoquant>>();
  }
  
  protected List<Polygon> generatePolygons() {
    geoPoints.clear();
    int halfHeight = this.getHeight() / 2;
    int halfWidth = this.getWidth() / 2;
    List<Polygon> polyList = new LinkedList<Polygon>();
    double radius, angle, angleStep;
    int[] xpoints;
    int[] ypoints;
    int i;
    for(List<Geoquant> list : geoList.values()) {
      int size = list.size();
      if(size != 0) {
        xpoints = new int[size];
        ypoints = new int[size];
        angleStep = 2 * Math.PI / size;

        i = 0;
        angle = 0;
        for(Geoquant q : list) {
          radius = (halfHeight / Math.PI) * (Math.atan(q.getValue()) + Math.PI / 2);
          xpoints[i] = (int) (radius * Math.cos(angle) + halfWidth);
          ypoints[i] = (int) (-1 * radius * Math.sin(angle) + halfHeight);

          geoPoints.add(new GeoPoint(xpoints[i], ypoints[i], q.toString()));
          
          i++;
          angle += angleStep; 
        }
        polyList.add(new Polygon(xpoints, ypoints, size));
      }
    }
    
    return polyList;
  }
  
  public void addGeoquant(Class<? extends Geoquant> c) {
    if(geoList.get(c) == null) {
      geoList.put(c, Geometry.getGeoquants(c));
      this.repaint();
    }
  }
  
  public void removeGeoquant(Class<? extends Geoquant> c) {
    if(geoList.remove(c) != null) {
      this.repaint();
    }
  }

}
