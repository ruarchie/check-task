package org.ruarchie.dev;

import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.LocalDate;

public class Applicant {
    private int id;
    private final String name;
    private final LocalDate birthday;
    private final String mail;
    private final String phone;
    private final char gender;
    private final int education_type;
    private final String institution;

    public Applicant(Map<String, Object> row) {
        try {
            this.id = Integer.parseInt(row.get("ID").toString());
            this.name = row.get("NAME").toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            this.birthday = LocalDate.parse(row.get("BIRTHDAY").toString(), formatter);
            this.mail = row.get("MAIL").toString();
            this.phone = row.get("PHONE").toString();
            this.gender = row.get("GENDER").toString().charAt(0);
            this.education_type = Integer.parseInt(row.get("EDUCATION_TYPE").toString());
            this.institution = row.get("INSTITUTION").toString();

        } catch (NullPointerException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public char getGender() {
        return gender;
    }

    public int getEducation_type() {
        return education_type;
    }

    public String getInstitution() {
        return institution;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", birthday=" + birthday +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", education_type=" + education_type +
                ", institution='" + institution + '\'' +
                '}';
    }
}
