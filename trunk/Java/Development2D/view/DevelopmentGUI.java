package view;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme.schemes;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.Vector;

public class DevelopmentGUI extends JFrame implements KeyListener {
  private static final long serialVersionUID = 1L;

  public static void main(String[] args) {
    JFrame window = new DevelopmentGUI();
    window.setVisible(true);
  }

  private int currentDepth = 21;
 
  private static int MAX_DEPTH = 25;
  private static int MAX_STEP_SIZE = 20;
  private static int INITIAL_STEP_SIZE = 3;
  private double stepSize = INITIAL_STEP_SIZE/100.0;
  private static int MAX_POINT_SIZE = 20;
  private static int INITIAL_POINT_SIZE = 3;
  private double radius = INITIAL_POINT_SIZE/100.0;

  private static Development development;
  private static Vector sourcePoint;
  private static Face sourceFace;
  private static ColorScheme colorScheme;
  private static DevelopmentView2D view2D;
  private static DevelopmentView3D view3D;
  private static DevelopmentViewEmbedded embeddedView;

  private JPanel sliderPanel;
  private JPanel movementPanel;
  private JPanel colorPanel;
  
  private String filename; // name of file with surface data
  private boolean showEmbedded = false; // flag for showing embedded view

  // Movement stuff
  private Timer timer; // timer for moving source
  private Timer moveTimer; // timer for moving objects
  private Timer keyHoldTimer;
  private static final double ROTATION_ANGLE = Math.PI / 80;

  private enum movements {
    left, right, forward, back
  };

  private movements curMovement;

  public DevelopmentGUI() {
    colorScheme = new ColorScheme(schemes.FACE);

    development = null;
    String filename = "surfaces/cone.off";
    loadSurface(filename);

    System.out.println("======== Initializing 2D view ========");
    view2D = new DevelopmentView2D(development, colorScheme, radius);
    System.out.println("---------------- done ----------------");
    development.addObserver(view2D);

    System.out.println("======== Initializing 3D view ========");
    view3D = new DevelopmentView3D(development, colorScheme, radius);
    System.out.println("---------------- done ----------------");
    development.addObserver(view3D);
    

    keyHoldTimer = new Timer(2, null);
    keyHoldTimer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        timer.stop();
      }
    });
    timer = new Timer(50, null);
    timer.addActionListener(new Moving());

    layoutGUI();
    
    moveTimer = new Timer(50, null);
    moveTimer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        development.moveObjects();
      }
    });
    moveTimer.start();
  }

  /*
   * loads triangulated surface from file given by filename. Chooses an arbitrary source face
   * and source point in that face. Constructs a new Development if one hasn't already been
   * made. Otherwise rebuilds development with new surface.
   */
  private void loadSurface(String filename) {
    this.filename = filename;
    EmbeddedTriangulation.readEmbeddedSurface(filename);
    
    Iterator<Integer> i = null;
    // pick some arbitrary face and source point
    i = Triangulation.faceTable.keySet().iterator();
    sourceFace = Triangulation.faceTable.get(i.next());

    sourcePoint = new Vector(0, 0);
    Iterator<Vertex> iv = sourceFace.getLocalVertices().iterator();
    while (iv.hasNext()) {
      sourcePoint.add(Coord2D.coordAt(iv.next(), sourceFace));
    }
    sourcePoint.scale(1.0f / 3.0f);

    if (development == null)
      development = new Development(sourceFace, sourcePoint, currentDepth, stepSize, radius);
    else development.rebuild(sourceFace, sourcePoint, currentDepth);
 
    
    if(showEmbedded) {
      if(embeddedView == null) embeddedView = new DevelopmentViewEmbedded(filename, development);
      else embeddedView.changeGeometry(filename);
    }

  }
  
  TitledBorder stepBorder = BorderFactory.createTitledBorder("Step Size (" + stepSize + ")");
  TitledBorder depthBorder = BorderFactory.createTitledBorder("Recursion Depth (" + currentDepth + ")");
  TitledBorder pointBorder = BorderFactory.createTitledBorder("Node Radius (" + radius + ")");


  private void layoutGUI() {
    this.setSize(220, 400);
    this.setResizable(true);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setTitle("Development View");

    // -------- MENU BAR --------
    JMenuBar menuBar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenuItem open = new JMenuItem("Load Surface");
    open.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JFileChooser fc = new JFileChooser();

        fc.setDialogTitle("Open File");
        // Choose only files, not directories
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        // Start in current directory
        fc.setCurrentDirectory(new File("."));
        fc.showOpenDialog(null);

        File file = null;
        try {
          file = fc.getSelectedFile();
        } catch (Exception ex) {
          System.out.println("Invalid file");
        }

        loadSurface(file.getAbsolutePath());
      }
    });
    menuBar.add(file);
    file.add(open);
    this.setJMenuBar(menuBar);

    sliderPanel = new JPanel();
    sliderPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
    sliderPanel.setLayout(new BoxLayout(sliderPanel,BoxLayout.Y_AXIS));
    
    // -------- DEPTH SLIDER --------
    JSlider depthSlider = new JSlider(1, MAX_DEPTH, currentDepth);
    depthSlider.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          currentDepth = ((JSlider)e.getSource()).getValue();
          development.setDepth(currentDepth);
          depthBorder.setTitle("Recursion Depth (" + currentDepth + ")");
          movementPanel.requestFocusInWindow();
        }
    });
    
    // -------- STEP SIZE SLIDER --------
    JSlider stepSizeSlider = new JSlider(1, MAX_STEP_SIZE, INITIAL_STEP_SIZE);
    stepSizeSlider.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          stepSize = ((JSlider)e.getSource()).getValue()/100.0;
          development.setStepSize(stepSize);
          stepBorder.setTitle("Step Size (" + stepSize + ")");
          movementPanel.requestFocusInWindow();
        }
    }); 
    
    // -------- POINT SIZE SLIDER --------
    JSlider pointSizeSlider = new JSlider(1, MAX_POINT_SIZE, INITIAL_POINT_SIZE);
    pointSizeSlider.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          radius = ((JSlider)e.getSource()).getValue()/100.0;
//          view2D.setRadius(radius);
//          view3D.setRadius(radius);
          development.setRadius(radius);
          pointBorder.setTitle("Node Radius (" + radius + ")");
          movementPanel.requestFocusInWindow();
        }
    }); 
    
    depthSlider.setBorder(depthBorder);
    sliderPanel.add(depthSlider);
    stepSizeSlider.setBorder(stepBorder);
    sliderPanel.add(stepSizeSlider);
    pointSizeSlider.setBorder(pointBorder);
    sliderPanel.add(pointSizeSlider);

    // -------- COLOR SCHEME BUTTONS --------
    colorPanel = new JPanel();
    JButton depthSchemeButton = new JButton("Depth");
    depthSchemeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (colorScheme.getSchemeType() != schemes.DEPTH) {
          colorScheme = new ColorScheme(schemes.DEPTH);
          view2D.setColorScheme(colorScheme);
          view3D.setColorScheme(colorScheme);
          movementPanel.requestFocusInWindow();
        }
      }
    });
    JButton faceSchemeButton = new JButton("Face");
    faceSchemeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (colorScheme.getSchemeType() != schemes.FACE) {
          colorScheme = new ColorScheme(schemes.FACE);
          view2D.setColorScheme(colorScheme);
          view3D.setColorScheme(colorScheme);
          movementPanel.requestFocusInWindow();
        }
      }
    });
    colorPanel.setLayout(new GridLayout(5, 1));
    colorPanel.add(new JLabel("Set Color Scheme"));
    colorPanel.add(depthSchemeButton);
    colorPanel.add(faceSchemeButton);
    
    // -------- STOP/START MOVEMENT BUTTON --------
    JButton stopStartButton = new JButton("Stop");
    stopStartButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JButton source = (JButton)e.getSource();
        if (source.getText().equals("Stop")) {
          source.setText("Start");
          moveTimer.stop();
        }
        else {
          source.setText("Stop");
          moveTimer.start();
        }
        movementPanel.requestFocusInWindow();
      }
    });
    colorPanel.add(stopStartButton);

    
    JPanel checkPanel = new JPanel();
    checkPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    
    // -------- SHOW EMBEDDED VIEW CHECKBOX --------
    JCheckBox showEmbeddedBox = new JCheckBox("Show embedded view");
    showEmbeddedBox.setSelected(showEmbedded);
    showEmbeddedBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showEmbedded = ((JCheckBox)e.getSource()).isSelected();
        setEmbeddedVisible(showEmbedded);
        movementPanel.requestFocusInWindow();
      }
    });
    
    JCheckBox drawEdgesBox = new JCheckBox("Draw edges");
    drawEdgesBox.setSelected(true);
    drawEdgesBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boolean value = ((JCheckBox)e.getSource()).isSelected();
        view2D.setDrawEdges(value);
        view3D.setDrawEdges(value);
        movementPanel.requestFocusInWindow();
      }
    });
    
    JCheckBox drawFacesBox = new JCheckBox("Draw faces");
    drawFacesBox.setSelected(true);
    drawFacesBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        boolean value = ((JCheckBox)e.getSource()).isSelected();
        view2D.setDrawFaces(value);
        view3D.setDrawFaces(value);
        movementPanel.requestFocusInWindow();
      }
    });
    checkPanel.setLayout(new BoxLayout(checkPanel, BoxLayout.Y_AXIS));
    checkPanel.add(showEmbeddedBox);
    checkPanel.add(drawEdgesBox);
    checkPanel.add(drawFacesBox);
    
    // -------- ADD PANELS --------
    this.setLayout(new FlowLayout());
    this.add(sliderPanel);
    this.add(colorPanel);
    this.add(checkPanel);

    

    // Arrow keys will move source point as long as focus is on this panel.
    // Other panels should return focus to this one after performing actions.
    movementPanel = new JPanel();
    this.add(movementPanel);

    movementPanel.addKeyListener(this);
    movementPanel.setFocusable(true);
    movementPanel.requestFocus();

    this.addWindowFocusListener(new WindowAdapter() {
      public void windowGainedFocus(WindowEvent e) {
        movementPanel.requestFocusInWindow();
      }
    });

  }
  
  private void setEmbeddedVisible(boolean setVisible) {
    if(setVisible && embeddedView == null) {
        embeddedView = new DevelopmentViewEmbedded(filename, development);
    } else if(!setVisible && embeddedView != null) {
      embeddedView.dispose();
      embeddedView = null;
    }
  }

  public class Moving implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (curMovement == movements.right) {
        development.rotate(ROTATION_ANGLE);
        
      } else if (curMovement == movements.left) {
        development.rotate(-ROTATION_ANGLE);
        
      } else if (curMovement == movements.forward) {
        development.translateSourcePoint("forward");
        
      } else if (curMovement == movements.back) {
        development.translateSourcePoint("back");
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    keyHoldTimer.stop();
    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
      curMovement = movements.right;
    else if (e.getKeyCode() == KeyEvent.VK_LEFT)
      curMovement = movements.left;
    else if (e.getKeyCode() == KeyEvent.VK_UP)
      curMovement = movements.forward;
    else if (e.getKeyCode() == KeyEvent.VK_DOWN)
      curMovement = movements.back;

    if (!timer.isRunning()) {
      timer.start();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    keyHoldTimer.start();
  }
}
