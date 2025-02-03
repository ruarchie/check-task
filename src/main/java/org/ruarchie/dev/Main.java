package org.ruarchie.dev;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        var applData = new DBQuery("select * from applicant");
        var row = applData.getData().getFirst();

        if (row != null) {
            var app = new Applicant(row);

            try (Connection conn = DBConnection.getConnection()) {
                Applicants<Applicant> apps = new Applicants<>(conn);
                apps.addCreateSQL("INSERT INTO applicant(name, birthday, mail, phone, gender, education_type, institution) VALUES( ?, ?, ?, ?, ?, ?, ? );");
                int id = apps.create(app);

                apps.addUpdateSQL("UPDATE applicant SET name = ?, birthday = ?, mail = ?, phone = ?, gender = ?, education_type = ?, institution = ? WHERE id = ?");
                apps.update(app);

                app.setId(id);
                apps.addDeleteSQL("DELETE FROM applicant WHERE id = ?");
                apps.delete(app);

                apps.addReadSQL("SELECT * FROM applicant");
                var arr = apps.read();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}