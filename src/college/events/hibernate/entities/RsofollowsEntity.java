package college.events.hibernate.entities;

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RSOFOLLOWS", schema = "APP", catalog = "")
public class RsofollowsEntity {
    private String userId;
    private String rsoId;

    @Id
    @Column(name = "USER_ID", nullable = false, length = 255)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "RSO_ID", nullable = true, length = 255)
    public String getRsoId() {
        return rsoId;
    }

    public void setRsoId(String rsoId) {
        this.rsoId = rsoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RsofollowsEntity that = (RsofollowsEntity) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(rsoId, that.rsoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, rsoId);
    }
}
