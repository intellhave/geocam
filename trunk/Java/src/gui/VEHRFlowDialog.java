package gui;

import geoquant.GeoRecorder;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

import solvers.newtonsMethod.EtaVEHRNewton;
import solvers.newtonsMethod.EtaVEHRGradient;
import solvers.newtonsMethod.RadiusVEHRNewton;
import solvers.newtonsMethod.RadiusVEHRGradient;
import solvers.newtonsMethod.WrongDirectionException;



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
public class VEHRFlowDialog extends EHRFlowDialog {
  
  public VEHRFlowDialog(GeoquantViewer owner) {
    super(owner);
    this.setTitle("VEHR Flow");
  }
  
  protected void handleRun(ActionEvent evt) {
    if(evt.getSource().equals(getRunButton())) {
      this.setVisible(false);
      if(getNmButton().isSelected()) {
        
        if(getRadFlowButton().isSelected()) {
          RadiusVEHRNewton radNM = new RadiusVEHRNewton();
          GeoRecorder rec = owner.getRecorder();
          rec.update(radNM, null);
          radNM.addObserver(rec);
          double[] log_radii = radNM.getLogRadii();
          if(getMaxFlowButton().isSelected()) {
            try {
              radNM.setLogRadii(radNM.maximize(log_radii));
            } catch (WrongDirectionException e) {
            }
          } else if(getMinFlowButton().isSelected()) {
            try {
              radNM.setLogRadii(radNM.minimize(log_radii));
            } catch (WrongDirectionException e) {
            }
          } else {
            radNM.setLogRadii(radNM.optimize(log_radii));
          }
          radNM.deleteObserver(rec);
          owner.getPolygonPanel().setRecorder(rec);
          owner.newFlow();
          owner.getPolygonPanel().repaint();
        } else {
          EtaVEHRNewton etaNM = new EtaVEHRNewton();
          GeoRecorder rec = owner.getRecorder();
          rec.update(etaNM, null);
          etaNM.addObserver(rec);
          double[] etas = etaNM.getEtas();

          if(getMaxFlowButton().isSelected()) {
            try {
              etaNM.setEtas(etaNM.maximize(etas));
            } catch (WrongDirectionException e) {
            }
          } else if(getMinFlowButton().isSelected()) {
            try {
              etaNM.setEtas(etaNM.minimize(etas));
            } catch (WrongDirectionException e) {
            }
          } else {
            for(int i = 0; i < 10; i++) {
              etaNM.setEtas(etaNM.optimize(etas));
              double max = 0;
              for(int j = 0; j < etas.length; j++) {
                if(max < etas[j]) {
                  max = etas[j];
                }
              }
              for(int j = 0; j < etas.length; j++) {
                etas[j] = etas[j] / max;
              }
            }
            double max = 0;
            for(int j = 0; j < etas.length; j++) {
              if(max < etas[j]) {
                max = etas[j];
              }
            }
            for(int j = 0; j < etas.length; j++) {
              etas[j] = etas[j] / max;
            }
            etaNM.setEtas(etas);
          }
          etaNM.deleteObserver(rec);
          owner.getPolygonPanel().setRecorder(rec);
          owner.newFlow();
          owner.getPolygonPanel().repaint();
        }
      } else if(getGradientButton().isSelected()) {
        if(getRadFlowButton().isSelected()) {
          RadiusVEHRGradient radNM = new RadiusVEHRGradient();
          GeoRecorder rec = owner.getRecorder();
          rec.update(radNM, null);
          radNM.addObserver(rec);
          double[] log_radii = radNM.getLogRadii();
          if(getMaxFlowButton().isSelected()) {
            try {
              radNM.setLogRadii(radNM.maximize(log_radii));
            } catch (WrongDirectionException e) {
            }
          } else if(getMinFlowButton().isSelected()) {
            try {
              radNM.setLogRadii(radNM.minimize(log_radii));
            } catch (WrongDirectionException e) {
            }
          } else {
            radNM.setLogRadii(radNM.optimize(log_radii));
          }
          radNM.deleteObserver(rec);
          owner.getPolygonPanel().setRecorder(rec);
          owner.newFlow();
          owner.getPolygonPanel().repaint();
        } else {
          EtaVEHRGradient etaNM = new EtaVEHRGradient();
          GeoRecorder rec = owner.getRecorder();
          rec.update(etaNM, null);
          etaNM.addObserver(rec);
          double[] etas = etaNM.getEtas();

          if(getMaxFlowButton().isSelected()) {
            try {
              etaNM.setEtas(etaNM.maximize(etas));
            } catch (WrongDirectionException e) {
            }
          } else if(getMinFlowButton().isSelected()) {
            try {
              etaNM.setEtas(etaNM.minimize(etas));
            } catch (WrongDirectionException e) {
            }
          } else {
            for(int i = 0; i < 10; i++) {
              etaNM.setEtas(etaNM.optimize(etas));
              double max = 0;
              for(int j = 0; j < etas.length; j++) {
                if(max < etas[j]) {
                  max = etas[j];
                }
              }
              for(int j = 0; j < etas.length; j++) {
                etas[j] = etas[j] / max;
              }
            }
            double max = 0;
            for(int j = 0; j < etas.length; j++) {
              if(max < etas[j]) {
                max = etas[j];
              }
            }
            for(int j = 0; j < etas.length; j++) {
              etas[j] = etas[j] / max;
            }
            etaNM.setEtas(etas);
          }
          etaNM.deleteObserver(rec);
          owner.getPolygonPanel().setRecorder(rec);
          owner.newFlow();
          owner.getPolygonPanel().repaint();
        }
      }
    }
    this.dispose();
  }

}
