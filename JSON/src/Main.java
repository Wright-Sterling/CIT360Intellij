
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Quiz myQuiz = new Quiz();
        myQuiz.readJsonFile("quiz.json");

        /* This could be split into a controller and a view but I just want to show
        that the JSON was ingested properly. */
        String response = "";
        Scanner userInput = new Scanner(System.in);
        while (true) {
            System.out.print("Would you like to answer a trivia question? ");
            response = userInput.nextLine();
            if(!response.toUpperCase().startsWith("Y")){
                break;
            }
            Question question = myQuiz.getRandomQuestion();
            System.out.println();
            System.out.println("Here's one from the " + question.getCategory() + " category:");
            System.out.println();
            System.out.println(question.getQuestion());
            System.out.println();
            System.out.println("Your choices:");
            ArrayList options = question.getOptions();
            for(int q = 0; q < options.size(); q++) {
                String option = (String) options.get(q);
                System.out.println((q+1) + " " + option);
            }
            System.out.print("Enter your answer: ");
            response = userInput.nextLine();
            System.out.println();
            System.out.println();
            System.out.println("The correct answer is: " + question.getAnswer());
            System.out.println();
        }
    }
}


