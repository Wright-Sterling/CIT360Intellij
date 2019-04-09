import org.hibernate.HibernateException;
import org.hibernate.Metamodel;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import quiz360.QuestionEntity;

import javax.persistence.metamodel.EntityType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import quizDbPopulator.OpentdbPopulator;


public class Main {
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

    public static void main(final String[] args) throws Exception {
        Map<String, Object> myMap = new HashMap<String, Object>();
        final Session session = getSession();
        try {
            System.out.println("querying all the managed entities...");
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    QuestionEntity qe = (QuestionEntity) o;
                    System.out.println("Id: " + qe.getId());
                    System.out.println("Question: " + qe.getQuestion());
                    ObjectMapper objectMapper = new ObjectMapper(); //Secret sauce!
                }
            }
            session.beginTransaction();
            final Query query2 = session.createNativeQuery("delete from question");
            int deletedCount = query2.executeUpdate();
            session.getTransaction().commit();
            System.out.println("Records deleted: "+deletedCount);
        } finally {
            session.close();
        }

        //Populator populator = new Populator();
        OpentdbPopulator populator = new OpentdbPopulator();
        //int junk = populator.databaseClear();
        populator.setQuestions(2);
        populator.setCategory(9);
        String results = populator.startRequest();
        System.out.println("Populator finished.");
        System.out.println("Results from Populator: " + results);
    }
}