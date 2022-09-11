package dao;

import java.sql.SQLException;
import java.util.Collection;

public interface CrudDao<TKey, TValue> {

    boolean create(Collection<TValue> values) throws SQLException;

    Collection<TValue> read(Collection<TKey> keys) throws SQLException;

    boolean update(Collection<TValue> values) throws SQLException;

    boolean delete(Collection<TKey> keys) throws SQLException;
}
