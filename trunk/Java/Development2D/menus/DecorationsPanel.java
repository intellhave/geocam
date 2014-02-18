package menus;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import marker.BreadCrumbs;
import marker.ForwardGeodesic;
import marker.MarkerHandler;

public class DecorationsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private MarkerHandler markers;
	private BreadCrumbs crumbs;
	private ForwardGeodesic geodesic;

	private JButton makeGeo;
	private JButton clearGeo;
	private JSlider geoLength;
	private TitledBorder geoLengthBorder;
	
	private JButton makeFlag;
	private JButton clearFlags;
	
	public DecorationsPanel(MarkerHandler mh) {
		this.markers = mh;
		this.crumbs = new BreadCrumbs(mh);
		this.geodesic = new ForwardGeodesic(mh);

		super.setBorder(BorderFactory.createTitledBorder("Geodesic Controls"));
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS ));
		
		makeGeo = new JButton("Add Geodesic");
		makeGeo.setAlignmentX(Component.CENTER_ALIGNMENT);
		clearGeo = new JButton("Clear Geodesics");	
		clearGeo.setAlignmentX(Component.CENTER_ALIGNMENT);
		geoLength = new JSlider();
		geoLength.setMinimum(5);
		geoLength.setMaximum(50);
		geoLength.setValue(10);
		geodesic.setLength(10);
	
		geoLengthBorder = BorderFactory.createTitledBorder("Geodesic Length (10)");
		geoLength.setBorder(geoLengthBorder);
		
		super.add(makeGeo);
		super.add(clearGeo);
		super.add(geoLength);
	
		makeFlag = new JButton("Place a Flag");
		makeFlag.setAlignmentX(Component.CENTER_ALIGNMENT);
		clearFlags = new JButton("Remove All Flags");
		clearFlags.setAlignmentX(Component.CENTER_ALIGNMENT);
		super.add(makeFlag);
		super.add(clearFlags);
		
		addListeners();		
	}
	
	public void setMarkerHandler(MarkerHandler other){
		this.markers = other;
		this.geodesic = new ForwardGeodesic(other);
		this.geodesic.setLength( geoLength.getValue() );
		this.crumbs = new BreadCrumbs(other);						
	}
	
	private void addListeners(){		
		makeGeo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				geodesic.generateGeodesic( markers.getSourceMarker().getPosition() );
			}
		});
		
		clearGeo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				geodesic.clear();
			}
		});
		
		makeFlag.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				crumbs.addMarker( markers.getSourceMarker().getPosition() );
			}
		});
		
		clearFlags.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				crumbs.clear();
			}
		});
		
		geoLength.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				int len = geoLength.getValue();
				geodesic.setLength( len );
				geoLengthBorder.setTitle("Geodesic Length (" + len + ")");
			}
		});
	}
}
