package org.ruarchie.dev;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Interviews<T extends Interview> extends Crud<T> {

    public Interviews(Connection conn) {
        super(conn);
    }

    @Override
    public int create(T entity) {

        try (PreparedStatement preparedStatement = conn.prepareStatement(createSQLText, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, entity.getId());
            String eventDateAsStr = String.valueOf(entity.getEventDate());
            Date eventDate = Date.valueOf(eventDateAsStr);
            preparedStatement.setDate(2, eventDate);
            preparedStatement.setInt(3, entity.getApplicant_id());
            preparedStatement.setString(4, entity.getResult());

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

            String eventDateAsStr = String.valueOf(entity.getEventDate());
            Date eventDate = !eventDateAsStr.equals("null") ? Date.valueOf(eventDateAsStr) : null;
            preparedStatement.setDate(1, eventDate);
            preparedStatement.setInt(2, entity.getApplicant_id());
            preparedStatement.setString(3, entity.getResult());
            preparedStatement.setInt(4, entity.getId());

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

        List<Interview> entitys = new ArrayList<>();

        var query = new DBQuery(readSQLText);

        for (Map<String, Object> row : query.getData()) {
            entitys.add(new Interview(row));
        }
        Interview[] interviewArray = new Interview[entitys.size()];
        interviewArray = entitys.toArray(interviewArray);
        return (T[]) interviewArray;
    }
}
