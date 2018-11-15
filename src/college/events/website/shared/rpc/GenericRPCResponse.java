package college.events.website.shared.rpc;

import java.io.Serializable;

/**
 * Generic rpc response to send from the server
 */
public class GenericRPCResponse<T> implements Serializable {

    /**
     * Response Object
     */
    private T payload;

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
     * @param payload Response message
     * @param success Successful server operation
     */
    public GenericRPCResponse(T payload, boolean success) {
        this.payload = payload;
        this.success = success;
    }

    /**
     * @return Response object
     */
    public T getPayload() {
        return payload;
    }

    /**
     * Set the response object
     * @param payload
     */
    public void setPayload(T payload) {
        this.payload = payload;
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
