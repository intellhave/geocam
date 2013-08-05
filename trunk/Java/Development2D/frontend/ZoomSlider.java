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
		DecimalFormat percentFormat = new DecimalFormat("0%");
		double z1 = Math.pow(10, (super.getValue() - 100.0) / 100.0);
		double z2 = (1.0 / (super.getValue() / 100.0));
		String str =  percentFormat.format(z2); 
		border.setTitle( this.title + " (" + str + ")");
		
		for(View v : views){
			v.setZoom(z1);
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
