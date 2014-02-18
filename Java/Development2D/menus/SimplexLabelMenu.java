package menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;

import marker.Marker;
import marker.Marker.MarkerType;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import marker.TextualMarkerAppearance;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Vector;
import development.Coord2D;
import development.ManifoldPosition;

public class SimplexLabelMenu extends JMenu {
    private static final long serialVersionUID = 1L;
    private MarkerHandler markers;
    private List<Marker> vertexTags;
    private List<Marker> edgeTags;
    private List<Marker> faceTags;

    private JCheckBoxMenuItem showVTags;
    private JCheckBoxMenuItem showETags;
    private JCheckBoxMenuItem showFTags;

    private final double TAG_SIZE = 0.10;
    
    public SimplexLabelMenu(MarkerHandler mh) {
	markers = mh;
	vertexTags = new LinkedList<Marker>();
	edgeTags = new LinkedList<Marker>();
	faceTags = new LinkedList<Marker>();

	super.setText("Simplex Labels");
	
	showVTags = new JCheckBoxMenuItem("Show Vertex Labels");	
	showETags = new JCheckBoxMenuItem("Show Edge Labels");
	showFTags = new JCheckBoxMenuItem("Show Face Labels");
	
	super.add(showVTags);
	super.add(showETags);
	super.add(showFTags);

	addListeners();
    }

    private void addListeners() {
	showVTags.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		showVertices(showVTags.isSelected());
	    }
	});

	showETags.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		showEdges(showETags.isSelected());
	    }
	});

	showFTags.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
		showFaces(showFTags.isSelected());
	    }
	});
    }

    private void removeLabels(List<Marker> labels) {
	for (Marker m : labels) {
	    m.setVisible(false);
	    m.flagForRemoval();
	}
	labels.clear();
    }

    private void showVertices(boolean show) {
	if (!show) {
	    removeLabels(vertexTags);
	    return;
	}
	for (triangulation.Vertex v : Triangulation.vertexTable.values()) {
	    Face f = v.getLocalFaces().get(0);
	    Vector psn = Coord2D.coordAt(v, f);
	    ManifoldPosition mp = new ManifoldPosition(f, psn);
	    MarkerAppearance ma = new TextualMarkerAppearance("V"
		    + v.getIndex(), TAG_SIZE);
	    Marker m = new Marker(mp, ma, MarkerType.TEXT);
	    markers.addMarker(m);
	    vertexTags.add(m);
	}
    }

    private void showEdges(boolean show) {
	if (!show) {
	    removeLabels(edgeTags);
	    return;
	}
	// Otherwise, we're tagging the edges.
	for (Edge e : Triangulation.edgeTable.values()) {
	    Vertex v1 = e.getLocalVertices().get(0);
	    Vertex v2 = e.getLocalVertices().get(1);
	    Face f = e.getLocalFaces().get(0);

	    Vector p1 = Coord2D.coordAt(v1, f);
	    Vector p2 = Coord2D.coordAt(v2, f);

	    Vector psn = Vector.add(Vector.scale(p1, 0.5),
		    Vector.scale(p2, 0.5));

	    ManifoldPosition mp = new ManifoldPosition(f, psn);
	    MarkerAppearance ma = new TextualMarkerAppearance("E"
		    + e.getIndex(), TAG_SIZE);
	    Marker m = new Marker(mp, ma, MarkerType.TEXT);
	    markers.addMarker(m);
	    edgeTags.add(m);
	}
    }

    private void showFaces(boolean show) {
	if (!show) {
	    removeLabels(faceTags);
	    return;
	}
	// Otherwise, we're tagging faces.
	for (Face f : Triangulation.faceTable.values()) {
	    List<Vertex> verts = f.getLocalVertices();
	    Vector p0 = Coord2D.coordAt(verts.get(0), f);
	    Vector p1 = Coord2D.coordAt(verts.get(1), f);
	    Vector p2 = Coord2D.coordAt(verts.get(2), f);
	    Vector psn = Vector.scale(Vector.add(Vector.add(p0, p1), p2),
		    1.0 / 3.0);

	    ManifoldPosition mp = new ManifoldPosition(f, psn);
	    MarkerAppearance ma = new TextualMarkerAppearance("F"
		    + f.getIndex(), TAG_SIZE);
	    Marker m = new Marker(mp, ma, MarkerType.TEXT);
	    markers.addMarker(m);
	    faceTags.add(m);
	}
    }

    public void setMarkerHandler(MarkerHandler other) {
	markers = other;
	vertexTags.clear();
	showVTags.setSelected(false);
	showETags.setSelected(false);
	showFTags.setSelected(false);
    }
}
