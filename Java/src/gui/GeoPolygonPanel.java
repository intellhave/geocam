package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.border.LineBorder;

import Geoquant.GeoRecorder;
import Geoquant.Geometry;
import Geoquant.Geoquant;

public class GeoPolygonPanel extends JPanel {
  protected List<GeoPoint> geoPoints;
  protected GeoquantViewer owner;
  private HashMap<Class<? extends Geoquant>, List<Geoquant>> geoList;
  private GeoRecorder recorder;
  private int currentStep;
  
  protected enum Form{
    geo, step
  }
  private Form currentForm;
  
  
  public GeoPolygonPanel() {
    super();
    geoPoints = new LinkedList<GeoPoint>();
    addMouseMotionListener(new GeoPolygonMouseListener());
    
    geoList = new HashMap<Class<? extends Geoquant>, List<Geoquant>>();
    currentForm = Form.geo;
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    int halfHeight = this.getHeight() / 2;
    int halfWidth = this.getWidth() / 2;
    int radius;
    

    // Draw 0-oval
    g.setColor(new Color(0, 150, 0));
    radius = (halfHeight / 2);
    g.drawOval(halfWidth - radius, halfHeight - radius, 
               2 * radius, 2 * radius);
    g.drawString("0", (int) ( radius * Math.cos(Math.PI/4) + halfWidth),
                      (int) (halfHeight - radius * Math.sin(Math.PI/4)));
    // Draw Infinity-oval
    radius = (halfHeight);
    g.drawOval(halfWidth - radius, halfHeight - radius, 
               2 * radius, 2 * radius);
    g.drawString("Infty", (int) ( radius * Math.cos(Math.PI/4) + halfWidth),
        (int) (halfHeight - radius * Math.sin(Math.PI/4)));
    
    // Draw StepNum, if applicable
    if(currentForm == Form.step) {
      g.drawString("Step: " + currentStep, this.getWidth() - 75, 20);
    }
    // Draw Polygons
    List<Polygon> polygons = generatePolygons();
    if(polygons == null) {
      return;
    }
    int num = polygons.size();
    int i = 0;
    for(Polygon p : polygons) {
      g.setColor(new Color(Math.min(510 - (i*255*2)/num, 255), 
                           Math.min((i*255*2)/num, 255),0));
      i++;
      g.drawPolygon(p);
      
      int circDiam = 5;
      for(int j = 0; j < p.npoints; j++) {
        g.fillOval(p.xpoints[j] - circDiam / 2, p.ypoints[j] - circDiam / 2, 
              circDiam, circDiam);
      } 
    }
  }
  
  protected List<Polygon> generatePolygons() {
    switch(currentForm) {
    case geo:
      return generateCurrentPolygons();
    case step:
      return generateStepPolygons();
    default:
      return null;
    }
  }
  
  protected class GeoPolygonMouseListener extends MouseAdapter {
    private JLabel message;
    private Popup geoDisplayPopup;
    private int x;
    private int y;
    private JPanel geoPanel;
    
    public GeoPolygonMouseListener() {
      super();

      message = new JLabel();
      message.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
    }
    
    public void mouseMoved(MouseEvent e) {
      if(geoDisplayPopup != null) {
        geoDisplayPopup.hide();
      }
      
      String pointMessage = "";

      x = e.getX();
      y = e.getY();
      double minDist = -1;
      if(geoPoints == null || geoPoints.size() == 0) {
        return;
      }
      for(GeoPoint p : geoPoints) {
        double dist = p.distance(x, y);
        if(minDist > dist || minDist < 0) {
          pointMessage = p.getMessage();
          minDist = dist;
        }
      }
      if(minDist > 2.5) {
        return;
      }
      geoPanel = GeoPolygonPanel.this;
      message.setText(pointMessage);
      PopupFactory factory = PopupFactory.getSharedInstance();
      geoDisplayPopup = factory.getPopup(owner, message, 
            (int) geoPanel.getLocationOnScreen().getX() + x, 
            (int) geoPanel.getLocationOnScreen().getY() + y - 15);
      geoDisplayPopup.show();
    }
  }
  
  protected void setForm(Form form) {
    currentForm = form;
  }
  
  protected void setOwner(GeoquantViewer owner) {
    this.owner = owner;
  }
  
  private List<Polygon> generateCurrentPolygons() {
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
  
  protected List<Polygon> generateStepPolygons() {
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
    if(recorder == null) {
      return polyList;
    }
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
  
  protected void addGeoquant(Class<? extends Geoquant> c) {
    if(geoList.get(c) == null) {
      geoList.put(c, Geometry.getGeoquants(c));
      this.repaint();
    }
  }
  
  protected void removeGeoquant(Class<? extends Geoquant> c) {
    if(geoList.remove(c) != null) {
      this.repaint();
    }
  }
  
  protected void setRecorder(GeoRecorder rec) {
    recorder = rec;
    currentStep = 0;
  }
  
  protected GeoRecorder getRecorder() {
    return recorder;
  }
  
  protected void setStep(int step) {
    this.currentStep = step;
  }
  
  protected int getCurrentStep() {
    return currentStep;
  }
  
  protected Form getForm() {
    return currentForm;
  }

}
