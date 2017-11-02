package com.util.gui.model;

import com.util.gui.util.GUIUtil;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rajendv3 on 5/07/2017.
 */
public class LinksTableModel extends AbstractTableModel {

    private String[] columns = new String[]{"S.No","Package Name","File Name"};
    private Map<Integer,String> dataMap = new HashMap<>();
    private String packageName;

    private Object[][] data;

    public LinksTableModel(Map<Integer, String> dataMap) {
        this.dataMap = dataMap;
    }

    public LinksTableModel() {
    }

    public LinksTableModel(String[] columns, List<String> links) {
        this.columns = columns;
        this.data = GUIUtil.parseJiraLinks(links,columns);
    }

    public LinksTableModel(Map<Integer, String> dataMap, String packageName) {
        this.dataMap = dataMap;
        this.packageName = packageName;
        this.data = GUIUtil.parseLinksAndPackages(dataMap,packageName,columns);
    }


    public String[] getColumns() {
        return columns;
    }

    @Override
    public int getRowCount() {
        return dataMap.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }



    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    public Object[][] getData() {
        return data;
    }
}
