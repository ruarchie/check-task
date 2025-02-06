package org.ruarchie.dev;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WorkPlaceTableModel<T extends WorkPlace> extends CustomTableModel<T> {

    private final List<Map<String, Object>> workPlaces;

    public WorkPlaceTableModel(String inputStreamName, int applicantId) {

        super();
        setTableData(String.format("select * from workplace where applicant_id = %s order by id", applicantId));
        setTableColumns("workplace");
        setAliases(inputStreamName);
        workPlaces = new ArrayList<>(tableData);
    }

    @Override
    protected void addRow(T entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        Object[] array = {entity.getId(), entity.getBeginDate().format(formatter), entity.getEndDate().format(formatter), entity.getApplicant_id(), entity.getJobDescription()};
        this.addRow(array);
    }

    @Override
    protected final void deleteRow(int rownum) {
        this.removeRow(rownum);
    }

    @Override
    protected final void updateRow(T entity, int rowNum) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        String beginDateStr = entity.getBeginDate().format(formatter);
        LocalDate beginDate = LocalDate.parse(beginDateStr, formatter);
        String endDateStr = entity.getEndDate().format(formatter);
        LocalDate endDate = LocalDate.parse(endDateStr, formatter);
        updateModelCell(rowNum, 1, beginDate);
        updateModelCell(rowNum, 2, endDate);
        updateModelCell(rowNum, 3, entity.getApplicant_id());
        updateModelCell(rowNum, 4, entity.getJobDescription());
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return i1 != 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> className = Object.class;

        if (columnIndex == 2 || columnIndex == 1) {
            className = String.class;
        }
        return className;
    }

    public List<Map<String, Object>> getWorkPlaces() {
        return workPlaces;
    }
}
