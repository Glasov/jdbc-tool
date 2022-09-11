package dao;

import java.util.Optional;

/**
 * @author alexglasov
 */
public class DataSourceInfo {
    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private final Optional<Integer> minPoolSize;
    private final Optional<Integer> maxPoolSize;
    private final Optional<Integer> maxStatements;

    public DataSourceInfo(
            String driver,
            String url,
            String user,
            String password,
            Optional<Integer> minPoolSize,
            Optional<Integer> maxPoolSize,
            Optional<Integer> maxStatements)
    {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.minPoolSize = minPoolSize;
        this.maxPoolSize = maxPoolSize;
        this.maxStatements = maxStatements;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    public Optional<Integer> getMinPoolSize() {
        return minPoolSize;
    }

    public Optional<Integer> getMaxPoolSize() {
        return maxPoolSize;
    }

    public Optional<Integer> getMaxStatements() {
        return maxStatements;
    }
}
