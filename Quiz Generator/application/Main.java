/**
 * Filename: Main.java
 * 
 * Project: Team Project
 * 
 * Authors: Marwan McBride, Declan Campbell, JunYu Wang, Christopher D'Amico
 */

package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Main class that runs the application and displays the GUI
 * 
 * @author Marwan McBride, Declan Campbell, JunYu Wang, Christopher D'Amico
 *
 */
public class Main extends Application implements EventHandler<ActionEvent> {

  private Stage windowStage; // stage that is used throughout the program

  private Scene indexScene; // scene that serves as the start screen
  private Scene addQuestionScene; // scene that allows a user to add a question
  private Scene questionScene; // scene that displays a question and several answers

  private Button doneAddingButton; // exits the add question scene and adds the question in storage
  private Button exitAddingQuestion; // exits the add question scene without adding a question
  private Button quitQuizButton; // ends the quiz without seeing results
  private Button seeResultsButton; // ends the quiz and moves to the results scene
  private Button startButton; // starts the quiz
  private Button indexToAddQuestionButton; // exits the index scene and moves to add question scene

  private ObservableList<String> topicsDisplay; // list of the topics for the quiz
  private ComboBox<String> topicComboBox; // ComboBox holding topicsDisplay
  private String topicChosen; // topic chosen by the user

  private String importFilePath; // file path used for importing a JSON file containing question
                                 // data

  private TextField numQuestionsQuery; // TextField allowing the user to input a desired number of
                                       // questions

  private int numQuestions; // number of questions chosen

  /**
   * Reads a JSON file and outputs a list of the topics of questions given in the file
   * 
   * @return the topic list
   */
  private ArrayList<String> readTopicJson() {
    ArrayList<String> topics = new ArrayList<String>(); // list of topics in file
    try {
      // parsing JSON file to add topics
      Object obj = new JSONParser().parse(new FileReader(importFilePath));
      JSONObject jo = (JSONObject) obj;
      JSONArray questionArray = (JSONArray) jo.get("questionArray");

      for (Object jsonobj : questionArray) {
        JSONObject temp = (JSONObject) jsonobj;
        topics.add((String) temp.get("topic"));
      }

    } catch (Exception e) {
      Alert alert = new Alert(AlertType.ERROR, "Invalid file given for importing questions");
      alert.showAndWait().filter(response -> response == ButtonType.OK);
    }
    return topics;
  }

  /**
   * Sets up the index scene, which serves as the start screen for the user
   * 
   * @return the index scene
   */
  private Scene indexScene() {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 640, 480);
    VBox vbox = new VBox();
    Label welcomeLabel = new Label("Welcome to the CS400 Quizzer!");
    welcomeLabel.setFont(new Font("Arial", 32));
    vbox.getChildren().add(welcomeLabel);

    // setting up topic drop-down menu
    topicComboBox = new ComboBox<String>(topicsDisplay);
    topicComboBox.setPromptText("Select a Topic");
    vbox.getChildren().add(topicComboBox);

    // button allowing user to start the quiz
    startButton = new Button("Start Quiz");
    startButton.setOnAction(this); // action defined in handle()

    HBox hbox = new HBox();

    // buttons allowing user to either type in a question or import questions from a JSON file
    indexToAddQuestionButton = new Button("Add Question");
    Button importButton = new Button("Import Questions");

    // import question button action
    importButton.setOnAction(e -> {
      BorderPane importQuestionForm = new BorderPane(); // BorderPane for import dialog

      // import question Label and TextField for user to enter in the file path
      Label importLabel = new Label("Enter a file path:");
      TextField importTextField = new TextField();
      importTextField.setMaxWidth(200);

      VBox importVBox = new VBox();

      // button allowing user to submit their input file path
      Button enterImportButton = new Button("Submit");

      importVBox.getChildren().addAll(importLabel, importTextField, enterImportButton);
      importVBox.setAlignment(Pos.CENTER);
      importVBox.setSpacing(20);
      importQuestionForm.setCenter(importVBox);

      Scene importScene = new Scene(importQuestionForm, 400, 300);
      final Stage dialog = new Stage();

      // setting up the dialog window
      dialog.initModality(Modality.APPLICATION_MODAL);
      dialog.initOwner(windowStage);
      dialog.setScene(importScene);
      dialog.setTitle("Import Question");

      // submit import button action
      enterImportButton.setOnAction(g -> {
        importFilePath = importTextField.getText();
        try {
          // checks that file is valid
          File file = new File(importFilePath);
          FileReader reader = new FileReader(file);

        } catch (FileNotFoundException f) {
          Alert alert = new Alert(AlertType.ERROR, "Invalid file");
          alert.showAndWait().filter(response -> response == ButtonType.OK);
        }
        // closes dialog and moves back to index scene
        dialog.close();

        // updates topics ComboBox on index scene
        ObservableList<String> temp = FXCollections.observableArrayList(readTopicJson());

        for (String s : temp) {
          if (!topicsDisplay.contains(s)) {
            topicsDisplay.add(s);
          }
        }
        topicComboBox.setItems(topicsDisplay);
      });

      dialog.show();
    });

    // number of questions Label and TextField for user to enter their desired number of questions
    Label numQuestionsLabel = new Label("Enter number of questions:");
    numQuestionsQuery = new TextField();
    numQuestionsQuery.setMaxWidth(100);

    // action for add question specified in handle()
    indexToAddQuestionButton.setOnAction(this);

    // adding elements to the scene
    vbox.getChildren().addAll(numQuestionsLabel, numQuestionsQuery);
    hbox.getChildren().addAll(indexToAddQuestionButton, importButton);
    hbox.alignmentProperty().set(Pos.BASELINE_RIGHT);
    vbox.getChildren().add(startButton);
    vbox.setSpacing(6);
    vbox.alignmentProperty().set(Pos.CENTER);
    root.setBottom(hbox);
    root.setCenter(vbox);

    return scene;
  }


  /**
   * Sets up the add question scene, where a user can add their own question
   * 
   * @return the add question scene
   */
  private Scene addQuestionScene() {

    VBox rootvbox = new VBox();
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 400, 480);

    // add question Label and TextField
    Label label_add = new Label("Add a question here: ");
    rootvbox.getChildren().add(label_add);
    TextField addQ = new TextField();
    rootvbox.getChildren().add(addQ);

    // add topic Label and TextField
    Label label_add1 = new Label("Topic:");
    rootvbox.getChildren().add(label_add1);
    TextField textField1 = new TextField();
    rootvbox.getChildren().add(textField1);

    // add answer choice 1 Label and TextField
    Label label_add2 = new Label("First answer choice:");
    rootvbox.getChildren().add(label_add2);
    TextField a1 = new TextField();
    rootvbox.getChildren().add(a1);

    // add answer choice 2 Label and TextField
    Label label_add3 = new Label("Second answer choice:");
    rootvbox.getChildren().add(label_add3);
    TextField a2 = new TextField();
    rootvbox.getChildren().add(a2);

    // add answer choice 3 Label and TextField
    Label label_add4 = new Label("Third answer choice:");
    rootvbox.getChildren().add(label_add4);
    TextField a3 = new TextField();
    rootvbox.getChildren().add(a3);

    // add answer choice 4 Label and TextField
    Label label_add5 = new Label("Fourth answer choice:");
    rootvbox.getChildren().add(label_add5);
    TextField a4 = new TextField();
    rootvbox.getChildren().add(a4);

    // add correct answer Label and TextField
    Label correctLabel = new Label("Correct answer (must be one of the 4 above):");
    rootvbox.getChildren().add(correctLabel);
    TextField correctAnswer = new TextField();
    rootvbox.getChildren().add(correctAnswer);

    rootvbox.alignmentProperty().set(Pos.CENTER);

    doneAddingButton = new Button("Add to question bank");

    // finish adding question action
    doneAddingButton.setOnAction(e -> {
      boolean allFilled = true; // true if all TextField objects were filled by the user
      HashSet<String> answers = new HashSet<String>(); // set of answers given by the user

      // checking if all fields were filled
      for (Node element : rootvbox.getChildren()) {
        if (element instanceof TextField && element != correctAnswer) {
          if (((TextField) element).getText() == null
              || ((TextField) element).getText().trim().length() == 0) {
            allFilled = false;
          } else {
            answers.add(((TextField) element).getText());
          }
        }
      }

      // if all fields were filled
      if (allFilled) {
        // checking that the correct answer is given as one of the 4 answer choices
        if (!answers.contains(correctAnswer.getText())) {
          Alert alert = new Alert(AlertType.ERROR, "Correct answer must be one of the 4 choices");
          alert.showAndWait().filter(response -> response == ButtonType.OK);
        } else {
          // clearing the form
          for (Node element : rootvbox.getChildren()) {
            if (element instanceof TextField) {
              ((TextField) element).setText("");
            }
          }
          // returning to start screen
          windowStage.setScene(indexScene);
          return;
        }
      }
      // if not all fields were filled
      else {
        Alert alert = new Alert(AlertType.ERROR, "All fields must be filled in");
        alert.showAndWait().filter(response -> response == ButtonType.OK);
      }
    });

    doneAddingButton.setAlignment(Pos.CENTER);

    // button allowing exit to start screen without adding the question
    exitAddingQuestion = new Button("Exit without adding");
    exitAddingQuestion.setOnAction(this); // action defined in handle()
    exitAddingQuestion.setAlignment(Pos.CENTER);

    // adding elements to the scene
    VBox bottom = new VBox();
    bottom.getChildren().addAll(doneAddingButton, exitAddingQuestion);
    bottom.setSpacing(20);
    bottom.setAlignment(Pos.CENTER);

    root.setBottom(bottom);
    root.setCenter(rootvbox);
    return scene;
  }

  /**
   * Sets up the question scene, the main quiz interface
   * 
   * @return the question scene
   */
  private Scene questionScene() {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 640, 480);

    // question labels
    Label questionNumberLabel = new Label("Question 1");
    questionNumberLabel.setMinSize(50, 70);
    questionNumberLabel.setScaleY(3);
    questionNumberLabel.setScaleX(3);

    Label questionLabel = new Label("What is the time complexity of hash table insert?");
    questionLabel.alignmentProperty().set(Pos.CENTER_RIGHT);
    questionLabel.setMinSize(50, 50);
    questionLabel.setScaleX(1.5);
    questionLabel.setScaleY(1.5);

    // answer choices
    ToggleGroup tg = new ToggleGroup();
    RadioButton b1 = new RadioButton("A. O(N)");
    RadioButton b2 = new RadioButton("B. O(logN)");
    RadioButton b3 = new RadioButton("C. O(1)");
    RadioButton b4 = new RadioButton("D. O(NlogN)");

    b1.setMaxWidth(200);
    b2.setMaxWidth(200);
    b3.setMaxWidth(200);
    b4.setMaxWidth(200);

    b1.setAlignment(Pos.CENTER_LEFT);
    b2.setAlignment(Pos.CENTER_LEFT);
    b3.setAlignment(Pos.CENTER_LEFT);
    b4.setAlignment(Pos.CENTER_LEFT);

    // button allowing the user to submit their answer and move to the next question
    Button submitButton = new Button("Submit");
    submitButton.setMinSize(40, 40);

    b1.setMinSize(20, 30);
    b2.setMinSize(20, 30);
    b3.setMinSize(20, 30);
    b4.setMinSize(20, 30);

    b1.setToggleGroup(tg);
    b2.setToggleGroup(tg);
    b3.setToggleGroup(tg);
    b4.setToggleGroup(tg);

    // button allowing user to finish the quiz and exit back to the start screen
    quitQuizButton = new Button("Exit Quiz");
    quitQuizButton.setOnAction(this); // action defined in handle()

    // button allowing user to finish the quiz and see their results
    seeResultsButton = new Button("Finish Now");
    seeResultsButton.setOnAction(this); // action defined in handle

    // adding elements to the scene
    VBox vbox = new VBox(questionNumberLabel, questionLabel, b1, b2, b3, b4, submitButton);
    vbox.alignmentProperty().set(Pos.CENTER);

    root.setCenter(vbox);
    root.setRight(quitQuizButton);
    root.setLeft(seeResultsButton);

    return scene;
  }

  /**
   * Sets up the initial display on the index scene
   */
  @Override
  public void start(Stage primaryStage) {
    try {
      windowStage = primaryStage;
      windowStage.setTitle("Quiz Generator");

      topicsDisplay = FXCollections.observableArrayList();

      topicsDisplay.add("binary trees");
      topicsDisplay.add("sorting");
     // indexScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
      questionScene = questionScene();
      addQuestionScene = addQuestionScene();
      indexScene = indexScene();
      primaryStage.setScene(indexScene);
      primaryStage.show();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    launch(args);
  }

  /**
   * Handles several button events that switch from one scene to another
   */
  @Override
  public void handle(ActionEvent event) {
    // index scene - add question button
    if (event.getSource() == indexToAddQuestionButton) {
      windowStage.setScene(addQuestionScene);
    }

    // add question scene - exit to index scene button
    if (event.getSource() == exitAddingQuestion) {
      windowStage.setScene(indexScene);
    }

    // question scene - exit to index scene button
    if (event.getSource() == quitQuizButton) {
      windowStage.setScene(indexScene);
    }

    // index scene - start quiz button
    if (event.getSource() == startButton) {
      try {
        topicChosen = (String) topicComboBox.getValue(); // user-chosen topic

        // user must choose a topic before starting the quiz
        if (topicChosen == null || topicChosen.trim().length() == 0) {
          Alert alert = new Alert(AlertType.ERROR, "Topic must be chosen");
          alert.showAndWait().filter(response -> response == ButtonType.OK);
          return;
        }
        numQuestions = Integer.parseInt(numQuestionsQuery.getText()); // user-input number of
                                                                      // questions
        // number of questions must be a positive integer
        if (numQuestions <= 0) {
          throw new IllegalArgumentException();
        }

        // changes to question scene if user correctly chose a topic and input a valid number
        windowStage.setScene(questionScene);

      } catch (IllegalArgumentException e) {
        Alert alert = new Alert(AlertType.ERROR, "Number of questions must be a positive integer");
        alert.showAndWait().filter(response -> response == ButtonType.OK);
      }
    }
  }
}
