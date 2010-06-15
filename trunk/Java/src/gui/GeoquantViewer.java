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
import javax.swing.SwingUtilities;

import Geoquant.Alpha;
import Geoquant.Curvature3D;
import Geoquant.Eta;
import Geoquant.Geometry;
import Geoquant.Geoquant;
import Geoquant.Length;
import Geoquant.NEHR;
import Geoquant.Radius;
import Geoquant.Volume;
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
  private JCheckBox sectionalCurvatureCheck;
  private JCheckBox radiusCheck;
  private JCheckBox faceHeightCheck;
  private JCheckBox etaCheck;
  private JCheckBox edgeHeightCheck;
  private JCheckBox dualAreaCheck;
  private JCheckBox coneAngleCheck;
  private JCheckBox alphaCheck;
  private JPanel geoPolygonPanel;
  private JCheckBox curv2DCheck;
  private JCheckBox curv3DCheck;
  private JCheckBox areaCheck;
  private JCheckBox partialEdgeCheck;
  private JCheckBox nehrCheck;
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
  private AbstractAction import2DAction;
  private JCheckBox totalVolumeSecondPartialCheck;
  private JCheckBox totalVolumePartialCheck;
  private JCheckBox totalVolumeCheck;
  private JCheckBox totalCurvatureCheck;
  private JPanel totalSelectPanel;
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
  private GeoPolygon poly;
  
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
                  .addComponent(getQuantityModPanel(), GroupLayout.PREFERRED_SIZE, 576, GroupLayout.PREFERRED_SIZE))
              .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
                  .addComponent(getGeoPolygonPanel(), GroupLayout.PREFERRED_SIZE, 378, GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(getJTabbedPane1(), GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE)))
          .addContainerGap(20, Short.MAX_VALUE));
        thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
          .addContainerGap()
          .addGroup(thisLayout.createParallelGroup()
              .addComponent(getGeoPolygonPanel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 487, GroupLayout.PREFERRED_SIZE)
              .addComponent(getJTabbedPane1(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 485, GroupLayout.PREFERRED_SIZE))
          .addGap(21)
          .addComponent(getQuantityModPanel(), GroupLayout.PREFERRED_SIZE, 389, GroupLayout.PREFERRED_SIZE)
          .addContainerGap(25, Short.MAX_VALUE));
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.setResizable(false);
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
    for(Radius r : Geometry.getRadii()) {
      r.setValue(1.0);
    }
    for(Alpha a : Geometry.getAlphas()) {
      a.setValue(1.0);
    }
    for(Eta e : Geometry.getEtas()) {
      e.setValue(1.0);
    }
    getGeoPolygonPanel().repaint();
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
                            .addComponent(getCirclePackRadioButton(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(rad2SetLabel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                                .addGap(14))
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(rad1SetLabel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                                .addGap(14))
                            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                                .addComponent(etaSetLabel, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
                                .addGap(14)))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
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
                        .addGap(14))
                    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                        .addComponent(rad1Slider, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                        .addGap(14))
                    .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                        .addComponent(rad2Slider, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
                        .addGap(14)))
                .addComponent(getEdgeListScrollPane(), GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
            .addComponent(getEdgeDisplayPanel(), GroupLayout.Alignment.LEADING, 0, 373, Short.MAX_VALUE))
        .addContainerGap());
      quantityModPanelLayout.setVerticalGroup(quantityModPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(quantityModPanelLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, quantityModPanelLayout.createSequentialGroup()
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(etaSetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                    .addComponent(etaSetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
                .addComponent(etaSlider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(19)
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rad1SetLabel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                    .addComponent(rad1SetField, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
                .addComponent(rad1Slider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(22)
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(rad2SetField, GroupLayout.Alignment.BASELINE, 0, 27, Short.MAX_VALUE)
                    .addComponent(rad2SetLabel, GroupLayout.Alignment.BASELINE, 0, 27, Short.MAX_VALUE))
                .addComponent(rad2Slider, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                .addGap(22)
                .addGroup(quantityModPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(getCirclePackRadioButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(getPerpBisectorRadioButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE))
                .addGap(12))
            .addGroup(quantityModPanelLayout.createSequentialGroup()
                .addComponent(getEdgeListScrollPane(), GroupLayout.PREFERRED_SIZE, 294, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)))
        .addGap(20)
        .addComponent(getEdgeDisplayPanel(), GroupLayout.PREFERRED_SIZE, 228, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(24, 24));
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
      GroupLayout geoSelectPanelLayout = new GroupLayout((JComponent)basicSelectPanel);
      basicSelectPanel.setLayout(geoSelectPanelLayout);
      basicSelectPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
      geoSelectPanelLayout.setHorizontalGroup(geoSelectPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(geoSelectPanelLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getConeAngleCheck(), GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
                .addGap(23))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getAreaCheck(), GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
                .addGap(37))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getAngleCheck(), GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                .addGap(42))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getAlphaCheck(), GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                .addGap(42))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getCurv3DCheck(), GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))
            .addComponent(getCurv2DCheck(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE))
        .addGap(34)
        .addGroup(geoSelectPanelLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getFaceHeightCheck(), GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                .addGap(15))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getLengthCheck(), GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                .addGap(19))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getEtaCheck(), GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                .addGap(40))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getDualAreaCheck(), GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                .addGap(36))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getEdgeHeightCheck(), GroupLayout.PREFERRED_SIZE, 97, GroupLayout.PREFERRED_SIZE)
                .addGap(19))
            .addComponent(getDihAngleCheck(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))
        .addGap(36)
        .addGroup(geoSelectPanelLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getRadiusCheck(), GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 68, Short.MAX_VALUE))
            .addGroup(geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getSectionalCurvatureCheck(), GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getVolumeCheck(), GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 69, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getPartialEdgeCheck(), GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 41, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, geoSelectPanelLayout.createSequentialGroup()
                .addComponent(getNehrCheck(), GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 68, Short.MAX_VALUE)))
        .addContainerGap(29, 29));
      geoSelectPanelLayout.setVerticalGroup(geoSelectPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(geoSelectPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getNehrCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getDihAngleCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getAlphaCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(geoSelectPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getPartialEdgeCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getDualAreaCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getAngleCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(geoSelectPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getRadiusCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getEdgeHeightCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getAreaCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(geoSelectPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getSectionalCurvatureCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getEtaCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getConeAngleCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(geoSelectPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getVolumeCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getFaceHeightCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getCurv2DCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(geoSelectPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(getLengthCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
            .addComponent(getCurv3DCheck(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE))
        .addContainerGap(30, 30));
      geoSelectPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getRadiusCheck(), getDihAngleCheck(), getAlphaCheck(), getAngleCheck(), getDualAreaCheck(), getNehrCheck(), getPartialEdgeCheck(), getEdgeHeightCheck(), getAreaCheck(), getConeAngleCheck(), getEtaCheck(), getSectionalCurvatureCheck(), getVolumeCheck(), getFaceHeightCheck(), getLengthCheck(), getCurv3DCheck(), getCurv2DCheck()});
    }
    return basicSelectPanel;
  }
  
  private JCheckBox getLengthCheck() {
    if(lengthCheck == null) {
      lengthCheck = new JCheckBox();
      lengthCheck.setText("Length");
      lengthCheck.addItemListener(this);
    }
    return lengthCheck;
  }
  
  private JCheckBox getVolumeCheck() {
    if(volumeCheck == null) {
      volumeCheck = new JCheckBox();
      volumeCheck.setText("Volume");
      volumeCheck.addItemListener(this);
    }
    return volumeCheck;
  }
  
  private JCheckBox getDihAngleCheck() {
    if(dihAngleCheck == null) {
      dihAngleCheck = new JCheckBox();
      dihAngleCheck.setText("Dihedral Angle");
      dihAngleCheck.addItemListener(this);
    }
    return dihAngleCheck;
  }
  
  private JCheckBox getNehrCheck() {
    if(nehrCheck == null) {
      nehrCheck = new JCheckBox();
      nehrCheck.setText("NEHR");
      nehrCheck.addItemListener(this);
    }
    return nehrCheck;
  }
  
  private JCheckBox getPartialEdgeCheck() {
    if(partialEdgeCheck == null) {
      partialEdgeCheck = new JCheckBox();
      partialEdgeCheck.setText("Partial Edge");
      partialEdgeCheck.addItemListener(this);
    }
    return partialEdgeCheck;
  }
  
  private JCheckBox getAreaCheck() {
    if(areaCheck == null) {
      areaCheck = new JCheckBox();
      areaCheck.setText("Area");
      areaCheck.addItemListener(this);
    }
    return areaCheck;
  }
  
  private JCheckBox getCurv3DCheck() {
    if(curv3DCheck == null) {
      curv3DCheck = new JCheckBox();
      curv3DCheck.setText("Curvature(3D)");
      curv3DCheck.addItemListener(this);
    }
    return curv3DCheck;
  }
  
  private JCheckBox getCurv2DCheck() {
    if(curv2DCheck == null) {
      curv2DCheck = new JCheckBox();
      curv2DCheck.setText("Curvature(2D)");
      curv2DCheck.addItemListener(this);
    }
    return curv2DCheck;
  }
  
  private JPanel getGeoPolygonPanel() {
    if(geoPolygonPanel == null) {
      geoPolygonPanel = new JPanel() {
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          LinkedList<Geoquant> geoList = populateList();
          int size = geoList.size();
          poly = null;
          if(size == 0) {
            return;
          }
          int[] xpoints = new int[size];
          int[] ypoints = new int[size];
          double angleStep = 2 * Math.PI / size;
          int halfHeight = this.getHeight() / 2;
          int halfWidth = this.getWidth() / 2;
          int i = 0;
          double angle = 0;
          double radius;
          for(Geoquant q : geoList) {
            radius = (int) (halfHeight / Math.PI) * (Math.atan(q.getValue()) + Math.PI / 2);
            xpoints[i] = (int) (radius * Math.cos(angle) + halfWidth);
            ypoints[i] = (int) (-1 * radius * Math.sin(angle) + halfHeight);
            i++;
            angle += angleStep;
          }
          poly = new GeoPolygon(xpoints, ypoints, size, geoList.toArray());
          g.drawPolygon(poly);
          
          g.setColor(Color.RED);
          int circDiam = 5;
          for(int j = 0; j < xpoints.length; j++) {
            g.fillOval(xpoints[j] - circDiam / 2, ypoints[j] - circDiam / 2, 
                  circDiam, circDiam);
          }          
        }
        private LinkedList<Geoquant> populateList() {
          LinkedList<Geoquant> geoList = new LinkedList<Geoquant>();
          if(getAlphaCheck().isSelected()) {
            geoList.addAll(Geometry.getAlphas());
          }
          if(getAngleCheck().isSelected()) {
            geoList.addAll(Geometry.getAngles());
          }
          if(getAreaCheck().isSelected()) {
            geoList.addAll(Geometry.getAreas());
          }
          if(getConeAngleCheck().isSelected()) {
            geoList.addAll(Geometry.getConeAngles());
          }
          if(getCurv2DCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvature2D());
          }
          if(getCurv3DCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvature3D());
          }
          if(getDihAngleCheck().isSelected()) {
            geoList.addAll(Geometry.getDihedralAngles());
          }
          if(getDualAreaCheck().isSelected()) {
            geoList.addAll(Geometry.getDualAreas());
          }
          if(getEdgeHeightCheck().isSelected()) {
            geoList.addAll(Geometry.getEdgeHeights());
          }
          if(getEtaCheck().isSelected()) {
            geoList.addAll(Geometry.getEtas());
          }
          if(getFaceHeightCheck().isSelected()) {
            geoList.addAll(Geometry.getFaceHeights());
          }
          if(getLengthCheck().isSelected()) {
            geoList.addAll(Geometry.getLengths());
          }
          if(getNehrCheck().isSelected()) {
            geoList.add(NEHR.getInstance());
          }
          if(getPartialEdgeCheck().isSelected()) {
            geoList.addAll(Geometry.getPartialEdges());
          }
          if(getRadiusCheck().isSelected()) {
            geoList.addAll(Geometry.getRadii());
          }
          if(getSectionalCurvatureCheck().isSelected()) {
            geoList.addAll(Geometry.getSectionalCurvatures());
          }
          if(getVolumeCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumes());
          }
          
          // Partials
          if(getCurvPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvaturePartials());
          }
          if(getDihAnglePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getDihedralAnglePartials());
          }
          if(getNehrPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getNEHRPartials());
          }
          if(getPartialEdgePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getPartialEdgePartials());
          }
          if(getRadiusPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getRadiusPartials());
          }
          if(getVolumePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumePartials());
          }
          
          // Second Partials
          if(getCurvatureSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getCurvatureSecondPartials());
          }
          if(getDihAngleSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getDihedralAngleSecondPartials());
          }
          if(getNehrSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getNEHRSecondPartials());
          }
          if(getPartialEdgeSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getPartialEdgeSecondPartials());
          }
          if(getVolumeSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumeSecondPartials());
          }
          
          // Sums
          if(getTotalCurvatureCheck().isSelected()) {
            geoList.add(Curvature3D.sum());
          }
          if(getTotalVolumeCheck().isSelected()) {
            geoList.add(Volume.sum());
          }
          if(getTotalVolumePartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumePartialSums());
          }
          if(getTotalVolumeSecondPartialCheck().isSelected()) {
            geoList.addAll(Geometry.getVolumeSecondPartialSums());
          }
          return geoList;
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

        } else {
          // selection, enable text field, slider, change label name
          etaSetField.setEnabled(true);
          etaSlider.setEnabled(true);
          rad1SetField.setEnabled(true);
          rad1Slider.setEnabled(true);
          rad2SetField.setEnabled(true);
          rad2Slider.setEnabled(true);
          Edge s = (Edge) EdgeListModel.getElementAt(EdgeList.getSelectedIndex());
          Vertex v1 = s.getLocalVertices().get(0);
          Vertex v2 = s.getLocalVertices().get(1);
          rad1SetLabel.setText("Radius " + v1.getIndex() + ":");
          rad1SetField.setText("" + Radius.valueAt(v1));
          rad2SetLabel.setText("Radius " + v2.getIndex() + ":");
          rad2SetField.setText("" + Radius.valueAt(v2));
          etaSetField.setText("" + Eta.valueAt(s));
          
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
        } else if(name.equals("rad1SetField")) {
          Radius.At(s.getLocalVertices().get(0)).setValue(value);
        } else if(name.equals("rad2SetField")) {
          Radius.At(s.getLocalVertices().get(1)).setValue(value);
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
  
  class GeoPoint extends Point {
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
  
  class GeoPolygon extends Polygon {
    private HashMap<GeoPoint, Geoquant> geoMap;
    public GeoPolygon(int[] xpoints, int[] ypoints, int npoints, Object[] geos) {
      super(xpoints, ypoints, npoints);
      geoMap = new HashMap<GeoPoint, Geoquant>();
      GeoPoint p;
      for(int i = 0; i < geos.length; i++) {
        p = new GeoPoint(xpoints[i], ypoints[i]);
        geoMap.put(p, (Geoquant) geos[i]);
      }
    }
    public Geoquant getVertex(int x, int y) {
      return geoMap.get(new GeoPoint(x, y));
    }
  }

  public void itemStateChanged(ItemEvent e) {
    getGeoPolygonPanel().repaint();
  }
  
  private JCheckBox getAlphaCheck() {
    if(alphaCheck == null) {
      alphaCheck = new JCheckBox();
      alphaCheck.setText("Alpha");
      alphaCheck.addItemListener(this);
    }
    return alphaCheck;
  }
  
  private JCheckBox getAngleCheck() {
    if(angleCheck == null) {
      angleCheck = new JCheckBox();
      angleCheck.setText("Angle");
      angleCheck.addItemListener(this);
    }
    return angleCheck;
  }
  
  private JCheckBox getConeAngleCheck() {
    if(coneAngleCheck == null) {
      coneAngleCheck = new JCheckBox();
      coneAngleCheck.setText("Cone Angle");
      coneAngleCheck.addItemListener(this);
    }
    return coneAngleCheck;
  }
  
  private JCheckBox getDualAreaCheck() {
    if(dualAreaCheck == null) {
      dualAreaCheck = new JCheckBox();
      dualAreaCheck.setText("Dual Area");
      dualAreaCheck.addItemListener(this);
    }
    return dualAreaCheck;
  }
  
  private JCheckBox getEdgeHeightCheck() {
    if(edgeHeightCheck == null) {
      edgeHeightCheck = new JCheckBox();
      edgeHeightCheck.setText("Edge Height");
      edgeHeightCheck.addItemListener(this);
    }
    return edgeHeightCheck;
  }
  
  private JCheckBox getEtaCheck() {
    if(etaCheck == null) {
      etaCheck = new JCheckBox();
      etaCheck.setText("Eta");
      etaCheck.addItemListener(this);
    }
    return etaCheck;
  }
  
  private JCheckBox getFaceHeightCheck() {
    if(faceHeightCheck == null) {
      faceHeightCheck = new JCheckBox();
      faceHeightCheck.setText("Face Height");
      faceHeightCheck.addItemListener(this);
    }
    return faceHeightCheck;
  }
  
  private JCheckBox getRadiusCheck() {
    if(radiusCheck == null) {
      radiusCheck = new JCheckBox();
      radiusCheck.setText("Radius");
      radiusCheck.addItemListener(this);
    }
    return radiusCheck;
  }
  
  private JCheckBox getSectionalCurvatureCheck() {
    if(sectionalCurvatureCheck == null) {
      sectionalCurvatureCheck = new JCheckBox();
      sectionalCurvatureCheck.setText("Sectional Curvature");
      sectionalCurvatureCheck.addItemListener(this);
    }
    return sectionalCurvatureCheck;
  }
  
  private JTabbedPane getJTabbedPane1() {
    if(geoSelectTabbedPane == null) {
      geoSelectTabbedPane = new JTabbedPane();
      geoSelectTabbedPane.addTab("Basic Quantities", null, getBasicSelectPanel(), null);
      geoSelectTabbedPane.addTab("Partials", null, getPartialSelectPanel(), null);
      geoSelectTabbedPane.addTab("Second Partials", null, getSecondPartialSelectPanel(), null);
      geoSelectTabbedPane.addTab("Sums", null, getTotalSelectPanel(), null);
    }
    return geoSelectTabbedPane;
  }
  
  private JPanel getPartialSelectPanel() {
    if(partialSelectPanel == null) {
      partialSelectPanel = new JPanel();
      GroupLayout partialSelectPanelLayout = new GroupLayout((JComponent)partialSelectPanel);
      partialSelectPanel.setLayout(partialSelectPanelLayout);
      partialSelectPanel.setPreferredSize(new java.awt.Dimension(485, 158));
      partialSelectPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
      partialSelectPanelLayout.setHorizontalGroup(partialSelectPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(partialSelectPanelLayout.createParallelGroup()
            .addGroup(partialSelectPanelLayout.createSequentialGroup()
                .addComponent(getPartialEdgePartialCheck(), GroupLayout.PREFERRED_SIZE, 122, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, partialSelectPanelLayout.createSequentialGroup()
                .addComponent(getNehrPartialCheck(), GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 48, Short.MAX_VALUE))
            .addGroup(partialSelectPanelLayout.createSequentialGroup()
                .addComponent(getCurvPartialCheck(), GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(partialSelectPanelLayout.createSequentialGroup()
                .addComponent(getDihAnglePartialCheck(), GroupLayout.PREFERRED_SIZE, 121, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, partialSelectPanelLayout.createSequentialGroup()
                .addComponent(getRadiusPartialCheck(), GroupLayout.PREFERRED_SIZE, 77, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 45, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, partialSelectPanelLayout.createSequentialGroup()
                .addComponent(getVolumePartialCheck(), GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 44, Short.MAX_VALUE)))
        .addContainerGap(353, 353));
      partialSelectPanelLayout.setVerticalGroup(partialSelectPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(getCurvPartialCheck(), GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(getDihAnglePartialCheck(), GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(getNehrPartialCheck(), GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(getPartialEdgePartialCheck(), GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 0, GroupLayout.PREFERRED_SIZE)
        .addComponent(getRadiusPartialCheck(), GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(getVolumePartialCheck(), GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(23, 23));
      partialSelectPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getVolumePartialCheck(), getRadiusPartialCheck(), getPartialEdgePartialCheck(), getNehrPartialCheck(), getDihAnglePartialCheck(), getCurvPartialCheck()});
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
                .addComponent(getDihAngleSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
            .addGroup(seconPartialSelectPanelLayout.createSequentialGroup()
                .addComponent(getCurvatureSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
            .addGroup(seconPartialSelectPanelLayout.createSequentialGroup()
                .addComponent(getNehrSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
            .addGroup(seconPartialSelectPanelLayout.createSequentialGroup()
                .addComponent(getPartialEdgeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE))
            .addGroup(seconPartialSelectPanelLayout.createSequentialGroup()
                .addComponent(getVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(359, Short.MAX_VALUE));
      seconPartialSelectPanelLayout.setVerticalGroup(seconPartialSelectPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(getCurvatureSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
        .addGap(11)
        .addComponent(getDihAngleSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
        .addGap(11)
        .addComponent(getNehrSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
        .addGap(11)
        .addComponent(getPartialEdgeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
        .addGap(11)
        .addComponent(getVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(31, Short.MAX_VALUE));
      seconPartialSelectPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getCurvatureSecondPartialCheck(), getDihAngleSecondPartialCheck(), getNehrSecondPartialCheck(), getPartialEdgeSecondPartialCheck(), getVolumeSecondPartialCheck()});
    }
    return secondPartialSelectPanel;
  }
  
  private JCheckBox getCurvPartialCheck() {
    if(curvPartialCheck == null) {
      curvPartialCheck = new JCheckBox();
      curvPartialCheck.setText("Curvature(3D)");
      curvPartialCheck.addItemListener(this);
    }
    return curvPartialCheck;
  }
  
  private JCheckBox getDihAnglePartialCheck() {
    if(dihAnglePartialCheck == null) {
      dihAnglePartialCheck = new JCheckBox();
      dihAnglePartialCheck.setText("Dihedral Angle");
      dihAnglePartialCheck.addItemListener(this);
    }
    return dihAnglePartialCheck;
  }
  
  private JCheckBox getNehrPartialCheck() {
    if(nehrPartialCheck == null) {
      nehrPartialCheck = new JCheckBox();
      nehrPartialCheck.setText("NEHR");
      nehrPartialCheck.addItemListener(this);
    }
    return nehrPartialCheck;
  }
  
  private JCheckBox getPartialEdgePartialCheck() {
    if(partialEdgePartialCheck == null) {
      partialEdgePartialCheck = new JCheckBox();
      partialEdgePartialCheck.setText("Partial Edge");
      partialEdgePartialCheck.addItemListener(this);
    }
    return partialEdgePartialCheck;
  }
  
  private JCheckBox getRadiusPartialCheck() {
    if(radiusPartialCheck == null) {
      radiusPartialCheck = new JCheckBox();
      radiusPartialCheck.setText("Radius");
      radiusPartialCheck.addItemListener(this);
    }
    return radiusPartialCheck;
  }
  
  private JCheckBox getVolumePartialCheck() {
    if(volumePartialCheck == null) {
      volumePartialCheck = new JCheckBox();
      volumePartialCheck.setText("Volume");
      volumePartialCheck.addItemListener(this);
    }
    return volumePartialCheck;
  }
  
  private JCheckBox getCurvatureSecondPartialCheck() {
    if(curvatureSecondPartialCheck == null) {
      curvatureSecondPartialCheck = new JCheckBox();
      curvatureSecondPartialCheck.setText("Curvature(3D)");
      curvatureSecondPartialCheck.addItemListener(this);
    }
    return curvatureSecondPartialCheck;
  }
  
  private JCheckBox getDihAngleSecondPartialCheck() {
    if(dihAngleSecondPartialCheck == null) {
      dihAngleSecondPartialCheck = new JCheckBox();
      dihAngleSecondPartialCheck.setText("Dihedral Angle");
      dihAngleSecondPartialCheck.addItemListener(this);
    }
    return dihAngleSecondPartialCheck;
  }
  
  private JCheckBox getNehrSecondPartialCheck() {
    if(nehrSecondPartialCheck == null) {
      nehrSecondPartialCheck = new JCheckBox();
      nehrSecondPartialCheck.setText("NEHR");
      nehrSecondPartialCheck.addItemListener(this);
    }
    return nehrSecondPartialCheck;
  }
  
  private JCheckBox getPartialEdgeSecondPartialCheck() {
    if(partialEdgeSecondPartialCheck == null) {
      partialEdgeSecondPartialCheck = new JCheckBox();
      partialEdgeSecondPartialCheck.setText("Partial Edge");
      partialEdgeSecondPartialCheck.addItemListener(this);
    }
    return partialEdgeSecondPartialCheck;
  }
  
  private JCheckBox getVolumeSecondPartialCheck() {
    if(volumeSecondPartialCheck == null) {
      volumeSecondPartialCheck = new JCheckBox();
      volumeSecondPartialCheck.setText("Volume");
      volumeSecondPartialCheck.addItemListener(this);
    }
    return volumeSecondPartialCheck;
  }
  
  private JPanel getTotalSelectPanel() {
    if(totalSelectPanel == null) {
      totalSelectPanel = new JPanel();
      GroupLayout totalSelectPanelLayout = new GroupLayout((JComponent)totalSelectPanel);
      totalSelectPanel.setLayout(totalSelectPanelLayout);
      totalSelectPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
      totalSelectPanelLayout.setHorizontalGroup(totalSelectPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(totalSelectPanelLayout.createParallelGroup()
            .addGroup(totalSelectPanelLayout.createSequentialGroup()
                .addComponent(getTotalVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 193, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, totalSelectPanelLayout.createSequentialGroup()
                .addComponent(getTotalVolumePartialCheck(), GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 37, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, totalSelectPanelLayout.createSequentialGroup()
                .addComponent(getTotalVolumeCheck(), GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 37, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, totalSelectPanelLayout.createSequentialGroup()
                .addComponent(getTotalCurvatureCheck(), GroupLayout.PREFERRED_SIZE, 156, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 37, Short.MAX_VALUE)))
        .addContainerGap(286, 286));
      totalSelectPanelLayout.setVerticalGroup(totalSelectPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addComponent(getTotalCurvatureCheck(), GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(getTotalVolumeCheck(), GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(getTotalVolumePartialCheck(), GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(getTotalVolumeSecondPartialCheck(), GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(54, Short.MAX_VALUE));
      totalSelectPanelLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getTotalCurvatureCheck(), getTotalVolumeCheck(), getTotalVolumePartialCheck(), getTotalVolumeSecondPartialCheck()});
    }
    return totalSelectPanel;
  }
  
  private JCheckBox getTotalCurvatureCheck() {
    if(totalCurvatureCheck == null) {
      totalCurvatureCheck = new JCheckBox();
      totalCurvatureCheck.setText("Total Curvature(3D)");
      totalCurvatureCheck.addItemListener(this);
    }
    return totalCurvatureCheck;
  }
  
  private JCheckBox getTotalVolumeCheck() {
    if(totalVolumeCheck == null) {
      totalVolumeCheck = new JCheckBox();
      totalVolumeCheck.setText("Total Volume");
      totalVolumeCheck.addItemListener(this);
    }
    return totalVolumeCheck;
  }
  
  private JCheckBox getTotalVolumePartialCheck() {
    if(totalVolumePartialCheck == null) {
      totalVolumePartialCheck = new JCheckBox();
      totalVolumePartialCheck.setText("Total Volume Partial");
      totalVolumePartialCheck.addItemListener(this);
    }
    return totalVolumePartialCheck;
  }
  
  private JCheckBox getTotalVolumeSecondPartialCheck() {
    if(totalVolumeSecondPartialCheck == null) {
      totalVolumeSecondPartialCheck = new JCheckBox();
      totalVolumeSecondPartialCheck.setText("Total Volume Second Partial");
      totalVolumeSecondPartialCheck.addItemListener(this);
    }
    return totalVolumeSecondPartialCheck;
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
      if(poly == null) {
        return;
      }
      x = e.getX();
      y = e.getY();
      Geoquant q = poly.getVertex(x, y);
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
}