package ui_designs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import services.RegistrationService;

public class Register {

    private Stage primaryStage;
    private RegistrationService registrationService;

    public Register(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.registrationService = new RegistrationService(); // Middleware instance
    }

    public Scene createRegisterScene() {
        // Main container
        HBox root = new HBox();
        root.setPrefSize(1000, 600);
        root.setStyle("-fx-background-color: #f0f0f0;");

        // Left Panel (Form)
        VBox leftPane = new VBox(20);
        leftPane.setId("left-pane");
        leftPane.setPrefWidth(400);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(40));

        // Right Panel (Image/Header)
        StackPane rightPane = new StackPane();
        rightPane.setId("right-pane");
        rightPane.setStyle("-fx-background-color: transparent;");

        // Gradient Background
        VBox gradientBackground = new VBox();
        gradientBackground.setStyle("-fx-background-color: linear-gradient(to bottom right, #6c5ce7, #4b3cad);");

        // Header Text
        VBox headerText = new VBox(10);
        Text appName = new Text("QUIZZY");
        appName.setFont(Font.font("Arial", 36));
        appName.setFill(Color.WHITE);
        Text slogan = new Text("Knowledge Platform");
        slogan.setFont(Font.font("Arial", 24));
        slogan.setFill(Color.WHITE);
        headerText.getChildren().addAll(appName, slogan);
        headerText.setAlignment(Pos.CENTER);

        rightPane.getChildren().addAll(gradientBackground, headerText);

        // Form Components
        Label registerHeader = new Label("Create an Account!");
        registerHeader.getStyleClass().add("register-header");

        Label subHeader = new Label("Please fill in the details to sign up");
        subHeader.getStyleClass().add("sub-header");

        TextField fullNameField = createTextField("Full Name");
        TextField emailField = createTextField("Email");
        TextField addressField = createTextField("Address");
        TextField phoneNumberField = createTextField("Phone Number");
        PasswordField passwordField = createPasswordField("Password");

        Button registerButton = new Button("REGISTER");
        styleButton(registerButton);

        Hyperlink signInLink = new Hyperlink("Already have an account? Sign In");
        signInLink.getStyleClass().add("hyperlink");
        signInLink.setOnAction(e -> openLoginPage());

        // Form Layout
        VBox form = new VBox(15);
        form.setMaxWidth(300);
        form.getChildren().addAll(
            registerHeader, subHeader,
            createLabel("Full Name"), fullNameField,
            createLabel("Email"), emailField,
            createLabel("Address"), addressField,
            createLabel("Phone Number"), phoneNumberField,
            createLabel("Password"), passwordField,
            registerButton, signInLink
        );

        leftPane.getChildren().add(form);
        root.getChildren().addAll(leftPane, rightPane);

        // Ensure only the rightPane grows
        HBox.setHgrow(leftPane, Priority.NEVER);
        HBox.setHgrow(rightPane, Priority.ALWAYS);

        primaryStage.setTitle("Quizzy - Register");

        // Scene Setup
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        // Handle Register Button Click
        registerButton.setOnAction(e -> {
            boolean success = registrationService.registerUser(
                fullNameField.getText(),
                emailField.getText(),
                addressField.getText(),
                phoneNumberField.getText(),
                passwordField.getText()
            );
        });

        return scene;
    }

    // Method to open the login page
    private void openLoginPage() {
        Login loginPage = new Login();
        loginPage.start(primaryStage);
    }

    // UI Helper Methods
    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("dark-field");
        field.setPrefWidth(300);
        field.setPrefHeight(40);
        return field;
    }

    private PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("dark-field");
        field.setPrefWidth(300);
        field.setPrefHeight(40);
        return field;
    }

    private void styleButton(Button button) {
        button.getStyleClass().add("dark-button");
        button.setPrefWidth(300);
        button.setPrefHeight(40);
    }

}