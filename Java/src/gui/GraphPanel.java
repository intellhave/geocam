package gui;

import geoquant.Length;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.border.LineBorder;

import triangulation.Edge;
import triangulation.Triangulation;
import triangulation.Vertex;

public class GraphPanel extends JPanel {
  private HashMap<Double, Color> lengthMap;
  private HashMap<Vertex, Integer> pointMap;
  protected List<GeoPoint> geoPoints;
  private GeoquantViewer owner;
  private JList list;
  private GraphPanelMouseListener mouseListener;
  
  public GraphPanel() {
    super();
    mouseListener = new GraphPanelMouseListener();
    this.addMouseListener(mouseListener);
    this.addMouseMotionListener(mouseListener);
    lengthMap = new HashMap<Double, Color>();
  }
  
  protected void setOwner(GeoquantViewer owner) {
    this.owner = owner;
  }
  
  protected void setList(JList list) {
    this.list = list;
  }
  
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    
    if(list == null) {
      return;
    }
    
    int halfHeight = this.getHeight() / 2;
    int halfWidth = this.getWidth() / 2;
    int size = Triangulation.vertexTable.size();
    int radius = Math.min(halfWidth, halfHeight) - 10;
    pointMap = new HashMap<Vertex, Integer>();
    geoPoints = new LinkedList<GeoPoint>();
    
    int[] xpoints = new int[size];
    int[] ypoints = new int[size];

    int circDiam = 10;
    double angleStep = 2 * Math.PI / size;
    double angle = Math.PI/2;
    ListModel model = list.getModel();
    Vertex v;
    for(int i = 0; i < model.getSize(); i++) {
      v = (Vertex) (model.getElementAt(i));
      xpoints[i] = (int) (radius * Math.cos(angle) + halfWidth);
      ypoints[i] = (int) (-1 * radius * Math.sin(angle) + halfHeight);

      pointMap.put(v, i);
      geoPoints.add(new GeoPoint(xpoints[i], ypoints[i], v.toString()));
      
      g.fillOval(xpoints[i] - circDiam / 2, ypoints[i] - circDiam / 2, 
              circDiam, circDiam);
      angle += angleStep; 
    }
    
    int v1, v2;
    Color c;
    ((Graphics2D) g).setStroke(new BasicStroke(3));
    Random r = new Random();
    for(Edge e : Triangulation.edgeTable.values()) {
      c = lengthMap.get(Length.valueAt(e));
      if(c == null) {
        c = new Color(r.nextInt(200), r.nextInt(200), r.nextInt(200));
        lengthMap.put(Length.valueAt(e), c);
      }
      g.setColor(c);
      v1 = pointMap.get(e.getLocalVertices().get(0));
      v2 = pointMap.get(e.getLocalVertices().get(1));
      
      g.drawLine(xpoints[v1], ypoints[v1], xpoints[v2], ypoints[v2]);
    }
    
  }
  
  protected class GraphPanelMouseListener extends MouseAdapter {
    private JLabel message;
    private Popup geoDisplayPopup;
    private int x;
    private int y;
    private JPanel geoPanel;
    
    public GraphPanelMouseListener() {
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
      geoPanel = GraphPanel.this;
      message.setText(pointMessage);
      PopupFactory factory = PopupFactory.getSharedInstance();
      geoDisplayPopup = factory.getPopup(owner, message, 
            (int) geoPanel.getLocationOnScreen().getX() + x + 20, 
            (int) geoPanel.getLocationOnScreen().getY() + y - 20);
      geoDisplayPopup.show();
    }
  
    public void mouseClicked(MouseEvent e) {
      String vertex = "";
      x = e.getX();
      y = e.getY();
      double minDist = -1;
      if(geoPoints == null || geoPoints.size() == 0) {
        return;
      }
      for(GeoPoint p : geoPoints) {
        double dist = p.distance(x, y);
        if(minDist > dist || minDist < 0) {
          vertex = p.getMessage();
          minDist = dist;
        }
      }

      if(minDist > 10) {
        return;
      }
      int vertIndex = Integer.parseInt(vertex.substring(7));
      int index = pointMap.get(Triangulation.vertexTable.get(vertIndex));
      
      if(e.isShiftDown()) {
        if(list.isSelectedIndex(index)) {
          list.removeSelectionInterval(index, index);
        } else {
          list.addSelectionInterval(index, index);
        }
      } else {
        list.setSelectedIndex(index);
      }
    }
  }
}
