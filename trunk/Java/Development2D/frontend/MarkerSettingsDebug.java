package frontend;

import java.awt.FlowLayout;

import javax.swing.JFrame;

public class MarkerSettingsDebug implements Subscriber<MarkerSettings>{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame jf = new JFrame();
		MarkerSettingsGUI msg = new MarkerSettingsGUI();
		jf.getContentPane().add(msg.getUIElement());

		//jf.setSize(220,530);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		jf.setLayout(new FlowLayout());
		jf.pack();
		
		jf.setVisible(true);
		
		MarkerSettingsDebug msd = new MarkerSettingsDebug();
		msg.addSubscriber(msd);
		System.out.println("Done.");
	}

	@Override
	public void update(MarkerSettings data) {
		System.out.println(data);		
	}

}
