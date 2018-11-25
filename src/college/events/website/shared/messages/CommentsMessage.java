package college.events.website.shared.messages;

import java.io.Serializable;

public class CommentsMessage implements Serializable {
    private String id;
    private String title;
    private String message;
    private String name;
    private String userID;

    private CommentsMessage() {

    }

    public CommentsMessage(String id, String title, String message, String name, String userID) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.name = name;
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
