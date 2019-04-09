package quizServlet;

import javax.servlet.annotation.WebServlet;
//import java.io.IOException;
import java.io.PrintWriter;

/* From hibernate example */
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import quiz360.QuestionEntity;

import javax.persistence.metamodel.EntityType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebServlet(name = "Servlet", urlPatterns = ("/Servlet"))
public class Servlet extends javax.servlet.http.HttpServlet {

    /* From Hibernate example */
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

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
    /*    out.println("<html><head></head><body>"); */
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        out.println("<h1>Super secret login information</h1>");
        out.println("<p>login: " + login + "</p>");
        out.println("<p>password: " + password + "</p>");
    /*    out.println("</body></html>"); */
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        /* From Hibernate example */
        Map<String, Object> myMap = new HashMap<String, Object>();
        final Session session = getSession();
        try {
            System.out.println("querying all the managed entities...");
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("FROM " + entityName + " ORDER BY rand()").setMaxResults(1);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    QuestionEntity qe = (QuestionEntity) o;
                    System.out.println("Id: " + qe.getId());
                    // Hibernate example: System.out.println("Question: " + qe.getQuestion());
                    /* Added to test */
                    PrintWriter out = response.getWriter();
                    response.setContentType("text/html");
                    out.println(qe.getQuestion());
                    ObjectMapper objectMapper = new ObjectMapper(); //Secret sauce!
                    try {
                        //myMap = objectMapper.readValue(qe.getQuestion(), HashMap.class);
                        Question question = objectMapper.readValue(qe.getQuestion(), Question.class);
                        /*
                        out.println("Question object: " + question);
                        out.println("Question attribute: " + question.getQuestion());
                         */
                        ArrayList qOpts = question.getIncorrect_answers();
                        String strOptions = shuffleOptions(qOpts, question.getCorrect_answer());
                        request.setAttribute("options", strOptions);
                        request.setAttribute("question", question.getQuestion());
                        request.getRequestDispatcher("index.jsp").forward(request, response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            session.close();
        }
    }

    private String shuffleOptions(ArrayList wrong, String right) {
        String options = "";
        ArrayList shuffled = wrong;
        shuffled.add(right);
        Collections.shuffle(shuffled);
        for (int i = 0; i < shuffled.size(); i++) {
            options = options +
                    "<p>"+
                    "<input type='radio' id='option"+i+"' name='radio-group'"+
                    "onclick='getAnswer(this.id)'>"+
                    "<label for='option"+i+"'>"+shuffled.get(i)+"</label>"+
                    "</p>";
        }
        return options;
    }
}
