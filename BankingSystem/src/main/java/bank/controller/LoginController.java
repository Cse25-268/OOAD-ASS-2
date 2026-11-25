package bank.controller;

import bank.dao.UserDAO;
import bank.model.BankException;

import java.sql.SQLException;

/**
 * Handles logging in for the bank staff.
 * The LoginView calls this class, it does not talk to the database itself.
 */
public class LoginController {

    private UserDAO userDAO;
    private String loggedInUser;

    public LoginController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // returns true if the login worked
    public boolean login(String username, String password) throws BankException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new BankException("Please enter both username and password");
        }
        try {
            if (userDAO.checkLogin(username.trim(), password)) {
                loggedInUser = username.trim();
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new BankException("Database error: " + e.getMessage());
        }
    }

    public void logout() {
        loggedInUser = null;
    }

    public boolean isLoggedIn() {
        return loggedInUser != null;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }
}
