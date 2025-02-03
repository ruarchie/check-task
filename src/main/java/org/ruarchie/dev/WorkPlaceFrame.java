package org.ruarchie.dev;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WorkPlaceFrame extends JFrame {
    private JLabel jLabHeader;
    private JTable jWpTable;
    private JPanel jPanelButton;
    private JScrollPane jWorkPlaceSpanel;
    private JButton jBAdd;
    private JButton jBDelete;
    private JButton jBUpdate;
    private JPanel jPanelWP;
    private JLabel jLabHidden;
    private final WorkPlaceTableModel<WorkPlace> wpTabModel;
    private int nextWorkPlaceID;

    public WorkPlaceFrame(int applicantId) {
        setTitle("Список мест работы");
        setSize(640, 480);
        setContentPane(jPanelWP);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jLabHidden.setVisible(false);
        jLabHidden.setText(String.valueOf(applicantId));
        initNextWorkPlaceID();

        wpTabModel = new WorkPlaceTableModel<>("target/classes/resources/workplace.xml", applicantId);
        wpTabModel.addAliases();
        wpTabModel.addData();
        jWpTable.setModel(wpTabModel);
        jWpTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

//        jWpTable.setDefaultRenderer(LocalDate.class, new DateRenderer());
        jWpTable.setDefaultEditor(String.class, new DateEditor());

        setColumnsSize();
        initListeners();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, dim.width - 300, dim.height - 300);
        setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        //pack();
        setVisible(true);
    }

    private void initNextWorkPlaceID() {

        var dbQuery = new DBQuery("SELECT MAX(id) id FROM workplace");
        var listMaxId = dbQuery.getData();

        Object ObjectID = listMaxId.getFirst().get("ID");
        if (ObjectID == null) {
            setNextWorkPlaceID(1);
        } else {
            String ObjectIDAsStr = String.valueOf(listMaxId.getFirst().get("ID"));
            int nextId = Integer.parseInt(ObjectIDAsStr) + 1;
            setNextWorkPlaceID(nextId);
        }
    }

    private void setColumnsSize() {

        float[] columnWidthPercentage = {0.05f, 0.1f, 0.1f, 0.0f, 0.75f};
        TableColumn column;
        TableColumnModel jTableColumnModel = jWpTable.getColumnModel();

        int commonWidth = jTableColumnModel.getTotalColumnWidth();
        for (int i = 0; i < jTableColumnModel.getColumnCount(); i++) {
            column = jTableColumnModel.getColumn(i);
            int preffWidth = Math.round(columnWidthPercentage[i] * commonWidth);
            column.setPreferredWidth(preffWidth);
            if (i == 3) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setWidth(0);
            }
        }
    }

    private void addWorkPlace(WorkPlace jWorkPlace) {

        wpTabModel.addRow(jWorkPlace);
        this.setNextWorkPlaceID(getNextWorkPlaceID() + 1);
    }

    private void deleteWorkPlace(WorkPlace jWorkPlace) {

        try (Connection conn = DBConnection.getConnection()) {

            WorkPlaces<WorkPlace> wps = new WorkPlaces<>(conn);
            wps.addDeleteSQL("DELETE FROM workplace WHERE id = ?");
            wps.delete(jWorkPlace);
            int selectedRow = jWpTable.getSelectedRow();
            if (selectedRow != -1) {
                wpTabModel.deleteRow(jWpTable.convertRowIndexToModel(selectedRow));
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void updateWorkPlace() {

        List<WorkPlace> wpNewId = new ArrayList<>();
        List<WorkPlace> wpUpdId = new ArrayList<>();

        List<Map<String, Object>> initialWorkPlaces = wpTabModel.getWorkPlaces();
        //List<Map<String, Object>> jTableWorkPlaces = new ArrayList<>();

        int initialWpSize = initialWorkPlaces.size();
        int jTableSize = wpTabModel.getRowCount();
        int columnsCount = wpTabModel.getColumnCount();
        List<String> columns = wpTabModel.getColumns();

        for (int i = 0; i < jTableSize; i++) { //Jtable

            Map<String, Object> jTableRow = new LinkedHashMap<>();
            int jTableId = Integer.parseInt(wpTabModel.getValueAt(i, 0).toString());

            for (int h = 0; h < columnsCount; h++) {
                jTableRow.put(columns.get(h), wpTabModel.getValueAt(i, h));
            }
            int countUnIdent = 0;

            for (Map<String, Object> iniWp : initialWorkPlaces) { //Before manipulate
                int iniRowId = Integer.parseInt(String.valueOf(iniWp.get("ID")));

                if (iniRowId == jTableId) {
                    wpUpdId.add(new WorkPlace(jTableRow));
                } else {
                    countUnIdent++;
                }
            }
            if (countUnIdent == initialWpSize) {
                //wpNewId.add(jTableRow);
                wpNewId.add(new WorkPlace(jTableRow));
            }
        }

        try (Connection conn = DBConnection.getConnection()) {

            WorkPlaces<WorkPlace> wps = new WorkPlaces<>(conn);
            wps.addCreateSQL("INSERT INTO workplace(id, begindate, enddate, applicant_id, jobdescription) VALUES(?, ?, ?, ?, ?)");
            for (WorkPlace wp : wpNewId) {
                wps.create(wp);
            }
            wps.addUpdateSQL("UPDATE workplace SET begindate = ?, enddate = ?, applicant_id = ?, jobdescription = ? WHERE id = ?");
            for (WorkPlace wp : wpUpdId) {
                wps.update(wp);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private WorkPlace getWorkPlace() {

        var emptyWp = new WorkPlace();
        int currentID = this.getNextWorkPlaceID();
        emptyWp.setId(currentID);
        emptyWp.setApplicant_id(Integer.parseInt(jLabHidden.getText()));

        return emptyWp;
    }

    private WorkPlace getSelectedWorkPlace() {

        int selectedRow = jWpTable.getSelectedRow();
        var selectedWorkPlace = new WorkPlace();

        if (selectedRow != -1) {

            int modelRow = jWpTable.convertRowIndexToModel(selectedRow);
            int currentID = Integer.parseInt(wpTabModel.getValueAt(modelRow, 0).toString());
            selectedWorkPlace.setId(currentID);
        }
        return selectedWorkPlace;
    }

    private void initListeners() {

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setColumnsSize();
            }
        });

        jBAdd.addActionListener(e -> addWorkPlace(getWorkPlace()));
        jBDelete.addActionListener(e -> deleteWorkPlace(getSelectedWorkPlace()));
        jBUpdate.addActionListener(e -> updateWorkPlace());
    }

    public void setNextWorkPlaceID(int nextWorkPlaceID) {
        this.nextWorkPlaceID = nextWorkPlaceID;
    }

    public int getNextWorkPlaceID() {
        return nextWorkPlaceID;
    }
}

