package dao;

/**
 * @author alexglasov
 */
public class DataSourceInfo {
    private final String url;
    private final String user;
    private final String password;
    private final int minPoolSize;
    private final int maxPoolSize;
    private final int maxStatements;

    public DataSourceInfo(
            String url,
            String user,
            String password,
            int minPoolSize,
            int maxPoolSize,
            int maxStatements)
    {
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

    public int getMinPoolSize() {
        return minPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getMaxStatements() {
        return maxStatements;
    }
}
