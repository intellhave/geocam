package gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;

public abstract class EHRFlowDialog extends JDialog {
  protected JButton runButton;
  protected JButton cancelButton;
  protected JRadioButton maxFlowButton;
  protected JPanel methodPanel;
  protected JRadioButton nmButton;
  protected JRadioButton gradientButton;
  protected JRadioButton neitherFlowButton;
  protected JRadioButton minFlowButton;
  protected JPanel directionPanel;
  protected JPanel quantPanel;
  protected ButtonGroup directionButtonGroup;
  protected ButtonGroup quantButtonGroup;
  protected ButtonGroup methodButtonGroup;
  protected JRadioButton radFlowButton;
  protected JRadioButton etaFlowButton;
  protected GeoquantViewer owner;
  
  public EHRFlowDialog(GeoquantViewer owner) {
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
      nmButton.setBounds(17, 21, 124, 20);
      getMethodButtonGroup().add(nmButton);
    }
    return nmButton;
  }
  
  protected JRadioButton getGradientButton() {
    if(gradientButton == null) {
      gradientButton = new JRadioButton();
      gradientButton.setText("Gradient Flow");
      gradientButton.setBounds(17, 42, 124, 21);
      getMethodButtonGroup().add(gradientButton);
    }
    return gradientButton;
  }
  
  protected abstract void handleRun(ActionEvent evt);
  
  protected class RunAction extends AbstractAction {
    public RunAction(String text) {
      super(text, null);
    }
    @Override
    public void actionPerformed(ActionEvent evt) {
      handleRun(evt);
    }
  }
}
