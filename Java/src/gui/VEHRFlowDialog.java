package gui;

import geoquant.GeoRecorder;

import java.awt.event.ActionEvent;

import solvers.implemented.EtaVEHRGradient;
import solvers.implemented.EtaVEHRNewton;
import solvers.implemented.RadiusVEHRGradient;
import solvers.implemented.RadiusVEHRNewton;



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
          try{
            radNM.setStepsize(Double.parseDouble(getStepsizeTextField().getText()));
            if(getPrecisionButton().isSelected()) {
              radNM.setStoppingCondition(Double.parseDouble(getPrecisionTextField().getText()));
              radNM.setLogRadii(radNM.run(log_radii));
            } else {
              radNM.setLogRadii(radNM.run(log_radii, Integer.parseInt(getNumStepsTextField().getText())));
            }

            radNM.deleteObserver(rec);
            owner.getPolygonPanel().setRecorder(rec);
            owner.newFlow();
            owner.getPolygonPanel().repaint();
          } catch(NumberFormatException ex) {
          }
        } else {
          EtaVEHRNewton etaNM = new EtaVEHRNewton();
          GeoRecorder rec = owner.getRecorder();
          rec.update(etaNM, null);
          etaNM.addObserver(rec);
          double[] etas = etaNM.getEtas();

          try{
            etaNM.setStepsize(Double.parseDouble(getStepsizeTextField().getText()));
            if(getPrecisionButton().isSelected()) {
              etaNM.setStoppingCondition(Double.parseDouble(getPrecisionTextField().getText()));
              etaNM.setEtas(etaNM.run(etas));
            } else {
              etaNM.setEtas(etaNM.run(etas, Integer.parseInt(getNumStepsTextField().getText())));
            }

            etaNM.deleteObserver(rec);
            owner.getPolygonPanel().setRecorder(rec);
            owner.newFlow();
            owner.getPolygonPanel().repaint();
          } catch(NumberFormatException ex) {
          }
        }
      } else if(getGradientButton().isSelected()) {
        if(getRadFlowButton().isSelected()) {
          RadiusVEHRGradient radNM = new RadiusVEHRGradient();
          GeoRecorder rec = owner.getRecorder();
          rec.update(radNM, null);

          double[] log_radii = radNM.getLogRadii();
          try{
            radNM.setStepsize(Double.parseDouble(getStepsizeTextField().getText()));
            if(getPrecisionButton().isSelected()) {
              radNM.setStoppingCondition(Double.parseDouble(getPrecisionTextField().getText()));
              radNM.setLogRadii(radNM.run(log_radii));
            } else {
              radNM.setLogRadii(radNM.run(log_radii, Integer.parseInt(getNumStepsTextField().getText())));
            }

            radNM.deleteObserver(rec);
            owner.getPolygonPanel().setRecorder(rec);
            owner.newFlow();
            owner.getPolygonPanel().repaint();
          } catch(NumberFormatException ex) {
          }
        } else {
          EtaVEHRGradient etaNM = new EtaVEHRGradient();
          GeoRecorder rec = owner.getRecorder();
          rec.update(etaNM, null);
          
          double[] etas = etaNM.getEtas();
          
          try{
            etaNM.setStepsize(Double.parseDouble(getStepsizeTextField().getText()));
            if(getPrecisionButton().isSelected()) {
              etaNM.setStoppingCondition(Double.parseDouble(getPrecisionTextField().getText()));
              etaNM.setEtas(etaNM.run(etas));
            } else {
              etaNM.setEtas(etaNM.run(etas, Integer.parseInt(getNumStepsTextField().getText())));
            }

            etaNM.deleteObserver(rec);
            owner.getPolygonPanel().setRecorder(rec);
            owner.newFlow();
            owner.getPolygonPanel().repaint();
          } catch(NumberFormatException ex) {
          }
        }
      }
    }
    this.dispose();
  }

}
