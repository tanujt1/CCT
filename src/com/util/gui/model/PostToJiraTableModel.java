package com.util.gui.model;

import javax.swing.table.AbstractTableModel;

/**
 * Created by rajendv3 on 8/07/2017.
 */
public class PostToJiraTableModel extends AbstractTableModel {

    public static String[] columns = new String[]{"Select","File Name"};
    private Object[][] data;

    public PostToJiraTableModel(Object[][] data) {
        this.data = data;
    }

    @Override
    public int getRowCount() {
        return data.length;
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
    public Class getColumnClass(int columnIndex) {
        if(columnIndex==0)
            return Boolean.class;
        else
            return getValueAt(0,columnIndex).getClass();
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }


}
