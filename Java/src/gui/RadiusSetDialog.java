package gui;
import geoquant.Eta;
import geoquant.Length;
import geoquant.Radius;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import triangulation.Edge;
import triangulation.Triangulation;
import triangulation.Vertex;

/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class RadiusSetDialog extends JDialog {
	private Hashtable<Integer, JLabel> labelTable;
	private JSlider radSlider;
	private JScrollPane jScrollPane1;
	private GraphPanel graphPanel;
	private JButton selectAllButton;
	private JTextField setRadiusTextField;
	private JLabel setRadiusLabel;
	private JList radiusList;
	private JButton randomizeButton;
	private JButton cancelButton;
	private GeoquantViewer owner;

	public RadiusSetDialog(GeoquantViewer owner) {
	  super(owner);
	  this.owner = owner;
    GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
    getContentPane().setLayout(thisLayout);
        thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
        	.addContainerGap()
        	.addGroup(thisLayout.createParallelGroup()
        	    .addComponent(getJScrollPane1(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
        	    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	        .addComponent(getGraphPanel(), GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
        	        .addGap(16)
        	        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	            .addComponent(getSetRadiusLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
        	            .addComponent(getSetRadiusTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))))
        	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        	.addGroup(thisLayout.createParallelGroup()
        	    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	        .addComponent(getSelectAllButton(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
        	        .addGap(0, 38, Short.MAX_VALUE))
        	    .addGroup(thisLayout.createSequentialGroup()
        	        .addGap(7)
        	        .addGroup(thisLayout.createParallelGroup()
        	            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                .addComponent(getCancelButton(), GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
        	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        	                .addComponent(getRandomizeButton(), GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
        	            .addGroup(thisLayout.createSequentialGroup()
        	                .addComponent(getRadSlider(), GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)))
        	        .addGap(0, 0, Short.MAX_VALUE)))
        	.addContainerGap(18, 18));
        thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
        	.addContainerGap()
        	.addGroup(thisLayout.createParallelGroup()
        	    .addGroup(thisLayout.createSequentialGroup()
        	        .addGroup(thisLayout.createParallelGroup()
        	            .addGroup(thisLayout.createSequentialGroup()
        	                .addComponent(getRandomizeButton(), GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
        	                .addGap(0, 0, Short.MAX_VALUE))
        	            .addComponent(getSelectAllButton(), GroupLayout.Alignment.LEADING, 0, 98, Short.MAX_VALUE)
        	            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                .addGap(0, 24, Short.MAX_VALUE)
        	                .addComponent(getSetRadiusLabel(), GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)))
        	        .addGap(16)
        	        .addGroup(thisLayout.createParallelGroup()
        	            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                .addComponent(getRadSlider(), GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
        	                .addGap(22)
        	                .addComponent(getCancelButton(), GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE))
        	            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                .addPreferredGap(getRadSlider(), getSetRadiusTextField(), LayoutStyle.ComponentPlacement.INDENT)
        	                .addComponent(getSetRadiusTextField(), GroupLayout.PREFERRED_SIZE, 164, GroupLayout.PREFERRED_SIZE)
        	                .addGap(29)
        	                .addComponent(getJScrollPane1(), GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE))))
        	    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	        .addComponent(getGraphPanel(), GroupLayout.PREFERRED_SIZE, 290, GroupLayout.PREFERRED_SIZE)
        	        .addGap(0, 170, Short.MAX_VALUE)))
        	.addContainerGap());

      this.setPreferredSize(new java.awt.Dimension(490, 419));
      this.setResizable(false);
      this.setSize(490, 419);
      this.setTitle("Set Radii");
	}
	
	private JSlider getRadSlider() {
		if (radSlider == null) {
			labelTable = new Hashtable<Integer, JLabel>();
			labelTable.put(0, new JLabel("0"));
			labelTable.put(10, new JLabel("1"));
			labelTable.put(20, new JLabel("2"));
			labelTable.put(30, new JLabel("3"));
			labelTable.put(40, new JLabel("4"));
			labelTable.put(50, new JLabel("5"));
			
			radSlider = new JSlider();
			radSlider.setValue(10);
			radSlider.setMaximum(50);
			radSlider.setLabelTable(labelTable);
			radSlider.setMajorTickSpacing(5);
			radSlider.setMinorTickSpacing(1);
			radSlider.setPaintLabels(true);
			radSlider.setPaintTicks(true);
			radSlider.setEnabled(true);
			radSlider.setName("radSlider");
			radSlider.addChangeListener(new SliderListener());
		}
		return radSlider;
	}
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText("Close");
			cancelButton.setAction(new CloseSetDialog("Close"));
		}
		return cancelButton;
	}
	private JButton getRandomizeButton() {
		if (randomizeButton == null) {
			randomizeButton = new JButton();
			randomizeButton.setText("Randomize");
			randomizeButton.addActionListener(new RandomizeListener());
		}
		return randomizeButton;
	}
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getRadiusList());
		}
		return jScrollPane1;
	}
	private JList getRadiusList() {
		if (radiusList == null) {
			radiusList = new JList();
			radiusList.setVisibleRowCount(-1);
			radiusList.setPreferredSize(new java.awt.Dimension(123, 272));
			radiusList.setModel(new DefaultComboBoxModel(Triangulation.vertexTable.values().toArray()));
			radiusList.addListSelectionListener(new SelectionListener());
		}
		return radiusList;
	}
	private JLabel getSetRadiusLabel() {
		if (setRadiusLabel == null) {
			setRadiusLabel = new JLabel();
			setRadiusLabel.setText("Set Radius:");
			setRadiusLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		}
		return setRadiusLabel;
	}
	private JTextField getSetRadiusTextField() {
		if (setRadiusTextField == null) {
			setRadiusTextField = new JTextField();
			setRadiusTextField.addActionListener(new SetValueListener());
		}
		return setRadiusTextField;
	}
	private JButton getSelectAllButton() {
		if (selectAllButton == null) {
			selectAllButton = new JButton();
			selectAllButton.setText("Select All");
			selectAllButton.addActionListener(new SelectAllListener());
		}
		return selectAllButton;
	}
	private GraphPanel getGraphPanel() {
		if (graphPanel == null) {
			graphPanel = new GraphPanel();
			graphPanel.setBackground(new java.awt.Color(255,255,255));
			graphPanel.setBorder(new LineBorder(new java.awt.Color(0,0,0),1,true));
			graphPanel.setOwner(owner);
			graphPanel.setList(getRadiusList());
		}
		return graphPanel;
	}

	protected void updateList() {
	  getRadiusList().setModel(new DefaultComboBoxModel(Triangulation.vertexTable.values().toArray()));
	}
	
	class RandomizeListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent arg0) {
      Object[] selected = radiusList.getSelectedValues();
      Random r = new Random();
      for(int i = 0; i < selected.length; i++) {
        Radius.at((Vertex) selected[i]).setValue(3 * r.nextDouble() + 0.1);
      }
      graphPanel.repaint();
      owner.getPolygonPanel().repaint();
    }
	}
		
	 class SelectAllListener implements ActionListener {

	    @Override
	    public void actionPerformed(ActionEvent arg0) {
	      radiusList.setSelectionInterval(0, radiusList.getModel().getSize() - 1);
	    }
	  }
	
	class CloseSetDialog extends AbstractAction {
	  public CloseSetDialog(String text) {
	    super(text, null);
	  }
	  
    @Override
    public void actionPerformed(ActionEvent arg0) {
      RadiusSetDialog.this.dispose();
    }
	}
	
  class SetValueListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
      try {
        Object[] selected = radiusList.getSelectedValues();
        double val = Double.parseDouble(getSetRadiusTextField().getText());

        for(int i = 0; i < selected.length; i++) {
          Radius.at((Vertex) selected[i]).setValue(val);
        }
                
        getGraphPanel().repaint();
        owner.getPolygonPanel().repaint();
      } catch (NumberFormatException exc) {
        return;
      } catch (ClassCastException exc) {
        return;
      }
      
    } 
  }
  
  class SliderListener implements ChangeListener {

    public void stateChanged(ChangeEvent e) {
      try {
        if(!getRadSlider().getValueIsAdjusting()) {
          Object[] selected = radiusList.getSelectedValues();
          double val = getRadSlider().getValue() / 10.0;

          for(int i = 0; i < selected.length; i++) {
            Radius.at((Vertex) selected[i]).setValue(val);
          }
        
          getSetRadiusTextField().setText( "" +   val );
        
          getGraphPanel().repaint();
          owner.getPolygonPanel().repaint();
        } 
      } catch (NumberFormatException exc) {
          return;
      } catch (ClassCastException exc) {
          return; 
      }
    }
  }
  
  class SelectionListener implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {

        if (getRadiusList().getSelectedIndex() != -1) {
          Vertex v = (Vertex) getRadiusList().getModel().getElementAt(getRadiusList().getSelectedIndex());
          getSetRadiusTextField().setText("" + Radius.valueAt(v));
        }
      }
    }
    
  }
}
