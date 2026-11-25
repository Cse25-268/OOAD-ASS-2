package bank;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.controller.LoginController;
import bank.dao.AccountDAO;
import bank.dao.CustomerDAO;
import bank.dao.Database;
import bank.dao.TransactionDAO;
import bank.dao.UserDAO;
import bank.view.LoginView;
import bank.view.MainView;
import bank.view.Messages;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;

/**
 * Starts the program. It creates the database, DAOs and controllers
 * and joins them together, then shows the login screen.
 *
 * Default login: admin / admin123
 */
public class Main extends Application {

    private Database database;

    @Override
    public void start(Stage stage) {
        try {
            // data layer
            database = new Database("jdbc:sqlite:bank.db");
            UserDAO userDAO = new UserDAO(database);
            CustomerDAO customerDAO = new CustomerDAO(database);
            AccountDAO accountDAO = new AccountDAO(database, customerDAO);
            TransactionDAO transactionDAO = new TransactionDAO(database);

            // controllers
            LoginController loginController = new LoginController(userDAO);
            CustomerController customerController = new CustomerController(customerDAO, accountDAO);
            AccountController accountController = new AccountController(accountDAO, transactionDAO);

            // views
            LoginView loginView = new LoginView(stage, loginController, () -> {
                MainView mainView = new MainView(stage, loginController, customerController, accountController,
                        () -> showLogin(stage, loginController, customerController, accountController));
                mainView.show();
            });
            loginView.show();

        } catch (SQLException e) {
            Messages.error("Could not open the database: " + e.getMessage());
        }
    }

    // goes back to the login screen after logout
    private void showLogin(Stage stage, LoginController loginController, CustomerController customerController,
                           AccountController accountController) {
        LoginView loginView = new LoginView(stage, loginController, () -> {
            MainView mainView = new MainView(stage, loginController, customerController, accountController,
                    () -> showLogin(stage, loginController, customerController, accountController));
            mainView.show();
        });
        loginView.show();
    }

    @Override
    public void stop() {
        if (database != null) {
            database.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
