package college.hibernate;

import college.hibernate.entities.UsersEntity;
import college.hibernate.security.EncryptionResult;
import college.hibernate.security.EncryptionService;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javax.persistence.Query;
import org.apache.derby.drda.NetworkServerControl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.GenericJDBCException;

/**
 * Class used by the server to talk to the database
 */
public class DbManager {
    /**
     * Instance of DbManager
     */
    private static DbManager instance;

    /**
     * Hibernate package properties
     */
    private static DbProperties properties;

    /**
     * Used to create database sessions
     */
    private static SessionFactory sessionFactory;

    /**
     * Starts up and shuts down the database server
     */
    private static NetworkServerControl control;

    /**
     * Port the database server is running on
     */
    private static int DB_SERVER_PORT;

    /**
     * URL template for the database server url
     */
    private static final String DB_BASE_URL_TEMPLATE = "jdbc:derby://%s:%d/%s/create=true";

    /**
     * Paths to database initialization SQL scripts
     */
    private static final String CREATE_USERS_TABLE = "WEB-INF/classes/college/hibernate/sql/users.table.create.sql";

    /**
     * Constructor preventing outside instantiation
     */
    private DbManager() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void ruin() {
                try {
                    if(control != null) {
                        control.shutdown();
                    }
                    System.out.println("DerbyDb server has shut down");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
String t = System.getProperty("user.dir");
        properties = DbProperties.getInstance();
        buildSessionFactory();
    }

    /**
     * Gets an instance of the DbManager
     * @return
     */
    public static DbManager getInstance() {
        if(instance == null) {
            instance = new DbManager();
        }
        return instance;
    }

    /**
     * Builds the session factory used to create sessions for queries
     */
    private void buildSessionFactory() {
        try {
            if(sessionFactory == null) {
                startDbServer();

                String url = String.format(DB_BASE_URL_TEMPLATE, properties.getDerbyDbServerHost(), DB_SERVER_PORT, properties.getDerbyDbLocation());

                Configuration conf = new Configuration().configure(DbManager.class.getResource("hibernate.cfg.xml"));
                conf.setProperty("hibernate.connection.url", url);
                conf.setProperty("connection.url", url);

                sessionFactory = conf.buildSessionFactory();
                initDb();
            }

        } catch (Throwable t) {
            System.err.println("Could not create SessionFactory. " + t);
            try {
                if (control != null) {
                    control.shutdown();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * Starts up the DerbyDb server
     */
    private void startDbServer() {
        try {
            DB_SERVER_PORT = properties.getDerbyDbServerPort();

            // Look for an open port if one was not specified
            if(DB_SERVER_PORT < 0) {
                ServerSocket socket = new ServerSocket(0);
                DB_SERVER_PORT = socket.getLocalPort();
                socket.close();
            }

            String host = properties.getDerbyDbServerHost();

            control = new NetworkServerControl(InetAddress.getByName(host), DB_SERVER_PORT);
            control.start(null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the database tables
     */
    private void initDb() {
        Session session = sessionFactory.openSession();
        if(session == null) {
            System.err.println("Could not initialize database because the session was null.");
            return;
        }

        Transaction t = null;
        try {
             t = session.beginTransaction();

             for(String s : getSQLFromFile(CREATE_USERS_TABLE)) {
                 try {
                     session.createSQLQuery(s).executeUpdate();
                 } catch (GenericJDBCException e) {
                     if(e.getCause() instanceof SQLException &&
                                (e.getSQLState().equals("X0Y68") || e.getSQLState().equals("X0Y32"))
                             ) {
                         System.out.printf("\"%s\" was already executed.\n", s);
                     } else {
                         throw e;
                     }
                 }
             }
            t.commit();
        } catch (Exception e) {
            System.err.println("Could not initialize DB. " + e);
            if(t != null) {
                t.rollback();
            }

            if(session != null) {
                session.close();
            }

        }
    }

    /**
     * Returns a login authentication token if the authentication succeeds
     *
     * @param username
     * @param password
     * @return Authentication token and query success
     */
    public QueryResponse login(String username, String password) {
        if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return new QueryResponse(false, "Username/password cannot be empty");
        }
        if(sessionFactory == null) {
            return new QueryResponse(false, "Server could not connect to the database");
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Could not create a database session.");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            Query query = session.createSQLQuery("SELECT * FROM APP.USERS WHERE USERNAME=:userName");
            query.setParameter("userName", username);

            UsersEntity user = (UsersEntity) query.getSingleResult();
            session.close();

            if(EncryptionService.authenticate(password, user.getPassword(), user.getSalt())) {
                return new QueryResponse(true, "token");
            }

        return new QueryResponse(false, "Incorrect password");

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, "Username not found");
        }
    }

    /**
     * Creates a new user account
     *
     * @param username Username of the account
     * @param password Password of the account
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @return Success of the operation and login token
     */
    public QueryResponse createAccount(String username, String password, String firstName, String lastName, String email) {
        if(sessionFactory == null) {
            return new QueryResponse(false, "Server could not connect to the database");
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Could not create a database session.");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            QueryResponse available = isUsernameAvailable(username);
            if(!available.getSuccess()) {
                return new QueryResponse(false, "Username is not available");
            }

            // Encrypted password
            EncryptionResult ePass = EncryptionService.encrypt(password);

            UsersEntity user = new UsersEntity();
            user.setUsername(username);
            user.setPassword(ePass.getEncryptedMessage());
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setEmail(email);
            user.setSalt(ePass.getSalt());

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

    /**
     * Checks if the username is already in use
     *
     * @param username Username to check the availability of
     * @return QueryResponse telling the availability
     */
    public QueryResponse isUsernameAvailable(String username) {
        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            Query query = session.createSQLQuery("SELECT * FROM APP.USERS WHERE USERNAME=:userName");
            query.setParameter("userName", username);

            query.getSingleResult();

            session.close();
            // No exception is thrown if the query succeeds. This means the username is unavailable
            return new QueryResponse(false, "Username is not available");
        } catch (Exception e) {
            // An exception means the username was not found and it is available for use
            session.close();
            return new QueryResponse(true);
        }
    }

    /**
     * Takes all of the sql commands in the passed in file and returns them as an array of strings
     *
     * @param path Path to the file
     * @return Array of sql commands
     */
    private String[] getSQLFromFile(String path) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            return reader.lines().collect(Collectors.joining()).split(";");
        } catch (Exception e) {
            System.err.println(e);

            return null;
        }
    }
}
