package tech.kuraudo.server;

import java.sql.*;

public class GateKeeper {

    private static Connection connection;
    private static Statement stmt;

    public GateKeeper() {
        try {
            Class.forName("org.sqlite.JDBC");
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:kuraudo.db");
        stmt = connection.createStatement();
    }

    public void disconnect() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfUserExists(String login) throws SQLException {
        boolean result = false;
        PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM users WHERE login = ?");
        ps.setString(1, login);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result = true;
            }
        }

        return result;
    }

    public boolean registerUser(String login, String password, String email) {
        boolean result = false;
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO users login, password, email) VALUES (?, ?, ?)");
            ps.setString(1, login);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.executeUpdate();
            result = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return result;
    }

    public boolean authorizeUser(String login, String password) throws SQLException {
        boolean result = false;
        PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM users WHERE login = ? AND pass = ?");
        ps.setString(1, login);
        ps.setString(2, password);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result = true;
            }
        }

        return result;
    }
}
