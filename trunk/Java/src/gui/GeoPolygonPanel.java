package gui;

import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.border.LineBorder;

public abstract class GeoPolygonPanel extends JPanel {
  private List<GeoPoint> geoPoints;
  private GeoquantViewer owner;
    
  public GeoPolygonPanel(GeoquantViewer owner) {
    super();
    this.owner = owner;
  }
  
  public void paintComponents(Graphics g) {
    super.paintComponent(g);
    List<Polygon> polygons = generatePolygons();
    
  }
  
  public abstract List<Polygon> generatePolygons();
  
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
      geoPanel = GeoPolygonPanel.this;
      message.setText(pointMessage);
      PopupFactory factory = PopupFactory.getSharedInstance();
      geoDisplayPopup = factory.getPopup(owner, message, 
            (int) geoPanel.getLocationOnScreen().getX() + x, 
            (int) geoPanel.getLocationOnScreen().getY() + y - 15);
      geoDisplayPopup.show();
    }
  }
}
