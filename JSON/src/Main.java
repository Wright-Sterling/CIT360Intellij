import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Main {

    public static void main(String[] args) {
        Quiz myQuiz = new Quiz();
        myQuiz.readJsonFile("quiz.json");
        System.out.println(myQuiz.getRandomQuestion());
    }
}


