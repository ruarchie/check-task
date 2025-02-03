package org.ruarchie.dev;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Educations<T extends Education> extends Crud<T> {

    public Educations(Connection conn) {
        super(conn);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] read() {

        List<Education> entitys = new ArrayList<>();

        var query = new DBQuery(readSQLText);

        int i = 0;
        for (Map<String, Object> row : query.getData()) {
            if (row.get("ID") != null) {
                entitys.add(new Education((int) row.get("ID"), row.get("NAME").toString()));
            } else {
                entitys.add(new Education(++i, row.get("NAME").toString()));
            }
        }
        Education[] edu = new Education[entitys.size()];
        edu = entitys.toArray(edu);
        return (T[]) edu;
    }

    @Override
    public int create(T entity) {
        return 0;
    }

    @Override
    public void delete(T entity) {

    }

    @Override
    public void update(T entity) {

    }
}


