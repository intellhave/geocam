package menus;

import view.View;

public interface ViewController {
	public void addView( View v );	
	public void removeView( View v );
	public void removeAllViews();	
}
