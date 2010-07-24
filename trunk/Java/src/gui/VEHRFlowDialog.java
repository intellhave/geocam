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

import solvers.newtonsMethod.EtaOptNEHR;
import solvers.newtonsMethod.EtaVEHRGradient;
import solvers.newtonsMethod.RadiusOptNEHR;
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
public class VEHRFlowDialog extends JDialog {
  private JButton runButton;
  private JButton cancelButton;
  private JRadioButton maxFlowButton;
  private JPanel methodPanel;
  private JRadioButton nmButton;
  private JRadioButton gradientButton;
  private JRadioButton neitherFlowButton;
  private JRadioButton minFlowButton;
  private JPanel directionPanel;
  private JPanel quantPanel;
  private ButtonGroup directionButtonGroup;
  private ButtonGroup quantButtonGroup;
  private ButtonGroup methodButtonGroup;
  private JRadioButton radFlowButton;
  private JRadioButton etaFlowButton;
  private GeoquantViewer owner;
  
  public VEHRFlowDialog(GeoquantViewer owner) {
    super(owner);
    this.owner = owner;
    GroupLayout nMethodDialogLayout = new GroupLayout((JComponent)this.getContentPane());
    this.setLayout(nMethodDialogLayout);
    nMethodDialogLayout.setVerticalGroup(nMethodDialogLayout.createSequentialGroup()
    	.addGroup(nMethodDialogLayout.createParallelGroup()
    	    .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
    	        .addComponent(getDirectionPanel(), GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
    	        .addGap(38)
    	        .addGroup(nMethodDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	            .addComponent(getRunButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
    	            .addComponent(getCancelButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
    	    .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
    	        .addComponent(getQuantPanel(), GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
    	        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    	        .addComponent(getMethodPanel(), 0, 84, Short.MAX_VALUE)))
    	.addContainerGap(22, 22));
    nMethodDialogLayout.setHorizontalGroup(nMethodDialogLayout.createSequentialGroup()
    	.addContainerGap()
    	.addGroup(nMethodDialogLayout.createParallelGroup()
    	    .addComponent(getQuantPanel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
    	    .addComponent(getMethodPanel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE))
    	.addGap(30)
    	.addGroup(nMethodDialogLayout.createParallelGroup()
    	    .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
    	        .addComponent(getRunButton(), GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
    	        .addGap(18)
    	        .addComponent(getCancelButton(), GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE))
    	    .addGroup(nMethodDialogLayout.createSequentialGroup()
    	        .addComponent(getDirectionPanel(), GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)))
    	.addContainerGap(22, Short.MAX_VALUE));
    nMethodDialogLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getCancelButton(), getRunButton()});
    nMethodDialogLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getMethodPanel(), getQuantPanel()});
    nMethodDialogLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getDirectionPanel(), getMethodPanel(), getQuantPanel()});
    nMethodDialogLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getCancelButton(), getRunButton()});
    this.setPreferredSize(new java.awt.Dimension(377, 222));
    this.setTitle("VEHR Flow");
    this.setResizable(false);
    this.setSize(377, 222);

  }
  
  protected JButton getRunButton() {
    if(runButton == null) {
      runButton = new JButton();
      runButton.setText("Run");
      runButton.setAction(new RunAction("Run"));
    }
    return runButton;
  }
  
  protected JButton getCancelButton() {
    if(cancelButton == null) {
      cancelButton = new JButton();
      cancelButton.setText("Cancel");
      cancelButton.setAction(new RunAction("Cancel"));
    }
    return cancelButton;
  }
  
  protected JPanel getDirectionPanel() {
    if(directionPanel == null) {
      directionPanel = new JPanel();
      GroupLayout nmDirectionPanelLayout = new GroupLayout((JComponent)directionPanel);
      directionPanel.setLayout(nmDirectionPanelLayout);
      directionPanel.setBorder(BorderFactory.createTitledBorder("Choose Direction"));
      nmDirectionPanelLayout.setHorizontalGroup(nmDirectionPanelLayout.createSequentialGroup()
      	.addContainerGap()
      	.addGroup(nmDirectionPanelLayout.createParallelGroup()
      	    .addGroup(GroupLayout.Alignment.LEADING, nmDirectionPanelLayout.createSequentialGroup()
      	        .addComponent(getNeitherFlowButton(), GroupLayout.PREFERRED_SIZE, 92, GroupLayout.PREFERRED_SIZE))
      	    .addGroup(nmDirectionPanelLayout.createSequentialGroup()
      	        .addComponent(getMinFlowButton(), GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE))
      	    .addGroup(nmDirectionPanelLayout.createSequentialGroup()
      	        .addComponent(getMaxFlowButton(), GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)))
      	.addContainerGap(38, Short.MAX_VALUE));
      nmDirectionPanelLayout.setVerticalGroup(nmDirectionPanelLayout.createSequentialGroup()
      	.addComponent(getMaxFlowButton(), GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
      	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      	.addComponent(getMinFlowButton(), GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
      	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      	.addComponent(getNeitherFlowButton(), GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
      	.addContainerGap(29, Short.MAX_VALUE));
    }
    return directionPanel;
  }

  protected ButtonGroup getDirectionButtonGroup() {
    if(directionButtonGroup == null) {
      directionButtonGroup = new ButtonGroup();
      
    }
    return directionButtonGroup;
  }
  
  protected ButtonGroup getMethodButtonGroup() {
    if(methodButtonGroup == null) {
      methodButtonGroup = new ButtonGroup();
  
    }
    return methodButtonGroup;
  }
  
  protected JPanel getQuantPanel() {
    if(quantPanel == null) {
      quantPanel = new JPanel();
      GroupLayout nmQuantPanelLayout = new GroupLayout((JComponent)quantPanel);
      quantPanel.setLayout(null);
      quantPanel.setBorder(BorderFactory.createTitledBorder("Choose Flow"));
      quantPanel.add(getRadFlowButton());
      quantPanel.add(getEtaFlowButton());
    }
    return quantPanel;
  }
  
  protected JRadioButton getRadFlowButton() {
    if(radFlowButton == null) {
      radFlowButton = new JRadioButton();
      radFlowButton.setText("Radius");
      radFlowButton.setSelected(true);
      radFlowButton.setBounds(17, 21, 65, 20);
      getQuantButtonGroup().add(radFlowButton);
    }
    return radFlowButton;
  }
  
  protected JRadioButton getEtaFlowButton() {
    if(etaFlowButton == null) {
      etaFlowButton = new JRadioButton();
      etaFlowButton.setText("Eta");
      etaFlowButton.setBounds(17, 42, 100, 21);
      getQuantButtonGroup().add(etaFlowButton);
    }
    return etaFlowButton;
  }
  
  protected ButtonGroup getQuantButtonGroup() {
    if(quantButtonGroup == null) {
      quantButtonGroup = new ButtonGroup();
    }
    return quantButtonGroup;
  }
  
  protected JRadioButton getMaxFlowButton() {
	  if(maxFlowButton == null) {
		  maxFlowButton = new JRadioButton();
		  maxFlowButton.setText("Maximum");
		  getDirectionButtonGroup().add(maxFlowButton);
	  }
	  return maxFlowButton;
  }
  
  protected JRadioButton getMinFlowButton() {
	  if(minFlowButton == null) {
		  minFlowButton = new JRadioButton();
		  minFlowButton.setText("Minimum");
		  getDirectionButtonGroup().add(minFlowButton);
	  }
	  return minFlowButton;
  }
  
  protected JRadioButton getNeitherFlowButton() {
	  if(neitherFlowButton == null) {
		  neitherFlowButton = new JRadioButton();
		  neitherFlowButton.setText("Neither");
		  neitherFlowButton.setSelected(true);
		  getDirectionButtonGroup().add(neitherFlowButton);
	  }
	  return neitherFlowButton;
  }
  
  private JPanel getMethodPanel() {
	  if(methodPanel == null) {
		  methodPanel = new JPanel();
		  GroupLayout methodPanelLayout = new GroupLayout((JComponent)methodPanel);
		  methodPanel.setLayout(null);
		  methodPanel.setBorder(BorderFactory.createTitledBorder("Choose Method"));
		  methodPanel.add(getGradientButton());
		  methodPanel.add(getNmButton());
	  }
	  return methodPanel;
  }
  
  private JRadioButton getNmButton() {
	  if(nmButton == null) {
		  nmButton = new JRadioButton();
		  nmButton.setText("Newton's Method");
		  nmButton.setSelected(true);
		  nmButton.setBounds(17, 21, 124, 20);
		  getMethodButtonGroup().add(nmButton);
	  }
	  return nmButton;
  }
  
  private JRadioButton getGradientButton() {
	  if(gradientButton == null) {
		  gradientButton = new JRadioButton();
		  gradientButton.setText("Gradient Flow");
		  gradientButton.setBounds(17, 42, 124, 21);
		  getMethodButtonGroup().add(gradientButton);
	  }
	  return gradientButton;
  }

  class RunAction extends AbstractAction {
    public RunAction(String text) {
      super(text, null);
    }
    @Override
    public void actionPerformed(ActionEvent evt) {
      if(evt.getSource().equals(getRunButton())) {
        VEHRFlowDialog.this.setVisible(false);
        if(getNmButton().isSelected()) {
          
          if(getRadFlowButton().isSelected()) {
            RadiusOptNEHR radNM = new RadiusOptNEHR();
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
            EtaOptNEHR etaNM = new EtaOptNEHR();
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
      VEHRFlowDialog.this.dispose();
    }
    
  }
}
