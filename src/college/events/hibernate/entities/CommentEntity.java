package college.events.hibernate.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "COMMENT", schema = "APP", catalog = "")
public class CommentEntity {
    private String id;
    private String title;
    private String message;
    private String userID;
    private String eventID;

    @Id
    @Column(name = "ID", nullable = false, length = 255)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "TITLE", nullable = false, length = 255)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "MESSAGE", nullable = false, length = 255)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "EVENT_ID", nullable = false, length = 255)
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    @Basic
    @Column(name = "USER_ID", nullable = false, length = 255)
    public String getUserID() {
        return eventID;
    }

    public void setUserID(String eventID) {
        this.eventID = eventID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentEntity that = (CommentEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(message, that.message) &&
                Objects.equals(eventID, that.eventID) &&
                Objects.equals(userID, that.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, message, eventID, userID);
    }
}
