package org.ruarchie.dev;

import org.h2.tools.RunScript;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private JPanel jPanelMain;
    private JLabel jLabHeader;
    private JTextField jTFName;
    private JTextField jTFMail;
    private JTextField jTFPhone;
    private JFormattedTextField jFTFBirthday;
    private JComboBox<String> jCBGender;
    private JComboBox<String> jCBEducation;
    private JLabel jLabName;
    private JLabel jLabBirthday;
    private JLabel jLabMail;
    private JLabel jLabPhone;
    private JLabel jLabGender;
    private JLabel jLabEducation;
    private JPanel jPanelButtonTop;
    private JButton jBAdd;
    private JButton jBUpdate;
    private JScrollPane jPanelApplicants;
    private JPanel jPanelButtonBottom;
    private JButton jBDelete;
    private JPanel jPanelStatus;
    private JLabel jLabStatus;
    private JLabel jLabInstitution;
    private JTextField jTFInstitution;
    private JLabel jLabHidden;
    private final ApplicantTableModel<Applicant> appTabModel;
    private JTable jAppTable;
    private JButton jBWorkPlace;
    private JLabel jLabSeparator;
    private JButton jBInterview;
    private WorkPlaceFrame jWorkPlaceFrame;

    private Applicant getJApplicant() {

        Map<String, Object> applRow = new LinkedHashMap<>();
        applRow.put("ID", 0);
        applRow.put("NAME", jTFName.getText());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
        String birthDayStr = jFTFBirthday.getText();
        try {
            LocalDate birthday = LocalDate.parse(birthDayStr, formatter);
            applRow.put("BIRTHDAY", birthday);

        } catch (DateTimeParseException e) {
            LocalDate birthday = LocalDate.now();
            jFTFBirthday.setText(birthday.format(formatter));
            applRow.put("BIRTHDAY", birthday);
        }

        // applRow.put("BIRTHDAY", birthday);

        applRow.put("MAIL", jTFMail.getText());
        applRow.put("PHONE", jTFPhone.getText());

        char gender = String.valueOf(jCBGender.getSelectedItem()).charAt(0);
        applRow.put("GENDER", gender);

        String eduName = String.valueOf(jCBEducation.getSelectedItem());
        int eduId = 99;
        for (var row : appTabModel.getEducations()) {
            if (row.get("NAME").toString().equals(eduName)) {
                eduId = (int) row.get("ID");
                break;
            }
        }
        applRow.put("EDUCATION_TYPE", eduId);
        applRow.put("INSTITUTION", jTFInstitution.getText());

        return new Applicant(applRow);
    }

    private int putJApplicant(Applicant jApplicant) {

        int id;
        try (Connection conn = DBConnection.getConnection()) {

            Applicants<Applicant> applicants = new Applicants<>(conn);
            applicants.addCreateSQL("INSERT INTO applicant(name, birthday, mail, phone, gender, education_type, institution) VALUES( ?, ?, ?, ?, ?, ?, ? );");
            id = applicants.create(jApplicant);

            jApplicant.setId(id);
            appTabModel.addRow(jApplicant);

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return id;
    }

    private void deleteJApplicant(Applicant jApplicant) {

        try (Connection conn = DBConnection.getConnection()) {

            Applicants<Applicant> applicants = new Applicants<>(conn);
            jApplicant.setId(Integer.parseInt(jLabHidden.getText()));
            applicants.addDeleteSQL("DELETE FROM applicant WHERE id = ?");
            applicants.delete(jApplicant);
            int selectedRow = jAppTable.getSelectedRow();
            if (selectedRow != -1) {
                appTabModel.deleteRow(jAppTable.convertRowIndexToModel(selectedRow));
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void updateJApplicant(Applicant jApplicant) {

        try (Connection conn = DBConnection.getConnection()) {

            Applicants<Applicant> applicants = new Applicants<>(conn);
            jApplicant.setId(Integer.parseInt(jLabHidden.getText()));
            applicants.addUpdateSQL("UPDATE applicant SET name = ?, birthday = ?, mail = ?, phone = ?, gender = ?, education_type = ?, institution = ? WHERE id = ?");
            applicants.update(jApplicant);
            int selectedRow = jAppTable.getSelectedRow();
            appTabModel.updateRow(getJApplicant(), jAppTable.convertRowIndexToModel(selectedRow));

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initJCB() {

        try (Connection conn = DBConnection.getConnection()) {
            Educations<Education> educations = new Educations<>(conn);
            educations.addReadSQL("SELECT * FROM education_type");

            for (Education e : educations.read()) {
                jCBEducation.addItem(String.valueOf(e.name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        jCBGender.addItem("Ж");
        jCBGender.addItem("М");
    }

    private void setJComponentsValues() {

        int selectedRow = jAppTable.getSelectedRow();
        if (selectedRow != -1) {

            int modelRow = jAppTable.convertRowIndexToModel(selectedRow);
            // Object modelvalue = tabModel.getValueAt(modelRow, 1);

            jLabHidden.setText(appTabModel.getValueAt(modelRow, 0).toString());

            jTFName.setText(appTabModel.getValueAt(modelRow, 1).toString());
            jFTFBirthday.setText(appTabModel.getValueAt(modelRow, 2).toString());
            jTFMail.setText(appTabModel.getValueAt(modelRow, 3).toString());
            jTFPhone.setText(appTabModel.getValueAt(modelRow, 4).toString());
            jCBGender.setSelectedItem(appTabModel.getValueAt(modelRow, 5).toString());
            jCBEducation.setSelectedItem(appTabModel.getValueAt(modelRow, 6).toString());
            jTFInstitution.setText(appTabModel.getValueAt(modelRow, 7).toString());
        }
    }

    private void showWorkPlaces() {
        int selectedRow = jAppTable.getSelectedRow();
        if (selectedRow != -1) {
            jWorkPlaceFrame = new WorkPlaceFrame(Integer.parseInt(jLabHidden.getText()));
        }
    }

    private void showInterview() {
        if (jAppTable.getSelectedRow() != -1) {
            new InterviewFrame(Integer.parseInt(jLabHidden.getText()));
        }
    }

    private void initListeners() {
        jBAdd.addActionListener(e -> putJApplicant(getJApplicant()));
        jBDelete.addActionListener(e -> deleteJApplicant(getJApplicant()));
        jBUpdate.addActionListener(e -> updateJApplicant(getJApplicant()));
        jBWorkPlace.addActionListener(e -> showWorkPlaces());
        jBInterview.addActionListener(e -> showInterview());
        jAppTable.getSelectionModel().addListSelectionListener(e -> setJComponentsValues());
    }

    public MainFrame() {
        setTitle("Система ведения соискателей");
        setSize(640, 480);
        setContentPane(jPanelMain);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            jFTFBirthday.setColumns(10);
            MaskFormatter dateMask = new MaskFormatter("##-##-####");
            dateMask.install(jFTFBirthday);
            jLabHidden.setVisible(false);

        } catch (ParseException | NumberFormatException ex) {
            jLabStatus.setText("Неверный формат даты");
        }

        appTabModel = new ApplicantTableModel<>("target/classes/resources/applicant.xml", "applicant");
        appTabModel.addAliases();
        appTabModel.addData();
        appTabModel.updateEducationField();
        jAppTable.setModel(appTabModel);

        initJCB();
        initListeners();

        //pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0,0,dim.width-100, dim.height-100);
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        setVisible(true);
    }

    public static void main(String[] args) {

        try (FileReader fileRead = new FileReader("target/classes/resources/h2init.sql")) {

            RunScript.execute(DBConnection.getConnection(), fileRead);

        } catch (FileNotFoundException | SQLException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        MainFrame mainFrame = new MainFrame();
    }
}