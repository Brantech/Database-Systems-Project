package college.events.website.shared.rpc;

import java.io.Serializable;

/**
 * Generic rpc response to send from the server
 */
public class GenericRPCResponse<T> implements Serializable {

    /**
     * Response Object
     */
    private T obj;

    /**
     * Successful operation
     */
    private boolean success;

    /**
     * Constructor for serialization
     */
    public GenericRPCResponse() {
    }

    /**
     * Constructor
     *
     * @param success Successful server operation
     */
    public GenericRPCResponse(boolean success) {
        this.success = success;
    }

    /**
     * Main constructor
     *
     * @param obj Response message
     * @param success Successful server operation
     */
    public GenericRPCResponse(T obj, boolean success) {
        this.obj = obj;
        this.success = success;
    }

    /**
     * @return Response object
     */
    public T getObj() {
        return obj;
    }

    /**
     * Set the response object
     * @param obj
     */
    public void setObj(T obj) {
        this.obj = obj;
    }

    /**
     * @return Server operation success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set success flag
     *
     * @param success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
