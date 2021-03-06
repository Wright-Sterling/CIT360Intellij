package quizServlet;

import java.util.ArrayList;

public class Question {
    private String category;
    private String type;
    private String difficulty;
    private String correctValue;
    private String incorrectValue;
    private int value;
    private String question;
    private ArrayList incorrect_answers;
    private String correct_answer;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCorrectValue() {
        switch(difficulty) {
            case "easy":
                value = 400;
                break;
            case "medium":
                value = 600;
                break;
            case "hard":
                value = 1000;
                break;
            default:
                value = 200; // handle unexpected values of difficulty
        }
        return String.valueOf(value);
    }

    public String getIncorrectValue() {
        int correct = Integer.parseInt(this.getCorrectValue());
        return (String.valueOf((int)(correct - correct*1.5)));
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList getIncorrect_answers() {
        return incorrect_answers;
    }

    public void setIncorrect_answers(ArrayList incorrect_answers) {
        this.incorrect_answers = incorrect_answers;
    }

    public String getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(String correct_answer) {
        this.correct_answer = correct_answer;
    }
}
