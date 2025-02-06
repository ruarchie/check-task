package org.ruarchie.dev;

import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class CustomTableModel<T> extends DefaultTableModel {

    protected List<String> columns;
    protected final List<String> aliases;
    protected List<Map<String, Object>> tableData;

    protected CustomTableModel() {

        this.aliases = new ArrayList<>();
        this.columns = new ArrayList<>();
    }

    protected void setTableData(String sqlText) {

        var dbQuery = new DBQuery(sqlText);
        this.tableData = new ArrayList<>();
        tableData = dbQuery.getData();
    }

    protected void setTableColumns(String tableName) {
        var dbQuery = new DBQuery("select column_name from information_schema.columns where table_name = '" + tableName.toUpperCase() + "'");
        var colData = dbQuery.getData();
        for (var cd : colData) {
            String colName = String.valueOf(cd.get("COLUMN_NAME"));
            columns.add(colName);
        }
        //return columns;
    }

    protected void setAliases(String inputStreamName) {

//        this.columns = getColumns(tableName);
        try (InputStream inputStream = new FileInputStream(inputStreamName)) {
            Properties props = new Properties();
            props.loadFromXML(inputStream);

            StringBuilder prop = new StringBuilder();

            for (int i = 0; i < props.size(); i++) {
                prop.append(props.getProperty(this.columns.get(i)));

                this.aliases.add(prop.toString());
                prop.setLength(0);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void addAliases() {
        for (String alias : aliases) {
            addColumn(alias);
        }
    }

    protected void addData() {

        Object[] array = new Object[this.columns.size()];

        for (Map<String, Object> row : tableData) {
            for (int i = 0; i < this.columns.size(); i++) {
                array[i] = row.get(this.columns.get(i));
            }
            addRow(array);
            Arrays.fill(array, null);
        }
    }

    protected void updateModelCell(int rowNumber, int columnNumber, Object cellValue) {

        setValueAt(cellValue, rowNumber, columnNumber);
    }

    abstract protected void addRow(T entity);

    abstract protected void deleteRow(int rowNum);

    abstract protected void updateRow(T entity, int rowNum);

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }

    public List<String> getColumns() {
        return columns;
    }
}

