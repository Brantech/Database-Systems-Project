package college.events.website.shared.messages;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LoginResponseMessage implements IsSerializable {

    private boolean isSuccess;
    private String uniID;
    private String rsoID;
    private String type;
    private String authToken;


    public LoginResponseMessage() {
    }

    public LoginResponseMessage(boolean isSuccess, String uniID, String rsoID, String type, String authToken) {
        this.isSuccess = isSuccess;
        this.uniID = uniID;
        this.rsoID = rsoID;
        this.type = type;
        this.authToken = authToken;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getUniID() {
        return uniID;
    }

    public void setUniID(String uniID) {
        this.uniID = uniID;
    }

    public String getRsoID() {
        return rsoID;
    }

    public void setRsoID(String rsoID) {
        this.rsoID = rsoID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
