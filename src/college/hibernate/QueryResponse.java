package college.hibernate;

/**
 * Response object DbManager.java sends back when it completes a query
 */
public class QueryResponse {

    private boolean success;
    private String message;
    private Throwable t;

    /**
     * Prevents invalid instantiation
     */
    private QueryResponse() {
    }

    /**
     * Constructor
     *
     * @param success Success of the query
     */
    public QueryResponse(boolean success) {
        this.success = success;
    }

    /**
     * Constructor
     *
     * @param success Success of the query
     * @param t Error caught
     */
    public QueryResponse(boolean success, Throwable t) {
        this.success = success;
        this.t = t;
    }

    /**
     * Constructor
     *
     * @param success Success of the query
     * @param message Response message
     */
    public QueryResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Constructor
     *
     * @param success Success of the query
     * @param message Response message
     * @param t Error caught
     */
    public QueryResponse(boolean success, String message, Throwable t) {
        this.success = success;
        this.message = message;
        this.t = t;
    }

    /**
     * @return Query success status
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * @return Query message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return Thrown error
     */
    public Throwable getError() {
        return t;
    }
}
