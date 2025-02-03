package org.ruarchie.dev;

public interface Crudable<T> {

    int create(T entity);

    void delete(T entity);

    void update(T entity);

    T[] read();
}