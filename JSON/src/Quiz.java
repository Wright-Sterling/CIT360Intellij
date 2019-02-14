import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Quiz {
    private Map<String, Object> myMap = new HashMap<String, Object>();
    private ArrayList<Question> quizQuestions = new ArrayList<Question>();

    public void readJsonFile(String fname) {
        byte[] mapData = new byte[0];
        try {
            mapData = Files.readAllBytes(Paths.get(fname));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper(); //Secret sauce!
        try {
            myMap = objectMapper.readValue(mapData, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object quizJson = myMap.get("quiz");
        /*
        I gave up trying to reduce the number of casts by refactoring.
        Time to move on to the next topic.
        */
        HashMap<String, Object> quiz = (HashMap) quizJson;
        for (String key : quiz.keySet()) {
            HashMap<String, Object> allQuestions = (HashMap) ((HashMap) quizJson).get(key);
            for (String key2 : allQuestions.keySet()) {
                HashMap<String, Object> oneQuestion = (HashMap) ((HashMap) allQuestions).get(key2);
                ArrayList <String> answers;
                answers = (ArrayList) oneQuestion.get("options");
                Question tempQuiz = new Question();
                tempQuiz.setCategory(key);
                tempQuiz.setQuestion((String) oneQuestion.get("question"));
                tempQuiz.setOptions(answers);
                tempQuiz.setAnswer((String) oneQuestion.get("answer"));
                quizQuestions.add(tempQuiz);
            }
        }
    }

    public Question getRandomQuestion() {
        Random rand = new Random();
        int randomQ = rand.nextInt(quizQuestions.size());
        return quizQuestions.get(randomQ);
    }
}