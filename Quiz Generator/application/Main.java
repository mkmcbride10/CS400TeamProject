package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
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
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class Main extends Application implements EventHandler<ActionEvent> {

  private Stage windowStage;

  private Scene indexScene;
  private Button addButtonIndex;

  private Scene addScene;
  private Button doneAdding;
  private Button exitAddingQuestion;

  private Button quitQuizButton;
  private Button startButton;

  private String importFilePath;

  private TextField numQuestionsQuery;
  private int numQuestions;

  // must be initialized here
  private ObservableList<String> topicsDisplay = FXCollections.observableArrayList();

  private Scene questionScene;

  /**
   * Read the Json file about the topics
   * 
   * @return the topic list
   */
  private ArrayList<String> readTopicJson() {
    ArrayList<String> topics = new ArrayList<String>();
    try {
      Object obj = new JSONParser().parse(new FileReader(importFilePath));
      JSONObject jo = (JSONObject) obj;
      JSONArray questionArray = (JSONArray) jo.get("questionArray");

      for (Object jsonobj : questionArray) {
        JSONObject temp = (JSONObject) jsonobj;
        topics.add((String) temp.get("topic"));
      }

    } catch (Exception e) {
      // TODO: handle exception
    }
    return topics;
  }

  /**
   * Add the elements on the index scene and return it Note: we should list the buttons(and so on)
   * in the private fields of this class to set on the "setOnAction"
   * 
   * @return index scene
   */
  private Scene indexScene() {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 640, 480);
    VBox vbox = new VBox();
    Label welcomeLabel = new Label("Welcome to the CS400 Quizzer!");
    welcomeLabel.setFont(new Font("Arial", 32));
    vbox.getChildren().add(welcomeLabel);

    // ObservableList<String> temp = FXCollections.observableArrayList(readTopicJson());
    importFilePath = "./validquestions.json";
    topicsDisplay = FXCollections.observableArrayList(readTopicJson());
    System.out.println(topicsDisplay.size());
    ComboBox<String> cb = new ComboBox<String>(topicsDisplay);
    cb.setPromptText("Select a Topic");
    vbox.getChildren().add(cb);
    startButton = new Button("Start Quiz");
    startButton.setOnAction(this);

    HBox hbox = new HBox();
    addButtonIndex = new Button("Add Question");
    Button importButton = new Button("Import Questions");

    importButton.setOnAction(e -> {
      BorderPane importQuestionForm = new BorderPane();
      

      Label importLabel = new Label("Enter a file path:");
      TextField importTextField = new TextField();
      importTextField.setMaxWidth(200);
      VBox importVBox = new VBox();
      Button enterImportButton = new Button("Submit");

      importVBox.getChildren().addAll(importLabel, importTextField, enterImportButton);
      importVBox.setAlignment(Pos.CENTER);
      importVBox.setSpacing(20);
      importQuestionForm.setCenter(importVBox);

      Scene importScene = new Scene(importQuestionForm, 400, 300);
      final Stage dialog = new Stage();


      dialog.initModality(Modality.APPLICATION_MODAL);
      dialog.initOwner(windowStage);
      dialog.setScene(importScene);

      enterImportButton.setOnAction(g -> {
        importFilePath = importTextField.getText();
        try {
          File file = new File(importFilePath);
          FileReader reader = new FileReader(file);
        } catch (FileNotFoundException f) {
          Alert alert = new Alert(AlertType.ERROR, "Invalid file");
          alert.showAndWait().filter(response -> response == ButtonType.OK);
        }
        dialog.close();
        ObservableList<String> temp = FXCollections.observableArrayList(readTopicJson());

        for (String s : temp) {
          if (!topicsDisplay.contains(s)) {
            topicsDisplay.add(s);
          }
        }
        System.out.println(topicsDisplay.size());
        cb.setItems(topicsDisplay);
      });

      dialog.show();

    });

    Label numQuestionsLabel = new Label("Enter number of questions:");
    numQuestionsQuery = new TextField();
    numQuestionsQuery.setMaxWidth(100);

    vbox.getChildren().addAll(numQuestionsLabel, numQuestionsQuery);
    hbox.getChildren().addAll(addButtonIndex, importButton);
    addButtonIndex.setOnAction(this);// catch the event
    hbox.alignmentProperty().set(Pos.BASELINE_RIGHT);
    vbox.getChildren().add(startButton);
    vbox.setSpacing(6);
    vbox.alignmentProperty().set(Pos.CENTER);
    root.setBottom(hbox);
    root.setCenter(vbox);

    return scene;
  }


  /**
   * Add the elements on the add_question scene and return it
   * 
   * @return
   * 
   * @return
   */
  private Scene addQuestionScene() {

    VBox rootvbox = new VBox();
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 400, 480);

    Label label_add = new Label("Add a question here: ");
    rootvbox.getChildren().add(label_add);
    TextField addQ = new TextField();
    rootvbox.getChildren().add(addQ);

    Label label_add1 = new Label("Topic:");
    rootvbox.getChildren().add(label_add1);
    TextField textField1 = new TextField();
    rootvbox.getChildren().add(textField1);

    Label label_add2 = new Label("First answer choice:");
    rootvbox.getChildren().add(label_add2);
    TextField a1 = new TextField();
    rootvbox.getChildren().add(a1);

    Label label_add3 = new Label("Second answer choice:");
    rootvbox.getChildren().add(label_add3);
    TextField a2 = new TextField();
    rootvbox.getChildren().add(a2);

    Label label_add4 = new Label("Third answer choice:");
    rootvbox.getChildren().add(label_add4);
    TextField a3 = new TextField();
    rootvbox.getChildren().add(a3);

    Label label_add5 = new Label("Fourth answer choice:");
    rootvbox.getChildren().add(label_add5);
    TextField a4 = new TextField();
    rootvbox.getChildren().add(a4);

    Label correctLabel = new Label("Correct answer (must be one of the 4 above):");
    rootvbox.getChildren().add(correctLabel);
    TextField correctAnswer = new TextField();
    rootvbox.getChildren().add(correctAnswer);

    rootvbox.alignmentProperty().set(Pos.CENTER);

    doneAdding = new Button("Add to question bank");

    doneAdding.setOnAction(e -> {
      boolean allFilled = true;
      HashSet<String> answers = new HashSet<String>();

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

      if (allFilled) {
        if (!answers.contains(correctAnswer.getText())) {
          Alert alert = new Alert(AlertType.ERROR, "Correct answer must be 1 of the 4 choices");
          alert.showAndWait().filter(response -> response == ButtonType.OK);
        } else {
          for (Node element : rootvbox.getChildren()) {
            if (element instanceof TextField) {
              ((TextField) element).setText("");
            }
          }
          windowStage.setScene(indexScene);
          return;
        }
      } else {
        Alert alert = new Alert(AlertType.ERROR, "All fields must be filled in");
        alert.showAndWait().filter(response -> response == ButtonType.OK);
      }
    });

    // doneAdd.setOnAction(this);

    doneAdding.setAlignment(Pos.CENTER);

    exitAddingQuestion = new Button("Exit without adding");
    exitAddingQuestion.setOnAction(this);
    exitAddingQuestion.setAlignment(Pos.CENTER);

    VBox bottom = new VBox();
    bottom.getChildren().addAll(doneAdding, exitAddingQuestion);
    bottom.setSpacing(20);
    bottom.setAlignment(Pos.CENTER);
    root.setBottom(bottom);
    root.setCenter(rootvbox);
    return scene;
  }

  private Scene questionScene() {
    BorderPane root = new BorderPane();
    Scene scene = new Scene(root, 640, 480);

    Label questionLabel = new Label("Question 1");
    questionLabel.setMinSize(50, 70);
    questionLabel.setScaleY(3);
    questionLabel.setScaleX(3);
    Label question = new Label("What is the time complexity of hash table insert?");
    question.alignmentProperty().set(Pos.CENTER);
    question.setMinSize(50, 50);
    question.setScaleX(1.5);
    question.setScaleY(1.5);

    ToggleGroup tg = new ToggleGroup();
    RadioButton b1 = new RadioButton("A. O(N)");
    RadioButton b2 = new RadioButton("B. O(logN)");
    RadioButton b3 = new RadioButton("C. O(1)");
    RadioButton b4 = new RadioButton("D. O(NlogN)");
    
    // it seems that using this can make b* buttons in a line
    b1.setMaxWidth(200);
    b2.setMaxWidth(200);
    b3.setMaxWidth(200);
    b4.setMaxWidth(200);
    
    
    b1.setAlignment(Pos.CENTER_LEFT);
    b2.setAlignment(Pos.CENTER_LEFT);
    b3.setAlignment(Pos.CENTER_LEFT);
    b4.setAlignment(Pos.CENTER_LEFT);


    Button submitButton = new Button("Submit");

    b1.setMinSize(20, 30);
    b2.setMinSize(20, 30);
    b3.setMinSize(20, 30);
    b4.setMinSize(20, 30);

    b1.setToggleGroup(tg);
    b2.setToggleGroup(tg);
    b3.setToggleGroup(tg);
    b4.setToggleGroup(tg);

    quitQuizButton = new Button("Exit Quiz");
    quitQuizButton.setOnAction(this);
//    submitButton.setMinSize(, 200);
    submitButton.setMinSize(60, 30);

    VBox vbox = new VBox(questionLabel, question, b1, b2, b3, b4, submitButton);
    vbox.alignmentProperty().set(Pos.CENTER);
    root.setCenter(vbox);
    root.setRight(quitQuizButton);

    return scene;
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      windowStage = primaryStage;

      questionScene = questionScene();
      addScene = addQuestionScene();
      indexScene = indexScene();
      primaryStage.setScene(indexScene);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
    launch(args);
  }

  @Override
  public void handle(ActionEvent event) {
    // TODO Auto-generated method stub
    if (event.getSource() == addButtonIndex)
      windowStage.setScene(addScene);

    /*
     * if (event.getSource() == doneAdd) { /* boolean allFilled = true; for (Node element :
     * rootvbox.getChildren()) { if (element instanceof TextField) { if (((TextField)
     * element).getText() == null || ((TextField) element).getText().trim().length() == 0) {
     * allFilled = false; } } } if (allFilled) { // add to question set
     * windowStage.setScene(indexScene); } else { Alert alert = new Alert(AlertType.ERROR,
     * "All fields must be filled in"); alert.showAndWait().filter(response -> response ==
     * ButtonType.OK); }
     * 
     * }
     */
    // windowStage.setScene(indexScene);


    if (event.getSource() == exitAddingQuestion) {
      windowStage.setScene(indexScene);
    }
    if (event.getSource() == quitQuizButton) {
      windowStage.setScene(indexScene);
    }
    if (event.getSource() == startButton) {
      try {
        numQuestions = Integer.parseInt(numQuestionsQuery.getText());
        if (numQuestions <= 0) {
          throw new IllegalArgumentException();
        }
        windowStage.setScene(questionScene);
      } catch (Exception e) {
        Alert alert = new Alert(AlertType.ERROR, "Number of questions must be a positive integer");
        alert.showAndWait().filter(response -> response == ButtonType.OK);
      }
    }
  }
}
