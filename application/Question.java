package application;


public class Question {
	String correctAnswer;
	String question;
	String[] allAnswers;
//	topic, imagePath, answers
	String metaData;
	String topic;
	String imagePath;
	
	
	public Question(String q, String ca, String[] aa, String metaData, String topic, String imageString) {
		question = q;
		correctAnswer= ca;
		allAnswers = aa;
		this.metaData = metaData;
		this.topic = topic;
		this.imagePath = imageString;
		
	}
	
	public boolean answeredCorrectly(String userAnswer) {
		if (userAnswer.contentEquals(correctAnswer))
			return true;
		else
			return false;
	}
}
