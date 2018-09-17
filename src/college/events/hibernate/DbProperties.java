package college.events.hibernate;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Class used for retrieving server properties
 */
public class DbProperties {
    private static DbProperties instance;

    /**
     * Derby database properties
     */
    private static final String DERBYDB_PROPERTIES = System.getProperty("user.dir") + "/webapps/root/WEB-INF/config/database.properties";

    /**
     * KEYS
     */
    private static final String DERBYDB_LOCATION = "dbLocation";
    private static final String DERBYDB_PORT = "dbPort";
    private static final String DERBYDB_HOST = "dbHost";


    private static Properties derby;


    /**
     * Constructor preventing outside instantiation
     */
    private DbProperties() {
        try{
            derby = new Properties();
            derby.load(new FileInputStream(DERBYDB_PROPERTIES));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Gets an instance of the class
     *
     * @return Instance of the class
     */
    public static DbProperties getInstance() {
        if(instance == null) {
            instance = new DbProperties();
        }
        return instance;
    }

    /**
     * Gets the location of the database
     *
     * @return Location of the database
     */
    public String getDerbyDbLocation() {
        return derby.getProperty(DERBYDB_LOCATION);
    }

    /**
     * @return Port specified in the properties file or -1 if it was not
     */
    public int getDerbyDbServerPort() {
        if(derby.containsKey(DERBYDB_PORT)) {
            return Integer.parseInt(derby.getProperty(DERBYDB_PORT));
        }
        return -1;
    }

    /**
     * Gets the hostname specified in the properties file
     *
     * @return Hostname
     */
    public String getDerbyDbServerHost() {
        return derby.getProperty(DERBYDB_HOST);
    }
}
