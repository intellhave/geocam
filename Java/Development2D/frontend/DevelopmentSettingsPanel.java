package frontend;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import development.Development;

public class DevelopmentSettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JFormattedTextField recDepthTextbox;
	private Development development;

	public DevelopmentSettingsPanel(Development development) {
		this.development = development;
		super.setLayout(new FlowLayout());

		recDepthTextbox = new JFormattedTextField(NumberFormat.getIntegerInstance());
		recDepthTextbox.setColumns(3);
		recDepthTextbox.setValue( development.getDepth() );

		JLabel label = new JLabel("Recursion Depth");
		super.add(label);
		super.add(recDepthTextbox);
				
		recDepthTextbox.addActionListener( new DepthBoxListener() );		
	}
	
	public int getDepthSetting(){
		String str = recDepthTextbox.getValue().toString();
		return Integer.parseInt(str);		
	}
	
	public void setDevelopment( Development otherDev ){
		this.development = otherDev;
		development.setDepth( getDepthSetting() );
	}
	
	private class DepthBoxListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int newDepth = getDepthSetting();
			if (newDepth < 1) {
				newDepth = 1;
			} else if (newDepth > 20) {
				newDepth = 20;
			}
			development.setDepth(newDepth);
			recDepthTextbox.setValue(newDepth);
		}
	}
}
