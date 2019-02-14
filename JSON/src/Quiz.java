import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Quiz {
    private static Map<String, Object> myMap = new HashMap<String, Object>();
    private static ArrayList<Question> quizQuestions = new ArrayList<Question>();

    public static void readJsonFile(String fname) {
        byte[] mapData = new byte[0];
        try {
            mapData = Files.readAllBytes(Paths.get(fname));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            myMap = objectMapper.readValue(mapData, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Map is: " + myMap);
        System.out.println("Keys are: " + myMap.keySet());
        Object quiz = myMap.get("quiz");
        HashMap<String, Object> tempmap = (HashMap) quiz;
//        HashMap<String, Object> tempmap = (HashMap<String, Object>) myMap.get("quiz");
        for (String key : tempmap.keySet()) {
            HashMap<String, Object> temp2map = (HashMap) ((HashMap) quiz).get(key);
            System.out.println("key: " + key + " value: " + temp2map);
            for (String key2 : temp2map.keySet()) {
                HashMap<String, Object> temp3map = (HashMap) ((HashMap) temp2map).get(key2);
                System.out.println("key: " + key2 + " value: " + temp3map);
                System.out.println("Question: " + temp3map.get("question"));
                ArrayList <String> answers;
                answers = (ArrayList) temp3map.get("options");
                System.out.println("Options:");
                for (String temp : answers) {
                    System.out.println(temp);
                }
                System.out.println("Answer: " + temp3map.get("answer"));
                Question tempQuiz = new Question();
                tempQuiz.setCategory(key);
                tempQuiz.setQuestion((String) temp3map.get("question"));
                tempQuiz.setOptions(answers);
                tempQuiz.setAnswer((String) temp3map.get("answer"));
                quizQuestions.add(tempQuiz);
                System.out.println("Adding question...");
            }
        }
    }

    public static String getRandomQuestion() {
        for (Map.Entry<String, Object> entry : myMap.entrySet()) {
            System.out.println(entry.getKey());// + " = " + entry.getValue());
        }
        return "quiz";
    }
}