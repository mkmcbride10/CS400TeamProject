package application;
	
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class QuestionScene extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,640,480);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			
			ComboBox<String> topics = new ComboBox<String>(FXCollections.observableArrayList("item"));
			Label label = new Label("Welcome");
			
			Label questionLabel = new Label("Question 1");
			questionLabel.setMinSize(50, 70);
			questionLabel.setScaleY(3);
			questionLabel.setScaleX(3);
			Label question = new Label("What is ?");
			question.setMinSize(50, 50);
			question.setScaleX(1.5);
			question.setScaleY(1.5);
			
			ToggleGroup tg = new ToggleGroup();
			RadioButton b1 = new RadioButton("A. answer 1");
			RadioButton b2 = new RadioButton("B. answer 2");
			RadioButton b3 = new RadioButton("C. answer 3");
			RadioButton b4 = new RadioButton("D. answer 4");
			
			Button submitButton = new Button("Submit");
			
			b1.setMinSize(20, 30);
            b2.setMinSize(20, 30);
            b3.setMinSize(20, 30);
            b4.setMinSize(20, 30);
			
			b1.setToggleGroup(tg);
			b2.setToggleGroup(tg);
	        b3.setToggleGroup(tg);
	        b4.setToggleGroup(tg);

			Button quitButton = new Button("See Results");
			submitButton.setMinSize(50, 40);
			
			VBox vbox = new VBox(questionLabel, question, b1, b2, b3, b4, submitButton);
			vbox.alignmentProperty().set(Pos.CENTER);
			root.setCenter(vbox);
			root.setRight(quitButton);
			
			Toggle selected = tg.getSelectedToggle();
			
			submitButton.setOnAction(new EventHandler<ActionEvent>() {
			  @Override
			  public void handle(ActionEvent event) {
			    questionLabel.setText("sdf");
			  }
			});
					
			primaryStage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
