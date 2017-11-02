/*
 * Created by JFormDesigner on Mon Jul 03 10:24:02 IST 2017
 */

package com.util.gui;

import com.util.BambooClient;
import com.util.BambooProperties;
import com.util.Exceptions.InvalidCredentialsException;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * @author Vijay Kumar
 */
public class CredentialsDialog extends JDialog {

    BambooProperties bambooProperties = new BambooProperties();
    BambooClient client;


    public CredentialsDialog(Window owner, String title, ModalityType modalityType, BambooProperties bambooProperties) {
        super(owner, title, modalityType);
        this.bambooProperties = bambooProperties;
        initComponents();
    }

    public CredentialsDialog(Window owner, String title, ModalityType modalityType, BambooProperties bambooProperties,BambooClient client) {
        super(owner, title, modalityType);
        this.bambooProperties = bambooProperties;
        this.client = client;
        initComponents();
    }

    private void okButtonActionPerformed(ActionEvent e) {

        String username = bUser.getText();
        String password = String.valueOf(bPass.getPassword());
        boolean shouldDispose = true;

        if(!StringUtils.isNotEmpty(username)){
            displayErrorDialog("Username cannot be empty!");
            shouldDispose=false;
        }


        if(!StringUtils.isNotEmpty(password)){
            displayErrorDialog("Password cannot be emtpy");
            shouldDispose=false;
        }

        bambooProperties.setUserName(username);
        bambooProperties.setPassword(password);

        if(shouldDispose)
            dispose();
    }

    private void displayErrorDialog(String message) {
        JOptionPane.showMessageDialog(new JFrame(),message,"Login Failed",JOptionPane.ERROR_MESSAGE);
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Vijay Kumar
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        bUser = new JFormattedTextField();
        label2 = new JLabel();
        bPass = new JPasswordField();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            // JFormDesigner evaluation mark
            dialogPane.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                    "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    java.awt.Color.red), dialogPane.getBorder())); dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("Bamboo Username");

                //---- label2 ----
                label2.setText("Bamboo Password");

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(label1)
                                .addComponent(label2))
                            .addGap(24, 24, 24)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(bUser, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                .addComponent(bPass, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label1)
                                .addComponent(bUser, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label2)
                                .addComponent(bPass, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.NORTH);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Vijay Kumar
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JFormattedTextField bUser;
    private JLabel label2;
    private JPasswordField bPass;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
