package frontend;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.View;

public class ZoomSlider extends JSlider implements ViewController{	

	private static final long serialVersionUID = 1L;
	private List<View> views;
	private TitledBorder border;
	private String title;
		
	public ZoomSlider(String title){
		this();
		this.title = title;
		super.setFocusable(false);
		update();
	}
	
	public ZoomSlider(){
		super.setFocusable(false); // Mouse input only.
		super.setMinimum(0);
		super.setMaximum(400);		
		super.setValue(100);
		
		views = new LinkedList<View>();
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				update();				
			}
		};
		this.addChangeListener(cl);		
		this.title = "";
		border = BorderFactory.createTitledBorder("");
		super.setBorder(border);
		update();
	}
	
	private void update(){
		DecimalFormat df = new DecimalFormat("##.##");
		String str =  df.format( super.getValue() / 200.0 ); 
		border.setTitle( this.title + " (" + str + ")");
		
		for(View v : views){
			v.setZoom(super.getValue() / 200.0);
		}
	}
	
	public void addView( View v ){
		if( views.contains(v) ) return;
		views.add(v);
		update(); // Make sure new view has the slider's settings.
	}
	
	public void removeView( View v ){
		views.remove(v);		
	}	
	
	public void removeAllViews(){
		views.clear();
	}
}
