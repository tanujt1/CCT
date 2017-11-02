/*
 * Created by JFormDesigner on Wed Jul 05 23:51:37 IST 2017
 */

package com.util.gui;

import java.awt.event.*;
import com.util.BambooProperties;
import com.util.gui.model.PostToJiraTableModel;
import com.util.gui.util.GUIUtil;
import com.util.webclients.JiraClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author Vijay Kumar
 */
public class JiraUI extends JFrame {

    private BambooProperties properties;
    private JiraClient client;
    private List<String> links;

    public JiraUI(BambooProperties properties, List<String> list){
        this.properties = properties;
        this.client = new JiraClient(properties);
        this.links = list;
        initComponents();
        showPostToJiraDialog();
    }

    public void showPostToJiraDialog(){

        //PostToJiraTableModel model =new PostToJiraTableModel(GUIUtil.parseJiraLinks(links,PostToJiraTableModel.columns));
        jiraLinks.setModel(new PostToJiraTableModel(GUIUtil.parseJiraLinks(links,PostToJiraTableModel.columns)));

        jiraLinks.getColumnModel().getColumn(0).setPreferredWidth(5);
        jiraLinks.getColumnModel().getColumn(1).setPreferredWidth(100);
        //jiraLinksList.getColumnModel().getColumn(2).setPreferredWidth(100);


    }

    public JiraUI(List<String> links) {
        this.links = links;
        initComponents();
    }

    private void okButtonActionPerformed(ActionEvent e) {
        jiraComment.setText("Button clicked");
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Vijay Kumar
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        jiraComment = new JFormattedTextField();
        label2 = new JLabel();
        jiraId = new JTextField();
        label3 = new JLabel();
        checkSelectAll = new JCheckBox();
        scrollPane1 = new JScrollPane();
        jiraLinks = new JTable();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Post Links to Jira");
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
                label1.setText("Comment Message");

                //---- label2 ----
                label2.setText("Jira Id");

                //---- label3 ----
                label3.setText("Choose Links to post to Jira");

                //---- checkSelectAll ----
                checkSelectAll.setText("Select All");

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(jiraLinks);
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                                .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(label2)
                                        .addComponent(jiraId, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                            .addComponent(label1, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)
                                            .addGap(0, 256, Short.MAX_VALUE))
                                        .addComponent(jiraComment, GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(label3)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 347, Short.MAX_VALUE)
                                    .addComponent(checkSelectAll)))
                            .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label2)
                                .addComponent(label1))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                .addComponent(jiraId, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                                .addComponent(jiraComment, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label3)
                                .addComponent(checkSelectAll))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(28, 28, 28))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText("Post to JIRA");
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
    private JFormattedTextField jiraComment;
    private JLabel label2;
    private JTextField jiraId;
    private JLabel label3;
    private JCheckBox checkSelectAll;
    private JScrollPane scrollPane1;
    private JTable jiraLinks;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        final Map<String,List<String>> map = new HashMap<String,List<String>>(){{
           put("abcc",new ArrayList<String>(){{
               add("askdfkasdklfhaklsdfkla");
               add("askdfkasdklfhaklsdfkla1");
               add("askdfkasdklfhaklsdfkla2");
           }}) ;
            put("asdfasdf",new ArrayList<String>(){{
                add("askdfkasdkl544556fhaklsdfkla");
                add("askdfkasdklfhakls564564545dfkla1");
                add("askdfkasdklfhakl21156456456sdfkla2");
            }}) ;
        }};

        List<Map<String,List<String>>> list= new ArrayList<Map<String,List<String>>>(){{
            add(map);
        }};

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        final List<String> strings = GUIUtil.parseLinksFromMap(list);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JiraUI jiraUI = new JiraUI(new BambooProperties(),strings);
                jiraUI.setVisible(true);
            }
        });

    }
}
