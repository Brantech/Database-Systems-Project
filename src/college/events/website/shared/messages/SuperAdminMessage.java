package college.events.website.shared.messages;

import java.io.Serializable;

public class SuperAdminMessage implements Serializable {
    private String id;
    private String subject;
    private String messageType;
    private String message;
    private String senderId;
    private String uniId;
    private long sendDate;

    public SuperAdminMessage() {
        super();
    }

    public SuperAdminMessage(String id, String subject, String messageType, String message, String senderId, String uniId, long sendDate) {
        this.id = id;
        this.subject = subject;
        this.messageType = messageType;
        this.message = message;
        this.senderId = senderId;
        this.uniId = uniId;
        this.sendDate = sendDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }

    public long getSendDate() {
        return sendDate;
    }

    public void setSendDate(long sendDate) {
        this.sendDate = sendDate;
    }
}
