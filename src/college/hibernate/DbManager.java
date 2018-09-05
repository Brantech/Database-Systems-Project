package college.hibernate;

import college.hibernate.entities.UsersEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class DbManager {
    private static DbManager instance;

    private static SessionFactory sessionFactory;
    private static Session session;

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
                Configuration conf = new Configuration().configure(DbManager.class.getResource("hibernate.cfg.xml"));

                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
                builder.applySettings(conf.getProperties());

                ServiceRegistry registry = builder.build();
                sessionFactory = conf.buildSessionFactory(registry);
            }
        } catch (Throwable t) {
            System.err.println("Could not create SessionFactory. " + t);
        }
    }

    public QueryResponse createAccount(String username, String password, String firstName, String lastName, String email) {
        UsersEntity user = new UsersEntity();
        user.setUsername(username);
        // TODO Encrypt the password
        user.setPassword(password);
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setEmail(email);

        session.save(user);
    }
}
