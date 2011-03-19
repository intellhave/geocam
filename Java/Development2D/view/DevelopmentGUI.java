package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import objects.BasicMovingObjects;
import objects.ManifoldPosition;
import objects.MovingObject;
import objects.ObjectAppearance;
import objects.ObjectDynamics;
import objects.PathAppearance;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme.schemes;
import development.Coord2D;
import development.Development;
import development.EmbeddedTriangulation;
import development.TimingStatistics;
import development.Vector;

public class DevelopmentGUI extends JFrame  implements Development.DevelopmentViewer, ObjectDynamics.DynamicsListener{
  private static final long serialVersionUID = 1L;

  public static void main(String[] args) {
    JFrame window = new DevelopmentGUI();
    window.setVisible(true);
  }
 
  //--- GUI options -----------------------
  private static int MAX_DEPTH          = 25;
  private static int MAX_POINT_SIZE     = 50; //hundredths of a unit
  private static int MAX_SPEED       = 10000; //in units per millisecond
  //------------------------------------
  
  //--- development options ------------------
  private static Development development;
  private static Vector sourcePoint;
  private static Face sourceFace;
  private static ColorScheme colorScheme;
  private int currentDepth = 8;
  private String filename; 
  //------------------------------------
  
  //--- viewers -----------------------
  private static DevelopmentView2D view2D = null;
  private static DevelopmentView3D view3D = null;
  private static DevelopmentViewEmbedded embeddedView = null;
  
  private static LinkedList<DevelopmentView> devViewers = new LinkedList<DevelopmentView>(); //active viewers
  
  //some viewer options
  private boolean showView2D = false;
  private boolean showView3D = false;
  private boolean showEmbedded = false;
  
  private boolean drawEdges = true;
  private boolean drawFaces = true;
  //------------------------------------
    
  //--- objects ------------------------
  //private static ShootingGame shootingGame = new ShootingGame();
  private static BasicMovingObjects dynamics = new BasicMovingObjects(50);
  private static final boolean INITIAL_MOVEMENT_STATUS = false;
  private static final int MOVING_OBJECT_COUNT = 50;
  double objectSpeed = 1; //units per second
  double objectRadius = 0.1;
  //don't generally need to keep track of this list, but GUI will change the objects' properties
  private static LinkedList<MovingObject> movingObjects = new LinkedList<MovingObject>(); 
  //------------------------------------

  public DevelopmentGUI() {
    colorScheme = new ColorScheme(schemes.FACE);

    development = null;
    String filename = "Data/off/tetra2.off";
    loadSurface(filename);

    layoutGUI();
    
    //make it display timing statistics on exit
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run(){ TimingStatistics.printData(); }
    });
    
    //set up objects
    development.getSourceObject().getAppearance().setRadius(objectRadius);
    Random rand = new Random();
    for(int i=0; i<MOVING_OBJECT_COUNT; i++){
      MovingObject newObject = new MovingObject( 
          development.getSource(), 
          new ObjectAppearance(objectRadius, randomColor(rand)), 
          randomUnitVector(rand) );
      newObject.setTrailEnabled(1,new PathAppearance(0.04,Color.BLACK,0.05,Color.BLUE));
      movingObjects.add(newObject);
    }
    for(MovingObject o : movingObjects){
      o.setSpeed(objectSpeed); //scale so speed is correct
      dynamics.addObject(o); 
    }
    if(INITIAL_MOVEMENT_STATUS){ 
      dynamics.start(); 
    }else{
      dynamics.evolve(300); //nudge the objects a little bit (300 ms)
    }

    //start listening for updates
    development.addViewer(this);
    dynamics.addListener(this);
  }
  
  private Color randomColor(Random rand){
    return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
  }
  
  private Vector randomUnitVector(Random rand){
    double a = rand.nextDouble()*Math.PI*2;
    return new Vector(Math.cos(a), Math.sin(a));
  }
  
  public void dynamicsEvent(int eventID){
    if(eventID == ObjectDynamics.EVENT_DYNAMICS_EVOLVED){
      updateGeometry(false,true);
    }
  }
  
  public void updateDevelopment(){
    updateGeometry(true,true);
  }
  
  private synchronized void updateGeometry(boolean dev, boolean obj){
    for(DevelopmentView dv : devViewers){ dv.updateGeometry(dev,obj); } 
  }
  
  private void initializeNewManifold(){
    for(DevelopmentView dv : devViewers){ dv.initializeNewManifold(); } 
  }
  
  /*
   * loads triangulated surface from file given by filename. Chooses an arbitrary source face
   * and source point in that face. Constructs a new Development if one hasn't already been
   * made. Otherwise rebuilds development with new surface.
   */
  private void loadSurface(String file) {
    filename = file;
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
      development = new Development(new ManifoldPosition(sourceFace, sourcePoint), currentDepth, objectRadius);
    else development.rebuild(new ManifoldPosition(sourceFace, sourcePoint), currentDepth);

  }
  
  TitledBorder depthBorder = BorderFactory.createTitledBorder("Recursion Depth (" + currentDepth + ")");
  TitledBorder pointBorder = BorderFactory.createTitledBorder("Object Radius (" + objectRadius + ")");
  TitledBorder speedBorder = BorderFactory.createTitledBorder("Speed (" + objectSpeed + ")");

  private void layoutGUI() {
    this.setSize(220, 470);
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

    JPanel sliderPanel = new JPanel();
    sliderPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
    sliderPanel.setLayout(new BoxLayout(sliderPanel,BoxLayout.Y_AXIS));
    
    // -------- DEPTH SLIDER --------
    JSlider depthSlider = new JSlider(1, MAX_DEPTH, currentDepth);
    depthSlider.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          currentDepth = ((JSlider)e.getSource()).getValue();
          development.setDepth(currentDepth);
          depthBorder.setTitle("Recursion Depth (" + currentDepth + ")");
          initializeNewManifold();
        }
    });
    
    // -------- SPEED SLIDER --------
    JSlider speedSlider = new JSlider(1, MAX_SPEED, (int)(objectSpeed*1000));
    speedSlider.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          objectSpeed = ((JSlider)e.getSource()).getValue()/1000.0;
          for(MovingObject o : movingObjects){ 
            o.setSpeed(objectSpeed); 
          }
          speedBorder.setTitle("Speed (" + objectSpeed + ")");
        }
    }); 
    
    // -------- POINT SIZE SLIDER --------
    JSlider pointSizeSlider = new JSlider(1, MAX_POINT_SIZE, (int)(objectRadius*100.0));
    pointSizeSlider.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e) {
          objectRadius = ((JSlider)e.getSource()).getValue()/100.0;
          development.getSourceObject().getAppearance().setRadius(objectRadius);
          for(MovingObject o : movingObjects){ 
            o.getAppearance().setRadius(objectRadius);
          }
          pointBorder.setTitle("Object Radius (" + objectRadius + ")");
          updateGeometry(false,true);
        }
    }); 
    
    depthSlider.setBorder(depthBorder);
    speedSlider.setBorder(speedBorder);
    pointSizeSlider.setBorder(pointBorder);
    
    sliderPanel.add(depthSlider);
    sliderPanel.add(speedSlider);
    sliderPanel.add(pointSizeSlider);

    // -------- COLOR SCHEME BUTTONS --------
    JPanel colorPanel = new JPanel();
    JButton depthSchemeButton = new JButton("Depth");
    depthSchemeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (colorScheme.getSchemeType() != schemes.DEPTH) {
          colorScheme = new ColorScheme(schemes.DEPTH);
          if(showView2D){ view2D.setColorScheme(colorScheme); }
          if(showView3D){ view3D.setColorScheme(colorScheme); }
        }
      }
    });
    JButton faceSchemeButton = new JButton("Face");
    faceSchemeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (colorScheme.getSchemeType() != schemes.FACE) {
          colorScheme = new ColorScheme(schemes.FACE);
          if(showView2D){ view2D.setColorScheme(colorScheme); }
          if(showView3D){ view3D.setColorScheme(colorScheme); }
        }
      }
    });
    colorPanel.setLayout(new GridLayout(5, 1));
    colorPanel.add(new JLabel("Set Color Scheme"));
    colorPanel.add(depthSchemeButton);
    colorPanel.add(faceSchemeButton);
    
    // -------- STOP/START MOVEMENT BUTTON --------
    JButton stopStartButton = new JButton("");
    if(INITIAL_MOVEMENT_STATUS){ stopStartButton.setText("Stop"); }
    else{ stopStartButton.setText("Start"); }
    stopStartButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JButton source = (JButton)e.getSource();
        if (source.getText().equals("Stop")) {
          source.setText("Start");
          dynamics.stop();
        }else{
          source.setText("Stop");
          dynamics.start();
        }
      }
    });
    colorPanel.add(stopStartButton);

    // -------- VIEWER PANEL --------
    JPanel viewerPanel = new JPanel();
    viewerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    
    JCheckBox showEmbeddedBox = new JCheckBox("Show embedded view");
    showEmbeddedBox.setSelected(showEmbedded);
    showEmbeddedBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showEmbedded = ((JCheckBox)e.getSource()).isSelected();
        if(showEmbedded == true){
          //start up the embedded viewer
          embeddedView = new DevelopmentViewEmbedded(development, colorScheme);
          embeddedView.setDrawEdges(drawEdges);
          embeddedView.setDrawFaces(drawFaces);
          embeddedView.updateGeometry(true,true);
          embeddedView.initializeNewManifold();
          devViewers.add(embeddedView);
        }else{
          //end the embedded  viewer
          devViewers.remove(embeddedView);
          embeddedView.dispose();
        }
      }
    });
    
    JCheckBox showView2DBox = new JCheckBox("Show 2D view");
    showView2DBox.setSelected(showView2D);
    showView2DBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showView2D = ((JCheckBox)e.getSource()).isSelected();
        if(showView2D == true){
          //start up the embedded viewer
          view2D = new DevelopmentView2D(development, colorScheme);
          view2D.setDrawEdges(drawEdges);
          view2D.setDrawFaces(drawFaces);
          view2D.updateGeometry(true,true);
          view2D.initializeNewManifold();
          devViewers.add(view2D);
        }else{
          //end the embedded  viewer
          devViewers.remove(view2D);
          view2D.dispose();
        }
      }
    });
    
    JCheckBox showView3DBox = new JCheckBox("Show 3D view");
    showView3DBox.setSelected(showView3D);
    showView3DBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showView3D = ((JCheckBox)e.getSource()).isSelected();
        if(showView3D == true){
          //start up the embedded viewer
          view3D = new DevelopmentView3D(development, colorScheme);
          view3D.setDrawEdges(drawEdges);
          view3D.setDrawFaces(drawFaces);
          view3D.updateGeometry(true,true);
          view3D.initializeNewManifold();
          devViewers.add(view3D);
        }else{
          //end the embedded  viewer
          devViewers.remove(view3D);
          view3D.dispose();
        }
      }
    });
    
    viewerPanel.setLayout(new BoxLayout(viewerPanel, BoxLayout.Y_AXIS));
    viewerPanel.add(showView2DBox);
    viewerPanel.add(showView3DBox);
    viewerPanel.add(showEmbeddedBox);
    
    // -------- DRAW OPTIONS PANEL --------
    JPanel drawOptionsPanel = new JPanel();
    drawOptionsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    
    JCheckBox drawEdgesBox = new JCheckBox("Draw edges");
    drawEdgesBox.setSelected(true);
    drawEdgesBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        drawEdges = ((JCheckBox)e.getSource()).isSelected();
        if(showView2D){ view2D.setDrawEdges(drawEdges); }
        if(showView3D){ view3D.setDrawEdges(drawEdges); }
      }
    });
    
    JCheckBox drawFacesBox = new JCheckBox("Draw faces");
    drawFacesBox.setSelected(true);
    drawFacesBox.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        drawFaces = ((JCheckBox)e.getSource()).isSelected();
        if(showView2D){ view2D.setDrawFaces(drawFaces); }
        if(showView3D){ view3D.setDrawFaces(drawFaces); }
      }
    });
    
    drawOptionsPanel.setLayout(new BoxLayout(drawOptionsPanel, BoxLayout.Y_AXIS));
    drawOptionsPanel.add(drawEdgesBox);
    drawOptionsPanel.add(drawFacesBox);
    
    // -------- ADD PANELS --------
    this.setLayout(new FlowLayout());
    this.add(sliderPanel);
    this.add(colorPanel);
    this.add(viewerPanel);
    this.add(drawOptionsPanel);
  }
}
