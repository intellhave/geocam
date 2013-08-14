package frontend;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import view.View;

public class ManifoldDisplaySettingsPanel extends JPanel implements ViewController {

	private static final long serialVersionUID = 1L;
	private JCheckBox showEdges;
	private JCheckBox showFaces;
	private JCheckBox showTextures;
	private JCheckBox showFaceLabels;

	private List<View> views;
	
	public ManifoldDisplaySettingsPanel() {
		views = new LinkedList<View>();
		super.setLayout(new FlowLayout());
		
		TitledBorder tb = BorderFactory.createTitledBorder("Manifold Display Controls");		
		super.setBorder(tb);
		
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
		jp.setAlignmentX(Component.CENTER_ALIGNMENT);
		super.add(jp);
		
		showEdges = new JCheckBox();
		showEdges.setText("Show Edges");
		showEdges.setSelected(false);
		jp.add(showEdges);

		showFaces = new JCheckBox();
		showFaces.setText("Show Faces");
		showFaces.setSelected(true);
		jp.add(showFaces);

		showTextures = new JCheckBox();
		showTextures.setText("Show Textures");
		showTextures.setSelected(true);
		jp.add(showTextures);
		
		showFaceLabels = new JCheckBox();
		showFaceLabels.setText("Show Face Labels");
		showFaceLabels.setSelected(false);
		jp.add(showFaceLabels);
		
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				update();
			}
		};
		showEdges.addActionListener(al);
		showFaces.addActionListener(al);
		showTextures.addActionListener(al);
		showFaceLabels.addActionListener(al);
		
		update();
	}

	private void update(){
		for(View v : views){
			v.setDrawEdges(showEdges.isSelected());
			v.setDrawFaces(showFaces.isSelected());
			v.setDrawTextures(showTextures.isSelected());
			v.setLabelFaces(showFaceLabels.isSelected());
		}
		
	}
	
	public void addView( View v ){
		if( views.contains(v) ) return;
		views.add(v);
		update();
	}
	
	public void removeView( View v ){
		views.remove(v);		
	}	
	
	public void removeAllViews(){
		views.clear();
	}
}
