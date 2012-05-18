package viewMKII;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JPanel;

public class SideBySideViewPanel extends JPanel implements ViewCompositor {

  private View v1, v2;
  
  public SideBySideViewPanel( View v, View w ){
    super();
    
    v1 = v; v2 = w;
    
    this.setLayout(null);
    this.setSize( new Dimension( 1000, 500 ) );
    Insets insets = this.getInsets();
    
    Component cv = (Component) v.getViewer().getViewingComponent(); 
    Component cw = (Component) w.getViewer().getViewingComponent();
        
    this.add(cv); 
    cv.setVisible(true);
    this.add(cw);
    cv.setVisible(true);
          
    int width = this.getWidth();
    int height = this.getHeight();
    cv.setBounds(insets.left, insets.top, width/2, height);    
    cw.setBounds(width/2 + insets.left, insets.top, width/2, height);    
    
    this.validate();  
  }  
  
  public void updateScene(){
    v1.updateScene();
    v2.updateScene();
  }
}
