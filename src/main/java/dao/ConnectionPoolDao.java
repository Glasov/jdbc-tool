package dao;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author alexglasov
 */
public class ConnectionPoolDao<TKey, TValue> extends JdbcDao<TKey, TValue> {
    private static ComboPooledDataSource dataSource = new ComboPooledDataSource();

    public ConnectionPoolDao(Class<TKey> tKeyClass, Class<TValue> tValueClass, DataSourceInfo dataSourceInfo)
            throws PropertyVetoException
    {
        super(tKeyClass, tValueClass);
        dataSource.setDriverClass(dataSourceInfo.getDriver());
        dataSource.setJdbcUrl(dataSourceInfo.getUrl());
        dataSource.setUser(dataSourceInfo.getUser());
        dataSource.setPassword(dataSourceInfo.getPassword());
        if (dataSourceInfo.getMinPoolSize().isPresent()) {
            dataSource.setMinPoolSize(dataSourceInfo.getMinPoolSize().get());
        }
        if (dataSourceInfo.getMaxPoolSize().isPresent()) {
            dataSource.setMaxPoolSize(dataSourceInfo.getMaxPoolSize().get());
        }
        if (dataSourceInfo.getMaxStatements().isPresent())  {
            dataSource.setMaxStatements(dataSourceInfo.getMaxStatements().get());
        }
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public String getName() {
        return null;
    }
}
