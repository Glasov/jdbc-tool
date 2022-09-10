package dao;

import java.util.Collection;

public interface CrudDao<TKey, TValue> {

    boolean create(Collection<TValue> values);

    Collection<TValue> read(Collection<TKey> keys);

    boolean update(Collection<TValue> values);

    boolean delete(Collection<TKey> keys);
}
