package ui_designs;

import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import models.MultipleChoiceQuestion;
import models.Question;
import models.QuizObserver;
import models.QuizSubject;
import models.TrueFalseQuestion;
import models.User;
import services.UserDashboardService;

import java.io.InputStream;
import java.util.Map;

public class UserDashboard implements QuizObserver {
    private VBox contentArea;
    private Label questionTextLabel;
    private VBox optionsContainer;
    private Label scoreLabel;
    private ComboBox<String> subjectComboBox;
    private ComboBox<String> difficultyComboBox;
    private UserDashboardService userDashboardService;
    private QuizSubject quizSubject;
    private User user;  // User instance to hold user details

    // Modify the constructor to accept a User object
    public UserDashboard(User user, QuizSubject quizSubject) {
        this.user = user;  // Store the user object
        this.quizSubject = quizSubject;
        this.userDashboardService = new UserDashboardService();
        quizSubject.addObserver(this);
    }

    public Scene createUserDashboardScene() {
        // Top Bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #6c5ce7, #4b3cad);");
        Label titleLabel = new Label("User Dashboard");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.WHITE);
        topBar.setAlignment(Pos.CENTER);
        topBar.getChildren().add(titleLabel);

        // Sidebar
        VBox sideBar = new VBox(20);
        sideBar.setPadding(new Insets(20));
        sideBar.setStyle("-fx-background-color: #1E1E1E;");
        sideBar.setAlignment(Pos.TOP_CENTER);

        // Profile Image
        ImageView profileImage = loadProfileImage(getClass().getResourceAsStream("/image/profile.jpg"));
        Label profileName = new Label(user.getFullName());  // Use user's name from the User object
        profileName.setFont(Font.font("Arial", 16));
        profileName.setTextFill(Color.WHITE);

        // Sidebar Buttons
        Button btnStartQuiz = createSidebarButton("Start Quiz");
        Button btnMixQuiz = createSidebarButton("Mix Quiz");
        Button btnViewResults = createSidebarButton("View Results");
        Button btnStudyMaterials = createSidebarButton("Study Materials");
        Button btnLogout = createSidebarButton("Logout", "#D32F2F", "white");

        // Add buttons to sidebar
        sideBar.getChildren().addAll(profileImage, profileName, btnStartQuiz, btnMixQuiz, btnViewResults, btnStudyMaterials, btnLogout);

        // Main Content Area
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Welcome to User Dashboard");
        welcomeLabel.setFont(Font.font("Arial", 18));

        contentArea.getChildren().addAll(welcomeLabel);

        // Button Click Events
        btnStartQuiz.setOnAction(e -> showQuizSelection());
        btnMixQuiz.setOnAction(e -> welcomeLabel.setText("Mixed Quiz"));
        btnViewResults.setOnAction(e -> welcomeLabel.setText("Detail of Attempt Quiz Result"));
        btnStudyMaterials.setOnAction(e -> welcomeLabel.setText("Study Material with Answer"));
        btnLogout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                System.exit(0);
            }
        });

        // Main Layout
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sideBar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        return scene;
    }

    private void showQuizSelection() {
        contentArea.getChildren().clear();
        // Create VBox for the quiz content (question and options)
        VBox quizContentContainer = new VBox(10);
        quizContentContainer.setAlignment(Pos.CENTER);

        // Subject and difficulty selection
        Label selectSubjectLabel = new Label("Select Subject:");
        subjectComboBox = new ComboBox<>();
        subjectComboBox.getItems().addAll("G.K", "Science", "Math", "English");
        subjectComboBox.setValue("Science");

        Label selectDifficultyLabel = new Label("Select Difficulty:");
        difficultyComboBox = new ComboBox<>();
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        difficultyComboBox.setValue("Medium");

        Button btnNext = new Button("Start Quiz");
        btnNext.setOnAction(e -> startQuiz());

        quizContentContainer.getChildren().addAll(selectSubjectLabel, subjectComboBox, selectDifficultyLabel, difficultyComboBox, btnNext);

        // Add both the scoreContainer and quizContentContainer to contentArea
        contentArea.getChildren().addAll(quizContentContainer);
    }

    private void startQuiz() {
        String selectedSubject = subjectComboBox.getValue();
        String selectedDifficulty = difficultyComboBox.getValue();

        contentArea.getChildren().clear();

        // Create an HBox to hold the score on the upper-right part
        HBox scoreContainer = new HBox();
        scoreContainer.setAlignment(Pos.TOP_RIGHT);
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", 16));
        scoreLabel.setTextFill(Color.BLUE);

        scoreContainer.getChildren().add(scoreLabel);  // Add the score label to the container

        // Create VBox for the quiz content (question and options)
        VBox quizContentContainer = new VBox(10);
        quizContentContainer.setAlignment(Pos.CENTER);

        // Question and options
        questionTextLabel = new Label();
        questionTextLabel.setFont(Font.font("Arial", 16));

        optionsContainer = new VBox(10);
        optionsContainer.setAlignment(Pos.CENTER);

        quizContentContainer.getChildren().addAll(questionTextLabel, optionsContainer);

        // Add both the scoreContainer and quizContentContainer to contentArea
        contentArea.getChildren().addAll(scoreContainer, quizContentContainer);

        loadNextQuestion(selectedDifficulty, selectedSubject);
    }

    private String selectedAnswer = "";  // Store the user's selected answer for comparison

    private void loadNextQuestion(String difficulty, String subject) {
        Question question = userDashboardService.fetchRandomQuestion(difficulty, subject);
        if (question == null) {
            questionTextLabel.setText("No question available.");
            optionsContainer.getChildren().clear();
            return;
        }

        questionTextLabel.setText(question.getFormattedQuestion());
        optionsContainer.getChildren().clear();

        // Clear the selected answer before loading new options
        selectedAnswer = "";

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;

            for (Map.Entry<String, String> entry : mcq.getOptions().entrySet()) {
                String optionLabel = entry.getKey();  // "A", "B", etc.
                String optionText = entry.getValue(); // "Option 1", "Option 2", etc.

                Button optionButton = new Button(optionLabel + ": " + optionText);
                optionButton.setOnAction(e -> {
                    // Store the user's selected answer but don't update the score yet
                    selectedAnswer = optionText;
                });
                optionsContainer.getChildren().add(optionButton);
            }
        } else if (question instanceof TrueFalseQuestion) {

            Button trueButton = new Button("True");
            trueButton.setOnAction(e -> {
                selectedAnswer = "True"; // Store the selected answer
            });

            Button falseButton = new Button("False");
            falseButton.setOnAction(e -> {
                selectedAnswer = "False"; // Store the selected answer
            });

            optionsContainer.getChildren().addAll(trueButton, falseButton);
        }

        // Create Next Question Button and check the selected answer when clicked
        Button btnNextQuestion = new Button("Next Question");
        btnNextQuestion.setOnAction(e -> {
            // Check the selected answer and update score if correct
            if (selectedAnswer != null && !selectedAnswer.isEmpty()) {
                if (selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                    System.out.println("Correct answer: " + selectedAnswer);
                    quizSubject.setScore(quizSubject.getScore() + 10); // Award points
                } else {
                    System.out.println("Incorrect answer: " + selectedAnswer);
                }
            }
            loadNextQuestion(difficulty, subject);  // Load next question
        });

        optionsContainer.getChildren().add(btnNextQuestion);
    }


    @Override
    public void updateScore(int newScore) {
        scoreLabel.setText("Score: " + newScore);
    }

    public ImageView loadProfileImage(InputStream inputStream) {
        try {
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            imageView.setClip(new Circle(40, 40, 40));  // Make the profile image round
            return imageView;
        } catch (Exception e) {
            System.out.println("Profile image not found!");
            return new ImageView(); // Return empty if image not found
        }
    }

    // Helper method to create sidebar buttons with text only
    private Button createSidebarButton(String text) {
        return createSidebarButton(text, "#ffffff", "#333333");
    }

    private Button createSidebarButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-size: 14px; -fx-background-radius: 5px;");

        // Add hover effect for the button
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #5b4bc4; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-size: 14px; -fx-background-radius: 5px;"));

        return button;
    }
}
