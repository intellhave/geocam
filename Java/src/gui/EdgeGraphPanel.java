package gui;

import geoquant.Length;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

public class EdgeGraphPanel extends JPanel {
  private HashMap<Double, Color> lengthMap;
  private HashMap<Vertex, Integer> pointMap;
  private HashMap<Edge, Integer> lineMap;
  protected List<GeoLine> geoLines;
  private GeoquantViewer owner;
  private JList list;
  private GraphPanelMouseListener mouseListener;
  
  public EdgeGraphPanel() {
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
    lineMap = new HashMap<Edge, Integer>();
    geoLines = new LinkedList<GeoLine>();
    
    int[] xpoints = new int[size];
    int[] ypoints = new int[size];

    int circDiam = 10;
    double angleStep = 2 * Math.PI / size;
    double angle = Math.PI/2;
    ListModel model = list.getModel();
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      xpoints[i] = (int) (radius * Math.cos(angle) + halfWidth);
      ypoints[i] = (int) (-1 * radius * Math.sin(angle) + halfHeight);

      pointMap.put(v, i);
      
      angle += angleStep;
      i++;
    }
    
    int v1, v2;
    Color c;
    ((Graphics2D) g).setStroke(new BasicStroke(3));
    
    Random r = new Random();
    Edge e;
    for(int j = 0; j < model.getSize(); j++) {
      e = (Edge) model.getElementAt(j);
      if(list.isSelectedIndex(j)) {
        c = Color.YELLOW;
      } else {
        c = lengthMap.get(Length.valueAt(e));
        if(c == null) {
          c = new Color(r.nextInt(200), r.nextInt(200), r.nextInt(200));
          lengthMap.put(Length.valueAt(e), c);
        }
      }
      g.setColor(c);
      v1 = pointMap.get(e.getLocalVertices().get(0));
      v2 = pointMap.get(e.getLocalVertices().get(1));
      lineMap.put(e, j);

      geoLines.add(new GeoLine(xpoints[v1], ypoints[v1], 
          xpoints[v2], ypoints[v2], e.toString()));
      
      g.drawLine(xpoints[v1], ypoints[v1], xpoints[v2], ypoints[v2]);
    }
    
    ((Graphics2D) g).setStroke(new BasicStroke(1));
    g.setColor(Color.BLACK);
    for(int j = 0; j < xpoints.length; j++) {
      g.fillOval(xpoints[j] - circDiam / 2, ypoints[j] - circDiam / 2, 
          circDiam, circDiam);
    }
  }
  
  private class GraphPanelMouseListener extends MouseAdapter {
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
      if(geoLines == null || geoLines.size() == 0) {
        return;
      }
      for(GeoLine l : geoLines) {
        double dist = l.ptSegDist(x, y);
        if(minDist > dist || minDist < 0) {
          pointMessage = l.getMessage();
          minDist = dist;
        }
      }
      if(minDist > 2.5) {
        return;
      }
      geoPanel = EdgeGraphPanel.this;
      message.setText(pointMessage);
      PopupFactory factory = PopupFactory.getSharedInstance();
      geoDisplayPopup = factory.getPopup(owner, message, 
            (int) geoPanel.getLocationOnScreen().getX() + x + 20, 
            (int) geoPanel.getLocationOnScreen().getY() + y - 20);
      geoDisplayPopup.show();
    }
  
    public void mouseClicked(MouseEvent e) {
      String edge = "";
      x = e.getX();
      y = e.getY();
      double minDist = -1;
      if(geoLines == null || geoLines.size() == 0) {
        return;
      }
      for(GeoLine l : geoLines) {
        double dist = l.ptSegDist(x, y);
        if(minDist > dist || minDist < 0) {
          edge = l.getMessage();
          minDist = dist;
        }
      }

      if(minDist > 10) {
        return;
      }
      int edgeIndex = Integer.parseInt(edge.substring(5));
      int index = lineMap.get(Triangulation.edgeTable.get(edgeIndex));
      
      if(e.isShiftDown()) {
        if(list.isSelectedIndex(index)) {
          list.removeSelectionInterval(index, index);
        } else {
          list.addSelectionInterval(index, index);
        }
      } else {
        list.setSelectedIndex(index);
      }
      EdgeGraphPanel.this.repaint();
    }
  }
}
