package quizServlet;

import javax.servlet.annotation.WebServlet;
import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import javax.persistence.metamodel.EntityType;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import com.fasterxml.jackson.databind.ObjectMapper;
import quiz360.QuestionEntity;


@WebServlet(name = "Servlet", urlPatterns = ("/Servlet"))
public class Servlet extends javax.servlet.http.HttpServlet {

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
        out.println("This resource is not available directly.");
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        final Session session = getSession();
        try {
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("FROM " + entityName + " ORDER BY rand()").setMaxResults(1);
                for (Object o : query.list()) {
                    QuestionEntity qe = (QuestionEntity) o;
                    PrintWriter out = response.getWriter();
                    response.setContentType("text/html");
                    out.println(qe.getQuestion());
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        Question question = objectMapper.readValue(qe.getQuestion(), Question.class);
                        ArrayList qOpts = question.getIncorrect_answers();
                        String strOptions = shuffleOptions(qOpts, question.getCorrect_answer());
                        request.setAttribute("options", strOptions);
                        System.out.println("Correct Answer: "+ question.getCorrect_answer());
                        request.setAttribute("correct_answer", question.getCorrect_answer());
                        request.setAttribute("question", question.getQuestion());
                        request.setAttribute("category", question.getCategory());
                        request.setAttribute("correct_value", question.getCorrectValue());
                        request.setAttribute("incorrect_value", question.getIncorrectValue());
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
