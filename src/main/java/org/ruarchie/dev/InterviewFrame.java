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

public class InterviewFrame extends JFrame {
    private JPanel jPanelInt;
    private JLabel jLabHeader;
    private JLabel jLabHidden;
    private JScrollPane jInterviewSpanel;
    private JTable jIntTable;
    private JPanel jPanelButton;
    private JButton jBAdd;
    private JButton jBDelete;
    private JButton jBUpdate;
    private final InterviewTableModel<Interview> intTabModel;
    private int interviewID;

    public InterviewFrame(int applicantId) {
        setTitle("Список пройденных интервью");
        setSize(640, 480);
        setContentPane(jPanelInt);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jLabHidden.setVisible(false);
        jLabHidden.setText(String.valueOf(applicantId));
        initIntID();

        intTabModel = new InterviewTableModel<>("target/classes/resources/interview.xml", applicantId);
        intTabModel.addAliases();
        intTabModel.addData();
        jIntTable.setModel(intTabModel);
        jIntTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

//        jIntTable.setDefaultRenderer(LocalDate.class, new DateRenderer());
        jIntTable.setDefaultEditor(String.class, new DateEditor());

        setColumnsSize();
        initListeners();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0,0,dim.width-200, dim.height-200);
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        //pack();
        setVisible(true);
    }

    private void initIntID() {

        var dbQuery = new DBQuery("SELECT MAX(id) id FROM interview");
        var listMaxId = dbQuery.getData();

        Object ObjectID = listMaxId.getFirst().get("ID");
        if (ObjectID == null) {
            setIntID(1);
        } else {
            String ObjectIDAsStr = String.valueOf(listMaxId.getFirst().get("ID"));
            int nextId = Integer.parseInt(ObjectIDAsStr) + 1;
            setIntID(nextId);
        }
    }

    private void setColumnsSize() {

        float[] columnWidthPercentage = {0.05f, 0.2f, 0.0f, 0.75f};
        TableColumn column;
        TableColumnModel jTableColumnModel = jIntTable.getColumnModel();

        int commonWidth = jTableColumnModel.getTotalColumnWidth();
        for (int i = 0; i < jTableColumnModel.getColumnCount(); i++) {
            column = jTableColumnModel.getColumn(i);
            int preffWidth = Math.round(columnWidthPercentage[i] * commonWidth);
            column.setPreferredWidth(preffWidth);
            if (i == 2) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setWidth(0);
            }
        }
    }

    private void addInterview(Interview jInterview) {

        intTabModel.addRow(jInterview);
        this.setIntID(getIntID() + 1);
    }

    private void deleteInterview(Interview jInterview) {

        try (Connection conn = DBConnection.getConnection()) {

            Interviews<Interview> inter = new Interviews<>(conn);
            inter.addDeleteSQL("DELETE FROM interview WHERE id = ?");
            inter.delete(jInterview);
            int selectedRow = jIntTable.getSelectedRow();
            if (selectedRow != -1) {
                intTabModel.deleteRow(jIntTable.convertRowIndexToModel(selectedRow));
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void updateInterview() {

        List<Interview> intNewId = new ArrayList<>();
        List<Interview> intUpdId = new ArrayList<>();

        List<Map<String, Object>> initialInterviews = intTabModel.getInterviews();

        int initialWpSize = initialInterviews.size();
        int jTableSize = intTabModel.getRowCount();
        int columnsCount = intTabModel.getColumnCount();
        List<String> columns = intTabModel.getColumns();

        for (int i = 0; i < jTableSize; i++) { //Jtable

            Map<String, Object> jTableRow = new LinkedHashMap<>();
            int jTableId = Integer.parseInt(intTabModel.getValueAt(i, 0).toString());

            for (int h = 0; h < columnsCount; h++) {
                jTableRow.put(columns.get(h), intTabModel.getValueAt(i, h));
            }
            int countUnIdent = 0;

            for (var iniInt : initialInterviews) { //Before manipulate
                int iniRowId = Integer.parseInt(String.valueOf(iniInt.get("ID")));

                if (iniRowId == jTableId) {
                    intUpdId.add(new Interview(jTableRow));
                } else {
                    countUnIdent++;
                }
            }
            if (countUnIdent == initialWpSize) {
                intNewId.add(new Interview(jTableRow));
            }
        }

        try (Connection conn = DBConnection.getConnection()) {

            Interviews<Interview> ints = new Interviews<>(conn);
            ints.addCreateSQL("INSERT INTO interview(id, eventdate, applicant_id, result) VALUES(?, ?, ?, ?)");
            for (Interview inter : intNewId) {
                ints.create(inter);
            }
            ints.addUpdateSQL("UPDATE interview SET eventdate = ?, applicant_id = ?, result = ? WHERE id = ?");
            for (Interview inter : intUpdId) {
                ints.update(inter);
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private Interview getInterview() {

        var emptyInt = new Interview();
        int currentID = this.getIntID();
        emptyInt.setId(currentID);
        emptyInt.setApplicant_id(Integer.parseInt(jLabHidden.getText()));

        return emptyInt;
    }

    private Interview getSelectedInterview() {

        int selectedRow = jIntTable.getSelectedRow();
        var selectedInterview = new Interview();

        if (selectedRow != -1) {

            int modelRow = jIntTable.convertRowIndexToModel(selectedRow);
            int currentID = Integer.parseInt(intTabModel.getValueAt(modelRow, 0).toString());
            selectedInterview.setId(currentID);
        }
        return selectedInterview;
    }

    private void initListeners() {

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setColumnsSize();
            }
        });

        jBAdd.addActionListener(e -> addInterview(getInterview()));
        jBDelete.addActionListener(e -> deleteInterview(getSelectedInterview()));
        jBUpdate.addActionListener(e -> updateInterview());
    }

    public void setIntID(int interviewID) {
        this.interviewID = interviewID;
    }

    public int getIntID() {
        return interviewID;
    }
}

