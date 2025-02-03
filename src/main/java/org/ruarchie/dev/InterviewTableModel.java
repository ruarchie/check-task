package org.ruarchie.dev;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InterviewTableModel <T extends Interview> extends CustomTableModel<T> {

    private final List<Map<String, Object>> interviews;

    public InterviewTableModel(String inputStreamName, int applicantId) {

        super();
        setTableData(String.format("select * from interview where applicant_id = %s order by id", applicantId));
        setColumns(inputStreamName, "interview");
        interviews = new ArrayList<>(tableData);
    }

    @Override
    protected void addRow(T entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        Object[] array = {entity.getId(), entity.getEventDate().format(formatter), entity.getApplicant_id(), entity.getResult()};
        this.addRow(array);
    }

    @Override
    protected final void deleteRow(int rownum) {
        this.removeRow(rownum);
    }

    @Override
    protected final void updateRow(T entity, int rowNum) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        String eventDateStr = entity.getEventDate().format(formatter);
        LocalDate eventDate = LocalDate.parse(eventDateStr, formatter);
        updateModelCell(rowNum, 1, eventDate);
        updateModelCell(rowNum, 2, entity.getApplicant_id());
        updateModelCell(rowNum, 3, entity.getResult());
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return i1 != 0;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {

        Class<?> className = Object.class;

        if (columnIndex == 1) {
            className = String.class;
        }
        return className;
    }

    public List<Map<String, Object>> getInterviews() {
        return interviews;
    }

}
