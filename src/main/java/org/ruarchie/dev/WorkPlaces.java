package org.ruarchie.dev;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WorkPlaces<T extends WorkPlace> extends Crud<T> {

    public WorkPlaces(Connection conn) {
        super(conn);
    }

    @Override
    public int create(T entity) {

        try (PreparedStatement preparedStatement = conn.prepareStatement(createSQLText, PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, entity.getId());
            String begDateAsStr = String.valueOf(entity.getBeginDate());
            Date begDate = Date.valueOf(begDateAsStr);
            preparedStatement.setDate(2, begDate);

            String endDateAsStr = String.valueOf(entity.getEndDate());
            Date endDatum = !endDateAsStr.equals("null") ? Date.valueOf(endDateAsStr) : null;
            preparedStatement.setDate(3, endDatum);
            preparedStatement.setInt(4, entity.getApplicant_id());
            preparedStatement.setString(5, entity.getJobDescription());

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

            String begDateAsStr = String.valueOf(entity.getBeginDate());
            Date begDate = !begDateAsStr.equals("null") ? Date.valueOf(begDateAsStr) : null;
            preparedStatement.setDate(1, begDate);
            String endDate = String.valueOf(entity.getEndDate());
            Date endDatum = !Objects.equals(endDate, "null") ? Date.valueOf(endDate) : null;
            preparedStatement.setDate(2, endDatum);
            preparedStatement.setInt(3, entity.getApplicant_id());
            preparedStatement.setString(4, entity.getJobDescription());
            preparedStatement.setInt(5, entity.getId());

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

        List<WorkPlace> entitys = new ArrayList<>();

        var query = new DBQuery(readSQLText);

        for (Map<String, Object> row : query.getData()) {
            entitys.add(new WorkPlace(row));
        }
        WorkPlace[] wp = new WorkPlace[entitys.size()];
        wp = entitys.toArray(wp);
        return (T[]) wp;
    }
}
