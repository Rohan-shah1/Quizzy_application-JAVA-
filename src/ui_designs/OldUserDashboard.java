
package ui_designs;

import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class OldUserDashboard {

    public Scene createUserDashboardScene() {
        // Top Bar with gradient effect
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #6c5ce7, #4b3cad);");
        Label titleLabel = new Label("User Dashboard");
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
        Label profileName = new Label("User");
        profileName.setFont(Font.font("Arial", 16));
        profileName.setTextFill(Color.WHITE);

        // Sidebar Buttons with the desired ones
        Button btnStartQuiz = createSidebarButton("Start Quiz");
        Button btnMixQuiz = createSidebarButton("Mix Quiz");
        Button btnViewResults = createSidebarButton("View Results");
        Button btnStudyMaterials = createSidebarButton("Study Materials");
        Button btnLogout = createSidebarButton("Logout", "#D32F2F", "white");

        // Add profile and selected buttons to sidebar
        sideBar.getChildren().addAll(profileImage, profileName, btnStartQuiz, btnMixQuiz, btnViewResults, btnStudyMaterials, btnLogout);

        // Main Content Area
        VBox contentArea = new VBox(20);
        contentArea.setPadding(new Insets(20));
        contentArea.setAlignment(Pos.CENTER);
        Label welcomeLabel = new Label("Welcome to User Dashboard");
        welcomeLabel.setFont(Font.font("Arial", 18));
        contentArea.getChildren().add(welcomeLabel);

        // Button Click Events
        btnStartQuiz.setOnAction(e -> welcomeLabel.setText("Selected Subject"));
        btnMixQuiz.setOnAction(e -> welcomeLabel.setText("Mixed Quiz"));
        btnViewResults.setOnAction(e -> welcomeLabel.setText("Detail of Attempt Quiz Result"));
        btnStudyMaterials.setOnAction(e -> welcomeLabel.setText("Study Material with Answer"));
        btnLogout.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                System.exit(0); // Close the application
            }
        });

        // Main Layout
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setLeft(sideBar);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());  // Load external stylesheet
        return scene;
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

    // Method to load profile image with round border
    private ImageView loadProfileImage(java.io.InputStream inputStream) {
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
            return new ImageView(); // Return empty if image not found
        }
    }

}
