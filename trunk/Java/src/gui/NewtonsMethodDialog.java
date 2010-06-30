package gui;

import java.awt.Component;
import java.awt.Frame;

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

public class NewtonsMethodDialog extends JDialog {
  private JButton runButton;
  private JButton cancelButton;
  private JPanel directionPanel;
  private JPanel quantPanel;
  private JCheckBox minFlowCheckBox;
  private JCheckBox maxFlowCheckBox;
  private ButtonGroup directionButtonGroup;
  private ButtonGroup quantButtonGroup;
  private JRadioButton radFlowButton;
  private JRadioButton etaFlowButton;
  
  public NewtonsMethodDialog(Frame owner) {
    super(owner);
    GroupLayout nMethodDialogLayout = new GroupLayout((JComponent)this.getContentPane());
    this.setLayout(nMethodDialogLayout);
    this.setPreferredSize(new java.awt.Dimension(374, 178));
    this.setTitle("Newton's Method");
    this.setSize(374, 178);
    nMethodDialogLayout.setHorizontalGroup(nMethodDialogLayout.createSequentialGroup()
      .addContainerGap()
      .addComponent(getQuantPanel(), GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
      .addGap(36)
      .addGroup(nMethodDialogLayout.createParallelGroup()
          .addGroup(nMethodDialogLayout.createSequentialGroup()
              .addComponent(getDirectionPanel(), GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)
              .addGap(0, 0, Short.MAX_VALUE))
          .addGroup(GroupLayout.Alignment.LEADING, nMethodDialogLayout.createSequentialGroup()
              .addPreferredGap(getDirectionPanel(), getRunButton(), LayoutStyle.ComponentPlacement.INDENT)
              .addComponent(getRunButton(), GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(getCancelButton(), GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)))
      .addContainerGap(18, 18));
    nMethodDialogLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {getDirectionPanel(), getQuantPanel()});
    nMethodDialogLayout.setVerticalGroup(nMethodDialogLayout.createSequentialGroup()
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      
      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
      .addGroup(nMethodDialogLayout.createParallelGroup()
          .addComponent(getDirectionPanel(), GroupLayout.Alignment.LEADING, 0, 103, Short.MAX_VALUE)
          .addGroup(nMethodDialogLayout.createSequentialGroup()
              .addComponent(getQuantPanel(), GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
              .addGap(0, 0, Short.MAX_VALUE)))
      .addGroup(nMethodDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
          .addComponent(getRunButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
          .addComponent(getCancelButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
      .addContainerGap());
    nMethodDialogLayout.linkSize(SwingConstants.VERTICAL, new Component[] {getQuantPanel(), getDirectionPanel()});
  
  }
  
  protected JButton getRunButton() {
    if(runButton == null) {
      runButton = new JButton();
      runButton.setText("Run");
    }
    return runButton;
  }
  
  protected JButton getCancelButton() {
    if(cancelButton == null) {
      cancelButton = new JButton();
      cancelButton.setText("Cancel");
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
            .addGroup(nmDirectionPanelLayout.createSequentialGroup()
                .addComponent(getMinFlowCheckBox(), GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE))
            .addGroup(GroupLayout.Alignment.LEADING, nmDirectionPanelLayout.createSequentialGroup()
                .addComponent(getMaxFlowCheckBox(), GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)))
        .addContainerGap(32, Short.MAX_VALUE));
      nmDirectionPanelLayout.setVerticalGroup(nmDirectionPanelLayout.createSequentialGroup()
        .addComponent(getMinFlowCheckBox(), GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(getMaxFlowCheckBox(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(11, Short.MAX_VALUE));
    }
    return directionPanel;
  }
  
  protected JCheckBox getMinFlowCheckBox() {
    if(minFlowCheckBox == null) {
      minFlowCheckBox = new JCheckBox();
      minFlowCheckBox.setText("Minimize");
      getDirectionButtonGroup().add(minFlowCheckBox);
    }
    return minFlowCheckBox;
  }
  
  protected JCheckBox getMaxFlowCheckBox() {
    if(maxFlowCheckBox == null) {
      maxFlowCheckBox = new JCheckBox();
      maxFlowCheckBox.setText("Maximize");
      getDirectionButtonGroup().add(maxFlowCheckBox);
    }
    return maxFlowCheckBox;
  }
  
  private ButtonGroup getDirectionButtonGroup() {
    if(directionButtonGroup == null) {
      directionButtonGroup = new ButtonGroup();
    }
    return directionButtonGroup;
  }
  
  private JPanel getQuantPanel() {
    if(quantPanel == null) {
      quantPanel = new JPanel();
      GroupLayout nmQuantPanelLayout = new GroupLayout((JComponent)quantPanel);
      quantPanel.setLayout(nmQuantPanelLayout);
      quantPanel.setBorder(BorderFactory.createTitledBorder("Choose Flow"));
      nmQuantPanelLayout.setHorizontalGroup(nmQuantPanelLayout.createSequentialGroup()
        .addContainerGap()
        .addGroup(nmQuantPanelLayout.createParallelGroup()
            .addGroup(GroupLayout.Alignment.LEADING, nmQuantPanelLayout.createSequentialGroup()
                .addComponent(getRadFlowButton(), GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 36, Short.MAX_VALUE))
            .addGroup(GroupLayout.Alignment.LEADING, nmQuantPanelLayout.createSequentialGroup()
                .addComponent(getEtaFlowButton(), GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap(44, 44));
      nmQuantPanelLayout.setVerticalGroup(nmQuantPanelLayout.createSequentialGroup()
        .addComponent(getRadFlowButton(), GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        .addComponent(getEtaFlowButton(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        .addContainerGap(41, Short.MAX_VALUE));
    }
    return quantPanel;
  }
  
  private JRadioButton getRadFlowButton() {
    if(radFlowButton == null) {
      radFlowButton = new JRadioButton();
      radFlowButton.setText("Radius");
      radFlowButton.setSelected(true);
      getQuantButtonGroup().add(radFlowButton);
    }
    return radFlowButton;
  }
  
  private JRadioButton getEtaFlowButton() {
    if(etaFlowButton == null) {
      etaFlowButton = new JRadioButton();
      etaFlowButton.setText("Eta");
      getQuantButtonGroup().add(etaFlowButton);
    }
    return etaFlowButton;
  }
  
  private ButtonGroup getQuantButtonGroup() {
    if(quantButtonGroup == null) {
      quantButtonGroup = new ButtonGroup();
    }
    return quantButtonGroup;
  }
}
