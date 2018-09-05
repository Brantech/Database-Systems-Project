package college.hibernate;

import college.hibernate.entities.UsersEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DbManager {
    private static DbManager instance;

    private static SessionFactory sessionFactory;

    private DbManager() {
        buildSessionFactory();
    }

    public static DbManager getInstance() {
        if(instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    private void buildSessionFactory() {
        try {
            if(sessionFactory == null) {
                sessionFactory = new Configuration().configure(DbManager.class.getResource("hibernate.cfg.xml")).buildSessionFactory();
            }
        } catch (Throwable t) {
            System.err.println("Could not create SessionFactory. " + t);
        }
    }

    public QueryResponse createAccount(String username, String password, String firstName, String lastName, String email) {
        Session session = sessionFactory.openSession();

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            UsersEntity user = new UsersEntity();
            user.setUsername(username);
            // TODO Encrypt the password
            user.setPassword(password);
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setEmail(email);

            session.persist(user);

            transaction.commit();
            session.close();

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            return new QueryResponse(false, e.toString());
        }
        return new QueryResponse(true);
    }
}
