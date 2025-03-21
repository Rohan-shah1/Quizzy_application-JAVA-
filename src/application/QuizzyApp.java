
package application;

import backend.CreateDb;
import backend.CreateTables;
import javafx.application.Application;
import ui_designs.Login;

public class QuizzyApp {

    public static void main(String[] args) {
    	CreateDb.createQuizzyDb();
    	CreateTables.createTables(args);

        // Start the JavaFX application by launching QuizzyLogin class
        Application.launch(Login.class, args);
    }
}
