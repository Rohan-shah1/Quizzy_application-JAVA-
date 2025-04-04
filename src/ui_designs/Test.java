package ui_designs;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Test extends Application {

    private Label timerLabel;
    private Label questionLabel;
    private ToggleGroup optionsGroup;
    private Button nextButton;
    private final String correctAnswer = "Super Dense Coding";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("QuizApp");

        // Main Layout
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #F8F9FA;");

        // Header Bar
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 20, 10, 20));
        header.setStyle("-fx-background-color: black; -fx-padding: 15px;");

        Label titleLabel = new Label("QuizApp");
        titleLabel.setFont(Font.font(20));
        titleLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
        header.getChildren().add(titleLabel);

        // Timer Box
        HBox timerBox = new HBox(5);
        timerBox.setAlignment(Pos.CENTER_RIGHT);
        timerLabel = new Label("00:04:28");
        timerLabel.setFont(Font.font(16));
        timerLabel.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 5 10; -fx-border-radius: 5px; -fx-background-radius: 5px;");
        HBox.setHgrow(timerBox, Priority.ALWAYS);
        timerBox.getChildren().add(timerLabel);

        // Question Box
        HBox questionBox = new HBox(10);
        questionBox.setPadding(new Insets(15));
        questionBox.setStyle("-fx-background-color: white; -fx-border-color: #DDD; -fx-border-radius: 10px;");
        Label questionNo = new Label("❕ Question No. 7 of 10");
        questionNo.setFont(Font.font(16));
        questionNo.setStyle("-fx-font-weight: bold;");
        questionBox.getChildren().add(questionNo);

        // Question Text
        questionLabel = new Label("Q. What is the name of the process that sends one qubit of information using two bits of classical information?");
        questionLabel.setFont(Font.font(18));
        questionLabel.setWrapText(true);
        questionLabel.setPadding(new Insets(10));

        // Options Box
        VBox optionsBox = new VBox(10);
        optionsBox.setPadding(new Insets(10, 0, 10, 10));
        optionsGroup = new ToggleGroup();

        String[] options = {
                "A. Quantum Teleportation",
                "B. Quantum Entanglement",
                "C. Quantum Programming",
                "D. Super Dense Coding"
        };

        for (String option : options) {
            RadioButton radioButton = new RadioButton(option);
            radioButton.setFont(Font.font(16));
            radioButton.setToggleGroup(optionsGroup);
            radioButton.setStyle("-fx-background-color: transparent;");
            radioButton.setOnMouseEntered(e -> radioButton.setStyle("-fx-background-color: #EAF2FD; -fx-border-radius: 5px;"));
            radioButton.setOnMouseExited(e -> radioButton.setStyle("-fx-background-color: transparent;"));

            optionsBox.getChildren().add(radioButton);
        }

        // Next Button
        nextButton = new Button("Next ➤");
        nextButton.setFont(Font.font(16));
        nextButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        nextButton.setDisable(true);

        // Enable the Next button only when an option is selected
        optionsGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            nextButton.setDisable(newToggle == null);
        });

        // Layout Containers
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        contentBox.setStyle("-fx-background-color: white; -fx-border-radius: 10px;");
        contentBox.getChildren().addAll(questionBox, questionLabel, optionsBox);

        HBox footerBox = new HBox();
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        footerBox.getChildren().add(nextButton);

        // Add Everything to Root Layout
        root.getChildren().addAll(header, timerBox, contentBox, footerBox);

        // Scene and Stage
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
