package org.ruarchie.dev;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Applicants<T extends Applicant> extends Crud<T> {

    public Applicants(Connection conn) {
        super(conn);
    }

    @Override
    public int create(T entity) {

        try (PreparedStatement preparedStatement = conn.prepareStatement(createSQLText, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, entity.getName());
            Date birthday = Date.valueOf(entity.getBirthday());
            preparedStatement.setDate(2, birthday);
            preparedStatement.setString(3, entity.getMail());
            preparedStatement.setString(4, entity.getPhone());
            preparedStatement.setString(5, entity.getGender() + "");
            preparedStatement.setInt(6, entity.getEducation_type());
            preparedStatement.setString(7, entity.getInstitution());

            preparedStatement.executeUpdate();
            ResultSet keyResult = preparedStatement.getGeneratedKeys();

            return keyResult.next() ? keyResult.getInt(1) : 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(T entity) {

        try (PreparedStatement preparedStatement = conn.prepareStatement(updateSQLText)) {

            preparedStatement.setString(1, entity.getName());
            Date birthday = Date.valueOf(entity.getBirthday());
            preparedStatement.setDate(2, birthday);
            preparedStatement.setString(3, entity.getMail());
            preparedStatement.setString(4, entity.getPhone());
            preparedStatement.setString(5, entity.getGender() + "");
            preparedStatement.setInt(6, entity.getEducation_type());
            preparedStatement.setString(7, entity.getInstitution());
            preparedStatement.setInt(8, entity.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(T entity) {

        try (PreparedStatement preparedStatement = conn.prepareStatement(deleteSQLText)) {

            preparedStatement.setInt(1, entity.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T[] read() {

        List<Applicant> entitys = new ArrayList<>();

        var query = new DBQuery(readSQLText);

        for (Map<String, Object> row : query.getData()) {
            entitys.add(new Applicant(row));
        }
        Applicant[] app = new Applicant[entitys.size()];
        app = entitys.toArray(app);
        return (T[])app;
    }
}
