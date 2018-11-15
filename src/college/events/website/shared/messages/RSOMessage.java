package college.events.website.shared.messages;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RSOMessage implements IsSerializable {

    private String RSO_ID;
    private String ADMIN_ID;
    private String NAME;
    private String DESCRIPTION;
    private String TYPE;
    private int MEMBERS;
    private String UNI_ID;

    private RSOMessage() {

    }

    public RSOMessage(String RSO_ID, String ADMIN_ID, String NAME, String DESCRIPTION, String TYPE, int MEMBERS, String UNI_ID) {
        this.RSO_ID = RSO_ID;
        this.ADMIN_ID = ADMIN_ID;
        this.NAME = NAME;
        this.DESCRIPTION = DESCRIPTION;
        this.TYPE = TYPE;
        this.MEMBERS = MEMBERS;
        this.UNI_ID = UNI_ID;
    }

    public String getRSO_ID() {
        return RSO_ID;
    }

    public void setRSO_ID(String RSO_ID) {
        this.RSO_ID = RSO_ID;
    }

    public String getADMIN_ID() {
        return ADMIN_ID;
    }

    public void setADMIN_ID(String ADMIN_ID) {
        this.ADMIN_ID = ADMIN_ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public int getMEMBERS() {
        return MEMBERS;
    }

    public void setMEMBERS(int MEMBERS) {
        this.MEMBERS = MEMBERS;
    }

    public String getUNI_ID() {
        return UNI_ID;
    }

    public void setUNI_ID(String UNI_ID) {
        this.UNI_ID = UNI_ID;
    }
}
