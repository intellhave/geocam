package gui;

import geoquant.GeoRecorder;

import java.awt.event.ActionEvent;

import solvers.implemented.EtaVEHRGradient;
import solvers.implemented.EtaVEHRNewton;
import solvers.implemented.RadiusLEHRGradient;
import solvers.implemented.RadiusVEHRGradient;
import solvers.implemented.RadiusVEHRNewton;

public class LEHRFlowDialog extends EHRFlowDialog {
  public LEHRFlowDialog(GeoquantViewer owner) {
    super(owner);
    this.setTitle("LEHR Flow");
    
    getNmButton().setSelected(false);
    getGradientButton().setSelected(true);
    getNmButton().setEnabled(false);
    getGradientButton().setEnabled(false);
    
    getRadFlowButton().setEnabled(false);
    getEtaFlowButton().setEnabled(false);
  }
  
  protected void handleRun(ActionEvent evt) {
    if(evt.getSource().equals(getRunButton())) {
      this.setVisible(false);
      RadiusLEHRGradient radNM = new RadiusLEHRGradient();
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
    }
    this.dispose();
  }
}
