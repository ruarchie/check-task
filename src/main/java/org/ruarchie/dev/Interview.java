package org.ruarchie.dev;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Map;

public class Interview {

    private int id;
    private final LocalDate eventDate;
    private int applicant_id;
    private final String result;

    public Interview(Map<String, Object> row) {
        this.id = Integer.parseInt(String.valueOf(row.get("ID")));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

        String eventDateAsStr = String.valueOf(row.get("EVENTDATE"));
        LocalDate eventDate = null;

        try {
            eventDate = LocalDate.parse(eventDateAsStr, formatter);

        } catch (DateTimeParseException e) {

            String warningText = String.format("<html>Неверное значение даты %s .<br/>Введите корректное значение.</html>", eventDateAsStr);
            var messageText = new JLabel(warningText, JLabel.CENTER);
            var messPane = new JOptionPane(messageText);
            var dialog = messPane.createDialog("Ошибка ввода!");
            dialog.setVisible(true);

            eventDate = LocalDate.now();
            throw e;

        } finally {
            this.eventDate = eventDate;
        }

        String applicant_id = String.valueOf(row.get("APPLICANT_ID"));
        this.applicant_id = Integer.parseInt(applicant_id);
        this.result = String.valueOf(row.get("RESULT"));
    }

    public Interview() {
        this.id = 0;
        this.eventDate = LocalDate.now();
        this.applicant_id = 0;
        this.result = "";
    }

    public int getId() {
        return id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public int getApplicant_id() {
        return applicant_id;
    }

    public String getResult() {
        return result;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setApplicant_id(int applicant_id) {
        this.applicant_id = applicant_id;
    }
}
