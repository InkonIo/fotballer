package db;

import java.sql.*;

public class MyJDBC {
    private static final String DB_URL = "jdbc:sqlite:database.db";

    // Создание таблицы, если она не существует
    private static void createTableIfNotExists() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL" +
                ");";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean register(String username, String password) {
        createTableIfNotExists(); // Убедимся, что таблица есть

        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement insertUser = connection.prepareStatement(
                     "INSERT INTO users (username, password) VALUES(?, ?)")) {

            if (!checkUser(username)) {
                insertUser.setString(1, username);
                insertUser.setString(2, password);
                insertUser.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                System.out.println("Успешное подключение к базе данных!");
                createTableIfNotExists(); // Проверяем таблицу при запуске
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к БД: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkUser(String username) {
        createTableIfNotExists(); // Проверяем таблицу перед выполнением запроса

        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement checkUserExists = connection.prepareStatement(sql)) {

            checkUserExists.setString(1, username);
            ResultSet resultSet = checkUserExists.executeQuery();

            return resultSet.next(); // Если есть запись, значит, пользователь существует
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateLogin(String username, String password) {
        createTableIfNotExists(); // Проверяем таблицу перед выполнением запроса

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL);
             PreparedStatement validateUser = connection.prepareStatement(sql)) {

            validateUser.setString(1, username);
            validateUser.setString(2, password);
            ResultSet resultSet = validateUser.executeQuery();

            return resultSet.next(); // Если есть совпадение, логин верный
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
