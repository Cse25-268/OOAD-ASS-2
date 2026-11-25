package bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Opens the database connection and creates the tables if they are not there yet.
 * We use SQLite so the whole database is just one file (bank.db).
 * All the DAO classes share the one connection from this class.
 */
public class Database {

    private Connection connection;

    public Database(String url) throws SQLException {
        connection = DriverManager.getConnection(url);
        createTables();
        addDefaultUser();
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        try (Statement st = connection.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");

            st.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "username TEXT PRIMARY KEY, "
                    + "password TEXT NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS customers ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "first_name TEXT NOT NULL, "
                    + "surname TEXT NOT NULL, "
                    + "id_number TEXT NOT NULL UNIQUE, "
                    + "address TEXT, "
                    + "phone TEXT, "
                    + "employer_name TEXT, "
                    + "employer_address TEXT)");

            st.execute("CREATE TABLE IF NOT EXISTS accounts ("
                    + "account_number TEXT PRIMARY KEY, "
                    + "customer_id INTEGER NOT NULL, "
                    + "type TEXT NOT NULL, "
                    + "branch TEXT, "
                    + "balance REAL NOT NULL, "
                    + "date_opened TEXT NOT NULL, "
                    + "FOREIGN KEY (customer_id) REFERENCES customers(id))");

            st.execute("CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "account_number TEXT NOT NULL, "
                    + "type TEXT NOT NULL, "
                    + "amount REAL NOT NULL, "
                    + "balance_after REAL NOT NULL, "
                    + "description TEXT, "
                    + "date_time TEXT NOT NULL, "
                    + "FOREIGN KEY (account_number) REFERENCES accounts(account_number))");
        }
    }

    // there must be at least one user to log in with
    private void addDefaultUser() throws SQLException {
        UserDAO userDAO = new UserDAO(this);
        if (!userDAO.userExists("admin")) {
            userDAO.addUser("admin", "admin123");
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Could not close database: " + e.getMessage());
        }
    }
}
