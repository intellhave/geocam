package gui;

import geoquant.Alpha;
import geoquant.Radius;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
public class VertexSetPanel extends JPanel {

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

  private Hashtable<Integer, JLabel> radiusLabelTable;
  private JButton randomizeAlphaButton;
  private JTextField setAlphaTextField;
  private Hashtable<Integer, JLabel> alphaLabelTable;
  private JSlider radSlider;
  private JScrollPane jScrollPane1;
  private JSlider alphaSlider;
  private JLabel setAlphaLabel;
  private VertexGraphPanel graphPanel;
  private JButton selectAllButton;
  private JTextField setRadiusTextField;
  private JLabel setRadiusLabel;
  private JList vertexList;
  private JButton randomizeRadiusButton;
  private GeoquantViewer owner;

  public VertexSetPanel() {
    super();
    GroupLayout thisLayout = new GroupLayout((JComponent)this);
    this.setLayout(thisLayout);
        thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
        	.addContainerGap()
        	.addGroup(thisLayout.createParallelGroup()
        	    .addGroup(thisLayout.createSequentialGroup()
        	        .addGroup(thisLayout.createParallelGroup()
        	            .addComponent(getSetAlphaLabel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
        	            .addComponent(getSetRadiusLabel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
        	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        	        .addGroup(thisLayout.createParallelGroup()
        	            .addComponent(getSetAlphaTextField(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
        	            .addComponent(getSetRadiusTextField(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
        	        .addGap(0, 194, Short.MAX_VALUE))
        	    .addGroup(thisLayout.createSequentialGroup()
        	        .addPreferredGap(getSetAlphaLabel(), getRandomizeRadiusButton(), LayoutStyle.ComponentPlacement.INDENT)
        	        .addGroup(thisLayout.createParallelGroup()
        	            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                .addComponent(getRandomizeRadiusButton(), GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
        	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        	                .addComponent(getRandomizeAlphaButton(), GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
        	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        	                .addGroup(thisLayout.createParallelGroup()
        	                    .addGroup(thisLayout.createSequentialGroup()
        	                        .addComponent(getJScrollPane1(), GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
        	                        .addGap(0, 0, Short.MAX_VALUE))
        	                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                        .addGap(16)
        	                        .addComponent(getSelectAllButton(), GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
        	                        .addGap(0, 23, GroupLayout.PREFERRED_SIZE))))
        	            .addGroup(thisLayout.createSequentialGroup()
        	                .addPreferredGap(getRandomizeRadiusButton(), getRadSlider(), LayoutStyle.ComponentPlacement.INDENT)
        	                .addGroup(thisLayout.createParallelGroup()
        	                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                        .addComponent(getRadSlider(), GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
        	                        .addGap(0, 182, Short.MAX_VALUE))
        	                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	                        .addComponent(getAlphaSlider(), GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
        	                        .addGap(0, 182, Short.MAX_VALUE))
        	                    .addComponent(getGraphPanel(), GroupLayout.Alignment.LEADING, 0, 417, Short.MAX_VALUE))))))
        	.addContainerGap());
        thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
    	.addContainerGap()
    	.addGroup(thisLayout.createParallelGroup()
    	    .addComponent(getJScrollPane1(), GroupLayout.Alignment.LEADING, 0, 223, Short.MAX_VALUE)
    	    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
    	        .addGroup(thisLayout.createParallelGroup()
    	            .addComponent(getSetRadiusLabel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
    	            .addComponent(getSetRadiusTextField(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
    	        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    	        .addComponent(getRadSlider(), GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
    	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    	        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	            .addComponent(getSetAlphaLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
    	            .addComponent(getSetAlphaTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
    	        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    	        .addComponent(getAlphaSlider(), GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
    	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
    	        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	            .addComponent(getRandomizeRadiusButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
    	            .addComponent(getRandomizeAlphaButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE))
    	        .addGap(6)))
    	.addComponent(getSelectAllButton(), GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
    	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    	.addComponent(getGraphPanel(), GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
    	.addContainerGap(10, 10));
        thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getRandomizeAlphaButton(), getRandomizeRadiusButton()});
        thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getSetAlphaTextField(), getSetRadiusTextField()});
        thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getAlphaSlider(), getRadSlider()});
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getRandomizeAlphaButton(), getRandomizeRadiusButton()});
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getSetAlphaLabel(), getSetRadiusLabel()});
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getSetAlphaTextField(), getSetRadiusTextField()});
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getAlphaSlider(), getRadSlider()});

      this.setPreferredSize(new java.awt.Dimension(440, 585));
      this.setSize(440, 585);
  }
  
  private JSlider getRadSlider() {
    if (radSlider == null) {
      radiusLabelTable = new Hashtable<Integer, JLabel>();
      radiusLabelTable.put(0, new JLabel("0"));
      radiusLabelTable.put(10, new JLabel("1"));
      radiusLabelTable.put(20, new JLabel("2"));
      radiusLabelTable.put(30, new JLabel("3"));
      radiusLabelTable.put(40, new JLabel("4"));
      radiusLabelTable.put(50, new JLabel("5"));
      
      radSlider = new JSlider();
      radSlider.setValue(10);
      radSlider.setMaximum(50);
      radSlider.setLabelTable(radiusLabelTable);
      radSlider.setMajorTickSpacing(5);
      radSlider.setMinorTickSpacing(1);
      radSlider.setPaintLabels(true);
      radSlider.setPaintTicks(true);
      radSlider.setEnabled(true);
      radSlider.setName("Radius");
      radSlider.addChangeListener(new SliderListener());
    }
    return radSlider;
  }
  
  public void setOwner(GeoquantViewer owner) {
    this.owner = owner;
  }

  private JButton getRandomizeRadiusButton() {
    if (randomizeRadiusButton == null) {
      randomizeRadiusButton = new JButton();
      randomizeRadiusButton.setText("Randomize Radii");
      randomizeRadiusButton.addActionListener(new RandomizeListener());
      randomizeRadiusButton.setName("Radius");
    }
    return randomizeRadiusButton;
  }
  private JScrollPane getJScrollPane1() {
    if (jScrollPane1 == null) {
      jScrollPane1 = new JScrollPane();
      jScrollPane1.setViewportView(getVertexList());
    }
    return jScrollPane1;
  }
  private JList getVertexList() {
    if (vertexList == null) {
      vertexList = new JList();
      vertexList.setVisibleRowCount(-1);
      vertexList.setPreferredSize(new java.awt.Dimension(137, 210));
      vertexList.setModel(new DefaultComboBoxModel(Triangulation.vertexTable.values().toArray()));
      vertexList.addListSelectionListener(new SelectionListener());
      vertexList.setPreferredSize(null);
    }
    return vertexList;
  }
  private JLabel getSetRadiusLabel() {
    if (setRadiusLabel == null) {
      setRadiusLabel = new JLabel();
      setRadiusLabel.setText("Radius:");
      setRadiusLabel.setHorizontalAlignment(SwingConstants.TRAILING);
    }
    return setRadiusLabel;
  }
  private JTextField getSetRadiusTextField() {
    if (setRadiusTextField == null) {
      setRadiusTextField = new JTextField();
      setRadiusTextField.addActionListener(new SetValueListener());
      setRadiusTextField.setName("Radius");
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
  private VertexGraphPanel getGraphPanel() {
    if (graphPanel == null) {
      graphPanel = new VertexGraphPanel();
      graphPanel.setBackground(new java.awt.Color(255,255,255));
      graphPanel.setBorder(new LineBorder(new java.awt.Color(0,0,0),1,true));
      graphPanel.setOwner(owner);
      graphPanel.setList(getVertexList());
    }
    return graphPanel;
  }

  protected void updateList() {
    getVertexList().setModel(new DefaultComboBoxModel(Triangulation.vertexTable.values().toArray()));
//    setAlphaTextField.setText(vertexList.getSelectedValue().toString().trim().);
//    setRadiusTextField.setText();
//    alphaSlider.setValue();
//    radSlider.setValue()
  }
  
  private JSlider getAlphaSlider() {
	  if(alphaSlider == null) {
	    alphaLabelTable = new Hashtable<Integer, JLabel>();
	    alphaLabelTable.put(0, new JLabel("0"));
	    alphaLabelTable.put(50, new JLabel("1"));
	    
		  alphaSlider = new JSlider();
		  alphaSlider.setValue(0);
		  alphaSlider.setMaximum(50);
		  alphaSlider.setLabelTable(alphaLabelTable);
		  alphaSlider.setMajorTickSpacing(5);
		  alphaSlider.setMinorTickSpacing(1);
		  alphaSlider.setPaintLabels(true);
		  alphaSlider.setPaintTicks(true);
		  alphaSlider.setEnabled(true);
		  alphaSlider.setName("Alpha");
		  alphaSlider.addChangeListener(new SliderListener());
	  }
	  return alphaSlider;
  }
  
  private JLabel getSetAlphaLabel() {
	  if(setAlphaLabel == null) {
		  setAlphaLabel = new JLabel();
		  setAlphaLabel.setText("Alpha:");
		  setAlphaLabel.setHorizontalAlignment(SwingConstants.TRAILING);
	  }
	  return setAlphaLabel;
  }
  
  private JTextField getSetAlphaTextField() {
	  if(setAlphaTextField == null) {
		  setAlphaTextField = new JTextField();
		  setAlphaTextField.setName("Alpha");
		  setAlphaTextField.addActionListener(new SetValueListener());
	  }
	  return setAlphaTextField;
  }
  
  private JButton getRandomizeAlphaButton() {
	  if(randomizeAlphaButton == null) {
		  randomizeAlphaButton = new JButton();
		  randomizeAlphaButton.setText("Randomize Alphas");
		  randomizeAlphaButton.setName("Alpha");
		  randomizeAlphaButton.addActionListener(new RandomizeListener());
	  }
	  return randomizeAlphaButton;
  }

  class RandomizeListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent evt) {
      JButton src = (JButton) evt.getSource();
      if(src.getName().equals("Radius")) {
        Object[] selected = vertexList.getSelectedValues();
        Random r = new Random();
        for(int i = 0; i < selected.length; i++) {
          Radius.at((Vertex) selected[i]).setValue(3 * r.nextDouble() + 0.1);
        }
        if(selected.length != 0) {
          Vertex v = (Vertex) getVertexList().getModel().getElementAt(getVertexList().getSelectedIndex());
          getSetRadiusTextField().setText("" + Radius.valueAt(v));
        }
      } else {
        Object[] selected = vertexList.getSelectedValues();
        Random r = new Random();
        for(int i = 0; i < selected.length; i++) {
          Alpha.at((Vertex) selected[i]).setValue(r.nextDouble());
        }
        if(selected.length != 0) {
          Vertex v = (Vertex) getVertexList().getModel().getElementAt(getVertexList().getSelectedIndex());
          getSetAlphaTextField().setText("" + Alpha.valueAt(v));
        }
      }
      graphPanel.repaint();
      owner.getPolygonPanel().repaint();
    }
  }
    
   class SelectAllListener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        vertexList.setSelectionInterval(0, vertexList.getModel().getSize() - 1);
      }
    }
  
  class SetValueListener implements ActionListener {
    
    public void actionPerformed(ActionEvent evt) {
      try {
        JTextField src = (JTextField) evt.getSource();
        Object[] selected = vertexList.getSelectedValues();
        double val = Double.parseDouble(src.getText());

        if(src.getName().equals("Radius")) {
          for(int i = 0; i < selected.length; i++) {
            Radius.at((Vertex) selected[i]).setValue(val);
          }
        } else {
          for(int i = 0; i < selected.length; i++) {
            Alpha.at((Vertex) selected[i]).setValue(val);
          }
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

    public void stateChanged(ChangeEvent evt) {
      try {
        JSlider src = (JSlider) evt.getSource();
        if(!src.getValueIsAdjusting()) {
          Object[] selected = vertexList.getSelectedValues();

          double val;
          if(src.getName().equals("Radius")) {
            val = getRadSlider().getValue() / 10.0;

            for(int i = 0; i < selected.length; i++) {
              Radius.at((Vertex) selected[i]).setValue(val);
            }

            getSetRadiusTextField().setText( "" +   val );
          } else {
            val = getAlphaSlider().getValue() / 50.0;
            
            for(int i = 0; i < selected.length; i++) {
              Alpha.at((Vertex) selected[i]).setValue(val);
            }
            
            getSetAlphaTextField().setText( "" +   val );
          }

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

        if (getVertexList().getSelectedIndex() != -1) {
          Vertex v = (Vertex) getVertexList().getModel().getElementAt(getVertexList().getSelectedIndex());
          getSetRadiusTextField().setText("" + Radius.valueAt(v));
          getSetAlphaTextField().setText("" + Alpha.valueAt(v));
        }
        getGraphPanel().repaint();
      }
    }
    
  }
}
