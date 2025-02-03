package org.ruarchie.dev;

import javax.swing.*;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.LocalDate;

public class WorkPlace {

    private int id;
    private final LocalDate beginDate;
    private final LocalDate endDate;
    private int applicant_id;
    private final String jobDescription;

    public WorkPlace(Map<String, Object> row) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

        this.id = Integer.parseInt(String.valueOf(row.get("ID")));

        String beginDateAsStr = String.valueOf(row.get("BEGINDATE"));
        LocalDate beginDate = null;

        try {
            beginDate = LocalDate.parse(beginDateAsStr, formatter);

        } catch (DateTimeParseException e) {

            String warningText = String.format("<html>Неверное значение даты %s .<br/>Введите корректное значение.</html>", beginDateAsStr);
            var messageText = new JLabel(warningText, JLabel.CENTER);
            var messPane = new JOptionPane(messageText);
            var dialog = messPane.createDialog("Ошибка ввода!");
            dialog.setVisible(true);

            beginDate = LocalDate.now();
            throw e;

        } finally {
            this.beginDate = beginDate;
        }

        String endDateAsStr = String.valueOf(row.get("ENDDATE"));
        LocalDate endDate = null;

        try {
            endDate = LocalDate.parse(endDateAsStr, formatter);

        } catch (DateTimeParseException e) {
//            endDate = null;

        } finally {
            this.endDate = endDate;
        }

        String applicant_id = String.valueOf(row.get("APPLICANT_ID"));
        this.applicant_id = Integer.parseInt(applicant_id);
        this.jobDescription = String.valueOf(row.get("JOBDESCRIPTION"));
    }

    public WorkPlace() {
        this.id = 0;
        this.beginDate = LocalDate.now();
        this.endDate = LocalDate.now();
        this.applicant_id = 0;
        this.jobDescription = "";
    }

    public int getId() {
        return id;
    }

    public LocalDate getBeginDate() {
        return beginDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getApplicant_id() {
        return applicant_id;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setApplicant_id(int applicant_id) {
        this.applicant_id = applicant_id;
    }
}
