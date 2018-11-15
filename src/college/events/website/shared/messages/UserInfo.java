package college.events.website.shared.messages;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserInfo implements IsSerializable {
    private String USER_ID;
    private String EMAIL;
    private String TYPE;
    private String UNI_ID;

    private String authToken;
    private UserInfo() {

    }

    public UserInfo(String USER_ID, String EMAIL, String TYPE, String UNI_ID, String authToken) {
        this.USER_ID = USER_ID;
        this.EMAIL = EMAIL;
        this.TYPE = TYPE;
        this.UNI_ID = UNI_ID;
        this.authToken = authToken;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getUNI_ID() {
        return UNI_ID;
    }

    public void setUNI_ID(String UNI_ID) {
        this.UNI_ID = UNI_ID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
