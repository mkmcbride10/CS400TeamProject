package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,640,480);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			VBox vbox = new VBox();
			Label welcomeLabel = new Label("Welcome to the CS400 Quizzer!");
			welcomeLabel.setFont(new Font("Arial", 32));
			vbox.getChildren().add(welcomeLabel);
			ObservableList<String> options = 
				    FXCollections.observableArrayList(
				        "Linux",
				        "Binary Search Trees",
				        "Hashing"
				    );
			ComboBox<String> cb = new ComboBox<String>(options);
			cb.setPromptText("Select a Topic");
			vbox.getChildren().add(cb);
			Button startButton = new Button("Start Quiz");
			HBox hbox = new HBox();
			Button addButton = new Button("Add Question");
			Button importButton = new Button("Import Questions");
			hbox.getChildren().addAll(addButton, importButton);
			hbox.alignmentProperty().set(Pos.BASELINE_RIGHT);
			vbox.getChildren().add(startButton);
			vbox.setSpacing(6);
			vbox.alignmentProperty().set(Pos.CENTER);
			root.setBottom(hbox);
			root.setCenter(vbox);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
