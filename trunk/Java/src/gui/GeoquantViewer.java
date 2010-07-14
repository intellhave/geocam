package gui;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.SwingUtilities;

import Geoquant.*;
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
public class GeoquantViewer extends javax.swing.JFrame implements ItemListener{

	{
		//Set Look & Feel
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
  private JList EdgeList;
  private AbstractAction importAction;
  private JPanel edgeDisplayPanel;
  private JRadioButton circlePackRadioButton;
  private JCheckBox angleCheck;
  private JCheckBox curvPartialCheck;
  private JPanel secondPartialSelectPanel;
  private JPanel partialSelectPanel;
  private JTabbedPane geoSelectTabbedPane;
  private JCheckBox radiusCheck;
  private JCheckBox faceHeightCheck;
  private JCheckBox etaCheck;
  private JCheckBox edgeHeightCheck;
  private JCheckBox dualAreaCheck;
  private JPanel geoPolygonPanel;
  private JCheckBox curv2DCheck;
  private JCheckBox curv3DCheck;
  private JCheckBox areaCheck;
  private JCheckBox partialEdgeCheck;
  private JCheckBox vehrCheck;
  private JCheckBox dihAngleCheck;
  private JCheckBox volumeCheck;
  private JCheckBox lengthCheck;
  private JPanel basicSelectPanel;
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
  private JCheckBox volumeSecondPartialCheck;
  private JCheckBox partialEdgeSecondPartialCheck;
  private JCheckBox nehrSecondPartialCheck;
  private JCheckBox dihAngleSecondPartialCheck;
  private JCheckBox curvatureSecondPartialCheck;
  private JCheckBox volumePartialCheck;
  private JCheckBox radiusPartialCheck;
  private JCheckBox partialEdgePartialCheck;
  private JCheckBox nehrPartialCheck;
  private JCheckBox dihAnglePartialCheck;
  private AbstractAction saveAction;
  private JCheckBox totalVolumeSecondPartialCheck;
  private JCheckBox totalVolumePartialCheck;
  private JCheckBox coneAngleCheck;
  private JCheckBox totalVolumeCheck;
  private JCheckBox totalCurvatureCheck;
  private JSlider lengthSlider;
  private JLabel lengthSetLabel;
  private JCheckBox edgeCurvatureCheck;
  private AbstractAction showVehrFlowDialog;
  private AbstractAction showEtaDialog;
  private JDialog setEtaDialog;
  private AbstractAction showSetRadiiDialog;
  private JLabel setRadiiLabel;
  private JLabel setEtaLabel;
  private JTextField setRadiiTextField;
  private JTextField setEtaTextField;
  private JButton okButton_SR;
  private JButton okButton_SE;
  private JButton cancelButton_SR;
  private JButton cancelButton_SE;
  private JDialog setRadiiDialog;
  private JMenuItem setEtaMenuItem;
  private JMenuItem setRadiiMenuItem;
  private JMenu editMenu;
  private VEHRFlowDialog nehrFlowDialog;
  private YamabeFlowDialog yamabe2DDialog;
  private YamabeFlowDialog yamabe3DDialog;
  private JMenuItem nehrFlowMenuItem;
  private JTextField lengthSetField;
  private JCheckBox vEinsteinCheck;
  private JCheckBox lEinsteinCheck;
  private JCheckBox vcscCheck;
  private JCheckBox lcscCheck;
  private JCheckBox lehrCheck;
  private AbstractAction saveGeoquantAction;
  private JMenuItem saveGeoquantsMenuItem;
  private AbstractAction showYamabe3DDialog;
  private JMenuItem yamabe3DMenuItem;
  private AbstractAction showYamabe2DDialog;
  private JMenuItem yamabe2DMenuItem;
  private JMenu runMenu;
  private JMenuItem saveMenu;
  private JFileChooser triangulationFileChooser;
  private JMenuItem importMenuItem;
  private JMenu fileMenu;
  private JMenuBar mainMenuBar;
  private JSlider etaSlider;
  private JLabel etaSetLabel;
  private JTextField etaSetField;
  private ListModel EdgeListModel;
  private Hashtable<Integer, JLabel> labelTable;
  private HashMap<GeoPoint, Geoquant> geoMap;
  private HashMap<JCheckBox, Color> geoColorTable;
  
  /**
  * Auto-generated main method to display this JFrame
  */
  public static void main(String[] args) {
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
        	.addGroup(thisLayout.createParallelGroup()
        	    .addGroup(thisLayout.createSequentialGroup()
        	        .addComponent(getQuantityModPanel(), GroupLayout.PREFERRED_SIZE, 622, GroupLayout.PREFERRED_SIZE))
        	    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
        	        .addComponent(getGeoPolygonPanel(), GroupLayout.PREFERRED_SIZE, 378, GroupLayout.PREFERRED_SIZE)
        	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        	        .addComponent(getJTabbedPane1(), GroupLayout.PREFERRED_SIZE, 232, GroupLayout.PREFERRED_SIZE)))
        	.addContainerGap(36, Short.MAX_VALUE));
        thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
        	.addContainerGap()
        	.addGroup(thisLayout.createParallelGroup()
        	    .addComponent(getGeoPolygonPanel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 487, GroupLayout.PREFERRED_SIZE)
        	    .addComponent(getJTabbedPane1(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 485, GroupLayout.PREFERRED_SIZE))
        	.addGap(21)
        	.addComponent(getQuantityModPanel(), GroupLayout.PREFERRED_SIZE, 438, GroupLayout.PREFERRED_SIZE)
        	.addContainerGap(25, Short.MAX_VALUE));
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.setResizable(false);
      {
        mainMenuBar = new JMenuBar();
        setJMenuBar(mainMenuBar);
        {
          fileMenu = new JMenu();
          mainMenuBar.add(fileMenu);
          mainMenuBar.add(getEditMenu());
          mainMenuBar.add(getRunMenu());
          fileMenu.setText("File");
          {
            importMenuItem = new JMenuItem();
            fileMenu.add(importMenuItem);
            fileMenu.add(getSaveMenu());
            fileMenu.add(getSaveGeoquantsMenuItem());
            importMenuItem.setText("Import Triangulation");
            importMenuItem.setAction(getImportAction());
          }
        }
      }
      pack();
      this.setSize(983, 713);
    } catch (Exception e) {
        //add your error handling code here
      e.printStackTrace();
    }
  }
  
  private AbstractAction getImportAction() {
    if(importAction == null) {
      importAction = new AbstractAction("Import Triangulation", null) {
        public void actionPerformed(ActionEvent e) {
          //Handle open button action.
          getTriangulationFileChooser();
          int returnVal = getTriangulationFileChooser().showOpenDialog(GeoquantViewer.this);
          
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getTriangulationFileChooser().getSelectedFile();
            TriangulationIO.readTriangulation(file.getAbsolutePath());
            newTriangulation();
            getSaveMenu().setEnabled(true);
          }
        }
      };
      importAction.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl pressed I"));
    }
    return importAction;
  }
  
  private AbstractAction getSaveAction() {
    if(saveAction == null) {
      saveAction = new AbstractAction("Save Triangulation", null) {
        public void actionPerformed(ActionEvent e) {
          // Handle Save Button Action
          int returnVal = getTriangulationFileChooser().showSaveDialog(GeoquantViewer.this);
          
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getTriangulationFileChooser().getSelectedFile();
            TriangulationIO.writeTriangulation(file.getAbsolutePath());
          }
        }
      };
      saveAction.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl pressed S"));
    }
    return saveAction;
  }
  
  private HashMap<JCheckBox, Color> getGeoColorTable() {
    if(geoColorTable == null) {
      geoColorTable = new HashMap<JCheckBox, Color>();
    }
    return geoColorTable;
  }
  
  private JFileChooser getTriangulationFileChooser() {
    if(triangulationFileChooser == null) {
      triangulationFileChooser = new JFileChooser("Data/Triangulations");
      triangulationFileChooser.setFileFilter(new TriangulationFilter());
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
          getLengthSetField().setText("" + Length.valueAt(s));
        } else if(name.equals("rad1Slider")) {
          rad1SetField.setText("" + value);
          Radius.At(s.getLocalVertices().get(0)).setValue(value);
          getLengthSetField().setText("" + Length.valueAt(s));
        } else if(name.equals("rad2Slider")) {
          rad2SetField.setText("" + value);
          Radius.At(s.getLocalVertices().get(1)).setValue(value);
          getLengthSetField().setText("" + Length.valueAt(s));
        } else if(name.equals("lengthSlider")){
          getLengthSetField().setText("" + value);
          Length.At(s).setValue(value);
        }
        getEdgeDisplayPanel().repaint();
        getGeoPolygonPanel().repaint();
      } 
    }
    
  }

  private void newTriangulation() {
    // Modify Edge List
    EdgeListModel = 
      new DefaultComboBoxModel(Triangulation.edgeTable.values().toArray());
    EdgeList.setModel(EdgeListModel);
    getNehrFlowMenuItem().setEnabled(true);
    getGeoPolygonPanel().repaint();
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
        etaSetField.setEnabled(false);
        etaSetField.addActionListener(new SetValueListener());
        
        rad1SetField = new JTextField();
        rad1SetField.setHorizontalAlignment(SwingConstants.TRAILING);
        rad1SetField.setName("rad1SetField");
        rad1SetField.setEnabled(false);
        rad1SetField.addActionListener(new SetValueListener());
        
        rad2SetField = new JTextField();
        rad2SetField.setHorizontalAlignment(SwingConstants.TRAILING);
        rad2SetField.setName("rad2SetField");
        rad2SetField.setEnabled(false);
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
        etaSlider.setEnabled(false);
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
        rad1Slider.setEnabled(false);
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
        rad2Slider.setEnabled(false);
        rad2Slider.addChangeListener(new SliderListener());
      }
      quantityModPanelLayout.setHorizontalGroup(quantityModPanelLayout.createSequentialGroup()
      	.addContainerGap()
      	.addGroup(quantityModPanelLayout.createParallelGroup()
      	    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
      	        .addGroup(quantityModPanelLayout.createParallelGroup()
      	            .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                .addGroup(quantityModPanelLayout.createParallelGroup()
      	                    .addComponent(etaSetLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
      	                    .addComponent(rad1SetLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
      	                    .addComponent(rad2SetLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
      	                    .addComponent(getLengthSetLabel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE))
      	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      	                .addGroup(quantityModPanelLayout.createParallelGroup()
      	                    .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                        .addComponent(etaSetField, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE))
      	                    .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                        .addComponent(rad1SetField, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE))
      	                    .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                        .addComponent(rad2SetField, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE))
      	                    .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                        .addComponent(getLengthSetField(), GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)))
      	                .addGap(0, 0, Short.MAX_VALUE))
      	            .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                .addComponent(etaSlider, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)
      	                .addGap(0, 0, Short.MAX_VALUE))
      	            .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                .addComponent(rad1Slider, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)
      	                .addGap(0, 0, Short.MAX_VALUE))
      	            .addGroup(quantityModPanelLayout.createSequentialGroup()
      	                .addComponent(rad2Slider, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)
      	                .addGap(0, 0, Short.MAX_VALUE))
      	            .addComponent(getLengthSlider(), GroupLayout.Alignment.LEADING, 0, 241, Short.MAX_VALUE)
      	            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
      	                .addGap(25)
      	                .addComponent(getCirclePackRadioButton(), GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
      	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
      	                .addComponent(getPerpBisectorRadioButton(), GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
      	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)))
      	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      	        .addComponent(getEdgeListScrollPane(), GroupLayout.PREFERRED_SIZE, 169, GroupLayout.PREFERRED_SIZE))
      	    .addComponent(getEdgeDisplayPanel(), GroupLayout.Alignment.LEADING, 0, 422, Short.MAX_VALUE))
      	.addContainerGap());
      quantityModPanelLayout.setVerticalGroup(quantityModPanelLayout.createSequentialGroup()
      	.addContainerGap()
      	.addGroup(quantityModPanelLayout.createParallelGroup()
      	    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
      	        .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      	            .addComponent(etaSetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
      	            .addComponent(etaSetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
      	        .addComponent(etaSlider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
      	        .addGap(18)
      	        .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      	            .addComponent(rad1SetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
      	            .addComponent(rad1SetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
      	        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      	        .addComponent(rad1Slider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
      	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      	        .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      	            .addComponent(rad2SetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
      	            .addComponent(rad2SetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
      	        .addComponent(rad2Slider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
      	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      	        .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      	            .addComponent(getLengthSetField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
      	            .addComponent(getLengthSetLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
      	        .addComponent(getLengthSlider(), GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
      	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 1, Short.MAX_VALUE)
      	        .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
      	            .addComponent(getCirclePackRadioButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
      	            .addComponent(getPerpBisectorRadioButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
      	    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
      	        .addComponent(getEdgeListScrollPane(), GroupLayout.PREFERRED_SIZE, 338, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 15, Short.MAX_VALUE)))
      	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
      	.addComponent(getEdgeDisplayPanel(), GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
      	.addContainerGap(19, 19));
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
        EdgeList.setPreferredSize(new java.awt.Dimension(149, 332));
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
      perpBisectorRadioButton.setSelected(true);
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
  
  private JPanel getEdgeDisplayPanel() {
    if(edgeDisplayPanel == null) {
      edgeDisplayPanel = new JPanel() {
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
        
          int index = EdgeList.getSelectedIndex();
          if(index == -1) {
            return;
          }
          
          // Draw Line
          g.setColor(Color.BLACK);
          Dimension size = this.getSize();
          g.drawLine(size.width / 3, size.height / 2, 2 * size.width / 3, size.height / 2);

          // Draw Circles
          g.setColor(Color.BLUE);
          Edge s = (Edge) EdgeListModel.getElementAt(index);
          Vertex v1 = s.getLocalVertices().get(0);
          Vertex v2 = s.getLocalVertices().get(1);
          double length = Length.valueAt(s);
          double rad1 = Radius.valueAt(v1);
          double rad2 = Radius.valueAt(v2);
          int lineLength = size.width / 3;
          int arcLength = (int) (rad1*lineLength / length);
          g.drawOval(size.width / 3 - arcLength, size.height/2 - arcLength, arcLength * 2, 
              arcLength * 2);
          arcLength = (int) (rad2*lineLength / length);
          g.drawOval(2 *size.width / 3 - arcLength, size.height/2 - arcLength, arcLength * 2, 
              arcLength * 2);
        }
      };
      edgeDisplayPanel.setBackground(new java.awt.Color(255,255,255));
      edgeDisplayPanel.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
      edgeDisplayPanel.addMouseMotionListener(new EdgePanelMouseListener());
    }
    return edgeDisplayPanel;
  }
    
  private JPanel getBasicSelectPanel() {
    if(basicSelectPanel == null) {
      basicSelectPanel = new JPanel();
      basicSelectPanel.setLayout(null);
      basicSelectPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
      basicSelectPanel.setPreferredSize(new java.awt.Dimension(487, 206));
      basicSelectPanel.add(getLEinsteinCheck());
      basicSelectPanel.add(getVcscCheck());
      basicSelectPanel.add(getVEinsteinCheck());
      basicSelectPanel.add(getLcscCheck());
      basicSelectPanel.add(getTotalCurvatureCheck());
      basicSelectPanel.add(getLehrCheck());
      basicSelectPanel.add(getVehrCheck());
      basicSelectPanel.add(getDihAngleCheck());
      basicSelectPanel.add(getFaceHeightCheck());
      basicSelectPanel.add(getDualAreaCheck());
      basicSelectPanel.add(getAreaCheck());
      basicSelectPanel.add(getVolumeCheck());
      basicSelectPanel.add(getTotalVolumeCheck());
      basicSelectPanel.add(getAngleCheck());
      basicSelectPanel.add(getConeAngleCheck());
      basicSelectPanel.add(getEdgeHeightCheck());
      basicSelectPanel.add(getCurv3DCheck());
      basicSelectPanel.add(getRadiusCheck());
      basicSelectPanel.add(getEtaCheck());
      basicSelectPanel.add(getLengthCheck());
      basicSelectPanel.add(getPartialEdgeCheck());
      basicSelectPanel.add(getEdgeCurvatureCheck());
      basicSelectPanel.add(getCurv2DCheck());
    }
    return basicSelectPanel;
  }
  
  
  
  protected JPanel getGeoPolygonPanel() {
    if(geoPolygonPanel == null) {
      geoPolygonPanel = new JPanel() {
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          LinkedList<Geoquant> geoList = new LinkedList<Geoquant>();
          int halfHeight = this.getHeight() / 2;
          int halfWidth = this.getWidth() / 2;
          int radius;

          if(geoMap != null) {
            geoMap.clear();
          }

          g.setColor(new Color(0, 150, 0));
          // Draw 0-oval
          radius = (halfHeight / 2);
          g.drawOval(halfWidth - radius, halfHeight - radius, 
                     2 * radius, 2 * radius);
          g.drawString("0", (int) ( radius * Math.cos(Math.PI/4) + halfWidth),
                            (int) (halfHeight - radius * Math.sin(Math.PI/4)));
          // Draw Infinity-oval
          radius = (halfHeight);
          g.drawOval(halfWidth - radius, halfHeight - radius, 
                     2 * radius, 2 * radius);
          
          g.drawString("Infty", (int) ( radius * Math.cos(Math.PI/4) + halfWidth),
              (int) (halfHeight - radius * Math.sin(Math.PI/4)));
          

          // Draw Polygons
          if(getAngleCheck().isSelected()) {
            geoList.addAll(Geometry.getAngles());
            drawPolygon(geoList, g, geoColorTable.get(getAngleCheck()));
            geoList.clear();
          }
          if(getAreaCheck().isSelected()) {
            geoList.addAll(Geometry.getAreas());
            drawPolygon(geoList, g, geoColorTable.get(getAreaCheck()));
            geoList.clear();
          }
          if(getConeAngleCheck().isSelected()) {
            geoList.addAll(Geometry.getConeAngles());
            drawPolygon(geoList, g, geoColorTable.get(getConeAngleCheck()));
            geoList.clear();
          }
          if(getCurv2DCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvature2D());
            drawPolygon(geoList, g, geoColorTable.get(getCurv2DCheck()));
            geoList.clear();
          }
          if(getCurv3DCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvature3D());
            drawPolygon(geoList, g, geoColorTable.get(getCurv3DCheck()));
            geoList.clear();
          }
          if(getDihAngleCheck().isSelected()) {
            geoList.addAll(Geometry.getDihedralAngles());
            drawPolygon(geoList, g, geoColorTable.get(getDihAngleCheck()));
            geoList.clear();
          }
          if(getDualAreaCheck().isSelected()) {
            geoList.addAll(Geometry.getDualAreas());
            drawPolygon(geoList, g, geoColorTable.get(getDualAreaCheck()));
            geoList.clear();
          }
          if(getEdgeHeightCheck().isSelected()) {
            geoList.addAll(Geometry.getEdgeHeights());
            drawPolygon(geoList, g, geoColorTable.get(getEdgeHeightCheck()));
            geoList.clear();
          }
          if(getEtaCheck().isSelected()) {
            geoList.addAll(Geometry.getEtas());
            drawPolygon(geoList, g, geoColorTable.get(getEtaCheck()));
            geoList.clear();
          }
          if(getFaceHeightCheck().isSelected()) {
            geoList.addAll(Geometry.getFaceHeights());
            drawPolygon(geoList, g, geoColorTable.get(getFaceHeightCheck()));
            geoList.clear();
          }
          if(getLengthCheck().isSelected()) {
            geoList.addAll(Geometry.getLengths());
            drawPolygon(geoList, g, geoColorTable.get(getLengthCheck()));
            geoList.clear();
          }
          if(getVehrCheck().isSelected()) {
            geoList.add(VEHR.getInstance());
            drawPolygon(geoList, g, geoColorTable.get(getVehrCheck()));
            geoList.clear();
          }
          if(getPartialEdgeCheck().isSelected()) {
            geoList.addAll(Geometry.getPartialEdges());
            drawPolygon(geoList, g, geoColorTable.get(getPartialEdgeCheck()));
            geoList.clear();
          }
          if(getRadiusCheck().isSelected()) {
            geoList.addAll(Geometry.getRadii());
            drawPolygon(geoList, g, geoColorTable.get(getRadiusCheck()));
            geoList.clear();
          }
          if(getVolumeCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumes());
            drawPolygon(geoList, g, geoColorTable.get(getVolumeCheck()));
            geoList.clear();
          }
          
          // Partials
          if(getCurvPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvaturePartials());
            drawPolygon(geoList, g, geoColorTable.get(getCurvPartialCheck()));
            geoList.clear();
          }
          if(getDihAnglePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getDihedralAnglePartials());
            drawPolygon(geoList, g, geoColorTable.get(getDihAnglePartialCheck()));
            geoList.clear();
          }
          if(getNehrPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVEHRPartials());
            drawPolygon(geoList, g, geoColorTable.get(getNehrPartialCheck()));
            geoList.clear();
          }
          if(getPartialEdgePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getPartialEdgePartials());
            drawPolygon(geoList, g, geoColorTable.get(getPartialEdgePartialCheck()));
            geoList.clear();
          }
          if(getRadiusPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getRadiusPartials());
            drawPolygon(geoList, g, geoColorTable.get(getRadiusPartialCheck()));
            geoList.clear();
          }
          if(getVolumePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumePartials());
            drawPolygon(geoList, g, geoColorTable.get(getVolumePartialCheck()));
            geoList.clear();
          }
          
          // Second Partials
          if(getCurvatureSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvatureSecondPartials());
            drawPolygon(geoList, g, geoColorTable.get(getCurvatureSecondPartialCheck()));
            geoList.clear();
          }
          if(getDihAngleSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getDihedralAngleSecondPartials());
            drawPolygon(geoList, g, geoColorTable.get(getDihAngleSecondPartialCheck()));
            geoList.clear();
          }
          if(getNehrSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVEHRSecondPartials());
            drawPolygon(geoList, g, geoColorTable.get(getNehrSecondPartialCheck()));
            geoList.clear();
          }
          if(getPartialEdgeSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getPartialEdgeSecondPartials());
            drawPolygon(geoList, g, geoColorTable.get(getPartialEdgeSecondPartialCheck()));
            geoList.clear();
          }
          if(getVolumeSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumeSecondPartials());
            drawPolygon(geoList, g, geoColorTable.get(getVolumeSecondPartialCheck()));
            geoList.clear();
          }
          
          // Sums
          if(getTotalCurvatureCheck().isSelected()) {
            geoList.add(Curvature3D.sum());
            drawPolygon(geoList, g, geoColorTable.get(getTotalCurvatureCheck()));
            geoList.clear();
          }
          if(getTotalVolumeCheck().isSelected()) {
            geoList.add(Volume.sum());
            drawPolygon(geoList, g, geoColorTable.get(getTotalVolumeCheck()));
            geoList.clear();
          }
          if(getTotalVolumePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumePartialSums());
            drawPolygon(geoList, g, geoColorTable.get(getTotalVolumePartialCheck()));
            geoList.clear();
          }
          if(getTotalVolumeSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumeSecondPartialSums());
            drawPolygon(geoList, g, geoColorTable.get(getTotalVolumeSecondPartialCheck()));
            geoList.clear();
          }
          
          if(getLehrCheck().isSelected()) {
            geoList.addAll(Geometry.getLEHR());
            drawPolygon(geoList, g, geoColorTable.get(getLehrCheck()));
            geoList.clear();
          }
          if(getLcscCheck().isSelected()) {
            geoList.addAll(Geometry.getLCSC());
            drawPolygon(geoList, g, geoColorTable.get(getLcscCheck()));
            geoList.clear();
          }
          if(getVcscCheck().isSelected()) {
            geoList.addAll(Geometry.getVCSC());
            drawPolygon(geoList, g, geoColorTable.get(getVcscCheck()));
            geoList.clear();
          }
          if(getLEinsteinCheck().isSelected()) {
            geoList.addAll(Geometry.getLEinsteins());
            drawPolygon(geoList, g, geoColorTable.get(getLEinsteinCheck()));
            geoList.clear();
          }
          if(getVEinsteinCheck().isSelected()) {
            geoList.addAll(Geometry.getVEinsteins());
            drawPolygon(geoList, g, geoColorTable.get(getVEinsteinCheck()));
            geoList.clear();
          }
          if(getEdgeCurvatureCheck().isSelected()) {
            geoList.addAll(Geometry.getEdgeCurvatures());
            drawPolygon(geoList, g, geoColorTable.get(getEdgeCurvatureCheck()));
            geoList.clear();
          }
        }
        
        private void drawPolygon(LinkedList<Geoquant> geoList, Graphics g, Color c) {
          int size = geoList.size();
          if(size == 0) {
            return;
          }
          int halfHeight = this.getHeight() / 2;
          int halfWidth = this.getWidth() / 2;
          double radius;
          int[] xpoints = new int[size];
          int[] ypoints = new int[size];
          double angleStep = 2 * Math.PI / size;

          // Draw Polygon
          g.setColor(Color.BLACK);
          int i = 0;
          double angle = 0;
          for(Geoquant q : geoList) {
            radius = (halfHeight / Math.PI) * (Math.atan(q.getValue()) + Math.PI / 2);
            xpoints[i] = (int) (radius * Math.cos(angle) + halfWidth);
            ypoints[i] = (int) (-1 * radius * Math.sin(angle) + halfHeight);
            i++;
            angle += angleStep;
          }
          g.drawPolygon(new GeoPolygon(xpoints, ypoints, size, geoList.toArray()));
          
          g.setColor(c);
          int circDiam = 5;
          for(int j = 0; j < xpoints.length; j++) {
            g.fillOval(xpoints[j] - circDiam / 2, ypoints[j] - circDiam / 2, 
                  circDiam, circDiam);
          }  
        }
      };
      geoPolygonPanel.setBackground(new java.awt.Color(255,255,255));
      geoPolygonPanel.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
      geoPolygonPanel.addMouseMotionListener(new GeoPolygonMouseListener());
    }
    return geoPolygonPanel;
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
          rad2Slider.setEnabled(false);
          getLengthSetField().setEnabled(false);
          getLengthSlider().setEnabled(false);
          
        } else {
          // selection, enable text field, slider, change label name
          etaSetField.setEnabled(true);
          etaSlider.setEnabled(true);
          rad1SetField.setEnabled(true);
          rad1Slider.setEnabled(true);
          rad2SetField.setEnabled(true);
          rad2Slider.setEnabled(true);
          getLengthSetField().setEnabled(true);
          getLengthSlider().setEnabled(true);
          Edge s = (Edge) EdgeListModel.getElementAt(EdgeList.getSelectedIndex());
          Vertex v1 = s.getLocalVertices().get(0);
          Vertex v2 = s.getLocalVertices().get(1);
          rad1SetLabel.setText("Radius " + v1.getIndex() + ":");
          rad1SetField.setText("" + Radius.valueAt(v1));
          rad2SetLabel.setText("Radius " + v2.getIndex() + ":");
          rad2SetField.setText("" + Radius.valueAt(v2));
          etaSetField.setText("" + Eta.valueAt(s));
          getLengthSetField().setText("" + Length.valueAt(s));
          
          getEdgeDisplayPanel().repaint();
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
          getLengthSetField().setText("" + Length.valueAt(s));
        } else if(name.equals("rad1SetField")) {
          Radius.At(s.getLocalVertices().get(0)).setValue(value);
          getLengthSetField().setText("" + Length.valueAt(s));
        } else if(name.equals("rad2SetField")) {
          Radius.At(s.getLocalVertices().get(1)).setValue(value);
          getLengthSetField().setText("" + Length.valueAt(s));
        } else if(name.equals("lengthSetField")) {
          Length.At(s).setValue(value);
        }
        getEdgeDisplayPanel().repaint();
        getGeoPolygonPanel().repaint();
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
        getEdgeDisplayPanel().repaint();
        getGeoPolygonPanel().repaint();
      } catch(NumberFormatException exc) {
        return;
      }
      
    }
    
  }
  
  class EdgePanelMouseListener extends MouseAdapter {
    private JLabel message;
    private Popup edgeDisplayPopup;
    private int x;
    private int y;
    private JPanel edgeDisplay;
    
    public EdgePanelMouseListener() {
      super();

      message = new JLabel();
      message.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
    }
    
    public void mouseMoved(MouseEvent e) {
      x = e.getX();
      y = e.getY();
      edgeDisplay = getEdgeDisplayPanel();
      int width = edgeDisplay.getWidth();
      int height = edgeDisplay.getHeight();
      int epsilon = 20;
      
      if(x > (width / 3 - epsilon) && x < (2 * width / 3 + epsilon)
          && y > (height / 2 - epsilon) && y < (height/2 + epsilon)) {
        
        int index = EdgeList.getSelectedIndex();
        if(index == -1) {
          return;
        }
        if(edgeDisplayPopup != null){
          edgeDisplayPopup.hide();
        }
        Edge s = (Edge) EdgeListModel.getElementAt(index); 
        message.setText("" + Length.At(s));
        PopupFactory factory = PopupFactory.getSharedInstance();
        edgeDisplayPopup = factory.getPopup(GeoquantViewer.this, message, 
              (int) edgeDisplay.getLocationOnScreen().getX() + x, 
              (int) edgeDisplay.getLocationOnScreen().getY() + y - 15);
        edgeDisplayPopup.show();
      } else if(edgeDisplayPopup != null){
        edgeDisplayPopup.hide();
      }
    }

  }
  
  private class GeoPoint extends Point {
    public GeoPoint(int x, int y) {
      super(x, y);
    }
    @Override
    public boolean equals(Object other) {
      if(other instanceof GeoPoint) {
        GeoPoint p = (GeoPoint) other;
        double distance = Math.pow(this.getX() - p.getX(), 2) 
                          + Math.pow(this.getY() - p.getY(), 2);
        return distance < 6.25;
      }
      return false;
    }
    
    @Override
    public int hashCode() {
      return 0;
    }
  }
  
  private class GeoPolygon extends Polygon {
    public GeoPolygon(int[] xpoints, int[] ypoints, int npoints, Object[] geos) {
      super(xpoints, ypoints, npoints);
      if(geoMap == null) {
        geoMap = new HashMap<GeoPoint, Geoquant>();
      }
      GeoPoint p;
      for(int i = 0; i < geos.length; i++) {
        p = new GeoPoint(xpoints[i], ypoints[i]);
        geoMap.put(p, (Geoquant) geos[i]);
      }
    }
  }

  public void itemStateChanged(ItemEvent e) {
    getGeoPolygonPanel().repaint();
  }
  
  
  
  private JTabbedPane getJTabbedPane1() {
    if(geoSelectTabbedPane == null) {
      geoSelectTabbedPane = new JTabbedPane();
      geoSelectTabbedPane.addTab("Basic Quantities", null, getBasicSelectPanel(), null);
      geoSelectTabbedPane.addTab("Partials", null, getPartialSelectPanel(), null);
      geoSelectTabbedPane.addTab("Second Partials", null, getSecondPartialSelectPanel(), null);
    }
    return geoSelectTabbedPane;
  }
  
  private JPanel getPartialSelectPanel() {
    if(partialSelectPanel == null) {
      partialSelectPanel = new JPanel();
      partialSelectPanel.setLayout(null);
      partialSelectPanel.setPreferredSize(new java.awt.Dimension(485, 158));
      partialSelectPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
      partialSelectPanel.add(getTotalVolumePartialCheck());
      partialSelectPanel.add(getPartialEdgePartialCheck());
      partialSelectPanel.add(getNehrPartialCheck());
      partialSelectPanel.add(getCurvPartialCheck());
      partialSelectPanel.add(getDihAnglePartialCheck());
      partialSelectPanel.add(getRadiusPartialCheck());
      partialSelectPanel.add(getVolumePartialCheck());
    }
    return partialSelectPanel;
  }
  
  private JPanel getSecondPartialSelectPanel() {
    if(secondPartialSelectPanel == null) {
      secondPartialSelectPanel = new JPanel();
      GroupLayout seconPartialSelectPanelLayout = new GroupLayout((JComponent)secondPartialSelectPanel);
      secondPartialSelectPanel.setLayout(seconPartialSelectPanelLayout);
      secondPartialSelectPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
      seconPartialSelectPanelLayout.setHorizontalGroup(seconPartialSelectPanelLayout.createSequentialGroup()
      	.addContainerGap()
      	.addGroup(seconPartialSelectPanelLayout.createParallelGroup()
      	    .addGroup(seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addComponent(getTotalVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 191, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 0, Short.MAX_VALUE))
      	    .addGroup(GroupLayout.Alignment.LEADING, seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addComponent(getDihAngleSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 82, Short.MAX_VALUE))
      	    .addGroup(seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addComponent(getCurvatureSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 0, Short.MAX_VALUE))
      	    .addGroup(GroupLayout.Alignment.LEADING, seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addComponent(getNehrSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 82, Short.MAX_VALUE))
      	    .addGroup(GroupLayout.Alignment.LEADING, seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addComponent(getPartialEdgeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 34, Short.MAX_VALUE))
      	    .addGroup(GroupLayout.Alignment.LEADING, seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addComponent(getVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 34, Short.MAX_VALUE)))
      	.addContainerGap(282, 282));
      seconPartialSelectPanelLayout.setVerticalGroup(seconPartialSelectPanelLayout.createSequentialGroup()
      	.addContainerGap()
      	.addGroup(seconPartialSelectPanelLayout.createParallelGroup()
      	    .addGroup(GroupLayout.Alignment.LEADING, seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addComponent(getCurvatureSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
      	        .addGap(16)
      	        .addComponent(getNehrSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
      	        .addGap(19)
      	        .addComponent(getVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 24, Short.MAX_VALUE))
      	    .addGroup(GroupLayout.Alignment.LEADING, seconPartialSelectPanelLayout.createSequentialGroup()
      	        .addGap(20)
      	        .addComponent(getDihAngleSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
      	        .addGap(17)
      	        .addComponent(getPartialEdgeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
      	        .addGap(22)
      	        .addComponent(getTotalVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
      	        .addGap(0, 0, Short.MAX_VALUE)))
      	.addContainerGap(58, 58));
      seconPartialSelectPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getCurvatureSecondPartialCheck(), getDihAngleSecondPartialCheck(), getNehrSecondPartialCheck(), getPartialEdgeSecondPartialCheck(), getVolumeSecondPartialCheck(), getTotalVolumeSecondPartialCheck()});
    }
    return secondPartialSelectPanel;
  }

  class GeoPolygonMouseListener extends MouseAdapter {
    private JLabel message;
    private Popup geoDisplayPopup;
    private int x;
    private int y;
    private JPanel geoPanel;
    
    public GeoPolygonMouseListener() {
      super();

      message = new JLabel();
      message.setBorder(new LineBorder(new java.awt.Color(0,0,0), 1, false));
    }
    public void mouseMoved(MouseEvent e) {
      if(geoDisplayPopup != null) {
        geoDisplayPopup.hide();
      }
      if(geoMap == null) {
        return;
      }
      x = e.getX();
      y = e.getY();
      Geoquant q = geoMap.get(new GeoPoint(x, y));
      if(q == null) {
        return;
      }
      geoPanel = getGeoPolygonPanel();
      message.setText("" + q);
      PopupFactory factory = PopupFactory.getSharedInstance();
      geoDisplayPopup = factory.getPopup(GeoquantViewer.this, message, 
            (int) geoPanel.getLocationOnScreen().getX() + x, 
            (int) geoPanel.getLocationOnScreen().getY() + y - 15);
      geoDisplayPopup.show();
    }
  }

  private JCheckBox getAngleCheck() {
    if(angleCheck == null) {
      angleCheck = new JCheckBox();
      angleCheck.setText("Face Angle");
      angleCheck.setBounds(161, 48, 148, 16);
      getGeoColorTable().put(angleCheck, new Color(240, 15, 0));
      angleCheck.addItemListener(this);
    }
    return angleCheck;
  }
  
  private JCheckBox getAreaCheck() {
    if(areaCheck == null) {
      areaCheck = new JCheckBox();
      areaCheck.setText("Face Area");
      areaCheck.setBounds(161, 88, 148, 16);
      getGeoColorTable().put(areaCheck, new Color(225, 30, 0));
      areaCheck.addItemListener(this);
    }
    return areaCheck;
  }

  private JCheckBox getCurv2DCheck() {
    if(curv2DCheck == null) {
      curv2DCheck = new JCheckBox();
      curv2DCheck.setText("Vertex Curvature (2D)");
      curv2DCheck.setBounds(8, 27, 148, 16);
      getGeoColorTable().put(curv2DCheck, new Color(195, 60, 0));
      curv2DCheck.addItemListener(this);
    }
    return curv2DCheck;
  }
  
  private JCheckBox getCurv3DCheck() {
    if(curv3DCheck == null) {
      curv3DCheck = new JCheckBox();
      curv3DCheck.setText("Vertex Curvature (3D)");
      curv3DCheck.setBounds(8, 47, 148, 16);
      getGeoColorTable().put(curv3DCheck, new Color(180, 75, 0));
      curv3DCheck.addItemListener(this);
    }
    return curv3DCheck;
  }
  
  private JCheckBox getDihAngleCheck() {
    if(dihAngleCheck == null) {
      dihAngleCheck = new JCheckBox();
      dihAngleCheck.setText("Dihedral Angle");
      dihAngleCheck.setBounds(161, 8, 148, 16);
      getGeoColorTable().put(dihAngleCheck, new Color(165, 90, 0));
      dihAngleCheck.addItemListener(this);
    }
    return dihAngleCheck;
  }
  
  private JCheckBox getDualAreaCheck() {
    if(dualAreaCheck == null) {
      dualAreaCheck = new JCheckBox();
      dualAreaCheck.setText("Dual Area");
      dualAreaCheck.setBounds(161, 28, 148, 16);
      getGeoColorTable().put(dualAreaCheck, new Color(150, 105, 0));
      dualAreaCheck.addItemListener(this);
    }
    return dualAreaCheck;
  }

  private JCheckBox getEdgeHeightCheck() {
    if(edgeHeightCheck == null) {
      edgeHeightCheck = new JCheckBox();
      edgeHeightCheck.setText("Edge Height");
      edgeHeightCheck.setBounds(8, 146, 148, 16);
      getGeoColorTable().put(edgeHeightCheck, new Color(135, 120, 0));
      edgeHeightCheck.addItemListener(this);
    }
    return edgeHeightCheck;
  }
  
  private JCheckBox getEtaCheck() {
    if(etaCheck == null) {
      etaCheck = new JCheckBox();
      etaCheck.setText("Eta");
      etaCheck.setBounds(8, 67, 148, 16);
      getGeoColorTable().put(etaCheck, new Color(120, 135, 0));
      etaCheck.addItemListener(this);
    }
    return etaCheck;
  }
  
  private JCheckBox getFaceHeightCheck() {
    if(faceHeightCheck == null) {
      faceHeightCheck = new JCheckBox();
      faceHeightCheck.setText("Face Height");
      faceHeightCheck.setBounds(161, 68, 146, 16);
      getGeoColorTable().put(faceHeightCheck, new Color(105, 150, 0));
      faceHeightCheck.addItemListener(this);
    }
    return faceHeightCheck;
  }
  
  private JCheckBox getLengthCheck() {
    if(lengthCheck == null) {
      lengthCheck = new JCheckBox();
      lengthCheck.setText("Edge Length");
      lengthCheck.setBounds(8, 86, 148, 16);
      getGeoColorTable().put(lengthCheck, new Color(90, 165, 0));
      lengthCheck.addItemListener(this);
    }
    return lengthCheck;
  }

  private JCheckBox getVehrCheck() {
    if(vehrCheck == null) {
      vehrCheck = new JCheckBox();
      vehrCheck.setText("VEHR");
      vehrCheck.setBounds(324, 129, 148, 16);
      getGeoColorTable().put(vehrCheck, new Color(75, 180, 0));
      vehrCheck.addItemListener(this);
    }
    return vehrCheck;
  }
  
  private JCheckBox getPartialEdgeCheck() {
    if(partialEdgeCheck == null) {
      partialEdgeCheck = new JCheckBox();
      partialEdgeCheck.setText("Pre-Metric Length");
      partialEdgeCheck.setBounds(8, 126, 148, 16);
      getGeoColorTable().put(partialEdgeCheck, new Color(60, 195, 0));
      partialEdgeCheck.addItemListener(this);
    }
    return partialEdgeCheck;
  }
  
  private JCheckBox getRadiusCheck() {
    if(radiusCheck == null) {
      radiusCheck = new JCheckBox();
      radiusCheck.setText("Radius");
      radiusCheck.setBounds(8, 8, 148, 16);
      getGeoColorTable().put(radiusCheck, new Color(45, 210, 0));
      radiusCheck.addItemListener(this);
    }
    return radiusCheck;
  }

  private JCheckBox getVolumeCheck() {
    if(volumeCheck == null) {
      volumeCheck = new JCheckBox();
      volumeCheck.setText("Tetra Volume");
      volumeCheck.setBounds(161, 108, 148, 16);
      getGeoColorTable().put(volumeCheck, new Color(15, 240, 0));
      volumeCheck.addItemListener(this);
    }
    return volumeCheck;
  }
  
  private JCheckBox getCurvPartialCheck() {
    if(curvPartialCheck == null) {
      curvPartialCheck = new JCheckBox();
      curvPartialCheck.setText("Vertex Curvature (3D)");
      curvPartialCheck.setBounds(8, 8, 156, 23);
      getGeoColorTable().put(curvPartialCheck, new Color(0, 255, 0));
      curvPartialCheck.addItemListener(this);
    }
    return curvPartialCheck;
  }
  
  private JCheckBox getDihAnglePartialCheck() {
    if(dihAnglePartialCheck == null) {
      dihAnglePartialCheck = new JCheckBox();
      dihAnglePartialCheck.setText("Dihedral Angle");
      dihAnglePartialCheck.setBounds(8, 29, 121, 23);
      getGeoColorTable().put(dihAnglePartialCheck, new Color(0, 240, 15));
      dihAnglePartialCheck.addItemListener(this);
    }
    return dihAnglePartialCheck;
  }
  
  private JCheckBox getNehrPartialCheck() {
    if(nehrPartialCheck == null) {
      nehrPartialCheck = new JCheckBox();
      nehrPartialCheck.setText("VEHR");
      nehrPartialCheck.setBounds(8, 50, 74, 23);
      getGeoColorTable().put(nehrPartialCheck, new Color(0, 225, 30));
      nehrPartialCheck.addItemListener(this);
    }
    return nehrPartialCheck;
  }
  
  private JCheckBox getPartialEdgePartialCheck() {
    if(partialEdgePartialCheck == null) {
      partialEdgePartialCheck = new JCheckBox();
      partialEdgePartialCheck.setText("Pre-Metric Length");
      partialEdgePartialCheck.setBounds(8, 71, 122, 23);
      getGeoColorTable().put(partialEdgePartialCheck, new Color(0, 210, 45));
      partialEdgePartialCheck.addItemListener(this);
    }
    return partialEdgePartialCheck;
  }
  
  private JCheckBox getRadiusPartialCheck() {
    if(radiusPartialCheck == null) {
      radiusPartialCheck = new JCheckBox();
      radiusPartialCheck.setText("Radius");
      radiusPartialCheck.setBounds(8, 92, 77, 23);
      getGeoColorTable().put(radiusPartialCheck, new Color(0, 195, 60));
      radiusPartialCheck.addItemListener(this);
    }
    return radiusPartialCheck;
  }
  
  private JCheckBox getVolumePartialCheck() {
    if(volumePartialCheck == null) {
      volumePartialCheck = new JCheckBox();
      volumePartialCheck.setText("Tetra Volume");
      volumePartialCheck.setBounds(8, 113, 122, 23);
      getGeoColorTable().put(volumePartialCheck, new Color(0, 180, 75));
      volumePartialCheck.addItemListener(this);
    }
    return volumePartialCheck;
  }
  
  private JCheckBox getCurvatureSecondPartialCheck() {
    if(curvatureSecondPartialCheck == null) {
      curvatureSecondPartialCheck = new JCheckBox();
      curvatureSecondPartialCheck.setText("Vertex Curvature (3D)");
      getGeoColorTable().put(curvatureSecondPartialCheck, new Color(0, 165, 90));
      curvatureSecondPartialCheck.addItemListener(this);
    }
    return curvatureSecondPartialCheck;
  }
  
  private JCheckBox getDihAngleSecondPartialCheck() {
    if(dihAngleSecondPartialCheck == null) {
      dihAngleSecondPartialCheck = new JCheckBox();
      dihAngleSecondPartialCheck.setText("Dihedral Angle");
      getGeoColorTable().put(dihAngleSecondPartialCheck, new Color(0, 150, 105));
      dihAngleSecondPartialCheck.addItemListener(this);
    }
    return dihAngleSecondPartialCheck;
  }
  
  private JCheckBox getNehrSecondPartialCheck() {
    if(nehrSecondPartialCheck == null) {
      nehrSecondPartialCheck = new JCheckBox();
      nehrSecondPartialCheck.setText("VEHR");
      getGeoColorTable().put(nehrSecondPartialCheck, new Color(0, 135, 120));
      nehrSecondPartialCheck.addItemListener(this);
    }
    return nehrSecondPartialCheck;
  }
  
  private JCheckBox getPartialEdgeSecondPartialCheck() {
    if(partialEdgeSecondPartialCheck == null) {
      partialEdgeSecondPartialCheck = new JCheckBox();
      partialEdgeSecondPartialCheck.setText("Pre-Metric Length");
      getGeoColorTable().put(partialEdgeSecondPartialCheck, new Color(0, 120, 135));
      partialEdgeSecondPartialCheck.addItemListener(this);
    }
    return partialEdgeSecondPartialCheck;
  }
  
  private JCheckBox getVolumeSecondPartialCheck() {
    if(volumeSecondPartialCheck == null) {
      volumeSecondPartialCheck = new JCheckBox();
      volumeSecondPartialCheck.setText("Tetra Volume");
      getGeoColorTable().put(volumeSecondPartialCheck, new Color(0, 105, 150));
      volumeSecondPartialCheck.addItemListener(this);
    }
    return volumeSecondPartialCheck;
  }

  private JMenuItem getSaveMenu() {
    if(saveMenu == null) {
      saveMenu = new JMenuItem();
      saveMenu.setText("Save Triangulation");
      saveMenu.setAction(getSaveAction());
      saveMenu.setEnabled(false);
    }
    return saveMenu;
  }
  
  private JMenu getRunMenu() {
    if(runMenu == null) {
      runMenu = new JMenu();
      runMenu.setText("Run");
      runMenu.add(getNehrFlowMenuItem());
      runMenu.add(getYamabe2DMenuItem());
      runMenu.add(getYamabe3DMenuItem());
    }
    return runMenu;
  }
  
  private JMenuItem getNehrFlowMenuItem() {
    if(nehrFlowMenuItem == null) {
      nehrFlowMenuItem = new JMenuItem();
      nehrFlowMenuItem.setText("NEHR Flow");
      nehrFlowMenuItem.setAction(getShowVehrFlowDialog());
      nehrFlowMenuItem.setEnabled(false);
    }
    return nehrFlowMenuItem;
  }
  
  private VEHRFlowDialog getNehrFlowDialog() {
    if(nehrFlowDialog == null) {
      nehrFlowDialog = new VEHRFlowDialog(this);
    }
    return nehrFlowDialog;
  }
  
  private YamabeFlowDialog getYamabe2DDialog() {
    if(yamabe2DDialog == null) {
      yamabe2DDialog = new YamabeFlowDialog(this, Geometry.Dimension.twoD);
    }
    return yamabe2DDialog;
  }
  
  private YamabeFlowDialog getYamabe3DDialog() {
    if(yamabe3DDialog == null) {
      yamabe3DDialog = new YamabeFlowDialog(this, Geometry.Dimension.threeD);
    }
    return yamabe3DDialog;
  }
  
  private JMenu getEditMenu() {
	  if(editMenu == null) {
		  editMenu = new JMenu();
		  editMenu.setText("Edit");
		  editMenu.add(getSetRadiiMenuItem());
		  editMenu.add(getSetEtaMenuItem());
	  }
	  return editMenu;
  }
  
  private JMenuItem getSetRadiiMenuItem() {
	  if(setRadiiMenuItem == null) {
		  setRadiiMenuItem = new JMenuItem();
		  setRadiiMenuItem.setText("Set Radii...");
		  setRadiiMenuItem.setAction(getShowSetRadiiDialog());
	  }
	  return setRadiiMenuItem;
  }
  
  private JMenuItem getSetEtaMenuItem() {
	  if(setEtaMenuItem == null) {
		  setEtaMenuItem = new JMenuItem();
		  setEtaMenuItem.setText("Set Etas...");
		  setEtaMenuItem.setAction(getShowEtaDialog());
	  }
	  return setEtaMenuItem;
  }
  
  private JDialog getSetRadiiDialog() {
	  if(setRadiiDialog == null) {
		  setRadiiDialog = new JDialog(this);
		  GroupLayout setRadiiDialogLayout = new GroupLayout((JComponent)setRadiiDialog.getContentPane());
		  setRadiiDialog.getContentPane().setLayout(setRadiiDialogLayout);
		  setRadiiDialog.setTitle("Set Radii");
		  setRadiiDialog.setResizable(false);
		  setRadiiDialog.setPreferredSize(new java.awt.Dimension(279, 133));
		  setRadiiDialog.setSize(279, 133);
		  setRadiiDialogLayout.setHorizontalGroup(setRadiiDialogLayout.createSequentialGroup()
		  	.addContainerGap()
		  	.addComponent(getSetRadiiLabel(), GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
		  	.addGroup(setRadiiDialogLayout.createParallelGroup()
		  	    .addGroup(GroupLayout.Alignment.LEADING, setRadiiDialogLayout.createSequentialGroup()
		  	        .addComponent(getOkButton_SR(), GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
		  	        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		  	        .addComponent(getCancelButton_SR(), GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
		  	    .addGroup(GroupLayout.Alignment.LEADING, setRadiiDialogLayout.createSequentialGroup()
		  	        .addPreferredGap(getOkButton_SR(), getSetRadiiTextField(), LayoutStyle.ComponentPlacement.INDENT)
		  	        .addComponent(getSetRadiiTextField(), GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)))
		  	.addContainerGap(6, Short.MAX_VALUE));
		  setRadiiDialogLayout.setVerticalGroup(setRadiiDialogLayout.createSequentialGroup()
		  	.addContainerGap()
		  	.addGroup(setRadiiDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		  	    .addComponent(getSetRadiiTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		  	    .addComponent(getSetRadiiLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		  	.addGap(29)
		  	.addGroup(setRadiiDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		  	    .addComponent(getOkButton_SR(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
		  	    .addComponent(getCancelButton_SR(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		  	.addContainerGap());
	  }
	  return setRadiiDialog;
  }
  
  private JButton getCancelButton_SR() {
	  if(cancelButton_SR == null) {
		  cancelButton_SR = new JButton();
		  cancelButton_SR.setText("Cancel");
		  cancelButton_SR.setAction(new CloseSetRadiiDialog("Cancel"));
	  }
	  return cancelButton_SR;
  }
  
  private JButton getCancelButton_SE() {
    if(cancelButton_SE == null) {
      cancelButton_SE = new JButton();
      cancelButton_SE.setText("Cancel");
      cancelButton_SE.setAction(new CloseSetEtaDialog("Cancel"));
    }
    return cancelButton_SE;
  }
  
  private JButton getOkButton_SR() {
	  if(okButton_SR == null) {
		  okButton_SR = new JButton();
		  okButton_SR.setText("OK");
		  okButton_SR.setAction(new CloseSetRadiiDialog("OK"));
	  }
	  return okButton_SR;
  }
  
  private JButton getOkButton_SE() {
    if(okButton_SE == null) {
      okButton_SE = new JButton();
      okButton_SE.setText("OK");
      okButton_SE.setAction(new CloseSetEtaDialog("OK"));
    }
    return okButton_SE;
  }
  
  private JTextField getSetRadiiTextField() {
	  if(setRadiiTextField == null) {
		  setRadiiTextField = new JTextField();
	  }
	  return setRadiiTextField;
  }
  
  private JTextField getSetEtaTextField() {
    if(setEtaTextField == null) {
      setEtaTextField = new JTextField();
    }
    return setEtaTextField;
  }
  
  private JLabel getSetRadiiLabel() {
	  if(setRadiiLabel == null) {
		  setRadiiLabel = new JLabel();
		  setRadiiLabel.setText("Set all radii to:");
	  }
	  return setRadiiLabel;
  }
    
  private JLabel getSetEtaLabel() {
    if(setEtaLabel == null) {
      setEtaLabel = new JLabel();
      setEtaLabel.setText("Set all eta to:");
    }
    return setEtaLabel;
  }
  
  private AbstractAction getShowSetRadiiDialog() {
	  if(showSetRadiiDialog == null) {
		  showSetRadiiDialog = new AbstractAction("Set Radii...", null) {
			  public void actionPerformed(ActionEvent evt) {
			    getSetRadiiDialog().pack();
			    getSetRadiiDialog().setLocationRelativeTo(null);
			    getSetRadiiDialog().setVisible(true);
			  }
		  };
	  }
	  return showSetRadiiDialog;
  }
  
  private JDialog getSetEtaDialog() {
	  if(setEtaDialog == null) {
	    setEtaDialog = new JDialog(this);
      GroupLayout setEtaDialogLayout = new GroupLayout((JComponent)setEtaDialog.getContentPane());
      setEtaDialog.getContentPane().setLayout(setEtaDialogLayout);
      setEtaDialog.setTitle("Set Eta");
      setEtaDialog.setResizable(false);
      setEtaDialog.setPreferredSize(new java.awt.Dimension(279, 133));
      setEtaDialog.setSize(279, 133);
      setEtaDialogLayout.setHorizontalGroup(setEtaDialogLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(getSetEtaLabel(), GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
        .addGroup(setEtaDialogLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, setEtaDialogLayout.createSequentialGroup()
                .addComponent(getOkButton_SE(), GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(getCancelButton_SE(), GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
            .addGroup(GroupLayout.Alignment.LEADING, setEtaDialogLayout.createSequentialGroup()
                .addPreferredGap(getOkButton_SE(), getSetEtaTextField(), LayoutStyle.ComponentPlacement.INDENT)
                .addComponent(getSetEtaTextField(), GroupLayout.PREFERRED_SIZE, 152, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(6, Short.MAX_VALUE));
      setEtaDialogLayout.setVerticalGroup(setEtaDialogLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(setEtaDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getSetEtaTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(getSetEtaLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
        .addGap(29)
        .addGroup(setEtaDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getOkButton_SE(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            .addComponent(getCancelButton_SE(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
        .addContainerGap());
	  }
	  return setEtaDialog;
  }
  
  private AbstractAction getShowEtaDialog() {
	  if(showEtaDialog == null) {
		  showEtaDialog = new AbstractAction("Set Etas...", null) {
			  public void actionPerformed(ActionEvent evt) {
	         getSetEtaDialog().pack();
	         getSetEtaDialog().setLocationRelativeTo(null);
	         getSetEtaDialog().setVisible(true);
			  }
		  };
	  }
	  return showEtaDialog;
  }
  
  private AbstractAction getShowVehrFlowDialog() {
	  if(showVehrFlowDialog == null) {
		  showVehrFlowDialog = new AbstractAction("NEHR Flow", null) {
			  public void actionPerformed(ActionEvent evt) {
			    getNehrFlowDialog().pack();
			    getNehrFlowDialog().setLocationRelativeTo(null);
			    getNehrFlowDialog().setVisible(true);
			  }
		  };
	  }
	  return showVehrFlowDialog;
  }
  
  private JMenuItem getYamabe2DMenuItem() {
	  if(yamabe2DMenuItem == null) {
		  yamabe2DMenuItem = new JMenuItem();
		  yamabe2DMenuItem.setText("2D Yamabe Flow");
		  yamabe2DMenuItem.setAction(getShowYamabe2DDialog());
	  }
	  return yamabe2DMenuItem;
  }
  
  private AbstractAction getShowYamabe2DDialog() {
	  if(showYamabe2DDialog == null) {
		  showYamabe2DDialog = new AbstractAction("2D Yamabe Flow", null) {
			  public void actionPerformed(ActionEvent evt) {
			    getYamabe2DDialog().pack();
			    getYamabe2DDialog().setLocationRelativeTo(null);
			    getYamabe2DDialog().setVisible(true);
			  }
		  };
	  }
	  return showYamabe2DDialog;
  }
  
  private JMenuItem getYamabe3DMenuItem() {
	  if(yamabe3DMenuItem == null) {
		  yamabe3DMenuItem = new JMenuItem();
		  yamabe3DMenuItem.setText("3D Yamabe Flow");
		  yamabe3DMenuItem.setAction(getShowYamabe3DDialog());
	  }
	  return yamabe3DMenuItem;
  }
  
  private AbstractAction getShowYamabe3DDialog() {
	  if(showYamabe3DDialog == null) {
		  showYamabe3DDialog = new AbstractAction("3D Yamabe Flow", null) {
			  public void actionPerformed(ActionEvent evt) {
          getYamabe3DDialog().pack();
          getYamabe3DDialog().setLocationRelativeTo(null);
          getYamabe3DDialog().setVisible(true);
			  }
		  };
	  }
	  return showYamabe3DDialog;
  }

  class TriangulationFilter extends FileFilter {
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }
      
      String extension = f.getName();
      int index = extension.lastIndexOf('.');
      if(index < 0) {
        return false;
      }

      extension = extension.substring(index).toLowerCase();
      return extension.equals(".xml");
    }
    public String getDescription() {
      return "XML (.xml)";
    }
    
  }
  
  class CloseSetRadiiDialog extends AbstractAction {
    public CloseSetRadiiDialog(String text) {
      super(text, null);
    }
    public void actionPerformed(ActionEvent evt) {
      if(getOkButton_SR().equals(evt.getSource())) {
        try{
          double rad_value = Double.parseDouble(getSetRadiiTextField().getText());
          for(Radius r : Geometry.getRadii()) {
            r.setValue(rad_value);
          }
          getGeoPolygonPanel().repaint();
        } catch(NumberFormatException e) {
        }
      }
      getSetRadiiDialog().dispose();
    }
  }
  
  class CloseSetEtaDialog extends AbstractAction {
    public CloseSetEtaDialog(String text) {
      super(text, null);
    }
    public void actionPerformed(ActionEvent evt) {
      if(getOkButton_SE().equals(evt.getSource())) {
        try{
          double eta_value = Double.parseDouble(getSetEtaTextField().getText());
          for(Eta e : Geometry.getEtas()) {
            e.setValue(eta_value);
          }
          getGeoPolygonPanel().repaint();
        } catch(NumberFormatException e) {
        }
      }
      getSetEtaDialog().dispose();
    }
  }
  
  protected GeoRecorder getRecorder() {
    GeoRecorder rec = new GeoRecorder();
    if(getAngleCheck().isSelected()) {
      rec.addGeoquant(Angle.class);
    }
    if(getAreaCheck().isSelected()) {
      rec.addGeoquant(Area.class);
    }
    if(getConeAngleCheck().isSelected()) {
      rec.addGeoquant(ConeAngle.class);
    }
    if(getCurv2DCheck().isSelected()) {
      rec.addGeoquant(Curvature2D.class);
    }
    if(getCurv3DCheck().isSelected()) {
      rec.addGeoquant(Curvature3D.class);
    }
    if(getDihAngleCheck().isSelected()) {
      rec.addGeoquant(DihedralAngle.class);
    }
    if(getDualAreaCheck().isSelected()) {
      rec.addGeoquant(DualArea.class);
    }
    if(getEdgeHeightCheck().isSelected()) {
      rec.addGeoquant(EdgeHeight.class);
    }
    if(getEtaCheck().isSelected()) {
      rec.addGeoquant(Eta.class);
    }
    if(getFaceHeightCheck().isSelected()) {
      rec.addGeoquant(FaceHeight.class);
    }
    if(getLengthCheck().isSelected()) {
      rec.addGeoquant(Length.class);
    }
    if(getVehrCheck().isSelected()) {
      rec.addGeoquant(VEHR.class);
    }
    if(getPartialEdgeCheck().isSelected()) {
      rec.addGeoquant(PartialEdge.class);
    }
    if(getRadiusCheck().isSelected()) {
      rec.addGeoquant(Radius.class);
    }
    if(getVolumeCheck().isSelected()) {
      rec.addGeoquant(Volume.class);
    }
    
    // Partials
    if(getCurvPartialCheck().isSelected()) {
      rec.addGeoquant(Curvature3D.Partial.class);
    }
    if(getDihAnglePartialCheck().isSelected()) {
      rec.addGeoquant(DihedralAngle.Partial.class);
    }
    if(getNehrPartialCheck().isSelected()) {
      rec.addGeoquant(VEHR.Partial.class);
    }
    if(getPartialEdgePartialCheck().isSelected()) {
      rec.addGeoquant(PartialEdge.Partial.class);
    }
    if(getRadiusPartialCheck().isSelected()) {
      rec.addGeoquant(Radius.Partial.class);
    }
    if(getVolumePartialCheck().isSelected()) {
      rec.addGeoquant(Volume.Partial.class);
    }
    
    // Second Partials
    if(getCurvatureSecondPartialCheck().isSelected()) {
      rec.addGeoquant(Curvature3D.SecondPartial.class);
    }
    if(getDihAngleSecondPartialCheck().isSelected()) {
      rec.addGeoquant(DihedralAngle.SecondPartial.class);
    }
    if(getNehrSecondPartialCheck().isSelected()) {
      rec.addGeoquant(VEHR.SecondPartial.class);
    }
    if(getPartialEdgeSecondPartialCheck().isSelected()) {
      rec.addGeoquant(PartialEdge.SecondPartial.class);
    }
    if(getVolumeSecondPartialCheck().isSelected()) {
      rec.addGeoquant(Volume.SecondPartial.class);
    }
    
    // Sums
    if(getTotalCurvatureCheck().isSelected()) {
      rec.addGeoquant(Curvature3D.Sum.class);
    }
    if(getTotalVolumeCheck().isSelected()) {
      rec.addGeoquant(Volume.Sum.class);
    }
    if(getTotalVolumePartialCheck().isSelected()) {
      rec.addGeoquant(Volume.PartialSum.class);
    }
    if(getTotalVolumeSecondPartialCheck().isSelected()) {
      rec.addGeoquant(Volume.SecondPartialSum.class);
    }
    
    if(getLehrCheck().isSelected()) {
      rec.addGeoquant(LEHR.class);
    }
    if(getLcscCheck().isSelected()) {
      rec.addGeoquant(LCSC.class);
    }
    if(getVcscCheck().isSelected()) {
      rec.addGeoquant(VCSC.class);
    }
    if(getLEinsteinCheck().isSelected()) {
      rec.addGeoquant(LEinstein.class);
    }
    if(getVEinsteinCheck().isSelected()) {
      rec.addGeoquant(VEinstein.class);
    }
    if(getEdgeCurvatureCheck().isSelected()) {
      rec.addGeoquant(EdgeCurvature.class);
    }
    return rec;
  }
  
  
  protected void showGeoquantHistory(GeoRecorder rec) {
    
  }
  
  private JMenuItem getSaveGeoquantsMenuItem() {
	  if(saveGeoquantsMenuItem == null) {
		  saveGeoquantsMenuItem = new JMenuItem();
		  saveGeoquantsMenuItem.setText("Save Geoquants");
		  saveGeoquantsMenuItem.setAction(getSaveGeoquantAction());
	  }
	  return saveGeoquantsMenuItem;
  }
  
  private AbstractAction getSaveGeoquantAction() {
	  if(saveGeoquantAction == null) {
		  saveGeoquantAction = new AbstractAction("Save Geoquants", null) {
        public void actionPerformed(ActionEvent e) {
          // Handle Save Button Action
          int returnVal = getTriangulationFileChooser().showSaveDialog(GeoquantViewer.this);
          
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = getTriangulationFileChooser().getSelectedFile();
            PrintStream out = null;
            try {
              out = new PrintStream(file);
            } catch (FileNotFoundException e1) {
              return;
            }
            saveQuantities(out);
            out.close();
          }
        }
		  };
	  }
	  return saveGeoquantAction;
  }
  
  private void saveQuantities(PrintStream out) {

    if(getAngleCheck().isSelected()) {
      for(Angle q : Geometry.getAngles()) {
        out.println(q);
      }
    }
    if(getAreaCheck().isSelected()) {
      for(Area q : Geometry.getAreas()) {
        out.println(q);
      }
    }
    if(getConeAngleCheck().isSelected()) {
      for(ConeAngle q : Geometry.getConeAngles()) {
        out.println(q);
      }
    }
    if(getCurv2DCheck().isSelected()) {
      for(Curvature2D q : Geometry.getCurvature2D()) {
        out.println(q);
      }
    }
    if(getCurv3DCheck().isSelected()) {
      for(Curvature3D q : Geometry.getCurvature3D()) {
        out.println(q);
      }
    }
    if(getDihAngleCheck().isSelected()) {
      for(DihedralAngle q : Geometry.getDihedralAngles()) {
        out.println(q);
      }
    }
    if(getDualAreaCheck().isSelected()) {
      for(DualArea q : Geometry.getDualAreas()) {
        out.println(q);
      }
    }
    if(getEdgeHeightCheck().isSelected()) {
      for(EdgeHeight q : Geometry.getEdgeHeights()) {
        out.println(q);
      }
    }
    if(getEtaCheck().isSelected()) {
      for(Eta q : Geometry.getEtas()) {
        out.println(q);
      }
    }
    if(getFaceHeightCheck().isSelected()) {
      for(FaceHeight q : Geometry.getFaceHeights()) {
        out.println(q);
      }
    }
    if(getLengthCheck().isSelected()) {
      for(Length q : Geometry.getLengths()) {
        out.println(q);
      }
    }
    if(getVehrCheck().isSelected()) {
      out.println(VEHR.getInstance());
    }
    if(getPartialEdgeCheck().isSelected()) {
      for(PartialEdge q : Geometry.getPartialEdges()) {
        out.println(q);
      }
    }
    if(getRadiusCheck().isSelected()) {
      for(Radius q : Geometry.getRadii()) {
        out.println(q);
      }
    }
    if(getVolumeCheck().isSelected()) {
      for(Volume q : Geometry.getVolumes()) {
        out.println(q);
      }
    }
    
    // Partials
    if(getCurvPartialCheck().isSelected()) {
      for(Curvature3D.Partial q : Geometry.getCurvaturePartials()) {
        out.println(q);
      }
    }
    if(getDihAnglePartialCheck().isSelected()) {
      for(DihedralAngle.Partial q : Geometry.getDihedralAnglePartials()) {
        out.println(q);
      }
    }
    if(getNehrPartialCheck().isSelected()) {
      for(VEHR.Partial q : Geometry.getVEHRPartials()) {
        out.println(q);
      }
    }
    if(getPartialEdgePartialCheck().isSelected()) {
      for(PartialEdge.Partial q : Geometry.getPartialEdgePartials()) {
        out.println(q);
      }
    }
    if(getRadiusPartialCheck().isSelected()) {
      for(Radius.Partial q : Geometry.getRadiusPartials()) {
        out.println(q);
      }
    }
    if(getVolumePartialCheck().isSelected()) {
      for(Volume.Partial q : Geometry.getVolumePartials()) {
        out.println(q);
      }
    }
    
    // Second Partials
    if(getCurvatureSecondPartialCheck().isSelected()) {
      for(Curvature3D.SecondPartial q : Geometry.getCurvatureSecondPartials()) {
        out.println(q);
      }
    }
    if(getDihAngleSecondPartialCheck().isSelected()) {
      for(DihedralAngle.SecondPartial q : Geometry.getDihedralAngleSecondPartials()) {
        out.println(q);
      }
    }
    if(getNehrSecondPartialCheck().isSelected()) {
      for(VEHR.SecondPartial q : Geometry.getVEHRSecondPartials()) {
        out.println(q);
      }
    }
    if(getPartialEdgeSecondPartialCheck().isSelected()) {
      for(PartialEdge.SecondPartial q : Geometry.getPartialEdgeSecondPartials()) {
        out.println(q);
      }
    }
    if(getVolumeSecondPartialCheck().isSelected()) {
      for(Volume.SecondPartial q : Geometry.getVolumeSecondPartials()) {
        out.println(q);
      }
    }
    
    // Sums
    if(getTotalCurvatureCheck().isSelected()) {
      out.println(Curvature3D.sum());
    }
    if(getTotalVolumeCheck().isSelected()) {
      out.println(Volume.sum());
    }
    if(getTotalVolumePartialCheck().isSelected()) {
      for(Volume.PartialSum q : Geometry.getVolumePartialSums()) {
        out.println(q);
      }
    }
    if(getTotalVolumeSecondPartialCheck().isSelected()) {
      for(Volume.SecondPartialSum q : Geometry.getVolumeSecondPartialSums()) {
        out.println(q);
      }
    }
    
    if(getLehrCheck().isSelected()) {
      out.println(LEHR.getInstance());
    }
    if(getLcscCheck().isSelected()) {
      for(LCSC lcsc : Geometry.getLCSC()) {
        out.println(lcsc);
      }
    }
    if(getVcscCheck().isSelected()) {
      for(VCSC vcsc : Geometry.getVCSC()) {
        out.println(vcsc);
      }
    }
    if(getLEinsteinCheck().isSelected()) {
      for(LEinstein le : Geometry.getLEinsteins()) {
        out.println(le);
      }
    }
    if(getVEinsteinCheck().isSelected()) {
      for(VEinstein ve : Geometry.getVEinsteins()) {
        out.println(ve);
      }
    }
    if(getEdgeCurvatureCheck().isSelected()) {
      for(EdgeCurvature c : Geometry.getEdgeCurvatures()) {
        out.println(c);
      }
    }
  }

  private JCheckBox getEdgeCurvatureCheck() {
	  if(edgeCurvatureCheck == null) {
		  edgeCurvatureCheck = new JCheckBox();
		  edgeCurvatureCheck.setText("Edge Curvature");
		  edgeCurvatureCheck.setBounds(8, 106, 148, 16);
      getGeoColorTable().put(edgeCurvatureCheck, new Color(0, 0, 0));
      edgeCurvatureCheck.addItemListener(this);
	  }
	  return edgeCurvatureCheck;
  }
  private JCheckBox getLehrCheck() {
	  if(lehrCheck == null) {
		  lehrCheck = new JCheckBox();
		  lehrCheck.setText("LEHR");
		  lehrCheck.setBounds(324, 108, 148, 16);
		  getGeoColorTable().put(lehrCheck, new Color(0, 0, 0));
		  lehrCheck.addItemListener(this);
	  }
	  return lehrCheck;
  }
  private JCheckBox getLcscCheck() {
	  if(lcscCheck == null) {
		  lcscCheck = new JCheckBox();
		  lcscCheck.setText("LCSC");
		  lcscCheck.setBounds(324, 48, 148, 16);
		  getGeoColorTable().put(lcscCheck, new Color(0, 0, 0));
		  lcscCheck.addItemListener(this);
	  }
	  return lcscCheck;
  }
  private JCheckBox getVcscCheck() {
	  if(vcscCheck == null) {
		  vcscCheck = new JCheckBox();
		  vcscCheck.setText("VCSC");
		  vcscCheck.setBounds(324, 68, 148, 16);
		  getGeoColorTable().put(vcscCheck, new Color(0, 0, 0));
		  vcscCheck.addItemListener(this);
	  }
	  return vcscCheck;
  }
  private JCheckBox getLEinsteinCheck() {
	  if(lEinsteinCheck == null) {
		  lEinsteinCheck = new JCheckBox();
		  lEinsteinCheck.setText("LEinstein");
		  lEinsteinCheck.setBounds(324, 8, 148, 16);
		  getGeoColorTable().put(lEinsteinCheck, new Color(0, 0, 0));
		  lEinsteinCheck.addItemListener(this);
	  }
	  return lEinsteinCheck;
  }
  private JCheckBox getVEinsteinCheck() {
	  if(vEinsteinCheck == null) {
		  vEinsteinCheck = new JCheckBox();
		  vEinsteinCheck.setText("VEinstein");
		  vEinsteinCheck.setBounds(324, 28, 148, 16);
		  getGeoColorTable().put(vEinsteinCheck, new Color(0, 0, 0));
		  vEinsteinCheck.addItemListener(this);
	  }
	  return vEinsteinCheck;
  }
  
  private JTextField getLengthSetField() {
	  if(lengthSetField == null) {
		  lengthSetField = new JTextField();
		  lengthSetField.setEnabled(false);
		  lengthSetField.setName("lengthSetField");
		  lengthSetField.setHorizontalAlignment(SwingConstants.TRAILING);
		  lengthSetField.addActionListener(new SetValueListener());
	  }
	  return lengthSetField;
  }
  
  private JLabel getLengthSetLabel() {
	  if(lengthSetLabel == null) {
		  lengthSetLabel = new JLabel();
		  lengthSetLabel.setText("Length:");
		  lengthSetLabel.setHorizontalAlignment(SwingConstants.CENTER);
		  lengthSetLabel.setBackground(new java.awt.Color(192,192,192));
		  lengthSetLabel.setForeground(new java.awt.Color(0,0,0));
	  }
	  return lengthSetLabel;
  }
  
  private JSlider getLengthSlider() {
	  if(lengthSlider == null) {
		  lengthSlider = new JSlider();
		  lengthSlider.setValue(10);
		  lengthSlider.setMaximum(50);
		  lengthSlider.setLabelTable(labelTable);
		  lengthSlider.setMajorTickSpacing(5);
		  lengthSlider.setMinorTickSpacing(1);
		  lengthSlider.setPaintLabels(true);
		  lengthSlider.setPaintTicks(true);
		  lengthSlider.setEnabled(false);
		  lengthSlider.setName("lengthSlider");
		  lengthSlider.addChangeListener(new SliderListener());
		  Double two = new Double(2);
		  String s = "2.0";
	  }
	  return lengthSlider;
  }
  private JCheckBox getTotalCurvatureCheck() {
	  if(totalCurvatureCheck == null) {
		  totalCurvatureCheck = new JCheckBox();
		  totalCurvatureCheck.setText("EHR");
		  totalCurvatureCheck.setBounds(324, 88, 148, 16);
		  getGeoColorTable().put(totalCurvatureCheck, new Color(0, 90, 165));
		  totalCurvatureCheck.addItemListener(this);
	  }
	  return totalCurvatureCheck;
  }
  private JCheckBox getTotalVolumeCheck() {
	  if(totalVolumeCheck == null) {
		  totalVolumeCheck = new JCheckBox();
		  totalVolumeCheck.setText("Total Volume");
		  totalVolumeCheck.setBounds(161, 128, 148, 16);
		  getGeoColorTable().put(totalVolumeCheck, new Color(0, 75, 180));
		  totalVolumeCheck.addItemListener(this);
	  }
	  return totalVolumeCheck;
  }
  private JCheckBox getConeAngleCheck() {
	  if(coneAngleCheck == null) {
		  coneAngleCheck = new JCheckBox();
		  coneAngleCheck.setText("Dihedral Angle Sum");
		  coneAngleCheck.setBounds(161, 148, 148, 16);
		  getGeoColorTable().put(coneAngleCheck, new Color(210, 45, 0));
		  coneAngleCheck.addItemListener(this);
	  }
	  return coneAngleCheck;
  }
  private JCheckBox getTotalVolumePartialCheck() {
	  if(totalVolumePartialCheck == null) {
		  totalVolumePartialCheck = new JCheckBox();
		  totalVolumePartialCheck.setText("Total Volume");
		  totalVolumePartialCheck.setBounds(8, 134, 156, 23);
		  getGeoColorTable().put(totalVolumePartialCheck, new Color(0, 60, 195));
		  totalVolumePartialCheck.addItemListener(this);
	  }
	  return totalVolumePartialCheck;
  }
  private JCheckBox getTotalVolumeSecondPartialCheck() {
	  if(totalVolumeSecondPartialCheck == null) {
		  totalVolumeSecondPartialCheck = new JCheckBox();
		  totalVolumeSecondPartialCheck.setText("Total Volume");
		  getGeoColorTable().put(totalVolumeSecondPartialCheck, new Color(0, 45, 210));
		  totalVolumeSecondPartialCheck.addItemListener(this);
	  }
	  return totalVolumeSecondPartialCheck;
  }

}

