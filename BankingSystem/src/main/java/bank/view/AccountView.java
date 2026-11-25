package bank.view;

import bank.controller.AccountController;
import bank.controller.CustomerController;
import bank.model.Account;
import bank.model.BankException;
import bank.model.Customer;
import bank.model.Transaction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Boundary class for opening accounts, depositing, withdrawing
 * and looking at the transaction history.
 * All the real work is done by the AccountController.
 */
public class AccountView {

    private CustomerController customerController;
    private AccountController accountController;

    private ComboBox<Customer> customerBox = new ComboBox<>();
    private ComboBox<String> typeBox = new ComboBox<>();
    private TextField branchField = new TextField("Main Mall");
    private TextField openingDepositField = new TextField("0");
    private TextField amountField = new TextField();

    private TableView<Account> accountTable = new TableView<>();
    private TableView<Transaction> transactionTable = new TableView<>();
    private VBox root = new VBox(10);

    public AccountView(CustomerController customerController, AccountController accountController) {
        this.customerController = customerController;
        this.accountController = accountController;
        buildScreen();
        refresh();
    }

    public VBox getContent() {
        return root;
    }

    private void buildScreen() {
        root.setPadding(new Insets(15));

        Label heading = new Label("Accounts");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // ---- open account form ----
        typeBox.setItems(FXCollections.observableArrayList("SAVINGS", "INVESTMENT", "CHEQUE"));
        typeBox.setValue("SAVINGS");
        customerBox.setPrefWidth(250);
        customerBox.setOnAction(e -> loadAccounts(null));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("Customer:"), 0, 0);
        form.add(customerBox, 1, 0);
        form.add(new Label("Account type:"), 2, 0);
        form.add(typeBox, 3, 0);
        form.add(new Label("Branch:"), 0, 1);
        form.add(branchField, 1, 1);
        form.add(new Label("Opening deposit (BWP):"), 2, 1);
        form.add(openingDepositField, 3, 1);

        Button openButton = new Button("Open Account");
        openButton.setOnAction(e -> handleOpenAccount());

        // ---- deposit / withdraw ----
        Button depositButton = new Button("Deposit");
        depositButton.setOnAction(e -> handleDeposit());
        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setOnAction(e -> handleWithdraw());
        HBox moneyBox = new HBox(10, new Label("Amount (BWP):"), amountField, depositButton, withdrawButton);

        // ---- account table ----
        addAccountColumn("Account No", "accountNumber");
        addAccountColumn("Type", "accountType");
        addAccountColumn("Branch", "branch");
        addAccountColumn("Balance", "balance");
        accountTable.setPrefHeight(180);
        accountTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> loadTransactions());

        // ---- transaction table ----
        addTransactionColumn("Date", "dateTimeText");
        addTransactionColumn("Type", "type");
        addTransactionColumn("Amount", "amount");
        addTransactionColumn("Balance after", "balanceAfter");
        addTransactionColumn("Description", "description");

        Label historyLabel = new Label("Transaction history of selected account");

        root.getChildren().addAll(heading, form, openButton, accountTable, moneyBox, historyLabel, transactionTable);
    }

    private void addAccountColumn(String title, String property) {
        TableColumn<Account, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(140);
        accountTable.getColumns().add(col);
    }

    private void addTransactionColumn(String title, String property) {
        TableColumn<Transaction, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(140);
        transactionTable.getColumns().add(col);
    }

    // reload the list of customers (called when the tab is opened)
    public void refresh() {
        try {
            Customer current = customerBox.getValue();
            customerBox.setItems(FXCollections.observableArrayList(customerController.getCustomers()));
            if (current != null) {
                // select the same customer again
                for (Customer c : customerBox.getItems()) {
                    if (c.getId() == current.getId()) {
                        customerBox.setValue(c);
                    }
                }
            }
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    // shows the accounts of the selected customer. If accountNumber is not null that account is selected again
    private void loadAccounts(String accountNumber) {
        Customer customer = customerBox.getValue();
        if (customer == null) {
            accountTable.getItems().clear();
            transactionTable.getItems().clear();
            return;
        }
        try {
            accountTable.setItems(FXCollections.observableArrayList(accountController.getAccountsFor(customer)));
            if (accountNumber != null) {
                for (Account a : accountTable.getItems()) {
                    if (a.getAccountNumber().equals(accountNumber)) {
                        accountTable.getSelectionModel().select(a);
                    }
                }
            }
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void loadTransactions() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            transactionTable.getItems().clear();
            return;
        }
        try {
            transactionTable.setItems(FXCollections.observableArrayList(
                    accountController.getTransactions(selected.getAccountNumber())));
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void handleOpenAccount() {
        try {
            double deposit = Double.parseDouble(openingDepositField.getText().trim());
            Account account = accountController.openAccount(
                    customerBox.getValue(), typeBox.getValue(), branchField.getText(), deposit);
            Messages.info("Account " + account.getAccountNumber() + " was opened.");
            loadAccounts(account.getAccountNumber());
        } catch (NumberFormatException ex) {
            Messages.error("Please enter a valid number for the opening deposit");
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void handleDeposit() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Messages.error("Please select an account first");
            return;
        }
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            accountController.deposit(selected.getAccountNumber(), amount);
            amountField.clear();
            loadAccounts(selected.getAccountNumber());
        } catch (NumberFormatException ex) {
            Messages.error("Please enter a valid amount");
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void handleWithdraw() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Messages.error("Please select an account first");
            return;
        }
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            accountController.withdraw(selected.getAccountNumber(), amount);
            amountField.clear();
            loadAccounts(selected.getAccountNumber());
        } catch (NumberFormatException ex) {
            Messages.error("Please enter a valid amount");
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    // called by the main window after interest has been paid
    public void reloadCurrent() {
        Account selected = accountTable.getSelectionModel().getSelectedItem();
        loadAccounts(selected == null ? null : selected.getAccountNumber());
    }
}
