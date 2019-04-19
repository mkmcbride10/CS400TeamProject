package application;

public class Question {
	String correctAnswer;
	String question;
	String[] allAnswers;
	
	public Question(String q, String ca, String[] aa) {
		question = q;
		correctAnswer= ca;
		allAnswers = aa;
	}
	
	public boolean answeredCorrectly(String userAnswer) {
		if (userAnswer.contentEquals(correctAnswer))
			return true;
		else
			return false;
	}
}
