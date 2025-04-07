package ui_designs;

import java.io.File;
import java.security.Timestamp;
import java.util.HashMap;
import java.util.Map;

import backend.AdminDashboardDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.PendingJoinRequest;
import models.Question;
import models.StudyMaterial;
import models.MultipleChoiceQuestion;
import models.TrueFalseQuestion;
import services.AdminDashboardService;
import javafx.scene.layout.HBox;

public class AdminDashboard {
	private AdminDashboardService adminDashboardService = new AdminDashboardService(); 

    // Method to create the Admin Dashboard Scene
    public Scene createAdminDashboardScene() {
        // Top Bar with gradient effect
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #6c5ce7, #4b3cad);");
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.WHITE);
        topBar.setAlignment(Pos.CENTER);
        topBar.getChildren().add(titleLabel);

        // Sidebar Menu
        VBox sideBar = new VBox(20);
        sideBar.setPadding(new Insets(20));
        sideBar.setStyle("-fx-background-color: #1E1E1E;");
        sideBar.setAlignment(Pos.TOP_CENTER);

        // Profile Image
        ImageView profileImage = loadProfileImage(getClass().getResourceAsStream("/image/profile.jpg"));
        Label profileName = new Label("Admin");
        profileName.setFont(Font.font("Arial", 16));
        profileName.setTextFill(Color.WHITE);

        // Sidebar Buttons
        Button btnManageUsers = createSidebarButton("Manage Users");
        Button btnManageQuestions = createSidebarButton("Manage Questions");
        Button btnStudyMaterials = createSidebarButton("Study Materials");
        Button btnLogout = createSidebarButton("Logout", "#D32F2F", "white");

        // Add elements to sidebar
        sideBar.getChildren().addAll(profileImage, profileName, btnManageUsers, btnManageQuestions, btnStudyMaterials, btnLogout);

        // Main Content Area (VBox is responsive)
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setAlignment(Pos.CENTER);
        Label welcomeLabel = new Label("Welcome to Admin Dashboard");
        welcomeLabel.setFont(Font.font("Arial", 18));
        contentArea.getChildren().add(welcomeLabel);

        // Button Click Events
        btnManageUsers.setOnAction(e -> {
            showPendingRequests(contentArea, welcomeLabel); // Show pending requests
        });
        
        btnManageQuestions.setOnAction(e -> {
            contentArea.getChildren().clear();  // Clear the content area
            showManageQuestions(contentArea,welcomeLabel);
            // Add specific content related to managing study materials here
        });
        
        btnStudyMaterials.setOnAction(e -> {
            contentArea.getChildren().clear();  // Clear the content area
            showStudyMaterials(contentArea,welcomeLabel);
            
            // Add specific content related to managing study materials here
        });
        btnLogout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                stage.close();
            }
        });

        // Main Layout (Content area remains responsive)
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sideBar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/user.css").toExternalForm());
        return scene;
    }

    // Helper method to create sidebar buttons
    private Button createSidebarButton(String text) {
        return createSidebarButton(text, "#ffffff", "#333333");
    }

    private Button createSidebarButton(String text, String bgColor, String textColor) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-size: 14px; -fx-background-radius: 5px;");

        // Hover effect
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #5b4bc4; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + textColor + "; -fx-font-size: 14px; -fx-background-radius: 5px;"));

        return button;
    }

    // Load profile image with round border
    public static ImageView loadProfileImage(java.io.InputStream inputStream) {
        try {
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            imageView.setClip(new javafx.scene.shape.Circle(40, 40, 40));  // Make the profile image round
            return imageView;
        } catch (Exception e) {
            System.out.println("Profile image not found!");
            return new ImageView();
        }
    }

 
    // Show Pending Join Requests from the database
    private void showPendingRequests(VBox contentArea, Label welcomeLabel) {
        contentArea.getChildren().clear();  // Clear the content area
        welcomeLabel.setText("Pending Join Requests");
        contentArea.getChildren().add(welcomeLabel);

        // TableView for Pending Requests
        TableView<PendingJoinRequest> table = new TableView<>();

        // Define columns
        TableColumn<PendingJoinRequest, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<PendingJoinRequest, String> phoneColumn = new TableColumn<>("Phone Number");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        TableColumn<PendingJoinRequest, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        TableColumn<PendingJoinRequest, Timestamp> requestedAtColumn = new TableColumn<>("Requested At");
        requestedAtColumn.setCellValueFactory(new PropertyValueFactory<>("requestedAt"));

        // Set resizable columns
        nameColumn.setResizable(true);
        phoneColumn.setResizable(true);
        emailColumn.setResizable(true);
        requestedAtColumn.setResizable(true);

        // Retrieve data from the database
        ObservableList<PendingJoinRequest> pendingRequests = adminDashboardService.fetchPendingRequest();
        table.setItems(pendingRequests);

        // Add columns to table
        table.getColumns().addAll(nameColumn, phoneColumn, emailColumn, requestedAtColumn);
        
        // Table should fill the available width and height
        table.setMaxWidth(Double.MAX_VALUE);
        table.setPrefWidth(Control.USE_COMPUTED_SIZE);
        table.setMinWidth(0);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Ensure the table's columns also take up the full width of the table
        nameColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        phoneColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        emailColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        requestedAtColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.25));

        // Add table to content area
        contentArea.getChildren().add(table);

        // Add accept and decline buttons
        Button btnAccept = new Button("Accept");
        Button btnDecline = new Button("Decline");
        btnAccept.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnDecline.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(20, btnAccept, btnDecline);
        buttonBox.setAlignment(Pos.CENTER);
        contentArea.getChildren().add(buttonBox);

        // Disable buttons initially
        btnAccept.setDisable(true);
        btnDecline.setDisable(true);

        // When a row is selected, show the buttons and allow actions
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                btnAccept.setDisable(false);
                btnDecline.setDisable(false);
            }
        });

        // Handle Accept button click
        btnAccept.setOnAction(e -> {
            PendingJoinRequest selectedRequest = table.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
            	adminDashboardService.approveJoinRequest(selectedRequest);
                table.getItems().remove(selectedRequest);  // Remove from TableView
                clearSelectionAndDisableButtons(table, btnAccept, btnDecline);
            }
        });

        // Handle Decline button click
        btnDecline.setOnAction(e -> {
            PendingJoinRequest selectedRequest = table.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                adminDashboardService.rejectJoinRequest(selectedRequest);
                table.getItems().remove(selectedRequest);  // Remove from TableView
                clearSelectionAndDisableButtons(table, btnAccept, btnDecline);
            }
        });
    }

    // Clear selection and disable buttons
    private void clearSelectionAndDisableButtons(TableView<PendingJoinRequest> table, Button btnAccept, Button btnDecline) {
        table.getSelectionModel().clearSelection();
        btnAccept.setDisable(true);
        btnDecline.setDisable(true);
    }
    
    
//from here the manage question section starts
    
    private void showManageQuestions(VBox contentArea, Label welcomeLabel) {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Manage Questions");
        titleLabel.setFont(Font.font("Arial", 18));

        Button btnAddQuestion = new Button("Add Question");
        Button btnUpdateDeleteQuestion = new Button("Update/Delete Question");

        btnAddQuestion.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnUpdateDeleteQuestion.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");

        HBox buttonBox = new HBox(20, btnAddQuestion, btnUpdateDeleteQuestion);
        buttonBox.setAlignment(Pos.CENTER);

        contentArea.getChildren().addAll(titleLabel, buttonBox);

        // Handle button actions
        btnAddQuestion.setOnAction(e -> showAddQuestion(contentArea, welcomeLabel));
        btnUpdateDeleteQuestion.setOnAction(e -> showUpdateDeleteQuestion(contentArea, welcomeLabel));
    }

    
    private void showAddQuestion(VBox contentArea, Label welcomeLabel) {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Add New Question");
        titleLabel.setFont(Font.font("Arial", 18));

        TextField questionField = new TextField();
        questionField.setStyle("-fx-prompt-text-fill: black;");
        questionField.setPromptText("Enter question text");

        ComboBox<String> subjectDropdown = new ComboBox<>();
        subjectDropdown.getItems().addAll("Math", "Science", "G.K", "English");

        ComboBox<String> difficultyDropdown = new ComboBox<>();
        difficultyDropdown.getItems().addAll("Easy", "Medium", "Hard");

        ComboBox<String> typeDropdown = new ComboBox<>();
        typeDropdown.getItems().addAll("MCQ", "True/False");

        TextField answerField = new TextField();
        answerField.setStyle("-fx-prompt-text-fill: black;");
        answerField.setPromptText("Enter correct answer");

        // Fields for MCQ options
        TextField optionAField = new TextField();
        optionAField.setPromptText("Option A");

        TextField optionBField = new TextField();
        optionBField.setPromptText("Option B");

        TextField optionCField = new TextField();
        optionCField.setPromptText("Option C");

        TextField optionDField = new TextField();
        optionDField.setPromptText("Option D");

        VBox mcqOptionsBox = new VBox(5, optionAField, optionBField, optionCField, optionDField);
        mcqOptionsBox.setVisible(false); // Hide by default

        // Show options only when MCQ is selected
        typeDropdown.setOnAction(e -> {
            if ("MCQ".equals(typeDropdown.getValue())) {
                mcqOptionsBox.setVisible(true);
            } else {
                mcqOptionsBox.setVisible(false);
            }
        });

        Button btnAdd = new Button("Add Question");
        btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        btnAdd.setOnAction(e -> {
            String questionText = questionField.getText();
            String subject = subjectDropdown.getValue();
            String difficulty = difficultyDropdown.getValue();
            String type = typeDropdown.getValue();
            String correctAnswer = answerField.getText();

            if (!questionText.isEmpty() && subject != null && difficulty != null && type != null && !correctAnswer.isEmpty()) {
                Question newQuestion = null;

                if (type.equals("MCQ")) {
                    // Create options map for MCQ
                    Map<String, String> options = new HashMap<>();
                    options.put("A", optionAField.getText());
                    options.put("B", optionBField.getText());
                    options.put("C", optionCField.getText());
                    options.put("D", optionDField.getText());

                    // Ensure all options are filled
                    if (options.values().stream().anyMatch(String::isEmpty)) {
                    	showAlert("Error", "All options are required for MCQ!");

                        System.out.println("All options are required for MCQ!");
                        return;
                    }

                    newQuestion = new MultipleChoiceQuestion(0, questionText, subject, difficulty, type, correctAnswer, options);
                } else if (type.equals("True/False")) {
                    newQuestion = new TrueFalseQuestion(0, questionText, subject, difficulty, type, correctAnswer);
                }

                if (newQuestion != null) {
                    // Insert the new question and its options into the database
                    AdminDashboardDAO.addQuestionToDatabase(newQuestion);
                	showAlert("Success", "Question added and inserted into the database successfully!");
                    System.out.println("Question added and inserted into the database successfully!");
                } else {
                	showAlert("Error", "Failed to add question, unknown type.");
                    System.out.println("Failed to add question, unknown type.");
                }
            } else {
            	showAlert("Error", "All fields are required!");
                System.out.println("All fields are required!");
            }
        });

        VBox formBox = new VBox(10, questionField, subjectDropdown, difficultyDropdown, typeDropdown, answerField, mcqOptionsBox, btnAdd);
        formBox.setAlignment(Pos.CENTER);

        contentArea.getChildren().addAll(titleLabel, formBox);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    private void showEditForm(Question selectedQuestion) {
        VBox editForm = new VBox(10);
        editForm.setPadding(new Insets(20));

        Label titleLabel = new Label("Edit Question");
        titleLabel.setFont(Font.font("Arial", 18));

        TextField questionTextField = new TextField(selectedQuestion.getQuestionText());
        questionTextField.setPromptText("Question Text");

        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("MCQ", "True/False");
        typeComboBox.setValue(selectedQuestion.getQuestionType());

        TextField answerTextField = new TextField(selectedQuestion.getCorrectAnswer());
        answerTextField.setPromptText("Correct Answer");

        TextField optionATextField = new TextField();
        TextField optionBTextField = new TextField();
        TextField optionCTextField = new TextField();
        TextField optionDTextField = new TextField();

        if (selectedQuestion instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) selectedQuestion;
            optionATextField.setText(mcq.getOptions().getOrDefault("A", ""));
            optionBTextField.setText(mcq.getOptions().getOrDefault("B", ""));
            optionCTextField.setText(mcq.getOptions().getOrDefault("C", ""));
            optionDTextField.setText(mcq.getOptions().getOrDefault("D", ""));
            optionATextField.setPromptText("Option A");
            optionBTextField.setPromptText("Option B");
            optionCTextField.setPromptText("Option C");
            optionDTextField.setPromptText("Option D");
        }

        Button btnSave = new Button("Save Changes");
        btnSave.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        btnSave.setOnAction(e -> {
            String newQuestionText = questionTextField.getText();
            String newAnswer = answerTextField.getText();
            String newType = typeComboBox.getValue();
            Map<String, String> newOptions = new HashMap<>();

            if ("MCQ".equals(newType)) {
                newOptions.put("A", optionATextField.getText().trim());
                newOptions.put("B", optionBTextField.getText().trim());
                newOptions.put("C", optionCTextField.getText().trim());
                newOptions.put("D", optionDTextField.getText().trim());
            }

            // Ensure no option is null or empty
            for (String key : newOptions.keySet()) {
                if (newOptions.get(key).isEmpty()) {
                    System.out.println("Error: " + key + " option is empty!");
                    return;
                }
            }

            System.out.println("Updated Question Text: " + newQuestionText);
            System.out.println("Updated Answer: " + newAnswer);
            System.out.println("Updated Type: " + newType);
            System.out.println("Updated Options: " + newOptions);

            selectedQuestion.setQuestionText(newQuestionText);
            selectedQuestion.setCorrectAnswer(newAnswer);
            selectedQuestion.setQuestionType(newType);

            if (selectedQuestion instanceof MultipleChoiceQuestion) {
                ((MultipleChoiceQuestion) selectedQuestion).setOptions(newOptions);
            }

            AdminDashboardDAO.updateQuestionInDatabase(selectedQuestion);
        });

        Button btnCancel = new Button("Cancel");
        btnCancel.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        btnCancel.setOnAction(e -> ((Stage) btnCancel.getScene().getWindow()).close());

        editForm.getChildren().addAll(titleLabel, questionTextField, typeComboBox, answerTextField, 
                                      optionATextField, optionBTextField, optionCTextField, optionDTextField, 
                                      btnSave, btnCancel);

        Stage stage = new Stage();
        stage.setTitle("Edit Question");
        stage.setScene(new Scene(editForm, 400, 500));
        stage.show();
    }


    private void showUpdateDeleteQuestion(VBox contentArea, Label welcomeLabel) {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Update/Delete Questions");
        titleLabel.setFont(Font.font("Arial", 18));

        ComboBox<String> subjectDropdown = new ComboBox<>();
        subjectDropdown.getItems().addAll("G.K", "Science", "Math", "English");

        ComboBox<String> difficultyDropdown = new ComboBox<>();
        difficultyDropdown.getItems().addAll("Easy", "Medium", "Hard");

        Button btnSearch = new Button("Search");
        btnSearch.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        TableView<Question> questionTable = new TableView<>();

        // Define columns
        TableColumn<Question, String> questionColumn = new TableColumn<>("Question");
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("questionText"));

        TableColumn<Question, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("questionType"));

        TableColumn<Question, String> optionsColumn = new TableColumn<>("Options");
        optionsColumn.setCellValueFactory(cellData -> {
            Question question = cellData.getValue();
            // Handle MCQ vs other types (True/False, etc.)
            if (question instanceof MultipleChoiceQuestion) {
                return new SimpleStringProperty(((MultipleChoiceQuestion) question).getOptions().toString());
            } else {
                return new SimpleStringProperty("N/A"); // For non-MCQ questions
            }
        });

        TableColumn<Question, String> answerColumn = new TableColumn<>("Answer");
        answerColumn.setCellValueFactory(new PropertyValueFactory<>("correctAnswer"));

        TableColumn<Question, Void> actionColumn = new TableColumn<>("Actions");

        // Action Column with Edit and Delete buttons
        actionColumn.setCellFactory(param -> new TableCell<Question, Void>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDelete = new Button("Delete");

            {
                // Set the action for Edit button
                btnEdit.setOnAction(e -> {
                    Question selected = getTableView().getItems().get(getIndex());
                    showEditForm(selected);  // Call the edit method
                });

                // Set the action for Delete button
                btnDelete.setOnAction(e -> {
                    Question selected = getTableView().getItems().get(getIndex());
                    deleteQuestion(selected);  // Call the delete method
                });

                // Style the buttons
                btnEdit.setStyle("-fx-background-color: #FFC107;");
                btnDelete.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }

            // Create the HBox to contain the buttons
            HBox pane = new HBox(10, btnEdit, btnDelete);

            // Override updateItem method to handle the rendering of the buttons
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);  // Remove graphic if empty
                } else {
                    setGraphic(pane);  // Set the graphic as the HBox with buttons
                }
            }
        });
        // Set resizable columns
        questionColumn.setResizable(true);
        typeColumn.setResizable(true);
        optionsColumn.setResizable(true);
        answerColumn.setResizable(true);
        actionColumn.setResizable(true);

        // Add the columns to the table
        questionTable.getColumns().addAll(questionColumn, typeColumn, optionsColumn, answerColumn, actionColumn);
        
        
        // Table should fill the available width and height
        questionTable.setMaxWidth(Double.MAX_VALUE);
        questionTable.setPrefWidth(Control.USE_COMPUTED_SIZE);
        questionTable.setMinWidth(0);
        questionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Ensure the table's columns also take up the full width of the table
        questionColumn.prefWidthProperty().bind(questionTable.widthProperty().multiply(0.25));
        typeColumn.prefWidthProperty().bind(questionTable.widthProperty().multiply(0.25));
        optionsColumn.prefWidthProperty().bind(questionTable.widthProperty().multiply(0.25));
        answerColumn.prefWidthProperty().bind(questionTable.widthProperty().multiply(0.25));
        actionColumn.prefWidthProperty().bind(questionTable.widthProperty().multiply(0.25));

        // Search button action
        btnSearch.setOnAction(e -> {
            String selectedSubject = subjectDropdown.getValue();
            String selectedDifficulty = difficultyDropdown.getValue();
            if (selectedSubject != null && selectedDifficulty != null) {
                questionTable.setItems(fetchFilteredQuestions(selectedSubject, selectedDifficulty));  // Fetch filtered questions
            } else {
                // Handle case when either subject or difficulty is not selected
            	showAlert("Error", "Please select both subject and difficulty level.");

                System.out.println("Please select both subject and difficulty level.");
            }
        });

        // Add all elements to the content area
        contentArea.getChildren().addAll(titleLabel, subjectDropdown, difficultyDropdown, btnSearch, questionTable);
    }



    private ObservableList<Question> fetchFilteredQuestions(String subject, String difficulty) {
        return AdminDashboardDAO.fetchFilteredQuestions(subject, difficulty);
    }

    private void deleteQuestion(Question question) {
        AdminDashboardDAO.deleteQuestionFromDatabase(question);
    }
    

// from here function of study materials starts
////////////////    
////////////////
    
    
    private void showStudyMaterials(VBox contentArea, Label welcomeLabel) {
        contentArea.getChildren().clear();

        // Label to show the section name
        Label titleLabel = new Label("Manage Study Materials");
        titleLabel.setFont(Font.font("Arial", 18));

        // Create Add and Delete buttons
        Button addButton = new Button("Add Study Material");
        Button deleteButton = new Button("Delete Study Material");

        // Set button styles
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        // Action for Add Study Material button
        addButton.setOnAction(e -> {
            showAddStudyMaterial(contentArea, welcomeLabel);
        });

        // Action for Delete Study Material button
        deleteButton.setOnAction(e -> {
        	showDeleteStudyMaterial(contentArea, welcomeLabel);
        });

        // Add buttons to the content area
        HBox buttonBox = new HBox(10, addButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Add the title and buttons to the content area
        contentArea.getChildren().addAll(titleLabel, buttonBox);
    }
    
    
    private void showAddStudyMaterial(VBox contentArea, Label welcomeLabel) {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Add New Study Material");
        titleLabel.setFont(Font.font("Arial", 18));

        TextField titleField = new TextField();
        titleField.setPromptText("Enter title of the material");

        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Enter description of the material");

        // Make the file variable effectively final by declaring it inside the event handler
        final File[] file = new File[1]; // Declare a single-element array to hold the file reference.

        // Add a button to choose a file
        Button fileChooserButton = new Button("Choose File");
        fileChooserButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));
            file[0] = fileChooser.showOpenDialog(contentArea.getScene().getWindow()); // Store the file in the array
        });

        Button btnAdd = new Button("Add Material");
        btnAdd.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        btnAdd.setOnAction(e -> {
            String title = titleField.getText();
            String description = descriptionField.getText();

            if (title.isEmpty() || description.isEmpty() || file[0] == null) {
                System.out.println("All fields are required, including file upload.");
            } else {
                boolean isUploaded = AdminDashboardDAO.uploadStudyMaterial(title, description, file[0]);
                if (isUploaded) {
                	showAlert("Success", "Study Material uploaded successfully.");

                    System.out.println("Study Material uploaded successfully.");
                } else {
                	showAlert("Error", "Failed to upload study material.");
                    System.out.println("Failed to upload study material.");
                }
            }
        });

        VBox formBox = new VBox(10, titleField, descriptionField, fileChooserButton, btnAdd);
        formBox.setAlignment(Pos.CENTER);

        contentArea.getChildren().addAll(titleLabel, formBox);
    }

    private void showDeleteStudyMaterial(VBox contentArea, Label welcomeLabel) {
        contentArea.getChildren().clear();

        Label titleLabel = new Label("Update/Delete Study Materials");
        titleLabel.setFont(Font.font("Arial", 18));

        TableView<StudyMaterial> materialTable = new TableView<>();
        
        // Table Columns
        TableColumn<StudyMaterial, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<StudyMaterial, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<StudyMaterial, String> fileTypeColumn = new TableColumn<>("File Type");
        fileTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        TableColumn<StudyMaterial, Timestamp> uploadedAtColumn = new TableColumn<>("Uploaded At");
        uploadedAtColumn.setCellValueFactory(new PropertyValueFactory<>("uploadedAt"));

        TableColumn<StudyMaterial, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(param -> new TableCell<StudyMaterial, Void>() {
            private final Button btnDelete = new Button("Delete");

            {
                btnDelete.setOnAction(e -> {
                    StudyMaterial selectedMaterial = getTableView().getItems().get(getIndex());
                    boolean isDeleted = AdminDashboardDAO.deleteStudyMaterial(selectedMaterial.getMaterialId());
                    if (isDeleted) {
                        System.out.println("Material deleted successfully.");
                    	showAlert("success", "Material deleted successfully.");

                        getTableView().getItems().remove(getIndex());  // Remove from UI table
                    } else {
                        System.out.println("Failed to delete material.");
                    	showAlert("Error", "Failed to delete material.");

                    }
                });

                btnDelete.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            }
           

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDelete);
                }
            }
        });
        // Set resizable columns
        titleColumn.setResizable(true);
        descriptionColumn.setResizable(true);
        fileTypeColumn.setResizable(true);
        uploadedAtColumn.setResizable(true);
        actionColumn.setResizable(true);


        materialTable.getColumns().addAll(titleColumn, descriptionColumn, fileTypeColumn, uploadedAtColumn, actionColumn);
        
        // Table should fill the available width and height
        materialTable.setMaxWidth(Double.MAX_VALUE);
        materialTable.setPrefWidth(Control.USE_COMPUTED_SIZE);
        materialTable.setMinWidth(0);
        materialTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Ensure the table's columns also take up the full width of the table
        titleColumn.prefWidthProperty().bind(materialTable.widthProperty().multiply(0.25));
        descriptionColumn.prefWidthProperty().bind(materialTable.widthProperty().multiply(0.25));
        fileTypeColumn.prefWidthProperty().bind(materialTable.widthProperty().multiply(0.25));
        uploadedAtColumn.prefWidthProperty().bind(materialTable.widthProperty().multiply(0.25));
        actionColumn.prefWidthProperty().bind(materialTable.widthProperty().multiply(0.25));

        // Fetch all materials from the database
        materialTable.setItems(FXCollections.observableArrayList(AdminDashboardDAO.getAllStudyMaterials()));

        contentArea.getChildren().addAll(titleLabel, materialTable);
    }



 
}

    





