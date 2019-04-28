package application;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * This class adds functionality to parse a json file and add questions to a question HashMap.
 * @author Declan Campbell
 *
 */
class jsonParser {
	
	// This inner class adds functionality to create answer objects. Answer objects have a choice and an isCorrect field.
	class Answer {
		String choice;
		Boolean isCorrect;
		
		public Answer(String choice, String isCorrect) {
			this.choice = choice;
			
			// If isCorrect is a "T", then set the isCorrect field to true, otherwise, set it to false.
			if (isCorrect.equals("T")) 
				this.isCorrect = true;
			else 
				this.isCorrect = false;
		}
	}
	
	// This inner class adds functionality to create question objects.
	class Question {
		
		private String metaData;
		private String questionText;
		private String topic;
		private String imagePath;
		private Answer[] answers;
		
		public Question(String metaData, String questionText, String topic, String imagePath, Answer[] answers) {
			this.metaData = metaData;
			this.questionText = questionText;
			this.topic = topic;
			this.imagePath = imagePath;
			this.answers = answers;
		}
	}
	
	// Fields for jsonParser.
	HashMap<String, List<Question>> topics; // This HashMap holds the different question topics and their associated questions.
	List<String> topicsList; // This list contains a list of the topics that are currently present in the topics HashMap.
	
	public jsonParser(String jsonFilepath) throws FileNotFoundException, IOException, ParseException {
		this.topics = new HashMap<String, List<Question>>();
		this.topicsList = new ArrayList<String>();
		
		// Parse the file.
        JSONObject obj = (JSONObject) new JSONParser().parse(new FileReader(jsonFilepath)); 
    	
        // Get the questions.
        JSONArray questions = (JSONArray) obj.get("questionArray");
        
        // Iterate through all questions.
        for (int i=0; i<questions.size(); i++) {
        	
        	// Create a JSONObject for the individual question. 
        	JSONObject myQuestion = (JSONObject) questions.get(i);
        	
        	// Now grab some info for this question.
        	String metaData = (String) myQuestion.get("meta-data");        // Grab the meta-data.
        	String questionText = (String) myQuestion.get("questionText"); // Grab the question text.
        	String topic = (String) myQuestion.get("topic");			   // Grab the topic.
        	String imagePath = (String) myQuestion.get("image");           // Grab the location of the image.
        	JSONArray choices = (JSONArray) myQuestion.get("choiceArray"); // Grab a list of the answer choices for this question.
        	
        	// Iterate through the choice options and add the correct data to the choices array.
        	Answer[] answers = new Answer[choices.size()];
        	for (int j=0; j<choices.size(); j++) {
        		JSONObject choiceObj = (JSONObject) choices.get(j);
        		String isCorrect = (String) choiceObj.get("isCorrect");
        		String choice = (String) choiceObj.get("choice");
        		answers[j] = new Answer(choice, isCorrect);
        	}
        	
        	// Now let's construct a questionObject using this information.
        	Question question = new Question(metaData, questionText, topic, imagePath, answers);
        	
        	// Check if this topic is in the HashMap. If it isn't, add it to the topicsList and the HashMap, otherwise just add it to the topics list.
        	Object value = topics.get(question.topic);
        	if (value == null) {
        		this.topics.put(question.topic, Arrays.asList(question));  //this.topicsList.add(question.topic);
        		this.topicsList.add(question.topic);
        	}
        	else 
        		this.topics.get(question.topic).add(question);
        }
	}
	 
    public static void main(String[] args) {
    	try {
    		// Parse this json.
    		jsonParser test = new jsonParser("test.json");
    		
    		// Iterate through topics contained in the topics list.
			for (String topic : test.topicsList) {
	    		System.out.println("Topic: " + topic);
	    		List<Question> questions = test.topics.get(topic);
	    		
	    		// Iterate through questions and print some info.
	    		for (Question question : questions) {
	    			System.out.println("    Question Text: " + question.questionText);
	    			System.out.println("        Meta Data: " + question.metaData);
	    			System.out.println("        Image path: " + question.imagePath);
	    			
	    			// Print the answers for the question.
	    			for (Answer answer : question.answers) 
	    				System.out.println("        Answer: " + answer.choice);
	    			
	    		}
	    	System.out.println();
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
    	
    }
}