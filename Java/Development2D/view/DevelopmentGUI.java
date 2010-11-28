package view;

import java.awt.Dimension;
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

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
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

  private int maxDepth = 23;
  private int currentDepth = maxDepth;

  private static Development development;
  private static Vector sourcePoint;
  private static Face sourceFace;
  private static ColorScheme colorScheme;
  private static DevelopmentView2D view2D;
  private static DevelopmentView3D view3D;

  private JPanel sliderPanel;
  private JPanel movementPanel;
  private JSlider depthSlider;
  private JLabel depthLabel = new JLabel("Max Recursion Depth (" + currentDepth
      + ")");
  private JPanel colorPanel;

  // Movement stuff
  private Timer timer;
  private Timer keyHoldTimer;
  private Vector movementDirection = new Vector(1, 0);
  private static final double ROTATION_ANGLE = Math.PI / 90;

  private enum movements {
    left, right, forward, back
  };

  private movements curMovement;

  public DevelopmentGUI() {
    colorScheme = new ColorScheme(schemes.DEPTH);

    development = null;
    loadSurface("surfaces/cone.off");

    System.out.println("Initializing 2D view");
    view2D = new DevelopmentView2D(development, colorScheme);
    System.out.println("done");
    development.addObserver(view2D);

    System.out.println("Initializing 3D view");
    view3D = new DevelopmentView3D(development, colorScheme);
    System.out.println("done");
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
  }

  private void loadSurface(String filename) {
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
      development = new Development(sourceFace, sourcePoint, maxDepth,
          currentDepth);
    else
      development.rebuild(sourceFace, sourcePoint, maxDepth, currentDepth);
  }

  private void layoutGUI() {
    this.setSize(220, 200);
    this.setResizable(true);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setTitle("Development View");

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
    sliderPanel.setLayout(new GridLayout(2, 1));
    depthSlider = new JSlider(1, maxDepth, currentDepth);
    depthSlider.setMaximumSize(new Dimension(400, 100));
    depthSlider.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int val = ((JSlider) e.getSource()).getValue();
        currentDepth = val;
        depthLabel.setText("Recursion Depth (" + currentDepth + ")");
        development.setDesiredDepth(currentDepth);
      }
    });
    sliderPanel.add(depthLabel);
    sliderPanel.add(depthSlider);

    colorPanel = new JPanel();
    JButton depthSchemeButton = new JButton("Depth");
    depthSchemeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (colorScheme.getSchemeType() != schemes.DEPTH) {
          colorScheme = new ColorScheme(schemes.DEPTH);
          view2D.setColorScheme(colorScheme);
          view3D.setColorScheme(colorScheme);
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
        }
      }
    });
    colorPanel.setLayout(new GridLayout(3, 1));
    colorPanel.add(new JLabel("Set Color Scheme"));
    colorPanel.add(depthSchemeButton);
    colorPanel.add(faceSchemeButton);

    this.setLayout(new FlowLayout());
    this.add(sliderPanel);
    this.add(colorPanel);

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

  public class Moving implements ActionListener {
    public void actionPerformed(ActionEvent e) {
      if (curMovement == movements.right) {
        rotateMovementDirection(ROTATION_ANGLE);
        view2D.rotate(ROTATION_ANGLE);
        view3D.rotate(ROTATION_ANGLE);
      } else if (curMovement == movements.left) {
        rotateMovementDirection(-ROTATION_ANGLE);
        view2D.rotate(-ROTATION_ANGLE);
        view3D.rotate(-ROTATION_ANGLE);
      } else if (curMovement == movements.forward) {
        development.translateSourcePoint(movementDirection);
      } else if (curMovement == movements.back) {
        Vector v = new Vector(movementDirection);
        v.scale(-1);
        development.translateSourcePoint(v);
      }
    }
  }

  private void rotateMovementDirection(double angle) {
    double cos = Math.cos(-angle);
    double sin = Math.sin(-angle);
    double x = movementDirection.getComponent(0);
    double y = movementDirection.getComponent(1);

    double x_new = cos * x - sin * y;
    double y_new = sin * x + cos * y;
    movementDirection = new Vector(x_new, y_new);
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
