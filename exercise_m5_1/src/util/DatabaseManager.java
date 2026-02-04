package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "m5_1_db";
    private static final String FULL_URL = URL + DB_NAME
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Los_Angeles";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            return DriverManager.getConnection(FULL_URL, USER, PASSWORD);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049) {
                System.out.println("Database does not exist. Initializing...");
                initializeDatabase();
                return DriverManager.getConnection(FULL_URL, USER, PASSWORD);
            }
            throw e;
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement()) {

            String sqlScript = new String(Files.readAllBytes(Paths.get("init_db.sql")));
            String[] statements = sqlScript.split(";");

            for (String sql : statements) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql);
                }
            }
            System.out.println("Database initialized successfully.");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Failed to initialize database.");
        }
    }
}
