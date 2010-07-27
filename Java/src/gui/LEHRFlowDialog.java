package gui;

import geoquant.GeoRecorder;

import java.awt.event.ActionEvent;

import solvers.newtonsMethod.EtaVEHRGradient;
import solvers.newtonsMethod.EtaVEHRNewton;
import solvers.newtonsMethod.RadiusLEHRGradient;
import solvers.newtonsMethod.RadiusVEHRGradient;
import solvers.newtonsMethod.RadiusVEHRNewton;
import solvers.newtonsMethod.WrongDirectionException;

public class LEHRFlowDialog extends EHRFlowDialog {
  public LEHRFlowDialog(GeoquantViewer owner) {
    super(owner);
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
//      if(getNmButton().isSelected()) {
//        
//        if(getRadFlowButton().isSelected()) {
//          RadiusVEHRNewton radNM = new RadiusVEHRNewton();
//          GeoRecorder rec = owner.getRecorder();
//          rec.update(radNM, null);
//          radNM.addObserver(rec);
//          double[] log_radii = radNM.getLogRadii();
//          if(getMaxFlowButton().isSelected()) {
//            try {
//              radNM.setLogRadii(radNM.maximize(log_radii));
//            } catch (WrongDirectionException e) {
//            }
//          } else if(getMinFlowButton().isSelected()) {
//            try {
//              radNM.setLogRadii(radNM.minimize(log_radii));
//            } catch (WrongDirectionException e) {
//            }
//          } else {
//            radNM.setLogRadii(radNM.optimize(log_radii));
//          }
//          radNM.deleteObserver(rec);
//          owner.getPolygonPanel().setRecorder(rec);
//          owner.newFlow();
//          owner.getPolygonPanel().repaint();
//        } else {
//          EtaVEHRNewton etaNM = new EtaVEHRNewton();
//          GeoRecorder rec = owner.getRecorder();
//          rec.update(etaNM, null);
//          etaNM.addObserver(rec);
//          double[] etas = etaNM.getEtas();
//
//          if(getMaxFlowButton().isSelected()) {
//            try {
//              etaNM.setEtas(etaNM.maximize(etas));
//            } catch (WrongDirectionException e) {
//            }
//          } else if(getMinFlowButton().isSelected()) {
//            try {
//              etaNM.setEtas(etaNM.minimize(etas));
//            } catch (WrongDirectionException e) {
//            }
//          } else {
//            for(int i = 0; i < 10; i++) {
//              etaNM.setEtas(etaNM.optimize(etas));
//              double max = 0;
//              for(int j = 0; j < etas.length; j++) {
//                if(max < etas[j]) {
//                  max = etas[j];
//                }
//              }
//              for(int j = 0; j < etas.length; j++) {
//                etas[j] = etas[j] / max;
//              }
//            }
//            double max = 0;
//            for(int j = 0; j < etas.length; j++) {
//              if(max < etas[j]) {
//                max = etas[j];
//              }
//            }
//            for(int j = 0; j < etas.length; j++) {
//              etas[j] = etas[j] / max;
//            }
//            etaNM.setEtas(etas);
//          }
//          etaNM.deleteObserver(rec);
//          owner.getPolygonPanel().setRecorder(rec);
//          owner.newFlow();
//          owner.getPolygonPanel().repaint();
//        }
//      } else if(getGradientButton().isSelected()) {
//        if(getRadFlowButton().isSelected()) {
//          RadiusVEHRGradient radNM = new RadiusVEHRGradient();
//          GeoRecorder rec = owner.getRecorder();
//          rec.update(radNM, null);
//          radNM.addObserver(rec);
//          double[] log_radii = radNM.getLogRadii();
//          if(getMaxFlowButton().isSelected()) {
//            try {
//              radNM.setLogRadii(radNM.maximize(log_radii));
//            } catch (WrongDirectionException e) {
//            }
//          } else if(getMinFlowButton().isSelected()) {
//            try {
//              radNM.setLogRadii(radNM.minimize(log_radii));
//            } catch (WrongDirectionException e) {
//            }
//          } else {
//            radNM.setLogRadii(radNM.optimize(log_radii));
//          }
//          radNM.deleteObserver(rec);
//          owner.getPolygonPanel().setRecorder(rec);
//          owner.newFlow();
//          owner.getPolygonPanel().repaint();
//        } else {
//          EtaVEHRGradient etaNM = new EtaVEHRGradient();
//          GeoRecorder rec = owner.getRecorder();
//          rec.update(etaNM, null);
//          etaNM.addObserver(rec);
//          double[] etas = etaNM.getEtas();
//
//          if(getMaxFlowButton().isSelected()) {
//            try {
//              etaNM.setEtas(etaNM.maximize(etas));
//            } catch (WrongDirectionException e) {
//            }
//          } else if(getMinFlowButton().isSelected()) {
//            try {
//              etaNM.setEtas(etaNM.minimize(etas));
//            } catch (WrongDirectionException e) {
//            }
//          } else {
//            for(int i = 0; i < 10; i++) {
//              etaNM.setEtas(etaNM.optimize(etas));
//              double max = 0;
//              for(int j = 0; j < etas.length; j++) {
//                if(max < etas[j]) {
//                  max = etas[j];
//                }
//              }
//              for(int j = 0; j < etas.length; j++) {
//                etas[j] = etas[j] / max;
//              }
//            }
//            double max = 0;
//            for(int j = 0; j < etas.length; j++) {
//              if(max < etas[j]) {
//                max = etas[j];
//              }
//            }
//            for(int j = 0; j < etas.length; j++) {
//              etas[j] = etas[j] / max;
//            }
//            etaNM.setEtas(etas);
//          }
//          etaNM.deleteObserver(rec);
//          owner.getPolygonPanel().setRecorder(rec);
//          owner.newFlow();
//          owner.getPolygonPanel().repaint();
//        }
//      }
    }
    this.dispose();
  }
}
