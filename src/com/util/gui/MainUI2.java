/*
 * Created by JFormDesigner on Mon Jul 03 10:29:49 IST 2017
 */

package com.util.gui;

import com.util.BambooClient;
import com.util.BambooProperties;
import com.util.Exceptions.ConcordionNotFoundException;
import com.util.Exceptions.InvalidCredentialsException;
import com.util.Exceptions.ValidationException;
import com.util.gui.model.LinksTableModel;
import com.util.gui.outputStream.CustomLogStream;
import com.util.gui.util.GUIUtil;
import com.util.helper.FileSaver;
import com.util.helper.FileSaverImpl;
import com.util.validators.*;
import com.util.webclients.JiraClient;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.LogManager;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.border.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * @author Vijay Kumar
 */
public class MainUI2 extends JFrame {
    public MainUI2() {
        initComponents();
    }

    private BambooProperties properties;
    private List<CSSValidator> validatorList = new ArrayList<CSSValidator>();

    private List<Map<String,List<String>>> parsedLinks = new ArrayList<>();

    private Map<String,String> filterMap = new HashMap<String,String>(){{
       put("All Links","all");
       put("Success Links","success");
       put("Failure Links","failure");
    }};

    private FileSaver fileSaver = new FileSaverImpl();

    private String packName;

    private ButtonGroup buttonGroup = new ButtonGroup();

    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    BambooClient client;

    private void okButtonActionPerformed(ActionEvent e) throws ConcordionNotFoundException {
        // TODO add your code here

        properties = new BambooProperties();
        client = new BambooClient(properties);

        collectParameters();

        CredentialsDialog credentialsDialog = new CredentialsDialog(this,"Login to Bamboo", Dialog.ModalityType.APPLICATION_MODAL,properties);
        credentialsDialog.show();


        try {
            if(StringUtils.isNotEmpty(properties.getUserName()) && StringUtils.isNotEmpty(properties.getPassword())){

                JDialog dialog =showLoadingDialog();
                dialog.show();

                if(client.loginToBamboo()) {
                    dialog.dispose();
                    dialog = showDialog("Message", "Log in successfull");
                    dialog.setModal(true);
                    dialog.setVisible(true);
                }else{
                    dialog.dispose();
                    displayErrorDialog("Log in Failed");
                }
            }else{
                displayErrorDialog("No Username/Password provided");
            }

        } catch (InvalidCredentialsException e1) {
            displayErrorDialog(e1.getMessage());
        } catch (IOException e1) {
            displayErrorDialog("bamboo.service.anz is not available");
        }

        if(client.isLoginSucces()){
           startScrappingForLinks();
           displayLinksInJList();
       }else{
           displayErrorDialog("Failed to fetch the links");
       }
    }

    private Future<Boolean> performLogin(final BambooClient client) throws InvalidCredentialsException, IOException {
        return pool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return client.loginToBamboo();
            }
        });
    }

    private void collectParameters() {

        String bambooJobId = bJob.getText();
        packName = packageName.getText();

        if(StringUtils.isNotEmpty(bambooJobId))
            bambooJobId = bambooJobId.toUpperCase();

       // validatorList.add(new JobValidator(bambooJobId));
        //validatorList.add(new PackageNameValidator(packName));

        ValidatorExecutor executor = new ValidatorExecutor(validatorList);

        try {
            executor.execute();
        } catch (ValidationException e) {
            displayErrorDialog(e.getMessage());
        }

        properties.setPackageName(packName);
        properties.setJob_id(bambooJobId);
        properties.setFilterLinks(filterMap.get(filterLinksCombo.getItemAt(filterLinksCombo.getSelectedIndex()).toString()));

        try{
            if(limitVal.isEnabled()){

                if(StringUtils.isEmpty(limitVal.getText())){
                    displayErrorDialog("Limit value cannot be empty");
                }else{
                    int limit = Integer.parseInt(limitVal.getText());
                    properties.setLimit(limit);
                }

            }else if(startRange.isEnabled() && endRange.isEnabled()){

                if(StringUtils.isEmpty(startRange.getText()) || StringUtils.isEmpty(endRange.getText())){
                   displayErrorDialog("Start range or End range cannot be empty!");
                }else{
                    final int start = Integer.parseInt(startRange.getText());
                    final int end = Integer.parseInt(endRange.getText());

                    properties.setRange(new ArrayList<Integer>(){{
                        add(start);
                        add(end);
                    }});
                }
            }
        }catch (NumberFormatException e){
            displayErrorDialog("Limit must be numeric");
        }

        System.out.println(properties);
    }

    private void displayLinksInJList(){
        List<String> list = new ArrayList<>();

        list.addAll(GUIUtil.parseLinksFromMap(parsedLinks));

        Map<Integer,String> dataMap = new HashMap<>();
        int row=0;
        for(String link : list){
            dataMap.put(row++,link);
        }

        LinksTableModel tableModel = new LinksTableModel(dataMap,packName);
        linksTable.setModel(tableModel);
        linkSize.setText(list.size()+" Links found");

    }

    private void startScrappingForLinks() throws ConcordionNotFoundException {

        try {
            parsedLinks.add(client.scrapNonNPPLinks());

            if(properties.isNppSummary())
                parsedLinks.add(client.scrapNppLinks());

        } catch (InvalidCredentialsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void saveFilesToDisk(BambooProperties properties){
        for(Map<String,List<String>> item : parsedLinks){
            for(Map.Entry<String,List<String>> mapItem : item.entrySet()){
                fileSaver.saveAsFile(mapItem.getValue(),mapItem.getKey(),properties);
            }
        }
    }

    private void showSaveAsFileChooser(){
        JFileChooser fileChooser = new JFileChooser("C:/");
        fileChooser.setDialogTitle("Save As");

        int userSelection = fileChooser.showSaveDialog(new JFrame());

        if(userSelection==JFileChooser.APPROVE_OPTION){
            File file = fileChooser.getSelectedFile();
            properties.setSaveAsFile(true);
            properties.setFile(file);
            properties.setFilePath(file.getAbsolutePath());
            saveFilesToDisk(properties);
            showDialog("Message","File saved successfullly").setVisible(true);
        }
    }

    private JDialog showDialog(String title,String message){

        JOptionPane optionPane = new JOptionPane(message,JOptionPane.INFORMATION_MESSAGE,JOptionPane.DEFAULT_OPTION,null,new Object[]{},null);

        JDialog dialog = new JDialog(this);
        dialog.setTitle(title);
        dialog.setModal(false);

        dialog.setContentPane(optionPane);
        dialog.pack();

        return dialog;
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        this.dispose();
    }

    private void rangeActionPerformed(ActionEvent e) {
        startRange.setEnabled(true);
        endRange.setEnabled(true);
    }

    private void limitActionPerformed(ActionEvent e) {
        startRange.setEnabled(true);
        endRange.setEnabled(false);

    }

    private void displayErrorDialog(String message) {
        JOptionPane.showMessageDialog(new JFrame(),message,"Login Failed",JOptionPane.ERROR_MESSAGE);
    }

    private JDialog showLoadingDialog(){
        return showDialog("Logging in to bamboo","Please wait");
    }

    private void showLogsActionPerformed(ActionEvent e) {

    }

    private void saveMenuActionPerformed(ActionEvent e) {
        showSaveAsFileChooser();
    }

    private void saveAsMenuActionPerformed(ActionEvent e) {
       showSaveAsFileChooser();
    }

    private void checkBox1ActionPerformed(ActionEvent e) {
       properties.setNppSummary(true);
    }

    private void newMenuActionPerformed(ActionEvent e) {
       properties = new BambooProperties();
       client.setBambooProperties(properties);
       bJob.setText("");
       packageName.setText("");
    }

    private void rlimitActionPerformed(ActionEvent e) {
        limitVal.setEnabled(true);

        startRange.setEnabled(false);
        endRange.setEnabled(false);
    }

    private void rRangeActionPerformed(ActionEvent e) {
        limitVal.setEnabled(false);
        startRange.setEnabled(true);
        endRange.setEnabled(true);
    }

    private void resetBtnActionPerformed(ActionEvent e) {
        buttonGroup.clearSelection();
        limitVal.setEnabled(false);
        startRange.setEnabled(false);
        endRange.setEnabled(false);
    }

    private void postToJiraMenuActionPerformed(ActionEvent e) {

        JiraUI jiraUI = new JiraUI(properties,GUIUtil.parseLinksFromMap(parsedLinks));
        jiraUI.setVisible(true);

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Vijay Kumar
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        newMenu = new JMenuItem();
        saveMenu = new JMenuItem();
        saveAsMenu = new JMenuItem();
        exitMenu = new JMenuItem();
        menu2 = new JMenu();
        postToJiraMenu = new JMenuItem();
        helpMenu = new JMenuItem();
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        label1 = new JLabel();
        bJob = new JTextField();
        label2 = new JLabel();
        packageName = new JTextField();
        separator1 = new JSeparator();
        label4 = new JLabel();
        separator2 = new JSeparator();
        showLogs = new JCheckBox();
        checkBox1 = new JCheckBox();
        label3 = new JLabel();
        filterLinksCombo = new JComboBox();
        rlimit = new JRadioButton();
        rRange = new JRadioButton();
        scrollPane2 = new JScrollPane();
        limitVal = new JTextArea();
        scrollPane4 = new JScrollPane();
        startRange = new JTextArea();
        scrollPane5 = new JScrollPane();
        endRange = new JTextArea();
        resetBtn = new JButton();
        tabbedPane1 = new JTabbedPane();
        panel3 = new JPanel();
        scrollPane3 = new JScrollPane();
        logArea = new JTextArea();
        panel2 = new JPanel();
        scrollPane1 = new JScrollPane();
        linksTable = new JTable();
        linkSize = new JLabel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setTitle("Concordion Specification Scrapper v1.0");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("File");

                //---- newMenu ----
                newMenu.setText("New");
                newMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        newMenuActionPerformed(e);
                    }
                });
                menu1.add(newMenu);

                //---- saveMenu ----
                saveMenu.setText("Save");
                saveMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveMenuActionPerformed(e);
                    }
                });
                menu1.add(saveMenu);

                //---- saveAsMenu ----
                saveAsMenu.setText("Save As");
                saveAsMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveAsMenuActionPerformed(e);
                    }
                });
                menu1.add(saveAsMenu);

                //---- exitMenu ----
                exitMenu.setText("Exit");
                menu1.add(exitMenu);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText("Options");

                //---- postToJiraMenu ----
                postToJiraMenu.setText("Post to Jira as Comment");
                postToJiraMenu.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        postToJiraMenuActionPerformed(e);
                    }
                });
                menu2.add(postToJiraMenu);

                //---- helpMenu ----
                helpMenu.setText("Help");
                menu2.add(helpMenu);
            }
            menuBar1.add(menu2);
        }
        setJMenuBar(menuBar1);

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

                //======== panel1 ========
                {
                    panel1.setBorder(new TitledBorder("Properties"));
                    panel1.setBackground(new Color(240, 240, 230));

                    //---- label1 ----
                    label1.setText("Bamboo Job");

                    //---- label2 ----
                    label2.setText("Package Name");

                    //---- label4 ----
                    label4.setText("Filter results by");

                    //---- showLogs ----
                    showLogs.setText("Enable Verbose Logging");
                    showLogs.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            showLogsActionPerformed(e);
                        }
                    });

                    //---- checkBox1 ----
                    checkBox1.setText("Include Npp links");
                    checkBox1.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            checkBox1ActionPerformed(e);
                        }
                    });

                    //---- label3 ----
                    label3.setText("Filter Links");

                    //---- rlimit ----
                    rlimit.setText("Limit");
                    rlimit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rlimitActionPerformed(e);
                        }
                    });

                    //---- rRange ----
                    rRange.setText("Range");
                    rRange.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            rRangeActionPerformed(e);
                        }
                    });

                    //======== scrollPane2 ========
                    {

                        //---- limitVal ----
                        limitVal.setEnabled(false);
                        scrollPane2.setViewportView(limitVal);
                    }

                    //======== scrollPane4 ========
                    {

                        //---- startRange ----
                        startRange.setEnabled(false);
                        scrollPane4.setViewportView(startRange);
                    }

                    //======== scrollPane5 ========
                    {

                        //---- endRange ----
                        endRange.setEnabled(false);
                        scrollPane5.setViewportView(endRange);
                    }

                    //---- resetBtn ----
                    resetBtn.setText("Reset filter");
                    resetBtn.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            resetBtnActionPerformed(e);
                        }
                    });

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(label1)
                                            .addComponent(label2))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(packageName, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
                                            .addComponent(bJob, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap())
                                    .addGroup(GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                        .addComponent(separator2)
                                        .addContainerGap(6, GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(showLogs)
                                            .addComponent(checkBox1)
                                            .addComponent(label3))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(filterLinksCombo, GroupLayout.PREFERRED_SIZE, 149, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap())
                                    .addComponent(separator1)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(resetBtn)
                                            .addComponent(label4))
                                        .addGap(23, 23, 23)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(rlimit)
                                            .addComponent(rRange))
                                        .addGap(39, 39, 39)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(scrollPane4, GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                                                .addGap(12, 12, 12)
                                                .addComponent(scrollPane5, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE))
                                            .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 159, GroupLayout.PREFERRED_SIZE))
                                        .addContainerGap(21, Short.MAX_VALUE))))
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label1)
                                    .addComponent(bJob, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(label2)
                                    .addComponent(packageName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(filterLinksCombo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label3))
                                .addGap(18, 18, 18)
                                .addComponent(separator1, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(rlimit)
                                        .addComponent(label4, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
                                    .addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(rRange, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(resetBtn))
                                    .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(scrollPane5, GroupLayout.Alignment.LEADING)
                                        .addComponent(scrollPane4, GroupLayout.Alignment.LEADING)))
                                .addGap(15, 15, 15)
                                .addComponent(separator2, GroupLayout.PREFERRED_SIZE, 8, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(checkBox1)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(showLogs))
                    );
                }

                //======== tabbedPane1 ========
                {

                    //======== panel3 ========
                    {

                        //======== scrollPane3 ========
                        {
                            scrollPane3.setViewportView(logArea);
                        }

                        GroupLayout panel3Layout = new GroupLayout(panel3);
                        panel3.setLayout(panel3Layout);
                        panel3Layout.setHorizontalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                                    .addContainerGap())
                        );
                        panel3Layout.setVerticalGroup(
                            panel3Layout.createParallelGroup()
                                .addGroup(panel3Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(scrollPane3, GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                                    .addContainerGap())
                        );
                    }
                    tabbedPane1.addTab("Logs", panel3);

                    //======== panel2 ========
                    {

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setViewportView(linksTable);
                        }

                        //---- linkSize ----
                        linkSize.setText("links");

                        GroupLayout panel2Layout = new GroupLayout(panel2);
                        panel2.setLayout(panel2Layout);
                        panel2Layout.setHorizontalGroup(
                            panel2Layout.createParallelGroup()
                                .addGroup(panel2Layout.createSequentialGroup()
                                    .addGap(12, 12, 12)
                                    .addComponent(linkSize, GroupLayout.PREFERRED_SIZE, 135, GroupLayout.PREFERRED_SIZE)
                                    .addContainerGap(521, Short.MAX_VALUE))
                                .addGroup(panel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 656, Short.MAX_VALUE)
                                    .addContainerGap())
                        );
                        panel2Layout.setVerticalGroup(
                            panel2Layout.createParallelGroup()
                                .addGroup(panel2Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 489, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(linkSize)
                                    .addGap(0, 0, Short.MAX_VALUE))
                        );
                    }
                    tabbedPane1.addTab("Output", panel2);
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(tabbedPane1, GroupLayout.DEFAULT_SIZE, 673, Short.MAX_VALUE)
                            .addContainerGap())
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(tabbedPane1)))
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
                okButton.setText("OK");
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            okButtonActionPerformed(e);
                        } catch (ConcordionNotFoundException e1) {
                            e1.printStackTrace();
                        }
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
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem newMenu;
    private JMenuItem saveMenu;
    private JMenuItem saveAsMenu;
    private JMenuItem exitMenu;
    private JMenu menu2;
    private JMenuItem postToJiraMenu;
    private JMenuItem helpMenu;
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JLabel label1;
    private JTextField bJob;
    private JLabel label2;
    private JTextField packageName;
    private JSeparator separator1;
    private JLabel label4;
    private JSeparator separator2;
    private JCheckBox showLogs;
    private JCheckBox checkBox1;
    private JLabel label3;
    private JComboBox filterLinksCombo;
    private JRadioButton rlimit;
    private JRadioButton rRange;
    private JScrollPane scrollPane2;
    private JTextArea limitVal;
    private JScrollPane scrollPane4;
    private JTextArea startRange;
    private JScrollPane scrollPane5;
    private JTextArea endRange;
    private JButton resetBtn;
    private JTabbedPane tabbedPane1;
    private JPanel panel3;
    private JScrollPane scrollPane3;
    private JTextArea logArea;
    private JPanel panel2;
    private JScrollPane scrollPane1;
    private JTable linksTable;
    private JLabel linkSize;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        MainUI2 mainUI2 = new MainUI2();

        LogManager.getLogManager().reset();

        PrintStream printStream = new PrintStream(new CustomLogStream(mainUI2.logArea));
        System.setOut(printStream);
        System.setErr(printStream);

        mainUI2.buttonGroup = new ButtonGroup();
        mainUI2.buttonGroup.add(mainUI2.rlimit);
        mainUI2.buttonGroup.add(mainUI2.rRange);

        mainUI2.filterLinksCombo.setModel(new DefaultComboBoxModel(){{
            addElement("All Links");
            addElement("Success Links");
            addElement("Failure Links");
        }});
        mainUI2.show();

    }
}

