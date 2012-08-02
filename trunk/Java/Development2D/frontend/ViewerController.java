package frontend;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
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

import marker.ForwardGeodesic;
import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import development.Development;
import development.ManifoldPosition;
import development.Vector;

public class ViewerController extends JFrame {

  private static final long serialVersionUID = 1L;

  /********************************************************************************
   * Viewer Controller Data
   * 
   * Data related to the model
   ********************************************************************************/
  private Development develop;
  private static MarkerHandler markerHandler;
  private static Marker source;
  private static ForwardGeodesic geo;

  public ViewerController(MarkerHandler mh, Development d, ForwardGeodesic geo) {
    develop = d;
    markerHandler = mh;
    source = markerHandler.getSourceMarker();
    this.geo = geo;
    layoutGUI();
    DevelopmentUI.setExponentialView(showView2DBox.isSelected());
    DevelopmentUI.setEmbeddedView(showEmbeddedBox.isSelected());
    DevelopmentUI.setFirstPersonView(showView3DBox.isSelected());
  }

  /********************************************************************************
   * JFrame Data
   * 
   * Data required for laying out the window
   ********************************************************************************/
  private JMenuBar menuBar;
  private JMenu file;
  private JMenuItem cube;
  private JMenuItem dodec;
  private JMenuItem tetra;
  private JMenuItem neckpinch;  
  private JMenuItem cone;
  private JMenuItem icosa;
  private JMenuItem saddle;
  private JPanel textBoxPanel;
  private JPanel recDepthPanel;
  private JFormattedTextField recDepth;
  private JLabel recDepthLabel;
  private JPanel numObjectsPanel;
  private JFormattedTextField numObjects;
  private JLabel numObjectsLabel;
  private JPanel sliderPanel;
  private JSlider speedSlider;
  private JSlider scalingSlider;
  private JSlider geoSlider;
  private JPanel buttonPanel;
  private JButton stopStartButton;
  private JButton clearGeosButton;
  private JPanel viewerPanel;
  private JCheckBox showView2DBox;
  private JCheckBox showView3DBox;
  private JCheckBox showEmbeddedBox;
  private JPanel drawOptionsPanel;
  private JCheckBox drawEdgesBox;
  private JCheckBox drawFacesBox;
  private JCheckBox drawAvatarBox;
  private JCheckBox textureEnabledBox;

  private static int MAX_SPEED = 4000;
  private static int MAX_SIZE = 10;

  TitledBorder depthBorder = BorderFactory.createTitledBorder("");
  TitledBorder pointBorder = BorderFactory.createTitledBorder("");
  TitledBorder speedBorder = BorderFactory.createTitledBorder("");
  TitledBorder objectsBorder = BorderFactory.createTitledBorder("");
  TitledBorder geoBorder = BorderFactory.createTitledBorder("");

  private void layoutGUI() {

    this.setSize(220, 520);
    this.setResizable(false);
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setTitle("Development View");
    this.setLayout(new FlowLayout());

    menuBar = new JMenuBar();
    this.setJMenuBar(menuBar);
    {
      file = new JMenu();
      menuBar.add(file);
      file.setText("File");
      {
        cube = new JMenuItem();
        dodec = new JMenuItem();
        tetra = new JMenuItem();
        neckpinch = new JMenuItem();
        cone = new JMenuItem();
        icosa = new JMenuItem();
        saddle = new JMenuItem();
        
        file.add(cube);
        file.add(dodec);
        file.add(tetra);
        file.add(neckpinch);
        file.add(cone);
        file.add(icosa);
        file.add(saddle);
        
        cube.setText("Cube");
        dodec.setText("Dodecahedron");
        tetra.setText("Tetrahedron");
        neckpinch.setText("Barbell");
        cone.setText("Cone");
        icosa.setText("Icosahedron");
        saddle.setText("Saddle");
        
        cube.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            File file = null;
            try {
              file = new File("Data/surfaces/cube_surf.off");
            } catch (Exception ex) {
              System.out.println("Invalid file");
            }
            DevelopmentUI.loadSurface(file.getAbsolutePath());
            DevelopmentUI.resetView();
            resetViewController();
          } 
        });
        
        dodec.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            File file = null;
            try {
              file = new File("Data/surfaces/dodec2.off");
            } catch (Exception ex) {
              System.out.println("Invalid file");
            }
            DevelopmentUI.loadSurface(file.getAbsolutePath());
            DevelopmentUI.resetView();
            resetViewController();
          } 
        });
        
        tetra.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            File file = null;
            try {
              file = new File("Data/surfaces/tetra2.off");
            } catch (Exception ex) {
              System.out.println("Invalid file");
            }
            DevelopmentUI.loadSurface(file.getAbsolutePath());
            DevelopmentUI.resetView();
            resetViewController();
          } 
        });
        
        neckpinch.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            File file = null;
            try {
              file = new File("Data/surfaces/neckpinch.off");
            } catch (Exception ex) {
              System.out.println("Invalid file");
            }
            DevelopmentUI.loadSurface(file.getAbsolutePath());
            DevelopmentUI.resetView();
            resetViewController();
          } 
        });
        
        cone.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            File file = null;
            try {
              file = new File("Data/surfaces/scaledCone.off");
            } catch (Exception ex) {
              System.out.println("Invalid file");
            }
            DevelopmentUI.loadSurface(file.getAbsolutePath());
            DevelopmentUI.resetView();
            resetViewController();
          } 
        });
        
        icosa.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            File file = null;
            try {
              file = new File("Data/off/icosa.off");
            } catch (Exception ex) {
              System.out.println("Invalid file");
            }
            DevelopmentUI.loadSurface(file.getAbsolutePath());
            DevelopmentUI.resetView();
            resetViewController();
          } 
        });
        
        saddle.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent arg0) {
            File file = null;
            try {
              file = new File("Data/off/saddle.off");
            } catch (Exception ex) {
              System.out.println("Invalid file");
            }
            DevelopmentUI.loadSurface(file.getAbsolutePath());
            DevelopmentUI.resetView();
            resetViewController();
          } 
        });
        
      }
    }

    // ******************************TEXT BOX PANEL********************************
    textBoxPanel = new JPanel();
    getContentPane().add(textBoxPanel);
    textBoxPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    textBoxPanel.setLayout(new GridLayout(2, 1));

    // ****************************RECURSION DEPTH TEXT BOX******************************
    // ******************************NUM OBJECTS TEXT BOX********************************
    {
      NumberFormat f = NumberFormat.getIntegerInstance();
      recDepth = new JFormattedTextField(f);
      recDepth.setColumns(3);
      recDepth.setValue(develop.getDepth());
      recDepthLabel = new JLabel("Recursion Depth");
      recDepthPanel = new JPanel();
      recDepthPanel.setLayout(new FlowLayout());
      recDepthPanel.add(recDepthLabel);
      recDepthPanel.add(recDepth);
      textBoxPanel.add(recDepthPanel);

      numObjects = new JFormattedTextField(f);
      numObjects.setColumns(3);
      int current = markerHandler.getAllMarkers().size() - 1;
      numObjects.setValue(current);
      numObjectsLabel = new JLabel("Number of objects");
      numObjectsPanel = new JPanel();
      numObjectsPanel.setLayout(new FlowLayout());
      numObjectsPanel.add(numObjectsLabel);
      numObjectsPanel.add(numObjects);
      textBoxPanel.add(numObjectsPanel);

      // Recursion depth action listener
      ActionListener recDepthListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String value = recDepth.getValue().toString();
          int newDepth = Integer.parseInt(value);
          if (newDepth < 1) {
            newDepth = 1;
          } else if (newDepth > 20) {
            newDepth = 20;
          }
          develop.setDepth(newDepth);
        }
      };
      recDepth.addActionListener(recDepthListener);

      // Number of objects action listener
      ActionListener numObjectsListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String value = numObjects.getValue().toString();
          int newMarkers = Integer.parseInt(value);
          if (newMarkers < 0) {
            newMarkers = 0;
          } else if (newMarkers > 20) {
            newMarkers = 20;
          }

          Set<Marker> markers = markerHandler.getAllMarkers();
          //get the current number of moving markers
          int currentMarkers = 0;
          for( Marker m : markers) {
            if(m.getMarkerType() == Marker.MarkerType.MOVING){
              currentMarkers++;
            }
          }

          // if necessary, add moving markers
          if (currentMarkers < newMarkers) {
            Random rand = new Random();
            ManifoldPosition pos;
            MarkerAppearance app;

            for (int ii = 0; ii < newMarkers - currentMarkers; ii++) {
              pos = new ManifoldPosition(develop.getSource());
              double scale = scalingSlider.getValue() / 10.0;
              app = new MarkerAppearance(source.getAppearance().getModelType(), scale);
              double a = rand.nextDouble() * Math.PI * 2;
              Vector vel = new Vector(Math.cos(a), Math.sin(a));
              // advance ants off of source point
              vel.scale(0.25);
              pos.move(vel);
              Marker m = new Marker(pos, app, Marker.MarkerType.MOVING, vel);
              double sliderSpeed = speedSlider.getValue() / 1000.0;
              m.setSpeed(0.05 * Math.pow(Math.E, sliderSpeed));

              markerHandler.addMarker(m);
            }
          }

          // if necessary, remove moving markers (make sure not to remove geodesic 
          // markers or the source marker)
          if (currentMarkers > newMarkers) {
            int counter = 0;            
            for (Marker m : markers) {
              if (m.getMarkerType() == Marker.MarkerType.MOVING){
                m.flagForRemoval();
                counter++;
              }
              if (counter == currentMarkers - newMarkers) break;
            }
          }
        }
      };
      numObjects.addActionListener(numObjectsListener);
    }

    // ********************************SLIDER PANEL**********************************
    sliderPanel = new JPanel();
    getContentPane().add(sliderPanel);
    sliderPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

    // ******************************SPEED SLIDER********************************
    /********************************************************************************
     * Note: Speed slider is using the exponential function y = 0.05*e^x to
     * convert input from the slider to a speed. This gives you more control
     * over low speeds on the slider.
     ********************************************************************************/
    {
      speedSlider = new JSlider();
      sliderPanel.add(speedSlider);
      speedSlider.setMaximum(MAX_SPEED);
      double speed = markerHandler.getMarkerSpeed(source);
      double speedToSet = Math.log(speed / 0.05);
      speedSlider.setValue((int) (speedToSet * 1000.0));

      final DecimalFormat speedFormat = new DecimalFormat("0.00");
      speedSlider.setBorder(speedBorder);
      speedBorder.setTitle("Speed (" + speedFormat.format(speed) + ")");

      // Speed slider action listener
      ChangeListener speedSliderListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          double sliderValue = speedSlider.getValue() / 1000.0;
          double newSpeed = 0.05 * Math.pow(Math.E, sliderValue);
          Set<Marker> allMarkers = markerHandler.getAllMarkers();          
          for(Marker m : allMarkers){
            if( m.getMarkerType() == Marker.MarkerType.MOVING){
              m.setSpeed(newSpeed);
            }
          }
          speedBorder.setTitle("Speed (" + speedFormat.format(newSpeed) + ")");
        }
      };
      speedSlider.addChangeListener(speedSliderListener);
    }

    // ******************************SCALE SLIDER********************************
    {
      scalingSlider = new JSlider();
      sliderPanel.add(scalingSlider);
      scalingSlider.setMaximum(MAX_SIZE);
      scalingSlider.setValue((int) (markerHandler.getMarkerScale(source) * 10));
      scalingSlider.setBorder(pointBorder);
      pointBorder.setTitle("Object scaling (" + scalingSlider.getValue() / 10.0 + ")");

      // Scaling slider change listener
      ChangeListener scalingSliderListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          double newScale = scalingSlider.getValue() / 10.0;
          Set<Marker> markers = markerHandler.getAllMarkers();
          Iterator<Marker> i = markers.iterator();

          synchronized (markers) {
            while (i.hasNext()) {
              Marker m = i.next();
              if (m.getMarkerType() == Marker.MarkerType.MOVING) {
                MarkerAppearance newAppearance = m.getAppearance();
                newAppearance.setScale(newScale);
                m.setAppearance(newAppearance);
              }
            }
          }
          pointBorder.setTitle("Object scaling (" + newScale + ")");
        }
      };
      scalingSlider.addChangeListener(scalingSliderListener);
    }
    
    // ********************************GEO SLIDER**********************************
    {
      geoSlider = new JSlider();
      sliderPanel.add(geoSlider);
      geoSlider.setMaximum(30);
      geoSlider.setValue ((int) geo.getLength());
      geoSlider.setBorder(geoBorder);
      geoBorder.setTitle("Geodesic length ("+ geoSlider.getValue()+")");
      
      ChangeListener geoSliderListener = new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
          int newLength = geoSlider.getValue();
          geo.setLength(newLength);
         geoBorder.setTitle("Geodesic length (" + newLength + ")");
        }
     
      };
      geoSlider.addChangeListener(geoSliderListener);
    }

    // ********************************BUTTON PANEL**********************************
    buttonPanel = new JPanel();
    getContentPane().add(buttonPanel);
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    
    //***************************CLEAR GEODESICS BUTTON***************************
    {
      clearGeosButton = new JButton();
      clearGeosButton.setText("Clear geodesics");
      buttonPanel.add(clearGeosButton);
      
      ActionListener geosButtonListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          Set<Marker> allMarkers = markerHandler.getAllMarkers();
          for(Marker m : allMarkers) {
            if(m.getMarkerType() == Marker.MarkerType.FIXED)
              m.flagForRemoval();
          }
        }
      };
      clearGeosButton.addActionListener(geosButtonListener);
      
    }
    // ****************************START/STOP BUTTON******************************
    {
      stopStartButton = new JButton();
      stopStartButton.setText("Stop");
      buttonPanel.add(stopStartButton);
      
      ActionListener stopStartButtonListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          if (stopStartButton.getText().equals("Stop")) {
            markerHandler.pauseSimulation();
            stopStartButton.setText("Start");
          } else {
            markerHandler.unpauseSimulation();
            stopStartButton.setText("Stop");
          }
        }
      };
      stopStartButton.addActionListener(stopStartButtonListener);
    }
    // ********************************VIEW PANEL**********************************
    viewerPanel = new JPanel();
    getContentPane().add(viewerPanel);
    viewerPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    viewerPanel.setLayout(new BoxLayout(viewerPanel, BoxLayout.Y_AXIS));

    // ***************************2D VIEW CHECK BOX*****************************
    {
      showView2DBox = new JCheckBox();
      viewerPanel.add(showView2DBox);
      showView2DBox.setText("Show Exponential View");
      showView2DBox.setSelected(true);
      
      ActionListener view2DListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean checkBox = showView2DBox.isSelected();
          DevelopmentUI.setExponentialView(checkBox);
          DevelopmentUI.setTexture(textureEnabledBox.isSelected());
        }
      };
      showView2DBox.addActionListener(view2DListener);    
    }
    // ***************************3D VIEW CHECK BOX*****************************
    {
      showView3DBox = new JCheckBox();
      viewerPanel.add(showView3DBox);
      showView3DBox.setText("Show First Person View");
      showView3DBox.setSelected(true);
      ActionListener view3DListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean checkBox = showView3DBox.isSelected();
          DevelopmentUI.setFirstPersonView(checkBox);
          DevelopmentUI.setTexture(textureEnabledBox.isSelected());
        }
      };
      showView3DBox.addActionListener(view3DListener);
    }
    // ************************EMBEDDED VIEW CHECK BOX**************************
    {
      showEmbeddedBox = new JCheckBox();
      viewerPanel.add(showEmbeddedBox);
      showEmbeddedBox.setText("Show Embedded View");
      showEmbeddedBox.setSelected(true);
      ActionListener viewEmbeddedListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean checkBox = showEmbeddedBox.isSelected();
          DevelopmentUI.setEmbeddedView(checkBox);
          DevelopmentUI.setTexture(textureEnabledBox.isSelected());
        }
      };
      showEmbeddedBox.addActionListener(viewEmbeddedListener);
    }
 // ************************TEXTURE ENABLED CHECK BOX**************************
    {
      textureEnabledBox = new JCheckBox();
      viewerPanel.add(textureEnabledBox);
      textureEnabledBox.setText("Display Texture");
      textureEnabledBox.setSelected(true);
      ActionListener TextureEnabledListener = new ActionListener(){
        public void actionPerformed(ActionEvent arg0) {
          boolean textureEnabled = textureEnabledBox.isSelected();
          DevelopmentUI.setTexture(textureEnabled);
        } 
    };
    textureEnabledBox.addActionListener(TextureEnabledListener);
    }
    // ******************************DRAW OPTIONS PANEL********************************
    drawOptionsPanel = new JPanel();
    getContentPane().add(drawOptionsPanel);
    drawOptionsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
    drawOptionsPanel.setLayout(new BoxLayout(drawOptionsPanel, BoxLayout.Y_AXIS));

    // ***************************DRAW EDGES CHECK BOX*****************************
    {
      drawEdgesBox = new JCheckBox();
      drawOptionsPanel.add(drawEdgesBox);
      drawEdgesBox.setText("Draw edges");
      drawEdgesBox.setSelected(true);

      ActionListener drawEdgesListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean boxChecked = drawEdgesBox.isSelected();
          DevelopmentUI.setDrawEdges(boxChecked);
        }
      };
      drawEdgesBox.addActionListener(drawEdgesListener);
    }
    // ***************************DRAW FACES CHECK BOX*****************************
    {
      drawFacesBox = new JCheckBox();
      drawOptionsPanel.add(drawFacesBox);
      drawFacesBox.setText("Draw faces");
      drawFacesBox.setSelected(true);

      ActionListener drawFacesListener = new ActionListener() {
        public void actionPerformed(ActionEvent arg0) {
          boolean boxChecked = drawFacesBox.isSelected();
          DevelopmentUI.setDrawFaces(boxChecked);
        }
      };
      drawFacesBox.addActionListener(drawFacesListener);
    }
    // ***************************DRAW AVATAR CHECK BOX*****************************
    {
      drawAvatarBox = new JCheckBox();
      drawOptionsPanel.add(drawAvatarBox);
      drawAvatarBox.setText("Draw avatar");
      drawAvatarBox.setSelected(true);

      // Draw avatar action listener
      ActionListener drawAvatar = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          boolean showAvatar = drawAvatarBox.isSelected();
          if (showAvatar) {
            source.setVisible(true);
          } else {
            source.setVisible(false);
          }
        }
      };
      drawAvatarBox.addActionListener(drawAvatar);
    }
  }

  private void resetViewController() {
    source = markerHandler.getSourceMarker();
    numObjects.setValue(markerHandler.getAllMarkers().size() - 1);
    recDepth.setValue(develop.getDepth());
    double speed = markerHandler.getMarkerSpeed(source);
    double speedToSet = Math.log(speed / 0.05);
    speedSlider.setValue((int) (speedToSet * 1000.0));
    scalingSlider.setValue((int) (markerHandler.getMarkerScale(source) * 10));
    stopStartButton.setText("Stop");
    showView2DBox.setSelected(true);
    showView3DBox.setSelected(true);
    showEmbeddedBox.setSelected(true);
    drawEdgesBox.setSelected(true);
    drawAvatarBox.setSelected(true);
    drawFacesBox.setSelected(true);
    System.out.println(markerHandler.getAllMarkers().size() - 1);
  }

  public void setMarkerHandler(MarkerHandler mh) {
    markerHandler = mh;
  }
  public void setGeodesics(ForwardGeodesic geo){
    this.geo = geo;
  }
}
