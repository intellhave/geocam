package gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.border.LineBorder;

public abstract class GeoPolygonPanel extends JPanel {
  protected List<GeoPoint> geoPoints;
  protected GeoquantViewer owner;
    
  public GeoPolygonPanel() {
    super();
    geoPoints = new LinkedList<GeoPoint>();
    addMouseMotionListener(new GeoPolygonMouseListener());
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
    
    // Draw Polygons
    List<Polygon> polygons = generatePolygons();
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
  
  protected abstract List<Polygon> generatePolygons();
  
  class GeoPolygonMouseListener extends MouseAdapter {
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
  
  public void setOwner(GeoquantViewer owner) {
    this.owner = owner;
  }
}
