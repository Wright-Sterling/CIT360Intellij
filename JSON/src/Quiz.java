import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Quiz {
    private static Map<String, String> myMap = new HashMap<String, String>();

    public static void readJsonFile(String fname) {
        byte[] mapData = new byte[0];
        try {
            mapData = Files.readAllBytes(Paths.get(fname));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Map<String, String> myMap = new HashMap<String, String>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            myMap = objectMapper.readValue(mapData, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Map is: " + myMap);
        System.out.println("Keys are: " + myMap.keySet());
        Object tempvar = myMap.get("quiz");
        HashMap<String, String> tempmap = (HashMap) tempvar;
        for (String key : tempmap.keySet()) {
            HashMap<String, String> temp2map = (HashMap) ((HashMap) tempvar).get(key);
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
            }
        }
    }

    public static String getRandomQuestion() {
        for (Map.Entry<String, String> entry : myMap.entrySet()) {
            System.out.println(entry.getKey());// + " = " + entry.getValue());
        }
        return myMap.get("quiz");
    }
}