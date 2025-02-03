package org.ruarchie.dev;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class ApplicantTableModel<T extends Applicant> extends CustomTableModel<T> {

    private final int EDUCATION_ID = 6;
    private final List<Map<String, Object>> educations;

   // private final List<Map<String, Object>> applicants;

    public ApplicantTableModel(String inputStreamName, String tableName) {

        super();
        setTableData("select * from applicant");
        setColumns(inputStreamName, tableName);
        //setTableData();

        var dbQuery = new DBQuery("select * from education_type");
        educations = dbQuery.getData();

        //applicants = new ArrayList<>(tableData);
    }

    protected void updateEducationField() {

        Object[] rowArray = new Object[EDUCATION_ID + 1];

        for (int i = 0; i < getRowCount(); i++) {
            rowArray[EDUCATION_ID] = getValueAt(i, EDUCATION_ID);
            int educationId = Integer.parseInt(rowArray[EDUCATION_ID].toString());
            String educationName = getEducationName(educationId);
            setValueAt(educationName, i, EDUCATION_ID);
        }
    }

    private String getEducationName(int education_type) {

        String educationName = "";

        for (Map<String, Object> row : educations) {
            int rowID = Integer.parseInt(row.get("ID").toString());
            if (rowID == education_type) {
                educationName = row.get("NAME").toString();
                break;
            }
        }
        return educationName;
    }

    @Override
    protected void addRow(T entity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        Object[] array = {entity.getId(), entity.getName(), entity.getBirthday().format(formatter), entity.getMail(), entity.getPhone(), entity.getGender(), getEducationName(entity.getEducation_type()), entity.getInstitution()};
        this.addRow(array);
    }

    @Override
    protected final void deleteRow(int rownum) {
        this.removeRow(rownum);
    }

    @Override
    protected final void updateRow(T entity, int rowNum) {

        updateModelCell(rowNum, 1, entity.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        String birthdayStr = entity.getBirthday().format(formatter);
        //LocalDate birthday = LocalDate.parse(birthdayStr, formatter);
        updateModelCell(rowNum, 2, birthdayStr);
        updateModelCell(rowNum, 3, entity.getMail());
        updateModelCell(rowNum, 4, entity.getPhone());
        updateModelCell(rowNum, 5, entity.getGender());
        updateModelCell(rowNum, 6, getEducationName(entity.getEducation_type()));
        updateModelCell(rowNum, 7, entity.getInstitution());
    }

    public List<Map<String, Object>> getEducations() {
        return educations;
    }
}
