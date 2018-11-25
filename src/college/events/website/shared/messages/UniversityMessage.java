package college.events.website.shared.messages;

import java.io.Serializable;

public class UniversityMessage implements Serializable {
    private String uniID;
    private String name;

    private UniversityMessage() {

    }

    public UniversityMessage(String uniID, String name) {
        this.uniID = uniID;
        this.name = name;
    }

    public String getUniID() {
        return uniID;
    }

    public void setUniID(String uniID) {
        this.uniID = uniID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
