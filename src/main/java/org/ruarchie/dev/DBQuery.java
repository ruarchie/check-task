package org.ruarchie.dev;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DBQuery implements Queryable {

    private final String sqlText;
    private final List<Map<String, Object>> queryData;

    public DBQuery(String sqlText) {
        this.sqlText = sqlText;
        queryData = new ArrayList<>();
    }

    public List<Map<String, Object>> getData() {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sqlText)) {

            ResultSet queryResult = preparedStatement.executeQuery();

            ResultSetMetaData metaResult = queryResult.getMetaData();
            int columnsCount = metaResult.getColumnCount();

            while (queryResult.next()) {

                Map<String, Object> row = new LinkedHashMap<>();

                for (int i = 1; i <= columnsCount; i++) {

                    String columnName = metaResult.getColumnName(i);
                    String typeData = metaResult.getColumnClassName(i);
                    Object value = queryResult.getObject(i);

                    if (typeData.equals("java.sql.Date") && value != null) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
                        LocalDate valueAsDate = LocalDate.parse(String.valueOf(value), formatter);

                        DateTimeFormatter formatterToString = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
                        row.put(columnName, valueAsDate.format(formatterToString));
                    } else {
                        row.put(columnName, value);
                    }
                }
                queryData.add(row);
            }
        } catch (SQLException | DateTimeParseException e) {
            throw new RuntimeException(e);
        }
        return queryData;
    }
}
