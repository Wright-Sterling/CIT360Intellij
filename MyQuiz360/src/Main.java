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
import java.util.Scanner;

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
        Scanner in = new Scanner(System.in);
        int option = 0;
        while(option != 4) {
            System.out.println("Lightning Quiz Admin");
            System.out.println("Menu:");
            System.out.println("1) Display all database rows");
            System.out.println("2) Erase all database rows");
            System.out.println("3) Repopulate database from web service");
            System.out.println("4) Exit admin system");

            option = in.nextInt();

            switch(option) {
                case 1:
                    dumpDatabase();
                    break;
                case 2:
                    clearDatabase();
                    break;
                case 3:
                    fillDatabase();
                    break;
                default:
                    System.out.println("Please enter a valid integer option.");
            }
        }
    }

    private static void dumpDatabase() {
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
        } finally {
            session.close();
        }
    }

    private static void clearDatabase() {
        final Session session = getSession();
        try {
            System.out.println("deleting all records...");
            session.beginTransaction();
            final Query query = session.createNativeQuery("delete from question");
            int deletedCount = query.executeUpdate();
            session.getTransaction().commit();
            System.out.println("..." + deletedCount + "records deleted.");
        } finally {
            session.close();
        }
    }

    private static void fillDatabase() {
        OpentdbPopulator populator = new OpentdbPopulator();
        Scanner in = new Scanner(System.in);
        System.out.print("Category (0,9-32):");
        int cat = in.nextInt();
        System.out.println("");
        System.out.print("Number of questions (1-50):");
        int qs = in.nextInt();
        System.out.println("");
        System.out.println("Populating database...");

        populator.setQuestions(qs);
        populator.setCategory(cat);
        String results = populator.startRequest();
        System.out.println("Populator finished.");
        System.out.println("Results from Populator: " + results);
    }
}