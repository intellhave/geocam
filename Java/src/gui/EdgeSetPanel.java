package gui;

import geoquant.Eta;
import geoquant.Length;

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

import triangulation.Edge;
import triangulation.Triangulation;


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
public class EdgeSetPanel extends JPanel {

  {
    //Set Look & Feel
    try {
      javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private Hashtable<Integer, JLabel> etaLabelTable;
  private JButton randomizeLengthButton;
  private JTextField setLengthTextField;
  private Hashtable<Integer, JLabel> lengthLabelTable;
  private JSlider etaSlider;
  private JScrollPane jScrollPane1;
  private JSlider lengthSlider;
  private JLabel setLengthLabel;
  private EdgeGraphPanel graphPanel;
  private JButton selectAllButton;
  private JTextField setEtaTextField;
  private JLabel setEtaLabel;
  private JList edgeList;
  private JButton randomizeEtaButton;
  private GeoquantViewer owner;

  public EdgeSetPanel() {
    super();
    GroupLayout thisLayout = new GroupLayout((JComponent)this);
    this.setLayout(thisLayout);
        thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
        	.addContainerGap()
        	.addGroup(thisLayout.createParallelGroup()
        	    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	            .addComponent(getSetEtaLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        	            .addComponent(getSetEtaTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
        	        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        	        .addComponent(getEtaSlider(), GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        	        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	            .addComponent(getSetLengthLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
        	            .addComponent(getSetLengthTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE))
        	        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        	        .addComponent(getLengthSlider(), GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
        	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
        	        .addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
        	            .addComponent(getRandomizeEtaButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
        	            .addComponent(getRandomizeLengthButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE))
        	        .addGap(6))
        	    .addComponent(getJScrollPane1(), GroupLayout.Alignment.LEADING, 0, 226, Short.MAX_VALUE))
        	.addComponent(getSelectAllButton(), GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
        	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        	.addComponent(getGraphPanel(), GroupLayout.PREFERRED_SIZE, 293, GroupLayout.PREFERRED_SIZE)
        	.addContainerGap(10, 10));
        thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
    	.addContainerGap()
    	.addGroup(thisLayout.createParallelGroup()
    	    .addGroup(thisLayout.createSequentialGroup()
    	        .addGroup(thisLayout.createParallelGroup()
    	            .addComponent(getSetLengthLabel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
    	            .addComponent(getSetEtaLabel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
    	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    	        .addGroup(thisLayout.createParallelGroup()
    	            .addGroup(thisLayout.createSequentialGroup()
    	                .addComponent(getSetLengthTextField(), GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE))
    	            .addGroup(thisLayout.createSequentialGroup()
    	                .addComponent(getSetEtaTextField(), GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)))
    	        .addGap(0, 196, Short.MAX_VALUE))
    	    .addGroup(thisLayout.createSequentialGroup()
    	        .addPreferredGap(getSetLengthLabel(), getRandomizeEtaButton(), LayoutStyle.ComponentPlacement.INDENT)
    	        .addGroup(thisLayout.createParallelGroup()
    	            .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
    	                .addComponent(getRandomizeEtaButton(), GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
    	                .addComponent(getRandomizeLengthButton(), 0, 141, Short.MAX_VALUE)
    	                .addGroup(thisLayout.createParallelGroup()
    	                    .addGroup(thisLayout.createSequentialGroup()
    	                        .addComponent(getJScrollPane1(), GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)
    	                        .addGap(0, 0, Short.MAX_VALUE))
    	                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
    	                        .addGap(16)
    	                        .addComponent(getSelectAllButton(), GroupLayout.PREFERRED_SIZE, 104, GroupLayout.PREFERRED_SIZE)
    	                        .addGap(0, 23, Short.MAX_VALUE))))
    	            .addGroup(thisLayout.createSequentialGroup()
    	                .addPreferredGap(getRandomizeEtaButton(), getEtaSlider(), LayoutStyle.ComponentPlacement.INDENT)
    	                .addGroup(thisLayout.createParallelGroup()
    	                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
    	                        .addComponent(getEtaSlider(), GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
    	                        .addGap(0, 184, Short.MAX_VALUE))
    	                    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
    	                        .addComponent(getLengthSlider(), GroupLayout.PREFERRED_SIZE, 235, GroupLayout.PREFERRED_SIZE)
    	                        .addGap(0, 184, Short.MAX_VALUE))
    	                    .addComponent(getGraphPanel(), GroupLayout.Alignment.LEADING, 0, 419, Short.MAX_VALUE))))))
    	.addContainerGap());
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getRandomizeLengthButton(), getRandomizeEtaButton()});
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getSetLengthLabel(), getSetEtaLabel()});
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getSetLengthTextField(), getSetEtaTextField()});
        thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getLengthSlider(), getEtaSlider()});
        thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getRandomizeLengthButton(), getRandomizeEtaButton()});
        thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getSetLengthTextField(), getSetEtaTextField()});
        thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getLengthSlider(), getEtaSlider()});

      this.setPreferredSize(new java.awt.Dimension(440, 585));
      this.setSize(440, 585);
  }
  
  private JSlider getEtaSlider() {
    if (etaSlider == null) {
      etaLabelTable = new Hashtable<Integer, JLabel>();
      etaLabelTable.put(0, new JLabel("0"));
      etaLabelTable.put(10, new JLabel("1"));
      etaLabelTable.put(20, new JLabel("2"));
      etaLabelTable.put(30, new JLabel("3"));
      etaLabelTable.put(40, new JLabel("4"));
      etaLabelTable.put(50, new JLabel("5"));
      
      etaSlider = new JSlider();
      etaSlider.setValue(10);
      etaSlider.setMaximum(50);
      etaSlider.setLabelTable(etaLabelTable);
      etaSlider.setMajorTickSpacing(5);
      etaSlider.setMinorTickSpacing(1);
      etaSlider.setPaintLabels(true);
      etaSlider.setPaintTicks(true);
      etaSlider.setEnabled(true);
      etaSlider.setName("Eta");
      etaSlider.addChangeListener(new SliderListener());
    }
    return etaSlider;
  }
  
  public void setOwner(GeoquantViewer owner) {
    this.owner = owner;
  }

  private JButton getRandomizeEtaButton() {
    if (randomizeEtaButton == null) {
      randomizeEtaButton = new JButton();
      randomizeEtaButton.setText("Randomize Etas");
      randomizeEtaButton.addActionListener(new RandomizeListener());
      randomizeEtaButton.setName("Eta");
    }
    return randomizeEtaButton;
  }
  private JScrollPane getJScrollPane1() {
    if (jScrollPane1 == null) {
      jScrollPane1 = new JScrollPane();
      jScrollPane1.setViewportView(getEdgeList());
    }
    return jScrollPane1;
  }
  private JList getEdgeList() {
    if (edgeList == null) {
      edgeList = new JList();
      edgeList.setVisibleRowCount(-1);
      edgeList.setPreferredSize(new java.awt.Dimension(137, 210));
      edgeList.setModel(new DefaultComboBoxModel(Triangulation.edgeTable.values().toArray()));
      edgeList.addListSelectionListener(new SelectionListener());
      
      edgeList.setPreferredSize(null);
    }
    return edgeList;
  }
  private JLabel getSetEtaLabel() {
    if (setEtaLabel == null) {
      setEtaLabel = new JLabel();
      setEtaLabel.setText("Eta:");
      setEtaLabel.setHorizontalAlignment(SwingConstants.TRAILING);
    }
    return setEtaLabel;
  }
  private JTextField getSetEtaTextField() {
    if (setEtaTextField == null) {
      setEtaTextField = new JTextField();
      setEtaTextField.addActionListener(new SetValueListener());
      setEtaTextField.setName("Eta");
    }
    return setEtaTextField;
  }
  private JButton getSelectAllButton() {
    if (selectAllButton == null) {
      selectAllButton = new JButton();
      selectAllButton.setText("Select All");
      selectAllButton.addActionListener(new SelectAllListener());
    }
    return selectAllButton;
  }
  private EdgeGraphPanel getGraphPanel() {
    if (graphPanel == null) {
      graphPanel = new EdgeGraphPanel();
      graphPanel.setBackground(new java.awt.Color(255,255,255));
      graphPanel.setBorder(new LineBorder(new java.awt.Color(0,0,0),1,true));
      graphPanel.setOwner(owner);
      graphPanel.setList(getEdgeList());
    }
    return graphPanel;
  }

  protected void updateList() {
    getEdgeList().setModel(new DefaultComboBoxModel(Triangulation.edgeTable.values().toArray()));
  }
  
  private JSlider getLengthSlider() {
    if(lengthSlider == null) {
      lengthLabelTable = new Hashtable<Integer, JLabel>();
      lengthLabelTable.put(0, new JLabel("0"));
      lengthLabelTable.put(10, new JLabel("1"));
      lengthLabelTable.put(20, new JLabel("2"));
      lengthLabelTable.put(30, new JLabel("3"));
      lengthLabelTable.put(40, new JLabel("4"));
      lengthLabelTable.put(50, new JLabel("5"));
      
      lengthSlider = new JSlider();
      lengthSlider.setValue(0);
      lengthSlider.setMaximum(50);
      lengthSlider.setLabelTable(lengthLabelTable);
      lengthSlider.setMajorTickSpacing(5);
      lengthSlider.setMinorTickSpacing(1);
      lengthSlider.setPaintLabels(true);
      lengthSlider.setPaintTicks(true);
      lengthSlider.setEnabled(true);
      lengthSlider.setName("Length");
      lengthSlider.addChangeListener(new SliderListener());
    }
    return lengthSlider;
  }
  
  private JLabel getSetLengthLabel() {
    if(setLengthLabel == null) {
      setLengthLabel = new JLabel();
      setLengthLabel.setText("Length:");
      setLengthLabel.setHorizontalAlignment(SwingConstants.TRAILING);
    }
    return setLengthLabel;
  }
  
  private JTextField getSetLengthTextField() {
    if(setLengthTextField == null) {
      setLengthTextField = new JTextField();
      setLengthTextField.setName("Length");
      setLengthTextField.addActionListener(new SetValueListener());
    }
    return setLengthTextField;
  }
  
  private JButton getRandomizeLengthButton() {
    if(randomizeLengthButton == null) {
      randomizeLengthButton = new JButton();
      randomizeLengthButton.setText("Randomize Lengths");
      randomizeLengthButton.setName("Length");
      randomizeLengthButton.addActionListener(new RandomizeListener());
    }
    return randomizeLengthButton;
  }

  class RandomizeListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent evt) {
      JButton src = (JButton) evt.getSource();
      if(src.getName().equals("Eta")) {
        Object[] selected = edgeList.getSelectedValues();
        Random r = new Random();
        for(int i = 0; i < selected.length; i++) {
          Eta.at((Edge) selected[i]).setValue(2 * r.nextDouble() + 0.1);
        }
        if(selected.length != 0) {
          Edge e = (Edge) getEdgeList().getModel().getElementAt(getEdgeList().getSelectedIndex());
          getSetEtaTextField().setText("" + Eta.valueAt(e));
          getSetLengthTextField().setText("" + Length.valueAt(e));
        }
      } else {
        Object[] selected = edgeList.getSelectedValues();
        Random r = new Random();
        for(int i = 0; i < selected.length; i++) {
          Length.at((Edge) selected[i]).setValue(r.nextDouble());
        }
        if(selected.length != 0) {
          Edge e = (Edge) getEdgeList().getModel().getElementAt(getEdgeList().getSelectedIndex());
          getSetLengthTextField().setText("" + Length.valueAt(e));
        }
      }
      graphPanel.repaint();
      owner.getPolygonPanel().repaint();
    }
  }
    
   class SelectAllListener implements ActionListener {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        edgeList.setSelectionInterval(0, edgeList.getModel().getSize() - 1);
      }
    }
  
  class SetValueListener implements ActionListener {
    
    public void actionPerformed(ActionEvent evt) {
      try {
        JTextField src = (JTextField) evt.getSource();
        Object[] selected = edgeList.getSelectedValues();
        double val = Double.parseDouble(src.getText());

        if(src.getName().equals("Eta")) {
          for(int i = 0; i < selected.length; i++) {
            Eta.at((Edge) selected[i]).setValue(val);
          }
          if(selected.length != 0) {
            Edge e = (Edge) getEdgeList().getModel().getElementAt(getEdgeList().getSelectedIndex());
            getSetLengthTextField().setText("" + Length.valueAt(e));
          }
        } else {
          for(int i = 0; i < selected.length; i++) {
            Length.at((Edge) selected[i]).setValue(val);
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
          Object[] selected = edgeList.getSelectedValues();

          double val;
          if(src.getName().equals("Eta")) {
            val = getEtaSlider().getValue() / 10.0;

            for(int i = 0; i < selected.length; i++) {
              Eta.at((Edge) selected[i]).setValue(val);
            }

            getSetEtaTextField().setText( "" +   val );
            if(selected.length != 0) {
              Edge e = (Edge) getEdgeList().getModel().getElementAt(getEdgeList().getSelectedIndex());
              getSetLengthTextField().setText("" + Length.valueAt(e));
            }
          } else {
            val = getLengthSlider().getValue() / 10.0;
            
            for(int i = 0; i < selected.length; i++) {
              Length.at((Edge) selected[i]).setValue(val);
            }
            
            getSetLengthTextField().setText( "" +   val );
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

    public void valueChanged(ListSelectionEvent evt) {
      if (!evt.getValueIsAdjusting()) {

        if (getEdgeList().getSelectedIndex() != -1) {
          Edge e = (Edge) getEdgeList().getModel().getElementAt(getEdgeList().getSelectedIndex());
          getSetEtaTextField().setText("" + Eta.valueAt(e));
          getSetLengthTextField().setText("" + Length.valueAt(e));  
        }
        getGraphPanel().repaint();
      }
    }
    
  }
}
