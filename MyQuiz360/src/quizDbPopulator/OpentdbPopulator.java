package quizDbPopulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import quiz360.QuestionEntity;
import quizServlet.Question;

import javax.persistence.metamodel.EntityType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class OpentdbPopulator {
    private static final String OPENTDB_BASE_URL = "https://opentdb.com/api.php?";
    private static final String OPENTDB_DEFAULT_PARAMETERS = "amount=1"; //return 1 question from any category
    private static final String OPENTDB_DEFAULT_URL = OPENTDB_BASE_URL + OPENTDB_DEFAULT_PARAMETERS;
    private static int questions = 0; // 1 - 50
    private static int category = 0; // 9 - 32
    private static String difficulty = ""; // "easy", "medium", "hard"
    private static String type = ""; // "boolean", "multiple"

    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    static Session sessionObj;
    /*static Session sessionObj;
    static SessionFactory sessionFactoryObj;

    // This Method Is Used To Create The Hibernate's SessionFactory Object
    private static SessionFactory buildSessionFactory() {
        // Creating Configuration Instance & Passing Hibernate Configuration File
        Configuration configObj = new Configuration();
        configObj.configure("hibernate.cfg.xml");

        // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
        ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();

        // Creating Hibernate SessionFactory Instance
        sessionFactoryObj = configObj.buildSessionFactory(serviceRegistryObj);
        return sessionFactoryObj;
    }
*/
    // Method 1: This Method Used To Create A New Student Record In The Database Table
    public static void createRecord(String jsonText) {
        QuestionEntity question = new QuestionEntity();
        try {
            // Getting Session Object From SessionFactory
            sessionObj = getSession();
            // Getting Transaction Object From Session Object
            sessionObj.beginTransaction();

            question.setQuestion(jsonText);
                sessionObj.save(question);

            // Committing The Transactions To The Database
            sessionObj.getTransaction().commit();
        } catch(Exception sqlException) {
            if(null != sessionObj.getTransaction()) {
                System.out.println(".......Transaction Is Being Rolled Back.......");
                sessionObj.getTransaction().rollback();
            }
            sqlException.printStackTrace();
        } finally {
            if(sessionObj != null) {
                sessionObj.close();
            }
        }
    }

    public OpentdbPopulator() {
        OpentdbPopulator.questions = 1; // Grab a question from any category
    }

    public OpentdbPopulator(int numQuestions) {
        OpentdbPopulator.questions = numQuestions;
    }

    public String startRequest() {
        String wsUrl = buildUrl();
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        try {
            // Start the client
            httpclient.start();

            // Execute request
            // One most likely would want to use a callback for operation result
            final CountDownLatch latch1 = new CountDownLatch(1);
            final HttpGet request = new HttpGet(wsUrl); // change to 50
            httpclient.execute(request, new FutureCallback<HttpResponse>() {
                public void completed(final HttpResponse response) {
                    latch1.countDown();
                    //System.out.println(request.getRequestLine() + "->" + response.getStatusLine());
                    if (response.getStatusLine().getStatusCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + response.getStatusLine().getStatusCode());
                    }

                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(
                                new InputStreamReader((response.getEntity().getContent())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String output = "";
                    System.out.println("Output from Server .... \n");
                    while (true) {
                        try {
                            if (!((output = br.readLine()) != null)) break;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        databaseAppend(output);
                    }
                }

                public void failed(final Exception ex) {
                    latch1.countDown();
                    System.out.println(request.getRequestLine() + "->" + ex);
                }

                public void cancelled() {
                    latch1.countDown();
                    System.out.println(request.getRequestLine() + " cancelled");
                }
            });
            latch1.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
            //} catch (ExecutionException e) {
            //  e.printStackTrace();
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ""; // replace with status
    }

    public static Integer getQuestions() {
        return questions;
    }

    public static void setQuestions(Integer questions) {
        OpentdbPopulator.questions = questions;
    }

    public static Integer getCategory() {
        return category;
    }

    public static void setCategory(Integer category) {
        OpentdbPopulator.category = category;
    }

    public static String getDifficulty() {
        return difficulty;
    }

    public static void setDifficulty(String difficulty) {
        OpentdbPopulator.difficulty = difficulty;
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type) {
        OpentdbPopulator.type = type;
    }

    private String buildUrl() {
        String tempUrl = OPENTDB_BASE_URL;
        tempUrl += questions > 0 ? "amount=" + questions : "amount=0"; // fail and return nothing
        tempUrl += category > 0 ? "&category=" + category : ""; // leave blank and return random
        tempUrl += difficulty != "" ? "&difficulty=" + difficulty : ""; // leave blank and return random
        tempUrl += type != "" ? "&type=" + type : ""; //leave blank and return random
        return tempUrl;
    }

    private boolean databaseAppend(String JsonText) {
        String oneRecord = "";
        System.out.println(JsonText);
        int start = 0; // '{' position in string
        int end = 0; // '}' position in string
        for (int i = 1; i < JsonText.length() - 1; i++) { // ignore opening and closing {}
            if (JsonText.charAt(i) == '{') // Looking for '{' position in string
                start = i;
            else if (JsonText.charAt(i) == '}') { // Looking for '}' position in  string
                end = i;
                oneRecord = "{" + JsonText.substring(start + 1, end) + "}"; // value between start and end
                System.out.println("Record: " + oneRecord);
                createRecord(oneRecord);
            }
        }
        return true;
    }
}