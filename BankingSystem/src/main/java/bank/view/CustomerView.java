package bank.view;

import bank.controller.CustomerController;
import bank.model.BankException;
import bank.model.Customer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Boundary class for registering and listing customers.
 * No business rules in here, the CustomerController does the checking.
 */
public class CustomerView {

    private CustomerController controller;

    private TextField firstNameField = new TextField();
    private TextField surnameField = new TextField();
    private TextField idNumberField = new TextField();
    private TextField addressField = new TextField();
    private TextField phoneField = new TextField();
    private TextField employerField = new TextField();
    private TextField employerAddressField = new TextField();

    private TableView<Customer> table = new TableView<>();
    private VBox root = new VBox(10);

    public CustomerView(CustomerController controller) {
        this.controller = controller;
        buildScreen();
        refresh();
    }

    public VBox getContent() {
        return root;
    }

    private void buildScreen() {
        root.setPadding(new Insets(15));

        // ---- the form ----
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(8);
        form.add(new Label("First name:"), 0, 0);
        form.add(firstNameField, 1, 0);
        form.add(new Label("Surname:"), 2, 0);
        form.add(surnameField, 3, 0);
        form.add(new Label("ID number:"), 0, 1);
        form.add(idNumberField, 1, 1);
        form.add(new Label("Phone:"), 2, 1);
        form.add(phoneField, 3, 1);
        form.add(new Label("Address:"), 0, 2);
        form.add(addressField, 1, 2);
        form.add(new Label("Employer:"), 2, 2);
        form.add(employerField, 3, 2);
        form.add(new Label("Employer address:"), 0, 3);
        form.add(employerAddressField, 1, 3);
        employerField.setPromptText("leave empty if not working");

        Button registerButton = new Button("Register Customer");
        registerButton.setOnAction(e -> handleRegister());

        Button employerButton = new Button("Update Employer of Selected");
        employerButton.setOnAction(e -> handleUpdateEmployer());

        Button deleteButton = new Button("Delete Selected");
        deleteButton.setOnAction(e -> handleDelete());

        HBox buttons = new HBox(10, registerButton, employerButton, deleteButton);

        // ---- the table ----
        addColumn("ID", "id");
        addColumn("First name", "firstName");
        addColumn("Surname", "surname");
        addColumn("ID number", "idNumber");
        addColumn("Phone", "phone");
        addColumn("Employer", "employerName");

        Label heading = new Label("Customers");
        heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        root.getChildren().addAll(heading, form, buttons, table);
    }

    private void addColumn(String title, String property) {
        TableColumn<Customer, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(130);
        table.getColumns().add(col);
    }

    // reloads the customers from the controller
    public void refresh() {
        try {
            table.setItems(FXCollections.observableArrayList(controller.getCustomers()));
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void handleRegister() {
        try {
            Customer c = controller.registerCustomer(
                    firstNameField.getText(), surnameField.getText(), idNumberField.getText(),
                    addressField.getText(), phoneField.getText(),
                    employerField.getText(), employerAddressField.getText());
            Messages.info("Customer " + c.getFullName() + " was registered.");
            clearForm();
            refresh();
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    // uses the employer fields in the form to update the selected customer
    private void handleUpdateEmployer() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Messages.error("Please select a customer in the table first");
            return;
        }
        try {
            controller.updateEmployer(selected, employerField.getText(), employerAddressField.getText());
            Messages.info("Employer details updated.");
            refresh();
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void handleDelete() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Messages.error("Please select a customer in the table first");
            return;
        }
        try {
            controller.deleteCustomer(selected);
            refresh();
        } catch (BankException ex) {
            Messages.error(ex.getMessage());
        }
    }

    private void clearForm() {
        firstNameField.clear();
        surnameField.clear();
        idNumberField.clear();
        addressField.clear();
        phoneField.clear();
        employerField.clear();
        employerAddressField.clear();
    }
}
