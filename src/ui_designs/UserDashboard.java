package ui_designs;

import javafx.scene.Scene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import models.MultipleChoiceQuestion;
import models.Question;
import models.QuizObserver;
import models.QuizSubject;
import models.Result;
import models.StudyMaterial;
import models.TrueFalseQuestion;
import models.User;
import services.UserDashboardService;
import java.sql.Timestamp; 
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

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
    private List<Question> mixedQuestions;
    private int currentMixedQuestionIndex = 0;
    private Label timerLabel;
    private Timeline timeline;
    private IntegerProperty timeSeconds = new SimpleIntegerProperty(0);
    private final int QUIZ_DURATION = 720;

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
        contentArea.setStyle("-fx-background-color: #e6f0ff;"); 
        

        Label welcomeLabel = new Label("Welcome to User Dashboard");
        welcomeLabel.setFont(Font.font("Arial", 18));
        welcomeLabel.getStyleClass().add("content-label");

        contentArea.getChildren().addAll(welcomeLabel);

        // Button Click Events
        btnStartQuiz.setOnAction(e -> showQuizSelection());
        btnMixQuiz.setOnAction(e -> startMixedQuiz());
        btnViewResults.setOnAction(e -> showUserResults()); 
        btnStudyMaterials.setOnAction(e -> showStudyMaterials());
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
        scene.getStylesheets().add(getClass().getResource("/css/user.css").toExternalForm());
        return scene;
    }

    private void showQuizSelection() {
        contentArea.getChildren().clear();
        // Create VBox for the quiz content (question and options)
        VBox quizContentContainer = new VBox(10);
        quizContentContainer.setAlignment(Pos.CENTER);

        // Subject and difficulty selection
        Label selectSubjectLabel = new Label("Select Subject:");
        selectSubjectLabel.getStyleClass().add("content-label");
        subjectComboBox = new ComboBox<>();
        subjectComboBox.getItems().addAll("G.K", "Science", "Math", "English");
        subjectComboBox.setValue("Science");

        Label selectDifficultyLabel = new Label("Select Difficulty:");
        selectDifficultyLabel.getStyleClass().add("content-label");
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
    	userDashboardService.resetQuizSession(); // Reset for new session
        String selectedSubject = subjectComboBox.getValue();
        String selectedDifficulty = difficultyComboBox.getValue();

        contentArea.getChildren().clear();
        

        // Create an HBox to hold the score on the upper-right part
        // Top: Score display
        HBox scoreContainer = new HBox(10);
        scoreContainer.setAlignment(Pos.CENTER_RIGHT);
        scoreContainer.setPadding(new Insets(10));
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", 16));
        scoreLabel.getStyleClass().add("score-label");
        scoreContainer.getChildren().add(scoreLabel); // Add the score label to the container

        // Create VBox for the quiz content (question and options)
        
        VBox quizContentContainer = new VBox(20);
        quizContentContainer.setAlignment(Pos.CENTER);
        quizContentContainer.setPadding(new Insets(20));
        
        // Question and options
        questionTextLabel = new Label();
        questionTextLabel.setFont(Font.font("Arial", 16));
        questionTextLabel.getStyleClass().add("content-label");

        optionsContainer = new VBox(10);
        optionsContainer.setAlignment(Pos.CENTER);

        quizContentContainer.getChildren().addAll(questionTextLabel, optionsContainer);

        // Add both the scoreContainer and quizContentContainer to contentArea
        contentArea.getChildren().addAll(scoreContainer, quizContentContainer);
        
     
        quizSubject.setScore(0);  

        loadNextQuestion(selectedDifficulty, selectedSubject);
    }

    private String selectedAnswer = "";  // Store the user's selected answer for comparison

    private void loadNextQuestion(String difficulty, String subject) {
        Question question = userDashboardService.fetchRandomQuestion(difficulty, subject);
        if (question == null) {
        	showCompletionPopup(quizSubject.getScore());  // Pass the user's score to the popup
            questionTextLabel.setText("Quizz for the given subject is completed. \nPlease select another subject or different difficulty level to play. ");
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
                optionButton.getStyleClass().add("option-button");
                optionButton.setOnAction(e -> {
                    optionsContainer.getChildren().forEach(node -> {
                        if (node instanceof Button) {
                            node.getStyleClass().remove("selected-option");
                            node.getStyleClass().add("option-button");
                        }
                    });
                    optionButton.getStyleClass().remove("option-button");
                    optionButton.getStyleClass().add("selected-option");
                    selectedAnswer = optionText;
                });
                optionsContainer.getChildren().add(optionButton);
            }
        } else if (question instanceof TrueFalseQuestion) {

        	Button trueButton = new Button("True");
        	Button falseButton = new Button("False");

        	// True/False handling
        	trueButton.getStyleClass().add("option-button");
        	falseButton.getStyleClass().add("option-button");

        	trueButton.setOnAction(e -> {
        	    trueButton.getStyleClass().add("selected-option");
        	    falseButton.getStyleClass().remove("selected-option");
        	    selectedAnswer = "True";
        	});

        	falseButton.setOnAction(e -> {
        		trueButton.getStyleClass().remove("selected-option");
          	    falseButton.getStyleClass().add("selected-option");
        	    selectedAnswer = "False";
        	});

            optionsContainer.getChildren().addAll(trueButton, falseButton);
        }
        

        // Create Next Question Button and check the selected answer when clicked
        Button btnNextQuestion = new Button("Next Question");
        btnNextQuestion.getStyleClass().add("next-button");
        
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
    private void showCompletionPopup(int score) {
    	
    	Result result = new Result(
    	        user.getUserId(), 
    	        subjectComboBox.getValue(),
    	        difficultyComboBox.getValue(),
    	        score
    	    );
    	    userDashboardService.saveQuizResult(result);
        // Create an Alert dialog to show the score
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Completed");
        alert.setHeaderText("Congratulations!");
        alert.setContentText("You have completed the quiz.\nYour score: " + score + " points.");
        
        // Show the alert
        alert.showAndWait();
    }
    private void showMixCompletionPopup(int score) {
 	   int timeTaken = QUIZ_DURATION - timeSeconds.get();
 	    
 	    // Save result
 	    Result result = new Result(
 	        user.getUserId(),
 	        timeTaken,
 	        score
 	    );
 	    userDashboardService.saveQuizResult(result);
     	
         // Create an Alert dialog to show the score
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle("Quiz Completed");
         alert.setHeaderText("Congratulations!");
         alert.setContentText("You have completed the quiz.\nYour score: " + score + " points.");
         
         // Show the alert
         alert.showAndWait();
     }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
 

    private void startMixedQuiz() {
        // Stop existing timeline if running
        if (timeline != null) {
            timeline.stop();
        }

        contentArea.getChildren().clear();
        userDashboardService.resetQuizSession();
        mixedQuestions = userDashboardService.fetchRandomMixedQuestions();
        currentMixedQuestionIndex = 0;

        if (mixedQuestions.isEmpty()) {
            showAlert("No Questions", "No questions available in the database!");
            return;
        }

        initializeQuizUI();
        quizSubject.setScore(0);
        setupTimer();
        loadNextMixedQuestion();
        timeline.play();
    }

    private void initializeQuizUI() {
        contentArea.getChildren().clear();

        // Use existing score container code from startQuiz()
        HBox scoreContainer = new HBox(10);
        scoreContainer.setAlignment(Pos.CENTER_RIGHT);
        scoreContainer.setPadding(new Insets(10));
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", 16));
        scoreLabel.getStyleClass().add("score-label");
        
        // Timer Label
        timerLabel = new Label();
        timerLabel.setFont(Font.font("Arial", 16));
        timerLabel.getStyleClass().add("timer-label");
        
        scoreContainer.getChildren().addAll(scoreLabel, timerLabel);
       

        // Keep your existing quiz content initialization
        VBox quizContentContainer = new VBox(20);
        quizContentContainer.setAlignment(Pos.CENTER);
        quizContentContainer.setPadding(new Insets(20));
        
        questionTextLabel = new Label();
        questionTextLabel.setFont(Font.font("Arial", 16));
        questionTextLabel.getStyleClass().add("content-label");

        optionsContainer = new VBox(10);
        optionsContainer.setAlignment(Pos.CENTER);

        quizContentContainer.getChildren().addAll(questionTextLabel, optionsContainer);
        contentArea.getChildren().addAll(scoreContainer, quizContentContainer);
    }

    private void loadNextMixedQuestion() {
    	if (currentMixedQuestionIndex >= mixedQuestions.size() || timeSeconds.get() <= 0) {
            timeline.stop();
            showMixCompletionPopup(quizSubject.getScore());
            questionTextLabel.setText("Quiz is completed. \nPlease start again to play. ");
            optionsContainer.getChildren().clear();
            return;
        }
    	 

        Question question = mixedQuestions.get(currentMixedQuestionIndex);
        currentMixedQuestionIndex++;

        // Display question
        displayQuestion(question);

        // Add navigation button
        Button navButton = new Button(
            currentMixedQuestionIndex < mixedQuestions.size() ? 
            "Next Question" : "Finish Quiz"
        );
        navButton.getStyleClass().add("next-button");
        navButton.setOnAction(e -> handleAnswerAndProceed(question));
        
        optionsContainer.getChildren().add(navButton);
    }

    private void displayQuestion(Question question) {
        questionTextLabel.setText(question.getFormattedQuestion());
        optionsContainer.getChildren().clear();
        selectedAnswer = "";

        // Use your existing code from loadNextQuestion
        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            
            for (Map.Entry<String, String> entry : mcq.getOptions().entrySet()) {
                String optionLabel = entry.getKey();
                String optionText = entry.getValue();

                Button optionButton = new Button(optionLabel + ": " + optionText);
                optionButton.getStyleClass().add("option-button");
                optionButton.setOnAction(e -> {
                    //  existing selection logic
                    optionsContainer.getChildren().forEach(node -> {
                        if (node instanceof Button) {
                            node.getStyleClass().remove("selected-option");
                            node.getStyleClass().add("option-button");
                        }
                    });
                    optionButton.getStyleClass().remove("option-button");
                    optionButton.getStyleClass().add("selected-option");
                    selectedAnswer = optionText;
                });
                optionsContainer.getChildren().add(optionButton);
            }
            
        } else if (question instanceof TrueFalseQuestion) {
            // Use your existing True/False logic
            Button trueButton = new Button("True");
            Button falseButton = new Button("False");

            trueButton.getStyleClass().add("option-button");
            falseButton.getStyleClass().add("option-button");

            trueButton.setOnAction(e -> {
                trueButton.getStyleClass().add("selected-option");
                falseButton.getStyleClass().remove("selected-option");
                selectedAnswer = "True";
            });

            falseButton.setOnAction(e -> {
                falseButton.getStyleClass().add("selected-option");
                trueButton.getStyleClass().remove("selected-option");
                selectedAnswer = "False";
            });

            optionsContainer.getChildren().addAll(trueButton, falseButton);
        }
    }

    private void handleAnswerAndProceed(Question question) {
        if (!selectedAnswer.isEmpty() && 
            selectedAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
            quizSubject.setScore(quizSubject.getScore() + 10);
        }
        loadNextMixedQuestion();
    }    
    private void setupTimer() {
        timeSeconds.set(QUIZ_DURATION);
        timerLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            int minutes = timeSeconds.get() / 60;
            int seconds = timeSeconds.get() % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }, timeSeconds));

        KeyFrame keyFrame = new KeyFrame(
            Duration.seconds(1),
            e -> handleTimerTick()
        );
        
        timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void handleTimerTick() {
        timeSeconds.set(timeSeconds.get() - 1);
        if (timeSeconds.get() <= 0) {
            timeline.stop();
            Platform.runLater(() -> {
                showMixCompletionPopup(quizSubject.getScore()); // Fix: Use mix-specific popup
                contentArea.getChildren().clear();
            });
        }
    }
    
    private void showStudyMaterials() {
        contentArea.getChildren().clear();
        
        VBox container = new VBox(20);
        container.getStyleClass().add("study-material-container");
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);
        
        Label title = new Label("Study Materials Library");
        title.getStyleClass().add("study-material-title");
        
        ProgressIndicator progress = new ProgressIndicator();
        container.getChildren().addAll(title, progress);
        contentArea.getChildren().add(container);
        
        // Load materials in background
        new Thread(() -> {
            List<StudyMaterial> materials = userDashboardService.getStudyMaterials();
            
            Platform.runLater(() -> {
                container.getChildren().remove(progress);
                if (materials.isEmpty()) {
                    Label emptyLabel = new Label("No study materials available");
                    emptyLabel.getStyleClass().add("empty-label");
                    container.getChildren().add(emptyLabel);
                    return;
                }
                
                VBox materialsList = new VBox(15);
                materialsList.setAlignment(Pos.TOP_CENTER);
                
                for (StudyMaterial material : materials) {
                    materialsList.getChildren().add(createMaterialCard(material));
                }
                
                ScrollPane scrollPane = new ScrollPane(materialsList);
                scrollPane.setFitToWidth(true);
                scrollPane.getStyleClass().add("material-scroll-pane");
                
                container.getChildren().add(scrollPane);
            });
        }).start();
    }

    private javafx.scene.Node createMaterialCard(StudyMaterial material) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 15px; -fx-background-radius: 8px;");
        card.setEffect(new DropShadow(5, Color.gray(0.5)));

        // Title
        Label titleLabel = new Label(material.getTitle() != null ? material.getTitle() : "Untitled");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Description
        Label descLabel = new Label(material.getDescription() != null ? material.getDescription() : "No description available");
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 14px;");

        // Bottom row container
        HBox detailsBox = new HBox();
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        detailsBox.setMaxWidth(Double.MAX_VALUE);  // Important for spacer

        // Left-aligned content
        HBox leftContent = new HBox(20);
        leftContent.setAlignment(Pos.CENTER_LEFT);
        
        Label typeLabel = new Label("Type: " + material.getFileType().toUpperCase());
        typeLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
        
        Label dateLabel = new Label("Uploaded: " + (material.getUploadedAt() != null ? 
            material.getFormattedDate() : "Unknown date"));
        dateLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px;");
        
        leftContent.getChildren().addAll(typeLabel, dateLabel);

        // Spacer to push button to right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right-aligned button
        Button downloadBtn = new Button("Download");
        downloadBtn.getStyleClass().add("download-button");
        downloadBtn.setOnAction(e -> handleDownload(material));

        // Add elements to details box
        detailsBox.getChildren().addAll(leftContent, spacer, downloadBtn);

        // Add all components to card
        card.getChildren().addAll(titleLabel, descLabel, detailsBox);

        return card;
    }
    
    private void handleDownload(StudyMaterial material) {
    	 if (material == null) {
    	        showErrorAlert("Error", "No material selected for download");
    	        return;
    	    }

    	    VBox container = (VBox) contentArea.lookup(".study-material-container");
    	    if (container == null) {
    	        showErrorAlert("Error", "UI initialization failed");
    	        return;
    	    }
        ProgressIndicator progress = new ProgressIndicator();
        container.getChildren().add(progress);
        
        new Thread(() -> {
            try {
                StudyMaterial fullMaterial = userDashboardService.loadMaterialWithData(material.getMaterialId());
                if (fullMaterial == null) return;
                
                File file = userDashboardService.downloadMaterial(fullMaterial);
                
                Platform.runLater(() -> {
                    container.getChildren().remove(progress);
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialFileName(file.getName());
                    File saveLocation = fileChooser.showSaveDialog(contentArea.getScene().getWindow());
                    
                    if (saveLocation != null) {
                        try {
                            Files.copy(file.toPath(), saveLocation.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            showAlert("Download Complete", 
                                "File saved to:\n" + saveLocation.getAbsolutePath());
                        } catch (IOException ex) {
                            showErrorAlert("Save Error", "Failed to save file to selected location");
                        }
                    }
                    file.delete(); // Cleanup temp file
                });
            } catch (IOException ex) {
                Platform.runLater(() -> {
                    container.getChildren().remove(progress);
                    showErrorAlert("Download Error", "Failed to download file");
                });
            }
        }).start();
    }
    
  //method to show results of user.
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showUserResults() {
        contentArea.getChildren().clear();

        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Your Quiz Results");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20)); 
        title.setStyle("-fx-text-fill: black;");

        TableView<Result> table = new TableView<>();

//        // Type Column
//        TableColumn<Result, String> typeCol = new TableColumn<>("Type");
//        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Subject Column (with null handling)
        TableColumn<Result, String> subjectCol = new TableColumn<>("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        subjectCol.setCellFactory(column -> new TableCell<Result, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "" : item);
            }
        });

        // Difficulty Column (with null handling)
        TableColumn<Result, String> difficultyCol = new TableColumn<>("Difficulty");
        difficultyCol.setCellValueFactory(new PropertyValueFactory<>("difficultyLevel"));
        difficultyCol.setCellFactory(column -> new TableCell<Result, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "" : item);
            }
        });

        // Time Taken Column (format MM:SS)
        TableColumn<Result, Integer> timeCol = new TableColumn<>("Time Taken");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeTaken"));
        timeCol.setCellFactory(column -> new TableCell<Result, Integer>() {
            @Override
            protected void updateItem(Integer seconds, boolean empty) {
                super.updateItem(seconds, empty);
                if (empty || seconds == null) {
                    setText("");
                } else {
                    int mins = seconds / 60;
                    int secs = seconds % 60;
                    setText(String.format("%02d:%02d", mins, secs));
                }
            }
        });

        // Score Column
        TableColumn<Result, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

     // Date Column (format timestamp)
        TableColumn<Result, Timestamp> dateCol = new TableColumn<>("Completed At");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("completedAt"));
        dateCol.setCellFactory(column -> new TableCell<Result, Timestamp>() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp timestamp, boolean empty) {
                super.updateItem(timestamp, empty);
                if (empty || timestamp == null) {
                    setText("");
                } else {
                    setText(formatter.format(timestamp.toLocalDateTime()));
                }
            }
        });

        table.getColumns().addAll(subjectCol, difficultyCol, timeCol, scoreCol, dateCol);
        
        List<Result> results = userDashboardService.getUserResults(user.getUserId());
        if (results.isEmpty()) {
            container.getChildren().add(new Label("No quiz attempts found."));
        } else {
            table.getItems().addAll(results);
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            container.getChildren().addAll(title, table);
        }

        contentArea.getChildren().add(container);
    }
}
