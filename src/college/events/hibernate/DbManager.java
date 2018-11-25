package college.events.hibernate;

import college.events.hibernate.entities.*;
import college.events.hibernate.security.EncryptionService;
import college.events.hibernate.test_data.InsertDBTestData;
import college.events.website.shared.messages.CommentsMessage;
import college.events.website.shared.messages.RSOMessage;
import college.events.website.shared.messages.UserInfo;
import com.google.gson.Gson;
import org.apache.derby.drda.NetworkServerControl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.query.NativeQuery;

import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
        RSO_REQUEST("RSO_REQUEST"), EVENT("Event");

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
    private static final String CREATE_COMMENTS_TABLE = "webapps/root/WEB-INF/classes/college/events/hibernate/sql/Comments.table.create.sql";

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
            executeSQLFromFile(CREATE_COMMENTS_TABLE, session);

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

            return new QueryResponse(true, EncryptionService.createToken(username, password), user);
        } catch (Exception e) {
            if(transaction != null) {
                transaction.rollback();
            }
            session.close();
            return new QueryResponse(false, e.toString());
        }
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
            Query query = session.createQuery("SELECT r FROM RsoEntity r WHERE r.uniId=:id AND r.status='ACTIVE'");
            query.setParameter("id", info.getUNI_ID());

            List<RsoEntity> results = query.getResultList();

            query = session.createQuery("SELECT f.rsoId FROM RsofollowsEntity f, RsoEntity r WHERE f.userId=:id AND f.rsoId=r.rsoId AND r.status='ACTIVE'");
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

    public QueryResponse getRSOs() {

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {

            Query query = session.createQuery("SELECT r FROM RsoEntity r");

            List<RsoEntity> results = query.getResultList();

            session.close();
            return new QueryResponse(true, results);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false);
        }
    }

    public QueryResponse getRSOName(String rsoID) {
        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            Query query = session.createQuery("SELECT r FROM RsoEntity r WHERE r.rsoId=:id AND r.status='ACTIVE'");
            query.setParameter("id", rsoID);

            RsoEntity rso = (RsoEntity) query.getSingleResult();

            session.close();

            return new QueryResponse(true, rso.getName());
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, "");
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

            session.createSQLQuery("INSERT INTO APP.MESSAGES VALUES(:id, :subject, :mType, :message, :payload, :senderID, :uniID, :sendDate)")
                    .setParameter("id", UUID.randomUUID().toString())
                    .setParameter("subject", "RSO Creation Request")
                    .setParameter("mType", MessageType.RSO_REQUEST.value)
                    .setParameter("message", String.format("%s wants to create the RSO named \"%s\"", admin.getEmail(), rsoName))
                    .setParameter("payload", gson.toJson(payload))
                    .setParameter("senderID", admin.getUserId())
                    .setParameter("uniID", uniID)
                    .setParameter("sendDate", Long.toString(System.currentTimeMillis()))
                    .executeUpdate();

            //session.persist(request);
            transaction.commit();

            session.close();
            return new QueryResponse(true);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false);
        }
    }

    public void createDemoRSO(String rsoName, String description, String type, String[] members, String uniID) {
        Session session = sessionFactory.openSession();
        if(session == null) {
            return;
        }

        try {
            session.beginTransaction();

            session.createSQLQuery("INSERT INTO APP.RSO VALUES (:id, :adminID, :name, :description, :type, :members, :status, :uniID)")
                    .setParameter("id", UUID.randomUUID().toString())
                    .setParameter("adminID", members[0])
                    .setParameter("name", rsoName)
                    .setParameter("description", description)
                    .setParameter("type", type)
                    .setParameter("members", 5)
                    .setParameter("status", "ACTIVE")
                    .setParameter("uniID", uniID)
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            session.close();
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

            session.createSQLQuery("INSERT INTO APP.RSO VALUES (:id, :adminID, :rName, :description, :rType, :members, :status, :uniID)")
                    .setParameter("id", rso.getRsoId())
                    .setParameter("adminID", rso.getAdminId())
                    .setParameter("rName", rso.getName())
                    .setParameter("description", rso.getDescription())
                    .setParameter("rType", rso.getType())
                    .setParameter("members", rso.getMembers())
                    .setParameter("status", "ACTIVE")
                    .setParameter("uniID", rso.getUniId())
                    .executeUpdate();

            for(String s : userIDs) {
                session.createSQLQuery("INSERT INTO APP.RSOFOLLOWS VALUES (:userID, :rsoID)")
                        .setParameter("userID", s)
                        .setParameter("rsoID", rso.getRsoId())
                        .executeUpdate();
            }

            session.createSQLQuery("INSERT INTO APP.RSOFOLLOWS VALUES (:userID, :rsoID)")
                    .setParameter("userID", rso.getAdminId())
                    .setParameter("rsoID", rso.getRsoId())
                    .executeUpdate();

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

    public QueryResponse decline(String messageID, String authToken) {
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

            session.createSQLQuery("UPDATE APP.RSO r SET MEMBERS=:count, STATUS=:status WHERE r.RSO_ID=:id")
                    .setParameter("count", rso.getMembers() + 1)
                    .setParameter("id", rso.getRsoId())
                    .setParameter("status", rso.getMembers() + 1 >= 5 ? "ACTIVE" : "INACTIVE")
                    .executeUpdate();

            session.createSQLQuery("INSERT INTO APP.RSOFOLLOWS VALUES (:userID, :rsoID)")
                    .setParameter("userID", user.getUserId())
                    .setParameter("rsoID", rso.getRsoId())
                    .executeUpdate();

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

            if(rso.getMembers() < 5) {
                rso.setStatus("INACTIVE");
            }
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

    public QueryResponse createEvent(String authToken, String eventName, String description, String location, String date, String time, String rsoID, String category, String privacy, String contactName, String contactPhone, String contactEmail) {
        if(authToken == null || eventName == null || location == null || date == null || time == null || category == null || privacy == null || contactName == null || contactPhone == null || contactEmail == null) {
            return new QueryResponse(false);
        }
        if(authToken.isEmpty() || eventName.isEmpty() || location.isEmpty() || date.isEmpty() || time.isEmpty() || category.isEmpty() || privacy.isEmpty() || contactName.isEmpty() || contactPhone.isEmpty() || contactEmail.isEmpty()) {
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

            String[] newTimes = time.split(" ");
            String[] newDates = date.split(" ");

            List<EventsEntity> conflicts = session.createSQLQuery("SELECT * FROM APP.EVENTS e WHERE e.LOCATION=:location")
                                                .setParameter("location", location)
                                                .addEntity(EventsEntity.class)
                                                .getResultList();

            for(EventsEntity e : conflicts) {
                String[] dates = e.getDate().split(" ");
                String[] times = e.getTime().split(" ");

                if(
                        (newDates[0].compareTo(dates[0]) >= 0 && newDates[0].compareTo(dates[1]) <= 0) || // Start date is during another event
                        (newDates[1].compareTo(dates[0]) >= 0 && newDates[1].compareTo(dates[1]) <= 0) // End date is during another event
                ) {
                    if(
                            (newTimes[0].compareTo(times[0]) >= 0 && newTimes[0].compareTo(times[1]) <= 0) || // Start time is during another event
                            (newTimes[1].compareTo(times[0]) >= 0 && newTimes[1].compareTo(times[1]) <= 0) // End time is during another event
                    ) {
                        session.close();
                        return new QueryResponse(false,
                                String.format("Could not create the event. %s is happening from %s to %s, %s to %s in the same location",
                                        e.getName(),
                                        new SimpleDateFormat("H:mm").format(new Date(Long.parseLong(times[0]))),
                                        new SimpleDateFormat("H:mm").format(new Date(Long.parseLong(times[1]))),
                                        new SimpleDateFormat("MM/dd/yyyy").format(new Date(Long.parseLong(dates[0]))),
                                        new SimpleDateFormat("MM/dd/yyyy").format(new Date(Long.parseLong(dates[1])))
                                )
                        );
                    }
                }
            }


            if(rsoID == null || rsoID.isEmpty()) {

                EventsEntity event = new EventsEntity();
                event.setEventId(UUID.randomUUID().toString());
                event.setName(eventName);
                event.setType(privacy);
                event.setCategory(category);
                event.setDescription(description);
                event.setTime(time);
                event.setDate(date);
                event.setLocation(location);
                event.setContactName(contactName);
                event.setContactPhone(contactPhone);
                event.setContactEmail(contactEmail);
                event.setUniId(user.getUniId());

                session.createSQLQuery("INSERT INTO APP.MESSAGES VALUES (:id, :subject, :mType, :message, :payload, :senderID, :uniID, :sendDate)")
                        .setParameter("id", UUID.randomUUID().toString())
                        .setParameter("subject", "Event Creation Request")
                        .setParameter("mType", "Event")
                        .setParameter("payload", gson.toJson(event, EventsEntity.class))
                        .setParameter("senderID", user.getUserId())
                        .setParameter("uniID", user.getUniId())
                        .setParameter("sendDate", System.currentTimeMillis() + "")
                        .executeUpdate();

                transaction.commit();
                session.close();
                return new QueryResponse(true, "EventMessage without an RSO must be approved by an administrator. A request has been made");
            }

            RsoEntity rso = (RsoEntity) session.createSQLQuery("SELECT * FROM APP.RSO r WHERE r.RSO_ID=:id")
                                    .setParameter("id", rsoID)
                                    .addEntity(RsoEntity.class)
                                    .getSingleResult();

            if(!rso.getAdminId().equals(user.getUserId())) {
                return new QueryResponse(false, "You cannot create an event for this RSO as you are not its admin.");
            }

            session.createSQLQuery("INSERT INTO APP.EVENTS VALUES (:eventID, :eName, :eType, :category, :description, :eTime, :eDate, :eLocation, :cName, :cPhone, :cEmail, :uniID, :rsoID)")
                    .setParameter("eventID", UUID.randomUUID().toString())
                    .setParameter("eName", eventName)
                    .setParameter("eType", privacy)
                    .setParameter("category", category)
                    .setParameter("description", description)
                    .setParameter("eTime", time)
                    .setParameter("eDate", date)
                    .setParameter("eLocation", location)
                    .setParameter("cName", contactName)
                    .setParameter("cPhone", contactPhone)
                    .setParameter("cEmail", contactEmail)
                    .setParameter("uniID", user.getUniId())
                    .setParameter("rsoID", rsoID)
                    .executeUpdate();


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

    public QueryResponse getUniversityName(String uniID) {
        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            Query query = session.createQuery("SELECT u FROM UniversitiesEntity u WHERE u.uniId=:id");
            query.setParameter("id", uniID);

            UniversitiesEntity uni = (UniversitiesEntity) query.getSingleResult();

            session.close();

            return new QueryResponse(true, uni.getName());
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, "");
        }
    }

    public QueryResponse getEventLocations() {
        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            List<String> ret = (List<String>) session.createSQLQuery("SELECT DISTINCT (e.LOCATION) FROM APP.EVENTS e").getResultList();
            session.close();
            return new QueryResponse(true, ret);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false);
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

            Query query = session.createQuery("SELECT m FROM MessagesEntity m");

            List<MessagesEntity> messages = query.getResultList();

            return new QueryResponse(true, messages);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, e.toString());
        }
    }

    public QueryResponse approveEvent(String messageID, String authToken) {
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
            if(!m.getMessageType().equals(MessageType.EVENT.value)) {
                return new QueryResponse(false, "Message is not an Event request");
            }

            EventsEntity payload = gson.fromJson(m.getPayload(), EventsEntity.class);

            session.persist(payload);
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

    public QueryResponse getEvents(String authToken, String uniID, String location) {
        if(authToken == null || authToken.isEmpty()) {
            return new QueryResponse(false);
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {

            Map<String, String> decoded = (Map<String, String>) gson.fromJson(EncryptionService.decrypt(authToken), Map.class);
            UsersEntity u = (UsersEntity) session.createSQLQuery("SELECT * FROM APP.USERS u WHERE u.USERNAME=:userName")
                            .addEntity(UsersEntity.class)
                            .setParameter("userName", decoded.get("username"))
                            .getSingleResult();

            if(!EncryptionService.authenticate(decoded.get("password"), u.getPassword())) {
                return new QueryResponse(false, "authentication error");
            }


            List<EventsEntity> events;
            if(uniID.equals("Any") && location.equals("Any")) {
                events = session.createSQLQuery("SELECT * FROM APP.EVENTS e, APP.RSOFOLLOWS r " +
                                                    "WHERE e.TYPE='Public' OR (e.UNI_ID=:uniID AND  r.RSO_ID IS NULL) OR (e.UNI_ID=:uniID AND r.USER_ID=:userID AND r.RSO_ID=e.RSO_ID)")
                        .addEntity(EventsEntity.class)
                        .setParameter("uniID", u.getUniId())
                        .setParameter("userID", u.getUserId())
                        .getResultList();
            } else if(uniID.equals("Any")) {
                events = session.createSQLQuery("SELECT * FROM APP.EVENTS e, APP.RSOFOLLOWS r WHERE e.LOCATION=:location " +
                                                    "AND (e.TYPE='Public' OR (e.UNI_ID=:userUniID AND  r.RSO_ID IS NULL) OR (e.UNI_ID=:userUniID AND r.USER_ID=:userID AND r.RSO_ID=e.RSO_ID))")
                        .addEntity(EventsEntity.class)
                        .setParameter("location", location)
                        .setParameter("userUniID", u.getUniId())
                        .setParameter("userID", u.getUserId())
                        .getResultList();
            } else if(location.equals("Any")){
                events = session.createSQLQuery("SELECT * FROM APP.EVENTS e, APP.RSOFOLLOWS r WHERE e.UNI_ID=:uniID " +
                                                    "AND (e.TYPE='Public' OR (e.UNI_ID=:userUniID AND  r.RSO_ID IS NULL) OR (e.UNI_ID=:userUniID AND r.USER_ID=:userID AND r.RSO_ID=e.RSO_ID))")
                        .addEntity(EventsEntity.class)
                        .setParameter("uniID", uniID)
                        .setParameter("userUniID", u.getUniId())
                        .setParameter("userID", u.getUserId())
                        .getResultList();
            } else {
                events = session.createSQLQuery("SELECT * FROM APP.EVENTS e, APP.RSOFOLLOWS r WHERE e.UNI_ID=:uniID AND e.LOCATION=:location " +
                                                    "AND (e.TYPE='Public' OR (e.UNI_ID=:userUniID AND  r.RSO_ID IS NULL) OR (e.UNI_ID=:userUniID AND r.USER_ID=:userID AND r.RSO_ID=e.RSO_ID))")
                        .addEntity(EventsEntity.class)
                        .setParameter("uniID", uniID)
                        .setParameter("location", location)
                        .setParameter("userUniID", u.getUniId())
                        .setParameter("userID", u.getUserId())
                        .getResultList();
            }

            events = events.stream().distinct().collect(Collectors.toList());

            return new QueryResponse(true, events);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, e.toString());
        }
    }

    public QueryResponse writeComment(String authToken, String title, String message, String eventID) {
        if(authToken == null || title == null || message == null || eventID == null) {
            return new QueryResponse(false, "Failed to create a new comment");
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            session.beginTransaction();
            QueryResponse login = login(authToken);
            if (!login.getSuccess()) {
                session.close();
                return new QueryResponse(false, "Authentication error");
            }
            UsersEntity user = (UsersEntity) login.getPayload();

            EventsEntity event = (EventsEntity) session.createSQLQuery("SELECT * FROM APP.EVENTS e WHERE e.EVENT_ID=:id")
                                                        .setParameter("id", eventID)
                                                        .addEntity(EventsEntity.class)
                                                        .getSingleResult();
            if(event == null) {
                session.close();
                return new QueryResponse(false, "Event not found");
            }

            if((!event.getUniId().equals(user.getUniId()) && !event.getType().equals("Public"))) {
                return new QueryResponse(false, "You do not have permission to comment on this event");
            }

            session.createSQLQuery("INSERT INTO APP.COMMENT VALUES (:id, :userID, :title, :message, :eventID)")
                    .setParameter("id", UUID.randomUUID().toString())
                    .setParameter("userID", user.getUserId())
                    .setParameter("title", title)
                    .setParameter("message", message)
                    .setParameter("eventID", eventID)
                    .executeUpdate();

            session.close();
            return new QueryResponse(true, "Comment Added");
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false, "Failed to create comment");
        }
    }

    public QueryResponse editComment(String authToken, String title, String message, String mID) {

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            session.beginTransaction();

            QueryResponse login = login(authToken);
            if (!login.getSuccess()) {
                session.close();
                return new QueryResponse(false, "Authentication error");
            }
            UsersEntity user = (UsersEntity) login.getPayload();

            CommentEntity comment = (CommentEntity) session.createSQLQuery("SELECT * FROM APP.COMMENT c WHERE c.ID=:mID").setParameter("mID", mID)
                                                            .addEntity(CommentEntity.class)
                                                            .getSingleResult();

            if(!user.getUserId().equals(comment.getUserID())) {
                session.close();
                return new QueryResponse(false, "This is not your comment");
            }

            session.createSQLQuery("UPDATE APP.COMMENT c SET TITLE=:title, MESSAGE=:message WHERE c.ID=:mID")
                    .setParameter("title", title)
                    .setParameter("message", message)
                    .setParameter("mID", comment.getId())
                    .executeUpdate();

            session.close();
            return new QueryResponse(true);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false);
        }
    }

    public QueryResponse getComments(String eventID) {
        if(eventID == null) {
            return new QueryResponse(false, "Event id cannot be null");
        }

        Session session = sessionFactory.openSession();
        if(session == null) {
            return new QueryResponse(false, "Server could not connect to database");
        }

        try {
            Query query = session.createSQLQuery("SELECT * FROM APP.COMMENT c WHERE c.EVENT_ID=:id");
            ((NativeQuery) query).addEntity(CommentEntity.class);
            query.setParameter("id", eventID);

            List<CommentEntity> comments = query.getResultList();

            List<CommentsMessage> ret = new ArrayList<>();
            for(CommentEntity c : comments) {
                String name = (String) session.createSQLQuery("SELECT FIRSTNAME FROM APP.USERS u WHERE u.USER_ID=:id")
                        .setParameter("id", c.getUserID())
                        .getSingleResult();
                ret.add(new CommentsMessage(c.getId(), c.getTitle(), c.getMessage(), name, c.getUserID()));
            }
            return new QueryResponse(false, ret);
        } catch (Exception e) {
            session.close();
            return new QueryResponse(false);
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
