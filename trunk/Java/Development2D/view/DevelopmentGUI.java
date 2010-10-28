package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import development.EmbeddedTriangulation;
import development.Vector;

public class DevelopmentGUI extends JFrame implements KeyListener {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

  public static void main(String[] args) {
    JFrame window = new DevelopmentGUI();
    window.setVisible(true);
  }

  private int maxDepth = 25;
  private int currentDepth = maxDepth;

  private static Development development;
  private static Vector sourcePoint;
  private static Face sourceFace;
  private static ColorScheme colorScheme;
  private static DevelopmentView2D view2D;
  private static DevelopmentView3D view3D;

  private JPanel sliderPanel;
  private JSlider depthSlider;
  private JLabel depthLabel = new JLabel("Max Recursion Depth (" + currentDepth
      + ")");
  private JPanel colorPanel;

  // Movement stuff
  private Timer timer;
  private boolean moving = false;
  private Vector movementDirection = new Vector(1,0);
  private static final double ROTATION_ANGLE = Math.PI / 90;
  private static final double STEP_SIZE = 0.2;

  private enum movements {
    left, right, forward, back
  };

  private movements curMovement;

  public DevelopmentGUI() {
    EmbeddedTriangulation.readEmbeddedSurface("models/cone.off");

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
//    Vector v = new Vector(movementDirection);
//    v.scale(.2);
//    sourcePoint.add(v);

    colorScheme = new ColorScheme(schemes.DEPTH);
    development = new Development(sourceFace, sourcePoint, maxDepth,
        currentDepth);
    view2D = new DevelopmentView2D(development, colorScheme);
    development.addObserver(view2D);

    view3D = new DevelopmentView3D(development, colorScheme);
    development.addObserver(view3D);

    timer = new Timer(50, null);
    timer.addActionListener(new Moving());

    layoutGUI();
  }

  private void layoutGUI() {
    this.setSize(220, 200);
    this.setResizable(true);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    // this.setLayout(new BorderLayout());

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
    ;
    colorPanel.add(new JLabel("Set Color Scheme"));
    colorPanel.add(depthSchemeButton);
    colorPanel.add(faceSchemeButton);

    this.setLayout(new FlowLayout());
    this.add(sliderPanel);
    this.add(colorPanel);

    this.addKeyListener(this);
    this.setFocusable(true);
    this.requestFocus();
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
      } else if( curMovement == movements.forward) {
        Vector v = new Vector(movementDirection);
        v.scale(STEP_SIZE);
        sourcePoint.add(v);
        development.setSourcePoint(sourcePoint);
        timer.stop();
      }
    else if( curMovement == movements.back) {
      Vector v = new Vector(movementDirection);
      v.scale(-STEP_SIZE);
      sourcePoint.add(v);
      development.setSourcePoint(sourcePoint);
      timer.stop();
    }
    }
  }
  
  private void rotateMovementDirection(double angle) {
    double cos = Math.cos(-angle);
    double sin = Math.sin(-angle);
    double x = movementDirection.getComponent(0);
    double y = movementDirection.getComponent(1);
    
    double x_new = cos*x - sin*y;
    double y_new = sin*x + cos*y;
    movementDirection = new Vector(x_new, y_new);
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
      curMovement = movements.right;
    else if (e.getKeyCode() == KeyEvent.VK_LEFT)
      curMovement = movements.left;
    else if (e.getKeyCode() == KeyEvent.VK_UP)
      curMovement = movements.forward;
    else if (e.getKeyCode() == KeyEvent.VK_DOWN)
      curMovement = movements.back;
    else
      return;
    if (moving == false) {
      moving = true;
      timer.start();
    } else {
      moving = false;
      timer.stop();
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

}
