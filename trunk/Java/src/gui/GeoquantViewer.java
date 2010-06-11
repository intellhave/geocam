package gui;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.SwingUtilities;

import Geoquant.Alpha;
import Geoquant.Eta;
import Geoquant.Geometry;
import Geoquant.Length;
import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Simplex;
import Triangulation.Triangulation;
import Triangulation.Vertex;

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
public class GeoquantViewer extends javax.swing.JFrame {
  private JList EdgeList;
  private AbstractAction importAction;
  private EdgeDisplayPanel edgeDisplayPanel;
  private JRadioButton circlePackRadioButton;
  private ButtonGroup alphaButtonGroup;
  private JRadioButton perpBisectorRadioButton;
  private JLabel rad2SetLabel;
  private JTextField rad2SetField;
  private JSlider rad2Slider;
  private JLabel rad1SetLabel;
  private JTextField rad1SetField;
  private JSlider rad1Slider;
  private JScrollPane edgeListScrollPane;
  private JPanel quantityModPanel;
  private AbstractAction import2DAction;
  private JMenuItem jMenuItem1;
  private JFileChooser triangulationFileChooser;
  private JMenuItem importMenuItem;
  private JMenu jMenu1;
  private JMenuBar mainMenuBar;
  private JSlider etaSlider;
  private JLabel etaSetLabel;
  private JTextField etaSetField;
  private ListModel EdgeListModel;
  private Hashtable<Integer, JLabel> labelTable;

  /**
  * Auto-generated main method to display this JFrame
  */
  public static void main(String[] args) {
    TriangulationIO.read3DTriangulationFile("Data/3DManifolds/StandardFormat/pentachoron.txt");
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        GeoquantViewer inst = new GeoquantViewer();
        inst.setLocationRelativeTo(null);
        inst.setVisible(true);
      }
    });
  }
  
  public GeoquantViewer() {
    super();
    initGUI();
  }
  
  private void initGUI() {
    try {
      GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
      getContentPane().setLayout(thisLayout);
        thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
          .addContainerGap()
          .addComponent(getQuantityModPanel(), 0, 576, Short.MAX_VALUE)
          .addContainerGap());
        thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
          .addContainerGap(520, 520)
          .addComponent(getQuantityModPanel(), GroupLayout.PREFERRED_SIZE, 389, GroupLayout.PREFERRED_SIZE)
          .addContainerGap(19, Short.MAX_VALUE));
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      {
        mainMenuBar = new JMenuBar();
        setJMenuBar(mainMenuBar);
        {
          jMenu1 = new JMenu();
          mainMenuBar.add(jMenu1);
          jMenu1.setText("File");
          {
            importMenuItem = new JMenuItem();
            jMenu1.add(getJMenuItem1());
            jMenu1.add(importMenuItem);
            importMenuItem.setText("Import 3D Triangulation");
            importMenuItem.setAction(getImportAction());
          }
        }
      }
      pack();
      this.setSize(936, 659);
    } catch (Exception e) {
        //add your error handling code here
      e.printStackTrace();
    }
  }
  
  private AbstractAction getImportAction() {
    if(importAction == null) {
      importAction = new AbstractAction("Import 3D Triangulation", null) {
        public void actionPerformed(ActionEvent e) {
          //Handle open button action.
          getTriangulationFileChooser();
          int returnVal = getTriangulationFileChooser().showOpenDialog(GeoquantViewer.this);
          
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getTriangulationFileChooser().getSelectedFile();
            Triangulation.reset();
            TriangulationIO.read3DTriangulationFile(file);
            newTriangulation();
          }
        }
      };
    }
    return importAction;
  }
  
  private JFileChooser getTriangulationFileChooser() {
    if(triangulationFileChooser == null) {
      triangulationFileChooser = new JFileChooser("Data");
    }
    return triangulationFileChooser;
  }

  class SliderListener implements ChangeListener {

    public void stateChanged(ChangeEvent e) {
      JSlider source = (JSlider)e.getSource();
      if (!source.getValueIsAdjusting()) {
        double value = (double)source.getValue() / 10.0;
        String name = source.getName();
        int index = EdgeList.getSelectedIndex();
        if(index == -1) {
          return;
        }
        Edge s = (Edge) EdgeListModel.getElementAt(index);
        if(name.equals("etaSlider")) {
          etaSetField.setText("" + value);
          Eta.At((Edge)s).setValue(value);         
        } else if(name.equals("rad1Slider")) {
          rad1SetField.setText("" + value);
          Radius.At(s.getLocalVertices().get(0)).setValue(value);
        } else if(name.equals("rad2Slider")) {
          rad2SetField.setText("" + value);
          Radius.At(s.getLocalVertices().get(1)).setValue(value);
        }
      } 
    }
    
  }

  private void newTriangulation() {
    // Modify Edge List
    EdgeListModel = 
      new DefaultComboBoxModel(Triangulation.edgeTable.values().toArray());
    EdgeList.setModel(EdgeListModel);
    for(Radius r : Geometry.getRadii()) {
      r.setValue(1.0);
    }
    for(Alpha a : Geometry.getAlphas()) {
      a.setValue(1.0);
    }
    for(Eta e : Geometry.getEtas()) {
      e.setValue(1.0);
    }
  }
  
  private JMenuItem getJMenuItem1() {
    if(jMenuItem1 == null) {
      jMenuItem1 = new JMenuItem();
      jMenuItem1.setText("jMenuItem1");
      jMenuItem1.setAction(getImport2DAction());
    }
    return jMenuItem1;
  }
  
  private AbstractAction getImport2DAction() {
    if(import2DAction == null) {
      import2DAction = new AbstractAction("Import 2D Triangulation", null) {
        public void actionPerformed(ActionEvent evt) {
          //Handle open button action.
          getTriangulationFileChooser();
          int returnVal = getTriangulationFileChooser().showOpenDialog(GeoquantViewer.this);
          
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getTriangulationFileChooser().getSelectedFile();
            //This is where a real application would open the file.
            Triangulation.reset();
            TriangulationIO.read2DTriangulationFile(file);
            newTriangulation();
          }
        }
      };
    }
    return import2DAction;
  }
  
  private JPanel getQuantityModPanel() {
    if(quantityModPanel == null) {
      quantityModPanel = new JPanel();
      GroupLayout quantityModPanelLayout = new GroupLayout((JComponent)quantityModPanel);
      quantityModPanel.setLayout(quantityModPanelLayout);
      quantityModPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
      quantityModPanel.setForeground(new java.awt.Color(0,0,0));
      {
        etaSetLabel = new JLabel();
        etaSetLabel.setText("Eta:");
        etaSetLabel.setBackground(new java.awt.Color(192,192,192));
        etaSetLabel.setForeground(new java.awt.Color(0,0,0));
        etaSetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        rad1SetLabel = new JLabel();
        rad1SetLabel.setText("Radius:");
        rad1SetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rad1SetLabel.setBackground(new java.awt.Color(192,192,192));
        rad1SetLabel.setForeground(new java.awt.Color(0,0,0));
        
        rad2SetLabel = new JLabel();
        rad2SetLabel.setText("Radius:");
        rad2SetLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rad2SetLabel.setBackground(new java.awt.Color(192,192,192));
        rad2SetLabel.setForeground(new java.awt.Color(0,0,0));
      }
      {
        etaSetField = new JTextField();
        etaSetField.setHorizontalAlignment(SwingConstants.TRAILING);
        etaSetField.setName("etaSetField");
        etaSetField.addActionListener(new SetValueListener());
        
        rad1SetField = new JTextField();
        rad1SetField.setHorizontalAlignment(SwingConstants.TRAILING);
        rad1SetField.setName("rad1SetField");
        rad1SetField.addActionListener(new SetValueListener());
        
        rad2SetField = new JTextField();
        rad2SetField.setHorizontalAlignment(SwingConstants.TRAILING);
        rad2SetField.setName("rad2SetField");
        rad2SetField.addActionListener(new SetValueListener());
      }
      {

        labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(0, new JLabel("0"));
        labelTable.put(10, new JLabel("1"));
        labelTable.put(20, new JLabel("2"));
        labelTable.put(30, new JLabel("3"));
        labelTable.put(40, new JLabel("4"));
        labelTable.put(50, new JLabel("5"));
        
        etaSlider = new JSlider();
        etaSlider.setMaximum(50);
        etaSlider.setMajorTickSpacing(5);
        etaSlider.setMinorTickSpacing(1);
        etaSlider.setValue(10);
        etaSlider.setPaintTicks(true);
        etaSlider.setLabelTable(labelTable);
        etaSlider.setPaintLabels(true);
        etaSlider.setName("etaSlider");
        etaSlider.addChangeListener(new SliderListener());
        
        rad1Slider = new JSlider();
        rad1Slider.setValue(10);
        rad1Slider.setMaximum(50);
        rad1Slider.setLabelTable(labelTable);
        rad1Slider.setMajorTickSpacing(5);
        rad1Slider.setMinorTickSpacing(1);
        rad1Slider.setPaintLabels(true);
        rad1Slider.setPaintTicks(true);
        rad1Slider.setName("rad1Slider");
        rad1Slider.addChangeListener(new SliderListener());
        
        rad2Slider = new JSlider();
        rad2Slider.setValue(10);
        rad2Slider.setMaximum(50);
        rad2Slider.setLabelTable(labelTable);
        rad2Slider.setMajorTickSpacing(5);
        rad2Slider.setMinorTickSpacing(1);
        rad2Slider.setPaintLabels(true);
        rad2Slider.setPaintTicks(true);
        rad2Slider.setName("rad2Slider");
        rad2Slider.addChangeListener(new SliderListener());
      }
      quantityModPanelLayout.setHorizontalGroup(quantityModPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(quantityModPanelLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                .addGroup(quantityModPanelLayout.createParallelGroup()
                    .addGroup(quantityModPanelLayout.createSequentialGroup()
                        .addGroup(quantityModPanelLayout.createParallelGroup()
                            .addComponent(getCirclePackRadioButton(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(rad2SetLabel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                                .addGap(14))
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(etaSetLabel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                                .addGap(14)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(quantityModPanelLayout.createParallelGroup()
                            .addComponent(getPerpBisectorRadioButton(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(rad2SetField, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                .addGap(7))
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(rad1SetField, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                .addGap(7))
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(etaSetField, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
                                .addGap(7))))
                    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                        .addComponent(etaSlider, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                        .addGap(19))
                    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                        .addComponent(rad1Slider, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                        .addGap(19))
                    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                        .addComponent(rad2Slider, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                        .addGap(19)))
                .addComponent(getEdgeListScrollPane(), GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(quantityModPanelLayout.createSequentialGroup()
                .addPreferredGap(getCirclePackRadioButton(), rad1SetLabel, LayoutStyle.ComponentPlacement.INDENT)
                .addGroup(quantityModPanelLayout.createParallelGroup()
                    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                        .addComponent(rad1SetLabel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 251, Short.MAX_VALUE))
                    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                        .addGap(28)
                        .addComponent(getEdgeDisplayPanel(), GroupLayout.PREFERRED_SIZE, 306, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(22)))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED));
      quantityModPanelLayout.setVerticalGroup(quantityModPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(quantityModPanelLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(etaSetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                    .addComponent(etaSetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(etaSlider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(19)
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rad1SetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                    .addComponent(rad1SetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rad1Slider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(22)
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rad2SetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                    .addComponent(rad2SetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rad2Slider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(22)
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(getCirclePackRadioButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
                    .addComponent(getPerpBisectorRadioButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
                .addGap(12))
            .addComponent(getEdgeListScrollPane(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 294, GroupLayout.PREFERRED_SIZE))
        .addGap(41)
        .addComponent(getEdgeDisplayPanel(), GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(22, Short.MAX_VALUE));
    }
    return quantityModPanel;
  }
  
  private JScrollPane getEdgeListScrollPane() {
    if(edgeListScrollPane == null) {
      edgeListScrollPane = new JScrollPane();
      {
        EdgeListModel = 
          new DefaultComboBoxModel();
        EdgeList = new JList();
        edgeListScrollPane.setViewportView(EdgeList);
        EdgeList.setModel(EdgeListModel);
        EdgeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        EdgeList.addListSelectionListener(new EdgeListSelectionListener());
        EdgeList.setVisibleRowCount(-1);
        EdgeList.setPreferredSize(null);
      }
    }
    return edgeListScrollPane;
  }
  
  private JRadioButton getCirclePackRadioButton() {
    if(circlePackRadioButton == null) {
      circlePackRadioButton = new JRadioButton();
      circlePackRadioButton.setText("Circle Packing");
      circlePackRadioButton.setActionCommand("1");
      circlePackRadioButton.addActionListener(new AlphaButtonListener());
      getAlphaButtonGroup().add(circlePackRadioButton);
      circlePackRadioButton.setSelected(true);
      circlePackRadioButton.setFont(new java.awt.Font("Tahoma",0,10));
    }
    return circlePackRadioButton;
  }
  
  private JRadioButton getPerpBisectorRadioButton() {
    if(perpBisectorRadioButton == null) {
      perpBisectorRadioButton = new JRadioButton();
      perpBisectorRadioButton.setText("Perp. Bisector");
      perpBisectorRadioButton.setActionCommand("0");
      perpBisectorRadioButton.setFont(new java.awt.Font("Tahoma",0,10));
      perpBisectorRadioButton.addActionListener(new AlphaButtonListener());
      getAlphaButtonGroup().add(perpBisectorRadioButton);
    }
    return perpBisectorRadioButton;
  }
  
  private ButtonGroup getAlphaButtonGroup() {
    if(alphaButtonGroup == null) {
      alphaButtonGroup = new ButtonGroup();
    }
    return alphaButtonGroup;
  }
  
  private EdgeDisplayPanel getEdgeDisplayPanel() {
    if(edgeDisplayPanel == null) {
      edgeDisplayPanel = new EdgeDisplayPanel();
    }
    return edgeDisplayPanel;
  }

  class EdgeListSelectionListener implements ListSelectionListener {

    public void valueChanged(ListSelectionEvent e) {
      if (!e.getValueIsAdjusting()) {

        if (EdgeList.getSelectedIndex() == -1) {
          //No selection, disable text field, slider.
          etaSetField.setEnabled(false);
          etaSlider.setEnabled(false);
          rad1SetField.setEnabled(false);
          rad1Slider.setEnabled(false);
          rad2SetField.setEnabled(false);
          rad2SetField.setEnabled(false);

        } else {
          // selection, enable text field, slider, change label name
          etaSetField.setEnabled(true);
          etaSlider.setEnabled(true);
          rad1SetField.setEnabled(true);
          rad1Slider.setEnabled(true);
          rad2SetField.setEnabled(true);
          rad2SetField.setEnabled(true);
          Edge s = (Edge) EdgeListModel.getElementAt(EdgeList.getSelectedIndex());
          Vertex v1 = s.getLocalVertices().get(0);
          Vertex v2 = s.getLocalVertices().get(1);
          rad1SetLabel.setText("Radius " + v1.getIndex() + ":");
          rad1SetField.setText("" + Radius.valueAt(v1));
          rad2SetLabel.setText("Radius " + v2.getIndex() + ":");
          rad2SetField.setText("" + Radius.valueAt(v2));
          etaSetField.setText("" + Eta.valueAt(s));
          System.out.println("Alpha: " + Alpha.valueAt(v1));
        }
      }
    }
    
  }
  
  class SetValueListener implements ActionListener {
    
    public void actionPerformed(ActionEvent e) {
      try {
        JTextField setField = (JTextField) e.getSource();
        int index = EdgeList.getSelectedIndex();
        if(index == -1) {
          return;
        }
        double value = Double.parseDouble(setField.getText());
        String name = setField.getName();
        Edge s = (Edge) EdgeListModel.getElementAt(index);
        if(name.equals("etaSetField")) {
          Eta.At(s).setValue(value);         
        } else if(name.equals("rad1SetField")) {
          Radius.At(s.getLocalVertices().get(0)).setValue(value);
        } else if(name.equals("rad2SetField")) {
          Radius.At(s.getLocalVertices().get(1)).setValue(value);
        }
      } catch (NumberFormatException exc) {
        return;
      } catch (ClassCastException exc) {
        return;
      }
      
    }
    
  }
  
  class AlphaButtonListener implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      try{
        double value = Double.parseDouble(e.getActionCommand());
        for(Alpha a : Geometry.getAlphas()) {
          a.setValue(value);
        }
      } catch(NumberFormatException exc) {
        return;
      }
      
    }
    
  }
  
  class EdgeDisplayPanel extends JPanel {
    public EdgeDisplayPanel() {
      super();
      setBorder(BorderFactory.createLineBorder(Color.black));
      setBackground(Color.WHITE);
    }
    
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(Color.WHITE);
      int index = EdgeList.getSelectedIndex();
      if(index == -1) {
        return;
      }
      Edge s = (Edge) EdgeListModel.getElementAt(index);
      double length = Length.valueAt(s);
      
    }
  }
}