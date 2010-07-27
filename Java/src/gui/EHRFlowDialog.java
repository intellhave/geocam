package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;


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
public abstract class EHRFlowDialog extends JDialog {
  protected JButton runButton;
  protected JButton cancelButton;
  protected JPanel methodPanel;
  protected JRadioButton nmButton;
  protected JRadioButton gradientButton;
  protected JLabel stepsizeLabel;
  protected JTextField stepsizeTextField;
  protected JRadioButton numStepsButton;
  protected JTextField numStepsTextField;
  protected JRadioButton precisionButton;
  protected JTextField precisionTextField;
  protected JPanel stoppingCondPanel;
  protected JPanel quantPanel;
  protected ButtonGroup quantButtonGroup;
  protected ButtonGroup methodButtonGroup;
  protected ButtonGroup stoppingCondButtonGroup;
  protected JRadioButton radFlowButton;
  protected JRadioButton etaFlowButton;
  protected GeoquantViewer owner;
  
  public EHRFlowDialog(GeoquantViewer owner) {
    super(owner);
    this.owner = owner;
    GroupLayout nMethodDialogLayout = new GroupLayout((JComponent)getContentPane());
    getContentPane().setLayout(nMethodDialogLayout);
    nMethodDialogLayout.setVerticalGroup(nMethodDialogLayout.createSequentialGroup()
    	.addGroup(nMethodDialogLayout.createParallelGroup()
    	    .addComponent(getMethodPanel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
    	    .addComponent(getQuantPanel(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
    	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    	.addComponent(getStoppingCondPanel(), 0, 111, Short.MAX_VALUE)
    	.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    	.addGroup(nMethodDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
    	    .addComponent(getStepsizeTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
    	    .addComponent(getStepsizeLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
    	    .addComponent(getRunButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
    	    .addComponent(getCancelButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
    	.addContainerGap(29, 29));
    nMethodDialogLayout.setHorizontalGroup(nMethodDialogLayout.createSequentialGroup()
    	.addContainerGap()
    	.addGroup(nMethodDialogLayout.createParallelGroup()
    	    .addGroup(nMethodDialogLayout.createSequentialGroup()
    	        .addGroup(nMethodDialogLayout.createParallelGroup()
    	            .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
    	                .addComponent(getStepsizeLabel(), GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
    	                .addGap(29)
    	                .addComponent(getStepsizeTextField(), GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE))
    	            .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
    	                .addComponent(getQuantPanel(), GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
    	                .addGap(35)))
    	        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    	        .addGroup(nMethodDialogLayout.createParallelGroup()
    	            .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
    	                .addGap(0, 0, Short.MAX_VALUE)
    	                .addComponent(getMethodPanel(), GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE))
    	            .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
    	                .addPreferredGap(getMethodPanel(), getRunButton(), LayoutStyle.ComponentPlacement.INDENT)
    	                .addComponent(getRunButton(), GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
    	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    	                .addComponent(getCancelButton(), GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))))
    	    .addGroup(nMethodDialogLayout.createSequentialGroup()
    	        .addComponent(getStoppingCondPanel(), GroupLayout.PREFERRED_SIZE, 356, GroupLayout.PREFERRED_SIZE)
    	        .addGap(0, 0, Short.MAX_VALUE)))
    	.addContainerGap(32, 32));
    nMethodDialogLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getRunButton(), getCancelButton()});
    nMethodDialogLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getMethodPanel(), getQuantPanel()});
    nMethodDialogLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getMethodPanel(), getQuantPanel()});
    this.setPreferredSize(new java.awt.Dimension(406, 292));
    this.setResizable(false);
    this.setSize(406, 292);
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
      radFlowButton.setBounds(17, 27, 124, 18);
      getQuantButtonGroup().add(radFlowButton);
    }
    return radFlowButton;
  }
  
  protected JRadioButton getEtaFlowButton() {
    if(etaFlowButton == null) {
      etaFlowButton = new JRadioButton();
      etaFlowButton.setText("Eta");
      etaFlowButton.setBounds(17, 48, 124, 18);
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

  protected JPanel getMethodPanel() {
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
  
  protected JRadioButton getNmButton() {
    if(nmButton == null) {
      nmButton = new JRadioButton();
      nmButton.setText("Newton's Method");
      nmButton.setSelected(true);
      nmButton.setBounds(17, 27, 124, 18);
      getMethodButtonGroup().add(nmButton);
    }
    return nmButton;
  }
  
  protected JRadioButton getGradientButton() {
    if(gradientButton == null) {
      gradientButton = new JRadioButton();
      gradientButton.setText("Gradient Flow");
      gradientButton.setBounds(17, 48, 124, 18);
      getMethodButtonGroup().add(gradientButton);
    }
    return gradientButton;
  }
  
  protected abstract void handleRun(ActionEvent evt);
  
  protected JPanel getStoppingCondPanel() {
	  if(stoppingCondPanel == null) {
		  stoppingCondPanel = new JPanel();
		  GroupLayout jPanel1Layout = new GroupLayout((JComponent)stoppingCondPanel);
		  stoppingCondPanel.setBorder(BorderFactory.createTitledBorder(null,"Choose Stopping Condition",TitledBorder.LEADING,TitledBorder.DEFAULT_POSITION));
		  stoppingCondPanel.setLayout(jPanel1Layout);
		  jPanel1Layout.setHorizontalGroup(jPanel1Layout.createSequentialGroup()
		  	.addGroup(jPanel1Layout.createParallelGroup()
		  	    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
		  	        .addComponent(getNumStepsTextField(), GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
		  	        .addGap(29))
		  	    .addComponent(getNumStepsButton(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 170, GroupLayout.PREFERRED_SIZE))
		  	.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		  	.addGroup(jPanel1Layout.createParallelGroup()
		  	    .addGroup(GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
		  	        .addComponent(getPrecisionButton(), GroupLayout.PREFERRED_SIZE, 144, GroupLayout.PREFERRED_SIZE)
		  	        .addGap(0, 0, Short.MAX_VALUE))
		  	    .addGroup(jPanel1Layout.createSequentialGroup()
		  	        .addGap(0, 0, Short.MAX_VALUE)
		  	        .addComponent(getPrecisionTextField(), GroupLayout.PREFERRED_SIZE, 143, GroupLayout.PREFERRED_SIZE)))
		  	.addContainerGap(20, 20));
		  jPanel1Layout.setVerticalGroup(jPanel1Layout.createSequentialGroup()
		  	.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		  	    .addComponent(getPrecisionButton(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
		  	    .addComponent(getNumStepsButton(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		  	.addGap(15)
		  	.addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		  	    .addComponent(getPrecisionTextField(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE)
		  	    .addComponent(getNumStepsTextField(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
		  	.addContainerGap(28, 28));
		  jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {getPrecisionTextField(), getNumStepsTextField()});
		  jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {getPrecisionButton(), getNumStepsButton()});
	  }
	  return stoppingCondPanel;
  }
  
  protected JTextField getPrecisionTextField() {
	  if(precisionTextField == null) {
		  precisionTextField = new JTextField();
		  precisionTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		  precisionTextField.setText("0.0001");
		  precisionTextField.setEnabled(false);
	  }
	  return precisionTextField;
  }
  
  protected JRadioButton getPrecisionButton() {
	  if(precisionButton == null) {
      precisionButton = new JRadioButton();
      precisionButton.setText("Precision");
      precisionButton.setActionCommand("P");
      precisionButton.addActionListener(new StoppingCondListener());
      getStoppingCondButtonGroup().add(precisionButton);
	  }
	  return precisionButton;
  }
  
  protected JTextField getNumStepsTextField() {
	  if(numStepsTextField == null) {
		  numStepsTextField = new JTextField();
		  numStepsTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		  numStepsTextField.setText("100");
	  }
	  return numStepsTextField;
  }
  
  protected JRadioButton getNumStepsButton() {
	  if(numStepsButton == null) {
		  numStepsButton = new JRadioButton();
		  numStepsButton.setText("Number of Steps");
		  numStepsButton.setSelected(true);
		  numStepsButton.setActionCommand("NS");
		  numStepsButton.addActionListener(new StoppingCondListener());
		  getStoppingCondButtonGroup().add(numStepsButton);
	  }
	  return numStepsButton;
  }
  
  protected JTextField getStepsizeTextField() {
	  if(stepsizeTextField == null) {
		  stepsizeTextField = new JTextField();
		  stepsizeTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		  stepsizeTextField.setText("1.0");
	  }
	  return stepsizeTextField;
  }
  
  protected JLabel getStepsizeLabel() {
	  if(stepsizeLabel == null) {
		  stepsizeLabel = new JLabel();
		  stepsizeLabel.setText("Stepsize:");
	  }
	  return stepsizeLabel;
  }

  protected class RunAction extends AbstractAction {
    public RunAction(String text) {
      super(text, null);
    }
    @Override
    public void actionPerformed(ActionEvent evt) {
      handleRun(evt);
    }
  }
  
  protected class StoppingCondListener implements ActionListener {
    public void actionPerformed(ActionEvent evt) {
       String name = evt.getActionCommand();
       if(name.equals("NS")) {
         getPrecisionTextField().setEnabled(false);
         getNumStepsTextField().setEnabled(true);
       } else if(name.equals("P")) {
         getPrecisionTextField().setEnabled(true);
         getNumStepsTextField().setEnabled(false);
       }
    }
  }
  
  protected ButtonGroup getStoppingCondButtonGroup() {
    if(stoppingCondButtonGroup == null) {
      stoppingCondButtonGroup = new ButtonGroup();
    }
    return stoppingCondButtonGroup;
  }
}
