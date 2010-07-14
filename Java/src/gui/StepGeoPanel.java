package gui;

import java.awt.Polygon;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Geoquant.GeoRecorder;
import Geoquant.Geoquant;

public class StepGeoPanel extends GeoPolygonPanel {
  private GeoRecorder recorder;
  private int currentStep;
  
  protected List<Polygon> generatePolygons() {
    geoPoints.clear();
    int halfHeight = this.getHeight() / 2;
    int halfWidth = this.getWidth() / 2;
    List<Polygon> polyList = new LinkedList<Polygon>();
    double radius, angle, angleStep;
    int[] xpoints;
    int[] ypoints;
    int i;
    List<List<Double>> pointLists;
    List<List<String>> descLists;
    List<Double> pointList;
    List<String> descList;
    for(Class<? extends Geoquant> c : owner.getSelectedList()) {
      pointLists = recorder.getValueHistory(c);
      descLists = recorder.getPrintableHistory(c);
      if(pointLists != null) {
        pointList = pointLists.get(currentStep);
        descList = descLists.get(currentStep);
        int size = pointList.size();
        if(size != 0) {
          xpoints = new int[size];
          ypoints = new int[size];
          angleStep = 2 * Math.PI / size;

          i = 0;
          angle = 0;
          Iterator<String> stringIt = descList.iterator();
          for(Double d : pointList) {
            radius = (halfHeight / Math.PI) * (Math.atan(d) + Math.PI / 2);
            xpoints[i] = (int) (radius * Math.cos(angle) + halfWidth);
            ypoints[i] = (int) (-1 * radius * Math.sin(angle) + halfHeight);

            geoPoints.add(new GeoPoint(xpoints[i], ypoints[i], stringIt.next()));
          
            i++;
            angle += angleStep; 
          }
          polyList.add(new Polygon(xpoints, ypoints, size));
        }
      }
    }
    
    return polyList;
  }
  
  public void setRecorder(GeoRecorder rec) {
    recorder = rec;
  }
  
  public void setStep(int step) {
    this.currentStep = step;
  }

}
