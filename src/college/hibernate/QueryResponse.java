package college.hibernate;

/**
 * Response object DbManager.java sends back when it completes a query
 */
public class QueryResponse {

    private boolean success;
    private String message;

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
     * @param message Response message
     */
    public QueryResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
