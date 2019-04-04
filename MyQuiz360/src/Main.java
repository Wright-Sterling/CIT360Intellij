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
                    try {
                        myMap = objectMapper.readValue(qe.getQuestion(), HashMap.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            session.close();
        }
    }
}