package college.events.hibernate;

import college.events.hibernate.entities.EventsEntity;
import college.events.hibernate.entities.MessagesEntity;
import college.events.hibernate.entities.RsoEntity;
import college.events.hibernate.entities.RsofollowsEntity;
import college.events.hibernate.entities.UniversitiesEntity;
import college.events.hibernate.entities.UsersEntity;
import college.events.hibernate.security.EncryptionService;
import college.events.hibernate.test_data.InsertDBTestData;
import college.events.website.shared.messages.RSOMessage;
import college.events.website.shared.messages.SuperAdminMessage;
import college.events.website.shared.messages.UserInfo;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    private static final Gson gson = new Gson();

    private enum UserType {
        SUPER_ADMIN("SUPER_ADMIN"), ADMIN("ADMIN"), STUDENT("STUDENT");

        private final String value;

        UserType(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    private enum MessageType {
        RSO_REQUEST("RSO_REQUEST");

        private final String value;

        MessageType(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

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
    private static final String DB_BASE_URL_TEMPLATE = "jdbc:derby://%s:%d/%s/;create=true";

    /**
     * Paths to database initialization SQL scripts
     */
    private static final String CREATE_UNIVERSITIES_TABLE = "webapps/root/WEB-INF/classes/college/events/hibernate/sql/Universities.table.create.sql";
    private static final String CREATE_USERS_TABLE = "webapps/root/WEB-INF/classes/college/events/hibernate/sql/Users.table.create.sql";
    private static final String CREATE_EVENTS_TABLE = "webapps/root/WEB-INF/classes/college/events/hibernate/sql/Events.table.create.sql";
    private static final String CREATE_RSO_TABLE = "webapps/root/WEB-INF/classes/college/events/hibernate/sql/RSO.table.create.sql";
    private static final String CREATE_MESSAGES_TABLE = "webapps/root/WEB-INF/classes/college/events/hibernate/sql/Messages.table.create.sql";
    private static final String CREATE_RSOFOLLOWS_TABLE = "webapps/root/WEB-INF/classes/college/events/hibernate/sql/RSOFollows.table.create.sql";

    /**
     * Constructor preventing outside instantiation
     */
    private DbManager() {
        // Shut down the db server on exit
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

                // Creates the url for the database server
                String url = String.format(DB_BASE_URL_TEMPLATE, properties.getDerbyDbServerHost(), DB_SERVER_PORT, properties.getDerbyDbLocation());

                // Loads the hibernate configuration properties
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

            executeSQLFromFile(CREATE_UNIVERSITIES_TABLE, session);
            executeSQLFromFile(CREATE_USERS_TABLE, session);
            executeSQLFromFile(CREATE_RSO_TABLE, session);
            executeSQLFromFile(CREATE_EVENTS_TABLE, session);
            executeSQLFromFile(CREATE_MESSAGES_TABLE, session);
            executeSQLFromFile(CREATE_RSOFOLLOWS_TABLE, session);

            t.commit();
            InsertDBTestData.insertTestData(this);
        } catch (Exception e) {
            System.err.println("Could not initialize DB. " + e);
            if(t != null) {
                t.rollback();
            }

            session.close();
        }
    }

    /**
     * Log in using an authentication token
     *
     * @param token Authentication token
     * @return Success
     */
    public QueryResponse login(String token) {
        if(token == null) {
            return new QueryResponse(false);
        }

        Map<String, String> decoded = (Map<String, String>) gson.fromJson(EncryptionService.decrypt(token), Map.class);
        if(decoded == null) {
            return new QueryResponse(false);
        }
        return login(decoded.get("username"), decoded.get("password"));
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

        // Open a session
        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Could not create a database session.");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            // Select the user with the username
            Query query = session.createQuery("SELECT u FROM UsersEntity u WHERE u.username=:userName", UsersEntity.class);
            query.setParameter("userName", username);

            UsersEntity user = (UsersEntity) query.getSingleResult();
            session.close();

            // Check if the given password is the same as the encrypted one in the db
            if(EncryptionService.authenticate(password, user.getPassword())) {
                return new QueryResponse(true, EncryptionService.createToken(username, password), user);
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
     * Creates a university profile
     *
     * @param name Name of university
     * @param location Location of university
     * @param description Description of university
     * @return
     */
    public QueryResponse createUniversity(String name, String location, String description, String authToken) {
        if(name == null) {
            return new QueryResponse(false, "Universities must have a name.");
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

            // Check if the user has sufficient permissions
            if(!authToken.equals("preload") && !isSuperAdmin(authToken, session)) {
                return new QueryResponse(false, "insufficient permissions");
            }

            UniversitiesEntity uni = new UniversitiesEntity();
            uni.setUniId(UUID.randomUUID().toString());
            uni.setName(name);
            uni.setLocation(location);
            uni.setDescritpion(description);
            uni.setStudents(0);

            session.persist(uni);

            transaction.commit();
            session.close();

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, e.toString());
        }
        return new QueryResponse(true);
    }

    //region Users

    /**
     * Create a new super admin
     *
     * @param username Username of the account
     * @param password Password of the account
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @return Success of the operation
     */
    public QueryResponse createSuperAdmin(String username, String password, String firstName, String lastName, String email) {
        if(username == null || password == null || firstName == null || lastName == null || email == null) {
            return new QueryResponse(false, "Super admins must have a username, password, first name, last name, and email address");
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
            QueryResponse available = isUsernameAvailable(username);
            if(!available.getSuccess()) {
                return new QueryResponse(false, "Username is not available");
            }


            UsersEntity user = new UsersEntity();
            user.setUserId(UUID.randomUUID().toString());
            user.setUsername(username);
            user.setPassword(EncryptionService.encrypt(password));
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setEmail(email);
            user.setType(UserType.SUPER_ADMIN.getValue());

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
     * Creates a new admin account
     *
     * @param username Username of the account
     * @param password Password of the account
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @param uniId University the admin is associated with
     * @return Success of the operation
     */
    public QueryResponse createAdmin(String username, String password, String firstName, String lastName, String email, String uniId) {
        if(username == null || password == null || firstName == null || lastName == null || email == null || uniId == null) {
            return new QueryResponse(false, "Admins must have a username, password, first name, last name, email address, and a university");
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
            QueryResponse available = isUsernameAvailable(username);
            if(!available.getSuccess()) {
                return new QueryResponse(false, "Username is not available");
            }


            UsersEntity user = new UsersEntity();
            user.setUserId(UUID.randomUUID().toString());
            user.setUsername(username);
            user.setPassword(EncryptionService.encrypt(password));
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setEmail(email);
            user.setType(UserType.ADMIN.getValue());
            user.setUniId(uniId);

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
     * Creates a new user account
     *
     * @param username Username of the account
     * @param password Password of the account
     * @param firstName User's first name
     * @param lastName User's last name
     * @param email User's email address
     * @param uniId University the student is in
     * @return Success of the operation
     */
    public QueryResponse createStudent(String username, String password, String firstName, String lastName, String email, String uniId) {
        if(username == null || password == null || firstName == null || lastName == null || email == null || uniId == null) {
            return new QueryResponse(false, "Students must have a username, password, first name, last name, email address, and a university");
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
            QueryResponse available = isUsernameAvailable(username);
            if(!available.getSuccess()) {
                return new QueryResponse(false, "Username is not available");
            }


            UsersEntity user = new UsersEntity();
            user.setUserId(UUID.randomUUID().toString());
            user.setUsername(username);
            user.setPassword(EncryptionService.encrypt(password));
            user.setFirstname(firstName);
            user.setLastname(lastName);
            user.setEmail(email);
            user.setStudentId(UUID.randomUUID().toString());
            user.setType(UserType.STUDENT.getValue());
            user.setUniId(uniId);

            session.persist(user);

            transaction.commit();
            session.close();

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, e.toString());
        }

        return new QueryResponse(true, EncryptionService.createToken(username, password));
    }

    /**
     * Checks if the username is already in use
     *
     * @param username Username to check the availability of
     * @return QueryResponse telling the availability
     */
    public QueryResponse isUsernameAvailable(String username) {
        if(username == null) {
            return new QueryResponse(false, "Username was empty");
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            Query query = session.createQuery("SELECT u FROM UsersEntity u WHERE u.username=:userName");
            query.setParameter("userName", username);

            if(query.getSingleResult() == null) {
                return new QueryResponse(true);
            }

            session.close();
            // No exception is thrown if the query succeeds. This means the username is unavailable
            return new QueryResponse(false, "Username is not available");
        } catch (Exception e) {
            // An exception means the username was not found and it is available for use
            session.close();
            return new QueryResponse(true);
        }
    }

    /** Checks if the user is a super admin */
    public boolean isSuperAdmin(String authToken, Session session) {
        // Check if the user has sufficient permissions
        if(!authToken.equals("preload")) {
            Map<String, String> decoded = (Map<String, String>) gson.fromJson(EncryptionService.decrypt(authToken), Map.class);

            Query query = session.createQuery("SELECT u FROM UsersEntity u WHERE u.username=:userName", UsersEntity.class);
            query.setParameter("userName", decoded.get("username"));

            UsersEntity u = (UsersEntity) query.getSingleResult();
            if(!EncryptionService.authenticate(decoded.get("password"), u.getPassword())) {
                return false;
            }

            if(!u.getType().equals(UserType.SUPER_ADMIN.value)) {
                return false;
            }

            return true;
        }

        return false;
    }

    private QueryResponse getUserByEmail(String userEmail) {
        if(userEmail == null) {
            return new QueryResponse(false);
        }

        if(sessionFactory == null) {
            return new QueryResponse(false, "Server could not connect to the database");
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Could not create a database session.");
        }

        try {
            Query query = session.createQuery("SELECT u FROM UsersEntity u WHERE u.email=:email");
            query.setParameter("email", userEmail);

            UsersEntity user = (UsersEntity) query.getSingleResult();
            if(user.getType().equals(UserType.SUPER_ADMIN.value)) {
                return new QueryResponse(false, "Could not find a student with the email: " + userEmail);
            }

            session.close();
            return new QueryResponse(true, user);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, "Could not find a student with the email: " + userEmail);
        }
    }

    //endregion

    //region RSO

    public QueryResponse getUniRSOs(UserInfo info) {
        if(info == null) {
            return new QueryResponse(false);
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            Query query = session.createQuery("SELECT r FROM RsoEntity r WHERE r.uniId=:id");
            query.setParameter("id", info.getUNI_ID());

            List<RsoEntity> results = query.getResultList();

            query = session.createQuery("SELECT f.rsoId FROM RsofollowsEntity f WHERE f.userId=:id");
            query.setParameter("id", info.getUSER_ID());

            List<String> follows = query.getResultList();

            List<Object> ret = new ArrayList<>();
            ret.add(results);
            ret.add(follows);

            session.close();
            return new QueryResponse(true, ret);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false);
        }
    }

    public QueryResponse createRSO(String rsoName, String description, String type, List<String> memberEmails, String authToken) {
        if(rsoName == null || memberEmails == null || type == null || memberEmails.size() != 4|| authToken == null) {
            return new QueryResponse(false);
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

            QueryResponse login = login(authToken);
            if(!login.getSuccess()) {
                return new QueryResponse(false, "Authentication error");
            }
            UsersEntity admin = (UsersEntity) login.getPayload();

            String uniID = (admin).getUniId();
            if(uniID == null || uniID.isEmpty()) {
                return new QueryResponse(false, "Requester is not a student");
            }

            QueryResponse usrLookup;
            List<String> userIDs = new ArrayList<>();

            usrLookup = getUserByEmail(memberEmails.get(0));
            if(!usrLookup.getSuccess() || !((UsersEntity) usrLookup.getPayload()).getUniId().equals(uniID)) {
                return usrLookup;
            }
            userIDs.add(((UsersEntity) usrLookup.getPayload()).getUserId());

            usrLookup = getUserByEmail(memberEmails.get(1));
            if(!usrLookup.getSuccess() || !((UsersEntity) usrLookup.getPayload()).getUniId().equals(uniID)) {
                return usrLookup;
            }
            userIDs.add(((UsersEntity) usrLookup.getPayload()).getUserId());

            usrLookup = getUserByEmail(memberEmails.get(2));
            if(!usrLookup.getSuccess() || !((UsersEntity) usrLookup.getPayload()).getUniId().equals(uniID)) {
                return usrLookup;
            }
            userIDs.add(((UsersEntity) usrLookup.getPayload()).getUserId());

            usrLookup = getUserByEmail(memberEmails.get(3));
            if(!usrLookup.getSuccess() || !((UsersEntity) usrLookup.getPayload()).getUniId().equals(uniID)) {
                return usrLookup;
            }
            userIDs.add(((UsersEntity) usrLookup.getPayload()).getUserId());

            RsoEntity rso = new RsoEntity();
            rso.setRsoId(UUID.randomUUID().toString());
            rso.setName(rsoName);
            rso.setDescription(description);
            rso.setType(type);
            rso.setAdminId(admin.getUserId());
            rso.setMembers(5);
            rso.setUniId(admin.getUniId());


            List<Object> payload = new ArrayList<>();
            payload.add(userIDs);
            payload.add(gson.toJson(rso));

            MessagesEntity request = new MessagesEntity();
            request.setId(UUID.randomUUID().toString());
            request.setSubject("RSO Creation Request");
            request.setMessage(String.format("%s wants to create the RSO named \"%s\"", admin.getEmail(), rsoName));
            request.setMessageType(MessageType.RSO_REQUEST.value);
            request.setSenderId(admin.getUserId());
            request.setUniId(uniID);
            request.setSendDate(Long.toString(System.currentTimeMillis()));
            request.setPayload(gson.toJson(payload));

            session.persist(request);
            transaction.commit();

            session.close();
            return new QueryResponse(true);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false);
        }
    }

    public QueryResponse deleteRSO(RSOMessage message, String authToken) {
        if(message == null || authToken == null) {
            return new QueryResponse(false);
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

            QueryResponse login = login(authToken);
            if(!login.getSuccess()) {
                session.close();
                return new QueryResponse(false, "Authentication error");
            }
            UsersEntity user = (UsersEntity) login.getPayload();

            Query query = session.createQuery("SELECT r FROM RsoEntity r WHERE r.rsoId=:id");
            query.setParameter("id", message.getRSO_ID());

            RsoEntity rso = (RsoEntity) query.getSingleResult();

            if(!user.getUserId().equals(rso.getAdminId())) {
                session.close();
                return new QueryResponse(false);
            }
            session.delete(rso);

            transaction.commit();
            session.close();
            return new QueryResponse(true);
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false);
        }
    }

    public QueryResponse approveRSOApplication(String messageID, String authToken) {
        if(messageID == null || authToken == null) {
            return new QueryResponse(false);
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            if(!isSuperAdmin(authToken, session)) {
                return new QueryResponse(false, "Invalid permissions");
            }

            Query query = session.createQuery("SELECT m FROM MessagesEntity m WHERE m.id=:id");
            query.setParameter("id", messageID);

            MessagesEntity m = (MessagesEntity) query.getSingleResult();
            if(!m.getMessageType().equals(MessageType.RSO_REQUEST.value)) {
                return new QueryResponse(false, "Message is not an RSO request");
            }

            List payload = gson.fromJson(m.getPayload(), List.class);
            List<String> userIDs = (List<String>) payload.get(0);
            RsoEntity rso = gson.fromJson((String) payload.get(1), RsoEntity.class);

            session.persist(rso);

            for(String s : userIDs) {
                RsofollowsEntity follow = new RsofollowsEntity();
                follow.setRsoId(rso.getRsoId());
                follow.setUserId(s);

                session.persist(follow);
            }

            RsofollowsEntity follow = new RsofollowsEntity();
            follow.setUserId(rso.getAdminId());
            follow.setRsoId(rso.getRsoId());
            session.persist(follow);

            session.delete(m);

            transaction.commit();
            session.close();
            return new QueryResponse(true);

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, e.toString());
        }
    }

    public QueryResponse declineRSOApplication(String messageID, String authToken) {
        if(messageID == null || authToken == null) {
            return new QueryResponse(false);
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();

            if(!isSuperAdmin(authToken, session)) {
                return new QueryResponse(false, "Invalid permissions");
            }

            Query query = session.createQuery("SELECT m FROM MessagesEntity m WHERE m.id=:id");
            query.setParameter("id", messageID);

            MessagesEntity m = (MessagesEntity) query.getSingleResult();
            if(!m.getMessageType().equals(MessageType.RSO_REQUEST.value)) {
                return new QueryResponse(false, "Message is not an RSO request");
            }

            session.delete(m);

            transaction.commit();
            session.close();
            return new QueryResponse(true);

        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, e.toString());
        }
    }

    public QueryResponse joinRSO(RSOMessage message, String authToken) {
        if(message == null || authToken == null) {
            return new QueryResponse(false);
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

            QueryResponse login = login(authToken);
            if(!login.getSuccess()) {
                session.close();
                return new QueryResponse(false, "Authentication error");
            }
            UsersEntity user = (UsersEntity) login.getPayload();

            Query query = session.createQuery("SELECT r FROM RsoEntity r WHERE r.rsoId=:id");
            query.setParameter("id", message.getRSO_ID());

            RsoEntity rso = (RsoEntity) query.getSingleResult();

            if(!user.getUniId().equals(rso.getUniId())) {
                session.close();
                return new QueryResponse(false);
            }

            rso.setMembers(rso.getMembers() + 1);
            session.update(rso);

            RsofollowsEntity follow = new RsofollowsEntity();
            follow.setRsoId(rso.getRsoId());
            follow.setUserId(user.getUserId());
            session.persist(follow);

            transaction.commit();
            session.close();
            return new QueryResponse(true);
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false);
        }
    }

    public QueryResponse leaveRSO(RSOMessage message, String authToken) {
        if(message == null || authToken == null) {
            return new QueryResponse(false);
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

            QueryResponse login = login(authToken);
            if(!login.getSuccess()) {
                session.close();
                return new QueryResponse(false, "Authentication error");
            }
            UsersEntity user = (UsersEntity) login.getPayload();

            Query query = session.createQuery("SELECT r FROM RsoEntity r WHERE r.rsoId=:id");
            query.setParameter("id", message.getRSO_ID());

            RsoEntity rso = (RsoEntity) query.getSingleResult();

            if(user.getUserId().equals(rso.getAdminId())) {
                session.close();
                return new QueryResponse(false);
            }
            rso.setMembers(rso.getMembers() - 1);
            session.update(rso);

            query = session.createQuery("SELECT f FROM RsofollowsEntity f WHERE f.rsoId=:rsoID AND f.userId=:userID");
            query.setParameter("rsoID", rso.getRsoId());
            query.setParameter("userID", user.getUserId());

            RsofollowsEntity follow = (RsofollowsEntity) query.getSingleResult();
            session.remove(follow);

            transaction.commit();
            session.close();
            return new QueryResponse(true);
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false);
        }
    }

    //endregion

    /**
     * Create a new message for the super admins of the university the student is affiliated with
     *
     * @param authToken Token containing the username and password
     * @return
     */
    public QueryResponse createMessage(String subject, String message, String type, String authToken) {
        if(authToken == null || authToken.isEmpty()) {
            return new QueryResponse(false, "Invalid authentication");
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

            Map<String, String> decoded = (Map<String, String>) gson.fromJson(EncryptionService.decrypt(authToken), Map.class);
            Query query = session.createQuery("SELECT u FROM UsersEntity u WHERE u.username=:userName", UsersEntity.class);
            query.setParameter("userName", decoded.get("username"));

            UsersEntity u = (UsersEntity) query.getSingleResult();
            if(!EncryptionService.authenticate(decoded.get("password"), u.getPassword())) {
                return new QueryResponse(false, "password error");
            }

            MessagesEntity m = new MessagesEntity();
            m.setId(UUID.randomUUID().toString());
            m.setSubject(subject);
            m.setMessageType(type);
            m.setMessage(message);
            m.setSendDate(Long.toString(System.currentTimeMillis()));
            m.setSenderId(u.getUserId());
            m.setUniId(u.getUniId());

            session.persist(m);

            transaction.commit();
            session.close();
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, e.toString());
        }

        return new QueryResponse(true);
    }

    public QueryResponse createEvent(String name, String type, String category, String description, String time, String date, String location, String contactPhone, String contactEmail, String uniID, String rsoID, String authToken) {
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

            Map<String, String> decoded = (Map<String, String>) gson.fromJson(EncryptionService.decrypt(authToken), Map.class);
            Query query = session.createQuery("SELECT u FROM UsersEntity u WHERE u.username=:userName", UsersEntity.class);
            query.setParameter("userName", decoded.get("username"));

            UsersEntity u = (UsersEntity) query.getSingleResult();
            if(!EncryptionService.authenticate(decoded.get("password"), u.getPassword())) {
                return new QueryResponse(false, "authentication error");
            }

            if((rsoID == null || rsoID.isEmpty()) && !u.getType().equals(UserType.SUPER_ADMIN)) {
                return new QueryResponse(false, "Insufficient premissions");
            }

            EventsEntity event = new EventsEntity();
            event.setEventId(UUID.randomUUID().toString());
            event.setName(name);
            event.setType(type);
            event.setCategory(category);
            event.setDescription(description);
            event.setTime(time);
            event.setDate(date);
            event.setLocation(location);
            event.setContactPhone(contactPhone);
            event.setContactEmail(contactEmail);
            event.setUniId(uniID);
            event.setRsoId(rsoID);

            session.persist(event);
            transaction.commit();
            session.close();

            return new QueryResponse(true);
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, e.toString());
        }
    }

    public QueryResponse getUniversities() {
        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            Query query = session.createQuery("SELECT u FROM UniversitiesEntity u");

            List<UniversitiesEntity> unis = query.getResultList();

            session.close();

            return new QueryResponse(true, unis);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, "No universities found");
        }
    }

    public QueryResponse getSuperAdminMessages(String authToken) {
        if(authToken == null || authToken.isEmpty()) {
            return new QueryResponse(false, "Invalid authentication");
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            if(!isSuperAdmin(authToken, session)) {
                session.close();
                return new QueryResponse(false, "Insufficient permissions");
            }

            Query query = session.createQuery("SELECT m FROM MessagesEntity m WHERE m.messageType=:mType");
            query.setParameter("mType", MessageType.RSO_REQUEST.value);

            List<MessagesEntity> messages = query.getResultList();

            return new QueryResponse(true, messages);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, e.toString());
        }
    }

    public QueryResponse getEvents(String authToken) {
        if(authToken == null || authToken.isEmpty()) {
            return new QueryResponse(false);
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {

            Map<String, String> decoded = (Map<String, String>) gson.fromJson(EncryptionService.decrypt(authToken), Map.class);
            Query query = session.createQuery("SELECT u FROM UsersEntity u WHERE u.username=:userName", UsersEntity.class);
            query.setParameter("userName", decoded.get("username"));

            UsersEntity u = (UsersEntity) query.getSingleResult();
            if(!EncryptionService.authenticate(decoded.get("password"), u.getPassword())) {
                return new QueryResponse(false, "authentication error");
            }

            query = session.createQuery("SELECT e FROM EventsEntity e WHERE e.type=:type");
            query.setParameter("type", "public");

            List<EventsEntity> events = query.getResultList();

            query = session.createQuery("SELECT f FROM RsofollowsEntity f WHERE f.userId=:id");
            query.setParameter("id", u.getUserId());

            for(RsofollowsEntity i : (List<RsofollowsEntity>) query.getResultList()) {
                query = session.createQuery("SELECT e FROM EventsEntity e WHERE e.rsoId=:id");
                query.setParameter("id", i.getRsoId());

                events.addAll(query.getResultList());
            }


            return new QueryResponse(true, events);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, e.toString());
        }
    }

    /**
     * Executes sql commands stored in a file
     *
     * @param path Path to file
     * @param session Db session
     * @throws Exception File was not found or malformed command
     */
    private void executeSQLFromFile(String path, Session session) throws Exception {

        for(String s : getSQLFromFile(path)) {
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
