package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BasicConnectionPool implements ConnectionPool {
    private String url;
    private String user;
    private String password;
    private List<Connection> connectionPool;
    private List<Connection> usedConnections=new ArrayList<>();
    private static final int INITIAL_POOL_SIZE=5;
    private static final int MAX_TIMEOUT = 10;
    private static final int MAX_POOL_SIZE = 10;



    public BasicConnectionPool(String url, String user, String password, List<Connection> connectionPool) {
        this.url=url;
        this.user=user;
        this.password=password;
        this.connectionPool=connectionPool;
    }

    public static BasicConnectionPool create(String url,String user, String password) throws SQLException {
        List<Connection> pool=new ArrayList<>(INITIAL_POOL_SIZE);
        for (int i=0;i<INITIAL_POOL_SIZE;i++){
            pool.add(createConnection(url,user,password));
        }
        return new BasicConnectionPool(url,user,password,pool);
    }

    private static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }


    @Override
    public Connection getConnection() throws SQLException {
        if (connectionPool.isEmpty()) {
            if (usedConnections.size() < MAX_POOL_SIZE) {
                connectionPool.add(createConnection(url, user, password));
            } else {
                throw new RuntimeException(
                        "Maximum pool size reached, no available connections!");
            }
        }

        Connection connection = connectionPool
                .remove(connectionPool.size() - 1);

        if(!connection.isValid(MAX_TIMEOUT)){
            connection = createConnection(url, user, password);
        }

        usedConnections.add(connection);
        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        return usedConnections.remove(connection);
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public int getPoolSize(){
        return connectionPool.size()+usedConnections.size();
    }
}
