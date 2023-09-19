import connection.BasicConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws SQLException {
        BasicConnectionPool basicConnectionPool=
                BasicConnectionPool.create("jdbc:postgresql://127.0.0.1:5532/postgres", "postgres","123456");

        Connection con=basicConnectionPool.getConnection();

        Statement statement=con.createStatement();

        String createTable="create table names(id serial, name varchar, PRIMARY KEY (id))";
        statement.addBatch(createTable);
        String insert="insert into names values (1,'John')";
        statement.addBatch(insert);
        statement.executeBatch();
        basicConnectionPool.releaseConnection(con);

    }
}