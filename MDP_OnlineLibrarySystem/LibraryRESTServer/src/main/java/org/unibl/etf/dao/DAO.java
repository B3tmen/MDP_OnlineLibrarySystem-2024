package org.unibl.etf.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/** Data Access Object pattern generic interface*/
public interface DAO <T> {
    T get(int id) throws IOException;

    List<T> getAll() throws IOException;

    int insert(T object) throws IOException;

    int update(T object) throws IOException;

    int delete(T object) throws IOException;
}
