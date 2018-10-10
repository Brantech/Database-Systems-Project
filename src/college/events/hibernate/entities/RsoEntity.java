package college.events.hibernate.entities;

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "RSO", schema = "APP", catalog = "")
public class RsoEntity {
    private int rsoId;
    private int members;

    @Id
    @Column(name = "RSO_ID", nullable = false)
    public int getRsoId() {
        return rsoId;
    }

    public void setRsoId(int rsoId) {
        this.rsoId = rsoId;
    }

    @Basic
    @Column(name = "MEMBERS", nullable = false)
    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RsoEntity rsoEntity = (RsoEntity) o;
        return rsoId == rsoEntity.rsoId &&
                members == rsoEntity.members;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rsoId, members);
    }
}
