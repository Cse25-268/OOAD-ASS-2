package bank.view;

import bank.controller.LoginController;
import bank.model.BankException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Boundary class for the login screen.
 * It only shows the fields and passes what the user typed to the LoginController.
 */
public class LoginView {

    private Stage stage;
    private LoginController controller;
    private Runnable onLoginSuccess;   // what to do after a good login

    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Label messageLabel = new Label();

    public LoginView(Stage stage, LoginController controller, Runnable onLoginSuccess) {
        this.stage = stage;
        this.controller = controller;
        this.onLoginSuccess = onLoginSuccess;
    }

    public void show() {
        Label title = new Label("Bank System Login");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Username:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);   // pressing Enter also logs in
        loginButton.setOnAction(e -> handleLogin());

        messageLabel.setStyle("-fx-text-fill: red;");

        VBox root = new VBox(15, title, form, loginButton, messageLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        stage.setTitle("Banking System - Login");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    private void handleLogin() {
        try {
            boolean ok = controller.login(usernameField.getText(), passwordField.getText());
            if (ok) {
                onLoginSuccess.run();
            } else {
                messageLabel.setText("Wrong username or password");
                passwordField.clear();
            }
        } catch (BankException ex) {
            messageLabel.setText(ex.getMessage());
        }
    }
}
