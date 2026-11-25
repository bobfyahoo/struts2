package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class Database {

    private static HikariDataSource dataSource;

    private Database() {
    }

    public static synchronized void initialize() {

        if (dataSource != null) {
            return;
        }

        String url = System.getenv("DATABASE_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || username == null || password == null) {
            throw new IllegalStateException(
                "Database environment variables are missing"
            );
        }

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);

        config.setMaximumPoolSize(5);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(10000);
        config.setPoolName("TicketPool");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection()
            throws SQLException {

        initialize();

        return dataSource.getConnection();
    }

    public static synchronized void shutdown() {

        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}