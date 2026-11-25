package bank.view;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.controller.LoginController;
import bank.model.BankException;
import bank.util.SampleData;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * The main window that is shown after login. It has a tab for customers
 * and a tab for accounts, and a toolbar at the top.
 */
public class MainView {

    private Stage stage;
    private LoginController loginController;
    private CustomerController customerController;
    private AccountController accountController;
    private Runnable onLogout;

    private CustomerView customerView;
    private AccountView accountView;

    public MainView(Stage stage, LoginController loginController, CustomerController customerController,
                    AccountController accountController, Runnable onLogout) {
        this.stage = stage;
        this.loginController = loginController;
        this.customerController = customerController;
        this.accountController = accountController;
        this.onLogout = onLogout;
    }

    public void show() {
        customerView = new CustomerView(customerController);
        accountView = new AccountView(customerController, accountController);

        Tab customerTab = new Tab("Customers", customerView.getContent());
        customerTab.setClosable(false);
        Tab accountTab = new Tab("Accounts", accountView.getContent());
        accountTab.setClosable(false);

        TabPane tabs = new TabPane(customerTab, accountTab);
        // when the accounts tab is opened, reload the customers in case new ones were added
        accountTab.setOnSelectionChanged(e -> {
            if (accountTab.isSelected()) {
                accountView.refresh();
            }
        });

        // ---- toolbar ----
        Label userLabel = new Label("Logged in as: " + loginController.getLoggedInUser());

        Button interestButton = new Button("Pay Monthly Interest");
        interestButton.setOnAction(e -> handlePayInterest());

        Button sampleButton = new Button("Load 10 Sample Records");
        sampleButton.setOnAction(e -> handleSampleData());

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            loginController.logout();
            onLogout.run();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, userLabel, spacer, interestButton, sampleButton, logoutButton);
        toolbar.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(tabs);

        stage.setTitle("Banking System");
        stage.setScene(new Scene(root, 950, 700));
        stage.show();
    }

    private void handlePayInterest() {
        try {
            double total = accountController.payMonthlyInterest();
            Messages.info("Monthly interest paid. Total interest: BWP" + String.format("%.2f", total));
            accountView.reloadCurrent();
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void handleSampleData() {
        try {
            int added = SampleData.load(customerController, accountController);
            Messages.info(added + " sample customers added.");
            customerView.refresh();
            accountView.refresh();
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }
}
