package org.ruarchie.dev;

import java.sql.Connection;

public abstract class Crud<T> implements Crudable<T> {

    protected Connection conn;
    protected String createSQLText;
    protected String readSQLText;
    protected String updateSQLText;
    protected String deleteSQLText;

    public Crud(Connection conn) {
        this.conn = conn;
    }

    public Crud<T> addReadSQL(String readSQLText) {
        this.setReadSQLText(readSQLText);
        return this;
    }

    public Crud<T> addDeleteSQL(String deleteSQLText) {
        this.setDeleteSQLText(deleteSQLText);
        return this;
    }

    public Crud<T> addUpdateSQL(String updateSQLText) {
        this.setUpdateSQLText(updateSQLText);
        return this;
    }

    public Crud<T> addCreateSQL(String createSQLText) {
        this.setCreateSQLText(createSQLText);
        return this;
    }

    public void setCreateSQLText(String createSQLText) {
        this.createSQLText = createSQLText;
    }

    public void setReadSQLText(String readSQLText) {
        this.readSQLText = readSQLText;
    }

    public void setUpdateSQLText(String updateSQLText) {
        this.updateSQLText = updateSQLText;
    }

    public void setDeleteSQLText(String deleteSQLText) {
        this.deleteSQLText = deleteSQLText;
    }

    public abstract int create(T entity);
}