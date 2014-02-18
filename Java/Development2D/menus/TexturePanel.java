package menus;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import triangulation.Face;
import view.TextureLibrary;
import view.TextureLibrary.TextureDescriptor;
import de.jreality.scene.Appearance;
import development.ManifoldPosition;

public class TexturePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private ManifoldPosition cursorPosition;
	public TexturePanel(ManifoldPosition mp){
		this.cursorPosition = mp;				
		super.setBorder( BorderFactory.createTitledBorder("Texture Controls"));
		super.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		for( TextureDescriptor td : TextureDescriptor.values() ){
			JButton jcb = new JButton();
			jcb.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			Appearance app = TextureLibrary.getAppearance( td );			
			String label = td.toString();
			label = label.substring(0,1).toUpperCase() + label.substring(1).toLowerCase();			
			jcb.setText(label);
			jcb.addActionListener( new TextureSetter( app ) );			
			super.add(jcb);
		}				
	}
	
	private class TextureSetter implements ActionListener {
		private Appearance app;
		public TextureSetter( Appearance app ){
			this.app = app;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {			
			Face f = cursorPosition.getFace();
			f.setAppearance(this.app);
		}				
	}
}
