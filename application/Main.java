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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.xml.ws.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.prism.paint.Color;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
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
	private Button submitAnswerButton;

	private ObservableList<String> topicsDisplay; // list of the topics for the quiz, need to be
													// update by the topicslist every time
	private ComboBox<String> topicComboBox; // ComboBox holding topicsDisplay
	private String topicChosen; // topic chosen by the user

	private String importFilePath; // file path used for importing a JSON file containing question
									// data

	private TextField numQuestionsQuery; // TextField allowing the user to input a desired number of
											// questions

	private int numQuestions; // number of questions chosen
	private int currQuestionNumber; // current question number
	private Question currQuestion; // current chosen question

	private ArrayList<Question> questionsInQuiz;

	private ArrayList<String> selected_topics = new ArrayList<String>(); // store the selected topics
	private ArrayList<String> topicsList; // store all the topics
	private HashMap<String, List<Question>> hashMap = new HashMap<String, List<Question>>(); // the hashtable used to
																								// store the questions

	private boolean if_current; // record if current answer is correct
	private String current_answer; // the choice of user on current question
	private ArrayList<Boolean> resultArrayList = new ArrayList<Boolean>();// record the whole results

	/**
	 * clear all the arrays and set to the index scene
	 */
	private void refresh() {
		resultArrayList.clear();
//		questionsInQuiz.clear();
		selected_topics.clear();
		windowStage.setScene(indexScene());
	}

	/**
	 * Using current topiclist and hashmap to save all the data to the json file
	 */
	private void save_to_json(String pathString) {
		JSONObject allQuestions = new JSONObject();
		JSONArray allQuestionInfo = new JSONArray();

		// Iterate through the list of topics that were added.
		for (String topicName : this.topicsList) {
			List<Question> questionList = this.hashMap.get(topicName);

			// Iterate through questions for this topic.
			for (Question question : questionList) {
				JSONObject questionObj = new JSONObject();
				questionObj.put("meta-data", question.metaData);
				questionObj.put("image", question.imagePath);
				questionObj.put("questionText", question.question);
				questionObj.put("topic", topicName);

				JSONArray answerChoices = new JSONArray();

				// Now add answer options for this question.
				for (String answerOption : question.allAnswers) {
					JSONObject answer = new JSONObject();

					answer.put("choiceText", answerOption);

					if (answerOption.equals(question.correctAnswer))
						answer.put("isCorrect", "T");
					else
						answer.put("isCorrect", "F");

					answerChoices.add(answer);
				}
				questionObj.put("choiceArray", answerChoices);
				allQuestionInfo.add(questionObj);
			}
		}
		allQuestions.put("questionArray", allQuestionInfo);

		try {
			FileWriter file = new FileWriter(pathString);
			file.write(allQuestions.toJSONString());
			file.flush();
		} catch (Exception e) {
		}
	}

	/**
	 * Parse the json with the given "jsonFilepath" Add new topics and questions to
	 * the existing topicsList and hashMap
	 * 
	 * @param jsonFilepath
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public void jsonParser(String jsonFilepath) {

		try {
			// this.topicsList = new ArrayList<String>();

			// Parse the file.
			Object obj1 = new JSONParser().parse(new FileReader(jsonFilepath));
			JSONObject obj = (JSONObject) obj1;

			// Get the questions.
			JSONArray questions = (JSONArray) obj.get("questionArray");

			// Iterate through all questions.
			for (int i = 0; i < questions.size(); i++) {

				// Create a JSONObject for the individual question.
				JSONObject myQuestion = (JSONObject) questions.get(i);

				// Now grab some info for this question.
				String metaData = (String) myQuestion.get("meta-data"); // Grab the meta-data.
				String questionText = (String) myQuestion.get("questionText"); // Grab the question text.
				questionText = questionText.replaceAll("\\$", "");
				String topic = (String) myQuestion.get("topic"); // Grab the topic.
				String imagePath = (String) myQuestion.get("image"); // Grab the location of the image.
				JSONArray choices = (JSONArray) myQuestion.get("choiceArray"); // Grab a list of the answer
																				// choices for this
																				// question.

				// Iterate through the choice options and add the correct data to the choices
				// array.
				if (choices == null)
					System.out.println("error");
				String[] answers = new String[choices.size()]; // store the choices of current question
				String correct_answer = ""; // store the correct answer
				for (int j = 0; j < choices.size(); j++) {
					JSONObject choiceObj = (JSONObject) choices.get(j);
					String choice = (String) choiceObj.get("choiceText");
					choice = choice.replaceAll("\\$", "");
					String isCorrect = (String) choiceObj.get("isCorrect");
					if (isCorrect.equals("T"))
						correct_answer = choice;
					// String choice = (String) choiceObj.get("choice");
					answers[j] = choice;
				}

				// Now let's construct a questionObject using this information.
				Question question = new Question(questionText, correct_answer, answers, metaData, topic, imagePath);

				// Check if this topic is in the HashMap. If it isn't, add it to the topicsList
				// and the HashMap, otherwise just add it to the topics list.
				if (hashMap.containsKey(topic)) {
					List<Question> tempList = hashMap.get(topic);
					tempList.add(question);
				} else {
					topicsList.add(topic); // add the topic to the topic list
					List<Question> tempList = new ArrayList<Question>();// add the question list
					tempList.add(question);
					hashMap.put(topic, tempList);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * Sets up the index scene, which serves as the start screen for the user
	 * 
	 * @return the index scene
	 */
	private Scene indexScene() {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 640, 640);
		VBox vbox = new VBox();

		Label instructionsLabel1 = new Label("click the combobox several times to add your topics");
		Label instructionsLabel2 = new Label(
				"You can see the topics name and number of them in the Upper right corner");
		vbox.getChildren().addAll(instructionsLabel1, instructionsLabel2);

		Label welcomeLabel = new Label("Welcome to the CS400 Quizzer!");
		welcomeLabel.setFont(new Font("Arial", 32));
		vbox.getChildren().add(welcomeLabel);

		// setting up topic drop-down menu

		topicComboBox = new ComboBox<String>(topicsDisplay);
		topicComboBox.setPromptText("Select a Topic");
		topicComboBox.setOnAction(e -> {
			String tempString = (String) topicComboBox.getValue();// record the selected topics
			if (!selected_topics.contains(tempString)) {
				selected_topics.add(tempString);
				windowStage.setScene(indexScene());// refresh to show the label of selected topics
			}
		});
		vbox.getChildren().add(topicComboBox);

		// set the label to display the selected topics
		Label selected_topicsLabel = new Label("Selected topics : ");
		String selectedString = "Selected topics :  ";
		for (String s : selected_topics) {
			selectedString += s;
			selectedString += " ; ";
		}
		selected_topicsLabel.setText(selectedString);

		// button allowing user to start the quiz
		startButton = new Button("Start Quiz");
		startButton.setOnAction(this); // action defined in handle()

		HBox hbox = new HBox();

		// buttons allowing user to either type in a question or import questions from a
		// JSON file
		indexToAddQuestionButton = new Button("Add Question");
		Button importButton = new Button("Import Questions(JSON)");
		// save and back to the index
		Button saveButton = new Button("Save");
		saveButton.setOnAction(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Hi");
			alert.setContentText("Are you sure to save?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				BorderPane rootBorderPane = new BorderPane();
				VBox saveBox = new VBox();
				Scene saveScene = new Scene(rootBorderPane, 100, 200);
				TextField savepathField = new TextField("Input the name of the json file you want to write");
				Button saveon = new Button("save");
				saveBox.getChildren().add(savepathField);
				saveBox.getChildren().add(saveon);
				rootBorderPane.setCenter(saveBox);
				Stage saveStage = new Stage();
				saveStage.setScene(saveScene);
				saveStage.show();
				saveon.setOnAction(ee -> {
					save_to_json(savepathField.getText());
					refresh();
					Alert alert1 = new Alert(AlertType.INFORMATION, "You have saved");
					alert1.showAndWait().filter(r -> r == ButtonType.OK);
					saveStage.close();
				});

			} else {
				alert.close();
			}
		});
		// save and exit
		Button withoutsaveButton = new Button("Exit without Save");
		withoutsaveButton.setOnAction(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Confirmation Dialog");
			alert.setHeaderText("Hi");
			alert.setContentText("Are you sure to exit without save?");

			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() == ButtonType.OK){
				Alert alert2 = new Alert(AlertType.INFORMATION, "Bye!");
			      alert2.showAndWait().filter(r -> r == ButtonType.OK);
			      windowStage.close();
			}
			else {
				alert.close();
			}
		});

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
					reader.close();
				} catch (FileNotFoundException f) {
					Alert alert = new Alert(AlertType.ERROR, "Invalid file");
					alert.showAndWait().filter(response -> response == ButtonType.OK);
				} catch (Exception f) {
					// TODO handle exception
				}
				// closes dialog and moves back to index scene
				dialog.close();

				try {
					jsonParser(importFilePath); // parse the new file
				} catch (Exception e2) {
					// TODO: handle exception
				}

				// updates topics ComboBox on index scene
				Collections.sort(topicsList, String.CASE_INSENSITIVE_ORDER);
				topicsDisplay = FXCollections.observableArrayList(topicsList);

				topicComboBox.setItems(topicsDisplay);
				windowStage.setScene(indexScene());
			});

			dialog.show();
		});

		// number of questions Label and TextField for user to enter their desired
		// number of questions
		Label numQuestionsLabel = new Label("Enter number of questions:");
		numQuestionsQuery = new TextField();
		numQuestionsQuery.setMaxWidth(100);

		// action for add question specified in handle()
		indexToAddQuestionButton.setOnAction(this);

		// adding elements to the scene
		vbox.getChildren().addAll(numQuestionsLabel, numQuestionsQuery);
		hbox.getChildren().addAll(indexToAddQuestionButton, importButton, saveButton, withoutsaveButton);
		hbox.alignmentProperty().set(Pos.BASELINE_RIGHT);
		vbox.getChildren().add(startButton);
		vbox.getChildren().add(selected_topicsLabel);// display the selected labels
		vbox.setSpacing(6);
		vbox.alignmentProperty().set(Pos.CENTER);

		// displays the number of question items in the current question data base.(Up
		// right corner)
		VBox numberBox = new VBox();
		for (String s : topicsList) {
			Label topic_Label = new Label();
			int topic_num = hashMap.get(s).size();
			String showString = s + " : " + ((Integer) topic_num).toString();
			topic_Label.setText(showString);
			numberBox.getChildren().add(topic_Label);

		}

		root.setRight(numberBox);
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
		Scene scene = new Scene(root, 800, 840);

		// give the user instructions about how to input question
		Label instructionLabel1 = new Label();
		String instructionString1 = "#1. If there is no image to display, please to input 'none'";
		instructionLabel1.setText(instructionString1);
		instructionLabel1.setFont(new Font(15));
		rootvbox.getChildren().add(instructionLabel1);
		Label instructionLabel2 = new Label();
		String instructionString2 = "#2. Make sure you input four choices\n which contain the right answer";
		instructionLabel2.setText(instructionString2);
		instructionLabel2.setFont(new Font(15));
		rootvbox.getChildren().add(instructionLabel2);
		Label instructionLabel3 = new Label(
				"#3. We assume that the user will put no more than 5 choices\n please fill the left blank with 'N/A' if your choices are less than 5");
		instructionLabel3.setFont(new Font(15));
		rootvbox.getChildren().add(instructionLabel3);

		// add question Label and TextField
		Label label_add = new Label("Add a question here: ");
		rootvbox.getChildren().add(label_add);
		TextField addQ = new TextField();
		rootvbox.getChildren().add(addQ);

		// add meta-data label and TextField
		Label metaLabel = new Label("Meta-data ");
		rootvbox.getChildren().add(metaLabel);
		TextField metaField = new TextField();
		rootvbox.getChildren().add(metaField);

		// add image path label and TextField
		Label imageLabel = new Label("Image file path");
		rootvbox.getChildren().add(imageLabel);
		TextField imageField = new TextField();
		rootvbox.getChildren().add(imageField);

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

		// add answer choice 4 Label and TextField
		Label label_add6 = new Label("Fifth answer choice:");
		rootvbox.getChildren().add(label_add6);
		TextField a5 = new TextField();
		rootvbox.getChildren().add(a5);

		// add correct answer Label and TextField
		Label correctLabel = new Label("Correct answer (must be one of the 5 above):");
		rootvbox.getChildren().add(correctLabel);
		TextField correctAnswer = new TextField();
		rootvbox.getChildren().add(correctAnswer);

		rootvbox.alignmentProperty().set(Pos.CENTER);

		doneAddingButton = new Button("Add to question bank");

		// finish adding question action
		doneAddingButton.setOnAction(e -> {
			boolean allFilled = true; // true if all TextField objects were filled by the user
			HashSet<String> answers = new HashSet<String>(); // set of answers given by the user
			String[] choiceString = new String[4]; // choices passed to generate the Question object
			String current_topic = textField1.getText();

			// add the answers manually
			String answer1 = a1.getText().trim();
			answers.add(answer1);
			choiceString[0] = answer1;

			String answer2 = a2.getText().trim();
			answers.add(answer2);
			choiceString[1] = answer2;

			String answer3 = a3.getText().trim();
			answers.add(answer3);
			choiceString[2] = answer3;

			String answer4 = a4.getText().trim();
			answers.add(answer4);
			choiceString[3] = answer4;

			// test if there is a blank textfield
			for (String s : choiceString) {
				if (s.length() == 0)
					allFilled = false;
			}

			if (allFilled) {
				// checking that the correct answer is given as one of the 4 answer choices
				if (!answers.contains(correctAnswer.getText())) {
					Alert alert = new Alert(AlertType.ERROR, "Correct answer must be one of the 4 choices");
					alert.showAndWait().filter(response -> response == ButtonType.OK);
				} else {
					// Question question = new Question(textField1.getText(),
					// correctAnswer.getText(), , metaData, topic, imageString)
					Question question = new Question(addQ.getText(), correctAnswer.getText(), choiceString,
							metaField.getText(), textField1.getText(), imageField.getText());
					// if this is a new topic, we need to initiate a question list
					if (!topicsList.contains(current_topic)) {
						topicsList.add(current_topic);
						List<Question> list = new ArrayList<Question>();
						list.add(question);
						hashMap.put(current_topic, list);
					}
					// if this is not a new topic, we add the question directly
					else {
						List<Question> list = hashMap.get(current_topic);
						list.add(question);
					}
					// clearing the form
					for (Node element : rootvbox.getChildren()) {
						if (element instanceof TextField) {
							((TextField) element).setText("");
						}
					}
					Collections.sort(topicsList, String.CASE_INSENSITIVE_ORDER);
					topicsDisplay = FXCollections.observableArrayList(topicsList);
					// returning to start screen
					indexScene = indexScene();
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
	 * Set up the result scene
	 * 
	 * @return result scene
	 */
	private Scene resultScene() {
		BorderPane root = new BorderPane();
		VBox vBox = new VBox(9);

		int total_num = resultArrayList.size();
		int correct_num = 0;
		int wrong_num = 0;
		for (Boolean boolean1 : resultArrayList) {
			if (boolean1.equals(true))
				correct_num++;
			else
				wrong_num++;
		}

		Label answerLabel = new Label(String.format("You have answered %d questions", total_num));
		Label totaLabel = new Label(String.format("The percentage of your correct answer is %.3f percent",
				100 * (float) correct_num / total_num));
		totaLabel.setStyle("-fx-font-weight: bold");
		String wrong_analyze = String.format("you get %d out of %d wrong", wrong_num, total_num);
		Label wrongtLabel = new Label(wrong_analyze);
		String correct_analyze = String.format("you get %d out of %d correct", correct_num, total_num);
		Label correctLabel = new Label(correct_analyze);
		Label scoreLabel = new Label(String.format("You got %d pts", correct_num));
		Button button1 = new Button("Back to index");

		button1.setOnAction(e -> {
			refresh();
		});

		vBox.getChildren().addAll(answerLabel, totaLabel, wrongtLabel, correctLabel, button1);

		root.setCenter(vBox);
		vBox.setAlignment(Pos.CENTER);

		Scene scene = new Scene(root, 640, 640);

		return scene;
	}

	/**
	 * Sets up the question scene, the main quiz interface
	 * 
	 * @return the question scene
	 */
	private Scene questionScene() {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 640, 640);

		System.out.println("Current question number is " + currQuestionNumber);

		// if the tool has displayed the number of questions the user select
		if (currQuestionNumber >= numQuestions) {
			currQuestionNumber = 0;
			return resultScene(); // change to result scene later
		}
		currQuestion = questionsInQuiz.get(currQuestionNumber);
		System.out.println("======== get question " + currQuestion.question);
		Label questionNumberLabel = new Label("Question " + (currQuestionNumber + 1));
		Label totalnumLabel = new Label("Total number of questions in this quiz is " + (numQuestions));
		totalnumLabel.setFont(new Font(15));
		questionNumberLabel.setFont(new Font(18));

		Label questionLabel = new Label(currQuestion.question);
		questionLabel.setFont(new Font(14));
		questionLabel.alignmentProperty().set(Pos.CENTER);
		questionLabel.setMaxWidth(400);
		questionLabel.setWrapText(true);
		questionLabel.setMinSize(50, 50);
		questionLabel.setScaleX(1.5);
		questionLabel.setScaleY(1.5);

		ToggleGroup tg = new ToggleGroup();

		String[] answerList = currQuestion.allAnswers;

		// store only the context of the current answer

		// button allowing the user to submit their answer and move to the next question
		submitAnswerButton = new Button("Submit");
		submitAnswerButton.setMinSize(40, 40);

		// button allowing user to finish the quiz and exit back to the start screen
		quitQuizButton = new Button("Exit Quiz");
		quitQuizButton.setOnAction(this); // action defined in handle()

		// button allowing user to finish the quiz and see their results
		seeResultsButton = new Button("Finish Now");
		seeResultsButton.setOnAction(this); // action defined in handle()

		submitAnswerButton.setOnAction(this); // action defined in handle()

		// adding elements to the scene
		VBox vbox = new VBox(questionNumberLabel, totalnumLabel, questionLabel);

		// only add the choices whose content is not "N/A"
		List<RadioButton> radioButtons = new ArrayList<RadioButton>();
		for (String string : answerList) {
			if (!string.equals("N/A"))
				radioButtons.add(new RadioButton(string));
		}

		for (RadioButton button : radioButtons) {
			button.setOnAction(e -> {
				current_answer = button.getText();
				System.out.println("select " + button.getText());
			});
			button.setMaxWidth(200);
			button.setAlignment(Pos.CENTER_LEFT);
			button.setMinSize(20, 30);
			button.setToggleGroup(tg);
			vbox.getChildren().add(button);
		}

		vbox.getChildren().add(submitAnswerButton);

		System.out.println("text is" + currQuestion.question);
		System.out.println("path is" + currQuestion.imagePath);
		// note that "none" json equals null

		// Note: if you add the question, you need to input it as none
		// // add picture here. Some questions may have corresponding images, that will
		// appear in a 200x200 window in your GUI
		boolean display = false;
		if (currQuestion.imagePath != null) {
			// load the image
			if (!currQuestion.imagePath.equals("none") && currQuestion.imagePath.trim().length() != 0) {
				System.out.println("=======543 the image path is" + currQuestion.imagePath);

				Image image = new Image(currQuestion.imagePath);

				// simple displays ImageView the image as is
				ImageView iv1 = new ImageView();
				iv1.setImage(image);
				iv1.maxWidth(100);
				iv1.maxHeight(100);
				BorderPane picturePane = new BorderPane();
				picturePane.setCenter(iv1);
				Scene pictureScene = new Scene(picturePane, 200, 200);
				Stage dialog = new Stage();
				// dialog.setScene(pictureScene);
				dialog.initModality(Modality.APPLICATION_MODAL);
				dialog.initOwner(windowStage);
				dialog.setScene(pictureScene);
				dialog.setTitle("Question image");
//				vbox.getChildren().add(iv1);
				dialog.show();
				display = true;
			}
		}
		// no picture need to display
		// If a question has no image, this window will be blank, or show a background
		// color or image.
		if (display == false) {
			Group pictureGroup = new Group();
			Scene pictureScene = new Scene(pictureGroup, 200, 200, javafx.scene.paint.Color.BLACK);
			Stage dialog = new Stage();
			// dialog.setScene(pictureScene);
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(windowStage);
			dialog.setScene(pictureScene);
			dialog.show();
		}
		System.out.println("======554");
		vbox.alignmentProperty().set(Pos.CENTER);
		vbox.setSpacing(20);

		root.setCenter(vbox);
		root.setRight(quitQuizButton);
		root.setLeft(seeResultsButton);

		return scene;
	}

	private ArrayList<Question> getQuestionList() {

		ArrayList<Question> questionsToUse = new ArrayList<Question>();

		for (String string : selected_topics) {
			try {
				ArrayList<Question> questions = (ArrayList<Question>) hashMap.get(string);
				for (int i = 0; i < questions.size(); ++i) {
					questionsToUse.add(questions.get(i));
				}
			} catch (Exception e) {
				// TODO add alert
			}

			// randomizes the question order
			Collections.shuffle(questionsToUse);
		}
		System.out.println("selected number of question is " + questionsToUse.size());
		return questionsToUse;

	}

	/**
	 * Sets up the initial display on the index scene
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			windowStage = primaryStage;
			windowStage.setTitle("Quiz Generator");

			topicsList = new ArrayList<String>();
			currQuestionNumber = 0;

			// System.out.println(System.getProperty("user.dir"));
			// File jsonFile = new File("validquestions.json");
			// String pathString = jsonFile.getCanonicalPath();
			// System.out.println(pathString);
			// jsonParser(pathString);

			Collections.sort(topicsList, String.CASE_INSENSITIVE_ORDER);
			topicsDisplay = FXCollections.observableArrayList(topicsList);

			// indexScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// questionScene = questionScene();
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
		if (event.getSource() == seeResultsButton) {
			windowStage.setScene(resultScene());
		}

		// index scene - add question button
		if (event.getSource() == indexToAddQuestionButton) {
			windowStage.setScene(addQuestionScene);
		}

		// add question scene - exit to index scene button
		if (event.getSource() == exitAddingQuestion) {
			windowStage.setScene(indexScene());
		}

		// question scene - exit to index scene button
		if (event.getSource() == quitQuizButton) {
			currQuestionNumber = 0;
			refresh();
			windowStage.setScene(indexScene());
		}

		// index scene - start quiz button
		if (event.getSource() == startButton) {
			try {
				topicChosen = (String) topicComboBox.getValue(); // user-chosen topic
				System.out.println("here is the topic 702 " + topicChosen);
				// user must choose a topic before starting the quiz
				if (selected_topics.size() == 0) {
					Alert alert = new Alert(AlertType.ERROR, "Topic must be chosen");
					alert.showAndWait().filter(response -> response == ButtonType.OK);

					return;
				}
				// if the user doesn't choose the topic, it will not jump to question page
				else {
					// System.out.println("just to get_number");
					numQuestions = Integer.parseInt(numQuestionsQuery.getText()); // user-input number of
					// System.out.println(numQuestions);
					// questions
					// number of questions must be a positive integer

					if (numQuestions <= 0) {
						System.out.println("<= Exception");
						throw new IllegalArgumentException();
					}

					// changes to question scene if user correctly chose a topic and input a valid
					// number
					questionsInQuiz = getQuestionList();
					if (numQuestions > questionsInQuiz.size())
						numQuestions = questionsInQuiz.size();// if the numebr the user input exceed the exact number
					questionScene = questionScene();
					windowStage.setScene(questionScene);
				}

			} catch (IllegalArgumentException e) {
				System.out.println("catch <= exception");
				Alert alert = new Alert(AlertType.ERROR, "There is user input or data loading error, please try again");
				alert.showAndWait().filter(response -> response == ButtonType.OK);
			}
		}
		if (event.getSource() == submitAnswerButton) {
			currQuestionNumber++;
			if_current = currQuestion.answeredCorrectly(current_answer); // determine whether user answer correctly
			resultArrayList.add(if_current); //
			if (if_current) {
				Alert alert = new Alert(AlertType.INFORMATION, "Correct! (Press OK to the next question)");
				alert.getDialogPane().setMinHeight(200);
				alert.showAndWait().filter(r -> r == ButtonType.OK);
			} else {
				Alert alert = new Alert(AlertType.INFORMATION, "Wrong! (Press OK to the next question)");
				alert.getDialogPane().setMinHeight(200);
				alert.showAndWait().filter(r -> r == ButtonType.OK);
			}
			// when you reach the number the user select, the questionScene() will return a
			// result scene
			questionScene = questionScene();
			windowStage.setScene(questionScene);
		}
	}
}
