import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
            System.out.println("key: " + key + " value: " + ((HashMap) tempvar).get(key));
        }
    }

    public static String getRandomQuestion() {
        for (Map.Entry<String, String> entry : myMap.entrySet()) {
            System.out.println(entry.getKey());// + " = " + entry.getValue());
        }
        return myMap.get("quiz");
    }
}